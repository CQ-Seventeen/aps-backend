package com.santoni.iot.aps.application.bom.impl;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.bom.StyleApplication;
import com.santoni.iot.aps.application.bom.assembler.StyleAssembler;
import com.santoni.iot.aps.application.bom.command.*;
import com.santoni.iot.aps.application.bom.dto.StyleComponentDTO;
import com.santoni.iot.aps.application.bom.query.*;
import com.santoni.iot.aps.application.support.dto.BatchOperateResultDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.application.bom.dto.StyleDTO;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Part;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleId;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingPartOrderId;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.support.repository.CodeRepository;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StyleApplicationImpl implements StyleApplication {

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private StyleAssembler styleAssembler;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional
    @Override
    public void createStyle(CreateStyleCommand cmd) {
        StyleCode styleCode;
        if (StringUtils.isBlank(cmd.getCode())) {
            styleCode = codeRepository.getStyleCode();
        } else {
            styleCode = new StyleCode(cmd.getCode());
        }
        var style = styleAssembler.composeStyleFromCreateCmd(cmd, styleCode);
        checkStyleExist(styleCode, style.getProduceOrderCode());

        var skuList = buildSku(styleCode, cmd.getSkuList(), style.getProduceOrderCode());
        styleRepository.saveStyle(style, skuList);
    }

    private void checkStyleExist(StyleCode styleCode, ProduceOrderCode produceOrderCode) {
        var exist = styleRepository.getStyleByCode(produceOrderCode, styleCode);
        if (null != exist) {
            throw new IllegalArgumentException("款式已存在,编号:" + styleCode.value());
        }
    }

    private List<StyleSku> buildSku(StyleCode styleCode, List<OperateStyleSkuCommand> skuCommandList, ProduceOrderCode produceOrderCode) {
        if (CollectionUtils.isEmpty(skuCommandList)) {
            return Collections.emptyList();
        }
        List<StyleSku> skuList = Lists.newArrayListWithExpectedSize(skuCommandList.size());
        for (var cmd : skuCommandList) {
            var sku = styleAssembler.composeStyleSkuFromCmd(styleCode, cmd, produceOrderCode);
            List<StyleComponent> components = CollectionUtils.isEmpty(cmd.getComponents()) ? List.of() :
                    cmd.getComponents()
                            .stream()
                            .map(it -> styleAssembler.composeStyleComponentFromCmd(sku.getCode(), it, produceOrderCode))
                            .toList();
            sku.addComponents(components);
            sku.calculateExpectedProduceTime();
            skuList.add(sku);
        }
        return skuList;
    }

    @Override
    public BatchOperateResultDTO batchCreateStyle(BatchCreateStyleCommand cmd) {
        List<StyleCode> nonBlankCode = Lists.newArrayList();
        int count = 0;
        for (var style : cmd.getStyleList()) {
            if (StringUtils.isBlank(style.getCode())) {
                count++;
            } else {
                nonBlankCode.add(new StyleCode(style.getCode()));
            }
        }
        var newCodeList = codeRepository.getMultiStyleCode(count);
        nonBlankCode.addAll(newCodeList);
        checkStyleCodeListExist(nonBlankCode);

        var codeQueue = Lists.newLinkedList(newCodeList);
        return batchSaveStyle(codeQueue, cmd);
    }

    private void checkStyleCodeListExist(List<StyleCode> styleCodeList) {
        var exist = styleRepository.listStyleByCode(styleCodeList);
        if (!exist.isEmpty()) {
            throw new IllegalArgumentException("款式已存在,编号:" +
                    exist.stream().map(it -> it.getCode().value()).collect(Collectors.joining(",")));
        }
    }

    private BatchOperateResultDTO batchSaveStyle(Deque<StyleCode> codeQueue, BatchCreateStyleCommand cmd) {
        List<String> failCodeList = Lists.newArrayList();
        int success = 0, fail = 0;
        for (var styleCmd : cmd.getStyleList()) {
            var styleCode = StringUtils.isBlank(styleCmd.getCode()) ? codeQueue.pop() : new StyleCode(styleCmd.getCode());
            var style = styleAssembler.composeStyleFromCreateCmd(styleCmd, styleCode);
            var skuList = buildSku(styleCode, styleCmd.getSkuList(), style.getProduceOrderCode());
            boolean res = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
                try {
                    styleRepository.saveStyle(style, skuList);
                    return true;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    return false;
                }
            }));
            if (res) {
                success++;
            } else {
                failCodeList.add(styleCode.value());
                fail++;
            }
        }
        return new BatchOperateResultDTO(success, fail, failCodeList);
    }

    @Transactional
    @Override
    public void updateStyle(UpdateStyleCommand cmd) {
        var existStyle = styleRepository.getStyleById(new StyleId(cmd.getId()));
        if (null == existStyle) {
            throw new IllegalArgumentException("款式不存在");
        }
        var style = styleAssembler.composeStyleFromUpdateCmd(cmd);
        if (!style.getCode().equals(existStyle.getCode())) {
            throw new IllegalArgumentException("款式编号不允许修改");
        }
        var skuList = buildSku(style.getCode(), cmd.getSkuList(), style.getProduceOrderCode());
        styleRepository.saveStyle(style, skuList);
    }

    @Override
    public PageResult<StyleDTO> pageQueryStyle(PageStyleQuery query) {
        var pageRes = styleRepository.pageQueryStyle(query);
        if (CollectionUtils.isEmpty(pageRes.getData())) {
            return PageResult.empty(pageRes);
        }
        var styleList = pageRes.getData().stream().map(it -> styleAssembler.assembleStyleDTO(it, Collections.emptyList())).toList();
        return PageResult.fromPageData(styleList, pageRes);
    }

    @Override
    public StyleDTO getStyleDetail(StyleDetailQuery query) {
        var style = styleRepository.getStyleById(new StyleId(query.getStyleId()));
        if (null == style) {
            throw new IllegalArgumentException("款式不存在");
        }
        var skuList = styleRepository.listStyleSkuByStyleCode(style.getProduceOrderCode(), style.getCode(), true);
        return styleAssembler.assembleStyleDTO(style, skuList);
    }

    @Override
    public StyleDTO getStyleDetailByCode(StyleDetailByCodeQuery query) {
        var produceOrderCode = StringUtils.isBlank(query.getProduceOrderCode()) ? null : new ProduceOrderCode(query.getProduceOrderCode());
        var style = styleRepository.getStyleByCode(produceOrderCode, new StyleCode(query.getStyleCode()));
        if (null == style) {
            throw new IllegalArgumentException("款式不存在");
        }
        var skuList = styleRepository.listStyleSkuByStyleCode(produceOrderCode, style.getCode(), true);
        return styleAssembler.assembleStyleDTO(style, skuList);
    }

    @Override
    public StyleComponentDTO getStyleComponentDetail(StyleComponentDetailQuery query) {
        var partOrder = weavingOrderRepository.getPartOrderById(new WeavingPartOrderId(query.getPartOrderId()));
        if (null == partOrder) {
            return null;
        }
        var component = styleRepository.getComponentBySkuAndPart(partOrder.getProduceOrderCode(), partOrder.getDemand().getSkuCode(), partOrder.getDemand().getPart());
        if (null == component) {
            throw new IllegalArgumentException("部件不存在");
        }
        return styleAssembler.assembleStyleComponentDTO(component);
    }

    @Override
    public void importOuterStyle(OperateOuterStyleCommand command) {
        var exist = styleRepository.lockStyleByCode(new ProduceOrderCode(command.getOuterProduceOrderCode()), new StyleCode(command.getCode()));
        if (null == exist) {
            createStyle(command);
        } else {
            command.setId(exist.getId().value());
            updateStyle(command);
        }
    }
}
