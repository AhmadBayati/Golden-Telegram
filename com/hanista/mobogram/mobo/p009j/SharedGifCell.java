package com.hanista.mobogram.mobo.p009j;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.PhotoViewer;

/* renamed from: com.hanista.mobogram.mobo.j.a */
public class SharedGifCell extends FrameLayout {
    private SharedGifCell[] f1172a;
    private MessageObject[] f1173b;
    private int[] f1174c;
    private SharedGifCell f1175d;
    private int f1176e;
    private boolean f1177f;

    /* renamed from: com.hanista.mobogram.mobo.j.a.1 */
    class SharedGifCell implements OnClickListener {
        final /* synthetic */ SharedGifCell f1160a;

        SharedGifCell(SharedGifCell sharedGifCell) {
            this.f1160a = sharedGifCell;
        }

        public void onClick(View view) {
            if (this.f1160a.f1175d != null) {
                int intValue = ((Integer) view.getTag()).intValue();
                this.f1160a.f1175d.didClickItem(this.f1160a, this.f1160a.f1174c[intValue], this.f1160a.f1173b[intValue], intValue);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.a.2 */
    class SharedGifCell implements OnLongClickListener {
        final /* synthetic */ SharedGifCell f1161a;

        SharedGifCell(SharedGifCell sharedGifCell) {
            this.f1161a = sharedGifCell;
        }

        public boolean onLongClick(View view) {
            if (this.f1161a.f1175d == null) {
                return false;
            }
            int intValue = ((Integer) view.getTag()).intValue();
            return this.f1161a.f1175d.didLongClickItem(this.f1161a, this.f1161a.f1174c[intValue], this.f1161a.f1173b[intValue], intValue);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.a.a */
    private class SharedGifCell extends FrameLayout {
        final /* synthetic */ SharedGifCell f1164a;
        private BackupImageView f1165b;
        private TextView f1166c;
        private LinearLayout f1167d;
        private View f1168e;
        private CheckBox f1169f;
        private FrameLayout f1170g;
        private AnimatorSet f1171h;

        /* renamed from: com.hanista.mobogram.mobo.j.a.a.1 */
        class SharedGifCell extends AnimatorListenerAdapterProxy {
            final /* synthetic */ boolean f1162a;
            final /* synthetic */ SharedGifCell f1163b;

            SharedGifCell(SharedGifCell sharedGifCell, boolean z) {
                this.f1163b = sharedGifCell;
                this.f1162a = z;
            }

            public void onAnimationCancel(Animator animator) {
                if (this.f1163b.f1171h != null && this.f1163b.f1171h.equals(animator)) {
                    this.f1163b.f1171h = null;
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (this.f1163b.f1171h != null && this.f1163b.f1171h.equals(animator)) {
                    this.f1163b.f1171h = null;
                    if (!this.f1162a) {
                        this.f1163b.setBackgroundColor(0);
                    }
                }
            }
        }

        public SharedGifCell(SharedGifCell sharedGifCell, Context context) {
            this.f1164a = sharedGifCell;
            super(context);
            this.f1170g = new FrameLayout(context);
            addView(this.f1170g, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.f1165b = new BackupImageView(context);
            this.f1165b.getImageReceiver().setNeedsQualityThumb(true);
            this.f1165b.getImageReceiver().setShouldGenerateQualityThumb(true);
            this.f1170g.addView(this.f1165b, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.f1167d = new LinearLayout(context);
            this.f1167d.setOrientation(0);
            this.f1167d.setBackgroundResource(C0338R.drawable.phototime);
            this.f1167d.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
            this.f1167d.setGravity(16);
            this.f1170g.addView(this.f1167d, LayoutHelper.createFrame(-1, 16, 83));
            this.f1166c = new TextView(context);
            this.f1166c.setTextColor(-1);
            this.f1166c.setTextSize(1, 12.0f);
            this.f1166c.setGravity(16);
            this.f1166c.setTypeface(FontUtil.m1176a().m1161d());
            this.f1167d.addView(this.f1166c, LayoutHelper.createLinear(-2, -2, 16, 4, 0, 0, 1));
            this.f1168e = new View(context);
            this.f1168e.setBackgroundResource(C0338R.drawable.list_selector);
            addView(this.f1168e, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.f1169f = new CheckBox(context, C0338R.drawable.round_check2);
            this.f1169f.setVisibility(4);
            addView(this.f1169f, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        public void m1231a(boolean z, boolean z2) {
            int i = -657931;
            float f = 0.85f;
            if (this.f1169f.getVisibility() != 0) {
                this.f1169f.setVisibility(0);
            }
            this.f1169f.setChecked(z, z2);
            if (this.f1171h != null) {
                this.f1171h.cancel();
                this.f1171h = null;
            }
            if (z2) {
                if (z) {
                    setBackgroundColor(-657931);
                }
                this.f1171h = new AnimatorSet();
                AnimatorSet animatorSet = this.f1171h;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout = this.f1170g;
                String str = "scaleX";
                float[] fArr = new float[1];
                fArr[0] = z ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
                FrameLayout frameLayout2 = this.f1170g;
                String str2 = "scaleY";
                float[] fArr2 = new float[1];
                if (!z) {
                    f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, str2, fArr2);
                animatorSet.playTogether(animatorArr);
                this.f1171h.setDuration(200);
                this.f1171h.addListener(new SharedGifCell(this, z));
                this.f1171h.start();
                return;
            }
            if (!z) {
                i = 0;
            }
            setBackgroundColor(i);
            this.f1170g.setScaleX(z ? 0.85f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            frameLayout2 = this.f1170g;
            if (!z) {
                f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
            frameLayout2.setScaleY(f);
        }

        public void clearAnimation() {
            super.clearAnimation();
            if (this.f1171h != null) {
                this.f1171h.cancel();
                this.f1171h = null;
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (VERSION.SDK_INT >= 21) {
                this.f1168e.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.a.b */
    public interface SharedGifCell {
        void didClickItem(SharedGifCell sharedGifCell, int i, MessageObject messageObject, int i2);

        boolean didLongClickItem(SharedGifCell sharedGifCell, int i, MessageObject messageObject, int i2);
    }

    public SharedGifCell(Context context) {
        super(context);
        this.f1173b = new MessageObject[6];
        this.f1172a = new SharedGifCell[6];
        this.f1174c = new int[6];
        for (int i = 0; i < 6; i++) {
            this.f1172a[i] = new SharedGifCell(this, context);
            addView(this.f1172a[i]);
            this.f1172a[i].setVisibility(4);
            this.f1172a[i].setTag(Integer.valueOf(i));
            this.f1172a[i].setOnClickListener(new SharedGifCell(this));
            this.f1172a[i].setOnLongClickListener(new SharedGifCell(this));
        }
    }

    public BackupImageView m1235a(int i) {
        return i >= this.f1176e ? null : this.f1172a[i].f1165b;
    }

    public void m1236a(int i, int i2, MessageObject messageObject) {
        this.f1173b[i] = messageObject;
        this.f1174c[i] = i2;
        if (messageObject != null) {
            this.f1172a[i].setVisibility(0);
            SharedGifCell sharedGifCell = this.f1172a[i];
            sharedGifCell.f1165b.getImageReceiver().setParentMessageObject(messageObject);
            sharedGifCell.f1165b.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(messageObject), false);
            int i3;
            DocumentAttribute documentAttribute;
            int i4;
            if (messageObject.isVideo()) {
                sharedGifCell.f1167d.setVisibility(0);
                for (i3 = 0; i3 < messageObject.getDocument().attributes.size(); i3++) {
                    documentAttribute = (DocumentAttribute) messageObject.getDocument().attributes.get(i3);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i4 = documentAttribute.duration;
                        break;
                    }
                }
                i4 = 0;
                i4 -= (i4 / 60) * 60;
                sharedGifCell.f1166c.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)}));
                if (messageObject.getDocument().thumb != null) {
                    sharedGifCell.f1165b.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_placeholder_in), null, messageObject.getDocument().thumb.location, "b", null, 0);
                    return;
                }
                sharedGifCell.f1165b.setImageResource(C0338R.drawable.photo_placeholder_in);
                return;
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) && messageObject.messageOwner.media.photo != null && !messageObject.photoThumbs.isEmpty()) {
                sharedGifCell.f1167d.setVisibility(4);
                sharedGifCell.f1165b.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_placeholder_in), null, FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80).location, "b", null, 0);
                return;
            } else if ((messageObject.isGif() || messageObject.isNewGif()) && !messageObject.photoThumbs.isEmpty()) {
                sharedGifCell.f1167d.setVisibility(0);
                for (i3 = 0; i3 < messageObject.getDocument().attributes.size(); i3++) {
                    documentAttribute = (DocumentAttribute) messageObject.getDocument().attributes.get(i3);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i4 = documentAttribute.duration;
                        break;
                    }
                }
                i4 = 0;
                i4 -= (i4 / 60) * 60;
                sharedGifCell.f1166c.setText("GIF, " + String.format("%d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)}) + ", " + AndroidUtilities.formatFileSize((long) messageObject.messageOwner.media.document.size));
                TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                messageObject.checkMediaExistance();
                boolean z = messageObject.mediaExists;
                String attachFileName = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                if (z || MediaController.m71a().m157a(32) || FileLoader.getInstance().isLoadingFile(attachFileName)) {
                    sharedGifCell.f1165b.setImage(messageObject.messageOwner.media.document, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_placeholder_in), null, closestPhotoSizeWithSize.location, "b", null, messageObject.messageOwner.media.document.size);
                    return;
                } else {
                    sharedGifCell.f1165b.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_placeholder_in), null, closestPhotoSizeWithSize.location, "b", null, 0);
                    return;
                }
            } else {
                sharedGifCell.f1167d.setVisibility(4);
                sharedGifCell.f1165b.setImageResource(C0338R.drawable.photo_placeholder_in);
                return;
            }
        }
        this.f1172a[i].clearAnimation();
        this.f1172a[i].setVisibility(4);
        this.f1173b[i] = null;
    }

    public void m1237a(int i, boolean z, boolean z2) {
        this.f1172a[i].m1231a(z, z2);
    }

    public MessageObject m1238b(int i) {
        return i >= this.f1176e ? null : this.f1173b[i];
    }

    public void m1239b(int i, int i2, MessageObject messageObject) {
        if (messageObject != null) {
            SharedGifCell sharedGifCell = this.f1172a[i];
            if ((messageObject.isGif() || messageObject.isNewGif()) && !messageObject.photoThumbs.isEmpty()) {
                sharedGifCell.f1167d.setVisibility(4);
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                String str = null;
                Bitmap bitmap = null;
                sharedGifCell.f1165b.setImage(messageObject.messageOwner.media.document, null, str, ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_placeholder_in), bitmap, closestPhotoSizeWithSize.location, "b", null, messageObject.messageOwner.media.document.size);
            }
        }
    }

    protected void onMeasure(int i, int i2) {
        int i3 = 0;
        int dp = AndroidUtilities.isTablet() ? (AndroidUtilities.dp(490.0f) - ((this.f1176e + 1) * AndroidUtilities.dp(4.0f))) / this.f1176e : (AndroidUtilities.displaySize.x - ((this.f1176e + 1) * AndroidUtilities.dp(4.0f))) / this.f1176e;
        for (int i4 = 0; i4 < this.f1176e; i4++) {
            LayoutParams layoutParams = (LayoutParams) this.f1172a[i4].getLayoutParams();
            layoutParams.topMargin = this.f1177f ? 0 : AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = ((AndroidUtilities.dp(4.0f) + dp) * i4) + AndroidUtilities.dp(4.0f);
            layoutParams.width = dp;
            layoutParams.height = dp;
            layoutParams.gravity = 51;
            this.f1172a[i4].setLayoutParams(layoutParams);
        }
        if (!this.f1177f) {
            i3 = AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(i3 + dp, C0700C.ENCODING_PCM_32BIT));
    }

    public void setDelegate(SharedGifCell sharedGifCell) {
        this.f1175d = sharedGifCell;
    }

    public void setIsFirst(boolean z) {
        this.f1177f = z;
    }

    public void setItemsCount(int i) {
        int i2 = 0;
        while (i2 < this.f1172a.length) {
            this.f1172a[i2].clearAnimation();
            this.f1172a[i2].setVisibility(i2 < i ? 0 : 4);
            i2++;
        }
        this.f1176e = i;
    }
}
