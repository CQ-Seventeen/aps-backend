package com.santoni.iot.aps.application.support;

import com.santoni.iot.aps.application.support.command.CreateCustomerCommand;
import com.santoni.iot.aps.application.support.command.UpdateCustomerCommand;
import com.santoni.iot.aps.application.support.dto.CustomerDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.application.support.query.PageCustomerQuery;

public interface CustomerApplication {

    void createCustomer(CreateCustomerCommand cmd);

    void updateCustomer(UpdateCustomerCommand cmd);

    PageResult<CustomerDTO> pageQueryCustomer(PageCustomerQuery query);
}
