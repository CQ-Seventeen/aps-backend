package com.santoni.iot.aps.domain.schedule.solver.or.config;

import ai.timefold.solver.core.config.solver.SolverConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeFoldSolverConfig {

    @Bean
    public SolverConfig ORSolver() {
        return SolverConfig.createFromXmlResource("config/basicSolverConfig.xml");
    }
}
