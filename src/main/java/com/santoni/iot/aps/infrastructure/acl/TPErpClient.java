package com.santoni.iot.aps.infrastructure.acl;

import com.santoni.iot.aps.application.plan.context.SyncTaskContext;
import com.santoni.iot.aps.domain.bom.entity.YarnStock;
import com.santoni.iot.aps.domain.bom.entity.YarnUsage;

import java.util.List;

public interface TPErpClient {

    List<YarnStock> queryYarnStock(List<YarnUsage> yarnList);

    boolean syncPlanTask(SyncTaskContext context);
}
