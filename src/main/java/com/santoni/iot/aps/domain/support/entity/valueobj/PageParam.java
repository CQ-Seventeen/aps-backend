package com.santoni.iot.aps.domain.support.entity.valueobj;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import lombok.Data;

@Data
public class PageParam {

    private int page;

    private int pageSize;

    protected void fromPageQuery(CommonPageQuery query) {
        this.page = query.getPage();
        this.pageSize = query.getPageSize();
    }
}
