package com.santoni.iot.aps.infrastructure.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.santoni.iot.aps.application.support.query.PageCustomerQuery;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.support.entity.Customer;
import com.santoni.iot.aps.domain.support.entity.PageData;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.support.repository.CustomerRepository;
import com.santoni.iot.aps.infrastructure.database.aps.CustomerMapper;
import com.santoni.iot.aps.infrastructure.factory.CustomerFactory;
import com.santoni.iot.aps.infrastructure.po.CustomerPO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchCustomerQO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerFactory customerFactory;

    @Override
    public Customer getCustomerByCode(CustomerCode code) {
        var po = customerMapper.getByCode(PlanContext.getInstituteId(), code.value());
        if (null == po) {
            return null;
        }
        return customerFactory.composeCustomer(po);
    }

    @Override
    public List<Customer> listCustomerByCode(Collection<CustomerCode> codeList) {
        var poList = customerMapper.listByCodeList(PlanContext.getInstituteId(),
                codeList.stream().map(CustomerCode::value).toList());
        if (CollectionUtils.isEmpty(poList)) {
            return List.of();
        }
        return poList.stream().map(it -> customerFactory.composeCustomer(it)).toList();
    }

    @Override
    public void saveCustomer(Customer customer) {
        var po = customerFactory.convertToCustomerPO(customer);
        if (null == customer.getId()) {
            customerMapper.insert(po);
        } else {
            po.setId(customer.getId().value());
            customerMapper.update(po);
        }

    }

    @Override
    public void batchSaveCustomer(List<Customer> customerList) {

    }

    @Override
    public PageData<Customer> pageQueryCustomer(PageCustomerQuery query) {
        IPage<CustomerPO> page = new Page<>(query.getPage(), query.getPageSize());
        var qo = new SearchCustomerQO();
        qo.setCode(query.getCode());
        var pageRes = customerMapper.searchCustomer(page, PlanContext.getInstituteId(), qo);

        return PageData.fromPage(pageRes.getRecords()
                .stream()
                .map(it -> customerFactory.composeCustomer(it))
                .toList(), page);
    }
}
