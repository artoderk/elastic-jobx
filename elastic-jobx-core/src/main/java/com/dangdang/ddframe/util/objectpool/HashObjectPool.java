package com.dangdang.ddframe.util.objectpool;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象池
 *
 * @author lei.panglei
 */
public class HashObjectPool<K, V> implements ObjectPool<K, V> {

    /** 对象池  */
    protected Map<K, V> objectPool = new ConcurrentHashMap<K, V>();

    /**
     * 获取一个对象池中的对象
     *
     * @param key key
     * @return value
     */
    public V get(K key) {
        return objectPool.get(key);
    }

    /**
     * 保存一个对象到对象池
     *
     * @param key key
     * @param value value
     */
    public void put(K key, V value) {
        if (value != null) {
            objectPool.put(key, value);
        }
    }

    /**
     * 判断对象池否存在
     *
     * @param key key
     * @return boolean
     */
    public boolean containsKey(K key) {
        return objectPool.containsKey(key);
    }

    /**
     * 清空对象池
     */
    public void clear() {
        objectPool.clear();
    }

    /**
     * 获取Key集合
     *
     * @return Key集合
     */
    public Set<K> keySet() {
        return objectPool.keySet();
    }

    /**
     * 通过Key删除一个对象
     *
     * @param k key
     */
    public void remove(K k) {
        objectPool.remove(k);
    }

    /**
     * 获取对象池中全部对象
     *
     * @return 全部对象
     */
    public Collection<V> values() {
        return objectPool.values();
    }

    /**
     * 获取对象池大小
     *
     * @return 对象池大小
     */
    public int size() {
        return objectPool.size();
    }

    /**
     * 对象池是否为空
     *
     * @return boolean
     */
    @Override
    public boolean isEmpty() {
        return objectPool.isEmpty();
    }

}
