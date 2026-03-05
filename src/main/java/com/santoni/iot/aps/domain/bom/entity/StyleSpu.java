package com.santoni.iot.aps.domain.bom.entity;

import com.santoni.iot.aps.domain.bom.entity.valueobj.*;
import com.santoni.iot.aps.domain.order.entity.valueobj.OuterOrderId;
import com.santoni.iot.aps.domain.order.entity.valueobj.ProduceOrderCode;
import com.santoni.iot.aps.domain.support.entity.valueobj.ImgUrl;
import com.santoni.iot.aps.domain.support.entity.valueobj.Symbol;
import lombok.Getter;

import java.util.List;

@Getter
public class StyleSpu {

    private StyleId id;

    private OuterOrderId outerProduceOrderId;

    private ProduceOrderCode produceOrderCode;

    private OuterStyleId outerId;

    private Symbol symbol;

    private StyleCode code;

    private StyleName name;

    private StyleDesc description;

    private List<ImgUrl> styleImages;

    public StyleSpu(StyleId id,
                    OuterOrderId outerProduceOrderId,
                    ProduceOrderCode produceOrderCode,
                    OuterStyleId outerId,
                    Symbol symbol,
                    StyleCode code,
                    StyleName name,
                    StyleDesc description,
                    List<ImgUrl> styleImages) {
        this.id = id;
        this.outerProduceOrderId = outerProduceOrderId;
        this.produceOrderCode = produceOrderCode;
        this.outerId = outerId;
        this.symbol = symbol;
        this.code = code;
        this.name = name;
        this.description = description;
        this.styleImages = styleImages;
    }

    public static StyleSpu of(StyleCode code, StyleName name) {
        return of(code, name, null);
    }

    public static StyleSpu of(StyleCode code, StyleName name, StyleDesc description) {
        return of(null, null, null, null, code, name, description, null);
    }

    public static StyleSpu of(OuterOrderId outerProduceOrderId,
                              ProduceOrderCode produceOrderCode,
                              OuterStyleId outerId,
                              Symbol symbol,
                              StyleCode code,
                              StyleName name,
                              StyleDesc description,
                              List<ImgUrl> styleImages) {
        if (null == code) {
            throw new IllegalArgumentException("款式编号不可为空");
        }
        return new StyleSpu(null, outerProduceOrderId, produceOrderCode, outerId, symbol, code, name, description, styleImages);
    }
}
