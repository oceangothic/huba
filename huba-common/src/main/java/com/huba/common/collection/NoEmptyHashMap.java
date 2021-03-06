package com.huba.common.collection;

import com.huba.common.utils.Preconditions;

import java.util.HashMap;
import java.util.Map;


public class NoEmptyHashMap<K,V> extends HashMap<K, V> {

    public NoEmptyHashMap() {
        super();
    }

    public NoEmptyHashMap(final Map<K,V> map) {
        super();
        putAll(map);
    }

    @Override
    public V put(final K key, final V value) {
        if (Preconditions.isBlank(key) || Preconditions.isBlank(value)) {
            return null;
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
