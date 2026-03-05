package com.santoni.iot.aps.domain.resource;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.domain.resource.entity.MachineFeature;
import com.santoni.iot.aps.domain.resource.entity.valueobj.*;
import com.santoni.iot.aps.domain.support.entity.MachineSize;
import com.santoni.iot.aps.domain.support.entity.valueobj.CylinderDiameter;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleNumber;
import com.santoni.iot.aps.domain.support.entity.valueobj.NeedleSpacing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MachineOptionTest {

    @Test
    public void testMergeOptionIntersect() {
        var options = buildMachineOptions();
        var anotherOptions = buildAnotherMachineOptions();

        options.intersectWith(anotherOptions);
        Assertions.assertEquals(1, options.getMachineSizeList().size());
        Assertions.assertEquals(1, options.getMachineTypeList().size());
        Assertions.assertEquals(1, options.getBareSpandexList().size());
        Assertions.assertEquals(2, options.getHighSpeedList().size());
        Assertions.assertEquals(1, options.getFeatureList().size());
        Assertions.assertTrue(options.getFeatureList().stream().anyMatch(it -> it.getAttr().code().equals("attr1")));
    }

    @Test
    public void testUnionOptionUnion() {
        var options = buildMachineOptions();
        var anotherOptions = buildAnotherMachineOptions();

        options.unionWith(anotherOptions);
        Assertions.assertEquals(2, options.getMachineSizeList().size());
        Assertions.assertEquals(2, options.getMachineTypeList().size());
        Assertions.assertEquals(2, options.getBareSpandexList().size());
        Assertions.assertEquals(2, options.getHighSpeedList().size());
        Assertions.assertEquals(2, options.getFeatureList().size());
        Assertions.assertTrue(options.getFeatureList().stream().anyMatch(it -> it.getAttr().code().equals("attr1")));
        Assertions.assertTrue(options.getFeatureList().stream().anyMatch(it -> it.getAttr().code().equals("attr2")));
        Assertions.assertTrue(options.getFeatureList().stream().filter(it -> it.getAttr().code().equals("attr1")).anyMatch(it -> it.getValueList().size() == 2));
    }

    private MachineOptions buildMachineOptions() {
        return new MachineOptions(
                Lists.newArrayList(MachineSize.styleSize(new CylinderDiameter(14), new NeedleSpacing(28))),
                Lists.newArrayList(new MachineType("TOP2FAST")),
                Lists.newArrayList(new BareSpandex("None")),
                List.of(),
                Lists.newArrayList(new MachineFeature(new MachineAttr("attr1"), Lists.newArrayList(new MachineAttrValue("value1")))),
                List.of()
        );
    }

    private MachineOptions buildAnotherMachineOptions() {
        return new MachineOptions(
                Lists.newArrayList(MachineSize.styleSize(new CylinderDiameter(14), new NeedleSpacing(28)),
                        MachineSize.styleSize(new CylinderDiameter(14), new NeedleSpacing(40))),
                Lists.newArrayList(new MachineType("TOP2FAST"), new MachineType("EX8FAST")),
                Lists.newArrayList(new BareSpandex("None"), new BareSpandex("ELAN 21")),
                Lists.newArrayList(HighSpeed.high(), HighSpeed.low()),
                Lists.newArrayList(new MachineFeature(new MachineAttr("attr1"), Lists.newArrayList(new MachineAttrValue("value1"), new MachineAttrValue("value2"))),
                        new MachineFeature(new MachineAttr("attr2"), Lists.newArrayList(new MachineAttrValue("value3")))),
                List.of()
        );
    }
}
