package com.santoni.iot.aps.application.resource.impl;

import com.santoni.iot.aps.application.resource.ResourceQueryApplication;
import com.santoni.iot.aps.application.resource.assembler.MachineAssembler;
import com.santoni.iot.aps.application.resource.assembler.MachineDTOAssembler;
import com.santoni.iot.aps.application.resource.dto.MachineAttrConfigDTO;
import com.santoni.iot.aps.application.resource.dto.MachineListDTO;
import com.santoni.iot.aps.application.resource.dto.MachineOptionsDTO;
import com.santoni.iot.aps.application.resource.dto.OverviewMachineDTO;
import com.santoni.iot.aps.application.resource.query.OverviewMachineQuery;
import com.santoni.iot.aps.application.resource.query.PageMachineQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.domain.plan.entity.valueobj.PlanEndTime;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.plan.service.PlanDomainService;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.domain.resource.repository.MachineAttrRepository;
import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import com.santoni.iot.aps.domain.resource.service.ResourceDomainService;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResourceQueryApplicationImpl implements ResourceQueryApplication {

    @Autowired
    private ResourceDomainService resourceDomainService;

    @Autowired
    private PlanDomainService planDomainService;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private MachineAttrRepository machineAttrRepository;

    @Autowired
    private MachineTaskRepository machineTaskRepository;

    @Autowired
    private MachineAssembler machineAssembler;

    @Autowired
    private MachineDTOAssembler machineDTOAssembler;

    @Override
    public PageResult<MachineListDTO> pageMachine(PageMachineQuery query) {
        var search = machineAssembler.composeSearchMachine(query);
        var pageRes = machineRepository.pageQueryMachine(search);
        if (CollectionUtils.isEmpty(pageRes.getData())) {
            return PageResult.empty(pageRes);
        }
        return PageResult.fromPageData(
                pageRes.getData().stream().map(it -> machineDTOAssembler.assembleMachineListDTO(it)).toList(),
                pageRes
        );
    }

    @Override
    public MachineOptionsDTO getMachineOptions() {
        var options = machineRepository.getMachineOptions();
        return machineDTOAssembler.assembleMachineOptionsDTO(options);
    }

    @Override
    public List<MachineAttrConfigDTO> getAllAttrConfig() {
        var configList = machineAttrRepository.listAllConfig();
        if (CollectionUtils.isEmpty(configList)) {
            return List.of();
        }
        return configList.stream().map(it -> machineDTOAssembler.assembleMachineAttrConfigDTO(it)).toList();
    }

    @Override
    public OverviewMachineDTO overviewMachine(OverviewMachineQuery query) {
        var allMachine = machineRepository.listAllMachine();
        var machineIdList = allMachine.stream().map(Machine::getId).toList();
        var taskMap = machineTaskRepository.listByMachines(machineIdList, new StartTime(query.getStartTime()), new PlanEndTime(query.getEndTime()))
                .stream().collect(Collectors.groupingBy(it -> it.getMachineId().value()));

        var machinePlanList = planDomainService.composeMachinePlanList(allMachine, taskMap,
                query.getStartTime(), query.getEndTime());
        var statistic = resourceDomainService.statisticMachine(machinePlanList, query.getStartTime(), query.getEndTime());
        return machineDTOAssembler.assembleOverviewMachineDTO(statistic);
    }
}
