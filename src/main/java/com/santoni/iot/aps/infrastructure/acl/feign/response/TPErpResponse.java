package com.santoni.iot.aps.infrastructure.acl.feign.response;

import lombok.Data;

@Data
public class TPErpResponse<T> {

    private int code;
    private String message;
    private T data;
}
