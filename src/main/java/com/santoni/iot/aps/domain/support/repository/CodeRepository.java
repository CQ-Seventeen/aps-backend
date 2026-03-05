package com.santoni.iot.aps.domain.support.repository;

import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;

import java.util.List;

public interface CodeRepository {

    StyleCode getStyleCode();

    List<StyleCode> getMultiStyleCode(int num);

    WeavingOrderCode getWeavingOrderCode();

    List<WeavingOrderCode> getMultiWeavingOrderCode(int num);

    ProduceOrderCode getProduceOrderCode();

    List<ProduceOrderCode> getMultiProduceOrderCode(int num);

    CustomerCode getCustomerCode();
}
