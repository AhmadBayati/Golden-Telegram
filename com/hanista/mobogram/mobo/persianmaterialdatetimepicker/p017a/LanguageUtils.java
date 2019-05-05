package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a;

import com.hanista.mobogram.messenger.LocaleController;
import java.util.ArrayList;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.a.a */
public class LanguageUtils {
    public static String m2043a(String str) {
        Locale locale = LocaleController.getInstance().currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        return (language.toLowerCase().equals("fa") || language.toLowerCase().equals("ku")) ? str.replace("0", "\u06f0").replace("1", "\u0661").replace("2", "\u06f2").replace("3", "\u06f3").replace("4", "\u06f4").replace("5", "\u06f5").replace("6", "\u06f6").replace("7", "\u06f7").replace("8", "\u06f8").replace("9", "\u06f9") : str;
    }

    public static ArrayList<String> m2044a(ArrayList<String> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.set(i, LanguageUtils.m2043a((String) arrayList.get(i)));
        }
        return arrayList;
    }

    public static String[] m2045a(String[] strArr) {
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = LanguageUtils.m2043a(strArr[i]);
        }
        return strArr;
    }

    public static String m2046b(String str) {
        return str.replace("\u06f0", "0").replace("\u0661", "1").replace("\u06f2", "2").replace("\u06f3", "3").replace("\u06f4", "4").replace("\u06f5", "5").replace("\u06f6", "6").replace("\u06f7", "7").replace("\u06f8", "8").replace("\u06f9", "9");
    }
}
