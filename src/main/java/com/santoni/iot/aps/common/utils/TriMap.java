package com.santoni.iot.aps.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TriMap<K1, K2, K3, V> implements Iterable<TriMap.TriEntry<K1, K2, K3, V>> {

    private final Map<K1, Map<K2, Map<K3, V>>> map = Maps.newHashMap();

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void put(K1 key1, K2 key2, K3 key3, V value) {
        map.computeIfAbsent(key1, k -> Maps.newHashMap())
                .computeIfAbsent(key2, k -> Maps.newHashMap())
                .put(key3, value);
    }

    public V get(K1 key1, K2 key2, K3 key3) {
        return map.getOrDefault(key1, Map.of())
                .getOrDefault(key2, Map.of())
                .get(key3);
    }

    public boolean containsKeys(K1 key1, K2 key2, K3 key3) {
        return map.containsKey(key1) &&
                map.get(key1).containsKey(key2) &&
                map.get(key1).get(key2).containsKey(key3);
    }

    public Map<K2, Map<K3, V>> getLevel2Map(K1 key1) {
        return map.getOrDefault(key1, Map.of());
    }

    @NotNull
    @Override
    public Iterator<TriEntry<K1, K2, K3, V>> iterator() {
        List<TriEntry<K1, K2, K3, V>> entries = Lists.newArrayList();

        for (var entry1 : map.entrySet()) {
            K1 key1 = entry1.getKey();
            for (var entry2 : entry1.getValue().entrySet()) {
                K2 key2 = entry2.getKey();
                for (var entry3 : entry2.getValue().entrySet()) {
                    K3 key3 = entry3.getKey();
                    V value = entry3.getValue();
                    entries.add(new TriEntry<>(key1, key2, key3, value));
                }
            }
        }

        return entries.iterator();
    }

    @Getter
    public static class TriEntry<K1, K2, K3, V> {
        private final K1 key1;
        private final K2 key2;
        private final K3 key3;
        private final V value;

        public TriEntry(K1 key1, K2 key2, K3 key3, V value) {
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
            this.value = value;
        }

    }
}
