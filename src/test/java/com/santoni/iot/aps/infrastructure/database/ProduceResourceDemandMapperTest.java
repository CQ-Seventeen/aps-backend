package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.infrastructure.database.aps.ProduceOrderDemandMapper;
import com.santoni.iot.aps.infrastructure.po.ProduceOrderDemandPO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class ProduceResourceDemandMapperTest {

    @Autowired
    private ProduceOrderDemandMapper produceOrderDemandMapper;

    @Test
    public void testInsert() {
        var po = buildProduceOrderDemandPO();
        produceOrderDemandMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildProduceOrderDemandPO();
        var po_2 = buildAnotherProduceOrderDemandPO();

        produceOrderDemandMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("produceOrderDemandMapper查询测试")
    class ProduceOrderDemandMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildProduceOrderDemandPO();
            var po_2 = buildAnotherProduceOrderDemandPO();

            produceOrderDemandMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetByOrderAndStyle() {
            var po = produceOrderDemandMapper.getByStyleCodeAndOrder("style1", 1);
            Assertions.assertNotNull(po);
        }

        @Test
        public void testGetByOrderId() {
            var poList = produceOrderDemandMapper.listByProduceOrderId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testUpdate() {
            var po = produceOrderDemandMapper.getByStyleCodeAndOrder("style1", 1);

            po.setOrderQuantity(50);
            produceOrderDemandMapper.update(po);

            po = produceOrderDemandMapper.getByStyleCodeAndOrder("style1", 1);
            Assertions.assertEquals(50, po.getOrderQuantity());
        }

        @Test
        public void testBatchUpdate() {
            var poList = produceOrderDemandMapper.listByProduceOrderId(1);
            for (var po : poList) {
                po.setOrderQuantity(50);
            }
            produceOrderDemandMapper.batchUpdate(poList);

            poList = produceOrderDemandMapper.listByProduceOrderId(1);
            for (var po : poList) {
                Assertions.assertEquals(50, po.getOrderQuantity());
            }
        }

        @Test
        public void testBatchDelete() {
            var poList = produceOrderDemandMapper.listByProduceOrderId(1);
            Assertions.assertFalse(poList.isEmpty());
            produceOrderDemandMapper.batchDelete(poList);

            poList = produceOrderDemandMapper.listByProduceOrderId(1);
            Assertions.assertTrue(poList.isEmpty());
        }

    }

    private ProduceOrderDemandPO buildProduceOrderDemandPO() {
        var po = new ProduceOrderDemandPO();
        po.setProduceOrderId(1);
        po.setStyleCode("style1");
        po.setColor("red");
        po.setOrderQuantity(100);
        po.setWeaveQuantity(120);
        po.setSampleQuantity(20);
        return po;
    }

    private ProduceOrderDemandPO buildAnotherProduceOrderDemandPO() {
        var po = new ProduceOrderDemandPO();
        po.setProduceOrderId(1);
        po.setStyleCode("style2");
        po.setColor("red");
        po.setOrderQuantity(100);
        po.setWeaveQuantity(120);
        po.setSampleQuantity(20);
        return po;
    }


}
