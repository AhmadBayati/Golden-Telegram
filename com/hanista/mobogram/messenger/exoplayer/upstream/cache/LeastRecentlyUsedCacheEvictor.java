package com.hanista.mobogram.messenger.exoplayer.upstream.cache;

import java.util.Comparator;
import java.util.TreeSet;

public final class LeastRecentlyUsedCacheEvictor implements CacheEvictor, Comparator<CacheSpan> {
    private long currentSize;
    private final TreeSet<CacheSpan> leastRecentlyUsed;
    private final long maxBytes;

    public LeastRecentlyUsedCacheEvictor(long j) {
        this.maxBytes = j;
        this.leastRecentlyUsed = new TreeSet(this);
    }

    private void evictCache(Cache cache, long j) {
        while (this.currentSize + j > this.maxBytes) {
            cache.removeSpan((CacheSpan) this.leastRecentlyUsed.first());
        }
    }

    public int compare(CacheSpan cacheSpan, CacheSpan cacheSpan2) {
        return cacheSpan.lastAccessTimestamp - cacheSpan2.lastAccessTimestamp == 0 ? cacheSpan.compareTo(cacheSpan2) : cacheSpan.lastAccessTimestamp < cacheSpan2.lastAccessTimestamp ? -1 : 1;
    }

    public void onCacheInitialized() {
    }

    public void onSpanAdded(Cache cache, CacheSpan cacheSpan) {
        this.leastRecentlyUsed.add(cacheSpan);
        this.currentSize += cacheSpan.length;
        evictCache(cache, 0);
    }

    public void onSpanRemoved(Cache cache, CacheSpan cacheSpan) {
        this.leastRecentlyUsed.remove(cacheSpan);
        this.currentSize -= cacheSpan.length;
    }

    public void onSpanTouched(Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan2) {
        onSpanRemoved(cache, cacheSpan);
        onSpanAdded(cache, cacheSpan2);
    }

    public void onStartFile(Cache cache, String str, long j, long j2) {
        evictCache(cache, j2);
    }
}
