package com.hanista.mobogram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;

public class CallingCodeInfo {
    public String callingCode;
    public ArrayList<String> countries;
    public ArrayList<String> intlPrefixes;
    public ArrayList<RuleSet> ruleSets;
    public ArrayList<String> trunkPrefixes;

    public CallingCodeInfo() {
        this.countries = new ArrayList();
        this.callingCode = TtmlNode.ANONYMOUS_REGION_ID;
        this.trunkPrefixes = new ArrayList();
        this.intlPrefixes = new ArrayList();
        this.ruleSets = new ArrayList();
    }

    String format(String str) {
        String str2;
        String substring;
        String str3;
        String str4 = null;
        if (str.startsWith(this.callingCode)) {
            str2 = this.callingCode;
            substring = str.substring(str2.length());
            str3 = null;
            str4 = str2;
        } else {
            str2 = matchingTrunkCode(str);
            if (str2 != null) {
                substring = str.substring(str2.length());
                str3 = str2;
            } else {
                str3 = null;
                substring = str;
            }
        }
        Iterator it = this.ruleSets.iterator();
        while (it.hasNext()) {
            str2 = ((RuleSet) it.next()).format(substring, str4, str3, true);
            if (str2 != null) {
                return str2;
            }
        }
        it = this.ruleSets.iterator();
        while (it.hasNext()) {
            str2 = ((RuleSet) it.next()).format(substring, str4, str3, false);
            if (str2 != null) {
                return str2;
            }
        }
        if (str4 == null || substring.length() == 0) {
            return str;
        }
        return String.format("%s %s", new Object[]{str4, substring});
    }

    boolean isValidPhoneNumber(String str) {
        String str2;
        String str3 = null;
        String str4;
        if (str.startsWith(this.callingCode)) {
            str4 = this.callingCode;
            str = str.substring(str4.length());
            str2 = null;
            str3 = str4;
        } else {
            str4 = matchingTrunkCode(str);
            if (str4 != null) {
                str = str.substring(str4.length());
                str2 = str4;
            } else {
                str2 = null;
            }
        }
        Iterator it = this.ruleSets.iterator();
        while (it.hasNext()) {
            if (((RuleSet) it.next()).isValid(str, str3, str2, true)) {
                return true;
            }
        }
        it = this.ruleSets.iterator();
        while (it.hasNext()) {
            if (((RuleSet) it.next()).isValid(str, str3, str2, false)) {
                return true;
            }
        }
        return false;
    }

    String matchingAccessCode(String str) {
        Iterator it = this.intlPrefixes.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (str.startsWith(str2)) {
                return str2;
            }
        }
        return null;
    }

    String matchingTrunkCode(String str) {
        Iterator it = this.trunkPrefixes.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (str.startsWith(str2)) {
                return str2;
            }
        }
        return null;
    }
}
