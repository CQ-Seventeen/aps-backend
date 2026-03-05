package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.infrastructure.database.aps.TaskSegmentMapper;
import com.santoni.iot.aps.infrastructure.po.TaskSegmentPO;
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
public class TaskSegmentMapperTest {

    @Autowired
    private TaskSegmentMapper taskSegmentMapper;

    @Test
    public void testInsert() {
        var po = buildTaskSegmentPO();
        taskSegmentMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildTaskSegmentPO();
        var po_2 = buildAnotherTaskSegmentPO();

        taskSegmentMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("taskSegmentMapper查询测试")
    class TaskSegmentMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildTaskSegmentPO();
            var po_2 = buildAnotherTaskSegmentPO();

            taskSegmentMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetById() {
            var po = taskSegmentMapper.getById(1);
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByTaskId() {
            var poList = taskSegmentMapper.listByTaskId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }


        @Test
        public void testUpdate() {
            var po = taskSegmentMapper.getById(1);

            var nowTime = TimeUtil.getStartOf(LocalDateTime.now());
            po.setPlanStartTime(nowTime.plusDays(30));
            po.setPlanEndTime(nowTime.plusDays(40));
            po.setSortIndex(2);
            taskSegmentMapper.update(po, 2);

            po = taskSegmentMapper.getById(1);
            Assertions.assertEquals(nowTime.plusDays(30), po.getPlanStartTime());
            Assertions.assertEquals(nowTime.plusDays(40), po.getPlanEndTime());
            Assertions.assertEquals(2, po.getSortIndex());
            Assertions.assertEquals(2, po.getOperatorId());
        }

        @Test
        public void testBatchUpdate() {
            var poList = taskSegmentMapper.listByTaskId(1);

            for (var po : poList) {
                po.setSortIndex(po.getSortIndex() + 1);
            }
            taskSegmentMapper.batchUpdate(poList, 2);

            poList = taskSegmentMapper.listByTaskId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertTrue(poList.stream().noneMatch(it -> it.getSortIndex() == 0));
        }

        @Test
        public void testDelete() {
            var poList = taskSegmentMapper.listByTaskId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());

            taskSegmentMapper.delete(poList.get(0).getId(), 2);

            poList = taskSegmentMapper.listByTaskId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(1, poList.size());
        }

    }

    private TaskSegmentPO buildTaskSegmentPO() {
        var po = new TaskSegmentPO();
        po.setTaskId(1);
        po.setPlannedQuantity(10);
        var nowTime = TimeUtil.getStartOf(LocalDateTime.now());
        po.setPlanStartTime(nowTime.plusDays(1));
        po.setPlanEndTime(nowTime.plusDays(10));
        po.setStatus(0);
        po.setSortIndex(0);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }

    private TaskSegmentPO buildAnotherTaskSegmentPO() {
        var po = new TaskSegmentPO();
        po.setTaskId(1);
        po.setPlannedQuantity(20);
        var nowTime = TimeUtil.getStartOf(LocalDateTime.now());
        po.setPlanStartTime(nowTime.plusDays(10));

        po.setPlanEndTime(nowTime.plusDays(30));
        po.setStatus(0);
        po.setSortIndex(1);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }


}
