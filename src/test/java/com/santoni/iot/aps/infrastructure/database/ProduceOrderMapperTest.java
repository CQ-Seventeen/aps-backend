package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.TimeUtil;
import com.santoni.iot.aps.infrastructure.database.aps.ProduceOrderMapper;
import com.santoni.iot.aps.infrastructure.po.ProduceOrderPO;
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
public class ProduceOrderMapperTest {

    @Autowired
    private ProduceOrderMapper produceOrderMapper;

    @Test
    public void testInsert() {
        var po = buildProduceOrderPO();
        produceOrderMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildProduceOrderPO();
        var po_2 = buildAnotherProduceOrderPO();

        produceOrderMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("produceOrderMapper查询测试")
    class ProduceOrderMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildProduceOrderPO();
            var po_2 = buildAnotherProduceOrderPO();

            produceOrderMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetById() {
            var po = produceOrderMapper.getById(1);
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByInstitute() {
            var poList = produceOrderMapper.listByInstituteId(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testUpdate() {
            var po = produceOrderMapper.getById(1);
            var newTime = TimeUtil.getStartOf(LocalDateTime.now()).plusDays(30);
            po.setDeliveryTime(newTime);
            produceOrderMapper.update(po, 2);

            po = produceOrderMapper.getById(1);
            Assertions.assertEquals(newTime, po.getDeliveryTime());
            Assertions.assertEquals(2, po.getOperatorId());
        }

    }

    private ProduceOrderPO buildProduceOrderPO() {
        var po = new ProduceOrderPO();
        po.setCode("produceOrder1");
        po.setInstituteId(1);
        po.setCustomerCode("customer1");
        po.setCustomerName("customer1");
        po.setDeliveryTime(TimeUtil.getStartOf(LocalDateTime.now()).plusDays(30));
        po.setStatus(0);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }

    private ProduceOrderPO buildAnotherProduceOrderPO() {
        var po = new ProduceOrderPO();
        po.setCode("produceOrder2");
        po.setInstituteId(1);
        po.setCustomerCode("customer1");
        po.setCustomerName("customer1");
        po.setDeliveryTime(TimeUtil.getStartOf(LocalDateTime.now()).plusDays(30));
        po.setStatus(0);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }


}
