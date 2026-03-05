package com.santoni.iot.aps.application.support.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchOperateResultDTO {

    private int successNumber;

    private int failNumber;

    private List<String> failCodeList;
}
