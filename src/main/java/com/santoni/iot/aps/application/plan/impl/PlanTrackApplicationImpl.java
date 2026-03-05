package com.santoni.iot.aps.application.plan.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.application.order.assembler.OrderAssembler;
import com.santoni.iot.aps.application.order.query.ProduceOrderQuery;
import com.santoni.iot.aps.application.plan.PlanTrackApplication;
import com.santoni.iot.aps.application.plan.assembler.PlanDTOAssembler;
import com.santoni.iot.aps.application.plan.assembler.ProducePlanDTOAssembler;
import com.santoni.iot.aps.application.plan.context.CanBeProducedOrderPlanContext;
import com.santoni.iot.aps.application.plan.context.ProduceOrderPlanContext;
import com.santoni.iot.aps.application.plan.dto.order.ProduceLevelPlanDTO;
import com.santoni.iot.aps.application.plan.dto.track.ProducePlanTrackDTO;
import com.santoni.iot.aps.application.plan.dto.track.ProducePredictDTO;
import com.santoni.iot.aps.application.plan.query.DailyYarnPredictQuery;
import com.santoni.iot.aps.application.plan.query.OrderPlanFilterByMachineQuery;
import com.santoni.iot.aps.application.plan.query.ProducePlanTrackQuery;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.domain.bom.entity.StyleComponent;
import com.santoni.iot.aps.domain.bom.entity.StyleSku;
import com.santoni.iot.aps.domain.bom.entity.valueobj.SkuCode;
import com.santoni.iot.aps.domain.bom.repository.StyleRepository;
import com.santoni.iot.aps.domain.execute.constant.SumKeyType;
import com.santoni.iot.aps.domain.execute.entity.OrderProduction;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.repository.ManuallyReportExecuteRepository;
import com.santoni.iot.aps.domain.execute.service.ExecuteDomainService;
import com.santoni.iot.aps.domain.order.entity.ProduceOrder;
import com.santoni.iot.aps.domain.order.entity.StyleDemand;
import com.santoni.iot.aps.domain.order.entity.WeavingPartOrder;
import com.santoni.iot.aps.domain.order.entity.valueobj.CanProduceOrderMap;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderId;
import com.santoni.iot.aps.domain.order.repository.ProduceOrderRepository;
import com.santoni.iot.aps.domain.order.repository.WeavingOrderRepository;
import com.santoni.iot.aps.domain.order.service.OrderDomainService;
import com.santoni.iot.aps.domain.plan.entity.machine.PlannedTask;
import com.santoni.iot.aps.domain.plan.repository.MachineTaskRepository;
import com.santoni.iot.aps.domain.support.entity.valueobj.EndTime;
import com.santoni.iot.aps.domain.support.entity.valueobj.StartTime;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanTrackApplicationImpl implements PlanTrackApplication {
    @Autowired
    private ExecuteDomainService executeDomainService;

    @Autowired
    private OrderDomainService orderDomainService;

    @Autowired
    private ManuallyReportExecuteRepository manuallyReportExecuteRepository;

    @Autowired
    private MachineTaskRepository machineTaskRepository;

    @Autowired
    private ProduceOrderRepository produceOrderRepository;

    @Autowired
    private WeavingOrderRepository weavingOrderRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private PlanDTOAssembler planDTOAssembler;

    @Autowired
    private ProducePlanDTOAssembler producePlanDTOAssembler;

    @Autowired
    private OrderAssembler orderAssembler;

    @Override
    public List<ProducePlanTrackDTO> queryProducePlanTrack(ProducePlanTrackQuery query) {
        var produceOrders = produceOrderRepository.unfinishedProduceOrder();
        var trackList = buildPlanTrackByOrder(produceOrders);
        trackList.sort(Comparator.comparing(ProducePlanTrackDTO::getDeliveryDate));
        return trackList;
    }

    @Override
    public List<ProducePlanTrackDTO> queryOrderPlanFilterByMachine(OrderPlanFilterByMachineQuery query) {
        var produceOrders = produceOrderRepository.unfinishedProduceOrder();
        ;
        if (CollectionUtils.isEmpty(produceOrders)) {
            return Collections.emptyList();
        }
        var context = prepareCanBeProducedOrderContext(produceOrders, query);

        return collectProductionAndPlan(produceOrders, context.partOrderMap(), context.componentMap(), true);
    }

    @Override
    public ProducePredictDTO dailyYarnPredict(DailyYarnPredictQuery query) {
        var curDate = TimeUtil.fromYYYYMMDD(query.getDate());
        var prevDay = TimeUtil.formatYYYYMMDD(curDate.minusDays(1));
        var prevProduction = manuallyReportExecuteRepository.listProductionByDate(new ProduceDate(prevDay), null);

        var startTime = TimeUtil.getStartOf(curDate);
        var endTime = TimeUtil.getEndOf(curDate);
        var curTasks = machineTaskRepository.listByTime(new StartTime(startTime), new EndTime(endTime));

        Set<SkuCode> skuCodeSet = Sets.newHashSet();
        for (var task : curTasks) {
            skuCodeSet.add(task.getSkuCode());
        }
        for (var production : prevProduction) {
            skuCodeSet.add(production.getSkuCode());
        }

        var componentMap = styleRepository.listStyleSkuByCode(null, skuCodeSet, true)
                .stream().collect(Collectors.toMap(
                        it -> it.getCode().value(),
                        StyleSku::getComponentMap,
                        (map1, map2) -> map1
                ));

        var predicts = executeDomainService.predictDailyProduction(
                prevProduction,
                curTasks,
                componentMap,
                startTime,
                endTime
        );
        var totalYarnUsage = executeDomainService.gatherYarnUsage(predicts);
        // todo 查库存

        return planDTOAssembler.assembleProducePredictDTO(predicts, totalYarnUsage);
    }

    @Override
    public List<ProducePlanTrackDTO> queryProduceOrderPlan(ProduceOrderQuery query) {
        var search = orderAssembler.composeSearchProduceOrder(query);
        search.nonPage();
        var produceOrders = produceOrderRepository.pageQueryProduceOrder(search).getData();
        return buildPlanTrackByOrder(produceOrders);
    }

    @Override
    public List<ProduceLevelPlanDTO> queryUnFinishedOrderPlan() {
        var produceOrders = produceOrderRepository.unfinishedProduceOrder();
        ;
        if (CollectionUtils.isEmpty(produceOrders)) {
            return Collections.emptyList();
        }
        var context = buildProducePlanContext(produceOrders);
        return produceOrders.stream()
                .map(it -> producePlanDTOAssembler.assembleProducePlanDTO(it, context.partOrderMap(), context.componentMap()))
                .sorted(Comparator.comparing(ProduceLevelPlanDTO::getDeliveryTime))
                .toList();
    }

    private List<ProducePlanTrackDTO> buildPlanTrackByOrder(List<ProduceOrder> produceOrders) {
        if (CollectionUtils.isEmpty(produceOrders)) {
            return Collections.emptyList();
        }
        var context = buildProducePlanContext(produceOrders);

        return collectProductionAndPlan(produceOrders, context.partOrderMap(), context.componentMap(), false);
    }

    private List<ProducePlanTrackDTO> collectProductionAndPlan(List<ProduceOrder> produceOrders,
                                                               Map<Long, List<WeavingPartOrder>> partOrderMap,
                                                               Map<String, Map<String, Map<String, StyleComponent>>> componentMap,
                                                               boolean filterEmptyOrder) {
        var orderProductions = initProduction(produceOrders, partOrderMap, componentMap, filterEmptyOrder);
        if (CollectionUtils.isEmpty(orderProductions)) {
            return Collections.emptyList();
        }
        collectProduction(orderProductions);

        var taskList = machineTaskRepository.listByProduceOrder(orderProductions.stream().map(it -> it.getProduceOrder().getCode()).toList());
        collectTask(orderProductions, taskList);
        return orderProductions.stream().map(it -> planDTOAssembler.assembleProducePlanTrackDTO(it)).collect(Collectors.toList());
    }

    private List<OrderProduction> initProduction(List<ProduceOrder> orderList,
                                                 Map<Long, List<WeavingPartOrder>> partOrderMap,
                                                 Map<String, Map<String, Map<String, StyleComponent>>> componentMap,
                                                 boolean filterEmptyOrder) {
        List<OrderProduction> res = Lists.newArrayListWithExpectedSize(orderList.size());

        for (var order : orderList) {
            var orderComponentMap = componentMap.getOrDefault(order.getCode().value(), Collections.emptyMap());
            var partOrders = partOrderMap.getOrDefault(order.getId().value(), List.of());
            if (CollectionUtils.isEmpty(partOrders) && filterEmptyOrder) {
                continue;
            }
            res.add(OrderProduction.deriveFromOrder(order, partOrders, orderComponentMap));
        }
        return res;
    }

    private void collectProduction(List<OrderProduction> orderProductions) {
        for (var production : orderProductions) {
            var keys = production.getAllComponentLevelKey();
            if (CollectionUtils.isEmpty(keys)) continue;
            var productions = manuallyReportExecuteRepository.listLatestProductionSumByKey(keys, SumKeyType.PART);
            production.collectProduction(productions);
        }
    }

    private void collectTask(List<OrderProduction> orderProductions, List<PlannedTask> taskList) {
        var taskMap = taskList.stream().collect(Collectors.groupingBy(it -> it.getProduceOrderCode().value()));
        for (var production : orderProductions) {
            var tasks = taskMap.get(production.getProduceOrder().getCode().value());
            if (CollectionUtils.isNotEmpty(tasks)) {
                production.collectMachineTask(tasks);
            }
        }
    }

    private CanBeProducedOrderPlanContext prepareCanBeProducedOrderContext(List<ProduceOrder> produceOrders,
                                                                           OrderPlanFilterByMachineQuery query) {
        Map<Long, List<WeavingPartOrder>> partOrderMap = Maps.newHashMap();
        Map<String, Map<String, Map<String, StyleComponent>>> componentMap = Maps.newHashMap();
        for (var order : produceOrders) {
            var res = filterCanProducePart(order, query);
            partOrderMap.put(order.getId().value(), res.partOrders());
            componentMap.put(order.getCode().value(), res.componentMap());
        }
        return new CanBeProducedOrderPlanContext(partOrderMap, componentMap);
    }

    private CanProduceOrderMap filterCanProducePart(ProduceOrder order, OrderPlanFilterByMachineQuery query) {
        var skuCodeSet = order.getDemands().stream().map(StyleDemand::getSkuCode).collect(Collectors.toSet());
        var styleSkuList = styleRepository.listStyleSkuByCode(order.getCode(), skuCodeSet, true);

        var componentMap = styleSkuList.stream().collect(Collectors.toMap(it -> it.getCode().value(), StyleSku::getComponentMap));
        var partOrders = weavingOrderRepository.listPartOrderByProduceOrderIds(Lists.newArrayList(order.getId()));
        return orderDomainService.filterCanProduceOrder(query.getCylinderDiameterList(), query.getNeedleSpacingList(), componentMap, partOrders);
    }

    private ProduceOrderPlanContext buildProducePlanContext(List<ProduceOrder> produceOrders) {
        var orderTagPair = produceOrders.stream()
                .collect(Collectors.teeing(
                        Collectors.mapping(ProduceOrder::getId, Collectors.toList()),
                        Collectors.mapping(ProduceOrder::getCode, Collectors.toList()),
                        Pair::of
                ));
        var partOrderMap = weavingOrderRepository.listPartOrderByProduceOrderIds(orderTagPair.getLeft())
                .stream().collect(Collectors.groupingBy(it -> it.getProduceOrderId().value()));
        var componentMap = styleRepository.listStyleSkuByOrderCode(orderTagPair.getRight(), true)
                .stream().collect(Collectors.groupingBy(it -> it.getProduceOrderCode().value(),
                        Collectors.toMap(it -> it.getCode().value(), StyleSku::getComponentMap)));
        return new ProduceOrderPlanContext(partOrderMap, componentMap);
    }
}
