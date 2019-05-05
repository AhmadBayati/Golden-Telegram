package com.google.android.gms.internal;

import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class zzapr extends zzaot<Object> {
    public static final zzaou bmp;
    private final zzaob bll;

    /* renamed from: com.google.android.gms.internal.zzapr.1 */
    static class C01651 implements zzaou {
        C01651() {
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == Object.class ? new zzapr(null) : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapr.2 */
    static /* synthetic */ class C01662 {
        static final /* synthetic */ int[] bmF;

        static {
            bmF = new int[zzapz.values().length];
            try {
                bmF[zzapz.BEGIN_ARRAY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                bmF[zzapz.BEGIN_OBJECT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                bmF[zzapz.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                bmF[zzapz.NUMBER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                bmF[zzapz.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                bmF[zzapz.NULL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    static {
        bmp = new C01651();
    }

    private zzapr(zzaob com_google_android_gms_internal_zzaob) {
        this.bll = com_google_android_gms_internal_zzaob;
    }

    public void zza(zzaqa com_google_android_gms_internal_zzaqa, Object obj) {
        if (obj == null) {
            com_google_android_gms_internal_zzaqa.bx();
            return;
        }
        zzaot zzk = this.bll.zzk(obj.getClass());
        if (zzk instanceof zzapr) {
            com_google_android_gms_internal_zzaqa.bv();
            com_google_android_gms_internal_zzaqa.bw();
            return;
        }
        zzk.zza(com_google_android_gms_internal_zzaqa, obj);
    }

    public Object zzb(zzapy com_google_android_gms_internal_zzapy) {
        switch (C01662.bmF[com_google_android_gms_internal_zzapy.bn().ordinal()]) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                List arrayList = new ArrayList();
                com_google_android_gms_internal_zzapy.beginArray();
                while (com_google_android_gms_internal_zzapy.hasNext()) {
                    arrayList.add(zzb(com_google_android_gms_internal_zzapy));
                }
                com_google_android_gms_internal_zzapy.endArray();
                return arrayList;
            case VideoPlayer.STATE_PREPARING /*2*/:
                Map com_google_android_gms_internal_zzapf = new zzapf();
                com_google_android_gms_internal_zzapy.beginObject();
                while (com_google_android_gms_internal_zzapy.hasNext()) {
                    com_google_android_gms_internal_zzapf.put(com_google_android_gms_internal_zzapy.nextName(), zzb(com_google_android_gms_internal_zzapy));
                }
                com_google_android_gms_internal_zzapy.endObject();
                return com_google_android_gms_internal_zzapf;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return com_google_android_gms_internal_zzapy.nextString();
            case VideoPlayer.STATE_READY /*4*/:
                return Double.valueOf(com_google_android_gms_internal_zzapy.nextDouble());
            case VideoPlayer.STATE_ENDED /*5*/:
                return Boolean.valueOf(com_google_android_gms_internal_zzapy.nextBoolean());
            case Method.TRACE /*6*/:
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }
}
