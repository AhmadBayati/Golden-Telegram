package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LinkPath;
import com.hanista.mobogram.ui.Components.TypefaceSpan;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderline;

public class BotHelpCell extends View {
    private BotHelpCellDelegate delegate;
    private int height;
    private String oldText;
    private ClickableSpan pressedLink;
    private StaticLayout textLayout;
    private TextPaint textPaint;
    private int textX;
    private int textY;
    private Paint urlPaint;
    private LinkPath urlPath;
    private int width;

    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context) {
        super(context);
        this.urlPath = new LinkPath();
        this.textPaint = new TextPaint(1);
        this.textPaint.setTypeface(FontUtil.m1176a().m1161d());
        this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.textPaint.setColor(Theme.MSG_TEXT_COLOR);
        this.textPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
        this.urlPaint = new Paint();
        this.urlPaint.setTypeface(FontUtil.m1176a().m1161d());
        this.urlPaint.setColor(Theme.MSG_LINK_SELECT_BACKGROUND_COLOR);
        initTheme();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.bp;
            int i2 = AdvanceTheme.br;
            this.textPaint.setColor(i);
            this.textPaint.linkColor = i2;
            this.urlPaint.setColor(AdvanceTheme.m2284b(AdvanceTheme.bs, 858877855, 0.3f));
        }
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        if (ThemeUtil.m2490b()) {
            Theme.backgroundMediaDrawableIn.setColorFilter(AdvanceTheme.bo, Mode.SRC_IN);
        }
        int width = (canvas.getWidth() - this.width) / 2;
        int dp = AndroidUtilities.dp(4.0f);
        Theme.backgroundMediaDrawableIn.setBounds(width, dp, this.width + width, this.height + dp);
        Theme.backgroundMediaDrawableIn.draw(canvas);
        canvas.save();
        width += AndroidUtilities.dp(11.0f);
        this.textX = width;
        float f = (float) width;
        dp += AndroidUtilities.dp(11.0f);
        this.textY = dp;
        canvas.translate(f, (float) dp);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, this.urlPaint);
        }
        if (this.textLayout != null) {
            this.textLayout.draw(canvas);
        }
        canvas.restore();
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), this.height + AndroidUtilities.dp(8.0f));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        Throwable e;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (this.textLayout != null) {
            if (motionEvent.getAction() == 0 || (this.pressedLink != null && motionEvent.getAction() == 1)) {
                if (motionEvent.getAction() == 0) {
                    resetPressedLink();
                    try {
                        int i = (int) (x - ((float) this.textX));
                        int lineForVertical = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                        int offsetForHorizontal = this.textLayout.getOffsetForHorizontal(lineForVertical, (float) i);
                        float lineLeft = this.textLayout.getLineLeft(lineForVertical);
                        if (lineLeft > ((float) i) || this.textLayout.getLineWidth(lineForVertical) + lineLeft < ((float) i)) {
                            resetPressedLink();
                        } else {
                            Spannable spannable = (Spannable) this.textLayout.getText();
                            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                            if (clickableSpanArr.length != 0) {
                                resetPressedLink();
                                this.pressedLink = clickableSpanArr[0];
                                try {
                                    lineForVertical = spannable.getSpanStart(this.pressedLink);
                                    this.urlPath.setCurrentLayout(this.textLayout, lineForVertical, 0.0f);
                                    this.textLayout.getSelectionPath(lineForVertical, spannable.getSpanEnd(this.pressedLink), this.urlPath);
                                    z = true;
                                } catch (Throwable e2) {
                                    try {
                                        FileLog.m18e("tmessages", e2);
                                        z = true;
                                    } catch (Exception e3) {
                                        e2 = e3;
                                        boolean z2 = true;
                                        resetPressedLink();
                                        FileLog.m18e("tmessages", e2);
                                        z = z2;
                                        if (!z) {
                                        }
                                    }
                                }
                                return z || super.onTouchEvent(motionEvent);
                            } else {
                                resetPressedLink();
                            }
                        }
                    } catch (Exception e4) {
                        e2 = e4;
                        z2 = false;
                        resetPressedLink();
                        FileLog.m18e("tmessages", e2);
                        z = z2;
                        if (z) {
                        }
                    }
                } else if (this.pressedLink != null) {
                    try {
                        if (this.pressedLink instanceof URLSpanNoUnderline) {
                            String url = ((URLSpanNoUnderline) this.pressedLink).getURL();
                            if ((url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) && this.delegate != null) {
                                this.delegate.didPressUrl(url);
                            }
                        } else if (this.pressedLink instanceof URLSpan) {
                            Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                        } else {
                            this.pressedLink.onClick(this);
                        }
                    } catch (Throwable e22) {
                        FileLog.m18e("tmessages", e22);
                    }
                    resetPressedLink();
                    z = true;
                    if (z) {
                    }
                }
            } else if (motionEvent.getAction() == 3) {
                resetPressedLink();
            }
        }
        z = false;
        if (z) {
        }
    }

    public void setDelegate(BotHelpCellDelegate botHelpCellDelegate) {
        this.delegate = botHelpCellDelegate;
    }

    public void setText(String str) {
        if (str == null || str.length() == 0) {
            setVisibility(8);
        } else if (str == null || this.oldText == null || !str.equals(this.oldText)) {
            this.oldText = str;
            setVisibility(0);
            if (AndroidUtilities.isTablet()) {
                this.width = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                this.width = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            CharSequence spannableStringBuilder = new SpannableStringBuilder();
            Object string = LocaleController.getString("BotInfoTitle", C0338R.string.BotInfoTitle);
            spannableStringBuilder.append(string);
            spannableStringBuilder.append("\n\n");
            spannableStringBuilder.append(str);
            MessageObject.addLinks(spannableStringBuilder);
            spannableStringBuilder.setSpan(new TypefaceSpan(FontUtil.m1176a().m1160c()), 0, string.length(), 33);
            Emoji.replaceEmoji(spannableStringBuilder, this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            try {
                this.textLayout = new StaticLayout(spannableStringBuilder, this.textPaint, this.width, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                this.width = 0;
                this.height = this.textLayout.getHeight() + AndroidUtilities.dp(22.0f);
                int lineCount = this.textLayout.getLineCount();
                for (int i = 0; i < lineCount; i++) {
                    this.width = (int) Math.ceil((double) Math.max((float) this.width, this.textLayout.getLineWidth(i) + this.textLayout.getLineLeft(i)));
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessage", e);
            }
            this.width += AndroidUtilities.dp(22.0f);
        }
    }
}
