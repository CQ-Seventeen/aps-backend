package com.santoni.iot.aps.application.support.dto;

import com.santoni.iot.aps.domain.support.entity.PageData;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PageResult<T> {

    private List<T> data;

    private long total;

    private long currentPage;

    private long totalPage;

    public PageResult(List<T> data, long total, long currentPage, long totalPage) {
        this.data = data;
        this.total = total;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
    }

    public static <T, E> PageResult<T> empty(PageData<E> data) {
        return new PageResult<T>(Collections.emptyList(), data.getTotal(), data.getCurPage(), data.getTotalPage());
    }

    public static <T> PageResult<T> empty(long total, long currentPage, long totalPage) {
        return new PageResult<T>(Collections.emptyList(), total, currentPage, totalPage);
    }

    public static <T, E> PageResult<T> fromPageData(List<T> data, PageData<E> pageData) {
        return new PageResult<T>(data, pageData.getTotal(), pageData.getCurPage(), pageData.getTotalPage());
    }
}
