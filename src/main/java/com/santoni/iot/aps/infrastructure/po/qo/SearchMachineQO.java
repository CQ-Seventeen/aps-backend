package com.santoni.iot.aps.infrastructure.po.qo;

import lombok.Data;

import java.util.List;

@Data
public class SearchMachineQO {

    private long instituteId;

    private Long factoryId;

    private List<Long> factoryIds;

    private List<Long> workshopIds;

    private List<Long> groupIds;

    private List<Integer> statusList;

    private List<Integer> cylinderDiameterList;

    private List<Integer> needleSpacingList;

    private List<Integer> needleNumberList;

    private List<String> typeList;

    private List<String> bareSpandexTypeList;

    private List<MachineSizeQO> sizeList;

    private List<MachineAttrQO> attrList;

    private Boolean highSpeed;

    private List<String> areaList;
}
