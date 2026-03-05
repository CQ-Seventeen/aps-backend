package com.santoni.iot.aps.domain.support.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Getter;

import java.util.List;

@Getter
public class PageData<T> {

    private List<T> data;

    private long total;

    private long curPage;

    private long totalPage;

    public PageData(List<T> data, long total, long curPage, long totalPage) {
        this.data = data;
        this.total = total;
        this.curPage = curPage;
        this.totalPage = totalPage;
    }

    public static <T, K> PageData<T> fromPage(List<T> data, IPage<K> page) {
        return new PageData<>(data, page.getTotal(), page.getCurrent(), page.getPages());
    }
}
