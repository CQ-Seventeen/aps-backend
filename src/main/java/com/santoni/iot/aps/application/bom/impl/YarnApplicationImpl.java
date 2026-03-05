package com.santoni.iot.aps.application.bom.impl;

import com.santoni.iot.aps.application.bom.YarnApplication;
import com.santoni.iot.aps.application.bom.assembler.YarnAssembler;
import com.santoni.iot.aps.application.bom.command.OperateOuterYarnCommand;
import com.santoni.iot.aps.domain.bom.repository.YarnRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class YarnApplicationImpl implements YarnApplication {

    @Autowired
    private YarnAssembler yarnAssembler;

    @Autowired
    private YarnRepository yarnRepository;

    @Override
    @Transactional
    public void importOuterYarn(OperateOuterYarnCommand command) {
        log.info("Importing outer yarn product: code={}, name={}", command.getProductCode(), command.getProductName());
        try {
            var yarnProduct = yarnAssembler.composeYarnProductFromCmd(command);
            yarnRepository.saveYarnProduce(yarnProduct);
            log.info("Yarn product imported successfully: code={}", command.getProductCode());
        } catch (Exception e) {
            log.error("Failed to import yarn product: code={}", command.getProductCode(), e);
            throw e;
        }
    }
}
