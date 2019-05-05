package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.MessageMedia;
import com.hanista.mobogram.tgnet.TLRPC.TL_geoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;
import com.hanista.mobogram.ui.Adapters.LocationActivityAdapter;
import com.hanista.mobogram.ui.Adapters.LocationActivitySearchAdapter;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.MapPlaceholderDrawable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int share = 1;
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private BackupImageView avatarImageView;
    private boolean checkPermission;
    private CircleOptions circleOptions;
    private LocationActivityDelegate delegate;
    private TextView distanceTextView;
    private LinearLayout emptyTextLayout;
    private boolean firstWas;
    private GoogleMap googleMap;
    private ListView listView;
    private ImageView locationButton;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private ImageView markerImageView;
    private int markerTop;
    private ImageView markerXImageView;
    private MessageObject messageObject;
    private Location myLocation;
    private TextView nameTextView;
    private boolean onResumeCalled;
    private int overScrollHeight;
    private LocationActivitySearchAdapter searchAdapter;
    private ListView searchListView;
    private boolean searchWas;
    private boolean searching;
    private Location userLocation;
    private boolean userLocationMoved;
    private boolean wasResults;

    public interface LocationActivityDelegate {
        void didSelectLocation(MessageMedia messageMedia);
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.13 */
    class AnonymousClass13 extends MapView {
        AnonymousClass13(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            AnimatorSet access$2100;
            Animator[] animatorArr;
            float[] fArr;
            if (motionEvent.getAction() == 0) {
                if (LocationActivity.this.animatorSet != null) {
                    LocationActivity.this.animatorSet.cancel();
                }
                LocationActivity.this.animatorSet = new AnimatorSet();
                LocationActivity.this.animatorSet.setDuration(200);
                access$2100 = LocationActivity.this.animatorSet;
                animatorArr = new Animator[LocationActivity.map_list_menu_map];
                fArr = new float[LocationActivity.share];
                fArr[0] = (float) (LocationActivity.this.markerTop + (-AndroidUtilities.dp(10.0f)));
                animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", fArr);
                fArr = new float[LocationActivity.share];
                fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[LocationActivity.share] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", fArr);
                access$2100.playTogether(animatorArr);
                LocationActivity.this.animatorSet.start();
            } else if (motionEvent.getAction() == LocationActivity.share) {
                if (LocationActivity.this.animatorSet != null) {
                    LocationActivity.this.animatorSet.cancel();
                }
                LocationActivity.this.animatorSet = new AnimatorSet();
                LocationActivity.this.animatorSet.setDuration(200);
                access$2100 = LocationActivity.this.animatorSet;
                animatorArr = new Animator[LocationActivity.map_list_menu_map];
                fArr = new float[LocationActivity.share];
                fArr[0] = (float) LocationActivity.this.markerTop;
                animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", fArr);
                fArr = new float[LocationActivity.share];
                fArr[0] = 0.0f;
                animatorArr[LocationActivity.share] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", fArr);
                access$2100.playTogether(animatorArr);
                LocationActivity.this.animatorSet.start();
            }
            if (motionEvent.getAction() == LocationActivity.map_list_menu_map) {
                if (!LocationActivity.this.userLocationMoved) {
                    access$2100 = new AnimatorSet();
                    access$2100.setDuration(200);
                    float[] fArr2 = new float[LocationActivity.share];
                    fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    access$2100.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", fArr2));
                    access$2100.start();
                    LocationActivity.this.userLocationMoved = true;
                }
                if (!(LocationActivity.this.googleMap == null || LocationActivity.this.userLocation == null)) {
                    LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.getCameraPosition().target.latitude);
                    LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.getCameraPosition().target.longitude);
                }
                LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.14 */
    class AnonymousClass14 implements Runnable {
        final /* synthetic */ MapView val$map;

        /* renamed from: com.hanista.mobogram.ui.LocationActivity.14.1 */
        class C16161 implements Runnable {

            /* renamed from: com.hanista.mobogram.ui.LocationActivity.14.1.1 */
            class C16151 implements OnMapReadyCallback {
                C16151() {
                }

                public void onMapReady(GoogleMap googleMap) {
                    LocationActivity.this.googleMap = googleMap;
                    LocationActivity.this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    LocationActivity.this.onMapInit();
                }
            }

            C16161() {
            }

            public void run() {
                if (LocationActivity.this.mapView != null && LocationActivity.this.getParentActivity() != null) {
                    try {
                        AnonymousClass14.this.val$map.onCreate(null);
                        MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                        LocationActivity.this.mapView.getMapAsync(new C16151());
                        LocationActivity.this.mapsInitialized = true;
                        if (LocationActivity.this.onResumeCalled) {
                            LocationActivity.this.mapView.onResume();
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }

        AnonymousClass14(MapView mapView) {
            this.val$map = mapView;
        }

        public void run() {
            try {
                this.val$map.onCreate(null);
            } catch (Exception e) {
            }
            AndroidUtilities.runOnUIThread(new C16161());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.1 */
    class C16171 extends ActionBarMenuOnItemClick {
        C16171() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                LocationActivity.this.finishFragment();
            } else if (i == LocationActivity.map_list_menu_map) {
                if (LocationActivity.this.googleMap != null) {
                    LocationActivity.this.googleMap.setMapType(LocationActivity.share);
                }
            } else if (i == LocationActivity.map_list_menu_satellite) {
                if (LocationActivity.this.googleMap != null) {
                    LocationActivity.this.googleMap.setMapType(LocationActivity.map_list_menu_map);
                }
            } else if (i == LocationActivity.map_list_menu_hybrid) {
                if (LocationActivity.this.googleMap != null) {
                    LocationActivity.this.googleMap.setMapType(LocationActivity.map_list_menu_hybrid);
                }
            } else if (i == LocationActivity.share) {
                try {
                    double d = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                    double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                    LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + d + "," + d2 + "?q=" + d + "," + d2)));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.2 */
    class C16182 extends ActionBarMenuItemSearchListener {
        C16182() {
        }

        public void onSearchCollapse() {
            LocationActivity.this.searching = false;
            LocationActivity.this.searchWas = false;
            LocationActivity.this.searchListView.setEmptyView(null);
            LocationActivity.this.listView.setVisibility(0);
            LocationActivity.this.mapViewClip.setVisibility(0);
            LocationActivity.this.searchListView.setVisibility(8);
            LocationActivity.this.emptyTextLayout.setVisibility(8);
            LocationActivity.this.searchAdapter.searchDelayed(null, null);
        }

        public void onSearchExpand() {
            LocationActivity.this.searching = true;
            LocationActivity.this.listView.setVisibility(8);
            LocationActivity.this.mapViewClip.setVisibility(8);
            LocationActivity.this.searchListView.setVisibility(0);
            LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyTextLayout);
        }

        public void onTextChanged(EditText editText) {
            if (LocationActivity.this.searchAdapter != null) {
                String obj = editText.getText().toString();
                if (obj.length() != 0) {
                    LocationActivity.this.searchWas = true;
                }
                LocationActivity.this.searchAdapter.searchDelayed(obj, LocationActivity.this.userLocation);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.3 */
    class C16193 extends FrameLayout {
        private boolean first;

        C16193(Context context) {
            super(context);
            this.first = true;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (z) {
                LocationActivity.this.fixLayoutInternal(this.first);
                this.first = false;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.4 */
    class C16204 extends ViewOutlineProvider {
        C16204() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.5 */
    class C16235 implements Runnable {
        final /* synthetic */ MapView val$map;

        /* renamed from: com.hanista.mobogram.ui.LocationActivity.5.1 */
        class C16221 implements Runnable {

            /* renamed from: com.hanista.mobogram.ui.LocationActivity.5.1.1 */
            class C16211 implements OnMapReadyCallback {
                C16211() {
                }

                public void onMapReady(GoogleMap googleMap) {
                    LocationActivity.this.googleMap = googleMap;
                    LocationActivity.this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    LocationActivity.this.onMapInit();
                }
            }

            C16221() {
            }

            public void run() {
                if (LocationActivity.this.mapView != null && LocationActivity.this.getParentActivity() != null) {
                    try {
                        C16235.this.val$map.onCreate(null);
                        MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                        LocationActivity.this.mapView.getMapAsync(new C16211());
                        LocationActivity.this.mapsInitialized = true;
                        if (LocationActivity.this.onResumeCalled) {
                            LocationActivity.this.mapView.onResume();
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }

        C16235(MapView mapView) {
            this.val$map = mapView;
        }

        public void run() {
            try {
                this.val$map.onCreate(null);
            } catch (Exception e) {
            }
            AndroidUtilities.runOnUIThread(new C16221());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.6 */
    class C16246 implements OnClickListener {
        C16246() {
        }

        public void onClick(View view) {
            if (LocationActivity.this.userLocation != null) {
                LatLng latLng = new LatLng(LocationActivity.this.userLocation.getLatitude(), LocationActivity.this.userLocation.getLongitude());
                if (LocationActivity.this.googleMap != null) {
                    LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.7 */
    class C16257 extends ViewOutlineProvider {
        C16257() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.8 */
    class C16268 implements OnClickListener {
        C16268() {
        }

        public void onClick(View view) {
            if (VERSION.SDK_INT >= 23) {
                Activity parentActivity = LocationActivity.this.getParentActivity();
                if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                    LocationActivity.this.showPermissionAlert(true);
                    return;
                }
            }
            if (LocationActivity.this.myLocation != null) {
                try {
                    Object[] objArr = new Object[LocationActivity.map_list_menu_hybrid];
                    objArr[0] = Double.valueOf(LocationActivity.this.myLocation.getLatitude());
                    objArr[LocationActivity.share] = Double.valueOf(LocationActivity.this.myLocation.getLongitude());
                    objArr[LocationActivity.map_list_menu_map] = Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat);
                    objArr[LocationActivity.map_list_menu_satellite] = Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long);
                    LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", objArr))));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LocationActivity.9 */
    class C16279 implements OnClickListener {
        C16279() {
        }

        public void onClick(View view) {
            if (VERSION.SDK_INT >= 23) {
                Activity parentActivity = LocationActivity.this.getParentActivity();
                if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                    LocationActivity.this.showPermissionAlert(true);
                    return;
                }
            }
            if (LocationActivity.this.myLocation != null && LocationActivity.this.googleMap != null) {
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude()), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        }
    }

    public LocationActivity() {
        this.checkPermission = true;
        this.userLocationMoved = false;
        this.firstWas = false;
        this.overScrollHeight = (AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f);
    }

    private void fixLayoutInternal(boolean z) {
        if (this.listView != null) {
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
            int measuredHeight = this.fragmentView.getMeasuredHeight();
            if (measuredHeight != 0) {
                this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(66.0f)) - currentActionBarHeight;
                LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                layoutParams.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.searchListView.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                this.searchListView.setLayoutParams(layoutParams);
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    if (this.googleMap != null) {
                        this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams);
                }
                this.adapter.notifyDataSetChanged();
                if (z) {
                    this.listView.setSelectionFromTop(0, -((int) ((((float) AndroidUtilities.dp(56.0f)) * 2.5f) + ((float) AndroidUtilities.dp(102.0f)))));
                    updateClipView(this.listView.getFirstVisiblePosition());
                    this.listView.post(new Runnable() {
                        public void run() {
                            LocationActivity.this.listView.setSelectionFromTop(0, -((int) ((((float) AndroidUtilities.dp(56.0f)) * 2.5f) + ((float) AndroidUtilities.dp(102.0f)))));
                            LocationActivity.this.updateClipView(LocationActivity.this.listView.getFirstVisiblePosition());
                        }
                    });
                    return;
                }
                updateClipView(this.listView.getFirstVisiblePosition());
            }
        }
    }

    private Location getLastLocation() {
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List providers = locationManager.getProviders(true);
        Location location = null;
        for (int size = providers.size() - 1; size >= 0; size--) {
            location = locationManager.getLastKnownLocation((String) providers.get(size));
            if (location != null) {
                return location;
            }
        }
        return location;
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            if (this.messageObject != null) {
                LatLng latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                try {
                    this.googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(C0338R.drawable.map_pin)));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
            } else {
                this.userLocation = new Location("network");
                this.userLocation.setLatitude(20.659322d);
                this.userLocation.setLongitude(-11.40625d);
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
                public void onMyLocationChange(Location location) {
                    LocationActivity.this.positionMarker(location);
                }
            });
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
        }
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            if (this.messageObject != null) {
                if (this.userLocation != null && this.distanceTextView != null) {
                    float distanceTo = location.distanceTo(this.userLocation);
                    TextView textView;
                    Object[] objArr;
                    if (distanceTo < 1000.0f) {
                        textView = this.distanceTextView;
                        objArr = new Object[map_list_menu_map];
                        objArr[0] = Integer.valueOf((int) distanceTo);
                        objArr[share] = LocaleController.getString("MetersAway", C0338R.string.MetersAway);
                        textView.setText(String.format("%d %s", objArr));
                        return;
                    }
                    textView = this.distanceTextView;
                    objArr = new Object[map_list_menu_map];
                    objArr[0] = Float.valueOf(distanceTo / 1000.0f);
                    objArr[share] = LocaleController.getString("KMetersAway", C0338R.string.KMetersAway);
                    textView.setText(String.format("%.2f %s", objArr));
                }
            } else if (this.googleMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (this.adapter != null) {
                    this.adapter.searchGooglePlacesWithQuery(null, this.myLocation);
                    this.adapter.setGpsLocation(this.myLocation);
                }
                if (!this.userLocationMoved) {
                    this.userLocation = new Location(location);
                    if (this.firstWas) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        return;
                    }
                    this.firstWas = true;
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            }
        }
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", C0338R.string.PermissionNoLocationPosition));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", C0338R.string.PermissionNoLocation));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", C0338R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                @TargetApi(9)
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (LocationActivity.this.getParentActivity() != null) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            LocationActivity.this.getParentActivity().startActivity(intent);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void updateClipView(int i) {
        View childAt = this.listView.getChildAt(0);
        if (childAt != null) {
            int top;
            int i2;
            if (i == 0) {
                top = childAt.getTop();
                i2 = (top < 0 ? top : 0) + this.overScrollHeight;
            } else {
                top = 0;
                i2 = 0;
            }
            if (((LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
                if (i2 <= 0) {
                    if (this.mapView.getVisibility() == 0) {
                        this.mapView.setVisibility(map_list_menu_hybrid);
                        this.mapViewClip.setVisibility(map_list_menu_hybrid);
                    }
                } else if (this.mapView.getVisibility() == map_list_menu_hybrid) {
                    this.mapView.setVisibility(0);
                    this.mapViewClip.setVisibility(0);
                }
                this.mapViewClip.setTranslationY((float) Math.min(0, top));
                this.mapView.setTranslationY((float) Math.max(0, (-top) / map_list_menu_map));
                ImageView imageView = this.markerImageView;
                int dp = ((-top) - AndroidUtilities.dp(42.0f)) + (i2 / map_list_menu_map);
                this.markerTop = dp;
                imageView.setTranslationY((float) dp);
                this.markerXImageView.setTranslationY((float) (((-top) - AndroidUtilities.dp(7.0f)) + (i2 / map_list_menu_map)));
                LayoutParams layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams != null && layoutParams.height != this.overScrollHeight + AndroidUtilities.dp(10.0f)) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    if (this.googleMap != null) {
                        this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams);
                }
            }
        }
    }

    private void updateSearchInterface() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private void updateUserData() {
        if (this.messageObject != null && this.avatarImageView != null) {
            CharSequence userName;
            TLObject tLObject;
            Drawable drawable;
            int i = this.messageObject.messageOwner.from_id;
            if (this.messageObject.isForwarded()) {
                i = this.messageObject.messageOwner.fwd_from.channel_id != 0 ? -this.messageObject.messageOwner.fwd_from.channel_id : this.messageObject.messageOwner.fwd_from.from_id;
            }
            String str = TtmlNode.ANONYMOUS_REGION_ID;
            TLObject tLObject2;
            Drawable avatarDrawable;
            Drawable drawable2;
            Object obj;
            if (i > 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(i));
                if (user != null) {
                    tLObject2 = user.photo != null ? user.photo.photo_small : null;
                    avatarDrawable = new AvatarDrawable(user);
                    userName = UserObject.getUserName(user);
                    drawable2 = avatarDrawable;
                    tLObject = tLObject2;
                    drawable = drawable2;
                } else {
                    drawable = null;
                    obj = str;
                    tLObject = null;
                }
            } else {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
                if (chat != null) {
                    tLObject2 = chat.photo != null ? chat.photo.photo_small : null;
                    avatarDrawable = new AvatarDrawable(chat);
                    userName = chat.title;
                    drawable2 = avatarDrawable;
                    tLObject = tLObject2;
                    drawable = drawable2;
                } else {
                    drawable = null;
                    obj = str;
                    tLObject = null;
                }
            }
            if (drawable != null) {
                this.avatarImageView.setImage(tLObject, null, drawable);
                this.nameTextView.setText(userName);
                return;
            }
            this.avatarImageView.setImageDrawable(null);
        }
    }

    public View createView(Context context) {
        int i = map_list_menu_satellite;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(this.messageObject != null);
        this.actionBar.setActionBarMenuOnItemClick(new C16171());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.messageObject != null) {
            if (this.messageObject.messageOwner.media.title == null || this.messageObject.messageOwner.media.title.length() <= 0) {
                this.actionBar.setTitle(LocaleController.getString("ChatLocation", C0338R.string.ChatLocation));
            } else {
                this.actionBar.setTitle(this.messageObject.messageOwner.media.title);
                if (this.messageObject.messageOwner.media.address != null && this.messageObject.messageOwner.media.address.length() > 0) {
                    this.actionBar.setSubtitle(this.messageObject.messageOwner.media.address);
                }
            }
            createMenu.addItem((int) share, (int) C0338R.drawable.share);
        } else {
            this.actionBar.setTitle(LocaleController.getString("ShareLocation", C0338R.string.ShareLocation));
            createMenu.addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C16182()).getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        }
        ActionBarMenuItem addItem = createMenu.addItem(0, (int) C0338R.drawable.ic_ab_other);
        addItem.addSubItem(map_list_menu_map, LocaleController.getString("Map", C0338R.string.Map), 0);
        addItem.addSubItem(map_list_menu_satellite, LocaleController.getString("Satellite", C0338R.string.Satellite), 0);
        addItem.addSubItem(map_list_menu_hybrid, LocaleController.getString("Hybrid", C0338R.string.Hybrid), 0);
        this.fragmentView = new C16193(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.locationButton = new ImageView(context);
        this.locationButton.setBackgroundResource(C0338R.drawable.floating_user_states);
        this.locationButton.setImageResource(C0338R.drawable.myloc_on);
        this.locationButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            int[] iArr = new int[share];
            iArr[0] = 16842919;
            float[] fArr = new float[map_list_menu_map];
            fArr[0] = (float) AndroidUtilities.dp(2.0f);
            fArr[share] = (float) AndroidUtilities.dp(4.0f);
            stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.locationButton, "translationZ", fArr).setDuration(200));
            iArr = new int[0];
            fArr = new float[map_list_menu_map];
            fArr[0] = (float) AndroidUtilities.dp(4.0f);
            fArr[share] = (float) AndroidUtilities.dp(2.0f);
            stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.locationButton, "translationZ", fArr).setDuration(200));
            this.locationButton.setStateListAnimator(stateListAnimator);
            this.locationButton.setOutlineProvider(new C16204());
        }
        View view;
        if (this.messageObject != null) {
            this.mapView = new MapView(context);
            frameLayout.setBackgroundDrawable(new MapPlaceholderDrawable());
            new Thread(new C16235(this.mapView)).start();
            View frameLayout2 = new FrameLayout(context);
            frameLayout2.setBackgroundResource(C0338R.drawable.location_panel);
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 60, 83));
            frameLayout2.setOnClickListener(new C16246());
            this.avatarImageView = new BackupImageView(context);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            frameLayout2.addView(this.avatarImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : map_list_menu_satellite) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 12.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextSize(share, 16.0f);
            this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.nameTextView.setMaxLines(share);
            this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
            this.nameTextView.setEllipsize(TruncateAt.END);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setGravity(LocaleController.isRTL ? 5 : map_list_menu_satellite);
            frameLayout2.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : map_list_menu_satellite) | 48, LocaleController.isRTL ? 12.0f : 72.0f, 10.0f, LocaleController.isRTL ? 72.0f : 12.0f, 0.0f));
            this.distanceTextView = new TextView(context);
            this.distanceTextView.setTextSize(share, 14.0f);
            this.distanceTextView.setTextColor(-13660983);
            this.distanceTextView.setMaxLines(share);
            this.distanceTextView.setEllipsize(TruncateAt.END);
            this.distanceTextView.setSingleLine(true);
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : map_list_menu_satellite);
            frameLayout2.addView(this.distanceTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : map_list_menu_satellite) | 48, LocaleController.isRTL ? 12.0f : 72.0f, 33.0f, LocaleController.isRTL ? 72.0f : 12.0f, 0.0f));
            this.userLocation = new Location("network");
            this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
            this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
            View imageView = new ImageView(context);
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.floating_white);
            if (drawable != null) {
                drawable.setColorFilter(ThemeUtil.m2485a().m2289c(), Mode.MULTIPLY);
            }
            imageView.setBackgroundDrawable(drawable);
            imageView.setImageResource(C0338R.drawable.navigate);
            imageView.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                stateListAnimator = new StateListAnimator();
                iArr = new int[share];
                iArr[0] = 16842919;
                float[] fArr2 = new float[map_list_menu_map];
                fArr2[0] = (float) AndroidUtilities.dp(2.0f);
                fArr2[share] = (float) AndroidUtilities.dp(4.0f);
                stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(imageView, "translationZ", fArr2).setDuration(200));
                iArr = new int[0];
                fArr2 = new float[map_list_menu_map];
                fArr2[0] = (float) AndroidUtilities.dp(4.0f);
                fArr2[share] = (float) AndroidUtilities.dp(2.0f);
                stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(imageView, "translationZ", fArr2).setDuration(200));
                imageView.setStateListAnimator(stateListAnimator);
                imageView.setOutlineProvider(new C16257());
            }
            frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? map_list_menu_satellite : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 28.0f));
            imageView.setOnClickListener(new C16268());
            view = this.locationButton;
            if (!LocaleController.isRTL) {
                i = 5;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 100.0f));
            this.locationButton.setOnClickListener(new C16279());
        } else {
            this.searchWas = false;
            this.searching = false;
            this.mapViewClip = new FrameLayout(context);
            this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
            if (this.adapter != null) {
                this.adapter.destroy();
            }
            if (this.searchAdapter != null) {
                this.searchAdapter.destroy();
            }
            this.listView = new ListView(context);
            ListView listView = this.listView;
            ListAdapter locationActivityAdapter = new LocationActivityAdapter(context);
            this.adapter = locationActivityAdapter;
            listView.setAdapter(locationActivityAdapter);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setDividerHeight(0);
            this.listView.setDivider(null);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                    if (i3 != 0) {
                        LocationActivity.this.updateClipView(i);
                    }
                }

                public void onScrollStateChanged(AbsListView absListView, int i) {
                }
            });
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    if (i == LocationActivity.share) {
                        if (!(LocationActivity.this.delegate == null || LocationActivity.this.userLocation == null)) {
                            MessageMedia tL_messageMediaGeo = new TL_messageMediaGeo();
                            tL_messageMediaGeo.geo = new TL_geoPoint();
                            tL_messageMediaGeo.geo.lat = LocationActivity.this.userLocation.getLatitude();
                            tL_messageMediaGeo.geo._long = LocationActivity.this.userLocation.getLongitude();
                            LocationActivity.this.delegate.didSelectLocation(tL_messageMediaGeo);
                        }
                        LocationActivity.this.finishFragment();
                        return;
                    }
                    tL_messageMediaGeo = LocationActivity.this.adapter.getItem(i);
                    if (!(tL_messageMediaGeo == null || LocationActivity.this.delegate == null)) {
                        LocationActivity.this.delegate.didSelectLocation(tL_messageMediaGeo);
                    }
                    LocationActivity.this.finishFragment();
                }
            });
            this.adapter.setDelegate(new BaseLocationAdapterDelegate() {
                public void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> arrayList) {
                    if (!LocationActivity.this.wasResults && !arrayList.isEmpty()) {
                        LocationActivity.this.wasResults = true;
                    }
                }
            });
            this.adapter.setOverScrollHeight(this.overScrollHeight);
            frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
            this.mapView = new AnonymousClass13(context);
            new Thread(new AnonymousClass14(this.mapView)).start();
            view = new View(context);
            view.setBackgroundResource(C0338R.drawable.header_shadow_reverse);
            this.mapViewClip.addView(view, LayoutHelper.createFrame(-1, map_list_menu_satellite, 83));
            this.markerImageView = new ImageView(context);
            this.markerImageView.setImageResource(C0338R.drawable.map_pin);
            this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(24, 42, 49));
            this.markerXImageView = new ImageView(context);
            this.markerXImageView.setAlpha(0.0f);
            this.markerXImageView.setImageResource(C0338R.drawable.place_x);
            this.mapViewClip.addView(this.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
            FrameLayout frameLayout3 = this.mapViewClip;
            View view2 = this.locationButton;
            int i2 = VERSION.SDK_INT >= 21 ? 56 : 60;
            float f = VERSION.SDK_INT >= 21 ? 56.0f : BitmapDescriptorFactory.HUE_YELLOW;
            if (!LocaleController.isRTL) {
                i = 5;
            }
            frameLayout3.addView(view2, LayoutHelper.createFrame(i2, f, i | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
            this.locationButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VERSION.SDK_INT >= 23) {
                        Activity parentActivity = LocationActivity.this.getParentActivity();
                        if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                            LocationActivity.this.showPermissionAlert(false);
                            return;
                        }
                    }
                    if (LocationActivity.this.myLocation != null && LocationActivity.this.googleMap != null) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.setDuration(200);
                        float[] fArr = new float[LocationActivity.share];
                        fArr[0] = 0.0f;
                        animatorSet.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", fArr));
                        animatorSet.start();
                        LocationActivity.this.adapter.setCustomLocation(null);
                        LocationActivity.this.userLocationMoved = false;
                        LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude())));
                    }
                }
            });
            this.locationButton.setAlpha(0.0f);
            this.emptyTextLayout = new LinearLayout(context);
            this.emptyTextLayout.setVisibility(8);
            this.emptyTextLayout.setOrientation(share);
            frameLayout.addView(this.emptyTextLayout, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 100.0f, 0.0f, 0.0f));
            this.emptyTextLayout.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            view = new TextView(context);
            view.setTextColor(-8355712);
            view.setTextSize(share, 20.0f);
            view.setGravity(17);
            view.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
            this.emptyTextLayout.addView(view, LayoutHelper.createLinear(-1, -1, 0.5f));
            this.emptyTextLayout.addView(new FrameLayout(context), LayoutHelper.createLinear(-1, -1, 0.5f));
            this.searchListView = new ListView(context);
            this.searchListView.setVisibility(8);
            this.searchListView.setDividerHeight(0);
            this.searchListView.setDivider(null);
            listView = this.searchListView;
            locationActivityAdapter = new LocationActivitySearchAdapter(context);
            this.searchAdapter = locationActivityAdapter;
            listView.setAdapter(locationActivityAdapter);
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new OnScrollListener() {
                public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                }

                public void onScrollStateChanged(AbsListView absListView, int i) {
                    if (i == LocationActivity.share && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    MessageMedia item = LocationActivity.this.searchAdapter.getItem(i);
                    if (!(item == null || LocationActivity.this.delegate == null)) {
                        LocationActivity.this.delegate.didSelectLocation(item);
                    }
                    LocationActivity.this.finishFragment();
                }
            });
            frameLayout.addView(this.actionBar);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & map_list_menu_map) != 0 || (intValue & share) != 0) {
                updateUserData();
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.locationPermissionGranted && this.googleMap != null) {
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        if (this.messageObject != null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        try {
            if (this.mapView != null) {
                this.mapView.onDestroy();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        if (this.adapter != null) {
            this.adapter.destroy();
        }
        if (this.searchAdapter != null) {
            this.searchAdapter.destroy();
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (this.mapView != null && this.mapsInitialized) {
            this.mapView.onLowMemory();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onPause();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        this.onResumeCalled = false;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onResume();
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
        }
        this.onResumeCalled = true;
        if (this.googleMap != null) {
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable th2) {
                FileLog.m18e("tmessages", th2);
            }
        }
        updateUserData();
        fixLayoutInternal(true);
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    String[] strArr = new String[map_list_menu_map];
                    strArr[0] = "android.permission.ACCESS_COARSE_LOCATION";
                    strArr[share] = "android.permission.ACCESS_FINE_LOCATION";
                    parentActivity.requestPermissions(strArr, map_list_menu_map);
                }
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.mapViewClip != null) {
                this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                updateClipView(this.listView.getFirstVisiblePosition());
            } else if (this.fragmentView != null) {
                ((FrameLayout) this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    public void setMessageObject(MessageObject messageObject) {
        this.messageObject = messageObject;
    }
}
