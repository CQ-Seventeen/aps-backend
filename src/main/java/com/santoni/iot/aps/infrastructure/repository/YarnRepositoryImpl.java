package com.santoni.iot.aps.infrastructure.repository;

import com.santoni.iot.aps.domain.bom.entity.YarnProduct;
import com.santoni.iot.aps.domain.bom.repository.YarnRepository;
import com.santoni.iot.aps.infrastructure.database.aps.YarnProductMapper;
import com.santoni.iot.aps.infrastructure.factory.YarnProductFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YarnRepositoryImpl implements YarnRepository {

    @Autowired
    private YarnProductMapper yarnProductMapper;

    @Autowired
    private YarnProductFactory yarnProductFactory;

    @Override
    public void saveYarnProduce(YarnProduct yarnProduct) {
        var po = yarnProductFactory.convertYarnProductEntityToPO(yarnProduct);
        yarnProductMapper.insert(po);
    }
}

