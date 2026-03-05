package com.santoni.iot.aps.application.support.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageCustomerQuery extends CommonPageQuery{

    private String code;

}
