package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require;

import com.santoni.iot.aps.common.utils.TriMap;
import com.santoni.iot.aps.domain.support.entity.Quantity;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EndTimeColumn {

    private LocalDateTime endTime;

    private List<StyleRequirement> requirementList;

    private TriMap<Integer, Integer, Boolean, Double> neededTime = new TriMap<>();

    public EndTimeColumn(LocalDateTime endTime, List<StyleRequirement> requirementList) {
        this.endTime = endTime;
        this.requirementList = requirementList;
        initTriMap(requirementList);
    }

    private void initTriMap(List<StyleRequirement> requirementList) {
        for (var requirement : requirementList) {
            var styleComponent = requirement.getStyleComponent();
            var totalTime = styleComponent.actualSeconds(Quantity.of(requirement.getQuantity()));
            boolean bareSpandex = null != styleComponent.getRequirement()
                    && !CollectionUtils.isEmpty(styleComponent.getRequirement().getBareSpandexList())
                    && !StringUtils.equals(styleComponent.getRequirement().getBareSpandexList().get(0).value(), "NONE");
            var existTime = neededTime.get(styleComponent.getMachineSize().getCylinderDiameter().value(),
                    styleComponent.getMachineSize().getNeedleSpacing().value(),
                    bareSpandex);
            if (null == existTime) {
                neededTime.put(styleComponent.getMachineSize().getCylinderDiameter().value(),
                        styleComponent.getMachineSize().getNeedleSpacing().value(),
                        bareSpandex, totalTime);
            } else {
                neededTime.put(styleComponent.getMachineSize().getCylinderDiameter().value(),
                        styleComponent.getMachineSize().getNeedleSpacing().value(),
                        bareSpandex, existTime + totalTime);
            }
        }
    }
}
