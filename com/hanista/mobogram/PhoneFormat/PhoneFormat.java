package com.hanista.mobogram.PhoneFormat;

import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PhoneFormat {
    private static volatile PhoneFormat Instance;
    public ByteBuffer buffer;
    public HashMap<String, ArrayList<String>> callingCodeCountries;
    public HashMap<String, CallingCodeInfo> callingCodeData;
    public HashMap<String, Integer> callingCodeOffsets;
    public HashMap<String, String> countryCallingCode;
    public byte[] data;
    public String defaultCallingCode;
    public String defaultCountry;
    private boolean initialzed;

    static {
        Instance = null;
    }

    public PhoneFormat() {
        this.initialzed = false;
        init(null);
    }

    public PhoneFormat(String str) {
        this.initialzed = false;
        init(str);
    }

    public static PhoneFormat getInstance() {
        PhoneFormat phoneFormat = Instance;
        if (phoneFormat == null) {
            synchronized (PhoneFormat.class) {
                phoneFormat = Instance;
                if (phoneFormat == null) {
                    phoneFormat = new PhoneFormat();
                    Instance = phoneFormat;
                }
            }
        }
        return phoneFormat;
    }

    public static String strip(String str) {
        StringBuilder stringBuilder = new StringBuilder(str);
        String str2 = "0123456789+*#";
        for (int length = stringBuilder.length() - 1; length >= 0; length--) {
            if (!str2.contains(stringBuilder.substring(length, length + 1))) {
                stringBuilder.deleteCharAt(length);
            }
        }
        return stringBuilder.toString();
    }

    public static String stripExceptNumbers(String str) {
        return stripExceptNumbers(str, false);
    }

    public static String stripExceptNumbers(String str, boolean z) {
        StringBuilder stringBuilder = new StringBuilder(str);
        String str2 = "0123456789";
        if (z) {
            str2 = str2 + "+";
        }
        for (int length = stringBuilder.length() - 1; length >= 0; length--) {
            if (!str2.contains(stringBuilder.substring(length, length + 1))) {
                stringBuilder.deleteCharAt(length);
            }
        }
        return stringBuilder.toString();
    }

    public String callingCodeForCountryCode(String str) {
        return (String) this.countryCallingCode.get(str.toLowerCase());
    }

    public CallingCodeInfo callingCodeInfo(String str) {
        CallingCodeInfo callingCodeInfo = (CallingCodeInfo) this.callingCodeData.get(str);
        if (callingCodeInfo != null) {
            return callingCodeInfo;
        }
        Integer num = (Integer) this.callingCodeOffsets.get(str);
        if (num == null) {
            return callingCodeInfo;
        }
        byte[] bArr = this.data;
        int intValue = num.intValue();
        CallingCodeInfo callingCodeInfo2 = new CallingCodeInfo();
        callingCodeInfo2.callingCode = str;
        callingCodeInfo2.countries = (ArrayList) this.callingCodeCountries.get(str);
        this.callingCodeData.put(str, callingCodeInfo2);
        short value16 = value16(intValue);
        int i = (intValue + 2) + 2;
        short value162 = value16(i);
        i = (i + 2) + 2;
        short value163 = value16(i);
        i = (i + 2) + 2;
        ArrayList arrayList = new ArrayList(5);
        while (true) {
            String valueString = valueString(i);
            if (valueString.length() == 0) {
                break;
            }
            arrayList.add(valueString);
            i += valueString.length() + 1;
        }
        callingCodeInfo2.trunkPrefixes = arrayList;
        i++;
        arrayList = new ArrayList(5);
        while (true) {
            valueString = valueString(i);
            if (valueString.length() == 0) {
                break;
            }
            arrayList.add(valueString);
            i += valueString.length() + 1;
        }
        callingCodeInfo2.intlPrefixes = arrayList;
        ArrayList arrayList2 = new ArrayList(value163);
        int i2 = intValue + value16;
        for (short s = (short) 0; s < value163; s++) {
            RuleSet ruleSet = new RuleSet();
            ruleSet.matchLen = value16(i2);
            i = i2 + 2;
            short value164 = value16(i);
            i2 = i + 2;
            ArrayList arrayList3 = new ArrayList(value164);
            for (short s2 = (short) 0; s2 < value164; s2++) {
                PhoneRule phoneRule = new PhoneRule();
                phoneRule.minVal = value32(i2);
                i2 += 4;
                phoneRule.maxVal = value32(i2);
                i2 += 4;
                int i3 = i2 + 1;
                phoneRule.byte8 = bArr[i2];
                i2 = i3 + 1;
                phoneRule.maxLen = bArr[i3];
                i3 = i2 + 1;
                phoneRule.otherFlag = bArr[i2];
                i2 = i3 + 1;
                phoneRule.prefixLen = bArr[i3];
                i3 = i2 + 1;
                phoneRule.flag12 = bArr[i2];
                i2 = i3 + 1;
                phoneRule.flag13 = bArr[i3];
                short value165 = value16(i2);
                i2 += 2;
                phoneRule.format = valueString(value165 + ((intValue + value16) + value162));
                i3 = phoneRule.format.indexOf("[[");
                if (i3 != -1) {
                    int indexOf = phoneRule.format.indexOf("]]");
                    r20 = new Object[2];
                    r20[0] = phoneRule.format.substring(0, i3);
                    r20[1] = phoneRule.format.substring(indexOf + 2);
                    phoneRule.format = String.format("%s%s", r20);
                }
                arrayList3.add(phoneRule);
                if (phoneRule.hasIntlPrefix) {
                    ruleSet.hasRuleWithIntlPrefix = true;
                }
                if (phoneRule.hasTrunkPrefix) {
                    ruleSet.hasRuleWithTrunkPrefix = true;
                }
            }
            ruleSet.rules = arrayList3;
            arrayList2.add(ruleSet);
        }
        callingCodeInfo2.ruleSets = arrayList2;
        return callingCodeInfo2;
    }

    public ArrayList countriesForCallingCode(String str) {
        Object substring;
        if (str.startsWith("+")) {
            substring = str.substring(1);
        }
        return (ArrayList) this.callingCodeCountries.get(substring);
    }

    public String defaultCallingCode() {
        return callingCodeForCountryCode(this.defaultCountry);
    }

    public CallingCodeInfo findCallingCodeInfo(String str) {
        CallingCodeInfo callingCodeInfo = null;
        int i = 0;
        while (i < 3 && i < str.length()) {
            callingCodeInfo = callingCodeInfo(str.substring(0, i + 1));
            if (callingCodeInfo != null) {
                break;
            }
            i++;
        }
        return callingCodeInfo;
    }

    public String format(String str) {
        if (!this.initialzed) {
            return str;
        }
        try {
            String strip = strip(str);
            String substring;
            if (strip.startsWith("+")) {
                substring = strip.substring(1);
                CallingCodeInfo findCallingCodeInfo = findCallingCodeInfo(substring);
                if (findCallingCodeInfo == null) {
                    return str;
                }
                return "+" + findCallingCodeInfo.format(substring);
            }
            CallingCodeInfo callingCodeInfo = callingCodeInfo(this.defaultCallingCode);
            if (callingCodeInfo == null) {
                return str;
            }
            substring = callingCodeInfo.matchingAccessCode(strip);
            if (substring == null) {
                return callingCodeInfo.format(strip);
            }
            strip = strip.substring(substring.length());
            callingCodeInfo = findCallingCodeInfo(strip);
            if (callingCodeInfo != null) {
                strip = callingCodeInfo.format(strip);
            }
            if (strip.length() == 0) {
                return substring;
            }
            return String.format("%s %s", new Object[]{substring, strip});
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return str;
        }
    }

    public void init(String str) {
        InputStream open;
        ByteArrayOutputStream byteArrayOutputStream;
        Throwable e;
        Exception e2;
        InputStream inputStream = null;
        try {
            open = ApplicationLoader.applicationContext.getAssets().open("PhoneFormats.dat");
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byte[] bArr = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
                    while (true) {
                        int read = open.read(bArr, 0, TLRPC.MESSAGE_FLAG_HAS_VIEWS);
                        if (read == -1) {
                            break;
                        }
                        byteArrayOutputStream.write(bArr, 0, read);
                    }
                    this.data = byteArrayOutputStream.toByteArray();
                    this.buffer = ByteBuffer.wrap(this.data);
                    this.buffer.order(ByteOrder.LITTLE_ENDIAN);
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Throwable e3) {
                            FileLog.m18e("tmessages", e3);
                        }
                    }
                    if (open != null) {
                        try {
                            open.close();
                        } catch (Throwable e32) {
                            FileLog.m18e("tmessages", e32);
                        }
                    }
                    if (str == null || str.length() == 0) {
                        this.defaultCountry = Locale.getDefault().getCountry().toLowerCase();
                    } else {
                        this.defaultCountry = str;
                    }
                    this.callingCodeOffsets = new HashMap(NalUnitUtil.EXTENDED_SAR);
                    this.callingCodeCountries = new HashMap(NalUnitUtil.EXTENDED_SAR);
                    this.callingCodeData = new HashMap(10);
                    this.countryCallingCode = new HashMap(NalUnitUtil.EXTENDED_SAR);
                    parseDataHeader();
                    this.initialzed = true;
                } catch (Exception e4) {
                    e2 = e4;
                    inputStream = open;
                    try {
                        e2.printStackTrace();
                        if (byteArrayOutputStream != null) {
                            try {
                                byteArrayOutputStream.close();
                            } catch (Throwable e322) {
                                FileLog.m18e("tmessages", e322);
                            }
                        }
                        if (inputStream == null) {
                            try {
                                inputStream.close();
                            } catch (Throwable e3222) {
                                FileLog.m18e("tmessages", e3222);
                            }
                        }
                    } catch (Throwable th) {
                        e3222 = th;
                        open = inputStream;
                        if (byteArrayOutputStream != null) {
                            try {
                                byteArrayOutputStream.close();
                            } catch (Throwable e5) {
                                FileLog.m18e("tmessages", e5);
                            }
                        }
                        if (open != null) {
                            try {
                                open.close();
                            } catch (Throwable e52) {
                                FileLog.m18e("tmessages", e52);
                            }
                        }
                        throw e3222;
                    }
                } catch (Throwable th2) {
                    e3222 = th2;
                    if (byteArrayOutputStream != null) {
                        byteArrayOutputStream.close();
                    }
                    if (open != null) {
                        open.close();
                    }
                    throw e3222;
                }
            } catch (Exception e6) {
                e2 = e6;
                byteArrayOutputStream = null;
                inputStream = open;
                e2.printStackTrace();
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (inputStream == null) {
                    inputStream.close();
                }
            } catch (Throwable th3) {
                e3222 = th3;
                byteArrayOutputStream = null;
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (open != null) {
                    open.close();
                }
                throw e3222;
            }
        } catch (Exception e7) {
            e2 = e7;
            byteArrayOutputStream = null;
            e2.printStackTrace();
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            if (inputStream == null) {
                inputStream.close();
            }
        } catch (Throwable th4) {
            e3222 = th4;
            byteArrayOutputStream = null;
            open = null;
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            if (open != null) {
                open.close();
            }
            throw e3222;
        }
    }

    public boolean isPhoneNumberValid(String str) {
        if (!this.initialzed) {
            return true;
        }
        String strip = strip(str);
        CallingCodeInfo findCallingCodeInfo;
        if (strip.startsWith("+")) {
            strip = strip.substring(1);
            findCallingCodeInfo = findCallingCodeInfo(strip);
            return findCallingCodeInfo != null && findCallingCodeInfo.isValidPhoneNumber(strip);
        } else {
            findCallingCodeInfo = callingCodeInfo(this.defaultCallingCode);
            if (findCallingCodeInfo == null) {
                return false;
            }
            String matchingAccessCode = findCallingCodeInfo.matchingAccessCode(strip);
            if (matchingAccessCode == null) {
                return findCallingCodeInfo.isValidPhoneNumber(strip);
            }
            strip = strip.substring(matchingAccessCode.length());
            if (strip.length() == 0) {
                return false;
            }
            findCallingCodeInfo = findCallingCodeInfo(strip);
            return findCallingCodeInfo != null && findCallingCodeInfo.isValidPhoneNumber(strip);
        }
    }

    public void parseDataHeader() {
        int value32 = value32(0);
        int i = (value32 * 12) + 4;
        int i2 = 4;
        int i3 = 0;
        while (i3 < value32) {
            String valueString = valueString(i2);
            i2 += 4;
            String valueString2 = valueString(i2);
            i2 += 4;
            int value322 = value32(i2) + i;
            int i4 = i2 + 4;
            if (valueString2.equals(this.defaultCountry)) {
                this.defaultCallingCode = valueString;
            }
            this.countryCallingCode.put(valueString2, valueString);
            this.callingCodeOffsets.put(valueString, Integer.valueOf(value322));
            ArrayList arrayList = (ArrayList) this.callingCodeCountries.get(valueString);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.callingCodeCountries.put(valueString, arrayList);
            }
            arrayList.add(valueString2);
            i3++;
            i2 = i4;
        }
        if (this.defaultCallingCode != null) {
            callingCodeInfo(this.defaultCallingCode);
        }
    }

    short value16(int i) {
        if (i + 2 > this.data.length) {
            return (short) 0;
        }
        this.buffer.position(i);
        return this.buffer.getShort();
    }

    int value32(int i) {
        if (i + 4 > this.data.length) {
            return 0;
        }
        this.buffer.position(i);
        return this.buffer.getInt();
    }

    public String valueString(int i) {
        int i2 = i;
        while (i2 < this.data.length) {
            try {
                if (this.data[i2] == null) {
                    return i == i2 - i ? TtmlNode.ANONYMOUS_REGION_ID : new String(this.data, i, i2 - i);
                } else {
                    i2++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }
}
