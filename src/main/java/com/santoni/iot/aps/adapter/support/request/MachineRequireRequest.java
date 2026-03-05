package com.santoni.iot.aps.adapter.support.request;

import com.santoni.iot.aps.adapter.resource.request.MachineFeatureRequest;
import lombok.Data;

import java.util.List;

@Data
public class MachineRequireRequest {

    private List<String> type;

    private List<String> bareSpandex;

    private List<MachineFeatureRequest> otherAttrList;
}
