package com.santoni.iot.aps.application.bom.assembler;

import com.santoni.iot.aps.application.bom.command.OperateOuterYarnCommand;
import com.santoni.iot.aps.domain.bom.entity.YarnProduct;
import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.valueobj.Color;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class YarnAssembler {

    public YarnProduct composeYarnProductFromCmd(OperateOuterYarnCommand cmd) {
        return new YarnProduct(
                new Yarn(cmd.getProductId(), cmd.getProductCode()),
                new YarnName(cmd.getProductName()),
                new PackageUnit(cmd.getPackageUnit()),
                StringUtils.isNotBlank(cmd.getBatch()) ? new LotNumber(cmd.getBatch()) : null,
                StringUtils.isNotBlank(cmd.getSupplierCode()) ? new SupplierCode(cmd.getSupplierCode()) : null,
                StringUtils.isNotBlank(cmd.getTwist()) ? new Twist(cmd.getTwist()) : null,
                StringUtils.isNotBlank(cmd.getColor()) ? new Color(null, cmd.getColor()) : null,
                StringUtils.isNotBlank(cmd.getDescription()) ? new YarnDesc(cmd.getDescription()) : null
        );
    }
}

