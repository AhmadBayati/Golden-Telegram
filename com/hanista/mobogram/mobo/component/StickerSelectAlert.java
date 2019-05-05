package com.hanista.mobogram.mobo.component;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.StickersAlert.StickersAlertDelegate;
import com.hanista.mobogram.ui.StickerPreviewViewer;

/* renamed from: com.hanista.mobogram.mobo.component.h */
public class StickerSelectAlert extends BottomSheet implements NotificationCenterDelegate {
    private TextView f465a;
    private FrameLayout f466b;
    private TextView f467c;
    private View f468d;
    private BackupImageView f469e;
    private TextView f470f;
    private Drawable f471g;
    private View[] f472h;
    private Document f473i;
    private StickersAlertDelegate f474j;
    private int f475k;
    private int f476l;

    /* renamed from: com.hanista.mobogram.mobo.component.h.1 */
    class StickerSelectAlert extends FrameLayout {
        final /* synthetic */ StickerSelectAlert f462a;

        StickerSelectAlert(StickerSelectAlert stickerSelectAlert, Context context) {
            this.f462a = stickerSelectAlert;
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            if (VERSION.SDK_INT >= 11) {
                this.f462a.f471g.setBounds(0, this.f462a.f475k - StickerSelectAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                this.f462a.f471g.draw(canvas);
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || this.f462a.f475k == 0 || motionEvent.getY() >= ((float) this.f462a.f475k)) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            this.f462a.dismiss();
            return true;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (VERSION.SDK_INT >= 11) {
                this.f462a.m518c();
            }
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i2);
            if (VERSION.SDK_INT >= 21) {
                size -= AndroidUtilities.statusBarHeight;
            }
            int dp = (AndroidUtilities.dp(96.0f) + (Math.max(3, 0) * AndroidUtilities.dp(82.0f))) + StickerSelectAlert.backgroundPaddingTop;
            if (VERSION.SDK_INT < 11) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, (AndroidUtilities.displaySize.y / 5) * 3), C0700C.ENCODING_PCM_32BIT));
            } else {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, size), C0700C.ENCODING_PCM_32BIT));
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !this.f462a.isDismissed() && super.onTouchEvent(motionEvent);
        }

        public void requestLayout() {
            super.requestLayout();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.h.2 */
    class StickerSelectAlert implements OnClickListener {
        final /* synthetic */ StickerSelectAlert f463a;

        StickerSelectAlert(StickerSelectAlert stickerSelectAlert) {
            this.f463a = stickerSelectAlert;
        }

        public void onClick(View view) {
            this.f463a.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.h.3 */
    class StickerSelectAlert implements OnClickListener {
        final /* synthetic */ StickerSelectAlert f464a;

        StickerSelectAlert(StickerSelectAlert stickerSelectAlert) {
            this.f464a = stickerSelectAlert;
        }

        public void onClick(View view) {
            this.f464a.f474j.onStickerSelected(this.f464a.f473i);
            this.f464a.dismiss();
        }
    }

    public StickerSelectAlert(Context context, Document document, StickersAlertDelegate stickersAlertDelegate) {
        Object obj;
        LayoutParams layoutParams;
        AnimatorSet animatorSet;
        super(context, false);
        this.f472h = new View[2];
        this.f475k = 0;
        this.f473i = document;
        this.f474j = stickersAlertDelegate;
        this.f471g = context.getResources().getDrawable(C0338R.drawable.sheet_shadow);
        this.containerView = new StickerSelectAlert(this, context);
        if (VERSION.SDK_INT < 11) {
            this.containerView.setBackgroundDrawable(this.f471g);
        } else {
            this.containerView.setWillNotDraw(false);
        }
        this.containerView.setPadding(backgroundPaddingLeft, VERSION.SDK_INT < 11 ? backgroundPaddingTop : 0, backgroundPaddingLeft, 0);
        this.f465a = new TextView(context);
        this.f472h[0] = new View(context);
        this.f472h[0].setBackgroundResource(C0338R.drawable.header_shadow);
        this.f472h[0].setAlpha(0.0f);
        this.f472h[0].clearAnimation();
        this.f472h[0].setVisibility(4);
        this.f472h[0].setTag(Integer.valueOf(1));
        this.containerView.addView(this.f472h[0], LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.f472h[1] = new View(context);
        this.f472h[1].setBackgroundResource(C0338R.drawable.header_shadow_reverse);
        this.containerView.addView(this.f472h[1], LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.f466b = new FrameLayout(context);
        this.f466b.setBackgroundColor(-536870913);
        this.f466b.setVisibility(8);
        this.f466b.setSoundEffectsEnabled(false);
        this.containerView.addView(this.f466b, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        View imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.delete_reply);
        imageView.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            imageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
        }
        this.f466b.addView(imageView, LayoutHelper.createFrame(48, 48, 53));
        imageView.setOnClickListener(new StickerSelectAlert(this));
        this.f469e = new BackupImageView(context);
        this.f469e.setAspectFit(true);
        int min = (int) (((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2)) / AndroidUtilities.density);
        this.f466b.addView(this.f469e, LayoutHelper.createFrame(min, min, 17));
        this.f470f = new TextView(context);
        this.f470f.setTextSize(1, BitmapDescriptorFactory.HUE_ORANGE);
        this.f470f.setGravity(85);
        this.f466b.addView(this.f470f, LayoutHelper.createFrame(min, min, 17));
        this.f467c = new TextView(context);
        this.f467c.setTextSize(1, 14.0f);
        this.f467c.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        this.f467c.setGravity(17);
        this.f467c.setBackgroundColor(-1);
        this.f467c.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.f467c.setText(LocaleController.getString("Close", C0338R.string.Close).toUpperCase());
        this.f467c.setTypeface(FontUtil.m1176a().m1160c());
        this.f467c.setVisibility(8);
        this.f466b.addView(this.f467c, LayoutHelper.createFrame(-1, 48, 83));
        this.f467c.setOnClickListener(new StickerSelectAlert(this));
        this.f468d = new View(context);
        this.f468d.setBackgroundResource(C0338R.drawable.header_shadow_reverse);
        this.f468d.setVisibility(8);
        this.f466b.addView(this.f468d, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.f474j != null) {
            this.f467c.setText(LocaleController.getString("SendSticker", C0338R.string.SendSticker).toUpperCase());
            this.f469e.setLayoutParams(LayoutHelper.createFrame(min, (float) min, 17, 0.0f, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE));
            this.f470f.setLayoutParams(LayoutHelper.createFrame(min, (float) min, 17, 0.0f, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE));
            this.f467c.setVisibility(0);
            this.f468d.setVisibility(0);
        }
        for (int i = 0; i < this.f473i.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) this.f473i.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                if (documentAttribute.alt != null && documentAttribute.alt.length() > 0) {
                    this.f470f.setText(Emoji.replaceEmoji(documentAttribute.alt, this.f470f.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                    obj = 1;
                    if (obj == null) {
                        this.f470f.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(this.f473i.id), this.f470f.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                    }
                    this.f469e.getImageReceiver().setImage(this.f473i, null, this.f473i.thumb.location, null, "webp", true);
                    layoutParams = (LayoutParams) this.f466b.getLayoutParams();
                    layoutParams.topMargin = this.f475k;
                    this.f466b.setLayoutParams(layoutParams);
                    this.f466b.setVisibility(0);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.f466b, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                    animatorSet.setDuration(200);
                    animatorSet.start();
                }
                obj = null;
                if (obj == null) {
                    this.f470f.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(this.f473i.id), this.f470f.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                }
                this.f469e.getImageReceiver().setImage(this.f473i, null, this.f473i.thumb.location, null, "webp", true);
                layoutParams = (LayoutParams) this.f466b.getLayoutParams();
                layoutParams.topMargin = this.f475k;
                this.f466b.setLayoutParams(layoutParams);
                this.f466b.setVisibility(0);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.f466b, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                animatorSet.setDuration(200);
                animatorSet.start();
            }
        }
        obj = null;
        if (obj == null) {
            this.f470f.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(this.f473i.id), this.f470f.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
        }
        this.f469e.getImageReceiver().setImage(this.f473i, null, this.f473i.thumb.location, null, "webp", true);
        layoutParams = (LayoutParams) this.f466b.getLayoutParams();
        layoutParams.topMargin = this.f475k;
        this.f466b.setLayoutParams(layoutParams);
        this.f466b.setVisibility(0);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.f466b, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    @SuppressLint({"NewApi"})
    private void m518c() {
        this.f465a.setTranslationY((float) this.f475k);
        this.f472h[0].setTranslationY((float) this.f475k);
        this.containerView.invalidate();
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoaded) {
            if (StickerPreviewViewer.getInstance().isVisible()) {
                StickerPreviewViewer.getInstance().close();
            }
            StickerPreviewViewer.getInstance().reset();
        }
    }

    public void dismiss() {
        super.dismiss();
        if (this.f476l != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.f476l, true);
            this.f476l = 0;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }
}
