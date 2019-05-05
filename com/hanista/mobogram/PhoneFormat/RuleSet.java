package com.hanista.mobogram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleSet {
    public static Pattern pattern;
    public boolean hasRuleWithIntlPrefix;
    public boolean hasRuleWithTrunkPrefix;
    public int matchLen;
    public ArrayList<PhoneRule> rules;

    static {
        pattern = Pattern.compile("[0-9]+");
    }

    public RuleSet() {
        this.rules = new ArrayList();
    }

    String format(String str, String str2, String str3, boolean z) {
        if (str.length() < this.matchLen) {
            return null;
        }
        Matcher matcher = pattern.matcher(str.substring(0, this.matchLen));
        int parseInt = matcher.find() ? Integer.parseInt(matcher.group(0)) : 0;
        Iterator it = this.rules.iterator();
        while (it.hasNext()) {
            PhoneRule phoneRule = (PhoneRule) it.next();
            if (parseInt >= phoneRule.minVal && parseInt <= phoneRule.maxVal && str.length() <= phoneRule.maxLen) {
                if (z) {
                    if (((phoneRule.flag12 & 3) == 0 && str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                        return phoneRule.format(str, str2, str3);
                    }
                } else if ((str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                    return phoneRule.format(str, str2, str3);
                }
            }
        }
        if (!z) {
            if (str2 != null) {
                it = this.rules.iterator();
                while (it.hasNext()) {
                    phoneRule = (PhoneRule) it.next();
                    if (parseInt >= phoneRule.minVal && parseInt <= phoneRule.maxVal && str.length() <= phoneRule.maxLen) {
                        if (str3 == null || (phoneRule.flag12 & 1) != 0) {
                            return phoneRule.format(str, str2, str3);
                        }
                    }
                }
            } else if (str3 != null) {
                it = this.rules.iterator();
                while (it.hasNext()) {
                    phoneRule = (PhoneRule) it.next();
                    if (parseInt >= phoneRule.minVal && parseInt <= phoneRule.maxVal && str.length() <= phoneRule.maxLen) {
                        if (str2 == null || (phoneRule.flag12 & 2) != 0) {
                            return phoneRule.format(str, str2, str3);
                        }
                    }
                }
            }
        }
        return null;
    }

    boolean isValid(String str, String str2, String str3, boolean z) {
        if (str.length() < this.matchLen) {
            return false;
        }
        Matcher matcher = pattern.matcher(str.substring(0, this.matchLen));
        if (matcher.find()) {
            int parseInt = Integer.parseInt(matcher.group(0));
        } else {
            boolean z2 = false;
        }
        Iterator it = this.rules.iterator();
        while (it.hasNext()) {
            PhoneRule phoneRule = (PhoneRule) it.next();
            if (parseInt >= phoneRule.minVal && parseInt <= phoneRule.maxVal && str.length() == phoneRule.maxLen) {
                if (z) {
                    if (((phoneRule.flag12 & 3) == 0 && str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                        return true;
                    }
                } else if ((str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                    return true;
                }
            }
        }
        if (z) {
            return false;
        }
        if (str2 != null && !this.hasRuleWithIntlPrefix) {
            it = this.rules.iterator();
            while (it.hasNext()) {
                phoneRule = (PhoneRule) it.next();
                if (parseInt >= phoneRule.minVal && parseInt <= phoneRule.maxVal && str.length() == phoneRule.maxLen) {
                    if (str3 == null || (phoneRule.flag12 & 1) != 0) {
                        return true;
                    }
                }
            }
            return false;
        } else if (str3 == null || this.hasRuleWithTrunkPrefix) {
            return false;
        } else {
            it = this.rules.iterator();
            while (it.hasNext()) {
                phoneRule = (PhoneRule) it.next();
                if (parseInt >= phoneRule.minVal && parseInt <= phoneRule.maxVal && str.length() == phoneRule.maxLen) {
                    if (str2 == null || (phoneRule.flag12 & 2) != 0) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
