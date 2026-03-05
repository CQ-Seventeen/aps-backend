package com.santoni.iot.aps.common.interceptor;

import com.santoni.iot.aps.common.context.PlanContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Slf4j
public class ContextInterceptor implements WebRequestInterceptor {
    @Override
    public void preHandle(WebRequest request) throws Exception {
        String headerUserId = request.getHeader("userId");
        String headerInstituteId = request.getHeader("instituteId");
        String headerFactoryId = request.getHeader("factoryId");
        try {
            long userId = StringUtils.isBlank(headerUserId) ? 0 : Long.parseLong(headerUserId);
            PlanContext.setUserId(userId);
            long instituteId = StringUtils.isBlank(headerInstituteId) ? 0 : Long.parseLong(headerInstituteId);
            PlanContext.setInstituteId(instituteId);
            long factoryId = StringUtils.isBlank(headerFactoryId) ? 0 : Long.parseLong(headerFactoryId);
            PlanContext.setFactoryId(factoryId);
        } catch (Exception e) {
            log.error("PreHandle init context fail", e);
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {

    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        PlanContext.clear();
    }
}
