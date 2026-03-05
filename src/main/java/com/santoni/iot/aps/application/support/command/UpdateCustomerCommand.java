package com.santoni.iot.aps.application.support.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateCustomerCommand extends CreateCustomerCommand {

    private long id;
}
