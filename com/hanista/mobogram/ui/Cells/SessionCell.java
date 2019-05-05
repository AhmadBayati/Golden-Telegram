package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.TL_authorization;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.Locale;

public class SessionCell extends FrameLayout {
    private static Paint paint;
    private TextView detailExTextView;
    private TextView detailTextView;
    private TextView nameTextView;
    boolean needDivider;
    private TextView onlineTextView;

    public SessionCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        addView(linearLayout, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_ORANGE, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 11.0f, 11.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        if (LocaleController.isRTL) {
            linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 51, 0, 2, 0, 0));
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 53, 10, 0, 0, 0));
        } else {
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 51, 0, 0, 10, 0));
            linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 53, 0, 2, 0, 0));
        }
        this.detailTextView = new TextView(context);
        this.detailTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLines(1);
        this.detailTextView.setMaxLines(1);
        this.detailTextView.setSingleLine(true);
        this.detailTextView.setEllipsize(TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 36.0f, 17.0f, 0.0f));
        this.detailExTextView = new TextView(context);
        this.detailExTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 59.0f, 17.0f, 0.0f));
    }

    private void setTheme() {
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2497h);
            int i = AdvanceTheme.f2498i;
            int i2 = AdvanceTheme.f2494e;
            int i3 = AdvanceTheme.f2495f;
            this.nameTextView.setTextColor(i2);
            this.detailTextView.setTextColor(i2);
            this.detailExTextView.setTextColor(i3);
            paint.setColor(i);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(90.0f), C0700C.ENCODING_PCM_32BIT));
        setTheme();
    }

    public void setSession(TL_authorization tL_authorization, boolean z) {
        this.needDivider = z;
        this.nameTextView.setText(String.format(Locale.US, "%s %s", new Object[]{tL_authorization.app_name, tL_authorization.app_version}));
        int i = AdvanceTheme.f2491b;
        int i2 = AdvanceTheme.f2495f;
        if ((tL_authorization.flags & 1) != 0) {
            this.onlineTextView.setText(LocaleController.getString("Online", C0338R.string.Online));
            this.onlineTextView.setTextColor(-13660983);
            if (ThemeUtil.m2490b()) {
                this.onlineTextView.setTextColor(i);
            }
        } else {
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tL_authorization.date_active));
            this.onlineTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
            if (ThemeUtil.m2490b()) {
                this.onlineTextView.setTextColor(i2);
            }
        }
        CharSequence stringBuilder = new StringBuilder();
        if (tL_authorization.ip.length() != 0) {
            stringBuilder.append(tL_authorization.ip);
        }
        if (tL_authorization.country.length() != 0) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append("\u2014 ");
            stringBuilder.append(tL_authorization.country);
        }
        this.detailExTextView.setText(stringBuilder);
        stringBuilder = new StringBuilder();
        if (tL_authorization.device_model.length() != 0) {
            stringBuilder.append(tL_authorization.device_model);
        }
        if (!(tL_authorization.system_version.length() == 0 && tL_authorization.platform.length() == 0)) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(", ");
            }
            if (tL_authorization.platform.length() != 0) {
                stringBuilder.append(tL_authorization.platform);
            }
            if (tL_authorization.system_version.length() != 0) {
                if (tL_authorization.platform.length() != 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(tL_authorization.system_version);
            }
        }
        if ((tL_authorization.flags & 2) == 0) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(LocaleController.getString("UnofficialApp", C0338R.string.UnofficialApp));
            stringBuilder.append(" (ID: ");
            stringBuilder.append(tL_authorization.api_id);
            stringBuilder.append(")");
        }
        this.detailTextView.setText(stringBuilder);
    }
}
