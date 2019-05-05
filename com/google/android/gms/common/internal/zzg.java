package com.google.android.gms.common.internal;

import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class zzg {
    public static final zzg BB;
    public static final zzg BC;
    public static final zzg BD;
    public static final zzg BE;
    public static final zzg BF;
    public static final zzg BG;
    public static final zzg BH;
    public static final zzg BI;
    public static final zzg BJ;
    public static final zzg BK;
    public static final zzg BL;
    public static final zzg BM;
    public static final zzg BN;
    public static final zzg BO;
    public static final zzg BP;

    /* renamed from: com.google.android.gms.common.internal.zzg.11 */
    class AnonymousClass11 extends zzg {
        final /* synthetic */ char BV;

        AnonymousClass11(char c) {
            this.BV = c;
        }

        public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
            return com_google_android_gms_common_internal_zzg.zzd(this.BV) ? com_google_android_gms_common_internal_zzg : super.zza(com_google_android_gms_common_internal_zzg);
        }

        public boolean zzd(char c) {
            return c == this.BV;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.1 */
    class C00901 extends zzg {
        C00901() {
        }

        public boolean zzd(char c) {
            return Character.isDigit(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.2 */
    class C00912 extends zzg {
        final /* synthetic */ char BQ;
        final /* synthetic */ char BR;

        C00912(char c, char c2) {
            this.BQ = c;
            this.BR = c2;
        }

        public boolean zzd(char c) {
            return c == this.BQ || c == this.BR;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.3 */
    class C00923 extends zzg {
        final /* synthetic */ char[] BS;

        C00923(char[] cArr) {
            this.BS = cArr;
        }

        public boolean zzd(char c) {
            return Arrays.binarySearch(this.BS, c) >= 0;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.4 */
    class C00934 extends zzg {
        final /* synthetic */ char BT;
        final /* synthetic */ char BU;

        C00934(char c, char c2) {
            this.BT = c;
            this.BU = c2;
        }

        public boolean zzd(char c) {
            return this.BT <= c && c <= this.BU;
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.5 */
    class C00945 extends zzg {
        C00945() {
        }

        public boolean zzd(char c) {
            return Character.isLetter(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.6 */
    class C00956 extends zzg {
        C00956() {
        }

        public boolean zzd(char c) {
            return Character.isLetterOrDigit(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.7 */
    class C00967 extends zzg {
        C00967() {
        }

        public boolean zzd(char c) {
            return Character.isUpperCase(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.8 */
    class C00978 extends zzg {
        C00978() {
        }

        public boolean zzd(char c) {
            return Character.isLowerCase(c);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzg.9 */
    class C00989 extends zzg {
        C00989() {
        }

        public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
            zzac.zzy(com_google_android_gms_common_internal_zzg);
            return this;
        }

        public boolean zzb(CharSequence charSequence) {
            zzac.zzy(charSequence);
            return true;
        }

        public boolean zzd(char c) {
            return true;
        }
    }

    private static class zza extends zzg {
        List<zzg> BW;

        zza(List<zzg> list) {
            this.BW = list;
        }

        public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
            List arrayList = new ArrayList(this.BW);
            arrayList.add((zzg) zzac.zzy(com_google_android_gms_common_internal_zzg));
            return new zza(arrayList);
        }

        public boolean zzd(char c) {
            for (zzg zzd : this.BW) {
                if (zzd.zzd(c)) {
                    return true;
                }
            }
            return false;
        }
    }

    static {
        BB = zza((CharSequence) "\t\n\u000b\f\r \u0085\u1680\u2028\u2029\u205f\u3000\u00a0\u180e\u202f").zza(zza('\u2000', '\u200a'));
        BC = zza((CharSequence) "\t\n\u000b\f\r \u0085\u1680\u2028\u2029\u205f\u3000").zza(zza('\u2000', '\u2006')).zza(zza('\u2008', '\u200a'));
        BD = zza('\u0000', '\u007f');
        zzg zza = zza('0', '9');
        zzg com_google_android_gms_common_internal_zzg = zza;
        for (char c : "\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".toCharArray()) {
            com_google_android_gms_common_internal_zzg = com_google_android_gms_common_internal_zzg.zza(zza(c, (char) (c + 9)));
        }
        BE = com_google_android_gms_common_internal_zzg;
        BF = zza('\t', '\r').zza(zza('\u001c', ' ')).zza(zzc('\u1680')).zza(zzc('\u180e')).zza(zza('\u2000', '\u2006')).zza(zza('\u2008', '\u200b')).zza(zza('\u2028', '\u2029')).zza(zzc('\u205f')).zza(zzc('\u3000'));
        BG = new C00901();
        BH = new C00945();
        BI = new C00956();
        BJ = new C00967();
        BK = new C00978();
        BL = zza('\u0000', '\u001f').zza(zza('\u007f', '\u009f'));
        BM = zza('\u0000', ' ').zza(zza('\u007f', '\u00a0')).zza(zzc('\u00ad')).zza(zza('\u0600', '\u0603')).zza(zza((CharSequence) "\u06dd\u070f\u1680\u17b4\u17b5\u180e")).zza(zza('\u2000', '\u200f')).zza(zza('\u2028', '\u202f')).zza(zza('\u205f', '\u2064')).zza(zza('\u206a', '\u206f')).zza(zzc('\u3000')).zza(zza('\ud800', '\uf8ff')).zza(zza((CharSequence) "\ufeff\ufff9\ufffa\ufffb"));
        BN = zza('\u0000', '\u04f9').zza(zzc('\u05be')).zza(zza('\u05d0', '\u05ea')).zza(zzc('\u05f3')).zza(zzc('\u05f4')).zza(zza('\u0600', '\u06ff')).zza(zza('\u0750', '\u077f')).zza(zza('\u0e00', '\u0e7f')).zza(zza('\u1e00', '\u20af')).zza(zza('\u2100', '\u213a')).zza(zza('\ufb50', '\ufdff')).zza(zza('\ufe70', '\ufeff')).zza(zza('\uff61', '\uffdc'));
        BO = new C00989();
        BP = new zzg() {
            public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
                return (zzg) zzac.zzy(com_google_android_gms_common_internal_zzg);
            }

            public boolean zzb(CharSequence charSequence) {
                return charSequence.length() == 0;
            }

            public boolean zzd(char c) {
                return false;
            }
        };
    }

    public static zzg zza(char c, char c2) {
        zzac.zzbs(c2 >= c);
        return new C00934(c, c2);
    }

    public static zzg zza(CharSequence charSequence) {
        switch (charSequence.length()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return BP;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return zzc(charSequence.charAt(0));
            case VideoPlayer.STATE_PREPARING /*2*/:
                return new C00912(charSequence.charAt(0), charSequence.charAt(1));
            default:
                char[] toCharArray = charSequence.toString().toCharArray();
                Arrays.sort(toCharArray);
                return new C00923(toCharArray);
        }
    }

    public static zzg zzc(char c) {
        return new AnonymousClass11(c);
    }

    public zzg zza(zzg com_google_android_gms_common_internal_zzg) {
        return new zza(Arrays.asList(new zzg[]{this, (zzg) zzac.zzy(com_google_android_gms_common_internal_zzg)}));
    }

    public boolean zzb(CharSequence charSequence) {
        for (int length = charSequence.length() - 1; length >= 0; length--) {
            if (!zzd(charSequence.charAt(length))) {
                return false;
            }
        }
        return true;
    }

    public abstract boolean zzd(char c);
}
