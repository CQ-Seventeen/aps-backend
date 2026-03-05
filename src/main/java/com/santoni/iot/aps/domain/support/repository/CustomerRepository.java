package com.santoni.iot.aps.domain.support.repository;

import com.santoni.iot.aps.application.support.query.PageCustomerQuery;
import com.santoni.iot.aps.domain.support.entity.Customer;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;

import java.util.Collection;
import java.util.List;

public interface CustomerRepository {

    Customer getCustomerByCode(CustomerCode code);

    List<Customer> listCustomerByCode(Collection<CustomerCode> codeList);

    void saveCustomer(Customer customer);

    void batchSaveCustomer(List<Customer> customerList);

    PageData<Customer> pageQueryCustomer(PageCustomerQuery query);

}
