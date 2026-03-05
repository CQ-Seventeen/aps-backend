package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.infrastructure.database.aps.StyleComponentMapper;
import com.santoni.iot.aps.infrastructure.po.StyleComponentPO;
import com.santoni.iot.aps.infrastructure.po.qo.ComponentPairQO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"}) // 确保 ddl.sql 中包含 style_component 表结构
public class StyleComponentMapperTest {

    @Autowired
    private StyleComponentMapper styleComponentMapper;

    @Test
    public void testInsert() {
        var po = buildStyleComponentPO();
        var result = styleComponentMapper.insert(po);
        assertEquals(1, result);
        assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po1 = buildStyleComponentPO();
        var po2 = buildAnotherStyleComponentPO();

        var result = styleComponentMapper.batchInsert(Lists.newArrayList(po1, po2));
        assertEquals(2, result); // MyBatis batchInsert 通常返回插入条数（取决于实现）
    }

    @Nested
    @DisplayName("styleComponentMapper 查询与更新测试")
    class StyleComponentQueryAndUpdateTest {

        private StyleComponentPO savedPo1;
        private StyleComponentPO savedPo2;

        @BeforeEach
        public void prepareData() {
            savedPo1 = buildStyleComponentPO();
            savedPo2 = buildAnotherStyleComponentPO();

            styleComponentMapper.insert(savedPo1);
            styleComponentMapper.insert(savedPo2);
        }

        @Test
        public void testFindBySkuCode() {
            var list = styleComponentMapper.findBySkuCode(1L, "SKU123");
            assertFalse(list.isEmpty());
            assertEquals(1, list.size());
            assertEquals("front", list.get(0).getPart());
        }

        @Test
        public void testListBySkuCodeList() {
            var list = styleComponentMapper.listBySkuCodeList(
                    1L, Lists.newArrayList("SKU123", "SKU456")
            );
            assertEquals(2, list.size());
        }

        @Test
        public void testGetBySkuCodeAndPart() {
            var po = styleComponentMapper.getBySkuCodeAndPart(1L, "SKU123", "front");
            assertNotNull(po);
            assertEquals("SKU123", po.getSkuCode());
            assertEquals("front", po.getPart());
        }

        @Test
        public void testListBySkuAndPartPair() {
            var pairs = Lists.newArrayList(
                    new ComponentPairQO("SKU123", "front"),
                    new ComponentPairQO("SKU456", "back")
            );
            List<StyleComponentPO> list = styleComponentMapper.listBySkuAndPartPair(1L, pairs);
            assertEquals(2, list.size());
        }

        @Test
        public void testUpdate() {
            savedPo1.setExpectedProduceTime(150.0);
            savedPo1.setDefaultEfficiency(new BigDecimal("0.85"));
            savedPo1.setModifiedTime(LocalDateTime.now());

            var result = styleComponentMapper.update(savedPo1);
            assertEquals(1, result);

            var updated = styleComponentMapper.getBySkuCodeAndPart(1L, "SKU123", "front");
            assertEquals(150.0, updated.getExpectedProduceTime());
            assertTrue(updated.getDefaultEfficiency().compareTo(new BigDecimal("0.85")) == 0);
        }

        @Test
        public void testDeleteBySkuCode() {
            var result = styleComponentMapper.deleteBySkuCode(1L, "SKU123");
            assertEquals(1, result);

            var list = styleComponentMapper.findBySkuCode(1L, "SKU123");
            assertTrue(list.isEmpty());
        }

        @Test
        public void testDelete() {
            // 先查出完整对象（含 id）
            var toDelete = styleComponentMapper.getBySkuCodeAndPart(1L, "SKU123", "front");
            assertNotNull(toDelete);

            var result = styleComponentMapper.delete(toDelete);
            assertEquals(1, result);

            var deleted = styleComponentMapper.getBySkuCodeAndPart(1L, "SKU123", "front");
            assertNull(deleted); // 假设 delete 是物理删除；如果是逻辑删除，应检查 deletedAt != 0
        }
    }

    private StyleComponentPO buildStyleComponentPO() {
        var po = new StyleComponentPO();
        po.setInstituteId(1L);
        po.setSkuCode("SKU123");
        po.setPart("front");
        po.setType(1);
        po.setNumber(2);
        po.setRatio(100);
        po.setCylinderDiameter(30);
        po.setNeedleSpacing(5);
        po.setDescription("Front panel");
        po.setExpectedProduceTime(120.0);
        po.setExpectedWeight(50.5);
        po.setStandardNumber(10);
        po.setMachineRequirement("MACHINE-A");
        po.setDefaultEfficiency(new BigDecimal("0.90"));
        po.setActualEfficiency(new BigDecimal("0.88"));
        po.setYarnUsage("YARN-001");
        po.setCreateTime(LocalDateTime.now());
        po.setModifiedTime(LocalDateTime.now());
        po.setDeletedAt(0L);
        return po;
    }

    private StyleComponentPO buildAnotherStyleComponentPO() {
        var po = new StyleComponentPO();
        po.setInstituteId(1L);
        po.setSkuCode("SKU456");
        po.setPart("back");
        po.setType(2);
        po.setNumber(1);
        po.setRatio(100);
        po.setCylinderDiameter(28);
        po.setNeedleSpacing(6);
        po.setDescription("Back panel");
        po.setExpectedProduceTime(100.0);
        po.setExpectedWeight(45.0);
        po.setStandardNumber(8);
        po.setMachineRequirement("MACHINE-B");
        po.setDefaultEfficiency(new BigDecimal("0.85"));
        po.setActualEfficiency(new BigDecimal("0.82"));
        po.setYarnUsage("YARN-002");
        po.setCreateTime(LocalDateTime.now());
        po.setModifiedTime(LocalDateTime.now());
        po.setDeletedAt(0L);
        return po;
    }
}