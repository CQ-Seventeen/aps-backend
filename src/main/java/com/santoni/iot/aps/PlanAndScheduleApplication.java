package com.santoni.iot.aps;

import com.santoni.iot.utils.record.annotation.EnableSantoniHeader;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableSantoniHeader
@EnableScheduling
@SpringBootApplication
@EnableFeignClients
public class PlanAndScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanAndScheduleApplication.class, args);
    }

}
