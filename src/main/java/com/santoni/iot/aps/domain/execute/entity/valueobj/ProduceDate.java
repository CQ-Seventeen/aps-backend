package com.santoni.iot.aps.domain.execute.entity.valueobj;

import com.santoni.iot.aps.common.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record ProduceDate(String value) {

    public ProduceDate {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("日期不可为空");
        }
        if (!TimeUtil.isYYYYMMDD(value)) {
            throw new IllegalArgumentException("日期格式必须为yyyy-MM-dd");
        }
    }

    public ProduceDate prevDay() {
        return new ProduceDate(TimeUtil.getPrevDayStr(this.value, 1));
    }

    public boolean isToday() {
        return TimeUtil.isToday(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProduceDate that)) return false;
        return StringUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
