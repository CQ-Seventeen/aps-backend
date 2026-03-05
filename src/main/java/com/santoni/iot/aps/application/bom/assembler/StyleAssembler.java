package com.santoni.iot.aps.application.bom.assembler;

import com.santoni.iot.aps.application.bom.command.*;
import com.santoni.iot.aps.application.bom.dto.*;
import com.santoni.iot.aps.application.resource.assembler.MachineAssembler;
import com.santoni.iot.aps.application.resource.assembler.MachineDTOAssembler;
import com.santoni.iot.aps.domain.bom.constant.ComponentType;
import com.santoni.iot.aps.domain.bom.entity.*;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Number;
import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.order.entity.valueobj.OuterOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.constant.PlanConstant;
import com.santoni.iot.aps.domain.resource.entity.valueobj.BareSpandex;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineType;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.valueobj.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StyleAssembler {

    @Autowired
    private MachineAssembler machineAssembler;

    @Autowired
    private MachineDTOAssembler machineDTOAssembler;

    public StyleSpu composeStyleFromCreateCmd(CreateStyleCommand cmd, StyleCode code) {
        return StyleSpu.of(StringUtils.isNotBlank(cmd.getOuterProduceOrderId()) ? new OuterOrderId(cmd.getOuterProduceOrderId()) : null,
                StringUtils.isNotBlank(cmd.getOuterProduceOrderCode()) ? new ProduceOrderCode(cmd.getOuterProduceOrderCode()) : null,
                StringUtils.isNotBlank(cmd.getOuterId()) ? new OuterStyleId(cmd.getOuterId()) : null,
                StringUtils.isNotBlank(cmd.getSymbol()) ? new Symbol(cmd.getSymbolId(), cmd.getSymbol()) : null,
                code,
                StringUtils.isBlank(cmd.getName()) ? null : new StyleName(cmd.getName()),
                StringUtils.isBlank(cmd.getDescription()) ? null : new StyleDesc(cmd.getDescription()),
                CollectionUtils.isEmpty(cmd.getImgUrls()) ? List.of() : cmd.getImgUrls().stream().map(ImgUrl::new).toList());
    }

    public StyleSpu composeStyleFromUpdateCmd(UpdateStyleCommand cmd) {
        return new StyleSpu(
                new StyleId(cmd.getId()),
                StringUtils.isNotBlank(cmd.getOuterProduceOrderId()) ? new OuterOrderId(cmd.getOuterProduceOrderId()) : null,
                StringUtils.isNotBlank(cmd.getOuterProduceOrderCode()) ? new ProduceOrderCode(cmd.getOuterProduceOrderCode()) : null,
                StringUtils.isNotBlank(cmd.getOuterId()) ? new OuterStyleId(cmd.getOuterId()) : null,
                StringUtils.isNotBlank(cmd.getSymbol()) ? new Symbol(cmd.getSymbolId(), cmd.getSymbol()) : null,
                new StyleCode(cmd.getCode()),
                StringUtils.isBlank(cmd.getName()) ? null : new StyleName(cmd.getName()),
                StringUtils.isBlank(cmd.getDescription()) ? null : new StyleDesc(cmd.getDescription()),
                CollectionUtils.isEmpty(cmd.getImgUrls()) ? List.of() : cmd.getImgUrls().stream().map(ImgUrl::new).toList());
    }

    public StyleSku composeStyleSkuFromCmd(StyleCode styleCode, OperateStyleSkuCommand cmd, ProduceOrderCode produceOrderCode) {
        return StyleSku.of(styleCode, produceOrderCode, new Size(cmd.getSizeId(), cmd.getSize()));
    }

    public StyleComponent composeStyleComponentFromCmd(SkuCode skuCode, OperateStyleComponentCommand cmd, ProduceOrderCode produceOrderCode) {
        return new StyleComponent(null,
                skuCode,
                produceOrderCode,
                new Part(cmd.getPartId(), cmd.getPart()),
                StringUtils.isBlank(cmd.getColor()) ? null : new Color(cmd.getColorId(), cmd.getColor()),
                ComponentType.getByCode(cmd.getType()),
                StringUtils.isBlank(cmd.getProgramFileName()) ? null : new ProgramFile(cmd.getProgramFileName(), cmd.getProgramFileUrl()),
                MachineSize.styleSize(new CylinderDiameter(cmd.getCylinderDiameter()), new NeedleSpacing(cmd.getNeedleSpacing())),
                new ExpectedProduceTime(cmd.getExpectedProduceTime()),
                composeMachineRequirement(cmd.getRequirement()),
                new Number(cmd.getNumber()),
                new ComponentRatio(cmd.getRatio()),
                null == cmd.getExpectedWeight() ? null : new ExpectedWeight(cmd.getExpectedWeight()),
                new StandardNumber(cmd.getStandardNumber()),
                StringUtils.isBlank(cmd.getDescription()) ? null : new StyleDesc(cmd.getDescription()),
                CollectionUtils.isEmpty(cmd.getYarnUsages()) ? Collections.emptyList()
                        : cmd.getYarnUsages().stream().map(it -> new YarnUsage(it.getYarnId(), it.getYarnCode(), it.getLotNumber(), it.getSupplierCode(),
                        it.getTwist(), it.getColor(), null, it.getPercentage())).toList(),
                new ProduceEfficiency(cmd.getDefaultProduceEfficiency(), cmd.getActualProduceEfficiency()),
                null == cmd.getFinishWidth() ? null : new FinishWidth(cmd.getFinishWidth()),
                null == cmd.getFinishLength() ? null : new FinishLength(cmd.getFinishLength()),
                null == cmd.getWasteRate() ? null : new WasteRate(cmd.getWasteRate()),
                StringUtils.isBlank(cmd.getDye()) ? null : new Dye(cmd.getDye())
        );
    }

    private MachineRequirement composeMachineRequirement(MachineRequirementCommand cmd) {
        if (null == cmd) {
            return null;
        }
        return new MachineRequirement(
                CollectionUtils.isEmpty(cmd.getType()) ? List.of() : cmd.getType().stream().map(MachineType::new).toList(),
                CollectionUtils.isEmpty(cmd.getBareSpandexTypeList()) ? List.of() : cmd.getBareSpandexTypeList().stream().map(BareSpandex::new).toList(),
                null,
                CollectionUtils.isEmpty(cmd.getOtherAttrList()) ? List.of() : cmd.getOtherAttrList().stream().map(it -> machineAssembler.composeMachineFeature(it)).toList()
        );
    }

    public StyleDTO assembleStyleDTO(StyleSpu style, List<StyleSku> skuList) {
        var dto = new StyleDTO();
        dto.setId(style.getId().value());
        if (null != style.getOuterProduceOrderId()) {
            dto.setProduceOrderId(style.getOuterProduceOrderId().value());
        }
        if (null != style.getProduceOrderCode()) {
            dto.setProduceOrderCode(style.getProduceOrderCode().value());
        }
        if (null != style.getOuterId()) {
            dto.setOuterStyleId(style.getOuterId().value());
        }
        dto.setCode(style.getCode().value());
        if (null != style.getSymbol()) {
            dto.setSymbolId(style.getSymbol().id());
            dto.setSymbol(style.getSymbol().value());
        }
        if (null != style.getName()) {
            dto.setName(style.getName().value());
        }
        if (null != style.getDescription()) {
            dto.setDescription(style.getDescription().value());
        }
        if (CollectionUtils.isNotEmpty(style.getStyleImages())) {
            dto.setImgUrls(style.getStyleImages().stream().map(ImgUrl::value).toList());
        }
        if (CollectionUtils.isNotEmpty(skuList)) {
            dto.setSkuList(skuList.stream().map(this::assembleStyleSkuDTO).toList());
        }
        return dto;
    }

    public StyleSkuDTO assembleStyleSkuDTO(StyleSku sku) {
        var dto = new StyleSkuDTO();
        dto.setStyleCode(sku.getStyleCode().value());
        dto.setSizeId(sku.getSize().id());
        dto.setSize(sku.getSize().value());
        dto.setSkuCode(sku.getCode().value());
        if (CollectionUtils.isNotEmpty(sku.getComponents())) {
            dto.setComponents(sku.getComponents().stream().map(this::assembleStyleComponentDTO).toList());
        }
        return dto;
    }

    public StyleComponentDTO assembleStyleComponentDTO(StyleComponent component) {
        var dto = new StyleComponentDTO();
        dto.setSkuCode(component.getSkuCode().value());
        if (null != component.getPart()) {
            dto.setPartId(component.getPart().id());
            dto.setPart(component.getPart().value());
        }
        if (null != component.getColor()) {
            dto.setColorId(component.getColor().id());
            dto.setColor(component.getColor().value());
        }
        dto.setType(component.getType().getCode());
        dto.setNumber(component.getNumber().value());
        dto.setRatio(component.getRatio().value());
        if (null != component.getMachineSize()) {
            dto.setCylinderDiameter(component.getMachineSize().getCylinderDiameter().value());
            dto.setNeedleSpacing(component.getMachineSize().getNeedleSpacing().value());
        }
        if (null != component.getProgram()) {
            dto.setProgramFile(component.getProgram().name());
        }
        if (null != component.getStyleDesc()) {
            dto.setDescription(component.getStyleDesc().value());
        }
        dto.setExpectedProduceTime(component.getExpectedProduceTime().value());
        if (null != component.getExpectedWeight()) {
            dto.setExpectedWeight(component.getExpectedWeight().value());
        }
        if (null != component.getStandardNumber()) {
            dto.setStandardNumber(component.getStandardNumber().value());
        }
        if (null != component.getRequirement()) {
            dto.setMachineRequirement(assembleMachineRequirementDTO(component.getRequirement()));
        }
        if (null != component.getProduceEfficiency()) {
            dto.setDefaultProduceEfficiency(component.getProduceEfficiency().getDefaultEfficiency());
            dto.setActualProduceEfficiency(component.getProduceEfficiency().getActualEfficiency());
        } else {
            dto.setDefaultProduceEfficiency(PlanConstant.DEFAULT_PRODUCE_EFFICIENCY);
        }
        if (CollectionUtils.isNotEmpty(component.getYarnUsages())) {
            dto.setYarnUsages(component.getYarnUsages().stream().map(this::convertToYarnUsageDTO).toList());
        }
        dto.setDailyTheoreticalQuantity(component.getDailyTheoreticalQuantity().getValue());
        return dto;
    }

    public YarnUsageDTO convertToYarnUsageDTO(YarnUsage yarnUsage) {
        var dto = new YarnUsageDTO();
        if (null != yarnUsage.getYarn()) {
            dto.setYarnId(yarnUsage.getYarn().code());
            dto.setYarnCode(yarnUsage.getYarn().code());
        }
        if (null != yarnUsage.getLotNumber()) {
            dto.setLotNumber(yarnUsage.getLotNumber().value());
        }
        if (null != yarnUsage.getSupplierCode()) {
            dto.setSupplierCode(yarnUsage.getSupplierCode().value());
        }
        if (null != yarnUsage.getTwist()) {
            dto.setTwist(yarnUsage.getTwist().value());
        }
        if (null != yarnUsage.getColor()) {
            dto.setColor(yarnUsage.getColor().value());
        }
        return dto;
    }

    private MachineRequirementDTO assembleMachineRequirementDTO(MachineRequirement requirement) {
        var dto = new MachineRequirementDTO();
        if (CollectionUtils.isNotEmpty(requirement.getTypeList())) {
            dto.setTypeList(requirement.getTypeList().stream().map(MachineType::value).toList());
        }
        if (CollectionUtils.isNotEmpty(requirement.getBareSpandexList())) {
            dto.setBareSpandexList(requirement.getBareSpandexList().stream().map(BareSpandex::value).toList());
        }
        if (CollectionUtils.isNotEmpty(requirement.getFeatureList())) {
            dto.setFeatureList(requirement.getFeatureList().stream().map(it -> machineDTOAssembler.assembleMachineFeatureDTO(it)).toList());
        }
        return dto;
    }
}
