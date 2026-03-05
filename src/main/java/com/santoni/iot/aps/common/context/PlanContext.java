package com.santoni.iot.aps.common.context;

public class PlanContext {

    private static final ThreadLocal<Long> userContext = new ThreadLocal<>();

    private static final ThreadLocal<Long> instituteContext = new ThreadLocal<>();

    private static final ThreadLocal<Long> factoryContext = new ThreadLocal<>();

    public static long getUserId() {
        Long userId = userContext.get();
        return null == userId || userId <= 0 ? 1 : userId;
    }

    public static long getInstituteId() {
        Long instituteId = instituteContext.get();
        return null == instituteId ? 0 : instituteId;
    }

    public static long getFactoryId() {
        Long factoryId = factoryContext.get();
        return null == factoryId ? 0 : factoryId;
    }

    public static void setUserId(long userId) {
        userContext.set(userId);
    }

    public static void setInstituteId(long instituteId) {
        instituteContext.set(instituteId);
    }

    public static void setFactoryId(long factoryId) {
        factoryContext.set(factoryId);
    }

    public static void clear() {
        userContext.remove();
        instituteContext.remove();
        factoryContext.remove();
    }
}
