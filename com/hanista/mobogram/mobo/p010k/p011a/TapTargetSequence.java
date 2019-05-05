package com.hanista.mobogram.mobo.p010k.p011a;

import android.app.Activity;
import android.app.Dialog;
import com.hanista.mobogram.mobo.p010k.p011a.TapTargetView.TapTargetView;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/* renamed from: com.hanista.mobogram.mobo.k.a.c */
public class TapTargetSequence {
    TapTargetSequence f1225a;
    boolean f1226b;
    private Activity f1227c;
    private Dialog f1228d;
    private final Queue<TapTarget> f1229e;
    private boolean f1230f;
    private final TapTargetView f1231g;

    /* renamed from: com.hanista.mobogram.mobo.k.a.c.a */
    public interface TapTargetSequence {
        void m1267a();

        void m1268a(TapTarget tapTarget);
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.c.1 */
    class TapTargetSequence extends TapTargetView {
        final /* synthetic */ TapTargetSequence f1224a;

        TapTargetSequence(TapTargetSequence tapTargetSequence) {
            this.f1224a = tapTargetSequence;
        }

        public void m1314a(TapTargetView tapTargetView) {
            super.m1310a(tapTargetView);
            this.f1224a.m1320b();
        }

        public void m1315b(TapTargetView tapTargetView) {
            super.m1312b(tapTargetView);
            if (this.f1224a.f1226b) {
                this.f1224a.m1320b();
            } else if (this.f1224a.f1225a != null) {
                this.f1224a.f1225a.m1268a(tapTargetView.f1280k);
            }
        }
    }

    public TapTargetSequence(Activity activity) {
        this.f1231g = new TapTargetSequence(this);
        if (activity == null) {
            throw new IllegalArgumentException("Activity is null");
        }
        this.f1227c = activity;
        this.f1229e = new LinkedList();
    }

    public TapTargetSequence(Dialog dialog) {
        this.f1231g = new TapTargetSequence(this);
        if (dialog == null) {
            throw new IllegalArgumentException("Activity is null");
        }
        this.f1228d = dialog;
        this.f1229e = new LinkedList();
    }

    public TapTargetSequence m1316a(TapTargetSequence tapTargetSequence) {
        this.f1225a = tapTargetSequence;
        return this;
    }

    public TapTargetSequence m1317a(List<TapTarget> list) {
        this.f1229e.addAll(list);
        return this;
    }

    public TapTargetSequence m1318a(boolean z) {
        this.f1226b = z;
        return this;
    }

    public void m1319a() {
        if (!this.f1229e.isEmpty() && !this.f1230f) {
            this.f1230f = true;
            m1320b();
        }
    }

    void m1320b() {
        try {
            if (this.f1227c != null) {
                TapTargetView.m1329a(this.f1227c, (TapTarget) this.f1229e.remove(), this.f1231g);
            } else {
                TapTargetView.m1330a(this.f1228d, (TapTarget) this.f1229e.remove(), this.f1231g);
            }
        } catch (NoSuchElementException e) {
            if (this.f1225a != null) {
                this.f1225a.m1267a();
            }
        }
    }
}
