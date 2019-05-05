package com.hanista.mobogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.LinkPath;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderline;

public class AboutLinkCell extends FrameLayout {
    private AboutLinkCellDelegate delegate;
    private ImageView imageView;
    private String oldText;
    private ClickableSpan pressedLink;
    private SpannableStringBuilder stringBuilder;
    private StaticLayout textLayout;
    private TextPaint textPaint;
    private int textX;
    private int textY;
    private Paint urlPaint;
    private LinkPath urlPath;

    public interface AboutLinkCellDelegate {
        void didPressUrl(String str);
    }

    public AboutLinkCell(Context context) {
        float f = 16.0f;
        super(context);
        this.urlPath = new LinkPath();
        this.textPaint = new TextPaint(1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.textPaint.setTypeface(FontUtil.m1176a().m1161d());
        this.textPaint.setColor(Theme.MSG_TEXT_COLOR);
        this.textPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
        this.urlPaint = new Paint();
        this.urlPaint.setColor(Theme.MSG_LINK_SELECT_BACKGROUND_COLOR);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        View view = this.imageView;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        float f2 = LocaleController.isRTL ? 0.0f : 16.0f;
        if (!LocaleController.isRTL) {
            f = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f2, 5.0f, f, 0.0f));
        setWillNotDraw(false);
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
        this.textX = dp;
        float f = (float) dp;
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas.translate(f, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, this.urlPaint);
        }
        try {
            if (this.textLayout != null) {
                this.textLayout.draw(canvas);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        canvas.restore();
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        this.textLayout = new StaticLayout(this.stringBuilder, this.textPaint, MeasureSpec.getSize(i) - AndroidUtilities.dp(87.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(this.textLayout.getHeight() + AndroidUtilities.dp(16.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        Throwable e;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(x, y);
        }
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

    public void setDelegate(AboutLinkCellDelegate aboutLinkCellDelegate) {
        this.delegate = aboutLinkCellDelegate;
    }

    public void setIconColor(int i) {
        this.imageView.setColorFilter(i, Mode.SRC_IN);
    }

    public void setLinkColor(int i) {
        this.textPaint.linkColor = i;
        this.urlPaint.setColor(Color.argb(Math.round(((float) Color.alpha(i)) * 0.13f), Color.red(i), Color.green(i), Color.blue(i)));
    }

    public void setTextAndIcon(String str, int i) {
        if (str == null || str.length() == 0) {
            setVisibility(8);
        } else if (str == null || this.oldText == null || !str.equals(this.oldText)) {
            this.oldText = str;
            this.stringBuilder = new SpannableStringBuilder(this.oldText);
            MessageObject.addLinks(this.stringBuilder, false);
            Emoji.replaceEmoji(this.stringBuilder, this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            requestLayout();
            if (i == 0) {
                this.imageView.setImageDrawable(null);
            } else {
                this.imageView.setImageResource(i);
            }
        }
    }

    public void setTextColor(int i) {
        this.textPaint.setColor(i);
    }
}
