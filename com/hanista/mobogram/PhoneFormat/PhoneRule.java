package com.hanista.mobogram.PhoneFormat;

import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;

public class PhoneRule {
    public int byte8;
    public int flag12;
    public int flag13;
    public String format;
    public boolean hasIntlPrefix;
    public boolean hasTrunkPrefix;
    public int maxLen;
    public int maxVal;
    public int minVal;
    public int otherFlag;
    public int prefixLen;

    String format(String str, String str2, String str3) {
        StringBuilder stringBuilder = new StringBuilder(20);
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (i < this.format.length()) {
            char charAt = this.format.charAt(i);
            switch (charAt) {
                case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                    if (i2 >= str.length()) {
                        if (i3 == 0) {
                            break;
                        }
                        stringBuilder.append(" ");
                        break;
                    }
                    stringBuilder.append(str.substring(i2, i2 + 1));
                    i2++;
                    continue;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    if (i2 < str.length()) {
                        i3 = 1;
                        break;
                    }
                    break;
                case 'c':
                    if (str2 == null) {
                        i5 = 1;
                        break;
                    }
                    stringBuilder.append(str2);
                    i5 = 1;
                    continue;
                case 'n':
                    if (str3 == null) {
                        i4 = 1;
                        break;
                    }
                    stringBuilder.append(str3);
                    i4 = 1;
                    continue;
            }
            if (!(charAt == ' ' && i > 0 && ((this.format.charAt(i - 1) == 'n' && str3 == null) || (this.format.charAt(i - 1) == 'c' && str2 == null))) && (i2 < str.length() || (r3 != 0 && charAt == ')'))) {
                stringBuilder.append(this.format.substring(i, i + 1));
                if (charAt == ')') {
                    i3 = 0;
                }
            }
            i++;
        }
        if (str2 != null && r5 == 0) {
            stringBuilder.insert(0, String.format("%s ", new Object[]{str2}));
        } else if (str3 != null && r4 == 0) {
            stringBuilder.insert(0, str3);
        }
        return stringBuilder.toString();
    }

    boolean hasIntlPrefix() {
        return (this.flag12 & 2) != 0;
    }

    boolean hasTrunkPrefix() {
        return (this.flag12 & 1) != 0;
    }
}
