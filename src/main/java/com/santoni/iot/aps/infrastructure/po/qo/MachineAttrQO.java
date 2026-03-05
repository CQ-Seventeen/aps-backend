package com.santoni.iot.aps.infrastructure.po.qo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MachineAttrQO {

    private String attrName;

    private List<String> attrValue;
}
