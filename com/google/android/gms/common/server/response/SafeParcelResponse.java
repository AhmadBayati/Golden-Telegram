package com.google.android.gms.common.server.response;

import android.os.Bundle;
import android.os.Parcel;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.server.response.FastJsonResponse.Field;
import com.google.android.gms.common.util.zzb;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzp;
import com.google.android.gms.common.util.zzq;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SafeParcelResponse extends FastSafeParcelableJsonResponse {
    public static final zze CREATOR;
    private final FieldMappingDictionary DB;
    private final Parcel DI;
    private final int DJ;
    private int DK;
    private int DL;
    private final String mClassName;
    private final int mVersionCode;

    static {
        CREATOR = new zze();
    }

    SafeParcelResponse(int i, Parcel parcel, FieldMappingDictionary fieldMappingDictionary) {
        this.mVersionCode = i;
        this.DI = (Parcel) zzac.zzy(parcel);
        this.DJ = 2;
        this.DB = fieldMappingDictionary;
        if (this.DB == null) {
            this.mClassName = null;
        } else {
            this.mClassName = this.DB.zzawg();
        }
        this.DK = 2;
    }

    private void zza(StringBuilder stringBuilder, int i, Object obj) {
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
            case Method.TRACE /*6*/:
                stringBuilder.append(obj);
            case Method.PATCH /*7*/:
                stringBuilder.append("\"").append(zzp.zzii(obj.toString())).append("\"");
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                stringBuilder.append("\"").append(zzc.zzp((byte[]) obj)).append("\"");
            case C0338R.styleable.PromptView_iconTint /*9*/:
                stringBuilder.append("\"").append(zzc.zzq((byte[]) obj));
                stringBuilder.append("\"");
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                zzq.zza(stringBuilder, (HashMap) obj);
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                throw new IllegalArgumentException("Method does not accept concrete type.");
            default:
                throw new IllegalArgumentException("Unknown type = " + i);
        }
    }

    private void zza(StringBuilder stringBuilder, Field<?, ?> field, Parcel parcel, int i) {
        switch (field.zzavr()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                zzb(stringBuilder, (Field) field, zza(field, Integer.valueOf(zza.zzg(parcel, i))));
            case VideoPlayer.TYPE_AUDIO /*1*/:
                zzb(stringBuilder, (Field) field, zza(field, zza.zzk(parcel, i)));
            case VideoPlayer.STATE_PREPARING /*2*/:
                zzb(stringBuilder, (Field) field, zza(field, Long.valueOf(zza.zzi(parcel, i))));
            case VideoPlayer.STATE_BUFFERING /*3*/:
                zzb(stringBuilder, (Field) field, zza(field, Float.valueOf(zza.zzl(parcel, i))));
            case VideoPlayer.STATE_READY /*4*/:
                zzb(stringBuilder, (Field) field, zza(field, Double.valueOf(zza.zzn(parcel, i))));
            case VideoPlayer.STATE_ENDED /*5*/:
                zzb(stringBuilder, (Field) field, zza(field, zza.zzp(parcel, i)));
            case Method.TRACE /*6*/:
                zzb(stringBuilder, (Field) field, zza(field, Boolean.valueOf(zza.zzc(parcel, i))));
            case Method.PATCH /*7*/:
                zzb(stringBuilder, (Field) field, zza(field, zza.zzq(parcel, i)));
            case TLRPC.USER_FLAG_USERNAME /*8*/:
            case C0338R.styleable.PromptView_iconTint /*9*/:
                zzb(stringBuilder, (Field) field, zza(field, zza.zzt(parcel, i)));
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                zzb(stringBuilder, (Field) field, zza(field, zzq(zza.zzs(parcel, i))));
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                throw new IllegalArgumentException("Method does not accept concrete type.");
            default:
                throw new IllegalArgumentException("Unknown field out type = " + field.zzavr());
        }
    }

    private void zza(StringBuilder stringBuilder, String str, Field<?, ?> field, Parcel parcel, int i) {
        stringBuilder.append("\"").append(str).append("\":");
        if (field.zzawb()) {
            zza(stringBuilder, field, parcel, i);
        } else {
            zzb(stringBuilder, field, parcel, i);
        }
    }

    private void zza(StringBuilder stringBuilder, Map<String, Field<?, ?>> map, Parcel parcel) {
        SparseArray zzav = zzav(map);
        stringBuilder.append('{');
        int zzcq = zza.zzcq(parcel);
        Object obj = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            Entry entry = (Entry) zzav.get(zza.zzgv(zzcp));
            if (entry != null) {
                if (obj != null) {
                    stringBuilder.append(",");
                }
                zza(stringBuilder, (String) entry.getKey(), (Field) entry.getValue(), parcel, zzcp);
                obj = 1;
            }
        }
        if (parcel.dataPosition() != zzcq) {
            throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
        }
        stringBuilder.append('}');
    }

    private static SparseArray<Entry<String, Field<?, ?>>> zzav(Map<String, Field<?, ?>> map) {
        SparseArray<Entry<String, Field<?, ?>>> sparseArray = new SparseArray();
        for (Entry entry : map.entrySet()) {
            sparseArray.put(((Field) entry.getValue()).zzavy(), entry);
        }
        return sparseArray;
    }

    private void zzb(StringBuilder stringBuilder, Field<?, ?> field, Parcel parcel, int i) {
        if (field.zzavw()) {
            stringBuilder.append("[");
            switch (field.zzavr()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    zzb.zza(stringBuilder, zza.zzw(parcel, i));
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    zzb.zza(stringBuilder, zza.zzy(parcel, i));
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    zzb.zza(stringBuilder, zza.zzx(parcel, i));
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    zzb.zza(stringBuilder, zza.zzz(parcel, i));
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    zzb.zza(stringBuilder, zza.zzaa(parcel, i));
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    zzb.zza(stringBuilder, zza.zzab(parcel, i));
                    break;
                case Method.TRACE /*6*/:
                    zzb.zza(stringBuilder, zza.zzv(parcel, i));
                    break;
                case Method.PATCH /*7*/:
                    zzb.zza(stringBuilder, zza.zzac(parcel, i));
                    break;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                case C0338R.styleable.PromptView_iconTint /*9*/:
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    throw new UnsupportedOperationException("List of type BASE64, BASE64_URL_SAFE, or STRING_MAP is not supported");
                case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                    Parcel[] zzag = zza.zzag(parcel, i);
                    int length = zzag.length;
                    for (int i2 = 0; i2 < length; i2++) {
                        if (i2 > 0) {
                            stringBuilder.append(",");
                        }
                        zzag[i2].setDataPosition(0);
                        zza(stringBuilder, field.zzawd(), zzag[i2]);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown field type out.");
            }
            stringBuilder.append("]");
            return;
        }
        switch (field.zzavr()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                stringBuilder.append(zza.zzg(parcel, i));
            case VideoPlayer.TYPE_AUDIO /*1*/:
                stringBuilder.append(zza.zzk(parcel, i));
            case VideoPlayer.STATE_PREPARING /*2*/:
                stringBuilder.append(zza.zzi(parcel, i));
            case VideoPlayer.STATE_BUFFERING /*3*/:
                stringBuilder.append(zza.zzl(parcel, i));
            case VideoPlayer.STATE_READY /*4*/:
                stringBuilder.append(zza.zzn(parcel, i));
            case VideoPlayer.STATE_ENDED /*5*/:
                stringBuilder.append(zza.zzp(parcel, i));
            case Method.TRACE /*6*/:
                stringBuilder.append(zza.zzc(parcel, i));
            case Method.PATCH /*7*/:
                stringBuilder.append("\"").append(zzp.zzii(zza.zzq(parcel, i))).append("\"");
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                stringBuilder.append("\"").append(zzc.zzp(zza.zzt(parcel, i))).append("\"");
            case C0338R.styleable.PromptView_iconTint /*9*/:
                stringBuilder.append("\"").append(zzc.zzq(zza.zzt(parcel, i)));
                stringBuilder.append("\"");
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                Bundle zzs = zza.zzs(parcel, i);
                Set<String> keySet = zzs.keySet();
                keySet.size();
                stringBuilder.append("{");
                int i3 = 1;
                for (String str : keySet) {
                    if (i3 == 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append("\"").append(str).append("\"");
                    stringBuilder.append(":");
                    stringBuilder.append("\"").append(zzp.zzii(zzs.getString(str))).append("\"");
                    i3 = 0;
                }
                stringBuilder.append("}");
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                Parcel zzaf = zza.zzaf(parcel, i);
                zzaf.setDataPosition(0);
                zza(stringBuilder, field.zzawd(), zzaf);
            default:
                throw new IllegalStateException("Unknown field type out");
        }
    }

    private void zzb(StringBuilder stringBuilder, Field<?, ?> field, Object obj) {
        if (field.zzavv()) {
            zzb(stringBuilder, (Field) field, (ArrayList) obj);
        } else {
            zza(stringBuilder, field.zzavq(), obj);
        }
    }

    private void zzb(StringBuilder stringBuilder, Field<?, ?> field, ArrayList<?> arrayList) {
        stringBuilder.append("[");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                stringBuilder.append(",");
            }
            zza(stringBuilder, field.zzavq(), arrayList.get(i));
        }
        stringBuilder.append("]");
    }

    public static HashMap<String, String> zzq(Bundle bundle) {
        HashMap<String, String> hashMap = new HashMap();
        for (String str : bundle.keySet()) {
            hashMap.put(str, bundle.getString(str));
        }
        return hashMap;
    }

    public int getVersionCode() {
        return this.mVersionCode;
    }

    public String toString() {
        zzac.zzb(this.DB, (Object) "Cannot convert to JSON on client side.");
        Parcel zzawi = zzawi();
        zzawi.setDataPosition(0);
        StringBuilder stringBuilder = new StringBuilder(100);
        zza(stringBuilder, this.DB.zzie(this.mClassName), zzawi);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zze com_google_android_gms_common_server_response_zze = CREATOR;
        zze.zza(this, parcel, i);
    }

    public Map<String, Field<?, ?>> zzavs() {
        return this.DB == null ? null : this.DB.zzie(this.mClassName);
    }

    public Parcel zzawi() {
        switch (this.DK) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                this.DL = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(this.DI);
                com.google.android.gms.common.internal.safeparcel.zzb.zzaj(this.DI, this.DL);
                this.DK = 2;
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                com.google.android.gms.common.internal.safeparcel.zzb.zzaj(this.DI, this.DL);
                this.DK = 2;
                break;
        }
        return this.DI;
    }

    FieldMappingDictionary zzawj() {
        switch (this.DJ) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return null;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return this.DB;
            case VideoPlayer.STATE_PREPARING /*2*/:
                return this.DB;
            default:
                throw new IllegalStateException("Invalid creation type: " + this.DJ);
        }
    }

    public Object zzia(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }

    public boolean zzib(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }
}
