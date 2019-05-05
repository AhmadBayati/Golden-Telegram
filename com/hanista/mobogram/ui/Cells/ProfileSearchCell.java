package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;

public class ProfileSearchCell extends BaseCell {
    private static Drawable botDrawable;
    private static Drawable broadcastDrawable;
    private static Drawable checkDrawable;
    private static Drawable countDrawable;
    private static Drawable countDrawableGrey;
    private static TextPaint countPaint;
    private static Drawable groupDrawable;
    private static Paint linePaint;
    private static Drawable lockDrawable;
    private static TextPaint nameEncryptedPaint;
    private static TextPaint namePaint;
    private static TextPaint offlinePaint;
    private static TextPaint onlinePaint;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private Chat chat;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop;
    private int countWidth;
    private CharSequence currentName;
    private long dialog_id;
    public float drawAlpha;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private EncryptedChat encryptedChat;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int lastUnreadCount;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameTop;
    private StaticLayout onlineLayout;
    private int onlineLeft;
    private CharSequence subLabel;
    public boolean useSeparator;
    private User user;

    public ProfileSearchCell(Context context) {
        super(context);
        this.user = null;
        this.chat = null;
        this.encryptedChat = null;
        this.lastName = null;
        this.lastStatus = 0;
        this.lastAvatar = null;
        this.useSeparator = false;
        this.drawAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.countTop = AndroidUtilities.dp(25.0f);
        if (namePaint == null) {
            namePaint = new TextPaint(1);
            namePaint.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            namePaint.setTypeface(FontUtil.m1176a().m1160c());
            nameEncryptedPaint = new TextPaint(1);
            nameEncryptedPaint.setColor(-16734706);
            nameEncryptedPaint.setTypeface(FontUtil.m1176a().m1160c());
            onlinePaint = new TextPaint(1);
            onlinePaint.setColor(Theme.MSG_LINK_TEXT_COLOR);
            offlinePaint = new TextPaint(1);
            offlinePaint.setColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
            linePaint = new Paint();
            linePaint.setColor(-2302756);
            countPaint = new TextPaint(1);
            countPaint.setColor(-1);
            countPaint.setTypeface(FontUtil.m1176a().m1160c());
            broadcastDrawable = getResources().getDrawable(C0338R.drawable.list_broadcast);
            lockDrawable = getResources().getDrawable(C0338R.drawable.list_secret);
            groupDrawable = getResources().getDrawable(C0338R.drawable.list_group);
            countDrawable = getResources().getDrawable(C0338R.drawable.dialogs_badge);
            countDrawableGrey = getResources().getDrawable(C0338R.drawable.dialogs_badge2);
            checkDrawable = getResources().getDrawable(C0338R.drawable.check_list);
            botDrawable = getResources().getDrawable(C0338R.drawable.bot_list);
            initTheme();
        }
        namePaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        nameEncryptedPaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        onlinePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        offlinePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        countPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.avatarImage = new ImageReceiver(this);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            namePaint.setColor(AdvanceTheme.m2286c(AdvanceTheme.f2489Z, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR));
            countPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.aa));
            countPaint.setColor(AdvanceTheme.ab);
            countDrawable.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.ac, AdvanceTheme.f2491b), Mode.SRC_IN);
        }
    }

    public void buildLayout() {
        CharSequence charSequence;
        CharSequence charSequence2;
        int measuredWidth;
        int i;
        int i2;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameGroup = false;
        this.drawCheck = false;
        this.drawNameBot = false;
        if (this.encryptedChat != null) {
            this.drawNameLock = true;
            this.dialog_id = ((long) this.encryptedChat.id) << 32;
            if (LocaleController.isRTL) {
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - lockDrawable.getIntrinsicWidth();
                this.nameLeft = AndroidUtilities.dp(11.0f);
            } else {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + lockDrawable.getIntrinsicWidth();
            }
            this.nameLockTop = AndroidUtilities.dp(16.5f);
        } else if (this.chat != null) {
            if (this.chat.id < 0) {
                this.dialog_id = AndroidUtilities.makeBroadcastId(this.chat.id);
                this.drawNameBroadcast = true;
                this.nameLockTop = AndroidUtilities.dp(28.5f);
            } else {
                this.dialog_id = (long) (-this.chat.id);
                if (!ChatObject.isChannel(this.chat) || this.chat.megagroup) {
                    this.drawNameGroup = true;
                    this.nameLockTop = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                } else {
                    this.drawNameBroadcast = true;
                    this.nameLockTop = AndroidUtilities.dp(28.5f);
                }
            }
            this.drawCheck = this.chat.verified;
            if (LocaleController.isRTL) {
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - (this.drawNameGroup ? groupDrawable.getIntrinsicWidth() : broadcastDrawable.getIntrinsicWidth());
                this.nameLeft = AndroidUtilities.dp(11.0f);
            } else {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = (this.drawNameGroup ? groupDrawable.getIntrinsicWidth() : broadcastDrawable.getIntrinsicWidth()) + AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
            }
        } else {
            this.dialog_id = (long) this.user.id;
            if (LocaleController.isRTL) {
                this.nameLeft = AndroidUtilities.dp(11.0f);
            } else {
                this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            if (this.user.bot) {
                this.drawNameBot = true;
                if (LocaleController.isRTL) {
                    this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 2))) - botDrawable.getIntrinsicWidth();
                    this.nameLeft = AndroidUtilities.dp(11.0f);
                } else {
                    this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                    this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + botDrawable.getIntrinsicWidth();
                }
                this.nameLockTop = AndroidUtilities.dp(16.5f);
            } else {
                this.nameLockTop = AndroidUtilities.dp(17.0f);
            }
            this.drawCheck = this.user.verified;
        }
        if (this.currentName != null) {
            charSequence = this.currentName;
        } else {
            String str = TtmlNode.ANONYMOUS_REGION_ID;
            if (this.chat != null) {
                str = this.chat.title;
            } else if (this.user != null) {
                str = UserObject.getUserName(this.user);
            }
            charSequence = str.replace('\n', ' ');
        }
        if (charSequence.length() != 0) {
            charSequence2 = charSequence;
        } else if (this.user == null || this.user.phone == null || this.user.phone.length() == 0) {
            Object string = LocaleController.getString("HiddenName", C0338R.string.HiddenName);
        } else {
            charSequence2 = PhoneFormat.getInstance().format("+" + this.user.phone);
        }
        TextPaint textPaint = this.encryptedChat != null ? nameEncryptedPaint : namePaint;
        if (LocaleController.isRTL) {
            measuredWidth = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            i = measuredWidth;
        } else {
            measuredWidth = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f);
            i = measuredWidth;
        }
        int dp = this.drawNameLock ? measuredWidth - (AndroidUtilities.dp(6.0f) + lockDrawable.getIntrinsicWidth()) : this.drawNameBroadcast ? measuredWidth - (AndroidUtilities.dp(6.0f) + broadcastDrawable.getIntrinsicWidth()) : this.drawNameGroup ? measuredWidth - (AndroidUtilities.dp(6.0f) + groupDrawable.getIntrinsicWidth()) : this.drawNameBot ? measuredWidth - (AndroidUtilities.dp(6.0f) + botDrawable.getIntrinsicWidth()) : measuredWidth;
        if (this.drawCount) {
            TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (tL_dialog == null || tL_dialog.unread_count == 0) {
                this.lastUnreadCount = 0;
                this.countLayout = null;
            } else {
                this.lastUnreadCount = tL_dialog.unread_count;
                CharSequence format = String.format("%d", new Object[]{Integer.valueOf(tL_dialog.unread_count)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) countPaint.measureText(format)));
                this.countLayout = new StaticLayout(format, countPaint, this.countWidth, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                measuredWidth = this.countWidth + AndroidUtilities.dp(18.0f);
                dp -= measuredWidth;
                if (LocaleController.isRTL) {
                    this.countLeft = AndroidUtilities.dp(19.0f);
                    this.nameLeft = measuredWidth + this.nameLeft;
                } else {
                    this.countLeft = (getMeasuredWidth() - this.countWidth) - AndroidUtilities.dp(19.0f);
                }
            }
            i2 = dp;
        } else {
            this.lastUnreadCount = 0;
            this.countLayout = null;
            i2 = dp;
        }
        this.nameLayout = new StaticLayout(TextUtils.ellipsize(charSequence2, textPaint, (float) (i2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint, i2, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        if (this.chat == null || this.subLabel != null) {
            if (LocaleController.isRTL) {
                this.onlineLeft = AndroidUtilities.dp(11.0f);
            } else {
                this.onlineLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            charSequence = TtmlNode.ANONYMOUS_REGION_ID;
            TextPaint textPaint2 = offlinePaint;
            if (this.subLabel != null) {
                charSequence = this.subLabel;
            } else if (this.user != null) {
                if (this.user.bot) {
                    charSequence = LocaleController.getString("Bot", C0338R.string.Bot);
                } else {
                    charSequence = LocaleController.formatUserStatus(this.user);
                    if (this.user != null && (this.user.id == UserConfig.getClientUserId() || (this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance().getCurrentTime()))) {
                        textPaint2 = onlinePaint;
                        charSequence = LocaleController.getString("Online", C0338R.string.Online);
                    }
                }
            }
            this.onlineLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint2, (float) (i - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint2, i, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            this.nameTop = AndroidUtilities.dp(13.0f);
            if (!(this.subLabel == null || this.chat == null)) {
                this.nameLockTop -= AndroidUtilities.dp(12.0f);
            }
        } else {
            this.onlineLayout = null;
            this.nameTop = AndroidUtilities.dp(25.0f);
        }
        if (LocaleController.isRTL) {
            measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        } else {
            measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
        }
        this.avatarImage.setImageCoords(measuredWidth, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        double ceil;
        if (LocaleController.isRTL) {
            if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineLeft(0) == 0.0f) {
                ceil = Math.ceil((double) this.nameLayout.getLineWidth(0));
                if (ceil < ((double) i2)) {
                    this.nameLeft = (int) ((((double) i2) - ceil) + ((double) this.nameLeft));
                }
            }
            if (this.onlineLayout != null && this.onlineLayout.getLineCount() > 0 && this.onlineLayout.getLineLeft(0) == 0.0f) {
                ceil = Math.ceil((double) this.onlineLayout.getLineWidth(0));
                if (ceil < ((double) i)) {
                    this.onlineLeft = (int) ((((double) i) - ceil) + ((double) this.onlineLeft));
                    return;
                }
                return;
            }
            return;
        }
        if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineRight(0) == ((float) i2)) {
            ceil = Math.ceil((double) this.nameLayout.getLineWidth(0));
            if (ceil < ((double) i2)) {
                this.nameLeft = (int) (((double) this.nameLeft) - (((double) i2) - ceil));
            }
        }
        if (this.onlineLayout != null && this.onlineLayout.getLineCount() > 0 && this.onlineLayout.getLineRight(0) == ((float) i)) {
            ceil = Math.ceil((double) this.onlineLayout.getLineWidth(0));
            if (ceil < ((double) i)) {
                this.onlineLeft = (int) (((double) this.onlineLeft) - (((double) i) - ceil));
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    protected void onDraw(Canvas canvas) {
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), linePaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), linePaint);
                }
            }
            if (this.drawAlpha != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                canvas.saveLayerAlpha(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), (int) (255.0f * this.drawAlpha), 4);
            }
            if (this.drawNameLock) {
                setDrawableBounds(lockDrawable, this.nameLockLeft, this.nameLockTop);
                lockDrawable.draw(canvas);
            } else if (this.drawNameGroup) {
                setDrawableBounds(groupDrawable, this.nameLockLeft, this.nameLockTop);
                groupDrawable.draw(canvas);
            } else if (this.drawNameBroadcast) {
                setDrawableBounds(broadcastDrawable, this.nameLockLeft, this.nameLockTop);
                broadcastDrawable.draw(canvas);
            } else if (this.drawNameBot) {
                setDrawableBounds(botDrawable, this.nameLockLeft, this.nameLockTop);
                botDrawable.draw(canvas);
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) this.nameTop);
                this.nameLayout.draw(canvas);
                canvas.restore();
                if (this.drawCheck) {
                    if (LocaleController.isRTL) {
                        setDrawableBounds(checkDrawable, (this.nameLeft - AndroidUtilities.dp(4.0f)) - checkDrawable.getIntrinsicWidth(), this.nameLockTop);
                    } else {
                        setDrawableBounds(checkDrawable, (this.nameLeft + ((int) this.nameLayout.getLineWidth(0))) + AndroidUtilities.dp(4.0f), this.nameLockTop);
                    }
                    checkDrawable.draw(canvas);
                }
            }
            if (this.onlineLayout != null) {
                canvas.save();
                canvas.translate((float) this.onlineLeft, (float) AndroidUtilities.dp(40.0f));
                this.onlineLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countLayout != null) {
                if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                    setDrawableBounds(countDrawableGrey, this.countLeft - AndroidUtilities.dp(5.5f), this.countTop, AndroidUtilities.dp(11.0f) + this.countWidth, countDrawableGrey.getIntrinsicHeight());
                    countDrawableGrey.draw(canvas);
                } else {
                    setDrawableBounds(countDrawable, this.countLeft - AndroidUtilities.dp(5.5f), this.countTop, AndroidUtilities.dp(11.0f) + this.countWidth, countDrawable.getIntrinsicHeight());
                    countDrawable.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            this.avatarImage.draw(canvas);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.user == null && this.chat == null && this.encryptedChat == null) {
            super.onLayout(z, i, i2, i3, i4);
        } else if (z) {
            buildLayout();
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(72.0f));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setData(TLObject tLObject, EncryptedChat encryptedChat, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        this.currentName = charSequence;
        if (tLObject instanceof User) {
            this.user = (User) tLObject;
            this.chat = null;
        } else if (tLObject instanceof Chat) {
            this.chat = (Chat) tLObject;
            this.user = null;
        }
        this.encryptedChat = encryptedChat;
        this.subLabel = charSequence2;
        this.drawCount = z;
        update(0);
    }

    public void update(int i) {
        TLObject tLObject;
        TLObject tLObject2;
        if (this.user != null) {
            tLObject2 = this.user.photo != null ? this.user.photo.photo_small : null;
            this.avatarDrawable.setInfo(this.user);
            tLObject = tLObject2;
        } else if (this.chat != null) {
            tLObject2 = this.chat.photo != null ? this.chat.photo.photo_small : null;
            this.avatarDrawable.setInfo(this.chat);
            tLObject = tLObject2;
        } else {
            this.avatarDrawable.setInfo(0, null, null, false);
            tLObject = null;
        }
        if (i != 0) {
            boolean z;
            boolean z2 = !(((i & 2) == 0 || this.user == null) && ((i & 8) == 0 || this.chat == null)) && ((this.lastAvatar != null && tLObject == null) || !(this.lastAvatar != null || tLObject == null || this.lastAvatar == null || tLObject == null || (this.lastAvatar.volume_id == tLObject.volume_id && this.lastAvatar.local_id == tLObject.local_id)));
            if (!(z2 || (i & 4) == 0 || this.user == null)) {
                if ((this.user.status != null ? this.user.status.expires : 0) != this.lastStatus) {
                    z2 = true;
                }
            }
            if (!((z2 || (i & 1) == 0 || this.user == null) && ((i & 16) == 0 || this.chat == null))) {
                if (!(this.user != null ? this.user.first_name + this.user.last_name : this.chat.title).equals(this.lastName)) {
                    z2 = true;
                }
            }
            if (!(z2 || !this.drawCount || (i & TLRPC.USER_FLAG_UNUSED2) == 0)) {
                TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
                if (!(tL_dialog == null || tL_dialog.unread_count == this.lastUnreadCount)) {
                    z = true;
                    if (!z) {
                        return;
                    }
                }
            }
            z = z2;
            if (z) {
                return;
            }
        }
        if (this.user != null) {
            if (this.user.status != null) {
                this.lastStatus = this.user.status.expires;
            } else {
                this.lastStatus = 0;
            }
            this.lastName = this.user.first_name + this.user.last_name;
        } else if (this.chat != null) {
            this.lastName = this.chat.title;
        }
        this.lastAvatar = tLObject;
        this.avatarImage.setImage(tLObject, "50_50", this.avatarDrawable, null, false);
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        postInvalidate();
    }
}
