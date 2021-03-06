package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.maps.model.internal.zzc;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public interface zzk extends IInterface {

    public static abstract class zza extends Binder implements zzk {

        private static class zza implements zzk {
            private IBinder zzajf;

            zza(IBinder iBinder) {
                this.zzajf = iBinder;
            }

            public IBinder asBinder() {
                return this.zzajf;
            }

            public void zza(zzc com_google_android_gms_maps_model_internal_zzc) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
                    obtain.writeStrongBinder(com_google_android_gms_maps_model_internal_zzc != null ? com_google_android_gms_maps_model_internal_zzc.asBinder() : null);
                    this.zzajf.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public zza() {
            attachInterface(this, "com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
        }

        public static zzk zzhz(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzk)) ? new zza(iBinder) : (zzk) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            switch (i) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
                    zza(com.google.android.gms.maps.model.internal.zzc.zza.zzjd(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void zza(zzc com_google_android_gms_maps_model_internal_zzc);
}
