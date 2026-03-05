package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot;

public class OccupiedTimeByNode {

    private double[][] arr;

    public OccupiedTimeByNode(int length) {
        arr = new double[2][length];
    }

    public void occupyTime(double[] timeArr, boolean bareSpandex) {
        if (bareSpandex) {
            arr[0] = timeArr;
        } else {
            arr[1] = timeArr;
        }
    }

    public double[][] getAllOccupiedTime() {
        return arr;
    }

    public double[] getOccupiedBareSpandexTime() {
        return arr[0];
    }

    public double[] getOccupiedNonBareSpandexTime() {
        return arr[1];
    }
}
