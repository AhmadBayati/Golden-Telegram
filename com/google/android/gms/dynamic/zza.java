package com.google.android.gms.dynamic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzi;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class zza<T extends LifecycleDelegate> {
    private T Oi;
    private Bundle Oj;
    private LinkedList<zza> Ok;
    private final zzf<T> Ol;

    /* renamed from: com.google.android.gms.dynamic.zza.1 */
    class C01061 implements zzf<T> {
        final /* synthetic */ zza Om;

        C01061(zza com_google_android_gms_dynamic_zza) {
            this.Om = com_google_android_gms_dynamic_zza;
        }

        public void zza(T t) {
            this.Om.Oi = t;
            Iterator it = this.Om.Ok.iterator();
            while (it.hasNext()) {
                ((zza) it.next()).zzb(this.Om.Oi);
            }
            this.Om.Ok.clear();
            this.Om.Oj = null;
        }
    }

    private interface zza {
        int getState();

        void zzb(LifecycleDelegate lifecycleDelegate);
    }

    /* renamed from: com.google.android.gms.dynamic.zza.2 */
    class C01072 implements zza {
        final /* synthetic */ zza Om;
        final /* synthetic */ Bundle On;
        final /* synthetic */ Bundle Oo;
        final /* synthetic */ Activity val$activity;

        C01072(zza com_google_android_gms_dynamic_zza, Activity activity, Bundle bundle, Bundle bundle2) {
            this.Om = com_google_android_gms_dynamic_zza;
            this.val$activity = activity;
            this.On = bundle;
            this.Oo = bundle2;
        }

        public int getState() {
            return 0;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.Om.Oi.onInflate(this.val$activity, this.On, this.Oo);
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.3 */
    class C01083 implements zza {
        final /* synthetic */ zza Om;
        final /* synthetic */ Bundle Oo;

        C01083(zza com_google_android_gms_dynamic_zza, Bundle bundle) {
            this.Om = com_google_android_gms_dynamic_zza;
            this.Oo = bundle;
        }

        public int getState() {
            return 1;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.Om.Oi.onCreate(this.Oo);
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.4 */
    class C01094 implements zza {
        final /* synthetic */ zza Om;
        final /* synthetic */ Bundle Oo;
        final /* synthetic */ FrameLayout Op;
        final /* synthetic */ LayoutInflater Oq;
        final /* synthetic */ ViewGroup Or;

        C01094(zza com_google_android_gms_dynamic_zza, FrameLayout frameLayout, LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            this.Om = com_google_android_gms_dynamic_zza;
            this.Op = frameLayout;
            this.Oq = layoutInflater;
            this.Or = viewGroup;
            this.Oo = bundle;
        }

        public int getState() {
            return 2;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.Op.removeAllViews();
            this.Op.addView(this.Om.Oi.onCreateView(this.Oq, this.Or, this.Oo));
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.5 */
    class C01105 implements OnClickListener {
        final /* synthetic */ Context zzamt;
        final /* synthetic */ int zzbom;

        C01105(Context context, int i) {
            this.zzamt = context;
            this.zzbom = i;
        }

        public void onClick(View view) {
            this.zzamt.startActivity(GooglePlayServicesUtil.zzfm(this.zzbom));
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.6 */
    class C01116 implements zza {
        final /* synthetic */ zza Om;

        C01116(zza com_google_android_gms_dynamic_zza) {
            this.Om = com_google_android_gms_dynamic_zza;
        }

        public int getState() {
            return 4;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.Om.Oi.onStart();
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.7 */
    class C01127 implements zza {
        final /* synthetic */ zza Om;

        C01127(zza com_google_android_gms_dynamic_zza) {
            this.Om = com_google_android_gms_dynamic_zza;
        }

        public int getState() {
            return 5;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.Om.Oi.onResume();
        }
    }

    public zza() {
        this.Ol = new C01061(this);
    }

    private void zza(Bundle bundle, zza com_google_android_gms_dynamic_zza_zza) {
        if (this.Oi != null) {
            com_google_android_gms_dynamic_zza_zza.zzb(this.Oi);
            return;
        }
        if (this.Ok == null) {
            this.Ok = new LinkedList();
        }
        this.Ok.add(com_google_android_gms_dynamic_zza_zza);
        if (bundle != null) {
            if (this.Oj == null) {
                this.Oj = (Bundle) bundle.clone();
            } else {
                this.Oj.putAll(bundle);
            }
        }
        zza(this.Ol);
    }

    public static void zzb(FrameLayout frameLayout) {
        Context context = frameLayout.getContext();
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        CharSequence zzi = zzi.zzi(context, isGooglePlayServicesAvailable);
        CharSequence zzk = zzi.zzk(context, isGooglePlayServicesAvailable);
        View linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        View textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        textView.setText(zzi);
        linearLayout.addView(textView);
        if (zzk != null) {
            View button = new Button(context);
            button.setLayoutParams(new LayoutParams(-2, -2));
            button.setText(zzk);
            linearLayout.addView(button);
            button.setOnClickListener(new C01105(context, isGooglePlayServicesAvailable));
        }
    }

    private void zzno(int i) {
        while (!this.Ok.isEmpty() && ((zza) this.Ok.getLast()).getState() >= i) {
            this.Ok.removeLast();
        }
    }

    public void onCreate(Bundle bundle) {
        zza(bundle, new C01083(this, bundle));
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = new FrameLayout(layoutInflater.getContext());
        zza(bundle, new C01094(this, frameLayout, layoutInflater, viewGroup, bundle));
        if (this.Oi == null) {
            zza(frameLayout);
        }
        return frameLayout;
    }

    public void onDestroy() {
        if (this.Oi != null) {
            this.Oi.onDestroy();
        } else {
            zzno(1);
        }
    }

    public void onDestroyView() {
        if (this.Oi != null) {
            this.Oi.onDestroyView();
        } else {
            zzno(2);
        }
    }

    public void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
        zza(bundle2, new C01072(this, activity, bundle, bundle2));
    }

    public void onLowMemory() {
        if (this.Oi != null) {
            this.Oi.onLowMemory();
        }
    }

    public void onPause() {
        if (this.Oi != null) {
            this.Oi.onPause();
        } else {
            zzno(5);
        }
    }

    public void onResume() {
        zza(null, new C01127(this));
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (this.Oi != null) {
            this.Oi.onSaveInstanceState(bundle);
        } else if (this.Oj != null) {
            bundle.putAll(this.Oj);
        }
    }

    public void onStart() {
        zza(null, new C01116(this));
    }

    public void onStop() {
        if (this.Oi != null) {
            this.Oi.onStop();
        } else {
            zzno(4);
        }
    }

    protected void zza(FrameLayout frameLayout) {
        zzb(frameLayout);
    }

    protected abstract void zza(zzf<T> com_google_android_gms_dynamic_zzf_T);

    public T zzbdt() {
        return this.Oi;
    }
}
