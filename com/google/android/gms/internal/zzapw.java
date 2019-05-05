package com.google.android.gms.internal;

import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;

public final class zzapw {
    public static final zzaot<Class> bmS;
    public static final zzaou bmT;
    public static final zzaot<BitSet> bmU;
    public static final zzaou bmV;
    public static final zzaot<Boolean> bmW;
    public static final zzaot<Boolean> bmX;
    public static final zzaou bmY;
    public static final zzaot<Number> bmZ;
    public static final zzaot<UUID> bnA;
    public static final zzaou bnB;
    public static final zzaou bnC;
    public static final zzaot<Calendar> bnD;
    public static final zzaou bnE;
    public static final zzaot<Locale> bnF;
    public static final zzaou bnG;
    public static final zzaot<zzaoh> bnH;
    public static final zzaou bnI;
    public static final zzaou bnJ;
    public static final zzaou bna;
    public static final zzaot<Number> bnb;
    public static final zzaou bnc;
    public static final zzaot<Number> bnd;
    public static final zzaou bne;
    public static final zzaot<Number> bnf;
    public static final zzaot<Number> bng;
    public static final zzaot<Number> bnh;
    public static final zzaot<Number> bni;
    public static final zzaou bnj;
    public static final zzaot<Character> bnk;
    public static final zzaou bnl;
    public static final zzaot<String> bnm;
    public static final zzaot<BigDecimal> bnn;
    public static final zzaot<BigInteger> bno;
    public static final zzaou bnp;
    public static final zzaot<StringBuilder> bnq;
    public static final zzaou bnr;
    public static final zzaot<StringBuffer> bns;
    public static final zzaou bnt;
    public static final zzaot<URL> bnu;
    public static final zzaou bnv;
    public static final zzaot<URI> bnw;
    public static final zzaou bnx;
    public static final zzaot<InetAddress> bny;
    public static final zzaou bnz;

    /* renamed from: com.google.android.gms.internal.zzapw.1 */
    static class C01711 extends zzaot<Class> {
        C01711() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Class cls) {
            if (cls == null) {
                com_google_android_gms_internal_zzaqa.bx();
            } else {
                String valueOf = String.valueOf(cls.getName());
                throw new UnsupportedOperationException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Attempted to serialize java.lang.Class: ").append(valueOf).append(". Forgot to register a type adapter?").toString());
            }
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzo(com_google_android_gms_internal_zzapy);
        }

        public Class zzo(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.20 */
    static class AnonymousClass20 implements zzaou {
        final /* synthetic */ zzapx blO;
        final /* synthetic */ zzaot bnM;

        AnonymousClass20(zzapx com_google_android_gms_internal_zzapx, zzaot com_google_android_gms_internal_zzaot) {
            this.blO = com_google_android_gms_internal_zzapx;
            this.bnM = com_google_android_gms_internal_zzaot;
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.equals(this.blO) ? this.bnM : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.21 */
    static class AnonymousClass21 implements zzaou {
        final /* synthetic */ zzaot bnM;
        final /* synthetic */ Class bnN;

        AnonymousClass21(Class cls, zzaot com_google_android_gms_internal_zzaot) {
            this.bnN = cls;
            this.bnM = com_google_android_gms_internal_zzaot;
        }

        public String toString() {
            String valueOf = String.valueOf(this.bnN.getName());
            String valueOf2 = String.valueOf(this.bnM);
            return new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append("Factory[type=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == this.bnN ? this.bnM : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.22 */
    static class AnonymousClass22 implements zzaou {
        final /* synthetic */ zzaot bnM;
        final /* synthetic */ Class bnO;
        final /* synthetic */ Class bnP;

        AnonymousClass22(Class cls, Class cls2, zzaot com_google_android_gms_internal_zzaot) {
            this.bnO = cls;
            this.bnP = cls2;
            this.bnM = com_google_android_gms_internal_zzaot;
        }

        public String toString() {
            String valueOf = String.valueOf(this.bnP.getName());
            String valueOf2 = String.valueOf(this.bnO.getName());
            String valueOf3 = String.valueOf(this.bnM);
            return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            Class by = com_google_android_gms_internal_zzapx_T.by();
            return (by == this.bnO || by == this.bnP) ? this.bnM : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.24 */
    static class AnonymousClass24 implements zzaou {
        final /* synthetic */ zzaot bnM;
        final /* synthetic */ Class bnQ;
        final /* synthetic */ Class bnR;

        AnonymousClass24(Class cls, Class cls2, zzaot com_google_android_gms_internal_zzaot) {
            this.bnQ = cls;
            this.bnR = cls2;
            this.bnM = com_google_android_gms_internal_zzaot;
        }

        public String toString() {
            String valueOf = String.valueOf(this.bnQ.getName());
            String valueOf2 = String.valueOf(this.bnR.getName());
            String valueOf3 = String.valueOf(this.bnM);
            return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            Class by = com_google_android_gms_internal_zzapx_T.by();
            return (by == this.bnQ || by == this.bnR) ? this.bnM : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.25 */
    static class AnonymousClass25 implements zzaou {
        final /* synthetic */ zzaot bnM;
        final /* synthetic */ Class bnS;

        AnonymousClass25(Class cls, zzaot com_google_android_gms_internal_zzaot) {
            this.bnS = cls;
            this.bnM = com_google_android_gms_internal_zzaot;
        }

        public String toString() {
            String valueOf = String.valueOf(this.bnS.getName());
            String valueOf2 = String.valueOf(this.bnM);
            return new StringBuilder((String.valueOf(valueOf).length() + 32) + String.valueOf(valueOf2).length()).append("Factory[typeHierarchy=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return this.bnS.isAssignableFrom(com_google_android_gms_internal_zzapx_T.by()) ? this.bnM : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.26 */
    static /* synthetic */ class AnonymousClass26 {
        static final /* synthetic */ int[] bmF;

        static {
            bmF = new int[zzapz.values().length];
            try {
                bmF[zzapz.NUMBER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                bmF[zzapz.BOOLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                bmF[zzapz.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                bmF[zzapz.NULL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                bmF[zzapz.BEGIN_ARRAY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                bmF[zzapz.BEGIN_OBJECT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                bmF[zzapz.END_DOCUMENT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                bmF[zzapz.NAME.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                bmF[zzapz.END_OBJECT.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                bmF[zzapz.END_ARRAY.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.2 */
    static class C01722 extends zzaot<Number> {
        C01722() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return Double.valueOf(com_google_android_gms_internal_zzapy.nextDouble());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.3 */
    static class C01733 extends zzaot<Number> {
        C01733() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) {
            zzapz bn = com_google_android_gms_internal_zzapy.bn();
            switch (AnonymousClass26.bmF[bn.ordinal()]) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    return new zzape(com_google_android_gms_internal_zzapy.nextString());
                case VideoPlayer.STATE_READY /*4*/:
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                default:
                    String valueOf = String.valueOf(bn);
                    throw new zzaoq(new StringBuilder(String.valueOf(valueOf).length() + 23).append("Expecting number, got: ").append(valueOf).toString());
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.4 */
    static class C01744 extends zzaot<Character> {
        C01744() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Character ch) {
            com_google_android_gms_internal_zzaqa.zzut(ch == null ? null : String.valueOf(ch));
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzp(com_google_android_gms_internal_zzapy);
        }

        public Character zzp(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            String nextString = com_google_android_gms_internal_zzapy.nextString();
            if (nextString.length() == 1) {
                return Character.valueOf(nextString.charAt(0));
            }
            String str = "Expecting character, got: ";
            nextString = String.valueOf(nextString);
            throw new zzaoq(nextString.length() != 0 ? str.concat(nextString) : new String(str));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.5 */
    static class C01755 extends zzaot<String> {
        C01755() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, String str) {
            com_google_android_gms_internal_zzaqa.zzut(str);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzq(com_google_android_gms_internal_zzapy);
        }

        public String zzq(zzapy com_google_android_gms_internal_zzapy) {
            zzapz bn = com_google_android_gms_internal_zzapy.bn();
            if (bn != zzapz.NULL) {
                return bn == zzapz.BOOLEAN ? Boolean.toString(com_google_android_gms_internal_zzapy.nextBoolean()) : com_google_android_gms_internal_zzapy.nextString();
            } else {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.6 */
    static class C01766 extends zzaot<BigDecimal> {
        C01766() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, BigDecimal bigDecimal) {
            com_google_android_gms_internal_zzaqa.zza(bigDecimal);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzr(com_google_android_gms_internal_zzapy);
        }

        public BigDecimal zzr(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return new BigDecimal(com_google_android_gms_internal_zzapy.nextString());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.7 */
    static class C01777 extends zzaot<BigInteger> {
        C01777() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, BigInteger bigInteger) {
            com_google_android_gms_internal_zzaqa.zza(bigInteger);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzs(com_google_android_gms_internal_zzapy);
        }

        public BigInteger zzs(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return new BigInteger(com_google_android_gms_internal_zzapy.nextString());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.8 */
    static class C01788 extends zzaot<StringBuilder> {
        C01788() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, StringBuilder stringBuilder) {
            com_google_android_gms_internal_zzaqa.zzut(stringBuilder == null ? null : stringBuilder.toString());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzt(com_google_android_gms_internal_zzapy);
        }

        public StringBuilder zzt(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return new StringBuilder(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzapw.9 */
    static class C01799 extends zzaot<StringBuffer> {
        C01799() {
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, StringBuffer stringBuffer) {
            com_google_android_gms_internal_zzaqa.zzut(stringBuffer == null ? null : stringBuffer.toString());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzu(com_google_android_gms_internal_zzapy);
        }

        public StringBuffer zzu(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return new StringBuffer(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    }

    private static final class zza<T extends Enum<T>> extends zzaot<T> {
        private final Map<String, T> bnT;
        private final Map<T, String> bnU;

        public zza(Class<T> cls) {
            this.bnT = new HashMap();
            this.bnU = new HashMap();
            try {
                for (Enum enumR : (Enum[]) cls.getEnumConstants()) {
                    String name = enumR.name();
                    zzaow com_google_android_gms_internal_zzaow = (zzaow) cls.getField(name).getAnnotation(zzaow.class);
                    if (com_google_android_gms_internal_zzaow != null) {
                        name = com_google_android_gms_internal_zzaow.value();
                        for (Object put : com_google_android_gms_internal_zzaow.be()) {
                            this.bnT.put(put, enumR);
                        }
                    }
                    String str = name;
                    this.bnT.put(str, enumR);
                    this.bnU.put(enumR, str);
                }
            } catch (NoSuchFieldException e) {
                throw new AssertionError();
            }
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) {
            com_google_android_gms_internal_zzaqa.zzut(t == null ? null : (String) this.bnU.get(t));
        }

        public T zzaf(zzapy com_google_android_gms_internal_zzapy) {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return (Enum) this.bnT.get(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
            return zzaf(com_google_android_gms_internal_zzapy);
        }
    }

    static {
        bmS = new C01711();
        bmT = zza(Class.class, bmS);
        bmU = new zzaot<BitSet>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, BitSet bitSet) {
                if (bitSet == null) {
                    com_google_android_gms_internal_zzaqa.bx();
                    return;
                }
                com_google_android_gms_internal_zzaqa.bt();
                for (int i = 0; i < bitSet.length(); i++) {
                    com_google_android_gms_internal_zzaqa.zzcu((long) (bitSet.get(i) ? 1 : 0));
                }
                com_google_android_gms_internal_zzaqa.bu();
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzx(com_google_android_gms_internal_zzapy);
            }

            public BitSet zzx(zzapy com_google_android_gms_internal_zzapy) {
                String valueOf;
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                BitSet bitSet = new BitSet();
                com_google_android_gms_internal_zzapy.beginArray();
                zzapz bn = com_google_android_gms_internal_zzapy.bn();
                int i = 0;
                while (bn != zzapz.END_ARRAY) {
                    boolean z;
                    switch (AnonymousClass26.bmF[bn.ordinal()]) {
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                            if (com_google_android_gms_internal_zzapy.nextInt() == 0) {
                                z = false;
                                break;
                            }
                            z = true;
                            break;
                        case VideoPlayer.STATE_PREPARING /*2*/:
                            z = com_google_android_gms_internal_zzapy.nextBoolean();
                            break;
                        case VideoPlayer.STATE_BUFFERING /*3*/:
                            Object nextString = com_google_android_gms_internal_zzapy.nextString();
                            try {
                                if (Integer.parseInt(nextString) == 0) {
                                    z = false;
                                    break;
                                }
                                z = true;
                                break;
                            } catch (NumberFormatException e) {
                                String str = "Error: Expecting: bitset number value (1, 0), Found: ";
                                valueOf = String.valueOf(nextString);
                                throw new zzaoq(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                            }
                        default:
                            valueOf = String.valueOf(bn);
                            throw new zzaoq(new StringBuilder(String.valueOf(valueOf).length() + 27).append("Invalid bitset value type: ").append(valueOf).toString());
                    }
                    if (z) {
                        bitSet.set(i);
                    }
                    i++;
                    bn = com_google_android_gms_internal_zzapy.bn();
                }
                com_google_android_gms_internal_zzapy.endArray();
                return bitSet;
            }
        };
        bmV = zza(BitSet.class, bmU);
        bmW = new zzaot<Boolean>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Boolean bool) {
                if (bool == null) {
                    com_google_android_gms_internal_zzaqa.bx();
                } else {
                    com_google_android_gms_internal_zzaqa.zzdf(bool.booleanValue());
                }
            }

            public Boolean zzae(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return com_google_android_gms_internal_zzapy.bn() == zzapz.STRING ? Boolean.valueOf(Boolean.parseBoolean(com_google_android_gms_internal_zzapy.nextString())) : Boolean.valueOf(com_google_android_gms_internal_zzapy.nextBoolean());
                } else {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzae(com_google_android_gms_internal_zzapy);
            }
        };
        bmX = new zzaot<Boolean>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Boolean bool) {
                com_google_android_gms_internal_zzaqa.zzut(bool == null ? "null" : bool.toString());
            }

            public Boolean zzae(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return Boolean.valueOf(com_google_android_gms_internal_zzapy.nextString());
                }
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzae(com_google_android_gms_internal_zzapy);
            }
        };
        bmY = zza(Boolean.TYPE, Boolean.class, bmW);
        bmZ = new zzaot<Number>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) {
                com_google_android_gms_internal_zzaqa.zza(number);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzg(com_google_android_gms_internal_zzapy);
            }

            public Number zzg(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                try {
                    return Byte.valueOf((byte) com_google_android_gms_internal_zzapy.nextInt());
                } catch (Throwable e) {
                    throw new zzaoq(e);
                }
            }
        };
        bna = zza(Byte.TYPE, Byte.class, bmZ);
        bnb = new zzaot<Number>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) {
                com_google_android_gms_internal_zzaqa.zza(number);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzg(com_google_android_gms_internal_zzapy);
            }

            public Number zzg(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                try {
                    return Short.valueOf((short) com_google_android_gms_internal_zzapy.nextInt());
                } catch (Throwable e) {
                    throw new zzaoq(e);
                }
            }
        };
        bnc = zza(Short.TYPE, Short.class, bnb);
        bnd = new zzaot<Number>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) {
                com_google_android_gms_internal_zzaqa.zza(number);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzg(com_google_android_gms_internal_zzapy);
            }

            public Number zzg(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                try {
                    return Integer.valueOf(com_google_android_gms_internal_zzapy.nextInt());
                } catch (Throwable e) {
                    throw new zzaoq(e);
                }
            }
        };
        bne = zza(Integer.TYPE, Integer.class, bnd);
        bnf = new zzaot<Number>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) {
                com_google_android_gms_internal_zzaqa.zza(number);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzg(com_google_android_gms_internal_zzapy);
            }

            public Number zzg(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                try {
                    return Long.valueOf(com_google_android_gms_internal_zzapy.nextLong());
                } catch (Throwable e) {
                    throw new zzaoq(e);
                }
            }
        };
        bng = new zzaot<Number>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) {
                com_google_android_gms_internal_zzaqa.zza(number);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzg(com_google_android_gms_internal_zzapy);
            }

            public Number zzg(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return Float.valueOf((float) com_google_android_gms_internal_zzapy.nextDouble());
                }
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        };
        bnh = new C01722();
        bni = new C01733();
        bnj = zza(Number.class, bni);
        bnk = new C01744();
        bnl = zza(Character.TYPE, Character.class, bnk);
        bnm = new C01755();
        bnn = new C01766();
        bno = new C01777();
        bnp = zza(String.class, bnm);
        bnq = new C01788();
        bnr = zza(StringBuilder.class, bnq);
        bns = new C01799();
        bnt = zza(StringBuffer.class, bns);
        bnu = new zzaot<URL>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, URL url) {
                com_google_android_gms_internal_zzaqa.zzut(url == null ? null : url.toExternalForm());
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzv(com_google_android_gms_internal_zzapy);
            }

            public URL zzv(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                String nextString = com_google_android_gms_internal_zzapy.nextString();
                return !"null".equals(nextString) ? new URL(nextString) : null;
            }
        };
        bnv = zza(URL.class, bnu);
        bnw = new zzaot<URI>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, URI uri) {
                com_google_android_gms_internal_zzaqa.zzut(uri == null ? null : uri.toASCIIString());
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzw(com_google_android_gms_internal_zzapy);
            }

            public URI zzw(zzapy com_google_android_gms_internal_zzapy) {
                URI uri = null;
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                } else {
                    try {
                        String nextString = com_google_android_gms_internal_zzapy.nextString();
                        if (!"null".equals(nextString)) {
                            uri = new URI(nextString);
                        }
                    } catch (Throwable e) {
                        throw new zzaoi(e);
                    }
                }
                return uri;
            }
        };
        bnx = zza(URI.class, bnw);
        bny = new zzaot<InetAddress>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, InetAddress inetAddress) {
                com_google_android_gms_internal_zzaqa.zzut(inetAddress == null ? null : inetAddress.getHostAddress());
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzy(com_google_android_gms_internal_zzapy);
            }

            public InetAddress zzy(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return InetAddress.getByName(com_google_android_gms_internal_zzapy.nextString());
                }
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        };
        bnz = zzb(InetAddress.class, bny);
        bnA = new zzaot<UUID>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, UUID uuid) {
                com_google_android_gms_internal_zzaqa.zzut(uuid == null ? null : uuid.toString());
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzz(com_google_android_gms_internal_zzapy);
            }

            public UUID zzz(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return UUID.fromString(com_google_android_gms_internal_zzapy.nextString());
                }
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        };
        bnB = zza(UUID.class, bnA);
        bnC = new zzaou() {

            /* renamed from: com.google.android.gms.internal.zzapw.15.1 */
            class C01701 extends zzaot<Timestamp> {
                final /* synthetic */ zzaot bnK;
                final /* synthetic */ AnonymousClass15 bnL;

                C01701(AnonymousClass15 anonymousClass15, zzaot com_google_android_gms_internal_zzaot) {
                    this.bnL = anonymousClass15;
                    this.bnK = com_google_android_gms_internal_zzaot;
                }

                public void zza(zzaqa com_google_android_gms_internal_zzaqa, Timestamp timestamp) {
                    this.bnK.zza(com_google_android_gms_internal_zzaqa, timestamp);
                }

                public Timestamp zzaa(zzapy com_google_android_gms_internal_zzapy) {
                    Date date = (Date) this.bnK.zzb(com_google_android_gms_internal_zzapy);
                    return date != null ? new Timestamp(date.getTime()) : null;
                }

                public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                    return zzaa(com_google_android_gms_internal_zzapy);
                }
            }

            public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
                return com_google_android_gms_internal_zzapx_T.by() != Timestamp.class ? null : new C01701(this, com_google_android_gms_internal_zzaob.zzk(Date.class));
            }
        };
        bnD = new zzaot<Calendar>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Calendar calendar) {
                if (calendar == null) {
                    com_google_android_gms_internal_zzaqa.bx();
                    return;
                }
                com_google_android_gms_internal_zzaqa.bv();
                com_google_android_gms_internal_zzaqa.zzus("year");
                com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(1));
                com_google_android_gms_internal_zzaqa.zzus("month");
                com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(2));
                com_google_android_gms_internal_zzaqa.zzus("dayOfMonth");
                com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(5));
                com_google_android_gms_internal_zzaqa.zzus("hourOfDay");
                com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(11));
                com_google_android_gms_internal_zzaqa.zzus("minute");
                com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(12));
                com_google_android_gms_internal_zzaqa.zzus("second");
                com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(13));
                com_google_android_gms_internal_zzaqa.bw();
            }

            public Calendar zzab(zzapy com_google_android_gms_internal_zzapy) {
                int i = 0;
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                com_google_android_gms_internal_zzapy.beginObject();
                int i2 = 0;
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                int i6 = 0;
                while (com_google_android_gms_internal_zzapy.bn() != zzapz.END_OBJECT) {
                    String nextName = com_google_android_gms_internal_zzapy.nextName();
                    int nextInt = com_google_android_gms_internal_zzapy.nextInt();
                    if ("year".equals(nextName)) {
                        i6 = nextInt;
                    } else if ("month".equals(nextName)) {
                        i5 = nextInt;
                    } else if ("dayOfMonth".equals(nextName)) {
                        i4 = nextInt;
                    } else if ("hourOfDay".equals(nextName)) {
                        i3 = nextInt;
                    } else if ("minute".equals(nextName)) {
                        i2 = nextInt;
                    } else if ("second".equals(nextName)) {
                        i = nextInt;
                    }
                }
                com_google_android_gms_internal_zzapy.endObject();
                return new GregorianCalendar(i6, i5, i4, i3, i2, i);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzab(com_google_android_gms_internal_zzapy);
            }
        };
        bnE = zzb(Calendar.class, GregorianCalendar.class, bnD);
        bnF = new zzaot<Locale>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Locale locale) {
                com_google_android_gms_internal_zzaqa.zzut(locale == null ? null : locale.toString());
            }

            public Locale zzac(zzapy com_google_android_gms_internal_zzapy) {
                if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(com_google_android_gms_internal_zzapy.nextString(), "_");
                String nextToken = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
                String nextToken2 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
                String nextToken3 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
                return (nextToken2 == null && nextToken3 == null) ? new Locale(nextToken) : nextToken3 == null ? new Locale(nextToken, nextToken2) : new Locale(nextToken, nextToken2, nextToken3);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzac(com_google_android_gms_internal_zzapy);
            }
        };
        bnG = zza(Locale.class, bnF);
        bnH = new zzaot<zzaoh>() {
            public void zza(zzaqa com_google_android_gms_internal_zzaqa, zzaoh com_google_android_gms_internal_zzaoh) {
                if (com_google_android_gms_internal_zzaoh == null || com_google_android_gms_internal_zzaoh.aV()) {
                    com_google_android_gms_internal_zzaqa.bx();
                } else if (com_google_android_gms_internal_zzaoh.aU()) {
                    zzaon aY = com_google_android_gms_internal_zzaoh.aY();
                    if (aY.bb()) {
                        com_google_android_gms_internal_zzaqa.zza(aY.aQ());
                    } else if (aY.ba()) {
                        com_google_android_gms_internal_zzaqa.zzdf(aY.getAsBoolean());
                    } else {
                        com_google_android_gms_internal_zzaqa.zzut(aY.aR());
                    }
                } else if (com_google_android_gms_internal_zzaoh.aS()) {
                    com_google_android_gms_internal_zzaqa.bt();
                    Iterator it = com_google_android_gms_internal_zzaoh.aX().iterator();
                    while (it.hasNext()) {
                        zza(com_google_android_gms_internal_zzaqa, (zzaoh) it.next());
                    }
                    com_google_android_gms_internal_zzaqa.bu();
                } else if (com_google_android_gms_internal_zzaoh.aT()) {
                    com_google_android_gms_internal_zzaqa.bv();
                    for (Entry entry : com_google_android_gms_internal_zzaoh.aW().entrySet()) {
                        com_google_android_gms_internal_zzaqa.zzus((String) entry.getKey());
                        zza(com_google_android_gms_internal_zzaqa, (zzaoh) entry.getValue());
                    }
                    com_google_android_gms_internal_zzaqa.bw();
                } else {
                    String valueOf = String.valueOf(com_google_android_gms_internal_zzaoh.getClass());
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 15).append("Couldn't write ").append(valueOf).toString());
                }
            }

            public zzaoh zzad(zzapy com_google_android_gms_internal_zzapy) {
                zzaoh com_google_android_gms_internal_zzaoe;
                switch (AnonymousClass26.bmF[com_google_android_gms_internal_zzapy.bn().ordinal()]) {
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        return new zzaon(new zzape(com_google_android_gms_internal_zzapy.nextString()));
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        return new zzaon(Boolean.valueOf(com_google_android_gms_internal_zzapy.nextBoolean()));
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        return new zzaon(com_google_android_gms_internal_zzapy.nextString());
                    case VideoPlayer.STATE_READY /*4*/:
                        com_google_android_gms_internal_zzapy.nextNull();
                        return zzaoj.bld;
                    case VideoPlayer.STATE_ENDED /*5*/:
                        com_google_android_gms_internal_zzaoe = new zzaoe();
                        com_google_android_gms_internal_zzapy.beginArray();
                        while (com_google_android_gms_internal_zzapy.hasNext()) {
                            com_google_android_gms_internal_zzaoe.zzc((zzaoh) zzb(com_google_android_gms_internal_zzapy));
                        }
                        com_google_android_gms_internal_zzapy.endArray();
                        return com_google_android_gms_internal_zzaoe;
                    case Method.TRACE /*6*/:
                        com_google_android_gms_internal_zzaoe = new zzaok();
                        com_google_android_gms_internal_zzapy.beginObject();
                        while (com_google_android_gms_internal_zzapy.hasNext()) {
                            com_google_android_gms_internal_zzaoe.zza(com_google_android_gms_internal_zzapy.nextName(), (zzaoh) zzb(com_google_android_gms_internal_zzapy));
                        }
                        com_google_android_gms_internal_zzapy.endObject();
                        return com_google_android_gms_internal_zzaoe;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
                return zzad(com_google_android_gms_internal_zzapy);
            }
        };
        bnI = zzb(zzaoh.class, bnH);
        bnJ = new zzaou() {
            public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
                Class by = com_google_android_gms_internal_zzapx_T.by();
                if (!Enum.class.isAssignableFrom(by) || by == Enum.class) {
                    return null;
                }
                if (!by.isEnum()) {
                    by = by.getSuperclass();
                }
                return new zza(by);
            }
        };
    }

    public static <TT> zzaou zza(zzapx<TT> com_google_android_gms_internal_zzapx_TT, zzaot<TT> com_google_android_gms_internal_zzaot_TT) {
        return new AnonymousClass20(com_google_android_gms_internal_zzapx_TT, com_google_android_gms_internal_zzaot_TT);
    }

    public static <TT> zzaou zza(Class<TT> cls, zzaot<TT> com_google_android_gms_internal_zzaot_TT) {
        return new AnonymousClass21(cls, com_google_android_gms_internal_zzaot_TT);
    }

    public static <TT> zzaou zza(Class<TT> cls, Class<TT> cls2, zzaot<? super TT> com_google_android_gms_internal_zzaot__super_TT) {
        return new AnonymousClass22(cls, cls2, com_google_android_gms_internal_zzaot__super_TT);
    }

    public static <TT> zzaou zzb(Class<TT> cls, zzaot<TT> com_google_android_gms_internal_zzaot_TT) {
        return new AnonymousClass25(cls, com_google_android_gms_internal_zzaot_TT);
    }

    public static <TT> zzaou zzb(Class<TT> cls, Class<? extends TT> cls2, zzaot<? super TT> com_google_android_gms_internal_zzaot__super_TT) {
        return new AnonymousClass24(cls, cls2, com_google_android_gms_internal_zzaot__super_TT);
    }
}
