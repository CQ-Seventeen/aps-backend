package com.santoni.iot.aps.adapter.resource.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class AggregateMachineRequest {

    private List<String> aggregateKeys;

}
