package com.santoni.iot.aps.infrastructure.factory;

import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.support.entity.Customer;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerName;
import com.santoni.iot.aps.infrastructure.po.CustomerPO;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory {

    public Customer composeCustomer(CustomerPO po) {
        return new Customer(new CustomerId(po.getId()),
                new CustomerCode(po.getCode()),
                new CustomerName(po.getName()));
    }

    public CustomerPO convertToCustomerPO(Customer customer) {
        var po = new CustomerPO();
        po.setInstituteId(PlanContext.getInstituteId());
        po.setCode(customer.getCode().value());
        if (null != customer.getName()) {
            po.setName(customer.getName().value());
        }
        return po;
    }
}
