package com.santoni.iot.aps.adapter.resource.request;

import com.santoni.iot.aps.adapter.support.request.CommonPageRequest;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryMachineRequest extends CommonPageRequest {

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

    private List<MachineFeatureRequest> featureList;

    private List<String> areaList;
}
