package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateCustomerRequest extends CreateCustomerRequest {

    private long id;

}
