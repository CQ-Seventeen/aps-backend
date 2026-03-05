package com.santoni.iot.aps.application.bom;

import com.santoni.iot.aps.application.bom.command.OperateOuterYarnCommand;

public interface YarnApplication {

    void importOuterYarn(OperateOuterYarnCommand command);
}
