package com.santoni.iot.aps.adapter.resource.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateMachineRequest extends CreateMachineRequest {

    private long id;

}
