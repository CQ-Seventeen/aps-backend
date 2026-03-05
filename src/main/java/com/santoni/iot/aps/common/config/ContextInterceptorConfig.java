package com.santoni.iot.aps.common.config;

import com.santoni.iot.aps.common.interceptor.ContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ContextInterceptorConfig implements WebMvcConfigurer {

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(new ContextInterceptor()).addPathPatterns("/**").order(Ordered.LOWEST_PRECEDENCE);
    }
}
