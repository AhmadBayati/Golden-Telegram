package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.signin.internal.SignInResponse;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public class zzqn implements zzqq {
    private final Context mContext;
    private final com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> vQ;
    private boolean xA;
    private final zzh xB;
    private final Map<Api<?>, Integer> xC;
    private ArrayList<Future<?>> xD;
    private final Lock xf;
    private final zzqr xk;
    private final com.google.android.gms.common.zzc xn;
    private ConnectionResult xo;
    private int xp;
    private int xq;
    private int xr;
    private final Bundle xs;
    private final Set<com.google.android.gms.common.api.Api.zzc> xt;
    private zzwz xu;
    private int xv;
    private boolean xw;
    private boolean xx;
    private zzr xy;
    private boolean xz;

    /* renamed from: com.google.android.gms.internal.zzqn.1 */
    class C01901 implements Runnable {
        final /* synthetic */ zzqn xE;

        C01901(zzqn com_google_android_gms_internal_zzqn) {
            this.xE = com_google_android_gms_internal_zzqn;
        }

        public void run() {
            this.xE.xn.zzbq(this.xE.mContext);
        }
    }

    private static class zza implements com.google.android.gms.common.internal.zze.zzf {
        private final Api<?> tv;
        private final int wT;
        private final WeakReference<zzqn> xF;

        public zza(zzqn com_google_android_gms_internal_zzqn, Api<?> api, int i) {
            this.xF = new WeakReference(com_google_android_gms_internal_zzqn);
            this.tv = api;
            this.wT = i;
        }

        public void zzh(@NonNull ConnectionResult connectionResult) {
            boolean z = false;
            zzqn com_google_android_gms_internal_zzqn = (zzqn) this.xF.get();
            if (com_google_android_gms_internal_zzqn != null) {
                if (Looper.myLooper() == com_google_android_gms_internal_zzqn.xk.wV.getLooper()) {
                    z = true;
                }
                zzac.zza(z, (Object) "onReportServiceBinding must be called on the GoogleApiClient handler thread");
                com_google_android_gms_internal_zzqn.xf.lock();
                try {
                    if (com_google_android_gms_internal_zzqn.zzfr(0)) {
                        if (!connectionResult.isSuccess()) {
                            com_google_android_gms_internal_zzqn.zzb(connectionResult, this.tv, this.wT);
                        }
                        if (com_google_android_gms_internal_zzqn.zzarj()) {
                            com_google_android_gms_internal_zzqn.zzark();
                        }
                        com_google_android_gms_internal_zzqn.xf.unlock();
                    }
                } finally {
                    com_google_android_gms_internal_zzqn.xf.unlock();
                }
            }
        }
    }

    private abstract class zzf implements Runnable {
        final /* synthetic */ zzqn xE;

        private zzf(zzqn com_google_android_gms_internal_zzqn) {
            this.xE = com_google_android_gms_internal_zzqn;
        }

        @WorkerThread
        public void run() {
            this.xE.xf.lock();
            try {
                if (!Thread.interrupted()) {
                    zzari();
                    this.xE.xf.unlock();
                }
            } catch (RuntimeException e) {
                this.xE.xk.zza(e);
            } finally {
                this.xE.xf.unlock();
            }
        }

        @WorkerThread
        protected abstract void zzari();
    }

    private class zzb extends zzf {
        final /* synthetic */ zzqn xE;
        private final Map<com.google.android.gms.common.api.Api.zze, zza> xG;

        /* renamed from: com.google.android.gms.internal.zzqn.zzb.1 */
        class C01911 extends zza {
            final /* synthetic */ ConnectionResult xH;
            final /* synthetic */ zzb xI;

            C01911(zzb com_google_android_gms_internal_zzqn_zzb, zzqq com_google_android_gms_internal_zzqq, ConnectionResult connectionResult) {
                this.xI = com_google_android_gms_internal_zzqn_zzb;
                this.xH = connectionResult;
                super(com_google_android_gms_internal_zzqq);
            }

            public void zzari() {
                this.xI.xE.zzg(this.xH);
            }
        }

        /* renamed from: com.google.android.gms.internal.zzqn.zzb.2 */
        class C01922 extends zza {
            final /* synthetic */ zzb xI;
            final /* synthetic */ com.google.android.gms.common.internal.zze.zzf xJ;

            C01922(zzb com_google_android_gms_internal_zzqn_zzb, zzqq com_google_android_gms_internal_zzqq, com.google.android.gms.common.internal.zze.zzf com_google_android_gms_common_internal_zze_zzf) {
                this.xI = com_google_android_gms_internal_zzqn_zzb;
                this.xJ = com_google_android_gms_common_internal_zze_zzf;
                super(com_google_android_gms_internal_zzqq);
            }

            public void zzari() {
                this.xJ.zzh(new ConnectionResult(16, null));
            }
        }

        public zzb(zzqn com_google_android_gms_internal_zzqn, Map<com.google.android.gms.common.api.Api.zze, zza> map) {
            this.xE = com_google_android_gms_internal_zzqn;
            super(null);
            this.xG = map;
        }

        @WorkerThread
        public void zzari() {
            int i;
            int i2 = 1;
            int i3 = 0;
            int i4 = 1;
            int i5 = 0;
            for (com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze : this.xG.keySet()) {
                if (!com_google_android_gms_common_api_Api_zze.zzapr()) {
                    i = 0;
                    i4 = i5;
                } else if (((zza) this.xG.get(com_google_android_gms_common_api_Api_zze)).wT == 0) {
                    i = 1;
                    break;
                } else {
                    i = i4;
                    i4 = 1;
                }
                i5 = i4;
                i4 = i;
            }
            i2 = i5;
            i = 0;
            if (i2 != 0) {
                i3 = this.xE.xn.isGooglePlayServicesAvailable(this.xE.mContext);
            }
            if (i3 == 0 || (r0 == 0 && i4 == 0)) {
                if (this.xE.xw) {
                    this.xE.xu.connect();
                }
                for (com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze2 : this.xG.keySet()) {
                    com.google.android.gms.common.internal.zze.zzf com_google_android_gms_common_internal_zze_zzf = (com.google.android.gms.common.internal.zze.zzf) this.xG.get(com_google_android_gms_common_api_Api_zze2);
                    if (!com_google_android_gms_common_api_Api_zze2.zzapr() || i3 == 0) {
                        com_google_android_gms_common_api_Api_zze2.zza(com_google_android_gms_common_internal_zze_zzf);
                    } else {
                        this.xE.xk.zza(new C01922(this, this.xE, com_google_android_gms_common_internal_zze_zzf));
                    }
                }
                return;
            }
            this.xE.xk.zza(new C01911(this, this.xE, new ConnectionResult(i3, null)));
        }
    }

    private class zzc extends zzf {
        final /* synthetic */ zzqn xE;
        private final ArrayList<com.google.android.gms.common.api.Api.zze> xK;

        public zzc(zzqn com_google_android_gms_internal_zzqn, ArrayList<com.google.android.gms.common.api.Api.zze> arrayList) {
            this.xE = com_google_android_gms_internal_zzqn;
            super(null);
            this.xK = arrayList;
        }

        @WorkerThread
        public void zzari() {
            this.xE.xk.wV.xX = this.xE.zzarp();
            Iterator it = this.xK.iterator();
            while (it.hasNext()) {
                ((com.google.android.gms.common.api.Api.zze) it.next()).zza(this.xE.xy, this.xE.xk.wV.xX);
            }
        }
    }

    private static class zzd extends com.google.android.gms.signin.internal.zzb {
        private final WeakReference<zzqn> xF;

        /* renamed from: com.google.android.gms.internal.zzqn.zzd.1 */
        class C01931 extends zza {
            final /* synthetic */ zzqn xL;
            final /* synthetic */ SignInResponse xM;
            final /* synthetic */ zzd xN;

            C01931(zzd com_google_android_gms_internal_zzqn_zzd, zzqq com_google_android_gms_internal_zzqq, zzqn com_google_android_gms_internal_zzqn, SignInResponse signInResponse) {
                this.xN = com_google_android_gms_internal_zzqn_zzd;
                this.xL = com_google_android_gms_internal_zzqn;
                this.xM = signInResponse;
                super(com_google_android_gms_internal_zzqq);
            }

            public void zzari() {
                this.xL.zza(this.xM);
            }
        }

        zzd(zzqn com_google_android_gms_internal_zzqn) {
            this.xF = new WeakReference(com_google_android_gms_internal_zzqn);
        }

        @BinderThread
        public void zzb(SignInResponse signInResponse) {
            zzqn com_google_android_gms_internal_zzqn = (zzqn) this.xF.get();
            if (com_google_android_gms_internal_zzqn != null) {
                com_google_android_gms_internal_zzqn.xk.zza(new C01931(this, com_google_android_gms_internal_zzqn, com_google_android_gms_internal_zzqn, signInResponse));
            }
        }
    }

    private class zze implements ConnectionCallbacks, OnConnectionFailedListener {
        final /* synthetic */ zzqn xE;

        private zze(zzqn com_google_android_gms_internal_zzqn) {
            this.xE = com_google_android_gms_internal_zzqn;
        }

        public void onConnected(Bundle bundle) {
            this.xE.xu.zza(new zzd(this.xE));
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            this.xE.xf.lock();
            try {
                if (this.xE.zzf(connectionResult)) {
                    this.xE.zzarn();
                    this.xE.zzark();
                } else {
                    this.xE.zzg(connectionResult);
                }
                this.xE.xf.unlock();
            } catch (Throwable th) {
                this.xE.xf.unlock();
            }
        }

        public void onConnectionSuspended(int i) {
        }
    }

    public zzqn(zzqr com_google_android_gms_internal_zzqr, zzh com_google_android_gms_common_internal_zzh, Map<Api<?>, Integer> map, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa, Lock lock, Context context) {
        this.xq = 0;
        this.xs = new Bundle();
        this.xt = new HashSet();
        this.xD = new ArrayList();
        this.xk = com_google_android_gms_internal_zzqr;
        this.xB = com_google_android_gms_common_internal_zzh;
        this.xC = map;
        this.xn = com_google_android_gms_common_zzc;
        this.vQ = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa;
        this.xf = lock;
        this.mContext = context;
    }

    private void zza(SignInResponse signInResponse) {
        if (zzfr(0)) {
            ConnectionResult zzave = signInResponse.zzave();
            if (zzave.isSuccess()) {
                ResolveAccountResponse zzcdl = signInResponse.zzcdl();
                ConnectionResult zzave2 = zzcdl.zzave();
                if (zzave2.isSuccess()) {
                    this.xx = true;
                    this.xy = zzcdl.zzavd();
                    this.xz = zzcdl.zzavf();
                    this.xA = zzcdl.zzavg();
                    zzark();
                    return;
                }
                String valueOf = String.valueOf(zzave2);
                Log.wtf("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                zzg(zzave2);
            } else if (zzf(zzave)) {
                zzarn();
                zzark();
            } else {
                zzg(zzave);
            }
        }
    }

    private boolean zza(int i, int i2, ConnectionResult connectionResult) {
        return (i2 != 1 || zze(connectionResult)) ? this.xo == null || i < this.xp : false;
    }

    private boolean zzarj() {
        this.xr--;
        if (this.xr > 0) {
            return false;
        }
        if (this.xr < 0) {
            Log.w("GoogleApiClientConnecting", this.xk.wV.zzarv());
            Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
            zzg(new ConnectionResult(8, null));
            return false;
        } else if (this.xo == null) {
            return true;
        } else {
            this.xk.yo = this.xp;
            zzg(this.xo);
            return false;
        }
    }

    private void zzark() {
        if (this.xr == 0) {
            if (!this.xw || this.xx) {
                zzarl();
            }
        }
    }

    private void zzarl() {
        ArrayList arrayList = new ArrayList();
        this.xq = 1;
        this.xr = this.xk.xW.size();
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.xk.xW.keySet()) {
            if (!this.xk.yl.containsKey(com_google_android_gms_common_api_Api_zzc)) {
                arrayList.add((com.google.android.gms.common.api.Api.zze) this.xk.xW.get(com_google_android_gms_common_api_Api_zzc));
            } else if (zzarj()) {
                zzarm();
            }
        }
        if (!arrayList.isEmpty()) {
            this.xD.add(zzqs.zzarz().submit(new zzc(this, arrayList)));
        }
    }

    private void zzarm() {
        this.xk.zzarx();
        zzqs.zzarz().execute(new C01901(this));
        if (this.xu != null) {
            if (this.xz) {
                this.xu.zza(this.xy, this.xA);
            }
            zzbq(false);
        }
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.xk.yl.keySet()) {
            ((com.google.android.gms.common.api.Api.zze) this.xk.xW.get(com_google_android_gms_common_api_Api_zzc)).disconnect();
        }
        this.xk.yp.zzn(this.xs.isEmpty() ? null : this.xs);
    }

    private void zzarn() {
        this.xw = false;
        this.xk.wV.xX = Collections.emptySet();
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.xt) {
            if (!this.xk.yl.containsKey(com_google_android_gms_common_api_Api_zzc)) {
                this.xk.yl.put(com_google_android_gms_common_api_Api_zzc, new ConnectionResult(17, null));
            }
        }
    }

    private void zzaro() {
        Iterator it = this.xD.iterator();
        while (it.hasNext()) {
            ((Future) it.next()).cancel(true);
        }
        this.xD.clear();
    }

    private Set<Scope> zzarp() {
        if (this.xB == null) {
            return Collections.emptySet();
        }
        Set<Scope> hashSet = new HashSet(this.xB.zzaug());
        Map zzaui = this.xB.zzaui();
        for (Api api : zzaui.keySet()) {
            if (!this.xk.yl.containsKey(api.zzapp())) {
                hashSet.addAll(((com.google.android.gms.common.internal.zzh.zza) zzaui.get(api)).hm);
            }
        }
        return hashSet;
    }

    private void zzb(ConnectionResult connectionResult, Api<?> api, int i) {
        if (i != 2) {
            int priority = api.zzapm().getPriority();
            if (zza(priority, i, connectionResult)) {
                this.xo = connectionResult;
                this.xp = priority;
            }
        }
        this.xk.yl.put(api.zzapp(), connectionResult);
    }

    private void zzbq(boolean z) {
        if (this.xu != null) {
            if (this.xu.isConnected() && z) {
                this.xu.zzcda();
            }
            this.xu.disconnect();
            this.xy = null;
        }
    }

    private boolean zze(ConnectionResult connectionResult) {
        return connectionResult.hasResolution() || this.xn.zzfl(connectionResult.getErrorCode()) != null;
    }

    private boolean zzf(ConnectionResult connectionResult) {
        return this.xv != 2 ? this.xv == 1 && !connectionResult.hasResolution() : true;
    }

    private boolean zzfr(int i) {
        if (this.xq == i) {
            return true;
        }
        Log.w("GoogleApiClientConnecting", this.xk.wV.zzarv());
        String valueOf = String.valueOf(this);
        Log.w("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Unexpected callback in ").append(valueOf).toString());
        Log.w("GoogleApiClientConnecting", "mRemainingConnections=" + this.xr);
        valueOf = String.valueOf(zzfs(this.xq));
        String valueOf2 = String.valueOf(zzfs(i));
        Log.wtf("GoogleApiClientConnecting", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("GoogleApiClient connecting is in step ").append(valueOf).append(" but received callback for step ").append(valueOf2).toString(), new Exception());
        zzg(new ConnectionResult(8, null));
        return false;
    }

    private String zzfs(int i) {
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return "STEP_GETTING_REMOTE_SERVICE";
            default:
                return "UNKNOWN";
        }
    }

    private void zzg(ConnectionResult connectionResult) {
        zzaro();
        zzbq(!connectionResult.hasResolution());
        this.xk.zzi(connectionResult);
        this.xk.yp.zzd(connectionResult);
    }

    public void begin() {
        this.xk.yl.clear();
        this.xw = false;
        this.xo = null;
        this.xq = 0;
        this.xv = 2;
        this.xx = false;
        this.xz = false;
        Map hashMap = new HashMap();
        int i = 0;
        for (Api api : this.xC.keySet()) {
            com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze = (com.google.android.gms.common.api.Api.zze) this.xk.xW.get(api.zzapp());
            int intValue = ((Integer) this.xC.get(api)).intValue();
            int i2 = (api.zzapm().getPriority() == 1 ? 1 : 0) | i;
            if (com_google_android_gms_common_api_Api_zze.zzahd()) {
                this.xw = true;
                if (intValue < this.xv) {
                    this.xv = intValue;
                }
                if (intValue != 0) {
                    this.xt.add(api.zzapp());
                }
            }
            hashMap.put(com_google_android_gms_common_api_Api_zze, new zza(this, api, intValue));
            i = i2;
        }
        if (i != 0) {
            this.xw = false;
        }
        if (this.xw) {
            this.xB.zzc(Integer.valueOf(this.xk.wV.getSessionId()));
            ConnectionCallbacks com_google_android_gms_internal_zzqn_zze = new zze();
            this.xu = (zzwz) this.vQ.zza(this.mContext, this.xk.wV.getLooper(), this.xB, this.xB.zzaum(), com_google_android_gms_internal_zzqn_zze, com_google_android_gms_internal_zzqn_zze);
        }
        this.xr = this.xk.xW.size();
        this.xD.add(zzqs.zzarz().submit(new zzb(this, hashMap)));
    }

    public void connect() {
    }

    public boolean disconnect() {
        zzaro();
        zzbq(true);
        this.xk.zzi(null);
        return true;
    }

    public void onConnected(Bundle bundle) {
        if (zzfr(1)) {
            if (bundle != null) {
                this.xs.putAll(bundle);
            }
            if (zzarj()) {
                zzarm();
            }
        }
    }

    public void onConnectionSuspended(int i) {
        zzg(new ConnectionResult(8, null));
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
        if (zzfr(1)) {
            zzb(connectionResult, api, i);
            if (zzarj()) {
                zzarm();
            }
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqc.zza<R, A>> T zzc(T t) {
        this.xk.wV.xQ.add(t);
        return t;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqc.zza<? extends Result, A>> T zzd(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
}
