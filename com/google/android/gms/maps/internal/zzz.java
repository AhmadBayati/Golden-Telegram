package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.maps.model.internal.zzg;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public interface zzz extends IInterface {

    public static abstract class zza extends Binder implements zzz {

        private static class zza implements zzz {
            private IBinder zzajf;

            zza(IBinder iBinder) {
                this.zzajf = iBinder;
            }

            public IBinder asBinder() {
                return this.zzajf;
            }

            public void zza(zzg com_google_android_gms_maps_model_internal_zzg) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IOnPolygonClickListener");
                    obtain.writeStrongBinder(com_google_android_gms_maps_model_internal_zzg != null ? com_google_android_gms_maps_model_internal_zzg.asBinder() : null);
                    this.zzajf.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public zza() {
            attachInterface(this, "com.google.android.gms.maps.internal.IOnPolygonClickListener");
        }

        public static zzz zzio(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.IOnPolygonClickListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzz)) ? new zza(iBinder) : (zzz) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            switch (i) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IOnPolygonClickListener");
                    zza(com.google.android.gms.maps.model.internal.zzg.zza.zzjh(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.maps.internal.IOnPolygonClickListener");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void zza(zzg com_google_android_gms_maps_model_internal_zzg);
}
