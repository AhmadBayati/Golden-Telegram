package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a;

import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.a.b */
public class PersianCalendar extends GregorianCalendar {
    private int f2118a;
    private int f2119b;
    private int f2120c;
    private String f2121d;

    public PersianCalendar() {
        this.f2121d = "/";
        setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private long m2047a(long j) {
        return ((86400000 * j) - 210866803200000L) + PersianCalendarUtils.m2058a((double) (getTimeInMillis() - -210866803200000L), 8.64E7d);
    }

    private String m2048a(int i) {
        return i < 9 ? "0" + i : String.valueOf(i);
    }

    protected void m2049a() {
        long a = PersianCalendarUtils.m2059a(((long) Math.floor((double) (getTimeInMillis() - -210866803200000L))) / 86400000);
        long j = a >> 16;
        int i = ((int) (65280 & a)) >> 8;
        int i2 = (int) (a & 255);
        if (j <= 0) {
            j--;
        }
        this.f2118a = (int) j;
        this.f2119b = i;
        this.f2120c = i2;
    }

    public void m2050a(int i, int i2, int i3) {
        int i4 = i2 + 1;
        this.f2118a = i;
        this.f2119b = i4;
        this.f2120c = i3;
        setTimeInMillis(m2047a(PersianCalendarUtils.m2060a(this.f2118a > 0 ? (long) this.f2118a : (long) (this.f2118a + 1), this.f2119b - 1, this.f2120c)));
    }

    public int m2051b() {
        return this.f2118a;
    }

    public int m2052c() {
        return this.f2119b;
    }

    public String m2053d() {
        Locale locale = LocaleController.getInstance().currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        return language.toLowerCase().equals("fa") ? ShamsiCalendar.getShamsiMonth(this.f2119b + 1) : language.toLowerCase().equals("ku") ? ShamsiCalendar.getShamsiMonthKur(this.f2119b + 1) : ShamsiCalendar.getShamsiMonthEn(this.f2119b + 1);
    }

    public int m2054e() {
        return this.f2120c;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String m2055f() {
        Locale locale = LocaleController.getInstance().currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.US);
        switch (get(7)) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return language.toLowerCase().equals("fa") ? ShamsiCalendar.shamsiWeekDays[1] : language.toLowerCase().equals("ku") ? ShamsiCalendar.shamsiWeekDaysKu[1] : simpleDateFormat.format(getTime());
            case VideoPlayer.STATE_PREPARING /*2*/:
                return language.toLowerCase().equals("fa") ? ShamsiCalendar.shamsiWeekDays[2] : language.toLowerCase().equals("ku") ? ShamsiCalendar.shamsiWeekDaysKu[2] : simpleDateFormat.format(getTime());
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return language.toLowerCase().equals("fa") ? ShamsiCalendar.shamsiWeekDays[3] : language.toLowerCase().equals("ku") ? ShamsiCalendar.shamsiWeekDaysKu[3] : simpleDateFormat.format(getTime());
            case VideoPlayer.STATE_READY /*4*/:
                return language.toLowerCase().equals("fa") ? ShamsiCalendar.shamsiWeekDays[4] : language.toLowerCase().equals("ku") ? ShamsiCalendar.shamsiWeekDaysKu[4] : simpleDateFormat.format(getTime());
            case VideoPlayer.STATE_ENDED /*5*/:
                return language.toLowerCase().equals("fa") ? ShamsiCalendar.shamsiWeekDays[5] : language.toLowerCase().equals("ku") ? ShamsiCalendar.shamsiWeekDaysKu[5] : simpleDateFormat.format(getTime());
            case Method.PATCH /*7*/:
                return language.toLowerCase().equals("fa") ? ShamsiCalendar.shamsiWeekDays[0] : language.toLowerCase().equals("ku") ? ShamsiCalendar.shamsiWeekDaysKu[0] : simpleDateFormat.format(getTime());
            default:
                return language.toLowerCase().equals("fa") ? ShamsiCalendar.shamsiWeekDays[6] : language.toLowerCase().equals("ku") ? ShamsiCalendar.shamsiWeekDaysKu[6] : simpleDateFormat.format(getTime());
        }
    }

    public String m2056g() {
        return m2055f() + "  " + this.f2120c + "  " + m2053d() + "  " + this.f2118a;
    }

    public String m2057h() {
        return TtmlNode.ANONYMOUS_REGION_ID + m2048a(this.f2118a) + this.f2121d + m2048a(m2052c() + 1) + this.f2121d + m2048a(this.f2120c);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public void set(int i, int i2) {
        super.set(i, i2);
        m2049a();
    }

    public void setTimeInMillis(long j) {
        super.setTimeInMillis(j);
        m2049a();
    }

    public void setTimeZone(TimeZone timeZone) {
        super.setTimeZone(timeZone);
        m2049a();
    }

    public String toString() {
        String gregorianCalendar = super.toString();
        return gregorianCalendar.substring(0, gregorianCalendar.length() - 1) + ",PersianDate=" + m2057h() + "]";
    }
}
