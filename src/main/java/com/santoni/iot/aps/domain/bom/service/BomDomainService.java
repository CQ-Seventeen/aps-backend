package com.santoni.iot.aps.domain.bom.service;

import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuAndComponentMap;

import java.util.List;

public interface BomDomainService {

    SkuAndComponentMap rebuildSkuList(List<StyleSku> skuList);
}
