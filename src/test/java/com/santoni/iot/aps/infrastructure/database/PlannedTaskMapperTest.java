package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.infrastructure.database.aps.PlannedTaskMapper;
import com.santoni.iot.aps.infrastructure.po.PlannedTaskPO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class PlannedTaskMapperTest {

    @Autowired
    private PlannedTaskMapper plannedTaskMapper;

    @Test
    public void testInsert() {
        var po = buildPlannedTaskPO();
        plannedTaskMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildPlannedTaskPO();
        var po_2 = buildAnotherPlannedTaskPO();

        plannedTaskMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("plannedTaskMapper查询测试")
    class PlannedTaskMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildPlannedTaskPO();
            var po_2 = buildAnotherPlannedTaskPO();

            plannedTaskMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetById() {
            var po = plannedTaskMapper.getById(1);
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByMachineId() {
            var poList = plannedTaskMapper.listByMachineId(1,  null, null);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(1, poList.size());
        }

        @Test
        public void testListByMachineIdWithEndTime() {
            var endTime = TimeUtil.getStartOf(LocalDateTime.now()).plusDays(10);
            var poList = plannedTaskMapper.listByMachineId(1, null, endTime);
            Assertions.assertFalse(poList.isEmpty());
        }

        @Test
        public void testListByMachineIdWithStartTimeAndEndTime() {
            var now = TimeUtil.getStartOf(LocalDateTime.now());
            var endTime = now.plusDays(10);
            var poList = plannedTaskMapper.listByMachineId(1, now, endTime);
            Assertions.assertFalse(poList.isEmpty());
        }

        @Test
        public void testUpdate() {
            var po = plannedTaskMapper.getById(1);

            po.setPlannedQuantity(50);
            plannedTaskMapper.update(po, 2);

            po = plannedTaskMapper.getById(1);
            Assertions.assertEquals(50, po.getPlannedQuantity());
            Assertions.assertEquals(2, po.getOperatorId());
        }

        @Test
        public void testBatchUpdateStatus() {
            var po = plannedTaskMapper.getById(1);
            plannedTaskMapper.updateStatus(1, po.getStatus(), 2);

            po = plannedTaskMapper.getById(1);
            Assertions.assertEquals(2, po.getStatus());
        }

        @Test
        public void testListByWeavingOrder() {
            var poList = plannedTaskMapper.listByWeavingPartOrderId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

    }

    private PlannedTaskPO buildPlannedTaskPO() {
        var po = new PlannedTaskPO();
        po.setFactoryId(1L);
        po.setWeavingPartOrderId(1);
        po.setProduceOrderCode("produce");
        po.setMachineId(1);
        po.setStyleCode("style1");
        po.setPart("身");
        po.setPlannedQuantity(40);
        var nowTime = TimeUtil.getStartOf(LocalDateTime.now());
        po.setPlanStartTime(nowTime.plusDays(1));
        po.setPlanEndTime(nowTime.plusDays(30));
        po.setStatus(0);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }

    private PlannedTaskPO buildAnotherPlannedTaskPO() {
        var po = new PlannedTaskPO();
        po.setFactoryId(1L);
        po.setWeavingPartOrderId(1);
        po.setProduceOrderCode("produce");
        po.setMachineId(2);
        po.setStyleCode("style1");
        po.setPart("身");
        po.setPlannedQuantity(30);
        var nowTime = TimeUtil.getStartOf(LocalDateTime.now());
        po.setPlanStartTime(nowTime.plusDays(1));
        po.setPlanEndTime(nowTime.plusDays(20));
        po.setStatus(0);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }


}
