package com.santoni.iot.aps.adapter.order.controller;

import com.santoni.iot.aps.application.order.OrderQueryApplication;
import com.santoni.iot.aps.application.order.dto.OverviewOrderDTO;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderQueryApplication orderQueryApplication;

    @GetMapping("/overview")
    @ResponseBody
    @SantoniHeader
    public ReturnData<OverviewOrderDTO> overviewOrder() {
        try {
            return new ReturnData<>(orderQueryApplication.overviewOrder());
        } catch (Exception e) {
            log.error("Overview error", e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

}
