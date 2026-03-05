package com.santoni.iot.aps.application.resource.query;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageMachineQuery extends CommonPageQuery {

    private Long factoryId;

    private List<Long> factoryIds;

    private List<Long> workshopIds;

    private List<Long> machineGroupIds;

    private List<Integer> statusList;

    private List<Integer> cylinderDiameterList;

    private List<Integer> needleSpacingList;

    private List<Integer> needleNumberList;

    private List<String> machineTypeList;

    private List<String> bareSpandexList;

    private Boolean highSpeed;

    private List<MachineFeatureQuery> featureList;

    private List<String> areaList;
}
