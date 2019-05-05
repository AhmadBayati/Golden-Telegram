package com.hanista.mobogram.messenger.exoplayer.text.webvtt;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import java.util.regex.Pattern;

public final class WebvttParserUtil {
    private static final Pattern HEADER;

    static {
        HEADER = Pattern.compile("^\ufeff?WEBVTT(( |\t).*)?$");
    }

    private WebvttParserUtil() {
    }

    public static float parsePercentage(String str) {
        if (str.endsWith("%")) {
            return Float.parseFloat(str.substring(0, str.length() - 1)) / 100.0f;
        }
        throw new NumberFormatException("Percentages must end with %");
    }

    public static long parseTimestampUs(String str) {
        int i = 0;
        long j = 0;
        String[] split = str.split("\\.", 2);
        String[] split2 = split[0].split(":");
        while (i < split2.length) {
            j = (j * 60) + Long.parseLong(split2[i]);
            i++;
        }
        return (Long.parseLong(split[1]) + (j * 1000)) * 1000;
    }

    public static void validateWebvttHeaderLine(ParsableByteArray parsableByteArray) {
        Object readLine = parsableByteArray.readLine();
        if (readLine == null || !HEADER.matcher(readLine).matches()) {
            throw new ParserException("Expected WEBVTT. Got " + readLine);
        }
    }
}
