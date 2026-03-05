package com.santoni.iot.aps.application.support;

import com.santoni.iot.aps.application.support.dto.FactoryDTO;
import com.santoni.iot.aps.application.support.dto.MachineGroupDTO;
import com.santoni.iot.aps.application.support.dto.WorkshopDTO;
import com.santoni.iot.aps.application.support.query.ListFactoryQuery;
import com.santoni.iot.aps.application.support.query.ListMachineGroupQuery;
import com.santoni.iot.aps.application.support.query.ListWorkshopQuery;

import java.util.List;

public interface OrganizationApplication {

    List<FactoryDTO> listFactoryByInstitute(ListFactoryQuery query);

    List<WorkshopDTO> listWorkshopByFactory(ListWorkshopQuery query);

    List<MachineGroupDTO> listMachineGroupByWorkshop(ListMachineGroupQuery query);

}
