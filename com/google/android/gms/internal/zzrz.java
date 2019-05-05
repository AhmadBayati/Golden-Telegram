package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzqc.zzb;

public final class zzrz implements zzry {

    /* renamed from: com.google.android.gms.internal.zzrz.1 */
    class C02111 extends zza {
        final /* synthetic */ zzrz Di;

        C02111(zzrz com_google_android_gms_internal_zzrz, GoogleApiClient googleApiClient) {
            this.Di = com_google_android_gms_internal_zzrz;
            super(googleApiClient);
        }

        protected void zza(zzsb com_google_android_gms_internal_zzsb) {
            ((zzsd) com_google_android_gms_internal_zzsb.zzatx()).zza(new zza(this));
        }
    }

    private static class zza extends zzrw {
        private final zzb<Status> Dj;

        public zza(zzb<Status> com_google_android_gms_internal_zzqc_zzb_com_google_android_gms_common_api_Status) {
            this.Dj = com_google_android_gms_internal_zzqc_zzb_com_google_android_gms_common_api_Status;
        }

        public void zzgw(int i) {
            this.Dj.setResult(new Status(i));
        }
    }

    public PendingResult<Status> zzg(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new C02111(this, googleApiClient));
    }
}
