package com.santoni.iot.aps.domain.support.entity.valueobj;

import org.apache.commons.lang3.StringUtils;

public record ImgUrl(String value) {

    public ImgUrl {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("图片链接不可为空");
        }
    }
}
