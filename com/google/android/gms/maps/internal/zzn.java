package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.maps.model.internal.zzf;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public interface zzn extends IInterface {

    public static abstract class zza extends Binder implements zzn {

        private static class zza implements zzn {
            private IBinder zzajf;

            zza(IBinder iBinder) {
                this.zzajf = iBinder;
            }

            public IBinder asBinder() {
                return this.zzajf;
            }

            public void zzg(zzf com_google_android_gms_maps_model_internal_zzf) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IOnInfoWindowCloseListener");
                    obtain.writeStrongBinder(com_google_android_gms_maps_model_internal_zzf != null ? com_google_android_gms_maps_model_internal_zzf.asBinder() : null);
                    this.zzajf.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public zza() {
            attachInterface(this, "com.google.android.gms.maps.internal.IOnInfoWindowCloseListener");
        }

        public static zzn zzic(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.IOnInfoWindowCloseListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzn)) ? new zza(iBinder) : (zzn) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            switch (i) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IOnInfoWindowCloseListener");
                    zzg(com.google.android.gms.maps.model.internal.zzf.zza.zzjg(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.maps.internal.IOnInfoWindowCloseListener");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void zzg(zzf com_google_android_gms_maps_model_internal_zzf);
}
