package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class StickerCell extends FrameLayout {
    private static AccelerateInterpolator interpolator;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private float scale;
    private boolean scaled;
    private Document sticker;
    private long time;

    static {
        interpolator = new AccelerateInterpolator(0.5f);
    }

    public StickerCell(Context context) {
        super(context);
        this.time = 0;
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        addView(this.imageView, LayoutHelper.createFrame(66, 66.0f, 1, 0.0f, 5.0f, 0.0f, 0.0f));
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && ((this.scaled && this.scale != DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) || !(this.scaled || this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (!this.scaled || this.scale == DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                this.scale += ((float) j2) / 400.0f;
                if (this.scale > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
            } else {
                this.scale -= ((float) j2) / 400.0f;
                if (this.scale < DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                    this.scale = DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return drawChild;
    }

    public Document getSticker() {
        return this.sticker;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((AndroidUtilities.dp(76.0f) + getPaddingLeft()) + getPaddingRight(), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setPressed(boolean z) {
        if (this.imageView.getImageReceiver().getPressed() != z) {
            this.imageView.getImageReceiver().setPressed(z);
            this.imageView.invalidate();
        }
        super.setPressed(z);
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setSticker(Document document, int i) {
        if (!(document == null || document.thumb == null)) {
            this.imageView.setImage(document.thumb.location, null, "webp", null);
        }
        this.sticker = document;
        if (i == -1) {
            setBackgroundResource(C0338R.drawable.stickers_back_left);
            setPadding(AndroidUtilities.dp(7.0f), 0, 0, 0);
        } else if (i == 0) {
            setBackgroundResource(C0338R.drawable.stickers_back_center);
            setPadding(0, 0, 0, 0);
        } else if (i == 1) {
            setBackgroundResource(C0338R.drawable.stickers_back_right);
            setPadding(0, 0, AndroidUtilities.dp(7.0f), 0);
        } else if (i == 2) {
            setBackgroundResource(C0338R.drawable.stickers_back_all);
            setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        }
        if (getBackground() != null) {
            getBackground().setAlpha(230);
        }
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }
}
