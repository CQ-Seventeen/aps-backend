package com.santoni.iot.aps.application.plan;

import com.santoni.iot.aps.application.plan.command.AssignOrderToFactoryCommand;

public interface FactoryPlanOperateApplication {

    void assignOrderToFactory(AssignOrderToFactoryCommand command);

}
