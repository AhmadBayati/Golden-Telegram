package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class LocationCell extends FrameLayout {
    private static Paint paint;
    private TextView addressTextView;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needDivider;

    public LocationCell(Context context) {
        int i = 16;
        int i2 = 5;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.imageView = new BackupImageView(context);
        this.imageView.setBackgroundResource(C0338R.drawable.round_grey);
        this.imageView.setSize(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
        this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR, Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 8.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 16 : 72), 5.0f, (float) (LocaleController.isRTL ? 72 : 16), 0.0f));
        this.addressTextView = new TextView(context);
        this.addressTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.addressTextView.setTextSize(1, 14.0f);
        this.addressTextView.setMaxLines(1);
        this.addressTextView.setEllipsize(TruncateAt.END);
        this.addressTextView.setSingleLine(true);
        this.addressTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        View view = this.addressTextView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        int i3 = i2 | 48;
        float f = (float) (LocaleController.isRTL ? 16 : 72);
        if (LocaleController.isRTL) {
            i = 72;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i3, f, BitmapDescriptorFactory.HUE_ORANGE, (float) i, 0.0f));
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(56.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setLocation(TL_messageMediaVenue tL_messageMediaVenue, String str, boolean z) {
        this.needDivider = z;
        this.nameTextView.setText(tL_messageMediaVenue.title);
        this.addressTextView.setText(tL_messageMediaVenue.address);
        this.imageView.setImage(str, null, null);
        setWillNotDraw(!z);
    }
}
