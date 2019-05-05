package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.PhotoViewer;

public class ChatActionCell extends BaseCell {
    private static Paint backPaint;
    private static TextPaint textPaint;
    private AvatarDrawable avatarDrawable;
    private MessageObject currentMessageObject;
    private int dateBubbleColor;
    private ChatActionCellDelegate delegate;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private URLSpan pressedLink;
    private int previousWidth;
    private int textHeight;
    private StaticLayout textLayout;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;

    public interface ChatActionCellDelegate {
        void didClickedImage(ChatActionCell chatActionCell);

        void didLongPressed(ChatActionCell chatActionCell);

        void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton);

        void didPressedReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenUserProfile(int i);
    }

    public ChatActionCell(Context context) {
        super(context);
        this.textWidth = 0;
        this.textHeight = 0;
        this.textX = 0;
        this.textY = 0;
        this.textXLeft = 0;
        this.previousWidth = 0;
        this.imagePressed = false;
        if (textPaint == null) {
            textPaint = new TextPaint(1);
            textPaint.setColor(-1);
            textPaint.linkColor = -1;
            textPaint.setTypeface(FontUtil.m1176a().m1160c());
            backPaint = new Paint(1);
        }
        backPaint.setColor(ApplicationLoader.getServiceMessageColor());
        initThemeBackPaint();
        this.imageReceiver = new ImageReceiver(this);
        this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable = new AvatarDrawable();
        textPaint.setTextSize((float) AndroidUtilities.dp((float) (MessagesController.getInstance().fontSize - 2)));
        initThemePhoto();
    }

    private int findMaxWidthAroundLine(int i) {
        int i2;
        int ceil = (int) Math.ceil((double) this.textLayout.getLineWidth(i));
        int lineCount = this.textLayout.getLineCount();
        for (i2 = i + 1; i2 < lineCount; i2++) {
            int ceil2 = (int) Math.ceil((double) this.textLayout.getLineWidth(i2));
            if (Math.abs(ceil2 - ceil) >= AndroidUtilities.dp(12.0f)) {
                break;
            }
            ceil = Math.max(ceil2, ceil);
        }
        for (i2 = i - 1; i2 >= 0; i2--) {
            lineCount = (int) Math.ceil((double) this.textLayout.getLineWidth(i2));
            if (Math.abs(lineCount - ceil) >= AndroidUtilities.dp(12.0f)) {
                break;
            }
            ceil = Math.max(lineCount, ceil);
        }
        return ceil;
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int c = AdvanceTheme.m2286c(AdvanceTheme.bV, -1);
            textPaint.setColor(c);
            if (c != -1) {
                textPaint.linkColor = AdvanceTheme.m2276a(AdvanceTheme.bV, -80);
            }
            textPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.m2286c(AdvanceTheme.bW, MessagesController.getInstance().fontSize - 2)));
        }
    }

    private void initThemeBackPaint() {
        if (ThemeUtil.m2490b()) {
            this.dateBubbleColor = AdvanceTheme.m2286c(AdvanceTheme.bn, ApplicationLoader.getServiceMessageColor());
            backPaint.setColor(this.dateBubbleColor);
        }
    }

    private void initThemePhoto() {
        if (ThemeUtil.m2490b()) {
            int dp = AndroidUtilities.dp((float) AdvanceTheme.by);
            this.imageReceiver.setRoundRadius(dp);
            this.avatarDrawable.setRadius(dp);
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public ImageReceiver getPhotoImage() {
        return this.imageReceiver;
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentMessageObject != null) {
            if (this.currentMessageObject.type == 11) {
                this.imageReceiver.draw(canvas);
            }
            initTheme();
            if (this.textLayout != null) {
                int lineCount = this.textLayout.getLineCount();
                int dp = AndroidUtilities.dp(6.0f);
                int dp2 = AndroidUtilities.dp(7.0f);
                int i = 0;
                int i2 = 0;
                while (i < lineCount) {
                    int findMaxWidthAroundLine;
                    int measuredWidth;
                    int i3;
                    int i4;
                    Object obj;
                    int findMaxWidthAroundLine2 = findMaxWidthAroundLine(i);
                    int measuredWidth2 = ((getMeasuredWidth() - findMaxWidthAroundLine2) / 2) - AndroidUtilities.dp(3.0f);
                    int dp3 = findMaxWidthAroundLine2 + AndroidUtilities.dp(6.0f);
                    int lineBottom = this.textLayout.getLineBottom(i);
                    i2 = lineBottom - i2;
                    int i5 = 0;
                    Object obj2 = i == lineCount + -1 ? 1 : null;
                    Object obj3 = i == 0 ? 1 : null;
                    if (obj3 != null) {
                        dp2 -= AndroidUtilities.dp(3.0f);
                        i2 += AndroidUtilities.dp(3.0f);
                    }
                    int dp4 = obj2 != null ? i2 + AndroidUtilities.dp(3.0f) : i2;
                    canvas.drawRect((float) measuredWidth2, (float) dp2, (float) (measuredWidth2 + dp3), (float) (dp2 + dp4), backPaint);
                    if (obj2 == null && i + 1 < lineCount) {
                        findMaxWidthAroundLine = findMaxWidthAroundLine(i + 1) + AndroidUtilities.dp(6.0f);
                        if ((dp * 2) + findMaxWidthAroundLine < dp3) {
                            measuredWidth = (getMeasuredWidth() - findMaxWidthAroundLine) / 2;
                            obj2 = 1;
                            i5 = AndroidUtilities.dp(3.0f);
                            canvas.drawRect((float) measuredWidth2, (float) (dp2 + dp4), (float) measuredWidth, (float) ((dp2 + dp4) + AndroidUtilities.dp(3.0f)), backPaint);
                            canvas.drawRect((float) (measuredWidth + findMaxWidthAroundLine), (float) (dp2 + dp4), (float) (measuredWidth2 + dp3), (float) ((dp2 + dp4) + AndroidUtilities.dp(3.0f)), backPaint);
                        } else if ((dp * 2) + dp3 < findMaxWidthAroundLine) {
                            i2 = AndroidUtilities.dp(3.0f);
                            findMaxWidthAroundLine2 = (dp2 + dp4) - AndroidUtilities.dp(9.0f);
                            if (ThemeUtil.m2490b()) {
                                Theme.cornerInner[2].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                                Theme.cornerInner[3].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                            }
                            i3 = measuredWidth2 - (dp * 2);
                            Theme.cornerInner[2].setBounds(i3, findMaxWidthAroundLine2, i3 + dp, findMaxWidthAroundLine2 + dp);
                            Theme.cornerInner[2].draw(canvas);
                            i3 = (measuredWidth2 + dp3) + dp;
                            Theme.cornerInner[3].setBounds(i3, findMaxWidthAroundLine2, i3 + dp, findMaxWidthAroundLine2 + dp);
                            Theme.cornerInner[3].draw(canvas);
                            i5 = i2;
                        } else {
                            i5 = AndroidUtilities.dp(6.0f);
                        }
                    }
                    if (obj3 != null || i <= 0) {
                        Object obj4 = obj3;
                        i4 = dp4;
                        obj = obj4;
                    } else {
                        findMaxWidthAroundLine = findMaxWidthAroundLine(i - 1) + AndroidUtilities.dp(6.0f);
                        if ((dp * 2) + findMaxWidthAroundLine < dp3) {
                            measuredWidth = (getMeasuredWidth() - findMaxWidthAroundLine) / 2;
                            dp2 -= AndroidUtilities.dp(3.0f);
                            dp4 += AndroidUtilities.dp(3.0f);
                            canvas.drawRect((float) measuredWidth2, (float) dp2, (float) measuredWidth, (float) (AndroidUtilities.dp(3.0f) + dp2), backPaint);
                            canvas.drawRect((float) (measuredWidth + findMaxWidthAroundLine), (float) dp2, (float) (measuredWidth2 + dp3), (float) (AndroidUtilities.dp(3.0f) + dp2), backPaint);
                            i4 = dp4;
                            obj = 1;
                        } else if ((dp * 2) + dp3 < findMaxWidthAroundLine) {
                            i2 = dp2 - AndroidUtilities.dp(3.0f);
                            dp4 += AndroidUtilities.dp(3.0f);
                            findMaxWidthAroundLine2 = i2 + dp;
                            if (ThemeUtil.m2490b()) {
                                Theme.cornerInner[0].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                                Theme.cornerInner[1].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                            }
                            i3 = measuredWidth2 - (dp * 2);
                            Theme.cornerInner[0].setBounds(i3, findMaxWidthAroundLine2, i3 + dp, findMaxWidthAroundLine2 + dp);
                            Theme.cornerInner[0].draw(canvas);
                            i3 = (measuredWidth2 + dp3) + dp;
                            Theme.cornerInner[1].setBounds(i3, findMaxWidthAroundLine2, i3 + dp, findMaxWidthAroundLine2 + dp);
                            Theme.cornerInner[1].draw(canvas);
                            dp2 = i2;
                            r20 = dp4;
                            obj = obj3;
                            i4 = r20;
                        } else {
                            dp2 -= AndroidUtilities.dp(6.0f);
                            r20 = dp4 + AndroidUtilities.dp(6.0f);
                            obj = obj3;
                            i4 = r20;
                        }
                    }
                    canvas.drawRect((float) (measuredWidth2 - dp), (float) (dp2 + dp), (float) measuredWidth2, (float) (((dp2 + i4) + i5) - dp), backPaint);
                    canvas.drawRect((float) (measuredWidth2 + dp3), (float) (dp2 + dp), (float) ((measuredWidth2 + dp3) + dp), (float) (((dp2 + i4) + i5) - dp), backPaint);
                    if (obj != null) {
                        if (ThemeUtil.m2490b()) {
                            Theme.cornerOuter[0].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                            Theme.cornerOuter[1].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                        }
                        i2 = measuredWidth2 - dp;
                        Theme.cornerOuter[0].setBounds(i2, dp2, i2 + dp, dp2 + dp);
                        Theme.cornerOuter[0].draw(canvas);
                        i2 = measuredWidth2 + dp3;
                        Theme.cornerOuter[1].setBounds(i2, dp2, i2 + dp, dp2 + dp);
                        Theme.cornerOuter[1].draw(canvas);
                    }
                    if (obj2 != null) {
                        i2 = ((dp2 + i4) + i5) - dp;
                        if (ThemeUtil.m2490b()) {
                            Theme.cornerOuter[2].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                            Theme.cornerOuter[3].setColorFilter(this.dateBubbleColor, Mode.SRC_IN);
                        }
                        findMaxWidthAroundLine2 = measuredWidth2 + dp3;
                        Theme.cornerOuter[2].setBounds(findMaxWidthAroundLine2, i2, findMaxWidthAroundLine2 + dp, i2 + dp);
                        Theme.cornerOuter[2].draw(canvas);
                        findMaxWidthAroundLine2 = measuredWidth2 - dp;
                        Theme.cornerOuter[3].setBounds(findMaxWidthAroundLine2, i2, findMaxWidthAroundLine2 + dp, i2 + dp);
                        Theme.cornerOuter[3].draw(canvas);
                    }
                    dp2 += i4;
                    i++;
                    i2 = lineBottom;
                }
                canvas.save();
                canvas.translate((float) this.textXLeft, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    protected void onLongPress() {
        if (this.delegate != null) {
            this.delegate.didLongPressed(this);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onMeasure(int r14, int r15) {
        /*
        r13 = this;
        r12 = 11;
        r2 = 1106247680; // 0x41f00000 float:30.0 double:5.465589745E-315;
        r9 = 1115684864; // 0x42800000 float:64.0 double:5.51221563E-315;
        r7 = 0;
        r0 = r13.currentMessageObject;
        if (r0 != 0) goto L_0x001c;
    L_0x000b:
        r0 = android.view.View.MeasureSpec.getSize(r14);
        r1 = r13.textHeight;
        r2 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        r13.setMeasuredDimension(r0, r1);
    L_0x001b:
        return;
    L_0x001c:
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r1 = android.view.View.MeasureSpec.getSize(r14);
        r8 = java.lang.Math.max(r0, r1);
        r0 = r13.previousWidth;
        if (r8 == r0) goto L_0x00d6;
    L_0x002c:
        r13.previousWidth = r8;
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r3 = r8 - r0;
        r0 = new android.text.StaticLayout;
        r1 = r13.currentMessageObject;
        r1 = r1.messageText;
        r2 = textPaint;
        r4 = android.text.Layout.Alignment.ALIGN_CENTER;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = 0;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        r13.textLayout = r0;
        r13.textHeight = r7;
        r13.textWidth = r7;
        r0 = r13.textLayout;	 Catch:{ Exception -> 0x008f }
        r2 = r0.getLineCount();	 Catch:{ Exception -> 0x008f }
        r1 = r7;
    L_0x0051:
        if (r1 >= r2) goto L_0x0096;
    L_0x0053:
        r0 = r13.textLayout;	 Catch:{ Exception -> 0x0087 }
        r0 = r0.getLineWidth(r1);	 Catch:{ Exception -> 0x0087 }
        r4 = (float) r3;	 Catch:{ Exception -> 0x0087 }
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 <= 0) goto L_0x005f;
    L_0x005e:
        r0 = (float) r3;	 Catch:{ Exception -> 0x0087 }
    L_0x005f:
        r4 = r13.textHeight;	 Catch:{ Exception -> 0x0087 }
        r4 = (double) r4;	 Catch:{ Exception -> 0x0087 }
        r6 = r13.textLayout;	 Catch:{ Exception -> 0x0087 }
        r6 = r6.getLineBottom(r1);	 Catch:{ Exception -> 0x0087 }
        r10 = (double) r6;	 Catch:{ Exception -> 0x0087 }
        r10 = java.lang.Math.ceil(r10);	 Catch:{ Exception -> 0x0087 }
        r4 = java.lang.Math.max(r4, r10);	 Catch:{ Exception -> 0x0087 }
        r4 = (int) r4;	 Catch:{ Exception -> 0x0087 }
        r13.textHeight = r4;	 Catch:{ Exception -> 0x0087 }
        r4 = r13.textWidth;	 Catch:{ Exception -> 0x008f }
        r4 = (double) r4;	 Catch:{ Exception -> 0x008f }
        r10 = (double) r0;	 Catch:{ Exception -> 0x008f }
        r10 = java.lang.Math.ceil(r10);	 Catch:{ Exception -> 0x008f }
        r4 = java.lang.Math.max(r4, r10);	 Catch:{ Exception -> 0x008f }
        r0 = (int) r4;	 Catch:{ Exception -> 0x008f }
        r13.textWidth = r0;	 Catch:{ Exception -> 0x008f }
        r0 = r1 + 1;
        r1 = r0;
        goto L_0x0051;
    L_0x0087:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);	 Catch:{ Exception -> 0x008f }
        goto L_0x001b;
    L_0x008f:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
    L_0x0096:
        r0 = r13.textWidth;
        r0 = r8 - r0;
        r0 = r0 / 2;
        r13.textX = r0;
        r0 = 1088421888; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r0);
        r13.textY = r0;
        r0 = r13.textLayout;
        r0 = r0.getWidth();
        r0 = r8 - r0;
        r0 = r0 / 2;
        r13.textXLeft = r0;
        r0 = r13.currentMessageObject;
        r0 = r0.type;
        if (r0 != r12) goto L_0x00d6;
    L_0x00b8:
        r0 = r13.imageReceiver;
        r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);
        r1 = r8 - r1;
        r1 = r1 / 2;
        r2 = r13.textHeight;
        r3 = 1097859072; // 0x41700000 float:15.0 double:5.424144515E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);
        r0.setImageCoords(r1, r2, r3, r4);
    L_0x00d6:
        r0 = r13.textHeight;
        r1 = r13.currentMessageObject;
        r1 = r1.type;
        if (r1 != r12) goto L_0x00e0;
    L_0x00de:
        r7 = 70;
    L_0x00e0:
        r1 = r7 + 14;
        r1 = (float) r1;
        r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
        r0 = r0 + r1;
        r13.setMeasuredDimension(r8, r0);
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.Cells.ChatActionCell.onMeasure(int, int):void");
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        boolean z2;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.delegate != null) {
                if (this.currentMessageObject.type == 11 && this.imageReceiver.isInsideImage(x, y)) {
                    this.imagePressed = true;
                    z = true;
                } else {
                    z = false;
                }
                if (z) {
                    startCheckLongPress();
                }
            }
            z = false;
        } else {
            if (motionEvent.getAction() != 2) {
                cancelCheckLongPress();
            }
            if (this.imagePressed) {
                if (motionEvent.getAction() == 1) {
                    this.imagePressed = false;
                    if (this.delegate != null) {
                        this.delegate.didClickedImage(this);
                        playSoundEffect(0);
                        z = false;
                    }
                } else if (motionEvent.getAction() == 3) {
                    this.imagePressed = false;
                    z = false;
                } else if (motionEvent.getAction() == 2 && !this.imageReceiver.isInsideImage(x, y)) {
                    this.imagePressed = false;
                }
            }
            z = false;
        }
        if (!z && (motionEvent.getAction() == 0 || (this.pressedLink != null && motionEvent.getAction() == 1))) {
            if (x < ((float) this.textX) || y < ((float) this.textY) || x > ((float) (this.textX + this.textWidth)) || y > ((float) (this.textY + this.textHeight))) {
                this.pressedLink = null;
            } else {
                x -= (float) this.textXLeft;
                int lineForVertical = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                int offsetForHorizontal = this.textLayout.getOffsetForHorizontal(lineForVertical, x);
                float lineLeft = this.textLayout.getLineLeft(lineForVertical);
                if (lineLeft > x || this.textLayout.getLineWidth(lineForVertical) + lineLeft < x || !(this.currentMessageObject.messageText instanceof Spannable)) {
                    this.pressedLink = null;
                } else {
                    URLSpan[] uRLSpanArr = (URLSpan[]) ((Spannable) this.currentMessageObject.messageText).getSpans(offsetForHorizontal, offsetForHorizontal, URLSpan.class);
                    if (uRLSpanArr.length != 0) {
                        if (motionEvent.getAction() == 0) {
                            this.pressedLink = uRLSpanArr[0];
                            z2 = true;
                        } else if (uRLSpanArr[0] == this.pressedLink) {
                            if (this.delegate != null) {
                                String url = uRLSpanArr[0].getURL();
                                if (url.startsWith("game")) {
                                    this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                                } else {
                                    this.delegate.needOpenUserProfile(Integer.parseInt(url));
                                }
                            }
                            z2 = true;
                        }
                        return z2 ? super.onTouchEvent(motionEvent) : z2;
                    } else {
                        this.pressedLink = null;
                    }
                }
            }
        }
        z2 = z;
        if (z2) {
        }
    }

    public void setDelegate(ChatActionCellDelegate chatActionCellDelegate) {
        this.delegate = chatActionCellDelegate;
    }

    public void setMessageObject(MessageObject messageObject) {
        boolean z = true;
        if (this.currentMessageObject != messageObject || (!this.hasReplyMessage && messageObject.replyMessageObject != null)) {
            this.currentMessageObject = messageObject;
            this.hasReplyMessage = messageObject.replyMessageObject != null;
            this.previousWidth = 0;
            if (this.currentMessageObject.type == 11) {
                int i;
                if (messageObject.messageOwner.to_id == null) {
                    i = 0;
                } else if (messageObject.messageOwner.to_id.chat_id != 0) {
                    i = messageObject.messageOwner.to_id.chat_id;
                } else if (messageObject.messageOwner.to_id.channel_id != 0) {
                    i = messageObject.messageOwner.to_id.channel_id;
                } else {
                    i = messageObject.messageOwner.to_id.user_id;
                    if (i == UserConfig.getClientUserId()) {
                        i = messageObject.messageOwner.from_id;
                    }
                }
                this.avatarDrawable.setInfo(i, null, null, false);
                if (this.currentMessageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage(this.currentMessageObject.messageOwner.action.newUserPhoto.photo_small, "50_50", this.avatarDrawable, null, false);
                } else {
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0f));
                    if (closestPhotoSizeWithSize != null) {
                        this.imageReceiver.setImage(closestPhotoSizeWithSize.location, "50_50", this.avatarDrawable, null, false);
                    } else {
                        this.imageReceiver.setImageBitmap(this.avatarDrawable);
                    }
                }
                ImageReceiver imageReceiver = this.imageReceiver;
                if (PhotoViewer.getInstance().isShowingImage(this.currentMessageObject)) {
                    z = false;
                }
                imageReceiver.setVisible(z, false);
            } else {
                this.imageReceiver.setImageBitmap((Bitmap) null);
            }
            requestLayout();
        }
    }
}
