package com.santoni.iot.aps.application.schedule.query;

import com.santoni.iot.aps.application.support.query.CommonPageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageScheduleLogQuery extends CommonPageQuery {

    private Integer type;

}
