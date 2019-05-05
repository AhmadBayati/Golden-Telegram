package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.hanista.mobogram.messenger.AndroidUtilities;

public class SizeNotifierFrameLayoutPhoto extends FrameLayout {
    private SizeNotifierFrameLayoutPhotoDelegate delegate;
    private int keyboardHeight;
    private Rect rect;
    private WindowManager windowManager;
    private boolean withoutWindow;

    public interface SizeNotifierFrameLayoutPhotoDelegate {
        void onSizeChanged(int i, boolean z);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.SizeNotifierFrameLayoutPhoto.1 */
    class C14701 implements Runnable {
        final /* synthetic */ boolean val$isWidthGreater;

        C14701(boolean z) {
            this.val$isWidthGreater = z;
        }

        public void run() {
            if (SizeNotifierFrameLayoutPhoto.this.delegate != null) {
                SizeNotifierFrameLayoutPhoto.this.delegate.onSizeChanged(SizeNotifierFrameLayoutPhoto.this.keyboardHeight, this.val$isWidthGreater);
            }
        }
    }

    public SizeNotifierFrameLayoutPhoto(Context context) {
        super(context);
        this.rect = new Rect();
    }

    public int getKeyboardHeight() {
        int i = 0;
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        if (this.withoutWindow) {
            int height = rootView.getHeight();
            if (this.rect.top != 0) {
                i = AndroidUtilities.statusBarHeight;
            }
            return ((height - i) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top);
        }
        int height2 = (AndroidUtilities.displaySize.y - this.rect.top) - (rootView.getHeight() - AndroidUtilities.getViewInset(rootView));
        return height2 > AndroidUtilities.dp(10.0f) ? height2 : 0;
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            post(new C14701(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y));
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        notifyHeightChanged();
    }

    public void setDelegate(SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate) {
        this.delegate = sizeNotifierFrameLayoutPhotoDelegate;
    }

    public void setWithoutWindow(boolean z) {
        this.withoutWindow = z;
    }
}
