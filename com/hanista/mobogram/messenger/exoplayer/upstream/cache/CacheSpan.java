package com.hanista.mobogram.messenger.exoplayer.upstream.cache;

import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CacheSpan implements Comparable<CacheSpan> {
    private static final Pattern CACHE_FILE_PATTERN_V1;
    private static final Pattern CACHE_FILE_PATTERN_V2;
    private static final String SUFFIX = ".v2.exo";
    public final File file;
    public final boolean isCached;
    public final String key;
    public final long lastAccessTimestamp;
    public final long length;
    public final long position;

    static {
        CACHE_FILE_PATTERN_V1 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v1\\.exo$");
        CACHE_FILE_PATTERN_V2 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v2\\.exo$");
    }

    CacheSpan(String str, long j, long j2, boolean z, long j3, File file) {
        this.key = str;
        this.position = j;
        this.length = j2;
        this.isCached = z;
        this.file = file;
        this.lastAccessTimestamp = j3;
    }

    public static CacheSpan createCacheEntry(File file) {
        Matcher matcher = CACHE_FILE_PATTERN_V2.matcher(file.getName());
        if (!matcher.matches()) {
            return null;
        }
        String unescapeFileName = Util.unescapeFileName(matcher.group(1));
        return unescapeFileName != null ? createCacheEntry(unescapeFileName, Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)), file) : null;
    }

    private static CacheSpan createCacheEntry(String str, long j, long j2, File file) {
        return new CacheSpan(str, j, file.length(), true, j2, file);
    }

    public static CacheSpan createClosedHole(String str, long j, long j2) {
        return new CacheSpan(str, j, j2, false, -1, null);
    }

    public static CacheSpan createLookup(String str, long j) {
        return new CacheSpan(str, j, -1, false, -1, null);
    }

    public static CacheSpan createOpenHole(String str, long j) {
        return new CacheSpan(str, j, -1, false, -1, null);
    }

    public static File getCacheFileName(File file, String str, long j, long j2) {
        return new File(file, Util.escapeFileName(str) + "." + j + "." + j2 + SUFFIX);
    }

    static File upgradeIfNeeded(File file) {
        Matcher matcher = CACHE_FILE_PATTERN_V1.matcher(file.getName());
        if (!matcher.matches()) {
            return file;
        }
        File cacheFileName = getCacheFileName(file.getParentFile(), matcher.group(1), Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)));
        FileLoader.renameTo(file, cacheFileName);
        return cacheFileName;
    }

    public int compareTo(CacheSpan cacheSpan) {
        if (!this.key.equals(cacheSpan.key)) {
            return this.key.compareTo(cacheSpan.key);
        }
        long j = this.position - cacheSpan.position;
        return j == 0 ? 0 : j < 0 ? -1 : 1;
    }

    public boolean isOpenEnded() {
        return this.length == -1;
    }

    public CacheSpan touch() {
        long currentTimeMillis = System.currentTimeMillis();
        File cacheFileName = getCacheFileName(this.file.getParentFile(), this.key, this.position, currentTimeMillis);
        FileLoader.renameTo(this.file, cacheFileName);
        return createCacheEntry(this.key, this.position, currentTimeMillis, cacheFileName);
    }
}
