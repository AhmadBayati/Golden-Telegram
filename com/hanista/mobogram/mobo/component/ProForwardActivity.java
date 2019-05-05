package com.hanista.mobogram.mobo.component;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageMedia;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaAudio_layer45;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVideo_layer45;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVideo_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Cells.ChatMessageCell;
import com.hanista.mobogram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.ShareAlert;
import com.hanista.mobogram.ui.Components.ShareAlert.OnDoneListener;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayout;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;

/* renamed from: com.hanista.mobogram.mobo.component.f */
public class ProForwardActivity extends BaseFragment {
    protected ChatActivityEnterView f448a;
    private FrameLayout f449b;
    private MessageObject f450c;
    private TextView f451d;
    private FrameLayout f452e;
    private FrameLayout f453f;
    private TextView f454g;

    /* renamed from: com.hanista.mobogram.mobo.component.f.1 */
    class ProForwardActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ProForwardActivity f439a;

        ProForwardActivity(ProForwardActivity proForwardActivity) {
            this.f439a = proForwardActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f439a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.f.2 */
    class ProForwardActivity extends SizeNotifierFrameLayout {
        int f440a;
        final /* synthetic */ ProForwardActivity f441b;

        ProForwardActivity(ProForwardActivity proForwardActivity, Context context) {
            this.f441b = proForwardActivity;
            super(context);
            this.f440a = 0;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? this.f441b.f448a.getEmojiPadding() : 0;
            setBottomClip(emojiPadding);
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != 8) {
                    int i6;
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    int i7 = layoutParams.gravity;
                    if (i7 == -1) {
                        i7 = 51;
                    }
                    int i8 = i7 & 112;
                    switch ((i7 & 7) & 7) {
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                            i7 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            i7 = (i3 - measuredWidth) - layoutParams.rightMargin;
                            break;
                        default:
                            i7 = layoutParams.leftMargin;
                            break;
                    }
                    switch (i8) {
                        case TLRPC.USER_FLAG_PHONE /*16*/:
                            i6 = (((((i4 - emojiPadding) - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            i6 = layoutParams.topMargin + getPaddingTop();
                            break;
                        case 80:
                            i6 = (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin;
                            break;
                        default:
                            i6 = layoutParams.topMargin;
                            break;
                    }
                    if (this.f441b.f448a.isPopupView(childAt)) {
                        i6 = this.f441b.f448a.getBottom();
                    }
                    childAt.layout(i7, i6, measuredWidth + i7, measuredHeight + i6);
                }
            }
            notifyHeightChanged();
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            size2 -= getPaddingTop();
            int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? size2 - this.f441b.f448a.getEmojiPadding() : size2;
            int childCount = getChildCount();
            measureChildWithMargins(this.f441b.f448a, i, 0, i2, 0);
            this.f440a = this.f441b.f448a.getMeasuredHeight();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (!(childAt == null || childAt.getVisibility() == 8 || childAt == this.f441b.f448a)) {
                    try {
                        if (childAt == this.f441b.f449b) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(emojiPadding, C0700C.ENCODING_PCM_32BIT));
                        } else if (this.f441b.f448a.isPopupView(childAt)) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, C0700C.ENCODING_PCM_32BIT));
                        } else {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.f.3 */
    class ProForwardActivity implements OnTouchListener {
        final /* synthetic */ ProForwardActivity f442a;

        ProForwardActivity(ProForwardActivity proForwardActivity) {
            this.f442a = proForwardActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.f.4 */
    class ProForwardActivity implements ChatActivityEnterViewDelegate {
        final /* synthetic */ ProForwardActivity f443a;

        ProForwardActivity(ProForwardActivity proForwardActivity) {
            this.f443a = proForwardActivity;
        }

        public void needSendTyping() {
        }

        public void onAttachButtonHidden() {
        }

        public void onAttachButtonShow() {
        }

        public void onMessageEditEnd(boolean z) {
        }

        public void onMessageSend(CharSequence charSequence) {
        }

        public void onStickersTab(boolean z) {
        }

        public void onTextChanged(CharSequence charSequence, boolean z) {
        }

        public void onWindowSizeChanged(int i) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.f.5 */
    class ProForwardActivity implements ChatMessageCellDelegate {
        final /* synthetic */ ProForwardActivity f444a;

        ProForwardActivity(ProForwardActivity proForwardActivity) {
            this.f444a = proForwardActivity;
        }

        public boolean canPerformActions() {
            return false;
        }

        public void didLongPressed(ChatMessageCell chatMessageCell) {
        }

        public void didLongPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
        }

        public void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
        }

        public void didPressedCancelSendButton(ChatMessageCell chatMessageCell) {
        }

        public void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
        }

        public void didPressedFavorite(ChatMessageCell chatMessageCell) {
        }

        public void didPressedImage(ChatMessageCell chatMessageCell) {
        }

        public void didPressedMenu(ChatMessageCell chatMessageCell) {
        }

        public void didPressedOther(ChatMessageCell chatMessageCell) {
        }

        public void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i) {
        }

        public void didPressedShare(ChatMessageCell chatMessageCell) {
        }

        public void didPressedUrl(MessageObject messageObject, ClickableSpan clickableSpan, boolean z) {
        }

        public void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
        }

        public void didPressedViaBot(ChatMessageCell chatMessageCell, String str) {
        }

        public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
        }

        public boolean needPlayAudio(MessageObject messageObject) {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.f.6 */
    class ProForwardActivity implements OnClickListener {
        final /* synthetic */ ProForwardActivity f445a;

        ProForwardActivity(ProForwardActivity proForwardActivity) {
            this.f445a = proForwardActivity;
        }

        public void onClick(View view) {
            String str = null;
            CharSequence trimmedString = AndroidUtilities.getTrimmedString(this.f445a.f448a.getFieldText());
            if (trimmedString != null && trimmedString.length() == 0) {
                trimmedString = null;
            }
            if (this.f445a.m505c()) {
                MessageMedia messageMedia = this.f445a.f450c.messageOwner.media;
                if (trimmedString != null) {
                    str = trimmedString.toString();
                }
                messageMedia.caption = str;
            } else {
                this.f445a.f450c.messageText = trimmedString;
                if (this.f445a.f450c.messageOwner != null) {
                    Message message = this.f445a.f450c.messageOwner;
                    if (trimmedString != null) {
                        str = trimmedString.toString();
                    }
                    message.message = str;
                }
            }
            this.f445a.f448a.closeKeyboard();
            this.f445a.m501a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.f.7 */
    class ProForwardActivity implements OnDoneListener {
        final /* synthetic */ ProForwardActivity f446a;

        ProForwardActivity(ProForwardActivity proForwardActivity) {
            this.f446a = proForwardActivity;
        }

        public void onDone() {
            Toast.makeText(this.f446a.getParentActivity(), LocaleController.getString("Sent", C0338R.string.Sent), 0).show();
            this.f446a.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.f.8 */
    class ProForwardActivity implements DialogInterface.OnClickListener {
        final /* synthetic */ ProForwardActivity f447a;

        ProForwardActivity(ProForwardActivity proForwardActivity) {
            this.f447a = proForwardActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    public ProForwardActivity(MessageObject messageObject) {
        this.f450c = new MessageObject(m499a(messageObject.messageOwner), null, true);
        this.f450c.photoThumbs = messageObject.photoThumbs;
    }

    private Message m499a(Message message) {
        if (message == null) {
            return null;
        }
        Message message2 = new Message();
        if (message instanceof TL_message) {
            message2 = new TL_message();
        } else if (message instanceof TL_message_secret) {
            message2 = new TL_message_secret();
        }
        message2.id = message.id;
        message2.from_id = message.from_id;
        message2.to_id = message.to_id;
        message2.date = message.date;
        message2.action = message.action;
        message2.reply_to_msg_id = message.reply_to_msg_id;
        message2.fwd_from = message.fwd_from;
        message2.reply_to_random_id = message.reply_to_random_id;
        message2.via_bot_name = message.via_bot_name;
        message2.edit_date = message.edit_date;
        message2.silent = message.silent;
        message2.message = message.message;
        if (message.media != null) {
            message2.media = m500a(message.media);
        }
        message2.flags = message.flags;
        message2.mentioned = message.mentioned;
        message2.media_unread = message.media_unread;
        message2.out = message.out;
        message2.unread = message.unread;
        message2.entities = message.entities;
        message2.reply_markup = message.reply_markup;
        message2.views = message.views;
        message2.via_bot_id = message.via_bot_id;
        message2.send_state = message.send_state;
        message2.fwd_msg_id = message.fwd_msg_id;
        message2.attachPath = message.attachPath;
        message2.params = message.params;
        message2.random_id = message.random_id;
        message2.local_id = message.local_id;
        message2.dialog_id = message.dialog_id;
        message2.ttl = message.ttl;
        message2.destroyTime = message.destroyTime;
        message2.layer = message.layer;
        message2.seq_in = message.seq_in;
        message2.seq_out = message.seq_out;
        message2.replyMessage = message.replyMessage;
        return message2;
    }

    private MessageMedia m500a(MessageMedia messageMedia) {
        MessageMedia tL_messageMediaUnsupported_old = messageMedia instanceof TL_messageMediaUnsupported_old ? new TL_messageMediaUnsupported_old() : messageMedia instanceof TL_messageMediaAudio_layer45 ? new TL_messageMediaAudio_layer45() : messageMedia instanceof TL_messageMediaPhoto_old ? new TL_messageMediaPhoto_old() : messageMedia instanceof TL_messageMediaUnsupported ? new TL_messageMediaUnsupported() : messageMedia instanceof TL_messageMediaEmpty ? new TL_messageMediaEmpty() : messageMedia instanceof TL_messageMediaVenue ? new TL_messageMediaVenue() : messageMedia instanceof TL_messageMediaVideo_old ? new TL_messageMediaVideo_old() : messageMedia instanceof TL_messageMediaDocument_old ? new TL_messageMediaDocument_old() : messageMedia instanceof TL_messageMediaDocument ? new TL_messageMediaDocument() : messageMedia instanceof TL_messageMediaContact ? new TL_messageMediaContact() : messageMedia instanceof TL_messageMediaPhoto ? new TL_messageMediaPhoto() : messageMedia instanceof TL_messageMediaVideo_layer45 ? new TL_messageMediaVideo_layer45() : messageMedia instanceof TL_messageMediaWebPage ? new TL_messageMediaWebPage() : messageMedia instanceof TL_messageMediaGeo ? new TL_messageMediaGeo() : new MessageMedia();
        tL_messageMediaUnsupported_old.bytes = messageMedia.bytes;
        tL_messageMediaUnsupported_old.caption = messageMedia.caption;
        tL_messageMediaUnsupported_old.photo = messageMedia.photo;
        tL_messageMediaUnsupported_old.audio_unused = messageMedia.audio_unused;
        tL_messageMediaUnsupported_old.geo = messageMedia.geo;
        tL_messageMediaUnsupported_old.title = messageMedia.title;
        tL_messageMediaUnsupported_old.address = messageMedia.address;
        tL_messageMediaUnsupported_old.provider = messageMedia.provider;
        tL_messageMediaUnsupported_old.venue_id = messageMedia.venue_id;
        tL_messageMediaUnsupported_old.document = messageMedia.document;
        tL_messageMediaUnsupported_old.video_unused = messageMedia.video_unused;
        tL_messageMediaUnsupported_old.phone_number = messageMedia.phone_number;
        tL_messageMediaUnsupported_old.first_name = messageMedia.first_name;
        tL_messageMediaUnsupported_old.last_name = messageMedia.last_name;
        tL_messageMediaUnsupported_old.user_id = messageMedia.user_id;
        tL_messageMediaUnsupported_old.webpage = messageMedia.webpage;
        return tL_messageMediaUnsupported_old;
    }

    private void m501a() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.f450c);
        showDialog(new ShareAlert(getParentActivity(), arrayList, false, true, false, new ProForwardActivity(this)));
    }

    private void m502b() {
        if (UserConfig.isClientActivated()) {
            SettingManager settingManager = new SettingManager();
            if (!settingManager.m944b("proForwardHelpDisplayed")) {
                settingManager.m943a("proForwardHelpDisplayed", true);
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ProForward", C0338R.string.ProForward)).setMessage(LocaleController.getString("ProForwardHelp", C0338R.string.ProForwardHelp));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ProForwardActivity(this));
                builder.create().show();
            }
        }
    }

    private boolean m505c() {
        return (this.f450c.messageOwner == null || this.f450c.messageOwner.media == null || (this.f450c.messageOwner.media instanceof TL_messageMediaWebPage) || (this.f450c.messageOwner.media instanceof TL_messageMediaEmpty)) ? false : true;
    }

    private void m506d() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2491b;
            int i2 = AdvanceTheme.bH;
            int i3 = AdvanceTheme.bG;
            if (i2 != -1) {
                i = -5395027;
            }
            i = AdvanceTheme.m2286c(i3, i);
            this.f451d.setTextColor(i);
            this.f454g.setTextColor(i);
            this.f452e.setBackgroundColor(i2);
            this.f453f.setBackgroundColor(i2);
        }
    }

    public View createView(Context context) {
        View view;
        Object obj;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ProForward", C0338R.string.ProForward));
        this.actionBar.setActionBarMenuOnItemClick(new ProForwardActivity(this));
        this.fragmentView = new ProForwardActivity(this, context);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
        sizeNotifierFrameLayout.setBackgroundImage(ApplicationLoader.getCachedWallpaper());
        this.f449b = new FrameLayout(context);
        sizeNotifierFrameLayout.addView(this.f449b, LayoutHelper.createFrame(-1, -2, 17));
        this.f449b.setOnTouchListener(new ProForwardActivity(this));
        this.f452e = new FrameLayout(context);
        this.f452e.setBackgroundColor(-1);
        if (m505c()) {
            this.f449b.addView(this.f452e, LayoutHelper.createFrame(-1, 48, 48));
        }
        View view2 = new View(context);
        view2.setBackgroundColor(-1513240);
        this.f452e.addView(view2, LayoutHelper.createFrame(-1, 1, 83));
        this.f451d = new TextView(context);
        this.f451d.setTextSize(1, 14.0f);
        this.f451d.setTextColor(-13141330);
        this.f451d.setTypeface(FontUtil.m1176a().m1160c());
        this.f451d.setSingleLine(true);
        this.f451d.setEllipsize(TruncateAt.END);
        this.f451d.setMaxLines(1);
        this.f451d.setText(LocaleController.getString("Media", C0338R.string.Media) + " : ");
        this.f452e.addView(this.f451d, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, 10.0f, 4.0f, 10.0f, 0.0f));
        if (this.f448a != null) {
            this.f448a.onDestroy();
        }
        this.f448a = new ChatActivityEnterView(getParentActivity(), sizeNotifierFrameLayout, null, false);
        this.f448a.setDisableSendSound(true);
        this.f448a.checkSendButton(false);
        this.f448a.setDialogId(this.f450c.getDialogId());
        this.f448a.getMessageEditText().setMaxLines(m505c() ? 10 : 15);
        sizeNotifierFrameLayout.addView(this.f448a, sizeNotifierFrameLayout.getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
        this.f448a.setDelegate(new ProForwardActivity(this));
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if (m505c()) {
            view2 = new ChatMessageCell(getParentActivity());
            String str2 = this.f450c.messageOwner.media.caption;
            this.f450c.messageOwner.media.caption = null;
            this.f450c.caption = null;
            view = view2;
            obj = str2;
        } else {
            this.f451d.setVisibility(8);
            CharSequence charSequence = this.f450c.messageText;
            this.f450c.messageText = null;
            if (this.f450c.messageOwner != null) {
                this.f450c.messageOwner.message = null;
            }
            view = new ChatMessageCell(getParentActivity());
            CharSequence charSequence2 = charSequence;
        }
        view.setDelegate(new ProForwardActivity(this));
        view.setMessageObject(this.f450c);
        if (m505c()) {
            this.f449b.addView(view, LayoutHelper.createFrame(-1, -2.0f, 48, 0.0f, m505c() ? 48.0f : 0.0f, 0.0f, 0.0f));
        }
        this.f448a.setFieldText(obj + TtmlNode.ANONYMOUS_REGION_ID);
        this.f448a.getSendButton().setOnClickListener(new ProForwardActivity(this));
        this.f453f = new FrameLayout(context);
        this.f453f.setClickable(true);
        this.f448a.addTopView(this.f453f, 48);
        view2 = new View(context);
        view2.setBackgroundColor(-1513240);
        this.f453f.addView(view2, LayoutHelper.createFrame(-1, 1, 83));
        this.f454g = new TextView(context);
        this.f454g.setTextSize(1, 14.0f);
        this.f454g.setTextColor(-13141330);
        this.f454g.setTypeface(FontUtil.m1176a().m1160c());
        this.f454g.setSingleLine(true);
        this.f454g.setEllipsize(TruncateAt.END);
        this.f454g.setMaxLines(1);
        if (m505c()) {
            this.f454g.setText(LocaleController.getString("MediaCaption", C0338R.string.MediaCaption) + " : ");
            this.f448a.getMessageEditText().setFilters(new InputFilter[]{new LengthFilter(Callback.DEFAULT_DRAG_ANIMATION_DURATION)});
            this.f448a.getMessageEditText().setHint(LocaleController.getString("MediaCaption", C0338R.string.MediaCaption));
        } else {
            this.f454g.setText(LocaleController.getString("EditText", C0338R.string.EditText) + " : ");
            this.f448a.getMessageEditText().setHint(LocaleController.getString("EditText", C0338R.string.EditText));
        }
        this.f453f.addView(this.f454g, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, 10.0f, 4.0f, 10.0f, 0.0f));
        this.f448a.showTopView(true, false);
        m502b();
        m506d();
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
