package com.googlecode.mp4parser.util;

import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.tgnet.ConnectionsManager;

public class IntHashMap {
    private transient int count;
    private float loadFactor;
    private transient Entry[] table;
    private int threshold;

    private static class Entry {
        int hash;
        int key;
        Entry next;
        Object value;

        protected Entry(int i, int i2, Object obj, Entry entry) {
            this.hash = i;
            this.key = i2;
            this.value = obj;
            this.next = entry;
        }
    }

    public IntHashMap() {
        this(20, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION);
    }

    public IntHashMap(int i) {
        this(i, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION);
    }

    public IntHashMap(int i, float f) {
        if (i < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + i);
        } else if (f <= 0.0f) {
            throw new IllegalArgumentException("Illegal Load: " + f);
        } else {
            if (i == 0) {
                i = 1;
            }
            this.loadFactor = f;
            this.table = new Entry[i];
            this.threshold = (int) (((float) i) * f);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void clear() {
        /*
        r3 = this;
        monitor-enter(r3);
        r1 = r3.table;	 Catch:{ all -> 0x0011 }
        r0 = r1.length;	 Catch:{ all -> 0x0011 }
    L_0x0004:
        r0 = r0 + -1;
        if (r0 >= 0) goto L_0x000d;
    L_0x0008:
        r0 = 0;
        r3.count = r0;	 Catch:{ all -> 0x0011 }
        monitor-exit(r3);
        return;
    L_0x000d:
        r2 = 0;
        r1[r0] = r2;	 Catch:{ all -> 0x0011 }
        goto L_0x0004;
    L_0x0011:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.util.IntHashMap.clear():void");
    }

    public boolean contains(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        Entry[] entryArr = this.table;
        int length = entryArr.length;
        while (true) {
            int i = length - 1;
            if (length <= 0) {
                return false;
            }
            for (Entry entry = entryArr[i]; entry != null; entry = entry.next) {
                if (entry.value.equals(obj)) {
                    return true;
                }
            }
            length = i;
        }
    }

    public boolean containsKey(int i) {
        Entry[] entryArr = this.table;
        for (Entry entry = entryArr[(ConnectionsManager.DEFAULT_DATACENTER_ID & i) % entryArr.length]; entry != null; entry = entry.next) {
            if (entry.hash == i) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(Object obj) {
        return contains(obj);
    }

    public Object get(int i) {
        Entry[] entryArr = this.table;
        for (Entry entry = entryArr[(ConnectionsManager.DEFAULT_DATACENTER_ID & i) % entryArr.length]; entry != null; entry = entry.next) {
            if (entry.hash == i) {
                return entry.value;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public Object put(int i, Object obj) {
        Entry[] entryArr = this.table;
        int length = (i & ConnectionsManager.DEFAULT_DATACENTER_ID) % entryArr.length;
        for (Entry entry = entryArr[length]; entry != null; entry = entry.next) {
            if (entry.hash == i) {
                Object obj2 = entry.value;
                entry.value = obj;
                return obj2;
            }
        }
        if (this.count >= this.threshold) {
            rehash();
            entryArr = this.table;
            length = (i & ConnectionsManager.DEFAULT_DATACENTER_ID) % entryArr.length;
        }
        entryArr[length] = new Entry(i, i, obj, entryArr[length]);
        this.count++;
        return null;
    }

    protected void rehash() {
        int length = this.table.length;
        Entry[] entryArr = this.table;
        int i = (length * 2) + 1;
        Entry[] entryArr2 = new Entry[i];
        this.threshold = (int) (((float) i) * this.loadFactor);
        this.table = entryArr2;
        while (true) {
            int i2 = length - 1;
            if (length > 0) {
                Entry entry = entryArr[i2];
                while (entry != null) {
                    Entry entry2 = entry.next;
                    int i3 = (entry.hash & ConnectionsManager.DEFAULT_DATACENTER_ID) % i;
                    entry.next = entryArr2[i3];
                    entryArr2[i3] = entry;
                    entry = entry2;
                }
                length = i2;
            } else {
                return;
            }
        }
    }

    public Object remove(int i) {
        Entry[] entryArr = this.table;
        int length = (ConnectionsManager.DEFAULT_DATACENTER_ID & i) % entryArr.length;
        Entry entry = entryArr[length];
        Entry entry2 = null;
        while (entry != null) {
            if (entry.hash == i) {
                if (entry2 != null) {
                    entry2.next = entry.next;
                } else {
                    entryArr[length] = entry.next;
                }
                this.count--;
                Object obj = entry.value;
                entry.value = null;
                return obj;
            }
            Entry entry3 = entry;
            entry = entry.next;
            entry2 = entry3;
        }
        return null;
    }

    public int size() {
        return this.count;
    }
}
