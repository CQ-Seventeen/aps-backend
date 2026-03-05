package com.santoni.iot.aps.application.plan.assembler;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.application.plan.query.FilterMachineQuery;
import com.santoni.iot.aps.application.plan.query.FreeSoonMachinePlanQuery;
import com.santoni.iot.aps.application.plan.query.MachinePlanQuery;
import com.santoni.iot.aps.application.plan.query.MachineTaskPageQuery;
import com.santoni.iot.aps.application.resource.assembler.MachineAssembler;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.plan.entity.valueobj.SearchPlannedTask;
import com.santoni.iot.aps.domain.resource.constant.MachineStatus;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.WorkshopId;
import com.santoni.iot.aps.domain.support.entity.valueobj.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PlanAssembler {

    @Autowired
    private MachineAssembler machineAssembler;

    public SearchPlannedTask composeSearchPlannedTask(MachineTaskPageQuery query) {
        return new SearchPlannedTask(
                null == query.getFactoryId() ? null : new FactoryId(query.getFactoryId()),
                StringUtils.isBlank(query.getProduceOrderCode()) ? null : new ProduceOrderCode(query.getProduceOrderCode()),
                StringUtils.isBlank(query.getStyleCode()) ? null : new StyleCode(query.getStyleCode()),
                null == query.getStartTime() ? null : new StartTime(query.getStartTime()),
                null == query.getEndTime() ? null : new EndTime(query.getEndTime()),
                query
        );
    }

    public SearchMachine composeSearchMachineFromFreeSoonQuery(FreeSoonMachinePlanQuery query) {
        var search = new SearchMachine(
                null == query.getFactoryId() ? null : new FactoryId(query.getFactoryId()),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                CollectionUtils.isNotEmpty(query.getCylinderDiameterList()) ? query.getCylinderDiameterList().stream().map(CylinderDiameter::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getNeedleSpacingList()) ? query.getNeedleSpacingList().stream().map(NeedleSpacing::new).toList() : null,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                CollectionUtils.isNotEmpty(query.getAreaList()) ? query.getAreaList().stream().map(MachineArea::new).toList() : null,
                0, 0
        );
        search.nonPage();
        return search;
    }

    public SearchMachine composeSearchMachine(MachinePlanQuery query) {
        var search = machineAssembler.composeSearchMachine(query);
        search.nonPage();
        return search;
    }

    public MachineOptions composeOptions(FilterMachineQuery query) {
        return new MachineOptions(
                List.of(),
                CollectionUtils.isNotEmpty(query.getMachineTypeList()) ? query.getMachineTypeList().stream().map(MachineType::new).toList() : null,
                CollectionUtils.isNotEmpty(query.getBareSpandexTypeList()) ? query.getBareSpandexTypeList().stream().map(BareSpandex::new).toList() : null,
                null == query.getHighSpeed() ? null : Lists.newArrayList(query.getHighSpeed() ? HighSpeed.high() : HighSpeed.low()),
                List.of(),
                List.of()
        );
    }

}
