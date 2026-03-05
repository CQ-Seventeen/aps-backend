package com.santoni.iot.aps.application.bom.query;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageStyleQuery extends CommonPageQuery {

    private String code;

}
