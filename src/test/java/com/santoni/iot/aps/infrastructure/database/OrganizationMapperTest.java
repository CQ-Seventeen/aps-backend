package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.infrastructure.database.aps.OrganizationMapper;
import com.santoni.iot.aps.infrastructure.po.OrganizationPO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class OrganizationMapperTest {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Test
    public void testInsert() {
        var po = buildOrgPO();
        organizationMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildOrgPO();
        var po_2 = buildAnotherOrgPO();

        organizationMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("orgMapper查询测试")
    class StyleMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildOrgPO();
            var po_2 = buildAnotherOrgPO();

            organizationMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetById() {
            var po = organizationMapper.getById(1);
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListChildren() {
            var poList = organizationMapper.listChildren(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(1, poList.size());
        }

    }

    private OrganizationPO buildOrgPO() {
        var po = new OrganizationPO();
        po.setCode("ins1");
        po.setParentId(0L);
        po.setLevel(0);
        return po;
    }

    private OrganizationPO buildAnotherOrgPO() {
        var po = new OrganizationPO();
        po.setCode("fac1");
        po.setParentId(1);
        po.setLevel(1);
        return po;
    }


}
