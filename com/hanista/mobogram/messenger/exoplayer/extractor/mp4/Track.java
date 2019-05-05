package com.hanista.mobogram.messenger.exoplayer.extractor.mp4;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.Util;

public final class Track {
    public static final int TYPE_sbtl;
    public static final int TYPE_soun;
    public static final int TYPE_subt;
    public static final int TYPE_text;
    public static final int TYPE_vide;
    public final long durationUs;
    public final long[] editListDurations;
    public final long[] editListMediaTimes;
    public final int id;
    public final MediaFormat mediaFormat;
    public final long movieTimescale;
    public final int nalUnitLengthFieldLength;
    public final TrackEncryptionBox[] sampleDescriptionEncryptionBoxes;
    public final long timescale;
    public final int type;

    static {
        TYPE_vide = Util.getIntegerCodeForString("vide");
        TYPE_soun = Util.getIntegerCodeForString("soun");
        TYPE_text = Util.getIntegerCodeForString(MimeTypes.BASE_TYPE_TEXT);
        TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
        TYPE_subt = Util.getIntegerCodeForString("subt");
    }

    public Track(int i, int i2, long j, long j2, long j3, MediaFormat mediaFormat, TrackEncryptionBox[] trackEncryptionBoxArr, int i3, long[] jArr, long[] jArr2) {
        this.id = i;
        this.type = i2;
        this.timescale = j;
        this.movieTimescale = j2;
        this.durationUs = j3;
        this.mediaFormat = mediaFormat;
        this.sampleDescriptionEncryptionBoxes = trackEncryptionBoxArr;
        this.nalUnitLengthFieldLength = i3;
        this.editListDurations = jArr;
        this.editListMediaTimes = jArr2;
    }
}
