package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.domain.bom.constant.ComponentType;
import com.santoni.iot.aps.domain.bom.entity.valueobj.Number;
import com.santoni.iot.aps.domain.bom.entity.*;
import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.order.entity.valueobj.OuterOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.resource.entity.valueobj.BareSpandex;
import com.santoni.iot.aps.domain.resource.entity.valueobj.HighSpeed;
import com.santoni.iot.aps.domain.resource.entity.valueobj.MachineType;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.valueobj.*;
import com.santoni.iot.aps.infrastructure.po.StyleComponentPO;
import com.santoni.iot.aps.infrastructure.po.StylePO;
import com.santoni.iot.aps.infrastructure.po.StyleSkuPO;
import com.santoni.iot.aps.infrastructure.po.assistance.MachineRequirementPO;
import com.santoni.iot.aps.infrastructure.po.assistance.YarnUsagePO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class StyleFactory {

    @Autowired
    private MachineFactory machineFactory;

    public StyleSku composeSku(StyleSkuPO po, List<StyleComponentPO> components) {
        return new StyleSku(new StyleCode(po.getStyleCode()),
                new SkuCode(po.getCode()),
                new Size(po.getSizeId(), po.getSize()),
                StringUtils.isNotBlank(po.getProduceOrderCode()) ? new ProduceOrderCode(po.getProduceOrderCode()) : null,
                CollectionUtils.isEmpty(components) ? List.of() : components.stream().map(this::composeComponent).toList(),
                Double.compare(po.getExpectedProduceTime(), 0.0) < 0 ? null : new ExpectedProduceTime(po.getExpectedProduceTime())
        );
    }

    public StyleComponent composeComponent(StyleComponentPO po) {
        return new StyleComponent(new ComponentId(po.getId()),
                new SkuCode(po.getSkuCode()),
                StringUtils.isNotBlank(po.getProduceOrderCode()) ? new ProduceOrderCode(po.getProduceOrderCode()) : null,
                new Part(po.getPartId(), po.getPart()),
                StringUtils.isBlank(po.getColor()) ? null : new Color(po.getColorId(), po.getColor()),
                ComponentType.getByCode(po.getType()),
                StringUtils.isBlank(po.getProgramFile()) ? null : new ProgramFile(po.getProgramFile(), po.getProgramFileUrl()),
                MachineSize.styleSize(new CylinderDiameter(po.getCylinderDiameter()), new NeedleSpacing(po.getNeedleSpacing())),
                new ExpectedProduceTime(po.getExpectedProduceTime()),
                composeMachineRequirement(po.getMachineRequirement()),
                new Number(po.getNumber()),
                new ComponentRatio(po.getRatio()),
                null == po.getExpectedWeight() ? null : new ExpectedWeight(po.getExpectedWeight()),
                new StandardNumber(po.getStandardNumber()),
                StringUtils.isBlank(po.getDescription()) ? null : new StyleDesc(po.getDescription()),
                composeYarnUsageList(po.getYarnUsage()),
                new ProduceEfficiency(po.getDefaultEfficiency(), po.getActualEfficiency()),
                Double.compare(po.getFinishWidth(), 0.0) < 0 ? null : new FinishWidth(po.getFinishWidth()),
                Double.compare(po.getFinishLength(), 0.0) < 0 ? null : new FinishLength(po.getFinishLength()),
                Double.compare(po.getWasteRate(), 0.0) < 0 ? null : new WasteRate(po.getWasteRate()),
                StringUtils.isNotBlank(po.getDye()) ? new Dye(po.getDye()) : null
        );
    }

    public StyleSkuPO convertToStyleSkuPO(StyleSku styleSku) {
        var po = new StyleSkuPO();
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != styleSku.getProduceOrderCode()) {
            po.setProduceOrderCode(styleSku.getProduceOrderCode().value());
        }
        po.setStyleCode(styleSku.getStyleCode().value());
        po.setCode(styleSku.getCode().value());
        po.setSizeId(styleSku.getSize().id());
        po.setSize(styleSku.getSize().value());
        po.setExpectedProduceTime(styleSku.getExpectedProduceTime().value());
        return po;
    }

    public StyleComponentPO convertToStyleComponentPO(StyleComponent component) {
        var po = new StyleComponentPO();
        if (null != component.getComponentId()) {
            po.setId(component.getComponentId().value());
        }
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != component.getProduceOrderCode()) {
            po.setProduceOrderCode(component.getProduceOrderCode().value());
        }
        po.setSkuCode(component.getSkuCode().value());
        po.setPartId(component.getPart().id());
        po.setPart(component.getPart().value());
        if (null != component.getColor()) {
            po.setColorId(component.getColor().id());
            po.setColor(component.getColor().value());
        }
        po.setType(component.getType().getCode());
        if (null != component.getProgram()) {
            po.setProgramFile(component.getProgram().name());
            po.setProgramFileUrl(component.getProgram().url());
        }
        po.setNumber(component.getNumber().value());
        po.setRatio(component.getRatio().value());
        po.setCylinderDiameter(component.getMachineSize().getCylinderDiameter().value());
        po.setNeedleSpacing(component.getMachineSize().getNeedleSpacing().value());
        if (null != component.getStyleDesc()) {
            po.setDescription(component.getStyleDesc().value());
        }
        po.setExpectedProduceTime(component.getExpectedProduceTime().value());
        if (null != component.getExpectedWeight()) {
            po.setExpectedWeight(component.getExpectedWeight().value());
        }
        if (null != component.getStandardNumber()) {
            po.setStandardNumber(component.getStandardNumber().value());
        }
        if (null != component.getRequirement()) {
            po.setMachineRequirement(JacksonUtil.toJson(covertToMachineRequirementPO(component.getRequirement())));
        }
        if (null != component.getProduceEfficiency()) {
            po.setDefaultEfficiency(component.getProduceEfficiency().getDefaultEfficiency());
            po.setActualEfficiency(component.getProduceEfficiency().getActualEfficiency());
        }
        if (CollectionUtils.isNotEmpty(component.getYarnUsages())) {
            var poList = component.getYarnUsages().stream().map(this::convertToYarnUsagePO).toList();
            po.setYarnUsage(JacksonUtil.toJson(poList));
        }
        if (null != component.getFinishWidth()) {
            po.setFinishWidth(component.getFinishWidth().value());
        }
        if (null != component.getFinishLength()) {
            po.setFinishLength(component.getFinishLength().value());
        }
        if (null != component.getWasteRate()) {
            po.setWasteRate(component.getWasteRate().value());
        }
        if (null != component.getDye()) {
            po.setDye(component.getDye().value());
        }
        return po;
    }

    private YarnUsagePO convertToYarnUsagePO(YarnUsage yarnUsage) {
        var po = new YarnUsagePO();
        if (null != yarnUsage.getYarn()) {
            po.setYarnId(yarnUsage.getYarn().id());
            po.setYarnCode(yarnUsage.getYarn().code());
        }
        if (null != yarnUsage.getLotNumber()) {
            po.setLotNumber(yarnUsage.getLotNumber().value());
        }
        if (null != yarnUsage.getSupplierCode()) {
            po.setSupplierCode(yarnUsage.getSupplierCode().value());
        }
        if (null != yarnUsage.getTwist()) {
            po.setTwist(yarnUsage.getTwist().value());
        }
        if (null != yarnUsage.getColor()) {
            po.setColor(yarnUsage.getColor().value());
        }
        return po;
    }

    private List<YarnUsage> composeYarnUsageList(String yarnUsageStr) {
        if (StringUtils.isBlank(yarnUsageStr)) {
            return List.of();
        }
        var poList = JacksonUtil.readAsObjList(yarnUsageStr, YarnUsagePO.class);
        return poList.stream().map(this::composeYarnUsage).toList();
    }

    private YarnUsage composeYarnUsage(YarnUsagePO po) {
        return new YarnUsage(po.getYarnId(), po.getYarnCode(), po.getLotNumber(), po.getSupplierCode(),
                po.getTwist(), po.getColor(), po.getWeight(), po.getPercentage());
    }

    private MachineRequirement composeMachineRequirement(String requirementStr) {
        if (StringUtils.isBlank(requirementStr)) {
            return null;
        }
        var po = JacksonUtil.readAsObj(requirementStr, MachineRequirementPO.class);
        if (null == po) {
            return null;
        }
        return new MachineRequirement(
                CollectionUtils.isEmpty(po.getTypeList()) ? List.of() : po.getTypeList().stream().filter(Objects::nonNull).map(MachineType::new).toList(),
                CollectionUtils.isEmpty(po.getBareSpandexList()) ? List.of() : po.getBareSpandexList().stream().filter(Objects::nonNull).map(BareSpandex::new).toList(),
                null == po.getHighSpeed() ? null : po.getHighSpeed() ? HighSpeed.high() : HighSpeed.low(),
                machineFactory.composeMachineFeature(po.getFeatureMap())
        );
    }

    private MachineRequirementPO covertToMachineRequirementPO(MachineRequirement requirement) {
        var po = new MachineRequirementPO();
        if (CollectionUtils.isNotEmpty(requirement.getTypeList())) {
            po.setTypeList(requirement.getTypeList().stream().map(MachineType::value).toList());
        }
        if (CollectionUtils.isNotEmpty(requirement.getBareSpandexList())) {
            po.setBareSpandexList(requirement.getBareSpandexList().stream().map(BareSpandex::value).toList());
        }
        po.setFeatureMap(machineFactory.convertFeaturesToMap(requirement.getFeatureList()));
        return po;
    }

    public StyleSpu composeStyle(StylePO po) {
        var images = JacksonUtil.readAsObjList(po.getImages(), String.class)
                .stream().map(ImgUrl::new).toList();
        return new StyleSpu(new StyleId(po.getId()),
                StringUtils.isNotBlank(po.getProduceOrderId()) ? new OuterOrderId(po.getProduceOrderId()) : null,
                StringUtils.isNotBlank(po.getProduceOrderCode()) ? new ProduceOrderCode(po.getProduceOrderCode()) : null,
                StringUtils.isNotBlank(po.getStyleId()) ? new OuterStyleId(po.getStyleId()) : null,
                StringUtils.isNotBlank(po.getSymbol()) ? new Symbol(po.getSymbolId(), po.getSymbol()) : null,
                new StyleCode(po.getCode()),
                StringUtils.isBlank(po.getName()) ? null : new StyleName(po.getName()),
                StringUtils.isBlank(po.getDescription()) ? null : new StyleDesc(po.getDescription()),
                images);
    }

    public StylePO convertToStylePO(StyleSpu style) {
        var po = new StylePO();
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != style.getOuterProduceOrderId()) {
            po.setProduceOrderId(style.getOuterProduceOrderId().value());
        }
        if (null != style.getProduceOrderCode()) {
            po.setProduceOrderCode(style.getProduceOrderCode().value());
        }
        if (null != style.getOuterId()) {
            po.setStyleId(style.getOuterId().value());
        }
        po.setCode(style.getCode().value());
        if (null != style.getSymbol()) {
            po.setSymbolId(style.getSymbol().id());
            po.setSymbol(style.getSymbol().value());
        }
        if (null != style.getName()) {
            po.setName(style.getName().value());
        }
        if (null != style.getDescription()) {
            po.setDescription(style.getDescription().value());
        }
        if (CollectionUtils.isNotEmpty(style.getStyleImages())) {
            po.setImages(JacksonUtil.toJson(style.getStyleImages().stream().map(ImgUrl::value).toList()));
        }
        return po;
    }
}
