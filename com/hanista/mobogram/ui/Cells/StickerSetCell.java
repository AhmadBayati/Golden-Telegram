package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.ArrayList;

public class StickerSetCell extends FrameLayout {
    private static Paint paint;
    private BackupImageView imageView;
    private boolean needDivider;
    private ImageView optionsButton;
    private Rect rect;
    private TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public StickerSetCell(Context context) {
        int i = 3;
        super(context);
        this.rect = new Rect();
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
        }
        this.textView = new TextView(context);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
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
        this.optionsButton = new ImageView(context);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR));
        this.optionsButton.setImageResource(C0338R.drawable.doc_actions_b);
        this.optionsButton.setScaleType(ScaleType.CENTER);
        View view = this.optionsButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(40, 40, i | 48));
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2497h);
            int i = AdvanceTheme.f2498i;
            int i2 = AdvanceTheme.f2494e;
            int i3 = AdvanceTheme.f2495f;
            this.textView.setTextColor(i2);
            this.valueTextView.setTextColor(i3);
            paint.setColor(i);
        }
    }

    public TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
        initTheme();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null) {
            this.optionsButton.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setOnOptionsClick(OnClickListener onClickListener) {
        this.optionsButton.setOnClickListener(onClickListener);
    }

    public void setStickersSet(TL_messages_stickerSet tL_messages_stickerSet, boolean z) {
        this.needDivider = z;
        this.stickersSet = tL_messages_stickerSet;
        this.textView.setText(this.stickersSet.set.title);
        if (this.stickersSet.set.archived) {
            this.textView.setAlpha(0.5f);
            this.valueTextView.setAlpha(0.5f);
            this.imageView.setAlpha(0.5f);
        } else {
            this.textView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.valueTextView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.imageView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        ArrayList arrayList = tL_messages_stickerSet.documents;
        if (arrayList == null || arrayList.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
            return;
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", arrayList.size()));
        Document document = (Document) arrayList.get(0);
        if (document.thumb != null && document.thumb.location != null) {
            this.imageView.setImage(document.thumb.location, null, "webp", null);
        }
    }
}
