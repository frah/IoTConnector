package iotc;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.BoundedMap;
import org.apache.commons.collections15.bidimap.AbstractDualBidiMap;
import org.apache.commons.collections15.map.LRUMap;

/**
 * 最大サイズの固定されたDualHashBidiMap
 * @author atsushi-o
 */
public class FixedDualHashBidiMap <K, V> extends AbstractDualBidiMap<K, V> implements BoundedMap<K, V>, Serializable {
    public FixedDualHashBidiMap(int maxSize) {
        super(new LRUMap<K, V>(maxSize), new LRUMap<V, K>(maxSize));
    }
    public FixedDualHashBidiMap(int maxSize, Map<? extends K, ? extends V> map) {
        this(maxSize);
        putAll(map);
    }
    protected FixedDualHashBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
    }

    @Override
    protected <K, V> BidiMap<K, V> createBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        return new FixedDualHashBidiMap(normalMap, reverseMap, inverseBidiMap);
    }

    @Override
    public boolean isFull() {
        return ((LRUMap)forwardMap).isFull();
    }

    @Override
    public int maxSize() {
        return ((LRUMap)forwardMap).maxSize();
    }
}
