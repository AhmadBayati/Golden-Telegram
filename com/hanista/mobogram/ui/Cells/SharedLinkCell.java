package com.hanista.mobogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityEmail;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityTextUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPage;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.LetterDrawable;
import com.hanista.mobogram.ui.Components.LinkPath;
import java.util.ArrayList;
import java.util.Locale;

public class SharedLinkCell extends FrameLayout {
    private static TextPaint descriptionTextPaint;
    private static Paint paint;
    private static TextPaint titleTextPaint;
    private static Paint urlPaint;
    private CheckBox checkBox;
    private SharedLinkCellDelegate delegate;
    private int description2Y;
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private int descriptionY;
    private boolean drawLinkImageView;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout;
    private boolean linkPreviewPressed;
    private int linkY;
    ArrayList<String> links;
    private MessageObject message;
    private boolean needDivider;
    private int pressedLink;
    private StaticLayout titleLayout;
    private int titleY;
    private LinkPath urlPath;

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(WebPage webPage);
    }

    public SharedLinkCell(Context context) {
        super(context);
        this.urlPath = new LinkPath();
        this.links = new ArrayList();
        this.linkLayout = new ArrayList();
        this.titleY = AndroidUtilities.dp(7.0f);
        this.descriptionY = AndroidUtilities.dp(27.0f);
        this.description2Y = AndroidUtilities.dp(27.0f);
        if (titleTextPaint == null) {
            titleTextPaint = new TextPaint(1);
            titleTextPaint.setTypeface(FontUtil.m1176a().m1160c());
            titleTextPaint.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            descriptionTextPaint = new TextPaint(1);
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            urlPaint = new Paint();
            urlPaint.setColor(Theme.MSG_LINK_SELECT_BACKGROUND_COLOR);
        }
        titleTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        setWillNotDraw(false);
        this.linkImageView = new ImageReceiver(this);
        this.letterDrawable = new LetterDrawable();
        this.checkBox = new CheckBox(context, C0338R.drawable.round_check2);
        this.checkBox.setVisibility(4);
        addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
    }

    public String getLink(int i) {
        return (i < 0 || i >= this.links.size()) ? null : (String) this.links.get(i);
    }

    public MessageObject getMessage() {
        return this.message;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    protected void onDraw(Canvas canvas) {
        int i = 0;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            descriptionTextPaint.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            descriptionTextPaint.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            this.descriptionLayout2.draw(canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            descriptionTextPaint.setColor(Theme.MSG_LINK_TEXT_COLOR);
            int i2 = 0;
            while (i < this.linkLayout.size()) {
                StaticLayout staticLayout = (StaticLayout) this.linkLayout.get(i);
                if (staticLayout.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + i2));
                    if (this.pressedLink == i) {
                        canvas.drawPath(this.urlPath, urlPaint);
                    }
                    staticLayout.draw(canvas);
                    canvas.restore();
                    i2 += staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
                }
                i++;
            }
        }
        this.letterDrawable.draw(canvas);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas);
        }
        if (!this.needDivider) {
            return;
        }
        if (LocaleController.isRTL) {
            canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), paint);
        } else {
            canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), paint);
        }
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        String str;
        Object obj;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        int lastIndexOf;
        Throwable th;
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.descriptionLayout2 = null;
        this.description2Y = this.descriptionY;
        this.linkLayout.clear();
        this.links.clear();
        int size = (MeasureSpec.getSize(i) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        Object obj2 = null;
        if ((this.message.messageOwner.media instanceof TL_messageMediaWebPage) && (this.message.messageOwner.media.webpage instanceof TL_webPage)) {
            WebPage webPage = this.message.messageOwner.media.webpage;
            if (this.message.photoThumbs == null && webPage.photo != null) {
                this.message.generateThumbs(true);
            }
            Object obj3 = (webPage.photo == null || this.message.photoThumbs == null) ? null : 1;
            str = webPage.title;
            if (str == null) {
                str = webPage.site_name;
            }
            obj = obj3;
            str2 = webPage.description;
            String str7 = webPage.url;
            str3 = str;
            obj2 = str7;
        } else {
            obj = null;
            str2 = null;
            str3 = null;
        }
        if (this.message == null || this.message.messageOwner.entities.isEmpty()) {
            str4 = null;
            str5 = str2;
            str6 = str3;
        } else {
            int i3 = 0;
            String str8 = null;
            String str9 = str3;
            str3 = str2;
            while (i3 < this.message.messageOwner.entities.size()) {
                MessageEntity messageEntity = (MessageEntity) this.message.messageOwner.entities.get(i3);
                if (messageEntity.length <= 0 || messageEntity.offset < 0) {
                    str2 = str8;
                    str8 = str9;
                } else if (messageEntity.offset >= this.message.messageOwner.message.length()) {
                    str2 = str8;
                    str8 = str9;
                } else {
                    if (messageEntity.offset + messageEntity.length > this.message.messageOwner.message.length()) {
                        messageEntity.length = this.message.messageOwner.message.length() - messageEntity.offset;
                    }
                    if (!(i3 != 0 || obj2 == null || (messageEntity.offset == 0 && messageEntity.length == this.message.messageOwner.message.length()))) {
                        if (this.message.messageOwner.entities.size() != 1) {
                            str8 = this.message.messageOwner.message;
                        } else if (str3 == null) {
                            str8 = this.message.messageOwner.message;
                        }
                    }
                    String str10 = null;
                    try {
                        if ((messageEntity instanceof TL_messageEntityTextUrl) || (messageEntity instanceof TL_messageEntityUrl)) {
                            str10 = messageEntity instanceof TL_messageEntityUrl ? this.message.messageOwner.message.substring(messageEntity.offset, messageEntity.offset + messageEntity.length) : messageEntity.url;
                            if (str9 == null || str9.length() == 0) {
                                try {
                                    str9 = Uri.parse(str10).getHost();
                                    if (str9 == null) {
                                        str9 = str10;
                                    }
                                    if (str9 != null) {
                                        lastIndexOf = str9.lastIndexOf(46);
                                        if (lastIndexOf >= 0) {
                                            str9 = str9.substring(0, lastIndexOf);
                                            lastIndexOf = str9.lastIndexOf(46);
                                            if (lastIndexOf >= 0) {
                                                str9 = str9.substring(lastIndexOf + 1);
                                            }
                                            str9 = str9.substring(0, 1).toUpperCase() + str9.substring(1);
                                        }
                                    }
                                    str2 = (messageEntity.offset == 0 && messageEntity.length == this.message.messageOwner.message.length()) ? str3 : this.message.messageOwner.message;
                                    str3 = str2;
                                    str2 = str9;
                                } catch (Throwable e) {
                                    th = e;
                                    str2 = str10;
                                    FileLog.m18e("tmessages", th);
                                    str7 = str8;
                                    str8 = str2;
                                    str2 = str7;
                                    i3++;
                                    str9 = str8;
                                    str8 = str2;
                                }
                            }
                            str2 = str9;
                        } else {
                            if ((messageEntity instanceof TL_messageEntityEmail) && (str9 == null || str9.length() == 0)) {
                                str10 = "mailto:" + this.message.messageOwner.message.substring(messageEntity.offset, messageEntity.offset + messageEntity.length);
                                str9 = this.message.messageOwner.message.substring(messageEntity.offset, messageEntity.offset + messageEntity.length);
                                if (!(messageEntity.offset == 0 && messageEntity.length == this.message.messageOwner.message.length())) {
                                    str3 = this.message.messageOwner.message;
                                    str2 = str9;
                                }
                            }
                            str2 = str9;
                        }
                        if (str10 != null) {
                            try {
                                if (str10.toLowerCase().indexOf("http") == 0 || str10.toLowerCase().indexOf("mailto") == 0) {
                                    this.links.add(str10);
                                } else {
                                    this.links.add("http://" + str10);
                                }
                            } catch (Exception e2) {
                                th = e2;
                                FileLog.m18e("tmessages", th);
                                str7 = str8;
                                str8 = str2;
                                str2 = str7;
                                i3++;
                                str9 = str8;
                                str8 = str2;
                            }
                        }
                        str7 = str8;
                        str8 = str2;
                        str2 = str7;
                    } catch (Throwable e3) {
                        Throwable th2 = e3;
                        str2 = str9;
                        th = th2;
                        FileLog.m18e("tmessages", th);
                        str7 = str8;
                        str8 = str2;
                        str2 = str7;
                        i3++;
                        str9 = str8;
                        str8 = str2;
                    }
                }
                i3++;
                str9 = str8;
                str8 = str2;
            }
            str4 = str8;
            str5 = str3;
            str6 = str9;
        }
        if (obj2 != null && this.links.isEmpty()) {
            this.links.add(obj2);
        }
        if (str6 != null) {
            try {
                this.titleLayout = new StaticLayout(TextUtils.ellipsize(str6.replace('\n', ' '), titleTextPaint, (float) Math.min((int) Math.ceil((double) titleTextPaint.measureText(str6)), size), TruncateAt.END), titleTextPaint, size, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            } catch (Throwable e32) {
                FileLog.m18e("tmessages", e32);
            }
            this.letterDrawable.setTitle(str6);
        }
        if (str5 != null) {
            try {
                this.descriptionLayout = ChatMessageCell.generateStaticLayout(str5, descriptionTextPaint, size, size, 0, 3);
                if (this.descriptionLayout.getLineCount() > 0) {
                    this.description2Y = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                }
            } catch (Throwable e322) {
                FileLog.m18e("tmessages", e322);
            }
        }
        if (str4 != null) {
            try {
                this.descriptionLayout2 = ChatMessageCell.generateStaticLayout(str4, descriptionTextPaint, size, size, 0, 3);
                this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1);
                if (this.descriptionLayout != null) {
                    this.description2Y += AndroidUtilities.dp(10.0f);
                }
            } catch (Throwable e3222) {
                FileLog.m18e("tmessages", e3222);
            }
        }
        if (!this.links.isEmpty()) {
            for (lastIndexOf = 0; lastIndexOf < this.links.size(); lastIndexOf++) {
                try {
                    str2 = (String) this.links.get(lastIndexOf);
                    StaticLayout staticLayout = new StaticLayout(TextUtils.ellipsize(str2.replace('\n', ' '), descriptionTextPaint, (float) Math.min((int) Math.ceil((double) descriptionTextPaint.measureText(str2)), size), TruncateAt.MIDDLE), descriptionTextPaint, size, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                    this.linkY = this.description2Y;
                    if (!(this.descriptionLayout2 == null || this.descriptionLayout2.getLineCount() == 0)) {
                        this.linkY += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                    this.linkLayout.add(staticLayout);
                } catch (Throwable e32222) {
                    FileLog.m18e("tmessages", e32222);
                }
            }
        }
        int dp = AndroidUtilities.dp(52.0f);
        int size2 = LocaleController.isRTL ? (MeasureSpec.getSize(i) - AndroidUtilities.dp(10.0f)) - dp : AndroidUtilities.dp(10.0f);
        this.letterDrawable.setBounds(size2, AndroidUtilities.dp(10.0f), size2 + dp, AndroidUtilities.dp(62.0f));
        if (obj != null) {
            PhotoSize photoSize;
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, dp, true);
            TLObject closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, 80);
            if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                photoSize = null;
            } else {
                TLObject tLObject = closestPhotoSizeWithSize2;
            }
            closestPhotoSizeWithSize.size = -1;
            if (photoSize != null) {
                photoSize.size = -1;
            }
            this.linkImageView.setImageCoords(size2, AndroidUtilities.dp(10.0f), dp, dp);
            str = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
            obj3 = 1;
            if (!FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists()) {
                obj3 = null;
            }
            str3 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)});
            if (obj3 != null || MediaController.m71a().m157a(1) || FileLoader.getInstance().isLoadingFile(str)) {
                this.linkImageView.setImage(closestPhotoSizeWithSize.location, str3, photoSize != null ? photoSize.location : null, String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), 0, null, false);
            } else if (photoSize != null) {
                this.linkImageView.setImage(null, null, photoSize.location, String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), 0, null, false);
            } else {
                this.linkImageView.setImageBitmap((Drawable) null);
            }
            this.drawLinkImageView = true;
        }
        size2 = 0;
        if (!(this.titleLayout == null || this.titleLayout.getLineCount() == 0)) {
            size2 = 0 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
        }
        if (!(this.descriptionLayout == null || this.descriptionLayout.getLineCount() == 0)) {
            size2 += this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
        }
        if (!(this.descriptionLayout2 == null || this.descriptionLayout2.getLineCount() == 0)) {
            size2 += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1);
            if (this.descriptionLayout != null) {
                size2 += AndroidUtilities.dp(10.0f);
            }
        }
        int i4 = size2;
        for (int i5 = 0; i5 < this.linkLayout.size(); i5++) {
            staticLayout = (StaticLayout) this.linkLayout.get(i5);
            if (staticLayout.getLineCount() > 0) {
                i4 += staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
            }
        }
        if (obj != null) {
            i4 = Math.max(AndroidUtilities.dp(48.0f), i4);
        }
        this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), C0700C.ENCODING_PCM_32BIT));
        setMeasuredDimension(MeasureSpec.getSize(i), (this.needDivider ? 1 : 0) + Math.max(AndroidUtilities.dp(72.0f), i4 + AndroidUtilities.dp(16.0f)));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (this.message == null || this.linkLayout.isEmpty() || this.delegate == null || !this.delegate.canPerformActions()) {
            resetPressedLink();
            z = false;
        } else if (motionEvent.getAction() == 0 || (this.linkPreviewPressed && motionEvent.getAction() == 1)) {
            boolean z2;
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int i = 0;
            for (int i2 = 0; i2 < this.linkLayout.size(); i2++) {
                StaticLayout staticLayout = (StaticLayout) this.linkLayout.get(i2);
                if (staticLayout.getLineCount() > 0) {
                    int lineBottom = staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
                    int dp = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
                    if (((float) x) < ((float) dp) + staticLayout.getLineLeft(0) || ((float) x) > ((float) dp) + staticLayout.getLineWidth(0) || y < this.linkY + i || y > (this.linkY + i) + lineBottom) {
                        i += lineBottom;
                    } else {
                        if (motionEvent.getAction() == 0) {
                            resetPressedLink();
                            this.pressedLink = i2;
                            this.linkPreviewPressed = true;
                            try {
                                this.urlPath.setCurrentLayout(staticLayout, 0, 0.0f);
                                staticLayout.getSelectionPath(0, staticLayout.getText().length(), this.urlPath);
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                            z2 = true;
                            z = true;
                        } else if (this.linkPreviewPressed) {
                            try {
                                WebPage webPage = (this.pressedLink != 0 || this.message.messageOwner.media == null) ? null : this.message.messageOwner.media.webpage;
                                if (webPage == null || VERSION.SDK_INT < 16 || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                    Browser.openUrl(getContext(), (String) this.links.get(this.pressedLink));
                                    resetPressedLink();
                                    z2 = true;
                                    z = true;
                                } else {
                                    this.delegate.needOpenWebView(webPage);
                                    resetPressedLink();
                                    z2 = true;
                                    z = true;
                                }
                            } catch (Throwable e2) {
                                FileLog.m18e("tmessages", e2);
                            }
                        } else {
                            z2 = true;
                            z = false;
                        }
                        if (!z2) {
                            resetPressedLink();
                        }
                    }
                }
            }
            z2 = false;
            z = false;
            if (z2) {
                resetPressedLink();
            }
        } else {
            if (motionEvent.getAction() == 3) {
                resetPressedLink();
                z = false;
            }
            z = false;
        }
        return z || super.onTouchEvent(motionEvent);
    }

    protected void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        invalidate();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    public void setDelegate(SharedLinkCellDelegate sharedLinkCellDelegate) {
        this.delegate = sharedLinkCellDelegate;
    }

    public void setLink(MessageObject messageObject, boolean z) {
        this.needDivider = z;
        resetPressedLink();
        this.message = messageObject;
        requestLayout();
    }
}
