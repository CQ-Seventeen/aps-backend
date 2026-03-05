package com.santoni.iot.aps.adapter.support.controller;

import com.santoni.iot.aps.adapter.support.request.*;
import com.santoni.iot.aps.application.bom.StyleApplication;
import com.santoni.iot.aps.application.bom.command.*;
import com.santoni.iot.aps.application.bom.dto.StyleComponentDTO;
import com.santoni.iot.aps.application.bom.dto.StyleDTO;
import com.santoni.iot.aps.application.bom.query.PageStyleQuery;
import com.santoni.iot.aps.application.bom.query.StyleComponentDetailQuery;
import com.santoni.iot.aps.application.bom.query.StyleDetailByCodeQuery;
import com.santoni.iot.aps.application.bom.query.StyleDetailQuery;
import com.santoni.iot.aps.application.resource.command.MachineFeatureCommand;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/style")
@RestController
@Slf4j
public class StyleController {

    @Autowired
    private StyleApplication styleApplication;

    @PostMapping("/create")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> createStyle(@RequestBody CreateStyleRequest request) {
        var cmd = new CreateStyleCommand();
        fillOperateStyleCmdByRequest(cmd, request);
        try {
            styleApplication.createStyle(cmd);
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("create style error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> updateStyle(@RequestBody UpdateStyleRequest request) {
        var cmd = new UpdateStyleCommand();
        fillOperateStyleCmdByRequest(cmd, request);
        cmd.setId(request.getId());
        try {
            styleApplication.updateStyle(cmd);
            return new ReturnData<>();
        } catch (Exception e) {
            log.error("update style error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/page")
    @ResponseBody
    @SantoniHeader
    public ReturnData<PageResult<StyleDTO>> pageQueryStyle(@RequestBody PageQueryStyleRequest request) {
        var query = new PageStyleQuery();
        query.setCode(request.getCode());
        query.copyPageParamFromPageRequest(request);
        try {
            return new ReturnData<>(styleApplication.pageQueryStyle(query));
        } catch (Exception e) {
            log.error("page query style error, req:{}", JacksonUtil.toJson(request), e);
            return new ReturnData<>(500, e.getMessage());
        }
    }

    @PostMapping("/batch_import")
    @ResponseBody
    @SantoniHeader
    public ReturnData<Void> batchImportStyle(@RequestPart("file") MultipartFile file) {

        return new ReturnData<>();
    }

    @GetMapping("/detail")
    @ResponseBody
    @SantoniHeader
    public ReturnData<StyleDTO> getStyleDetail(@RequestParam("styleId") long styleId) {
        try {
            var query = new StyleDetailQuery();
            query.setStyleId(styleId);
            return new ReturnData<>(styleApplication.getStyleDetail(query));
        } catch (IllegalArgumentException iae) {
            log.error("Query Style Detail error, bad request:{}", styleId);
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query Style Detail error, req:{}", styleId, e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @GetMapping("/detail_by_code")
    @ResponseBody
    @SantoniHeader
    public ReturnData<StyleDTO> getStyleDetail(@RequestParam("styleCode") String styleCode, @RequestParam("orderCode") String orderCode) {
        try {
            var query = new StyleDetailByCodeQuery();
            query.setStyleCode(styleCode);
            query.setProduceOrderCode(orderCode);
            return new ReturnData<>(styleApplication.getStyleDetailByCode(query));
        } catch (IllegalArgumentException iae) {
            log.error("Query Style Detail ByCode error, bad request:{}", styleCode);
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query Style Detail error, req:{}", styleCode, e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    @GetMapping("/component_detail")
    @ResponseBody
    @SantoniHeader
    public ReturnData<StyleComponentDTO> getStyleComponentByCode(@RequestParam("partOrderId") long partOrderId) {
        try {
            var query = new StyleComponentDetailQuery();
            query.setPartOrderId(partOrderId);
            return new ReturnData<>(styleApplication.getStyleComponentDetail(query));
        } catch (IllegalArgumentException iae) {
            log.error("Query StyleComponent Detail error, bad request, orderId:{}", partOrderId);
            return new ReturnData<>(400, iae.getMessage());
        } catch (Exception e) {
            log.error("Query StyleComponent DetailByCode error, req:{}", partOrderId, e);
            return new ReturnData<>(500, "服务内部错误");
        }
    }

    private void fillOperateStyleCmdByRequest(CreateStyleCommand cmd, CreateStyleRequest request) {
        cmd.setCode(request.getCode());
        cmd.setName(request.getName());
        cmd.setDescription(request.getDescription());
        if (CollectionUtils.isNotEmpty(request.getImgUrls())) {
            cmd.setImgUrls(request.getImgUrls());
        }
        if (CollectionUtils.isNotEmpty(request.getSizeList())) {
            cmd.setSkuList(request.getSizeList().stream().map(this::convertOperateStyleSkuReqToCmd).toList());
        }
    }

    private OperateStyleSkuCommand convertOperateStyleSkuReqToCmd(OperateStyleSkuRequest request) {
        var cmd = new OperateStyleSkuCommand();
        cmd.setSizeId(request.getSize());
        cmd.setSize(request.getSize());
        if (CollectionUtils.isNotEmpty(request.getComponents())) {
            cmd.setComponents(request.getComponents().stream().map(this::convertOperateStyleComponentReqToCmd).toList());
        }
        return cmd;
    }

    private OperateStyleComponentCommand convertOperateStyleComponentReqToCmd(OperateStyleComponentRequest request) {
        var cmd = new OperateStyleComponentCommand();
        cmd.setPartId(request.getPartId());
        cmd.setPart(request.getPart());
        cmd.setColorId(request.getColorId());
        cmd.setColor(request.getColor());
        cmd.setCylinderDiameter(request.getCylinderDiameter());
        cmd.setNeedleSpacing(request.getNeedleSpacing());
        cmd.setType(request.getType());
        cmd.setProgramFileName(request.getProgramFileName());
        cmd.setProgramFileUrl(request.getProgramFileUrl());
        cmd.setNumber(request.getNumber());
        cmd.setRatio(request.getRatio());
        cmd.setExpectedProduceTime(request.getExpectedProduceTime());
        cmd.setExpectedWeight(request.getExpectedWeight());
        cmd.setStandardNumber(request.getStandardNumber());
        if (null != request.getRequirement()) {
            cmd.setRequirement(convertToMachineRequirementCommand(request.getRequirement()));
        }
        cmd.setDescription(request.getDescription());
        return cmd;
    }

    private MachineRequirementCommand convertToMachineRequirementCommand(MachineRequireRequest request) {
        var cmd = new MachineRequirementCommand();
        if (CollectionUtils.isNotEmpty(request.getType())) {
            cmd.setType(request.getType());
        }
        if (CollectionUtils.isNotEmpty(request.getBareSpandex())) {
            cmd.setBareSpandexTypeList(request.getBareSpandex());
        }
        if (CollectionUtils.isNotEmpty(request.getOtherAttrList())) {
            cmd.setOtherAttrList(request.getOtherAttrList()
                    .stream().map(it -> new MachineFeatureCommand(it.getAttrCode(), it.getAttrValues()))
                    .toList());
        }
        return cmd;
    }

}
