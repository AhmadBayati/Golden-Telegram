package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.support.v4.view.PointerIconCompat;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.query.DraftQuery;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.dialogdm.DialogDm;
import com.hanista.mobogram.mobo.dialogdm.DialogDmUtil;
import com.hanista.mobogram.mobo.p003d.ImageListActivity;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p019r.TabsUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.DraftMessage;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatRequested;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatWaiting;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageService;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import java.util.ArrayList;

public class DialogCell extends BaseCell {
    private static Paint backPaint;
    private static Drawable botDrawable;
    private static Drawable broadcastDrawable;
    private static Drawable checkDrawable;
    private static Drawable clockDrawable;
    private static Drawable countDrawable;
    private static Drawable countDrawableGrey;
    private static TextPaint countPaint;
    private static TextPaint docTypePaint;
    private static Drawable errorDrawable;
    private static Drawable groupDrawable;
    private static TextPaint groupPaint;
    private static Drawable halfCheckDrawable;
    private static Paint linePaint;
    private static Drawable lockDrawable;
    private static TextPaint mediaPaint;
    private static TextPaint messageCountPaint;
    private static TextPaint messagePaint;
    private static TextPaint messagePrintingPaint;
    private static TextPaint messageTypingPaint;
    private static Drawable muteDrawable;
    private static TextPaint nameEncryptedPaint;
    private static TextPaint namePaint;
    private static TextPaint nameUnknownPaint;
    private static Drawable superGroupDrawable;
    private static TextPaint timePaint;
    private static Drawable verifiedDrawable;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private int avatarTop;
    private long categoryId;
    private Chat chat;
    private int checkDrawLeft;
    private int checkDrawTop;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop;
    private int countWidth;
    private long currentDialogId;
    private int currentEditDate;
    private DialogDm dialogDm;
    private boolean dialogMuted;
    private int dialogsType;
    private StaticLayout docTypeLayout;
    private int docTypeLeft;
    private DraftMessage draftMessage;
    private boolean drawCheck1;
    private boolean drawCheck2;
    private boolean drawClock;
    private boolean drawCount;
    private boolean drawError;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawStatus;
    private boolean drawVerified;
    private EncryptedChat encryptedChat;
    private int errorLeft;
    private int errorTop;
    private int halfCheckDrawLeft;
    private boolean hiddenMode;
    private int index;
    private boolean isDialogCell;
    private boolean isSelected;
    private int lastMessageDate;
    private CharSequence lastPrintString;
    private int lastSendState;
    private String lastStatus;
    private boolean lastUnreadState;
    private MessageObject message;
    private StaticLayout messageCountLayout;
    private int messageCountLeft;
    private StaticLayout messageLayout;
    private int messageLeft;
    private int messageTop;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
    private GradientDrawable statusBG;
    private StaticLayout timeLayout;
    private int timeLeft;
    private int timeTop;
    private int unreadCount;
    public boolean useSeparator;
    private User user;

    public DialogCell(Context context) {
        super(context);
        this.user = null;
        this.chat = null;
        this.encryptedChat = null;
        this.lastPrintString = null;
        this.useSeparator = false;
        this.timeTop = AndroidUtilities.dp(17.0f);
        this.checkDrawTop = AndroidUtilities.dp(18.0f);
        this.messageTop = AndroidUtilities.dp(40.0f);
        this.errorTop = AndroidUtilities.dp(39.0f);
        this.countTop = AndroidUtilities.dp(39.0f);
        this.avatarTop = AndroidUtilities.dp(10.0f);
        this.lastStatus = TtmlNode.ANONYMOUS_REGION_ID;
        if (namePaint == null) {
            namePaint = new TextPaint(1);
            namePaint.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            namePaint.setTypeface(FontUtil.m1176a().m1160c());
            nameEncryptedPaint = new TextPaint(1);
            nameEncryptedPaint.setColor(-16734706);
            nameEncryptedPaint.setTypeface(FontUtil.m1176a().m1160c());
            messagePaint = new TextPaint(1);
            messagePaint.setTypeface(FontUtil.m1176a().m1161d());
            messagePaint.setColor(Theme.DIALOGS_MESSAGE_TEXT_COLOR);
            messagePaint.linkColor = Theme.DIALOGS_MESSAGE_TEXT_COLOR;
            linePaint = new Paint();
            linePaint.setColor(-2302756);
            backPaint = new Paint();
            backPaint.setColor(251658240);
            messagePrintingPaint = new TextPaint(1);
            messagePrintingPaint.setTypeface(FontUtil.m1176a().m1161d());
            messagePrintingPaint.setColor(ThemeUtil.m2485a().m2289c());
            timePaint = new TextPaint(1);
            timePaint.setTypeface(FontUtil.m1176a().m1161d());
            timePaint.setColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
            countPaint = new TextPaint(1);
            countPaint.setColor(-1);
            countPaint.setTypeface(FontUtil.m1176a().m1160c());
            lockDrawable = getResources().getDrawable(C0338R.drawable.list_secret);
            checkDrawable = getResources().getDrawable(C0338R.drawable.dialogs_check);
            halfCheckDrawable = getResources().getDrawable(C0338R.drawable.dialogs_halfcheck);
            clockDrawable = getResources().getDrawable(C0338R.drawable.msg_clock);
            errorDrawable = getResources().getDrawable(C0338R.drawable.dialogs_warning);
            countDrawable = getResources().getDrawable(C0338R.drawable.dialogs_badge);
            countDrawableGrey = getResources().getDrawable(C0338R.drawable.dialogs_badge2);
            groupDrawable = getResources().getDrawable(C0338R.drawable.list_group);
            superGroupDrawable = getResources().getDrawable(C0338R.drawable.list_supergroup);
            broadcastDrawable = getResources().getDrawable(C0338R.drawable.list_broadcast);
            muteDrawable = getResources().getDrawable(C0338R.drawable.mute_grey);
            verifiedDrawable = getResources().getDrawable(C0338R.drawable.check_list);
            botDrawable = getResources().getDrawable(C0338R.drawable.bot_list);
            docTypePaint = new TextPaint(1);
            docTypePaint.setTypeface(FontUtil.m1176a().m1161d());
            docTypePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            docTypePaint.setColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            messageCountPaint = new TextPaint(1);
            messageCountPaint.setTypeface(FontUtil.m1176a().m1161d());
            messageCountPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            messageCountPaint.setColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            mediaPaint = new TextPaint(1);
            mediaPaint.setTypeface(FontUtil.m1176a().m1161d());
            mediaPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            mediaPaint.setColor(-16711936);
            messageTypingPaint = new TextPaint(1);
            messageTypingPaint.setTypeface(FontUtil.m1176a().m1161d());
            messageTypingPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            messageTypingPaint.setColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            nameUnknownPaint = new TextPaint(1);
            nameUnknownPaint.setTextSize((float) AndroidUtilities.dp(17.0f));
            nameUnknownPaint.setColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
            nameUnknownPaint.setTypeface(FontUtil.m1176a().m1160c());
            groupPaint = new TextPaint(1);
            groupPaint.setTextSize((float) AndroidUtilities.dp(17.0f));
            groupPaint.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            groupPaint.setTypeface(FontUtil.m1176a().m1160c());
        }
        namePaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        nameEncryptedPaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        messagePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        messagePrintingPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        timePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        countPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        setBackgroundResource(ThemeUtil.m2485a().m2293g());
        this.avatarImage = new ImageReceiver(this);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.statusBG = new GradientDrawable();
        this.statusBG.setColor(-7829368);
        this.statusBG.setCornerRadius((float) AndroidUtilities.dp(16.0f));
        this.statusBG.setStroke(AndroidUtilities.dp(2.0f), -1);
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2491b;
            int a = AdvanceTheme.m2276a(AdvanceTheme.f2491b, 21);
            int c = AdvanceTheme.m2286c(AdvanceTheme.f2489Z, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            namePaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ao));
            namePaint.setColor(c);
            groupPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.aq));
            groupPaint.setColor(AdvanceTheme.m2286c(AdvanceTheme.aj, c));
            lockDrawable.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.al, AdvanceTheme.m2286c(AdvanceTheme.f2489Z, i)), Mode.SRC_IN);
            nameEncryptedPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ao));
            nameEncryptedPaint.setColor(AdvanceTheme.m2286c(AdvanceTheme.f2489Z, a));
            nameUnknownPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ao));
            nameUnknownPaint.setColor(AdvanceTheme.ap);
            messagePaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ar));
            messagePaint.setColor(AdvanceTheme.m2286c(AdvanceTheme.am, Theme.DIALOGS_MESSAGE_TEXT_COLOR));
            messagePrintingPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ar));
            messagePrintingPaint.setColor(AdvanceTheme.m2286c(AdvanceTheme.am, i));
            mediaPaint.setColor(AdvanceTheme.m2286c(AdvanceTheme.ah, a));
            messageTypingPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ar));
            messageTypingPaint.setColor(AdvanceTheme.af);
            timePaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.ag));
            timePaint.setColor(AdvanceTheme.ae);
            countPaint.setTextSize((float) AndroidUtilities.dp((float) AdvanceTheme.aa));
            countPaint.setColor(AdvanceTheme.ab);
            checkDrawable.setColorFilter(AdvanceTheme.ad, Mode.SRC_IN);
            halfCheckDrawable.setColorFilter(AdvanceTheme.ad, Mode.SRC_IN);
            clockDrawable.setColorFilter(AdvanceTheme.ad, Mode.SRC_IN);
            countDrawable.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.ac, i), Mode.SRC_IN);
            if (AdvanceTheme.m2286c(AdvanceTheme.ac, i) == AdvanceTheme.ai) {
                countDrawableGrey.setColorFilter(AdvanceTheme.m2276a(AdvanceTheme.ai, -64), Mode.SRC_IN);
            } else {
                countDrawableGrey.setColorFilter(AdvanceTheme.ai, Mode.SRC_IN);
            }
            i = AdvanceTheme.m2286c(AdvanceTheme.al, AdvanceTheme.m2286c(AdvanceTheme.aj, Theme.MSG_TEXT_COLOR));
            groupDrawable.setColorFilter(i, Mode.SRC_IN);
            superGroupDrawable.setColorFilter(i, Mode.SRC_IN);
            broadcastDrawable.setColorFilter(i, Mode.SRC_IN);
            botDrawable.setColorFilter(i, Mode.SRC_IN);
            i = AdvanceTheme.ak;
            muteDrawable.setColorFilter(i, Mode.SRC_IN);
            docTypePaint.setColor(i);
            messageCountPaint.setColor(i);
            linePaint.setColor(AdvanceTheme.as);
            backPaint.setColor(AdvanceTheme.m2276a(AdvanceTheme.f2491b, -64));
            i = AndroidUtilities.dp((float) AdvanceTheme.at);
            if (this.avatarImage != null) {
                this.avatarImage.setRoundRadius(i);
            }
            if (this.avatarDrawable != null) {
                this.avatarDrawable.setRadius(i);
            }
            this.statusBG.setStroke(AndroidUtilities.dp(2.0f), AdvanceTheme.f2514y);
            setChecks(getContext());
        }
    }

    private static void setChecks(Context context) {
        if (ThemeUtil.m2490b()) {
            String str = AdvanceTheme.bD;
            if (str.equals(ImageListActivity.m528b(1))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_2);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_2);
            } else if (str.equals(ImageListActivity.m528b(2))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_3);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_3);
            } else if (str.equals(ImageListActivity.m528b(3))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_4);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_4);
            } else if (str.equals(ImageListActivity.m528b(4))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_5);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_5);
            } else if (str.equals(ImageListActivity.m528b(5))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_6);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_6);
            } else if (str.equals(ImageListActivity.m528b(6))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_7);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_7);
            } else if (str.equals(ImageListActivity.m528b(7))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_8);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_8);
            } else if (str.equals(ImageListActivity.m528b(8))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_9);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_9);
            } else if (str.equals(ImageListActivity.m528b(9))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_10);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_10);
            } else if (str.equals(ImageListActivity.m528b(10))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_11);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_11);
            } else if (str.equals(ImageListActivity.m528b(11))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check_12);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck_12);
            } else {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_check);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.dialogs_halfcheck);
            }
        }
    }

    private boolean setStatusColor() {
        if (this.user == null || !this.drawStatus) {
            return false;
        }
        String formatUserStatus = LocaleController.formatUserStatus(this.user);
        if (formatUserStatus == null || formatUserStatus.equals(this.lastStatus)) {
            return false;
        }
        this.lastStatus = formatUserStatus;
        if (formatUserStatus.equals(LocaleController.getString("ALongTimeAgo", C0338R.string.ALongTimeAgo))) {
            this.statusBG.setColor(Theme.MSG_TEXT_COLOR);
        } else if (formatUserStatus.equals(LocaleController.getString("Online", C0338R.string.Online))) {
            this.statusBG.setColor(-16718218);
        } else if (formatUserStatus.equals(LocaleController.getString("Lately", C0338R.string.Lately))) {
            this.statusBG.setColor(-3355444);
        } else {
            this.statusBG.setColor(-7829368);
        }
        return true;
    }

    public void buildLayout() {
        int measuredWidth;
        int intrinsicWidth;
        Object obj;
        TextPaint textPaint;
        CharSequence charSequence;
        String str;
        int i;
        String charSequence2;
        CharSequence valueOf;
        String str2;
        int measuredWidth2;
        String str3 = TtmlNode.ANONYMOUS_REGION_ID;
        String str4 = TtmlNode.ANONYMOUS_REGION_ID;
        String str5 = TtmlNode.ANONYMOUS_REGION_ID;
        CharSequence charSequence3 = null;
        if (this.isDialogCell) {
            charSequence3 = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
        }
        TextPaint textPaint2 = namePaint;
        TextPaint textPaint3 = messagePaint;
        this.drawNameGroup = false;
        this.drawNameBroadcast = false;
        this.drawNameLock = false;
        this.drawNameBot = false;
        this.drawVerified = false;
        this.drawStatus = false;
        if (this.encryptedChat != null) {
            this.drawNameLock = true;
            this.nameLockTop = AndroidUtilities.dp(16.5f);
            if (LocaleController.isRTL) {
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - lockDrawable.getIntrinsicWidth();
                this.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + lockDrawable.getIntrinsicWidth();
            }
        } else if (this.chat != null) {
            if (this.chat.id < 0 || (ChatObject.isChannel(this.chat) && !this.chat.megagroup)) {
                this.drawNameBroadcast = true;
                this.nameLockTop = AndroidUtilities.dp(16.5f);
            } else {
                this.drawNameGroup = true;
                this.nameLockTop = AndroidUtilities.dp(17.5f);
            }
            this.drawVerified = this.chat.verified;
            if (this.chat.username != null && this.chat.username.equalsIgnoreCase("hanista_channel")) {
                this.drawVerified = true;
            }
            if (this.chat.id == 1009604744) {
                this.drawVerified = true;
            }
            if (LocaleController.isRTL) {
                measuredWidth = getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                intrinsicWidth = this.drawNameGroup ? this.chat.megagroup ? superGroupDrawable.getIntrinsicWidth() : groupDrawable.getIntrinsicWidth() : broadcastDrawable.getIntrinsicWidth();
                this.nameLockLeft = measuredWidth - intrinsicWidth;
                this.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                measuredWidth = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4));
                intrinsicWidth = this.drawNameGroup ? this.chat.megagroup ? superGroupDrawable.getIntrinsicWidth() : groupDrawable.getIntrinsicWidth() : broadcastDrawable.getIntrinsicWidth();
                this.nameLeft = intrinsicWidth + measuredWidth;
            }
        } else {
            if (LocaleController.isRTL) {
                this.nameLeft = AndroidUtilities.dp(14.0f);
            } else {
                this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            }
            if (this.user != null) {
                if (this.user.bot) {
                    this.drawNameBot = true;
                    this.nameLockTop = AndroidUtilities.dp(16.5f);
                    if (LocaleController.isRTL) {
                        this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - botDrawable.getIntrinsicWidth();
                        this.nameLeft = AndroidUtilities.dp(14.0f);
                    } else {
                        this.nameLockLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
                        this.nameLeft = AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 4)) + botDrawable.getIntrinsicWidth();
                    }
                } else {
                    this.drawStatus = MoboConstants.f1350q;
                }
                this.drawVerified = this.user.verified;
            }
        }
        intrinsicWidth = this.lastMessageDate;
        if (this.lastMessageDate == 0 && this.message != null) {
            intrinsicWidth = this.message.messageOwner.date;
        }
        if (this.isDialogCell) {
            this.draftMessage = DraftQuery.getDraft(this.currentDialogId);
            if ((this.draftMessage != null && ((TextUtils.isEmpty(this.draftMessage.message) && this.draftMessage.reply_to_msg_id == 0) || (r3 > this.draftMessage.date && this.unreadCount != 0))) || (!(!ChatObject.isChannel(this.chat) || this.chat.megagroup || this.chat.creator || this.chat.editor) || (this.chat != null && (this.chat.left || this.chat.kicked)))) {
                this.draftMessage = null;
            }
        } else {
            this.draftMessage = null;
        }
        int i2;
        if (charSequence3 != null) {
            this.lastPrintString = charSequence3;
            textPaint3 = messagePrintingPaint;
            if (ThemeUtil.m2490b()) {
                obj = 1;
                textPaint = messageTypingPaint;
                charSequence = charSequence3;
            }
            i2 = 1;
            textPaint = textPaint3;
            charSequence = charSequence3;
        } else {
            this.lastPrintString = null;
            Object obj2;
            if (this.draftMessage != null) {
                if (TextUtils.isEmpty(this.draftMessage.message)) {
                    Object string = LocaleController.getString("Draft", C0338R.string.Draft);
                    SpannableStringBuilder valueOf2 = SpannableStringBuilder.valueOf(string);
                    valueOf2.setSpan(new ForegroundColorSpan(Theme.DIALOGS_DRAFT_TEXT_COLOR), 0, string.length(), 33);
                    obj = null;
                    textPaint = textPaint3;
                    obj2 = valueOf2;
                } else {
                    str = this.draftMessage.message;
                    if (str.length() > 150) {
                        str = str.substring(0, 150);
                    }
                    charSequence3 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{LocaleController.getString("Draft", C0338R.string.Draft), str.replace('\n', ' ')}));
                    charSequence3.setSpan(new ForegroundColorSpan(Theme.DIALOGS_DRAFT_TEXT_COLOR), 0, r5.length() + 1, 33);
                    obj = null;
                    textPaint = textPaint3;
                    charSequence = Emoji.replaceEmoji(charSequence3, messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                }
            } else if (this.message == null) {
                if (this.encryptedChat != null) {
                    textPaint3 = messagePrintingPaint;
                    if (this.encryptedChat instanceof TL_encryptedChatRequested) {
                        i2 = 1;
                        textPaint = textPaint3;
                        obj2 = LocaleController.getString("EncryptionProcessing", C0338R.string.EncryptionProcessing);
                    } else if (this.encryptedChat instanceof TL_encryptedChatWaiting) {
                        if (this.user == null || this.user.first_name == null) {
                            i2 = 1;
                            textPaint = textPaint3;
                            obj2 = LocaleController.formatString("AwaitingEncryption", C0338R.string.AwaitingEncryption, TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            i2 = 1;
                            textPaint = textPaint3;
                            obj2 = LocaleController.formatString("AwaitingEncryption", C0338R.string.AwaitingEncryption, this.user.first_name);
                        }
                    } else if (this.encryptedChat instanceof TL_encryptedChatDiscarded) {
                        i2 = 1;
                        textPaint = textPaint3;
                        obj2 = LocaleController.getString("EncryptionRejected", C0338R.string.EncryptionRejected);
                    } else if (this.encryptedChat instanceof TL_encryptedChat) {
                        if (this.encryptedChat.admin_id != UserConfig.getClientUserId()) {
                            i2 = 1;
                            textPaint = textPaint3;
                            obj2 = LocaleController.getString("EncryptedChatStartedIncoming", C0338R.string.EncryptedChatStartedIncoming);
                        } else if (this.user == null || this.user.first_name == null) {
                            i2 = 1;
                            textPaint = textPaint3;
                            obj2 = LocaleController.formatString("EncryptedChatStartedOutgoing", C0338R.string.EncryptedChatStartedOutgoing, TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            i2 = 1;
                            textPaint = textPaint3;
                            obj2 = LocaleController.formatString("EncryptedChatStartedOutgoing", C0338R.string.EncryptedChatStartedOutgoing, this.user.first_name);
                        }
                    }
                }
                i2 = 1;
                textPaint = textPaint3;
                obj2 = str5;
            } else {
                User user = null;
                Chat chat = null;
                if (this.message.isFromUser()) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(this.message.messageOwner.from_id));
                } else {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(this.message.messageOwner.to_id.channel_id));
                }
                if (this.message.messageOwner instanceof TL_messageService) {
                    charSequence3 = this.message.messageText;
                    textPaint3 = messagePrintingPaint;
                    if (ThemeUtil.m2490b()) {
                        i2 = 1;
                        textPaint = messageTypingPaint;
                        charSequence = charSequence3;
                    }
                    i2 = 1;
                    textPaint = textPaint3;
                    charSequence = charSequence3;
                } else if (this.chat != null && this.chat.id > 0 && chat == null) {
                    str = this.message.isOutOwner() ? LocaleController.getString("FromYou", C0338R.string.FromYou) : user != null ? UserObject.getFirstName(user).replace("\n", TtmlNode.ANONYMOUS_REGION_ID) : chat != null ? chat.title.replace("\n", TtmlNode.ANONYMOUS_REGION_ID) : "DELETED";
                    i = AdvanceTheme.an;
                    measuredWidth = AdvanceTheme.m2286c(AdvanceTheme.ah, i);
                    if (this.message.caption != null) {
                        charSequence2 = this.message.caption.toString();
                        if (charSequence2.length() > 150) {
                            charSequence2 = charSequence2.substring(0, 150);
                        }
                        valueOf = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{str, charSequence2.replace('\n', ' ')}));
                    } else if (this.message.messageOwner.media != null && !this.message.isMediaEmpty()) {
                        textPaint3 = messagePrintingPaint;
                        valueOf = this.message.messageOwner.media instanceof TL_messageMediaGame ? SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{str, "\ud83c\udfae " + this.message.messageOwner.media.game.title})) : SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{str, this.message.messageText}));
                        if (ThemeUtil.m2490b()) {
                            valueOf.setSpan(new ForegroundColorSpan(measuredWidth), str.length() + 2, valueOf.length(), 33);
                        } else {
                            valueOf.setSpan(new ForegroundColorSpan(Theme.DIALOGS_PRINTING_TEXT_COLOR), str.length() + 2, valueOf.length(), 33);
                        }
                    } else if (this.message.messageOwner.message != null) {
                        charSequence2 = this.message.messageOwner.message;
                        if (charSequence2.length() > 150) {
                            charSequence2 = charSequence2.substring(0, 150);
                        }
                        valueOf = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[]{str, charSequence2.replace('\n', ' ')}));
                    } else {
                        valueOf = SpannableStringBuilder.valueOf(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    if (valueOf.length() > 0) {
                        if (ThemeUtil.m2490b()) {
                            valueOf.setSpan(new ForegroundColorSpan(i), 0, str.length() + 1, 33);
                        } else {
                            valueOf.setSpan(new ForegroundColorSpan(Theme.DIALOGS_PRINTING_TEXT_COLOR), 0, str.length() + 1, 33);
                        }
                    }
                    obj = null;
                    textPaint = textPaint3;
                    charSequence = Emoji.replaceEmoji(valueOf, messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                } else if (this.message.caption != null) {
                    i2 = 1;
                    textPaint = textPaint3;
                    charSequence = this.message.caption;
                } else {
                    str = this.message.messageOwner.media instanceof TL_messageMediaGame ? "\ud83c\udfae " + this.message.messageOwner.media.game.title : this.message.messageText;
                    if (!(this.message.messageOwner.media == null || this.message.isMediaEmpty())) {
                        textPaint3 = messagePrintingPaint;
                        if (ThemeUtil.m2490b()) {
                            i2 = 1;
                            textPaint = mediaPaint;
                            obj2 = str;
                        }
                    }
                    i2 = 1;
                    textPaint = textPaint3;
                    obj2 = str;
                }
            }
        }
        if (this.draftMessage != null) {
            valueOf = LocaleController.stringForMessageListDate((long) this.draftMessage.date);
        } else if (this.lastMessageDate != 0) {
            valueOf = LocaleController.stringForMessageListDate((long) this.lastMessageDate);
        } else if (this.message != null) {
            valueOf = LocaleController.stringForMessageListDate((long) this.message.messageOwner.date);
        } else {
            Object obj3 = str4;
        }
        if (this.message == null) {
            this.drawCheck1 = false;
            this.drawCheck2 = false;
            this.drawClock = false;
            this.drawCount = false;
            this.drawError = false;
            str2 = null;
        } else {
            if (this.unreadCount != 0) {
                this.drawCount = true;
                str = String.format("%d", new Object[]{Integer.valueOf(this.unreadCount)});
            } else {
                this.drawCount = false;
                str = null;
            }
            if (!this.message.isOut() || this.draftMessage != null) {
                this.drawCheck1 = false;
                this.drawCheck2 = false;
                this.drawClock = false;
                this.drawError = false;
            } else if (this.message.isSending()) {
                this.drawCheck1 = false;
                this.drawCheck2 = false;
                this.drawClock = true;
                this.drawError = false;
                str2 = str;
            } else if (this.message.isSendError()) {
                this.drawCheck1 = false;
                this.drawCheck2 = false;
                this.drawClock = false;
                this.drawError = true;
                this.drawCount = false;
                str2 = str;
            } else if (this.message.isSent()) {
                boolean z = !this.message.isUnread() || (ChatObject.isChannel(this.chat) && !this.chat.megagroup);
                this.drawCheck1 = z;
                this.drawCheck2 = true;
                this.drawClock = false;
                this.drawError = false;
                str2 = str;
            }
            str2 = str;
        }
        int ceil = (int) Math.ceil((double) timePaint.measureText(valueOf));
        this.timeLayout = new StaticLayout(valueOf, timePaint, ceil, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        if (LocaleController.isRTL) {
            this.timeLeft = AndroidUtilities.dp(15.0f);
        } else {
            this.timeLeft = (getMeasuredWidth() - AndroidUtilities.dp(15.0f)) - ceil;
        }
        if (this.chat != null) {
            charSequence2 = this.chat.title;
            textPaint3 = ThemeUtil.m2490b() ? groupPaint : textPaint2;
        } else if (this.user != null) {
            TextPaint textPaint4;
            if (this.user.id == UserConfig.getClientUserId()) {
                charSequence2 = LocaleController.getString("ChatYourSelfName", C0338R.string.ChatYourSelfName);
                textPaint4 = textPaint2;
            } else if (this.user.id / PointerIconCompat.TYPE_DEFAULT == 777 || this.user.id / PointerIconCompat.TYPE_DEFAULT == 333 || ContactsController.getInstance().contactsDict.get(this.user.id) != null) {
                charSequence2 = UserObject.getUserName(this.user);
                textPaint4 = textPaint2;
            } else if (ContactsController.getInstance().contactsDict.size() == 0 && (!ContactsController.getInstance().contactsLoaded || ContactsController.getInstance().isLoadingContacts())) {
                charSequence2 = UserObject.getUserName(this.user);
                textPaint4 = textPaint2;
            } else if (this.user.phone == null || this.user.phone.length() == 0) {
                charSequence2 = UserObject.getUserName(this.user);
                textPaint4 = ThemeUtil.m2490b() ? nameUnknownPaint : textPaint2;
            } else {
                charSequence2 = PhoneFormat.getInstance().format("+" + this.user.phone);
                textPaint4 = textPaint2;
            }
            textPaint3 = this.encryptedChat != null ? nameEncryptedPaint : textPaint4;
        } else {
            textPaint3 = textPaint2;
            charSequence2 = str3;
        }
        if (charSequence2.length() == 0) {
            charSequence2 = LocaleController.getString("HiddenName", C0338R.string.HiddenName);
        }
        if (LocaleController.isRTL) {
            measuredWidth2 = ((getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - ceil;
            this.nameLeft += ceil;
        } else {
            measuredWidth2 = ((getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f)) - ceil;
        }
        if (this.drawNameLock) {
            measuredWidth2 -= AndroidUtilities.dp(4.0f) + lockDrawable.getIntrinsicWidth();
        } else if (this.drawNameGroup) {
            measuredWidth2 -= (this.chat.megagroup ? superGroupDrawable.getIntrinsicWidth() : groupDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(4.0f);
        } else if (this.drawNameBroadcast) {
            measuredWidth2 -= AndroidUtilities.dp(4.0f) + broadcastDrawable.getIntrinsicWidth();
        } else if (this.drawNameBot) {
            measuredWidth2 -= AndroidUtilities.dp(4.0f) + botDrawable.getIntrinsicWidth();
        }
        if (this.drawClock) {
            i = clockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            measuredWidth2 -= i;
            if (LocaleController.isRTL) {
                this.checkDrawLeft = (ceil + this.timeLeft) + AndroidUtilities.dp(5.0f);
                this.nameLeft += i;
            } else {
                this.checkDrawLeft = this.timeLeft - i;
            }
        } else if (this.drawCheck2) {
            i = checkDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0f);
            measuredWidth2 -= i;
            if (this.drawCheck1) {
                measuredWidth2 -= halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0f);
                if (LocaleController.isRTL) {
                    this.checkDrawLeft = (ceil + this.timeLeft) + AndroidUtilities.dp(5.0f);
                    this.halfCheckDrawLeft = this.checkDrawLeft + AndroidUtilities.dp(5.5f);
                    this.nameLeft += (i + halfCheckDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(8.0f);
                } else {
                    this.halfCheckDrawLeft = this.timeLeft - i;
                    this.checkDrawLeft = this.halfCheckDrawLeft - AndroidUtilities.dp(5.5f);
                }
            } else if (LocaleController.isRTL) {
                this.checkDrawLeft = (ceil + this.timeLeft) + AndroidUtilities.dp(5.0f);
                this.nameLeft += i;
            } else {
                this.checkDrawLeft = this.timeLeft - i;
            }
        }
        if (this.dialogMuted && !this.drawVerified) {
            ceil = AndroidUtilities.dp(6.0f) + muteDrawable.getIntrinsicWidth();
            measuredWidth2 -= ceil;
            if (LocaleController.isRTL) {
                this.nameLeft = ceil + this.nameLeft;
            }
        } else if (this.drawVerified) {
            ceil = AndroidUtilities.dp(6.0f) + verifiedDrawable.getIntrinsicWidth();
            measuredWidth2 -= ceil;
            if (LocaleController.isRTL) {
                this.nameLeft = ceil + this.nameLeft;
            }
        }
        ceil = Math.max(AndroidUtilities.dp(12.0f), measuredWidth2);
        try {
            this.nameLayout = new StaticLayout(TextUtils.ellipsize(charSequence2.replace('\n', ' '), textPaint3, (float) (ceil - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint3, ceil, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        intrinsicWidth = getMeasuredWidth() - AndroidUtilities.dp((float) (AndroidUtilities.leftBaseline + 16));
        if (LocaleController.isRTL) {
            this.messageLeft = AndroidUtilities.dp(16.0f);
            measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f);
        } else {
            this.messageLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            measuredWidth2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f);
        }
        this.avatarImage.setImageCoords(measuredWidth2, this.avatarTop, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f));
        int dp;
        if (this.drawError) {
            dp = AndroidUtilities.dp(8.0f) + errorDrawable.getIntrinsicWidth();
            measuredWidth2 = intrinsicWidth - dp;
            if (LocaleController.isRTL) {
                this.errorLeft = AndroidUtilities.dp(11.0f);
                this.messageLeft += dp;
            } else {
                this.errorLeft = (getMeasuredWidth() - errorDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(11.0f);
            }
            intrinsicWidth = measuredWidth2;
        } else if (str2 != null) {
            this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) countPaint.measureText(str2)));
            this.countLayout = new StaticLayout(str2, countPaint, this.countWidth, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            dp = AndroidUtilities.dp(18.0f) + this.countWidth;
            measuredWidth2 = intrinsicWidth - dp;
            if (LocaleController.isRTL) {
                this.countLeft = AndroidUtilities.dp(19.0f);
                this.messageLeft += dp;
            } else {
                this.countLeft = (getMeasuredWidth() - this.countWidth) - AndroidUtilities.dp(19.0f);
            }
            this.drawCount = true;
            intrinsicWidth = measuredWidth2;
        } else {
            this.drawCount = false;
        }
        if (obj != null) {
            if (charSequence == null) {
                charSequence = TtmlNode.ANONYMOUS_REGION_ID;
            }
            str = charSequence.toString();
            if (str.length() > 150) {
                str = str.substring(0, 150);
            }
            charSequence = Emoji.replaceEmoji(str.replace('\n', ' '), messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
        }
        measuredWidth = Math.max(AndroidUtilities.dp(12.0f), intrinsicWidth);
        try {
            this.messageLayout = new StaticLayout(TextUtils.ellipsize(charSequence, textPaint, (float) (measuredWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), textPaint, measuredWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        float lineLeft;
        double ceil2;
        double ceil3;
        if (LocaleController.isRTL) {
            if (this.nameLayout != null && this.nameLayout.getLineCount() > 0) {
                lineLeft = this.nameLayout.getLineLeft(0);
                ceil2 = Math.ceil((double) this.nameLayout.getLineWidth(0));
                if (this.dialogMuted && !this.drawVerified) {
                    this.nameMuteLeft = (int) (((((double) this.nameLeft) + (((double) ceil) - ceil2)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) muteDrawable.getIntrinsicWidth()));
                } else if (this.drawVerified) {
                    this.nameMuteLeft = (int) (((((double) this.nameLeft) + (((double) ceil) - ceil2)) - ((double) AndroidUtilities.dp(6.0f))) - ((double) verifiedDrawable.getIntrinsicWidth()));
                }
                if (lineLeft == 0.0f && ceil2 < ((double) ceil)) {
                    this.nameLeft = (int) (((double) this.nameLeft) + (((double) ceil) - ceil2));
                }
            }
            if (this.messageLayout != null && this.messageLayout.getLineCount() > 0 && this.messageLayout.getLineLeft(0) == 0.0f) {
                ceil3 = Math.ceil((double) this.messageLayout.getLineWidth(0));
                if (ceil3 < ((double) measuredWidth)) {
                    this.messageLeft = (int) ((((double) measuredWidth) - ceil3) + ((double) this.messageLeft));
                }
            }
        } else {
            if (this.nameLayout != null && this.nameLayout.getLineCount() > 0) {
                lineLeft = this.nameLayout.getLineRight(0);
                if (lineLeft == ((float) ceil)) {
                    ceil2 = Math.ceil((double) this.nameLayout.getLineWidth(0));
                    if (ceil2 < ((double) ceil)) {
                        this.nameLeft = (int) (((double) this.nameLeft) - (((double) ceil) - ceil2));
                    }
                }
                if (this.dialogMuted || this.drawVerified) {
                    this.nameMuteLeft = (int) ((lineLeft + ((float) this.nameLeft)) + ((float) AndroidUtilities.dp(6.0f)));
                }
            }
            if (this.messageLayout != null && this.messageLayout.getLineCount() > 0 && this.messageLayout.getLineRight(0) == ((float) measuredWidth)) {
                ceil3 = Math.ceil((double) this.messageLayout.getLineWidth(0));
                if (ceil3 < ((double) measuredWidth)) {
                    this.messageLeft = (int) (((double) this.messageLeft) - (((double) measuredWidth) - ceil3));
                }
            }
        }
        if (this.dialogDm != null) {
            valueOf = TextUtils.ellipsize(DialogDmUtil.m616b(this.dialogDm.m563c()), docTypePaint, (float) ((getMeasuredWidth() - AndroidUtilities.dp(52.0f)) - AndroidUtilities.dp(12.0f)), TruncateAt.END);
            ceil = (int) Math.ceil((double) docTypePaint.measureText(valueOf + TtmlNode.ANONYMOUS_REGION_ID));
            this.docTypeLayout = new StaticLayout(valueOf, docTypePaint, ceil, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            if (LocaleController.isRTL) {
                this.docTypeLeft = (getMeasuredWidth() - ceil) - AndroidUtilities.dp(72.0f);
            } else {
                this.docTypeLeft = AndroidUtilities.dp(72.0f);
            }
            valueOf = TextUtils.ellipsize(DialogDmUtil.m615a(this.dialogDm.m564d()), messageCountPaint, (float) ((getMeasuredWidth() - AndroidUtilities.dp(52.0f)) - AndroidUtilities.dp(12.0f)), TruncateAt.END);
            ceil = (int) Math.ceil((double) messageCountPaint.measureText(valueOf + TtmlNode.ANONYMOUS_REGION_ID));
            this.messageCountLayout = new StaticLayout(valueOf, messageCountPaint, ceil, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            if (LocaleController.isRTL) {
                this.messageCountLeft = (getMeasuredWidth() - ceil) - AndroidUtilities.dp(72.0f);
            } else {
                this.messageCountLeft = AndroidUtilities.dp(72.0f);
            }
        }
    }

    public void checkCurrentDialogIndex() {
        ArrayList dialogsArray = getDialogsArray();
        if (this.index < dialogsArray.size()) {
            TL_dialog tL_dialog = (TL_dialog) dialogsArray.get(this.index);
            DraftMessage draft = DraftQuery.getDraft(this.currentDialogId);
            MessageObject messageObject = (MessageObject) MessagesController.getInstance().dialogMessage.get(Long.valueOf(tL_dialog.id));
            if (this.currentDialogId != tL_dialog.id || ((this.message != null && this.message.getId() != tL_dialog.top_message) || ((messageObject != null && messageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != tL_dialog.unread_count || this.message != messageObject || ((this.message == null && messageObject != null) || draft != this.draftMessage)))) {
                this.currentDialogId = tL_dialog.id;
                update(0);
            }
        }
    }

    public void clear() {
        this.message = null;
        this.lastMessageDate = 0;
        this.lastPrintString = null;
        this.lastSendState = 0;
    }

    public ImageReceiver getAvatarImage() {
        return this.avatarImage;
    }

    public long getDialogId() {
        return this.currentDialogId;
    }

    public ArrayList<TL_dialog> getDialogsArray() {
        return TabsUtil.m2259a(this.dialogsType, this.categoryId, this.hiddenMode);
    }

    public boolean hasOverlappingRendering() {
        return false;
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
        if (this.currentDialogId != 0) {
            if (this.isSelected) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), backPaint);
            }
            if (this.drawNameLock) {
                setDrawableBounds(lockDrawable, this.nameLockLeft, this.nameLockTop);
                lockDrawable.draw(canvas);
            } else if (this.drawNameGroup) {
                if (this.chat.megagroup) {
                    setDrawableBounds(superGroupDrawable, this.nameLockLeft, this.nameLockTop);
                    superGroupDrawable.draw(canvas);
                } else {
                    setDrawableBounds(groupDrawable, this.nameLockLeft, this.nameLockTop);
                    groupDrawable.draw(canvas);
                }
            } else if (this.drawNameBroadcast) {
                setDrawableBounds(broadcastDrawable, this.nameLockLeft, this.nameLockTop);
                broadcastDrawable.draw(canvas);
            } else if (this.drawNameBot) {
                setDrawableBounds(botDrawable, this.nameLockLeft, this.nameLockTop);
                botDrawable.draw(canvas);
            }
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, (float) AndroidUtilities.dp(13.0f));
                this.nameLayout.draw(canvas);
                canvas.restore();
            }
            canvas.save();
            canvas.translate((float) this.timeLeft, (float) this.timeTop);
            this.timeLayout.draw(canvas);
            canvas.restore();
            if (this.messageLayout != null) {
                canvas.save();
                canvas.translate((float) this.messageLeft, (float) this.messageTop);
                try {
                    this.messageLayout.draw(canvas);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                canvas.restore();
            }
            if (this.drawClock) {
                setDrawableBounds(clockDrawable, this.checkDrawLeft, this.checkDrawTop);
                clockDrawable.draw(canvas);
            } else if (this.drawCheck2) {
                if (this.drawCheck1) {
                    setDrawableBounds(halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
                    halfCheckDrawable.draw(canvas);
                    setDrawableBounds(checkDrawable, this.checkDrawLeft, this.checkDrawTop);
                    checkDrawable.draw(canvas);
                } else {
                    setDrawableBounds(checkDrawable, this.checkDrawLeft, this.checkDrawTop);
                    checkDrawable.draw(canvas);
                }
            }
            if (this.dialogMuted && !this.drawVerified) {
                setDrawableBounds(muteDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
                muteDrawable.draw(canvas);
            } else if (this.drawVerified) {
                setDrawableBounds(verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
                verifiedDrawable.draw(canvas);
            }
            if (this.drawError) {
                setDrawableBounds(errorDrawable, this.errorLeft, this.errorTop);
                errorDrawable.draw(canvas);
            } else if (this.drawCount) {
                if (this.dialogMuted) {
                    setDrawableBounds(countDrawableGrey, this.countLeft - AndroidUtilities.dp(5.5f), this.countTop, this.countWidth + AndroidUtilities.dp(11.0f), countDrawable.getIntrinsicHeight());
                    countDrawableGrey.draw(canvas);
                } else {
                    setDrawableBounds(countDrawable, this.countLeft - AndroidUtilities.dp(5.5f), this.countTop, this.countWidth + AndroidUtilities.dp(11.0f), countDrawable.getIntrinsicHeight());
                    if (ThemeUtil.m2490b()) {
                        int i = AdvanceTheme.aa;
                        setDrawableBounds(countDrawable, this.countLeft - AndroidUtilities.dp(5.5f), this.countTop + AndroidUtilities.dp((float) (i > 13 ? (i - 13) / 2 : 0)), this.countWidth + AndroidUtilities.dp(11.0f), countDrawable.getIntrinsicHeight());
                    }
                    countDrawable.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                if (this.countLayout != null) {
                    this.countLayout.draw(canvas);
                }
                canvas.restore();
            }
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), linePaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), linePaint);
                }
            }
            this.avatarImage.draw(canvas);
            if (this.drawStatus) {
                int measuredWidth;
                if (LocaleController.isRTL) {
                    measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 65.0f : 61.0f)) + AndroidUtilities.dp(4.0f);
                } else {
                    measuredWidth = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 13.0f : 9.0f) + AndroidUtilities.dp(36.0f);
                }
                setDrawableBounds(this.statusBG, measuredWidth, AndroidUtilities.dp(46.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                this.statusBG.draw(canvas);
            }
            if (this.dialogDm != null) {
                canvas.save();
                canvas.translate((float) this.docTypeLeft, (float) (this.messageTop + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)));
                this.docTypeLayout.draw(canvas);
                canvas.restore();
                canvas.save();
                canvas.translate((float) this.messageCountLeft, (float) (this.messageTop + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW)));
                this.messageCountLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentDialogId == 0) {
            super.onLayout(z, i, i2, i3, i4);
        } else if (z) {
            buildLayout();
        }
    }

    protected void onMeasure(int i, int i2) {
        int i3 = 1;
        if (this.dialogDm != null) {
            int size = MeasureSpec.getSize(i);
            int dp = AndroidUtilities.dp(127.0f);
            if (!this.useSeparator) {
                i3 = 0;
            }
            setMeasuredDimension(size, i3 + dp);
            return;
        }
        size = MeasureSpec.getSize(i);
        dp = AndroidUtilities.dp(72.0f);
        if (!this.useSeparator) {
            i3 = 0;
        }
        setMeasuredDimension(size, i3 + dp);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setCategoryId(long j) {
        this.categoryId = j;
    }

    public void setDialog(long j, MessageObject messageObject, int i) {
        this.currentDialogId = j;
        this.message = messageObject;
        this.isDialogCell = false;
        this.lastMessageDate = i;
        this.currentEditDate = messageObject != null ? messageObject.messageOwner.edit_date : 0;
        this.unreadCount = 0;
        boolean z = messageObject != null && messageObject.isUnread();
        this.lastUnreadState = z;
        if (this.message != null) {
            this.lastSendState = this.message.messageOwner.send_state;
        }
        update(0);
    }

    public void setDialog(TL_dialog tL_dialog, int i, int i2) {
        this.currentDialogId = tL_dialog.id;
        this.isDialogCell = true;
        this.index = i;
        this.dialogsType = i2;
        update(0);
    }

    public void setDialogDm(DialogDm dialogDm) {
        this.dialogDm = dialogDm;
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    public void setHiddenMode(boolean z) {
        this.hiddenMode = z;
    }

    public void update(int i) {
        TL_dialog tL_dialog;
        boolean z;
        TLObject tLObject;
        initTheme();
        if (this.isDialogCell) {
            tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
            if (tL_dialog != null && i == 0) {
                this.message = (MessageObject) MessagesController.getInstance().dialogMessage.get(Long.valueOf(tL_dialog.id));
                boolean z2 = this.message != null && this.message.isUnread();
                this.lastUnreadState = z2;
                this.unreadCount = tL_dialog.unread_count;
                this.currentEditDate = this.message != null ? this.message.messageOwner.edit_date : 0;
                this.lastMessageDate = tL_dialog.last_message_date;
                if (this.message != null) {
                    this.lastSendState = this.message.messageOwner.send_state;
                }
            }
        }
        if (i != 0) {
            if (this.isDialogCell && (i & 64) != 0) {
                CharSequence charSequence = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
                if ((this.lastPrintString != null && charSequence == null) || ((this.lastPrintString == null && charSequence != null) || !(this.lastPrintString == null || charSequence == null || this.lastPrintString.equals(charSequence)))) {
                    z2 = true;
                    if (!(z2 || (i & 4) == 0 || this.user == null || !setStatusColor())) {
                        z2 = true;
                    }
                    if (!(z2 || (i & 2) == 0 || this.chat != null)) {
                        z2 = true;
                    }
                    if (!(z2 || (i & 1) == 0 || this.chat != null)) {
                        z2 = true;
                    }
                    if (!(z2 || (i & 8) == 0 || this.user != null)) {
                        z2 = true;
                    }
                    if (!(z2 || (i & 16) == 0 || this.user != null)) {
                        z2 = true;
                    }
                    if (!(z2 || (i & TLRPC.USER_FLAG_UNUSED2) == 0)) {
                        if (this.message == null && this.lastUnreadState != this.message.isUnread()) {
                            this.lastUnreadState = this.message.isUnread();
                            z = true;
                            this.lastSendState = this.message.messageOwner.send_state;
                            z = true;
                            if (!z) {
                                return;
                            }
                        } else if (this.isDialogCell) {
                            tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
                            if (!(tL_dialog == null || this.unreadCount == tL_dialog.unread_count)) {
                                this.unreadCount = tL_dialog.unread_count;
                                if (MoboConstants.f1334a && (MoboConstants.f1344k & 32) != 0) {
                                    if (this.unreadCount != 0) {
                                        MessagesController.getInstance().dialogsUnreadOnly.remove(tL_dialog);
                                        z = true;
                                        if (!(z || (i & ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) == 0 || this.message == null || this.lastSendState == this.message.messageOwner.send_state)) {
                                            this.lastSendState = this.message.messageOwner.send_state;
                                            z = true;
                                        }
                                        if (z) {
                                            return;
                                        }
                                    } else if (!MessagesController.getInstance().dialogsUnreadOnly.contains(tL_dialog)) {
                                        MessagesController.getInstance().dialogsUnreadOnly.add(tL_dialog);
                                    }
                                }
                                z = true;
                                this.lastSendState = this.message.messageOwner.send_state;
                                z = true;
                                if (z) {
                                    return;
                                }
                            }
                        }
                    }
                    z = z2;
                    this.lastSendState = this.message.messageOwner.send_state;
                    z = true;
                    if (z) {
                        return;
                    }
                }
            }
            z2 = false;
            z2 = true;
            z2 = true;
            z2 = true;
            z2 = true;
            z2 = true;
            if (this.message == null) {
            }
            if (this.isDialogCell) {
                tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
                this.unreadCount = tL_dialog.unread_count;
                if (this.unreadCount != 0) {
                    if (MessagesController.getInstance().dialogsUnreadOnly.contains(tL_dialog)) {
                        MessagesController.getInstance().dialogsUnreadOnly.add(tL_dialog);
                    }
                    z = true;
                    this.lastSendState = this.message.messageOwner.send_state;
                    z = true;
                    if (z) {
                        return;
                    }
                }
                MessagesController.getInstance().dialogsUnreadOnly.remove(tL_dialog);
                z = true;
                this.lastSendState = this.message.messageOwner.send_state;
                z = true;
                if (z) {
                    return;
                }
            }
            z = z2;
            this.lastSendState = this.message.messageOwner.send_state;
            z = true;
            if (z) {
                return;
            }
        }
        z = this.isDialogCell && MessagesController.getInstance().isDialogMuted(this.currentDialogId);
        this.dialogMuted = z;
        this.user = null;
        this.chat = null;
        this.encryptedChat = null;
        int i2 = (int) this.currentDialogId;
        int i3 = (int) (this.currentDialogId >> 32);
        if (i2 == 0) {
            this.encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i3));
            if (this.encryptedChat != null) {
                this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.encryptedChat.user_id));
            }
        } else if (i3 == 1) {
            this.chat = MessagesController.getInstance().getChat(Integer.valueOf(i2));
        } else if (i2 < 0) {
            this.chat = MessagesController.getInstance().getChat(Integer.valueOf(-i2));
            if (!(this.isDialogCell || this.chat == null || this.chat.migrated_to == null)) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat.migrated_to.channel_id));
                if (chat != null) {
                    this.chat = chat;
                }
            }
        } else {
            this.user = MessagesController.getInstance().getUser(Integer.valueOf(i2));
        }
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
            tLObject = null;
        }
        setStatusColor();
        this.avatarImage.setImage(tLObject, "50_50", this.avatarDrawable, null, false);
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        invalidate();
    }
}
