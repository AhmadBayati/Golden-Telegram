package com.hanista.mobogram.messenger.exoplayer.text.ttml;

import android.util.Log;
import android.util.Pair;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.SubtitleParser;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParserUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlParser implements SubtitleParser {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CLOCK_TIME;
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE;
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final Pattern FONT_SIZE;
    private static final Pattern OFFSET_TIME;
    private static final Pattern PERCENTAGE_COORDINATES;
    private static final String TAG = "TtmlParser";
    private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
    private final XmlPullParserFactory xmlParserFactory;

    private static final class FrameAndTickRate {
        final float effectiveFrameRate;
        final int subFrameRate;
        final int tickRate;

        FrameAndTickRate(float f, int i, int i2) {
            this.effectiveFrameRate = f;
            this.subFrameRate = i;
            this.tickRate = i2;
        }
    }

    static {
        CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
        OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
        FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
        PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
        DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(BitmapDescriptorFactory.HUE_ORANGE, 1, 1);
    }

    public TtmlParser() {
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    private TtmlStyle createIfNull(TtmlStyle ttmlStyle) {
        return ttmlStyle == null ? new TtmlStyle() : ttmlStyle;
    }

    private static boolean isSupportedTag(String str) {
        return str.equals(TtmlNode.TAG_TT) || str.equals(TtmlNode.TAG_HEAD) || str.equals(TtmlNode.TAG_BODY) || str.equals(TtmlNode.TAG_DIV) || str.equals(TtmlNode.TAG_P) || str.equals(TtmlNode.TAG_SPAN) || str.equals(TtmlNode.TAG_BR) || str.equals(ATTR_STYLE) || str.equals(TtmlNode.TAG_STYLING) || str.equals(TtmlNode.TAG_LAYOUT) || str.equals(ATTR_REGION) || str.equals(TtmlNode.TAG_METADATA) || str.equals(TtmlNode.TAG_SMPTE_IMAGE) || str.equals(TtmlNode.TAG_SMPTE_DATA) || str.equals(TtmlNode.TAG_SMPTE_INFORMATION);
    }

    private static void parseFontSize(String str, TtmlStyle ttmlStyle) {
        Matcher matcher;
        String[] split = str.split("\\s+");
        if (split.length == 1) {
            matcher = FONT_SIZE.matcher(str);
        } else if (split.length == 2) {
            matcher = FONT_SIZE.matcher(split[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            throw new ParserException("Invalid number of entries for fontSize: " + split.length + ".");
        }
        if (matcher.matches()) {
            String group = matcher.group(3);
            int i = -1;
            switch (group.hashCode()) {
                case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
                    if (group.equals("%")) {
                        i = 2;
                        break;
                    }
                    break;
                case 3240:
                    if (group.equals("em")) {
                        i = 1;
                        break;
                    }
                    break;
                case 3592:
                    if (group.equals("px")) {
                        i = 0;
                        break;
                    }
                    break;
            }
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    ttmlStyle.setFontSizeUnit(1);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    ttmlStyle.setFontSizeUnit(2);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    ttmlStyle.setFontSizeUnit(3);
                    break;
                default:
                    throw new ParserException("Invalid unit for fontSize: '" + group + "'.");
            }
            ttmlStyle.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        throw new ParserException("Invalid expression for fontSize: '" + str + "'.");
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlPullParser) {
        int i = DEFAULT_FRAME_RATE;
        String attributeValue = xmlPullParser.getAttributeValue(TTP, "frameRate");
        if (attributeValue != null) {
            i = Integer.parseInt(attributeValue);
        }
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        String attributeValue2 = xmlPullParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (attributeValue2 != null) {
            String[] split = attributeValue2.split(" ");
            if (split.length != 2) {
                throw new ParserException("frameRateMultiplier doesn't have 2 parts");
            }
            f = ((float) Integer.parseInt(split[0])) / ((float) Integer.parseInt(split[1]));
        }
        int i2 = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String attributeValue3 = xmlPullParser.getAttributeValue(TTP, "subFrameRate");
        if (attributeValue3 != null) {
            i2 = Integer.parseInt(attributeValue3);
        }
        int i3 = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        String attributeValue4 = xmlPullParser.getAttributeValue(TTP, "tickRate");
        if (attributeValue4 != null) {
            i3 = Integer.parseInt(attributeValue4);
        }
        return new FrameAndTickRate(((float) i) * f, i2, i3);
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlPullParser, Map<String, TtmlStyle> map, Map<String, TtmlRegion> map2) {
        do {
            xmlPullParser.next();
            if (ParserUtil.isStartTag(xmlPullParser, ATTR_STYLE)) {
                String attributeValue = ParserUtil.getAttributeValue(xmlPullParser, ATTR_STYLE);
                TtmlStyle parseStyleAttributes = parseStyleAttributes(xmlPullParser, new TtmlStyle());
                if (attributeValue != null) {
                    String[] parseStyleIds = parseStyleIds(attributeValue);
                    for (Object obj : parseStyleIds) {
                        parseStyleAttributes.chain((TtmlStyle) map.get(obj));
                    }
                }
                if (parseStyleAttributes.getId() != null) {
                    map.put(parseStyleAttributes.getId(), parseStyleAttributes);
                }
            } else if (ParserUtil.isStartTag(xmlPullParser, ATTR_REGION)) {
                Pair parseRegionAttributes = parseRegionAttributes(xmlPullParser);
                if (parseRegionAttributes != null) {
                    map2.put(parseRegionAttributes.first, parseRegionAttributes.second);
                }
            }
        } while (!ParserUtil.isEndTag(xmlPullParser, TtmlNode.TAG_HEAD));
        return map;
    }

    private TtmlNode parseNode(XmlPullParser xmlPullParser, TtmlNode ttmlNode, Map<String, TtmlRegion> map, FrameAndTickRate frameAndTickRate) {
        long parseTimeExpression;
        long j = 0;
        long j2 = -1;
        long j3 = -1;
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        String[] strArr = null;
        int attributeCount = xmlPullParser.getAttributeCount();
        TtmlStyle parseStyleAttributes = parseStyleAttributes(xmlPullParser, null);
        int i = 0;
        while (i < attributeCount) {
            long j4;
            String attributeName = xmlPullParser.getAttributeName(i);
            String attributeValue = xmlPullParser.getAttributeValue(i);
            if (ATTR_BEGIN.equals(attributeName)) {
                j2 = j;
                parseTimeExpression = parseTimeExpression(attributeValue, frameAndTickRate);
                j4 = j3;
                j3 = parseTimeExpression;
            } else if (ATTR_END.equals(attributeName)) {
                j4 = parseTimeExpression(attributeValue, frameAndTickRate);
                j3 = j2;
                j2 = j;
            } else if (ATTR_DURATION.equals(attributeName)) {
                parseTimeExpression = j3;
                j3 = j2;
                j2 = parseTimeExpression(attributeValue, frameAndTickRate);
                j4 = parseTimeExpression;
            } else if (ATTR_STYLE.equals(attributeName)) {
                String[] parseStyleIds = parseStyleIds(attributeValue);
                if (parseStyleIds.length > 0) {
                    strArr = parseStyleIds;
                }
                j4 = j3;
                j3 = j2;
                j2 = j;
            } else if (ATTR_REGION.equals(attributeName) && map.containsKey(attributeValue)) {
                str = attributeValue;
                j4 = j3;
                j3 = j2;
                j2 = j;
            } else {
                j4 = j3;
                j3 = j2;
                j2 = j;
            }
            i++;
            j = j2;
            j2 = j3;
            j3 = j4;
        }
        if (!(ttmlNode == null || ttmlNode.startTimeUs == -1)) {
            if (j2 != -1) {
                j2 += ttmlNode.startTimeUs;
            }
            if (j3 != -1) {
                parseTimeExpression = j3 + ttmlNode.startTimeUs;
                j3 = j2;
                j2 = parseTimeExpression;
                if (j2 == -1) {
                    if (j > 0) {
                        if (!(ttmlNode == null || ttmlNode.endTimeUs == -1)) {
                        }
                    }
                }
                return TtmlNode.buildNode(xmlPullParser.getName(), j3, j2, parseStyleAttributes, strArr, str);
            }
        }
        parseTimeExpression = j3;
        j3 = j2;
        j2 = parseTimeExpression;
        if (j2 == -1) {
            j2 = j > 0 ? ttmlNode.endTimeUs : j3 + j;
        }
        return TtmlNode.buildNode(xmlPullParser.getName(), j3, j2, parseStyleAttributes, strArr, str);
    }

    private Pair<String, TtmlRegion> parseRegionAttributes(XmlPullParser xmlPullParser) {
        String attributeValue = ParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_ID);
        Object attributeValue2 = ParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_ORIGIN);
        Object attributeValue3 = ParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_EXTENT);
        if (attributeValue2 == null || attributeValue == null) {
            return null;
        }
        float parseFloat;
        float parseFloat2;
        float parseFloat3;
        Matcher matcher = PERCENTAGE_COORDINATES.matcher(attributeValue2);
        if (matcher.matches()) {
            try {
                parseFloat = Float.parseFloat(matcher.group(1)) / 100.0f;
                parseFloat2 = Float.parseFloat(matcher.group(2)) / 100.0f;
            } catch (Throwable e) {
                Log.w(TAG, "Ignoring region with malformed origin: '" + attributeValue2 + "'", e);
                parseFloat2 = Cue.DIMEN_UNSET;
                parseFloat = Cue.DIMEN_UNSET;
            }
        } else {
            parseFloat2 = Cue.DIMEN_UNSET;
            parseFloat = Cue.DIMEN_UNSET;
        }
        if (attributeValue3 != null) {
            matcher = PERCENTAGE_COORDINATES.matcher(attributeValue3);
            if (matcher.matches()) {
                try {
                    parseFloat3 = Float.parseFloat(matcher.group(1)) / 100.0f;
                } catch (Throwable e2) {
                    Log.w(TAG, "Ignoring malformed region extent: '" + attributeValue3 + "'", e2);
                }
                return parseFloat == Cue.DIMEN_UNSET ? new Pair(attributeValue, new TtmlRegion(parseFloat, parseFloat2, 0, parseFloat3)) : null;
            }
        }
        parseFloat3 = Cue.DIMEN_UNSET;
        if (parseFloat == Cue.DIMEN_UNSET) {
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r13, com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlStyle r14) {
        /*
        r12 = this;
        r6 = 3;
        r5 = 2;
        r3 = -1;
        r4 = 1;
        r2 = 0;
        r8 = r13.getAttributeCount();
        r7 = r2;
        r0 = r14;
    L_0x000b:
        if (r7 >= r8) goto L_0x0242;
    L_0x000d:
        r9 = r13.getAttributeValue(r7);
        r1 = r13.getAttributeName(r7);
        r10 = r1.hashCode();
        switch(r10) {
            case -1550943582: goto L_0x0066;
            case -1224696685: goto L_0x0045;
            case -1065511464: goto L_0x0071;
            case -879295043: goto L_0x007c;
            case -734428249: goto L_0x005b;
            case 3355: goto L_0x0024;
            case 94842723: goto L_0x003a;
            case 365601008: goto L_0x0050;
            case 1287124693: goto L_0x002f;
            default: goto L_0x001c;
        };
    L_0x001c:
        r1 = r3;
    L_0x001d:
        switch(r1) {
            case 0: goto L_0x0088;
            case 1: goto L_0x009e;
            case 2: goto L_0x00cf;
            case 3: goto L_0x0100;
            case 4: goto L_0x010a;
            case 5: goto L_0x0137;
            case 6: goto L_0x0148;
            case 7: goto L_0x0159;
            case 8: goto L_0x01dd;
            default: goto L_0x0020;
        };
    L_0x0020:
        r1 = r7 + 1;
        r7 = r1;
        goto L_0x000b;
    L_0x0024:
        r10 = "id";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x002d:
        r1 = r2;
        goto L_0x001d;
    L_0x002f:
        r10 = "backgroundColor";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x0038:
        r1 = r4;
        goto L_0x001d;
    L_0x003a:
        r10 = "color";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x0043:
        r1 = r5;
        goto L_0x001d;
    L_0x0045:
        r10 = "fontFamily";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x004e:
        r1 = r6;
        goto L_0x001d;
    L_0x0050:
        r10 = "fontSize";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x0059:
        r1 = 4;
        goto L_0x001d;
    L_0x005b:
        r10 = "fontWeight";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x0064:
        r1 = 5;
        goto L_0x001d;
    L_0x0066:
        r10 = "fontStyle";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x006f:
        r1 = 6;
        goto L_0x001d;
    L_0x0071:
        r10 = "textAlign";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x007a:
        r1 = 7;
        goto L_0x001d;
    L_0x007c:
        r10 = "textDecoration";
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x001c;
    L_0x0085:
        r1 = 8;
        goto L_0x001d;
    L_0x0088:
        r1 = "style";
        r10 = r13.getName();
        r1 = r1.equals(r10);
        if (r1 == 0) goto L_0x0020;
    L_0x0095:
        r0 = r12.createIfNull(r0);
        r0 = r0.setId(r9);
        goto L_0x0020;
    L_0x009e:
        r0 = r12.createIfNull(r0);
        r1 = com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlColorParser.parseColor(r9);	 Catch:{ IllegalArgumentException -> 0x00ab }
        r0.setBackgroundColor(r1);	 Catch:{ IllegalArgumentException -> 0x00ab }
        goto L_0x0020;
    L_0x00ab:
        r1 = move-exception;
        r1 = "TtmlParser";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing background value: '";
        r10 = r10.append(r11);
        r9 = r10.append(r9);
        r10 = "'";
        r9 = r9.append(r10);
        r9 = r9.toString();
        android.util.Log.w(r1, r9);
        goto L_0x0020;
    L_0x00cf:
        r0 = r12.createIfNull(r0);
        r1 = com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlColorParser.parseColor(r9);	 Catch:{ IllegalArgumentException -> 0x00dc }
        r0.setFontColor(r1);	 Catch:{ IllegalArgumentException -> 0x00dc }
        goto L_0x0020;
    L_0x00dc:
        r1 = move-exception;
        r1 = "TtmlParser";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing color value: '";
        r10 = r10.append(r11);
        r9 = r10.append(r9);
        r10 = "'";
        r9 = r9.append(r10);
        r9 = r9.toString();
        android.util.Log.w(r1, r9);
        goto L_0x0020;
    L_0x0100:
        r0 = r12.createIfNull(r0);
        r0 = r0.setFontFamily(r9);
        goto L_0x0020;
    L_0x010a:
        r0 = r12.createIfNull(r0);	 Catch:{ ParserException -> 0x0113 }
        parseFontSize(r9, r0);	 Catch:{ ParserException -> 0x0113 }
        goto L_0x0020;
    L_0x0113:
        r1 = move-exception;
        r1 = "TtmlParser";
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "failed parsing fontSize value: '";
        r10 = r10.append(r11);
        r9 = r10.append(r9);
        r10 = "'";
        r9 = r9.append(r10);
        r9 = r9.toString();
        android.util.Log.w(r1, r9);
        goto L_0x0020;
    L_0x0137:
        r0 = r12.createIfNull(r0);
        r1 = "bold";
        r1 = r1.equalsIgnoreCase(r9);
        r0 = r0.setBold(r1);
        goto L_0x0020;
    L_0x0148:
        r0 = r12.createIfNull(r0);
        r1 = "italic";
        r1 = r1.equalsIgnoreCase(r9);
        r0 = r0.setItalic(r1);
        goto L_0x0020;
    L_0x0159:
        r1 = com.hanista.mobogram.messenger.exoplayer.util.Util.toLowerInvariant(r9);
        r9 = r1.hashCode();
        switch(r9) {
            case -1364013995: goto L_0x01a2;
            case 100571: goto L_0x0197;
            case 3317767: goto L_0x0176;
            case 108511772: goto L_0x018c;
            case 109757538: goto L_0x0181;
            default: goto L_0x0164;
        };
    L_0x0164:
        r1 = r3;
    L_0x0165:
        switch(r1) {
            case 0: goto L_0x016a;
            case 1: goto L_0x01ad;
            case 2: goto L_0x01b9;
            case 3: goto L_0x01c5;
            case 4: goto L_0x01d1;
            default: goto L_0x0168;
        };
    L_0x0168:
        goto L_0x0020;
    L_0x016a:
        r0 = r12.createIfNull(r0);
        r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r0 = r0.setTextAlign(r1);
        goto L_0x0020;
    L_0x0176:
        r9 = "left";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x0164;
    L_0x017f:
        r1 = r2;
        goto L_0x0165;
    L_0x0181:
        r9 = "start";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x0164;
    L_0x018a:
        r1 = r4;
        goto L_0x0165;
    L_0x018c:
        r9 = "right";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x0164;
    L_0x0195:
        r1 = r5;
        goto L_0x0165;
    L_0x0197:
        r9 = "end";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x0164;
    L_0x01a0:
        r1 = r6;
        goto L_0x0165;
    L_0x01a2:
        r9 = "center";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x0164;
    L_0x01ab:
        r1 = 4;
        goto L_0x0165;
    L_0x01ad:
        r0 = r12.createIfNull(r0);
        r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r0 = r0.setTextAlign(r1);
        goto L_0x0020;
    L_0x01b9:
        r0 = r12.createIfNull(r0);
        r1 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r0 = r0.setTextAlign(r1);
        goto L_0x0020;
    L_0x01c5:
        r0 = r12.createIfNull(r0);
        r1 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        r0 = r0.setTextAlign(r1);
        goto L_0x0020;
    L_0x01d1:
        r0 = r12.createIfNull(r0);
        r1 = android.text.Layout.Alignment.ALIGN_CENTER;
        r0 = r0.setTextAlign(r1);
        goto L_0x0020;
    L_0x01dd:
        r1 = com.hanista.mobogram.messenger.exoplayer.util.Util.toLowerInvariant(r9);
        r9 = r1.hashCode();
        switch(r9) {
            case -1461280213: goto L_0x0219;
            case -1026963764: goto L_0x020e;
            case 913457136: goto L_0x0203;
            case 1679736913: goto L_0x01f8;
            default: goto L_0x01e8;
        };
    L_0x01e8:
        r1 = r3;
    L_0x01e9:
        switch(r1) {
            case 0: goto L_0x01ee;
            case 1: goto L_0x0224;
            case 2: goto L_0x022e;
            case 3: goto L_0x0238;
            default: goto L_0x01ec;
        };
    L_0x01ec:
        goto L_0x0020;
    L_0x01ee:
        r0 = r12.createIfNull(r0);
        r0 = r0.setLinethrough(r4);
        goto L_0x0020;
    L_0x01f8:
        r9 = "linethrough";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x01e8;
    L_0x0201:
        r1 = r2;
        goto L_0x01e9;
    L_0x0203:
        r9 = "nolinethrough";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x01e8;
    L_0x020c:
        r1 = r4;
        goto L_0x01e9;
    L_0x020e:
        r9 = "underline";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x01e8;
    L_0x0217:
        r1 = r5;
        goto L_0x01e9;
    L_0x0219:
        r9 = "nounderline";
        r1 = r1.equals(r9);
        if (r1 == 0) goto L_0x01e8;
    L_0x0222:
        r1 = r6;
        goto L_0x01e9;
    L_0x0224:
        r0 = r12.createIfNull(r0);
        r0 = r0.setLinethrough(r2);
        goto L_0x0020;
    L_0x022e:
        r0 = r12.createIfNull(r0);
        r0 = r0.setUnderline(r4);
        goto L_0x0020;
    L_0x0238:
        r0 = r12.createIfNull(r0);
        r0 = r0.setUnderline(r2);
        goto L_0x0020;
    L_0x0242:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlParser.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlStyle):com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlStyle");
    }

    private String[] parseStyleIds(String str) {
        return str.split("\\s+");
    }

    private static long parseTimeExpression(String str, FrameAndTickRate frameAndTickRate) {
        double d = 0.0d;
        Matcher matcher = CLOCK_TIME.matcher(str);
        if (matcher.matches()) {
            double parseLong = ((double) Long.parseLong(matcher.group(3))) + (((double) (Long.parseLong(matcher.group(1)) * 3600)) + ((double) (Long.parseLong(matcher.group(2)) * 60)));
            String group = matcher.group(4);
            parseLong += group != null ? Double.parseDouble(group) : 0.0d;
            group = matcher.group(5);
            double parseLong2 = (group != null ? (double) (((float) Long.parseLong(group)) / frameAndTickRate.effectiveFrameRate) : 0.0d) + parseLong;
            String group2 = matcher.group(6);
            if (group2 != null) {
                d = (((double) Long.parseLong(group2)) / ((double) frameAndTickRate.subFrameRate)) / ((double) frameAndTickRate.effectiveFrameRate);
            }
            return (long) ((parseLong2 + d) * 1000000.0d);
        }
        Matcher matcher2 = OFFSET_TIME.matcher(str);
        if (matcher2.matches()) {
            parseLong2 = Double.parseDouble(matcher2.group(1));
            String group3 = matcher2.group(2);
            if (group3.equals("h")) {
                parseLong2 *= 3600.0d;
            } else if (group3.equals("m")) {
                parseLong2 *= 60.0d;
            } else if (!group3.equals("s")) {
                if (group3.equals("ms")) {
                    parseLong2 /= 1000.0d;
                } else if (group3.equals("f")) {
                    parseLong2 /= (double) frameAndTickRate.effectiveFrameRate;
                } else if (group3.equals("t")) {
                    parseLong2 /= (double) frameAndTickRate.tickRate;
                }
            }
            return (long) (parseLong2 * 1000000.0d);
        }
        throw new ParserException("Malformed time expression: " + str);
    }

    public boolean canParse(String str) {
        return MimeTypes.APPLICATION_TTML.equals(str);
    }

    public TtmlSubtitle parse(byte[] bArr, int i, int i2) {
        try {
            XmlPullParser newPullParser = this.xmlParserFactory.newPullParser();
            Map hashMap = new HashMap();
            Map hashMap2 = new HashMap();
            hashMap2.put(TtmlNode.ANONYMOUS_REGION_ID, new TtmlRegion());
            newPullParser.setInput(new ByteArrayInputStream(bArr, i, i2), null);
            TtmlSubtitle ttmlSubtitle = null;
            LinkedList linkedList = new LinkedList();
            int i3 = 0;
            int eventType = newPullParser.getEventType();
            FrameAndTickRate frameAndTickRate = DEFAULT_FRAME_AND_TICK_RATE;
            for (int i4 = eventType; i4 != 1; i4 = newPullParser.getEventType()) {
                TtmlNode ttmlNode = (TtmlNode) linkedList.peekLast();
                if (i3 == 0) {
                    TtmlSubtitle ttmlSubtitle2;
                    FrameAndTickRate frameAndTickRate2;
                    int i5;
                    String name = newPullParser.getName();
                    if (i4 == 2) {
                        if (TtmlNode.TAG_TT.equals(name)) {
                            frameAndTickRate = parseFrameAndTickRates(newPullParser);
                        }
                        int i6;
                        if (!isSupportedTag(name)) {
                            Log.i(TAG, "Ignoring unsupported tag: " + newPullParser.getName());
                            eventType = i3 + 1;
                            ttmlSubtitle2 = ttmlSubtitle;
                            i6 = eventType;
                            frameAndTickRate2 = frameAndTickRate;
                            i5 = i6;
                        } else if (TtmlNode.TAG_HEAD.equals(name)) {
                            parseHeader(newPullParser, hashMap, hashMap2);
                            frameAndTickRate2 = frameAndTickRate;
                            i5 = i3;
                            ttmlSubtitle2 = ttmlSubtitle;
                        } else {
                            try {
                                TtmlNode parseNode = parseNode(newPullParser, ttmlNode, hashMap2, frameAndTickRate);
                                linkedList.addLast(parseNode);
                                if (ttmlNode != null) {
                                    ttmlNode.addChild(parseNode);
                                }
                                frameAndTickRate2 = frameAndTickRate;
                                i5 = i3;
                                ttmlSubtitle2 = ttmlSubtitle;
                            } catch (Throwable e) {
                                Log.w(TAG, "Suppressing parser error", e);
                                eventType = i3 + 1;
                                ttmlSubtitle2 = ttmlSubtitle;
                                i6 = eventType;
                                frameAndTickRate2 = frameAndTickRate;
                                i5 = i6;
                            }
                        }
                    } else if (i4 == 4) {
                        ttmlNode.addChild(TtmlNode.buildTextNode(newPullParser.getText()));
                        frameAndTickRate2 = frameAndTickRate;
                        i5 = i3;
                        ttmlSubtitle2 = ttmlSubtitle;
                    } else if (i4 == 3) {
                        TtmlSubtitle ttmlSubtitle3 = newPullParser.getName().equals(TtmlNode.TAG_TT) ? new TtmlSubtitle((TtmlNode) linkedList.getLast(), hashMap, hashMap2) : ttmlSubtitle;
                        linkedList.removeLast();
                        FrameAndTickRate frameAndTickRate3 = frameAndTickRate;
                        i5 = i3;
                        ttmlSubtitle2 = ttmlSubtitle3;
                        frameAndTickRate2 = frameAndTickRate3;
                    } else {
                        frameAndTickRate2 = frameAndTickRate;
                        i5 = i3;
                        ttmlSubtitle2 = ttmlSubtitle;
                    }
                    ttmlSubtitle = ttmlSubtitle2;
                    i3 = i5;
                    frameAndTickRate = frameAndTickRate2;
                } else if (i4 == 2) {
                    i3++;
                } else if (i4 == 3) {
                    i3--;
                }
                newPullParser.next();
            }
            return ttmlSubtitle;
        } catch (Throwable e2) {
            throw new ParserException("Unable to parse source", e2);
        } catch (Throwable e22) {
            throw new IllegalStateException("Unexpected error when reading input.", e22);
        }
    }
}
