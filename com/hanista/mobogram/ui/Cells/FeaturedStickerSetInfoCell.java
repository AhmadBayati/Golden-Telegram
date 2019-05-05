package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    private static Paint botProgressPaint;
    private TextView addButton;
    private int angle;
    private boolean drawProgress;
    Drawable drawable;
    private boolean hasOnClick;
    private TextView infoTextView;
    private boolean isInstalled;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float progressAlpha;
    private RectF rect;
    private StickerSetCovered set;

    /* renamed from: com.hanista.mobogram.ui.Cells.FeaturedStickerSetInfoCell.1 */
    class C11081 extends Drawable {
        Paint paint;

        C11081() {
            this.paint = new Paint(1);
        }

        public void draw(Canvas canvas) {
            this.paint.setColor(-11688214);
            canvas.drawCircle((float) AndroidUtilities.dp(8.0f), 0.0f, (float) AndroidUtilities.dp(4.0f), this.paint);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(26.0f);
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

    /* renamed from: com.hanista.mobogram.ui.Cells.FeaturedStickerSetInfoCell.2 */
    class C11092 extends TextView {
        C11092(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (FeaturedStickerSetInfoCell.this.drawProgress || !(FeaturedStickerSetInfoCell.this.drawProgress || FeaturedStickerSetInfoCell.this.progressAlpha == 0.0f)) {
                FeaturedStickerSetInfoCell.botProgressPaint.setAlpha(Math.min(NalUnitUtil.EXTENDED_SAR, (int) (FeaturedStickerSetInfoCell.this.progressAlpha * 255.0f)));
                int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                FeaturedStickerSetInfoCell.this.rect.set((float) measuredWidth, (float) AndroidUtilities.dp(3.0f), (float) (measuredWidth + AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(11.0f));
                canvas.drawArc(FeaturedStickerSetInfoCell.this.rect, (float) FeaturedStickerSetInfoCell.this.angle, 220.0f, false, FeaturedStickerSetInfoCell.botProgressPaint);
                invalidate(((int) FeaturedStickerSetInfoCell.this.rect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.bottom) + AndroidUtilities.dp(2.0f));
                long currentTimeMillis = System.currentTimeMillis();
                if (Math.abs(FeaturedStickerSetInfoCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                    long access$500 = currentTimeMillis - FeaturedStickerSetInfoCell.this.lastUpdateTime;
                    FeaturedStickerSetInfoCell.this.angle = (int) ((((float) (360 * access$500)) / 2000.0f) + ((float) FeaturedStickerSetInfoCell.this.angle));
                    FeaturedStickerSetInfoCell.this.angle = FeaturedStickerSetInfoCell.this.angle - ((FeaturedStickerSetInfoCell.this.angle / 360) * 360);
                    if (FeaturedStickerSetInfoCell.this.drawProgress) {
                        if (FeaturedStickerSetInfoCell.this.progressAlpha < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                            FeaturedStickerSetInfoCell.this.progressAlpha = (((float) access$500) / 200.0f) + FeaturedStickerSetInfoCell.this.progressAlpha;
                            if (FeaturedStickerSetInfoCell.this.progressAlpha > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                FeaturedStickerSetInfoCell.this.progressAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            }
                        }
                    } else if (FeaturedStickerSetInfoCell.this.progressAlpha > 0.0f) {
                        FeaturedStickerSetInfoCell.this.progressAlpha = FeaturedStickerSetInfoCell.this.progressAlpha - (((float) access$500) / 200.0f);
                        if (FeaturedStickerSetInfoCell.this.progressAlpha < 0.0f) {
                            FeaturedStickerSetInfoCell.this.progressAlpha = 0.0f;
                        }
                    }
                }
                FeaturedStickerSetInfoCell.this.lastUpdateTime = currentTimeMillis;
                invalidate();
            }
        }
    }

    public FeaturedStickerSetInfoCell(Context context, int i) {
        super(context);
        this.rect = new RectF();
        this.drawable = new C11081();
        if (botProgressPaint == null) {
            botProgressPaint = new Paint(1);
            botProgressPaint.setColor(-1);
            botProgressPaint.setStrokeCap(Cap.ROUND);
            botProgressPaint.setStyle(Style.STROKE);
        }
        botProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        addView(this.nameTextView, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 51, (float) i, 8.0f, 100.0f, 0.0f));
        this.infoTextView = new TextView(context);
        this.infoTextView.setTextColor(-7697782);
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        addView(this.infoTextView, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 51, (float) i, BitmapDescriptorFactory.HUE_ORANGE, 100.0f, 0.0f));
        this.addButton = new C11092(context);
        this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        this.addButton.setGravity(17);
        this.addButton.setTextColor(-1);
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(FontUtil.m1176a().m1160c());
        addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
    }

    public StickerSetCovered getStickerSet() {
        return this.set;
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW), C0700C.ENCODING_PCM_32BIT));
    }

    public void setAddOnClickListener(OnClickListener onClickListener) {
        this.hasOnClick = true;
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean z) {
        this.drawProgress = z;
        this.lastUpdateTime = System.currentTimeMillis();
        this.addButton.invalidate();
    }

    public void setStickerSet(StickerSetCovered stickerSetCovered, boolean z) {
        this.lastUpdateTime = System.currentTimeMillis();
        this.nameTextView.setText(stickerSetCovered.set.title);
        this.infoTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        if (z) {
            this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, this.drawable, null);
        } else {
            this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        if (this.hasOnClick) {
            this.addButton.setVisibility(0);
            boolean isStickerPackInstalled = StickersQuery.isStickerPackInstalled(stickerSetCovered.set.id);
            this.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                this.addButton.setBackgroundResource(C0338R.drawable.del_states);
                this.addButton.setText(LocaleController.getString("StickersRemove", C0338R.string.StickersRemove).toUpperCase());
            } else {
                this.addButton.setBackgroundResource(C0338R.drawable.add_states);
                this.addButton.setText(LocaleController.getString("Add", C0338R.string.Add).toUpperCase());
            }
        } else {
            this.addButton.setVisibility(8);
        }
        this.set = stickerSetCovered;
    }
}
