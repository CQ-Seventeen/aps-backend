package com.santoni.iot.aps.adapter.support.controller;

import com.santoni.iot.aps.application.support.OrganizationApplication;
import com.santoni.iot.aps.application.support.dto.FactoryDTO;
import com.santoni.iot.aps.application.support.dto.MachineGroupDTO;
import com.santoni.iot.aps.application.support.dto.WorkshopDTO;
import com.santoni.iot.aps.application.support.query.ListFactoryQuery;
import com.santoni.iot.aps.application.support.query.ListMachineGroupQuery;
import com.santoni.iot.aps.application.support.query.ListWorkshopQuery;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/organization")
@RestController
@Slf4j
public class OrganizationController {

    @Autowired
    private OrganizationApplication organizationApplication;

    @GetMapping("/get_factory")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<FactoryDTO>> listFactoryByInstitute(@RequestHeader("instituteId") long instituteId) {
        var query = new ListFactoryQuery();
        query.setInstituteId(instituteId);
        return new ReturnData<>(organizationApplication.listFactoryByInstitute(query));
    }

    @GetMapping("/get_workshop")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<WorkshopDTO>> listWorkshopByFactory(@RequestParam("factoryId") long factoryId) {
        var query = new ListWorkshopQuery();
        query.setFactoryId(factoryId);
        return new ReturnData<>(organizationApplication.listWorkshopByFactory(query));
    }

    @GetMapping("/get_machine_group")
    @ResponseBody
    @SantoniHeader
    public ReturnData<List<MachineGroupDTO>> listMachineGroupByWorkshop(@RequestParam("workshopId") long workshopId) {
        var query = new ListMachineGroupQuery();
        query.setWorkshopId(workshopId);
        return new ReturnData<>(organizationApplication.listMachineGroupByWorkshop(query));
    }
}
