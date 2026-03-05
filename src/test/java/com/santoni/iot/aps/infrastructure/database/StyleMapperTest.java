package com.santoni.iot.aps.infrastructure.database;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.santoni.iot.aps.infrastructure.database.aps.StyleMapper;
import com.santoni.iot.aps.infrastructure.po.StylePO;
import com.santoni.iot.aps.infrastructure.po.qo.SearchStyleQO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class StyleMapperTest {

    @Autowired
    private StyleMapper styleMapper;

    @Test
    public void testInsert() {
        var po = buildStylePO();
        styleMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildStylePO();
        var po_2 = buildAnotherStylePO();

        styleMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("styleMapper查询测试")
    class StyleMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildStylePO();
            var po_2 = buildAnotherStylePO();

            styleMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetByCode() {
            var po = styleMapper.getByCode(1, "code1");
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByCode() {
            var poList = styleMapper.listByCodeList(1, Lists.newArrayList("code1", "code2"));
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testSearchStyle() {
            var qo = new SearchStyleQO();
            qo.setCode("code");
            IPage<StylePO> page = new Page<>(1, 10);
            var pageRes = styleMapper.searchStyle(page, 1, qo);
            Assertions.assertFalse(pageRes.getRecords().isEmpty());
            Assertions.assertEquals(2, pageRes.getRecords().size());
        }

        @Test
        public void testUpdate() {
            var po = styleMapper.getByCode(1, "code1");
            po.setDescription("200");
            styleMapper.update(po);

            po = styleMapper.getByCode(1, "code1");
            Assertions.assertEquals("200", po.getDescription());
        }

        @Test
        public void testBatchUpdate() {
            var poList = styleMapper.listByCodeList(1, Lists.newArrayList("code1", "code2"));
            for (var po : poList) {
                po.setDescription("200");
            }
            styleMapper.batchUpdate(poList);

            poList = styleMapper.listByCodeList(1, Lists.newArrayList("code1", "code2"));
            for (var po : poList) {
                Assertions.assertEquals("200", po.getDescription());
            }
        }

    }

    private StylePO buildStylePO() {
        var po = new StylePO();
        po.setInstituteId(1);
        po.setCode("code1");
        po.setDescription("desc1");
        return po;
    }

    private StylePO buildAnotherStylePO() {
        var po = new StylePO();
        po.setInstituteId(1);
        po.setCode("code2");
        po.setDescription("desc2");
        return po;
    }


}
