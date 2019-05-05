package com.hanista.mobogram.mobo.p010k.p011a;

import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/* renamed from: com.hanista.mobogram.mobo.k.a.g */
class ViewUtil {

    /* renamed from: com.hanista.mobogram.mobo.k.a.g.1 */
    static class ViewUtil implements OnGlobalLayoutListener {
        final /* synthetic */ ViewTreeObserver f1299a;
        final /* synthetic */ View f1300b;
        final /* synthetic */ Runnable f1301c;

        ViewUtil(ViewTreeObserver viewTreeObserver, View view, Runnable runnable) {
            this.f1299a = viewTreeObserver;
            this.f1300b = view;
            this.f1301c = runnable;
        }

        public void onGlobalLayout() {
            ViewTreeObserver viewTreeObserver = this.f1299a.isAlive() ? this.f1299a : this.f1300b.getViewTreeObserver();
            if (VERSION.SDK_INT >= 16) {
                viewTreeObserver.removeOnGlobalLayoutListener(this);
            } else {
                viewTreeObserver.removeGlobalOnLayoutListener(this);
            }
            this.f1301c.run();
        }
    }

    static void m1353a(View view, Runnable runnable) {
        if (ViewUtil.m1354a(view)) {
            runnable.run();
            return;
        }
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewUtil(viewTreeObserver, view, runnable));
    }

    static boolean m1354a(View view) {
        return VERSION.SDK_INT >= 19 ? view.isLaidOut() : view.getWidth() > 0 && view.getHeight() > 0;
    }
}
