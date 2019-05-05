package com.hanista.mobogram.messenger.exoplayer.text.ttml;

import android.text.TextUtils;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TtmlColorParser {
    static final int AQUA = 16777215;
    static final int BLACK = -16777216;
    static final int BLUE = -16776961;
    private static final Map<String, Integer> COLOR_NAME_MAP;
    static final int CYAN = -16711681;
    static final int FUCHSIA = -65281;
    static final int GRAY = -8355712;
    static final int GREEN = -16744448;
    static final int LIME = -16711936;
    static final int MAGENTA = -65281;
    static final int MAROON = -8388608;
    static final int NAVY = -16777088;
    static final int OLIVE = -8355840;
    static final int PURPLE = -8388480;
    static final int RED = -65536;
    private static final String RGB = "rgb";
    private static final String RGBA = "rgba";
    private static final Pattern RGBA_PATTERN;
    private static final Pattern RGB_PATTERN;
    static final int SILVER = -4144960;
    static final int TEAL = -16744320;
    static final int TRANSPARENT = 0;
    static final int WHITE = -1;
    static final int YELLOW = -256;

    static {
        RGB_PATTERN = Pattern.compile("^rgb\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)$");
        RGBA_PATTERN = Pattern.compile("^rgba\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)$");
        COLOR_NAME_MAP = new HashMap();
        COLOR_NAME_MAP.put("transparent", Integer.valueOf(TRANSPARENT));
        COLOR_NAME_MAP.put("black", Integer.valueOf(BLACK));
        COLOR_NAME_MAP.put("silver", Integer.valueOf(SILVER));
        COLOR_NAME_MAP.put("gray", Integer.valueOf(GRAY));
        COLOR_NAME_MAP.put("white", Integer.valueOf(WHITE));
        COLOR_NAME_MAP.put("maroon", Integer.valueOf(MAROON));
        COLOR_NAME_MAP.put("red", Integer.valueOf(RED));
        COLOR_NAME_MAP.put("purple", Integer.valueOf(PURPLE));
        COLOR_NAME_MAP.put("fuchsia", Integer.valueOf(MAGENTA));
        COLOR_NAME_MAP.put("magenta", Integer.valueOf(MAGENTA));
        COLOR_NAME_MAP.put("green", Integer.valueOf(GREEN));
        COLOR_NAME_MAP.put("lime", Integer.valueOf(LIME));
        COLOR_NAME_MAP.put("olive", Integer.valueOf(OLIVE));
        COLOR_NAME_MAP.put("yellow", Integer.valueOf(YELLOW));
        COLOR_NAME_MAP.put("navy", Integer.valueOf(NAVY));
        COLOR_NAME_MAP.put("blue", Integer.valueOf(BLUE));
        COLOR_NAME_MAP.put("teal", Integer.valueOf(TEAL));
        COLOR_NAME_MAP.put("aqua", Integer.valueOf(AQUA));
        COLOR_NAME_MAP.put("cyan", Integer.valueOf(CYAN));
    }

    TtmlColorParser() {
    }

    private static int argb(int i, int i2, int i3, int i4) {
        return (((i << 24) | (i2 << 16)) | (i3 << 8)) | i4;
    }

    public static int parseColor(String str) {
        Assertions.checkArgument(!TextUtils.isEmpty(str));
        Object replace = str.replace(" ", TtmlNode.ANONYMOUS_REGION_ID);
        if (replace.charAt(TRANSPARENT) == '#') {
            int parseLong = (int) Long.parseLong(replace.substring(1), 16);
            if (replace.length() == 7) {
                return BLACK | parseLong;
            }
            if (replace.length() == 9) {
                return ((parseLong & NalUnitUtil.EXTENDED_SAR) << 24) | (parseLong >>> 8);
            }
            throw new IllegalArgumentException();
        }
        Matcher matcher;
        if (replace.startsWith(RGBA)) {
            matcher = RGBA_PATTERN.matcher(replace);
            if (matcher.matches()) {
                return argb(255 - Integer.parseInt(matcher.group(4), 10), Integer.parseInt(matcher.group(1), 10), Integer.parseInt(matcher.group(2), 10), Integer.parseInt(matcher.group(3), 10));
            }
        } else if (replace.startsWith(RGB)) {
            matcher = RGB_PATTERN.matcher(replace);
            if (matcher.matches()) {
                return rgb(Integer.parseInt(matcher.group(1), 10), Integer.parseInt(matcher.group(2), 10), Integer.parseInt(matcher.group(3), 10));
            }
        } else {
            Integer num = (Integer) COLOR_NAME_MAP.get(Util.toLowerInvariant(replace));
            if (num != null) {
                return num.intValue();
            }
        }
        throw new IllegalArgumentException();
    }

    private static int rgb(int i, int i2, int i3) {
        return argb(NalUnitUtil.EXTENDED_SAR, i, i2, i3);
    }
}
