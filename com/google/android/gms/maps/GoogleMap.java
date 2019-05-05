package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.RemoteException;
import android.support.annotation.RequiresPermission;
import android.view.View;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.zzp;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import com.google.android.gms.maps.model.internal.zzb;
import com.google.android.gms.maps.model.internal.zzc;
import com.google.android.gms.maps.model.internal.zzf;
import com.google.android.gms.maps.model.internal.zzg;
import com.google.android.gms.maps.model.internal.zzh;

public final class GoogleMap {
    public static final int MAP_TYPE_HYBRID = 4;
    public static final int MAP_TYPE_NONE = 0;
    public static final int MAP_TYPE_NORMAL = 1;
    public static final int MAP_TYPE_SATELLITE = 2;
    public static final int MAP_TYPE_TERRAIN = 3;
    private final IGoogleMapDelegate akI;
    private UiSettings akJ;

    /* renamed from: com.google.android.gms.maps.GoogleMap.10 */
    class AnonymousClass10 extends com.google.android.gms.maps.internal.zzr.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnMapLoadedCallback akU;

        AnonymousClass10(GoogleMap googleMap, OnMapLoadedCallback onMapLoadedCallback) {
            this.akL = googleMap;
            this.akU = onMapLoadedCallback;
        }

        public void onMapLoaded() {
            this.akU.onMapLoaded();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.11 */
    class AnonymousClass11 extends com.google.android.gms.maps.internal.zzk.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnGroundOverlayClickListener akV;

        AnonymousClass11(GoogleMap googleMap, OnGroundOverlayClickListener onGroundOverlayClickListener) {
            this.akL = googleMap;
            this.akV = onGroundOverlayClickListener;
        }

        public void zza(zzc com_google_android_gms_maps_model_internal_zzc) {
            this.akV.onGroundOverlayClick(new GroundOverlay(com_google_android_gms_maps_model_internal_zzc));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.12 */
    class AnonymousClass12 extends com.google.android.gms.maps.internal.ILocationSourceDelegate.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ LocationSource akW;

        /* renamed from: com.google.android.gms.maps.GoogleMap.12.1 */
        class C02251 implements OnLocationChangedListener {
            final /* synthetic */ zzp akX;
            final /* synthetic */ AnonymousClass12 akY;

            C02251(AnonymousClass12 anonymousClass12, zzp com_google_android_gms_maps_internal_zzp) {
                this.akY = anonymousClass12;
                this.akX = com_google_android_gms_maps_internal_zzp;
            }

            public void onLocationChanged(Location location) {
                try {
                    this.akX.zze(location);
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                }
            }
        }

        AnonymousClass12(GoogleMap googleMap, LocationSource locationSource) {
            this.akL = googleMap;
            this.akW = locationSource;
        }

        public void activate(zzp com_google_android_gms_maps_internal_zzp) {
            this.akW.activate(new C02251(this, com_google_android_gms_maps_internal_zzp));
        }

        public void deactivate() {
            this.akW.deactivate();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.13 */
    class AnonymousClass13 extends com.google.android.gms.maps.internal.zzj.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnCircleClickListener akZ;

        AnonymousClass13(GoogleMap googleMap, OnCircleClickListener onCircleClickListener) {
            this.akL = googleMap;
            this.akZ = onCircleClickListener;
        }

        public void zza(zzb com_google_android_gms_maps_model_internal_zzb) {
            this.akZ.onCircleClick(new Circle(com_google_android_gms_maps_model_internal_zzb));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.14 */
    class AnonymousClass14 extends com.google.android.gms.maps.internal.zzz.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnPolygonClickListener ala;

        AnonymousClass14(GoogleMap googleMap, OnPolygonClickListener onPolygonClickListener) {
            this.akL = googleMap;
            this.ala = onPolygonClickListener;
        }

        public void zza(zzg com_google_android_gms_maps_model_internal_zzg) {
            this.ala.onPolygonClick(new Polygon(com_google_android_gms_maps_model_internal_zzg));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.15 */
    class AnonymousClass15 extends com.google.android.gms.maps.internal.zzaa.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnPolylineClickListener alb;

        AnonymousClass15(GoogleMap googleMap, OnPolylineClickListener onPolylineClickListener) {
            this.akL = googleMap;
            this.alb = onPolylineClickListener;
        }

        public void zza(IPolylineDelegate iPolylineDelegate) {
            this.alb.onPolylineClick(new Polyline(iPolylineDelegate));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.16 */
    class AnonymousClass16 extends com.google.android.gms.maps.internal.zzag.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ SnapshotReadyCallback alc;

        AnonymousClass16(GoogleMap googleMap, SnapshotReadyCallback snapshotReadyCallback) {
            this.akL = googleMap;
            this.alc = snapshotReadyCallback;
        }

        public void onSnapshotReady(Bitmap bitmap) {
            this.alc.onSnapshotReady(bitmap);
        }

        public void zzag(zzd com_google_android_gms_dynamic_zzd) {
            this.alc.onSnapshotReady((Bitmap) zze.zzae(com_google_android_gms_dynamic_zzd));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.17 */
    class AnonymousClass17 extends com.google.android.gms.maps.internal.zzy.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnPoiClickListener ald;

        AnonymousClass17(GoogleMap googleMap, OnPoiClickListener onPoiClickListener) {
            this.akL = googleMap;
            this.ald = onPoiClickListener;
        }

        public void zza(PointOfInterest pointOfInterest) {
            this.ald.onPoiClick(pointOfInterest);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.18 */
    class AnonymousClass18 extends com.google.android.gms.maps.internal.zze.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnCameraChangeListener ale;

        AnonymousClass18(GoogleMap googleMap, OnCameraChangeListener onCameraChangeListener) {
            this.akL = googleMap;
            this.ale = onCameraChangeListener;
        }

        public void onCameraChange(CameraPosition cameraPosition) {
            this.ale.onCameraChange(cameraPosition);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.19 */
    class AnonymousClass19 extends com.google.android.gms.maps.internal.zzi.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnCameraMoveStartedListener alf;

        AnonymousClass19(GoogleMap googleMap, OnCameraMoveStartedListener onCameraMoveStartedListener) {
            this.akL = googleMap;
            this.alf = onCameraMoveStartedListener;
        }

        public void onCameraMoveStarted(int i) {
            this.alf.onCameraMoveStarted(i);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.1 */
    class C02261 extends com.google.android.gms.maps.internal.zzl.zza {
        final /* synthetic */ OnIndoorStateChangeListener akK;
        final /* synthetic */ GoogleMap akL;

        C02261(GoogleMap googleMap, OnIndoorStateChangeListener onIndoorStateChangeListener) {
            this.akL = googleMap;
            this.akK = onIndoorStateChangeListener;
        }

        public void onIndoorBuildingFocused() {
            this.akK.onIndoorBuildingFocused();
        }

        public void zza(com.google.android.gms.maps.model.internal.zzd com_google_android_gms_maps_model_internal_zzd) {
            this.akK.onIndoorLevelActivated(new IndoorBuilding(com_google_android_gms_maps_model_internal_zzd));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.20 */
    class AnonymousClass20 extends com.google.android.gms.maps.internal.zzh.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnCameraMoveListener alg;

        AnonymousClass20(GoogleMap googleMap, OnCameraMoveListener onCameraMoveListener) {
            this.akL = googleMap;
            this.alg = onCameraMoveListener;
        }

        public void onCameraMove() {
            this.alg.onCameraMove();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.21 */
    class AnonymousClass21 extends com.google.android.gms.maps.internal.zzg.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnCameraMoveCanceledListener alh;

        AnonymousClass21(GoogleMap googleMap, OnCameraMoveCanceledListener onCameraMoveCanceledListener) {
            this.akL = googleMap;
            this.alh = onCameraMoveCanceledListener;
        }

        public void onCameraMoveCanceled() {
            this.alh.onCameraMoveCanceled();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.22 */
    class AnonymousClass22 extends com.google.android.gms.maps.internal.zzf.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnCameraIdleListener ali;

        AnonymousClass22(GoogleMap googleMap, OnCameraIdleListener onCameraIdleListener) {
            this.akL = googleMap;
            this.ali = onCameraIdleListener;
        }

        public void onCameraIdle() {
            this.ali.onCameraIdle();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.23 */
    class AnonymousClass23 extends com.google.android.gms.maps.internal.zzq.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnMapClickListener alj;

        AnonymousClass23(GoogleMap googleMap, OnMapClickListener onMapClickListener) {
            this.akL = googleMap;
            this.alj = onMapClickListener;
        }

        public void onMapClick(LatLng latLng) {
            this.alj.onMapClick(latLng);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.24 */
    class AnonymousClass24 extends com.google.android.gms.maps.internal.zzs.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnMapLongClickListener alk;

        AnonymousClass24(GoogleMap googleMap, OnMapLongClickListener onMapLongClickListener) {
            this.akL = googleMap;
            this.alk = onMapLongClickListener;
        }

        public void onMapLongClick(LatLng latLng) {
            this.alk.onMapLongClick(latLng);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.2 */
    class C02272 extends com.google.android.gms.maps.internal.zzu.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnMarkerClickListener akM;

        C02272(GoogleMap googleMap, OnMarkerClickListener onMarkerClickListener) {
            this.akL = googleMap;
            this.akM = onMarkerClickListener;
        }

        public boolean zza(zzf com_google_android_gms_maps_model_internal_zzf) {
            return this.akM.onMarkerClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.3 */
    class C02283 extends com.google.android.gms.maps.internal.zzv.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnMarkerDragListener akN;

        C02283(GoogleMap googleMap, OnMarkerDragListener onMarkerDragListener) {
            this.akL = googleMap;
            this.akN = onMarkerDragListener;
        }

        public void zzb(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.akN.onMarkerDragStart(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }

        public void zzc(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.akN.onMarkerDragEnd(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }

        public void zzd(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.akN.onMarkerDrag(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.4 */
    class C02294 extends com.google.android.gms.maps.internal.zzm.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnInfoWindowClickListener akO;

        C02294(GoogleMap googleMap, OnInfoWindowClickListener onInfoWindowClickListener) {
            this.akL = googleMap;
            this.akO = onInfoWindowClickListener;
        }

        public void zze(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.akO.onInfoWindowClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.5 */
    class C02305 extends com.google.android.gms.maps.internal.zzo.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnInfoWindowLongClickListener akP;

        C02305(GoogleMap googleMap, OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
            this.akL = googleMap;
            this.akP = onInfoWindowLongClickListener;
        }

        public void zzf(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.akP.onInfoWindowLongClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.6 */
    class C02316 extends com.google.android.gms.maps.internal.zzn.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnInfoWindowCloseListener akQ;

        C02316(GoogleMap googleMap, OnInfoWindowCloseListener onInfoWindowCloseListener) {
            this.akL = googleMap;
            this.akQ = onInfoWindowCloseListener;
        }

        public void zzg(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.akQ.onInfoWindowClose(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.7 */
    class C02327 extends com.google.android.gms.maps.internal.zzd.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ InfoWindowAdapter akR;

        C02327(GoogleMap googleMap, InfoWindowAdapter infoWindowAdapter) {
            this.akL = googleMap;
            this.akR = infoWindowAdapter;
        }

        public zzd zzh(zzf com_google_android_gms_maps_model_internal_zzf) {
            return zze.zzac(this.akR.getInfoWindow(new Marker(com_google_android_gms_maps_model_internal_zzf)));
        }

        public zzd zzi(zzf com_google_android_gms_maps_model_internal_zzf) {
            return zze.zzac(this.akR.getInfoContents(new Marker(com_google_android_gms_maps_model_internal_zzf)));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.8 */
    class C02338 extends com.google.android.gms.maps.internal.zzx.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnMyLocationChangeListener akS;

        C02338(GoogleMap googleMap, OnMyLocationChangeListener onMyLocationChangeListener) {
            this.akL = googleMap;
            this.akS = onMyLocationChangeListener;
        }

        public void zzaf(zzd com_google_android_gms_dynamic_zzd) {
            this.akS.onMyLocationChange((Location) zze.zzae(com_google_android_gms_dynamic_zzd));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.9 */
    class C02349 extends com.google.android.gms.maps.internal.zzw.zza {
        final /* synthetic */ GoogleMap akL;
        final /* synthetic */ OnMyLocationButtonClickListener akT;

        C02349(GoogleMap googleMap, OnMyLocationButtonClickListener onMyLocationButtonClickListener) {
            this.akL = googleMap;
            this.akT = onMyLocationButtonClickListener;
        }

        public boolean onMyLocationButtonClick() {
            return this.akT.onMyLocationButtonClick();
        }
    }

    public interface CancelableCallback {
        void onCancel();

        void onFinish();
    }

    public interface InfoWindowAdapter {
        View getInfoContents(Marker marker);

        View getInfoWindow(Marker marker);
    }

    @Deprecated
    public interface OnCameraChangeListener {
        void onCameraChange(CameraPosition cameraPosition);
    }

    public interface OnCameraIdleListener {
        void onCameraIdle();
    }

    public interface OnCameraMoveCanceledListener {
        void onCameraMoveCanceled();
    }

    public interface OnCameraMoveListener {
        void onCameraMove();
    }

    public interface OnCameraMoveStartedListener {
        public static final int REASON_API_ANIMATION = 2;
        public static final int REASON_DEVELOPER_ANIMATION = 3;
        public static final int REASON_GESTURE = 1;

        void onCameraMoveStarted(int i);
    }

    public interface OnCircleClickListener {
        void onCircleClick(Circle circle);
    }

    public interface OnGroundOverlayClickListener {
        void onGroundOverlayClick(GroundOverlay groundOverlay);
    }

    public interface OnIndoorStateChangeListener {
        void onIndoorBuildingFocused();

        void onIndoorLevelActivated(IndoorBuilding indoorBuilding);
    }

    public interface OnInfoWindowClickListener {
        void onInfoWindowClick(Marker marker);
    }

    public interface OnInfoWindowCloseListener {
        void onInfoWindowClose(Marker marker);
    }

    public interface OnInfoWindowLongClickListener {
        void onInfoWindowLongClick(Marker marker);
    }

    public interface OnMapClickListener {
        void onMapClick(LatLng latLng);
    }

    public interface OnMapLoadedCallback {
        void onMapLoaded();
    }

    public interface OnMapLongClickListener {
        void onMapLongClick(LatLng latLng);
    }

    public interface OnMarkerClickListener {
        boolean onMarkerClick(Marker marker);
    }

    public interface OnMarkerDragListener {
        void onMarkerDrag(Marker marker);

        void onMarkerDragEnd(Marker marker);

        void onMarkerDragStart(Marker marker);
    }

    public interface OnMyLocationButtonClickListener {
        boolean onMyLocationButtonClick();
    }

    @Deprecated
    public interface OnMyLocationChangeListener {
        void onMyLocationChange(Location location);
    }

    public interface OnPoiClickListener {
        void onPoiClick(PointOfInterest pointOfInterest);
    }

    public interface OnPolygonClickListener {
        void onPolygonClick(Polygon polygon);
    }

    public interface OnPolylineClickListener {
        void onPolylineClick(Polyline polyline);
    }

    public interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap bitmap);
    }

    private static final class zza extends com.google.android.gms.maps.internal.zzb.zza {
        private final CancelableCallback all;

        zza(CancelableCallback cancelableCallback) {
            this.all = cancelableCallback;
        }

        public void onCancel() {
            this.all.onCancel();
        }

        public void onFinish() {
            this.all.onFinish();
        }
    }

    protected GoogleMap(IGoogleMapDelegate iGoogleMapDelegate) {
        this.akI = (IGoogleMapDelegate) zzac.zzy(iGoogleMapDelegate);
    }

    public final Circle addCircle(CircleOptions circleOptions) {
        try {
            return new Circle(this.akI.addCircle(circleOptions));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final GroundOverlay addGroundOverlay(GroundOverlayOptions groundOverlayOptions) {
        try {
            zzc addGroundOverlay = this.akI.addGroundOverlay(groundOverlayOptions);
            return addGroundOverlay != null ? new GroundOverlay(addGroundOverlay) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Marker addMarker(MarkerOptions markerOptions) {
        try {
            zzf addMarker = this.akI.addMarker(markerOptions);
            return addMarker != null ? new Marker(addMarker) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Polygon addPolygon(PolygonOptions polygonOptions) {
        try {
            return new Polygon(this.akI.addPolygon(polygonOptions));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Polyline addPolyline(PolylineOptions polylineOptions) {
        try {
            return new Polyline(this.akI.addPolyline(polylineOptions));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final TileOverlay addTileOverlay(TileOverlayOptions tileOverlayOptions) {
        try {
            zzh addTileOverlay = this.akI.addTileOverlay(tileOverlayOptions);
            return addTileOverlay != null ? new TileOverlay(addTileOverlay) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate) {
        try {
            this.akI.animateCamera(cameraUpdate.zzbrh());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate, int i, CancelableCallback cancelableCallback) {
        try {
            this.akI.animateCameraWithDurationAndCallback(cameraUpdate.zzbrh(), i, cancelableCallback == null ? null : new zza(cancelableCallback));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate, CancelableCallback cancelableCallback) {
        try {
            this.akI.animateCameraWithCallback(cameraUpdate.zzbrh(), cancelableCallback == null ? null : new zza(cancelableCallback));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void clear() {
        try {
            this.akI.clear();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final CameraPosition getCameraPosition() {
        try {
            return this.akI.getCameraPosition();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public IndoorBuilding getFocusedBuilding() {
        try {
            com.google.android.gms.maps.model.internal.zzd focusedBuilding = this.akI.getFocusedBuilding();
            return focusedBuilding != null ? new IndoorBuilding(focusedBuilding) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final int getMapType() {
        try {
            return this.akI.getMapType();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final float getMaxZoomLevel() {
        try {
            return this.akI.getMaxZoomLevel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final float getMinZoomLevel() {
        try {
            return this.akI.getMinZoomLevel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Deprecated
    public final Location getMyLocation() {
        try {
            return this.akI.getMyLocation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Projection getProjection() {
        try {
            return new Projection(this.akI.getProjection());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final UiSettings getUiSettings() {
        try {
            if (this.akJ == null) {
                this.akJ = new UiSettings(this.akI.getUiSettings());
            }
            return this.akJ;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isBuildingsEnabled() {
        try {
            return this.akI.isBuildingsEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isIndoorEnabled() {
        try {
            return this.akI.isIndoorEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isMyLocationEnabled() {
        try {
            return this.akI.isMyLocationEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isTrafficEnabled() {
        try {
            return this.akI.isTrafficEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void moveCamera(CameraUpdate cameraUpdate) {
        try {
            this.akI.moveCamera(cameraUpdate.zzbrh());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void resetMinMaxZoomPreference() {
        try {
            this.akI.resetMinMaxZoomPreference();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setBuildingsEnabled(boolean z) {
        try {
            this.akI.setBuildingsEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setContentDescription(String str) {
        try {
            this.akI.setContentDescription(str);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean setIndoorEnabled(boolean z) {
        try {
            return this.akI.setIndoorEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setInfoWindowAdapter(InfoWindowAdapter infoWindowAdapter) {
        if (infoWindowAdapter == null) {
            try {
                this.akI.setInfoWindowAdapter(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setInfoWindowAdapter(new C02327(this, infoWindowAdapter));
    }

    public void setLatLngBoundsForCameraTarget(LatLngBounds latLngBounds) {
        try {
            this.akI.setLatLngBoundsForCameraTarget(latLngBounds);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setLocationSource(LocationSource locationSource) {
        if (locationSource == null) {
            try {
                this.akI.setLocationSource(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setLocationSource(new AnonymousClass12(this, locationSource));
    }

    public boolean setMapStyle(MapStyleOptions mapStyleOptions) {
        try {
            return this.akI.setMapStyle(mapStyleOptions);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMapType(int i) {
        try {
            this.akI.setMapType(i);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setMaxZoomPreference(float f) {
        try {
            this.akI.setMaxZoomPreference(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setMinZoomPreference(float f) {
        try {
            this.akI.setMinZoomPreference(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public final void setMyLocationEnabled(boolean z) {
        try {
            this.akI.setMyLocationEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Deprecated
    public final void setOnCameraChangeListener(OnCameraChangeListener onCameraChangeListener) {
        if (onCameraChangeListener == null) {
            try {
                this.akI.setOnCameraChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnCameraChangeListener(new AnonymousClass18(this, onCameraChangeListener));
    }

    public final void setOnCameraIdleListener(OnCameraIdleListener onCameraIdleListener) {
        if (onCameraIdleListener == null) {
            try {
                this.akI.setOnCameraIdleListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnCameraIdleListener(new AnonymousClass22(this, onCameraIdleListener));
    }

    public final void setOnCameraMoveCanceledListener(OnCameraMoveCanceledListener onCameraMoveCanceledListener) {
        if (onCameraMoveCanceledListener == null) {
            try {
                this.akI.setOnCameraMoveCanceledListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnCameraMoveCanceledListener(new AnonymousClass21(this, onCameraMoveCanceledListener));
    }

    public final void setOnCameraMoveListener(OnCameraMoveListener onCameraMoveListener) {
        if (onCameraMoveListener == null) {
            try {
                this.akI.setOnCameraMoveListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnCameraMoveListener(new AnonymousClass20(this, onCameraMoveListener));
    }

    public final void setOnCameraMoveStartedListener(OnCameraMoveStartedListener onCameraMoveStartedListener) {
        if (onCameraMoveStartedListener == null) {
            try {
                this.akI.setOnCameraMoveStartedListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnCameraMoveStartedListener(new AnonymousClass19(this, onCameraMoveStartedListener));
    }

    public final void setOnCircleClickListener(OnCircleClickListener onCircleClickListener) {
        if (onCircleClickListener == null) {
            try {
                this.akI.setOnCircleClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnCircleClickListener(new AnonymousClass13(this, onCircleClickListener));
    }

    public final void setOnGroundOverlayClickListener(OnGroundOverlayClickListener onGroundOverlayClickListener) {
        if (onGroundOverlayClickListener == null) {
            try {
                this.akI.setOnGroundOverlayClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnGroundOverlayClickListener(new AnonymousClass11(this, onGroundOverlayClickListener));
    }

    public final void setOnIndoorStateChangeListener(OnIndoorStateChangeListener onIndoorStateChangeListener) {
        if (onIndoorStateChangeListener == null) {
            try {
                this.akI.setOnIndoorStateChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnIndoorStateChangeListener(new C02261(this, onIndoorStateChangeListener));
    }

    public final void setOnInfoWindowClickListener(OnInfoWindowClickListener onInfoWindowClickListener) {
        if (onInfoWindowClickListener == null) {
            try {
                this.akI.setOnInfoWindowClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnInfoWindowClickListener(new C02294(this, onInfoWindowClickListener));
    }

    public final void setOnInfoWindowCloseListener(OnInfoWindowCloseListener onInfoWindowCloseListener) {
        if (onInfoWindowCloseListener == null) {
            try {
                this.akI.setOnInfoWindowCloseListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnInfoWindowCloseListener(new C02316(this, onInfoWindowCloseListener));
    }

    public final void setOnInfoWindowLongClickListener(OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
        if (onInfoWindowLongClickListener == null) {
            try {
                this.akI.setOnInfoWindowLongClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnInfoWindowLongClickListener(new C02305(this, onInfoWindowLongClickListener));
    }

    public final void setOnMapClickListener(OnMapClickListener onMapClickListener) {
        if (onMapClickListener == null) {
            try {
                this.akI.setOnMapClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnMapClickListener(new AnonymousClass23(this, onMapClickListener));
    }

    public void setOnMapLoadedCallback(OnMapLoadedCallback onMapLoadedCallback) {
        if (onMapLoadedCallback == null) {
            try {
                this.akI.setOnMapLoadedCallback(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnMapLoadedCallback(new AnonymousClass10(this, onMapLoadedCallback));
    }

    public final void setOnMapLongClickListener(OnMapLongClickListener onMapLongClickListener) {
        if (onMapLongClickListener == null) {
            try {
                this.akI.setOnMapLongClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnMapLongClickListener(new AnonymousClass24(this, onMapLongClickListener));
    }

    public final void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
        if (onMarkerClickListener == null) {
            try {
                this.akI.setOnMarkerClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnMarkerClickListener(new C02272(this, onMarkerClickListener));
    }

    public final void setOnMarkerDragListener(OnMarkerDragListener onMarkerDragListener) {
        if (onMarkerDragListener == null) {
            try {
                this.akI.setOnMarkerDragListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnMarkerDragListener(new C02283(this, onMarkerDragListener));
    }

    public final void setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener onMyLocationButtonClickListener) {
        if (onMyLocationButtonClickListener == null) {
            try {
                this.akI.setOnMyLocationButtonClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnMyLocationButtonClickListener(new C02349(this, onMyLocationButtonClickListener));
    }

    @Deprecated
    public final void setOnMyLocationChangeListener(OnMyLocationChangeListener onMyLocationChangeListener) {
        if (onMyLocationChangeListener == null) {
            try {
                this.akI.setOnMyLocationChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnMyLocationChangeListener(new C02338(this, onMyLocationChangeListener));
    }

    public final void setOnPoiClickListener(OnPoiClickListener onPoiClickListener) {
        if (onPoiClickListener == null) {
            try {
                this.akI.setOnPoiClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnPoiClickListener(new AnonymousClass17(this, onPoiClickListener));
    }

    public final void setOnPolygonClickListener(OnPolygonClickListener onPolygonClickListener) {
        if (onPolygonClickListener == null) {
            try {
                this.akI.setOnPolygonClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnPolygonClickListener(new AnonymousClass14(this, onPolygonClickListener));
    }

    public final void setOnPolylineClickListener(OnPolylineClickListener onPolylineClickListener) {
        if (onPolylineClickListener == null) {
            try {
                this.akI.setOnPolylineClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.akI.setOnPolylineClickListener(new AnonymousClass15(this, onPolylineClickListener));
    }

    public final void setPadding(int i, int i2, int i3, int i4) {
        try {
            this.akI.setPadding(i, i2, i3, i4);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setTrafficEnabled(boolean z) {
        try {
            this.akI.setTrafficEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void snapshot(SnapshotReadyCallback snapshotReadyCallback) {
        snapshot(snapshotReadyCallback, null);
    }

    public final void snapshot(SnapshotReadyCallback snapshotReadyCallback, Bitmap bitmap) {
        try {
            this.akI.snapshot(new AnonymousClass16(this, snapshotReadyCallback), (zze) (bitmap != null ? zze.zzac(bitmap) : null));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void stopAnimation() {
        try {
            this.akI.stopAnimation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
