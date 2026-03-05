package com.santoni.iot.aps.application.support.command;

import lombok.Data;

@Data
public class CreateCustomerCommand {

    private String code;

    private String name;
}
