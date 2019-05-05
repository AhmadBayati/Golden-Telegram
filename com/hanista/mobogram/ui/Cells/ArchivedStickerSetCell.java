package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.Switch;

public class ArchivedStickerSetCell extends FrameLayout {
    private static Paint paint;
    private Switch checkBox;
    private BackupImageView imageView;
    private boolean needDivider;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Rect rect;
    private StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;

    /* renamed from: com.hanista.mobogram.ui.Cells.ArchivedStickerSetCell.1 */
    class C10991 extends Drawable {
        Paint paint;

        C10991() {
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

    /* renamed from: com.hanista.mobogram.ui.Cells.ArchivedStickerSetCell.2 */
    class C11002 implements OnClickListener {
        C11002() {
        }

        public void onClick(View view) {
        }
    }

    public ArchivedStickerSetCell(Context context, boolean z) {
        int i = 3;
        super(context);
        this.rect = new Rect();
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
        }
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-7697782);
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        if (z) {
            this.checkBox = new Switch(context);
            this.checkBox.setDuplicateParentStateEnabled(false);
            this.checkBox.setFocusable(false);
            this.checkBox.setFocusableInTouchMode(false);
            View view = this.checkBox;
            if (!LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 16, 14.0f, 0.0f, 14.0f, 0.0f));
        }
    }

    public StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    public boolean isChecked() {
        return this.checkBox != null && this.checkBox.isChecked();
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.checkBox != null) {
            this.checkBox.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                motionEvent.offsetLocation(-this.checkBox.getX(), -this.checkBox.getY());
                return this.checkBox.onTouchEvent(motionEvent);
            }
        }
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setChecked(boolean z) {
        this.checkBox.setOnCheckedChangeListener(null);
        this.checkBox.setChecked(z);
        this.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    public void setOnCheckClick(OnCheckedChangeListener onCheckedChangeListener) {
        Switch switchR = this.checkBox;
        this.onCheckedChangeListener = onCheckedChangeListener;
        switchR.setOnCheckedChangeListener(onCheckedChangeListener);
        this.checkBox.setOnClickListener(new C11002());
    }

    public void setStickersSet(StickerSetCovered stickerSetCovered, boolean z, boolean z2) {
        this.needDivider = z;
        this.stickersSet = stickerSetCovered;
        setWillNotDraw(!this.needDivider);
        this.textView.setText(this.stickersSet.set.title);
        if (z2) {
            Drawable c10991 = new C10991();
            TextView textView = this.textView;
            Drawable drawable = LocaleController.isRTL ? null : c10991;
            if (!LocaleController.isRTL) {
                c10991 = null;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, c10991, null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        if (stickerSetCovered.cover != null && stickerSetCovered.cover.thumb != null && stickerSetCovered.cover.thumb.location != null) {
            this.imageView.setImage(stickerSetCovered.cover.thumb.location, null, "webp", null);
        } else if (!stickerSetCovered.covers.isEmpty()) {
            this.imageView.setImage(((Document) stickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
        }
    }
}
