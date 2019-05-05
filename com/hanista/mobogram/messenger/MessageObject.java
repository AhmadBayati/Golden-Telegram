package com.hanista.mobogram.messenger;

import android.graphics.Typeface;
import android.support.v4.view.PointerIconCompat;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.Emoji.EmojiSpan;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p008i.MainFont;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAnimated;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_game;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonRow;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChannelCreate;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatAddUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatCreate;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionGameScore;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionHistoryClear;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionTTLChange;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionUserJoined;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityBold;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityBotCommand;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityEmail;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityHashtag;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityItalic;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityMention;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityPre;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityTextUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageForwarded_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageForwarded_old2;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageService;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_old2;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_old3;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_old4;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_replyInlineMarkup;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPage;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.TypefaceSpan;
import com.hanista.mobogram.ui.Components.URLSpanBotCommand;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderline;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderlineBold;
import com.hanista.mobogram.ui.Components.URLSpanReplacement;
import com.hanista.mobogram.ui.Components.URLSpanUserMention;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 10;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    private static TextPaint botButtonPaint;
    private static TextPaint gameTextPaint;
    public static TextPaint textPaint;
    public static TextPaint textPaintLeft;
    private static TextPaint textPaintOneEmoji;
    public static TextPaint textPaintRight;
    private static TextPaint textPaintThreeEmoji;
    private static TextPaint textPaintTwoEmoji;
    public static Pattern urlPattern;
    public boolean attachPathExists;
    public float audioProgress;
    public int audioProgressSec;
    public CharSequence caption;
    public int contentType;
    public String dateKey;
    public boolean deleted;
    public boolean forceUpdate;
    private int generatedWithMinSize;
    public int lastLineWidth;
    private boolean layoutCreated;
    public CharSequence linkDescription;
    public boolean mediaExists;
    public Message messageOwner;
    public CharSequence messageText;
    public String monthKey;
    public ArrayList<PhotoSize> photoThumbs;
    public ArrayList<PhotoSize> photoThumbs2;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public int textHeight;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public int type;
    public VideoEditedInfo videoEditedInfo;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;

    public static class TextLayoutBlock {
        public int charactersOffset;
        public int height;
        public StaticLayout textLayout;
        public float textXOffset;
        public float textYOffset;
    }

    static {
        textPaintRight = new TextPaint(MESSAGE_SEND_STATE_SENDING);
        textPaintLeft = new TextPaint(MESSAGE_SEND_STATE_SENDING);
    }

    public MessageObject(Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, boolean z) {
        TLObject user;
        int i;
        int intValue;
        Object[] objArr;
        this.type = PointerIconCompat.TYPE_DEFAULT;
        if (textPaint == null) {
            textPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaint.setTypeface(FontUtil.m1176a().m1161d());
            textPaint.setColor(Theme.MSG_TEXT_COLOR);
            textPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
        }
        if (gameTextPaint == null) {
            gameTextPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            gameTextPaint.setTypeface(FontUtil.m1176a().m1161d());
            gameTextPaint.setColor(Theme.MSG_TEXT_COLOR);
            gameTextPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
            gameTextPaint.setTypeface(FontUtil.m1176a().m1161d());
        }
        if (textPaintOneEmoji == null) {
            textPaintOneEmoji = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaintOneEmoji.setTypeface(FontUtil.m1176a().m1161d());
            textPaintOneEmoji.setTextSize((float) AndroidUtilities.dp(28.0f));
        }
        if (textPaintTwoEmoji == null) {
            textPaintTwoEmoji = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaintTwoEmoji.setTypeface(FontUtil.m1176a().m1161d());
            textPaintTwoEmoji.setTextSize((float) AndroidUtilities.dp(24.0f));
        }
        if (textPaintThreeEmoji == null) {
            textPaintThreeEmoji = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaintThreeEmoji.setTypeface(FontUtil.m1176a().m1161d());
            textPaintThreeEmoji.setTextSize((float) AndroidUtilities.dp(20.0f));
        }
        textPaint.setTextSize((float) AndroidUtilities.dp((float) MessagesController.getInstance().fontSize));
        gameTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.messageOwner = message;
        initThemeTextPaint();
        if (message.replyMessage != null) {
            this.replyMessageObject = new MessageObject(message.replyMessage, abstractMap, abstractMap2, false);
        }
        TLObject tLObject = null;
        if (message.from_id > 0) {
            if (abstractMap != null) {
                tLObject = (User) abstractMap.get(Integer.valueOf(message.from_id));
            }
            user = tLObject == null ? MessagesController.getInstance().getUser(Integer.valueOf(message.from_id)) : tLObject;
        } else {
            user = null;
        }
        Object[] objArr2;
        String firstName;
        if (message instanceof TL_messageService) {
            if (message.action != null) {
                if (message.action instanceof TL_messageActionChatCreate) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouCreateGroup", C0338R.string.ActionYouCreateGroup);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", C0338R.string.ActionCreateGroup), "un1", user);
                    }
                } else if (message.action instanceof TL_messageActionChatDeleteUser) {
                    if (message.action.user_id != message.from_id) {
                        tLObject = null;
                        if (abstractMap != null) {
                            tLObject = (User) abstractMap.get(Integer.valueOf(message.action.user_id));
                        }
                        if (tLObject == null) {
                            tLObject = MessagesController.getInstance().getUser(Integer.valueOf(message.action.user_id));
                        }
                        if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", C0338R.string.ActionYouKickUser), "un2", tLObject);
                        } else if (message.action.user_id == UserConfig.getClientUserId()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", C0338R.string.ActionKickUserYou), "un1", user);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", C0338R.string.ActionKickUser), "un2", tLObject);
                            this.messageText = replaceWithLink(this.messageText, "un1", user);
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouLeftUser", C0338R.string.ActionYouLeftUser);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", C0338R.string.ActionLeftUser), "un1", user);
                    }
                } else if (message.action instanceof TL_messageActionChatAddUser) {
                    i = this.messageOwner.action.user_id;
                    intValue = (i == 0 && this.messageOwner.action.users.size() == MESSAGE_SEND_STATE_SENDING) ? ((Integer) this.messageOwner.action.users.get(0)).intValue() : i;
                    if (intValue != 0) {
                        tLObject = null;
                        if (abstractMap != null) {
                            tLObject = (User) abstractMap.get(Integer.valueOf(intValue));
                        }
                        if (tLObject == null) {
                            tLObject = MessagesController.getInstance().getUser(Integer.valueOf(intValue));
                        }
                        if (intValue == message.from_id) {
                            if (message.to_id.channel_id != 0 && !isMegagroup()) {
                                this.messageText = LocaleController.getString("ChannelJoined", C0338R.string.ChannelJoined);
                            } else if (message.to_id.channel_id == 0 || !isMegagroup()) {
                                if (isOut()) {
                                    this.messageText = LocaleController.getString("ActionAddUserSelfYou", C0338R.string.ActionAddUserSelfYou);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", C0338R.string.ActionAddUserSelf), "un1", user);
                                }
                            } else if (intValue == UserConfig.getClientUserId()) {
                                this.messageText = LocaleController.getString("ChannelMegaJoined", C0338R.string.ChannelMegaJoined);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", C0338R.string.ActionAddUserSelfMega), "un1", user);
                            }
                        } else if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", C0338R.string.ActionYouAddUser), "un2", tLObject);
                        } else if (intValue != UserConfig.getClientUserId()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", C0338R.string.ActionAddUser), "un2", tLObject);
                            this.messageText = replaceWithLink(this.messageText, "un1", user);
                        } else if (message.to_id.channel_id == 0) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", C0338R.string.ActionAddUserYou), "un1", user);
                        } else if (isMegagroup()) {
                            this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", C0338R.string.MegaAddedBy), "un1", user);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", C0338R.string.ChannelAddedBy), "un1", user);
                        }
                    } else if (isOut()) {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", C0338R.string.ActionYouAddUser), "un2", message.action.users, abstractMap);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", C0338R.string.ActionAddUser), "un2", message.action.users, abstractMap);
                        this.messageText = replaceWithLink(this.messageText, "un1", user);
                    }
                } else if (message.action instanceof TL_messageActionChatJoinedByLink) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionInviteYou", C0338R.string.ActionInviteYou);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", C0338R.string.ActionInviteUser), "un1", user);
                    }
                } else if (message.action instanceof TL_messageActionChatEditPhoto) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedPhoto", C0338R.string.ActionChannelChangedPhoto);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedPhoto", C0338R.string.ActionYouChangedPhoto);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", C0338R.string.ActionChangedPhoto), "un1", user);
                    }
                } else if (message.action instanceof TL_messageActionChatEditTitle) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedTitle", C0338R.string.ActionChannelChangedTitle).replace("un2", message.action.title);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedTitle", C0338R.string.ActionYouChangedTitle).replace("un2", message.action.title);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", C0338R.string.ActionChangedTitle).replace("un2", message.action.title), "un1", user);
                    }
                } else if (message.action instanceof TL_messageActionChatDeletePhoto) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", C0338R.string.ActionChannelRemovedPhoto);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouRemovedPhoto", C0338R.string.ActionYouRemovedPhoto);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", C0338R.string.ActionRemovedPhoto), "un1", user);
                    }
                } else if (message.action instanceof TL_messageActionTTLChange) {
                    if (message.action.ttl != 0) {
                        if (isOut()) {
                            objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                            objArr2[0] = AndroidUtilities.formatTTLString(message.action.ttl);
                            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", C0338R.string.MessageLifetimeChangedOutgoing, objArr2);
                        } else {
                            objArr2 = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                            objArr2[0] = UserObject.getFirstName(user);
                            objArr2[MESSAGE_SEND_STATE_SENDING] = AndroidUtilities.formatTTLString(message.action.ttl);
                            this.messageText = LocaleController.formatString("MessageLifetimeChanged", C0338R.string.MessageLifetimeChanged, objArr2);
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", C0338R.string.MessageLifetimeYouRemoved);
                    } else {
                        objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                        objArr2[0] = UserObject.getFirstName(user);
                        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", C0338R.string.MessageLifetimeRemoved, objArr2);
                    }
                } else if (message.action instanceof TL_messageActionLoginUnknownLocation) {
                    Object formatString;
                    long j = ((long) message.date) * 1000;
                    if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                        String str = TtmlNode.ANONYMOUS_REGION_ID + message.date;
                    } else {
                        Object[] objArr3 = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                        objArr3[0] = LocaleController.getInstance().formatterYear.format(j);
                        objArr3[MESSAGE_SEND_STATE_SENDING] = LocaleController.getInstance().formatterDay.format(j);
                        formatString = LocaleController.formatString("formatDateAtTime", C0338R.string.formatDateAtTime, objArr3);
                    }
                    User currentUser = UserConfig.getCurrentUser();
                    if (currentUser == null) {
                        if (abstractMap != null) {
                            currentUser = (User) abstractMap.get(Integer.valueOf(this.messageOwner.to_id.user_id));
                        }
                        if (currentUser == null) {
                            currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                        }
                    }
                    firstName = currentUser != null ? UserObject.getFirstName(currentUser) : TtmlNode.ANONYMOUS_REGION_ID;
                    this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", C0338R.string.NotificationUnrecognizedDevice, firstName, formatString, message.action.title, message.action.address);
                } else if (message.action instanceof TL_messageActionUserJoined) {
                    objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                    objArr2[0] = UserObject.getUserName(user);
                    this.messageText = LocaleController.formatString("NotificationContactJoined", C0338R.string.NotificationContactJoined, objArr2);
                } else if (message.action instanceof TL_messageActionUserUpdatedPhoto) {
                    objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                    objArr2[0] = UserObject.getUserName(user);
                    this.messageText = LocaleController.formatString("NotificationContactNewPhoto", C0338R.string.NotificationContactNewPhoto, objArr2);
                } else if (message.action instanceof TL_messageEncryptedAction) {
                    if (message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                        if (isOut()) {
                            this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", C0338R.string.ActionTakeScreenshootYou, new Object[0]);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", C0338R.string.ActionTakeScreenshoot), "un1", user);
                        }
                    } else if (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                        TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TL_decryptedMessageActionSetMessageTTL) message.action.encryptedAction;
                        if (tL_decryptedMessageActionSetMessageTTL.ttl_seconds != 0) {
                            if (isOut()) {
                                objArr = new Object[MESSAGE_SEND_STATE_SENDING];
                                objArr[0] = AndroidUtilities.formatTTLString(tL_decryptedMessageActionSetMessageTTL.ttl_seconds);
                                this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", C0338R.string.MessageLifetimeChangedOutgoing, objArr);
                            } else {
                                objArr = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                                objArr[0] = UserObject.getFirstName(user);
                                objArr[MESSAGE_SEND_STATE_SENDING] = AndroidUtilities.formatTTLString(tL_decryptedMessageActionSetMessageTTL.ttl_seconds);
                                this.messageText = LocaleController.formatString("MessageLifetimeChanged", C0338R.string.MessageLifetimeChanged, objArr);
                            }
                        } else if (isOut()) {
                            this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", C0338R.string.MessageLifetimeYouRemoved);
                        } else {
                            objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                            objArr2[0] = UserObject.getFirstName(user);
                            this.messageText = LocaleController.formatString("MessageLifetimeRemoved", C0338R.string.MessageLifetimeRemoved, objArr2);
                        }
                    }
                } else if (message.action instanceof TL_messageActionCreatedBroadcastList) {
                    this.messageText = LocaleController.formatString("YouCreatedBroadcastList", C0338R.string.YouCreatedBroadcastList, new Object[0]);
                } else if (message.action instanceof TL_messageActionChannelCreate) {
                    if (isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionCreateMega", C0338R.string.ActionCreateMega);
                    } else {
                        this.messageText = LocaleController.getString("ActionCreateChannel", C0338R.string.ActionCreateChannel);
                    }
                } else if (message.action instanceof TL_messageActionChatMigrateTo) {
                    this.messageText = LocaleController.getString("ActionMigrateFromGroup", C0338R.string.ActionMigrateFromGroup);
                } else if (message.action instanceof TL_messageActionChannelMigrateFrom) {
                    this.messageText = LocaleController.getString("ActionMigrateFromGroup", C0338R.string.ActionMigrateFromGroup);
                } else if (message.action instanceof TL_messageActionPinMessage) {
                    generatePinMessageText(user, user == null ? (Chat) abstractMap2.get(Integer.valueOf(message.to_id.channel_id)) : null);
                } else if (message.action instanceof TL_messageActionHistoryClear) {
                    this.messageText = LocaleController.getString("HistoryCleared", C0338R.string.HistoryCleared);
                } else if (message.action instanceof TL_messageActionGameScore) {
                    generateGameMessageText(user);
                }
            }
        } else if (isMediaEmpty()) {
            this.messageText = message.message;
        } else if (message.media instanceof TL_messageMediaPhoto) {
            this.messageText = LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
        } else if (isVideo()) {
            this.messageText = LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
        } else if (isVoice()) {
            this.messageText = LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
        } else if ((message.media instanceof TL_messageMediaGeo) || (message.media instanceof TL_messageMediaVenue)) {
            this.messageText = LocaleController.getString("AttachLocation", C0338R.string.AttachLocation);
        } else if (message.media instanceof TL_messageMediaContact) {
            this.messageText = LocaleController.getString("AttachContact", C0338R.string.AttachContact);
        } else if (message.media instanceof TL_messageMediaGame) {
            this.messageText = message.message;
        } else if (message.media instanceof TL_messageMediaUnsupported) {
            this.messageText = LocaleController.getString("UnsupportedMedia", C0338R.string.UnsupportedMedia);
        } else if (message.media instanceof TL_messageMediaDocument) {
            if (isSticker()) {
                firstName = getStrickerChar();
                if (firstName == null || firstName.length() <= 0) {
                    this.messageText = LocaleController.getString("AttachSticker", C0338R.string.AttachSticker);
                } else {
                    objArr2 = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                    objArr2[0] = firstName;
                    objArr2[MESSAGE_SEND_STATE_SENDING] = LocaleController.getString("AttachSticker", C0338R.string.AttachSticker);
                    this.messageText = String.format("%s %s", objArr2);
                }
            } else if (isMusic()) {
                this.messageText = LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
            } else if (isGif()) {
                this.messageText = LocaleController.getString("AttachGif", C0338R.string.AttachGif);
            } else {
                CharSequence documentFileName = FileLoader.getDocumentFileName(message.media.document);
                if (documentFileName == null || documentFileName.length() <= 0) {
                    this.messageText = LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                } else {
                    this.messageText = documentFileName;
                }
            }
        }
        if (this.messageText == null) {
            this.messageText = TtmlNode.ANONYMOUS_REGION_ID;
        }
        setType();
        measureInlineBotButtons();
        Calendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        intValue = gregorianCalendar.get(6);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(gregorianCalendar.get(MESSAGE_SEND_STATE_SENDING)), Integer.valueOf(gregorianCalendar.get(MESSAGE_SEND_STATE_SEND_ERROR)), Integer.valueOf(intValue)});
        objArr = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
        objArr[0] = Integer.valueOf(r3);
        objArr[MESSAGE_SEND_STATE_SENDING] = Integer.valueOf(i);
        this.monthKey = String.format("%d_%02d", objArr);
        if (this.messageOwner.message != null && this.messageOwner.id < 0 && this.messageOwner.message.length() > 6 && (isVideo() || isNewGif())) {
            this.videoEditedInfo = new VideoEditedInfo();
            if (!this.videoEditedInfo.parseString(this.messageOwner.message)) {
                this.videoEditedInfo = null;
            }
        }
        generateCaption();
        if (z) {
            TextPaint textPaint = this.messageOwner.media instanceof TL_messageMediaGame ? gameTextPaint : textPaint;
            int[] iArr = MessagesController.getInstance().allowBigEmoji ? new int[MESSAGE_SEND_STATE_SENDING] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            if (iArr != null && iArr[0] >= MESSAGE_SEND_STATE_SENDING && iArr[0] <= 3) {
                TextPaint textPaint2;
                switch (iArr[0]) {
                    case MESSAGE_SEND_STATE_SENDING /*1*/:
                        textPaint2 = textPaintOneEmoji;
                        intValue = AndroidUtilities.dp(32.0f);
                        break;
                    case MESSAGE_SEND_STATE_SEND_ERROR /*2*/:
                        textPaint2 = textPaintTwoEmoji;
                        intValue = AndroidUtilities.dp(28.0f);
                        break;
                    default:
                        textPaint2 = textPaintThreeEmoji;
                        intValue = AndroidUtilities.dp(24.0f);
                        break;
                }
                if (!MoboConstants.aP) {
                    EmojiSpan[] emojiSpanArr = (EmojiSpan[]) ((Spannable) this.messageText).getSpans(0, this.messageText.length(), EmojiSpan.class);
                    if (emojiSpanArr != null && emojiSpanArr.length > 0) {
                        for (int i2 = 0; i2 < emojiSpanArr.length; i2 += MESSAGE_SEND_STATE_SENDING) {
                            emojiSpanArr[i2].replaceFontMetrics(textPaint2.getFontMetricsInt(), intValue);
                        }
                    }
                }
            }
            generateLayout(user);
        }
        this.layoutCreated = z;
        generateThumbs(false);
        checkMediaExistance();
    }

    public MessageObject(Message message, AbstractMap<Integer, User> abstractMap, boolean z) {
        this(message, abstractMap, null, z);
    }

    public static void addLinks(CharSequence charSequence) {
        addLinks(charSequence, true);
    }

    public static void addLinks(CharSequence charSequence, boolean z) {
        if ((charSequence instanceof Spannable) && containsUrls(charSequence)) {
            if (charSequence.length() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                try {
                    Linkify.addLinks((Spannable) charSequence, 5);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            } else {
                try {
                    Linkify.addLinks((Spannable) charSequence, MESSAGE_SEND_STATE_SENDING);
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
            addUsernamesAndHashtags(charSequence, z);
        }
    }

    private static void addUsernamesAndHashtags(CharSequence charSequence, boolean z) {
        try {
            if (urlPattern == null) {
                urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+");
            }
            Matcher matcher = urlPattern.matcher(charSequence);
            while (matcher.find()) {
                Object uRLSpanBotCommand;
                int start = matcher.start();
                int end = matcher.end();
                int i = (charSequence.charAt(start) == '@' || charSequence.charAt(start) == '#' || charSequence.charAt(start) == '/') ? start : start + MESSAGE_SEND_STATE_SENDING;
                if (charSequence.charAt(i) == '/') {
                    uRLSpanBotCommand = z ? new URLSpanBotCommand(charSequence.subSequence(i, end).toString()) : null;
                } else {
                    URLSpanNoUnderline uRLSpanNoUnderline = new URLSpanNoUnderline(charSequence.subSequence(i, end).toString());
                }
                if (uRLSpanBotCommand != null) {
                    ((Spannable) charSequence).setSpan(uRLSpanBotCommand, i, end, 0);
                }
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static boolean canDeleteMessage(Message message, Chat chat) {
        boolean z = false;
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(message.to_id.channel_id));
        }
        if (ChatObject.isChannel(chat)) {
            return message.id != MESSAGE_SEND_STATE_SENDING;
        } else {
            if (isOut(message) || !ChatObject.isChannel(chat)) {
                z = true;
            }
            return z;
        }
    }

    public static boolean canEditMessage(Message message, Chat chat) {
        boolean z = true;
        if (message == null || message.to_id == null) {
            return false;
        }
        if ((message.action != null && !(message.action instanceof TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0) {
            return false;
        }
        if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getClientUserId()) {
            return true;
        }
        if (Math.abs(message.date - ConnectionsManager.getInstance().getCurrentTime()) > MessagesController.getInstance().maxEditTime) {
            return false;
        }
        if (message.to_id.channel_id == 0) {
            if (!((message.out || message.from_id == UserConfig.getClientUserId()) && ((message.media instanceof TL_messageMediaPhoto) || (((message.media instanceof TL_messageMediaDocument) && !isStickerMessage(message)) || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage) || message.media == null)))) {
                z = false;
            }
            return z;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(message.to_id.channel_id));
            if (chat == null) {
                return false;
            }
        }
        if (!(chat.megagroup && message.out)) {
            if (chat.megagroup) {
                return false;
            }
            if (!((chat.creator || (chat.editor && isOut(message))) && message.post)) {
                return false;
            }
        }
        return (message.media instanceof TL_messageMediaPhoto) || (((message.media instanceof TL_messageMediaDocument) && !isStickerMessage(message)) || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage) || message.media == null);
    }

    private static boolean containsUrls(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() < MESSAGE_SEND_STATE_SEND_ERROR || charSequence.length() > 20480) {
            return false;
        }
        int length = charSequence.length();
        int i = 0;
        char c = '\u0000';
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt >= '0' && charAt <= '9') {
                i2 = i4 + MESSAGE_SEND_STATE_SENDING;
                if (i2 >= 6) {
                    return true;
                }
                i3 = 0;
                i4 = i2;
                i2 = 0;
            } else if (charAt == ' ' || i4 <= 0) {
                i4 = 0;
            }
            if ((charAt == '@' || charAt == '#' || charAt == '/') && i == 0) {
                return true;
            }
            if (i != 0 && (charSequence.charAt(i - 1) == ' ' || charSequence.charAt(i - 1) == '\n')) {
                return true;
            }
            if (charAt == ':') {
                i3 = i3 == 0 ? MESSAGE_SEND_STATE_SENDING : 0;
            } else if (charAt == '/') {
                if (i3 == MESSAGE_SEND_STATE_SEND_ERROR) {
                    return true;
                }
                i3 = i3 == MESSAGE_SEND_STATE_SENDING ? i3 + MESSAGE_SEND_STATE_SENDING : 0;
            } else if (charAt == '.') {
                i2 = (i2 != 0 || c == ' ') ? 0 : i2 + MESSAGE_SEND_STATE_SENDING;
            } else if (charAt != ' ' && c == '.' && i2 == MESSAGE_SEND_STATE_SENDING) {
                return true;
            } else {
                i2 = 0;
            }
            i += MESSAGE_SEND_STATE_SENDING;
            c = charAt;
        }
        return false;
    }

    public static long getDialogId(Message message) {
        if (message.dialog_id == 0 && message.to_id != null) {
            if (message.to_id.chat_id != 0) {
                if (message.to_id.chat_id < 0) {
                    message.dialog_id = AndroidUtilities.makeBroadcastId(message.to_id.chat_id);
                } else {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                }
            } else if (message.to_id.channel_id != 0) {
                message.dialog_id = (long) (-message.to_id.channel_id);
            } else if (isOut(message)) {
                message.dialog_id = (long) message.to_id.user_id;
            } else {
                message.dialog_id = (long) message.from_id;
            }
        }
        return message.dialog_id;
    }

    public static InputStickerSet getInputStickerSet(Message message) {
        if (!(message.media == null || message.media.document == null)) {
            Iterator it = message.media.document.attributes.iterator();
            while (it.hasNext()) {
                DocumentAttribute documentAttribute = (DocumentAttribute) it.next();
                if (documentAttribute instanceof TL_documentAttributeSticker) {
                    return documentAttribute.stickerset instanceof TL_inputStickerSetEmpty ? null : documentAttribute.stickerset;
                }
            }
        }
        return null;
    }

    public static TextPaint getTextPaint() {
        if (textPaint == null) {
            textPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaint.setTypeface(FontUtil.m1176a().m1161d());
            textPaint.setColor(Theme.MSG_TEXT_COLOR);
            textPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
        }
        if (gameTextPaint == null) {
            gameTextPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            gameTextPaint.setTypeface(FontUtil.m1176a().m1161d());
            gameTextPaint.setColor(Theme.MSG_TEXT_COLOR);
            gameTextPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
        }
        if (textPaintOneEmoji == null) {
            textPaintOneEmoji = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaintOneEmoji.setTypeface(FontUtil.m1176a().m1161d());
            textPaintOneEmoji.setTextSize((float) AndroidUtilities.dp(28.0f));
        }
        if (textPaintTwoEmoji == null) {
            textPaintTwoEmoji = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaintTwoEmoji.setTypeface(FontUtil.m1176a().m1161d());
            textPaintTwoEmoji.setTextSize((float) AndroidUtilities.dp(24.0f));
        }
        if (textPaintThreeEmoji == null) {
            textPaintThreeEmoji = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaintThreeEmoji.setTypeface(FontUtil.m1176a().m1161d());
            textPaintThreeEmoji.setTextSize((float) AndroidUtilities.dp(20.0f));
        }
        textPaint.setTextSize((float) AndroidUtilities.dp((float) MessagesController.getInstance().fontSize));
        gameTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        return textPaint;
    }

    public static int getUnreadFlags(Message message) {
        int i = 0;
        if (!message.unread) {
            i = MESSAGE_SEND_STATE_SENDING;
        }
        return !message.media_unread ? i | MESSAGE_SEND_STATE_SEND_ERROR : i;
    }

    private void initThemeTextPaint() {
        if (ThemeUtil.m2490b()) {
            textPaintLeft.setColor(AdvanceTheme.bp);
            textPaintLeft.setTypeface(FontUtil.m1176a().m1161d());
            textPaintLeft.linkColor = AdvanceTheme.br;
            textPaintLeft.setTextSize((float) AndroidUtilities.dp((float) MessagesController.getInstance().fontSize));
            textPaint = textPaintLeft;
            textPaintRight.setColor(AdvanceTheme.bt);
            textPaintRight.setTypeface(FontUtil.m1176a().m1161d());
            textPaintRight.linkColor = AdvanceTheme.bv;
            textPaintRight.setTextSize((float) AndroidUtilities.dp((float) MessagesController.getInstance().fontSize));
            textPaint.setColor(AdvanceTheme.bp);
            textPaint.linkColor = AdvanceTheme.br;
            if (isOut()) {
                textPaint = textPaintRight;
                textPaint.linkColor = textPaintRight.linkColor;
            } else if (isReply()) {
                textPaint = textPaintLeft;
                textPaint.linkColor = textPaintLeft.linkColor;
            }
            if (this.messageOwner.to_id != null && this.messageOwner.to_id.channel_id != 0 && !isMegagroup() && !isOutOwner()) {
                textPaint = textPaintLeft;
                textPaint.linkColor = textPaintLeft.linkColor;
            }
        }
    }

    public static boolean isContentUnread(Message message) {
        return message.media_unread;
    }

    public static boolean isForwardedMessage(Message message) {
        return (message.flags & 4) != 0;
    }

    public static boolean isGameMessage(Message message) {
        return message.media instanceof TL_messageMediaGame;
    }

    public static boolean isGifDocument(Document document) {
        return (document == null || document.thumb == null || document.mime_type == null || (!document.mime_type.equals("image/gif") && !isNewGifDocument(document))) ? false : true;
    }

    public static boolean isMaskDocument(Document document) {
        if (document == null) {
            return false;
        }
        for (int i = 0; i < document.attributes.size(); i += MESSAGE_SEND_STATE_SENDING) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if ((documentAttribute instanceof TL_documentAttributeSticker) && documentAttribute.mask) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMaskMessage(Message message) {
        return (message.media == null || message.media.document == null || !isMaskDocument(message.media.document)) ? false : true;
    }

    public static boolean isMediaEmpty(Message message) {
        return message == null || message.media == null || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage);
    }

    public static boolean isMegagroup(Message message) {
        return (message.flags & TLRPC.MESSAGE_FLAG_MEGAGROUP) != 0;
    }

    public static boolean isMusicDocument(Document document) {
        if (document == null) {
            return false;
        }
        int i = 0;
        while (i < document.attributes.size()) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeAudio) {
                return !documentAttribute.voice;
            } else {
                i += MESSAGE_SEND_STATE_SENDING;
            }
        }
        return false;
    }

    public static boolean isMusicMessage(Message message) {
        return message.media instanceof TL_messageMediaWebPage ? isMusicDocument(message.media.webpage.document) : (message.media == null || message.media.document == null || !isMusicDocument(message.media.document)) ? false : true;
    }

    public static boolean isNewGifDocument(Document document) {
        if (document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4)) {
            return false;
        }
        for (int i = 0; i < document.attributes.size(); i += MESSAGE_SEND_STATE_SENDING) {
            if (document.attributes.get(i) instanceof TL_documentAttributeAnimated) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewGifMessage(Message message) {
        return message.media instanceof TL_messageMediaWebPage ? isNewGifDocument(message.media.webpage.document) : (message.media == null || message.media.document == null || !isNewGifDocument(message.media.document)) ? false : true;
    }

    public static boolean isOut(Message message) {
        return message.out;
    }

    public static boolean isStickerDocument(Document document) {
        if (document == null) {
            return false;
        }
        for (int i = 0; i < document.attributes.size(); i += MESSAGE_SEND_STATE_SENDING) {
            if (((DocumentAttribute) document.attributes.get(i)) instanceof TL_documentAttributeSticker) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStickerMessage(Message message) {
        return (message.media == null || message.media.document == null || !isStickerDocument(message.media.document)) ? false : true;
    }

    public static boolean isUnread(Message message) {
        return message.unread;
    }

    public static boolean isVideoDocument(Document document) {
        if (document == null) {
            return false;
        }
        boolean z = false;
        boolean z2 = false;
        for (int i = 0; i < document.attributes.size(); i += MESSAGE_SEND_STATE_SENDING) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeVideo) {
                z = true;
            } else if (documentAttribute instanceof TL_documentAttributeAnimated) {
                z2 = true;
            }
        }
        return z && !z2;
    }

    public static boolean isVideoMessage(Message message) {
        return message.media instanceof TL_messageMediaWebPage ? isVideoDocument(message.media.webpage.document) : (message.media == null || message.media.document == null || !isVideoDocument(message.media.document)) ? false : true;
    }

    public static boolean isVoiceDocument(Document document) {
        if (document == null) {
            return false;
        }
        for (int i = 0; i < document.attributes.size(); i += MESSAGE_SEND_STATE_SENDING) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeAudio) {
                return documentAttribute.voice;
            }
        }
        return false;
    }

    public static boolean isVoiceMessage(Message message) {
        return message.media instanceof TL_messageMediaWebPage ? isVoiceDocument(message.media.webpage.document) : (message.media == null || message.media.document == null || !isVoiceDocument(message.media.document)) ? false : true;
    }

    public static void setUnreadFlags(Message message, int i) {
        boolean z = true;
        message.unread = (i & MESSAGE_SEND_STATE_SENDING) == 0;
        if ((i & MESSAGE_SEND_STATE_SEND_ERROR) != 0) {
            z = false;
        }
        message.media_unread = z;
    }

    public void applyNewText() {
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            User user = null;
            if (isFromUser()) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            this.messageText = this.messageOwner.message;
            this.messageText = Emoji.replaceEmoji(this.messageText, (this.messageOwner.media instanceof TL_messageMediaGame ? gameTextPaint : textPaint).getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            generateLayout(user);
        }
    }

    public boolean canDeleteMessage(Chat chat) {
        return canDeleteMessage(this.messageOwner, chat);
    }

    public boolean canDeleteMessageOriginal(Chat chat) {
        boolean z = false;
        Message message = this.messageOwner;
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(message.to_id.channel_id));
        }
        if (ChatObject.isChannel(chat)) {
            if (message.id == MESSAGE_SEND_STATE_SENDING) {
                return false;
            }
            if (chat.creator) {
                return true;
            }
            if (chat.editor) {
                if (isOut(message)) {
                    return true;
                }
                if (message.from_id > 0 && !message.post) {
                    return true;
                }
            } else if (chat.moderator) {
                if (message.from_id > 0 && !message.post) {
                    return true;
                }
            } else if (isOut(message) && message.from_id > 0) {
                return true;
            }
        }
        if (isOut(message) || !ChatObject.isChannel(chat)) {
            z = true;
        }
        return z;
    }

    public boolean canEditMessage(Chat chat) {
        return canEditMessage(this.messageOwner, chat);
    }

    public boolean checkLayout() {
        if (this.type != 0 || this.messageOwner.to_id == null || this.messageText == null || this.messageText.length() == 0) {
            return false;
        }
        if (this.layoutCreated) {
            if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f)) {
                this.layoutCreated = false;
            }
        }
        if (this.layoutCreated) {
            return false;
        }
        this.layoutCreated = true;
        User user = null;
        if (isFromUser()) {
            user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        this.messageText = Emoji.replaceEmoji(this.messageText, (this.messageOwner.media instanceof TL_messageMediaGame ? gameTextPaint : textPaint).getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        generateLayout(user);
        return true;
    }

    public void checkMediaExistance() {
        this.attachPathExists = false;
        this.mediaExists = false;
        if (this.type == MESSAGE_SEND_STATE_SENDING) {
            if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
                this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
            }
        } else if (this.type == 8 || this.type == 3 || this.type == 9 || this.type == MESSAGE_SEND_STATE_SEND_ERROR || this.type == 14) {
            if (this.messageOwner.attachPath != null && this.messageOwner.attachPath.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
            }
        } else {
            TLObject document = getDocument();
            if (document != null) {
                this.mediaExists = FileLoader.getPathToAttach(document).exists();
            } else if (this.type == 0) {
                document = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                if (document != null && document != null) {
                    this.mediaExists = FileLoader.getPathToAttach(document, true).exists();
                }
            }
        }
    }

    public void generateCaption() {
        if (this.caption == null && this.messageOwner.media != null && this.messageOwner.media.caption != null && this.messageOwner.media.caption.length() > 0) {
            this.caption = Emoji.replaceEmoji(this.messageOwner.media.caption, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (containsUrls(this.caption)) {
                try {
                    Linkify.addLinks((Spannable) this.caption, 5);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                addUsernamesAndHashtags(this.caption, true);
            }
        }
    }

    public void generateGameMessageText(User user) {
        if (user == null && this.messageOwner.from_id > 0) {
            TLObject user2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        TLObject tLObject = null;
        if (!(this.replyMessageObject == null || this.replyMessageObject.messageOwner.media == null || this.replyMessageObject.messageOwner.media.game == null)) {
            tLObject = this.replyMessageObject.messageOwner.media.game;
        }
        if (tLObject != null) {
            Object[] objArr;
            if (user2 == null || user2.id != UserConfig.getClientUserId()) {
                objArr = new Object[MESSAGE_SEND_STATE_SENDING];
                objArr[0] = LocaleController.formatPluralString("Points", this.messageOwner.action.score);
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", C0338R.string.ActionUserScoredInGame, objArr), "un1", user2);
            } else {
                objArr = new Object[MESSAGE_SEND_STATE_SENDING];
                objArr[0] = LocaleController.formatPluralString("Points", this.messageOwner.action.score);
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", C0338R.string.ActionYouScoredInGame, objArr);
            }
            this.messageText = replaceWithLink(this.messageText, "un2", tLObject);
        } else if (user2 == null || user2.id != UserConfig.getClientUserId()) {
            r2 = new Object[MESSAGE_SEND_STATE_SENDING];
            r2[0] = LocaleController.formatPluralString("Points", this.messageOwner.action.score);
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", C0338R.string.ActionUserScored, r2), "un1", user2);
        } else {
            r2 = new Object[MESSAGE_SEND_STATE_SENDING];
            r2[0] = LocaleController.formatPluralString("Points", this.messageOwner.action.score);
            this.messageText = LocaleController.formatString("ActionYouScored", C0338R.string.ActionYouScored, r2);
        }
    }

    public void generateLayout(User user) {
        if (this.type == 0 && this.messageOwner.to_id != null && this.messageText != null && this.messageText.length() != 0) {
            int i;
            Object obj;
            int i2;
            int i3;
            generateLinkDescription();
            this.textLayoutBlocks = new ArrayList();
            this.textWidth = 0;
            if (this.messageOwner.send_state != 0) {
                for (i = 0; i < this.messageOwner.entities.size(); i += MESSAGE_SEND_STATE_SENDING) {
                    if (!(this.messageOwner.entities.get(i) instanceof TL_inputMessageEntityMentionName)) {
                        obj = MESSAGE_SEND_STATE_SENDING;
                        break;
                    }
                }
                obj = null;
            } else {
                obj = !this.messageOwner.entities.isEmpty() ? MESSAGE_SEND_STATE_SENDING : null;
            }
            Object obj2 = (obj == null && ((this.messageOwner instanceof TL_message_old) || (this.messageOwner instanceof TL_message_old2) || (this.messageOwner instanceof TL_message_old3) || (this.messageOwner instanceof TL_message_old4) || (this.messageOwner instanceof TL_messageForwarded_old) || (this.messageOwner instanceof TL_messageForwarded_old2) || (this.messageOwner instanceof TL_message_secret) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0 || (this.messageOwner.media instanceof TL_messageMediaUnsupported)))) ? MESSAGE_SEND_STATE_SENDING : null;
            if (obj2 != null) {
                addLinks(this.messageText);
            } else if ((this.messageText instanceof Spannable) && this.messageText.length() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                try {
                    Linkify.addLinks((Spannable) this.messageText, 4);
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            }
            if (this.messageText instanceof Spannable) {
                Spannable spannable = (Spannable) this.messageText;
                int size = this.messageOwner.entities.size();
                URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, this.messageText.length(), URLSpan.class);
                for (i2 = 0; i2 < size; i2 += MESSAGE_SEND_STATE_SENDING) {
                    MessageEntity messageEntity = (MessageEntity) this.messageOwner.entities.get(i2);
                    if (messageEntity.length > 0 && messageEntity.offset >= 0 && messageEntity.offset < this.messageOwner.message.length()) {
                        if (messageEntity.offset + messageEntity.length > this.messageOwner.message.length()) {
                            messageEntity.length = this.messageOwner.message.length() - messageEntity.offset;
                        }
                        if (uRLSpanArr != null && uRLSpanArr.length > 0) {
                            for (i3 = 0; i3 < uRLSpanArr.length; i3 += MESSAGE_SEND_STATE_SENDING) {
                                if (uRLSpanArr[i3] != null) {
                                    int spanStart = spannable.getSpanStart(uRLSpanArr[i3]);
                                    int spanEnd = spannable.getSpanEnd(uRLSpanArr[i3]);
                                    if ((messageEntity.offset <= spanStart && messageEntity.offset + messageEntity.length >= spanStart) || (messageEntity.offset <= spanEnd && messageEntity.offset + messageEntity.length >= spanEnd)) {
                                        spannable.removeSpan(uRLSpanArr[i3]);
                                        uRLSpanArr[i3] = null;
                                    }
                                }
                            }
                        }
                        if (messageEntity instanceof TL_messageEntityBold) {
                            spannable.setSpan(new TypefaceSpan(new MainFont().m1207c()), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                        } else if (messageEntity instanceof TL_messageEntityItalic) {
                            spannable.setSpan(new TypefaceSpan(new MainFont().m1209e()), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                        } else if ((messageEntity instanceof TL_messageEntityCode) || (messageEntity instanceof TL_messageEntityPre)) {
                            spannable.setSpan(new TypefaceSpan(Typeface.MONOSPACE, AndroidUtilities.dp((float) (MessagesController.getInstance().fontSize - 1))), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                        } else if (messageEntity instanceof TL_messageEntityMentionName) {
                            spannable.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + ((TL_messageEntityMentionName) messageEntity).user_id), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                        } else if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                            spannable.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + ((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                        } else if (obj2 == null) {
                            String substring = this.messageOwner.message.substring(messageEntity.offset, messageEntity.offset + messageEntity.length);
                            if (messageEntity instanceof TL_messageEntityBotCommand) {
                                spannable.setSpan(new URLSpanBotCommand(substring), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                            } else if ((messageEntity instanceof TL_messageEntityHashtag) || (messageEntity instanceof TL_messageEntityMention)) {
                                spannable.setSpan(new URLSpanNoUnderline(substring), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                            } else if (messageEntity instanceof TL_messageEntityEmail) {
                                spannable.setSpan(new URLSpanReplacement("mailto:" + substring), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                            } else if (messageEntity instanceof TL_messageEntityUrl) {
                                if (substring.toLowerCase().startsWith("http")) {
                                    spannable.setSpan(new URLSpan(substring), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                                } else {
                                    spannable.setSpan(new URLSpan("http://" + substring), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                                }
                            } else if (messageEntity instanceof TL_messageEntityTextUrl) {
                                spannable.setSpan(new URLSpanReplacement(messageEntity.url), messageEntity.offset, messageEntity.length + messageEntity.offset, 33);
                            }
                        }
                    }
                }
            }
            obj = (this.messageOwner.from_id <= 0 || ((this.messageOwner.to_id.channel_id == 0 && this.messageOwner.to_id.chat_id == 0 && !(this.messageOwner.media instanceof TL_messageMediaGame)) || isOut())) ? null : MESSAGE_SEND_STATE_SENDING;
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x;
            i = this.generatedWithMinSize - AndroidUtilities.dp(obj != null ? 122.0f : 80.0f);
            if ((user != null && user.bot) || ((isMegagroup() || !(this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.channel_id == 0)) && !isOut())) {
                i -= AndroidUtilities.dp(20.0f);
            }
            i3 = this.messageOwner.media instanceof TL_messageMediaGame ? i - AndroidUtilities.dp(10.0f) : i;
            initThemeTextPaint();
            TextPaint textPaint = this.messageOwner.media instanceof TL_messageMediaGame ? gameTextPaint : textPaint;
            try {
                StaticLayout staticLayout = new StaticLayout(this.messageText, textPaint, i3, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                this.textHeight = staticLayout.getHeight();
                int lineCount = staticLayout.getLineCount();
                int ceil = (int) Math.ceil((double) (((float) lineCount) / 10.0f));
                int i4 = 0;
                float f = 0.0f;
                int i5 = 0;
                while (i5 < ceil) {
                    float f2;
                    float lineLeft;
                    int min = Math.min(LINES_PER_BLOCK, lineCount - i4);
                    TextLayoutBlock textLayoutBlock = new TextLayoutBlock();
                    if (ceil == MESSAGE_SEND_STATE_SENDING) {
                        textLayoutBlock.textLayout = staticLayout;
                        textLayoutBlock.textYOffset = 0.0f;
                        textLayoutBlock.charactersOffset = 0;
                        textLayoutBlock.height = this.textHeight;
                        f2 = f;
                    } else {
                        i2 = staticLayout.getLineStart(i4);
                        int lineEnd = staticLayout.getLineEnd((i4 + min) - 1);
                        if (lineEnd < i2) {
                            min = i4;
                        } else {
                            textLayoutBlock.charactersOffset = i2;
                            try {
                                textLayoutBlock.textLayout = new StaticLayout(this.messageText.subSequence(i2, lineEnd), textPaint, i3, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                                textLayoutBlock.textYOffset = (float) staticLayout.getLineTop(i4);
                                if (i5 != 0) {
                                    textLayoutBlock.height = (int) (textLayoutBlock.textYOffset - f);
                                }
                                textLayoutBlock.height = Math.max(textLayoutBlock.height, textLayoutBlock.textLayout.getLineBottom(textLayoutBlock.textLayout.getLineCount() - 1));
                                f2 = textLayoutBlock.textYOffset;
                                if (i5 == ceil - 1) {
                                    min = Math.max(min, textLayoutBlock.textLayout.getLineCount());
                                    try {
                                        this.textHeight = Math.max(this.textHeight, (int) (textLayoutBlock.textYOffset + ((float) textLayoutBlock.textLayout.getHeight())));
                                    } catch (Throwable e) {
                                        FileLog.m18e("tmessages", e);
                                    }
                                }
                            } catch (Throwable e2) {
                                FileLog.m18e("tmessages", e2);
                                min = i4;
                            }
                        }
                        i5 += MESSAGE_SEND_STATE_SENDING;
                        i4 = min;
                    }
                    this.textLayoutBlocks.add(textLayoutBlock);
                    textLayoutBlock.textXOffset = 0.0f;
                    try {
                        lineLeft = textLayoutBlock.textLayout.getLineLeft(min - 1);
                        textLayoutBlock.textXOffset = lineLeft;
                    } catch (Throwable e3) {
                        FileLog.m18e("tmessages", e3);
                        lineLeft = 0.0f;
                    }
                    float f3 = 0.0f;
                    try {
                        f3 = textLayoutBlock.textLayout.getLineWidth(min - 1);
                    } catch (Throwable e4) {
                        FileLog.m18e("tmessages", e4);
                    }
                    int ceil2 = (int) Math.ceil((double) f3);
                    if (i5 == ceil - 1) {
                        this.lastLineWidth = ceil2;
                    }
                    int ceil3 = (int) Math.ceil((double) (f3 + lineLeft));
                    obj2 = lineLeft == 0.0f ? MESSAGE_SEND_STATE_SENDING : null;
                    if (min > MESSAGE_SEND_STATE_SENDING) {
                        int i6 = 0;
                        int i7 = ceil3;
                        int i8 = ceil2;
                        f3 = 0.0f;
                        float f4 = 0.0f;
                        while (i6 < min) {
                            float lineWidth;
                            try {
                                lineWidth = textLayoutBlock.textLayout.getLineWidth(i6);
                            } catch (Throwable e5) {
                                FileLog.m18e("tmessages", e5);
                                lineWidth = 0.0f;
                            }
                            float f5 = lineWidth > ((float) (i3 + 20)) ? (float) i3 : lineWidth;
                            try {
                                lineWidth = textLayoutBlock.textLayout.getLineLeft(i6);
                            } catch (Throwable e52) {
                                FileLog.m18e("tmessages", e52);
                                lineWidth = 0.0f;
                            }
                            if (lineWidth >= 0.0f) {
                                textLayoutBlock.textXOffset = Math.min(textLayoutBlock.textXOffset, lineWidth);
                            }
                            if (lineWidth == 0.0f) {
                                obj2 = MESSAGE_SEND_STATE_SENDING;
                            }
                            float max = Math.max(f4, f5);
                            f4 = Math.max(f3, f5 + lineWidth);
                            i8 = Math.max(i8, (int) Math.ceil((double) f5));
                            i6 += MESSAGE_SEND_STATE_SENDING;
                            i7 = Math.max(i7, (int) Math.ceil((double) (f5 + lineWidth)));
                            f3 = f4;
                            f4 = max;
                        }
                        if (obj2 == null) {
                            if (i5 == ceil - 1) {
                                this.lastLineWidth = i8;
                            }
                            f3 = f4;
                        } else if (i5 == ceil - 1) {
                            this.lastLineWidth = ceil3;
                        }
                        this.textWidth = Math.max(this.textWidth, (int) Math.ceil((double) f3));
                    } else {
                        this.textWidth = Math.max(this.textWidth, Math.min(i3, ceil2));
                    }
                    if (obj2 != null) {
                        textLayoutBlock.textXOffset = 0.0f;
                    }
                    min += i4;
                    f = f2;
                    i5 += MESSAGE_SEND_STATE_SENDING;
                    i4 = min;
                }
            } catch (Throwable th2) {
                FileLog.m18e("tmessages", th2);
            }
        }
    }

    public void generateLinkDescription() {
        if (this.linkDescription == null) {
            if ((this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage) && this.messageOwner.media.webpage.description != null) {
                this.linkDescription = Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
            } else if ((this.messageOwner.media instanceof TL_messageMediaGame) && this.messageOwner.media.game.description != null) {
                this.linkDescription = Factory.getInstance().newSpannable(this.messageOwner.media.game.description);
            }
            if (this.linkDescription != null) {
                if (containsUrls(this.linkDescription)) {
                    try {
                        Linkify.addLinks((Spannable) this.linkDescription, MESSAGE_SEND_STATE_SENDING);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
                this.linkDescription = Emoji.replaceEmoji(this.linkDescription, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
        }
    }

    public void generatePinMessageText(User user, Chat chat) {
        TLObject user2;
        if (user == null && chat == null) {
            if (this.messageOwner.from_id > 0) {
                user2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            if (user2 == null) {
                TLObject chat2 = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
            }
        }
        CharSequence string;
        String str;
        if (this.replyMessageObject == null) {
            string = LocaleController.getString("ActionPinnedNoText", C0338R.string.ActionPinnedNoText);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.isMusic()) {
            string = LocaleController.getString("ActionPinnedMusic", C0338R.string.ActionPinnedMusic);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.isVideo()) {
            string = LocaleController.getString("ActionPinnedVideo", C0338R.string.ActionPinnedVideo);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.isGif()) {
            string = LocaleController.getString("ActionPinnedGif", C0338R.string.ActionPinnedGif);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.isVoice()) {
            string = LocaleController.getString("ActionPinnedVoice", C0338R.string.ActionPinnedVoice);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.isSticker()) {
            string = LocaleController.getString("ActionPinnedSticker", C0338R.string.ActionPinnedSticker);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaDocument) {
            string = LocaleController.getString("ActionPinnedFile", C0338R.string.ActionPinnedFile);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGeo) {
            string = LocaleController.getString("ActionPinnedGeo", C0338R.string.ActionPinnedGeo);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaContact) {
            string = LocaleController.getString("ActionPinnedContact", C0338R.string.ActionPinnedContact);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
            string = LocaleController.getString("ActionPinnedPhoto", C0338R.string.ActionPinnedPhoto);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
            Object[] objArr = new Object[MESSAGE_SEND_STATE_SENDING];
            objArr[0] = "\ud83c\udfae " + this.replyMessageObject.messageOwner.media.game.title;
            string = LocaleController.formatString("ActionPinnedGame", C0338R.string.ActionPinnedGame, objArr);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        } else if (this.replyMessageObject.messageText == null || this.replyMessageObject.messageText.length() <= 0) {
            string = LocaleController.getString("ActionPinnedNoText", C0338R.string.ActionPinnedNoText);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        } else {
            string = this.replyMessageObject.messageText;
            if (string.length() > 20) {
                string = string.subSequence(0, 20) + "...";
            }
            Object[] objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
            objArr2[0] = Emoji.replaceEmoji(string, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            string = LocaleController.formatString("ActionPinnedText", C0338R.string.ActionPinnedText, objArr2);
            str = "un1";
            if (user2 == null) {
                user2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, user2);
        }
    }

    public void generateThumbs(boolean z) {
        int i;
        PhotoSize photoSize;
        int i2;
        PhotoSize photoSize2;
        if (this.messageOwner instanceof TL_messageService) {
            if (!(this.messageOwner.action instanceof TL_messageActionChatEditPhoto)) {
                return;
            }
            if (!z) {
                this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
            } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty()) {
                for (i = 0; i < this.photoThumbs.size(); i += MESSAGE_SEND_STATE_SENDING) {
                    photoSize = (PhotoSize) this.photoThumbs.get(i);
                    for (i2 = 0; i2 < this.messageOwner.action.photo.sizes.size(); i2 += MESSAGE_SEND_STATE_SENDING) {
                        photoSize2 = (PhotoSize) this.messageOwner.action.photo.sizes.get(i2);
                        if (!(photoSize2 instanceof TL_photoSizeEmpty) && photoSize2.type.equals(photoSize.type)) {
                            photoSize.location = photoSize2.location;
                            break;
                        }
                    }
                }
            }
        } else if (this.messageOwner.media != null && !(this.messageOwner.media instanceof TL_messageMediaEmpty)) {
            if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (!z || (this.photoThumbs != null && this.photoThumbs.size() != this.messageOwner.media.photo.sizes.size())) {
                    this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
                } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty()) {
                    for (i = 0; i < this.photoThumbs.size(); i += MESSAGE_SEND_STATE_SENDING) {
                        photoSize = (PhotoSize) this.photoThumbs.get(i);
                        for (i2 = 0; i2 < this.messageOwner.media.photo.sizes.size(); i2 += MESSAGE_SEND_STATE_SENDING) {
                            photoSize2 = (PhotoSize) this.messageOwner.media.photo.sizes.get(i2);
                            if (!(photoSize2 instanceof TL_photoSizeEmpty) && photoSize2.type.equals(photoSize.type)) {
                                photoSize.location = photoSize2.location;
                                break;
                            }
                        }
                    }
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                if (!(this.messageOwner.media.document.thumb instanceof TL_photoSizeEmpty)) {
                    if (!z) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.document.thumb);
                    } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty() && this.messageOwner.media.document.thumb != null) {
                        photoSize = (PhotoSize) this.photoThumbs.get(0);
                        photoSize.location = this.messageOwner.media.document.thumb.location;
                        photoSize.f2664w = this.messageOwner.media.document.thumb.f2664w;
                        photoSize.f2663h = this.messageOwner.media.document.thumb.f2663h;
                    }
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaGame) {
                if (!(this.messageOwner.media.game.document == null || (this.messageOwner.media.game.document.thumb instanceof TL_photoSizeEmpty))) {
                    if (!z) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.game.document.thumb);
                    } else if (!(this.photoThumbs == null || this.photoThumbs.isEmpty() || this.messageOwner.media.game.document.thumb == null)) {
                        ((PhotoSize) this.photoThumbs.get(0)).location = this.messageOwner.media.game.document.thumb.location;
                    }
                }
                if (this.messageOwner.media.game.photo != null) {
                    if (!z || this.photoThumbs2 == null) {
                        this.photoThumbs2 = new ArrayList(this.messageOwner.media.game.photo.sizes);
                    } else if (!this.photoThumbs2.isEmpty()) {
                        for (i = 0; i < this.photoThumbs2.size(); i += MESSAGE_SEND_STATE_SENDING) {
                            photoSize = (PhotoSize) this.photoThumbs2.get(i);
                            for (i2 = 0; i2 < this.messageOwner.media.game.photo.sizes.size(); i2 += MESSAGE_SEND_STATE_SENDING) {
                                photoSize2 = (PhotoSize) this.messageOwner.media.game.photo.sizes.get(i2);
                                if (!(photoSize2 instanceof TL_photoSizeEmpty) && photoSize2.type.equals(photoSize.type)) {
                                    photoSize.location = photoSize2.location;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (this.photoThumbs == null && this.photoThumbs2 != null) {
                    this.photoThumbs = this.photoThumbs2;
                    this.photoThumbs2 = null;
                }
            } else if (!(this.messageOwner.media instanceof TL_messageMediaWebPage)) {
            } else {
                if (this.messageOwner.media.webpage.photo != null) {
                    if (!z || this.photoThumbs == null) {
                        this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
                    } else if (!this.photoThumbs.isEmpty()) {
                        for (i = 0; i < this.photoThumbs.size(); i += MESSAGE_SEND_STATE_SENDING) {
                            photoSize = (PhotoSize) this.photoThumbs.get(i);
                            for (i2 = 0; i2 < this.messageOwner.media.webpage.photo.sizes.size(); i2 += MESSAGE_SEND_STATE_SENDING) {
                                photoSize2 = (PhotoSize) this.messageOwner.media.webpage.photo.sizes.get(i2);
                                if (!(photoSize2 instanceof TL_photoSizeEmpty) && photoSize2.type.equals(photoSize.type)) {
                                    photoSize.location = photoSize2.location;
                                    break;
                                }
                            }
                        }
                    }
                } else if (this.messageOwner.media.webpage.document != null && !(this.messageOwner.media.webpage.document.thumb instanceof TL_photoSizeEmpty)) {
                    if (!z) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
                    } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty() && this.messageOwner.media.webpage.document.thumb != null) {
                        ((PhotoSize) this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
                    }
                }
            }
        }
    }

    public int getApproximateHeight() {
        int i = 0;
        int dp;
        if (this.type == 0) {
            int i2 = this.textHeight;
            dp = ((this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage)) ? AndroidUtilities.dp(100.0f) : 0;
            dp += i2;
            return isReply() ? dp + AndroidUtilities.dp(42.0f) : dp;
        } else if (this.type == MESSAGE_SEND_STATE_SEND_ERROR) {
            return AndroidUtilities.dp(72.0f);
        } else {
            if (this.type == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (this.type == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (this.type == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (this.type == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (this.type == LINES_PER_BLOCK) {
                return AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            }
            if (this.type == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            if (this.type == 13) {
                int i3;
                float f = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                float minTabletSide = AndroidUtilities.isTablet() ? ((float) AndroidUtilities.getMinTabletSide()) * 0.5f : ((float) AndroidUtilities.displaySize.x) * 0.5f;
                Iterator it = this.messageOwner.media.document.attributes.iterator();
                while (it.hasNext()) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) it.next();
                    if (documentAttribute instanceof TL_documentAttributeImageSize) {
                        i3 = documentAttribute.f2659w;
                        i = documentAttribute.f2658h;
                        dp = i3;
                        break;
                    }
                }
                dp = 0;
                if (dp == 0) {
                    i = (int) f;
                    dp = AndroidUtilities.dp(100.0f) + i;
                }
                if (((float) i) > f) {
                    i3 = (int) (((float) dp) * (f / ((float) i)));
                    dp = (int) f;
                    i = i3;
                } else {
                    i3 = dp;
                    dp = i;
                    i = i3;
                }
                if (((float) i) > minTabletSide) {
                    dp = (int) (((float) dp) * (minTabletSide / ((float) i)));
                }
                return dp + AndroidUtilities.dp(14.0f);
            }
            dp = AndroidUtilities.isTablet() ? (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f) : (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            i = AndroidUtilities.dp(100.0f) + dp;
            if (dp > AndroidUtilities.getPhotoSize()) {
                dp = AndroidUtilities.getPhotoSize();
            }
            if (i > AndroidUtilities.getPhotoSize()) {
                i = AndroidUtilities.getPhotoSize();
            }
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (closestPhotoSizeWithSize != null) {
                dp = (int) (((float) closestPhotoSizeWithSize.f2663h) / (((float) closestPhotoSizeWithSize.f2664w) / ((float) dp)));
                if (dp == 0) {
                    dp = AndroidUtilities.dp(100.0f);
                }
                if (dp <= i) {
                    i = dp < AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN) ? AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN) : dp;
                }
                if (isSecretPhoto()) {
                    i = AndroidUtilities.isTablet() ? (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f) : (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                }
            }
            return AndroidUtilities.dp(14.0f) + i;
        }
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public Document getDocument() {
        return this.messageOwner.media instanceof TL_messageMediaWebPage ? this.messageOwner.media.webpage.document : this.messageOwner.media != null ? this.messageOwner.media.document : null;
    }

    public String getDocumentName() {
        return (this.messageOwner.media == null || this.messageOwner.media.document == null) ? TtmlNode.ANONYMOUS_REGION_ID : FileLoader.getDocumentFileName(this.messageOwner.media.document);
    }

    public int getDuration() {
        Document document = this.type == 0 ? this.messageOwner.media.webpage.document : this.messageOwner.media.document;
        for (int i = 0; i < document.attributes.size(); i += MESSAGE_SEND_STATE_SENDING) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeAudio) {
                return documentAttribute.duration;
            }
        }
        return 0;
    }

    public String getExtension() {
        String fileName = getFileName();
        int lastIndexOf = fileName.lastIndexOf(46);
        String str = null;
        if (lastIndexOf != -1) {
            str = fileName.substring(lastIndexOf + MESSAGE_SEND_STATE_SENDING);
        }
        if (str == null || str.length() == 0) {
            str = this.messageOwner.media.document.mime_type;
        }
        if (str == null) {
            str = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return str.toUpperCase();
    }

    public String getFileName() {
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(this.messageOwner.media.document);
        }
        if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
            ArrayList arrayList = this.messageOwner.media.photo.sizes;
            if (arrayList.size() > 0) {
                TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    return FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                }
            }
        } else if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
            return FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public int getFileType() {
        return isVideo() ? MESSAGE_SEND_STATE_SEND_ERROR : isVoice() ? MESSAGE_SEND_STATE_SENDING : this.messageOwner.media instanceof TL_messageMediaDocument ? 3 : this.messageOwner.media instanceof TL_messageMediaPhoto ? 0 : 4;
    }

    public String getForwardedName() {
        if (this.messageOwner.fwd_from != null) {
            if (this.messageOwner.fwd_from.channel_id != 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                if (chat != null) {
                    return chat.title;
                }
            } else if (this.messageOwner.fwd_from.from_id != 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                if (user != null) {
                    return UserObject.getUserName(user);
                }
            }
        }
        return null;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public String getMusicAuthor() {
        User user = null;
        Document document = this.type == 0 ? this.messageOwner.media.webpage.document : this.messageOwner.media.document;
        int i = 0;
        while (i < document.attributes.size()) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeAudio) {
                if (documentAttribute.voice) {
                    if (isOutOwner() || (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id == UserConfig.getClientUserId())) {
                        return LocaleController.getString("FromYou", C0338R.string.FromYou);
                    }
                    Chat chat;
                    if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.channel_id != 0) {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                    } else if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id != 0) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                        r1 = null;
                    } else if (this.messageOwner.from_id < 0) {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(-this.messageOwner.from_id));
                    } else {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
                        r1 = null;
                    }
                    if (user != null) {
                        return UserObject.getUserName(user);
                    }
                    if (chat != null) {
                        return chat.title;
                    }
                }
                String str = documentAttribute.performer;
                return (str == null || str.length() == 0) ? LocaleController.getString("AudioUnknownArtist", C0338R.string.AudioUnknownArtist) : str;
            } else {
                i += MESSAGE_SEND_STATE_SENDING;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public String getMusicTitle() {
        Document document = this.type == 0 ? this.messageOwner.media.webpage.document : this.messageOwner.media.document;
        int i = 0;
        while (i < document.attributes.size()) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (!(documentAttribute instanceof TL_documentAttributeAudio)) {
                i += MESSAGE_SEND_STATE_SENDING;
            } else if (documentAttribute.voice) {
                return LocaleController.formatDateAudio((long) this.messageOwner.date);
            } else {
                String str = documentAttribute.title;
                if (str != null && str.length() != 0) {
                    return str;
                }
                str = FileLoader.getDocumentFileName(document);
                return (str == null || str.length() == 0) ? LocaleController.getString("AudioUnknownTitle", C0338R.string.AudioUnknownTitle) : str;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        int i = this.messageOwner.ttl;
        if (this.messageOwner.destroyTime != 0) {
            i = Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance().getCurrentTime());
        }
        return i < 60 ? i + "s" : (i / 60) + "m";
    }

    public String getStickerEmoji() {
        int i = 0;
        while (i < this.messageOwner.media.document.attributes.size()) {
            DocumentAttribute documentAttribute = (DocumentAttribute) this.messageOwner.media.document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                return (documentAttribute.alt == null || documentAttribute.alt.length() <= 0) ? null : documentAttribute.alt;
            } else {
                i += MESSAGE_SEND_STATE_SENDING;
            }
        }
        return null;
    }

    public String getStrickerChar() {
        if (!(this.messageOwner.media == null || this.messageOwner.media.document == null)) {
            Iterator it = this.messageOwner.media.document.attributes.iterator();
            while (it.hasNext()) {
                DocumentAttribute documentAttribute = (DocumentAttribute) it.next();
                if (documentAttribute instanceof TL_documentAttributeSticker) {
                    return documentAttribute.alt;
                }
            }
        }
        return null;
    }

    public int getUnradFlags() {
        return getUnreadFlags(this.messageOwner);
    }

    public boolean hasPhotoStickers() {
        return (this.messageOwner.media == null || this.messageOwner.media.photo == null || !this.messageOwner.media.photo.has_stickers) ? false : true;
    }

    public boolean isContentUnread() {
        return this.messageOwner.media_unread;
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public boolean isFromUser() {
        return this.messageOwner.from_id > 0 && !this.messageOwner.post;
    }

    public boolean isGame() {
        return isGameMessage(this.messageOwner);
    }

    public boolean isGif() {
        return (this.messageOwner.media instanceof TL_messageMediaDocument) && isGifDocument(this.messageOwner.media.document);
    }

    public boolean isMask() {
        return isMaskMessage(this.messageOwner);
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public boolean isMegagroup() {
        return isMegagroup(this.messageOwner);
    }

    public boolean isMusic() {
        return isMusicMessage(this.messageOwner);
    }

    public boolean isNewGif() {
        return this.messageOwner.media != null && isNewGifDocument(this.messageOwner.media.document);
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        return this.messageOwner.out && this.messageOwner.from_id > 0 && !this.messageOwner.post;
    }

    public boolean isReply() {
        return (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner instanceof TL_messageEmpty)) && !((this.messageOwner.reply_to_msg_id == 0 && this.messageOwner.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0);
    }

    public boolean isSecretMedia() {
        return (this.messageOwner instanceof TL_message_secret) && (((this.messageOwner.media instanceof TL_messageMediaPhoto) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60) || isVoice() || isVideo());
    }

    public boolean isSecretPhoto() {
        return (this.messageOwner instanceof TL_message_secret) && (this.messageOwner.media instanceof TL_messageMediaPhoto) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60;
    }

    public boolean isSendError() {
        return this.messageOwner.send_state == MESSAGE_SEND_STATE_SEND_ERROR && this.messageOwner.id < 0;
    }

    public boolean isSending() {
        return this.messageOwner.send_state == MESSAGE_SEND_STATE_SENDING && this.messageOwner.id < 0;
    }

    public boolean isSent() {
        return this.messageOwner.send_state == 0 || this.messageOwner.id > 0;
    }

    public boolean isSticker() {
        return this.type != PointerIconCompat.TYPE_DEFAULT ? this.type == 13 : isStickerMessage(this.messageOwner);
    }

    public boolean isUnread() {
        return this.messageOwner.unread;
    }

    public boolean isVideo() {
        return isVideoMessage(this.messageOwner);
    }

    public boolean isVoice() {
        return isVoiceMessage(this.messageOwner);
    }

    public boolean isWebpageDocument() {
        return (!(this.messageOwner.media instanceof TL_messageMediaWebPage) || this.messageOwner.media.webpage.document == null || isGifDocument(this.messageOwner.media.webpage.document)) ? false : true;
    }

    public void measureInlineBotButtons() {
        this.wantedBotKeyboardWidth = 0;
        if (this.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
            if (botButtonPaint == null) {
                botButtonPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
                botButtonPaint.setTypeface(FontUtil.m1176a().m1160c());
            }
            botButtonPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            for (int i = 0; i < this.messageOwner.reply_markup.rows.size(); i += MESSAGE_SEND_STATE_SENDING) {
                TL_keyboardButtonRow tL_keyboardButtonRow = (TL_keyboardButtonRow) this.messageOwner.reply_markup.rows.get(i);
                int size = tL_keyboardButtonRow.buttons.size();
                int i2 = 0;
                int i3 = 0;
                while (i2 < size) {
                    StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(((KeyboardButton) tL_keyboardButtonRow.buttons.get(i2)).text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), botButtonPaint, AndroidUtilities.dp(2000.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                    i2 += MESSAGE_SEND_STATE_SENDING;
                    i3 = staticLayout.getLineCount() > 0 ? Math.max(i3, ((int) Math.ceil((double) (staticLayout.getLineWidth(0) - staticLayout.getLineLeft(0)))) + AndroidUtilities.dp(4.0f)) : i3;
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + i3) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, TLObject tLObject) {
        int indexOf = TextUtils.indexOf(charSequence, str);
        if (indexOf < 0) {
            return charSequence;
        }
        String userName;
        String str2;
        if (tLObject instanceof User) {
            userName = UserObject.getUserName((User) tLObject);
            str2 = TtmlNode.ANONYMOUS_REGION_ID + ((User) tLObject).id;
        } else if (tLObject instanceof Chat) {
            userName = ((Chat) tLObject).title;
            str2 = TtmlNode.ANONYMOUS_REGION_ID + (-((Chat) tLObject).id);
        } else if (tLObject instanceof TL_game) {
            userName = ((TL_game) tLObject).title;
            str2 = "game";
        } else {
            userName = TtmlNode.ANONYMOUS_REGION_ID;
            str2 = "0";
        }
        String[] strArr = new String[MESSAGE_SEND_STATE_SENDING];
        strArr[0] = str;
        CharSequence[] charSequenceArr = new String[MESSAGE_SEND_STATE_SENDING];
        charSequenceArr[0] = userName;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TextUtils.replace(charSequence, strArr, charSequenceArr));
        spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold(TtmlNode.ANONYMOUS_REGION_ID + str2), indexOf, userName.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, ArrayList<Integer> arrayList, AbstractMap<Integer, User> abstractMap) {
        if (TextUtils.indexOf(charSequence, str) < 0) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        for (int i = 0; i < arrayList.size(); i += MESSAGE_SEND_STATE_SENDING) {
            User user = null;
            if (abstractMap != null) {
                user = (User) abstractMap.get(arrayList.get(i));
            }
            if (user == null) {
                user = MessagesController.getInstance().getUser((Integer) arrayList.get(i));
            }
            if (user != null) {
                Object userName = UserObject.getUserName(user);
                int length = spannableStringBuilder.length();
                if (spannableStringBuilder.length() != 0) {
                    spannableStringBuilder.append(", ");
                }
                spannableStringBuilder.append(userName);
                spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold(TtmlNode.ANONYMOUS_REGION_ID + user.id), length, userName.length() + length, 33);
            }
        }
        String[] strArr = new String[MESSAGE_SEND_STATE_SENDING];
        strArr[0] = str;
        CharSequence[] charSequenceArr = new CharSequence[MESSAGE_SEND_STATE_SENDING];
        charSequenceArr[0] = spannableStringBuilder;
        return TextUtils.replace(charSequence, strArr, charSequenceArr);
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public void setIsRead() {
        this.messageOwner.unread = false;
    }

    public void setType() {
        int i = this.type;
        if ((this.messageOwner instanceof TL_message) || (this.messageOwner instanceof TL_messageForwarded_old2)) {
            if (isMediaEmpty()) {
                this.type = 0;
                if (this.messageText == null || this.messageText.length() == 0) {
                    this.messageText = "Empty message";
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                this.type = MESSAGE_SEND_STATE_SENDING;
            } else if ((this.messageOwner.media instanceof TL_messageMediaGeo) || (this.messageOwner.media instanceof TL_messageMediaVenue)) {
                this.type = 4;
            } else if (isVideo()) {
                this.type = 3;
            } else if (isVoice()) {
                this.type = MESSAGE_SEND_STATE_SEND_ERROR;
            } else if (isMusic()) {
                this.type = 14;
            } else if (this.messageOwner.media instanceof TL_messageMediaContact) {
                this.type = 12;
            } else if (this.messageOwner.media instanceof TL_messageMediaUnsupported) {
                this.type = 0;
            } else if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                if (this.messageOwner.media.document == null || this.messageOwner.media.document.mime_type == null) {
                    this.type = 9;
                } else if (isGifDocument(this.messageOwner.media.document)) {
                    this.type = 8;
                } else if (this.messageOwner.media.document.mime_type.equals("image/webp") && isSticker()) {
                    this.type = 13;
                } else {
                    this.type = 9;
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaGame) {
                this.type = 0;
            }
        } else if (this.messageOwner instanceof TL_messageService) {
            if (this.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if ((this.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (this.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto)) {
                this.contentType = MESSAGE_SEND_STATE_SENDING;
                this.type = 11;
            } else if (this.messageOwner.action instanceof TL_messageEncryptedAction) {
                if ((this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = MESSAGE_SEND_STATE_SENDING;
                    this.type = LINES_PER_BLOCK;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (this.messageOwner.action instanceof TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else {
                this.contentType = MESSAGE_SEND_STATE_SENDING;
                this.type = LINES_PER_BLOCK;
            }
        }
        if (i != PointerIconCompat.TYPE_DEFAULT && i != this.type) {
            generateThumbs(false);
        }
    }
}
