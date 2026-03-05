package com.santoni.iot.aps.adapter.support.controller;

import com.santoni.iot.aps.adapter.support.request.SyncBomPartReq;
import com.santoni.iot.aps.adapter.support.request.SyncBomReq;
import com.santoni.iot.aps.adapter.support.request.SyncProductReq;
import com.santoni.iot.aps.adapter.support.request.SyncProductItemReq;
import com.santoni.iot.aps.application.bom.StyleApplication;
import com.santoni.iot.aps.application.bom.YarnApplication;
import com.santoni.iot.aps.application.bom.command.*;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import com.santoni.iot.utils.record.constant.Header;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/sync")
@RestController
@Slf4j
public class OuterSyncStyleController {

    @Autowired
    private StyleApplication styleApplication;

    @Autowired
    private YarnApplication yarnApplication;

    @PostMapping("/bom")
    @ResponseBody
    @SantoniHeader(Header.NONE)
    public ReturnData<Void> syncBom(@RequestBody SyncBomReq req) {
        log.info("Receive Sync BOM Req: {}", req);
        try {
            if (req.getOperation() != 0) {
                var cmd = convertToCommand(req);
                PlanContext.setInstituteId(1L);
                styleApplication.importOuterStyle(cmd);
            }
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("Sync Bom error", e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/product")
    @ResponseBody
    @SantoniHeader(Header.NONE)
    public ReturnData<Void> syncProduct(@RequestBody SyncProductReq req) {
        log.info("Receive Sync Product Req: {}", req);
        try {
            if (req.getOperation() != 0) {
                if (req.getDataList() != null && !req.getDataList().isEmpty()) {
                    log.info("Processing {} yarn products", req.getDataList().size());
                    PlanContext.setInstituteId(1L);
                    for (SyncProductItemReq item : req.getDataList()) {
                        var cmd = convertToYarnCommand(item);
                        yarnApplication.importOuterYarn(cmd);
                    }
                }
            }
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("Sync Product error", e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    private OperateOuterYarnCommand convertToYarnCommand(SyncProductItemReq item) {
        var cmd = new OperateOuterYarnCommand();
        cmd.setProductType(item.getProductType());
        cmd.setProductId(item.getProductId());
        cmd.setProductName(item.getProductName());
        cmd.setProductCode(item.getProductCode());
        cmd.setPackageUnit(item.getPackageUnit());
        cmd.setBatch(item.getBatch());
        cmd.setSupplierCode(item.getSupplierCode());
        cmd.setTwist(item.getTwist());
        cmd.setColor(item.getColor());
        cmd.setDescription(item.getDescription());
        return cmd;
    }

    private OperateOuterStyleCommand convertToCommand(SyncBomReq req) {
        var cmd = new OperateOuterStyleCommand();
        cmd.setOuterId(req.getStyleId());
        cmd.setCode(req.getStyleCode());
        cmd.setName(req.getStyleName());
        cmd.setSymbolId(req.getSymbolId());
        cmd.setSymbol(req.getSymbol());
        cmd.setOuterProduceOrderId(req.getManufactureOrderId());
        cmd.setOuterProduceOrderCode(req.getManufactureOrder());

        // 第一步：构建以 partReq 为基准的冗余结构
        List<PartWithComponentInfo> partWithComponents = new ArrayList<>();
        for (SyncBomPartReq partReq : req.getPartList()) {
            partWithComponents.add(new PartWithComponentInfo(partReq));
        }

        // 第二步：按 size 进行 group，构造 OperateStyleSkuCommand
        Map<String, OperateStyleSkuCommand> skuMap = new LinkedHashMap<>();
        for (PartWithComponentInfo item : partWithComponents) {
            String sizeKey = item.partReq.getSize();
            OperateStyleSkuCommand sku = skuMap.computeIfAbsent(sizeKey, k -> {
                var skuCmd = new OperateStyleSkuCommand();
                skuCmd.setSizeId(item.partReq.getSizeId());
                skuCmd.setSize(item.partReq.getSize());
                skuCmd.setComponents(new ArrayList<>());
                return skuCmd;
            });

            // 检查该 sku 中是否已存在相同 partId 的 component
            String partId = item.partReq.getPartId();
            boolean partExists = sku.getComponents().stream()
                    .anyMatch(comp -> partId != null && partId.equals(comp.getPartId()));
            
            // 如果已存在相同 part，跳过
            if (partExists) {
                continue;
            }

            // 第三步：平铺开的第三层转为 OperateStyleComponentCommand
            var component = convertSyncBomPartReqToComponentCmd(item.partReq);

            // 构建纱线信息
            if (CollectionUtils.isNotEmpty(item.partReq.getProductList())) {
                List<StyleYarnUsageCommand> yarnUsages = item.partReq.getProductList().stream()
                        .map(product -> {
                            var yarn = new StyleYarnUsageCommand();
                            yarn.setYarnCode(product.getProductCode());
                            yarn.setLotNumber(product.getBatch());
                            yarn.setSupplierCode(product.getSupplierCode());
                            yarn.setTwist(product.getTwist());
                            yarn.setColor(product.getColor());
                            yarn.setPercentage(product.getPercentage() != null ?
                                    BigDecimal.valueOf(product.getPercentage()) : BigDecimal.ZERO);
                            return yarn;
                        })
                        .collect(Collectors.toList());
                component.setYarnUsages(yarnUsages);
            }

            sku.getComponents().add(component);
        }
        cmd.setSkuList(new ArrayList<>(skuMap.values()));

        return cmd;
    }

    private OperateStyleComponentCommand convertSyncBomPartReqToComponentCmd(SyncBomPartReq req) {
        var component = new OperateStyleComponentCommand();
        component.setPartId(req.getPartId());
        component.setPart(req.getPart());
        component.setColorId(req.getPartColorId());
        component.setColor(req.getPartColor());
        component.setType(1); // 默认类型
        component.setProgramFileName(req.getProgram());
        component.setCylinderDiameter(StringUtils.isBlank(req.getDiameter()) ? 0 : Integer.parseInt(req.getDiameter().split("寸")[0]));
        component.setNeedleSpacing(StringUtils.isBlank(req.getMachineType()) ? 0 : Integer.parseInt(req.getMachineType().split("G")[0]));
        component.setNumber(1);
        component.setStandardNumber(req.getStandardQuantity());
        component.setRatio(StringUtils.isBlank(req.getFinishRatio()) ? 1 : Integer.parseInt(req.getFinishRatio().split(":")[1])); // 默认比例
        component.setExpectedProduceTime(req.getFinishDuration() != null ?
                req.getFinishDuration().doubleValue() : 0.0);
        component.setExpectedWeight(req.getFinishWeight());
        component.setDescription(String.format("Part: %s, Color: %s",
                req.getPart(), req.getPartColor()));
        
        // 新增字段转换
        if (req.getFinishWaist() != null) {
            try {
                component.setFinishWidth(Double.parseDouble(req.getFinishWaist()));
            } catch (NumberFormatException e) {
                log.warn("Invalid finish width value: {}", req.getFinishWaist());
            }
        }
        if (req.getFinishLength() != null) {
            try {
                component.setFinishLength(Double.parseDouble(req.getFinishLength()));
            } catch (NumberFormatException e) {
                log.warn("Invalid finish length value: {}", req.getFinishLength());
            }
        }
        if (req.getWastageRate() != null) {
            try {
                component.setWasteRate(Double.parseDouble(req.getWastageRate()));
            } catch (NumberFormatException e) {
                log.warn("Invalid waste rate value: {}", req.getWastageRate());
            }
        }
        component.setDye(req.getDye());
        
        return component;
    }

    /**
     * 内部类：以 partReq 为基准的结构
     */
    private static class PartWithComponentInfo {
        SyncBomPartReq partReq;

        PartWithComponentInfo(SyncBomPartReq partReq) {
            this.partReq = partReq;
        }
    }
}
