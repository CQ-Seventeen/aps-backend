package com.santoni.iot.aps.domain.bom.repository;

import com.santoni.iot.aps.application.bom.query.PageStyleQuery;
import com.santoni.iot.aps.domain.bom.entity.*;
import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.support.entity.PageData;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;

public interface StyleRepository {

    StyleSpu getStyleById(StyleId id);

    StyleSpu getStyleByCode(ProduceOrderCode orderCode, StyleCode code);

    StyleSpu lockStyleByCode(ProduceOrderCode orderCode, StyleCode code);

    List<StyleSpu> listStyleByCode(List<StyleCode> codeList);

    void saveStyle(StyleSpu style, List<StyleSku> skuList);

    void batchSaveStyle(List<StyleSpu> styleList);

    PageData<StyleSpu> pageQueryStyle(PageStyleQuery query);

    List<StyleSku> listStyleSkuByCode(ProduceOrderCode produceOrderCode, Collection<SkuCode> skuCodeList, boolean needComponent);

    StyleSku getStyleSkuByCode(ProduceOrderCode orderCode, SkuCode skuCode, boolean needComponent);

    List<StyleSku> listStyleSkuByStyleCode(ProduceOrderCode orderCode, StyleCode styleCode, boolean needComponent);

    StyleSku getStyleSkuByStyleAndSize(StyleCode styleCode, Size size, boolean needComponent);

    StyleComponent getComponentBySkuAndPart(ProduceOrderCode orderCode, SkuCode skuCode, Part part);

    List<StyleComponent> listComponentBySkuAndPart(List<Pair<SkuCode, Part>> pairList);

    List<StyleSku> listStyleSkuByOrderCode(Collection<ProduceOrderCode> orderCodeList, boolean needComponent);

}
