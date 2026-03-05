package com.santoni.iot.aps.infrastructure.database;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.utils.JacksonUtil;
import com.santoni.iot.aps.domain.support.entity.valueobj.OptionType;
import com.santoni.iot.aps.infrastructure.database.aps.MachineAttrConfigMapper;
import com.santoni.iot.aps.infrastructure.po.MachineAttrConfigPO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class MachineAttrConfigMapperTest {

    @Autowired
    private MachineAttrConfigMapper machineAttrConfigMapper;

    @Test
    public void testInsert() {
        var po = buildMachineAttrConfigPO();
        machineAttrConfigMapper.insert(po);
        Assertions.assertTrue(po.getId() > 0);
    }

    @Test
    public void testBatchInsert() {
        var po_1 = buildMachineAttrConfigPO();
        var po_2 = buildAnotherMachineAttrConfigPO();

        machineAttrConfigMapper.batchInsert(Lists.newArrayList(po_1, po_2));
    }

    @Nested
    @DisplayName("machineAttrConfigMapper查询测试")
    class MachineAttrConfigMapperNestTest {

        @BeforeEach
        public void prepareData() {
            var po_1 = buildMachineAttrConfigPO();
            var po_2 = buildAnotherMachineAttrConfigPO();

            machineAttrConfigMapper.batchInsert(Lists.newArrayList(po_1, po_2));
        }

        @Test
        public void testGetByCode() {
            var po = machineAttrConfigMapper.getByAttrCode(1, "attr1");
            Assertions.assertNotNull(po);
        }

        @Test
        public void testListByCode() {
            var poList = machineAttrConfigMapper.listByCodeList(1, Lists.newArrayList("attr1", "attr2"));
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

        @Test
        public void testUpdate() {
            var po = machineAttrConfigMapper.getByAttrCode(1, "attr1");
            po.setOptionType(OptionType.MULTIPLE.getCode());
            machineAttrConfigMapper.update(po, 1);

            po = machineAttrConfigMapper.getByAttrCode(1, "attr1");
            Assertions.assertEquals(OptionType.MULTIPLE.getCode(), po.getOptionType());
        }

        @Test
        public void testListAll() {
            var poList = machineAttrConfigMapper.listAll(1);
            Assertions.assertFalse(poList.isEmpty());
            Assertions.assertEquals(2, poList.size());
        }

    }

    private MachineAttrConfigPO buildMachineAttrConfigPO() {
        var po = new MachineAttrConfigPO();
        po.setInstituteId(1);
        po.setAttrCode("attr1");
        po.setOptionalValues(JacksonUtil.toJson(Lists.newArrayList("v1", "v2")));
        po.setOptionType(OptionType.SINGLE.getCode());
        po.setUseToFilter(false);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }

    private MachineAttrConfigPO buildAnotherMachineAttrConfigPO() {
        var po = new MachineAttrConfigPO();
        po.setInstituteId(1);
        po.setAttrCode("attr2");
        po.setOptionalValues(JacksonUtil.toJson(Lists.newArrayList("v21", "v22")));
        po.setOptionType(OptionType.SINGLE.getCode());
        po.setUseToFilter(true);
        po.setCreatorId(1);
        po.setOperatorId(1);
        return po;
    }
}
