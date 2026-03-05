package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.bom.entity.YarnProduct;
import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import com.santoni.iot.aps.infrastructure.po.YarnProductPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class YarnProductFactory {

    public YarnProduct composeYarnProduct(YarnProductPO po) {
        return new YarnProduct(
                new Yarn(po.getOuterId(), po.getYarnCode()),
                new YarnName(po.getYarnName()),
                new PackageUnit(po.getPackageUnit()),
                StringUtils.isNotBlank(po.getBatch()) ? new LotNumber(po.getBatch()) : null,
                StringUtils.isNotBlank(po.getSupplierCode()) ? new SupplierCode(po.getSupplierCode()) : null,
                StringUtils.isNotBlank(po.getTwist()) ? new Twist(po.getTwist()) : null,
                StringUtils.isNotBlank(po.getColor()) ? new Color(po.getColorId(), po.getColor()) : null,
                StringUtils.isNotBlank(po.getDescription()) ? new YarnDesc(po.getDescription()) : null
        );
    }

    public YarnProductPO convertYarnProductEntityToPO(YarnProduct yarnProduct) {
        var po = new YarnProductPO();
        po.setInstituteId(PlanContext.getInstituteId());
        if (null != yarnProduct.getYarn()) {
            po.setOuterId(yarnProduct.getYarn().id());
            po.setYarnCode(yarnProduct.getYarn().code());
        }
        po.setYarnName(yarnProduct.getYarnName().value());
        po.setPackageUnit(yarnProduct.getPackageUnit().value());
        if (null != yarnProduct.getLotNumber()) {
            po.setBatch(yarnProduct.getLotNumber().value());
        }
        if (null != yarnProduct.getSupplierCode()) {
            po.setSupplierCode(yarnProduct.getSupplierCode().value());
        }
        if (null != yarnProduct.getTwist()) {
            po.setTwist(yarnProduct.getTwist().value());
        }
        if (null != yarnProduct.getColor()) {
            po.setColorId(yarnProduct.getColor().id());
            po.setColor(yarnProduct.getColor().value());
        }
        if (null != yarnProduct.getDesc()) {
            po.setDescription(yarnProduct.getDesc().value());
        }
        return po;
    }
}

