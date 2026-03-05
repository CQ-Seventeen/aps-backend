package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.bom.query.PageStyleQuery;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.bom.entity.*;
import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import com.santoni.iot.aps.infrastructure.database.aps.StyleComponentMapper;
import com.santoni.iot.aps.infrastructure.database.aps.StyleMapper;
import com.santoni.iot.aps.infrastructure.database.aps.StyleSkuMapper;
import com.santoni.iot.aps.infrastructure.factory.StyleFactory;
import com.santoni.iot.aps.infrastructure.po.StyleComponentPO;
import com.santoni.iot.aps.infrastructure.po.StylePO;
import com.santoni.iot.aps.infrastructure.po.StyleSkuPO;
import com.santoni.iot.aps.infrastructure.po.qo.ComponentPairQO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchStyleQO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StyleRepositoryImpl implements StyleRepository {

    @Autowired
    private StyleMapper styleMapper;

    @Autowired
    private StyleSkuMapper styleSkuMapper;

    @Autowired
    private StyleComponentMapper styleComponentMapper;

    @Autowired
    private StyleFactory styleFactory;

    @Override
    public StyleSpu getStyleById(StyleId id) {
        var po = styleMapper.getById(id.value());
        if (null == po) {
            return null;
        }
        return styleFactory.composeStyle(po);
    }

    @Override
    public StyleSpu getStyleByCode(ProduceOrderCode orderCode, StyleCode code) {
        StylePO po;
        if (null == orderCode) {
            po = styleMapper.getByCode(PlanContext.getInstituteId(), code.value());
        } else {
            po = styleMapper.getByOrderAndCode(PlanContext.getInstituteId(), orderCode.value(), code.value());
        }
        if (null == po) {
            return null;
        }
        return styleFactory.composeStyle(po);
    }

    @Override
    public StyleSpu lockStyleByCode(ProduceOrderCode orderCode, StyleCode code) {
        var po = styleMapper.lockByOrderAndCode(PlanContext.getInstituteId(), orderCode.value(), code.value());
        if (null == po) {
            return null;
        }
        return styleFactory.composeStyle(po);
    }

    @Override
    public List<StyleSpu> listStyleByCode(List<StyleCode> codeList) {
        var poList = styleMapper.listByCodeList(PlanContext.getInstituteId(), codeList.stream().map(StyleCode::value).toList());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream()
                .map(it -> styleFactory.composeStyle(it))
                .toList();
    }

    @Override
    public void saveStyle(StyleSpu style, List<StyleSku> skuList) {
        var po = styleFactory.convertToStylePO(style);
        if (null == style.getId()) {
            insertStyle(po, skuList);
        } else {
            po.setId(style.getId().value());
            updateStyle(po, skuList);
        }
    }

    private void insertStyle(StylePO po, List<StyleSku> skuList) {
        styleMapper.insert(po);
        List<StyleSkuPO> skuPOList = Lists.newArrayListWithExpectedSize(skuList.size());
        List<StyleComponentPO> componentPOList = Lists.newArrayList();
        for (var sku : skuList) {
            skuPOList.add(styleFactory.convertToStyleSkuPO(sku));
            if (CollectionUtils.isNotEmpty(sku.getComponents())) {
                componentPOList.addAll(sku.getComponents().stream().map(it -> styleFactory.convertToStyleComponentPO(it)).toList());
            }
        }
        if (CollectionUtils.isNotEmpty(skuPOList)) {
            styleSkuMapper.batchInsert(skuPOList);
        }
        if (CollectionUtils.isNotEmpty(componentPOList)) {
            styleComponentMapper.batchInsert(componentPOList);
        }
    }

    private void updateStyle(StylePO po, List<StyleSku> skuList) {
        styleMapper.update(po);
        var existSkuMap = styleSkuMapper.findByStyleCode(PlanContext.getInstituteId(), po.getCode())
                .stream().collect(Collectors.toMap(StyleSkuPO::getSize, it -> it, (v1, v2) -> v1));
        for (var sku : skuList) {
            if (existSkuMap.containsKey(sku.getSize().value())) {
                updateSku(sku);
                existSkuMap.remove(sku.getSize().value());
            } else {
                insertSku(sku);
            }
        }
        if (MapUtils.isNotEmpty(existSkuMap)) {
            for (var sku : existSkuMap.values()) {
                deleteSku(sku);
            }
        }
    }

    private void insertSku(StyleSku sku) {
        var po = styleFactory.convertToStyleSkuPO(sku);
        styleSkuMapper.insert(po);
        var componentPOList = sku.getComponents().stream().map(it -> styleFactory.convertToStyleComponentPO(it)).toList();
        styleComponentMapper.batchInsert(componentPOList);
    }

    private void updateSku(StyleSku sku) {
        var po = styleFactory.convertToStyleSkuPO(sku);
        styleSkuMapper.update(po);
        var existComponentMap = styleComponentMapper.listByOrderAndSkuCode(PlanContext.getInstituteId(), po.getProduceOrderCode(), po.getCode())
                .stream().collect(Collectors.toMap(StyleComponentPO::getPart, it -> it, (v1, v2) -> v1));
        var componentPOList = sku.getComponents().stream().map(it -> styleFactory.convertToStyleComponentPO(it)).toList();
        for (var component : componentPOList) {
            if (existComponentMap.containsKey(component.getPart())) {
                styleComponentMapper.update(component);
                existComponentMap.remove(component.getPart());
            } else {
                styleComponentMapper.insert(component);
            }
        }
        if (MapUtils.isNotEmpty(existComponentMap)) {
            for (var component : existComponentMap.values()) {
                styleComponentMapper.delete(component);
            }
        }
    }

    private void deleteSku(StyleSkuPO sku) {
        styleSkuMapper.delete(sku);
        styleComponentMapper.deleteBySkuCode(PlanContext.getInstituteId(), sku.getCode());
    }

    @Override
    @Transactional
    public void batchSaveStyle(List<StyleSpu> styleList) {
        List<StylePO> insertList = Lists.newArrayList();
        List<StylePO> updateList = Lists.newArrayList();
        for (var style : styleList) {
            var po = styleFactory.convertToStylePO(style);
            if (null == style.getId()) {
                insertList.add(po);
            } else {
                po.setId(style.getId().value());
                updateList.add(po);
            }
        }
        styleMapper.batchInsert(insertList);
        styleMapper.batchUpdate(updateList);
    }

    @Override
    public PageData<StyleSpu> pageQueryStyle(PageStyleQuery query) {
        IPage<StylePO> page = new Page<>(query.getPage(), query.getPageSize());
        var qo = new SearchStyleQO();
        qo.setCode(query.getCode());
        var pageRes = styleMapper.searchStyle(page, PlanContext.getInstituteId(), qo);

        return PageData.fromPage(pageRes.getRecords()
                .stream()
                .map(it -> styleFactory.composeStyle(it))
                .toList(), page);
    }

    @Override
    public List<StyleSku> listStyleSkuByCode(ProduceOrderCode produceOrderCode, Collection<SkuCode> skuCodeList, boolean needComponent) {
        var skuCodeStrList = skuCodeList.stream().map(SkuCode::value).toList();
        List<StyleSkuPO> poList;
        if (null == produceOrderCode) {
            poList = styleSkuMapper.listBySkuCodeList(PlanContext.getInstituteId(), skuCodeStrList);
        } else {
            poList = styleSkuMapper.listByOrderAndSkuCode(PlanContext.getInstituteId(), produceOrderCode.value(), skuCodeStrList);
        }
        return composeStyleSkuList(produceOrderCode, poList, needComponent);
    }

    @Override
    public StyleSku getStyleSkuByCode(ProduceOrderCode orderCode, SkuCode skuCode, boolean needComponent) {
        StyleSkuPO po;
        if (null == orderCode) {
            po = styleSkuMapper.findBySkuCode(PlanContext.getInstituteId(), skuCode.value());
        } else {
            po = styleSkuMapper.findByOrderAndCode(PlanContext.getInstituteId(), orderCode.value(), skuCode.value());
        }
        return composeStyleSku(po, needComponent);
    }

    @Override
    public List<StyleSku> listStyleSkuByStyleCode(ProduceOrderCode orderCode, StyleCode styleCode, boolean needComponent) {
        var poList = styleSkuMapper.findByStyleCode(PlanContext.getInstituteId(), styleCode.value());
        return composeStyleSkuList(orderCode, poList, needComponent);
    }

    @Override
    public StyleSku getStyleSkuByStyleAndSize(StyleCode styleCode, Size size, boolean needComponent) {
        var po = styleSkuMapper.findByStyleAndSize(PlanContext.getInstituteId(), styleCode.value(), size.value());
        return composeStyleSku(po, needComponent);
    }

    @Override
    public StyleComponent getComponentBySkuAndPart(ProduceOrderCode orderCode, SkuCode skuCode, Part part) {
        StyleComponentPO po;
        if (null == orderCode) {
            po = styleComponentMapper.getBySkuCodeAndPart(PlanContext.getInstituteId(), skuCode.value(), part.value());
        } else {
            po = styleComponentMapper.getByOrderAndSkuCodeAndPart(PlanContext.getInstituteId(), orderCode.value(), skuCode.value(), part.value());
        }
        if (null == po) {
            return null;
        }
        return styleFactory.composeComponent(po);
    }

    @Override
    public List<StyleComponent> listComponentBySkuAndPart(List<Pair<SkuCode, Part>> pairList) {
        var qoList = pairList.stream().map(it -> new ComponentPairQO(it.getLeft().value(), it.getRight().value())).toList();
        var componentList = styleComponentMapper.listBySkuAndPartPair(PlanContext.getInstituteId(), qoList);
        if (CollectionUtils.isEmpty(componentList)) {
            return Collections.emptyList();
        }
        return componentList.stream().map(it -> styleFactory.composeComponent(it)).toList();
    }

    @Override
    public List<StyleSku> listStyleSkuByOrderCode(Collection<ProduceOrderCode> orderCodeList, boolean needComponent) {
        var orderCodeStr = orderCodeList.stream().map(ProduceOrderCode::value).toList();
        var skuList = styleSkuMapper.listByOrderCodeList(PlanContext.getInstituteId(), orderCodeStr);

        var skuMap = skuList.stream().collect(Collectors.groupingBy(StyleSkuPO::getProduceOrderCode));

        List<StyleSku> res = Lists.newArrayList();
        for (var orderCode : orderCodeList) {
            var poList = skuMap.get(orderCode.value());
            if (CollectionUtils.isNotEmpty(poList)) {
                res.addAll(composeStyleSkuList(orderCode, poList, needComponent));
            }
        }
        return res;
    }

    private List<StyleSku> composeStyleSkuList(ProduceOrderCode orderCode, List<StyleSkuPO> poList, boolean needComponent) {
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        if (!needComponent) {
            return poList.stream().map(it -> styleFactory.composeSku(it, Collections.emptyList())).toList();
        }
        var skuCodeStrList = poList.stream().map(StyleSkuPO::getCode).toList();
        var componentMap = styleComponentMapper.listByOrderAndSkuCodeList(PlanContext.getInstituteId(), orderCode.value(), skuCodeStrList)
                .stream().collect(Collectors.groupingBy(StyleComponentPO::getSkuCode));
        return poList.stream().map(it -> styleFactory.composeSku(it, componentMap.get(it.getCode()))).toList();
    }

    private StyleSku composeStyleSku(StyleSkuPO po, boolean needComponent) {
        if (null == po) {
            return null;
        }
        if (!needComponent) {
            return styleFactory.composeSku(po, Collections.emptyList());
        }
        var componentPOList = styleComponentMapper.findBySkuCode(PlanContext.getInstituteId(), po.getCode());
        return styleFactory.composeSku(po, componentPOList);
    }

}
