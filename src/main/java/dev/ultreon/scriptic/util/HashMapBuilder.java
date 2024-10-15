package dev.ultreon.scriptic.util;

import java.util.HashMap;

public class HashMapBuilder<K, V> {
    private final HashMap<K, V> map = new HashMap<>();

    public HashMapBuilder() {

    }

    public HashMapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public HashMap<K, V> build() {
        return map;
    }
}
