package com.hanista.mobogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.support.v4.view.PointerIconCompat;
import android.text.format.DateFormat;
import android.util.Xml;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.time.FastDateFormat;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC.TL_userEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastMonth;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastWeek;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusRecently;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import com.hanista.mobogram.util.shamsicalendar.ShamsiDate;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;

public class LocaleController {
    private static volatile LocaleController Instance = null;
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    private static boolean is24HourFormat;
    public static boolean isRTL;
    public static int nameDisplayOrder;
    private HashMap<String, PluralRules> allRules;
    private boolean changingConfiguration;
    public FastDateFormat chatDate;
    public FastDateFormat chatFullDate;
    public Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    private LocaleInfo defaultLocalInfo;
    public FastDateFormat formatterDay;
    public FastDateFormat formatterEnDay;
    public FastDateFormat formatterMonth;
    public FastDateFormat formatterMonthYear;
    public FastDateFormat formatterWeek;
    public FastDateFormat formatterYear;
    public FastDateFormat formatterYearMax;
    private String languageOverride;
    public HashMap<String, LocaleInfo> languagesDict;
    private HashMap<String, String> localeValues;
    private ArrayList<LocaleInfo> otherLanguages;
    public ArrayList<LocaleInfo> sortedLanguages;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;

    /* renamed from: com.hanista.mobogram.messenger.LocaleController.1 */
    class C04541 implements Comparator<LocaleInfo> {
        C04541() {
        }

        public int compare(LocaleInfo localeInfo, LocaleInfo localeInfo2) {
            return localeInfo.name.compareTo(localeInfo2.name);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.LocaleController.2 */
    class C04552 implements Comparator<LocaleInfo> {
        C04552() {
        }

        public int compare(LocaleInfo localeInfo, LocaleInfo localeInfo2) {
            return localeInfo.shortName == null ? -1 : localeInfo2.shortName == null ? LocaleController.QUANTITY_ZERO : localeInfo.name.compareTo(localeInfo2.name);
        }
    }

    public static class LocaleInfo {
        public String name;
        public String nameEnglish;
        public String pathToFile;
        public String shortName;

        public static LocaleInfo createWithString(String str) {
            if (str == null || str.length() == 0) {
                return null;
            }
            String[] split = str.split("\\|");
            if (split.length != LocaleController.QUANTITY_TWO) {
                return null;
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.name = split[LocaleController.QUANTITY_OTHER];
            localeInfo.nameEnglish = split[LocaleController.QUANTITY_ZERO];
            localeInfo.shortName = split[LocaleController.QUANTITY_ONE];
            localeInfo.pathToFile = split[3];
            return localeInfo;
        }

        public String getSaveString() {
            return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile;
        }
    }

    public static abstract class PluralRules {
        abstract int quantityForNumber(int i);
    }

    public static class PluralRules_Arabic extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            return i == 0 ? LocaleController.QUANTITY_ZERO : i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : i == LocaleController.QUANTITY_ONE ? LocaleController.QUANTITY_TWO : (i2 < 3 || i2 > 10) ? (i2 < 11 || i2 > 99) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_MANY : LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Balkan extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            return (i3 != LocaleController.QUANTITY_ZERO || i2 == 11) ? (i3 < LocaleController.QUANTITY_ONE || i3 > LocaleController.QUANTITY_TWO || (i2 >= 12 && i2 <= 14)) ? (i3 == 0 || ((i3 >= 5 && i3 <= 9) || (i2 >= 11 && i2 <= 14))) ? LocaleController.QUANTITY_MANY : LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_FEW : LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Breton extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 0 ? LocaleController.QUANTITY_ZERO : i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : i == LocaleController.QUANTITY_ONE ? LocaleController.QUANTITY_TWO : i == 3 ? LocaleController.QUANTITY_FEW : i == 6 ? LocaleController.QUANTITY_MANY : LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        public int quantityForNumber(int i) {
            return i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : (i < LocaleController.QUANTITY_ONE || i > LocaleController.QUANTITY_TWO) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_French extends PluralRules {
        public int quantityForNumber(int i) {
            return (i < 0 || i >= LocaleController.QUANTITY_ONE) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 0 ? LocaleController.QUANTITY_ZERO : (i <= 0 || i >= LocaleController.QUANTITY_ONE) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Latvian extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 0 ? LocaleController.QUANTITY_ZERO : (i % 10 != LocaleController.QUANTITY_ZERO || i % 100 == 11) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            return (i3 != LocaleController.QUANTITY_ZERO || (i2 >= 11 && i2 <= 19)) ? (i3 < LocaleController.QUANTITY_ONE || i3 > 9 || (i2 >= 11 && i2 <= 19)) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_FEW : LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        public int quantityForNumber(int i) {
            return (i % 10 != LocaleController.QUANTITY_ZERO || i == 11) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            return i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : (i == 0 || (i2 >= LocaleController.QUANTITY_ONE && i2 <= 10)) ? LocaleController.QUANTITY_FEW : (i2 < 11 || i2 > 19) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_MANY;
        }
    }

    public static class PluralRules_None extends PluralRules {
        public int quantityForNumber(int i) {
            return LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_One extends PluralRules {
        public int quantityForNumber(int i) {
            return i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Persian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            return i == 0 ? LocaleController.QUANTITY_ZERO : i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : i == LocaleController.QUANTITY_ONE ? LocaleController.QUANTITY_TWO : (i2 < 3 || i2 > 10) ? (i2 < 11 || i2 > 99) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_MANY : LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            return i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : (i3 < LocaleController.QUANTITY_ONE || i3 > LocaleController.QUANTITY_TWO || ((i2 >= 12 && i2 <= 14) || (i2 >= 22 && i2 <= 24))) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            return i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : (i == 0 || (i2 >= LocaleController.QUANTITY_ZERO && i2 <= 19)) ? LocaleController.QUANTITY_FEW : LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Slovenian extends PluralRules {
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            return i2 == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : i2 == LocaleController.QUANTITY_ONE ? LocaleController.QUANTITY_TWO : (i2 < 3 || i2 > LocaleController.QUANTITY_TWO) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_FEW;
        }
    }

    public static class PluralRules_Tachelhit extends PluralRules {
        public int quantityForNumber(int i) {
            return (i < 0 || i > LocaleController.QUANTITY_ZERO) ? (i < LocaleController.QUANTITY_ONE || i > 10) ? LocaleController.QUANTITY_OTHER : LocaleController.QUANTITY_FEW : LocaleController.QUANTITY_ONE;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        public int quantityForNumber(int i) {
            return i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : i == LocaleController.QUANTITY_ONE ? LocaleController.QUANTITY_TWO : LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        public int quantityForNumber(int i) {
            return i == 0 ? LocaleController.QUANTITY_ZERO : i == LocaleController.QUANTITY_ZERO ? LocaleController.QUANTITY_ONE : i == LocaleController.QUANTITY_ONE ? LocaleController.QUANTITY_TWO : i == 3 ? LocaleController.QUANTITY_FEW : i == 6 ? LocaleController.QUANTITY_MANY : LocaleController.QUANTITY_OTHER;
        }
    }

    public static class PluralRules_Zero extends PluralRules {
        public int quantityForNumber(int i) {
            return (i == 0 || i == LocaleController.QUANTITY_ZERO) ? LocaleController.QUANTITY_ONE : LocaleController.QUANTITY_OTHER;
        }
    }

    private class TimeZoneChangedReceiver extends BroadcastReceiver {

        /* renamed from: com.hanista.mobogram.messenger.LocaleController.TimeZoneChangedReceiver.1 */
        class C04561 implements Runnable {
            C04561() {
            }

            public void run() {
                if (!LocaleController.this.formatterMonth.getTimeZone().equals(TimeZone.getDefault())) {
                    LocaleController.getInstance().recreateFormatters();
                }
            }
        }

        private TimeZoneChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new C04561());
        }
    }

    static {
        isRTL = false;
        nameDisplayOrder = QUANTITY_ZERO;
        is24HourFormat = false;
        Instance = null;
    }

    public LocaleController() {
        boolean z = true;
        this.allRules = new HashMap();
        this.localeValues = new HashMap();
        this.changingConfiguration = false;
        this.sortedLanguages = new ArrayList();
        this.languagesDict = new HashMap();
        this.otherLanguages = new ArrayList();
        addRules(new String[]{"bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast"}, new PluralRules_One());
        String[] strArr = new String[QUANTITY_ONE];
        strArr[QUANTITY_OTHER] = "cs";
        strArr[QUANTITY_ZERO] = "sk";
        addRules(strArr, new PluralRules_Czech());
        addRules(new String[]{"ff", "fr", "kab"}, new PluralRules_French());
        addRules(new String[]{"hr", "ru", "sr", "uk", "be", "bs", "sh"}, new PluralRules_Balkan());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "lv";
        addRules(strArr, new PluralRules_Latvian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "lt";
        addRules(strArr, new PluralRules_Lithuanian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "pl";
        addRules(strArr, new PluralRules_Polish());
        strArr = new String[QUANTITY_ONE];
        strArr[QUANTITY_OTHER] = "ro";
        strArr[QUANTITY_ZERO] = "mo";
        addRules(strArr, new PluralRules_Romanian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "sl";
        addRules(strArr, new PluralRules_Slovenian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "ar";
        addRules(strArr, new PluralRules_Arabic());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "fa";
        addRules(strArr, new PluralRules_Persian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "ku";
        addRules(strArr, new PluralRules_Persian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "mk";
        addRules(strArr, new PluralRules_Macedonian());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "cy";
        addRules(strArr, new PluralRules_Welsh());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = TtmlNode.TAG_BR;
        addRules(strArr, new PluralRules_Breton());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "lag";
        addRules(strArr, new PluralRules_Langi());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "shi";
        addRules(strArr, new PluralRules_Tachelhit());
        strArr = new String[QUANTITY_ZERO];
        strArr[QUANTITY_OTHER] = "mt";
        addRules(strArr, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", TtmlNode.ATTR_ID, "jv", "ka", "km", "kn", "ms", "th"}, new PluralRules_None());
        LocaleInfo localeInfo = new LocaleInfo();
        localeInfo.name = "English";
        localeInfo.nameEnglish = "English";
        localeInfo.shortName = "en";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Italiano";
        localeInfo.nameEnglish = "Italian";
        localeInfo.shortName = "it";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Espa\u00f1ol";
        localeInfo.nameEnglish = "Spanish";
        localeInfo.shortName = "es";
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Deutsch";
        localeInfo.nameEnglish = "German";
        localeInfo.shortName = "de";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Nederlands";
        localeInfo.nameEnglish = "Dutch";
        localeInfo.shortName = "nl";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u067e\u0627\u0631\u0633\u06cc";
        localeInfo.nameEnglish = "Persian";
        localeInfo.shortName = "fa";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u06a9\u0648\u0631\u062f\u06cc (\u0646\u0627\u0648\u0647\u200c\u0646\u062f\u06cc)";
        localeInfo.nameEnglish = "Kurdish";
        localeInfo.shortName = "ku";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\u0627\u0644\u0639\u0631\u0628\u064a\u0629";
        localeInfo.nameEnglish = "Arabic";
        localeInfo.shortName = "ar";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Portugu\u00eas (Brasil)";
        localeInfo.nameEnglish = "Portuguese (Brazil)";
        localeInfo.shortName = "pt_BR";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "Portugu\u00eas (Portugal)";
        localeInfo.nameEnglish = "Portuguese (Portugal)";
        localeInfo.shortName = "pt_PT";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        localeInfo = new LocaleInfo();
        localeInfo.name = "\ud55c\uad6d\uc5b4";
        localeInfo.nameEnglish = "Korean";
        localeInfo.shortName = "ko";
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        loadOtherLanguages();
        Iterator it = this.otherLanguages.iterator();
        while (it.hasNext()) {
            localeInfo = (LocaleInfo) it.next();
            this.sortedLanguages.add(localeInfo);
            this.languagesDict.put(localeInfo.shortName, localeInfo);
        }
        Collections.sort(this.sortedLanguages, new C04541());
        localeInfo = new LocaleInfo();
        this.defaultLocalInfo = localeInfo;
        localeInfo.name = "System default";
        localeInfo.nameEnglish = "System default";
        localeInfo.shortName = null;
        localeInfo.pathToFile = null;
        this.sortedLanguages.add(QUANTITY_OTHER, localeInfo);
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        try {
            String string = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", QUANTITY_OTHER).getString("language", null);
            if (string != null) {
                localeInfo = (LocaleInfo) this.languagesDict.get(string);
                if (localeInfo == null) {
                    z = false;
                }
            } else {
                z = false;
                localeInfo = null;
            }
            if (localeInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                localeInfo = (LocaleInfo) this.languagesDict.get(this.systemDefaultLocale.getLanguage());
            }
            if (localeInfo == null) {
                localeInfo = (LocaleInfo) this.languagesDict.get(getLocaleString(this.systemDefaultLocale));
            }
            if (localeInfo == null) {
                localeInfo = (LocaleInfo) this.languagesDict.get("en");
            }
            applyLanguage(localeInfo, z);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
    }

    private void addRules(String[] strArr, PluralRules pluralRules) {
        int length = strArr.length;
        for (int i = QUANTITY_OTHER; i < length; i += QUANTITY_ZERO) {
            this.allRules.put(strArr[i], pluralRules);
        }
    }

    private FastDateFormat createFormatter(Locale locale, String str, String str2) {
        if (str == null || str.length() == 0) {
            str = str2;
        }
        try {
            return FastDateFormat.getInstance(str, locale);
        } catch (Exception e) {
            return FastDateFormat.getInstance(str2, locale);
        }
    }

    public static String formatDate(long j) {
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(QUANTITY_ZERO);
            instance.setTimeInMillis(j * 1000);
            int i3 = instance.get(6);
            int i4 = instance.get(QUANTITY_ZERO);
            return (i3 == i && i2 == i4) ? getInstance().formatterDay.format(new Date(j * 1000)) : (i3 + QUANTITY_ZERO == i && i2 == i4) ? getString("Yesterday", C0338R.string.Yesterday) : i2 == i4 ? getInstance().formatterMonth.format(new Date(j * 1000)) : getInstance().formatterYear.format(new Date(j * 1000));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long j) {
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(QUANTITY_ZERO);
            instance.setTimeInMillis(j * 1000);
            int i3 = instance.get(6);
            int i4 = instance.get(QUANTITY_ZERO);
            Object[] objArr;
            if (i3 == i && i2 == i4) {
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getString("TodayAt", C0338R.string.TodayAt);
                objArr[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                return String.format("%s %s", objArr);
            } else if (i3 + QUANTITY_ZERO == i && i2 == i4) {
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getString("YesterdayAt", C0338R.string.YesterdayAt);
                objArr[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                return String.format("%s %s", objArr);
            } else if (i2 == i4) {
                r2 = new Object[QUANTITY_ONE];
                r2[QUANTITY_OTHER] = getInstance().formatterMonth.format(new Date(j * 1000));
                r2[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                return formatString("formatDateAtTime", C0338R.string.formatDateAtTime, r2);
            } else {
                r2 = new Object[QUANTITY_ONE];
                r2[QUANTITY_OTHER] = getInstance().formatterYear.format(new Date(j * 1000));
                r2[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                return formatString("formatDateAtTime", C0338R.string.formatDateAtTime, r2);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return "LOC_ERR";
        }
    }

    public static String formatDateChat(long j) {
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(QUANTITY_ZERO);
            instance.setTimeInMillis(j * 1000);
            int i2 = instance.get(QUANTITY_ZERO);
            Locale locale = getInstance().currentLocale;
            if (locale == null) {
                locale = Locale.getDefault();
            }
            String language = locale.getLanguage();
            if (!language.toLowerCase().equals("fa") && !language.toLowerCase().equals("ku") && !MoboConstants.f1337d) {
                return i == i2 ? getInstance().chatDate.format(j * 1000) : getInstance().chatFullDate.format(j * 1000);
            } else {
                ShamsiDate dateToShamsi = ShamsiCalendar.dateToShamsi(new Date(j * 1000));
                if (i != i2) {
                    return dateToShamsi.toDateString();
                }
                StringBuilder append = new StringBuilder().append(dateToShamsi.getDay()).append(" ");
                language = language.toLowerCase().equals("fa") ? ShamsiCalendar.getShamsiMonth(dateToShamsi.getMonth()) : language.toLowerCase().equals("ku") ? ShamsiCalendar.getShamsiMonthKur(dateToShamsi.getMonth()) : ShamsiCalendar.getShamsiMonthEn(dateToShamsi.getMonth());
                return append.append(language).toString();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDateOnline(long j) {
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        if (language == null) {
            language = "en";
        }
        int i;
        int i2;
        int i3;
        Object[] objArr;
        if (language.toLowerCase().equals("fa") || language.toLowerCase().equals("ku") || MoboConstants.f1337d) {
            try {
                Calendar instance = Calendar.getInstance();
                i = instance.get(6);
                i2 = instance.get(QUANTITY_ZERO);
                instance.setTimeInMillis(j * 1000);
                int i4 = instance.get(6);
                i3 = instance.get(QUANTITY_ZERO);
                ShamsiDate dateToShamsi = ShamsiCalendar.dateToShamsi(new Date(j * 1000));
                if (i4 == i && i2 == i3) {
                    return String.format("%s %s %s", new Object[]{getString("LastSeen", C0338R.string.LastSeen), getString("TodayAt", C0338R.string.TodayAt), getInstance().formatterDay.format(new Date(j * 1000))});
                } else if (i4 + QUANTITY_ZERO == i && i2 == i3) {
                    return String.format("%s %s %s", new Object[]{getString("LastSeen", C0338R.string.LastSeen), getString("YesterdayAt", C0338R.string.YesterdayAt), getInstance().formatterDay.format(new Date(j * 1000))});
                } else if (i2 == i3) {
                    StringBuilder append = new StringBuilder().append(dateToShamsi.getDay()).append(" ");
                    language = language.toLowerCase().equals("fa") ? ShamsiCalendar.getShamsiMonth(dateToShamsi.getMonth()) : language.toLowerCase().equals("ku") ? ShamsiCalendar.getShamsiMonthKur(dateToShamsi.getMonth()) : ShamsiCalendar.getShamsiMonthEn(dateToShamsi.getMonth());
                    Object[] objArr2 = new Object[QUANTITY_ONE];
                    objArr2[QUANTITY_OTHER] = append.append(language).toString();
                    objArr2[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                    language = formatString("formatDateAtTime", C0338R.string.formatDateAtTime, objArr2);
                    objArr = new Object[QUANTITY_ONE];
                    objArr[QUANTITY_OTHER] = getString("LastSeenDate", C0338R.string.LastSeenDate);
                    objArr[QUANTITY_ZERO] = language;
                    return String.format("%s %s", objArr);
                } else {
                    objArr = new Object[QUANTITY_ONE];
                    objArr[QUANTITY_OTHER] = dateToShamsi.toDateString();
                    objArr[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                    language = formatString("formatDateAtTime", C0338R.string.formatDateAtTime, objArr);
                    objArr = new Object[QUANTITY_ONE];
                    objArr[QUANTITY_OTHER] = getString("LastSeenDate", C0338R.string.LastSeenDate);
                    objArr[QUANTITY_ZERO] = language;
                    return String.format("%s %s", objArr);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return "LOC_ERR";
            }
        }
        try {
            Calendar instance2 = Calendar.getInstance();
            i3 = instance2.get(6);
            i = instance2.get(QUANTITY_ZERO);
            instance2.setTimeInMillis(j * 1000);
            i2 = instance2.get(6);
            int i5 = instance2.get(QUANTITY_ZERO);
            if (i2 == i3 && i == i5) {
                return String.format("%s %s %s", new Object[]{getString("LastSeen", C0338R.string.LastSeen), getString("TodayAt", C0338R.string.TodayAt), getInstance().formatterDay.format(new Date(j * 1000))});
            } else if (i2 + QUANTITY_ZERO == i3 && i == i5) {
                return String.format("%s %s %s", new Object[]{getString("LastSeen", C0338R.string.LastSeen), getString("YesterdayAt", C0338R.string.YesterdayAt), getInstance().formatterDay.format(new Date(j * 1000))});
            } else if (i == i5) {
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getInstance().formatterMonth.format(new Date(j * 1000));
                objArr[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                language = formatString("formatDateAtTime", C0338R.string.formatDateAtTime, objArr);
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getString("LastSeenDate", C0338R.string.LastSeenDate);
                objArr[QUANTITY_ZERO] = language;
                return String.format("%s %s", objArr);
            } else {
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getInstance().formatterYear.format(new Date(j * 1000));
                objArr[QUANTITY_ZERO] = getInstance().formatterDay.format(new Date(j * 1000));
                language = formatString("formatDateAtTime", C0338R.string.formatDateAtTime, objArr);
                objArr = new Object[QUANTITY_ONE];
                objArr[QUANTITY_OTHER] = getString("LastSeenDate", C0338R.string.LastSeenDate);
                objArr[QUANTITY_ZERO] = language;
                return String.format("%s %s", objArr);
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
            return "LOC_ERR";
        }
    }

    public static String formatPluralString(String str, int i) {
        if (str == null || str.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + str;
        }
        String str2 = str + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(i));
        int identifier = ApplicationLoader.applicationContext.getResources().getIdentifier(str2, "string", ApplicationLoader.applicationContext.getPackageName());
        Object[] objArr = new Object[QUANTITY_ZERO];
        objArr[QUANTITY_OTHER] = Integer.valueOf(i);
        return formatString(str2, identifier, objArr);
    }

    public static String formatShortNumber(int i, int[] iArr) {
        if (MoboConstants.f1320M) {
            return new DecimalFormat("#,###").format(new Long((long) i));
        }
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        int i2 = QUANTITY_OTHER;
        while (i / PointerIconCompat.TYPE_DEFAULT > 0) {
            str = str + "K";
            i2 = (i % PointerIconCompat.TYPE_DEFAULT) / 100;
            i /= PointerIconCompat.TYPE_DEFAULT;
        }
        if (iArr != null) {
            double d = ((double) i) + (((double) i2) / 10.0d);
            for (int i3 = QUANTITY_OTHER; i3 < str.length(); i3 += QUANTITY_ZERO) {
                d *= 1000.0d;
            }
            iArr[QUANTITY_OTHER] = (int) d;
        }
        Object[] objArr;
        if (i2 == 0 || str.length() <= 0) {
            if (str.length() == QUANTITY_ONE) {
                Object[] objArr2 = new Object[QUANTITY_ZERO];
                objArr2[QUANTITY_OTHER] = Integer.valueOf(i);
                return String.format(Locale.US, "%dM", objArr2);
            }
            objArr = new Object[QUANTITY_ONE];
            objArr[QUANTITY_OTHER] = Integer.valueOf(i);
            objArr[QUANTITY_ZERO] = str;
            return String.format(Locale.US, "%d%s", objArr);
        } else if (str.length() == QUANTITY_ONE) {
            objArr = new Object[QUANTITY_ONE];
            objArr[QUANTITY_OTHER] = Integer.valueOf(i);
            objArr[QUANTITY_ZERO] = Integer.valueOf(i2);
            return String.format(Locale.US, "%d.%dM", objArr);
        } else {
            return String.format(Locale.US, "%d.%d%s", new Object[]{Integer.valueOf(i), Integer.valueOf(i2), str});
        }
    }

    public static String formatString(String str, int i, Object... objArr) {
        try {
            String str2 = (String) getInstance().localeValues.get(str);
            if (str2 == null) {
                str2 = ApplicationLoader.applicationContext.getString(i);
            }
            return getInstance().currentLocale != null ? String.format(getInstance().currentLocale, str2, objArr) : String.format(str2, objArr);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatStringSimple(String str, Object... objArr) {
        try {
            return getInstance().currentLocale != null ? String.format(getInstance().currentLocale, str, objArr) : String.format(str, objArr);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return "LOC_ERR: " + str;
        }
    }

    public static String formatUserStatus(User user) {
        if (!(user == null || user.status == null || user.status.expires != 0)) {
            if (user.status instanceof TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
            return getString("Online", C0338R.string.Online);
        }
        if (user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user) || (user instanceof TL_userEmpty)) {
            return getString("ALongTimeAgo", C0338R.string.ALongTimeAgo);
        }
        return user.status.expires > ConnectionsManager.getInstance().getCurrentTime() ? getString("Online", C0338R.string.Online) : user.status.expires == -1 ? getString("Invisible", C0338R.string.Invisible) : user.status.expires == -100 ? getString("Lately", C0338R.string.Lately) : user.status.expires == -101 ? getString("WithinAWeek", C0338R.string.WithinAWeek) : user.status.expires == -102 ? getString("WithinAMonth", C0338R.string.WithinAMonth) : formatDateOnline((long) user.status.expires);
    }

    public static String formatYear(long j) {
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        return (language.toLowerCase().equals("fa") || language.toLowerCase().equals("ku") || MoboConstants.f1337d) ? ShamsiCalendar.dateToShamsi(new Date(j * 1000)).toDateString() : getInstance().formatterYear.format(new Date(j * 1000));
    }

    public static String formatYearMonth(long j) {
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        if (!language.toLowerCase().equals("fa") && !language.toLowerCase().equals("ku") && !MoboConstants.f1337d) {
            return getInstance().formatterMonthYear.format(j * 1000);
        }
        ShamsiDate dateToShamsi = ShamsiCalendar.dateToShamsi(new Date(j * 1000));
        language = language.toLowerCase().equals("fa") ? ShamsiCalendar.getShamsiMonth(dateToShamsi.getMonth()) : language.toLowerCase().equals("ku") ? ShamsiCalendar.getShamsiMonthKur(dateToShamsi.getMonth()) : ShamsiCalendar.getShamsiMonthEn(dateToShamsi.getMonth());
        return dateToShamsi.getYear() + " " + language;
    }

    public static String getCurrentLanguageName() {
        return getString("LanguageName", C0338R.string.LanguageName);
    }

    public static LocaleController getInstance() {
        LocaleController localeController = Instance;
        if (localeController == null) {
            synchronized (LocaleController.class) {
                localeController = Instance;
                if (localeController == null) {
                    localeController = new LocaleController();
                    Instance = localeController;
                }
            }
        }
        return localeController;
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        Throwable e;
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        try {
            HashMap<String, String> hashMap = new HashMap();
            XmlPullParser newPullParser = Xml.newPullParser();
            fileInputStream2 = new FileInputStream(file);
            try {
                newPullParser.setInput(fileInputStream2, C0700C.UTF8_NAME);
                String str = null;
                String str2 = null;
                String str3 = null;
                for (int eventType = newPullParser.getEventType(); eventType != QUANTITY_ZERO; eventType = newPullParser.next()) {
                    if (eventType == QUANTITY_ONE) {
                        str2 = newPullParser.getName();
                        if (newPullParser.getAttributeCount() > 0) {
                            str3 = newPullParser.getAttributeValue(QUANTITY_OTHER);
                        }
                    } else if (eventType == QUANTITY_TWO) {
                        if (str3 != null) {
                            str = newPullParser.getText();
                            if (str != null) {
                                str = str.trim().replace("\\n", "\n").replace("\\", TtmlNode.ANONYMOUS_REGION_ID);
                            }
                        }
                    } else if (eventType == 3) {
                        str3 = null;
                        str = null;
                        str2 = null;
                    }
                    if (!(str2 == null || !str2.equals("string") || str == null || str3 == null || str.length() == 0 || str3.length() == 0)) {
                        hashMap.put(str3, str);
                        str3 = null;
                        str = null;
                        str2 = null;
                    }
                }
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                }
                return hashMap;
            } catch (Exception e3) {
                e2 = e3;
                fileInputStream = fileInputStream2;
                try {
                    FileLog.m18e("tmessages", e2);
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable e22) {
                            FileLog.m18e("tmessages", e22);
                        }
                    }
                    return new HashMap();
                } catch (Throwable th) {
                    e22 = th;
                    fileInputStream2 = fileInputStream;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Throwable e4) {
                            FileLog.m18e("tmessages", e4);
                        }
                    }
                    throw e22;
                }
            } catch (Throwable th2) {
                e22 = th2;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                throw e22;
            }
        } catch (Exception e5) {
            e22 = e5;
            fileInputStream = null;
            FileLog.m18e("tmessages", e22);
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            return new HashMap();
        } catch (Throwable th3) {
            e22 = th3;
            fileInputStream2 = null;
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            throw e22;
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder stringBuilder = new StringBuilder(11);
        stringBuilder.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(country);
        if (variant.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(variant);
        return stringBuilder.toString();
    }

    public static String getLocaleStringIso639() {
        Locale systemDefaultLocale = getInstance().getSystemDefaultLocale();
        if (systemDefaultLocale == null) {
            return "en";
        }
        String language = systemDefaultLocale.getLanguage();
        String country = systemDefaultLocale.getCountry();
        String variant = systemDefaultLocale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder stringBuilder = new StringBuilder(11);
        stringBuilder.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            stringBuilder.append('-');
        }
        stringBuilder.append(country);
        if (variant.length() > 0) {
            stringBuilder.append('_');
        }
        stringBuilder.append(variant);
        return stringBuilder.toString();
    }

    public static String getString(String str, int i) {
        return getInstance().getStringInternal(str, i);
    }

    private String getStringInternal(String str, int i) {
        String str2 = (String) this.localeValues.get(str);
        if (str2 == null) {
            try {
                str2 = ApplicationLoader.applicationContext.getString(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        return str2 == null ? "LOC_ERR:" + str : str2;
    }

    private void loadCurrentLocale() {
        this.localeValues.clear();
    }

    private void loadOtherLanguages() {
        int i = QUANTITY_OTHER;
        String string = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", QUANTITY_OTHER).getString("locales", null);
        if (string != null && string.length() != 0) {
            String[] split = string.split("&");
            int length = split.length;
            while (i < length) {
                LocaleInfo createWithString = LocaleInfo.createWithString(split[i]);
                if (createWithString != null) {
                    this.otherLanguages.add(createWithString);
                }
                i += QUANTITY_ZERO;
            }
        }
    }

    private void saveOtherLanguages() {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", QUANTITY_OTHER).edit();
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        Iterator it = this.otherLanguages.iterator();
        while (it.hasNext()) {
            String saveString = ((LocaleInfo) it.next()).getSaveString();
            if (saveString != null) {
                str = (str.length() != 0 ? str + "&" : str) + saveString;
            }
        }
        edit.putString("locales", str);
        edit.commit();
    }

    public static String stringForMessageListDate(long j) {
        try {
            Calendar instance = Calendar.getInstance();
            int i = instance.get(6);
            int i2 = instance.get(QUANTITY_ZERO);
            instance.setTimeInMillis(j * 1000);
            int i3 = instance.get(6);
            int i4 = instance.get(QUANTITY_ZERO);
            Locale locale = getInstance().currentLocale;
            if (locale == null) {
                locale = Locale.getDefault();
            }
            String language = locale.getLanguage();
            if (language.toLowerCase().equals("fa") || language.toLowerCase().equals("ku") || MoboConstants.f1337d) {
                ShamsiDate dateToShamsi = ShamsiCalendar.dateToShamsi(new Date(j * 1000));
                if (i2 != i4) {
                    return dateToShamsi.toDateString();
                }
                i = i3 - i;
                if (i == 0 || (i == -1 && ((long) ((int) (System.currentTimeMillis() / 1000))) - j < 28800)) {
                    return getInstance().formatterDay.format(new Date(j * 1000));
                }
                if (i <= -7 || i > -1) {
                    StringBuilder append = new StringBuilder().append(dateToShamsi.getDay()).append(" ");
                    language = language.toLowerCase().equals("fa") ? ShamsiCalendar.getShamsiMonth(dateToShamsi.getMonth()) : language.toLowerCase().equals("ku") ? ShamsiCalendar.getShamsiMonthKur(dateToShamsi.getMonth()) : ShamsiCalendar.getShamsiMonthEn(dateToShamsi.getMonth());
                    return append.append(language).toString();
                } else if (!language.toLowerCase().equals("ku")) {
                    return getInstance().formatterWeek.format(new Date(j * 1000));
                } else {
                    instance = Calendar.getInstance();
                    instance.setTimeInMillis(j * 1000);
                    return (String) ShamsiCalendar.getShamsiWeekDaysKyMap().get(Integer.valueOf(instance.get(7)));
                }
            } else if (i2 != i4) {
                return getInstance().formatterYear.format(new Date(j * 1000));
            } else {
                int i5 = i3 - i;
                return (i5 == 0 || (i5 == -1 && ((long) ((int) (System.currentTimeMillis() / 1000))) - j < 28800)) ? getInstance().formatterDay.format(new Date(j * 1000)) : (i5 <= -7 || i5 > -1) ? getInstance().formatterMonth.format(new Date(j * 1000)) : getInstance().formatterWeek.format(new Date(j * 1000));
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return "LOC_ERR";
        }
    }

    private String stringForQuantity(int i) {
        switch (i) {
            case QUANTITY_ZERO /*1*/:
                return "zero";
            case QUANTITY_ONE /*2*/:
                return "one";
            case QUANTITY_TWO /*4*/:
                return "two";
            case QUANTITY_FEW /*8*/:
                return "few";
            case QUANTITY_MANY /*16*/:
                return "many";
            default:
                return "other";
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z) {
        applyLanguage(localeInfo, z, false);
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2) {
        LocaleInfo localeInfo2 = null;
        if (localeInfo != null) {
            try {
                Locale locale;
                if (localeInfo.shortName != null) {
                    String[] split = localeInfo.shortName.split("_");
                    locale = split.length == QUANTITY_ZERO ? new Locale(localeInfo.shortName) : new Locale(split[QUANTITY_OTHER], split[QUANTITY_ZERO]);
                    if (locale != null && z) {
                        this.languageOverride = localeInfo.shortName;
                        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", QUANTITY_OTHER).edit();
                        edit.putString("language", localeInfo.shortName);
                        edit.commit();
                    }
                } else {
                    Locale locale2 = this.systemDefaultLocale;
                    this.languageOverride = null;
                    Editor edit2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", QUANTITY_OTHER).edit();
                    edit2.remove("language");
                    edit2.commit();
                    if (locale2 != null) {
                        if (locale2.getLanguage() != null) {
                            localeInfo2 = (LocaleInfo) this.languagesDict.get(locale2.getLanguage());
                        }
                        if (localeInfo2 == null) {
                            localeInfo2 = (LocaleInfo) this.languagesDict.get(getLocaleString(locale2));
                        }
                        if (localeInfo2 == null) {
                            locale = Locale.US;
                        }
                    }
                    locale = locale2;
                }
                if (locale != null) {
                    if (localeInfo.pathToFile == null) {
                        this.localeValues.clear();
                    } else if (!z2) {
                        this.localeValues = getLocaleFileStrings(new File(localeInfo.pathToFile));
                    }
                    this.currentLocale = locale;
                    this.currentLocaleInfo = localeInfo;
                    this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                    if (this.currentPluralRules == null) {
                        this.currentPluralRules = (PluralRules) this.allRules.get("en");
                    }
                    this.changingConfiguration = true;
                    Locale.setDefault(this.currentLocale);
                    Configuration configuration = new Configuration();
                    configuration.locale = this.currentLocale;
                    ApplicationLoader.applicationContext.getResources().updateConfiguration(configuration, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                    this.changingConfiguration = false;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                this.changingConfiguration = false;
            }
            recreateFormatters();
        }
    }

    public boolean applyLanguageFile(File file) {
        try {
            HashMap localeFileStrings = getLocaleFileStrings(file);
            String str = (String) localeFileStrings.get("LanguageName");
            String str2 = (String) localeFileStrings.get("LanguageNameInEnglish");
            String str3 = (String) localeFileStrings.get("LanguageCode");
            if (str != null && str.length() > 0 && str2 != null && str2.length() > 0 && str3 != null && str3.length() > 0) {
                if (str.contains("&") || str.contains("|")) {
                    return false;
                }
                if (str2.contains("&") || str2.contains("|")) {
                    return false;
                }
                if (str3.contains("&") || str3.contains("|") || str3.contains("/") || str3.contains("\\")) {
                    return false;
                }
                File file2 = new File(ApplicationLoader.getFilesDirFixed(), str3 + ".xml");
                if (!AndroidUtilities.copyFile(file, file2)) {
                    return false;
                }
                LocaleInfo localeInfo = (LocaleInfo) this.languagesDict.get(str3);
                if (localeInfo == null) {
                    localeInfo = new LocaleInfo();
                    localeInfo.name = str;
                    localeInfo.nameEnglish = str2;
                    localeInfo.shortName = str3;
                    localeInfo.pathToFile = file2.getAbsolutePath();
                    this.sortedLanguages.add(localeInfo);
                    this.languagesDict.put(localeInfo.shortName, localeInfo);
                    this.otherLanguages.add(localeInfo);
                    Collections.sort(this.sortedLanguages, new C04552());
                    saveOtherLanguages();
                }
                this.localeValues = localeFileStrings;
                applyLanguage(localeInfo, true, true);
                return true;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return false;
    }

    public boolean deleteLanguage(LocaleInfo localeInfo) {
        if (localeInfo.pathToFile == null) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            applyLanguage(this.defaultLocalInfo, true);
        }
        this.otherLanguages.remove(localeInfo);
        this.sortedLanguages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.shortName);
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public String getTranslitString(String str) {
        if (this.translitChars == null) {
            this.translitChars = new HashMap(520);
            this.translitChars.put("\u023c", "c");
            this.translitChars.put("\u1d87", "n");
            this.translitChars.put("\u0256", "d");
            this.translitChars.put("\u1eff", "y");
            this.translitChars.put("\u1d13", "o");
            this.translitChars.put("\u00f8", "o");
            this.translitChars.put("\u1e01", "a");
            this.translitChars.put("\u02af", "h");
            this.translitChars.put("\u0177", "y");
            this.translitChars.put("\u029e", "k");
            this.translitChars.put("\u1eeb", "u");
            this.translitChars.put("\ua733", "aa");
            this.translitChars.put("\u0133", "ij");
            this.translitChars.put("\u1e3d", "l");
            this.translitChars.put("\u026a", "i");
            this.translitChars.put("\u1e07", "b");
            this.translitChars.put("\u0280", "r");
            this.translitChars.put("\u011b", "e");
            this.translitChars.put("\ufb03", "ffi");
            this.translitChars.put("\u01a1", "o");
            this.translitChars.put("\u2c79", "r");
            this.translitChars.put("\u1ed3", "o");
            this.translitChars.put("\u01d0", "i");
            this.translitChars.put("\ua755", TtmlNode.TAG_P);
            this.translitChars.put("\u00fd", "y");
            this.translitChars.put("\u1e1d", "e");
            this.translitChars.put("\u2092", "o");
            this.translitChars.put("\u2c65", "a");
            this.translitChars.put("\u0299", "b");
            this.translitChars.put("\u1e1b", "e");
            this.translitChars.put("\u0188", "c");
            this.translitChars.put("\u0266", "h");
            this.translitChars.put("\u1d6c", "b");
            this.translitChars.put("\u1e63", "s");
            this.translitChars.put("\u0111", "d");
            this.translitChars.put("\u1ed7", "o");
            this.translitChars.put("\u025f", "j");
            this.translitChars.put("\u1e9a", "a");
            this.translitChars.put("\u024f", "y");
            this.translitChars.put("\u043b", "l");
            this.translitChars.put("\u028c", "v");
            this.translitChars.put("\ua753", TtmlNode.TAG_P);
            this.translitChars.put("\ufb01", "fi");
            this.translitChars.put("\u1d84", "k");
            this.translitChars.put("\u1e0f", "d");
            this.translitChars.put("\u1d0c", "l");
            this.translitChars.put("\u0117", "e");
            this.translitChars.put("\u0451", "yo");
            this.translitChars.put("\u1d0b", "k");
            this.translitChars.put("\u010b", "c");
            this.translitChars.put("\u0281", "r");
            this.translitChars.put("\u0195", "hv");
            this.translitChars.put("\u0180", "b");
            this.translitChars.put("\u1e4d", "o");
            this.translitChars.put("\u0223", "ou");
            this.translitChars.put("\u01f0", "j");
            this.translitChars.put("\u1d83", "g");
            this.translitChars.put("\u1e4b", "n");
            this.translitChars.put("\u0249", "j");
            this.translitChars.put("\u01e7", "g");
            this.translitChars.put("\u01f3", "dz");
            this.translitChars.put("\u017a", "z");
            this.translitChars.put("\ua737", "au");
            this.translitChars.put("\u01d6", "u");
            this.translitChars.put("\u1d79", "g");
            this.translitChars.put("\u022f", "o");
            this.translitChars.put("\u0250", "a");
            this.translitChars.put("\u0105", "a");
            this.translitChars.put("\u00f5", "o");
            this.translitChars.put("\u027b", "r");
            this.translitChars.put("\ua74d", "o");
            this.translitChars.put("\u01df", "a");
            this.translitChars.put("\u0234", "l");
            this.translitChars.put("\u0282", "s");
            this.translitChars.put("\ufb02", "fl");
            this.translitChars.put("\u0209", "i");
            this.translitChars.put("\u2c7b", "e");
            this.translitChars.put("\u1e49", "n");
            this.translitChars.put("\u00ef", "i");
            this.translitChars.put("\u00f1", "n");
            this.translitChars.put("\u1d09", "i");
            this.translitChars.put("\u0287", "t");
            this.translitChars.put("\u1e93", "z");
            this.translitChars.put("\u1ef7", "y");
            this.translitChars.put("\u0233", "y");
            this.translitChars.put("\u1e69", "s");
            this.translitChars.put("\u027d", "r");
            this.translitChars.put("\u011d", "g");
            this.translitChars.put("\u0432", "v");
            this.translitChars.put("\u1d1d", "u");
            this.translitChars.put("\u1e33", "k");
            this.translitChars.put("\ua76b", "et");
            this.translitChars.put("\u012b", "i");
            this.translitChars.put("\u0165", "t");
            this.translitChars.put("\ua73f", "c");
            this.translitChars.put("\u029f", "l");
            this.translitChars.put("\ua739", "av");
            this.translitChars.put("\u00fb", "u");
            this.translitChars.put("\u00e6", "ae");
            this.translitChars.put("\u0438", "i");
            this.translitChars.put("\u0103", "a");
            this.translitChars.put("\u01d8", "u");
            this.translitChars.put("\ua785", "s");
            this.translitChars.put("\u1d63", "r");
            this.translitChars.put("\u1d00", "a");
            this.translitChars.put("\u0183", "b");
            this.translitChars.put("\u1e29", "h");
            this.translitChars.put("\u1e67", "s");
            this.translitChars.put("\u2091", "e");
            this.translitChars.put("\u029c", "h");
            this.translitChars.put("\u1e8b", "x");
            this.translitChars.put("\ua745", "k");
            this.translitChars.put("\u1e0b", "d");
            this.translitChars.put("\u01a3", "oi");
            this.translitChars.put("\ua751", TtmlNode.TAG_P);
            this.translitChars.put("\u0127", "h");
            this.translitChars.put("\u2c74", "v");
            this.translitChars.put("\u1e87", "w");
            this.translitChars.put("\u01f9", "n");
            this.translitChars.put("\u026f", "m");
            this.translitChars.put("\u0261", "g");
            this.translitChars.put("\u0274", "n");
            this.translitChars.put("\u1d18", TtmlNode.TAG_P);
            this.translitChars.put("\u1d65", "v");
            this.translitChars.put("\u016b", "u");
            this.translitChars.put("\u1e03", "b");
            this.translitChars.put("\u1e57", TtmlNode.TAG_P);
            this.translitChars.put("\u044c", TtmlNode.ANONYMOUS_REGION_ID);
            this.translitChars.put("\u00e5", "a");
            this.translitChars.put("\u0255", "c");
            this.translitChars.put("\u1ecd", "o");
            this.translitChars.put("\u1eaf", "a");
            this.translitChars.put("\u0192", "f");
            this.translitChars.put("\u01e3", "ae");
            this.translitChars.put("\ua761", "vy");
            this.translitChars.put("\ufb00", "ff");
            this.translitChars.put("\u1d89", "r");
            this.translitChars.put("\u00f4", "o");
            this.translitChars.put("\u01ff", "o");
            this.translitChars.put("\u1e73", "u");
            this.translitChars.put("\u0225", "z");
            this.translitChars.put("\u1e1f", "f");
            this.translitChars.put("\u1e13", "d");
            this.translitChars.put("\u0207", "e");
            this.translitChars.put("\u0215", "u");
            this.translitChars.put("\u043f", TtmlNode.TAG_P);
            this.translitChars.put("\u0235", "n");
            this.translitChars.put("\u02a0", "q");
            this.translitChars.put("\u1ea5", "a");
            this.translitChars.put("\u01e9", "k");
            this.translitChars.put("\u0129", "i");
            this.translitChars.put("\u1e75", "u");
            this.translitChars.put("\u0167", "t");
            this.translitChars.put("\u027e", "r");
            this.translitChars.put("\u0199", "k");
            this.translitChars.put("\u1e6b", "t");
            this.translitChars.put("\ua757", "q");
            this.translitChars.put("\u1ead", "a");
            this.translitChars.put("\u043d", "n");
            this.translitChars.put("\u0284", "j");
            this.translitChars.put("\u019a", "l");
            this.translitChars.put("\u1d82", "f");
            this.translitChars.put("\u0434", "d");
            this.translitChars.put("\u1d74", "s");
            this.translitChars.put("\ua783", "r");
            this.translitChars.put("\u1d8c", "v");
            this.translitChars.put("\u0275", "o");
            this.translitChars.put("\u1e09", "c");
            this.translitChars.put("\u1d64", "u");
            this.translitChars.put("\u1e91", "z");
            this.translitChars.put("\u1e79", "u");
            this.translitChars.put("\u0148", "n");
            this.translitChars.put("\u028d", "w");
            this.translitChars.put("\u1ea7", "a");
            this.translitChars.put("\u01c9", "lj");
            this.translitChars.put("\u0253", "b");
            this.translitChars.put("\u027c", "r");
            this.translitChars.put("\u00f2", "o");
            this.translitChars.put("\u1e98", "w");
            this.translitChars.put("\u0257", "d");
            this.translitChars.put("\ua73d", "ay");
            this.translitChars.put("\u01b0", "u");
            this.translitChars.put("\u1d80", "b");
            this.translitChars.put("\u01dc", "u");
            this.translitChars.put("\u1eb9", "e");
            this.translitChars.put("\u01e1", "a");
            this.translitChars.put("\u0265", "h");
            this.translitChars.put("\u1e4f", "o");
            this.translitChars.put("\u01d4", "u");
            this.translitChars.put("\u028e", "y");
            this.translitChars.put("\u0231", "o");
            this.translitChars.put("\u1ec7", "e");
            this.translitChars.put("\u1ebf", "e");
            this.translitChars.put("\u012d", "i");
            this.translitChars.put("\u2c78", "e");
            this.translitChars.put("\u1e6f", "t");
            this.translitChars.put("\u1d91", "d");
            this.translitChars.put("\u1e27", "h");
            this.translitChars.put("\u1e65", "s");
            this.translitChars.put("\u00eb", "e");
            this.translitChars.put("\u1d0d", "m");
            this.translitChars.put("\u00f6", "o");
            this.translitChars.put("\u00e9", "e");
            this.translitChars.put("\u0131", "i");
            this.translitChars.put("\u010f", "d");
            this.translitChars.put("\u1d6f", "m");
            this.translitChars.put("\u1ef5", "y");
            this.translitChars.put("\u044f", "ya");
            this.translitChars.put("\u0175", "w");
            this.translitChars.put("\u1ec1", "e");
            this.translitChars.put("\u1ee9", "u");
            this.translitChars.put("\u01b6", "z");
            this.translitChars.put("\u0135", "j");
            this.translitChars.put("\u1e0d", "d");
            this.translitChars.put("\u016d", "u");
            this.translitChars.put("\u029d", "j");
            this.translitChars.put("\u0436", "zh");
            this.translitChars.put("\u00ea", "e");
            this.translitChars.put("\u01da", "u");
            this.translitChars.put("\u0121", "g");
            this.translitChars.put("\u1e59", "r");
            this.translitChars.put("\u019e", "n");
            this.translitChars.put("\u044a", TtmlNode.ANONYMOUS_REGION_ID);
            this.translitChars.put("\u1e17", "e");
            this.translitChars.put("\u1e9d", "s");
            this.translitChars.put("\u1d81", "d");
            this.translitChars.put("\u0137", "k");
            this.translitChars.put("\u1d02", "ae");
            this.translitChars.put("\u0258", "e");
            this.translitChars.put("\u1ee3", "o");
            this.translitChars.put("\u1e3f", "m");
            this.translitChars.put("\ua730", "f");
            this.translitChars.put("\u0430", "a");
            this.translitChars.put("\u1eb5", "a");
            this.translitChars.put("\ua74f", "oo");
            this.translitChars.put("\u1d86", "m");
            this.translitChars.put("\u1d7d", TtmlNode.TAG_P);
            this.translitChars.put("\u0446", "ts");
            this.translitChars.put("\u1eef", "u");
            this.translitChars.put("\u2c6a", "k");
            this.translitChars.put("\u1e25", "h");
            this.translitChars.put("\u0163", "t");
            this.translitChars.put("\u1d71", TtmlNode.TAG_P);
            this.translitChars.put("\u1e41", "m");
            this.translitChars.put("\u00e1", "a");
            this.translitChars.put("\u1d0e", "n");
            this.translitChars.put("\ua75f", "v");
            this.translitChars.put("\u00e8", "e");
            this.translitChars.put("\u1d8e", "z");
            this.translitChars.put("\ua77a", "d");
            this.translitChars.put("\u1d88", TtmlNode.TAG_P);
            this.translitChars.put("\u043c", "m");
            this.translitChars.put("\u026b", "l");
            this.translitChars.put("\u1d22", "z");
            this.translitChars.put("\u0271", "m");
            this.translitChars.put("\u1e5d", "r");
            this.translitChars.put("\u1e7d", "v");
            this.translitChars.put("\u0169", "u");
            this.translitChars.put("\u00df", "ss");
            this.translitChars.put("\u0442", "t");
            this.translitChars.put("\u0125", "h");
            this.translitChars.put("\u1d75", "t");
            this.translitChars.put("\u0290", "z");
            this.translitChars.put("\u1e5f", "r");
            this.translitChars.put("\u0272", "n");
            this.translitChars.put("\u00e0", "a");
            this.translitChars.put("\u1e99", "y");
            this.translitChars.put("\u1ef3", "y");
            this.translitChars.put("\u1d14", "oe");
            this.translitChars.put("\u044b", "i");
            this.translitChars.put("\u2093", "x");
            this.translitChars.put("\u0217", "u");
            this.translitChars.put("\u2c7c", "j");
            this.translitChars.put("\u1eab", "a");
            this.translitChars.put("\u0291", "z");
            this.translitChars.put("\u1e9b", "s");
            this.translitChars.put("\u1e2d", "i");
            this.translitChars.put("\ua735", "ao");
            this.translitChars.put("\u0240", "z");
            this.translitChars.put("\u00ff", "y");
            this.translitChars.put("\u01dd", "e");
            this.translitChars.put("\u01ed", "o");
            this.translitChars.put("\u1d05", "d");
            this.translitChars.put("\u1d85", "l");
            this.translitChars.put("\u00f9", "u");
            this.translitChars.put("\u1ea1", "a");
            this.translitChars.put("\u1e05", "b");
            this.translitChars.put("\u1ee5", "u");
            this.translitChars.put("\u043a", "k");
            this.translitChars.put("\u1eb1", "a");
            this.translitChars.put("\u1d1b", "t");
            this.translitChars.put("\u01b4", "y");
            this.translitChars.put("\u2c66", "t");
            this.translitChars.put("\u0437", "z");
            this.translitChars.put("\u2c61", "l");
            this.translitChars.put("\u0237", "j");
            this.translitChars.put("\u1d76", "z");
            this.translitChars.put("\u1e2b", "h");
            this.translitChars.put("\u2c73", "w");
            this.translitChars.put("\u1e35", "k");
            this.translitChars.put("\u1edd", "o");
            this.translitChars.put("\u00ee", "i");
            this.translitChars.put("\u0123", "g");
            this.translitChars.put("\u0205", "e");
            this.translitChars.put("\u0227", "a");
            this.translitChars.put("\u1eb3", "a");
            this.translitChars.put("\u0449", "sch");
            this.translitChars.put("\u024b", "q");
            this.translitChars.put("\u1e6d", "t");
            this.translitChars.put("\ua778", "um");
            this.translitChars.put("\u1d04", "c");
            this.translitChars.put("\u1e8d", "x");
            this.translitChars.put("\u1ee7", "u");
            this.translitChars.put("\u1ec9", "i");
            this.translitChars.put("\u1d1a", "r");
            this.translitChars.put("\u015b", "s");
            this.translitChars.put("\ua74b", "o");
            this.translitChars.put("\u1ef9", "y");
            this.translitChars.put("\u1e61", "s");
            this.translitChars.put("\u01cc", "nj");
            this.translitChars.put("\u0201", "a");
            this.translitChars.put("\u1e97", "t");
            this.translitChars.put("\u013a", "l");
            this.translitChars.put("\u017e", "z");
            this.translitChars.put("\u1d7a", "th");
            this.translitChars.put("\u018c", "d");
            this.translitChars.put("\u0219", "s");
            this.translitChars.put("\u0161", "s");
            this.translitChars.put("\u1d99", "u");
            this.translitChars.put("\u1ebd", "e");
            this.translitChars.put("\u1e9c", "s");
            this.translitChars.put("\u0247", "e");
            this.translitChars.put("\u1e77", "u");
            this.translitChars.put("\u1ed1", "o");
            this.translitChars.put("\u023f", "s");
            this.translitChars.put("\u1d20", "v");
            this.translitChars.put("\ua76d", "is");
            this.translitChars.put("\u1d0f", "o");
            this.translitChars.put("\u025b", "e");
            this.translitChars.put("\u01fb", "a");
            this.translitChars.put("\ufb04", "ffl");
            this.translitChars.put("\u2c7a", "o");
            this.translitChars.put("\u020b", "i");
            this.translitChars.put("\u1d6b", "ue");
            this.translitChars.put("\u0221", "d");
            this.translitChars.put("\u2c6c", "z");
            this.translitChars.put("\u1e81", "w");
            this.translitChars.put("\u1d8f", "a");
            this.translitChars.put("\ua787", "t");
            this.translitChars.put("\u011f", "g");
            this.translitChars.put("\u0273", "n");
            this.translitChars.put("\u029b", "g");
            this.translitChars.put("\u1d1c", "u");
            this.translitChars.put("\u0444", "f");
            this.translitChars.put("\u1ea9", "a");
            this.translitChars.put("\u1e45", "n");
            this.translitChars.put("\u0268", "i");
            this.translitChars.put("\u1d19", "r");
            this.translitChars.put("\u01ce", "a");
            this.translitChars.put("\u017f", "s");
            this.translitChars.put("\u0443", "u");
            this.translitChars.put("\u022b", "o");
            this.translitChars.put("\u027f", "r");
            this.translitChars.put("\u01ad", "t");
            this.translitChars.put("\u1e2f", "i");
            this.translitChars.put("\u01fd", "ae");
            this.translitChars.put("\u2c71", "v");
            this.translitChars.put("\u0276", "oe");
            this.translitChars.put("\u1e43", "m");
            this.translitChars.put("\u017c", "z");
            this.translitChars.put("\u0115", "e");
            this.translitChars.put("\ua73b", "av");
            this.translitChars.put("\u1edf", "o");
            this.translitChars.put("\u1ec5", "e");
            this.translitChars.put("\u026c", "l");
            this.translitChars.put("\u1ecb", "i");
            this.translitChars.put("\u1d6d", "d");
            this.translitChars.put("\ufb06", "st");
            this.translitChars.put("\u1e37", "l");
            this.translitChars.put("\u0155", "r");
            this.translitChars.put("\u1d15", "ou");
            this.translitChars.put("\u0288", "t");
            this.translitChars.put("\u0101", "a");
            this.translitChars.put("\u044d", "e");
            this.translitChars.put("\u1e19", "e");
            this.translitChars.put("\u1d11", "o");
            this.translitChars.put("\u00e7", "c");
            this.translitChars.put("\u1d8a", "s");
            this.translitChars.put("\u1eb7", "a");
            this.translitChars.put("\u0173", "u");
            this.translitChars.put("\u1ea3", "a");
            this.translitChars.put("\u01e5", "g");
            this.translitChars.put("\u0440", "r");
            this.translitChars.put("\ua741", "k");
            this.translitChars.put("\u1e95", "z");
            this.translitChars.put("\u015d", "s");
            this.translitChars.put("\u1e15", "e");
            this.translitChars.put("\u0260", "g");
            this.translitChars.put("\ua749", "l");
            this.translitChars.put("\ua77c", "f");
            this.translitChars.put("\u1d8d", "x");
            this.translitChars.put("\u0445", "h");
            this.translitChars.put("\u01d2", "o");
            this.translitChars.put("\u0119", "e");
            this.translitChars.put("\u1ed5", "o");
            this.translitChars.put("\u01ab", "t");
            this.translitChars.put("\u01eb", "o");
            this.translitChars.put("i\u0307", "i");
            this.translitChars.put("\u1e47", "n");
            this.translitChars.put("\u0107", "c");
            this.translitChars.put("\u1d77", "g");
            this.translitChars.put("\u1e85", "w");
            this.translitChars.put("\u1e11", "d");
            this.translitChars.put("\u1e39", "l");
            this.translitChars.put("\u0447", "ch");
            this.translitChars.put("\u0153", "oe");
            this.translitChars.put("\u1d73", "r");
            this.translitChars.put("\u013c", "l");
            this.translitChars.put("\u0211", "r");
            this.translitChars.put("\u022d", "o");
            this.translitChars.put("\u1d70", "n");
            this.translitChars.put("\u1d01", "ae");
            this.translitChars.put("\u0140", "l");
            this.translitChars.put("\u00e4", "a");
            this.translitChars.put("\u01a5", TtmlNode.TAG_P);
            this.translitChars.put("\u1ecf", "o");
            this.translitChars.put("\u012f", "i");
            this.translitChars.put("\u0213", "r");
            this.translitChars.put("\u01c6", "dz");
            this.translitChars.put("\u1e21", "g");
            this.translitChars.put("\u1e7b", "u");
            this.translitChars.put("\u014d", "o");
            this.translitChars.put("\u013e", "l");
            this.translitChars.put("\u1e83", "w");
            this.translitChars.put("\u021b", "t");
            this.translitChars.put("\u0144", "n");
            this.translitChars.put("\u024d", "r");
            this.translitChars.put("\u0203", "a");
            this.translitChars.put("\u00fc", "u");
            this.translitChars.put("\ua781", "l");
            this.translitChars.put("\u1d10", "o");
            this.translitChars.put("\u1edb", "o");
            this.translitChars.put("\u1d03", "b");
            this.translitChars.put("\u0279", "r");
            this.translitChars.put("\u1d72", "r");
            this.translitChars.put("\u028f", "y");
            this.translitChars.put("\u1d6e", "f");
            this.translitChars.put("\u2c68", "h");
            this.translitChars.put("\u014f", "o");
            this.translitChars.put("\u00fa", "u");
            this.translitChars.put("\u1e5b", "r");
            this.translitChars.put("\u02ae", "h");
            this.translitChars.put("\u00f3", "o");
            this.translitChars.put("\u016f", "u");
            this.translitChars.put("\u1ee1", "o");
            this.translitChars.put("\u1e55", TtmlNode.TAG_P);
            this.translitChars.put("\u1d96", "i");
            this.translitChars.put("\u1ef1", "u");
            this.translitChars.put("\u00e3", "a");
            this.translitChars.put("\u1d62", "i");
            this.translitChars.put("\u1e71", "t");
            this.translitChars.put("\u1ec3", "e");
            this.translitChars.put("\u1eed", "u");
            this.translitChars.put("\u00ed", "i");
            this.translitChars.put("\u0254", "o");
            this.translitChars.put("\u0441", "s");
            this.translitChars.put("\u0439", "i");
            this.translitChars.put("\u027a", "r");
            this.translitChars.put("\u0262", "g");
            this.translitChars.put("\u0159", "r");
            this.translitChars.put("\u1e96", "h");
            this.translitChars.put("\u0171", "u");
            this.translitChars.put("\u020d", "o");
            this.translitChars.put("\u0448", "sh");
            this.translitChars.put("\u1e3b", "l");
            this.translitChars.put("\u1e23", "h");
            this.translitChars.put("\u0236", "t");
            this.translitChars.put("\u0146", "n");
            this.translitChars.put("\u1d92", "e");
            this.translitChars.put("\u00ec", "i");
            this.translitChars.put("\u1e89", "w");
            this.translitChars.put("\u0431", "b");
            this.translitChars.put("\u0113", "e");
            this.translitChars.put("\u1d07", "e");
            this.translitChars.put("\u0142", "l");
            this.translitChars.put("\u1ed9", "o");
            this.translitChars.put("\u026d", "l");
            this.translitChars.put("\u1e8f", "y");
            this.translitChars.put("\u1d0a", "j");
            this.translitChars.put("\u1e31", "k");
            this.translitChars.put("\u1e7f", "v");
            this.translitChars.put("\u0229", "e");
            this.translitChars.put("\u00e2", "a");
            this.translitChars.put("\u015f", "s");
            this.translitChars.put("\u0157", "r");
            this.translitChars.put("\u028b", "v");
            this.translitChars.put("\u2090", "a");
            this.translitChars.put("\u2184", "c");
            this.translitChars.put("\u1d93", "e");
            this.translitChars.put("\u0270", "m");
            this.translitChars.put("\u0435", "e");
            this.translitChars.put("\u1d21", "w");
            this.translitChars.put("\u020f", "o");
            this.translitChars.put("\u010d", "c");
            this.translitChars.put("\u01f5", "g");
            this.translitChars.put("\u0109", "c");
            this.translitChars.put("\u044e", "yu");
            this.translitChars.put("\u1d97", "o");
            this.translitChars.put("\ua743", "k");
            this.translitChars.put("\ua759", "q");
            this.translitChars.put("\u0433", "g");
            this.translitChars.put("\u1e51", "o");
            this.translitChars.put("\ua731", "s");
            this.translitChars.put("\u1e53", "o");
            this.translitChars.put("\u021f", "h");
            this.translitChars.put("\u0151", "o");
            this.translitChars.put("\ua729", "tz");
            this.translitChars.put("\u1ebb", "e");
            this.translitChars.put("\u043e", "o");
        }
        StringBuilder stringBuilder = new StringBuilder(str.length());
        int length = str.length();
        for (int i = QUANTITY_OTHER; i < length; i += QUANTITY_ZERO) {
            String substring = str.substring(i, i + QUANTITY_ZERO);
            String str2 = (String) this.translitChars.get(substring);
            if (str2 != null) {
                stringBuilder.append(str2);
            } else {
                stringBuilder.append(substring);
            }
        }
        return stringBuilder.toString();
    }

    public void onDeviceConfigurationChange(Configuration configuration) {
        if (!this.changingConfiguration) {
            is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
            this.systemDefaultLocale = configuration.locale;
            if (this.languageOverride != null) {
                LocaleInfo localeInfo = this.currentLocaleInfo;
                this.currentLocaleInfo = null;
                applyLanguage(localeInfo, false);
                return;
            }
            Locale locale = configuration.locale;
            if (locale != null) {
                String displayName = locale.getDisplayName();
                String displayName2 = this.currentLocale.getDisplayName();
                if (!(displayName == null || displayName2 == null || displayName.equals(displayName2))) {
                    recreateFormatters();
                }
                this.currentLocale = locale;
                this.currentPluralRules = (PluralRules) this.allRules.get(this.currentLocale.getLanguage());
                if (this.currentPluralRules == null) {
                    this.currentPluralRules = (PluralRules) this.allRules.get("en");
                }
            }
        }
    }

    public void recreateFormatters() {
        int i = QUANTITY_ZERO;
        Locale locale = this.currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        if (language == null) {
            language = "en";
        }
        boolean z = language.toLowerCase().equals("ar") || language.toLowerCase().equals("fa") || language.toLowerCase().equals("ku");
        isRTL = z;
        if (language.toLowerCase().equals("ko")) {
            i = QUANTITY_ONE;
        }
        nameDisplayOrder = i;
        this.formatterMonth = createFormatter(locale, getStringInternal("formatterMonth", C0338R.string.formatterMonth), "dd MMM");
        this.formatterYear = createFormatter(locale, getStringInternal("formatterYear", C0338R.string.formatterYear), "dd.MM.yy");
        this.formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", C0338R.string.formatterYearMax), "dd.MM.yyyy");
        this.chatDate = createFormatter(locale, getStringInternal("chatDate", C0338R.string.chatDate), "d MMMM");
        this.chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", C0338R.string.chatFullDate), "d MMMM yyyy");
        this.formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", C0338R.string.formatterWeek), "EEE");
        this.formatterMonthYear = createFormatter(locale, getStringInternal("formatterMonthYear", C0338R.string.formatterMonthYear), "MMMM yyyy");
        Locale locale2 = (language.toLowerCase().equals("ar") || language.toLowerCase().equals("fa") || language.toLowerCase().equals("ku") || language.toLowerCase().equals("ko")) ? locale : Locale.US;
        this.formatterDay = createFormatter(locale2, is24HourFormat ? getStringInternal("formatterDay24H", C0338R.string.formatterDay24H) : getStringInternal("formatterDay12H", C0338R.string.formatterDay12H), is24HourFormat ? "HH:mm" : "h:mm a");
        if (!(language.toLowerCase().equals("ar") || language.toLowerCase().equals("ko"))) {
            locale = Locale.US;
        }
        this.formatterEnDay = createFormatter(locale, is24HourFormat ? getStringInternal("formatterDay24H", C0338R.string.formatterDay24H) : getStringInternal("formatterDay12H", C0338R.string.formatterDay12H), is24HourFormat ? "HH:mm" : "h:mm a");
    }
}
