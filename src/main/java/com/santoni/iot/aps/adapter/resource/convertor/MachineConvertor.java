package com.santoni.iot.aps.adapter.resource.convertor;

import com.santoni.iot.aps.adapter.resource.request.OperateMachineAttrConfigRequest;
import com.santoni.iot.aps.application.resource.command.CreateMachineCommand;
import com.santoni.iot.aps.application.resource.command.MachineFeatureCommand;
import com.santoni.iot.aps.application.resource.command.OperateMachineAttrConfigCommand;
import com.santoni.iot.aps.application.resource.command.UpdateMachineCommand;
import com.santoni.iot.aps.application.resource.query.MachineFeatureQuery;
import com.santoni.iot.aps.application.resource.query.PageMachineQuery;
import com.santoni.iot.aps.adapter.resource.request.CreateMachineRequest;
import com.santoni.iot.aps.adapter.resource.request.PageQueryMachineRequest;
import com.santoni.iot.aps.adapter.resource.request.UpdateMachineRequest;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;

public class MachineConvertor {

    public static CreateMachineCommand convertCreateMachineReqToCmd(CreateMachineRequest request, long instituteId) {
        var cmd = new CreateMachineCommand();
        fillCreateMachineCommand(cmd, request, instituteId);
        return cmd;
    }

    public static UpdateMachineCommand convertUpdateMachineReqToCmd(UpdateMachineRequest request, long instituteId) {
        var cmd = new UpdateMachineCommand();
        fillCreateMachineCommand(cmd, request, instituteId);
        cmd.setMachineId(request.getId());
        return cmd;
    }

    private static void fillCreateMachineCommand(CreateMachineCommand cmd, CreateMachineRequest request, long instituteId) {
        cmd.setDeviceId(request.getDeviceId());
        cmd.setCode(request.getCode());
        cmd.setInstituteId(instituteId);
        cmd.setFactoryId(request.getFactoryId());
        cmd.setWorkshopId(request.getWorkshopId());
        cmd.setMachineGroupId(request.getMachineGroupId());
        cmd.setCylinderDiameter(request.getCylinderDiameter());
        cmd.setNeedleSpacing(request.getNeedleSpacing());
        cmd.setNeedleNumber(request.getNeedleNumber());
        cmd.setMachineType(request.getMachineType());
        cmd.setBareSpandexType(request.getBareSpandexType());
        cmd.setHighSpeed(request.isHighSpeed());
        cmd.setFeatures(CollectionUtils.isEmpty(request.getFeatureList()) ? Collections.emptyList() :
                request.getFeatureList().stream()
                        .map(it -> new MachineFeatureCommand(it.getAttrCode(), it.getAttrValues()))
                        .toList());
    }

    public static PageMachineQuery convertPageMachineRequestToQuery(PageQueryMachineRequest request) {
        var query = new PageMachineQuery();
        fillMachineQueryWithRequest(query, request);
        query.copyPageParamFromPageRequest(request);
        return query;
    }

    public static void fillMachineQueryWithRequest(PageMachineQuery query, PageQueryMachineRequest request) {
        query.setFactoryIds(request.getFactoryIds());
        query.setWorkshopIds(request.getWorkshopIds());
        query.setMachineGroupIds(request.getMachineGroupIds());
        query.setStatusList(request.getStatusList());
        query.setCylinderDiameterList(request.getCylinderDiameterList());
        query.setNeedleSpacingList(request.getNeedleSpacingList());
        query.setNeedleNumberList(request.getNeedleNumberList());
        query.setMachineTypeList(request.getMachineTypeList());
        query.setBareSpandexList(request.getBareSpandexList());
        query.setHighSpeed(request.getHighSpeed());
        if (CollectionUtils.isNotEmpty(request.getFeatureList())) {
            query.setFeatureList(request.getFeatureList().stream()
                    .map(it -> new MachineFeatureQuery(it.getAttrCode(), it.getAttrValues())).toList());
        }
        query.setAreaList(request.getAreaList());
    }

    public static OperateMachineAttrConfigCommand convertOperateAttrConfigToCmd(OperateMachineAttrConfigRequest request) {
        return new OperateMachineAttrConfigCommand(
                request.getAttrCode(),
                request.getOptionalValues(),
                request.getOptionType(),
                request.isUseToFilter()
        );
    }

}
