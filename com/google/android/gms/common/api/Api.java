package com.google.android.gms.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzr;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Api<O extends ApiOptions> {
    private final String mName;
    private final zza<?, O> vi;
    private final zzh<?, O> vj;
    private final zzf<?> vk;
    private final zzi<?> vl;

    public interface ApiOptions {

        public interface HasOptions extends ApiOptions {
        }

        public interface NotRequiredOptions extends ApiOptions {
        }

        public interface Optional extends HasOptions, NotRequiredOptions {
        }

        public static final class NoOptions implements NotRequiredOptions {
            private NoOptions() {
            }
        }
    }

    public static abstract class zzd<T extends zzb, O> {
        public int getPriority() {
            return ConnectionsManager.DEFAULT_DATACENTER_ID;
        }

        public List<Scope> zzp(O o) {
            return Collections.emptyList();
        }
    }

    public static abstract class zza<T extends zze, O> extends zzd<T, O> {
        public abstract T zza(Context context, Looper looper, com.google.android.gms.common.internal.zzh com_google_android_gms_common_internal_zzh, O o, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener);
    }

    public interface zzb {
    }

    public static class zzc<C extends zzb> {
    }

    public interface zze extends zzb {
        void disconnect();

        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

        boolean isConnected();

        boolean isConnecting();

        void zza(com.google.android.gms.common.internal.zze.zzf com_google_android_gms_common_internal_zze_zzf);

        void zza(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set);

        boolean zzahd();

        boolean zzahs();

        Intent zzaht();

        boolean zzapr();

        @Nullable
        IBinder zzaps();
    }

    public static final class zzf<C extends zze> extends zzc<C> {
    }

    public interface zzg<T extends IInterface> extends zzb {
        void zza(int i, T t);

        T zzh(IBinder iBinder);

        String zzix();

        String zziy();
    }

    public static abstract class zzh<T extends zzg, O> extends zzd<T, O> {
        public abstract int zzapt();

        public abstract T zzr(O o);
    }

    public static final class zzi<C extends zzg> extends zzc<C> {
    }

    public <C extends zze> Api(String str, zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, zzf<C> com_google_android_gms_common_api_Api_zzf_C) {
        zzac.zzb((Object) com_google_android_gms_common_api_Api_zza_C__O, (Object) "Cannot construct an Api with a null ClientBuilder");
        zzac.zzb((Object) com_google_android_gms_common_api_Api_zzf_C, (Object) "Cannot construct an Api with a null ClientKey");
        this.mName = str;
        this.vi = com_google_android_gms_common_api_Api_zza_C__O;
        this.vj = null;
        this.vk = com_google_android_gms_common_api_Api_zzf_C;
        this.vl = null;
    }

    public String getName() {
        return this.mName;
    }

    public zzd<?, O> zzapm() {
        return zzapq() ? null : this.vi;
    }

    public zza<?, O> zzapn() {
        zzac.zza(this.vi != null, (Object) "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
        return this.vi;
    }

    public zzh<?, O> zzapo() {
        zzac.zza(false, (Object) "This API was constructed with a ClientBuilder. Use getClientBuilder");
        return null;
    }

    public zzc<?> zzapp() {
        if (this.vk != null) {
            return this.vk;
        }
        throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
    }

    public boolean zzapq() {
        return false;
    }
}
