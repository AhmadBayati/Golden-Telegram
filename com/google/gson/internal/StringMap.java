package com.google.gson.internal;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

public final class StringMap<V> extends AbstractMap<String, V> {
    private static final Entry[] EMPTY_TABLE;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final int MINIMUM_CAPACITY = 4;
    private static final int seed;
    private Set<Entry<String, V>> entrySet;
    private LinkedEntry<V> header;
    private Set<String> keySet;
    private int size;
    private LinkedEntry<V>[] table;
    private int threshold;
    private Collection<V> values;

    private abstract class LinkedHashIterator<T> implements Iterator<T> {
        LinkedEntry<V> lastReturned;
        LinkedEntry<V> next;

        private LinkedHashIterator() {
            this.next = StringMap.this.header.nxt;
            this.lastReturned = null;
        }

        public final boolean hasNext() {
            return this.next != StringMap.this.header;
        }

        final LinkedEntry<V> nextEntry() {
            LinkedEntry<V> linkedEntry = this.next;
            if (linkedEntry == StringMap.this.header) {
                throw new NoSuchElementException();
            }
            this.next = linkedEntry.nxt;
            this.lastReturned = linkedEntry;
            return linkedEntry;
        }

        public final void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            StringMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }

    private final class EntrySet extends AbstractSet<Entry<String, V>> {

        /* renamed from: com.google.gson.internal.StringMap.EntrySet.1 */
        class C02891 extends LinkedHashIterator<Entry<String, V>> {
            C02891() {
                super(null);
            }

            public final Entry<String, V> next() {
                return nextEntry();
            }
        }

        private EntrySet() {
        }

        public void clear() {
            StringMap.this.clear();
        }

        public boolean contains(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            Object obj2 = StringMap.this.get(entry.getKey());
            return obj2 != null && obj2.equals(entry.getValue());
        }

        public Iterator<Entry<String, V>> iterator() {
            return new C02891();
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return StringMap.this.removeMapping(entry.getKey(), entry.getValue());
        }

        public int size() {
            return StringMap.this.size;
        }
    }

    private final class KeySet extends AbstractSet<String> {

        /* renamed from: com.google.gson.internal.StringMap.KeySet.1 */
        class C02901 extends LinkedHashIterator<String> {
            C02901() {
                super(null);
            }

            public final String next() {
                return nextEntry().key;
            }
        }

        private KeySet() {
        }

        public void clear() {
            StringMap.this.clear();
        }

        public boolean contains(Object obj) {
            return StringMap.this.containsKey(obj);
        }

        public Iterator<String> iterator() {
            return new C02901();
        }

        public boolean remove(Object obj) {
            int access$500 = StringMap.this.size;
            StringMap.this.remove(obj);
            return StringMap.this.size != access$500;
        }

        public int size() {
            return StringMap.this.size;
        }
    }

    static class LinkedEntry<V> implements Entry<String, V> {
        final int hash;
        final String key;
        LinkedEntry<V> next;
        LinkedEntry<V> nxt;
        LinkedEntry<V> prv;
        V value;

        LinkedEntry() {
            this(null, null, 0, null, null, null);
            this.prv = this;
            this.nxt = this;
        }

        LinkedEntry(String str, V v, int i, LinkedEntry<V> linkedEntry, LinkedEntry<V> linkedEntry2, LinkedEntry<V> linkedEntry3) {
            this.key = str;
            this.value = v;
            this.hash = i;
            this.next = linkedEntry;
            this.nxt = linkedEntry2;
            this.prv = linkedEntry3;
        }

        public final boolean equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            Object value = entry.getValue();
            if (!this.key.equals(entry.getKey())) {
                return false;
            }
            if (this.value == null) {
                if (value != null) {
                    return false;
                }
            } else if (!this.value.equals(value)) {
                return false;
            }
            return true;
        }

        public final String getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.value;
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = this.key == null ? 0 : this.key.hashCode();
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode ^ i;
        }

        public final V setValue(V v) {
            V v2 = this.value;
            this.value = v;
            return v2;
        }

        public final String toString() {
            return this.key + "=" + this.value;
        }
    }

    private final class Values extends AbstractCollection<V> {

        /* renamed from: com.google.gson.internal.StringMap.Values.1 */
        class C02911 extends LinkedHashIterator<V> {
            C02911() {
                super(null);
            }

            public final V next() {
                return nextEntry().value;
            }
        }

        private Values() {
        }

        public void clear() {
            StringMap.this.clear();
        }

        public boolean contains(Object obj) {
            return StringMap.this.containsValue(obj);
        }

        public Iterator<V> iterator() {
            return new C02911();
        }

        public int size() {
            return StringMap.this.size;
        }
    }

    static {
        EMPTY_TABLE = new LinkedEntry[2];
        seed = new Random().nextInt();
    }

    public StringMap() {
        this.table = (LinkedEntry[]) EMPTY_TABLE;
        this.threshold = -1;
        this.header = new LinkedEntry();
    }

    private void addNewEntry(String str, V v, int i, int i2) {
        LinkedEntry linkedEntry = this.header;
        LinkedEntry linkedEntry2 = linkedEntry.prv;
        LinkedEntry linkedEntry3 = new LinkedEntry(str, v, i, this.table[i2], linkedEntry, linkedEntry2);
        LinkedEntry[] linkedEntryArr = this.table;
        linkedEntry.prv = linkedEntry3;
        linkedEntry2.nxt = linkedEntry3;
        linkedEntryArr[i2] = linkedEntry3;
    }

    private LinkedEntry<V>[] doubleCapacity() {
        LinkedEntry<V>[] linkedEntryArr = this.table;
        int length = linkedEntryArr.length;
        if (length == MAXIMUM_CAPACITY) {
            return linkedEntryArr;
        }
        LinkedEntry<V>[] makeTable = makeTable(length * 2);
        if (this.size == 0) {
            return makeTable;
        }
        for (int i = 0; i < length; i++) {
            LinkedEntry linkedEntry = linkedEntryArr[i];
            if (linkedEntry != null) {
                int i2 = linkedEntry.hash & length;
                makeTable[i | i2] = linkedEntry;
                LinkedEntry linkedEntry2 = null;
                LinkedEntry linkedEntry3 = linkedEntry;
                for (linkedEntry = linkedEntry.next; linkedEntry != null; linkedEntry = linkedEntry.next) {
                    int i3;
                    int i4 = linkedEntry.hash & length;
                    if (i4 != i2) {
                        if (linkedEntry2 == null) {
                            makeTable[i | i4] = linkedEntry;
                        } else {
                            linkedEntry2.next = linkedEntry;
                        }
                        i3 = i4;
                    } else {
                        linkedEntry3 = linkedEntry2;
                        i3 = i2;
                    }
                    i2 = i3;
                    linkedEntry2 = linkedEntry3;
                    linkedEntry3 = linkedEntry;
                }
                if (linkedEntry2 != null) {
                    linkedEntry2.next = null;
                }
            }
        }
        return makeTable;
    }

    private LinkedEntry<V> getEntry(String str) {
        if (str == null) {
            return null;
        }
        int hash = hash(str);
        LinkedEntry[] linkedEntryArr = this.table;
        LinkedEntry<V> linkedEntry = linkedEntryArr[(linkedEntryArr.length - 1) & hash];
        while (linkedEntry != null) {
            String str2 = linkedEntry.key;
            if (str2 == str || (linkedEntry.hash == hash && str.equals(str2))) {
                return linkedEntry;
            }
            linkedEntry = linkedEntry.next;
        }
        return null;
    }

    private static int hash(String str) {
        int i;
        int i2 = seed;
        for (i = 0; i < str.length(); i++) {
            i2 += str.charAt(i);
            i2 = (i2 + i2) << 10;
            i2 ^= i2 >>> 6;
        }
        i = ((i2 >>> 20) ^ (i2 >>> 12)) ^ i2;
        return (i >>> MINIMUM_CAPACITY) ^ ((i >>> 7) ^ i);
    }

    private LinkedEntry<V>[] makeTable(int i) {
        LinkedEntry[] linkedEntryArr = new LinkedEntry[i];
        this.table = linkedEntryArr;
        this.threshold = (i >> 1) + (i >> 2);
        return linkedEntryArr;
    }

    private boolean removeMapping(Object obj, Object obj2) {
        if (obj == null || !(obj instanceof String)) {
            return false;
        }
        int hash = hash((String) obj);
        LinkedEntry[] linkedEntryArr = this.table;
        int length = hash & (linkedEntryArr.length - 1);
        LinkedEntry linkedEntry = linkedEntryArr[length];
        LinkedEntry linkedEntry2 = null;
        while (linkedEntry != null) {
            if (linkedEntry.hash != hash || !obj.equals(linkedEntry.key)) {
                LinkedEntry linkedEntry3 = linkedEntry;
                linkedEntry = linkedEntry.next;
                linkedEntry2 = linkedEntry3;
            } else if (obj2 != null ? !obj2.equals(linkedEntry.value) : linkedEntry.value != null) {
                return false;
            } else {
                if (linkedEntry2 == null) {
                    linkedEntryArr[length] = linkedEntry.next;
                } else {
                    linkedEntry2.next = linkedEntry.next;
                }
                this.size--;
                unlink(linkedEntry);
                return true;
            }
        }
        return false;
    }

    private void unlink(LinkedEntry<V> linkedEntry) {
        linkedEntry.prv.nxt = linkedEntry.nxt;
        linkedEntry.nxt.prv = linkedEntry.prv;
        linkedEntry.prv = null;
        linkedEntry.nxt = null;
    }

    public void clear() {
        if (this.size != 0) {
            Arrays.fill(this.table, null);
            this.size = 0;
        }
        LinkedEntry linkedEntry = this.header;
        LinkedEntry linkedEntry2 = linkedEntry.nxt;
        while (linkedEntry2 != linkedEntry) {
            LinkedEntry linkedEntry3 = linkedEntry2.nxt;
            linkedEntry2.prv = null;
            linkedEntry2.nxt = null;
            linkedEntry2 = linkedEntry3;
        }
        linkedEntry.prv = linkedEntry;
        linkedEntry.nxt = linkedEntry;
    }

    public boolean containsKey(Object obj) {
        return (obj instanceof String) && getEntry((String) obj) != null;
    }

    public Set<Entry<String, V>> entrySet() {
        Set<Entry<String, V>> set = this.entrySet;
        if (set != null) {
            return set;
        }
        set = new EntrySet();
        this.entrySet = set;
        return set;
    }

    public V get(Object obj) {
        if (!(obj instanceof String)) {
            return null;
        }
        LinkedEntry entry = getEntry((String) obj);
        return entry != null ? entry.value : null;
    }

    public Set<String> keySet() {
        Set<String> set = this.keySet;
        if (set != null) {
            return set;
        }
        set = new KeySet();
        this.keySet = set;
        return set;
    }

    public V put(String str, V v) {
        if (str == null) {
            throw new NullPointerException("key == null");
        }
        int hash = hash(str);
        LinkedEntry[] linkedEntryArr = this.table;
        int length = (linkedEntryArr.length - 1) & hash;
        LinkedEntry linkedEntry = linkedEntryArr[length];
        while (linkedEntry != null) {
            if (linkedEntry.hash == hash && str.equals(linkedEntry.key)) {
                V v2 = linkedEntry.value;
                linkedEntry.value = v;
                return v2;
            }
            linkedEntry = linkedEntry.next;
        }
        int i = this.size;
        this.size = i + 1;
        if (i > this.threshold) {
            length = (doubleCapacity().length - 1) & hash;
        }
        addNewEntry(str, v, hash, length);
        return null;
    }

    public V remove(Object obj) {
        if (obj == null || !(obj instanceof String)) {
            return null;
        }
        int hash = hash((String) obj);
        LinkedEntry[] linkedEntryArr = this.table;
        int length = hash & (linkedEntryArr.length - 1);
        LinkedEntry linkedEntry = linkedEntryArr[length];
        LinkedEntry linkedEntry2 = null;
        while (linkedEntry != null) {
            if (linkedEntry.hash == hash && obj.equals(linkedEntry.key)) {
                if (linkedEntry2 == null) {
                    linkedEntryArr[length] = linkedEntry.next;
                } else {
                    linkedEntry2.next = linkedEntry.next;
                }
                this.size--;
                unlink(linkedEntry);
                return linkedEntry.value;
            }
            LinkedEntry linkedEntry3 = linkedEntry;
            linkedEntry = linkedEntry.next;
            linkedEntry2 = linkedEntry3;
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    public Collection<V> values() {
        Collection<V> collection = this.values;
        if (collection != null) {
            return collection;
        }
        collection = new Values();
        this.values = collection;
        return collection;
    }
}
