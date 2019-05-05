package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.internal.zzsi;
import com.hanista.mobogram.tgnet.TLRPC;

public class zzaa {
    private static String CS;
    private static int CT;
    private static Object zzaok;
    private static boolean zzcdp;

    static {
        zzaok = new Object();
    }

    public static String zzcg(Context context) {
        zzci(context);
        return CS;
    }

    public static int zzch(Context context) {
        zzci(context);
        return CT;
    }

    private static void zzci(Context context) {
        synchronized (zzaok) {
            if (zzcdp) {
                return;
            }
            zzcdp = true;
            try {
                Bundle bundle = zzsi.zzcr(context).getApplicationInfo(context.getPackageName(), TLRPC.USER_FLAG_UNUSED).metaData;
                if (bundle == null) {
                    return;
                }
                CS = bundle.getString("com.google.app.id");
                CT = bundle.getInt("com.google.android.gms.version");
            } catch (Throwable e) {
                Log.wtf("MetadataValueReader", "This should never happen.", e);
            }
        }
    }
}
