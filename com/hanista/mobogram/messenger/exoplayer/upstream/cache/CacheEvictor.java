package com.hanista.mobogram.messenger.exoplayer.upstream.cache;

import com.hanista.mobogram.messenger.exoplayer.upstream.cache.Cache.Listener;

public interface CacheEvictor extends Listener {
    void onCacheInitialized();

    void onStartFile(Cache cache, String str, long j, long j2);
}
