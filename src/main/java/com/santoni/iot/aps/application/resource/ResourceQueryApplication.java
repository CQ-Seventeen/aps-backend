package com.santoni.iot.aps.application.resource;

import com.santoni.iot.aps.application.resource.dto.MachineAttrConfigDTO;
import com.santoni.iot.aps.application.resource.dto.MachineListDTO;
import com.santoni.iot.aps.application.resource.dto.MachineOptionsDTO;
import com.santoni.iot.aps.application.resource.dto.OverviewMachineDTO;
import com.santoni.iot.aps.application.resource.query.OverviewMachineQuery;
import com.santoni.iot.aps.application.resource.query.PageMachineQuery;
import com.santoni.iot.aps.application.support.dto.PageResult;

import java.util.List;

public interface ResourceQueryApplication {

    PageResult<MachineListDTO> pageMachine(PageMachineQuery query);

    MachineOptionsDTO getMachineOptions();

    List<MachineAttrConfigDTO> getAllAttrConfig();

    OverviewMachineDTO overviewMachine(OverviewMachineQuery query);
}
