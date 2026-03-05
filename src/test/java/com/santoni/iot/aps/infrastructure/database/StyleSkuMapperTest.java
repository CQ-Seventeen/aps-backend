package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.infrastructure.database.aps.StyleSkuMapper;
import com.santoni.iot.aps.infrastructure.po.StyleSkuPO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class StyleSkuMapperTest {

    @Autowired
    private StyleSkuMapper styleSkuMapper;

    @Test
    public void testInsert() {
        var po = buildStyleSkuPO();
        styleSkuMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildStyleSkuPO();
        var po_2 = buildAnotherStyleSkuPO();

        styleSkuMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("styleSkuMapper查询测试")
    class StyleMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildStyleSkuPO();
            var po_2 = buildAnotherStyleSkuPO();

            styleSkuMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetByCode() {
            var po = styleSkuMapper.findBySkuCode(1, "code1x");
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByCode() {
            var poList = styleSkuMapper.listBySkuCodeList(1, Lists.newArrayList("code1x", "code2x"));
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testUpdate() {
            var po = styleSkuMapper.findBySkuCode(1, "code1x");
            po.setExpectedProduceTime(200);
            styleSkuMapper.update(po);

            po = styleSkuMapper.findBySkuCode(1, "code1x");
            Assertions.assertEquals(200, po.getExpectedProduceTime());
        }

    }

    private StyleSkuPO buildStyleSkuPO() {
        var po = new StyleSkuPO();
        po.setInstituteId(1);
        po.setCode("code1x");
        po.setStyleCode("code1");
        po.setSize("x");
        return po;
    }

    private StyleSkuPO buildAnotherStyleSkuPO() {
        var po = new StyleSkuPO();
        po.setInstituteId(1);
        po.setCode("code2x");
        po.setStyleCode("code2");
        po.setSize("x");
        return po;
    }


}
