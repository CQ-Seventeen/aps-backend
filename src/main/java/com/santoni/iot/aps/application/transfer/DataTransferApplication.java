package com.santoni.iot.aps.application.transfer;

import com.santoni.iot.aps.application.transfer.dto.ExcelTemplateDTO;

import java.util.List;

public interface DataTransferApplication {

    List<ExcelTemplateDTO> listAllExcelTemplate();
}
