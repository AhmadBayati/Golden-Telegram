package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Loadable;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;

public abstract class Chunk implements Loadable {
    public static final int NO_PARENT_ID = -1;
    public static final int TRIGGER_ADAPTIVE = 3;
    public static final int TRIGGER_CUSTOM_BASE = 10000;
    public static final int TRIGGER_INITIAL = 1;
    public static final int TRIGGER_MANUAL = 2;
    public static final int TRIGGER_TRICK_PLAY = 4;
    public static final int TRIGGER_UNSPECIFIED = 0;
    public static final int TYPE_CUSTOM_BASE = 10000;
    public static final int TYPE_DRM = 3;
    public static final int TYPE_MANIFEST = 4;
    public static final int TYPE_MEDIA = 1;
    public static final int TYPE_MEDIA_INITIALIZATION = 2;
    public static final int TYPE_UNSPECIFIED = 0;
    protected final DataSource dataSource;
    public final DataSpec dataSpec;
    public final Format format;
    public final int parentId;
    public final int trigger;
    public final int type;

    public Chunk(DataSource dataSource, DataSpec dataSpec, int i, int i2, Format format, int i3) {
        this.dataSource = (DataSource) Assertions.checkNotNull(dataSource);
        this.dataSpec = (DataSpec) Assertions.checkNotNull(dataSpec);
        this.type = i;
        this.trigger = i2;
        this.format = format;
        this.parentId = i3;
    }

    public abstract long bytesLoaded();
}
