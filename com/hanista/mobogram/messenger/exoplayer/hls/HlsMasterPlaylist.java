package com.hanista.mobogram.messenger.exoplayer.hls;

import java.util.Collections;
import java.util.List;

public final class HlsMasterPlaylist extends HlsPlaylist {
    public final List<Variant> audios;
    public final String muxedAudioLanguage;
    public final String muxedCaptionLanguage;
    public final List<Variant> subtitles;
    public final List<Variant> variants;

    public HlsMasterPlaylist(String str, List<Variant> list, List<Variant> list2, List<Variant> list3, String str2, String str3) {
        super(str, 0);
        this.variants = Collections.unmodifiableList(list);
        this.audios = Collections.unmodifiableList(list2);
        this.subtitles = Collections.unmodifiableList(list3);
        this.muxedAudioLanguage = str2;
        this.muxedCaptionLanguage = str3;
    }
}
