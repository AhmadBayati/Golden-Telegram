package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.PhotoViewer;

public class PhotoAttachPhotoCell extends FrameLayout {
    private static Rect rect;
    private CheckBox checkBox;
    private FrameLayout checkFrame;
    private PhotoAttachPhotoCellDelegate delegate;
    private BackupImageView imageView;
    private boolean isLast;
    private PhotoEntry photoEntry;
    private boolean pressed;

    public interface PhotoAttachPhotoCellDelegate {
        void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell);
    }

    static {
        rect = new Rect();
    }

    public PhotoAttachPhotoCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        addView(this.imageView, LayoutHelper.createFrame(80, 80.0f));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42.0f, 51, 38.0f, 0.0f, 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0338R.drawable.checkbig);
        this.checkBox.setSize(30);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-12793105);
        addView(this.checkBox, LayoutHelper.createFrame(30, BitmapDescriptorFactory.HUE_ORANGE, 51, 46.0f, 4.0f, 0.0f, 0.0f));
        this.checkBox.setVisibility(0);
        initThemeCheckBox();
    }

    private void initThemeCheckBox() {
        if (ThemeUtil.m2490b()) {
            this.checkBox.setColor(AdvanceTheme.f2491b);
        }
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public PhotoEntry getPhotoEntry() {
        return this.photoEntry;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.isLast ? 0 : 6) + 80)), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = true;
        this.checkFrame.getHitRect(rect);
        if (motionEvent.getAction() == 0) {
            if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                this.pressed = true;
                invalidate();
            }
            z = false;
        } else {
            if (this.pressed) {
                if (motionEvent.getAction() == 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    this.pressed = false;
                    playSoundEffect(0);
                    this.delegate.onCheckClick(this);
                    invalidate();
                    z = false;
                } else if (motionEvent.getAction() == 3) {
                    this.pressed = false;
                    invalidate();
                    z = false;
                } else if (motionEvent.getAction() == 2 && !rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    this.pressed = false;
                    invalidate();
                }
            }
            z = false;
        }
        return !z ? super.onTouchEvent(motionEvent) : z;
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setDelegate(PhotoAttachPhotoCellDelegate photoAttachPhotoCellDelegate) {
        this.delegate = photoAttachPhotoCellDelegate;
    }

    public void setOnCheckClickLisnener(OnClickListener onClickListener) {
        this.checkFrame.setOnClickListener(onClickListener);
    }

    public void setPhotoEntry(PhotoEntry photoEntry, boolean z) {
        int i = 0;
        this.pressed = false;
        this.photoEntry = photoEntry;
        this.isLast = z;
        if (this.photoEntry.thumbPath != null) {
            this.imageView.setImage(this.photoEntry.thumbPath, null, getResources().getDrawable(C0338R.drawable.nophotos));
        } else if (this.photoEntry.path != null) {
            this.imageView.setOrientation(this.photoEntry.orientation, true);
            this.imageView.setImage("thumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, null, getResources().getDrawable(C0338R.drawable.nophotos));
        } else {
            this.imageView.setImageResource(C0338R.drawable.nophotos);
        }
        boolean isShowingImage = PhotoViewer.getInstance().isShowingImage(this.photoEntry.path);
        this.imageView.getImageReceiver().setVisible(!isShowingImage, true);
        CheckBox checkBox = this.checkBox;
        if (isShowingImage) {
            i = 4;
        }
        checkBox.setVisibility(i);
        requestLayout();
    }
}
