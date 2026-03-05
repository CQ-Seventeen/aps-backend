package com.santoni.iot.aps.domain.support.entity;

import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerId;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerName;
import lombok.Getter;

@Getter
public class Customer {

    private CustomerId id;

    private CustomerCode code;

    private CustomerName name;

    public Customer(CustomerId id, CustomerCode code, CustomerName name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static Customer newOf(CustomerCode code, CustomerName name) {
        if (null == code) {
            throw new IllegalArgumentException("客户编号不可为空");
        }
        return new Customer(null, code, name);
    }
}
