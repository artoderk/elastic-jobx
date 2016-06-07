package com.dangdang.ddframe.util.objectpool;

import java.util.Collection;
import java.util.Set;

/**
 * <code>ObjectPool</code>
 * <p>对象池接口</p>
 * @author panglei
 */
public interface ObjectPool<K, V> {

    /**
     * 获取对象池中的对象
     *
     * @param key 对象池中索引
     * @return value
     */
    public V get(K key);

    /**
     * 将对象放入对象池
     *
     * @param key key
     * @param value value
     */
    public void put(K key, V value);

    /**
     * 判断对象是否粗在
     *
     * @param key key
     * @return boolean
     */
    public boolean containsKey(K key);

    /**
     * 清除对象池
     */
    public void clear();

    /**
     * 返回对象池中的key列表
     *
     * @return key列表
     */
    public Set<K> keySet();

    /**
     * 删除指定key对应的对象
     *
     * @param k key
     */
    public void remove(K k);

    /**
     * 返回对象池中所有对象
     *
     * @return 所有对象
     */
    public Collection<V> values();

    /**
     * 返回对象池列表
     *
     * @return 对象池列表
     */
    public int size();

    /**
     * 判断对象池是否为空
     *
     * @return boolean
     */
    public boolean isEmpty();

}
