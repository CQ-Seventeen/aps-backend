package com.santoni.iot.aps.infrastructure.acl.feign;

import com.santoni.iot.aps.infrastructure.acl.feign.request.BatchQueryStockReq;
import com.santoni.iot.aps.infrastructure.acl.feign.request.WeavingScheduleRequest;
import com.santoni.iot.aps.infrastructure.acl.feign.response.TPErpResponse;
import com.santoni.iot.aps.infrastructure.acl.feign.response.YarnStockResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "tp-erp-service", url = "${feign.tp-erp-service.url}")
public interface TPErpFeign {

    @RequestMapping(value = "/yarn/stock/query", method = RequestMethod.POST)
    TPErpResponse<YarnStockResult> queryYarnStock(@RequestBody BatchQueryStockReq request);

    @RequestMapping(value = "/prodTask/saveWeav", method = RequestMethod.POST)
    TPErpResponse<Void> syncPlanTask(@RequestBody WeavingScheduleRequest request);


}
