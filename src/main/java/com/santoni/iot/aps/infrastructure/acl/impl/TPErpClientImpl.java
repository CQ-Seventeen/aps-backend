package com.santoni.iot.aps.infrastructure.acl.impl;

import com.santoni.iot.aps.application.plan.context.SyncTaskContext;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.YarnStock;
import com.santoni.iot.aps.domain.bom.entity.YarnUsage;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.plan.constant.PlanConstant;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.resource.entity.Machine;
import com.santoni.iot.aps.infrastructure.acl.TPErpClient;
import com.santoni.iot.aps.infrastructure.acl.feign.TPErpFeign;
import com.santoni.iot.aps.infrastructure.acl.feign.request.WeavingScheduleItem;
import com.santoni.iot.aps.infrastructure.acl.feign.request.WeavingScheduleRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TPErpClientImpl implements TPErpClient {

    @Autowired
    private TPErpFeign tpErpFeign;

    @Override
    public List<YarnStock> queryYarnStock(List<YarnUsage> yarnList) {
        return List.of();
    }

    @Override
    public boolean syncPlanTask(SyncTaskContext context) {
        var request = buildScheduleRequest(context.task(), context.weavingPartOrder(), context.machine(), context.styleComponent(), context.produceOrder());
        try {
            log.info("syncPlanTask: request={}", JacksonUtil.toJson(request));
            var res = tpErpFeign.syncPlanTask(request);
            log.info("syncPlanTask: res={}", JacksonUtil.toJson(res));
            return res.getCode() == 200;
        } catch (Exception e) {
            log.error("syncPlanTask error, request:{}", JacksonUtil.toJson(request), e);
            return false;
        }
    }

    private WeavingScheduleRequest buildScheduleRequest(PlannedTask task,
                                                        WeavingPartOrder partOrder,
                                                        Machine machine,
                                                        StyleComponent styleComponent,
                                                        ProduceOrder produceOrder) {
        var request = new WeavingScheduleRequest();
        var item = new WeavingScheduleItem();

        // 任务相关
        if (null != task.getTaskCode()) {
            item.setScheduleBillNo(task.getTaskCode().value());
        }
        if (null != task.getId()) {
            item.setScheduleBillId(String.valueOf(task.getId().value()));
        }
        if (null != partOrder.getTaskDetailId()) {
            item.setTaskDtlId(partOrder.getTaskDetailId().value());
        }
        
        // 机台相关
        if (null != machine.getOuterId()) {
            item.setMcId(machine.getOuterId().value());
        }
        if (null != machine.getMachineDeviceId()) {
            item.setMcName(machine.getMachineDeviceId().value());
        }
        if (null != machine.getMachineSize() && null != machine.getMachineSize().getCylinderDiameter()) {
            item.setMcSizeName(machine.getMachineSize().getCylinderDiameter().value() + "寸");
        }
        
        // 生产订单相关
        if (null != task.getProduceOrderCode()) {
            item.setProdOrderNo(task.getProduceOrderCode().value());
        }
        if (null != produceOrder.getOuterId()) {
            item.setProdOrderId(produceOrder.getOuterId().value());
        }
        
        // 款式相关
        var demand = partOrder.getDemand();
        if (null != demand) {
            if (null != demand.getStyleCode()) {
                item.setItemCode(demand.getStyleCode().value());
            }
            if (null != demand.getPart()) {
                item.setPartsName(demand.getPart().value());
                item.setPartsId(demand.getPart().id());
            }
            if (null != demand.getSize()) {
                item.setSizeName(demand.getSize().value());
                item.setSizeId(demand.getSize().id());
            }
            if (null != demand.getColor()) {
                item.setColorName(demand.getColor().value());
                item.setColorId(demand.getColor().id());
            }
        }
        
        // 计划数量和时间
        if (null != task.getPlan()) {
            var plan = task.getPlan();
            if (null != plan.getQuantity()) {
                item.setQty(plan.getQuantity().getValue());
            }
            if (null != plan.getPeriod()) {
                if (null != plan.getPeriod().getStart()) {
                    item.setStartDate(TimeUtil.formatYYYYMMDD(plan.getPeriod().getStart().value()));
                }
                if (null != plan.getPeriod().getEnd()) {
                    item.setEndDate(TimeUtil.formatYYYYMMDD(plan.getPeriod().getEnd().value()));
                }
                item.setFinishDays((double) plan.getPeriod().totalSeconds() / PlanConstant.SECONDS_PER_DAY);
            }
        }
        
        // 交付时间
        if (null != partOrder.getDeliveryTime()) {
            item.setCustDeliveryDate(TimeUtil.formatYYYYMMDD(partOrder.getDeliveryTime().value()));
        }
        
        // 组件相关
        if (null != styleComponent) {
            // 下机时间（秒）
            if (null != styleComponent.getExpectedProduceTime()) {
                var totalSeconds = styleComponent.totalSeconds(
                    task.getPlan() != null && task.getPlan().getQuantity() != null 
                        ? task.getPlan().getQuantity().toQuantity() 
                        : com.santoni.iot.aps.domain.support.entity.Quantity.zero()
                );
                item.setLogoutTime((int) totalSeconds);
            }
            
            // 下机克重
            if (null != styleComponent.getExpectedWeight()) {
                item.setLogoutWeight(styleComponent.getExpectedWeight().value());
            }
            
            // 下机腰宽
            if (null != styleComponent.getFinishWidth()) {
                item.setLogoutWidth(String.valueOf(styleComponent.getFinishWidth().value()));
            }
            
            // 下机长度
            if (null != styleComponent.getFinishLength()) {
                item.setLogoutHeight(String.valueOf(styleComponent.getFinishLength().value()));
            }
            
            // 染色备注
            if (null != styleComponent.getDye()) {
                item.setDyeMemo(styleComponent.getDye().value());
            }

            
            // 理论日产量
            if (null != styleComponent.getDailyTheoreticalQuantity()) {
                item.setTheoryYieldDay(styleComponent.getDailyTheoreticalQuantity().getValue());
            }
            
            // 程序相关
            if (null != styleComponent.getProgram()) {
                item.setProgramName(styleComponent.getProgram().name());
            }
            
            // 机器尺寸
            if (null != styleComponent.getMachineSize()) {
                if (null != styleComponent.getMachineSize().getCylinderDiameter()) {
                    item.setMcSizeName(styleComponent.getMachineSize().getCylinderDiameter().value() + "寸");
                }
            }
        }
        
        // 单位
        if (null != partOrder.getUnit()) {
            item.setUnitName(partOrder.getUnit().value());
        }
        
        // 备注
        if (null != partOrder.getComment()) {
            item.setMemo(partOrder.getComment().value());
        }
        
        // 计算完成天数
        if (null != task.getPlan() && null != task.getPlan().getPeriod()) {
            var period = task.getPlan().getPeriod();
            var days = period.totalSeconds() / 86400.0;
            item.setFinishDays(days);
        }

        // 其他字段设置默认值或留空
        item.setFabricFactor(1);
        item.setSeqNumber(1);
        item.setSeqCrossNumber(1);
        item.setConesNum(1); // 默认值，可能需要根据实际情况计算
        item.setCompId("1868834734909423002");
        item.setDeptId("1000394");
        
        // 设置请求
        request.setScheduleList(new ArrayList<>(List.of(item)));
        request.setFisMcClassSchecling("0");

        request.setCompId("1868834734909423002");
        request.setDeptId("1000394");

        // deptId 和 signLabel 可能需要从其他地方获取
        
        return request;
    }
}
