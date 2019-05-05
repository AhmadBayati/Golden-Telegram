package com.googlecode.mp4parser.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class RangeStartMap<K extends Comparable, V> implements Map<K, V> {
    TreeMap<K, V> base;

    /* renamed from: com.googlecode.mp4parser.util.RangeStartMap.1 */
    class C03371 implements Comparator<K> {
        C03371() {
        }

        public int compare(K k, K k2) {
            return -k.compareTo(k2);
        }
    }

    public RangeStartMap() {
        this.base = new TreeMap(new C03371());
    }

    public RangeStartMap(K k, V v) {
        this.base = new TreeMap(new C03371());
        put((Comparable) k, (Object) v);
    }

    public void clear() {
        this.base.clear();
    }

    public boolean containsKey(Object obj) {
        return this.base.get(obj) != null;
    }

    public boolean containsValue(Object obj) {
        return false;
    }

    public Set<Entry<K, V>> entrySet() {
        return this.base.entrySet();
    }

    public V get(Object obj) {
        if (!(obj instanceof Comparable)) {
            return null;
        }
        Comparable comparable = (Comparable) obj;
        if (isEmpty()) {
            return null;
        }
        Iterator it = this.base.keySet().iterator();
        Object obj2 = (Comparable) it.next();
        while (it.hasNext()) {
            if (comparable.compareTo(obj2) >= 0) {
                return this.base.get(obj2);
            }
            Comparable comparable2 = (Comparable) it.next();
        }
        return this.base.get(obj2);
    }

    public boolean isEmpty() {
        return this.base.isEmpty();
    }

    public Set<K> keySet() {
        return this.base.keySet();
    }

    public V put(K k, V v) {
        return this.base.put(k, v);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        this.base.putAll(map);
    }

    public V remove(Object obj) {
        if (!(obj instanceof Comparable)) {
            return null;
        }
        Comparable comparable = (Comparable) obj;
        if (isEmpty()) {
            return null;
        }
        Iterator it = this.base.keySet().iterator();
        Object obj2 = (Comparable) it.next();
        while (it.hasNext()) {
            if (comparable.compareTo(obj2) >= 0) {
                return this.base.remove(obj2);
            }
            Comparable comparable2 = (Comparable) it.next();
        }
        return this.base.remove(obj2);
    }

    public int size() {
        return this.base.size();
    }

    public Collection<V> values() {
        return this.base.values();
    }
}
