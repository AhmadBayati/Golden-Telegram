package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.auth.api.signin.internal.zzk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzm;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

public final class zzqp extends GoogleApiClient implements com.google.android.gms.internal.zzqy.zza {
    private final Context mContext;
    private final int vN;
    private final GoogleApiAvailability vP;
    final com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> vQ;
    final zzh xB;
    final Map<Api<?>, Integer> xC;
    private final zzm xO;
    private zzqy xP;
    final Queue<com.google.android.gms.internal.zzqc.zza<?, ?>> xQ;
    private volatile boolean xR;
    private long xS;
    private long xT;
    private final zza xU;
    zzqv xV;
    final Map<zzc<?>, zze> xW;
    Set<Scope> xX;
    private final zzre xY;
    private final ArrayList<zzqf> xZ;
    private final Lock xf;
    private Integer ya;
    Set<zzrp> yb;
    final zzrq yc;
    private final com.google.android.gms.common.internal.zzm.zza yd;
    private final Looper zzajn;

    /* renamed from: com.google.android.gms.internal.zzqp.1 */
    class C01941 implements com.google.android.gms.common.internal.zzm.zza {
        final /* synthetic */ zzqp ye;

        C01941(zzqp com_google_android_gms_internal_zzqp) {
            this.ye = com_google_android_gms_internal_zzqp;
        }

        public boolean isConnected() {
            return this.ye.isConnected();
        }

        public Bundle zzaoe() {
            return null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzqp.2 */
    class C01952 implements ConnectionCallbacks {
        final /* synthetic */ zzqp ye;
        final /* synthetic */ AtomicReference yf;
        final /* synthetic */ zzrm yg;

        C01952(zzqp com_google_android_gms_internal_zzqp, AtomicReference atomicReference, zzrm com_google_android_gms_internal_zzrm) {
            this.ye = com_google_android_gms_internal_zzqp;
            this.yf = atomicReference;
            this.yg = com_google_android_gms_internal_zzrm;
        }

        public void onConnected(Bundle bundle) {
            this.ye.zza((GoogleApiClient) this.yf.get(), this.yg, true);
        }

        public void onConnectionSuspended(int i) {
        }
    }

    /* renamed from: com.google.android.gms.internal.zzqp.3 */
    class C01963 implements OnConnectionFailedListener {
        final /* synthetic */ zzqp ye;
        final /* synthetic */ zzrm yg;

        C01963(zzqp com_google_android_gms_internal_zzqp, zzrm com_google_android_gms_internal_zzrm) {
            this.ye = com_google_android_gms_internal_zzqp;
            this.yg = com_google_android_gms_internal_zzrm;
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            this.yg.zzc(new Status(8));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzqp.4 */
    class C01974 implements ResultCallback<Status> {
        final /* synthetic */ GoogleApiClient lX;
        final /* synthetic */ zzqp ye;
        final /* synthetic */ zzrm yg;
        final /* synthetic */ boolean yh;

        C01974(zzqp com_google_android_gms_internal_zzqp, zzrm com_google_android_gms_internal_zzrm, boolean z, GoogleApiClient googleApiClient) {
            this.ye = com_google_android_gms_internal_zzqp;
            this.yg = com_google_android_gms_internal_zzrm;
            this.yh = z;
            this.lX = googleApiClient;
        }

        public /* synthetic */ void onResult(@NonNull Result result) {
            zzp((Status) result);
        }

        public void zzp(@NonNull Status status) {
            zzk.zzbd(this.ye.mContext).zzaie();
            if (status.isSuccess() && this.ye.isConnected()) {
                this.ye.reconnect();
            }
            this.yg.zzc((Result) status);
            if (this.yh) {
                this.lX.disconnect();
            }
        }
    }

    final class zza extends Handler {
        final /* synthetic */ zzqp ye;

        zza(zzqp com_google_android_gms_internal_zzqp, Looper looper) {
            this.ye = com_google_android_gms_internal_zzqp;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    this.ye.zzarr();
                case VideoPlayer.STATE_PREPARING /*2*/:
                    this.ye.resume();
                default:
                    Log.w("GoogleApiClientImpl", "Unknown message id: " + message.what);
            }
        }
    }

    static class zzb extends com.google.android.gms.internal.zzqv.zza {
        private WeakReference<zzqp> yi;

        zzb(zzqp com_google_android_gms_internal_zzqp) {
            this.yi = new WeakReference(com_google_android_gms_internal_zzqp);
        }

        public void zzaqp() {
            zzqp com_google_android_gms_internal_zzqp = (zzqp) this.yi.get();
            if (com_google_android_gms_internal_zzqp != null) {
                com_google_android_gms_internal_zzqp.resume();
            }
        }
    }

    public zzqp(Context context, Lock lock, Looper looper, zzh com_google_android_gms_common_internal_zzh, GoogleApiAvailability googleApiAvailability, com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa, Map<Api<?>, Integer> map, List<ConnectionCallbacks> list, List<OnConnectionFailedListener> list2, Map<zzc<?>, zze> map2, int i, int i2, ArrayList<zzqf> arrayList) {
        this.xP = null;
        this.xQ = new LinkedList();
        this.xS = 120000;
        this.xT = HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
        this.xX = new HashSet();
        this.xY = new zzre();
        this.ya = null;
        this.yb = null;
        this.yd = new C01941(this);
        this.mContext = context;
        this.xf = lock;
        this.xO = new zzm(looper, this.yd);
        this.zzajn = looper;
        this.xU = new zza(this, looper);
        this.vP = googleApiAvailability;
        this.vN = i;
        if (this.vN >= 0) {
            this.ya = Integer.valueOf(i2);
        }
        this.xC = map;
        this.xW = map2;
        this.xZ = arrayList;
        this.yc = new zzrq(this.xW);
        for (ConnectionCallbacks registerConnectionCallbacks : list) {
            this.xO.registerConnectionCallbacks(registerConnectionCallbacks);
        }
        for (OnConnectionFailedListener registerConnectionFailedListener : list2) {
            this.xO.registerConnectionFailedListener(registerConnectionFailedListener);
        }
        this.xB = com_google_android_gms_common_internal_zzh;
        this.vQ = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa;
    }

    private void resume() {
        this.xf.lock();
        try {
            if (isResuming()) {
                zzarq();
            }
            this.xf.unlock();
        } catch (Throwable th) {
            this.xf.unlock();
        }
    }

    public static int zza(Iterable<zze> iterable, boolean z) {
        int i = 0;
        int i2 = 0;
        for (zze com_google_android_gms_common_api_Api_zze : iterable) {
            if (com_google_android_gms_common_api_Api_zze.zzahd()) {
                i2 = 1;
            }
            i = com_google_android_gms_common_api_Api_zze.zzahs() ? 1 : i;
        }
        return i2 != 0 ? (i == 0 || !z) ? 1 : 2 : 3;
    }

    private void zza(GoogleApiClient googleApiClient, zzrm com_google_android_gms_internal_zzrm, boolean z) {
        zzrx.Dh.zzg(googleApiClient).setResultCallback(new C01974(this, com_google_android_gms_internal_zzrm, z, googleApiClient));
    }

    private void zzarq() {
        this.xO.zzauu();
        this.xP.connect();
    }

    private void zzarr() {
        this.xf.lock();
        try {
            if (zzart()) {
                zzarq();
            }
            this.xf.unlock();
        } catch (Throwable th) {
            this.xf.unlock();
        }
    }

    private void zzb(@NonNull zzqz com_google_android_gms_internal_zzqz) {
        if (this.vN >= 0) {
            zzqa.zza(com_google_android_gms_internal_zzqz).zzfq(this.vN);
            return;
        }
        throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
    }

    private void zzft(int i) {
        if (this.ya == null) {
            this.ya = Integer.valueOf(i);
        } else if (this.ya.intValue() != i) {
            String valueOf = String.valueOf(zzfu(i));
            String valueOf2 = String.valueOf(zzfu(this.ya.intValue()));
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 51) + String.valueOf(valueOf2).length()).append("Cannot use sign-in mode: ").append(valueOf).append(". Mode was already set to ").append(valueOf2).toString());
        }
        if (this.xP == null) {
            Object obj = null;
            Object obj2 = null;
            for (zze com_google_android_gms_common_api_Api_zze : this.xW.values()) {
                if (com_google_android_gms_common_api_Api_zze.zzahd()) {
                    obj2 = 1;
                }
                obj = com_google_android_gms_common_api_Api_zze.zzahs() ? 1 : obj;
            }
            switch (this.ya.intValue()) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    if (obj2 == null) {
                        throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
                    } else if (obj != null) {
                        throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
                    }
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    if (obj2 != null) {
                        this.xP = zzqh.zza(this.mContext, this, this.xf, this.zzajn, this.vP, this.xW, this.xB, this.xC, this.vQ, this.xZ);
                        return;
                    }
                    break;
            }
            this.xP = new zzqr(this.mContext, this, this.xf, this.zzajn, this.vP, this.xW, this.xB, this.xC, this.vQ, this.xZ, this);
        }
    }

    static String zzfu(int i) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return "SIGN_IN_MODE_REQUIRED";
            case VideoPlayer.STATE_PREPARING /*2*/:
                return "SIGN_IN_MODE_OPTIONAL";
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return "SIGN_IN_MODE_NONE";
            default:
                return "UNKNOWN";
        }
    }

    public ConnectionResult blockingConnect() {
        boolean z = true;
        zzac.zza(Looper.myLooper() != Looper.getMainLooper(), (Object) "blockingConnect must not be called on the UI thread");
        this.xf.lock();
        try {
            if (this.vN >= 0) {
                if (this.ya == null) {
                    z = false;
                }
                zzac.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.ya == null) {
                this.ya = Integer.valueOf(zza(this.xW.values(), false));
            } else if (this.ya.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzft(this.ya.intValue());
            this.xO.zzauu();
            ConnectionResult blockingConnect = this.xP.blockingConnect();
            return blockingConnect;
        } finally {
            this.xf.unlock();
        }
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        boolean z = false;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            z = true;
        }
        zzac.zza(z, (Object) "blockingConnect must not be called on the UI thread");
        zzac.zzb((Object) timeUnit, (Object) "TimeUnit must not be null");
        this.xf.lock();
        try {
            if (this.ya == null) {
                this.ya = Integer.valueOf(zza(this.xW.values(), false));
            } else if (this.ya.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzft(this.ya.intValue());
            this.xO.zzauu();
            ConnectionResult blockingConnect = this.xP.blockingConnect(j, timeUnit);
            return blockingConnect;
        } finally {
            this.xf.unlock();
        }
    }

    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        zzac.zza(isConnected(), (Object) "GoogleApiClient is not connected yet.");
        zzac.zza(this.ya.intValue() != 2, (Object) "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
        PendingResult com_google_android_gms_internal_zzrm = new zzrm((GoogleApiClient) this);
        if (this.xW.containsKey(zzrx.fa)) {
            zza(this, com_google_android_gms_internal_zzrm, false);
        } else {
            AtomicReference atomicReference = new AtomicReference();
            GoogleApiClient build = new Builder(this.mContext).addApi(zzrx.API).addConnectionCallbacks(new C01952(this, atomicReference, com_google_android_gms_internal_zzrm)).addOnConnectionFailedListener(new C01963(this, com_google_android_gms_internal_zzrm)).setHandler(this.xU).build();
            atomicReference.set(build);
            build.connect();
        }
        return com_google_android_gms_internal_zzrm;
    }

    public void connect() {
        boolean z = false;
        this.xf.lock();
        try {
            if (this.vN >= 0) {
                if (this.ya != null) {
                    z = true;
                }
                zzac.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.ya == null) {
                this.ya = Integer.valueOf(zza(this.xW.values(), false));
            } else if (this.ya.intValue() == 2) {
                throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            connect(this.ya.intValue());
        } finally {
            this.xf.unlock();
        }
    }

    public void connect(int i) {
        boolean z = true;
        this.xf.lock();
        if (!(i == 3 || i == 1 || i == 2)) {
            z = false;
        }
        try {
            zzac.zzb(z, "Illegal sign-in mode: " + i);
            zzft(i);
            zzarq();
        } finally {
            this.xf.unlock();
        }
    }

    public void disconnect() {
        this.xf.lock();
        try {
            this.yc.release();
            if (this.xP != null) {
                this.xP.disconnect();
            }
            this.xY.release();
            for (com.google.android.gms.internal.zzqc.zza com_google_android_gms_internal_zzqc_zza : this.xQ) {
                com_google_android_gms_internal_zzqc_zza.zza(null);
                com_google_android_gms_internal_zzqc_zza.cancel();
            }
            this.xQ.clear();
            if (this.xP != null) {
                zzart();
                this.xO.zzaut();
                this.xf.unlock();
            }
        } finally {
            this.xf.unlock();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("mContext=").println(this.mContext);
        printWriter.append(str).append("mResuming=").print(this.xR);
        printWriter.append(" mWorkQueue.size()=").print(this.xQ.size());
        this.yc.dump(printWriter);
        if (this.xP != null) {
            this.xP.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    @NonNull
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        this.xf.lock();
        try {
            if (!isConnected() && !isResuming()) {
                throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
            } else if (this.xW.containsKey(api.zzapp())) {
                ConnectionResult connectionResult = this.xP.getConnectionResult(api);
                if (connectionResult != null) {
                    this.xf.unlock();
                } else if (isResuming()) {
                    connectionResult = ConnectionResult.uJ;
                } else {
                    Log.w("GoogleApiClientImpl", zzarv());
                    Log.wtf("GoogleApiClientImpl", String.valueOf(api.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
                    connectionResult = new ConnectionResult(8, null);
                    this.xf.unlock();
                }
                return connectionResult;
            } else {
                throw new IllegalArgumentException(String.valueOf(api.getName()).concat(" was never registered with GoogleApiClient"));
            }
        } finally {
            this.xf.unlock();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public Looper getLooper() {
        return this.zzajn;
    }

    public int getSessionId() {
        return System.identityHashCode(this);
    }

    public boolean hasConnectedApi(@NonNull Api<?> api) {
        zze com_google_android_gms_common_api_Api_zze = (zze) this.xW.get(api.zzapp());
        return com_google_android_gms_common_api_Api_zze != null && com_google_android_gms_common_api_Api_zze.isConnected();
    }

    public boolean isConnected() {
        return this.xP != null && this.xP.isConnected();
    }

    public boolean isConnecting() {
        return this.xP != null && this.xP.isConnecting();
    }

    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        return this.xO.isConnectionCallbacksRegistered(connectionCallbacks);
    }

    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        return this.xO.isConnectionFailedListenerRegistered(onConnectionFailedListener);
    }

    boolean isResuming() {
        return this.xR;
    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.xO.registerConnectionCallbacks(connectionCallbacks);
    }

    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.xO.registerConnectionFailedListener(onConnectionFailedListener);
    }

    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        zzb(new zzqz(fragmentActivity));
    }

    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.xO.unregisterConnectionCallbacks(connectionCallbacks);
    }

    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.xO.unregisterConnectionFailedListener(onConnectionFailedListener);
    }

    @NonNull
    public <C extends zze> C zza(@NonNull zzc<C> com_google_android_gms_common_api_Api_zzc_C) {
        Object obj = (zze) this.xW.get(com_google_android_gms_common_api_Api_zzc_C);
        zzac.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public void zza(zzrp com_google_android_gms_internal_zzrp) {
        this.xf.lock();
        try {
            if (this.yb == null) {
                this.yb = new HashSet();
            }
            this.yb.add(com_google_android_gms_internal_zzrp);
        } finally {
            this.xf.unlock();
        }
    }

    public boolean zza(@NonNull Api<?> api) {
        return this.xW.containsKey(api.zzapp());
    }

    public boolean zza(zzrl com_google_android_gms_internal_zzrl) {
        return this.xP != null && this.xP.zza(com_google_android_gms_internal_zzrl);
    }

    public void zzaqb() {
        if (this.xP != null) {
            this.xP.zzaqb();
        }
    }

    void zzars() {
        if (!isResuming()) {
            this.xR = true;
            if (this.xV == null) {
                this.xV = this.vP.zza(this.mContext.getApplicationContext(), new zzb(this));
            }
            this.xU.sendMessageDelayed(this.xU.obtainMessage(1), this.xS);
            this.xU.sendMessageDelayed(this.xU.obtainMessage(2), this.xT);
        }
    }

    boolean zzart() {
        if (!isResuming()) {
            return false;
        }
        this.xR = false;
        this.xU.removeMessages(2);
        this.xU.removeMessages(1);
        if (this.xV != null) {
            this.xV.unregister();
            this.xV = null;
        }
        return true;
    }

    boolean zzaru() {
        boolean z = false;
        this.xf.lock();
        try {
            if (this.yb != null) {
                if (!this.yb.isEmpty()) {
                    z = true;
                }
                this.xf.unlock();
            }
            return z;
        } finally {
            this.xf.unlock();
        }
    }

    String zzarv() {
        Writer stringWriter = new StringWriter();
        dump(TtmlNode.ANONYMOUS_REGION_ID, null, new PrintWriter(stringWriter), null);
        return stringWriter.toString();
    }

    <C extends zze> C zzb(zzc<?> com_google_android_gms_common_api_Api_zzc_) {
        Object obj = (zze) this.xW.get(com_google_android_gms_common_api_Api_zzc_);
        zzac.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public void zzb(zzrp com_google_android_gms_internal_zzrp) {
        this.xf.lock();
        try {
            if (this.yb == null) {
                Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
            } else if (!this.yb.remove(com_google_android_gms_internal_zzrp)) {
                Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
            } else if (!zzaru()) {
                this.xP.zzaqy();
            }
            this.xf.unlock();
        } catch (Throwable th) {
            this.xf.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqc.zza<R, A>> T zzc(@NonNull T t) {
        zzac.zzb(t.zzapp() != null, (Object) "This task can not be enqueued (it's probably a Batch or malformed)");
        boolean containsKey = this.xW.containsKey(t.zzapp());
        String name = t.zzaqn() != null ? t.zzaqn().getName() : "the API";
        zzac.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.xf.lock();
        try {
            if (this.xP == null) {
                this.xQ.add(t);
            } else {
                t = this.xP.zzc(t);
                this.xf.unlock();
            }
            return t;
        } finally {
            this.xf.unlock();
        }
    }

    public void zzc(int i, boolean z) {
        if (i == 1 && !z) {
            zzars();
        }
        this.yc.zzasw();
        this.xO.zzgo(i);
        this.xO.zzaut();
        if (i == 2) {
            zzarq();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqc.zza<? extends Result, A>> T zzd(@NonNull T t) {
        zzac.zzb(t.zzapp() != null, (Object) "This task can not be executed (it's probably a Batch or malformed)");
        boolean containsKey = this.xW.containsKey(t.zzapp());
        String name = t.zzaqn() != null ? t.zzaqn().getName() : "the API";
        zzac.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.xf.lock();
        try {
            if (this.xP == null) {
                throw new IllegalStateException("GoogleApiClient is not connected yet.");
            }
            if (isResuming()) {
                this.xQ.add(t);
                while (!this.xQ.isEmpty()) {
                    zzqe com_google_android_gms_internal_zzqe = (com.google.android.gms.internal.zzqc.zza) this.xQ.remove();
                    this.yc.zzb(com_google_android_gms_internal_zzqe);
                    com_google_android_gms_internal_zzqe.zzz(Status.wa);
                }
            } else {
                t = this.xP.zzd(t);
                this.xf.unlock();
            }
            return t;
        } finally {
            this.xf.unlock();
        }
    }

    public void zzd(ConnectionResult connectionResult) {
        if (!this.vP.zzd(this.mContext, connectionResult.getErrorCode())) {
            zzart();
        }
        if (!isResuming()) {
            this.xO.zzn(connectionResult);
            this.xO.zzaut();
        }
    }

    public void zzn(Bundle bundle) {
        while (!this.xQ.isEmpty()) {
            zzd((com.google.android.gms.internal.zzqc.zza) this.xQ.remove());
        }
        this.xO.zzp(bundle);
    }

    public <L> zzrd<L> zzs(@NonNull L l) {
        this.xf.lock();
        try {
            zzrd<L> zzb = this.xY.zzb(l, this.zzajn);
            return zzb;
        } finally {
            this.xf.unlock();
        }
    }
}
