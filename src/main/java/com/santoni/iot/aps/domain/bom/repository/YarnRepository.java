package com.santoni.iot.aps.domain.bom.repository;

import com.santoni.iot.aps.domain.bom.entity.YarnProduct;

public interface YarnRepository {

    void saveYarnProduce(YarnProduct yarnProduct);
}
