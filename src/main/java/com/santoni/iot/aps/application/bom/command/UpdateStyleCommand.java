package com.santoni.iot.aps.application.bom.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateStyleCommand extends CreateStyleCommand {

    private long id;
}
