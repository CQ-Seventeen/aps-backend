package com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.santoni.iot.aps.domain.schedule.solver.greedy.entity.snapshot.require.StyleRequirement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StyleRequireTable {

    private Map<String, TreeSet<StyleRequirement>> table = Maps.newHashMap();

    private ArrayList<String> styles = Lists.newArrayList();

    public void put(StyleRequirement requirement) {
        var row = table.get(requirement.getKey());
        if (null == row) {
            var treeSet = Sets.newTreeSet(StyleRequirement::compareTo);
            treeSet.add(requirement);
            table.put(requirement.getKey(), treeSet);
            insertToSortList(requirement.getKey());
        } else {
            row.add(requirement);
            modifySortList(requirement.getKey());
        }
    }

    public StyleRequirement getFirst() {
        if (CollectionUtils.isEmpty(styles)) {
            return null;
        }
        var styleCode = styles.get(0);
        var requirements = table.get(styleCode);
        if (CollectionUtils.isEmpty(requirements)) {
            return null;
        }
        return requirements.first();
    }

    public void arranged(String styleCode, long weavingOrderId, int quantity) {
        var requirements = table.get(styleCode);
        if (CollectionUtils.isEmpty(requirements)) {
            throw new IllegalArgumentException("Requirements of " + styleCode + " not exist");
        }
        var requirement = requirements.stream()
                .filter(it -> it.getWeavingPartOrderId() == weavingOrderId && it.getQuantity() >= quantity)
                .findFirst().orElse(null);
        if (null == requirement) {
            throw new IllegalArgumentException("Requirement of " + styleCode + " left quantity less than " + quantity);
        }
        requirement.arrangedQuantity(quantity);

        requirements.remove(requirement);
        if (requirement.hasLeft()) {
            requirements.add(requirement);
        }
        modifySortList(styleCode);
    }

    private void insertToSortList(String styleCode) {
        if (CollectionUtils.isEmpty(styles)) {
            styles.add(styleCode);
            return;
        }
        var newIndex = findStyleIndex(styleCode);
        styles.add(newIndex, styleCode);
    }

    private void modifySortList(String styleCode) {
        var index = findExistStyleIndex(styleCode);
        styles.remove(index);
        var newIndex = findStyleIndex(styleCode);
        styles.add(newIndex, styleCode);
    }

    private int findStyleIndex(String styleCode) {
        int low = 0, high = styles.size() - 1;
        if (compareStyle(styleCode, styles.get(low)) < 0) {
            return low;
        }
        if (compareStyle(styleCode, styles.get(high)) > 0) {
            return high;
        }
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int comp = compareStyle(styles.get(mid), styleCode);

            if (comp < 0) {
                low = mid + 1;
            } else if (comp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return low;
    }

    private int findExistStyleIndex(String styleCode) {
        for (int i = 0; i < styles.size(); i++) {
            if (StringUtils.equals(styles.get(i), styleCode)) {
                return i;
            }
        }
        return -1;
    }

    private int compareStyle(String styleCode1, String styleCode2) {
        var style1LeftRequire = table.get(styleCode1);
        var style2LeftRequire = table.get(styleCode2);
        if (CollectionUtils.isEmpty(style1LeftRequire)) {
            return 1;
        }
        if (CollectionUtils.isEmpty(style2LeftRequire)) {
            return -1;
        }

        return style1LeftRequire.first().compareTo(style2LeftRequire.first());
    }
}
