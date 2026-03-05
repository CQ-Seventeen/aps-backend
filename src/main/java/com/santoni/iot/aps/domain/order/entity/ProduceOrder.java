package com.santoni.iot.aps.domain.order.entity;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.order.constant.ProduceOrderStatus;
import com.santoni.iot.aps.domain.order.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.Customer;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import com.santoni.iot.aps.domain.support.entity.organization.OuterFactory;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.repository.CodeRepository;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Getter
public class ProduceOrder implements Comparable<ProduceOrder> {

    private ProduceOrderId id;

    private OuterOrderId outerId;

    private ProduceOrderCode code;

    private OuterFactory factory;

    private Customer customer;

    private List<StyleDemand> demands;

    private DeliveryTime deliveryTime;

    private ManufactureBatch manufactureBatch;

    private ManufactureDate manufactureDate;

    private ProduceOrderStatus status;

    public void applyChange(ProduceOrder newOrder) {
        this.factory = newOrder.getFactory();
        this.demands = newOrder.getDemands();
        this.deliveryTime = newOrder.getDeliveryTime();
        this.manufactureBatch = newOrder.getManufactureBatch();
        this.manufactureDate = newOrder.getManufactureDate();
    }

    public List<WeavingOrder> generateWeavingOrders(CodeRepository codeRepository, FactoryId factoryId) {
        if (null == this.id) {
            throw new UnsupportedOperationException("生产单id缺失");
        }
        List<WeavingOrder> res = Lists.newArrayListWithExpectedSize(demands.size());
        var codeList = codeRepository.getMultiWeavingOrderCode(demands.size());
        Queue<WeavingOrderCode> codeQueue = new LinkedList<>(codeList);
        for (var demand : demands) {
            res.add(WeavingOrder.newOf(factoryId, codeQueue.poll(), this.id, this.code, demand, this.deliveryTime));
        }
        return res;
    }

    public int getTotalQuantity() {
        int result = 0;
        for (var styleDemand : demands) {
            result += styleDemand.getWeaveQuantity().getValue();
        }
        return result;
    }

    public static ProduceOrder newOf(ProduceOrderCode code, Customer customer, List<StyleDemand> demand, DeliveryTime deliveryTime) {
        return new ProduceOrder(null, null, code, null, customer, demand, deliveryTime, null, null, ProduceOrderStatus.CREATED);
    }

    public static ProduceOrder newOf(OuterOrderId outerId, ProduceOrderCode code, OuterFactory factory, Customer customer, List<StyleDemand> demand, DeliveryTime deliveryTime, ManufactureBatch manufactureBatch, ManufactureDate manufactureDate) {
        return new ProduceOrder(null, outerId, code, factory, customer, demand, deliveryTime, manufactureBatch, manufactureDate, ProduceOrderStatus.CREATED);
    }

    public ProduceOrder(ProduceOrderId id,
                        OuterOrderId outerId,
                        ProduceOrderCode code,
                        OuterFactory factory,
                        Customer customer,
                        List<StyleDemand> demands,
                        DeliveryTime deliveryTime,
                        ManufactureBatch manufactureBatch,
                        ManufactureDate manufactureDate,
                        ProduceOrderStatus status) {
        this.id = id;
        this.outerId = outerId;
        this.code = code;
        this.factory = factory;
        this.customer = customer;
        this.demands = demands;
        this.deliveryTime = deliveryTime;
        this.manufactureBatch = manufactureBatch;
        this.manufactureDate = manufactureDate;
        this.status = status;
    }

    private List<StyleDemand> mergeStyleDemands(List<StyleDemand> demands) {
        var demandMap = demands.stream().collect(Collectors.groupingBy(it -> it.getSkuCode().value(),
                Collectors.groupingBy(it -> it.getColor().value())));
        List<StyleDemand> res = Lists.newArrayListWithExpectedSize(demandMap.size());
        for (var demand : demandMap.entrySet()) {
            for (var colorDemand : demand.getValue().entrySet()) {
                if (colorDemand.getValue().size() == 1) {
                    res.add(colorDemand.getValue().get(0));
                } else {
                    res.add(mergeSameStyleDemand(colorDemand.getValue()));
                }
            }
        }
        return res;
    }

    private StyleDemand mergeSameStyleDemand(List<StyleDemand> demands) {
        int orderQuantity = 0, weaveQuantity = 0, sampleQuantity = 0;
        for (var demand : demands) {
            orderQuantity += demand.getOrderQuantity().getValue();
            weaveQuantity += demand.getWeaveQuantity().getValue();
            sampleQuantity += demand.getSampleQuantity().getValue();
        }
        return StyleDemand.of(demands.get(0).getSkuCode(), demands.get(0).getColor(),
                Quantity.of(orderQuantity), Quantity.of(weaveQuantity), Quantity.of(sampleQuantity));
    }

    @Override
    public int compareTo(@NotNull ProduceOrder o) {
        return this.deliveryTime.value().compareTo(o.deliveryTime.value());
    }

    public boolean unPlanned() {
        return this.status == ProduceOrderStatus.CREATED || this.status == ProduceOrderStatus.PLANNING_PRODUCING || this.status == ProduceOrderStatus.PLANNING_NO_PRODUCE;
    }

    public ProduceSkuDemand extractDemand() {
        var midRes = this.demands.stream()
                .collect(Collectors.teeing(
                        Collectors.mapping(StyleDemand::getSkuCode,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream().distinct().toList()
                        )),
                        Collectors.groupingBy(it -> it.getSkuCode().value()),
                        Pair::of
                ));
        return new ProduceSkuDemand(midRes.getLeft(), midRes.getRight());
    }

}
