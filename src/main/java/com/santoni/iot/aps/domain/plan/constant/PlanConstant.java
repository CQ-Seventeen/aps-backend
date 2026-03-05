package com.santoni.iot.aps.domain.plan.constant;

import java.math.BigDecimal;

public class PlanConstant {

    public static final double ALLOW_PER_PRODUCT_DEVIATION = 3.0;

    public final static int SECONDS_PER_DAY = 21 * 3600;

    public final static BigDecimal DEFAULT_PRODUCE_EFFICIENCY = new BigDecimal("0.875");;

    public final static BigDecimal SECONDS_ONE_DAY = BigDecimal.valueOf(24 * 3600);

    public final static int FIRST_SHIFT_END_HOUR = 7;

    public final static int SECOND_SHIFT_END_HOUR = 19;
}
