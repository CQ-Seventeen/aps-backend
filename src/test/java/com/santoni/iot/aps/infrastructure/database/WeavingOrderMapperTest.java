package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.infrastructure.database.aps.WeavingOrderMapper;
import com.santoni.iot.aps.infrastructure.po.WeavingOrderPO;
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
public class WeavingOrderMapperTest {

    @Autowired
    private WeavingOrderMapper weavingOrderMapper;

    @Test
    public void testInsert() {
        var po = buildWeavingOrderPO();
        weavingOrderMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildWeavingOrderPO();
        var po_2 = buildAnotherWeavingOrderPO();

        weavingOrderMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("weavingOrderMapper查询测试")
    class WeavingOrderMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildWeavingOrderPO();
            var po_2 = buildAnotherWeavingOrderPO();

            weavingOrderMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetById() {
            var po = weavingOrderMapper.getById(1);
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByProduceOrder() {
            var poList = weavingOrderMapper.listByProduceOrderId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testListByCode() {
            var poList = weavingOrderMapper.listByCodeList(1, Lists.newArrayList("weavingOrder1", "weavingOrder2"));
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testUpdate() {
            var po = weavingOrderMapper.getById(1);
            var newTime = TimeUtil.getStartOf(LocalDateTime.now()).plusDays(60);
            po.setFinishTime(newTime);
            weavingOrderMapper.update(po, 2);

            po = weavingOrderMapper.getById(1);
            Assertions.assertEquals(newTime, po.getFinishTime());
            Assertions.assertEquals(2, po.getOperatorId());
        }

    }

    private WeavingOrderPO buildWeavingOrderPO() {
        var po = new WeavingOrderPO();
        po.setInstituteId(1);
        po.setCode("weavingOrder1");
        po.setProduceOrderId(1);
        po.setProduceOrderCode("produce1");
        po.setStyleCode("style1");
        po.setColor(JacksonUtil.toJson(Lists.newArrayList("红色")));
        po.setQuantity(200);
        po.setPlannedQuantity(0);
        po.setFinishTime(TimeUtil.getStartOf(LocalDateTime.now()).plusDays(30));
        po.setStatus(0);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }

    private WeavingOrderPO buildAnotherWeavingOrderPO() {
        var po = new WeavingOrderPO();
        po.setInstituteId(1);
        po.setCode("weavingOrder2");
        po.setProduceOrderId(1);
        po.setProduceOrderCode("produce1");
        po.setStyleCode("style2");
        po.setColor(JacksonUtil.toJson(Lists.newArrayList("红色")));
        po.setQuantity(300);
        po.setPlannedQuantity(0);
        po.setFinishTime(TimeUtil.getStartOf(LocalDateTime.now()).plusDays(30));
        po.setStatus(0);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }


}
