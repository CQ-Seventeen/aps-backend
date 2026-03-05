package com.santoni.iot.aps.domain.bom.service.impl;

import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuAndComponentMap;
import com.santoni.iot.aps.domain.bom.service.BomDomainService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BomDomainServiceImpl implements BomDomainService {
    @Override
    public SkuAndComponentMap rebuildSkuList(List<StyleSku> skuList) {
        var midRes = skuList.stream()
                .collect(Collectors.teeing(
                        Collectors.flatMapping(it -> it.getComponents().stream(),
                                Collectors.groupingBy(it -> it.getMachineSize().getCylinderDiameter().value())),
                        Collectors.toMap(it -> it.getCode().value(), it -> it, (v1, v2) -> v2),
                        Pair::of));
        return new SkuAndComponentMap(midRes.getRight(), midRes.getLeft());
    }
}
