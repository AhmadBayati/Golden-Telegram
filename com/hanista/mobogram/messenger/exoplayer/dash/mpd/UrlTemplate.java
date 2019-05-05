package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import java.util.Locale;

public final class UrlTemplate {
    private static final String BANDWIDTH = "Bandwidth";
    private static final int BANDWIDTH_ID = 3;
    private static final String DEFAULT_FORMAT_TAG = "%01d";
    private static final String ESCAPED_DOLLAR = "$$";
    private static final String NUMBER = "Number";
    private static final int NUMBER_ID = 2;
    private static final String REPRESENTATION = "RepresentationID";
    private static final int REPRESENTATION_ID = 1;
    private static final String TIME = "Time";
    private static final int TIME_ID = 4;
    private final int identifierCount;
    private final String[] identifierFormatTags;
    private final int[] identifiers;
    private final String[] urlPieces;

    private UrlTemplate(String[] strArr, int[] iArr, String[] strArr2, int i) {
        this.urlPieces = strArr;
        this.identifiers = iArr;
        this.identifierFormatTags = strArr2;
        this.identifierCount = i;
    }

    public static UrlTemplate compile(String str) {
        String[] strArr = new String[5];
        int[] iArr = new int[TIME_ID];
        String[] strArr2 = new String[TIME_ID];
        return new UrlTemplate(strArr, iArr, strArr2, parseTemplate(str, strArr, iArr, strArr2));
    }

    private static int parseTemplate(String str, String[] strArr, int[] iArr, String[] strArr2) {
        strArr[0] = TtmlNode.ANONYMOUS_REGION_ID;
        int i = 0;
        int i2 = 0;
        while (i2 < str.length()) {
            int indexOf = str.indexOf("$", i2);
            if (indexOf == -1) {
                strArr[i] = strArr[i] + str.substring(i2);
                i2 = str.length();
            } else if (indexOf != i2) {
                strArr[i] = strArr[i] + str.substring(i2, indexOf);
                i2 = indexOf;
            } else if (str.startsWith(ESCAPED_DOLLAR, i2)) {
                strArr[i] = strArr[i] + "$";
                i2 += NUMBER_ID;
            } else {
                int indexOf2 = str.indexOf("$", i2 + REPRESENTATION_ID);
                String substring = str.substring(i2 + REPRESENTATION_ID, indexOf2);
                if (substring.equals(REPRESENTATION)) {
                    iArr[i] = REPRESENTATION_ID;
                } else {
                    int indexOf3 = substring.indexOf("%0");
                    String str2 = DEFAULT_FORMAT_TAG;
                    if (indexOf3 != -1) {
                        str2 = substring.substring(indexOf3);
                        if (!str2.endsWith("d")) {
                            str2 = str2 + "d";
                        }
                        substring = substring.substring(0, indexOf3);
                    }
                    if (substring.equals(NUMBER)) {
                        iArr[i] = NUMBER_ID;
                    } else if (substring.equals(BANDWIDTH)) {
                        iArr[i] = BANDWIDTH_ID;
                    } else if (substring.equals(TIME)) {
                        iArr[i] = TIME_ID;
                    } else {
                        throw new IllegalArgumentException("Invalid template: " + str);
                    }
                    strArr2[i] = str2;
                }
                i += REPRESENTATION_ID;
                strArr[i] = TtmlNode.ANONYMOUS_REGION_ID;
                i2 = indexOf2 + REPRESENTATION_ID;
            }
        }
        return i;
    }

    public String buildUri(String str, int i, int i2, long j) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i3 = 0; i3 < this.identifierCount; i3 += REPRESENTATION_ID) {
            stringBuilder.append(this.urlPieces[i3]);
            if (this.identifiers[i3] == REPRESENTATION_ID) {
                stringBuilder.append(str);
            } else if (this.identifiers[i3] == NUMBER_ID) {
                r3 = Locale.US;
                r4 = this.identifierFormatTags[i3];
                r5 = new Object[REPRESENTATION_ID];
                r5[0] = Integer.valueOf(i);
                stringBuilder.append(String.format(r3, r4, r5));
            } else if (this.identifiers[i3] == BANDWIDTH_ID) {
                r3 = Locale.US;
                r4 = this.identifierFormatTags[i3];
                r5 = new Object[REPRESENTATION_ID];
                r5[0] = Integer.valueOf(i2);
                stringBuilder.append(String.format(r3, r4, r5));
            } else if (this.identifiers[i3] == TIME_ID) {
                r3 = Locale.US;
                r4 = this.identifierFormatTags[i3];
                r5 = new Object[REPRESENTATION_ID];
                r5[0] = Long.valueOf(j);
                stringBuilder.append(String.format(r3, r4, r5));
            }
        }
        stringBuilder.append(this.urlPieces[this.identifierCount]);
        return stringBuilder.toString();
    }
}
