package com.hanista.mobogram.messenger.time;

import android.support.v4.view.PointerIconCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FastDatePrinter implements DatePrinter, Serializable {
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache;
    private static final long serialVersionUID = 1;
    private final Locale mLocale;
    private transient int mMaxLengthEstimate;
    private final String mPattern;
    private transient Rule[] mRules;
    private final TimeZone mTimeZone;

    private interface Rule {
        void appendTo(StringBuffer stringBuffer, Calendar calendar);

        int estimateLength();
    }

    private static class CharacterLiteral implements Rule {
        private final char mValue;

        CharacterLiteral(char c) {
            this.mValue = c;
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            stringBuffer.append(this.mValue);
        }

        public int estimateLength() {
            return FastDatePrinter.LONG;
        }
    }

    private interface NumberRule extends Rule {
        void appendTo(StringBuffer stringBuffer, int i);
    }

    private static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int i, int i2) {
            if (i2 < FastDatePrinter.SHORT) {
                throw new IllegalArgumentException();
            }
            this.mField = i;
            this.mSize = i2;
        }

        public final void appendTo(StringBuffer stringBuffer, int i) {
            int i2;
            if (i < 100) {
                i2 = this.mSize;
                while (true) {
                    i2--;
                    if (i2 >= FastDatePrinter.MEDIUM) {
                        stringBuffer.append('0');
                    } else {
                        stringBuffer.append((char) ((i / 10) + 48));
                        stringBuffer.append((char) ((i % 10) + 48));
                        return;
                    }
                }
            }
            i2 = i < PointerIconCompat.TYPE_DEFAULT ? FastDatePrinter.SHORT : Integer.toString(i).length();
            int i3 = this.mSize;
            while (true) {
                i3--;
                if (i3 >= i2) {
                    stringBuffer.append('0');
                } else {
                    stringBuffer.append(Integer.toString(i));
                    return;
                }
            }
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            appendTo(stringBuffer, calendar.get(this.mField));
        }

        public int estimateLength() {
            return 4;
        }
    }

    private static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String str) {
            this.mValue = str;
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            stringBuffer.append(this.mValue);
        }

        public int estimateLength() {
            return this.mValue.length();
        }
    }

    private static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int i, String[] strArr) {
            this.mField = i;
            this.mValues = strArr;
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            stringBuffer.append(this.mValues[calendar.get(this.mField)]);
        }

        public int estimateLength() {
            int i = FastDatePrinter.FULL;
            int length = this.mValues.length;
            while (true) {
                int i2 = length - 1;
                if (i2 < 0) {
                    return i;
                }
                length = this.mValues[i2].length();
                if (length <= i) {
                    length = i;
                }
                i = length;
                length = i2;
            }
        }
    }

    private static class TimeZoneDisplayKey {
        private final Locale mLocale;
        private final int mStyle;
        private final TimeZone mTimeZone;

        TimeZoneDisplayKey(TimeZone timeZone, boolean z, int i, Locale locale) {
            this.mTimeZone = timeZone;
            if (z) {
                this.mStyle = TLRPC.MESSAGE_FLAG_MEGAGROUP | i;
            } else {
                this.mStyle = i;
            }
            this.mLocale = locale;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey timeZoneDisplayKey = (TimeZoneDisplayKey) obj;
            return this.mTimeZone.equals(timeZoneDisplayKey.mTimeZone) && this.mStyle == timeZoneDisplayKey.mStyle && this.mLocale.equals(timeZoneDisplayKey.mLocale);
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }
    }

    private static class TimeZoneNameRule implements Rule {
        private final String mDaylight;
        private final Locale mLocale;
        private final String mStandard;
        private final int mStyle;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int i) {
            this.mLocale = locale;
            this.mStyle = i;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, i, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, i, locale);
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            TimeZone timeZone = calendar.getTimeZone();
            if (!timeZone.useDaylightTime() || calendar.get(16) == 0) {
                stringBuffer.append(FastDatePrinter.getTimeZoneDisplay(timeZone, false, this.mStyle, this.mLocale));
            } else {
                stringBuffer.append(FastDatePrinter.getTimeZoneDisplay(timeZone, true, this.mStyle, this.mLocale));
            }
        }

        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }
    }

    private static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON;
        static final TimeZoneNumberRule INSTANCE_NO_COLON;
        final boolean mColon;

        static {
            INSTANCE_COLON = new TimeZoneNumberRule(true);
            INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        }

        TimeZoneNumberRule(boolean z) {
            this.mColon = z;
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            int i = calendar.get(15) + calendar.get(16);
            if (i < 0) {
                stringBuffer.append('-');
                i = -i;
            } else {
                stringBuffer.append('+');
            }
            int i2 = i / 3600000;
            stringBuffer.append((char) ((i2 / 10) + 48));
            stringBuffer.append((char) ((i2 % 10) + 48));
            if (this.mColon) {
                stringBuffer.append(':');
            }
            i = (i / 60000) - (i2 * 60);
            stringBuffer.append((char) ((i / 10) + 48));
            stringBuffer.append((char) ((i % 10) + 48));
        }

        public int estimateLength() {
            return 5;
        }
    }

    private static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule numberRule) {
            this.mRule = numberRule;
        }

        public void appendTo(StringBuffer stringBuffer, int i) {
            this.mRule.appendTo(stringBuffer, i);
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            int i = calendar.get(10);
            if (i == 0) {
                i = calendar.getLeastMaximum(10) + FastDatePrinter.LONG;
            }
            this.mRule.appendTo(stringBuffer, i);
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }
    }

    private static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule numberRule) {
            this.mRule = numberRule;
        }

        public void appendTo(StringBuffer stringBuffer, int i) {
            this.mRule.appendTo(stringBuffer, i);
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            int i = calendar.get(11);
            if (i == 0) {
                i = calendar.getMaximum(11) + FastDatePrinter.LONG;
            }
            this.mRule.appendTo(stringBuffer, i);
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }
    }

    private static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE;

        static {
            INSTANCE = new TwoDigitMonthField();
        }

        TwoDigitMonthField() {
        }

        public final void appendTo(StringBuffer stringBuffer, int i) {
            stringBuffer.append((char) ((i / 10) + 48));
            stringBuffer.append((char) ((i % 10) + 48));
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            appendTo(stringBuffer, calendar.get(FastDatePrinter.MEDIUM) + FastDatePrinter.LONG);
        }

        public int estimateLength() {
            return FastDatePrinter.MEDIUM;
        }
    }

    private static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int i) {
            this.mField = i;
        }

        public final void appendTo(StringBuffer stringBuffer, int i) {
            if (i < 100) {
                stringBuffer.append((char) ((i / 10) + 48));
                stringBuffer.append((char) ((i % 10) + 48));
                return;
            }
            stringBuffer.append(Integer.toString(i));
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            appendTo(stringBuffer, calendar.get(this.mField));
        }

        public int estimateLength() {
            return FastDatePrinter.MEDIUM;
        }
    }

    private static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE;

        static {
            INSTANCE = new TwoDigitYearField();
        }

        TwoDigitYearField() {
        }

        public final void appendTo(StringBuffer stringBuffer, int i) {
            stringBuffer.append((char) ((i / 10) + 48));
            stringBuffer.append((char) ((i % 10) + 48));
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            appendTo(stringBuffer, calendar.get(FastDatePrinter.LONG) % 100);
        }

        public int estimateLength() {
            return FastDatePrinter.MEDIUM;
        }
    }

    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE;

        static {
            INSTANCE = new UnpaddedMonthField();
        }

        UnpaddedMonthField() {
        }

        public final void appendTo(StringBuffer stringBuffer, int i) {
            if (i < 10) {
                stringBuffer.append((char) (i + 48));
                return;
            }
            stringBuffer.append((char) ((i / 10) + 48));
            stringBuffer.append((char) ((i % 10) + 48));
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            appendTo(stringBuffer, calendar.get(FastDatePrinter.MEDIUM) + FastDatePrinter.LONG);
        }

        public int estimateLength() {
            return FastDatePrinter.MEDIUM;
        }
    }

    private static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        UnpaddedNumberField(int i) {
            this.mField = i;
        }

        public final void appendTo(StringBuffer stringBuffer, int i) {
            if (i < 10) {
                stringBuffer.append((char) (i + 48));
            } else if (i < 100) {
                stringBuffer.append((char) ((i / 10) + 48));
                stringBuffer.append((char) ((i % 10) + 48));
            } else {
                stringBuffer.append(Integer.toString(i));
            }
        }

        public void appendTo(StringBuffer stringBuffer, Calendar calendar) {
            appendTo(stringBuffer, calendar.get(this.mField));
        }

        public int estimateLength() {
            return 4;
        }
    }

    static {
        cTimeZoneDisplayCache = new ConcurrentHashMap(7);
    }

    protected FastDatePrinter(String str, TimeZone timeZone, Locale locale) {
        this.mPattern = str;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private String applyRulesToString(Calendar calendar) {
        return applyRules(calendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    static String getTimeZoneDisplay(TimeZone timeZone, boolean z, int i, Locale locale) {
        TimeZoneDisplayKey timeZoneDisplayKey = new TimeZoneDisplayKey(timeZone, z, i, locale);
        String str = (String) cTimeZoneDisplayCache.get(timeZoneDisplayKey);
        if (str != null) {
            return str;
        }
        String displayName = timeZone.getDisplayName(z, i, locale);
        str = (String) cTimeZoneDisplayCache.putIfAbsent(timeZoneDisplayKey, displayName);
        return str != null ? str : displayName;
    }

    private void init() {
        List parsePattern = parsePattern();
        this.mRules = (Rule[]) parsePattern.toArray(new Rule[parsePattern.size()]);
        int i = FULL;
        int length = this.mRules.length;
        while (true) {
            length--;
            if (length >= 0) {
                i += this.mRules[length].estimateLength();
            } else {
                this.mMaxLengthEstimate = i;
                return;
            }
        }
    }

    private GregorianCalendar newCalendar() {
        return new GregorianCalendar(this.mTimeZone, this.mLocale);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        objectInputStream.defaultReadObject();
        init();
    }

    protected StringBuffer applyRules(Calendar calendar, StringBuffer stringBuffer) {
        Rule[] ruleArr = this.mRules;
        int length = ruleArr.length;
        for (int i = FULL; i < length; i += LONG) {
            ruleArr[i].appendTo(stringBuffer, calendar);
        }
        return stringBuffer;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter fastDatePrinter = (FastDatePrinter) obj;
        return this.mPattern.equals(fastDatePrinter.mPattern) && this.mTimeZone.equals(fastDatePrinter.mTimeZone) && this.mLocale.equals(fastDatePrinter.mLocale);
    }

    public String format(long j) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTimeInMillis(j);
        return applyRulesToString(newCalendar);
    }

    public String format(Calendar calendar) {
        return format(calendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
    }

    public String format(Date date) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTime(date);
        return applyRulesToString(newCalendar);
    }

    public StringBuffer format(long j, StringBuffer stringBuffer) {
        return format(new Date(j), stringBuffer);
    }

    public StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        if (obj instanceof Date) {
            return format((Date) obj, stringBuffer);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, stringBuffer);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), stringBuffer);
        }
        throw new IllegalArgumentException("Unknown class: " + (obj == null ? "<null>" : obj.getClass().getName()));
    }

    public StringBuffer format(Calendar calendar, StringBuffer stringBuffer) {
        return applyRules(calendar, stringBuffer);
    }

    public StringBuffer format(Date date, StringBuffer stringBuffer) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTime(date);
        return applyRules(newCalendar, stringBuffer);
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public String getPattern() {
        return this.mPattern;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    public int hashCode() {
        return this.mPattern.hashCode() + ((this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)) * 13);
    }

    protected List<Rule> parsePattern() {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(this.mLocale);
        List<Rule> arrayList = new ArrayList();
        String[] eras = dateFormatSymbols.getEras();
        String[] months = dateFormatSymbols.getMonths();
        String[] shortMonths = dateFormatSymbols.getShortMonths();
        String[] weekdays = dateFormatSymbols.getWeekdays();
        String[] shortWeekdays = dateFormatSymbols.getShortWeekdays();
        String[] amPmStrings = dateFormatSymbols.getAmPmStrings();
        int length = this.mPattern.length();
        int[] iArr = new int[LONG];
        int i = FULL;
        while (i < length) {
            iArr[FULL] = i;
            String parseToken = parseToken(this.mPattern, iArr);
            int i2 = iArr[FULL];
            i = parseToken.length();
            if (i == 0) {
                return arrayList;
            }
            Object stringLiteral;
            switch (parseToken.charAt(FULL)) {
                case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                    parseToken = parseToken.substring(LONG);
                    if (parseToken.length() != LONG) {
                        stringLiteral = new StringLiteral(parseToken);
                        break;
                    }
                    stringLiteral = new CharacterLiteral(parseToken.charAt(FULL));
                    break;
                case 'D':
                    stringLiteral = selectNumberRule(6, i);
                    break;
                case 'E':
                    TextField textField = new TextField(7, i < 4 ? shortWeekdays : weekdays);
                    break;
                case 'F':
                    stringLiteral = selectNumberRule(8, i);
                    break;
                case 'G':
                    stringLiteral = new TextField(FULL, eras);
                    break;
                case XtraBox.MP4_XTRA_BT_GUID /*72*/:
                    stringLiteral = selectNumberRule(11, i);
                    break;
                case 'K':
                    stringLiteral = selectNumberRule(10, i);
                    break;
                case 'M':
                    if (i < 4) {
                        if (i != SHORT) {
                            if (i != MEDIUM) {
                                stringLiteral = UnpaddedMonthField.INSTANCE;
                                break;
                            }
                            stringLiteral = TwoDigitMonthField.INSTANCE;
                            break;
                        }
                        stringLiteral = new TextField(MEDIUM, shortMonths);
                        break;
                    }
                    stringLiteral = new TextField(MEDIUM, months);
                    break;
                case 'S':
                    stringLiteral = selectNumberRule(14, i);
                    break;
                case 'W':
                    stringLiteral = selectNumberRule(4, i);
                    break;
                case 'Z':
                    if (i != LONG) {
                        stringLiteral = TimeZoneNumberRule.INSTANCE_COLON;
                        break;
                    }
                    stringLiteral = TimeZoneNumberRule.INSTANCE_NO_COLON;
                    break;
                case 'a':
                    stringLiteral = new TextField(9, amPmStrings);
                    break;
                case 'd':
                    stringLiteral = selectNumberRule(5, i);
                    break;
                case 'h':
                    TwelveHourField twelveHourField = new TwelveHourField(selectNumberRule(10, i));
                    break;
                case 'k':
                    TwentyFourHourField twentyFourHourField = new TwentyFourHourField(selectNumberRule(11, i));
                    break;
                case 'm':
                    stringLiteral = selectNumberRule(12, i);
                    break;
                case 's':
                    stringLiteral = selectNumberRule(13, i);
                    break;
                case 'w':
                    stringLiteral = selectNumberRule(SHORT, i);
                    break;
                case 'y':
                    if (i != MEDIUM) {
                        if (i < 4) {
                            i = 4;
                        }
                        stringLiteral = selectNumberRule(LONG, i);
                        break;
                    }
                    stringLiteral = TwoDigitYearField.INSTANCE;
                    break;
                case 'z':
                    if (i < 4) {
                        stringLiteral = new TimeZoneNameRule(this.mTimeZone, this.mLocale, FULL);
                        break;
                    }
                    stringLiteral = new TimeZoneNameRule(this.mTimeZone, this.mLocale, LONG);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal pattern component: " + parseToken);
            }
            arrayList.add(stringLiteral);
            i = i2 + LONG;
        }
        return arrayList;
    }

    protected String parseToken(String str, int[] iArr) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = iArr[FULL];
        int length = str.length();
        char charAt = str.charAt(i);
        if ((charAt < 'A' || charAt > 'Z') && (charAt < 'a' || charAt > 'z')) {
            stringBuilder.append('\'');
            int i2 = FULL;
            while (i < length) {
                char charAt2 = str.charAt(i);
                if (charAt2 != '\'') {
                    if (i2 == 0 && ((charAt2 >= 'A' && charAt2 <= 'Z') || (charAt2 >= 'a' && charAt2 <= 'z'))) {
                        i--;
                        break;
                    }
                    stringBuilder.append(charAt2);
                } else if (i + LONG >= length || str.charAt(i + LONG) != '\'') {
                    i2 = i2 == 0 ? LONG : FULL;
                } else {
                    i += LONG;
                    stringBuilder.append(charAt2);
                }
                i += LONG;
            }
        } else {
            stringBuilder.append(charAt);
            while (i + LONG < length && str.charAt(i + LONG) == charAt) {
                stringBuilder.append(charAt);
                i += LONG;
            }
        }
        iArr[FULL] = i;
        return stringBuilder.toString();
    }

    protected NumberRule selectNumberRule(int i, int i2) {
        switch (i2) {
            case LONG /*1*/:
                return new UnpaddedNumberField(i);
            case MEDIUM /*2*/:
                return new TwoDigitNumberField(i);
            default:
                return new PaddedNumberField(i, i2);
        }
    }

    public String toString() {
        return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
    }
}
