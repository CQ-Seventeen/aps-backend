package com.santoni.iot.aps.domain.execute.entity;

import com.santoni.iot.aps.domain.execute.constant.SumKeyType;
import com.santoni.iot.aps.domain.execute.entity.valueobj.AlteredQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceDate;
import com.santoni.iot.aps.domain.execute.entity.valueobj.ProduceQuantity;
import com.santoni.iot.aps.domain.execute.entity.valueobj.SumKey;
import com.santoni.iot.aps.domain.support.entity.organization.valueobj.FactoryId;
import com.santoni.iot.aps.domain.support.entity.valueobj.RecordId;
import lombok.Getter;

@Getter
public class ProductionSum {

    private RecordId id;

    private FactoryId factoryId;

    private SumKey key;

    private SumKeyType keyType;

    private ProduceQuantity quantity;

    private ProduceQuantity tillTodayQuantity;

    private ProduceQuantity defectQuantity;

    private ProduceQuantity defectTdQuantity;

    private ProduceDate date;

    public ProductionSum(RecordId id,
                         FactoryId factoryId,
                         SumKey key,
                         SumKeyType keyType,
                         ProduceQuantity quantity,
                         ProduceQuantity tillTodayQuantity,
                         ProduceQuantity defectQuantity,
                         ProduceQuantity defectTdQuantity,
                         ProduceDate date) {
        this.id = id;
        this.factoryId = factoryId;
        this.key = key;
        this.keyType = keyType;
        this.quantity = quantity;
        this.tillTodayQuantity = tillTodayQuantity;
        this.defectQuantity = defectQuantity;
        this.defectTdQuantity = defectTdQuantity;
        this.date = date;
    }

    public static ProductionSum newSum(FactoryId factoryId, SumKey key,
                                       SumKeyType keyType, ProduceQuantity quantity,
                                       ProduceQuantity defectQuantity, ProduceDate date) {
        return new ProductionSum(null, factoryId, key, keyType, quantity, quantity.copy(), defectQuantity, defectQuantity.copy(), date);
    }

    public ProductionSum accumulateNextDay(ProduceDate date, ProduceQuantity quantity) {
        return new ProductionSum(null, this.factoryId, this.key, this.keyType,
                quantity, this.tillTodayQuantity.plus(quantity), this.defectQuantity.copy(),
                this.defectTdQuantity.copy(), date);
    }

    public AlteredQuantity modifyQuantity(ProduceQuantity newQuantity) {
        var altered = AlteredQuantity.from(this.quantity, newQuantity);
        this.quantity = newQuantity;
        this.tillTodayQuantity = this.tillTodayQuantity.applyAlter(altered);
        return altered;
    }

    public void modifyQuantity(ProduceQuantity newQuantity, ProduceQuantity newDefectQuantity) {
        var altered = AlteredQuantity.from(this.quantity, newQuantity);
        this.quantity = newQuantity;
        this.tillTodayQuantity = this.tillTodayQuantity.applyAlter(altered);

        var defectAltered = AlteredQuantity.from(this.defectTdQuantity, newDefectQuantity);
        this.defectQuantity = newDefectQuantity;
        this.defectTdQuantity = this.defectTdQuantity.applyAlter(defectAltered);
    }

    public AlteredQuantity applyAlter(AlteredQuantity alter) {
        this.quantity = this.quantity.applyAlter(alter);
        this.tillTodayQuantity = this.tillTodayQuantity.applyAlter(alter);
        return alter;
    }

    public ProductionSum accumulateNextDay(ProduceDate date, ProduceQuantity quantity, ProduceQuantity defectQuantity) {
        return new ProductionSum(null, this.factoryId, this.key, this.keyType,
                quantity, this.tillTodayQuantity.plus(quantity),
                defectQuantity, this.defectTdQuantity.plus(defectQuantity),
                date);
    }
}
