package com.santoni.iot.aps.application.transfer.dto;

import lombok.Data;

@Data
public class ExcelTemplateDTO {

    private String operateObject;

    private String importTemplate;

    private String exportTemplate;
}
