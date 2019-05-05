package com.hanista.mobogram.messenger.exoplayer.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCue.Builder;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebvttCueParser {
    private static final char CHAR_AMPERSAND = '&';
    private static final char CHAR_GREATER_THAN = '>';
    private static final char CHAR_LESS_THAN = '<';
    private static final char CHAR_SEMI_COLON = ';';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_SPACE = ' ';
    private static final Pattern COMMENT;
    public static final Pattern CUE_HEADER_PATTERN;
    private static final Pattern CUE_SETTING_PATTERN;
    private static final String ENTITY_AMPERSAND = "amp";
    private static final String ENTITY_GREATER_THAN = "gt";
    private static final String ENTITY_LESS_THAN = "lt";
    private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
    private static final String SPACE = " ";
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;
    private static final String TAG = "WebvttCueParser";
    private static final String TAG_BOLD = "b";
    private static final String TAG_CLASS = "c";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_LANG = "lang";
    private static final String TAG_UNDERLINE = "u";
    private static final String TAG_VOICE = "v";
    private final StringBuilder textBuilder;

    private static final class StartTag {
        public final String name;
        public final int position;

        public StartTag(String str, int i) {
            this.position = i;
            this.name = str;
        }
    }

    static {
        CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
        COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
        CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
    }

    public WebvttCueParser() {
        this.textBuilder = new StringBuilder();
    }

    private static void applyEntity(String str, SpannableStringBuilder spannableStringBuilder) {
        Object obj = -1;
        switch (str.hashCode()) {
            case 3309:
                if (str.equals(ENTITY_GREATER_THAN)) {
                    obj = STYLE_BOLD;
                    break;
                }
                break;
            case 3464:
                if (str.equals(ENTITY_LESS_THAN)) {
                    obj = null;
                    break;
                }
                break;
            case 96708:
                if (str.equals(ENTITY_AMPERSAND)) {
                    obj = 3;
                    break;
                }
                break;
            case 3374865:
                if (str.equals(ENTITY_NON_BREAK_SPACE)) {
                    obj = STYLE_ITALIC;
                    break;
                }
                break;
        }
        switch (obj) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                spannableStringBuilder.append(CHAR_LESS_THAN);
            case STYLE_BOLD /*1*/:
                spannableStringBuilder.append(CHAR_GREATER_THAN);
            case STYLE_ITALIC /*2*/:
                spannableStringBuilder.append(CHAR_SPACE);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                spannableStringBuilder.append(CHAR_AMPERSAND);
            default:
                Log.w(TAG, "ignoring unsupported entity: '&" + str + ";'");
        }
    }

    private static void applySpansForTag(StartTag startTag, SpannableStringBuilder spannableStringBuilder) {
        String str = startTag.name;
        int i = -1;
        switch (str.hashCode()) {
            case 98:
                if (str.equals(TAG_BOLD)) {
                    i = 0;
                    break;
                }
                break;
            case 105:
                if (str.equals(TAG_ITALIC)) {
                    i = STYLE_BOLD;
                    break;
                }
                break;
            case 117:
                if (str.equals(TAG_UNDERLINE)) {
                    i = STYLE_ITALIC;
                    break;
                }
                break;
        }
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                spannableStringBuilder.setSpan(new StyleSpan(STYLE_BOLD), startTag.position, spannableStringBuilder.length(), 33);
            case STYLE_BOLD /*1*/:
                spannableStringBuilder.setSpan(new StyleSpan(STYLE_ITALIC), startTag.position, spannableStringBuilder.length(), 33);
            case STYLE_ITALIC /*2*/:
                spannableStringBuilder.setSpan(new UnderlineSpan(), startTag.position, spannableStringBuilder.length(), 33);
            default:
        }
    }

    private static int findEndOfTag(String str, int i) {
        int indexOf = str.indexOf(62, i);
        return indexOf == -1 ? str.length() : indexOf + STYLE_BOLD;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.regex.Matcher findNextCueHeader(com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray r2) {
        /*
    L_0x0000:
        r0 = r2.readLine();
        if (r0 == 0) goto L_0x002c;
    L_0x0006:
        r1 = COMMENT;
        r1 = r1.matcher(r0);
        r1 = r1.matches();
        if (r1 == 0) goto L_0x001f;
    L_0x0012:
        r0 = r2.readLine();
        if (r0 == 0) goto L_0x0000;
    L_0x0018:
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0000;
    L_0x001e:
        goto L_0x0012;
    L_0x001f:
        r1 = CUE_HEADER_PATTERN;
        r0 = r1.matcher(r0);
        r1 = r0.matches();
        if (r1 == 0) goto L_0x0000;
    L_0x002b:
        return r0;
    L_0x002c:
        r0 = 0;
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCueParser.findNextCueHeader(com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray):java.util.regex.Matcher");
    }

    private static boolean isSupportedTag(String str) {
        boolean z = true;
        switch (str.hashCode()) {
            case 98:
                if (str.equals(TAG_BOLD)) {
                    z = false;
                    break;
                }
                break;
            case 99:
                if (str.equals(TAG_CLASS)) {
                    z = true;
                    break;
                }
                break;
            case 105:
                if (str.equals(TAG_ITALIC)) {
                    z = true;
                    break;
                }
                break;
            case 117:
                if (str.equals(TAG_UNDERLINE)) {
                    z = true;
                    break;
                }
                break;
            case 118:
                if (str.equals(TAG_VOICE)) {
                    z = true;
                    break;
                }
                break;
            case 3314158:
                if (str.equals(TAG_LANG)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case STYLE_BOLD /*1*/:
            case STYLE_ITALIC /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                return true;
            default:
                return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean parseCue(java.util.regex.Matcher r6, com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray r7, com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCue.Builder r8, java.lang.StringBuilder r9) {
        /*
        r0 = 1;
        r1 = 0;
        r2 = 1;
        r2 = r6.group(r2);	 Catch:{ NumberFormatException -> 0x0046 }
        r2 = com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r2);	 Catch:{ NumberFormatException -> 0x0046 }
        r2 = r8.setStartTime(r2);	 Catch:{ NumberFormatException -> 0x0046 }
        r3 = 2;
        r3 = r6.group(r3);	 Catch:{ NumberFormatException -> 0x0046 }
        r4 = com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r3);	 Catch:{ NumberFormatException -> 0x0046 }
        r2.setEndTime(r4);	 Catch:{ NumberFormatException -> 0x0046 }
        r2 = 3;
        r2 = r6.group(r2);
        parseCueSettingsList(r2, r8);
        r9.setLength(r1);
    L_0x0026:
        r1 = r7.readLine();
        if (r1 == 0) goto L_0x0067;
    L_0x002c:
        r2 = r1.isEmpty();
        if (r2 != 0) goto L_0x0067;
    L_0x0032:
        r2 = r9.length();
        if (r2 <= 0) goto L_0x003e;
    L_0x0038:
        r2 = "\n";
        r9.append(r2);
    L_0x003e:
        r1 = r1.trim();
        r9.append(r1);
        goto L_0x0026;
    L_0x0046:
        r0 = move-exception;
        r0 = "WebvttCueParser";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Skipping cue with bad header: ";
        r2 = r2.append(r3);
        r3 = r6.group();
        r2 = r2.append(r3);
        r2 = r2.toString();
        android.util.Log.w(r0, r2);
        r0 = r1;
    L_0x0066:
        return r0;
    L_0x0067:
        r1 = r9.toString();
        parseCueText(r1, r8);
        goto L_0x0066;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCueParser.parseCue(java.util.regex.Matcher, com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray, com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCue$Builder, java.lang.StringBuilder):boolean");
    }

    static void parseCueSettingsList(String str, Builder builder) {
        Matcher matcher = CUE_SETTING_PATTERN.matcher(str);
        while (matcher.find()) {
            String group = matcher.group(STYLE_BOLD);
            String group2 = matcher.group(STYLE_ITALIC);
            try {
                if ("line".equals(group)) {
                    parseLineAttribute(group2, builder);
                } else if ("align".equals(group)) {
                    builder.setTextAlignment(parseTextAlignment(group2));
                } else if ("position".equals(group)) {
                    parsePositionAttribute(group2, builder);
                } else if ("size".equals(group)) {
                    builder.setWidth(WebvttParserUtil.parsePercentage(group2));
                } else {
                    Log.w(TAG, "Unknown cue setting " + group + ":" + group2);
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Skipping bad cue setting: " + matcher.group());
            }
        }
    }

    static void parseCueText(String str, Builder builder) {
        CharSequence spannableStringBuilder = new SpannableStringBuilder();
        Stack stack = new Stack();
        int i = 0;
        while (i < str.length()) {
            char charAt = str.charAt(i);
            int indexOf;
            int indexOf2;
            switch (charAt) {
                case NalUnitTypes.NAL_TYPE_FD_NUT /*38*/:
                    indexOf = str.indexOf(59, i + STYLE_BOLD);
                    indexOf2 = str.indexOf(32, i + STYLE_BOLD);
                    if (indexOf == -1) {
                        indexOf = indexOf2;
                    } else if (indexOf2 != -1) {
                        indexOf = Math.min(indexOf, indexOf2);
                    }
                    if (indexOf == -1) {
                        spannableStringBuilder.append(charAt);
                        i += STYLE_BOLD;
                        break;
                    }
                    applyEntity(str.substring(i + STYLE_BOLD, indexOf), spannableStringBuilder);
                    if (indexOf == indexOf2) {
                        spannableStringBuilder.append(SPACE);
                    }
                    i = indexOf + STYLE_BOLD;
                    break;
                case '<':
                    if (i + STYLE_BOLD < str.length()) {
                        indexOf2 = str.charAt(i + STYLE_BOLD) == CHAR_SLASH ? STYLE_BOLD : 0;
                        indexOf = findEndOfTag(str, i + STYLE_BOLD);
                        int i2 = str.charAt(indexOf + -2) == CHAR_SLASH ? STYLE_BOLD : 0;
                        String[] tokenizeTag = tokenizeTag(str.substring((indexOf2 != 0 ? STYLE_ITALIC : STYLE_BOLD) + i, i2 != 0 ? indexOf - 2 : indexOf - 1));
                        if (tokenizeTag != null) {
                            if (isSupportedTag(tokenizeTag[0])) {
                                if (indexOf2 == 0) {
                                    if (i2 == 0) {
                                        stack.push(new StartTag(tokenizeTag[0], spannableStringBuilder.length()));
                                        i = indexOf;
                                        break;
                                    }
                                }
                                while (!stack.isEmpty()) {
                                    StartTag startTag = (StartTag) stack.pop();
                                    applySpansForTag(startTag, spannableStringBuilder);
                                    if (startTag.name.equals(tokenizeTag[0])) {
                                        i = indexOf;
                                        break;
                                    }
                                }
                                i = indexOf;
                            } else {
                                i = indexOf;
                                break;
                            }
                        }
                        i = indexOf;
                        break;
                    }
                    i += STYLE_BOLD;
                    break;
                default:
                    spannableStringBuilder.append(charAt);
                    i += STYLE_BOLD;
                    break;
            }
        }
        while (!stack.isEmpty()) {
            applySpansForTag((StartTag) stack.pop(), spannableStringBuilder);
        }
        builder.setText(spannableStringBuilder);
    }

    private static void parseLineAttribute(String str, Builder builder) {
        int indexOf = str.indexOf(44);
        if (indexOf != -1) {
            builder.setLineAnchor(parsePositionAnchor(str.substring(indexOf + STYLE_BOLD)));
            str = str.substring(0, indexOf);
        } else {
            builder.setLineAnchor(TLRPC.MESSAGE_FLAG_MEGAGROUP);
        }
        if (str.endsWith("%")) {
            builder.setLine(WebvttParserUtil.parsePercentage(str)).setLineType(0);
        } else {
            builder.setLine((float) Integer.parseInt(str)).setLineType(STYLE_BOLD);
        }
    }

    private static int parsePositionAnchor(String str) {
        int i = -1;
        switch (str.hashCode()) {
            case -1364013995:
                if (str.equals(TtmlNode.CENTER)) {
                    i = STYLE_BOLD;
                    break;
                }
                break;
            case -1074341483:
                if (str.equals("middle")) {
                    i = STYLE_ITALIC;
                    break;
                }
                break;
            case 100571:
                if (str.equals(TtmlNode.END)) {
                    i = 3;
                    break;
                }
                break;
            case 109757538:
                if (str.equals(TtmlNode.START)) {
                    i = 0;
                    break;
                }
                break;
        }
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return 0;
            case STYLE_BOLD /*1*/:
            case STYLE_ITALIC /*2*/:
                return STYLE_BOLD;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return STYLE_ITALIC;
            default:
                Log.w(TAG, "Invalid anchor value: " + str);
                return TLRPC.MESSAGE_FLAG_MEGAGROUP;
        }
    }

    private static void parsePositionAttribute(String str, Builder builder) {
        int indexOf = str.indexOf(44);
        if (indexOf != -1) {
            builder.setPositionAnchor(parsePositionAnchor(str.substring(indexOf + STYLE_BOLD)));
            str = str.substring(0, indexOf);
        } else {
            builder.setPositionAnchor(TLRPC.MESSAGE_FLAG_MEGAGROUP);
        }
        builder.setPosition(WebvttParserUtil.parsePercentage(str));
    }

    private static Alignment parseTextAlignment(String str) {
        Object obj = -1;
        switch (str.hashCode()) {
            case -1364013995:
                if (str.equals(TtmlNode.CENTER)) {
                    obj = STYLE_ITALIC;
                    break;
                }
                break;
            case -1074341483:
                if (str.equals("middle")) {
                    obj = 3;
                    break;
                }
                break;
            case 100571:
                if (str.equals(TtmlNode.END)) {
                    obj = 4;
                    break;
                }
                break;
            case 3317767:
                if (str.equals(TtmlNode.LEFT)) {
                    obj = STYLE_BOLD;
                    break;
                }
                break;
            case 108511772:
                if (str.equals(TtmlNode.RIGHT)) {
                    obj = 5;
                    break;
                }
                break;
            case 109757538:
                if (str.equals(TtmlNode.START)) {
                    obj = null;
                    break;
                }
                break;
        }
        switch (obj) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case STYLE_BOLD /*1*/:
                return Alignment.ALIGN_NORMAL;
            case STYLE_ITALIC /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return Alignment.ALIGN_CENTER;
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                return Alignment.ALIGN_OPPOSITE;
            default:
                Log.w(TAG, "Invalid alignment value: " + str);
                return null;
        }
    }

    private static String[] tokenizeTag(String str) {
        String trim = str.replace("\\s+", SPACE).trim();
        if (trim.length() == 0) {
            return null;
        }
        if (trim.contains(SPACE)) {
            trim = trim.substring(0, trim.indexOf(SPACE));
        }
        return trim.split("\\.");
    }

    boolean parseNextValidCue(ParsableByteArray parsableByteArray, Builder builder) {
        Matcher findNextCueHeader;
        do {
            findNextCueHeader = findNextCueHeader(parsableByteArray);
            if (findNextCueHeader == null) {
                return false;
            }
        } while (!parseCue(findNextCueHeader, parsableByteArray, builder, this.textBuilder));
        return true;
    }
}
