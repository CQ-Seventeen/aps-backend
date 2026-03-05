package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryCustomerRequest extends CommonPageRequest{

    private String code;

}
