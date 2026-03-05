package com.santoni.iot.aps.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListOperateUtil {

    public static <T, E> List<T> operate(List<T> lOperand, List<T> rOperand, Function<T, E> getProperty, boolean isIntersect) {
        if (CollectionUtils.isEmpty(rOperand)) {
            return lOperand;
        }
        if (CollectionUtils.isEmpty(lOperand)) {
            return rOperand;
        }
        Set<E> propertySet = lOperand.stream().map(getProperty).collect(Collectors.toSet());
        List<T> result = Lists.newArrayList();
        for (T element : rOperand) {
            if (isIntersect && propertySet.contains(getProperty.apply(element))) {
                result.add(element);
            }
            if (!isIntersect && !propertySet.contains(getProperty.apply(element))) {
                result.add(element);
            }
        }
        if (!isIntersect) {
            result.addAll(lOperand);
        }
        return result;
    }
}
