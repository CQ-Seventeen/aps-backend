package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.infrastructure.database.aps.CustomerMapper;
import com.santoni.iot.aps.infrastructure.po.CustomerPO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    public void testInsert() {
        var po = buildCustomerPO();
        customerMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildCustomerPO();
        var po_2 = buildAnotherCustomerPO();

        customerMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("customerMapper查询测试")
    class CustomerMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildCustomerPO();
            var po_2 = buildAnotherCustomerPO();

            customerMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetByCode() {
            var po = customerMapper.getByCode(1, "code1");
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByCode() {
            var poList = customerMapper.listByCodeList(1, Lists.newArrayList("code1", "code2"));
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testUpdate() {
            var po = customerMapper.getByCode(1, "code1");
            po.setName("name11");
            customerMapper.update(po);

            po = customerMapper.getByCode(1, "code1");
            Assertions.assertEquals("name11", po.getName());
        }

        @Test
        public void testBatchUpdate() {
            var poList = customerMapper.listByCodeList(1, Lists.newArrayList("code1", "code2"));
            for (var po : poList) {
                po.setName("name111");
            }
            customerMapper.batchUpdate(poList);

            poList = customerMapper.listByCodeList(1, Lists.newArrayList("code1", "code2"));
            for (var po : poList) {
                Assertions.assertEquals("name111", po.getName());
            }
        }

    }

    private CustomerPO buildCustomerPO() {
        var po = new CustomerPO();
        po.setInstituteId(1);
        po.setCode("code1");
        po.setName("name1");
        return po;
    }

    private CustomerPO buildAnotherCustomerPO() {
        var po = new CustomerPO();
        po.setInstituteId(1);
        po.setCode("code2");
        po.setName("name2");
        return po;
    }


}
