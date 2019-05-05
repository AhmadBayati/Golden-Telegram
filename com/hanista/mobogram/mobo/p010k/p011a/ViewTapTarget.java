package com.hanista.mobogram.mobo.p010k.p011a;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.view.View;

/* renamed from: com.hanista.mobogram.mobo.k.a.f */
public class ViewTapTarget extends TapTarget {
    final View f1298r;

    /* renamed from: com.hanista.mobogram.mobo.k.a.f.1 */
    class ViewTapTarget implements Runnable {
        final /* synthetic */ Runnable f1296a;
        final /* synthetic */ ViewTapTarget f1297b;

        ViewTapTarget(ViewTapTarget viewTapTarget, Runnable runnable) {
            this.f1297b = viewTapTarget;
            this.f1296a = runnable;
        }

        public void run() {
            int[] iArr = new int[2];
            this.f1297b.f1298r.getLocationOnScreen(iArr);
            this.f1297b.c = new Rect(iArr[0], iArr[1], iArr[0] + this.f1297b.f1298r.getWidth(), iArr[1] + this.f1297b.f1298r.getHeight());
            Bitmap createBitmap = Bitmap.createBitmap(this.f1297b.f1298r.getWidth(), this.f1297b.f1298r.getHeight(), Config.ARGB_8888);
            this.f1297b.f1298r.draw(new Canvas(createBitmap));
            this.f1297b.d = new BitmapDrawable(this.f1297b.f1298r.getContext().getResources(), createBitmap);
            this.f1297b.d.setBounds(0, 0, this.f1297b.d.getIntrinsicWidth(), this.f1297b.d.getIntrinsicHeight());
            this.f1296a.run();
        }
    }

    protected ViewTapTarget(View view, CharSequence charSequence, @Nullable CharSequence charSequence2) {
        super(charSequence, charSequence2);
        this.f1298r = view;
    }

    public void m1352a(Runnable runnable) {
        ViewUtil.m1353a(this.f1298r, new ViewTapTarget(this, runnable));
    }
}
