package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;

public interface IStreetViewPanoramaDelegate extends IInterface {

    public static abstract class zza extends Binder implements IStreetViewPanoramaDelegate {

        private static class zza implements IStreetViewPanoramaDelegate {
            private IBinder zzajf;

            zza(IBinder iBinder) {
                this.zzajf = iBinder;
            }

            public void animateTo(StreetViewPanoramaCamera streetViewPanoramaCamera, long j) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (streetViewPanoramaCamera != null) {
                        obtain.writeInt(1);
                        streetViewPanoramaCamera.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeLong(j);
                    this.zzajf.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.zzajf;
            }

            public void enablePanning(boolean z) {
                int i = 0;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (z) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    this.zzajf.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void enableStreetNames(boolean z) {
                int i = 0;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (z) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    this.zzajf.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void enableUserNavigation(boolean z) {
                int i = 0;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (z) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    this.zzajf.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void enableZoom(boolean z) {
                int i = 1;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (!z) {
                        i = 0;
                    }
                    obtain.writeInt(i);
                    this.zzajf.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public StreetViewPanoramaCamera getPanoramaCamera() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    this.zzajf.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    StreetViewPanoramaCamera streetViewPanoramaCamera = obtain2.readInt() != 0 ? (StreetViewPanoramaCamera) StreetViewPanoramaCamera.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return streetViewPanoramaCamera;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public StreetViewPanoramaLocation getStreetViewPanoramaLocation() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    this.zzajf.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    StreetViewPanoramaLocation streetViewPanoramaLocation = obtain2.readInt() != 0 ? (StreetViewPanoramaLocation) StreetViewPanoramaLocation.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return streetViewPanoramaLocation;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isPanningGesturesEnabled() {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    this.zzajf.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isStreetNamesEnabled() {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    this.zzajf.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isUserNavigationEnabled() {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    this.zzajf.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isZoomGesturesEnabled() {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    this.zzajf.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd orientationToPoint(StreetViewPanoramaOrientation streetViewPanoramaOrientation) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (streetViewPanoramaOrientation != null) {
                        obtain.writeInt(1);
                        streetViewPanoramaOrientation.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzajf.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzfe = com.google.android.gms.dynamic.zzd.zza.zzfe(obtain2.readStrongBinder());
                    return zzfe;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public StreetViewPanoramaOrientation pointToOrientation(zzd com_google_android_gms_dynamic_zzd) {
                StreetViewPanoramaOrientation streetViewPanoramaOrientation = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    this.zzajf.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        streetViewPanoramaOrientation = (StreetViewPanoramaOrientation) StreetViewPanoramaOrientation.CREATOR.createFromParcel(obtain2);
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return streetViewPanoramaOrientation;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setOnStreetViewPanoramaCameraChangeListener(zzab com_google_android_gms_maps_internal_zzab) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    obtain.writeStrongBinder(com_google_android_gms_maps_internal_zzab != null ? com_google_android_gms_maps_internal_zzab.asBinder() : null);
                    this.zzajf.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setOnStreetViewPanoramaChangeListener(zzac com_google_android_gms_maps_internal_zzac) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    obtain.writeStrongBinder(com_google_android_gms_maps_internal_zzac != null ? com_google_android_gms_maps_internal_zzac.asBinder() : null);
                    this.zzajf.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setOnStreetViewPanoramaClickListener(zzad com_google_android_gms_maps_internal_zzad) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    obtain.writeStrongBinder(com_google_android_gms_maps_internal_zzad != null ? com_google_android_gms_maps_internal_zzad.asBinder() : null);
                    this.zzajf.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setOnStreetViewPanoramaLongClickListener(zzae com_google_android_gms_maps_internal_zzae) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    obtain.writeStrongBinder(com_google_android_gms_maps_internal_zzae != null ? com_google_android_gms_maps_internal_zzae.asBinder() : null);
                    this.zzajf.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setPosition(LatLng latLng) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (latLng != null) {
                        obtain.writeInt(1);
                        latLng.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzajf.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setPositionWithID(String str) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    obtain.writeString(str);
                    this.zzajf.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setPositionWithRadius(LatLng latLng, int i) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (latLng != null) {
                        obtain.writeInt(1);
                        latLng.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    this.zzajf.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IStreetViewPanoramaDelegate zzix(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IStreetViewPanoramaDelegate)) ? new zza(iBinder) : (IStreetViewPanoramaDelegate) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            IBinder iBinder = null;
            int i3 = 0;
            boolean z;
            boolean isZoomGesturesEnabled;
            switch (i) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (parcel.readInt() != 0) {
                        z = true;
                    }
                    enableZoom(z);
                    parcel2.writeNoException();
                    return true;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (parcel.readInt() != 0) {
                        z = true;
                    }
                    enablePanning(z);
                    parcel2.writeNoException();
                    return true;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (parcel.readInt() != 0) {
                        z = true;
                    }
                    enableUserNavigation(z);
                    parcel2.writeNoException();
                    return true;
                case VideoPlayer.STATE_READY /*4*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    if (parcel.readInt() != 0) {
                        z = true;
                    }
                    enableStreetNames(z);
                    parcel2.writeNoException();
                    return true;
                case VideoPlayer.STATE_ENDED /*5*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    isZoomGesturesEnabled = isZoomGesturesEnabled();
                    parcel2.writeNoException();
                    if (isZoomGesturesEnabled) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                case Method.TRACE /*6*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    isZoomGesturesEnabled = isPanningGesturesEnabled();
                    parcel2.writeNoException();
                    if (isZoomGesturesEnabled) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                case Method.PATCH /*7*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    isZoomGesturesEnabled = isUserNavigationEnabled();
                    parcel2.writeNoException();
                    if (isZoomGesturesEnabled) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    isZoomGesturesEnabled = isStreetNamesEnabled();
                    parcel2.writeNoException();
                    if (isZoomGesturesEnabled) {
                        i3 = 1;
                    }
                    parcel2.writeInt(i3);
                    return true;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    animateTo(parcel.readInt() != 0 ? (StreetViewPanoramaCamera) StreetViewPanoramaCamera.CREATOR.createFromParcel(parcel) : null, parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    StreetViewPanoramaCamera panoramaCamera = getPanoramaCamera();
                    parcel2.writeNoException();
                    if (panoramaCamera != null) {
                        parcel2.writeInt(1);
                        panoramaCamera.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    setPositionWithID(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case Atom.FULL_HEADER_SIZE /*12*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    setPosition(parcel.readInt() != 0 ? (LatLng) LatLng.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    setPositionWithRadius(parcel.readInt() != 0 ? (LatLng) LatLng.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    StreetViewPanoramaLocation streetViewPanoramaLocation = getStreetViewPanoramaLocation();
                    parcel2.writeNoException();
                    if (streetViewPanoramaLocation != null) {
                        parcel2.writeInt(1);
                        streetViewPanoramaLocation.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    setOnStreetViewPanoramaChangeListener(com.google.android.gms.maps.internal.zzac.zza.zzir(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case TLRPC.USER_FLAG_PHONE /*16*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    setOnStreetViewPanoramaCameraChangeListener(com.google.android.gms.maps.internal.zzab.zza.zziq(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    setOnStreetViewPanoramaClickListener(com.google.android.gms.maps.internal.zzad.zza.zzis(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case C0338R.styleable.PromptView_secondaryText /*18*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    StreetViewPanoramaOrientation pointToOrientation = pointToOrientation(com.google.android.gms.dynamic.zzd.zza.zzfe(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    if (pointToOrientation != null) {
                        parcel2.writeInt(1);
                        pointToOrientation.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    zzd orientationToPoint = orientationToPoint(parcel.readInt() != 0 ? (StreetViewPanoramaOrientation) StreetViewPanoramaOrientation.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    if (orientationToPoint != null) {
                        iBinder = orientationToPoint.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    setOnStreetViewPanoramaLongClickListener(com.google.android.gms.maps.internal.zzae.zza.zzit(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void animateTo(StreetViewPanoramaCamera streetViewPanoramaCamera, long j);

    void enablePanning(boolean z);

    void enableStreetNames(boolean z);

    void enableUserNavigation(boolean z);

    void enableZoom(boolean z);

    StreetViewPanoramaCamera getPanoramaCamera();

    StreetViewPanoramaLocation getStreetViewPanoramaLocation();

    boolean isPanningGesturesEnabled();

    boolean isStreetNamesEnabled();

    boolean isUserNavigationEnabled();

    boolean isZoomGesturesEnabled();

    zzd orientationToPoint(StreetViewPanoramaOrientation streetViewPanoramaOrientation);

    StreetViewPanoramaOrientation pointToOrientation(zzd com_google_android_gms_dynamic_zzd);

    void setOnStreetViewPanoramaCameraChangeListener(zzab com_google_android_gms_maps_internal_zzab);

    void setOnStreetViewPanoramaChangeListener(zzac com_google_android_gms_maps_internal_zzac);

    void setOnStreetViewPanoramaClickListener(zzad com_google_android_gms_maps_internal_zzad);

    void setOnStreetViewPanoramaLongClickListener(zzae com_google_android_gms_maps_internal_zzae);

    void setPosition(LatLng latLng);

    void setPositionWithID(String str);

    void setPositionWithRadius(LatLng latLng, int i);
}
