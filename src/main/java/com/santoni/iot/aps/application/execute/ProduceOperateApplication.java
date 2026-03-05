package com.santoni.iot.aps.application.execute;

import com.santoni.iot.aps.application.execute.command.RecordMachineProductionCommand;
import com.santoni.iot.aps.application.execute.command.WriteProductionToTaskCommand;

public interface ProduceOperateApplication {

    void removeRecord();

    void writeProductionToTask(WriteProductionToTaskCommand command);

    void sumProduction();

    void reportProduction(RecordMachineProductionCommand command);
}
