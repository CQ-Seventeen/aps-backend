package com.santoni.iot.aps.application.bom.command;

import com.santoni.iot.aps.application.resource.command.MachineFeatureCommand;
import lombok.Data;

import java.util.List;

@Data
public class MachineRequirementCommand {

    private List<String> type;

    private List<String> bareSpandexTypeList;

    private List<MachineFeatureCommand> otherAttrList;
}
