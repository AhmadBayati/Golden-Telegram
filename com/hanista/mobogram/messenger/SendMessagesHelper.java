package com.hanista.mobogram.messenger;

import android.app.AlertDialog.Builder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.audioinfo.AudioInfo;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.query.DraftQuery;
import com.hanista.mobogram.messenger.query.SearchQuery;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.QuickAckDelegate;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInlineResult;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.DecryptedMessage;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputDocument;
import com.hanista.mobogram.tgnet.TLRPC.InputEncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.InputMedia;
import com.hanista.mobogram.tgnet.TLRPC.InputPeer;
import com.hanista.mobogram.tgnet.TLRPC.InputUser;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.MessageMedia;
import com.hanista.mobogram.tgnet.TLRPC.Peer;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.ReplyMarkup;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMediaResult;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageText;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionTyping;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaAudio_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaDocument_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaGeoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaVideo_layer17;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaVideo_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessage_layer17;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessage_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAnimated;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeFilename;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker_layer55;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import com.hanista.mobogram.tgnet.TLRPC.TL_game;
import com.hanista.mobogram.tgnet.TLRPC.TL_geoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputEncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputGeoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaGeoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaGifExternal;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaUploadedDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaUploadedPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaUploadedThumbDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_editMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_forwardMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_sendBroadcast;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_sendMedia;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_sendMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_photo;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoCachedSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateMessageID;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewChannelMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateShortSentMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_user;
import com.hanista.mobogram.tgnet.TLRPC.TL_userContact_old2;
import com.hanista.mobogram.tgnet.TLRPC.TL_userRequest_old2;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPagePending;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPageUrlPending;
import com.hanista.mobogram.tgnet.TLRPC.Update;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SendMessagesHelper implements NotificationCenterDelegate {
    private static volatile SendMessagesHelper Instance;
    private ChatFull currentChatInfo;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages;
    private LocationProvider locationProvider;
    private HashMap<Integer, Message> sendingMessages;
    private HashMap<Integer, MessageObject> unsentMessages;
    private HashMap<String, MessageObject> waitingForCallback;
    private HashMap<String, MessageObject> waitingForLocation;

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.10 */
    class AnonymousClass10 implements QuickAckDelegate {
        final /* synthetic */ Message val$newMsgObj;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.10.1 */
        class C06461 implements Runnable {
            final /* synthetic */ int val$msg_id;

            C06461(int i) {
                this.val$msg_id = i;
            }

            public void run() {
                AnonymousClass10.this.val$newMsgObj.send_state = 0;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(this.val$msg_id));
            }
        }

        AnonymousClass10(Message message) {
            this.val$newMsgObj = message;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C06461(this.val$newMsgObj.id));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ ArrayList val$chats;
        final /* synthetic */ ArrayList val$encryptedChats;
        final /* synthetic */ ArrayList val$messages;
        final /* synthetic */ ArrayList val$users;

        AnonymousClass11(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
            this.val$users = arrayList;
            this.val$chats = arrayList2;
            this.val$encryptedChats = arrayList3;
            this.val$messages = arrayList4;
        }

        public void run() {
            MessagesController.getInstance().putUsers(this.val$users, true);
            MessagesController.getInstance().putChats(this.val$chats, true);
            MessagesController.getInstance().putEncryptedChats(this.val$encryptedChats, true);
            for (int i = 0; i < this.val$messages.size(); i++) {
                SendMessagesHelper.this.retrySendMessage(new MessageObject((Message) this.val$messages.get(i), null, false), true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.12 */
    static class AnonymousClass12 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ TL_document val$documentFinal;
        final /* synthetic */ HashMap val$params;
        final /* synthetic */ String val$pathFinal;
        final /* synthetic */ MessageObject val$reply_to_msg;

        AnonymousClass12(TL_document tL_document, String str, long j, MessageObject messageObject, HashMap hashMap) {
            this.val$documentFinal = tL_document;
            this.val$pathFinal = str;
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
            this.val$params = hashMap;
        }

        public void run() {
            SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$pathFinal, this.val$dialog_id, this.val$reply_to_msg, null, this.val$params);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.13 */
    static class AnonymousClass13 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$messageObjects;
        final /* synthetic */ MessageObject val$reply_to_msg;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.13.1 */
        class C06471 implements Runnable {
            final /* synthetic */ TL_document val$documentFinal;
            final /* synthetic */ MessageObject val$messageObject;
            final /* synthetic */ HashMap val$params;

            C06471(TL_document tL_document, MessageObject messageObject, HashMap hashMap) {
                this.val$documentFinal = tL_document;
                this.val$messageObject = messageObject;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$messageObject.messageOwner.attachPath, AnonymousClass13.this.val$dialog_id, AnonymousClass13.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass13(ArrayList arrayList, long j, MessageObject messageObject) {
            this.val$messageObjects = arrayList;
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            int size = this.val$messageObjects.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = (MessageObject) this.val$messageObjects.get(i);
                String str = messageObject.messageOwner.attachPath;
                File file = new File(str);
                Object obj = ((int) this.val$dialog_id) == 0 ? 1 : null;
                String str2 = str != null ? str + MimeTypes.BASE_TYPE_AUDIO + file.length() : str;
                TL_document tL_document = null;
                if (obj == null) {
                    tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(str2, obj == null ? 1 : 4);
                }
                TL_document tL_document2 = tL_document == null ? (TL_document) messageObject.messageOwner.media.document : tL_document;
                if (obj != null) {
                    EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.val$dialog_id >> 32)));
                    if (encryptedChat != null) {
                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) < 46) {
                            for (int i2 = 0; i2 < tL_document2.attributes.size(); i2++) {
                                if (tL_document2.attributes.get(i2) instanceof TL_documentAttributeAudio) {
                                    TL_documentAttributeAudio_old tL_documentAttributeAudio_old = new TL_documentAttributeAudio_old();
                                    tL_documentAttributeAudio_old.duration = ((DocumentAttribute) tL_document2.attributes.get(i2)).duration;
                                    tL_document2.attributes.remove(i2);
                                    tL_document2.attributes.add(tL_documentAttributeAudio_old);
                                    break;
                                }
                            }
                        }
                    } else {
                        return;
                    }
                }
                HashMap hashMap = new HashMap();
                if (str2 != null) {
                    hashMap.put("originalPath", str2);
                }
                AndroidUtilities.runOnUIThread(new C06471(tL_document2, messageObject, hashMap));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.14 */
    static class AnonymousClass14 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ String val$mime;
        final /* synthetic */ ArrayList val$originalPaths;
        final /* synthetic */ ArrayList val$paths;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ ArrayList val$uris;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.14.1 */
        class C06481 implements Runnable {
            C06481() {
            }

            public void run() {
                try {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", C0338R.string.UnsupportedAttachment), 0).show();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        AnonymousClass14(ArrayList arrayList, ArrayList arrayList2, String str, long j, MessageObject messageObject, ArrayList arrayList3) {
            this.val$paths = arrayList;
            this.val$originalPaths = arrayList2;
            this.val$mime = str;
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
            this.val$uris = arrayList3;
        }

        public void run() {
            Object obj;
            if (this.val$paths != null) {
                int i = 0;
                obj = null;
                while (i < this.val$paths.size()) {
                    Object obj2 = !SendMessagesHelper.prepareSendingDocumentInternal((String) this.val$paths.get(i), (String) this.val$originalPaths.get(i), null, this.val$mime, this.val$dialog_id, this.val$reply_to_msg, null) ? 1 : obj;
                    i++;
                    obj = obj2;
                }
            } else {
                obj = null;
            }
            if (this.val$uris != null) {
                for (int i2 = 0; i2 < this.val$uris.size(); i2++) {
                    if (!SendMessagesHelper.prepareSendingDocumentInternal(null, null, (Uri) this.val$uris.get(i2), this.val$mime, this.val$dialog_id, this.val$reply_to_msg, null)) {
                        obj = 1;
                    }
                }
            }
            if (obj != null) {
                AndroidUtilities.runOnUIThread(new C06481());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.15 */
    static class AnonymousClass15 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ HashMap val$params;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ BotInlineResult val$result;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.15.1 */
        class C06491 implements Runnable {
            final /* synthetic */ TL_document val$finalDocument;
            final /* synthetic */ TL_game val$finalGame;
            final /* synthetic */ String val$finalPathFinal;
            final /* synthetic */ TL_photo val$finalPhoto;

            C06491(TL_document tL_document, String str, TL_photo tL_photo, TL_game tL_game) {
                this.val$finalDocument = tL_document;
                this.val$finalPathFinal = str;
                this.val$finalPhoto = tL_photo;
                this.val$finalGame = tL_game;
            }

            public void run() {
                if (this.val$finalDocument != null) {
                    this.val$finalDocument.caption = AnonymousClass15.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(this.val$finalDocument, null, this.val$finalPathFinal, AnonymousClass15.this.val$dialog_id, AnonymousClass15.this.val$reply_to_msg, AnonymousClass15.this.val$result.send_message.reply_markup, AnonymousClass15.this.val$params);
                } else if (this.val$finalPhoto != null) {
                    this.val$finalPhoto.caption = AnonymousClass15.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(this.val$finalPhoto, AnonymousClass15.this.val$result.content_url, AnonymousClass15.this.val$dialog_id, AnonymousClass15.this.val$reply_to_msg, AnonymousClass15.this.val$result.send_message.reply_markup, AnonymousClass15.this.val$params);
                } else if (this.val$finalGame != null) {
                    SendMessagesHelper.getInstance().sendMessage(this.val$finalGame, AnonymousClass15.this.val$dialog_id, AnonymousClass15.this.val$result.send_message.reply_markup, AnonymousClass15.this.val$params);
                }
            }
        }

        AnonymousClass15(BotInlineResult botInlineResult, long j, HashMap hashMap, MessageObject messageObject) {
            this.val$result = botInlineResult;
            this.val$dialog_id = j;
            this.val$params = hashMap;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            TL_document tL_document;
            TL_game tL_game;
            TL_photo tL_photo;
            String str;
            if (!(this.val$result instanceof TL_botInlineMediaResult)) {
                if (this.val$result.content_url != null) {
                    File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.content_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.content_url, "file"));
                    String absolutePath = file.exists() ? file.getAbsolutePath() : this.val$result.content_url;
                    String str2 = this.val$result.type;
                    int i = -1;
                    switch (str2.hashCode()) {
                        case -1890252483:
                            if (str2.equals("sticker")) {
                                i = 4;
                                break;
                            }
                            break;
                        case 102340:
                            if (str2.equals("gif")) {
                                i = 5;
                                break;
                            }
                            break;
                        case 3143036:
                            if (str2.equals("file")) {
                                i = 2;
                                break;
                            }
                            break;
                        case 93166550:
                            if (str2.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                                i = 0;
                                break;
                            }
                            break;
                        case 106642994:
                            if (str2.equals("photo")) {
                                i = 6;
                                break;
                            }
                            break;
                        case 112202875:
                            if (str2.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                                i = 3;
                                break;
                            }
                            break;
                        case 112386354:
                            if (str2.equals("voice")) {
                                boolean z = true;
                                break;
                            }
                            break;
                    }
                    switch (i) {
                        case VideoPlayer.TRACK_DEFAULT /*0*/:
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                        case VideoPlayer.STATE_PREPARING /*2*/:
                        case VideoPlayer.STATE_BUFFERING /*3*/:
                        case VideoPlayer.STATE_READY /*4*/:
                        case VideoPlayer.STATE_ENDED /*5*/:
                            tL_document = new TL_document();
                            tL_document.id = 0;
                            tL_document.size = 0;
                            tL_document.dc_id = 0;
                            tL_document.mime_type = this.val$result.content_type;
                            tL_document.date = ConnectionsManager.getInstance().getCurrentTime();
                            TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                            tL_document.attributes.add(tL_documentAttributeFilename);
                            String str3 = this.val$result.type;
                            int i2 = -1;
                            switch (str3.hashCode()) {
                                case -1890252483:
                                    if (str3.equals("sticker")) {
                                        i2 = 5;
                                        break;
                                    }
                                    break;
                                case 102340:
                                    if (str3.equals("gif")) {
                                        i2 = 0;
                                        break;
                                    }
                                    break;
                                case 3143036:
                                    if (str3.equals("file")) {
                                        i2 = 3;
                                        break;
                                    }
                                    break;
                                case 93166550:
                                    if (str3.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                                        i2 = 2;
                                        break;
                                    }
                                    break;
                                case 112202875:
                                    if (str3.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                                        i2 = 4;
                                        break;
                                    }
                                    break;
                                case 112386354:
                                    if (str3.equals("voice")) {
                                        boolean z2 = true;
                                        break;
                                    }
                                    break;
                            }
                            Bitmap createVideoThumbnail;
                            TL_documentAttributeAudio tL_documentAttributeAudio;
                            switch (i2) {
                                case VideoPlayer.TRACK_DEFAULT /*0*/:
                                    tL_documentAttributeFilename.file_name = "animation.gif";
                                    if (absolutePath.endsWith("mp4")) {
                                        tL_document.mime_type = MimeTypes.VIDEO_MP4;
                                        tL_document.attributes.add(new TL_documentAttributeAnimated());
                                    } else {
                                        tL_document.mime_type = "image/gif";
                                    }
                                    try {
                                        createVideoThumbnail = absolutePath.endsWith("mp4") ? ThumbnailUtils.createVideoThumbnail(absolutePath, 1) : ImageLoader.loadBitmap(absolutePath, null, 90.0f, 90.0f, true);
                                        if (createVideoThumbnail != null) {
                                            tL_document.thumb = ImageLoader.scaleAndSaveImage(createVideoThumbnail, 90.0f, 90.0f, 55, false);
                                            createVideoThumbnail.recycle();
                                            break;
                                        }
                                    } catch (Throwable th) {
                                        FileLog.m18e("tmessages", th);
                                        break;
                                    }
                                    break;
                                case VideoPlayer.TYPE_AUDIO /*1*/:
                                    tL_documentAttributeAudio = new TL_documentAttributeAudio();
                                    tL_documentAttributeAudio.duration = this.val$result.duration;
                                    tL_documentAttributeAudio.voice = true;
                                    tL_documentAttributeFilename.file_name = "audio.ogg";
                                    tL_document.attributes.add(tL_documentAttributeAudio);
                                    tL_document.thumb = new TL_photoSizeEmpty();
                                    tL_document.thumb.type = "s";
                                    break;
                                case VideoPlayer.STATE_PREPARING /*2*/:
                                    tL_documentAttributeAudio = new TL_documentAttributeAudio();
                                    tL_documentAttributeAudio.duration = this.val$result.duration;
                                    tL_documentAttributeAudio.title = this.val$result.title;
                                    tL_documentAttributeAudio.flags |= 1;
                                    if (this.val$result.description != null) {
                                        tL_documentAttributeAudio.performer = this.val$result.description;
                                        tL_documentAttributeAudio.flags |= 2;
                                    }
                                    tL_documentAttributeFilename.file_name = "audio.mp3";
                                    tL_document.attributes.add(tL_documentAttributeAudio);
                                    tL_document.thumb = new TL_photoSizeEmpty();
                                    tL_document.thumb.type = "s";
                                    break;
                                case VideoPlayer.STATE_BUFFERING /*3*/:
                                    i2 = this.val$result.content_type.indexOf(47);
                                    if (i2 == -1) {
                                        tL_documentAttributeFilename.file_name = "file";
                                        break;
                                    } else {
                                        tL_documentAttributeFilename.file_name = "file." + this.val$result.content_type.substring(i2 + 1);
                                        break;
                                    }
                                case VideoPlayer.STATE_READY /*4*/:
                                    tL_documentAttributeFilename.file_name = "video.mp4";
                                    TL_documentAttributeVideo tL_documentAttributeVideo = new TL_documentAttributeVideo();
                                    tL_documentAttributeVideo.w = this.val$result.f2655w;
                                    tL_documentAttributeVideo.h = this.val$result.f2654h;
                                    tL_documentAttributeVideo.duration = this.val$result.duration;
                                    tL_document.attributes.add(tL_documentAttributeVideo);
                                    try {
                                        createVideoThumbnail = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "jpg")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (createVideoThumbnail != null) {
                                            tL_document.thumb = ImageLoader.scaleAndSaveImage(createVideoThumbnail, 90.0f, 90.0f, 55, false);
                                            createVideoThumbnail.recycle();
                                            break;
                                        }
                                    } catch (Throwable th2) {
                                        FileLog.m18e("tmessages", th2);
                                        break;
                                    }
                                    break;
                                case VideoPlayer.STATE_ENDED /*5*/:
                                    TL_documentAttributeSticker tL_documentAttributeSticker = new TL_documentAttributeSticker();
                                    tL_documentAttributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                                    tL_documentAttributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                    tL_document.attributes.add(tL_documentAttributeSticker);
                                    TL_documentAttributeImageSize tL_documentAttributeImageSize = new TL_documentAttributeImageSize();
                                    tL_documentAttributeImageSize.w = this.val$result.f2655w;
                                    tL_documentAttributeImageSize.h = this.val$result.f2654h;
                                    tL_document.attributes.add(tL_documentAttributeImageSize);
                                    tL_documentAttributeFilename.file_name = "sticker.webp";
                                    try {
                                        createVideoThumbnail = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "webp")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (createVideoThumbnail != null) {
                                            tL_document.thumb = ImageLoader.scaleAndSaveImage(createVideoThumbnail, 90.0f, 90.0f, 55, false);
                                            createVideoThumbnail.recycle();
                                            break;
                                        }
                                    } catch (Throwable th22) {
                                        FileLog.m18e("tmessages", th22);
                                        break;
                                    }
                                    break;
                            }
                            if (tL_documentAttributeFilename.file_name == null) {
                                tL_documentAttributeFilename.file_name = "file";
                            }
                            if (tL_document.mime_type == null) {
                                tL_document.mime_type = "application/octet-stream";
                            }
                            if (tL_document.thumb != null) {
                                tL_game = null;
                                tL_photo = null;
                                str = absolutePath;
                                break;
                            }
                            tL_document.thumb = new TL_photoSize();
                            tL_document.thumb.f2664w = this.val$result.f2655w;
                            tL_document.thumb.f2663h = this.val$result.f2654h;
                            tL_document.thumb.size = 0;
                            tL_document.thumb.location = new TL_fileLocationUnavailable();
                            tL_document.thumb.type = "x";
                            tL_game = null;
                            tL_photo = null;
                            str = absolutePath;
                            break;
                        case Method.TRACE /*6*/:
                            tL_photo = file.exists() ? SendMessagesHelper.getInstance().generatePhotoSizes(absolutePath, null) : null;
                            if (tL_photo == null) {
                                tL_photo = new TL_photo();
                                tL_photo.date = ConnectionsManager.getInstance().getCurrentTime();
                                TL_photoSize tL_photoSize = new TL_photoSize();
                                tL_photoSize.w = this.val$result.f2655w;
                                tL_photoSize.h = this.val$result.f2654h;
                                tL_photoSize.size = 1;
                                tL_photoSize.location = new TL_fileLocationUnavailable();
                                tL_photoSize.type = "x";
                                tL_photo.sizes.add(tL_photoSize);
                            }
                            tL_game = null;
                            tL_document = null;
                            str = absolutePath;
                            break;
                        default:
                            tL_game = null;
                            tL_photo = null;
                            tL_document = null;
                            str = absolutePath;
                            break;
                    }
                }
                tL_game = null;
                tL_photo = null;
                tL_document = null;
                str = null;
            } else if (this.val$result.type.equals("game")) {
                if (((int) this.val$dialog_id) != 0) {
                    tL_game = new TL_game();
                    tL_game.title = this.val$result.title;
                    tL_game.description = this.val$result.description;
                    tL_game.short_name = this.val$result.id;
                    tL_game.photo = this.val$result.photo;
                    if (this.val$result.document instanceof TL_document) {
                        tL_game.document = this.val$result.document;
                        tL_game.flags |= 1;
                        tL_photo = null;
                        tL_document = null;
                        str = null;
                    } else {
                        tL_photo = null;
                        tL_document = null;
                        str = null;
                    }
                } else {
                    return;
                }
            } else if (this.val$result.document != null) {
                if (this.val$result.document instanceof TL_document) {
                    tL_game = null;
                    tL_photo = null;
                    tL_document = (TL_document) this.val$result.document;
                    str = null;
                }
                tL_game = null;
                tL_photo = null;
                tL_document = null;
                str = null;
            } else {
                if (this.val$result.photo != null && (this.val$result.photo instanceof TL_photo)) {
                    tL_game = null;
                    tL_photo = (TL_photo) this.val$result.photo;
                    tL_document = null;
                    str = null;
                }
                tL_game = null;
                tL_photo = null;
                tL_document = null;
                str = null;
            }
            if (!(this.val$params == null || this.val$result.content_url == null)) {
                this.val$params.put("originalPath", this.val$result.content_url);
            }
            AndroidUtilities.runOnUIThread(new C06491(tL_document, str, tL_photo, tL_game));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.16 */
    static class AnonymousClass16 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$photos;
        final /* synthetic */ MessageObject val$reply_to_msg;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.16.1 */
        class C06501 implements Runnable {
            final /* synthetic */ TL_document val$documentFinal;
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ String val$pathFinal;

            C06501(TL_document tL_document, String str, HashMap hashMap) {
                this.val$documentFinal = tL_document;
                this.val$pathFinal = str;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$pathFinal, AnonymousClass16.this.val$dialog_id, AnonymousClass16.this.val$reply_to_msg, null, this.val$params);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.16.2 */
        class C06512 implements Runnable {
            final /* synthetic */ boolean val$needDownloadHttpFinal;
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ TL_photo val$photoFinal;
            final /* synthetic */ SearchImage val$searchImage;

            C06512(TL_photo tL_photo, boolean z, SearchImage searchImage, HashMap hashMap) {
                this.val$photoFinal = tL_photo;
                this.val$needDownloadHttpFinal = z;
                this.val$searchImage = searchImage;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$photoFinal, this.val$needDownloadHttpFinal ? this.val$searchImage.imageUrl : null, AnonymousClass16.this.val$dialog_id, AnonymousClass16.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass16(long j, ArrayList arrayList, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$photos = arrayList;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            boolean z = ((int) this.val$dialog_id) == 0;
            for (int i = 0; i < this.val$photos.size(); i++) {
                SearchImage searchImage = (SearchImage) this.val$photos.get(i);
                if (searchImage.type == 1) {
                    TL_document tL_document;
                    File pathToAttach;
                    HashMap hashMap = new HashMap();
                    TL_document tL_document2;
                    if (searchImage.document instanceof TL_document) {
                        tL_document2 = (TL_document) searchImage.document;
                        tL_document = tL_document2;
                        pathToAttach = FileLoader.getPathToAttach(tL_document2, true);
                    } else {
                        File file;
                        if (!z) {
                            Document document = (Document) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !z ? 1 : 4);
                            if (document instanceof TL_document) {
                                tL_document2 = (TL_document) document;
                                file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                                tL_document = tL_document2;
                                pathToAttach = file;
                            }
                        }
                        tL_document2 = null;
                        file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                        tL_document = tL_document2;
                        pathToAttach = file;
                    }
                    if (tL_document == null) {
                        if (searchImage.localUrl != null) {
                            hashMap.put("url", searchImage.localUrl);
                        }
                        File file2 = null;
                        tL_document = new TL_document();
                        tL_document.id = 0;
                        tL_document.date = ConnectionsManager.getInstance().getCurrentTime();
                        TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                        tL_documentAttributeFilename.file_name = "animation.gif";
                        tL_document.attributes.add(tL_documentAttributeFilename);
                        tL_document.size = searchImage.size;
                        tL_document.dc_id = 0;
                        if (pathToAttach.toString().endsWith("mp4")) {
                            tL_document.mime_type = MimeTypes.VIDEO_MP4;
                            tL_document.attributes.add(new TL_documentAttributeAnimated());
                        } else {
                            tL_document.mime_type = "image/gif";
                        }
                        if (pathToAttach.exists()) {
                            file2 = pathToAttach;
                        } else {
                            pathToAttach = null;
                        }
                        if (file2 == null) {
                            file2 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg"));
                            if (!file2.exists()) {
                                file2 = null;
                            }
                        }
                        if (file2 != null) {
                            try {
                                Bitmap createVideoThumbnail = file2.getAbsolutePath().endsWith("mp4") ? ThumbnailUtils.createVideoThumbnail(file2.getAbsolutePath(), 1) : ImageLoader.loadBitmap(file2.getAbsolutePath(), null, 90.0f, 90.0f, true);
                                if (createVideoThumbnail != null) {
                                    tL_document.thumb = ImageLoader.scaleAndSaveImage(createVideoThumbnail, 90.0f, 90.0f, 55, z);
                                    createVideoThumbnail.recycle();
                                }
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                        }
                        if (tL_document.thumb == null) {
                            tL_document.thumb = new TL_photoSize();
                            tL_document.thumb.f2664w = searchImage.width;
                            tL_document.thumb.f2663h = searchImage.height;
                            tL_document.thumb.size = 0;
                            tL_document.thumb.location = new TL_fileLocationUnavailable();
                            tL_document.thumb.type = "x";
                        }
                    }
                    if (searchImage.caption != null) {
                        tL_document.caption = searchImage.caption.toString();
                    }
                    String str = searchImage.imageUrl;
                    String file3 = pathToAttach == null ? searchImage.imageUrl : pathToAttach.toString();
                    if (!(hashMap == null || searchImage.imageUrl == null)) {
                        hashMap.put("originalPath", searchImage.imageUrl);
                    }
                    AndroidUtilities.runOnUIThread(new C06501(tL_document, file3, hashMap));
                } else {
                    boolean z2 = true;
                    TL_photo tL_photo = null;
                    if (!z) {
                        tL_photo = (TL_photo) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !z ? 0 : 3);
                    }
                    if (tL_photo == null) {
                        File file4 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                        if (file4.exists() && file4.length() != 0) {
                            tL_photo = SendMessagesHelper.getInstance().generatePhotoSizes(file4.toString(), null);
                            if (tL_photo != null) {
                                z2 = false;
                            }
                        }
                        if (tL_photo == null) {
                            file4 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg"));
                            if (file4.exists()) {
                                tL_photo = SendMessagesHelper.getInstance().generatePhotoSizes(file4.toString(), null);
                            }
                            if (tL_photo == null) {
                                tL_photo = new TL_photo();
                                tL_photo.date = ConnectionsManager.getInstance().getCurrentTime();
                                TL_photoSize tL_photoSize = new TL_photoSize();
                                tL_photoSize.w = searchImage.width;
                                tL_photoSize.h = searchImage.height;
                                tL_photoSize.size = 0;
                                tL_photoSize.location = new TL_fileLocationUnavailable();
                                tL_photoSize.type = "x";
                                tL_photo.sizes.add(tL_photoSize);
                            }
                        }
                    }
                    if (tL_photo != null) {
                        if (searchImage.caption != null) {
                            tL_photo.caption = searchImage.caption.toString();
                        }
                        HashMap hashMap2 = new HashMap();
                        if (searchImage.imageUrl != null) {
                            hashMap2.put("originalPath", searchImage.imageUrl);
                        }
                        AndroidUtilities.runOnUIThread(new C06512(tL_photo, z2, searchImage, hashMap2));
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.17 */
    static class AnonymousClass17 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ String val$text;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.17.1 */
        class C06531 implements Runnable {

            /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.17.1.1 */
            class C06521 implements Runnable {
                C06521() {
                }

                public void run() {
                    String access$1100 = SendMessagesHelper.getTrimmedString(AnonymousClass17.this.val$text);
                    if (access$1100.length() != 0) {
                        int ceil = (int) Math.ceil((double) (((float) access$1100.length()) / 4096.0f));
                        for (int i = 0; i < ceil; i++) {
                            SendMessagesHelper.getInstance().sendMessage(access$1100.substring(i * ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, Math.min((i + 1) * ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, access$1100.length())), AnonymousClass17.this.val$dialog_id, null, null, true, null, null, null);
                        }
                    }
                }
            }

            C06531() {
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C06521());
            }
        }

        AnonymousClass17(String str, long j) {
            this.val$text = str;
            this.val$dialog_id = j;
        }

        public void run() {
            Utilities.stageQueue.postRunnable(new C06531());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.18 */
    static class AnonymousClass18 implements Runnable {
        final /* synthetic */ ArrayList val$captions;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$masks;
        final /* synthetic */ ArrayList val$pathsCopy;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ ArrayList val$urisCopy;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.18.1 */
        class C06541 implements Runnable {
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ TL_photo val$photoFinal;

            C06541(TL_photo tL_photo, HashMap hashMap) {
                this.val$photoFinal = tL_photo;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$photoFinal, null, AnonymousClass18.this.val$dialog_id, AnonymousClass18.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass18(long j, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$pathsCopy = arrayList;
            this.val$urisCopy = arrayList2;
            this.val$captions = arrayList3;
            this.val$masks = arrayList4;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            Object obj = ((int) this.val$dialog_id) == 0 ? 1 : null;
            ArrayList arrayList = null;
            ArrayList arrayList2 = null;
            ArrayList arrayList3 = null;
            int size = !this.val$pathsCopy.isEmpty() ? this.val$pathsCopy.size() : this.val$urisCopy.size();
            String str = null;
            Uri uri = null;
            String str2 = null;
            int i = 0;
            while (i < size) {
                Uri uri2;
                String str3;
                String str4;
                String a;
                String str5;
                if (this.val$pathsCopy.isEmpty()) {
                    uri2 = !this.val$urisCopy.isEmpty() ? (Uri) this.val$urisCopy.get(i) : uri;
                } else {
                    uri2 = uri;
                    str = (String) this.val$pathsCopy.get(i);
                }
                if (str != null || uri2 == null) {
                    str3 = str;
                    str4 = str;
                } else {
                    str3 = AndroidUtilities.getPath(uri2);
                    str4 = uri2.toString();
                }
                Object obj2 = null;
                if (str3 == null || !(str3.endsWith(".gif") || str3.endsWith(".webp"))) {
                    if (str3 == null && uri2 != null) {
                        if (MediaController.m103c(uri2)) {
                            obj2 = 1;
                            str2 = uri2.toString();
                            a = MediaController.m77a(uri2, "gif");
                            str4 = str2;
                            str5 = "gif";
                        } else if (MediaController.m95b(uri2)) {
                            obj2 = 1;
                            str2 = uri2.toString();
                            a = MediaController.m77a(uri2, "webp");
                            str4 = str2;
                            str5 = "webp";
                        }
                    }
                    a = str3;
                    str5 = str2;
                } else {
                    a = str3;
                    str5 = str3.endsWith(".gif") ? "gif" : "webp";
                    obj2 = 1;
                }
                if (obj2 != null) {
                    ArrayList arrayList4;
                    ArrayList arrayList5;
                    ArrayList arrayList6;
                    if (arrayList == null) {
                        arrayList4 = new ArrayList();
                        arrayList5 = new ArrayList();
                        arrayList6 = new ArrayList();
                    } else {
                        arrayList6 = arrayList3;
                        arrayList5 = arrayList2;
                        arrayList4 = arrayList;
                    }
                    arrayList4.add(a);
                    arrayList5.add(str4);
                    arrayList6.add(this.val$captions != null ? (String) this.val$captions.get(i) : null);
                    arrayList3 = arrayList6;
                    arrayList2 = arrayList5;
                    arrayList = arrayList4;
                } else {
                    if (a != null) {
                        File file = new File(a);
                        str4 = str4 + file.length() + "_" + file.lastModified();
                    } else {
                        str4 = null;
                    }
                    TL_photo tL_photo = null;
                    if (obj == null) {
                        tL_photo = (TL_photo) MessagesStorage.getInstance().getSentFile(str4, obj == null ? 0 : 3);
                        if (tL_photo == null && uri2 != null) {
                            tL_photo = (TL_photo) MessagesStorage.getInstance().getSentFile(AndroidUtilities.getPath(uri2), obj == null ? 0 : 3);
                        }
                    }
                    TL_photo generatePhotoSizes = tL_photo == null ? SendMessagesHelper.getInstance().generatePhotoSizes(str, uri2) : tL_photo;
                    if (generatePhotoSizes != null) {
                        HashMap hashMap = new HashMap();
                        if (this.val$captions != null) {
                            generatePhotoSizes.caption = (String) this.val$captions.get(i);
                        }
                        if (this.val$masks != null) {
                            ArrayList arrayList7 = (ArrayList) this.val$masks.get(i);
                            boolean z = (arrayList7 == null || arrayList7.isEmpty()) ? false : true;
                            generatePhotoSizes.has_stickers = z;
                            if (z) {
                                AbstractSerializedData serializedData = new SerializedData((arrayList7.size() * 20) + 4);
                                serializedData.writeInt32(arrayList7.size());
                                for (int i2 = 0; i2 < arrayList7.size(); i2++) {
                                    ((InputDocument) arrayList7.get(i2)).serializeToStream(serializedData);
                                }
                                hashMap.put("masks", Utilities.bytesToHex(serializedData.toByteArray()));
                            }
                        }
                        if (str4 != null) {
                            hashMap.put("originalPath", str4);
                        }
                        AndroidUtilities.runOnUIThread(new C06541(generatePhotoSizes, hashMap));
                    } else {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                            arrayList2 = new ArrayList();
                            arrayList3 = new ArrayList();
                        }
                        arrayList.add(a);
                        arrayList2.add(str4);
                        arrayList3.add(this.val$captions != null ? (String) this.val$captions.get(i) : null);
                    }
                }
                i++;
                str2 = str5;
                uri = uri2;
            }
            if (arrayList != null && !arrayList.isEmpty()) {
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    SendMessagesHelper.prepareSendingDocumentInternal((String) arrayList.get(i3), (String) arrayList2.get(i3), null, str2, this.val$dialog_id, this.val$reply_to_msg, (String) arrayList3.get(i3));
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.19 */
    static class AnonymousClass19 implements Runnable {
        final /* synthetic */ String val$caption;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ long val$duration;
        final /* synthetic */ long val$estimatedSize;
        final /* synthetic */ int val$height;
        final /* synthetic */ MessageObject val$reply_to_msg;
        final /* synthetic */ VideoEditedInfo val$videoEditedInfo;
        final /* synthetic */ String val$videoPath;
        final /* synthetic */ int val$width;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.19.1 */
        class C06551 implements Runnable {
            final /* synthetic */ String val$finalPath;
            final /* synthetic */ HashMap val$params;
            final /* synthetic */ TL_document val$videoFinal;

            C06551(TL_document tL_document, String str, HashMap hashMap) {
                this.val$videoFinal = tL_document;
                this.val$finalPath = str;
                this.val$params = hashMap;
            }

            public void run() {
                SendMessagesHelper.getInstance().sendMessage(this.val$videoFinal, AnonymousClass19.this.val$videoEditedInfo, this.val$finalPath, AnonymousClass19.this.val$dialog_id, AnonymousClass19.this.val$reply_to_msg, null, this.val$params);
            }
        }

        AnonymousClass19(long j, VideoEditedInfo videoEditedInfo, String str, long j2, int i, int i2, long j3, String str2, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$videoEditedInfo = videoEditedInfo;
            this.val$videoPath = str;
            this.val$duration = j2;
            this.val$height = i;
            this.val$width = i2;
            this.val$estimatedSize = j3;
            this.val$caption = str2;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            TL_document tL_document = null;
            boolean z = ((int) this.val$dialog_id) == 0;
            if (this.val$videoEditedInfo != null || this.val$videoPath.endsWith("mp4")) {
                String str = this.val$videoPath;
                String str2 = this.val$videoPath;
                File file = new File(str2);
                Object obj = str2 + file.length() + "_" + file.lastModified();
                if (this.val$videoEditedInfo != null) {
                    obj = obj + this.val$duration + "_" + this.val$videoEditedInfo.startTime + "_" + this.val$videoEditedInfo.endTime;
                    if (this.val$videoEditedInfo.resultWidth == this.val$videoEditedInfo.originalWidth) {
                        obj = obj + "_" + this.val$videoEditedInfo.resultWidth;
                    }
                }
                String str3;
                PhotoSize scaleAndSaveImage;
                TL_document tL_document2;
                TL_documentAttributeVideo tL_documentAttributeVideo;
                VideoEditedInfo videoEditedInfo;
                VideoEditedInfo videoEditedInfo2;
                int i;
                int i2;
                String str4;
                File file2;
                HashMap hashMap;
                if (z) {
                    if (null != null) {
                        str3 = str;
                    } else {
                        scaleAndSaveImage = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(this.val$videoPath, 1), 90.0f, 90.0f, 55, z);
                        tL_document2 = new TL_document();
                        tL_document2.thumb = scaleAndSaveImage;
                        if (tL_document2.thumb != null) {
                            tL_document2.thumb.type = "s";
                        } else {
                            tL_document2.thumb = new TL_photoSizeEmpty();
                            tL_document2.thumb.type = "s";
                        }
                        tL_document2.mime_type = MimeTypes.VIDEO_MP4;
                        UserConfig.saveConfig(false);
                        tL_documentAttributeVideo = new TL_documentAttributeVideo();
                        tL_document2.attributes.add(tL_documentAttributeVideo);
                        if (this.val$videoEditedInfo == null) {
                            if (file.exists()) {
                                tL_document2.size = (int) file.length();
                            }
                            SendMessagesHelper.fillVideoAttribute(this.val$videoPath, tL_documentAttributeVideo, null);
                            tL_document = tL_document2;
                            str3 = str;
                        } else {
                            if (this.val$videoEditedInfo.bitrate != -1) {
                                tL_documentAttributeVideo.duration = (int) (this.val$duration / 1000);
                                if (this.val$videoEditedInfo.rotationValue != 90) {
                                }
                                tL_documentAttributeVideo.w = this.val$height;
                                tL_documentAttributeVideo.h = this.val$width;
                            } else {
                                tL_document2.attributes.add(new TL_documentAttributeAnimated());
                                SendMessagesHelper.fillVideoAttribute(this.val$videoPath, tL_documentAttributeVideo, this.val$videoEditedInfo);
                                videoEditedInfo = this.val$videoEditedInfo;
                                videoEditedInfo2 = this.val$videoEditedInfo;
                                i = tL_documentAttributeVideo.w;
                                videoEditedInfo2.resultWidth = i;
                                videoEditedInfo.originalWidth = i;
                                videoEditedInfo = this.val$videoEditedInfo;
                                videoEditedInfo2 = this.val$videoEditedInfo;
                                i2 = tL_documentAttributeVideo.h;
                                videoEditedInfo2.resultHeight = i2;
                                videoEditedInfo.originalHeight = i2;
                            }
                            tL_document2.size = (int) this.val$estimatedSize;
                            str4 = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
                            UserConfig.lastLocalId--;
                            file2 = new File(FileLoader.getInstance().getDirectory(4), str4);
                            UserConfig.saveConfig(false);
                            str4 = file2.getAbsolutePath();
                            tL_document = tL_document2;
                            str3 = str4;
                        }
                    }
                    tL_document.caption = this.val$videoEditedInfo.caption;
                    hashMap = new HashMap();
                    tL_document.caption = this.val$caption;
                    if (obj != null) {
                        hashMap.put("originalPath", obj);
                    }
                    AndroidUtilities.runOnUIThread(new C06551(tL_document, str3, hashMap));
                    return;
                }
                if (null != null) {
                    scaleAndSaveImage = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(this.val$videoPath, 1), 90.0f, 90.0f, 55, z);
                    tL_document2 = new TL_document();
                    tL_document2.thumb = scaleAndSaveImage;
                    if (tL_document2.thumb != null) {
                        tL_document2.thumb = new TL_photoSizeEmpty();
                        tL_document2.thumb.type = "s";
                    } else {
                        tL_document2.thumb.type = "s";
                    }
                    tL_document2.mime_type = MimeTypes.VIDEO_MP4;
                    UserConfig.saveConfig(false);
                    tL_documentAttributeVideo = new TL_documentAttributeVideo();
                    tL_document2.attributes.add(tL_documentAttributeVideo);
                    if (this.val$videoEditedInfo == null) {
                        if (this.val$videoEditedInfo.bitrate != -1) {
                            tL_document2.attributes.add(new TL_documentAttributeAnimated());
                            SendMessagesHelper.fillVideoAttribute(this.val$videoPath, tL_documentAttributeVideo, this.val$videoEditedInfo);
                            videoEditedInfo = this.val$videoEditedInfo;
                            videoEditedInfo2 = this.val$videoEditedInfo;
                            i = tL_documentAttributeVideo.w;
                            videoEditedInfo2.resultWidth = i;
                            videoEditedInfo.originalWidth = i;
                            videoEditedInfo = this.val$videoEditedInfo;
                            videoEditedInfo2 = this.val$videoEditedInfo;
                            i2 = tL_documentAttributeVideo.h;
                            videoEditedInfo2.resultHeight = i2;
                            videoEditedInfo.originalHeight = i2;
                        } else {
                            tL_documentAttributeVideo.duration = (int) (this.val$duration / 1000);
                            if (this.val$videoEditedInfo.rotationValue != 90 || this.val$videoEditedInfo.rotationValue == 270) {
                                tL_documentAttributeVideo.w = this.val$height;
                                tL_documentAttributeVideo.h = this.val$width;
                            } else {
                                tL_documentAttributeVideo.w = this.val$width;
                                tL_documentAttributeVideo.h = this.val$height;
                            }
                        }
                        tL_document2.size = (int) this.val$estimatedSize;
                        str4 = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
                        UserConfig.lastLocalId--;
                        file2 = new File(FileLoader.getInstance().getDirectory(4), str4);
                        UserConfig.saveConfig(false);
                        str4 = file2.getAbsolutePath();
                        tL_document = tL_document2;
                        str3 = str4;
                    } else {
                        if (file.exists()) {
                            tL_document2.size = (int) file.length();
                        }
                        SendMessagesHelper.fillVideoAttribute(this.val$videoPath, tL_documentAttributeVideo, null);
                        tL_document = tL_document2;
                        str3 = str;
                    }
                } else {
                    str3 = str;
                }
                if (!(this.val$videoEditedInfo == null || this.val$videoEditedInfo.caption == null)) {
                    tL_document.caption = this.val$videoEditedInfo.caption;
                }
                hashMap = new HashMap();
                tL_document.caption = this.val$caption;
                if (obj != null) {
                    hashMap.put("originalPath", obj);
                }
                AndroidUtilities.runOnUIThread(new C06551(tL_document, str3, hashMap));
                return;
            }
            SendMessagesHelper.prepareSendingDocumentInternal(this.val$videoPath, this.val$videoPath, null, null, this.val$dialog_id, this.val$reply_to_msg, this.val$caption);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.1 */
    class C06561 implements LocationProviderDelegate {
        C06561() {
        }

        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap hashMap = new HashMap(SendMessagesHelper.this.waitingForLocation);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, hashMap);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.2 */
    class C06582 implements Runnable {
        final /* synthetic */ File val$cacheFile;
        final /* synthetic */ DelayedMessage val$message;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.2.1 */
        class C06571 implements Runnable {
            final /* synthetic */ TL_photo val$photo;

            C06571(TL_photo tL_photo) {
                this.val$photo = tL_photo;
            }

            public void run() {
                if (this.val$photo != null) {
                    C06582.this.val$message.httpLocation = null;
                    C06582.this.val$message.obj.messageOwner.media.photo = this.val$photo;
                    C06582.this.val$message.obj.messageOwner.attachPath = C06582.this.val$cacheFile.toString();
                    C06582.this.val$message.location = ((PhotoSize) this.val$photo.sizes.get(this.val$photo.sizes.size() - 1)).location;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(C06582.this.val$message.obj.messageOwner);
                    MessagesStorage.getInstance().putMessages(arrayList, false, true, false, 0);
                    SendMessagesHelper.this.performSendDelayedMessage(C06582.this.val$message);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, C06582.this.val$message.obj);
                    return;
                }
                FileLog.m16e("tmessages", "can't load image " + C06582.this.val$message.httpLocation + " to file " + C06582.this.val$cacheFile.toString());
                MessagesStorage.getInstance().markMessageAsSendError(C06582.this.val$message.obj.messageOwner);
                C06582.this.val$message.obj.messageOwner.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(C06582.this.val$message.obj.getId()));
                SendMessagesHelper.this.processSentMessage(C06582.this.val$message.obj.getId());
            }
        }

        C06582(File file, DelayedMessage delayedMessage) {
            this.val$cacheFile = file;
            this.val$message = delayedMessage;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C06571(SendMessagesHelper.getInstance().generatePhotoSizes(this.val$cacheFile.toString(), null)));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.3 */
    class C06603 implements Runnable {
        final /* synthetic */ File val$cacheFile;
        final /* synthetic */ DelayedMessage val$message;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.3.1 */
        class C06591 implements Runnable {
            C06591() {
            }

            public void run() {
                C06603.this.val$message.httpLocation = null;
                C06603.this.val$message.obj.messageOwner.attachPath = C06603.this.val$cacheFile.toString();
                C06603.this.val$message.location = C06603.this.val$message.documentLocation.thumb.location;
                ArrayList arrayList = new ArrayList();
                arrayList.add(C06603.this.val$message.obj.messageOwner);
                MessagesStorage.getInstance().putMessages(arrayList, false, true, false, 0);
                SendMessagesHelper.this.performSendDelayedMessage(C06603.this.val$message);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, C06603.this.val$message.obj);
            }
        }

        C06603(DelayedMessage delayedMessage, File file) {
            this.val$message = delayedMessage;
            this.val$cacheFile = file;
        }

        public void run() {
            boolean z = true;
            if (this.val$message.documentLocation.thumb.location instanceof TL_fileLocationUnavailable) {
                try {
                    Bitmap loadBitmap = ImageLoader.loadBitmap(this.val$cacheFile.getAbsolutePath(), null, 90.0f, 90.0f, true);
                    if (loadBitmap != null) {
                        TL_document tL_document = this.val$message.documentLocation;
                        if (this.val$message.sendEncryptedRequest == null) {
                            z = false;
                        }
                        tL_document.thumb = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, z);
                        loadBitmap.recycle();
                    }
                } catch (Throwable e) {
                    this.val$message.documentLocation.thumb = null;
                    FileLog.m18e("tmessages", e);
                }
                if (this.val$message.documentLocation.thumb == null) {
                    this.val$message.documentLocation.thumb = new TL_photoSizeEmpty();
                    this.val$message.documentLocation.thumb.type = "s";
                }
            }
            AndroidUtilities.runOnUIThread(new C06591());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.4 */
    class C06654 implements RequestDelegate {
        final /* synthetic */ Long val$archMsgInfoId;
        final /* synthetic */ boolean val$isMegagroupFinal;
        final /* synthetic */ HashMap val$messagesByRandomIdsFinal;
        final /* synthetic */ ArrayList val$newMsgArr;
        final /* synthetic */ ArrayList val$newMsgObjArr;
        final /* synthetic */ long val$peer;
        final /* synthetic */ Peer val$to_id;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.4.1 */
        class C06621 implements Runnable {
            final /* synthetic */ Message val$message;
            final /* synthetic */ Message val$newMsgObj;
            final /* synthetic */ int val$oldId;
            final /* synthetic */ ArrayList val$sentMessages;

            /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.4.1.1 */
            class C06611 implements Runnable {
                C06611() {
                }

                public void run() {
                    C06621.this.val$newMsgObj.send_state = 0;
                    SearchQuery.increasePeerRaiting(C06654.this.val$peer);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(C06621.this.val$oldId), Integer.valueOf(C06621.this.val$message.id), C06621.this.val$message, Long.valueOf(C06654.this.val$peer));
                    SendMessagesHelper.this.processSentMessage(C06621.this.val$oldId);
                    SendMessagesHelper.this.removeFromSendingMessages(C06621.this.val$oldId);
                }
            }

            C06621(Message message, int i, ArrayList arrayList, Message message2) {
                this.val$newMsgObj = message;
                this.val$oldId = i;
                this.val$sentMessages = arrayList;
                this.val$message = message2;
            }

            public void run() {
                MessagesStorage.getInstance().updateMessageStateAndId(this.val$newMsgObj.random_id, Integer.valueOf(this.val$oldId), this.val$newMsgObj.id, 0, false, C06654.this.val$to_id.channel_id);
                if (C06654.this.val$archMsgInfoId != null) {
                    ArchiveUtil.m270c(C06654.this.val$archMsgInfoId, this.val$newMsgObj.id);
                    MessagesController.getInstance().markDialogAsRead(this.val$newMsgObj.dialog_id, this.val$newMsgObj.id, Math.max(0, this.val$newMsgObj.id), this.val$newMsgObj.date, true, true);
                }
                MessagesStorage.getInstance().putMessages(this.val$sentMessages, true, false, false, 0);
                AndroidUtilities.runOnUIThread(new C06611());
                if (MessageObject.isVideoMessage(this.val$newMsgObj) || MessageObject.isNewGifMessage(this.val$newMsgObj)) {
                    SendMessagesHelper.this.stopVideoService(this.val$newMsgObj.attachPath);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.4.2 */
        class C06632 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C06632(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (this.val$error.text.equals("PEER_FLOOD")) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(0));
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.4.3 */
        class C06643 implements Runnable {
            final /* synthetic */ Message val$newMsgObj;

            C06643(Message message) {
                this.val$newMsgObj = message;
            }

            public void run() {
                this.val$newMsgObj.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.val$newMsgObj.id));
                SendMessagesHelper.this.processSentMessage(this.val$newMsgObj.id);
                if (MessageObject.isVideoMessage(this.val$newMsgObj) || MessageObject.isNewGifMessage(this.val$newMsgObj)) {
                    SendMessagesHelper.this.stopVideoService(this.val$newMsgObj.attachPath);
                }
                SendMessagesHelper.this.removeFromSendingMessages(this.val$newMsgObj.id);
            }
        }

        C06654(long j, boolean z, HashMap hashMap, ArrayList arrayList, ArrayList arrayList2, Peer peer, Long l) {
            this.val$peer = j;
            this.val$isMegagroupFinal = z;
            this.val$messagesByRandomIdsFinal = hashMap;
            this.val$newMsgObjArr = arrayList;
            this.val$newMsgArr = arrayList2;
            this.val$to_id = peer;
            this.val$archMsgInfoId = l;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            int i = 0;
            if (tL_error == null) {
                Update update;
                HashMap hashMap = new HashMap();
                Updates updates = (Updates) tLObject;
                int i2 = 0;
                while (i2 < updates.updates.size()) {
                    update = (Update) updates.updates.get(i2);
                    if (update instanceof TL_updateMessageID) {
                        TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) update;
                        hashMap.put(Integer.valueOf(tL_updateMessageID.id), Long.valueOf(tL_updateMessageID.random_id));
                        updates.updates.remove(i2);
                        i2--;
                    }
                    i2++;
                }
                Integer num = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(this.val$peer));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$peer));
                    MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(this.val$peer), num);
                }
                Integer num2 = num;
                for (int i3 = 0; i3 < updates.updates.size(); i3++) {
                    update = (Update) updates.updates.get(i3);
                    if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                        Message message;
                        if (update instanceof TL_updateNewMessage) {
                            message = ((TL_updateNewMessage) update).message;
                            MessagesController.getInstance().processNewDifferenceParams(-1, update.pts, -1, update.pts_count);
                        } else {
                            message = ((TL_updateNewChannelMessage) update).message;
                            MessagesController.getInstance().processNewChannelDifferenceParams(update.pts, update.pts_count, message.to_id.channel_id);
                            if (this.val$isMegagroupFinal) {
                                message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                            }
                        }
                        message.unread = num2.intValue() < message.id;
                        Long l = (Long) hashMap.get(Integer.valueOf(message.id));
                        if (l != null) {
                            Message message2 = (Message) this.val$messagesByRandomIdsFinal.get(l);
                            if (message2 != null) {
                                i2 = this.val$newMsgObjArr.indexOf(message2);
                                if (i2 != -1) {
                                    MessageObject messageObject = (MessageObject) this.val$newMsgArr.get(i2);
                                    this.val$newMsgObjArr.remove(i2);
                                    this.val$newMsgArr.remove(i2);
                                    int i4 = message2.id;
                                    ArrayList arrayList = new ArrayList();
                                    arrayList.add(message);
                                    message2.id = message.id;
                                    SendMessagesHelper.this.updateMediaPaths(messageObject, message, null, true);
                                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06621(message2, i4, arrayList, message));
                                }
                            }
                        }
                    }
                }
            } else {
                AndroidUtilities.runOnUIThread(new C06632(tL_error));
            }
            while (i < this.val$newMsgObjArr.size()) {
                Message message3 = (Message) this.val$newMsgObjArr.get(i);
                MessagesStorage.getInstance().markMessageAsSendError(message3);
                AndroidUtilities.runOnUIThread(new C06643(message3));
                i++;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.5 */
    class C06685 implements RequestDelegate {
        final /* synthetic */ Runnable val$callback;
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.5.1 */
        class C06661 implements Runnable {
            C06661() {
            }

            public void run() {
                C06685.this.val$callback.run();
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.5.2 */
        class C06672 implements Runnable {
            C06672() {
            }

            public void run() {
                Builder builder = new Builder(C06685.this.val$fragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setMessage(LocaleController.getString("EditMessageError", C0338R.string.EditMessageError));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                C06685.this.val$fragment.showDialog(builder.create());
            }
        }

        C06685(Runnable runnable, BaseFragment baseFragment) {
            this.val$callback = runnable;
            this.val$fragment = baseFragment;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C06661());
            if (tL_error == null) {
                MessagesController.getInstance().processUpdates((Updates) tLObject, false);
            } else if (!tL_error.text.equals("MESSAGE_NOT_MODIFIED")) {
                AndroidUtilities.runOnUIThread(new C06672());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.6 */
    class C06706 implements RequestDelegate {
        final /* synthetic */ KeyboardButton val$button;
        final /* synthetic */ String val$key;
        final /* synthetic */ MessageObject val$messageObject;
        final /* synthetic */ ChatActivity val$parentFragment;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.6.1 */
        class C06691 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C06691(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                TL_game tL_game = null;
                SendMessagesHelper.this.waitingForCallback.remove(C06706.this.val$key);
                if (this.val$response != null) {
                    TL_messages_botCallbackAnswer tL_messages_botCallbackAnswer = (TL_messages_botCallbackAnswer) this.val$response;
                    User user;
                    if (tL_messages_botCallbackAnswer.message != null) {
                        if (!tL_messages_botCallbackAnswer.alert) {
                            String formatName;
                            int i = C06706.this.val$messageObject.messageOwner.from_id;
                            if (C06706.this.val$messageObject.messageOwner.via_bot_id != 0) {
                                i = C06706.this.val$messageObject.messageOwner.via_bot_id;
                            }
                            if (i > 0) {
                                user = MessagesController.getInstance().getUser(Integer.valueOf(i));
                                if (user != null) {
                                    formatName = ContactsController.formatName(user.first_name, user.last_name);
                                }
                                formatName = null;
                            } else {
                                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
                                if (chat != null) {
                                    formatName = chat.title;
                                }
                                formatName = null;
                            }
                            if (formatName == null) {
                                formatName = "bot";
                            }
                            C06706.this.val$parentFragment.showAlert(formatName, tL_messages_botCallbackAnswer.message);
                        } else if (C06706.this.val$parentFragment.getParentActivity() != null) {
                            Builder builder = new Builder(C06706.this.val$parentFragment.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                            builder.setMessage(tL_messages_botCallbackAnswer.message);
                            C06706.this.val$parentFragment.showDialog(builder.create());
                        }
                    } else if (tL_messages_botCallbackAnswer.url != null && C06706.this.val$parentFragment.getParentActivity() != null) {
                        int i2 = C06706.this.val$messageObject.messageOwner.from_id;
                        if (C06706.this.val$messageObject.messageOwner.via_bot_id != 0) {
                            i2 = C06706.this.val$messageObject.messageOwner.via_bot_id;
                        }
                        user = MessagesController.getInstance().getUser(Integer.valueOf(i2));
                        boolean z = user != null && user.verified;
                        if (C06706.this.val$button instanceof TL_keyboardButtonGame) {
                            if (C06706.this.val$messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                                tL_game = C06706.this.val$messageObject.messageOwner.media.game;
                            }
                            if (tL_game != null) {
                                ChatActivity chatActivity = C06706.this.val$parentFragment;
                                MessageObject messageObject = C06706.this.val$messageObject;
                                String str = tL_messages_botCallbackAnswer.url;
                                z = !z && ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("askgame_" + i2, true);
                                chatActivity.showOpenGameAlert(tL_game, messageObject, str, z, i2);
                                return;
                            }
                            return;
                        }
                        C06706.this.val$parentFragment.showOpenUrlAlert(tL_messages_botCallbackAnswer.url, false);
                    }
                }
            }
        }

        C06706(String str, ChatActivity chatActivity, MessageObject messageObject, KeyboardButton keyboardButton) {
            this.val$key = str;
            this.val$parentFragment = chatActivity;
            this.val$messageObject = messageObject;
            this.val$button = keyboardButton;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C06691(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.7 */
    class C06717 implements RequestDelegate {
        final /* synthetic */ long val$newTaskId;

        C06717(long j) {
            this.val$newTaskId = j;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesController.getInstance().processUpdates((Updates) tLObject, false);
            }
            if (this.val$newTaskId != 0) {
                MessagesStorage.getInstance().removePendingTask(this.val$newTaskId);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.8 */
    class C06738 implements Runnable {
        final /* synthetic */ String val$path;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.8.1 */
        class C06721 implements Runnable {
            C06721() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.stopEncodingService, C06738.this.val$path);
            }
        }

        C06738(String str) {
            this.val$path = str;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C06721());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.9 */
    class C06809 implements RequestDelegate {
        final /* synthetic */ MessageObject val$msgObj;
        final /* synthetic */ Message val$newMsgObj;
        final /* synthetic */ String val$originalPath;
        final /* synthetic */ TLObject val$req;

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.9.1 */
        class C06791 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.9.1.1 */
            class C06741 implements Runnable {
                final /* synthetic */ TL_updateShortSentMessage val$res;

                C06741(TL_updateShortSentMessage tL_updateShortSentMessage) {
                    this.val$res = tL_updateShortSentMessage;
                }

                public void run() {
                    MessagesController.getInstance().processNewDifferenceParams(-1, this.val$res.pts, this.val$res.date, this.val$res.pts_count);
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.9.1.2 */
            class C06752 implements Runnable {
                final /* synthetic */ TL_updateNewMessage val$newMessage;

                C06752(TL_updateNewMessage tL_updateNewMessage) {
                    this.val$newMessage = tL_updateNewMessage;
                }

                public void run() {
                    MessagesController.getInstance().processNewDifferenceParams(-1, this.val$newMessage.pts, -1, this.val$newMessage.pts_count);
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.9.1.3 */
            class C06763 implements Runnable {
                final /* synthetic */ TL_updateNewChannelMessage val$newMessage;

                C06763(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
                    this.val$newMessage = tL_updateNewChannelMessage;
                }

                public void run() {
                    MessagesController.getInstance().processNewChannelDifferenceParams(this.val$newMessage.pts, this.val$newMessage.pts_count, this.val$newMessage.message.to_id.channel_id);
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.9.1.4 */
            class C06784 implements Runnable {
                final /* synthetic */ String val$attachPath;
                final /* synthetic */ boolean val$isBroadcast;
                final /* synthetic */ int val$oldId;
                final /* synthetic */ ArrayList val$sentMessages;

                /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.9.1.4.1 */
                class C06771 implements Runnable {
                    C06771() {
                    }

                    public void run() {
                        if (C06784.this.val$isBroadcast) {
                            for (int i = 0; i < C06784.this.val$sentMessages.size(); i++) {
                                Message message = (Message) C06784.this.val$sentMessages.get(i);
                                ArrayList arrayList = new ArrayList();
                                MessageObject messageObject = new MessageObject(message, null, false);
                                arrayList.add(messageObject);
                                MessagesController.getInstance().updateInterfaceWithMessages(messageObject.getDialogId(), arrayList, true);
                            }
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        }
                        SearchQuery.increasePeerRaiting(C06809.this.val$newMsgObj.dialog_id);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i2 = NotificationCenter.messageReceivedByServer;
                        Object[] objArr = new Object[4];
                        objArr[0] = Integer.valueOf(C06784.this.val$oldId);
                        objArr[1] = Integer.valueOf(C06784.this.val$isBroadcast ? C06784.this.val$oldId : C06809.this.val$newMsgObj.id);
                        objArr[2] = C06809.this.val$newMsgObj;
                        objArr[3] = Long.valueOf(C06809.this.val$newMsgObj.dialog_id);
                        instance.postNotificationName(i2, objArr);
                        SendMessagesHelper.this.processSentMessage(C06784.this.val$oldId);
                        SendMessagesHelper.this.removeFromSendingMessages(C06784.this.val$oldId);
                    }
                }

                C06784(int i, boolean z, ArrayList arrayList, String str) {
                    this.val$oldId = i;
                    this.val$isBroadcast = z;
                    this.val$sentMessages = arrayList;
                    this.val$attachPath = str;
                }

                public void run() {
                    MessagesStorage.getInstance().updateMessageStateAndId(C06809.this.val$newMsgObj.random_id, Integer.valueOf(this.val$oldId), this.val$isBroadcast ? this.val$oldId : C06809.this.val$newMsgObj.id, 0, false, C06809.this.val$newMsgObj.to_id.channel_id);
                    MessagesStorage.getInstance().putMessages(this.val$sentMessages, true, false, this.val$isBroadcast, 0);
                    if (this.val$isBroadcast) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(C06809.this.val$newMsgObj);
                        MessagesStorage.getInstance().putMessages(arrayList, true, false, false, 0);
                    }
                    AndroidUtilities.runOnUIThread(new C06771());
                    if (MessageObject.isVideoMessage(C06809.this.val$newMsgObj) || MessageObject.isNewGifMessage(C06809.this.val$newMsgObj)) {
                        SendMessagesHelper.this.stopVideoService(this.val$attachPath);
                    }
                }
            }

            C06791(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean z;
                if (this.val$error == null) {
                    int i;
                    boolean z2;
                    int i2 = C06809.this.val$newMsgObj.id;
                    boolean z3 = C06809.this.val$req instanceof TL_messages_sendBroadcast;
                    ArrayList arrayList = new ArrayList();
                    String str = C06809.this.val$newMsgObj.attachPath;
                    Message message;
                    Message message2;
                    if (this.val$response instanceof TL_updateShortSentMessage) {
                        TL_updateShortSentMessage tL_updateShortSentMessage = (TL_updateShortSentMessage) this.val$response;
                        message = C06809.this.val$newMsgObj;
                        message2 = C06809.this.val$newMsgObj;
                        i = tL_updateShortSentMessage.id;
                        message2.id = i;
                        message.local_id = i;
                        C06809.this.val$newMsgObj.date = tL_updateShortSentMessage.date;
                        C06809.this.val$newMsgObj.entities = tL_updateShortSentMessage.entities;
                        C06809.this.val$newMsgObj.out = tL_updateShortSentMessage.out;
                        if (tL_updateShortSentMessage.media != null) {
                            C06809.this.val$newMsgObj.media = tL_updateShortSentMessage.media;
                            message = C06809.this.val$newMsgObj;
                            message.flags |= TLRPC.USER_FLAG_UNUSED3;
                        }
                        if ((tL_updateShortSentMessage.media instanceof TL_messageMediaGame) && !TextUtils.isEmpty(tL_updateShortSentMessage.message)) {
                            C06809.this.val$newMsgObj.message = tL_updateShortSentMessage.message;
                        }
                        if (!C06809.this.val$newMsgObj.entities.isEmpty()) {
                            message = C06809.this.val$newMsgObj;
                            message.flags |= TLRPC.USER_FLAG_UNUSED;
                        }
                        Utilities.stageQueue.postRunnable(new C06741(tL_updateShortSentMessage));
                        arrayList.add(C06809.this.val$newMsgObj);
                        z2 = false;
                    } else if (this.val$response instanceof Updates) {
                        ArrayList arrayList2 = ((Updates) this.val$response).updates;
                        int i3 = 0;
                        while (i3 < arrayList2.size()) {
                            Update update = (Update) arrayList2.get(i3);
                            if (update instanceof TL_updateNewMessage) {
                                TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                                message = tL_updateNewMessage.message;
                                arrayList.add(message);
                                C06809.this.val$newMsgObj.id = tL_updateNewMessage.message.id;
                                Utilities.stageQueue.postRunnable(new C06752(tL_updateNewMessage));
                                break;
                            } else if (update instanceof TL_updateNewChannelMessage) {
                                TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                                message = tL_updateNewChannelMessage.message;
                                arrayList.add(message);
                                if ((C06809.this.val$newMsgObj.flags & TLRPC.MESSAGE_FLAG_MEGAGROUP) != 0) {
                                    message2 = tL_updateNewChannelMessage.message;
                                    message2.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                }
                                Utilities.stageQueue.postRunnable(new C06763(tL_updateNewChannelMessage));
                            } else {
                                i3++;
                            }
                        }
                        message = null;
                        if (message != null) {
                            Integer num = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                            if (num == null) {
                                num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                                MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), num);
                            }
                            message.unread = num.intValue() < message.id;
                            C06809.this.val$newMsgObj.id = message.id;
                            SendMessagesHelper.this.updateMediaPaths(C06809.this.val$msgObj, message, C06809.this.val$originalPath, false);
                            z2 = false;
                        } else {
                            z2 = true;
                        }
                    } else {
                        z2 = false;
                    }
                    if (!z2) {
                        C06809.this.val$newMsgObj.send_state = 0;
                        NotificationCenter instance = NotificationCenter.getInstance();
                        i = NotificationCenter.messageReceivedByServer;
                        Object[] objArr = new Object[4];
                        objArr[0] = Integer.valueOf(i2);
                        objArr[1] = Integer.valueOf(z3 ? i2 : C06809.this.val$newMsgObj.id);
                        objArr[2] = C06809.this.val$newMsgObj;
                        objArr[3] = Long.valueOf(C06809.this.val$newMsgObj.dialog_id);
                        instance.postNotificationName(i, objArr);
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06784(i2, z3, arrayList, str));
                    }
                    z = z2;
                } else {
                    if (this.val$error.text.equals("PEER_FLOOD")) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(0));
                    }
                    z = true;
                }
                if (z) {
                    MessagesStorage.getInstance().markMessageAsSendError(C06809.this.val$newMsgObj);
                    C06809.this.val$newMsgObj.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(C06809.this.val$newMsgObj.id));
                    SendMessagesHelper.this.processSentMessage(C06809.this.val$newMsgObj.id);
                    if (MessageObject.isVideoMessage(C06809.this.val$newMsgObj) || MessageObject.isNewGifMessage(C06809.this.val$newMsgObj)) {
                        SendMessagesHelper.this.stopVideoService(C06809.this.val$newMsgObj.attachPath);
                    }
                    SendMessagesHelper.this.removeFromSendingMessages(C06809.this.val$newMsgObj.id);
                }
            }
        }

        C06809(Message message, TLObject tLObject, MessageObject messageObject, String str) {
            this.val$newMsgObj = message;
            this.val$req = tLObject;
            this.val$msgObj = messageObject;
            this.val$originalPath = str;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C06791(tL_error, tLObject));
        }
    }

    protected class DelayedMessage {
        public TL_document documentLocation;
        public EncryptedChat encryptedChat;
        public String httpLocation;
        public FileLocation location;
        public MessageObject obj;
        public String originalPath;
        public TL_decryptedMessage sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public VideoEditedInfo videoEditedInfo;

        protected DelayedMessage() {
        }
    }

    public static class LocationProvider {
        private LocationProviderDelegate delegate;
        private GpsLocationListener gpsLocationListener;
        private Location lastKnownLocation;
        private LocationManager locationManager;
        private Runnable locationQueryCancelRunnable;
        private GpsLocationListener networkLocationListener;

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        /* renamed from: com.hanista.mobogram.messenger.SendMessagesHelper.LocationProvider.1 */
        class C06811 implements Runnable {
            C06811() {
            }

            public void run() {
                if (LocationProvider.this.locationQueryCancelRunnable == this) {
                    if (LocationProvider.this.delegate != null) {
                        if (LocationProvider.this.lastKnownLocation != null) {
                            LocationProvider.this.delegate.onLocationAcquired(LocationProvider.this.lastKnownLocation);
                        } else {
                            LocationProvider.this.delegate.onUnableLocationAcquire();
                        }
                    }
                    LocationProvider.this.cleanup();
                }
            }
        }

        private class GpsLocationListener implements LocationListener {
            private GpsLocationListener() {
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    FileLog.m16e("tmessages", "found location " + location);
                    LocationProvider.this.lastKnownLocation = location;
                    if (location.getAccuracy() < 100.0f) {
                        if (LocationProvider.this.delegate != null) {
                            LocationProvider.this.delegate.onLocationAcquired(location);
                        }
                        if (LocationProvider.this.locationQueryCancelRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(LocationProvider.this.locationQueryCancelRunnable);
                        }
                        LocationProvider.this.cleanup();
                    }
                }
            }

            public void onProviderDisabled(String str) {
            }

            public void onProviderEnabled(String str) {
            }

            public void onStatusChanged(String str, int i, Bundle bundle) {
            }
        }

        public LocationProvider() {
            this.gpsLocationListener = new GpsLocationListener();
            this.networkLocationListener = new GpsLocationListener();
        }

        public LocationProvider(LocationProviderDelegate locationProviderDelegate) {
            this.gpsLocationListener = new GpsLocationListener();
            this.networkLocationListener = new GpsLocationListener();
            this.delegate = locationProviderDelegate;
        }

        private void cleanup() {
            this.locationManager.removeUpdates(this.gpsLocationListener);
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.lastKnownLocation = null;
            this.locationQueryCancelRunnable = null;
        }

        public void setDelegate(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        public void start() {
            if (this.locationManager == null) {
                this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            }
            try {
                this.locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            try {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Throwable e22) {
                FileLog.m18e("tmessages", e22);
            }
            if (this.locationQueryCancelRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
            }
            this.locationQueryCancelRunnable = new C06811();
            AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
        }

        public void stop() {
            if (this.locationManager != null) {
                if (this.locationQueryCancelRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
                }
                cleanup();
            }
        }
    }

    static {
        Instance = null;
    }

    public SendMessagesHelper() {
        this.currentChatInfo = null;
        this.delayedMessages = new HashMap();
        this.unsentMessages = new HashMap();
        this.sendingMessages = new HashMap();
        this.waitingForLocation = new HashMap();
        this.waitingForCallback = new HashMap();
        this.locationProvider = new LocationProvider(new C06561());
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    private static void fillVideoAttribute(String str, TL_documentAttributeVideo tL_documentAttributeVideo, VideoEditedInfo videoEditedInfo) {
        MediaMetadataRetriever mediaMetadataRetriever;
        Object obj;
        Throwable e;
        MediaPlayer create;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                mediaMetadataRetriever.setDataSource(str);
                String extractMetadata = mediaMetadataRetriever.extractMetadata(18);
                if (extractMetadata != null) {
                    tL_documentAttributeVideo.w = Integer.parseInt(extractMetadata);
                }
                extractMetadata = mediaMetadataRetriever.extractMetadata(19);
                if (extractMetadata != null) {
                    tL_documentAttributeVideo.h = Integer.parseInt(extractMetadata);
                }
                extractMetadata = mediaMetadataRetriever.extractMetadata(9);
                if (extractMetadata != null) {
                    tL_documentAttributeVideo.duration = (int) Math.ceil((double) (((float) Long.parseLong(extractMetadata)) / 1000.0f));
                }
                if (VERSION.SDK_INT >= 17) {
                    extractMetadata = mediaMetadataRetriever.extractMetadata(24);
                    if (extractMetadata != null) {
                        int intValue = Utilities.parseInt(extractMetadata).intValue();
                        if (videoEditedInfo != null) {
                            videoEditedInfo.rotationValue = intValue;
                        } else if (intValue == 90 || intValue == 270) {
                            intValue = tL_documentAttributeVideo.w;
                            tL_documentAttributeVideo.w = tL_documentAttributeVideo.h;
                            tL_documentAttributeVideo.h = intValue;
                        }
                    }
                }
                obj = 1;
                if (mediaMetadataRetriever != null) {
                    try {
                        mediaMetadataRetriever.release();
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    FileLog.m18e("tmessages", e);
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e4) {
                            FileLog.m18e("tmessages", e4);
                            obj = null;
                        }
                    }
                    obj = null;
                    if (obj != null) {
                        try {
                            create = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(str)));
                            if (create == null) {
                                tL_documentAttributeVideo.duration = (int) Math.ceil((double) (((float) create.getDuration()) / 1000.0f));
                                tL_documentAttributeVideo.w = create.getVideoWidth();
                                tL_documentAttributeVideo.h = create.getVideoHeight();
                                create.release();
                            }
                        } catch (Throwable e42) {
                            FileLog.m18e("tmessages", e42);
                            return;
                        }
                    }
                } catch (Throwable th) {
                    e42 = th;
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e22) {
                            FileLog.m18e("tmessages", e22);
                        }
                    }
                    throw e42;
                }
            }
        } catch (Exception e5) {
            e42 = e5;
            mediaMetadataRetriever = null;
            FileLog.m18e("tmessages", e42);
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
            obj = null;
            if (obj != null) {
                create = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(str)));
                if (create == null) {
                    tL_documentAttributeVideo.duration = (int) Math.ceil((double) (((float) create.getDuration()) / 1000.0f));
                    tL_documentAttributeVideo.w = create.getVideoWidth();
                    tL_documentAttributeVideo.h = create.getVideoHeight();
                    create.release();
                }
            }
        } catch (Throwable th2) {
            e42 = th2;
            mediaMetadataRetriever = null;
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
            throw e42;
        }
        if (obj != null) {
            create = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(str)));
            if (create == null) {
                tL_documentAttributeVideo.duration = (int) Math.ceil((double) (((float) create.getDuration()) / 1000.0f));
                tL_documentAttributeVideo.w = create.getVideoWidth();
                tL_documentAttributeVideo.h = create.getVideoHeight();
                create.release();
            }
        }
    }

    public static SendMessagesHelper getInstance() {
        SendMessagesHelper sendMessagesHelper = Instance;
        if (sendMessagesHelper == null) {
            synchronized (SendMessagesHelper.class) {
                sendMessagesHelper = Instance;
                if (sendMessagesHelper == null) {
                    sendMessagesHelper = new SendMessagesHelper();
                    Instance = sendMessagesHelper;
                }
            }
        }
        return sendMessagesHelper;
    }

    private static String getTrimmedString(String str) {
        String trim = str.trim();
        if (trim.length() == 0) {
            return trim;
        }
        while (str.startsWith("\n")) {
            str = str.substring(1);
        }
        while (str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private void performSendDelayedMessage(DelayedMessage delayedMessage) {
        String file;
        if (delayedMessage.type == 0) {
            if (delayedMessage.httpLocation != null) {
                putToDelayedMessages(delayedMessage.httpLocation, delayedMessage);
                ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "file");
            } else if (delayedMessage.sendRequest != null) {
                file = FileLoader.getPathToAttach(delayedMessage.location).toString();
                putToDelayedMessages(file, delayedMessage);
                FileLoader.getInstance().uploadFile(file, false, true);
            } else {
                file = FileLoader.getPathToAttach(delayedMessage.location).toString();
                if (delayedMessage.sendEncryptedRequest == null || delayedMessage.location.dc_id == 0 || new File(file).exists()) {
                    putToDelayedMessages(file, delayedMessage);
                    FileLoader.getInstance().uploadFile(file, true, true);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(delayedMessage.location), delayedMessage);
                FileLoader.getInstance().loadFile(delayedMessage.location, "jpg", 0, false);
            }
        } else if (delayedMessage.type == 1) {
            if (delayedMessage.videoEditedInfo != null) {
                file = delayedMessage.obj.messageOwner.attachPath;
                if (file == null) {
                    file = FileLoader.getInstance().getDirectory(4) + "/" + delayedMessage.documentLocation.id + ".mp4";
                }
                putToDelayedMessages(file, delayedMessage);
                MediaController.m71a().m177f(delayedMessage.obj);
            } else if (delayedMessage.sendRequest != null) {
                if ((delayedMessage.sendRequest instanceof TL_messages_sendMedia ? ((TL_messages_sendMedia) delayedMessage.sendRequest).media : ((TL_messages_sendBroadcast) delayedMessage.sendRequest).media).file == null) {
                    file = delayedMessage.obj.messageOwner.attachPath;
                    if (file == null) {
                        file = FileLoader.getInstance().getDirectory(4) + "/" + delayedMessage.documentLocation.id + ".mp4";
                    }
                    putToDelayedMessages(file, delayedMessage);
                    if (delayedMessage.obj.videoEditedInfo != null) {
                        FileLoader.getInstance().uploadFile(file, false, false, delayedMessage.documentLocation.size);
                        return;
                    } else {
                        FileLoader.getInstance().uploadFile(file, false, false);
                        return;
                    }
                }
                file = FileLoader.getInstance().getDirectory(4) + "/" + delayedMessage.location.volume_id + "_" + delayedMessage.location.local_id + ".jpg";
                putToDelayedMessages(file, delayedMessage);
                FileLoader.getInstance().uploadFile(file, false, true);
            } else {
                file = delayedMessage.obj.messageOwner.attachPath;
                if (file == null) {
                    file = FileLoader.getInstance().getDirectory(4) + "/" + delayedMessage.documentLocation.id + ".mp4";
                }
                putToDelayedMessages(file, delayedMessage);
                if (delayedMessage.obj.videoEditedInfo != null) {
                    FileLoader.getInstance().uploadFile(file, true, false, delayedMessage.documentLocation.size);
                } else {
                    FileLoader.getInstance().uploadFile(file, true, false);
                }
            }
        } else if (delayedMessage.type == 2) {
            if (delayedMessage.httpLocation != null) {
                putToDelayedMessages(delayedMessage.httpLocation, delayedMessage);
                ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "gif");
            } else if (delayedMessage.sendRequest != null) {
                InputMedia inputMedia = delayedMessage.sendRequest instanceof TL_messages_sendMedia ? ((TL_messages_sendMedia) delayedMessage.sendRequest).media : ((TL_messages_sendBroadcast) delayedMessage.sendRequest).media;
                if (inputMedia.file == null) {
                    file = delayedMessage.obj.messageOwner.attachPath;
                    putToDelayedMessages(file, delayedMessage);
                    if (delayedMessage.sendRequest != null) {
                        FileLoader.getInstance().uploadFile(file, false, false);
                    } else {
                        FileLoader.getInstance().uploadFile(file, true, false);
                    }
                } else if (inputMedia.thumb == null && delayedMessage.location != null) {
                    file = FileLoader.getInstance().getDirectory(4) + "/" + delayedMessage.location.volume_id + "_" + delayedMessage.location.local_id + ".jpg";
                    putToDelayedMessages(file, delayedMessage);
                    FileLoader.getInstance().uploadFile(file, false, true);
                }
            } else {
                file = delayedMessage.obj.messageOwner.attachPath;
                if (delayedMessage.sendEncryptedRequest == null || delayedMessage.documentLocation.dc_id == 0 || new File(file).exists()) {
                    putToDelayedMessages(file, delayedMessage);
                    FileLoader.getInstance().uploadFile(file, true, false);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(delayedMessage.documentLocation), delayedMessage);
                FileLoader.getInstance().loadFile(delayedMessage.documentLocation, true, false);
            }
        } else if (delayedMessage.type == 3) {
            file = delayedMessage.obj.messageOwner.attachPath;
            putToDelayedMessages(file, delayedMessage);
            if (delayedMessage.sendRequest != null) {
                FileLoader.getInstance().uploadFile(file, false, true);
            } else {
                FileLoader.getInstance().uploadFile(file, true, true);
            }
        }
    }

    private void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str) {
        Message message = messageObject.messageOwner;
        putToSendingMessages(message);
        ConnectionsManager.getInstance().sendRequest(tLObject, new C06809(message, tLObject, messageObject, str), new AnonymousClass10(message), (tLObject instanceof TL_messages_sendMessage ? TLRPC.USER_FLAG_UNUSED : 0) | 68);
    }

    public static void prepareSendingAudioDocuments(ArrayList<MessageObject> arrayList, long j, MessageObject messageObject) {
        new Thread(new AnonymousClass13(arrayList, j, messageObject)).start();
    }

    public static void prepareSendingBotContextResult(BotInlineResult botInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject) {
        if (botInlineResult != null) {
            if (botInlineResult.send_message instanceof TL_botInlineMessageMediaAuto) {
                new Thread(new AnonymousClass15(botInlineResult, j, hashMap, messageObject)).run();
            } else if (botInlineResult.send_message instanceof TL_botInlineMessageText) {
                getInstance().sendMessage(botInlineResult.send_message.message, j, messageObject, null, !botInlineResult.send_message.no_webpage, botInlineResult.send_message.entities, botInlineResult.send_message.reply_markup, hashMap);
            } else if (botInlineResult.send_message instanceof TL_botInlineMessageMediaVenue) {
                r1 = new TL_messageMediaVenue();
                r1.geo = botInlineResult.send_message.geo;
                r1.address = botInlineResult.send_message.address;
                r1.title = botInlineResult.send_message.title;
                r1.provider = botInlineResult.send_message.provider;
                r1.venue_id = botInlineResult.send_message.venue_id;
                getInstance().sendMessage(r1, j, messageObject, botInlineResult.send_message.reply_markup, (HashMap) hashMap);
            } else if (botInlineResult.send_message instanceof TL_botInlineMessageMediaGeo) {
                r1 = new TL_messageMediaGeo();
                r1.geo = botInlineResult.send_message.geo;
                getInstance().sendMessage(r1, j, messageObject, botInlineResult.send_message.reply_markup, (HashMap) hashMap);
            } else if (botInlineResult.send_message instanceof TL_botInlineMessageMediaContact) {
                User tL_user = new TL_user();
                tL_user.phone = botInlineResult.send_message.phone_number;
                tL_user.first_name = botInlineResult.send_message.first_name;
                tL_user.last_name = botInlineResult.send_message.last_name;
                getInstance().sendMessage(tL_user, j, messageObject, botInlineResult.send_message.reply_markup, (HashMap) hashMap);
            }
        }
    }

    public static void prepareSendingDocument(String str, String str2, Uri uri, String str3, long j, MessageObject messageObject) {
        if ((str != null && str2 != null) || uri != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = null;
            if (uri != null) {
                arrayList3 = new ArrayList();
            }
            arrayList.add(str);
            arrayList2.add(str2);
            prepareSendingDocuments(arrayList, arrayList2, arrayList3, str3, j, messageObject);
        }
    }

    private static boolean prepareSendingDocumentInternal(String str, String str2, Uri uri, String str3, long j, MessageObject messageObject, String str4) {
        if ((str == null || str.length() == 0) && uri == null) {
            return false;
        }
        if (uri != null && AndroidUtilities.isInternalUri(uri)) {
            return false;
        }
        if (str != null && AndroidUtilities.isInternalUri(Uri.fromFile(new File(str)))) {
            return false;
        }
        String str5;
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        Object obj = null;
        if (uri != null) {
            str5 = null;
            if (str3 != null) {
                str5 = singleton.getExtensionFromMimeType(str3);
            }
            if (str5 == null) {
                str5 = "txt";
            }
            str = MediaController.m77a(uri, str5);
            if (str == null) {
                return false;
            }
        }
        File file = new File(str);
        if (!file.exists() || file.length() == 0) {
            return false;
        }
        TL_document tL_document;
        boolean z = ((int) j) == 0;
        Object obj2 = !z ? 1 : null;
        String name = file.getName();
        str5 = TtmlNode.ANONYMOUS_REGION_ID;
        int lastIndexOf = str.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? str.substring(lastIndexOf + 1) : str5;
        if (substring.toLowerCase().equals("mp3") || substring.toLowerCase().equals("m4a")) {
            AudioInfo audioInfo = AudioInfo.getAudioInfo(file);
            if (!(audioInfo == null || audioInfo.getDuration() == 0)) {
                TL_documentAttributeAudio tL_documentAttributeAudio;
                if (z) {
                    EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (j >> 32)));
                    if (encryptedChat == null) {
                        return false;
                    }
                    tL_documentAttributeAudio = AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 46 ? new TL_documentAttributeAudio() : new TL_documentAttributeAudio_old();
                } else {
                    tL_documentAttributeAudio = new TL_documentAttributeAudio();
                }
                tL_documentAttributeAudio.duration = (int) (audioInfo.getDuration() / 1000);
                tL_documentAttributeAudio.title = audioInfo.getTitle();
                tL_documentAttributeAudio.performer = audioInfo.getArtist();
                if (tL_documentAttributeAudio.title == null) {
                    tL_documentAttributeAudio.title = TtmlNode.ANONYMOUS_REGION_ID;
                    tL_documentAttributeAudio.flags |= 1;
                }
                if (tL_documentAttributeAudio.performer == null) {
                    tL_documentAttributeAudio.performer = TtmlNode.ANONYMOUS_REGION_ID;
                    tL_documentAttributeAudio.flags |= 2;
                }
                obj = tL_documentAttributeAudio;
            }
        }
        if (str2 != null) {
            str2 = obj != null ? str2 + MimeTypes.BASE_TYPE_AUDIO + file.length() : str2 + TtmlNode.ANONYMOUS_REGION_ID + file.length();
        }
        TL_document tL_document2 = null;
        if (!z) {
            tL_document2 = (TL_document) MessagesStorage.getInstance().getSentFile(str2, !z ? 1 : 4);
            if (!(tL_document2 != null || str.equals(str2) || z)) {
                tL_document2 = (TL_document) MessagesStorage.getInstance().getSentFile(str + file.length(), !z ? 1 : 4);
            }
        }
        if (tL_document2 == null) {
            TL_document tL_document3 = new TL_document();
            tL_document3.id = 0;
            tL_document3.date = ConnectionsManager.getInstance().getCurrentTime();
            TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
            tL_documentAttributeFilename.file_name = name;
            tL_document3.attributes.add(tL_documentAttributeFilename);
            tL_document3.size = (int) file.length();
            tL_document3.dc_id = 0;
            if (obj != null) {
                tL_document3.attributes.add(obj);
            }
            if (substring.length() == 0) {
                tL_document3.mime_type = "application/octet-stream";
            } else if (substring.toLowerCase().equals("webp")) {
                tL_document3.mime_type = "image/webp";
            } else {
                substring = singleton.getMimeTypeFromExtension(substring.toLowerCase());
                if (substring != null) {
                    tL_document3.mime_type = substring;
                } else {
                    tL_document3.mime_type = "application/octet-stream";
                }
            }
            if (tL_document3.mime_type.equals("image/gif")) {
                try {
                    Bitmap loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                    if (loadBitmap != null) {
                        tL_documentAttributeFilename.file_name = "animation.gif";
                        tL_document3.thumb = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, z);
                        loadBitmap.recycle();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            if (tL_document3.mime_type.equals("image/webp") && obj2 != null) {
                Options options = new Options();
                try {
                    options.inJustDecodeBounds = true;
                    RandomAccessFile randomAccessFile = new RandomAccessFile(str, "r");
                    ByteBuffer map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) str.length());
                    Utilities.loadWebpImage(null, map, map.limit(), options, true);
                    randomAccessFile.close();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
                if (options.outWidth != 0 && options.outHeight != 0 && options.outWidth <= 800 && options.outHeight <= 800) {
                    TL_documentAttributeSticker tL_documentAttributeSticker = new TL_documentAttributeSticker();
                    tL_documentAttributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                    tL_documentAttributeSticker.stickerset = new TL_inputStickerSetEmpty();
                    tL_document3.attributes.add(tL_documentAttributeSticker);
                    TL_documentAttributeImageSize tL_documentAttributeImageSize = new TL_documentAttributeImageSize();
                    tL_documentAttributeImageSize.w = options.outWidth;
                    tL_documentAttributeImageSize.h = options.outHeight;
                    tL_document3.attributes.add(tL_documentAttributeImageSize);
                }
            }
            if (tL_document3.thumb == null) {
                tL_document3.thumb = new TL_photoSizeEmpty();
                tL_document3.thumb.type = "s";
            }
            tL_document = tL_document3;
        } else {
            tL_document = tL_document2;
        }
        tL_document.caption = str4;
        HashMap hashMap = new HashMap();
        if (str2 != null) {
            hashMap.put("originalPath", str2);
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass12(tL_document, str, j, messageObject, hashMap));
        return true;
    }

    public static void prepareSendingDocuments(ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, long j, MessageObject messageObject) {
        if (arrayList != null || arrayList2 != null || arrayList3 != null) {
            if (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size()) {
                new Thread(new AnonymousClass14(arrayList, arrayList2, str, j, messageObject, arrayList3)).start();
            }
        }
    }

    public static void prepareSendingPhoto(String str, Uri uri, long j, MessageObject messageObject, CharSequence charSequence, ArrayList<InputDocument> arrayList) {
        ArrayList arrayList2;
        ArrayList arrayList3;
        ArrayList arrayList4;
        ArrayList arrayList5 = null;
        if (str == null || str.length() == 0) {
            arrayList2 = null;
        } else {
            arrayList2 = new ArrayList();
            arrayList2.add(str);
        }
        if (uri != null) {
            arrayList3 = new ArrayList();
            arrayList3.add(uri);
        } else {
            arrayList3 = null;
        }
        if (charSequence != null) {
            arrayList4 = new ArrayList();
            arrayList4.add(charSequence.toString());
        } else {
            arrayList4 = null;
        }
        if (!(arrayList == null || arrayList.isEmpty())) {
            arrayList5 = new ArrayList();
            arrayList5.add(new ArrayList(arrayList));
        }
        prepareSendingPhotos(arrayList2, arrayList3, j, messageObject, arrayList4, arrayList5);
    }

    public static void prepareSendingPhotos(ArrayList<String> arrayList, ArrayList<Uri> arrayList2, long j, MessageObject messageObject, ArrayList<String> arrayList3, ArrayList<ArrayList<InputDocument>> arrayList4) {
        if (arrayList != null || arrayList2 != null) {
            if (arrayList != null && arrayList.isEmpty()) {
                return;
            }
            if (arrayList2 == null || !arrayList2.isEmpty()) {
                ArrayList arrayList5 = new ArrayList();
                ArrayList arrayList6 = new ArrayList();
                if (arrayList != null) {
                    arrayList5.addAll(arrayList);
                }
                if (arrayList2 != null) {
                    arrayList6.addAll(arrayList2);
                }
                new Thread(new AnonymousClass18(j, arrayList5, arrayList6, arrayList3, arrayList4, messageObject)).start();
            }
        }
    }

    public static void prepareSendingPhotosSearch(ArrayList<SearchImage> arrayList, long j, MessageObject messageObject) {
        if (arrayList != null && !arrayList.isEmpty()) {
            new Thread(new AnonymousClass16(j, arrayList, messageObject)).start();
        }
    }

    public static void prepareSendingText(String str, long j) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass17(str, j));
    }

    public static void prepareSendingVideo(String str, long j, long j2, int i, int i2, VideoEditedInfo videoEditedInfo, long j3, MessageObject messageObject, String str2) {
        if (str != null && str.length() != 0) {
            new Thread(new AnonymousClass19(j3, videoEditedInfo, str, j2, i2, i, j, str2, messageObject)).start();
        }
    }

    private void putToDelayedMessages(String str, DelayedMessage delayedMessage) {
        ArrayList arrayList = (ArrayList) this.delayedMessages.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.delayedMessages.put(str, arrayList);
        }
        arrayList.add(delayedMessage);
    }

    private void sendLocation(Location location) {
        MessageMedia tL_messageMediaGeo = new TL_messageMediaGeo();
        tL_messageMediaGeo.geo = new TL_geoPoint();
        tL_messageMediaGeo.geo.lat = location.getLatitude();
        tL_messageMediaGeo.geo._long = location.getLongitude();
        for (Entry value : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            getInstance().sendMessage(tL_messageMediaGeo, messageObject.getDialogId(), messageObject, null, null);
        }
    }

    private void sendMessage(String str, MessageMedia messageMedia, TL_photo tL_photo, VideoEditedInfo videoEditedInfo, User user, TL_document tL_document, TL_game tL_game, long j, String str2, MessageObject messageObject, WebPage webPage, boolean z, MessageObject messageObject2, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        Throwable e;
        MessageObject messageObject3;
        Message message;
        Message tL_message;
        if (j != 0) {
            String str3;
            Message message2;
            int i;
            int i2;
            Object obj;
            InputPeer inputPeer;
            EncryptedChat encryptedChat;
            EncryptedChat encryptedChat2;
            Chat chat;
            Object obj2;
            int i3;
            Document document;
            int i4;
            Message message3;
            WebPage tL_webPageUrlPending;
            int i5;
            MessageMedia messageMedia2;
            String str4;
            int i6;
            DocumentAttribute documentAttribute;
            TL_documentAttributeSticker_layer55 tL_documentAttributeSticker_layer55;
            String stickerSetName;
            ArrayList arrayList2;
            User user2;
            ArrayList arrayList3;
            Iterator it;
            InputUser inputUser;
            ArrayList arrayList4;
            ArrayList arrayList5;
            TLObject tL_messages_sendInlineBotResult;
            DecryptedMessage tL_decryptedMessage;
            if (hashMap != null) {
                if (hashMap.containsKey("originalPath")) {
                    str3 = (String) hashMap.get("originalPath");
                    message2 = null;
                    i = (int) j;
                    i2 = (int) (j >> 32);
                    obj = null;
                    inputPeer = i == 0 ? MessagesController.getInputPeer(i) : null;
                    if (i == 0) {
                        encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i2));
                        if (encryptedChat == null) {
                            encryptedChat2 = encryptedChat;
                        } else if (messageObject2 != null) {
                            MessagesStorage.getInstance().markMessageAsSendError(messageObject2.messageOwner);
                            messageObject2.messageOwner.send_state = 2;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject2.getId()));
                            processSentMessage(messageObject2.getId());
                            return;
                        } else {
                            return;
                        }
                    } else if (inputPeer instanceof TL_inputPeerChannel) {
                        encryptedChat2 = null;
                    } else {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(inputPeer.channel_id));
                        obj2 = (chat != null || chat.megagroup) ? null : 1;
                        encryptedChat2 = null;
                        obj = obj2;
                    }
                    if (messageObject2 == null) {
                        try {
                            message2 = messageObject2.messageOwner;
                            try {
                                if (messageObject2.isForwarded()) {
                                    if (messageObject2.type == 0) {
                                        if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGame)) {
                                            str = message2.message;
                                        }
                                        i3 = 0;
                                    } else if (messageObject2.type == 4) {
                                        messageMedia = message2.media;
                                        i3 = 1;
                                    } else if (messageObject2.type != 1) {
                                        tL_photo = (TL_photo) message2.media.photo;
                                        i3 = 2;
                                    } else if (messageObject2.type != 3 || videoEditedInfo != null) {
                                        document = (TL_document) message2.media.document;
                                        i3 = 3;
                                    } else if (messageObject2.type == 12) {
                                        user = new TL_userRequest_old2();
                                        user.phone = message2.media.phone_number;
                                        user.first_name = message2.media.first_name;
                                        user.last_name = message2.media.last_name;
                                        user.id = message2.media.user_id;
                                        i3 = 6;
                                    } else if (messageObject2.type == 8 || messageObject2.type == 9 || messageObject2.type == 13 || messageObject2.type == 14) {
                                        document = (TL_document) message2.media.document;
                                        i3 = 7;
                                    } else if (messageObject2.type == 2) {
                                        document = (TL_document) message2.media.document;
                                        i3 = 8;
                                    } else {
                                        i3 = -1;
                                    }
                                    if (hashMap != null) {
                                        if (hashMap.containsKey("query_id")) {
                                            i4 = 9;
                                            message3 = message2;
                                        }
                                    }
                                    i4 = i3;
                                    message3 = message2;
                                } else {
                                    i4 = 4;
                                    message3 = message2;
                                }
                            } catch (Exception e2) {
                                e = e2;
                                messageObject3 = null;
                                message = message2;
                                FileLog.m18e("tmessages", e);
                                MessagesStorage.getInstance().markMessageAsSendError(message);
                                if (messageObject3 != null) {
                                    messageObject3.messageOwner.send_state = 2;
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                                processSentMessage(message.id);
                            }
                        } catch (Exception e3) {
                            e = e3;
                            messageObject3 = null;
                            message = message2;
                            FileLog.m18e("tmessages", e);
                            MessagesStorage.getInstance().markMessageAsSendError(message);
                            if (messageObject3 != null) {
                                messageObject3.messageOwner.send_state = 2;
                            }
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                            processSentMessage(message.id);
                        }
                    }
                    if (str != null) {
                        if (encryptedChat2 != null) {
                            if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                                message2 = new TL_message_secret();
                                if (arrayList != null) {
                                    try {
                                        if (!arrayList.isEmpty()) {
                                            message2.entities = arrayList;
                                        }
                                    } catch (Exception e4) {
                                        e = e4;
                                        messageObject3 = null;
                                        message = message2;
                                        FileLog.m18e("tmessages", e);
                                        MessagesStorage.getInstance().markMessageAsSendError(message);
                                        if (messageObject3 != null) {
                                            messageObject3.messageOwner.send_state = 2;
                                        }
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                                        processSentMessage(message.id);
                                    }
                                }
                                if (encryptedChat2 != null && (webPage instanceof TL_webPagePending)) {
                                    if (webPage.url == null) {
                                        tL_webPageUrlPending = new TL_webPageUrlPending();
                                        tL_webPageUrlPending.url = webPage.url;
                                        webPage = tL_webPageUrlPending;
                                    } else {
                                        webPage = null;
                                    }
                                }
                                if (webPage != null) {
                                    message2.media = new TL_messageMediaEmpty();
                                } else {
                                    message2.media = new TL_messageMediaWebPage();
                                    message2.media.webpage = webPage;
                                }
                                if (hashMap != null) {
                                    if (hashMap.containsKey("query_id")) {
                                        i3 = 9;
                                        message2.message = str;
                                    }
                                }
                                i3 = 0;
                                message2.message = str;
                            }
                        }
                        message2 = new TL_message();
                        if (arrayList != null) {
                            if (arrayList.isEmpty()) {
                                message2.entities = arrayList;
                            }
                        }
                        if (webPage.url == null) {
                            webPage = null;
                        } else {
                            tL_webPageUrlPending = new TL_webPageUrlPending();
                            tL_webPageUrlPending.url = webPage.url;
                            webPage = tL_webPageUrlPending;
                        }
                        if (webPage != null) {
                            message2.media = new TL_messageMediaWebPage();
                            message2.media.webpage = webPage;
                        } else {
                            message2.media = new TL_messageMediaEmpty();
                        }
                        if (hashMap != null) {
                            if (hashMap.containsKey("query_id")) {
                                i3 = 9;
                                message2.message = str;
                            }
                        }
                        i3 = 0;
                        message2.message = str;
                    } else if (messageMedia != null) {
                        if (encryptedChat2 != null) {
                            if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                                message2 = new TL_message_secret();
                                message2.media = messageMedia;
                                message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                                if (hashMap != null) {
                                    if (hashMap.containsKey("query_id")) {
                                        i3 = 9;
                                    }
                                }
                                i3 = 1;
                            }
                        }
                        message2 = new TL_message();
                        message2.media = messageMedia;
                        message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                        if (hashMap != null) {
                            if (hashMap.containsKey("query_id")) {
                                i3 = 9;
                            }
                        }
                        i3 = 1;
                    } else if (tL_photo != null) {
                        message2 = (encryptedChat2 != null || AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 17) ? new TL_message() : new TL_message_secret();
                        message2.media = new TL_messageMediaPhoto();
                        message2.media.caption = tL_photo.caption == null ? tL_photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                        message2.media.photo = tL_photo;
                        if (hashMap != null) {
                            if (hashMap.containsKey("query_id")) {
                                i5 = 9;
                                message2.message = "-1";
                                if (str2 != null && str2.length() > 0) {
                                    if (str2.startsWith("http")) {
                                        message2.attachPath = str2;
                                        i3 = i5;
                                    }
                                }
                                message2.attachPath = FileLoader.getPathToAttach(((PhotoSize) tL_photo.sizes.get(tL_photo.sizes.size() - 1)).location, true).toString();
                                i3 = i5;
                            }
                        }
                        i5 = 2;
                        message2.message = "-1";
                        if (str2.startsWith("http")) {
                            message2.attachPath = str2;
                            i3 = i5;
                        }
                        message2.attachPath = FileLoader.getPathToAttach(((PhotoSize) tL_photo.sizes.get(tL_photo.sizes.size() - 1)).location, true).toString();
                        i3 = i5;
                    } else if (tL_game != null) {
                        tL_message = new TL_message();
                        try {
                            tL_message.media = new TL_messageMediaGame();
                            tL_message.media.caption = TtmlNode.ANONYMOUS_REGION_ID;
                            tL_message.media.game = tL_game;
                            tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
                            if (hashMap != null) {
                                if (hashMap.containsKey("query_id")) {
                                    i3 = 9;
                                    message2 = tL_message;
                                }
                            }
                            i3 = -1;
                            message2 = tL_message;
                        } catch (Exception e5) {
                            e = e5;
                            messageObject3 = null;
                            message = tL_message;
                            FileLog.m18e("tmessages", e);
                            MessagesStorage.getInstance().markMessageAsSendError(message);
                            if (messageObject3 != null) {
                                messageObject3.messageOwner.send_state = 2;
                            }
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                            processSentMessage(message.id);
                        }
                    } else if (user != null) {
                        if (encryptedChat2 != null) {
                            if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                                message2 = new TL_message_secret();
                                message2.media = new TL_messageMediaContact();
                                message2.media.phone_number = user.phone;
                                message2.media.first_name = user.first_name;
                                message2.media.last_name = user.last_name;
                                message2.media.user_id = user.id;
                                if (message2.media.first_name == null) {
                                    messageMedia2 = message2.media;
                                    str4 = TtmlNode.ANONYMOUS_REGION_ID;
                                    messageMedia2.first_name = str4;
                                    user.first_name = str4;
                                }
                                if (message2.media.last_name == null) {
                                    messageMedia2 = message2.media;
                                    str4 = TtmlNode.ANONYMOUS_REGION_ID;
                                    messageMedia2.last_name = str4;
                                    user.last_name = str4;
                                }
                                message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                                if (hashMap != null) {
                                    if (hashMap.containsKey("query_id")) {
                                        i3 = 9;
                                    }
                                }
                                i3 = 6;
                            }
                        }
                        message2 = new TL_message();
                        message2.media = new TL_messageMediaContact();
                        message2.media.phone_number = user.phone;
                        message2.media.first_name = user.first_name;
                        message2.media.last_name = user.last_name;
                        message2.media.user_id = user.id;
                        if (message2.media.first_name == null) {
                            messageMedia2 = message2.media;
                            str4 = TtmlNode.ANONYMOUS_REGION_ID;
                            messageMedia2.first_name = str4;
                            user.first_name = str4;
                        }
                        if (message2.media.last_name == null) {
                            messageMedia2 = message2.media;
                            str4 = TtmlNode.ANONYMOUS_REGION_ID;
                            messageMedia2.last_name = str4;
                            user.last_name = str4;
                        }
                        message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                        if (hashMap != null) {
                            if (hashMap.containsKey("query_id")) {
                                i3 = 9;
                            }
                        }
                        i3 = 6;
                    } else if (tL_document == null) {
                        message2 = (encryptedChat2 != null || AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 17) ? new TL_message() : new TL_message_secret();
                        message2.media = new TL_messageMediaDocument();
                        message2.media.caption = tL_document.caption == null ? tL_document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                        message2.media.document = tL_document;
                        if (hashMap != null) {
                            if (hashMap.containsKey("query_id")) {
                                i5 = 9;
                                if (videoEditedInfo == null) {
                                    message2.message = "-1";
                                } else {
                                    message2.message = videoEditedInfo.getString();
                                }
                                if (encryptedChat2 != null || tL_document.dc_id <= 0 || MessageObject.isStickerDocument(tL_document)) {
                                    message2.attachPath = str2;
                                } else {
                                    message2.attachPath = FileLoader.getPathToAttach(tL_document).toString();
                                }
                                if (encryptedChat2 != null && MessageObject.isStickerDocument(tL_document)) {
                                    i6 = 0;
                                    while (i6 < tL_document.attributes.size()) {
                                        documentAttribute = (DocumentAttribute) tL_document.attributes.get(i6);
                                        if (!(documentAttribute instanceof TL_documentAttributeSticker)) {
                                            i6++;
                                        } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 46) {
                                            tL_document.attributes.remove(i6);
                                            tL_document.attributes.add(new TL_documentAttributeSticker_old());
                                            i3 = i5;
                                        } else {
                                            tL_document.attributes.remove(i6);
                                            tL_documentAttributeSticker_layer55 = new TL_documentAttributeSticker_layer55();
                                            tL_document.attributes.add(tL_documentAttributeSticker_layer55);
                                            tL_documentAttributeSticker_layer55.alt = documentAttribute.alt;
                                            if (documentAttribute.stickerset != null) {
                                                stickerSetName = StickersQuery.getStickerSetName(documentAttribute.stickerset.id);
                                                if (stickerSetName != null || stickerSetName.length() <= 0) {
                                                    tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                                } else {
                                                    tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetShortName();
                                                    tL_documentAttributeSticker_layer55.stickerset.short_name = stickerSetName;
                                                }
                                            } else {
                                                tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                            }
                                            i3 = i5;
                                        }
                                    }
                                }
                                i3 = i5;
                            }
                        }
                        if (MessageObject.isVideoDocument(tL_document) && videoEditedInfo == null) {
                            i5 = MessageObject.isVoiceDocument(tL_document) ? 8 : 7;
                            if (videoEditedInfo == null) {
                                message2.message = "-1";
                            } else {
                                message2.message = videoEditedInfo.getString();
                            }
                            if (encryptedChat2 != null) {
                            }
                            message2.attachPath = str2;
                            i6 = 0;
                            while (i6 < tL_document.attributes.size()) {
                                documentAttribute = (DocumentAttribute) tL_document.attributes.get(i6);
                                if (!(documentAttribute instanceof TL_documentAttributeSticker)) {
                                    i6++;
                                } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 46) {
                                    tL_document.attributes.remove(i6);
                                    tL_document.attributes.add(new TL_documentAttributeSticker_old());
                                    i3 = i5;
                                } else {
                                    tL_document.attributes.remove(i6);
                                    tL_documentAttributeSticker_layer55 = new TL_documentAttributeSticker_layer55();
                                    tL_document.attributes.add(tL_documentAttributeSticker_layer55);
                                    tL_documentAttributeSticker_layer55.alt = documentAttribute.alt;
                                    if (documentAttribute.stickerset != null) {
                                        stickerSetName = StickersQuery.getStickerSetName(documentAttribute.stickerset.id);
                                        if (stickerSetName != null) {
                                        }
                                        tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                    } else {
                                        tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                    }
                                    i3 = i5;
                                }
                            }
                            i3 = i5;
                        } else {
                            i5 = 3;
                            if (videoEditedInfo == null) {
                                message2.message = videoEditedInfo.getString();
                            } else {
                                message2.message = "-1";
                            }
                            if (encryptedChat2 != null) {
                            }
                            message2.attachPath = str2;
                            i6 = 0;
                            while (i6 < tL_document.attributes.size()) {
                                documentAttribute = (DocumentAttribute) tL_document.attributes.get(i6);
                                if (!(documentAttribute instanceof TL_documentAttributeSticker)) {
                                    i6++;
                                } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 46) {
                                    tL_document.attributes.remove(i6);
                                    tL_documentAttributeSticker_layer55 = new TL_documentAttributeSticker_layer55();
                                    tL_document.attributes.add(tL_documentAttributeSticker_layer55);
                                    tL_documentAttributeSticker_layer55.alt = documentAttribute.alt;
                                    if (documentAttribute.stickerset != null) {
                                        tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                    } else {
                                        stickerSetName = StickersQuery.getStickerSetName(documentAttribute.stickerset.id);
                                        if (stickerSetName != null) {
                                        }
                                        tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                    }
                                    i3 = i5;
                                } else {
                                    tL_document.attributes.remove(i6);
                                    tL_document.attributes.add(new TL_documentAttributeSticker_old());
                                    i3 = i5;
                                }
                            }
                            i3 = i5;
                        }
                    } else {
                        i3 = -1;
                    }
                    if (message2.attachPath == null) {
                        message2.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    i5 = UserConfig.getNewMessageId();
                    message2.id = i5;
                    message2.local_id = i5;
                    message2.out = true;
                    if (obj != null || inputPeer == null) {
                        message2.from_id = UserConfig.getClientUserId();
                        message2.flags |= TLRPC.USER_FLAG_UNUSED2;
                    } else {
                        message2.from_id = -inputPeer.channel_id;
                    }
                    UserConfig.saveConfig(false);
                    i4 = i3;
                    message3 = message2;
                    if (message3.random_id == 0) {
                        message3.random_id = getNextRandomId();
                    }
                    if (hashMap != null) {
                        if (hashMap.containsKey("bot")) {
                            if (encryptedChat2 == null) {
                                message3.via_bot_name = (String) hashMap.get("bot_name");
                                if (message3.via_bot_name == null) {
                                    message3.via_bot_name = TtmlNode.ANONYMOUS_REGION_ID;
                                }
                            } else {
                                message3.via_bot_id = Utilities.parseInt((String) hashMap.get("bot")).intValue();
                            }
                            message3.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                        }
                    }
                    message3.params = hashMap;
                    if (messageObject2 == null || !messageObject2.resendAsIs) {
                        message3.date = ConnectionsManager.getInstance().getCurrentTime();
                        if (inputPeer instanceof TL_inputPeerChannel) {
                            message3.unread = true;
                        } else {
                            if (obj != null) {
                                message3.views = 1;
                                message3.flags |= TLRPC.MESSAGE_FLAG_HAS_VIEWS;
                            }
                            chat = MessagesController.getInstance().getChat(Integer.valueOf(inputPeer.channel_id));
                            if (chat != null) {
                                if (chat.megagroup) {
                                    message3.post = true;
                                    if (chat.signatures) {
                                        message3.from_id = UserConfig.getClientUserId();
                                    }
                                } else {
                                    message3.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                    message3.unread = true;
                                }
                            }
                        }
                    }
                    message3.flags |= TLRPC.USER_FLAG_UNUSED3;
                    message3.dialog_id = j;
                    if (messageObject != null) {
                        if (encryptedChat2 != null || messageObject.messageOwner.random_id == 0) {
                            message3.flags |= 8;
                        } else {
                            message3.reply_to_random_id = messageObject.messageOwner.random_id;
                            message3.flags |= 8;
                        }
                        message3.reply_to_msg_id = messageObject.getId();
                    }
                    if (replyMarkup != null && encryptedChat2 == null) {
                        message3.flags |= 64;
                        message3.reply_markup = replyMarkup;
                    }
                    if (i != 0) {
                        message3.to_id = new TL_peerUser();
                        if (encryptedChat2.participant_id != UserConfig.getClientUserId()) {
                            message3.to_id.user_id = encryptedChat2.admin_id;
                        } else {
                            message3.to_id.user_id = encryptedChat2.participant_id;
                        }
                        message3.ttl = encryptedChat2.ttl;
                        if (message3.ttl != 0) {
                            if (MessageObject.isVoiceMessage(message3)) {
                                for (i5 = 0; i5 < message3.media.document.attributes.size(); i5++) {
                                    documentAttribute = (DocumentAttribute) message3.media.document.attributes.get(i5);
                                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                                        i3 = documentAttribute.duration;
                                        break;
                                    }
                                }
                                i3 = 0;
                                message3.ttl = Math.max(encryptedChat2.ttl, i3 + 1);
                                arrayList2 = null;
                            } else if (MessageObject.isVideoMessage(message3)) {
                                for (i5 = 0; i5 < message3.media.document.attributes.size(); i5++) {
                                    documentAttribute = (DocumentAttribute) message3.media.document.attributes.get(i5);
                                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                                        i3 = documentAttribute.duration;
                                        break;
                                    }
                                }
                                i3 = 0;
                                message3.ttl = Math.max(encryptedChat2.ttl, i3 + 1);
                            }
                        }
                        arrayList2 = null;
                    } else if (i2 == 1) {
                        message3.to_id = MessagesController.getPeer(i);
                        if (i > 0) {
                            user2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
                            if (user2 != null) {
                                processSentMessage(message3.id);
                                return;
                            }
                            if (user2.bot) {
                                message3.unread = false;
                            }
                            arrayList2 = null;
                        }
                        arrayList2 = null;
                    } else if (this.currentChatInfo != null) {
                        MessagesStorage.getInstance().markMessageAsSendError(message3);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message3.id));
                        processSentMessage(message3.id);
                        return;
                    } else {
                        arrayList3 = new ArrayList();
                        it = this.currentChatInfo.participants.participants.iterator();
                        while (it.hasNext()) {
                            inputUser = MessagesController.getInputUser(MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) it.next()).user_id)));
                            if (inputUser != null) {
                                arrayList3.add(inputUser);
                            }
                        }
                        message3.to_id = new TL_peerChat();
                        message3.to_id.chat_id = i;
                        arrayList2 = arrayList3;
                    }
                    if ((encryptedChat2 == null || AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) && i2 != 1 && MessageObject.isVoiceMessage(message3) && message3.to_id.channel_id == 0) {
                        message3.media_unread = true;
                    }
                    message3.send_state = 1;
                    messageObject3 = new MessageObject(message3, null, true);
                    try {
                        messageObject3.replyMessageObject = messageObject;
                        if (!messageObject3.isForwarded() && ((messageObject3.type == 3 || videoEditedInfo != null) && !TextUtils.isEmpty(message3.attachPath))) {
                            messageObject3.attachPathExists = true;
                        }
                        arrayList4 = new ArrayList();
                        arrayList4.add(messageObject3);
                        arrayList5 = new ArrayList();
                        arrayList5.add(message3);
                        MessagesStorage.getInstance().putMessages(arrayList5, false, true, false, 0);
                        MessagesController.getInstance().updateInterfaceWithMessages(j, arrayList4);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        if (BuildVars.DEBUG_VERSION && inputPeer != null) {
                            FileLog.m16e("tmessages", "send message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
                        }
                        if (i4 == 0 && (i4 != 9 || str == null || encryptedChat2 == null)) {
                            TLObject tL_messages_forwardMessages;
                            if ((i4 < 1 || i4 > 3) && ((i4 < 5 || i4 > 8) && (i4 != 9 || encryptedChat2 == null))) {
                                if (i4 == 4) {
                                    tL_messages_forwardMessages = new TL_messages_forwardMessages();
                                    tL_messages_forwardMessages.to_peer = inputPeer;
                                    tL_messages_forwardMessages.with_my_score = messageObject2.messageOwner.with_my_score;
                                    if (messageObject2.messageOwner.ttl != 0) {
                                        Chat chat2 = MessagesController.getInstance().getChat(Integer.valueOf(-messageObject2.messageOwner.ttl));
                                        tL_messages_forwardMessages.from_peer = new TL_inputPeerChannel();
                                        tL_messages_forwardMessages.from_peer.channel_id = -messageObject2.messageOwner.ttl;
                                        if (chat2 != null) {
                                            tL_messages_forwardMessages.from_peer.access_hash = chat2.access_hash;
                                        }
                                    } else {
                                        tL_messages_forwardMessages.from_peer = new TL_inputPeerEmpty();
                                    }
                                    if (messageObject2.messageOwner.to_id instanceof TL_peerChannel) {
                                        tL_messages_forwardMessages.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + j, false);
                                    }
                                    tL_messages_forwardMessages.random_id.add(Long.valueOf(message3.random_id));
                                    if (messageObject2.getId() >= 0) {
                                        tL_messages_forwardMessages.id.add(Integer.valueOf(messageObject2.getId()));
                                    } else {
                                        tL_messages_forwardMessages.id.add(Integer.valueOf(messageObject2.messageOwner.fwd_msg_id));
                                    }
                                    performSendMessageRequest(tL_messages_forwardMessages, messageObject3, null);
                                    return;
                                } else if (i4 == 9) {
                                    tL_messages_sendInlineBotResult = new TL_messages_sendInlineBotResult();
                                    tL_messages_sendInlineBotResult.peer = inputPeer;
                                    tL_messages_sendInlineBotResult.random_id = message3.random_id;
                                    if (messageObject != null) {
                                        tL_messages_sendInlineBotResult.flags |= 1;
                                        tL_messages_sendInlineBotResult.reply_to_msg_id = messageObject.getId();
                                    }
                                    if (message3.to_id instanceof TL_peerChannel) {
                                        tL_messages_sendInlineBotResult.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + j, false);
                                    }
                                    tL_messages_sendInlineBotResult.query_id = Utilities.parseLong((String) hashMap.get("query_id")).longValue();
                                    tL_messages_sendInlineBotResult.id = (String) hashMap.get(TtmlNode.ATTR_ID);
                                    if (messageObject2 == null) {
                                        tL_messages_sendInlineBotResult.clear_draft = true;
                                        DraftQuery.cleanDraft(j, false);
                                    }
                                    performSendMessageRequest(tL_messages_sendInlineBotResult, messageObject3, null);
                                    return;
                                } else {
                                    return;
                                }
                            } else if (encryptedChat2 == null) {
                                InputMedia inputMedia;
                                r4 = null;
                                InputMedia tL_inputMediaVenue;
                                if (i4 == 1) {
                                    if (messageMedia instanceof TL_messageMediaVenue) {
                                        tL_inputMediaVenue = new TL_inputMediaVenue();
                                        tL_inputMediaVenue.address = messageMedia.address;
                                        tL_inputMediaVenue.title = messageMedia.title;
                                        tL_inputMediaVenue.provider = messageMedia.provider;
                                        tL_inputMediaVenue.venue_id = messageMedia.venue_id;
                                    } else {
                                        tL_inputMediaVenue = new TL_inputMediaGeoPoint();
                                    }
                                    tL_inputMediaVenue.geo_point = new TL_inputGeoPoint();
                                    tL_inputMediaVenue.geo_point.lat = messageMedia.geo.lat;
                                    tL_inputMediaVenue.geo_point._long = messageMedia.geo._long;
                                    inputMedia = tL_inputMediaVenue;
                                } else if (i4 == 2 || (i4 == 9 && tL_photo != null)) {
                                    if (tL_photo.access_hash == 0) {
                                        InputMedia tL_inputMediaUploadedPhoto = new TL_inputMediaUploadedPhoto();
                                        tL_inputMediaUploadedPhoto.caption = tL_photo.caption != null ? tL_photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        if (hashMap != null) {
                                            stickerSetName = (String) hashMap.get("masks");
                                            if (stickerSetName != null) {
                                                AbstractSerializedData serializedData = new SerializedData(Utilities.hexToBytes(stickerSetName));
                                                i5 = serializedData.readInt32(false);
                                                for (i3 = 0; i3 < i5; i3++) {
                                                    tL_inputMediaUploadedPhoto.stickers.add(InputDocument.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                                                }
                                                tL_inputMediaUploadedPhoto.flags |= 1;
                                            }
                                        }
                                        DelayedMessage delayedMessage = new DelayedMessage();
                                        delayedMessage.originalPath = str3;
                                        delayedMessage.type = 0;
                                        delayedMessage.obj = messageObject3;
                                        if (str2 != null && str2.length() > 0) {
                                            if (str2.startsWith("http")) {
                                                delayedMessage.httpLocation = str2;
                                                inputMedia = tL_inputMediaUploadedPhoto;
                                                r4 = delayedMessage;
                                            }
                                        }
                                        delayedMessage.location = ((PhotoSize) tL_photo.sizes.get(tL_photo.sizes.size() - 1)).location;
                                        inputMedia = tL_inputMediaUploadedPhoto;
                                        r4 = delayedMessage;
                                    } else {
                                        tL_inputMediaVenue = new TL_inputMediaPhoto();
                                        tL_inputMediaVenue.id = new TL_inputPhoto();
                                        tL_inputMediaVenue.caption = tL_photo.caption != null ? tL_photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        tL_inputMediaVenue.id.id = tL_photo.id;
                                        tL_inputMediaVenue.id.access_hash = tL_photo.access_hash;
                                        inputMedia = tL_inputMediaVenue;
                                    }
                                } else if (i4 == 3) {
                                    if (document.access_hash == 0) {
                                        r3 = document.thumb.location != null ? new TL_inputMediaUploadedThumbDocument() : new TL_inputMediaUploadedDocument();
                                        r3.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        r3.mime_type = document.mime_type;
                                        r3.attributes = document.attributes;
                                        r2 = new DelayedMessage();
                                        r2.originalPath = str3;
                                        r2.type = 1;
                                        r2.obj = messageObject3;
                                        r2.location = document.thumb.location;
                                        r2.documentLocation = document;
                                        r2.videoEditedInfo = videoEditedInfo;
                                        r4 = r2;
                                        inputMedia = r3;
                                    } else {
                                        tL_inputMediaVenue = new TL_inputMediaDocument();
                                        tL_inputMediaVenue.id = new TL_inputDocument();
                                        tL_inputMediaVenue.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        tL_inputMediaVenue.id.id = document.id;
                                        tL_inputMediaVenue.id.access_hash = document.access_hash;
                                        inputMedia = tL_inputMediaVenue;
                                    }
                                } else if (i4 == 6) {
                                    tL_inputMediaVenue = new TL_inputMediaContact();
                                    tL_inputMediaVenue.phone_number = user.phone;
                                    tL_inputMediaVenue.first_name = user.first_name;
                                    tL_inputMediaVenue.last_name = user.last_name;
                                    inputMedia = tL_inputMediaVenue;
                                } else if (i4 == 7 || i4 == 9) {
                                    if (document.access_hash == 0) {
                                        if (encryptedChat2 != null || str3 == null || str3.length() <= 0 || !str3.startsWith("http") || hashMap == null) {
                                            r3 = (document.thumb.location == null || !(document.thumb.location instanceof TL_fileLocation)) ? new TL_inputMediaUploadedDocument() : new TL_inputMediaUploadedThumbDocument();
                                            r2 = new DelayedMessage();
                                            r2.originalPath = str3;
                                            r2.type = 2;
                                            r2.obj = messageObject3;
                                            r2.documentLocation = document;
                                            r2.location = document.thumb.location;
                                        } else {
                                            r3 = new TL_inputMediaGifExternal();
                                            String[] split = ((String) hashMap.get("url")).split("\\|");
                                            if (split.length == 2) {
                                                ((TL_inputMediaGifExternal) r3).url = split[0];
                                                r3.f2662q = split[1];
                                            }
                                            r2 = null;
                                        }
                                        r3.mime_type = document.mime_type;
                                        r3.attributes = document.attributes;
                                        r3.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        r4 = r2;
                                        inputMedia = r3;
                                    } else {
                                        tL_inputMediaVenue = new TL_inputMediaDocument();
                                        tL_inputMediaVenue.id = new TL_inputDocument();
                                        tL_inputMediaVenue.id.id = document.id;
                                        tL_inputMediaVenue.id.access_hash = document.access_hash;
                                        tL_inputMediaVenue.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        inputMedia = tL_inputMediaVenue;
                                    }
                                } else if (i4 != 8) {
                                    inputMedia = null;
                                } else if (document.access_hash == 0) {
                                    r3 = new TL_inputMediaUploadedDocument();
                                    r3.mime_type = document.mime_type;
                                    r3.attributes = document.attributes;
                                    r3.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    r2 = new DelayedMessage();
                                    r2.type = 3;
                                    r2.obj = messageObject3;
                                    r2.documentLocation = document;
                                    r4 = r2;
                                    inputMedia = r3;
                                } else {
                                    tL_inputMediaVenue = new TL_inputMediaDocument();
                                    tL_inputMediaVenue.id = new TL_inputDocument();
                                    tL_inputMediaVenue.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    tL_inputMediaVenue.id.id = document.id;
                                    tL_inputMediaVenue.id.access_hash = document.access_hash;
                                    inputMedia = tL_inputMediaVenue;
                                }
                                if (arrayList2 != null) {
                                    tL_messages_forwardMessages = new TL_messages_sendBroadcast();
                                    ArrayList arrayList6 = new ArrayList();
                                    for (int i7 = 0; i7 < arrayList2.size(); i7++) {
                                        arrayList6.add(Long.valueOf(Utilities.random.nextLong()));
                                    }
                                    tL_messages_forwardMessages.contacts = arrayList2;
                                    tL_messages_forwardMessages.media = inputMedia;
                                    tL_messages_forwardMessages.random_id = arrayList6;
                                    tL_messages_forwardMessages.message = TtmlNode.ANONYMOUS_REGION_ID;
                                    if (r4 != null) {
                                        r4.sendRequest = tL_messages_forwardMessages;
                                    }
                                    if (messageObject2 == null) {
                                        DraftQuery.cleanDraft(j, false);
                                    }
                                } else {
                                    tL_messages_forwardMessages = new TL_messages_sendMedia();
                                    tL_messages_forwardMessages.peer = inputPeer;
                                    if (message3.to_id instanceof TL_peerChannel) {
                                        tL_messages_forwardMessages.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + j, false);
                                    }
                                    tL_messages_forwardMessages.random_id = message3.random_id;
                                    tL_messages_forwardMessages.media = inputMedia;
                                    if (messageObject != null) {
                                        tL_messages_forwardMessages.flags |= 1;
                                        tL_messages_forwardMessages.reply_to_msg_id = messageObject.getId();
                                    }
                                    if (r4 != null) {
                                        r4.sendRequest = tL_messages_forwardMessages;
                                    }
                                }
                                if (i4 == 1) {
                                    performSendMessageRequest(tL_messages_forwardMessages, messageObject3, null);
                                    return;
                                } else if (i4 == 2) {
                                    if (tL_photo.access_hash == 0) {
                                        performSendDelayedMessage(r4);
                                        return;
                                    } else {
                                        performSendMessageRequest(tL_messages_forwardMessages, messageObject3, null);
                                        return;
                                    }
                                } else if (i4 == 3) {
                                    if (document.access_hash == 0) {
                                        performSendDelayedMessage(r4);
                                        return;
                                    } else {
                                        performSendMessageRequest(tL_messages_forwardMessages, messageObject3, null);
                                        return;
                                    }
                                } else if (i4 == 6) {
                                    performSendMessageRequest(tL_messages_forwardMessages, messageObject3, null);
                                    return;
                                } else if (i4 == 7) {
                                    if (document.access_hash != 0 || r4 == null) {
                                        performSendMessageRequest(tL_messages_forwardMessages, messageObject3, str3);
                                        return;
                                    } else {
                                        performSendDelayedMessage(r4);
                                        return;
                                    }
                                } else if (i4 != 8) {
                                    return;
                                } else {
                                    if (document.access_hash == 0) {
                                        performSendDelayedMessage(r4);
                                        return;
                                    } else {
                                        performSendMessageRequest(tL_messages_forwardMessages, messageObject3, null);
                                        return;
                                    }
                                }
                            } else {
                                if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) {
                                    tL_decryptedMessage = new TL_decryptedMessage();
                                    tL_decryptedMessage.ttl = message3.ttl;
                                    if (!(arrayList == null || arrayList.isEmpty())) {
                                        tL_decryptedMessage.entities = arrayList;
                                        tL_decryptedMessage.flags |= TLRPC.USER_FLAG_UNUSED;
                                    }
                                    if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                                        tL_decryptedMessage.reply_to_random_id = messageObject.messageOwner.random_id;
                                        tL_decryptedMessage.flags |= 8;
                                    }
                                    tL_decryptedMessage.flags |= TLRPC.USER_FLAG_UNUSED3;
                                } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                                    tL_decryptedMessage = new TL_decryptedMessage_layer17();
                                    tL_decryptedMessage.ttl = message3.ttl;
                                } else {
                                    tL_decryptedMessage = new TL_decryptedMessage_layer8();
                                    tL_decryptedMessage.random_bytes = new byte[15];
                                    Utilities.random.nextBytes(tL_decryptedMessage.random_bytes);
                                }
                                if (hashMap != null) {
                                    if (hashMap.get("bot_name") != null) {
                                        tL_decryptedMessage.via_bot_name = (String) hashMap.get("bot_name");
                                        tL_decryptedMessage.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                                    }
                                }
                                tL_decryptedMessage.random_id = message3.random_id;
                                tL_decryptedMessage.message = TtmlNode.ANONYMOUS_REGION_ID;
                                if (i4 == 1) {
                                    if (!(messageMedia instanceof TL_messageMediaVenue) || AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 46) {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaGeoPoint();
                                    } else {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaVenue();
                                        tL_decryptedMessage.media.address = messageMedia.address;
                                        tL_decryptedMessage.media.title = messageMedia.title;
                                        tL_decryptedMessage.media.provider = messageMedia.provider;
                                        tL_decryptedMessage.media.venue_id = messageMedia.venue_id;
                                    }
                                    tL_decryptedMessage.media.lat = messageMedia.geo.lat;
                                    tL_decryptedMessage.media._long = messageMedia.geo._long;
                                    SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, null, null, messageObject3);
                                } else if (i4 == 2 || (i4 == 9 && tL_photo != null)) {
                                    PhotoSize photoSize = (PhotoSize) tL_photo.sizes.get(0);
                                    PhotoSize photoSize2 = (PhotoSize) tL_photo.sizes.get(tL_photo.sizes.size() - 1);
                                    ImageLoader.fillPhotoSizeWithBytes(photoSize);
                                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaPhoto();
                                        tL_decryptedMessage.media.caption = tL_photo.caption != null ? tL_photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        if (photoSize.bytes != null) {
                                            ((TL_decryptedMessageMediaPhoto) tL_decryptedMessage.media).thumb = photoSize.bytes;
                                        } else {
                                            ((TL_decryptedMessageMediaPhoto) tL_decryptedMessage.media).thumb = new byte[0];
                                        }
                                    } else {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaPhoto_layer8();
                                        if (photoSize.bytes != null) {
                                            ((TL_decryptedMessageMediaPhoto_layer8) tL_decryptedMessage.media).thumb = photoSize.bytes;
                                        } else {
                                            ((TL_decryptedMessageMediaPhoto_layer8) tL_decryptedMessage.media).thumb = new byte[0];
                                        }
                                    }
                                    tL_decryptedMessage.media.thumb_h = photoSize.f2663h;
                                    tL_decryptedMessage.media.thumb_w = photoSize.f2664w;
                                    tL_decryptedMessage.media.f2657w = photoSize2.f2664w;
                                    tL_decryptedMessage.media.f2656h = photoSize2.f2663h;
                                    tL_decryptedMessage.media.size = photoSize2.size;
                                    if (photoSize2.location.key == null) {
                                        r4 = new DelayedMessage();
                                        r4.originalPath = str3;
                                        r4.sendEncryptedRequest = tL_decryptedMessage;
                                        r4.type = 0;
                                        r4.obj = messageObject3;
                                        r4.encryptedChat = encryptedChat2;
                                        if (str2 != null && str2.length() > 0) {
                                            if (str2.startsWith("http")) {
                                                r4.httpLocation = str2;
                                                performSendDelayedMessage(r4);
                                            }
                                        }
                                        r4.location = ((PhotoSize) tL_photo.sizes.get(tL_photo.sizes.size() - 1)).location;
                                        performSendDelayedMessage(r4);
                                    } else {
                                        r6 = new TL_inputEncryptedFile();
                                        r6.id = photoSize2.location.volume_id;
                                        r6.access_hash = photoSize2.location.secret;
                                        tL_decryptedMessage.media.key = photoSize2.location.key;
                                        tL_decryptedMessage.media.iv = photoSize2.location.iv;
                                        SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, r6, null, messageObject3);
                                    }
                                } else if (i4 == 3) {
                                    ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) {
                                        if (MessageObject.isNewGifDocument(document)) {
                                            tL_decryptedMessage.media = new TL_decryptedMessageMediaDocument();
                                            tL_decryptedMessage.media.attributes = document.attributes;
                                            if (document.thumb == null || document.thumb.bytes == null) {
                                                ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb = new byte[0];
                                            } else {
                                                ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb = document.thumb.bytes;
                                            }
                                        } else {
                                            tL_decryptedMessage.media = new TL_decryptedMessageMediaVideo();
                                            if (document.thumb == null || document.thumb.bytes == null) {
                                                ((TL_decryptedMessageMediaVideo) tL_decryptedMessage.media).thumb = new byte[0];
                                            } else {
                                                ((TL_decryptedMessageMediaVideo) tL_decryptedMessage.media).thumb = document.thumb.bytes;
                                            }
                                        }
                                    } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaVideo_layer17();
                                        if (document.thumb == null || document.thumb.bytes == null) {
                                            ((TL_decryptedMessageMediaVideo_layer17) tL_decryptedMessage.media).thumb = new byte[0];
                                        } else {
                                            ((TL_decryptedMessageMediaVideo_layer17) tL_decryptedMessage.media).thumb = document.thumb.bytes;
                                        }
                                    } else {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaVideo_layer8();
                                        if (document.thumb == null || document.thumb.bytes == null) {
                                            ((TL_decryptedMessageMediaVideo_layer8) tL_decryptedMessage.media).thumb = new byte[0];
                                        } else {
                                            ((TL_decryptedMessageMediaVideo_layer8) tL_decryptedMessage.media).thumb = document.thumb.bytes;
                                        }
                                    }
                                    tL_decryptedMessage.media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    tL_decryptedMessage.media.mime_type = MimeTypes.VIDEO_MP4;
                                    tL_decryptedMessage.media.size = document.size;
                                    for (r4 = 0; r4 < document.attributes.size(); r4++) {
                                        documentAttribute = (DocumentAttribute) document.attributes.get(r4);
                                        if (documentAttribute instanceof TL_documentAttributeVideo) {
                                            tL_decryptedMessage.media.f2657w = documentAttribute.f2659w;
                                            tL_decryptedMessage.media.f2656h = documentAttribute.f2658h;
                                            tL_decryptedMessage.media.duration = documentAttribute.duration;
                                            break;
                                        }
                                    }
                                    tL_decryptedMessage.media.thumb_h = document.thumb.f2663h;
                                    tL_decryptedMessage.media.thumb_w = document.thumb.f2664w;
                                    if (document.access_hash == 0) {
                                        r2 = new DelayedMessage();
                                        r2.originalPath = str3;
                                        r2.sendEncryptedRequest = tL_decryptedMessage;
                                        r2.type = 1;
                                        r2.obj = messageObject3;
                                        r2.encryptedChat = encryptedChat2;
                                        r2.documentLocation = document;
                                        r2.videoEditedInfo = videoEditedInfo;
                                        performSendDelayedMessage(r2);
                                    } else {
                                        r6 = new TL_inputEncryptedFile();
                                        r6.id = document.id;
                                        r6.access_hash = document.access_hash;
                                        tL_decryptedMessage.media.key = document.key;
                                        tL_decryptedMessage.media.iv = document.iv;
                                        SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, r6, null, messageObject3);
                                    }
                                } else if (i4 == 6) {
                                    tL_decryptedMessage.media = new TL_decryptedMessageMediaContact();
                                    tL_decryptedMessage.media.phone_number = user.phone;
                                    tL_decryptedMessage.media.first_name = user.first_name;
                                    tL_decryptedMessage.media.last_name = user.last_name;
                                    tL_decryptedMessage.media.user_id = user.id;
                                    SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, null, null, messageObject3);
                                } else if (i4 == 7 || (i4 == 9 && document != null)) {
                                    if (MessageObject.isStickerDocument(document)) {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaExternalDocument();
                                        tL_decryptedMessage.media.id = document.id;
                                        tL_decryptedMessage.media.date = document.date;
                                        tL_decryptedMessage.media.access_hash = document.access_hash;
                                        tL_decryptedMessage.media.mime_type = document.mime_type;
                                        tL_decryptedMessage.media.size = document.size;
                                        tL_decryptedMessage.media.dc_id = document.dc_id;
                                        tL_decryptedMessage.media.attributes = document.attributes;
                                        if (document.thumb == null) {
                                            ((TL_decryptedMessageMediaExternalDocument) tL_decryptedMessage.media).thumb = new TL_photoSizeEmpty();
                                            ((TL_decryptedMessageMediaExternalDocument) tL_decryptedMessage.media).thumb.type = "s";
                                        } else {
                                            ((TL_decryptedMessageMediaExternalDocument) tL_decryptedMessage.media).thumb = document.thumb;
                                        }
                                        SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, null, null, messageObject3);
                                    } else {
                                        ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) {
                                            tL_decryptedMessage.media = new TL_decryptedMessageMediaDocument();
                                            tL_decryptedMessage.media.attributes = document.attributes;
                                            tL_decryptedMessage.media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                            if (document.thumb == null || document.thumb.bytes == null) {
                                                ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb = new byte[0];
                                                tL_decryptedMessage.media.thumb_h = 0;
                                                tL_decryptedMessage.media.thumb_w = 0;
                                            } else {
                                                ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb = document.thumb.bytes;
                                                tL_decryptedMessage.media.thumb_h = document.thumb.f2663h;
                                                tL_decryptedMessage.media.thumb_w = document.thumb.f2664w;
                                            }
                                        } else {
                                            tL_decryptedMessage.media = new TL_decryptedMessageMediaDocument_layer8();
                                            tL_decryptedMessage.media.file_name = FileLoader.getDocumentFileName(document);
                                            if (document.thumb == null || document.thumb.bytes == null) {
                                                ((TL_decryptedMessageMediaDocument_layer8) tL_decryptedMessage.media).thumb = new byte[0];
                                                tL_decryptedMessage.media.thumb_h = 0;
                                                tL_decryptedMessage.media.thumb_w = 0;
                                            } else {
                                                ((TL_decryptedMessageMediaDocument_layer8) tL_decryptedMessage.media).thumb = document.thumb.bytes;
                                                tL_decryptedMessage.media.thumb_h = document.thumb.f2663h;
                                                tL_decryptedMessage.media.thumb_w = document.thumb.f2664w;
                                            }
                                        }
                                        tL_decryptedMessage.media.size = document.size;
                                        tL_decryptedMessage.media.mime_type = document.mime_type;
                                        if (document.key == null) {
                                            r2 = new DelayedMessage();
                                            r2.originalPath = str3;
                                            r2.sendEncryptedRequest = tL_decryptedMessage;
                                            r2.type = 2;
                                            r2.obj = messageObject3;
                                            r2.encryptedChat = encryptedChat2;
                                            if (str2 != null && str2.length() > 0) {
                                                if (str2.startsWith("http")) {
                                                    r2.httpLocation = str2;
                                                }
                                            }
                                            r2.documentLocation = document;
                                            performSendDelayedMessage(r2);
                                        } else {
                                            r6 = new TL_inputEncryptedFile();
                                            r6.id = document.id;
                                            r6.access_hash = document.access_hash;
                                            tL_decryptedMessage.media.key = document.key;
                                            tL_decryptedMessage.media.iv = document.iv;
                                            SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, r6, null, messageObject3);
                                        }
                                    }
                                } else if (i4 == 8) {
                                    DelayedMessage delayedMessage2 = new DelayedMessage();
                                    delayedMessage2.encryptedChat = encryptedChat2;
                                    delayedMessage2.sendEncryptedRequest = tL_decryptedMessage;
                                    delayedMessage2.obj = messageObject3;
                                    delayedMessage2.documentLocation = document;
                                    delayedMessage2.type = 3;
                                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) {
                                        tL_decryptedMessage.media = new TL_decryptedMessageMediaDocument();
                                        tL_decryptedMessage.media.attributes = document.attributes;
                                        tL_decryptedMessage.media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                        if (document.thumb == null || document.thumb.bytes == null) {
                                            ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb = new byte[0];
                                            tL_decryptedMessage.media.thumb_h = 0;
                                            tL_decryptedMessage.media.thumb_w = 0;
                                        } else {
                                            ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb = document.thumb.bytes;
                                            tL_decryptedMessage.media.thumb_h = document.thumb.f2663h;
                                            tL_decryptedMessage.media.thumb_w = document.thumb.f2664w;
                                        }
                                        tL_decryptedMessage.media.mime_type = document.mime_type;
                                        tL_decryptedMessage.media.size = document.size;
                                        delayedMessage2.originalPath = str3;
                                    } else {
                                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                                            tL_decryptedMessage.media = new TL_decryptedMessageMediaAudio();
                                        } else {
                                            tL_decryptedMessage.media = new TL_decryptedMessageMediaAudio_layer8();
                                        }
                                        for (r4 = 0; r4 < document.attributes.size(); r4++) {
                                            documentAttribute = (DocumentAttribute) document.attributes.get(r4);
                                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                                tL_decryptedMessage.media.duration = documentAttribute.duration;
                                                break;
                                            }
                                        }
                                        tL_decryptedMessage.media.mime_type = "audio/ogg";
                                        tL_decryptedMessage.media.size = document.size;
                                        delayedMessage2.type = 3;
                                    }
                                    performSendDelayedMessage(delayedMessage2);
                                }
                                if (messageObject2 == null) {
                                    DraftQuery.cleanDraft(j, false);
                                    return;
                                }
                                return;
                            }
                        } else if (encryptedChat2 == null) {
                            if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) {
                                tL_decryptedMessage = new TL_decryptedMessage();
                                tL_decryptedMessage.ttl = message3.ttl;
                                if (!(arrayList == null || arrayList.isEmpty())) {
                                    tL_decryptedMessage.entities = arrayList;
                                    tL_decryptedMessage.flags |= TLRPC.USER_FLAG_UNUSED;
                                }
                                if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                                    tL_decryptedMessage.reply_to_random_id = messageObject.messageOwner.random_id;
                                    tL_decryptedMessage.flags |= 8;
                                }
                            } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 17) {
                                tL_decryptedMessage = new TL_decryptedMessage_layer17();
                                tL_decryptedMessage.ttl = message3.ttl;
                            } else {
                                tL_decryptedMessage = new TL_decryptedMessage_layer8();
                                tL_decryptedMessage.random_bytes = new byte[15];
                                Utilities.random.nextBytes(tL_decryptedMessage.random_bytes);
                            }
                            if (hashMap != null) {
                                if (hashMap.get("bot_name") != null) {
                                    tL_decryptedMessage.via_bot_name = (String) hashMap.get("bot_name");
                                    tL_decryptedMessage.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                                }
                            }
                            tL_decryptedMessage.random_id = message3.random_id;
                            tL_decryptedMessage.message = str;
                            if (webPage != null || webPage.url == null) {
                                tL_decryptedMessage.media = new TL_decryptedMessageMediaEmpty();
                            } else {
                                tL_decryptedMessage.media = new TL_decryptedMessageMediaWebPage();
                                tL_decryptedMessage.media.url = webPage.url;
                                tL_decryptedMessage.flags |= TLRPC.USER_FLAG_UNUSED3;
                            }
                            SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, null, null, messageObject3);
                            if (messageObject2 == null) {
                                DraftQuery.cleanDraft(j, false);
                            }
                        } else if (arrayList2 == null) {
                            tL_messages_sendInlineBotResult = new TL_messages_sendBroadcast();
                            arrayList3 = new ArrayList();
                            for (i3 = 0; i3 < arrayList2.size(); i3++) {
                                arrayList3.add(Long.valueOf(Utilities.random.nextLong()));
                            }
                            tL_messages_sendInlineBotResult.message = str;
                            tL_messages_sendInlineBotResult.contacts = arrayList2;
                            tL_messages_sendInlineBotResult.media = new TL_inputMediaEmpty();
                            tL_messages_sendInlineBotResult.random_id = arrayList3;
                            performSendMessageRequest(tL_messages_sendInlineBotResult, messageObject3, null);
                        } else {
                            tL_messages_sendInlineBotResult = new TL_messages_sendMessage();
                            tL_messages_sendInlineBotResult.message = str;
                            tL_messages_sendInlineBotResult.clear_draft = messageObject2 != null;
                            if (message3.to_id instanceof TL_peerChannel) {
                                tL_messages_sendInlineBotResult.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + j, false);
                            }
                            tL_messages_sendInlineBotResult.peer = inputPeer;
                            tL_messages_sendInlineBotResult.random_id = message3.random_id;
                            if (messageObject != null) {
                                tL_messages_sendInlineBotResult.flags |= 1;
                                tL_messages_sendInlineBotResult.reply_to_msg_id = messageObject.getId();
                            }
                            if (!z) {
                                tL_messages_sendInlineBotResult.no_webpage = true;
                            }
                            if (!(arrayList == null || arrayList.isEmpty())) {
                                tL_messages_sendInlineBotResult.entities = arrayList;
                                tL_messages_sendInlineBotResult.flags |= 8;
                            }
                            performSendMessageRequest(tL_messages_sendInlineBotResult, messageObject3, null);
                            if (messageObject2 == null) {
                                DraftQuery.cleanDraft(j, false);
                            }
                        }
                    } catch (Exception e6) {
                        e = e6;
                        message = message3;
                        FileLog.m18e("tmessages", e);
                        MessagesStorage.getInstance().markMessageAsSendError(message);
                        if (messageObject3 != null) {
                            messageObject3.messageOwner.send_state = 2;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                        processSentMessage(message.id);
                    }
                }
            }
            str3 = null;
            message2 = null;
            i = (int) j;
            i2 = (int) (j >> 32);
            obj = null;
            if (i == 0) {
            }
            if (i == 0) {
                encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i2));
                if (encryptedChat == null) {
                    encryptedChat2 = encryptedChat;
                } else if (messageObject2 != null) {
                    MessagesStorage.getInstance().markMessageAsSendError(messageObject2.messageOwner);
                    messageObject2.messageOwner.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject2.getId()));
                    processSentMessage(messageObject2.getId());
                    return;
                } else {
                    return;
                }
            } else if (inputPeer instanceof TL_inputPeerChannel) {
                encryptedChat2 = null;
            } else {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(inputPeer.channel_id));
                if (chat != null) {
                }
                encryptedChat2 = null;
                obj = obj2;
            }
            if (messageObject2 == null) {
                if (str != null) {
                    if (encryptedChat2 != null) {
                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                            message2 = new TL_message_secret();
                            if (arrayList != null) {
                                if (arrayList.isEmpty()) {
                                    message2.entities = arrayList;
                                }
                            }
                            if (webPage.url == null) {
                                tL_webPageUrlPending = new TL_webPageUrlPending();
                                tL_webPageUrlPending.url = webPage.url;
                                webPage = tL_webPageUrlPending;
                            } else {
                                webPage = null;
                            }
                            if (webPage != null) {
                                message2.media = new TL_messageMediaEmpty();
                            } else {
                                message2.media = new TL_messageMediaWebPage();
                                message2.media.webpage = webPage;
                            }
                            if (hashMap != null) {
                                if (hashMap.containsKey("query_id")) {
                                    i3 = 9;
                                    message2.message = str;
                                }
                            }
                            i3 = 0;
                            message2.message = str;
                        }
                    }
                    message2 = new TL_message();
                    if (arrayList != null) {
                        if (arrayList.isEmpty()) {
                            message2.entities = arrayList;
                        }
                    }
                    if (webPage.url == null) {
                        webPage = null;
                    } else {
                        tL_webPageUrlPending = new TL_webPageUrlPending();
                        tL_webPageUrlPending.url = webPage.url;
                        webPage = tL_webPageUrlPending;
                    }
                    if (webPage != null) {
                        message2.media = new TL_messageMediaWebPage();
                        message2.media.webpage = webPage;
                    } else {
                        message2.media = new TL_messageMediaEmpty();
                    }
                    if (hashMap != null) {
                        if (hashMap.containsKey("query_id")) {
                            i3 = 9;
                            message2.message = str;
                        }
                    }
                    i3 = 0;
                    message2.message = str;
                } else if (messageMedia != null) {
                    if (encryptedChat2 != null) {
                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                            message2 = new TL_message_secret();
                            message2.media = messageMedia;
                            message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                            if (hashMap != null) {
                                if (hashMap.containsKey("query_id")) {
                                    i3 = 9;
                                }
                            }
                            i3 = 1;
                        }
                    }
                    message2 = new TL_message();
                    message2.media = messageMedia;
                    message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                    if (hashMap != null) {
                        if (hashMap.containsKey("query_id")) {
                            i3 = 9;
                        }
                    }
                    i3 = 1;
                } else if (tL_photo != null) {
                    if (encryptedChat2 != null) {
                    }
                    message2.media = new TL_messageMediaPhoto();
                    if (tL_photo.caption == null) {
                    }
                    message2.media.caption = tL_photo.caption == null ? tL_photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    message2.media.photo = tL_photo;
                    if (hashMap != null) {
                        if (hashMap.containsKey("query_id")) {
                            i5 = 9;
                            message2.message = "-1";
                            if (str2.startsWith("http")) {
                                message2.attachPath = str2;
                                i3 = i5;
                            }
                            message2.attachPath = FileLoader.getPathToAttach(((PhotoSize) tL_photo.sizes.get(tL_photo.sizes.size() - 1)).location, true).toString();
                            i3 = i5;
                        }
                    }
                    i5 = 2;
                    message2.message = "-1";
                    if (str2.startsWith("http")) {
                        message2.attachPath = str2;
                        i3 = i5;
                    }
                    message2.attachPath = FileLoader.getPathToAttach(((PhotoSize) tL_photo.sizes.get(tL_photo.sizes.size() - 1)).location, true).toString();
                    i3 = i5;
                } else if (tL_game != null) {
                    tL_message = new TL_message();
                    tL_message.media = new TL_messageMediaGame();
                    tL_message.media.caption = TtmlNode.ANONYMOUS_REGION_ID;
                    tL_message.media.game = tL_game;
                    tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
                    if (hashMap != null) {
                        if (hashMap.containsKey("query_id")) {
                            i3 = 9;
                            message2 = tL_message;
                        }
                    }
                    i3 = -1;
                    message2 = tL_message;
                } else if (user != null) {
                    if (encryptedChat2 != null) {
                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                            message2 = new TL_message_secret();
                            message2.media = new TL_messageMediaContact();
                            message2.media.phone_number = user.phone;
                            message2.media.first_name = user.first_name;
                            message2.media.last_name = user.last_name;
                            message2.media.user_id = user.id;
                            if (message2.media.first_name == null) {
                                messageMedia2 = message2.media;
                                str4 = TtmlNode.ANONYMOUS_REGION_ID;
                                messageMedia2.first_name = str4;
                                user.first_name = str4;
                            }
                            if (message2.media.last_name == null) {
                                messageMedia2 = message2.media;
                                str4 = TtmlNode.ANONYMOUS_REGION_ID;
                                messageMedia2.last_name = str4;
                                user.last_name = str4;
                            }
                            message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                            if (hashMap != null) {
                                if (hashMap.containsKey("query_id")) {
                                    i3 = 9;
                                }
                            }
                            i3 = 6;
                        }
                    }
                    message2 = new TL_message();
                    message2.media = new TL_messageMediaContact();
                    message2.media.phone_number = user.phone;
                    message2.media.first_name = user.first_name;
                    message2.media.last_name = user.last_name;
                    message2.media.user_id = user.id;
                    if (message2.media.first_name == null) {
                        messageMedia2 = message2.media;
                        str4 = TtmlNode.ANONYMOUS_REGION_ID;
                        messageMedia2.first_name = str4;
                        user.first_name = str4;
                    }
                    if (message2.media.last_name == null) {
                        messageMedia2 = message2.media;
                        str4 = TtmlNode.ANONYMOUS_REGION_ID;
                        messageMedia2.last_name = str4;
                        user.last_name = str4;
                    }
                    message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                    if (hashMap != null) {
                        if (hashMap.containsKey("query_id")) {
                            i3 = 9;
                        }
                    }
                    i3 = 6;
                } else if (tL_document == null) {
                    i3 = -1;
                } else {
                    if (encryptedChat2 != null) {
                    }
                    message2.media = new TL_messageMediaDocument();
                    if (tL_document.caption == null) {
                    }
                    message2.media.caption = tL_document.caption == null ? tL_document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    message2.media.document = tL_document;
                    if (hashMap != null) {
                        if (hashMap.containsKey("query_id")) {
                            i5 = 9;
                            if (videoEditedInfo == null) {
                                message2.message = videoEditedInfo.getString();
                            } else {
                                message2.message = "-1";
                            }
                            if (encryptedChat2 != null) {
                            }
                            message2.attachPath = str2;
                            i6 = 0;
                            while (i6 < tL_document.attributes.size()) {
                                documentAttribute = (DocumentAttribute) tL_document.attributes.get(i6);
                                if (!(documentAttribute instanceof TL_documentAttributeSticker)) {
                                    i6++;
                                } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 46) {
                                    tL_document.attributes.remove(i6);
                                    tL_documentAttributeSticker_layer55 = new TL_documentAttributeSticker_layer55();
                                    tL_document.attributes.add(tL_documentAttributeSticker_layer55);
                                    tL_documentAttributeSticker_layer55.alt = documentAttribute.alt;
                                    if (documentAttribute.stickerset != null) {
                                        tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                    } else {
                                        stickerSetName = StickersQuery.getStickerSetName(documentAttribute.stickerset.id);
                                        if (stickerSetName != null) {
                                        }
                                        tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                                    }
                                    i3 = i5;
                                } else {
                                    tL_document.attributes.remove(i6);
                                    tL_document.attributes.add(new TL_documentAttributeSticker_old());
                                    i3 = i5;
                                }
                            }
                            i3 = i5;
                        }
                    }
                    if (MessageObject.isVideoDocument(tL_document)) {
                    }
                    i5 = 3;
                    if (videoEditedInfo == null) {
                        message2.message = "-1";
                    } else {
                        message2.message = videoEditedInfo.getString();
                    }
                    if (encryptedChat2 != null) {
                    }
                    message2.attachPath = str2;
                    i6 = 0;
                    while (i6 < tL_document.attributes.size()) {
                        documentAttribute = (DocumentAttribute) tL_document.attributes.get(i6);
                        if (!(documentAttribute instanceof TL_documentAttributeSticker)) {
                            i6++;
                        } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 46) {
                            tL_document.attributes.remove(i6);
                            tL_document.attributes.add(new TL_documentAttributeSticker_old());
                            i3 = i5;
                        } else {
                            tL_document.attributes.remove(i6);
                            tL_documentAttributeSticker_layer55 = new TL_documentAttributeSticker_layer55();
                            tL_document.attributes.add(tL_documentAttributeSticker_layer55);
                            tL_documentAttributeSticker_layer55.alt = documentAttribute.alt;
                            if (documentAttribute.stickerset != null) {
                                stickerSetName = StickersQuery.getStickerSetName(documentAttribute.stickerset.id);
                                if (stickerSetName != null) {
                                }
                                tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                            } else {
                                tL_documentAttributeSticker_layer55.stickerset = new TL_inputStickerSetEmpty();
                            }
                            i3 = i5;
                        }
                    }
                    i3 = i5;
                }
                if (message2.attachPath == null) {
                    message2.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                }
                i5 = UserConfig.getNewMessageId();
                message2.id = i5;
                message2.local_id = i5;
                message2.out = true;
                if (obj != null) {
                }
                message2.from_id = UserConfig.getClientUserId();
                message2.flags |= TLRPC.USER_FLAG_UNUSED2;
                UserConfig.saveConfig(false);
                i4 = i3;
                message3 = message2;
            } else {
                message2 = messageObject2.messageOwner;
                if (messageObject2.isForwarded()) {
                    if (messageObject2.type == 0) {
                        if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                            str = message2.message;
                        }
                        i3 = 0;
                    } else if (messageObject2.type == 4) {
                        messageMedia = message2.media;
                        i3 = 1;
                    } else if (messageObject2.type != 1) {
                        if (messageObject2.type != 3) {
                        }
                        document = (TL_document) message2.media.document;
                        i3 = 3;
                    } else {
                        tL_photo = (TL_photo) message2.media.photo;
                        i3 = 2;
                    }
                    if (hashMap != null) {
                        if (hashMap.containsKey("query_id")) {
                            i4 = 9;
                            message3 = message2;
                        }
                    }
                    i4 = i3;
                    message3 = message2;
                } else {
                    i4 = 4;
                    message3 = message2;
                }
            }
            try {
                if (message3.random_id == 0) {
                    message3.random_id = getNextRandomId();
                }
                if (hashMap != null) {
                    if (hashMap.containsKey("bot")) {
                        if (encryptedChat2 == null) {
                            message3.via_bot_id = Utilities.parseInt((String) hashMap.get("bot")).intValue();
                        } else {
                            message3.via_bot_name = (String) hashMap.get("bot_name");
                            if (message3.via_bot_name == null) {
                                message3.via_bot_name = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                        }
                        message3.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                    }
                }
                message3.params = hashMap;
                message3.date = ConnectionsManager.getInstance().getCurrentTime();
                if (inputPeer instanceof TL_inputPeerChannel) {
                    message3.unread = true;
                } else {
                    if (obj != null) {
                        message3.views = 1;
                        message3.flags |= TLRPC.MESSAGE_FLAG_HAS_VIEWS;
                    }
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(inputPeer.channel_id));
                    if (chat != null) {
                        if (chat.megagroup) {
                            message3.post = true;
                            if (chat.signatures) {
                                message3.from_id = UserConfig.getClientUserId();
                            }
                        } else {
                            message3.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                            message3.unread = true;
                        }
                    }
                }
                message3.flags |= TLRPC.USER_FLAG_UNUSED3;
                message3.dialog_id = j;
                if (messageObject != null) {
                    if (encryptedChat2 != null) {
                    }
                    message3.flags |= 8;
                    message3.reply_to_msg_id = messageObject.getId();
                }
                message3.flags |= 64;
                message3.reply_markup = replyMarkup;
                if (i != 0) {
                    message3.to_id = new TL_peerUser();
                    if (encryptedChat2.participant_id != UserConfig.getClientUserId()) {
                        message3.to_id.user_id = encryptedChat2.participant_id;
                    } else {
                        message3.to_id.user_id = encryptedChat2.admin_id;
                    }
                    message3.ttl = encryptedChat2.ttl;
                    if (message3.ttl != 0) {
                        if (MessageObject.isVoiceMessage(message3)) {
                            while (i5 < message3.media.document.attributes.size()) {
                                documentAttribute = (DocumentAttribute) message3.media.document.attributes.get(i5);
                                if (documentAttribute instanceof TL_documentAttributeAudio) {
                                    i3 = documentAttribute.duration;
                                    break;
                                    message3.ttl = Math.max(encryptedChat2.ttl, i3 + 1);
                                    arrayList2 = null;
                                } else {
                                }
                            }
                            i3 = 0;
                            message3.ttl = Math.max(encryptedChat2.ttl, i3 + 1);
                            arrayList2 = null;
                        } else if (MessageObject.isVideoMessage(message3)) {
                            while (i5 < message3.media.document.attributes.size()) {
                                documentAttribute = (DocumentAttribute) message3.media.document.attributes.get(i5);
                                if (documentAttribute instanceof TL_documentAttributeVideo) {
                                    i3 = documentAttribute.duration;
                                    break;
                                    message3.ttl = Math.max(encryptedChat2.ttl, i3 + 1);
                                } else {
                                }
                            }
                            i3 = 0;
                            message3.ttl = Math.max(encryptedChat2.ttl, i3 + 1);
                        }
                    }
                    arrayList2 = null;
                } else if (i2 == 1) {
                    message3.to_id = MessagesController.getPeer(i);
                    if (i > 0) {
                        user2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
                        if (user2 != null) {
                            if (user2.bot) {
                                message3.unread = false;
                            }
                            arrayList2 = null;
                        } else {
                            processSentMessage(message3.id);
                            return;
                        }
                    }
                    arrayList2 = null;
                } else if (this.currentChatInfo != null) {
                    arrayList3 = new ArrayList();
                    it = this.currentChatInfo.participants.participants.iterator();
                    while (it.hasNext()) {
                        inputUser = MessagesController.getInputUser(MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) it.next()).user_id)));
                        if (inputUser != null) {
                            arrayList3.add(inputUser);
                        }
                    }
                    message3.to_id = new TL_peerChat();
                    message3.to_id.chat_id = i;
                    arrayList2 = arrayList3;
                } else {
                    MessagesStorage.getInstance().markMessageAsSendError(message3);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message3.id));
                    processSentMessage(message3.id);
                    return;
                }
                message3.media_unread = true;
                message3.send_state = 1;
                messageObject3 = new MessageObject(message3, null, true);
                messageObject3.replyMessageObject = messageObject;
                messageObject3.attachPathExists = true;
                arrayList4 = new ArrayList();
                arrayList4.add(messageObject3);
                arrayList5 = new ArrayList();
                arrayList5.add(message3);
                MessagesStorage.getInstance().putMessages(arrayList5, false, true, false, 0);
                MessagesController.getInstance().updateInterfaceWithMessages(j, arrayList4);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                FileLog.m16e("tmessages", "send message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
                if (i4 == 0) {
                }
                if (encryptedChat2 == null) {
                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46) {
                        tL_decryptedMessage = new TL_decryptedMessage();
                        tL_decryptedMessage.ttl = message3.ttl;
                        tL_decryptedMessage.entities = arrayList;
                        tL_decryptedMessage.flags |= TLRPC.USER_FLAG_UNUSED;
                        tL_decryptedMessage.reply_to_random_id = messageObject.messageOwner.random_id;
                        tL_decryptedMessage.flags |= 8;
                    } else if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) < 17) {
                        tL_decryptedMessage = new TL_decryptedMessage_layer8();
                        tL_decryptedMessage.random_bytes = new byte[15];
                        Utilities.random.nextBytes(tL_decryptedMessage.random_bytes);
                    } else {
                        tL_decryptedMessage = new TL_decryptedMessage_layer17();
                        tL_decryptedMessage.ttl = message3.ttl;
                    }
                    if (hashMap != null) {
                        if (hashMap.get("bot_name") != null) {
                            tL_decryptedMessage.via_bot_name = (String) hashMap.get("bot_name");
                            tL_decryptedMessage.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                        }
                    }
                    tL_decryptedMessage.random_id = message3.random_id;
                    tL_decryptedMessage.message = str;
                    if (webPage != null) {
                    }
                    tL_decryptedMessage.media = new TL_decryptedMessageMediaEmpty();
                    SecretChatHelper.getInstance().performSendEncryptedRequest(tL_decryptedMessage, messageObject3.messageOwner, encryptedChat2, null, null, messageObject3);
                    if (messageObject2 == null) {
                        DraftQuery.cleanDraft(j, false);
                    }
                } else if (arrayList2 == null) {
                    tL_messages_sendInlineBotResult = new TL_messages_sendMessage();
                    tL_messages_sendInlineBotResult.message = str;
                    if (messageObject2 != null) {
                    }
                    tL_messages_sendInlineBotResult.clear_draft = messageObject2 != null;
                    if (message3.to_id instanceof TL_peerChannel) {
                        tL_messages_sendInlineBotResult.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + j, false);
                    }
                    tL_messages_sendInlineBotResult.peer = inputPeer;
                    tL_messages_sendInlineBotResult.random_id = message3.random_id;
                    if (messageObject != null) {
                        tL_messages_sendInlineBotResult.flags |= 1;
                        tL_messages_sendInlineBotResult.reply_to_msg_id = messageObject.getId();
                    }
                    if (z) {
                        tL_messages_sendInlineBotResult.no_webpage = true;
                    }
                    tL_messages_sendInlineBotResult.entities = arrayList;
                    tL_messages_sendInlineBotResult.flags |= 8;
                    performSendMessageRequest(tL_messages_sendInlineBotResult, messageObject3, null);
                    if (messageObject2 == null) {
                        DraftQuery.cleanDraft(j, false);
                    }
                } else {
                    tL_messages_sendInlineBotResult = new TL_messages_sendBroadcast();
                    arrayList3 = new ArrayList();
                    for (i3 = 0; i3 < arrayList2.size(); i3++) {
                        arrayList3.add(Long.valueOf(Utilities.random.nextLong()));
                    }
                    tL_messages_sendInlineBotResult.message = str;
                    tL_messages_sendInlineBotResult.contacts = arrayList2;
                    tL_messages_sendInlineBotResult.media = new TL_inputMediaEmpty();
                    tL_messages_sendInlineBotResult.random_id = arrayList3;
                    performSendMessageRequest(tL_messages_sendInlineBotResult, messageObject3, null);
                }
            } catch (Exception e7) {
                e = e7;
                messageObject3 = null;
                message = message3;
                FileLog.m18e("tmessages", e);
                MessagesStorage.getInstance().markMessageAsSendError(message);
                if (messageObject3 != null) {
                    messageObject3.messageOwner.send_state = 2;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                processSentMessage(message.id);
            }
        }
    }

    private void updateMediaPaths(MessageObject messageObject, Message message, String str, boolean z) {
        Message message2 = messageObject.messageOwner;
        if (message != null) {
            int i;
            PhotoSize photoSize;
            PhotoSize photoSize2;
            if ((message.media instanceof TL_messageMediaPhoto) && message.media.photo != null && (message2.media instanceof TL_messageMediaPhoto) && message2.media.photo != null) {
                MessagesStorage.getInstance().putSentFile(str, message.media.photo, 0);
                if (message2.media.photo.sizes.size() == 1 && (((PhotoSize) message2.media.photo.sizes.get(0)).location instanceof TL_fileLocationUnavailable)) {
                    message2.media.photo.sizes = message.media.photo.sizes;
                } else {
                    for (i = 0; i < message.media.photo.sizes.size(); i++) {
                        photoSize = (PhotoSize) message.media.photo.sizes.get(i);
                        if (!(photoSize == null || photoSize.location == null || (photoSize instanceof TL_photoSizeEmpty) || photoSize.type == null)) {
                            int i2 = 0;
                            while (i2 < message2.media.photo.sizes.size()) {
                                photoSize2 = (PhotoSize) message2.media.photo.sizes.get(i2);
                                if (photoSize2 == null || photoSize2.location == null || photoSize2.type == null || !((photoSize2.location.volume_id == -2147483648L && photoSize.type.equals(photoSize2.type)) || (photoSize.f2664w == photoSize2.f2664w && photoSize.f2663h == photoSize2.f2663h))) {
                                    i2++;
                                } else {
                                    String str2 = photoSize2.location.volume_id + "_" + photoSize2.location.local_id;
                                    String str3 = photoSize.location.volume_id + "_" + photoSize.location.local_id;
                                    if (!str2.equals(str3)) {
                                        File file = new File(FileLoader.getInstance().getDirectory(4), str2 + ".jpg");
                                        File pathToAttach = (message.media.photo.sizes.size() == 1 || photoSize.f2664w > 90 || photoSize.f2663h > 90) ? FileLoader.getPathToAttach(photoSize) : new File(FileLoader.getInstance().getDirectory(4), str3 + ".jpg");
                                        FileLoader.renameTo(file, pathToAttach);
                                        ImageLoader.getInstance().replaceImageInCache(str2, str3, photoSize.location, z);
                                        photoSize2.location = photoSize.location;
                                        photoSize2.size = photoSize.size;
                                    }
                                }
                            }
                        }
                    }
                }
                message.message = message2.message;
                message.attachPath = message2.attachPath;
                message2.media.photo.id = message.media.photo.id;
                message2.media.photo.access_hash = message.media.photo.access_hash;
            } else if ((message.media instanceof TL_messageMediaDocument) && message.media.document != null && (message2.media instanceof TL_messageMediaDocument) && message2.media.document != null) {
                DocumentAttribute documentAttribute;
                byte[] bArr;
                if (MessageObject.isVideoMessage(message)) {
                    MessagesStorage.getInstance().putSentFile(str, message.media.document, 2);
                    message.attachPath = message2.attachPath;
                } else if (!MessageObject.isVoiceMessage(message)) {
                    MessagesStorage.getInstance().putSentFile(str, message.media.document, 1);
                }
                photoSize = message2.media.document.thumb;
                photoSize2 = message.media.document.thumb;
                if (photoSize != null && photoSize.location != null && photoSize.location.volume_id == -2147483648L && photoSize2 != null && photoSize2.location != null && !(photoSize2 instanceof TL_photoSizeEmpty) && !(photoSize instanceof TL_photoSizeEmpty)) {
                    String str4 = photoSize.location.volume_id + "_" + photoSize.location.local_id;
                    String str5 = photoSize2.location.volume_id + "_" + photoSize2.location.local_id;
                    if (!str4.equals(str5)) {
                        FileLoader.renameTo(new File(FileLoader.getInstance().getDirectory(4), str4 + ".jpg"), new File(FileLoader.getInstance().getDirectory(4), str5 + ".jpg"));
                        ImageLoader.getInstance().replaceImageInCache(str4, str5, photoSize2.location, z);
                        photoSize.location = photoSize2.location;
                        photoSize.size = photoSize2.size;
                    }
                } else if (photoSize != null && MessageObject.isStickerMessage(message) && photoSize.location != null) {
                    photoSize2.location = photoSize.location;
                } else if ((photoSize != null && (photoSize.location instanceof TL_fileLocationUnavailable)) || (photoSize instanceof TL_photoSizeEmpty)) {
                    message2.media.document.thumb = message.media.document.thumb;
                }
                message2.media.document.dc_id = message.media.document.dc_id;
                message2.media.document.id = message.media.document.id;
                message2.media.document.access_hash = message.media.document.access_hash;
                for (int i3 = 0; i3 < message2.media.document.attributes.size(); i3++) {
                    documentAttribute = (DocumentAttribute) message2.media.document.attributes.get(i3);
                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                        bArr = documentAttribute.waveform;
                        break;
                    }
                }
                bArr = null;
                message2.media.document.attributes = message.media.document.attributes;
                if (bArr != null) {
                    for (i = 0; i < message2.media.document.attributes.size(); i++) {
                        documentAttribute = (DocumentAttribute) message2.media.document.attributes.get(i);
                        if (documentAttribute instanceof TL_documentAttributeAudio) {
                            documentAttribute.waveform = bArr;
                            documentAttribute.flags |= 4;
                        }
                    }
                }
                message2.media.document.size = message.media.document.size;
                message2.media.document.mime_type = message.media.document.mime_type;
                if ((message.flags & 4) == 0 && MessageObject.isOut(message)) {
                    if (MessageObject.isNewGifDocument(message.media.document)) {
                        StickersQuery.addRecentGif(message.media.document, message.date);
                    } else if (MessageObject.isStickerDocument(message.media.document)) {
                        StickersQuery.addRecentSticker(0, message.media.document, message.date);
                    }
                }
                if (message2.attachPath == null || !message2.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath())) {
                    message.attachPath = message2.attachPath;
                    message.message = message2.message;
                    return;
                }
                File file2 = new File(message2.attachPath);
                File pathToAttach2 = FileLoader.getPathToAttach(message.media.document);
                if (!FileLoader.renameTo(file2, pathToAttach2)) {
                    message.attachPath = message2.attachPath;
                    message.message = message2.message;
                } else if (MessageObject.isVideoMessage(message)) {
                    messageObject.attachPathExists = true;
                } else {
                    messageObject.mediaExists = messageObject.attachPathExists;
                    messageObject.attachPathExists = false;
                    message2.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                    if (str != null && str.startsWith("http")) {
                        MessagesStorage.getInstance().addRecentLocalFile(str, pathToAttach2.toString(), message2.media.document);
                    }
                }
            } else if ((message.media instanceof TL_messageMediaContact) && (message2.media instanceof TL_messageMediaContact)) {
                message2.media = message.media;
            } else if (message.media instanceof TL_messageMediaWebPage) {
                message2.media = message.media;
            } else if (message.media instanceof TL_messageMediaGame) {
                message2.media = message.media;
                if ((message2.media instanceof TL_messageMediaGame) && !TextUtils.isEmpty(message.message)) {
                    message2.entities = message.entities;
                    message2.message = message.message;
                }
            }
        }
    }

    public void cancelSendingMessage(MessageObject messageObject) {
        boolean z = false;
        String str = null;
        for (Entry entry : this.delayedMessages.entrySet()) {
            String str2;
            boolean z2;
            ArrayList arrayList = (ArrayList) entry.getValue();
            int i = 0;
            while (i < arrayList.size()) {
                DelayedMessage delayedMessage = (DelayedMessage) arrayList.get(i);
                if (delayedMessage.obj.getId() == messageObject.getId()) {
                    arrayList.remove(i);
                    MediaController.m71a().m179g(delayedMessage.obj);
                    if (arrayList.size() == 0) {
                        String str3 = (String) entry.getKey();
                        if (delayedMessage.sendEncryptedRequest != null) {
                            str2 = str3;
                            z2 = true;
                        } else {
                            str2 = str3;
                            z2 = z;
                        }
                        z = z2;
                        str = str2;
                    }
                    z2 = z;
                    str2 = str;
                    z = z2;
                    str = str2;
                } else {
                    i++;
                }
            }
            z2 = z;
            str2 = str;
            z = z2;
            str = str2;
        }
        if (str != null) {
            if (str.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(str);
            } else {
                FileLoader.getInstance().cancelUploadFile(str, z);
            }
            stopVideoService(str);
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Integer.valueOf(messageObject.getId()));
        MessagesController.getInstance().deleteMessages(arrayList2, null, null, messageObject.messageOwner.to_id.channel_id);
    }

    public void checkUnsentMessages() {
        MessagesStorage.getInstance().getUnsentMessages(PointerIconCompat.TYPE_DEFAULT);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
        this.currentChatInfo = null;
        this.locationProvider.stop();
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.FileDidUpload) {
            String str = (String) objArr[0];
            InputFile inputFile = (InputFile) objArr[1];
            InputEncryptedFile inputEncryptedFile = (InputEncryptedFile) objArr[2];
            ArrayList arrayList = (ArrayList) this.delayedMessages.get(str);
            if (arrayList != null) {
                int i2 = 0;
                while (i2 < arrayList.size()) {
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList.get(i2);
                    InputMedia inputMedia = null;
                    if (delayedMessage.sendRequest instanceof TL_messages_sendMedia) {
                        inputMedia = ((TL_messages_sendMedia) delayedMessage.sendRequest).media;
                    } else if (delayedMessage.sendRequest instanceof TL_messages_sendBroadcast) {
                        inputMedia = ((TL_messages_sendBroadcast) delayedMessage.sendRequest).media;
                    }
                    if (inputFile != null && inputMedia != null) {
                        if (delayedMessage.type == 0) {
                            inputMedia.file = inputFile;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath);
                        } else if (delayedMessage.type == 1) {
                            if (inputMedia.file == null) {
                                inputMedia.file = inputFile;
                                if (inputMedia.thumb != null || delayedMessage.location == null) {
                                    performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath);
                                } else {
                                    performSendDelayedMessage(delayedMessage);
                                }
                            } else {
                                inputMedia.thumb = inputFile;
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath);
                            }
                        } else if (delayedMessage.type == 2) {
                            if (inputMedia.file == null) {
                                inputMedia.file = inputFile;
                                if (inputMedia.thumb != null || delayedMessage.location == null) {
                                    performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath);
                                } else {
                                    performSendDelayedMessage(delayedMessage);
                                }
                            } else {
                                inputMedia.thumb = inputFile;
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath);
                            }
                        } else if (delayedMessage.type == 3) {
                            inputMedia.file = inputFile;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath);
                        }
                        arrayList.remove(i2);
                        i2--;
                    } else if (!(inputEncryptedFile == null || delayedMessage.sendEncryptedRequest == null)) {
                        if ((delayedMessage.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaVideo) || (delayedMessage.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaPhoto) || (delayedMessage.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaDocument)) {
                            delayedMessage.sendEncryptedRequest.media.size = (int) ((Long) objArr[5]).longValue();
                        }
                        delayedMessage.sendEncryptedRequest.media.key = (byte[]) objArr[3];
                        delayedMessage.sendEncryptedRequest.media.iv = (byte[]) objArr[4];
                        SecretChatHelper.getInstance().performSendEncryptedRequest(delayedMessage.sendEncryptedRequest, delayedMessage.obj.messageOwner, delayedMessage.encryptedChat, inputEncryptedFile, delayedMessage.originalPath, delayedMessage.obj);
                        arrayList.remove(i2);
                        i2--;
                    }
                    i2++;
                }
                if (arrayList.isEmpty()) {
                    this.delayedMessages.remove(str);
                }
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            r0 = (String) objArr[0];
            boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
            r1 = (ArrayList) this.delayedMessages.get(r0);
            if (r1 != null) {
                r3 = 0;
                while (r3 < r1.size()) {
                    r2 = (DelayedMessage) r1.get(r3);
                    if ((booleanValue && r2.sendEncryptedRequest != null) || !(booleanValue || r2.sendRequest == null)) {
                        MessagesStorage.getInstance().markMessageAsSendError(r2.obj.messageOwner);
                        r2.obj.messageOwner.send_state = 2;
                        r1.remove(r3);
                        r3--;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(r2.obj.getId()));
                        processSentMessage(r2.obj.getId());
                    }
                    r3++;
                }
                if (r1.isEmpty()) {
                    this.delayedMessages.remove(r0);
                }
            }
        } else if (i == NotificationCenter.FilePreparingStarted) {
            r0 = (MessageObject) objArr[0];
            r1 = (String) objArr[1];
            r1 = (ArrayList) this.delayedMessages.get(r0.messageOwner.attachPath);
            if (r1 != null) {
                for (r3 = 0; r3 < r1.size(); r3++) {
                    r2 = (DelayedMessage) r1.get(r3);
                    if (r2.obj == r0) {
                        r2.videoEditedInfo = null;
                        performSendDelayedMessage(r2);
                        r1.remove(r3);
                        break;
                    }
                }
                if (r1.isEmpty()) {
                    this.delayedMessages.remove(r0.messageOwner.attachPath);
                }
            }
        } else if (i == NotificationCenter.FileNewChunkAvailable) {
            MessageObject messageObject = (MessageObject) objArr[0];
            r0 = (String) objArr[1];
            long longValue = ((Long) objArr[2]).longValue();
            FileLoader.getInstance().checkUploadNewDataAvailable(r0, ((int) messageObject.getDialogId()) == 0, longValue);
            if (longValue != 0) {
                ArrayList arrayList2 = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                if (arrayList2 != null) {
                    for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                        DelayedMessage delayedMessage2 = (DelayedMessage) arrayList2.get(i3);
                        if (delayedMessage2.obj == messageObject) {
                            delayedMessage2.obj.videoEditedInfo = null;
                            delayedMessage2.obj.messageOwner.message = "-1";
                            delayedMessage2.obj.messageOwner.media.document.size = (int) longValue;
                            r1 = new ArrayList();
                            r1.add(delayedMessage2.obj.messageOwner);
                            MessagesStorage.getInstance().putMessages(r1, false, true, false, 0);
                            break;
                        }
                    }
                    if (arrayList2.isEmpty()) {
                        this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                    }
                }
            }
        } else if (i == NotificationCenter.FilePreparingFailed) {
            r0 = (MessageObject) objArr[0];
            r1 = (String) objArr[1];
            stopVideoService(r0.messageOwner.attachPath);
            ArrayList arrayList3 = (ArrayList) this.delayedMessages.get(r1);
            if (arrayList3 != null) {
                int i4 = 0;
                while (i4 < arrayList3.size()) {
                    DelayedMessage delayedMessage3 = (DelayedMessage) arrayList3.get(i4);
                    if (delayedMessage3.obj == r0) {
                        MessagesStorage.getInstance().markMessageAsSendError(delayedMessage3.obj.messageOwner);
                        delayedMessage3.obj.messageOwner.send_state = 2;
                        arrayList3.remove(i4);
                        i4--;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(delayedMessage3.obj.getId()));
                        processSentMessage(delayedMessage3.obj.getId());
                    }
                    i4++;
                }
                if (arrayList3.isEmpty()) {
                    this.delayedMessages.remove(r1);
                }
            }
        } else if (i == NotificationCenter.httpFileDidLoaded) {
            r0 = (String) objArr[0];
            r1 = (ArrayList) this.delayedMessages.get(r0);
            if (r1 != null) {
                for (r3 = 0; r3 < r1.size(); r3++) {
                    r2 = (DelayedMessage) r1.get(r3);
                    if (r2.type == 0) {
                        Utilities.globalQueue.postRunnable(new C06582(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(r2.httpLocation) + "." + ImageLoader.getHttpUrlExtension(r2.httpLocation, "file")), r2));
                    } else if (r2.type == 2) {
                        Utilities.globalQueue.postRunnable(new C06603(r2, new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(r2.httpLocation) + ".gif")));
                    }
                }
                this.delayedMessages.remove(r0);
            }
        } else if (i == NotificationCenter.FileDidLoaded) {
            r0 = (String) objArr[0];
            r1 = (ArrayList) this.delayedMessages.get(r0);
            if (r1 != null) {
                for (r3 = 0; r3 < r1.size(); r3++) {
                    performSendDelayedMessage((DelayedMessage) r1.get(r3));
                }
                this.delayedMessages.remove(r0);
            }
        } else if (i == NotificationCenter.httpFileDidFailedLoad || i == NotificationCenter.FileDidFailedLoad) {
            r0 = (String) objArr[0];
            r1 = (ArrayList) this.delayedMessages.get(r0);
            if (r1 != null) {
                Iterator it = r1.iterator();
                while (it.hasNext()) {
                    DelayedMessage delayedMessage4 = (DelayedMessage) it.next();
                    MessagesStorage.getInstance().markMessageAsSendError(delayedMessage4.obj.messageOwner);
                    delayedMessage4.obj.messageOwner.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(delayedMessage4.obj.getId()));
                    processSentMessage(delayedMessage4.obj.getId());
                }
                this.delayedMessages.remove(r0);
            }
        }
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<MessageEntity> arrayList, Runnable runnable) {
        boolean z2 = false;
        if (baseFragment == null || baseFragment.getParentActivity() == null || runnable == null) {
            return 0;
        }
        TLObject tL_messages_editMessage = new TL_messages_editMessage();
        tL_messages_editMessage.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
        tL_messages_editMessage.message = str;
        tL_messages_editMessage.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
        tL_messages_editMessage.id = messageObject.getId();
        if (!z) {
            z2 = true;
        }
        tL_messages_editMessage.no_webpage = z2;
        if (arrayList != null) {
            tL_messages_editMessage.entities = arrayList;
            tL_messages_editMessage.flags |= 8;
        }
        return ConnectionsManager.getInstance().sendRequest(tL_messages_editMessage, new C06685(runnable, baseFragment));
    }

    public TL_photo generatePhotoSizes(String str, Uri uri) {
        Bitmap loadBitmap = ImageLoader.loadBitmap(str, uri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (loadBitmap == null && AndroidUtilities.getPhotoSize() != 800) {
            loadBitmap = ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true);
        }
        ArrayList arrayList = new ArrayList();
        PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, true);
        if (scaleAndSaveImage != null) {
            arrayList.add(scaleAndSaveImage);
        }
        scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (scaleAndSaveImage != null) {
            arrayList.add(scaleAndSaveImage);
        }
        if (loadBitmap != null) {
            loadBitmap.recycle();
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        UserConfig.saveConfig(false);
        TL_photo tL_photo = new TL_photo();
        tL_photo.date = ConnectionsManager.getInstance().getCurrentTime();
        tL_photo.sizes = arrayList;
        return tL_photo;
    }

    protected ArrayList<DelayedMessage> getDelayedMessages(String str) {
        return (ArrayList) this.delayedMessages.get(str);
    }

    protected long getNextRandomId() {
        long j = 0;
        while (j == 0) {
            j = Utilities.random.nextLong();
        }
        return j;
    }

    public boolean isSendingCallback(MessageObject messageObject, KeyboardButton keyboardButton) {
        return (messageObject == null || keyboardButton == null || !this.waitingForCallback.containsKey(messageObject.getId() + "_" + Utilities.bytesToHex(keyboardButton.data))) ? false : true;
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, KeyboardButton keyboardButton) {
        return (messageObject == null || keyboardButton == null || !this.waitingForLocation.containsKey(messageObject.getId() + "_" + Utilities.bytesToHex(keyboardButton.data))) ? false : true;
    }

    public boolean isSendingMessage(int i) {
        return this.sendingMessages.containsKey(Integer.valueOf(i));
    }

    public void processForwardFromMyName(MessageObject messageObject, long j) {
        processForwardFromMyName(messageObject, j, !MoboConstants.f1343j);
    }

    public void processForwardFromMyName(MessageObject messageObject, long j, boolean z) {
        if (messageObject != null) {
            ArrayList arrayList;
            if (messageObject.messageOwner.media == null || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || (messageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                if (messageObject.messageOwner.message != null) {
                    WebPage webPage = null;
                    if (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                        webPage = messageObject.messageOwner.media.webpage;
                    }
                    sendMessage(messageObject.messageOwner.message, j, messageObject.replyMessageObject, webPage, true, messageObject.messageOwner.entities, null, null);
                    return;
                }
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, j);
            } else if (messageObject.messageOwner.media.photo instanceof TL_photo) {
                TL_photo tL_photo = (TL_photo) messageObject.messageOwner.media.photo;
                if (z) {
                    tL_photo.caption = messageObject.messageOwner.media.caption;
                }
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, j, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                TL_document tL_document = (TL_document) messageObject.messageOwner.media.document;
                if (z) {
                    tL_document.caption = messageObject.messageOwner.media.caption;
                }
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, j, messageObject.replyMessageObject, null, null);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaVenue) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                sendMessage(messageObject.messageOwner.media, j, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.phone_number != null) {
                User tL_userContact_old2 = new TL_userContact_old2();
                tL_userContact_old2.phone = messageObject.messageOwner.media.phone_number;
                tL_userContact_old2.first_name = messageObject.messageOwner.media.first_name;
                tL_userContact_old2.last_name = messageObject.messageOwner.media.last_name;
                tL_userContact_old2.id = messageObject.messageOwner.media.user_id;
                sendMessage(tL_userContact_old2, j, messageObject.replyMessageObject, null, null);
            } else {
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, j);
            }
        }
    }

    public void processProForward(MessageObject messageObject, long j, boolean z) {
        if (messageObject != null) {
            ArrayList arrayList;
            if (messageObject.messageOwner.media == null || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || (messageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                if (messageObject.messageOwner.message != null) {
                    WebPage webPage = null;
                    if (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                        webPage = messageObject.messageOwner.media.webpage;
                    }
                    sendMessage(messageObject.messageOwner.message, j, messageObject.replyMessageObject, webPage, true, messageObject.messageOwner.entities, null, null);
                    return;
                }
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, j);
            } else if (messageObject.messageOwner.media.photo instanceof TL_photo) {
                TL_photo tL_photo = (TL_photo) messageObject.messageOwner.media.photo;
                if (z) {
                    tL_photo.caption = messageObject.messageOwner.media.caption;
                }
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, j, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                TL_document tL_document = (TL_document) messageObject.messageOwner.media.document;
                if (z) {
                    tL_document.caption = messageObject.messageOwner.media.caption;
                }
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, j, messageObject.replyMessageObject, null, null);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaVenue) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                sendMessage(messageObject.messageOwner.media, j, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.phone_number != null) {
                User tL_userContact_old2 = new TL_userContact_old2();
                tL_userContact_old2.phone = messageObject.messageOwner.media.phone_number;
                tL_userContact_old2.first_name = messageObject.messageOwner.media.first_name;
                tL_userContact_old2.last_name = messageObject.messageOwner.media.last_name;
                tL_userContact_old2.id = messageObject.messageOwner.media.user_id;
                sendMessage(tL_userContact_old2, j, messageObject.replyMessageObject, null, null);
            } else {
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, j);
            }
        }
    }

    protected void processSentMessage(int i) {
        int size = this.unsentMessages.size();
        this.unsentMessages.remove(Integer.valueOf(i));
        if (size != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    protected void processUnsentMessages(ArrayList<Message> arrayList, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, ArrayList<EncryptedChat> arrayList4) {
        AndroidUtilities.runOnUIThread(new AnonymousClass11(arrayList2, arrayList3, arrayList4, arrayList));
    }

    protected void putToSendingMessages(Message message) {
        this.sendingMessages.put(Integer.valueOf(message.id), message);
    }

    protected void removeFromSendingMessages(int i) {
        this.sendingMessages.remove(Integer.valueOf(i));
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean z) {
        if (messageObject.getId() >= 0) {
            return false;
        }
        if (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) {
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                MessagesStorage.getInstance().markMessageAsSendError(messageObject.messageOwner);
                messageObject.messageOwner.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            if (messageObject.messageOwner.random_id == 0) {
                messageObject.messageOwner.random_id = getNextRandomId();
            }
            if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                SecretChatHelper.getInstance().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionDeleteMessages) {
                SecretChatHelper.getInstance().sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionFlushHistory) {
                SecretChatHelper.getInstance().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNotifyLayer) {
                SecretChatHelper.getInstance().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionReadMessages) {
                SecretChatHelper.getInstance().sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                SecretChatHelper.getInstance().sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!((messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionTyping) || (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionResend))) {
                if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionCommitKey) {
                    SecretChatHelper.getInstance().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAbortKey) {
                    SecretChatHelper.getInstance().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionRequestKey) {
                    SecretChatHelper.getInstance().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAcceptKey) {
                    SecretChatHelper.getInstance().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNoop) {
                    SecretChatHelper.getInstance().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (z) {
            this.unsentMessages.put(Integer.valueOf(messageObject.getId()), messageObject);
        }
        sendMessage(messageObject);
        return true;
    }

    public void sendCallback(MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity) {
        if (messageObject != null && keyboardButton != null && chatActivity != null) {
            String str = messageObject.getId() + "_" + Utilities.bytesToHex(keyboardButton.data);
            this.waitingForCallback.put(str, messageObject);
            TLObject tL_messages_getBotCallbackAnswer = new TL_messages_getBotCallbackAnswer();
            tL_messages_getBotCallbackAnswer.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
            tL_messages_getBotCallbackAnswer.msg_id = messageObject.getId();
            tL_messages_getBotCallbackAnswer.game = keyboardButton instanceof TL_keyboardButtonGame;
            if (keyboardButton.data != null) {
                tL_messages_getBotCallbackAnswer.flags |= 1;
                tL_messages_getBotCallbackAnswer.data = keyboardButton.data;
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_getBotCallbackAnswer, new C06706(str, chatActivity, messageObject, keyboardButton), 2);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, KeyboardButton keyboardButton) {
        this.waitingForLocation.put(messageObject.getId() + "_" + Utilities.bytesToHex(keyboardButton.data), messageObject);
        this.locationProvider.start();
    }

    public void sendGame(InputPeer inputPeer, TL_inputMediaGame tL_inputMediaGame, long j, long j2) {
        NativeByteBuffer nativeByteBuffer;
        Throwable e;
        if (inputPeer != null && tL_inputMediaGame != null) {
            TLObject tL_messages_sendMedia = new TL_messages_sendMedia();
            tL_messages_sendMedia.peer = inputPeer;
            if (tL_messages_sendMedia.peer instanceof TL_inputPeerChannel) {
                tL_messages_sendMedia.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + inputPeer.channel_id, false);
            }
            tL_messages_sendMedia.random_id = j != 0 ? j : getNextRandomId();
            tL_messages_sendMedia.media = tL_inputMediaGame;
            if (j2 == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(((inputPeer.getObjectSize() + tL_inputMediaGame.getObjectSize()) + 4) + 8);
                    try {
                        nativeByteBuffer.writeInt32(3);
                        nativeByteBuffer.writeInt64(j);
                        inputPeer.serializeToStream(nativeByteBuffer);
                        tL_inputMediaGame.serializeToStream(nativeByteBuffer);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.m18e("tmessages", e);
                        j2 = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance().sendRequest(tL_messages_sendMedia, new C06717(j2));
                    }
                } catch (Throwable e3) {
                    Throwable th = e3;
                    nativeByteBuffer = null;
                    e = th;
                    FileLog.m18e("tmessages", e);
                    j2 = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                    ConnectionsManager.getInstance().sendRequest(tL_messages_sendMedia, new C06717(j2));
                }
                j2 = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_sendMedia, new C06717(j2));
        }
    }

    public void sendMessage(MessageObject messageObject) {
        MessageObject messageObject2 = messageObject;
        sendMessage(null, null, null, null, null, null, null, messageObject.getDialogId(), messageObject.messageOwner.attachPath, null, null, true, messageObject2, null, messageObject.messageOwner.reply_markup, messageObject.messageOwner.params);
    }

    public void sendMessage(MessageMedia messageMedia, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, messageMedia, null, null, null, null, null, j, null, messageObject, null, true, null, null, replyMarkup, hashMap);
    }

    public void sendMessage(TL_document tL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, null, videoEditedInfo, null, tL_document, null, j, str, messageObject, null, true, null, null, replyMarkup, hashMap);
    }

    public void sendMessage(TL_game tL_game, long j, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, null, null, null, null, tL_game, j, null, null, null, true, null, null, replyMarkup, hashMap);
    }

    public void sendMessage(TL_photo tL_photo, String str, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, tL_photo, null, null, null, null, j, str, messageObject, null, true, null, null, replyMarkup, hashMap);
    }

    public void sendMessage(User user, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, null, null, user, null, null, j, null, messageObject, null, true, null, null, replyMarkup, hashMap);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, WebPage webPage, boolean z, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(str, null, null, null, null, null, null, j, null, messageObject, webPage, z, null, arrayList, replyMarkup, hashMap);
    }

    public void sendMessage(ArrayList<MessageObject> arrayList, long j) {
        sendMessage(arrayList, j, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendMessage(java.util.ArrayList<com.hanista.mobogram.messenger.MessageObject> r23, long r24, java.lang.Long r26) {
        /*
        r22 = this;
        r0 = r24;
        r2 = (int) r0;
        if (r2 == 0) goto L_0x000d;
    L_0x0005:
        if (r23 == 0) goto L_0x000d;
    L_0x0007:
        r2 = r23.isEmpty();
        if (r2 == 0) goto L_0x000e;
    L_0x000d:
        return;
    L_0x000e:
        r0 = r24;
        r6 = (int) r0;
        r0 = r24;
        r2 = (int) r0;
        r10 = com.hanista.mobogram.messenger.MessagesController.getPeer(r2);
        r3 = 0;
        r2 = 0;
        if (r6 <= 0) goto L_0x0073;
    L_0x001c:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r5 = java.lang.Integer.valueOf(r6);
        r4 = r4.getUser(r5);
        if (r4 == 0) goto L_0x000d;
    L_0x002a:
        r12 = r2;
        r13 = r3;
    L_0x002c:
        r9 = new java.util.ArrayList;
        r9.<init>();
        r8 = new java.util.ArrayList;
        r8.<init>();
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r3 = new java.util.HashMap;
        r3.<init>();
        r18 = com.hanista.mobogram.messenger.MessagesController.getInputPeer(r6);
        r2 = 0;
        r14 = r2;
        r15 = r3;
        r16 = r4;
        r17 = r5;
    L_0x0050:
        r2 = r23.size();
        if (r14 >= r2) goto L_0x000d;
    L_0x0056:
        r0 = r23;
        r2 = r0.get(r14);
        r11 = r2;
        r11 = (com.hanista.mobogram.messenger.MessageObject) r11;
        r2 = r11.getId();
        if (r2 > 0) goto L_0x008d;
    L_0x0065:
        r3 = r15;
        r4 = r16;
        r5 = r17;
    L_0x006a:
        r2 = r14 + 1;
        r14 = r2;
        r15 = r3;
        r16 = r4;
        r17 = r5;
        goto L_0x0050;
    L_0x0073:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r5 = -r6;
        r5 = java.lang.Integer.valueOf(r5);
        r4 = r4.getChat(r5);
        r5 = com.hanista.mobogram.messenger.ChatObject.isChannel(r4);
        if (r5 == 0) goto L_0x03ca;
    L_0x0086:
        r3 = r4.megagroup;
        r2 = r4.signatures;
        r12 = r2;
        r13 = r3;
        goto L_0x002c;
    L_0x008d:
        r3 = new com.hanista.mobogram.tgnet.TLRPC$TL_message;
        r3.<init>();
        r2 = r11.isForwarded();
        if (r2 == 0) goto L_0x0320;
    L_0x0098:
        r2 = r11.messageOwner;
        r2 = r2.fwd_from;
        r3.fwd_from = r2;
    L_0x009e:
        r2 = r11.messageOwner;
        r2 = r2.media;
        r3.media = r2;
        r2 = 4;
        r3.flags = r2;
        r2 = r3.media;
        if (r2 == 0) goto L_0x00b1;
    L_0x00ab:
        r2 = r3.flags;
        r2 = r2 | 512;
        r3.flags = r2;
    L_0x00b1:
        if (r13 == 0) goto L_0x00ba;
    L_0x00b3:
        r2 = r3.flags;
        r4 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r2 = r2 | r4;
        r3.flags = r2;
    L_0x00ba:
        r2 = r11.messageOwner;
        r2 = r2.via_bot_id;
        if (r2 == 0) goto L_0x00cc;
    L_0x00c0:
        r2 = r11.messageOwner;
        r2 = r2.via_bot_id;
        r3.via_bot_id = r2;
        r2 = r3.flags;
        r2 = r2 | 2048;
        r3.flags = r2;
    L_0x00cc:
        r2 = r11.messageOwner;
        r2 = r2.message;
        r3.message = r2;
        r2 = r11.getId();
        r3.fwd_msg_id = r2;
        r2 = r11.messageOwner;
        r2 = r2.attachPath;
        r3.attachPath = r2;
        r2 = r11.messageOwner;
        r2 = r2.entities;
        r3.entities = r2;
        r2 = r3.entities;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x00f2;
    L_0x00ec:
        r2 = r3.flags;
        r2 = r2 | 128;
        r3.flags = r2;
    L_0x00f2:
        r2 = r3.attachPath;
        if (r2 != 0) goto L_0x00fb;
    L_0x00f6:
        r2 = "";
        r3.attachPath = r2;
    L_0x00fb:
        r2 = com.hanista.mobogram.messenger.UserConfig.getNewMessageId();
        r3.id = r2;
        r3.local_id = r2;
        r2 = 1;
        r3.out = r2;
        r2 = r10.channel_id;
        if (r2 == 0) goto L_0x0389;
    L_0x010a:
        if (r13 != 0) goto L_0x0389;
    L_0x010c:
        if (r12 == 0) goto L_0x0384;
    L_0x010e:
        r2 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();
    L_0x0112:
        r3.from_id = r2;
        r2 = 1;
        r3.post = r2;
    L_0x0117:
        r4 = r3.random_id;
        r6 = 0;
        r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r2 != 0) goto L_0x0125;
    L_0x011f:
        r4 = r22.getNextRandomId();
        r3.random_id = r4;
    L_0x0125:
        r4 = r3.random_id;
        r2 = java.lang.Long.valueOf(r4);
        r0 = r17;
        r0.add(r2);
        r4 = r3.random_id;
        r2 = java.lang.Long.valueOf(r4);
        r15.put(r2, r3);
        r2 = r3.fwd_msg_id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r16;
        r0.add(r2);
        r2 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();
        r2 = r2.getCurrentTime();
        r3.date = r2;
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r2 == 0) goto L_0x039c;
    L_0x0154:
        if (r13 != 0) goto L_0x0397;
    L_0x0156:
        r2 = 1;
        r3.views = r2;
        r2 = r3.flags;
        r2 = r2 | 1024;
        r3.flags = r2;
    L_0x015f:
        r0 = r24;
        r3.dialog_id = r0;
        r3.to_id = r10;
        r2 = com.hanista.mobogram.messenger.MessageObject.isVoiceMessage(r3);
        if (r2 == 0) goto L_0x0174;
    L_0x016b:
        r2 = r3.to_id;
        r2 = r2.channel_id;
        if (r2 != 0) goto L_0x0174;
    L_0x0171:
        r2 = 1;
        r3.media_unread = r2;
    L_0x0174:
        r2 = r11.messageOwner;
        r2 = r2.to_id;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_peerChannel;
        if (r2 == 0) goto L_0x0185;
    L_0x017c:
        r2 = r11.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        r2 = -r2;
        r3.ttl = r2;
    L_0x0185:
        r2 = new com.hanista.mobogram.messenger.MessageObject;
        r4 = 0;
        r5 = 1;
        r2.<init>(r3, r4, r5);
        r4 = r2.messageOwner;
        r5 = 1;
        r4.send_state = r5;
        r9.add(r2);
        r8.add(r3);
        r0 = r22;
        r0.putToSendingMessages(r3);
        r2 = com.hanista.mobogram.messenger.BuildVars.DEBUG_VERSION;
        if (r2 == 0) goto L_0x01eb;
    L_0x01a0:
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "forward message user_id = ";
        r3 = r3.append(r4);
        r0 = r18;
        r4 = r0.user_id;
        r3 = r3.append(r4);
        r4 = " chat_id = ";
        r3 = r3.append(r4);
        r0 = r18;
        r4 = r0.chat_id;
        r3 = r3.append(r4);
        r4 = " channel_id = ";
        r3 = r3.append(r4);
        r0 = r18;
        r4 = r0.channel_id;
        r3 = r3.append(r4);
        r4 = " access_hash = ";
        r3 = r3.append(r4);
        r0 = r18;
        r4 = r0.access_hash;
        r3 = r3.append(r4);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
    L_0x01eb:
        r2 = r8.size();
        r3 = 100;
        if (r2 == r3) goto L_0x0219;
    L_0x01f3:
        r2 = r23.size();
        r2 = r2 + -1;
        if (r14 == r2) goto L_0x0219;
    L_0x01fb:
        r2 = r23.size();
        r2 = r2 + -1;
        if (r14 == r2) goto L_0x03c3;
    L_0x0203:
        r2 = r14 + 1;
        r0 = r23;
        r2 = r0.get(r2);
        r2 = (com.hanista.mobogram.messenger.MessageObject) r2;
        r2 = r2.getDialogId();
        r4 = r11.getDialogId();
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 == 0) goto L_0x03c3;
    L_0x0219:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r3 = new java.util.ArrayList;
        r3.<init>(r8);
        r4 = 0;
        r5 = 1;
        r6 = 0;
        r7 = 0;
        r2.putMessages(r3, r4, r5, r6, r7);
        r2 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r24;
        r2.updateInterfaceWithMessages(r0, r9);
        r2 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r3 = com.hanista.mobogram.messenger.NotificationCenter.dialogsNeedReload;
        r4 = 0;
        r4 = new java.lang.Object[r4];
        r2.postNotificationName(r3, r4);
        r2 = 0;
        com.hanista.mobogram.messenger.UserConfig.saveConfig(r2);
        r19 = new com.hanista.mobogram.tgnet.TLRPC$TL_messages_forwardMessages;
        r19.<init>();
        r0 = r18;
        r1 = r19;
        r1.to_peer = r0;
        r0 = r19;
        r2 = r0.to_peer;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r2 == 0) goto L_0x027e;
    L_0x0255:
        r2 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;
        r3 = "Notifications";
        r4 = 0;
        r2 = r2.getSharedPreferences(r3, r4);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "silent_";
        r3 = r3.append(r4);
        r0 = r24;
        r3 = r3.append(r0);
        r3 = r3.toString();
        r4 = 0;
        r2 = r2.getBoolean(r3, r4);
        r0 = r19;
        r0.silent = r2;
    L_0x027e:
        r2 = r11.messageOwner;
        r2 = r2.to_id;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_peerChannel;
        if (r2 == 0) goto L_0x03b5;
    L_0x0286:
        r2 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r3 = r11.messageOwner;
        r3 = r3.to_id;
        r3 = r3.channel_id;
        r3 = java.lang.Integer.valueOf(r3);
        r2 = r2.getChat(r3);
        r3 = new com.hanista.mobogram.tgnet.TLRPC$TL_inputPeerChannel;
        r3.<init>();
        r0 = r19;
        r0.from_peer = r3;
        r0 = r19;
        r3 = r0.from_peer;
        r4 = r11.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        r3.channel_id = r4;
        if (r2 == 0) goto L_0x02b7;
    L_0x02af:
        r0 = r19;
        r3 = r0.from_peer;
        r4 = r2.access_hash;
        r3.access_hash = r4;
    L_0x02b7:
        r0 = r17;
        r1 = r19;
        r1.random_id = r0;
        r0 = r16;
        r1 = r19;
        r1.id = r0;
        r2 = r23.size();
        r3 = 1;
        if (r2 != r3) goto L_0x03c0;
    L_0x02ca:
        r2 = 0;
        r0 = r23;
        r2 = r0.get(r2);
        r2 = (com.hanista.mobogram.messenger.MessageObject) r2;
        r2 = r2.messageOwner;
        r2 = r2.with_my_score;
        if (r2 == 0) goto L_0x03c0;
    L_0x02d9:
        r2 = 1;
    L_0x02da:
        r0 = r19;
        r0.with_my_score = r2;
        r20 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();
        r2 = new com.hanista.mobogram.messenger.SendMessagesHelper$4;
        r3 = r22;
        r4 = r24;
        r6 = r13;
        r7 = r15;
        r11 = r26;
        r2.<init>(r4, r6, r7, r8, r9, r10, r11);
        r3 = 68;
        r0 = r20;
        r1 = r19;
        r0.sendRequest(r1, r2, r3);
        r2 = r23.size();
        r2 = r2 + -1;
        if (r14 == r2) goto L_0x03c3;
    L_0x0300:
        r9 = new java.util.ArrayList;
        r9.<init>();
        r8 = new java.util.ArrayList;
        r8.<init>();
        r17 = new java.util.ArrayList;
        r17.<init>();
        r16 = new java.util.ArrayList;
        r16.<init>();
        r15 = new java.util.HashMap;
        r15.<init>();
        r3 = r15;
        r4 = r16;
        r5 = r17;
        goto L_0x006a;
    L_0x0320:
        r2 = new com.hanista.mobogram.tgnet.TLRPC$TL_messageFwdHeader;
        r2.<init>();
        r3.fwd_from = r2;
        r2 = r11.isFromUser();
        if (r2 == 0) goto L_0x0345;
    L_0x032d:
        r2 = r3.fwd_from;
        r4 = r11.messageOwner;
        r4 = r4.from_id;
        r2.from_id = r4;
        r2 = r3.fwd_from;
        r4 = r2.flags;
        r4 = r4 | 1;
        r2.flags = r4;
    L_0x033d:
        r2 = r11.messageOwner;
        r2 = r2.date;
        r3.date = r2;
        goto L_0x009e;
    L_0x0345:
        r2 = r3.fwd_from;
        r4 = r11.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        r2.channel_id = r4;
        r2 = r3.fwd_from;
        r4 = r2.flags;
        r4 = r4 | 2;
        r2.flags = r4;
        r2 = r11.messageOwner;
        r2 = r2.post;
        if (r2 == 0) goto L_0x033d;
    L_0x035d:
        r2 = r3.fwd_from;
        r4 = r11.getId();
        r2.channel_post = r4;
        r2 = r3.fwd_from;
        r4 = r2.flags;
        r4 = r4 | 4;
        r2.flags = r4;
        r2 = r11.messageOwner;
        r2 = r2.from_id;
        if (r2 <= 0) goto L_0x033d;
    L_0x0373:
        r2 = r3.fwd_from;
        r4 = r11.messageOwner;
        r4 = r4.from_id;
        r2.from_id = r4;
        r2 = r3.fwd_from;
        r4 = r2.flags;
        r4 = r4 | 1;
        r2.flags = r4;
        goto L_0x033d;
    L_0x0384:
        r2 = r10.channel_id;
        r2 = -r2;
        goto L_0x0112;
    L_0x0389:
        r2 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();
        r3.from_id = r2;
        r2 = r3.flags;
        r2 = r2 | 256;
        r3.flags = r2;
        goto L_0x0117;
    L_0x0397:
        r2 = 1;
        r3.unread = r2;
        goto L_0x015f;
    L_0x039c:
        r2 = r11.messageOwner;
        r2 = r2.flags;
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x03b0;
    L_0x03a4:
        r2 = r11.messageOwner;
        r2 = r2.views;
        r3.views = r2;
        r2 = r3.flags;
        r2 = r2 | 1024;
        r3.flags = r2;
    L_0x03b0:
        r2 = 1;
        r3.unread = r2;
        goto L_0x015f;
    L_0x03b5:
        r2 = new com.hanista.mobogram.tgnet.TLRPC$TL_inputPeerEmpty;
        r2.<init>();
        r0 = r19;
        r0.from_peer = r2;
        goto L_0x02b7;
    L_0x03c0:
        r2 = 0;
        goto L_0x02da;
    L_0x03c3:
        r3 = r15;
        r4 = r16;
        r5 = r17;
        goto L_0x006a;
    L_0x03ca:
        r12 = r2;
        r13 = r3;
        goto L_0x002c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, java.lang.Long):void");
    }

    public void sendSticker(Document document, long j, MessageObject messageObject) {
        if (document != null) {
            TL_document tL_document;
            if (((int) j) == 0) {
                if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (j >> 32))) == null) {
                    return;
                }
                if (document.thumb instanceof TL_photoSize) {
                    File pathToAttach = FileLoader.getPathToAttach(document.thumb, true);
                    if (pathToAttach.exists()) {
                        try {
                            int length = (int) pathToAttach.length();
                            byte[] bArr = new byte[((int) pathToAttach.length())];
                            new RandomAccessFile(pathToAttach, "r").readFully(bArr);
                            tL_document = new TL_document();
                            tL_document.thumb = new TL_photoCachedSize();
                            tL_document.thumb.location = document.thumb.location;
                            tL_document.thumb.size = document.thumb.size;
                            tL_document.thumb.f2664w = document.thumb.f2664w;
                            tL_document.thumb.f2663h = document.thumb.f2663h;
                            tL_document.thumb.type = document.thumb.type;
                            tL_document.thumb.bytes = bArr;
                            tL_document.id = document.id;
                            tL_document.access_hash = document.access_hash;
                            tL_document.date = document.date;
                            tL_document.mime_type = document.mime_type;
                            tL_document.size = document.size;
                            tL_document.dc_id = document.dc_id;
                            tL_document.attributes = document.attributes;
                            if (tL_document.mime_type == null) {
                                tL_document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        getInstance().sendMessage(tL_document, null, null, j, messageObject, null, null);
                    }
                }
            }
            Document document2 = document;
            getInstance().sendMessage(tL_document, null, null, j, messageObject, null, null);
        }
    }

    public void setCurrentChatInfo(ChatFull chatFull) {
        this.currentChatInfo = chatFull;
    }

    protected void stopVideoService(String str) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06738(str));
    }
}
