package com.santoni.iot.aps.application.support.assembler;

import com.santoni.iot.aps.application.support.command.CreateCustomerCommand;
import com.santoni.iot.aps.application.support.dto.CustomerDTO;
import com.santoni.iot.aps.domain.support.entity.Customer;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerName;
import org.springframework.stereotype.Component;

@Component
public class CustomerAssembler {

    public Customer composeCustomerFromCreateCmd(CreateCustomerCommand cmd, CustomerCode code) {
        return Customer.newOf(code,
                new CustomerName(cmd.getName()));
    }

    public CustomerDTO convertToCustomerDTO(Customer customer) {
        var dto = new CustomerDTO();
        dto.setId(customer.getId().value());
        dto.setCode(customer.getCode().value());
        if (null != customer.getName()) {
            dto.setName(customer.getName().value());
        }
        return dto;
    }
}
