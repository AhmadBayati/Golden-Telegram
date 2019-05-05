package com.hanista.mobogram.messenger.exoplayer.upstream.cache;

import android.os.ConditionVariable;
import com.hanista.mobogram.messenger.exoplayer.upstream.cache.Cache.Listener;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public final class SimpleCache implements Cache {
    private final File cacheDir;
    private final HashMap<String, TreeSet<CacheSpan>> cachedSpans;
    private final CacheEvictor evictor;
    private final HashMap<String, ArrayList<Listener>> listeners;
    private final HashMap<String, CacheSpan> lockedSpans;
    private long totalSpace;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.upstream.cache.SimpleCache.1 */
    class C07511 extends Thread {
        final /* synthetic */ ConditionVariable val$conditionVariable;

        C07511(String str, ConditionVariable conditionVariable) {
            this.val$conditionVariable = conditionVariable;
            super(str);
        }

        public void run() {
            synchronized (SimpleCache.this) {
                this.val$conditionVariable.open();
                SimpleCache.this.initialize();
            }
        }
    }

    public SimpleCache(File file, CacheEvictor cacheEvictor) {
        this.totalSpace = 0;
        this.cacheDir = file;
        this.evictor = cacheEvictor;
        this.lockedSpans = new HashMap();
        this.cachedSpans = new HashMap();
        this.listeners = new HashMap();
        ConditionVariable conditionVariable = new ConditionVariable();
        new C07511("SimpleCache.initialize()", conditionVariable).start();
        conditionVariable.block();
    }

    private void addSpan(CacheSpan cacheSpan) {
        TreeSet treeSet = (TreeSet) this.cachedSpans.get(cacheSpan.key);
        if (treeSet == null) {
            treeSet = new TreeSet();
            this.cachedSpans.put(cacheSpan.key, treeSet);
        }
        treeSet.add(cacheSpan);
        this.totalSpace += cacheSpan.length;
        notifySpanAdded(cacheSpan);
    }

    private CacheSpan getSpan(CacheSpan cacheSpan) {
        String str = cacheSpan.key;
        long j = cacheSpan.position;
        TreeSet treeSet = (TreeSet) this.cachedSpans.get(str);
        if (treeSet == null) {
            return CacheSpan.createOpenHole(str, cacheSpan.position);
        }
        CacheSpan cacheSpan2 = (CacheSpan) treeSet.floor(cacheSpan);
        if (cacheSpan2 == null || cacheSpan2.position > j || j >= cacheSpan2.position + cacheSpan2.length) {
            CacheSpan cacheSpan3 = (CacheSpan) treeSet.ceiling(cacheSpan);
            return cacheSpan3 == null ? CacheSpan.createOpenHole(str, cacheSpan.position) : CacheSpan.createClosedHole(str, cacheSpan.position, cacheSpan3.position - cacheSpan.position);
        } else if (cacheSpan2.file.exists()) {
            return cacheSpan2;
        } else {
            removeStaleSpans();
            return getSpan(cacheSpan);
        }
    }

    private void initialize() {
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
        }
        File[] listFiles = this.cacheDir.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                File file2;
                if (file2.length() == 0) {
                    file2.delete();
                } else {
                    file2 = CacheSpan.upgradeIfNeeded(file2);
                    CacheSpan createCacheEntry = CacheSpan.createCacheEntry(file2);
                    if (createCacheEntry == null) {
                        file2.delete();
                    } else {
                        addSpan(createCacheEntry);
                    }
                }
            }
            this.evictor.onCacheInitialized();
        }
    }

    private void notifySpanAdded(CacheSpan cacheSpan) {
        ArrayList arrayList = (ArrayList) this.listeners.get(cacheSpan.key);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((Listener) arrayList.get(size)).onSpanAdded(this, cacheSpan);
            }
        }
        this.evictor.onSpanAdded(this, cacheSpan);
    }

    private void notifySpanRemoved(CacheSpan cacheSpan) {
        ArrayList arrayList = (ArrayList) this.listeners.get(cacheSpan.key);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((Listener) arrayList.get(size)).onSpanRemoved(this, cacheSpan);
            }
        }
        this.evictor.onSpanRemoved(this, cacheSpan);
    }

    private void notifySpanTouched(CacheSpan cacheSpan, CacheSpan cacheSpan2) {
        ArrayList arrayList = (ArrayList) this.listeners.get(cacheSpan.key);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((Listener) arrayList.get(size)).onSpanTouched(this, cacheSpan, cacheSpan2);
            }
        }
        this.evictor.onSpanTouched(this, cacheSpan, cacheSpan2);
    }

    private void removeStaleSpans() {
        Iterator it = this.cachedSpans.entrySet().iterator();
        while (it.hasNext()) {
            Iterator it2 = ((TreeSet) ((Entry) it.next()).getValue()).iterator();
            Object obj = 1;
            while (it2.hasNext()) {
                Object obj2;
                CacheSpan cacheSpan = (CacheSpan) it2.next();
                if (cacheSpan.file.exists()) {
                    obj2 = null;
                } else {
                    it2.remove();
                    if (cacheSpan.isCached) {
                        this.totalSpace -= cacheSpan.length;
                    }
                    notifySpanRemoved(cacheSpan);
                    obj2 = obj;
                }
                obj = obj2;
            }
            if (obj != null) {
                it.remove();
            }
        }
    }

    private synchronized CacheSpan startReadWriteNonBlocking(CacheSpan cacheSpan) {
        CacheSpan cacheSpan2;
        CacheSpan span = getSpan(cacheSpan);
        if (span.isCached) {
            TreeSet treeSet = (TreeSet) this.cachedSpans.get(span.key);
            Assertions.checkState(treeSet.remove(span));
            CacheSpan touch = span.touch();
            treeSet.add(touch);
            notifySpanTouched(span, touch);
            cacheSpan2 = touch;
        } else if (this.lockedSpans.containsKey(cacheSpan.key)) {
            cacheSpan2 = null;
        } else {
            this.lockedSpans.put(cacheSpan.key, span);
            cacheSpan2 = span;
        }
        return cacheSpan2;
    }

    public synchronized NavigableSet<CacheSpan> addListener(String str, Listener listener) {
        ArrayList arrayList = (ArrayList) this.listeners.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.listeners.put(str, arrayList);
        }
        arrayList.add(listener);
        return getCachedSpans(str);
    }

    public synchronized void commitFile(File file) {
        CacheSpan createCacheEntry = CacheSpan.createCacheEntry(file);
        Assertions.checkState(createCacheEntry != null);
        Assertions.checkState(this.lockedSpans.containsKey(createCacheEntry.key));
        if (file.exists()) {
            if (file.length() == 0) {
                file.delete();
            } else {
                addSpan(createCacheEntry);
                notifyAll();
            }
        }
    }

    public synchronized long getCacheSpace() {
        return this.totalSpace;
    }

    public synchronized NavigableSet<CacheSpan> getCachedSpans(String str) {
        NavigableSet<CacheSpan> navigableSet;
        TreeSet treeSet = (TreeSet) this.cachedSpans.get(str);
        if (treeSet == null) {
            navigableSet = null;
        } else {
            Object treeSet2 = new TreeSet(treeSet);
        }
        return navigableSet;
    }

    public synchronized Set<String> getKeys() {
        return new HashSet(this.cachedSpans.keySet());
    }

    public synchronized boolean isCached(String str, long j, long j2) {
        boolean z;
        TreeSet treeSet = (TreeSet) this.cachedSpans.get(str);
        if (treeSet == null) {
            z = false;
        } else {
            CacheSpan cacheSpan = (CacheSpan) treeSet.floor(CacheSpan.createLookup(str, j));
            if (cacheSpan == null || cacheSpan.position + cacheSpan.length <= j) {
                z = false;
            } else {
                long j3 = j + j2;
                long j4 = cacheSpan.position + cacheSpan.length;
                if (j4 >= j3) {
                    z = true;
                } else {
                    for (CacheSpan cacheSpan2 : treeSet.tailSet(cacheSpan, false)) {
                        if (cacheSpan2.position > j4) {
                            z = false;
                            break;
                        }
                        long max = Math.max(j4, cacheSpan2.length + cacheSpan2.position);
                        if (max >= j3) {
                            z = true;
                            break;
                        }
                        j4 = max;
                    }
                    z = false;
                }
            }
        }
        return z;
    }

    public synchronized void releaseHoleSpan(CacheSpan cacheSpan) {
        Assertions.checkState(cacheSpan == this.lockedSpans.remove(cacheSpan.key));
        notifyAll();
    }

    public synchronized void removeListener(String str, Listener listener) {
        ArrayList arrayList = (ArrayList) this.listeners.get(str);
        if (arrayList != null) {
            arrayList.remove(listener);
            if (arrayList.isEmpty()) {
                this.listeners.remove(str);
            }
        }
    }

    public synchronized void removeSpan(CacheSpan cacheSpan) {
        TreeSet treeSet = (TreeSet) this.cachedSpans.get(cacheSpan.key);
        this.totalSpace -= cacheSpan.length;
        Assertions.checkState(treeSet.remove(cacheSpan));
        cacheSpan.file.delete();
        if (treeSet.isEmpty()) {
            this.cachedSpans.remove(cacheSpan.key);
        }
        notifySpanRemoved(cacheSpan);
    }

    public synchronized File startFile(String str, long j, long j2) {
        Assertions.checkState(this.lockedSpans.containsKey(str));
        if (!this.cacheDir.exists()) {
            removeStaleSpans();
            this.cacheDir.mkdirs();
        }
        this.evictor.onStartFile(this, str, j, j2);
        return CacheSpan.getCacheFileName(this.cacheDir, str, j, System.currentTimeMillis());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.hanista.mobogram.messenger.exoplayer.upstream.cache.CacheSpan startReadWrite(java.lang.String r3, long r4) {
        /*
        r2 = this;
        monitor-enter(r2);
        r0 = com.hanista.mobogram.messenger.exoplayer.upstream.cache.CacheSpan.createLookup(r3, r4);	 Catch:{ all -> 0x0011 }
    L_0x0005:
        r1 = r2.startReadWriteNonBlocking(r0);	 Catch:{ all -> 0x0011 }
        if (r1 == 0) goto L_0x000d;
    L_0x000b:
        monitor-exit(r2);
        return r1;
    L_0x000d:
        r2.wait();	 Catch:{ all -> 0x0011 }
        goto L_0x0005;
    L_0x0011:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.upstream.cache.SimpleCache.startReadWrite(java.lang.String, long):com.hanista.mobogram.messenger.exoplayer.upstream.cache.CacheSpan");
    }

    public synchronized CacheSpan startReadWriteNonBlocking(String str, long j) {
        return startReadWriteNonBlocking(CacheSpan.createLookup(str, j));
    }
}
