package com.santoni.iot.aps.application.bom.command;

import lombok.Data;

import java.util.List;

@Data
public class BatchCreateStyleCommand {

    private List<CreateStyleCommand> styleList;
}
