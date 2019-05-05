package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import com.hanista.mobogram.messenger.AndroidUtilities;

public class SizeNotifierFrameLayout extends FrameLayout {
    private Drawable backgroundDrawable;
    private int bottomClip;
    private SizeNotifierFrameLayoutDelegate delegate;
    private int keyboardHeight;
    private Rect rect;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.SizeNotifierFrameLayout.1 */
    class C14691 implements Runnable {
        final /* synthetic */ boolean val$isWidthGreater;

        C14691(boolean z) {
            this.val$isWidthGreater = z;
        }

        public void run() {
            if (SizeNotifierFrameLayout.this.delegate != null) {
                SizeNotifierFrameLayout.this.delegate.onSizeChanged(SizeNotifierFrameLayout.this.keyboardHeight, this.val$isWidthGreater);
            }
        }
    }

    public SizeNotifierFrameLayout(Context context) {
        super(context);
        this.rect = new Rect();
        setWillNotDraw(false);
    }

    public Drawable getBackgroundImage() {
        return this.backgroundDrawable;
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        return ((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top);
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            post(new C14691(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y));
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.backgroundDrawable == null) {
            super.onDraw(canvas);
        } else if (this.backgroundDrawable instanceof ColorDrawable) {
            if (this.bottomClip != 0) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - this.bottomClip);
            }
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.backgroundDrawable.draw(canvas);
            if (this.bottomClip != 0) {
                canvas.restore();
            }
        } else if (!(this.backgroundDrawable instanceof BitmapDrawable)) {
        } else {
            float f;
            if (((BitmapDrawable) this.backgroundDrawable).getTileModeX() == TileMode.REPEAT) {
                canvas.save();
                f = 2.0f / AndroidUtilities.density;
                canvas.scale(f, f);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
                return;
            }
            float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
            f = ((float) (getMeasuredHeight() + this.keyboardHeight)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
            if (measuredWidth >= f) {
                f = measuredWidth;
            }
            int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * f));
            int ceil2 = (int) Math.ceil((double) (f * ((float) this.backgroundDrawable.getIntrinsicHeight())));
            int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
            int measuredHeight = ((getMeasuredHeight() - ceil2) + this.keyboardHeight) / 2;
            if (this.bottomClip != 0) {
                canvas.save();
                canvas.clipRect(0, 0, ceil, getMeasuredHeight() - this.bottomClip);
            }
            this.backgroundDrawable.setBounds(measuredWidth2, measuredHeight, ceil + measuredWidth2, ceil2 + measuredHeight);
            this.backgroundDrawable.draw(canvas);
            if (this.bottomClip != 0) {
                canvas.restore();
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        notifyHeightChanged();
    }

    public void setBackgroundImage(Drawable drawable) {
        this.backgroundDrawable = drawable;
    }

    public void setBottomClip(int i) {
        this.bottomClip = i;
    }

    public void setDelegate(SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate) {
        this.delegate = sizeNotifierFrameLayoutDelegate;
    }
}
