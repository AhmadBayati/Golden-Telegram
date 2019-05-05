package com.hanista.mobogram.messenger.volley;

import android.os.Process;
import com.hanista.mobogram.messenger.volley.Cache.Entry;
import java.util.concurrent.BlockingQueue;

public class CacheDispatcher extends Thread {
    private static final boolean DEBUG;
    private final Cache mCache;
    private final BlockingQueue<Request<?>> mCacheQueue;
    private final ResponseDelivery mDelivery;
    private final BlockingQueue<Request<?>> mNetworkQueue;
    private volatile boolean mQuit;

    /* renamed from: com.hanista.mobogram.messenger.volley.CacheDispatcher.1 */
    class C08881 implements Runnable {
        final /* synthetic */ Request val$request;

        C08881(Request request) {
            this.val$request = request;
        }

        public void run() {
            try {
                CacheDispatcher.this.mNetworkQueue.put(this.val$request);
            } catch (InterruptedException e) {
            }
        }
    }

    static {
        DEBUG = VolleyLog.DEBUG;
    }

    public CacheDispatcher(BlockingQueue<Request<?>> blockingQueue, BlockingQueue<Request<?>> blockingQueue2, Cache cache, ResponseDelivery responseDelivery) {
        this.mQuit = false;
        this.mCacheQueue = blockingQueue;
        this.mNetworkQueue = blockingQueue2;
        this.mCache = cache;
        this.mDelivery = responseDelivery;
    }

    public void quit() {
        this.mQuit = true;
        interrupt();
    }

    public void run() {
        if (DEBUG) {
            VolleyLog.m202v("start new dispatcher", new Object[0]);
        }
        Process.setThreadPriority(10);
        this.mCache.initialize();
        while (true) {
            try {
                Request request = (Request) this.mCacheQueue.take();
                request.addMarker("cache-queue-take");
                if (request.isCanceled()) {
                    request.finish("cache-discard-canceled");
                } else {
                    Entry entry = this.mCache.get(request.getCacheKey());
                    if (entry == null) {
                        request.addMarker("cache-miss");
                        this.mNetworkQueue.put(request);
                    } else if (entry.isExpired()) {
                        request.addMarker("cache-hit-expired");
                        request.setCacheEntry(entry);
                        this.mNetworkQueue.put(request);
                    } else {
                        request.addMarker("cache-hit");
                        Response parseNetworkResponse = request.parseNetworkResponse(new NetworkResponse(entry.data, entry.responseHeaders));
                        request.addMarker("cache-hit-parsed");
                        if (entry.refreshNeeded()) {
                            request.addMarker("cache-hit-refresh-needed");
                            request.setCacheEntry(entry);
                            parseNetworkResponse.intermediate = true;
                            this.mDelivery.postResponse(request, parseNetworkResponse, new C08881(request));
                        } else {
                            this.mDelivery.postResponse(request, parseNetworkResponse);
                        }
                    }
                }
            } catch (InterruptedException e) {
                if (this.mQuit) {
                    return;
                }
            }
        }
    }
}
