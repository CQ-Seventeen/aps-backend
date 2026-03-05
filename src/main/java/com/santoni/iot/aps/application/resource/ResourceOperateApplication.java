package com.santoni.iot.aps.application.resource;

import com.santoni.iot.aps.application.resource.command.BatchCreateMachineCommand;
import com.santoni.iot.aps.application.resource.command.CreateMachineCommand;
import com.santoni.iot.aps.application.resource.command.OperateMachineAttrConfigCommand;
import com.santoni.iot.aps.application.resource.command.UpdateMachineCommand;
import com.santoni.iot.aps.application.support.dto.BatchOperateResultDTO;

public interface ResourceOperateApplication {

    BatchOperateResultDTO batchCreateMachine(BatchCreateMachineCommand cmd);

    void createMachine(CreateMachineCommand command);

    void updateMachine(UpdateMachineCommand command);

    void createMachineAttr(OperateMachineAttrConfigCommand command);

    void updateMachineAttr(OperateMachineAttrConfigCommand command);
}
