package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.santoni.iot.aps.infrastructure.database.aps.MachineMapper;
import com.santoni.iot.aps.infrastructure.po.MachinePO;
import com.santoni.iot.aps.infrastructure.po.qo.MachineSizeQO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchMachineQO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class MachineMapperTest {

    @Autowired
    private MachineMapper machineMapper;

    @Test
    public void testInsert() {
        var po = buildMachinePO();
        machineMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildMachinePO();
        var po_2 = buildAnotherMachinePO();

        machineMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("machineMapper查询测试")
    class MachineMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildMachinePO();
            var po_2 = buildAnotherMachinePO();

            machineMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetById() {
            var po = machineMapper.getById(1);
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListById() {
            var poList = machineMapper.listById(Lists.newArrayList(1L, 2L));
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testGetByDeviceId() {
            var po = machineMapper.getByDeviceId(1, 1, "1");
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByOptions() {
            var sizeQO = new MachineSizeQO();
            sizeQO.setCylinderDiameter(28);
            sizeQO.setNeedleSpacing(1440);
            var qo = new SearchMachineQO();
            qo.setInstituteId(1L);
            qo.setSizeList(Lists.newArrayList(sizeQO));

            var poList = machineMapper.listByOptions(qo, null);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testUpdate() {
            var po = machineMapper.getById(1);
            po.setCylinderDiameter(30);
            machineMapper.update(po);

            po = machineMapper.getById(1);
            Assertions.assertEquals(30, po.getCylinderDiameter());
        }

    }

    private MachinePO buildMachinePO() {
        var po = new MachinePO();
        po.setDeviceId("1");
        po.setCode("code1");
        po.setInstituteId(1);
        po.setFactoryId(1);
        po.setWorkshopId(1);
        po.setMachineGroupId(1);
        po.setCylinderDiameter(28);
        po.setNeedleSpacing(20);
        po.setNeedleNumber(1440);
        po.setMachineType("SMP");
        po.setBareSpandexType(null);
        po.setHighSpeed(false);
        Map<String, List<String>> features = Maps.newHashMap();
        features.put("test1", Lists.newArrayList("v1", "v2"));
        po.setFeatures(features);
        po.setStatus(2);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }

    private MachinePO buildAnotherMachinePO() {
        var po = new MachinePO();
        po.setDeviceId("2");
        po.setCode("code2");
        po.setInstituteId(1);
        po.setFactoryId(1);
        po.setWorkshopId(1);
        po.setMachineGroupId(1);
        po.setCylinderDiameter(28);
        po.setNeedleSpacing(20);
        po.setNeedleNumber(1440);
        po.setMachineType("SMP");
        po.setBareSpandexType(null);
        po.setHighSpeed(false);
        po.setStatus(2);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }


}
