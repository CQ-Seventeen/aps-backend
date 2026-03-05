package com.santoni.iot.aps.application.support.query;

import com.santoni.iot.aps.adapter.support.request.CommonPageRequest;
import lombok.Data;

@Data
public class CommonPageQuery {

    private int page;

    private int pageSize;

    public void copyPageParamFromPageRequest(CommonPageRequest commonPageRequest) {
        this.page = commonPageRequest.getPage();
        this.pageSize = commonPageRequest.getPageSize();
    }
}
