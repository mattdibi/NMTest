package org.eclipse.kura.NMTest;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ValueMapper<K, V> {
    private final Map<K, V> mapper;
    
    public static class Builder<K, V> {
        private final Map<K, V> builderMapper;
        
        public Builder() {
            this.builderMapper = new HashMap<>();
        }
        
        public Builder<K,V> with(K key, V value) {
            this.builderMapper.put(key, value);
            return this;
        }
        
        public ValueMapper<K, V> build() {
            return new ValueMapper<>(this.builderMapper);
        }
    }

    public static class EnumBuilder<K extends Enum<K>, V> {
        private final EnumMap<K, V> builderMapper;
        
        public EnumBuilder(Class<K> enumType) {
            this.builderMapper = new EnumMap<K, V>(enumType);
        }
        
        public EnumBuilder<K,V> with(K key, V value) {
            this.builderMapper.put(key, value);
            return this;
        }
        
        public ValueMapper<K, V> build() {
            return new ValueMapper<>(this.builderMapper);
        }
    }

    ValueMapper(Map<K, V> mapContent) {
        this.mapper = Objects.requireNonNull(mapContent);
    }
    
    public V get(K key) {
        if(!mapper.containsKey(key)) {
            throw new NoSuchElementException(String.format("Key \"%s\" not found", key.toString()));
        }

        V value = this.mapper.get(key);

        if(Objects.isNull(value)) {
            throw new NoSuchElementException(String.format("Value for \"%s\" key is null", key.toString()));
        }
        
        return value;
    }
}