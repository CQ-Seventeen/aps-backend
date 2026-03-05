package com.santoni.iot.aps.application.support.impl;

import com.santoni.iot.aps.application.support.CustomerApplication;
import com.santoni.iot.aps.application.support.assembler.CustomerAssembler;
import com.santoni.iot.aps.application.support.command.CreateCustomerCommand;
import com.santoni.iot.aps.application.support.command.UpdateCustomerCommand;
import com.santoni.iot.aps.application.support.dto.CustomerDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.application.support.query.PageCustomerQuery;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.repository.CodeRepository;
import com.santoni.iot.aps.domain.support.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerApplicationImpl implements CustomerApplication {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAssembler customerAssembler;

    @Autowired
    private CodeRepository codeRepository;

    @Override
    public void createCustomer(CreateCustomerCommand cmd) {
        CustomerCode customerCode;
        if (StringUtils.isBlank(cmd.getCode())) {
            customerCode = codeRepository.getCustomerCode();
        } else {
            customerCode = new CustomerCode(cmd.getCode());
        }
        checkCustomerExist(customerCode);

        var customer = customerAssembler.composeCustomerFromCreateCmd(cmd, customerCode);
        customerRepository.saveCustomer(customer);
    }

    @Override
    public void updateCustomer(UpdateCustomerCommand cmd) {

    }

    @Override
    public PageResult<CustomerDTO> pageQueryCustomer(PageCustomerQuery query) {
        var pageRes = customerRepository.pageQueryCustomer(query);
        if (CollectionUtils.isEmpty(pageRes.getData())) {
            return PageResult.empty(pageRes);
        }
        var customerList = pageRes.getData().stream().map(it -> customerAssembler.convertToCustomerDTO(it)).toList();
        return PageResult.fromPageData(customerList, pageRes);
    }

    private void checkCustomerExist(CustomerCode customerCode) {
        var exist = customerRepository.getCustomerByCode(customerCode);
        if (null != exist) {
            throw new IllegalArgumentException("客户已存在,编号:" + customerCode.value());
        }
    }
}
