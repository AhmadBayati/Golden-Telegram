package com.hanista.mobogram.messenger.volley.toolbox;

import android.os.SystemClock;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.Cache;
import com.hanista.mobogram.messenger.volley.Cache.Entry;
import com.hanista.mobogram.messenger.volley.VolleyLog;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskBasedCache implements Cache {
    private static final int CACHE_MAGIC = 538247942;
    private static final int DEFAULT_DISK_USAGE_BYTES = 5242880;
    private static final float HYSTERESIS_FACTOR = 0.9f;
    private final Map<String, CacheHeader> mEntries;
    private final int mMaxCacheSizeInBytes;
    private final File mRootDirectory;
    private long mTotalSize;

    static class CacheHeader {
        public String etag;
        public String key;
        public long lastModified;
        public Map<String, String> responseHeaders;
        public long serverDate;
        public long size;
        public long softTtl;
        public long ttl;

        private CacheHeader() {
        }

        public CacheHeader(String str, Entry entry) {
            this.key = str;
            this.size = (long) entry.data.length;
            this.etag = entry.etag;
            this.serverDate = entry.serverDate;
            this.lastModified = entry.lastModified;
            this.ttl = entry.ttl;
            this.softTtl = entry.softTtl;
            this.responseHeaders = entry.responseHeaders;
        }

        public static CacheHeader readHeader(InputStream inputStream) {
            CacheHeader cacheHeader = new CacheHeader();
            if (DiskBasedCache.readInt(inputStream) != DiskBasedCache.CACHE_MAGIC) {
                throw new IOException();
            }
            cacheHeader.key = DiskBasedCache.readString(inputStream);
            cacheHeader.etag = DiskBasedCache.readString(inputStream);
            if (cacheHeader.etag.equals(TtmlNode.ANONYMOUS_REGION_ID)) {
                cacheHeader.etag = null;
            }
            cacheHeader.serverDate = DiskBasedCache.readLong(inputStream);
            cacheHeader.lastModified = DiskBasedCache.readLong(inputStream);
            cacheHeader.ttl = DiskBasedCache.readLong(inputStream);
            cacheHeader.softTtl = DiskBasedCache.readLong(inputStream);
            cacheHeader.responseHeaders = DiskBasedCache.readStringStringMap(inputStream);
            return cacheHeader;
        }

        public Entry toCacheEntry(byte[] bArr) {
            Entry entry = new Entry();
            entry.data = bArr;
            entry.etag = this.etag;
            entry.serverDate = this.serverDate;
            entry.lastModified = this.lastModified;
            entry.ttl = this.ttl;
            entry.softTtl = this.softTtl;
            entry.responseHeaders = this.responseHeaders;
            return entry;
        }

        public boolean writeHeader(OutputStream outputStream) {
            try {
                DiskBasedCache.writeInt(outputStream, DiskBasedCache.CACHE_MAGIC);
                DiskBasedCache.writeString(outputStream, this.key);
                DiskBasedCache.writeString(outputStream, this.etag == null ? TtmlNode.ANONYMOUS_REGION_ID : this.etag);
                DiskBasedCache.writeLong(outputStream, this.serverDate);
                DiskBasedCache.writeLong(outputStream, this.lastModified);
                DiskBasedCache.writeLong(outputStream, this.ttl);
                DiskBasedCache.writeLong(outputStream, this.softTtl);
                DiskBasedCache.writeStringStringMap(this.responseHeaders, outputStream);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                VolleyLog.m199d("%s", e.toString());
                return false;
            }
        }
    }

    private static class CountingInputStream extends FilterInputStream {
        private int bytesRead;

        private CountingInputStream(InputStream inputStream) {
            super(inputStream);
            this.bytesRead = 0;
        }

        public int read() {
            int read = super.read();
            if (read != -1) {
                this.bytesRead++;
            }
            return read;
        }

        public int read(byte[] bArr, int i, int i2) {
            int read = super.read(bArr, i, i2);
            if (read != -1) {
                this.bytesRead += read;
            }
            return read;
        }
    }

    public DiskBasedCache(File file) {
        this(file, DEFAULT_DISK_USAGE_BYTES);
    }

    public DiskBasedCache(File file, int i) {
        this.mEntries = new LinkedHashMap(16, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION, true);
        this.mTotalSize = 0;
        this.mRootDirectory = file;
        this.mMaxCacheSizeInBytes = i;
    }

    private String getFilenameForKey(String str) {
        int length = str.length() / 2;
        return String.valueOf(str.substring(0, length).hashCode()) + String.valueOf(str.substring(length).hashCode());
    }

    private void pruneIfNeeded(int i) {
        if (this.mTotalSize + ((long) i) >= ((long) this.mMaxCacheSizeInBytes)) {
            int i2;
            if (VolleyLog.DEBUG) {
                VolleyLog.m202v("Pruning old cache entries.", new Object[0]);
            }
            long j = this.mTotalSize;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            Iterator it = this.mEntries.entrySet().iterator();
            int i3 = 0;
            while (it.hasNext()) {
                CacheHeader cacheHeader = (CacheHeader) ((Map.Entry) it.next()).getValue();
                if (getFileForKey(cacheHeader.key).delete()) {
                    this.mTotalSize -= cacheHeader.size;
                } else {
                    VolleyLog.m199d("Could not delete cache entry for key=%s, filename=%s", cacheHeader.key, getFilenameForKey(cacheHeader.key));
                }
                it.remove();
                i2 = i3 + 1;
                if (((float) (this.mTotalSize + ((long) i))) < ((float) this.mMaxCacheSizeInBytes) * HYSTERESIS_FACTOR) {
                    break;
                }
                i3 = i2;
            }
            i2 = i3;
            if (VolleyLog.DEBUG) {
                VolleyLog.m202v("pruned %d files, %d bytes, %d ms", Integer.valueOf(i2), Long.valueOf(this.mTotalSize - j), Long.valueOf(SystemClock.elapsedRealtime() - elapsedRealtime));
            }
        }
    }

    private void putEntry(String str, CacheHeader cacheHeader) {
        if (this.mEntries.containsKey(str)) {
            CacheHeader cacheHeader2 = (CacheHeader) this.mEntries.get(str);
            this.mTotalSize = (cacheHeader.size - cacheHeader2.size) + this.mTotalSize;
        } else {
            this.mTotalSize += cacheHeader.size;
        }
        this.mEntries.put(str, cacheHeader);
    }

    private static int read(InputStream inputStream) {
        int read = inputStream.read();
        if (read != -1) {
            return read;
        }
        throw new EOFException();
    }

    static int readInt(InputStream inputStream) {
        return (((0 | (read(inputStream) << 0)) | (read(inputStream) << 8)) | (read(inputStream) << 16)) | (read(inputStream) << 24);
    }

    static long readLong(InputStream inputStream) {
        return (((((((0 | ((((long) read(inputStream)) & 255) << null)) | ((((long) read(inputStream)) & 255) << 8)) | ((((long) read(inputStream)) & 255) << 16)) | ((((long) read(inputStream)) & 255) << 24)) | ((((long) read(inputStream)) & 255) << 32)) | ((((long) read(inputStream)) & 255) << 40)) | ((((long) read(inputStream)) & 255) << 48)) | ((((long) read(inputStream)) & 255) << 56);
    }

    static String readString(InputStream inputStream) {
        return new String(streamToBytes(inputStream, (int) readLong(inputStream)), C0700C.UTF8_NAME);
    }

    static Map<String, String> readStringStringMap(InputStream inputStream) {
        int readInt = readInt(inputStream);
        Map<String, String> emptyMap = readInt == 0 ? Collections.emptyMap() : new HashMap(readInt);
        for (int i = 0; i < readInt; i++) {
            emptyMap.put(readString(inputStream).intern(), readString(inputStream).intern());
        }
        return emptyMap;
    }

    private void removeEntry(String str) {
        CacheHeader cacheHeader = (CacheHeader) this.mEntries.get(str);
        if (cacheHeader != null) {
            this.mTotalSize -= cacheHeader.size;
            this.mEntries.remove(str);
        }
    }

    private static byte[] streamToBytes(InputStream inputStream, int i) {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 == i) {
            return bArr;
        }
        throw new IOException("Expected " + i + " bytes, read " + i2 + " bytes");
    }

    static void writeInt(OutputStream outputStream, int i) {
        outputStream.write((i >> 0) & NalUnitUtil.EXTENDED_SAR);
        outputStream.write((i >> 8) & NalUnitUtil.EXTENDED_SAR);
        outputStream.write((i >> 16) & NalUnitUtil.EXTENDED_SAR);
        outputStream.write((i >> 24) & NalUnitUtil.EXTENDED_SAR);
    }

    static void writeLong(OutputStream outputStream, long j) {
        outputStream.write((byte) ((int) (j >>> null)));
        outputStream.write((byte) ((int) (j >>> 8)));
        outputStream.write((byte) ((int) (j >>> 16)));
        outputStream.write((byte) ((int) (j >>> 24)));
        outputStream.write((byte) ((int) (j >>> 32)));
        outputStream.write((byte) ((int) (j >>> 40)));
        outputStream.write((byte) ((int) (j >>> 48)));
        outputStream.write((byte) ((int) (j >>> 56)));
    }

    static void writeString(OutputStream outputStream, String str) {
        byte[] bytes = str.getBytes(C0700C.UTF8_NAME);
        writeLong(outputStream, (long) bytes.length);
        outputStream.write(bytes, 0, bytes.length);
    }

    static void writeStringStringMap(Map<String, String> map, OutputStream outputStream) {
        if (map != null) {
            writeInt(outputStream, map.size());
            for (Map.Entry entry : map.entrySet()) {
                writeString(outputStream, (String) entry.getKey());
                writeString(outputStream, (String) entry.getValue());
            }
            return;
        }
        writeInt(outputStream, 0);
    }

    public synchronized void clear() {
        synchronized (this) {
            File[] listFiles = this.mRootDirectory.listFiles();
            if (listFiles != null) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
            this.mEntries.clear();
            this.mTotalSize = 0;
            VolleyLog.m199d("Cache cleared.", new Object[0]);
        }
    }

    public synchronized Entry get(String str) {
        Entry entry;
        CountingInputStream countingInputStream;
        IOException e;
        Throwable th;
        CacheHeader cacheHeader = (CacheHeader) this.mEntries.get(str);
        if (cacheHeader == null) {
            entry = null;
        } else {
            File fileForKey = getFileForKey(str);
            try {
                countingInputStream = new CountingInputStream(null);
                try {
                    CacheHeader.readHeader(countingInputStream);
                    entry = cacheHeader.toCacheEntry(streamToBytes(countingInputStream, (int) (fileForKey.length() - ((long) countingInputStream.bytesRead))));
                    if (countingInputStream != null) {
                        try {
                            countingInputStream.close();
                        } catch (IOException e2) {
                            entry = null;
                        }
                    }
                } catch (IOException e3) {
                    e = e3;
                    try {
                        VolleyLog.m199d("%s: %s", fileForKey.getAbsolutePath(), e.toString());
                        remove(str);
                        if (countingInputStream != null) {
                            try {
                                countingInputStream.close();
                            } catch (IOException e4) {
                                entry = null;
                            }
                        }
                        entry = null;
                        return entry;
                    } catch (Throwable th2) {
                        th = th2;
                        if (countingInputStream != null) {
                            try {
                                countingInputStream.close();
                            } catch (IOException e5) {
                                entry = null;
                            }
                        }
                        throw th;
                    }
                }
            } catch (IOException e6) {
                e = e6;
                countingInputStream = null;
                VolleyLog.m199d("%s: %s", fileForKey.getAbsolutePath(), e.toString());
                remove(str);
                if (countingInputStream != null) {
                    countingInputStream.close();
                }
                entry = null;
                return entry;
            } catch (Throwable th3) {
                th = th3;
                countingInputStream = null;
                if (countingInputStream != null) {
                    countingInputStream.close();
                }
                throw th;
            }
        }
        return entry;
    }

    public File getFileForKey(String str) {
        return new File(this.mRootDirectory, getFilenameForKey(str));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void initialize() {
        /*
        r9 = this;
        r0 = 0;
        monitor-enter(r9);
        r1 = r9.mRootDirectory;	 Catch:{ all -> 0x006d }
        r1 = r1.exists();	 Catch:{ all -> 0x006d }
        if (r1 != 0) goto L_0x0026;
    L_0x000a:
        r0 = r9.mRootDirectory;	 Catch:{ all -> 0x006d }
        r0 = r0.mkdirs();	 Catch:{ all -> 0x006d }
        if (r0 != 0) goto L_0x0024;
    L_0x0012:
        r0 = "Unable to create cache dir %s";
        r1 = 1;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x006d }
        r2 = 0;
        r3 = r9.mRootDirectory;	 Catch:{ all -> 0x006d }
        r3 = r3.getAbsolutePath();	 Catch:{ all -> 0x006d }
        r1[r2] = r3;	 Catch:{ all -> 0x006d }
        com.hanista.mobogram.messenger.volley.VolleyLog.m200e(r0, r1);	 Catch:{ all -> 0x006d }
    L_0x0024:
        monitor-exit(r9);
        return;
    L_0x0026:
        r1 = r9.mRootDirectory;	 Catch:{ all -> 0x006d }
        r3 = r1.listFiles();	 Catch:{ all -> 0x006d }
        if (r3 == 0) goto L_0x0024;
    L_0x002e:
        r4 = r3.length;	 Catch:{ all -> 0x006d }
        r2 = r0;
    L_0x0030:
        if (r2 >= r4) goto L_0x0024;
    L_0x0032:
        r5 = r3[r2];	 Catch:{ all -> 0x006d }
        r1 = 0;
        r0 = new java.io.BufferedInputStream;	 Catch:{ IOException -> 0x0057, all -> 0x0066 }
        r6 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0057, all -> 0x0066 }
        r6.<init>(r5);	 Catch:{ IOException -> 0x0057, all -> 0x0066 }
        r0.<init>(r6);	 Catch:{ IOException -> 0x0057, all -> 0x0066 }
        r1 = com.hanista.mobogram.messenger.volley.toolbox.DiskBasedCache.CacheHeader.readHeader(r0);	 Catch:{ IOException -> 0x0079 }
        r6 = r5.length();	 Catch:{ IOException -> 0x0079 }
        r1.size = r6;	 Catch:{ IOException -> 0x0079 }
        r6 = r1.key;	 Catch:{ IOException -> 0x0079 }
        r9.putEntry(r6, r1);	 Catch:{ IOException -> 0x0079 }
        if (r0 == 0) goto L_0x0053;
    L_0x0050:
        r0.close();	 Catch:{ IOException -> 0x0070 }
    L_0x0053:
        r0 = r2 + 1;
        r2 = r0;
        goto L_0x0030;
    L_0x0057:
        r0 = move-exception;
        r0 = r1;
    L_0x0059:
        if (r5 == 0) goto L_0x005e;
    L_0x005b:
        r5.delete();	 Catch:{ all -> 0x0074 }
    L_0x005e:
        if (r0 == 0) goto L_0x0053;
    L_0x0060:
        r0.close();	 Catch:{ IOException -> 0x0064 }
        goto L_0x0053;
    L_0x0064:
        r0 = move-exception;
        goto L_0x0053;
    L_0x0066:
        r0 = move-exception;
    L_0x0067:
        if (r1 == 0) goto L_0x006c;
    L_0x0069:
        r1.close();	 Catch:{ IOException -> 0x0072 }
    L_0x006c:
        throw r0;	 Catch:{ all -> 0x006d }
    L_0x006d:
        r0 = move-exception;
        monitor-exit(r9);
        throw r0;
    L_0x0070:
        r0 = move-exception;
        goto L_0x0053;
    L_0x0072:
        r1 = move-exception;
        goto L_0x006c;
    L_0x0074:
        r1 = move-exception;
        r8 = r1;
        r1 = r0;
        r0 = r8;
        goto L_0x0067;
    L_0x0079:
        r1 = move-exception;
        goto L_0x0059;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.volley.toolbox.DiskBasedCache.initialize():void");
    }

    public synchronized void invalidate(String str, boolean z) {
        Entry entry = get(str);
        if (entry != null) {
            entry.softTtl = 0;
            if (z) {
                entry.ttl = 0;
            }
            put(str, entry);
        }
    }

    public synchronized void put(String str, Entry entry) {
        pruneIfNeeded(entry.data.length);
        File fileForKey = getFileForKey(str);
        try {
            OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileForKey));
            CacheHeader cacheHeader = new CacheHeader(str, entry);
            if (cacheHeader.writeHeader(bufferedOutputStream)) {
                bufferedOutputStream.write(entry.data);
                bufferedOutputStream.close();
                putEntry(str, cacheHeader);
            } else {
                bufferedOutputStream.close();
                VolleyLog.m199d("Failed to write header for %s", fileForKey.getAbsolutePath());
                throw new IOException();
            }
        } catch (IOException e) {
            if (!fileForKey.delete()) {
                VolleyLog.m199d("Could not clean up file %s", fileForKey.getAbsolutePath());
            }
        }
    }

    public synchronized void remove(String str) {
        boolean delete = getFileForKey(str).delete();
        removeEntry(str);
        if (!delete) {
            VolleyLog.m199d("Could not delete cache entry for key=%s, filename=%s", str, getFilenameForKey(str));
        }
    }
}
