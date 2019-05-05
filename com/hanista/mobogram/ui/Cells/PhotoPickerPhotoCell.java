package com.hanista.mobogram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class PhotoPickerPhotoCell extends FrameLayout {
    private AnimatorSet animator;
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public int itemWidth;
    public BackupImageView photoImage;

    /* renamed from: com.hanista.mobogram.ui.Cells.PhotoPickerPhotoCell.1 */
    class C11111 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$checked;

        C11111(boolean z) {
            this.val$checked = z;
        }

        public void onAnimationCancel(Animator animator) {
            if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator)) {
                PhotoPickerPhotoCell.this.animator = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animator)) {
                PhotoPickerPhotoCell.this.animator = null;
                if (!this.val$checked) {
                    PhotoPickerPhotoCell.this.setBackgroundColor(0);
                }
            }
        }
    }

    public PhotoPickerPhotoCell(Context context) {
        super(context);
        this.photoImage = new BackupImageView(context);
        addView(this.photoImage, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.checkFrame = new FrameLayout(context);
        addView(this.checkFrame, LayoutHelper.createFrame(42, 42, 53));
        this.checkBox = new CheckBox(context, C0338R.drawable.checkbig);
        this.checkBox.setSize(30);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-12793105);
        addView(this.checkBox, LayoutHelper.createFrame(30, BitmapDescriptorFactory.HUE_ORANGE, 53, 0.0f, 4.0f, 4.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.itemWidth, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(this.itemWidth, C0700C.ENCODING_PCM_32BIT));
    }

    public void setChecked(boolean z, boolean z2) {
        int i = -16119286;
        float f = 0.85f;
        this.checkBox.setChecked(z, z2);
        if (this.animator != null) {
            this.animator.cancel();
            this.animator = null;
        }
        if (z2) {
            if (z) {
                setBackgroundColor(-16119286);
            }
            this.animator = new AnimatorSet();
            AnimatorSet animatorSet = this.animator;
            Animator[] animatorArr = new Animator[2];
            BackupImageView backupImageView = this.photoImage;
            String str = "scaleX";
            float[] fArr = new float[1];
            fArr[0] = z ? 0.85f : 1.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, str, fArr);
            BackupImageView backupImageView2 = this.photoImage;
            String str2 = "scaleY";
            float[] fArr2 = new float[1];
            if (!z) {
                f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, str2, fArr2);
            animatorSet.playTogether(animatorArr);
            this.animator.setDuration(200);
            this.animator.addListener(new C11111(z));
            this.animator.start();
            return;
        }
        if (!z) {
            i = 0;
        }
        setBackgroundColor(i);
        this.photoImage.setScaleX(z ? 0.85f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        backupImageView2 = this.photoImage;
        if (!z) {
            f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        backupImageView2.setScaleY(f);
    }
}
