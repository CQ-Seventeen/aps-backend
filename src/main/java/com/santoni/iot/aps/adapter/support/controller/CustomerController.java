package com.santoni.iot.aps.adapter.support.controller;

import com.santoni.iot.aps.application.support.CustomerApplication;
import com.santoni.iot.aps.application.support.command.CreateCustomerCommand;
import com.santoni.iot.aps.application.support.command.UpdateCustomerCommand;
import com.santoni.iot.aps.application.support.dto.CustomerDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.application.support.query.PageCustomerQuery;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.adapter.support.request.CreateCustomerRequest;
import com.santoni.iot.aps.adapter.support.request.PageQueryCustomerRequest;
import com.santoni.iot.aps.adapter.support.request.UpdateCustomerRequest;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/customer")
@RestController
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerApplication customerApplication;

    @PostMapping("/create")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> createCustomer(@RequestBody CreateCustomerRequest request) {
        var cmd = new CreateCustomerCommand();
        fillOperateCustomerCmdByRequest(cmd, request);
        try {
            customerApplication.createCustomer(cmd);
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("create customer error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> updateCustomer(@RequestBody UpdateCustomerRequest request) {
        var cmd = new UpdateCustomerCommand();
        fillOperateCustomerCmdByRequest(cmd, request);
        cmd.setId(request.getId());
        try {
            customerApplication.updateCustomer(cmd);
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("update customer error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<CustomerDTO>> pageQueryCustomer(@RequestBody PageQueryCustomerRequest request) {
        var query = new PageCustomerQuery();
        query.setCode(request.getCode());
        query.copyPageParamFromPageRequest(request);
        try {
            return new ReturnData<>(customerApplication.pageQueryCustomer(query));
        } catch (Exception e) {
            log.error("page query customer error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/batch_import")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> batchImportCustomer(@RequestPart("file") MultipartFile file) {

        return new ReturnData<>();
    }

    private void fillOperateCustomerCmdByRequest(CreateCustomerCommand cmd, CreateCustomerRequest request) {
        cmd.setCode(request.getCode());
        cmd.setName(request.getName());
    }

}
