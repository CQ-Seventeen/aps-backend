package com.santoni.iot.aps.common.config;

import lombok.Data;

@Data
public class MinioProperties {
    private String address;
    private String openBucket;
    private String user;
    private String password;
    private String download;
}