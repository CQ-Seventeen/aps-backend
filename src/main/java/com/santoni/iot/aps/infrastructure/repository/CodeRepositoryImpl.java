package com.santoni.iot.aps.infrastructure.repository;

import com.google.common.collect.Lists;
import com.santoni.iot.aps.common.context.PlanContext;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.order.entity.valueobj.WeavingOrderCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.CustomerCode;
import com.santoni.iot.aps.domain.bom.entity.valueobj.StyleCode;
import com.santoni.iot.aps.domain.support.repository.CodeRepository;
import com.santoni.iot.aps.infrastructure.database.aps.CodeGeneratorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Repository
public class CodeRepositoryImpl implements CodeRepository {

    @Autowired
    private CodeGeneratorMapper codeGeneratorMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final String STYLE_CODE_TYPE = "Style";

    private static final String WEAVING_ORDER_CODE_TYPE = "WeavingOrder";

    private static final String PRODUCE_ORDER_CODE_TYPE = "ProduceOrder";

    private static final String CUSTOMER_CODE_TYPE = "Customer";

    @Override
    public StyleCode getStyleCode() {
        var code = getCode(STYLE_CODE_TYPE, 1);
        return new StyleCode("ST" + code.get(0));
    }

    @Override
    public List<StyleCode> getMultiStyleCode(int num) {
        var codeList = getCode(STYLE_CODE_TYPE, num);
        return codeList.stream().map(it -> new StyleCode("ST" + it)).toList();
    }

    @Override
    public WeavingOrderCode getWeavingOrderCode() {
        var code = getCode(WEAVING_ORDER_CODE_TYPE, 1);
        return new WeavingOrderCode("WO" + code.get(0));
    }

    @Override
    public List<WeavingOrderCode> getMultiWeavingOrderCode(int num) {
        var codeList = getCode(WEAVING_ORDER_CODE_TYPE, num);
        return codeList.stream().map(it -> new WeavingOrderCode("WO" + it)).toList();
    }

    @Override
    public ProduceOrderCode getProduceOrderCode() {
        var code = getCode(PRODUCE_ORDER_CODE_TYPE, 1);
        return new ProduceOrderCode("PO" + code.get(0));
    }

    @Override
    public List<ProduceOrderCode> getMultiProduceOrderCode(int num) {
        var codeList = getCode(PRODUCE_ORDER_CODE_TYPE, num);
        return codeList.stream().map(it -> new ProduceOrderCode("PO" + it)).toList();
    }

    @Override
    public CustomerCode getCustomerCode() {
        var code = getCode(CUSTOMER_CODE_TYPE, 1);
        return new CustomerCode("CUS" + code.get(0));
    }

    private List<String> getCode(String type, int number) {
        var current = (Long) transactionTemplate.execute(status -> {
            try {
                var cur = codeGeneratorMapper.lock(type, PlanContext.getInstituteId());
                codeGeneratorMapper.updateAtomically(PlanContext.getInstituteId(), type, number);
                return cur;
            } catch (Exception e) {
                status.setRollbackOnly();
                return 0L;
            }
        });
        if (null == current || current < 0) {
            throw new IllegalArgumentException("获取编号失败");
        }
        List<String> result = Lists.newArrayListWithExpectedSize(number);
        for (int i = 0; i < number; i++) {
            result.add(String.format("%09d", current + i));
        }
        return result;
    }
}
