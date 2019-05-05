package com.hanista.mobogram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.PhotoViewer;

public class SharedPhotoVideoCell extends FrameLayout {
    private SharedPhotoVideoCellDelegate delegate;
    private int[] indeces;
    private boolean isFirst;
    private int itemsCount;
    private MessageObject[] messageObjects;
    private PhotoVideoView[] photoVideoViews;

    public interface SharedPhotoVideoCellDelegate {
        void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);

        boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);
    }

    /* renamed from: com.hanista.mobogram.ui.Cells.SharedPhotoVideoCell.1 */
    class C11151 implements OnClickListener {
        C11151() {
        }

        public void onClick(View view) {
            if (SharedPhotoVideoCell.this.delegate != null) {
                int intValue = ((Integer) view.getTag()).intValue();
                SharedPhotoVideoCell.this.delegate.didClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[intValue], SharedPhotoVideoCell.this.messageObjects[intValue], intValue);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Cells.SharedPhotoVideoCell.2 */
    class C11162 implements OnLongClickListener {
        C11162() {
        }

        public boolean onLongClick(View view) {
            if (SharedPhotoVideoCell.this.delegate == null) {
                return false;
            }
            int intValue = ((Integer) view.getTag()).intValue();
            return SharedPhotoVideoCell.this.delegate.didLongClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[intValue], SharedPhotoVideoCell.this.messageObjects[intValue], intValue);
        }
    }

    private class PhotoVideoView extends FrameLayout {
        private AnimatorSet animator;
        private CheckBox checkBox;
        private FrameLayout container;
        private BackupImageView imageView;
        private View selector;
        private LinearLayout videoInfoContainer;
        private TextView videoTextView;

        /* renamed from: com.hanista.mobogram.ui.Cells.SharedPhotoVideoCell.PhotoVideoView.1 */
        class C11171 extends AnimatorListenerAdapterProxy {
            final /* synthetic */ boolean val$checked;

            C11171(boolean z) {
                this.val$checked = z;
            }

            public void onAnimationCancel(Animator animator) {
                if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animator)) {
                    PhotoVideoView.this.animator = null;
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animator)) {
                    PhotoVideoView.this.animator = null;
                    if (!this.val$checked) {
                        PhotoVideoView.this.setBackgroundColor(0);
                    }
                }
            }
        }

        public PhotoVideoView(Context context) {
            super(context);
            this.container = new FrameLayout(context);
            addView(this.container, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.imageView = new BackupImageView(context);
            this.imageView.getImageReceiver().setNeedsQualityThumb(true);
            this.imageView.getImageReceiver().setShouldGenerateQualityThumb(true);
            this.container.addView(this.imageView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.videoInfoContainer = new LinearLayout(context);
            this.videoInfoContainer.setOrientation(0);
            this.videoInfoContainer.setBackgroundResource(C0338R.drawable.phototime);
            this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
            this.videoInfoContainer.setGravity(16);
            this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
            View imageView = new ImageView(context);
            imageView.setImageResource(C0338R.drawable.ic_video);
            this.videoInfoContainer.addView(imageView, LayoutHelper.createLinear(-2, -2));
            this.videoTextView = new TextView(context);
            this.videoTextView.setTextColor(-1);
            this.videoTextView.setTextSize(1, 12.0f);
            this.videoTextView.setGravity(16);
            this.videoTextView.setTypeface(FontUtil.m1176a().m1161d());
            this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createLinear(-2, -2, 16, 4, 0, 0, 1));
            this.selector = new View(context);
            this.selector.setBackgroundResource(C0338R.drawable.list_selector);
            addView(this.selector, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.checkBox = new CheckBox(context, C0338R.drawable.round_check2);
            this.checkBox.setVisibility(4);
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        public void clearAnimation() {
            super.clearAnimation();
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        public void setChecked(boolean z, boolean z2) {
            int i = -657931;
            float f = 0.85f;
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
            if (z2) {
                if (z) {
                    setBackgroundColor(-657931);
                }
                this.animator = new AnimatorSet();
                AnimatorSet animatorSet = this.animator;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout = this.container;
                String str = "scaleX";
                float[] fArr = new float[1];
                fArr[0] = z ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
                FrameLayout frameLayout2 = this.container;
                String str2 = "scaleY";
                float[] fArr2 = new float[1];
                if (!z) {
                    f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, str2, fArr2);
                animatorSet.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new C11171(z));
                this.animator.start();
                return;
            }
            if (!z) {
                i = 0;
            }
            setBackgroundColor(i);
            this.container.setScaleX(z ? 0.85f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            frameLayout2 = this.container;
            if (!z) {
                f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
            frameLayout2.setScaleY(f);
        }
    }

    public SharedPhotoVideoCell(Context context) {
        super(context);
        this.messageObjects = new MessageObject[6];
        this.photoVideoViews = new PhotoVideoView[6];
        this.indeces = new int[6];
        for (int i = 0; i < 6; i++) {
            this.photoVideoViews[i] = new PhotoVideoView(context);
            addView(this.photoVideoViews[i]);
            this.photoVideoViews[i].setVisibility(4);
            this.photoVideoViews[i].setTag(Integer.valueOf(i));
            this.photoVideoViews[i].setOnClickListener(new C11151());
            this.photoVideoViews[i].setOnLongClickListener(new C11162());
        }
    }

    public BackupImageView getImageView(int i) {
        return i >= this.itemsCount ? null : this.photoVideoViews[i].imageView;
    }

    public MessageObject getMessageObject(int i) {
        return i >= this.itemsCount ? null : this.messageObjects[i];
    }

    protected void onMeasure(int i, int i2) {
        int i3 = 0;
        int dp = AndroidUtilities.isTablet() ? (AndroidUtilities.dp(490.0f) - ((this.itemsCount + 1) * AndroidUtilities.dp(4.0f))) / this.itemsCount : (AndroidUtilities.displaySize.x - ((this.itemsCount + 1) * AndroidUtilities.dp(4.0f))) / this.itemsCount;
        for (int i4 = 0; i4 < this.itemsCount; i4++) {
            LayoutParams layoutParams = (LayoutParams) this.photoVideoViews[i4].getLayoutParams();
            layoutParams.topMargin = this.isFirst ? 0 : AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = ((AndroidUtilities.dp(4.0f) + dp) * i4) + AndroidUtilities.dp(4.0f);
            layoutParams.width = dp;
            layoutParams.height = dp;
            layoutParams.gravity = 51;
            this.photoVideoViews[i4].setLayoutParams(layoutParams);
        }
        if (!this.isFirst) {
            i3 = AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(i3 + dp, C0700C.ENCODING_PCM_32BIT));
    }

    public void setChecked(int i, boolean z, boolean z2) {
        this.photoVideoViews[i].setChecked(z, z2);
    }

    public void setDelegate(SharedPhotoVideoCellDelegate sharedPhotoVideoCellDelegate) {
        this.delegate = sharedPhotoVideoCellDelegate;
    }

    public void setIsFirst(boolean z) {
        this.isFirst = z;
    }

    public void setItem(int i, int i2, MessageObject messageObject) {
        this.messageObjects[i] = messageObject;
        this.indeces[i] = i2;
        if (messageObject != null) {
            this.photoVideoViews[i].setVisibility(0);
            PhotoVideoView photoVideoView = this.photoVideoViews[i];
            photoVideoView.imageView.getImageReceiver().setParentMessageObject(messageObject);
            photoVideoView.imageView.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(messageObject), false);
            if (messageObject.isVideo()) {
                int i3;
                int i4;
                photoVideoView.videoInfoContainer.setVisibility(0);
                for (i3 = 0; i3 < messageObject.getDocument().attributes.size(); i3++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) messageObject.getDocument().attributes.get(i3);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i4 = documentAttribute.duration;
                        break;
                    }
                }
                i4 = 0;
                i4 -= (i4 / 60) * 60;
                photoVideoView.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)}));
                if (messageObject.getDocument().thumb != null) {
                    photoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_placeholder_in), null, messageObject.getDocument().thumb.location, "b", null, 0);
                    return;
                }
                photoVideoView.imageView.setImageResource(C0338R.drawable.photo_placeholder_in);
                return;
            } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.messageOwner.media.photo == null || messageObject.photoThumbs.isEmpty()) {
                photoVideoView.videoInfoContainer.setVisibility(4);
                photoVideoView.imageView.setImageResource(C0338R.drawable.photo_placeholder_in);
                return;
            } else {
                photoVideoView.videoInfoContainer.setVisibility(4);
                photoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_placeholder_in), null, FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80).location, "b", null, 0);
                return;
            }
        }
        this.photoVideoViews[i].clearAnimation();
        this.photoVideoViews[i].setVisibility(4);
        this.messageObjects[i] = null;
    }

    public void setItemsCount(int i) {
        int i2 = 0;
        while (i2 < this.photoVideoViews.length) {
            this.photoVideoViews[i2].clearAnimation();
            this.photoVideoViews[i2].setVisibility(i2 < i ? 0 : 4);
            i2++;
        }
        this.itemsCount = i;
    }
}
