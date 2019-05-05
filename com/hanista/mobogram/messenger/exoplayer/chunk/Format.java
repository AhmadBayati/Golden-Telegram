package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.util.Comparator;

public class Format {
    public final int audioChannels;
    public final int audioSamplingRate;
    public final int bitrate;
    public final String codecs;
    public final float frameRate;
    public final int height;
    public final String id;
    public final String language;
    public final String mimeType;
    public final int width;

    public static final class DecreasingBandwidthComparator implements Comparator<Format> {
        public int compare(Format format, Format format2) {
            return format2.bitrate - format.bitrate;
        }
    }

    public Format(String str, String str2, int i, int i2, float f, int i3, int i4, int i5) {
        this(str, str2, i, i2, f, i3, i4, i5, null);
    }

    public Format(String str, String str2, int i, int i2, float f, int i3, int i4, int i5, String str3) {
        this(str, str2, i, i2, f, i3, i4, i5, str3, null);
    }

    public Format(String str, String str2, int i, int i2, float f, int i3, int i4, int i5, String str3, String str4) {
        this.id = (String) Assertions.checkNotNull(str);
        this.mimeType = str2;
        this.width = i;
        this.height = i2;
        this.frameRate = f;
        this.audioChannels = i3;
        this.audioSamplingRate = i4;
        this.bitrate = i5;
        this.language = str3;
        this.codecs = str4;
    }

    public boolean equals(Object obj) {
        return this == obj ? true : (obj == null || getClass() != obj.getClass()) ? false : ((Format) obj).id.equals(this.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}
