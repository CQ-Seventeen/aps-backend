package com.santoni.iot.aps.application.plan;

import com.santoni.iot.aps.application.order.query.ProduceOrderQuery;
import com.santoni.iot.aps.application.plan.dto.order.ProduceLevelPlanDTO;
import com.santoni.iot.aps.application.plan.dto.track.ProducePlanTrackDTO;
import com.santoni.iot.aps.application.plan.dto.track.ProducePredictDTO;
import com.santoni.iot.aps.application.plan.query.DailyYarnPredictQuery;
import com.santoni.iot.aps.application.plan.query.OrderPlanFilterByMachineQuery;
import com.santoni.iot.aps.application.plan.query.ProducePlanTrackQuery;

import java.util.List;

public interface PlanTrackApplication {

    List<ProducePlanTrackDTO> queryProducePlanTrack(ProducePlanTrackQuery query);

    List<ProducePlanTrackDTO> queryOrderPlanFilterByMachine(OrderPlanFilterByMachineQuery query);

    ProducePredictDTO dailyYarnPredict(DailyYarnPredictQuery query);

    List<ProducePlanTrackDTO> queryProduceOrderPlan(ProduceOrderQuery query);

    List<ProduceLevelPlanDTO> queryUnFinishedOrderPlan();

}
