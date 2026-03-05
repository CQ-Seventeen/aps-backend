package com.santoni.iot.aps.domain.plan.entity.valueobj;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SortIndex {

    private int value;

    private SortIndex(int value) {
        this.value = value;
    }

    public static SortIndex of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("任务段顺序不可小于0");
        }
        return new SortIndex(value);
    }

    public static SortIndex init() {
        return new SortIndex(0);
    }

    public SortIndex next() {
        int value = this.value + 1;
        return new SortIndex(value);
    }

    public void backward() {
        value++;
    }

    public void forward() {
        if (value > 0) {
            value--;
        } else {
            throw new UnsupportedOperationException("无法将首位的任务段向前置");
        }
    }

    public boolean after(SortIndex other) {
        return this.value > other.value;
    }
}
