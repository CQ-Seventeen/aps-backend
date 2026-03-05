package com.santoni.iot.aps.adapter.support.request;

import lombok.Data;

@Data
public class CommonPageRequest {

    private int page = 1;

    private int pageSize = 10;
}
