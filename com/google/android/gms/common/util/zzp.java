package com.google.android.gms.common.util;

import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class zzp {
    private static final Pattern EW;
    private static final Pattern EX;

    static {
        EW = Pattern.compile("\\\\.");
        EX = Pattern.compile("[\\\\\"/\b\f\n\r\t]");
    }

    public static boolean zzf(Object obj, Object obj2) {
        if (obj == null && obj2 == null) {
            return true;
        }
        if (obj == null || obj2 == null) {
            return false;
        }
        if ((obj instanceof JSONObject) && (obj2 instanceof JSONObject)) {
            JSONObject jSONObject = (JSONObject) obj;
            JSONObject jSONObject2 = (JSONObject) obj2;
            if (jSONObject.length() != jSONObject2.length()) {
                return false;
            }
            Iterator keys = jSONObject.keys();
            while (keys.hasNext()) {
                String str = (String) keys.next();
                if (!jSONObject2.has(str)) {
                    return false;
                }
                try {
                    if (!zzf(jSONObject.get(str), jSONObject2.get(str))) {
                        return false;
                    }
                } catch (JSONException e) {
                    return false;
                }
            }
            return true;
        } else if (!(obj instanceof JSONArray) || !(obj2 instanceof JSONArray)) {
            return obj.equals(obj2);
        } else {
            JSONArray jSONArray = (JSONArray) obj;
            JSONArray jSONArray2 = (JSONArray) obj2;
            if (jSONArray.length() != jSONArray2.length()) {
                return false;
            }
            int i = 0;
            while (i < jSONArray.length()) {
                try {
                    if (!zzf(jSONArray.get(i), jSONArray2.get(i))) {
                        return false;
                    }
                    i++;
                } catch (JSONException e2) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String zzii(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        Matcher matcher = EX.matcher(str);
        StringBuffer stringBuffer = null;
        while (matcher.find()) {
            if (stringBuffer == null) {
                stringBuffer = new StringBuffer();
            }
            switch (matcher.group().charAt(0)) {
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    matcher.appendReplacement(stringBuffer, "\\\\b");
                    break;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    matcher.appendReplacement(stringBuffer, "\\\\t");
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    matcher.appendReplacement(stringBuffer, "\\\\n");
                    break;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    matcher.appendReplacement(stringBuffer, "\\\\f");
                    break;
                case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                    matcher.appendReplacement(stringBuffer, "\\\\r");
                    break;
                case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                    matcher.appendReplacement(stringBuffer, "\\\\\\\"");
                    break;
                case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                    matcher.appendReplacement(stringBuffer, "\\\\/");
                    break;
                case '\\':
                    matcher.appendReplacement(stringBuffer, "\\\\\\\\");
                    break;
                default:
                    break;
            }
        }
        if (stringBuffer == null) {
            return str;
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
}
