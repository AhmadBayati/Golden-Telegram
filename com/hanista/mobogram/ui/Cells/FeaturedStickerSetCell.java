package com.hanista.mobogram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.Switch;

public class FeaturedStickerSetCell extends FrameLayout {
    private static Paint botProgressPaint;
    private static Paint paint;
    private TextView addButton;
    private int angle;
    private Switch checkBox;
    private ImageView checkImage;
    private AnimatorSet currentAnimation;
    private boolean drawProgress;
    private BackupImageView imageView;
    private boolean isInstalled;
    private long lastUpdateTime;
    private boolean needDivider;
    private float progressAlpha;
    private RectF progressRect;
    private Rect rect;
    private StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;
    private boolean wasLayout;

    /* renamed from: com.hanista.mobogram.ui.Cells.FeaturedStickerSetCell.1 */
    class C11041 extends TextView {
        C11041(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (FeaturedStickerSetCell.this.drawProgress || !(FeaturedStickerSetCell.this.drawProgress || FeaturedStickerSetCell.this.progressAlpha == 0.0f)) {
                FeaturedStickerSetCell.botProgressPaint.setAlpha(Math.min(NalUnitUtil.EXTENDED_SAR, (int) (FeaturedStickerSetCell.this.progressAlpha * 255.0f)));
                int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                FeaturedStickerSetCell.this.progressRect.set((float) measuredWidth, (float) AndroidUtilities.dp(3.0f), (float) (measuredWidth + AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(11.0f));
                canvas.drawArc(FeaturedStickerSetCell.this.progressRect, (float) FeaturedStickerSetCell.this.angle, 220.0f, false, FeaturedStickerSetCell.botProgressPaint);
                invalidate(((int) FeaturedStickerSetCell.this.progressRect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.bottom) + AndroidUtilities.dp(2.0f));
                long currentTimeMillis = System.currentTimeMillis();
                if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                    long access$500 = currentTimeMillis - FeaturedStickerSetCell.this.lastUpdateTime;
                    FeaturedStickerSetCell.this.angle = (int) ((((float) (360 * access$500)) / 2000.0f) + ((float) FeaturedStickerSetCell.this.angle));
                    FeaturedStickerSetCell.this.angle = FeaturedStickerSetCell.this.angle - ((FeaturedStickerSetCell.this.angle / 360) * 360);
                    if (FeaturedStickerSetCell.this.drawProgress) {
                        if (FeaturedStickerSetCell.this.progressAlpha < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                            FeaturedStickerSetCell.this.progressAlpha = (((float) access$500) / 200.0f) + FeaturedStickerSetCell.this.progressAlpha;
                            if (FeaturedStickerSetCell.this.progressAlpha > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                FeaturedStickerSetCell.this.progressAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            }
                        }
                    } else if (FeaturedStickerSetCell.this.progressAlpha > 0.0f) {
                        FeaturedStickerSetCell.this.progressAlpha = FeaturedStickerSetCell.this.progressAlpha - (((float) access$500) / 200.0f);
                        if (FeaturedStickerSetCell.this.progressAlpha < 0.0f) {
                            FeaturedStickerSetCell.this.progressAlpha = 0.0f;
                        }
                    }
                }
                FeaturedStickerSetCell.this.lastUpdateTime = currentTimeMillis;
                invalidate();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Cells.FeaturedStickerSetCell.2 */
    class C11052 extends Drawable {
        Paint paint;

        C11052() {
            this.paint = new Paint(1);
        }

        public void draw(Canvas canvas) {
            this.paint.setColor(-12277526);
            canvas.drawCircle((float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(8.0f);
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(12.0f);
        }

        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Cells.FeaturedStickerSetCell.3 */
    class C11063 extends AnimatorListenerAdapterProxy {
        C11063() {
        }

        public void onAnimationCancel(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                FeaturedStickerSetCell.this.currentAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                FeaturedStickerSetCell.this.addButton.setVisibility(4);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Cells.FeaturedStickerSetCell.4 */
    class C11074 extends AnimatorListenerAdapterProxy {
        C11074() {
        }

        public void onAnimationCancel(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                FeaturedStickerSetCell.this.currentAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                FeaturedStickerSetCell.this.checkImage.setVisibility(4);
            }
        }
    }

    public FeaturedStickerSetCell(Context context) {
        int i = 3;
        super(context);
        this.rect = new Rect();
        this.progressRect = new RectF();
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
        }
        if (botProgressPaint == null) {
            botProgressPaint = new Paint(1);
            botProgressPaint.setColor(-1);
            botProgressPaint.setStrokeCap(Cap.ROUND);
            botProgressPaint.setStyle(Style.STROKE);
        }
        botProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-7697782);
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.addButton = new C11041(context);
        this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        this.addButton.setGravity(17);
        this.addButton.setTextColor(-1);
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(FontUtil.m1176a().m1160c());
        this.addButton.setBackgroundResource(C0338R.drawable.add_states);
        this.addButton.setText(LocaleController.getString("Add", C0338R.string.Add).toUpperCase());
        View view = this.addButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, 28.0f, i | 48, LocaleController.isRTL ? 14.0f : 0.0f, 18.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setImageResource(C0338R.drawable.sticker_added);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f));
    }

    public StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int left = (this.addButton.getLeft() + (this.addButton.getMeasuredWidth() / 2)) - (this.checkImage.getMeasuredWidth() / 2);
        int top = (this.addButton.getTop() + (this.addButton.getMeasuredHeight() / 2)) - (this.checkImage.getMeasuredHeight() / 2);
        this.checkImage.layout(left, top, this.checkImage.getMeasuredWidth() + left, this.checkImage.getMeasuredHeight() + top);
        this.wasLayout = true;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setAddOnClickListener(OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean z) {
        this.drawProgress = z;
        this.lastUpdateTime = System.currentTimeMillis();
        this.addButton.invalidate();
    }

    public void setStickersSet(StickerSetCovered stickerSetCovered, boolean z, boolean z2) {
        boolean z3 = stickerSetCovered == this.stickersSet && this.wasLayout;
        this.needDivider = z;
        this.stickersSet = stickerSetCovered;
        this.lastUpdateTime = System.currentTimeMillis();
        setWillNotDraw(!this.needDivider);
        if (this.currentAnimation != null) {
            this.currentAnimation.cancel();
            this.currentAnimation = null;
        }
        this.textView.setText(this.stickersSet.set.title);
        if (z2) {
            Drawable c11052 = new C11052();
            TextView textView = this.textView;
            Drawable drawable = LocaleController.isRTL ? null : c11052;
            if (!LocaleController.isRTL) {
                c11052 = null;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, c11052, null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        if (stickerSetCovered.cover != null && stickerSetCovered.cover.thumb != null && stickerSetCovered.cover.thumb.location != null) {
            this.imageView.setImage(stickerSetCovered.cover.thumb.location, null, "webp", null);
        } else if (!stickerSetCovered.covers.isEmpty()) {
            this.imageView.setImage(((Document) stickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
        }
        boolean z4;
        if (z3) {
            z4 = this.isInstalled;
            z3 = StickersQuery.isStickerPackInstalled(stickerSetCovered.set.id);
            this.isInstalled = z3;
            if (z3) {
                if (!z4) {
                    this.checkImage.setVisibility(0);
                    this.addButton.setClickable(false);
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(200);
                    this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.addButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.01f}), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{0.01f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{0.01f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                    this.currentAnimation.addListener(new C11063());
                    this.currentAnimation.start();
                    return;
                }
                return;
            } else if (z4) {
                this.addButton.setVisibility(0);
                this.addButton.setClickable(true);
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(200);
                this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.01f}), ObjectAnimator.ofFloat(this.addButton, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[]{0.01f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[]{0.01f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                this.currentAnimation.addListener(new C11074());
                this.currentAnimation.start();
                return;
            } else {
                return;
            }
        }
        z4 = StickersQuery.isStickerPackInstalled(stickerSetCovered.set.id);
        this.isInstalled = z4;
        if (z4) {
            this.addButton.setVisibility(4);
            this.addButton.setClickable(false);
            this.checkImage.setVisibility(0);
            this.checkImage.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.checkImage.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.checkImage.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            return;
        }
        this.addButton.setVisibility(0);
        this.addButton.setClickable(true);
        this.checkImage.setVisibility(4);
        this.addButton.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.addButton.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.addButton.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
}
