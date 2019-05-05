package com.hanista.mobogram.messenger.exoplayer.hls;

import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsMediaPlaylist.Segment;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriLoadable.Parser;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

public final class HlsPlaylistParser implements Parser<HlsPlaylist> {
    private static final String AUDIO_TYPE = "AUDIO";
    private static final String BANDWIDTH_ATTR = "BANDWIDTH";
    private static final Pattern BANDWIDTH_ATTR_REGEX;
    private static final Pattern BYTERANGE_REGEX;
    private static final String BYTERANGE_TAG = "#EXT-X-BYTERANGE";
    private static final String CLOSED_CAPTIONS_TYPE = "CLOSED-CAPTIONS";
    private static final String CODECS_ATTR = "CODECS";
    private static final Pattern CODECS_ATTR_REGEX;
    private static final String DISCONTINUITY_SEQUENCE_TAG = "#EXT-X-DISCONTINUITY-SEQUENCE";
    private static final String DISCONTINUITY_TAG = "#EXT-X-DISCONTINUITY";
    private static final String ENDLIST_TAG = "#EXT-X-ENDLIST";
    private static final String INSTREAM_ID_ATTR = "INSTREAM-ID";
    private static final Pattern INSTREAM_ID_ATTR_REGEX;
    private static final String IV_ATTR = "IV";
    private static final Pattern IV_ATTR_REGEX;
    private static final String KEY_TAG = "#EXT-X-KEY";
    private static final String LANGUAGE_ATTR = "LANGUAGE";
    private static final Pattern LANGUAGE_ATTR_REGEX;
    private static final Pattern MEDIA_DURATION_REGEX;
    private static final String MEDIA_DURATION_TAG = "#EXTINF";
    private static final Pattern MEDIA_SEQUENCE_REGEX;
    private static final String MEDIA_SEQUENCE_TAG = "#EXT-X-MEDIA-SEQUENCE";
    private static final String MEDIA_TAG = "#EXT-X-MEDIA";
    private static final String METHOD_AES128 = "AES-128";
    private static final String METHOD_ATTR = "METHOD";
    private static final Pattern METHOD_ATTR_REGEX;
    private static final String METHOD_NONE = "NONE";
    private static final String NAME_ATTR = "NAME";
    private static final Pattern NAME_ATTR_REGEX;
    private static final String RESOLUTION_ATTR = "RESOLUTION";
    private static final Pattern RESOLUTION_ATTR_REGEX;
    private static final String STREAM_INF_TAG = "#EXT-X-STREAM-INF";
    private static final String SUBTITLES_TYPE = "SUBTITLES";
    private static final Pattern TARGET_DURATION_REGEX;
    private static final String TARGET_DURATION_TAG = "#EXT-X-TARGETDURATION";
    private static final String TYPE_ATTR = "TYPE";
    private static final Pattern TYPE_ATTR_REGEX;
    private static final String URI_ATTR = "URI";
    private static final Pattern URI_ATTR_REGEX;
    private static final Pattern VERSION_REGEX;
    private static final String VERSION_TAG = "#EXT-X-VERSION";
    private static final String VIDEO_TYPE = "VIDEO";

    private static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> queue, BufferedReader bufferedReader) {
            this.extraLines = queue;
            this.reader = bufferedReader;
        }

        public boolean hasNext() {
            if (this.next != null) {
                return true;
            }
            if (this.extraLines.isEmpty()) {
                do {
                    String readLine = this.reader.readLine();
                    this.next = readLine;
                    if (readLine == null) {
                        return false;
                    }
                    this.next = this.next.trim();
                } while (this.next.isEmpty());
                return true;
            }
            this.next = (String) this.extraLines.poll();
            return true;
        }

        public String next() {
            if (!hasNext()) {
                return null;
            }
            String str = this.next;
            this.next = null;
            return str;
        }
    }

    static {
        BANDWIDTH_ATTR_REGEX = Pattern.compile("BANDWIDTH=(\\d+)\\b");
        CODECS_ATTR_REGEX = Pattern.compile("CODECS=\"(.+?)\"");
        RESOLUTION_ATTR_REGEX = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
        MEDIA_DURATION_REGEX = Pattern.compile("#EXTINF:([\\d.]+)\\b");
        MEDIA_SEQUENCE_REGEX = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
        TARGET_DURATION_REGEX = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
        VERSION_REGEX = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
        BYTERANGE_REGEX = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
        METHOD_ATTR_REGEX = Pattern.compile("METHOD=(NONE|AES-128)");
        URI_ATTR_REGEX = Pattern.compile("URI=\"(.+?)\"");
        IV_ATTR_REGEX = Pattern.compile("IV=([^,.*]+)");
        TYPE_ATTR_REGEX = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
        LANGUAGE_ATTR_REGEX = Pattern.compile("LANGUAGE=\"(.+?)\"");
        NAME_ATTR_REGEX = Pattern.compile("NAME=\"(.+?)\"");
        INSTREAM_ID_ATTR_REGEX = Pattern.compile("INSTREAM-ID=\"(.+?)\"");
    }

    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator lineIterator, String str) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        String str2 = null;
        Object obj = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        int i = -1;
        int i2 = -1;
        int i3 = 0;
        while (lineIterator.hasNext()) {
            String next = lineIterator.next();
            String parseStringAttr;
            if (next.startsWith(MEDIA_TAG)) {
                String str6;
                parseStringAttr = HlsParserUtil.parseStringAttr(next, TYPE_ATTR_REGEX, TYPE_ATTR);
                if (CLOSED_CAPTIONS_TYPE.equals(parseStringAttr)) {
                    if ("CC1".equals(HlsParserUtil.parseStringAttr(next, INSTREAM_ID_ATTR_REGEX, INSTREAM_ID_ATTR))) {
                        str3 = HlsParserUtil.parseOptionalStringAttr(next, LANGUAGE_ATTR_REGEX);
                    }
                    parseStringAttr = str3;
                    str6 = str4;
                } else if (SUBTITLES_TYPE.equals(parseStringAttr)) {
                    str6 = HlsParserUtil.parseStringAttr(next, NAME_ATTR_REGEX, NAME_ATTR);
                    arrayList3.add(new Variant(HlsParserUtil.parseStringAttr(next, URI_ATTR_REGEX, URI_ATTR), new Format(str6, MimeTypes.APPLICATION_M3U8, -1, -1, Face.UNCOMPUTED_PROBABILITY, -1, -1, -1, HlsParserUtil.parseOptionalStringAttr(next, LANGUAGE_ATTR_REGEX), str2)));
                    parseStringAttr = str3;
                    str6 = str4;
                } else if (AUDIO_TYPE.equals(parseStringAttr)) {
                    String parseOptionalStringAttr = HlsParserUtil.parseOptionalStringAttr(next, LANGUAGE_ATTR_REGEX);
                    String parseOptionalStringAttr2 = HlsParserUtil.parseOptionalStringAttr(next, URI_ATTR_REGEX);
                    if (parseOptionalStringAttr2 != null) {
                        arrayList2.add(new Variant(parseOptionalStringAttr2, new Format(HlsParserUtil.parseStringAttr(next, NAME_ATTR_REGEX, NAME_ATTR), MimeTypes.APPLICATION_M3U8, -1, -1, Face.UNCOMPUTED_PROBABILITY, -1, -1, -1, parseOptionalStringAttr, str2)));
                        parseStringAttr = str3;
                        str6 = str4;
                    } else {
                        parseStringAttr = str3;
                        str6 = parseOptionalStringAttr;
                    }
                } else {
                    parseStringAttr = str3;
                    str6 = str4;
                }
                str3 = parseStringAttr;
                str4 = str6;
            } else {
                if (next.startsWith(STREAM_INF_TAG)) {
                    int parseInt;
                    int parseInt2;
                    int parseIntAttr = HlsParserUtil.parseIntAttr(next, BANDWIDTH_ATTR_REGEX, BANDWIDTH_ATTR);
                    str2 = HlsParserUtil.parseOptionalStringAttr(next, CODECS_ATTR_REGEX);
                    String parseOptionalStringAttr3 = HlsParserUtil.parseOptionalStringAttr(next, NAME_ATTR_REGEX);
                    parseStringAttr = HlsParserUtil.parseOptionalStringAttr(next, RESOLUTION_ATTR_REGEX);
                    if (parseStringAttr != null) {
                        String[] split = parseStringAttr.split("x");
                        parseInt = Integer.parseInt(split[0]);
                        if (parseInt <= 0) {
                            parseInt = -1;
                        }
                        parseInt2 = Integer.parseInt(split[1]);
                        if (parseInt2 <= 0) {
                            parseInt2 = -1;
                        }
                    } else {
                        parseInt = -1;
                        parseInt2 = -1;
                    }
                    obj = 1;
                    str5 = parseOptionalStringAttr3;
                    i = parseInt2;
                    i2 = parseInt;
                    i3 = parseIntAttr;
                } else {
                    if (!(next.startsWith("#") || r12 == null)) {
                        arrayList.add(new Variant(next, new Format(str5 == null ? Integer.toString(arrayList.size()) : str5, MimeTypes.APPLICATION_M3U8, i2, i, Face.UNCOMPUTED_PROBABILITY, -1, -1, i3, null, str2)));
                        str2 = null;
                        obj = null;
                        str5 = null;
                        i = -1;
                        i2 = -1;
                        i3 = 0;
                    }
                }
            }
        }
        return new HlsMasterPlaylist(str, arrayList, arrayList2, arrayList3, str4, str3);
    }

    private static HlsMediaPlaylist parseMediaPlaylist(LineIterator lineIterator, String str) {
        List arrayList = new ArrayList();
        int i = 0;
        long j = 0;
        long j2 = -1;
        boolean z = false;
        String str2 = null;
        boolean z2 = true;
        int i2 = 1;
        int i3 = 0;
        int i4 = 0;
        String str3 = null;
        long j3 = 0;
        double d = 0.0d;
        int i5 = 0;
        while (lineIterator.hasNext()) {
            String next = lineIterator.next();
            if (next.startsWith(TARGET_DURATION_TAG)) {
                i3 = HlsParserUtil.parseIntAttr(next, TARGET_DURATION_REGEX, TARGET_DURATION_TAG);
            } else if (next.startsWith(MEDIA_SEQUENCE_TAG)) {
                int parseIntAttr = HlsParserUtil.parseIntAttr(next, MEDIA_SEQUENCE_REGEX, MEDIA_SEQUENCE_TAG);
                i5 = parseIntAttr;
                i4 = parseIntAttr;
            } else if (next.startsWith(VERSION_TAG)) {
                i2 = HlsParserUtil.parseIntAttr(next, VERSION_REGEX, VERSION_TAG);
            } else if (next.startsWith(MEDIA_DURATION_TAG)) {
                d = HlsParserUtil.parseDoubleAttr(next, MEDIA_DURATION_REGEX, MEDIA_DURATION_TAG);
            } else if (next.startsWith(KEY_TAG)) {
                String parseOptionalStringAttr;
                boolean equals = METHOD_AES128.equals(HlsParserUtil.parseStringAttr(next, METHOD_ATTR_REGEX, METHOD_ATTR));
                if (equals) {
                    String parseStringAttr = HlsParserUtil.parseStringAttr(next, URI_ATTR_REGEX, URI_ATTR);
                    parseOptionalStringAttr = HlsParserUtil.parseOptionalStringAttr(next, IV_ATTR_REGEX);
                    next = parseStringAttr;
                } else {
                    next = null;
                    parseOptionalStringAttr = null;
                }
                str3 = parseOptionalStringAttr;
                z = equals;
                str2 = next;
            } else if (next.startsWith(BYTERANGE_TAG)) {
                String[] split = HlsParserUtil.parseStringAttr(next, BYTERANGE_REGEX, BYTERANGE_TAG).split("@");
                j2 = Long.parseLong(split[0]);
                j3 = split.length > 1 ? Long.parseLong(split[1]) : j3;
            } else if (next.startsWith(DISCONTINUITY_SEQUENCE_TAG)) {
                i = Integer.parseInt(next.substring(next.indexOf(58) + 1));
            } else if (next.equals(DISCONTINUITY_TAG)) {
                i++;
            } else if (!next.startsWith("#")) {
                String toHexString = !z ? null : str3 != null ? str3 : Integer.toHexString(i5);
                int i6 = i5 + 1;
                long j4 = j2 == -1 ? 0 : j3;
                arrayList.add(new Segment(next, d, i, j, z, str2, toHexString, j4, j2));
                j += (long) (1000000.0d * d);
                d = 0.0d;
                long j5 = j2 != -1 ? j4 + j2 : j4;
                j2 = -1;
                i5 = i6;
                j3 = j5;
            } else if (next.equals(ENDLIST_TAG)) {
                z2 = false;
            }
        }
        return new HlsMediaPlaylist(str, i4, i3, i2, z2, Collections.unmodifiableList(arrayList));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.hanista.mobogram.messenger.exoplayer.hls.HlsPlaylist parse(java.lang.String r5, java.io.InputStream r6) {
        /*
        r4 = this;
        r1 = new java.io.BufferedReader;
        r0 = new java.io.InputStreamReader;
        r0.<init>(r6);
        r1.<init>(r0);
        r0 = new java.util.LinkedList;
        r0.<init>();
    L_0x000f:
        r2 = r1.readLine();	 Catch:{ all -> 0x0095 }
        if (r2 == 0) goto L_0x009a;
    L_0x0015:
        r2 = r2.trim();	 Catch:{ all -> 0x0095 }
        r3 = r2.isEmpty();	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x000f;
    L_0x001f:
        r3 = "#EXT-X-STREAM-INF";
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x0095 }
        if (r3 == 0) goto L_0x0038;
    L_0x0028:
        r0.add(r2);	 Catch:{ all -> 0x0095 }
        r2 = new com.hanista.mobogram.messenger.exoplayer.hls.HlsPlaylistParser$LineIterator;	 Catch:{ all -> 0x0095 }
        r2.<init>(r0, r1);	 Catch:{ all -> 0x0095 }
        r0 = parseMasterPlaylist(r2, r5);	 Catch:{ all -> 0x0095 }
        r1.close();
    L_0x0037:
        return r0;
    L_0x0038:
        r3 = "#EXT-X-TARGETDURATION";
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x0080;
    L_0x0041:
        r3 = "#EXT-X-MEDIA-SEQUENCE";
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x0080;
    L_0x004a:
        r3 = "#EXTINF";
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x0080;
    L_0x0053:
        r3 = "#EXT-X-KEY";
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x0080;
    L_0x005c:
        r3 = "#EXT-X-BYTERANGE";
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x0080;
    L_0x0065:
        r3 = "#EXT-X-DISCONTINUITY";
        r3 = r2.equals(r3);	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x0080;
    L_0x006e:
        r3 = "#EXT-X-DISCONTINUITY-SEQUENCE";
        r3 = r2.equals(r3);	 Catch:{ all -> 0x0095 }
        if (r3 != 0) goto L_0x0080;
    L_0x0077:
        r3 = "#EXT-X-ENDLIST";
        r3 = r2.equals(r3);	 Catch:{ all -> 0x0095 }
        if (r3 == 0) goto L_0x0090;
    L_0x0080:
        r0.add(r2);	 Catch:{ all -> 0x0095 }
        r2 = new com.hanista.mobogram.messenger.exoplayer.hls.HlsPlaylistParser$LineIterator;	 Catch:{ all -> 0x0095 }
        r2.<init>(r0, r1);	 Catch:{ all -> 0x0095 }
        r0 = parseMediaPlaylist(r2, r5);	 Catch:{ all -> 0x0095 }
        r1.close();
        goto L_0x0037;
    L_0x0090:
        r0.add(r2);	 Catch:{ all -> 0x0095 }
        goto L_0x000f;
    L_0x0095:
        r0 = move-exception;
        r1.close();
        throw r0;
    L_0x009a:
        r1.close();
        r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
        r1 = "Failed to parse the playlist, could not identify any tags.";
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.hls.HlsPlaylistParser.parse(java.lang.String, java.io.InputStream):com.hanista.mobogram.messenger.exoplayer.hls.HlsPlaylist");
    }
}
