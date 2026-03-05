package com.santoni.iot.aps.application.transfer.impl;

import com.santoni.iot.aps.application.transfer.DataTransferApplication;
import com.santoni.iot.aps.application.transfer.dto.ExcelTemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataTransferApplicationImpl implements DataTransferApplication {

    @Override
    public List<ExcelTemplateDTO> listAllExcelTemplate() {
        return List.of();
    }
}
