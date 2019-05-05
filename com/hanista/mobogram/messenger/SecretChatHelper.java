package com.hanista.mobogram.messenger;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.view.InputDeviceCompat;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLClassStore;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.DecryptedMessage;
import com.hanista.mobogram.tgnet.TLRPC.DecryptedMessageAction;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedMessage;
import com.hanista.mobogram.tgnet.TLRPC.InputEncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
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
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageLayer;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaDocument_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaGeoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageService;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageService_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeFilename;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentEncrypted;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatRequested;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatWaiting;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileEncryptedLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import com.hanista.mobogram.tgnet.TLRPC.TL_geoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputEncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageService;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_acceptEncryption;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_dhConfig;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_discardEncryption;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getDhConfig;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_requestEncryption;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_sendEncrypted;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_sendEncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_sendEncryptedService;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_photo;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoCachedSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateEncryption;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPageUrlPending;
import com.hanista.mobogram.tgnet.TLRPC.Update;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.messages_DhConfig;
import com.hanista.mobogram.tgnet.TLRPC.messages_SentEncryptedMessage;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SecretChatHelper {
    public static final int CURRENT_SECRET_CHAT_LAYER = 46;
    private static volatile SecretChatHelper Instance;
    private HashMap<Integer, EncryptedChat> acceptingChats;
    public ArrayList<Update> delayedEncryptedChatUpdates;
    private ArrayList<Long> pendingEncMessagesToDelete;
    private HashMap<Integer, ArrayList<TL_decryptedMessageHolder>> secretHolesQueue;
    private ArrayList<Integer> sendingNotifyLayer;
    private boolean startingSecretChat;

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ EncryptedChat val$encryptedChat;

        AnonymousClass10(EncryptedChat encryptedChat) {
            this.val$encryptedChat = encryptedChat;
        }

        public void run() {
            MessagesController.getInstance().putEncryptedChat(this.val$encryptedChat, false);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$encryptedChat);
            SecretChatHelper.this.sendNotifyLayerMessage(this.val$encryptedChat, null);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ TL_encryptedChatDiscarded val$newChat;

        AnonymousClass11(TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
            this.val$newChat = tL_encryptedChatDiscarded;
        }

        public void run() {
            MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.13 */
    class AnonymousClass13 implements RequestDelegate {
        final /* synthetic */ EncryptedChat val$encryptedChat;

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.13.1 */
        class C06221 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.13.1.1 */
            class C06211 implements Runnable {
                final /* synthetic */ EncryptedChat val$newChat;

                C06211(EncryptedChat encryptedChat) {
                    this.val$newChat = encryptedChat;
                }

                public void run() {
                    MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
                    SecretChatHelper.this.sendNotifyLayerMessage(this.val$newChat, null);
                }
            }

            C06221() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(AnonymousClass13.this.val$encryptedChat.id));
                if (tL_error == null) {
                    EncryptedChat encryptedChat = (EncryptedChat) tLObject;
                    encryptedChat.auth_key = AnonymousClass13.this.val$encryptedChat.auth_key;
                    encryptedChat.user_id = AnonymousClass13.this.val$encryptedChat.user_id;
                    encryptedChat.seq_in = AnonymousClass13.this.val$encryptedChat.seq_in;
                    encryptedChat.seq_out = AnonymousClass13.this.val$encryptedChat.seq_out;
                    encryptedChat.key_create_date = AnonymousClass13.this.val$encryptedChat.key_create_date;
                    encryptedChat.key_use_count_in = AnonymousClass13.this.val$encryptedChat.key_use_count_in;
                    encryptedChat.key_use_count_out = AnonymousClass13.this.val$encryptedChat.key_use_count_out;
                    MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                    AndroidUtilities.runOnUIThread(new C06211(encryptedChat));
                }
            }
        }

        AnonymousClass13(EncryptedChat encryptedChat) {
            this.val$encryptedChat = encryptedChat;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                int i;
                messages_DhConfig com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig = (messages_DhConfig) tLObject;
                if (tLObject instanceof TL_messages_dhConfig) {
                    if (Utilities.isGoodPrime(com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2671p, com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2670g)) {
                        MessagesStorage.secretPBytes = com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2671p;
                        MessagesStorage.secretG = com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2670g;
                        MessagesStorage.lastSecretVersion = com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.version;
                        MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
                    } else {
                        SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(this.val$encryptedChat.id));
                        SecretChatHelper.this.declineSecretChat(this.val$encryptedChat.id);
                        return;
                    }
                }
                byte[] bArr = new byte[TLRPC.USER_FLAG_UNUSED2];
                for (i = 0; i < TLRPC.USER_FLAG_UNUSED2; i++) {
                    bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.random[i]);
                }
                this.val$encryptedChat.a_or_b = bArr;
                this.val$encryptedChat.seq_in = 1;
                this.val$encryptedChat.seq_out = 0;
                BigInteger bigInteger = new BigInteger(1, MessagesStorage.secretPBytes);
                BigInteger modPow = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, bArr), bigInteger);
                BigInteger bigInteger2 = new BigInteger(1, this.val$encryptedChat.g_a);
                if (Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
                    byte[] bArr2;
                    byte[] bArr3;
                    Object obj;
                    Object toByteArray = modPow.toByteArray();
                    if (toByteArray.length > TLRPC.USER_FLAG_UNUSED2) {
                        bArr2 = new byte[TLRPC.USER_FLAG_UNUSED2];
                        System.arraycopy(toByteArray, 1, bArr2, 0, TLRPC.USER_FLAG_UNUSED2);
                    } else {
                        Object obj2 = toByteArray;
                    }
                    Object toByteArray2 = bigInteger2.modPow(new BigInteger(1, bArr), bigInteger).toByteArray();
                    if (toByteArray2.length > TLRPC.USER_FLAG_UNUSED2) {
                        bArr3 = new byte[TLRPC.USER_FLAG_UNUSED2];
                        System.arraycopy(toByteArray2, toByteArray2.length + InputDeviceCompat.SOURCE_ANY, bArr3, 0, TLRPC.USER_FLAG_UNUSED2);
                    } else if (toByteArray2.length < TLRPC.USER_FLAG_UNUSED2) {
                        obj = new byte[TLRPC.USER_FLAG_UNUSED2];
                        System.arraycopy(toByteArray2, 0, obj, 256 - toByteArray2.length, toByteArray2.length);
                        for (i = 0; i < 256 - toByteArray2.length; i++) {
                            toByteArray2[i] = null;
                        }
                        toByteArray = obj;
                    } else {
                        toByteArray = toByteArray2;
                    }
                    obj = Utilities.computeSHA1(bArr3);
                    toByteArray2 = new byte[8];
                    System.arraycopy(obj, obj.length - 8, toByteArray2, 0, 8);
                    this.val$encryptedChat.auth_key = bArr3;
                    this.val$encryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                    TLObject tL_messages_acceptEncryption = new TL_messages_acceptEncryption();
                    tL_messages_acceptEncryption.g_b = bArr2;
                    tL_messages_acceptEncryption.peer = new TL_inputEncryptedChat();
                    tL_messages_acceptEncryption.peer.chat_id = this.val$encryptedChat.id;
                    tL_messages_acceptEncryption.peer.access_hash = this.val$encryptedChat.access_hash;
                    tL_messages_acceptEncryption.key_fingerprint = Utilities.bytesToLong(toByteArray2);
                    ConnectionsManager.getInstance().sendRequest(tL_messages_acceptEncryption, new C06221());
                    return;
                }
                SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(this.val$encryptedChat.id));
                SecretChatHelper.this.declineSecretChat(this.val$encryptedChat.id);
                return;
            }
            SecretChatHelper.this.acceptingChats.remove(Integer.valueOf(this.val$encryptedChat.id));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.14 */
    class AnonymousClass14 implements RequestDelegate {
        final /* synthetic */ Context val$context;
        final /* synthetic */ ProgressDialog val$progressDialog;
        final /* synthetic */ User val$user;

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.14.1 */
        class C06231 implements Runnable {
            C06231() {
            }

            public void run() {
                try {
                    if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                        AnonymousClass14.this.val$progressDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.14.2 */
        class C06272 implements RequestDelegate {
            final /* synthetic */ byte[] val$salt;

            /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.14.2.1 */
            class C06251 implements Runnable {
                final /* synthetic */ TLObject val$response;

                /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.14.2.1.1 */
                class C06241 implements Runnable {
                    C06241() {
                    }

                    public void run() {
                        if (!SecretChatHelper.this.delayedEncryptedChatUpdates.isEmpty()) {
                            MessagesController.getInstance().processUpdateArray(SecretChatHelper.this.delayedEncryptedChatUpdates, null, null, false);
                            SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                        }
                    }
                }

                C06251(TLObject tLObject) {
                    this.val$response = tLObject;
                }

                public void run() {
                    SecretChatHelper.this.startingSecretChat = false;
                    if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                        try {
                            AnonymousClass14.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                    EncryptedChat encryptedChat = (EncryptedChat) this.val$response;
                    encryptedChat.user_id = encryptedChat.participant_id;
                    encryptedChat.seq_in = 0;
                    encryptedChat.seq_out = 1;
                    encryptedChat.a_or_b = C06272.this.val$salt;
                    MessagesController.getInstance().putEncryptedChat(encryptedChat, false);
                    TL_dialog tL_dialog = new TL_dialog();
                    tL_dialog.id = ((long) encryptedChat.id) << 32;
                    tL_dialog.unread_count = 0;
                    tL_dialog.top_message = 0;
                    tL_dialog.last_message_date = ConnectionsManager.getInstance().getCurrentTime();
                    MessagesController.getInstance().dialogs_dict.put(Long.valueOf(tL_dialog.id), tL_dialog);
                    MessagesController.getInstance().dialogs.add(tL_dialog);
                    MessagesController.getInstance().sortDialogs(null);
                    MessagesStorage.getInstance().putEncryptedChat(encryptedChat, AnonymousClass14.this.val$user, tL_dialog);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatCreated, encryptedChat);
                    Utilities.stageQueue.postRunnable(new C06241());
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.14.2.2 */
            class C06262 implements Runnable {
                C06262() {
                }

                public void run() {
                    if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                        SecretChatHelper.this.startingSecretChat = false;
                        try {
                            AnonymousClass14.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        Builder builder = new Builder(AnonymousClass14.this.val$context);
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("CreateEncryptedChatError", C0338R.string.CreateEncryptedChatError));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        builder.show().setCanceledOnTouchOutside(true);
                    }
                }
            }

            C06272(byte[] bArr) {
                this.val$salt = bArr;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    AndroidUtilities.runOnUIThread(new C06251(tLObject));
                    return;
                }
                SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                AndroidUtilities.runOnUIThread(new C06262());
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.14.3 */
        class C06283 implements Runnable {
            C06283() {
            }

            public void run() {
                SecretChatHelper.this.startingSecretChat = false;
                if (!((Activity) AnonymousClass14.this.val$context).isFinishing()) {
                    try {
                        AnonymousClass14.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }

        AnonymousClass14(Context context, ProgressDialog progressDialog, User user) {
            this.val$context = context;
            this.val$progressDialog = progressDialog;
            this.val$user = user;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                byte[] bArr;
                messages_DhConfig com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig = (messages_DhConfig) tLObject;
                if (tLObject instanceof TL_messages_dhConfig) {
                    if (Utilities.isGoodPrime(com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2671p, com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2670g)) {
                        MessagesStorage.secretPBytes = com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2671p;
                        MessagesStorage.secretG = com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.f2670g;
                        MessagesStorage.lastSecretVersion = com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.version;
                        MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
                    } else {
                        AndroidUtilities.runOnUIThread(new C06231());
                        return;
                    }
                }
                byte[] bArr2 = new byte[TLRPC.USER_FLAG_UNUSED2];
                for (int i = 0; i < TLRPC.USER_FLAG_UNUSED2; i++) {
                    bArr2[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ com_hanista_mobogram_tgnet_TLRPC_messages_DhConfig.random[i]);
                }
                Object toByteArray = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, bArr2), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
                if (toByteArray.length > TLRPC.USER_FLAG_UNUSED2) {
                    bArr = new byte[TLRPC.USER_FLAG_UNUSED2];
                    System.arraycopy(toByteArray, 1, bArr, 0, TLRPC.USER_FLAG_UNUSED2);
                } else {
                    Object obj = toByteArray;
                }
                TLObject tL_messages_requestEncryption = new TL_messages_requestEncryption();
                tL_messages_requestEncryption.g_a = bArr;
                tL_messages_requestEncryption.user_id = MessagesController.getInputUser(this.val$user);
                tL_messages_requestEncryption.random_id = Utilities.random.nextInt();
                ConnectionsManager.getInstance().sendRequest(tL_messages_requestEncryption, new C06272(bArr2), 2);
                return;
            }
            SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
            AndroidUtilities.runOnUIThread(new C06283());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.15 */
    class AnonymousClass15 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        AnonymousClass15(int i) {
            this.val$reqId = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialogInterface.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.1 */
    class C06291 implements Runnable {
        final /* synthetic */ ArrayList val$pendingEncMessagesToDeleteCopy;

        C06291(ArrayList arrayList) {
            this.val$pendingEncMessagesToDeleteCopy = arrayList;
        }

        public void run() {
            for (int i = 0; i < this.val$pendingEncMessagesToDeleteCopy.size(); i++) {
                MessageObject messageObject = (MessageObject) MessagesController.getInstance().dialogMessagesByRandomIds.get(this.val$pendingEncMessagesToDeleteCopy.get(i));
                if (messageObject != null) {
                    messageObject.deleted = true;
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.2 */
    class C06302 implements Runnable {
        final /* synthetic */ TL_dialog val$dialog;
        final /* synthetic */ EncryptedChat val$newChat;

        C06302(TL_dialog tL_dialog, EncryptedChat encryptedChat) {
            this.val$dialog = tL_dialog;
            this.val$newChat = encryptedChat;
        }

        public void run() {
            MessagesController.getInstance().dialogs_dict.put(Long.valueOf(this.val$dialog.id), this.val$dialog);
            MessagesController.getInstance().dialogs.add(this.val$dialog);
            MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            MessagesController.getInstance().sortDialogs(null);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.3 */
    class C06313 implements Runnable {
        final /* synthetic */ EncryptedChat val$exist;
        final /* synthetic */ EncryptedChat val$newChat;

        C06313(EncryptedChat encryptedChat, EncryptedChat encryptedChat2) {
            this.val$exist = encryptedChat;
            this.val$newChat = encryptedChat2;
        }

        public void run() {
            if (this.val$exist != null) {
                MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            }
            MessagesStorage.getInstance().updateEncryptedChat(this.val$newChat);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.4 */
    class C06364 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;
        final /* synthetic */ InputEncryptedFile val$encryptedFile;
        final /* synthetic */ MessageObject val$newMsg;
        final /* synthetic */ Message val$newMsgObj;
        final /* synthetic */ String val$originalPath;
        final /* synthetic */ DecryptedMessage val$req;

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.4.1 */
        class C06351 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.4.1.1 */
            class C06331 implements Runnable {
                final /* synthetic */ String val$attachPath;
                final /* synthetic */ messages_SentEncryptedMessage val$res;

                /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.4.1.1.1 */
                class C06321 implements Runnable {
                    C06321() {
                    }

                    public void run() {
                        C06364.this.val$newMsgObj.send_state = 0;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(C06364.this.val$newMsgObj.id), Integer.valueOf(C06364.this.val$newMsgObj.id), C06364.this.val$newMsgObj, Long.valueOf(C06364.this.val$newMsgObj.dialog_id));
                        SendMessagesHelper.getInstance().processSentMessage(C06364.this.val$newMsgObj.id);
                        if (MessageObject.isVideoMessage(C06364.this.val$newMsgObj) || MessageObject.isNewGifMessage(C06364.this.val$newMsgObj)) {
                            SendMessagesHelper.getInstance().stopVideoService(C06331.this.val$attachPath);
                        }
                        SendMessagesHelper.getInstance().removeFromSendingMessages(C06364.this.val$newMsgObj.id);
                    }
                }

                C06331(messages_SentEncryptedMessage com_hanista_mobogram_tgnet_TLRPC_messages_SentEncryptedMessage, String str) {
                    this.val$res = com_hanista_mobogram_tgnet_TLRPC_messages_SentEncryptedMessage;
                    this.val$attachPath = str;
                }

                public void run() {
                    if (SecretChatHelper.isSecretInvisibleMessage(C06364.this.val$newMsgObj)) {
                        this.val$res.date = 0;
                    }
                    MessagesStorage.getInstance().updateMessageStateAndId(C06364.this.val$newMsgObj.random_id, Integer.valueOf(C06364.this.val$newMsgObj.id), C06364.this.val$newMsgObj.id, this.val$res.date, false, 0);
                    AndroidUtilities.runOnUIThread(new C06321());
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.4.1.2 */
            class C06342 implements Runnable {
                C06342() {
                }

                public void run() {
                    C06364.this.val$newMsgObj.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(C06364.this.val$newMsgObj.id));
                    SendMessagesHelper.getInstance().processSentMessage(C06364.this.val$newMsgObj.id);
                    if (MessageObject.isVideoMessage(C06364.this.val$newMsgObj) || MessageObject.isNewGifMessage(C06364.this.val$newMsgObj)) {
                        SendMessagesHelper.getInstance().stopVideoService(C06364.this.val$newMsgObj.attachPath);
                    }
                    SendMessagesHelper.getInstance().removeFromSendingMessages(C06364.this.val$newMsgObj.id);
                }
            }

            C06351() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null && (C06364.this.val$req.action instanceof TL_decryptedMessageActionNotifyLayer)) {
                    EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(C06364.this.val$chat.id));
                    if (encryptedChat == null) {
                        encryptedChat = C06364.this.val$chat;
                    }
                    if (encryptedChat.key_hash == null) {
                        encryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
                    }
                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= SecretChatHelper.CURRENT_SECRET_CHAT_LAYER && encryptedChat.key_hash.length == 16) {
                        try {
                            Object computeSHA256 = Utilities.computeSHA256(C06364.this.val$chat.auth_key, 0, C06364.this.val$chat.auth_key.length);
                            Object obj = new byte[36];
                            System.arraycopy(C06364.this.val$chat.key_hash, 0, obj, 0, 16);
                            System.arraycopy(computeSHA256, 0, obj, 16, 20);
                            encryptedChat.key_hash = obj;
                            MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                        } catch (Throwable th) {
                            FileLog.m18e("tmessages", th);
                        }
                    }
                    SecretChatHelper.this.sendingNotifyLayer.remove(Integer.valueOf(encryptedChat.id));
                    encryptedChat.layer = AndroidUtilities.setMyLayerVersion(encryptedChat.layer, SecretChatHelper.CURRENT_SECRET_CHAT_LAYER);
                    MessagesStorage.getInstance().updateEncryptedChatLayer(encryptedChat);
                }
                if (C06364.this.val$newMsgObj == null) {
                    return;
                }
                if (tL_error == null) {
                    String str = C06364.this.val$newMsgObj.attachPath;
                    messages_SentEncryptedMessage com_hanista_mobogram_tgnet_TLRPC_messages_SentEncryptedMessage = (messages_SentEncryptedMessage) tLObject;
                    if (SecretChatHelper.isSecretVisibleMessage(C06364.this.val$newMsgObj)) {
                        C06364.this.val$newMsgObj.date = com_hanista_mobogram_tgnet_TLRPC_messages_SentEncryptedMessage.date;
                    }
                    if (C06364.this.val$newMsg != null && (com_hanista_mobogram_tgnet_TLRPC_messages_SentEncryptedMessage.file instanceof TL_encryptedFile)) {
                        SecretChatHelper.this.updateMediaPaths(C06364.this.val$newMsg, com_hanista_mobogram_tgnet_TLRPC_messages_SentEncryptedMessage.file, C06364.this.val$req, C06364.this.val$originalPath);
                    }
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06331(com_hanista_mobogram_tgnet_TLRPC_messages_SentEncryptedMessage, str));
                    return;
                }
                MessagesStorage.getInstance().markMessageAsSendError(C06364.this.val$newMsgObj);
                AndroidUtilities.runOnUIThread(new C06342());
            }
        }

        C06364(EncryptedChat encryptedChat, DecryptedMessage decryptedMessage, Message message, InputEncryptedFile inputEncryptedFile, MessageObject messageObject, String str) {
            this.val$chat = encryptedChat;
            this.val$req = decryptedMessage;
            this.val$newMsgObj = message;
            this.val$encryptedFile = inputEncryptedFile;
            this.val$newMsg = messageObject;
            this.val$originalPath = str;
        }

        public void run() {
            int i = 0;
            try {
                TLObject tL_decryptedMessageLayer;
                TLObject tL_messages_sendEncryptedFile;
                if (AndroidUtilities.getPeerLayerVersion(this.val$chat.layer) >= 17) {
                    tL_decryptedMessageLayer = new TL_decryptedMessageLayer();
                    tL_decryptedMessageLayer.layer = Math.min(Math.max(17, AndroidUtilities.getMyLayerVersion(this.val$chat.layer)), AndroidUtilities.getPeerLayerVersion(this.val$chat.layer));
                    tL_decryptedMessageLayer.message = this.val$req;
                    tL_decryptedMessageLayer.random_bytes = new byte[15];
                    Utilities.random.nextBytes(tL_decryptedMessageLayer.random_bytes);
                    if (this.val$chat.seq_in == 0 && this.val$chat.seq_out == 0) {
                        if (this.val$chat.admin_id == UserConfig.getClientUserId()) {
                            this.val$chat.seq_out = 1;
                        } else {
                            this.val$chat.seq_in = 1;
                        }
                    }
                    if (this.val$newMsgObj.seq_in == 0 && this.val$newMsgObj.seq_out == 0) {
                        tL_decryptedMessageLayer.in_seq_no = this.val$chat.seq_in;
                        tL_decryptedMessageLayer.out_seq_no = this.val$chat.seq_out;
                        EncryptedChat encryptedChat = this.val$chat;
                        encryptedChat.seq_out += 2;
                        if (AndroidUtilities.getPeerLayerVersion(this.val$chat.layer) >= 20) {
                            if (this.val$chat.key_create_date == 0) {
                                this.val$chat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                            }
                            encryptedChat = this.val$chat;
                            encryptedChat.key_use_count_out = (short) (encryptedChat.key_use_count_out + 1);
                            if ((this.val$chat.key_use_count_out >= (short) 100 || this.val$chat.key_create_date < ConnectionsManager.getInstance().getCurrentTime() - 604800) && this.val$chat.exchange_id == 0 && this.val$chat.future_key_fingerprint == 0) {
                                SecretChatHelper.this.requestNewSecretChatKey(this.val$chat);
                            }
                        }
                        MessagesStorage.getInstance().updateEncryptedChatSeq(this.val$chat);
                        if (this.val$newMsgObj != null) {
                            this.val$newMsgObj.seq_in = tL_decryptedMessageLayer.in_seq_no;
                            this.val$newMsgObj.seq_out = tL_decryptedMessageLayer.out_seq_no;
                            MessagesStorage.getInstance().setMessageSeq(this.val$newMsgObj.id, this.val$newMsgObj.seq_in, this.val$newMsgObj.seq_out);
                        }
                    } else {
                        tL_decryptedMessageLayer.in_seq_no = this.val$newMsgObj.seq_in;
                        tL_decryptedMessageLayer.out_seq_no = this.val$newMsgObj.seq_out;
                    }
                    FileLog.m16e("tmessages", this.val$req + " send message with in_seq = " + tL_decryptedMessageLayer.in_seq_no + " out_seq = " + tL_decryptedMessageLayer.out_seq_no);
                } else {
                    tL_decryptedMessageLayer = this.val$req;
                }
                int objectSize = tL_decryptedMessageLayer.getObjectSize();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(objectSize + 4);
                nativeByteBuffer.writeInt32(objectSize);
                tL_decryptedMessageLayer.serializeToStream(nativeByteBuffer);
                Object computeSHA1 = Utilities.computeSHA1(nativeByteBuffer.buffer);
                byte[] bArr = new byte[16];
                if (computeSHA1.length != 0) {
                    System.arraycopy(computeSHA1, computeSHA1.length - 16, bArr, 0, 16);
                }
                MessageKeyData generateMessageKeyData = MessageKeyData.generateMessageKeyData(this.val$chat.auth_key, bArr, false);
                int length = nativeByteBuffer.length();
                if (length % 16 != 0) {
                    i = 16 - (length % 16);
                }
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(length + i);
                nativeByteBuffer.position(0);
                nativeByteBuffer2.writeBytes(nativeByteBuffer);
                if (i != 0) {
                    byte[] bArr2 = new byte[i];
                    Utilities.random.nextBytes(bArr2);
                    nativeByteBuffer2.writeBytes(bArr2);
                }
                nativeByteBuffer.reuse();
                Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, true, false, 0, nativeByteBuffer2.limit());
                NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer((bArr.length + 8) + nativeByteBuffer2.length());
                nativeByteBuffer2.position(0);
                nativeByteBuffer3.writeInt64(this.val$chat.key_fingerprint);
                nativeByteBuffer3.writeBytes(bArr);
                nativeByteBuffer3.writeBytes(nativeByteBuffer2);
                nativeByteBuffer2.reuse();
                nativeByteBuffer3.position(0);
                if (this.val$encryptedFile != null) {
                    tL_messages_sendEncryptedFile = new TL_messages_sendEncryptedFile();
                    tL_messages_sendEncryptedFile.data = nativeByteBuffer3;
                    tL_messages_sendEncryptedFile.random_id = this.val$req.random_id;
                    tL_messages_sendEncryptedFile.peer = new TL_inputEncryptedChat();
                    tL_messages_sendEncryptedFile.peer.chat_id = this.val$chat.id;
                    tL_messages_sendEncryptedFile.peer.access_hash = this.val$chat.access_hash;
                    tL_messages_sendEncryptedFile.file = this.val$encryptedFile;
                } else if (this.val$req instanceof TL_decryptedMessageService) {
                    tL_messages_sendEncryptedFile = new TL_messages_sendEncryptedService();
                    tL_messages_sendEncryptedFile.data = nativeByteBuffer3;
                    tL_messages_sendEncryptedFile.random_id = this.val$req.random_id;
                    tL_messages_sendEncryptedFile.peer = new TL_inputEncryptedChat();
                    tL_messages_sendEncryptedFile.peer.chat_id = this.val$chat.id;
                    tL_messages_sendEncryptedFile.peer.access_hash = this.val$chat.access_hash;
                } else {
                    tL_messages_sendEncryptedFile = new TL_messages_sendEncrypted();
                    tL_messages_sendEncryptedFile.data = nativeByteBuffer3;
                    tL_messages_sendEncryptedFile.random_id = this.val$req.random_id;
                    tL_messages_sendEncryptedFile.peer = new TL_inputEncryptedChat();
                    tL_messages_sendEncryptedFile.peer.chat_id = this.val$chat.id;
                    tL_messages_sendEncryptedFile.peer.access_hash = this.val$chat.access_hash;
                }
                ConnectionsManager.getInstance().sendRequest(tL_messages_sendEncryptedFile, new C06351(), 64);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.5 */
    class C06375 implements Runnable {
        final /* synthetic */ EncryptedChat val$chat;

        C06375(EncryptedChat encryptedChat) {
            this.val$chat = encryptedChat;
        }

        public void run() {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$chat);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.6 */
    class C06406 implements Runnable {
        final /* synthetic */ long val$did;

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.6.1 */
        class C06391 implements Runnable {

            /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.6.1.1 */
            class C06381 implements Runnable {
                C06381() {
                }

                public void run() {
                    NotificationsController.getInstance().processReadMessages(null, C06406.this.val$did, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
                    HashMap hashMap = new HashMap();
                    hashMap.put(Long.valueOf(C06406.this.val$did), Integer.valueOf(0));
                    NotificationsController.getInstance().processDialogsUpdateRead(hashMap);
                }
            }

            C06391() {
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C06381());
            }
        }

        C06406(long j) {
            this.val$did = j;
        }

        public void run() {
            TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$did));
            if (tL_dialog != null) {
                tL_dialog.unread_count = 0;
                MessagesController.getInstance().dialogMessage.remove(Long.valueOf(tL_dialog.id));
            }
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06391());
            MessagesStorage.getInstance().deleteDialog(this.val$did, 1);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(this.val$did), Boolean.valueOf(false));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.7 */
    class C06437 implements Runnable {
        final /* synthetic */ EncryptedChat val$encryptedChat;
        final /* synthetic */ int val$endSeq;
        final /* synthetic */ int val$startSeq;

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.7.1 */
        class C06411 implements Comparator<Message> {
            C06411() {
            }

            public int compare(Message message, Message message2) {
                return AndroidUtilities.compare(message.seq_out, message2.seq_out);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.7.2 */
        class C06422 implements Runnable {
            final /* synthetic */ ArrayList val$messages;

            C06422(ArrayList arrayList) {
                this.val$messages = arrayList;
            }

            public void run() {
                for (int i = 0; i < this.val$messages.size(); i++) {
                    MessageObject messageObject = new MessageObject((Message) this.val$messages.get(i), null, false);
                    messageObject.resendAsIs = true;
                    SendMessagesHelper.getInstance().retrySendMessage(messageObject, true);
                }
            }
        }

        C06437(int i, EncryptedChat encryptedChat, int i2) {
            this.val$startSeq = i;
            this.val$encryptedChat = encryptedChat;
            this.val$endSeq = i2;
        }

        public void run() {
            try {
                int i = this.val$startSeq;
                if (this.val$encryptedChat.admin_id == UserConfig.getClientUserId() && i % 2 == 0) {
                    i++;
                }
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[]{Integer.valueOf(this.val$encryptedChat.id), Integer.valueOf(i), Integer.valueOf(i), Integer.valueOf(this.val$endSeq), Integer.valueOf(this.val$endSeq)}), new Object[0]);
                boolean next = queryFinalized.next();
                queryFinalized.dispose();
                if (!next) {
                    long j = ((long) this.val$encryptedChat.id) << 32;
                    HashMap hashMap = new HashMap();
                    ArrayList arrayList = new ArrayList();
                    for (int i2 = i; i2 < this.val$endSeq; i2 += 2) {
                        hashMap.put(Integer.valueOf(i2), null);
                    }
                    SQLiteCursor queryFinalized2 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(this.val$endSeq)}), new Object[0]);
                    while (queryFinalized2.next()) {
                        Object TLdeserialize;
                        long longValue = queryFinalized2.longValue(1);
                        if (longValue == 0) {
                            longValue = Utilities.random.nextLong();
                        }
                        int intValue = queryFinalized2.intValue(2);
                        int intValue2 = queryFinalized2.intValue(3);
                        int intValue3 = queryFinalized2.intValue(5);
                        AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            TLdeserialize.random_id = longValue;
                            TLdeserialize.dialog_id = j;
                            TLdeserialize.seq_in = intValue;
                            TLdeserialize.seq_out = intValue2;
                            TLdeserialize.ttl = queryFinalized2.intValue(4);
                        } else {
                            TLdeserialize = SecretChatHelper.this.createDeleteMessage(intValue3, intValue2, intValue, longValue, this.val$encryptedChat);
                        }
                        arrayList.add(TLdeserialize);
                        hashMap.remove(Integer.valueOf(intValue2));
                    }
                    queryFinalized2.dispose();
                    if (!hashMap.isEmpty()) {
                        for (Entry key : hashMap.entrySet()) {
                            arrayList.add(SecretChatHelper.this.createDeleteMessage(UserConfig.getNewMessageId(), ((Integer) key.getKey()).intValue(), 0, Utilities.random.nextLong(), this.val$encryptedChat));
                        }
                        UserConfig.saveConfig(false);
                    }
                    Collections.sort(arrayList, new C06411());
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(this.val$encryptedChat);
                    AndroidUtilities.runOnUIThread(new C06422(arrayList));
                    SendMessagesHelper.getInstance().processUnsentMessages(arrayList, new ArrayList(), new ArrayList(), arrayList2);
                    MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(this.val$encryptedChat.id), Integer.valueOf(i), Integer.valueOf(this.val$endSeq)})).stepThis().dispose();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.8 */
    class C06448 implements Comparator<TL_decryptedMessageHolder> {
        C06448() {
        }

        public int compare(TL_decryptedMessageHolder tL_decryptedMessageHolder, TL_decryptedMessageHolder tL_decryptedMessageHolder2) {
            return tL_decryptedMessageHolder.layer.out_seq_no > tL_decryptedMessageHolder2.layer.out_seq_no ? 1 : tL_decryptedMessageHolder.layer.out_seq_no < tL_decryptedMessageHolder2.layer.out_seq_no ? -1 : 0;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.SecretChatHelper.9 */
    class C06459 implements Runnable {
        final /* synthetic */ TL_encryptedChatDiscarded val$newChat;

        C06459(TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
            this.val$newChat = tL_encryptedChatDiscarded;
        }

        public void run() {
            MessagesController.getInstance().putEncryptedChat(this.val$newChat, false);
            MessagesStorage.getInstance().updateEncryptedChat(this.val$newChat);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.encryptedChatUpdated, this.val$newChat);
        }
    }

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor;
        public int date;
        public EncryptedFile file;
        public TL_decryptedMessageLayer layer;
        public boolean new_key_used;
        public long random_id;

        static {
            constructor = 1431655929;
        }

        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.random_id = abstractSerializedData.readInt64(z);
            this.date = abstractSerializedData.readInt32(z);
            this.layer = TL_decryptedMessageLayer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (abstractSerializedData.readBool(z)) {
                this.file = EncryptedFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.new_key_used = abstractSerializedData.readBool(z);
        }

        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt64(this.random_id);
            abstractSerializedData.writeInt32(this.date);
            this.layer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeBool(this.file != null);
            if (this.file != null) {
                this.file.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeBool(this.new_key_used);
        }
    }

    static {
        Instance = null;
    }

    public SecretChatHelper() {
        this.sendingNotifyLayer = new ArrayList();
        this.secretHolesQueue = new HashMap();
        this.acceptingChats = new HashMap();
        this.delayedEncryptedChatUpdates = new ArrayList();
        this.pendingEncMessagesToDelete = new ArrayList();
        this.startingSecretChat = false;
    }

    private void applyPeerLayer(EncryptedChat encryptedChat, int i) {
        int peerLayerVersion = AndroidUtilities.getPeerLayerVersion(encryptedChat.layer);
        if (i > peerLayerVersion) {
            if (encryptedChat.key_hash.length == 16 && peerLayerVersion >= CURRENT_SECRET_CHAT_LAYER) {
                try {
                    Object computeSHA256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
                    Object obj = new byte[36];
                    System.arraycopy(encryptedChat.key_hash, 0, obj, 0, 16);
                    System.arraycopy(computeSHA256, 0, obj, 16, 20);
                    encryptedChat.key_hash = obj;
                    MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            }
            encryptedChat.layer = AndroidUtilities.setPeerLayerVersion(encryptedChat.layer, i);
            MessagesStorage.getInstance().updateEncryptedChatLayer(encryptedChat);
            if (peerLayerVersion < CURRENT_SECRET_CHAT_LAYER) {
                sendNotifyLayerMessage(encryptedChat, null);
            }
            AndroidUtilities.runOnUIThread(new C06375(encryptedChat));
        }
    }

    private Message createDeleteMessage(int i, int i2, int i3, long j, EncryptedChat encryptedChat) {
        Message tL_messageService = new TL_messageService();
        tL_messageService.action = new TL_messageEncryptedAction();
        tL_messageService.action.encryptedAction = new TL_decryptedMessageActionDeleteMessages();
        tL_messageService.action.encryptedAction.random_ids.add(Long.valueOf(j));
        tL_messageService.id = i;
        tL_messageService.local_id = i;
        tL_messageService.from_id = UserConfig.getClientUserId();
        tL_messageService.unread = true;
        tL_messageService.out = true;
        tL_messageService.flags = TLRPC.USER_FLAG_UNUSED2;
        tL_messageService.dialog_id = ((long) encryptedChat.id) << 32;
        tL_messageService.to_id = new TL_peerUser();
        tL_messageService.send_state = 1;
        tL_messageService.seq_in = i3;
        tL_messageService.seq_out = i2;
        if (encryptedChat.participant_id == UserConfig.getClientUserId()) {
            tL_messageService.to_id.user_id = encryptedChat.admin_id;
        } else {
            tL_messageService.to_id.user_id = encryptedChat.participant_id;
        }
        tL_messageService.date = 0;
        tL_messageService.random_id = j;
        return tL_messageService;
    }

    private TL_messageService createServiceSecretMessage(EncryptedChat encryptedChat, DecryptedMessageAction decryptedMessageAction) {
        TL_messageService tL_messageService = new TL_messageService();
        tL_messageService.action = new TL_messageEncryptedAction();
        tL_messageService.action.encryptedAction = decryptedMessageAction;
        int newMessageId = UserConfig.getNewMessageId();
        tL_messageService.id = newMessageId;
        tL_messageService.local_id = newMessageId;
        tL_messageService.from_id = UserConfig.getClientUserId();
        tL_messageService.unread = true;
        tL_messageService.out = true;
        tL_messageService.flags = TLRPC.USER_FLAG_UNUSED2;
        tL_messageService.dialog_id = ((long) encryptedChat.id) << 32;
        tL_messageService.to_id = new TL_peerUser();
        tL_messageService.send_state = 1;
        if (encryptedChat.participant_id == UserConfig.getClientUserId()) {
            tL_messageService.to_id.user_id = encryptedChat.admin_id;
        } else {
            tL_messageService.to_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessageAction instanceof TL_decryptedMessageActionScreenshotMessages) || (decryptedMessageAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
            tL_messageService.date = ConnectionsManager.getInstance().getCurrentTime();
        } else {
            tL_messageService.date = 0;
        }
        tL_messageService.random_id = SendMessagesHelper.getInstance().getNextRandomId();
        UserConfig.saveConfig(false);
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_messageService);
        MessagesStorage.getInstance().putMessages(arrayList, false, true, true, 0);
        return tL_messageService;
    }

    public static SecretChatHelper getInstance() {
        SecretChatHelper secretChatHelper = Instance;
        if (secretChatHelper == null) {
            synchronized (SecretChatHelper.class) {
                secretChatHelper = Instance;
                if (secretChatHelper == null) {
                    secretChatHelper = new SecretChatHelper();
                    Instance = secretChatHelper;
                }
            }
        }
        return secretChatHelper;
    }

    public static boolean isSecretInvisibleMessage(Message message) {
        return (!(message.action instanceof TL_messageEncryptedAction) || (message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) ? false : true;
    }

    public static boolean isSecretVisibleMessage(Message message) {
        return (message.action instanceof TL_messageEncryptedAction) && ((message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL));
    }

    private void resendMessages(int i, int i2, EncryptedChat encryptedChat) {
        if (encryptedChat != null && i2 - i >= 0) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06437(i, encryptedChat, i2));
        }
    }

    private void updateMediaPaths(MessageObject messageObject, EncryptedFile encryptedFile, DecryptedMessage decryptedMessage, String str) {
        Message message = messageObject.messageOwner;
        if (encryptedFile == null) {
            return;
        }
        ArrayList arrayList;
        if ((message.media instanceof TL_messageMediaPhoto) && message.media.photo != null) {
            PhotoSize photoSize = (PhotoSize) message.media.photo.sizes.get(message.media.photo.sizes.size() - 1);
            String str2 = photoSize.location.volume_id + "_" + photoSize.location.local_id;
            photoSize.location = new TL_fileEncryptedLocation();
            photoSize.location.key = decryptedMessage.media.key;
            photoSize.location.iv = decryptedMessage.media.iv;
            photoSize.location.dc_id = encryptedFile.dc_id;
            photoSize.location.volume_id = encryptedFile.id;
            photoSize.location.secret = encryptedFile.access_hash;
            photoSize.location.local_id = encryptedFile.key_fingerprint;
            String str3 = photoSize.location.volume_id + "_" + photoSize.location.local_id;
            FileLoader.renameTo(new File(FileLoader.getInstance().getDirectory(4), str2 + ".jpg"), FileLoader.getPathToAttach(photoSize));
            ImageLoader.getInstance().replaceImageInCache(str2, str3, photoSize.location, true);
            arrayList = new ArrayList();
            arrayList.add(message);
            MessagesStorage.getInstance().putMessages(arrayList, false, true, false, 0);
        } else if ((message.media instanceof TL_messageMediaDocument) && message.media.document != null) {
            Document document = message.media.document;
            message.media.document = new TL_documentEncrypted();
            message.media.document.id = encryptedFile.id;
            message.media.document.access_hash = encryptedFile.access_hash;
            message.media.document.date = document.date;
            message.media.document.attributes = document.attributes;
            message.media.document.mime_type = document.mime_type;
            message.media.document.size = encryptedFile.size;
            message.media.document.key = decryptedMessage.media.key;
            message.media.document.iv = decryptedMessage.media.iv;
            message.media.document.thumb = document.thumb;
            message.media.document.dc_id = encryptedFile.dc_id;
            message.media.document.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
            if (message.attachPath != null && message.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath()) && FileLoader.renameTo(new File(message.attachPath), FileLoader.getPathToAttach(message.media.document))) {
                messageObject.mediaExists = messageObject.attachPathExists;
                messageObject.attachPathExists = false;
                message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            }
            arrayList = new ArrayList();
            arrayList.add(message);
            MessagesStorage.getInstance().putMessages(arrayList, false, true, false, 0);
        }
    }

    public void acceptSecretChat(EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(Integer.valueOf(encryptedChat.id)) == null) {
            this.acceptingChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            TLObject tL_messages_getDhConfig = new TL_messages_getDhConfig();
            tL_messages_getDhConfig.random_length = TLRPC.USER_FLAG_UNUSED2;
            tL_messages_getDhConfig.version = MessagesStorage.lastSecretVersion;
            ConnectionsManager.getInstance().sendRequest(tL_messages_getDhConfig, new AnonymousClass13(encryptedChat));
        }
    }

    public void checkSecretHoles(EncryptedChat encryptedChat, ArrayList<Message> arrayList) {
        ArrayList arrayList2 = (ArrayList) this.secretHolesQueue.get(Integer.valueOf(encryptedChat.id));
        if (arrayList2 != null) {
            Collections.sort(arrayList2, new C06448());
            int i = 0;
            while (arrayList2.size() > 0) {
                TL_decryptedMessageHolder tL_decryptedMessageHolder = (TL_decryptedMessageHolder) arrayList2.get(0);
                if (tL_decryptedMessageHolder.layer.out_seq_no != encryptedChat.seq_in && encryptedChat.seq_in != tL_decryptedMessageHolder.layer.out_seq_no - 2) {
                    break;
                }
                applyPeerLayer(encryptedChat, tL_decryptedMessageHolder.layer.layer);
                encryptedChat.seq_in = tL_decryptedMessageHolder.layer.out_seq_no;
                encryptedChat.in_seq_no = tL_decryptedMessageHolder.layer.in_seq_no;
                arrayList2.remove(0);
                Message processDecryptedObject = processDecryptedObject(encryptedChat, tL_decryptedMessageHolder.file, tL_decryptedMessageHolder.date, tL_decryptedMessageHolder.random_id, tL_decryptedMessageHolder.layer.message, tL_decryptedMessageHolder.new_key_used);
                if (processDecryptedObject != null) {
                    arrayList.add(processDecryptedObject);
                }
                i = 1;
            }
            if (arrayList2.isEmpty()) {
                this.secretHolesQueue.remove(Integer.valueOf(encryptedChat.id));
            }
            if (i != 0) {
                MessagesStorage.getInstance().updateEncryptedChatSeq(encryptedChat);
            }
        }
    }

    public void cleanup() {
        this.sendingNotifyLayer.clear();
        this.acceptingChats.clear();
        this.secretHolesQueue.clear();
        this.delayedEncryptedChatUpdates.clear();
        this.pendingEncMessagesToDelete.clear();
        this.startingSecretChat = false;
    }

    public void declineSecretChat(int i) {
        TLObject tL_messages_discardEncryption = new TL_messages_discardEncryption();
        tL_messages_discardEncryption.chat_id = i;
        ConnectionsManager.getInstance().sendRequest(tL_messages_discardEncryption, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    protected ArrayList<Message> decryptMessage(EncryptedMessage encryptedMessage) {
        boolean z = false;
        EncryptedChat encryptedChatDB = MessagesController.getInstance().getEncryptedChatDB(encryptedMessage.chat_id);
        if (encryptedChatDB == null || (encryptedChatDB instanceof TL_encryptedChatDiscarded)) {
            return null;
        }
        try {
            byte[] bArr;
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedMessage.bytes.length);
            nativeByteBuffer.writeBytes(encryptedMessage.bytes);
            nativeByteBuffer.position(0);
            long readInt64 = nativeByteBuffer.readInt64(false);
            if (encryptedChatDB.key_fingerprint == readInt64) {
                bArr = encryptedChatDB.auth_key;
            } else if (encryptedChatDB.future_key_fingerprint == 0 || encryptedChatDB.future_key_fingerprint != readInt64) {
                bArr = null;
            } else {
                z = true;
                bArr = encryptedChatDB.future_auth_key;
            }
            if (bArr != null) {
                byte[] readData = nativeByteBuffer.readData(16, false);
                MessageKeyData generateMessageKeyData = MessageKeyData.generateMessageKeyData(bArr, readData, false);
                Utilities.aesIgeEncryption(nativeByteBuffer.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, false, false, 24, nativeByteBuffer.limit() - 24);
                int readInt32 = nativeByteBuffer.readInt32(false);
                if (readInt32 < 0 || readInt32 > nativeByteBuffer.limit() - 28) {
                    return null;
                }
                bArr = Utilities.computeSHA1(nativeByteBuffer.buffer, 24, Math.min((readInt32 + 4) + 24, nativeByteBuffer.buffer.limit()));
                if (!Utilities.arraysEquals(readData, 0, bArr, bArr.length - 16)) {
                    return null;
                }
                TLObject tLObject;
                TLObject TLdeserialize = TLClassStore.Instance().TLdeserialize(nativeByteBuffer, nativeByteBuffer.readInt32(false), false);
                nativeByteBuffer.reuse();
                if (!z && AndroidUtilities.getPeerLayerVersion(encryptedChatDB.layer) >= 20) {
                    encryptedChatDB.key_use_count_in = (short) (encryptedChatDB.key_use_count_in + 1);
                }
                if (TLdeserialize instanceof TL_decryptedMessageLayer) {
                    TL_decryptedMessageLayer tL_decryptedMessageLayer = (TL_decryptedMessageLayer) TLdeserialize;
                    if (encryptedChatDB.seq_in == 0 && encryptedChatDB.seq_out == 0) {
                        if (encryptedChatDB.admin_id == UserConfig.getClientUserId()) {
                            encryptedChatDB.seq_out = 1;
                        } else {
                            encryptedChatDB.seq_in = 1;
                        }
                    }
                    if (tL_decryptedMessageLayer.random_bytes.length < 15) {
                        FileLog.m16e("tmessages", "got random bytes less than needed");
                        return null;
                    }
                    FileLog.m16e("tmessages", "current chat in_seq = " + encryptedChatDB.seq_in + " out_seq = " + encryptedChatDB.seq_out);
                    FileLog.m16e("tmessages", "got message with in_seq = " + tL_decryptedMessageLayer.in_seq_no + " out_seq = " + tL_decryptedMessageLayer.out_seq_no);
                    if (tL_decryptedMessageLayer.out_seq_no < encryptedChatDB.seq_in) {
                        return null;
                    }
                    if (encryptedChatDB.seq_in == tL_decryptedMessageLayer.out_seq_no || encryptedChatDB.seq_in == tL_decryptedMessageLayer.out_seq_no - 2) {
                        applyPeerLayer(encryptedChatDB, tL_decryptedMessageLayer.layer);
                        encryptedChatDB.seq_in = tL_decryptedMessageLayer.out_seq_no;
                        encryptedChatDB.in_seq_no = tL_decryptedMessageLayer.in_seq_no;
                        MessagesStorage.getInstance().updateEncryptedChatSeq(encryptedChatDB);
                        tLObject = tL_decryptedMessageLayer.message;
                    } else {
                        FileLog.m16e("tmessages", "got hole");
                        ArrayList arrayList = (ArrayList) this.secretHolesQueue.get(Integer.valueOf(encryptedChatDB.id));
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                            this.secretHolesQueue.put(Integer.valueOf(encryptedChatDB.id), arrayList);
                        }
                        if (arrayList.size() >= 4) {
                            this.secretHolesQueue.remove(Integer.valueOf(encryptedChatDB.id));
                            TL_encryptedChatDiscarded tL_encryptedChatDiscarded = new TL_encryptedChatDiscarded();
                            tL_encryptedChatDiscarded.id = encryptedChatDB.id;
                            tL_encryptedChatDiscarded.user_id = encryptedChatDB.user_id;
                            tL_encryptedChatDiscarded.auth_key = encryptedChatDB.auth_key;
                            tL_encryptedChatDiscarded.key_create_date = encryptedChatDB.key_create_date;
                            tL_encryptedChatDiscarded.key_use_count_in = encryptedChatDB.key_use_count_in;
                            tL_encryptedChatDiscarded.key_use_count_out = encryptedChatDB.key_use_count_out;
                            tL_encryptedChatDiscarded.seq_in = encryptedChatDB.seq_in;
                            tL_encryptedChatDiscarded.seq_out = encryptedChatDB.seq_out;
                            AndroidUtilities.runOnUIThread(new C06459(tL_encryptedChatDiscarded));
                            declineSecretChat(encryptedChatDB.id);
                            return null;
                        }
                        TL_decryptedMessageHolder tL_decryptedMessageHolder = new TL_decryptedMessageHolder();
                        tL_decryptedMessageHolder.layer = tL_decryptedMessageLayer;
                        tL_decryptedMessageHolder.file = encryptedMessage.file;
                        tL_decryptedMessageHolder.random_id = encryptedMessage.random_id;
                        tL_decryptedMessageHolder.date = encryptedMessage.date;
                        tL_decryptedMessageHolder.new_key_used = z;
                        arrayList.add(tL_decryptedMessageHolder);
                        return null;
                    }
                }
                tLObject = TLdeserialize;
                ArrayList<Message> arrayList2 = new ArrayList();
                Message processDecryptedObject = processDecryptedObject(encryptedChatDB, encryptedMessage.file, encryptedMessage.date, encryptedMessage.random_id, tLObject, z);
                if (processDecryptedObject != null) {
                    arrayList2.add(processDecryptedObject);
                }
                checkSecretHoles(encryptedChatDB, arrayList2);
                return arrayList2;
            }
            nativeByteBuffer.reuse();
            FileLog.m16e("tmessages", String.format("fingerprint mismatch %x", new Object[]{Long.valueOf(readInt64)}));
            return null;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    protected void performSendEncryptedRequest(DecryptedMessage decryptedMessage, Message message, EncryptedChat encryptedChat, InputEncryptedFile inputEncryptedFile, String str, MessageObject messageObject) {
        if (decryptedMessage != null && encryptedChat.auth_key != null && !(encryptedChat instanceof TL_encryptedChatRequested) && !(encryptedChat instanceof TL_encryptedChatWaiting)) {
            SendMessagesHelper.getInstance().putToSendingMessages(message);
            Utilities.stageQueue.postRunnable(new C06364(encryptedChat, decryptedMessage, message, inputEncryptedFile, messageObject, str));
        }
    }

    public void processAcceptedSecretChat(EncryptedChat encryptedChat) {
        BigInteger bigInteger = new BigInteger(1, MessagesStorage.secretPBytes);
        BigInteger bigInteger2 = new BigInteger(1, encryptedChat.g_a_or_b);
        if (Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
            byte[] bArr;
            Object obj;
            Object toByteArray = bigInteger2.modPow(new BigInteger(1, encryptedChat.a_or_b), bigInteger).toByteArray();
            if (toByteArray.length > TLRPC.USER_FLAG_UNUSED2) {
                bArr = new byte[TLRPC.USER_FLAG_UNUSED2];
                System.arraycopy(toByteArray, toByteArray.length + InputDeviceCompat.SOURCE_ANY, bArr, 0, TLRPC.USER_FLAG_UNUSED2);
            } else if (toByteArray.length < TLRPC.USER_FLAG_UNUSED2) {
                obj = new byte[TLRPC.USER_FLAG_UNUSED2];
                System.arraycopy(toByteArray, 0, obj, 256 - toByteArray.length, toByteArray.length);
                for (int i = 0; i < 256 - toByteArray.length; i++) {
                    toByteArray[i] = null;
                }
                r0 = obj;
            } else {
                r0 = toByteArray;
            }
            obj = Utilities.computeSHA1(bArr);
            toByteArray = new byte[8];
            System.arraycopy(obj, obj.length - 8, toByteArray, 0, 8);
            if (encryptedChat.key_fingerprint == Utilities.bytesToLong(toByteArray)) {
                encryptedChat.auth_key = bArr;
                encryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                encryptedChat.seq_in = 0;
                encryptedChat.seq_out = 1;
                MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                AndroidUtilities.runOnUIThread(new AnonymousClass10(encryptedChat));
                return;
            }
            EncryptedChat tL_encryptedChatDiscarded = new TL_encryptedChatDiscarded();
            tL_encryptedChatDiscarded.id = encryptedChat.id;
            tL_encryptedChatDiscarded.user_id = encryptedChat.user_id;
            tL_encryptedChatDiscarded.auth_key = encryptedChat.auth_key;
            tL_encryptedChatDiscarded.key_create_date = encryptedChat.key_create_date;
            tL_encryptedChatDiscarded.key_use_count_in = encryptedChat.key_use_count_in;
            tL_encryptedChatDiscarded.key_use_count_out = encryptedChat.key_use_count_out;
            tL_encryptedChatDiscarded.seq_in = encryptedChat.seq_in;
            tL_encryptedChatDiscarded.seq_out = encryptedChat.seq_out;
            MessagesStorage.getInstance().updateEncryptedChat(tL_encryptedChatDiscarded);
            AndroidUtilities.runOnUIThread(new AnonymousClass11(tL_encryptedChatDiscarded));
            declineSecretChat(encryptedChat.id);
            return;
        }
        declineSecretChat(encryptedChat.id);
    }

    public Message processDecryptedObject(EncryptedChat encryptedChat, EncryptedFile encryptedFile, int i, long j, TLObject tLObject, boolean z) {
        if (tLObject != null) {
            int i2 = encryptedChat.admin_id;
            if (i2 == UserConfig.getClientUserId()) {
                i2 = encryptedChat.participant_id;
            }
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20 && encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint == 0 && encryptedChat.key_use_count_in >= (short) 120) {
                requestNewSecretChatKey(encryptedChat);
            }
            if (encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint != 0 && !z) {
                encryptedChat.future_auth_key = new byte[TLRPC.USER_FLAG_UNUSED2];
                encryptedChat.future_key_fingerprint = 0;
                MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
            } else if (encryptedChat.exchange_id != 0 && z) {
                encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                encryptedChat.auth_key = encryptedChat.future_auth_key;
                encryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                encryptedChat.future_auth_key = new byte[TLRPC.USER_FLAG_UNUSED2];
                encryptedChat.future_key_fingerprint = 0;
                encryptedChat.key_use_count_in = (short) 0;
                encryptedChat.key_use_count_out = (short) 0;
                encryptedChat.exchange_id = 0;
                MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
            }
            Message tL_message_secret;
            int newMessageId;
            byte[] bArr;
            if (tLObject instanceof TL_decryptedMessage) {
                TL_decryptedMessage tL_decryptedMessage = (TL_decryptedMessage) tLObject;
                if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                    tL_message_secret = new TL_message_secret();
                    tL_message_secret.ttl = tL_decryptedMessage.ttl;
                    tL_message_secret.entities = tL_decryptedMessage.entities;
                } else {
                    tL_message_secret = new TL_message();
                    tL_message_secret.ttl = encryptedChat.ttl;
                }
                tL_message_secret.message = tL_decryptedMessage.message;
                tL_message_secret.date = i;
                newMessageId = UserConfig.getNewMessageId();
                tL_message_secret.id = newMessageId;
                tL_message_secret.local_id = newMessageId;
                UserConfig.saveConfig(false);
                tL_message_secret.from_id = i2;
                tL_message_secret.to_id = new TL_peerUser();
                tL_message_secret.random_id = j;
                tL_message_secret.to_id.user_id = UserConfig.getClientUserId();
                tL_message_secret.unread = true;
                tL_message_secret.flags = 768;
                if (tL_decryptedMessage.via_bot_name != null && tL_decryptedMessage.via_bot_name.length() > 0) {
                    tL_message_secret.via_bot_name = tL_decryptedMessage.via_bot_name;
                    tL_message_secret.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                }
                tL_message_secret.dialog_id = ((long) encryptedChat.id) << 32;
                if (tL_decryptedMessage.reply_to_random_id != 0) {
                    tL_message_secret.reply_to_random_id = tL_decryptedMessage.reply_to_random_id;
                    tL_message_secret.flags |= 8;
                }
                if (tL_decryptedMessage.media == null || (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaEmpty)) {
                    tL_message_secret.media = new TL_messageMediaEmpty();
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaWebPage) {
                    tL_message_secret.media = new TL_messageMediaWebPage();
                    tL_message_secret.media.webpage = new TL_webPageUrlPending();
                    tL_message_secret.media.webpage.url = tL_decryptedMessage.media.url;
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaContact) {
                    tL_message_secret.media = new TL_messageMediaContact();
                    tL_message_secret.media.last_name = tL_decryptedMessage.media.last_name;
                    tL_message_secret.media.first_name = tL_decryptedMessage.media.first_name;
                    tL_message_secret.media.phone_number = tL_decryptedMessage.media.phone_number;
                    tL_message_secret.media.user_id = tL_decryptedMessage.media.user_id;
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaGeoPoint) {
                    tL_message_secret.media = new TL_messageMediaGeo();
                    tL_message_secret.media.geo = new TL_geoPoint();
                    tL_message_secret.media.geo.lat = tL_decryptedMessage.media.lat;
                    tL_message_secret.media.geo._long = tL_decryptedMessage.media._long;
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaPhoto) {
                    if (tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null || tL_decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    tL_message_secret.media = new TL_messageMediaPhoto();
                    tL_message_secret.media.caption = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    tL_message_secret.media.photo = new TL_photo();
                    tL_message_secret.media.photo.date = tL_message_secret.date;
                    bArr = ((TL_decryptedMessageMediaPhoto) tL_decryptedMessage.media).thumb;
                    if (bArr != null && bArr.length != 0 && bArr.length <= 6000 && tL_decryptedMessage.media.thumb_w <= 100 && tL_decryptedMessage.media.thumb_h <= 100) {
                        TL_photoCachedSize tL_photoCachedSize = new TL_photoCachedSize();
                        tL_photoCachedSize.w = tL_decryptedMessage.media.thumb_w;
                        tL_photoCachedSize.h = tL_decryptedMessage.media.thumb_h;
                        tL_photoCachedSize.bytes = bArr;
                        tL_photoCachedSize.type = "s";
                        tL_photoCachedSize.location = new TL_fileLocationUnavailable();
                        tL_message_secret.media.photo.sizes.add(tL_photoCachedSize);
                    }
                    TL_photoSize tL_photoSize = new TL_photoSize();
                    tL_photoSize.w = tL_decryptedMessage.media.f2657w;
                    tL_photoSize.h = tL_decryptedMessage.media.f2656h;
                    tL_photoSize.type = "x";
                    tL_photoSize.size = encryptedFile.size;
                    tL_photoSize.location = new TL_fileEncryptedLocation();
                    tL_photoSize.location.key = tL_decryptedMessage.media.key;
                    tL_photoSize.location.iv = tL_decryptedMessage.media.iv;
                    tL_photoSize.location.dc_id = encryptedFile.dc_id;
                    tL_photoSize.location.volume_id = encryptedFile.id;
                    tL_photoSize.location.secret = encryptedFile.access_hash;
                    tL_photoSize.location.local_id = encryptedFile.key_fingerprint;
                    tL_message_secret.media.photo.sizes.add(tL_photoSize);
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaVideo) {
                    if (tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null || tL_decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    tL_message_secret.media = new TL_messageMediaDocument();
                    tL_message_secret.media.document = new TL_documentEncrypted();
                    tL_message_secret.media.document.key = tL_decryptedMessage.media.key;
                    tL_message_secret.media.document.iv = tL_decryptedMessage.media.iv;
                    tL_message_secret.media.document.dc_id = encryptedFile.dc_id;
                    tL_message_secret.media.caption = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    tL_message_secret.media.document.date = i;
                    tL_message_secret.media.document.size = encryptedFile.size;
                    tL_message_secret.media.document.id = encryptedFile.id;
                    tL_message_secret.media.document.access_hash = encryptedFile.access_hash;
                    tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                    if (tL_message_secret.media.document.mime_type == null) {
                        tL_message_secret.media.document.mime_type = MimeTypes.VIDEO_MP4;
                    }
                    bArr = ((TL_decryptedMessageMediaVideo) tL_decryptedMessage.media).thumb;
                    if (bArr == null || bArr.length == 0 || bArr.length > 6000 || tL_decryptedMessage.media.thumb_w > 100 || tL_decryptedMessage.media.thumb_h > 100) {
                        tL_message_secret.media.document.thumb = new TL_photoSizeEmpty();
                        tL_message_secret.media.document.thumb.type = "s";
                    } else {
                        tL_message_secret.media.document.thumb = new TL_photoCachedSize();
                        tL_message_secret.media.document.thumb.bytes = bArr;
                        tL_message_secret.media.document.thumb.f2664w = tL_decryptedMessage.media.thumb_w;
                        tL_message_secret.media.document.thumb.f2663h = tL_decryptedMessage.media.thumb_h;
                        tL_message_secret.media.document.thumb.type = "s";
                        tL_message_secret.media.document.thumb.location = new TL_fileLocationUnavailable();
                    }
                    TL_documentAttributeVideo tL_documentAttributeVideo = new TL_documentAttributeVideo();
                    tL_documentAttributeVideo.w = tL_decryptedMessage.media.f2657w;
                    tL_documentAttributeVideo.h = tL_decryptedMessage.media.f2656h;
                    tL_documentAttributeVideo.duration = tL_decryptedMessage.media.duration;
                    tL_message_secret.media.document.attributes.add(tL_documentAttributeVideo);
                    if (tL_message_secret.ttl == 0) {
                        return tL_message_secret;
                    }
                    tL_message_secret.ttl = Math.max(tL_decryptedMessage.media.duration + 2, tL_message_secret.ttl);
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaDocument) {
                    if (tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null || tL_decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    tL_message_secret.media = new TL_messageMediaDocument();
                    tL_message_secret.media.caption = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    tL_message_secret.media.document = new TL_documentEncrypted();
                    tL_message_secret.media.document.id = encryptedFile.id;
                    tL_message_secret.media.document.access_hash = encryptedFile.access_hash;
                    tL_message_secret.media.document.date = i;
                    if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaDocument_layer8) {
                        TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                        tL_documentAttributeFilename.file_name = tL_decryptedMessage.media.file_name;
                        tL_message_secret.media.document.attributes.add(tL_documentAttributeFilename);
                    } else {
                        tL_message_secret.media.document.attributes = tL_decryptedMessage.media.attributes;
                    }
                    tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                    tL_message_secret.media.document.size = tL_decryptedMessage.media.size != 0 ? Math.min(tL_decryptedMessage.media.size, encryptedFile.size) : encryptedFile.size;
                    tL_message_secret.media.document.key = tL_decryptedMessage.media.key;
                    tL_message_secret.media.document.iv = tL_decryptedMessage.media.iv;
                    if (tL_message_secret.media.document.mime_type == null) {
                        tL_message_secret.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    bArr = ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb;
                    if (bArr == null || bArr.length == 0 || bArr.length > 6000 || tL_decryptedMessage.media.thumb_w > 100 || tL_decryptedMessage.media.thumb_h > 100) {
                        tL_message_secret.media.document.thumb = new TL_photoSizeEmpty();
                        tL_message_secret.media.document.thumb.type = "s";
                    } else {
                        tL_message_secret.media.document.thumb = new TL_photoCachedSize();
                        tL_message_secret.media.document.thumb.bytes = bArr;
                        tL_message_secret.media.document.thumb.f2664w = tL_decryptedMessage.media.thumb_w;
                        tL_message_secret.media.document.thumb.f2663h = tL_decryptedMessage.media.thumb_h;
                        tL_message_secret.media.document.thumb.type = "s";
                        tL_message_secret.media.document.thumb.location = new TL_fileLocationUnavailable();
                    }
                    tL_message_secret.media.document.dc_id = encryptedFile.dc_id;
                    if (!MessageObject.isVoiceMessage(tL_message_secret)) {
                        return tL_message_secret;
                    }
                    tL_message_secret.media_unread = true;
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaExternalDocument) {
                    tL_message_secret.media = new TL_messageMediaDocument();
                    tL_message_secret.media.caption = TtmlNode.ANONYMOUS_REGION_ID;
                    tL_message_secret.media.document = new TL_document();
                    tL_message_secret.media.document.id = tL_decryptedMessage.media.id;
                    tL_message_secret.media.document.access_hash = tL_decryptedMessage.media.access_hash;
                    tL_message_secret.media.document.date = tL_decryptedMessage.media.date;
                    tL_message_secret.media.document.attributes = tL_decryptedMessage.media.attributes;
                    tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                    tL_message_secret.media.document.dc_id = tL_decryptedMessage.media.dc_id;
                    tL_message_secret.media.document.size = tL_decryptedMessage.media.size;
                    tL_message_secret.media.document.thumb = ((TL_decryptedMessageMediaExternalDocument) tL_decryptedMessage.media).thumb;
                    if (tL_message_secret.media.document.mime_type != null) {
                        return tL_message_secret;
                    }
                    tL_message_secret.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                    return tL_message_secret;
                } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaAudio) {
                    if (tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null || tL_decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    tL_message_secret.media = new TL_messageMediaDocument();
                    tL_message_secret.media.document = new TL_documentEncrypted();
                    tL_message_secret.media.document.key = tL_decryptedMessage.media.key;
                    tL_message_secret.media.document.iv = tL_decryptedMessage.media.iv;
                    tL_message_secret.media.document.id = encryptedFile.id;
                    tL_message_secret.media.document.access_hash = encryptedFile.access_hash;
                    tL_message_secret.media.document.date = i;
                    tL_message_secret.media.document.size = encryptedFile.size;
                    tL_message_secret.media.document.dc_id = encryptedFile.dc_id;
                    tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                    tL_message_secret.media.document.thumb = new TL_photoSizeEmpty();
                    tL_message_secret.media.document.thumb.type = "s";
                    tL_message_secret.media.caption = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    if (tL_message_secret.media.document.mime_type == null) {
                        tL_message_secret.media.document.mime_type = "audio/ogg";
                    }
                    TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                    tL_documentAttributeAudio.duration = tL_decryptedMessage.media.duration;
                    tL_documentAttributeAudio.voice = true;
                    tL_message_secret.media.document.attributes.add(tL_documentAttributeAudio);
                    if (tL_message_secret.ttl == 0) {
                        return tL_message_secret;
                    }
                    tL_message_secret.ttl = Math.max(tL_decryptedMessage.media.duration + 1, tL_message_secret.ttl);
                    return tL_message_secret;
                } else if (!(tL_decryptedMessage.media instanceof TL_decryptedMessageMediaVenue)) {
                    return null;
                } else {
                    tL_message_secret.media = new TL_messageMediaVenue();
                    tL_message_secret.media.geo = new TL_geoPoint();
                    tL_message_secret.media.geo.lat = tL_decryptedMessage.media.lat;
                    tL_message_secret.media.geo._long = tL_decryptedMessage.media._long;
                    tL_message_secret.media.title = tL_decryptedMessage.media.title;
                    tL_message_secret.media.address = tL_decryptedMessage.media.address;
                    tL_message_secret.media.provider = tL_decryptedMessage.media.provider;
                    tL_message_secret.media.venue_id = tL_decryptedMessage.media.venue_id;
                    return tL_message_secret;
                }
            } else if (tLObject instanceof TL_decryptedMessageService) {
                TL_decryptedMessageService tL_decryptedMessageService = (TL_decryptedMessageService) tLObject;
                if ((tL_decryptedMessageService.action instanceof TL_decryptedMessageActionSetMessageTTL) || (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionScreenshotMessages)) {
                    tL_message_secret = new TL_messageService();
                    if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionSetMessageTTL) {
                        tL_message_secret.action = new TL_messageEncryptedAction();
                        if (tL_decryptedMessageService.action.ttl_seconds < 0 || tL_decryptedMessageService.action.ttl_seconds > 31536000) {
                            tL_decryptedMessageService.action.ttl_seconds = 31536000;
                        }
                        encryptedChat.ttl = tL_decryptedMessageService.action.ttl_seconds;
                        tL_message_secret.action.encryptedAction = tL_decryptedMessageService.action;
                        MessagesStorage.getInstance().updateEncryptedChatTTL(encryptedChat);
                    } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionScreenshotMessages) {
                        tL_message_secret.action = new TL_messageEncryptedAction();
                        tL_message_secret.action.encryptedAction = tL_decryptedMessageService.action;
                    }
                    newMessageId = UserConfig.getNewMessageId();
                    tL_message_secret.id = newMessageId;
                    tL_message_secret.local_id = newMessageId;
                    UserConfig.saveConfig(false);
                    tL_message_secret.unread = true;
                    tL_message_secret.flags = TLRPC.USER_FLAG_UNUSED2;
                    tL_message_secret.date = i;
                    tL_message_secret.from_id = i2;
                    tL_message_secret.to_id = new TL_peerUser();
                    tL_message_secret.to_id.user_id = UserConfig.getClientUserId();
                    tL_message_secret.dialog_id = ((long) encryptedChat.id) << 32;
                    return tL_message_secret;
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionFlushHistory) {
                    AndroidUtilities.runOnUIThread(new C06406(((long) encryptedChat.id) << 32));
                    return null;
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionDeleteMessages) {
                    if (!tL_decryptedMessageService.action.random_ids.isEmpty()) {
                        this.pendingEncMessagesToDelete.addAll(tL_decryptedMessageService.action.random_ids);
                    }
                    return null;
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionReadMessages) {
                    if (!tL_decryptedMessageService.action.random_ids.isEmpty()) {
                        newMessageId = ConnectionsManager.getInstance().getCurrentTime();
                        MessagesStorage.getInstance().createTaskForSecretChat(encryptedChat.id, newMessageId, newMessageId, 1, tL_decryptedMessageService.action.random_ids);
                    }
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionNotifyLayer) {
                    applyPeerLayer(encryptedChat, tL_decryptedMessageService.action.layer);
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionRequestKey) {
                    if (encryptedChat.exchange_id != 0) {
                        if (encryptedChat.exchange_id > tL_decryptedMessageService.action.exchange_id) {
                            FileLog.m16e("tmessages", "we already have request key with higher exchange_id");
                            return null;
                        }
                        sendAbortKeyMessage(encryptedChat, null, encryptedChat.exchange_id);
                    }
                    r2 = new byte[TLRPC.USER_FLAG_UNUSED2];
                    Utilities.random.nextBytes(r2);
                    BigInteger bigInteger = new BigInteger(1, MessagesStorage.secretPBytes);
                    r0 = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, r2), bigInteger);
                    BigInteger bigInteger2 = new BigInteger(1, tL_decryptedMessageService.action.g_a);
                    if (Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
                        byte[] bArr2;
                        r1 = r0.toByteArray();
                        if (r1.length > TLRPC.USER_FLAG_UNUSED2) {
                            bArr = new byte[TLRPC.USER_FLAG_UNUSED2];
                            System.arraycopy(r1, 1, bArr, 0, TLRPC.USER_FLAG_UNUSED2);
                        } else {
                            r0 = r1;
                        }
                        Object toByteArray = bigInteger2.modPow(new BigInteger(1, r2), bigInteger).toByteArray();
                        if (toByteArray.length > TLRPC.USER_FLAG_UNUSED2) {
                            bArr2 = new byte[TLRPC.USER_FLAG_UNUSED2];
                            System.arraycopy(toByteArray, toByteArray.length + InputDeviceCompat.SOURCE_ANY, bArr2, 0, TLRPC.USER_FLAG_UNUSED2);
                        } else if (toByteArray.length < TLRPC.USER_FLAG_UNUSED2) {
                            r2 = new byte[TLRPC.USER_FLAG_UNUSED2];
                            System.arraycopy(toByteArray, 0, r2, 256 - toByteArray.length, toByteArray.length);
                            for (int i3 = 0; i3 < 256 - toByteArray.length; i3++) {
                                toByteArray[i3] = (byte) 0;
                            }
                            r1 = r2;
                        } else {
                            r1 = toByteArray;
                        }
                        r2 = Utilities.computeSHA1(bArr2);
                        toByteArray = new byte[8];
                        System.arraycopy(r2, r2.length - 8, toByteArray, 0, 8);
                        encryptedChat.exchange_id = tL_decryptedMessageService.action.exchange_id;
                        encryptedChat.future_auth_key = bArr2;
                        encryptedChat.future_key_fingerprint = Utilities.bytesToLong(toByteArray);
                        encryptedChat.g_a_or_b = bArr;
                        MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                        sendAcceptKeyMessage(encryptedChat, null);
                    } else {
                        sendAbortKeyMessage(encryptedChat, null, tL_decryptedMessageService.action.exchange_id);
                        return null;
                    }
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionAcceptKey) {
                    if (encryptedChat.exchange_id == tL_decryptedMessageService.action.exchange_id) {
                        r0 = new BigInteger(1, MessagesStorage.secretPBytes);
                        BigInteger bigInteger3 = new BigInteger(1, tL_decryptedMessageService.action.g_b);
                        if (Utilities.isGoodGaAndGb(bigInteger3, r0)) {
                            r2 = bigInteger3.modPow(new BigInteger(1, encryptedChat.a_or_b), r0).toByteArray();
                            if (r2.length > TLRPC.USER_FLAG_UNUSED2) {
                                bArr = new byte[TLRPC.USER_FLAG_UNUSED2];
                                System.arraycopy(r2, r2.length + InputDeviceCompat.SOURCE_ANY, bArr, 0, TLRPC.USER_FLAG_UNUSED2);
                            } else if (r2.length < TLRPC.USER_FLAG_UNUSED2) {
                                r1 = new byte[TLRPC.USER_FLAG_UNUSED2];
                                System.arraycopy(r2, 0, r1, 256 - r2.length, r2.length);
                                for (i2 = 0; i2 < 256 - r2.length; i2++) {
                                    r2[i2] = (byte) 0;
                                }
                                r0 = r1;
                            } else {
                                r0 = r2;
                            }
                            r1 = Utilities.computeSHA1(bArr);
                            r2 = new byte[8];
                            System.arraycopy(r1, r1.length - 8, r2, 0, 8);
                            long bytesToLong = Utilities.bytesToLong(r2);
                            if (tL_decryptedMessageService.action.key_fingerprint == bytesToLong) {
                                encryptedChat.future_auth_key = bArr;
                                encryptedChat.future_key_fingerprint = bytesToLong;
                                MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                                sendCommitKeyMessage(encryptedChat, null);
                            } else {
                                encryptedChat.future_auth_key = new byte[TLRPC.USER_FLAG_UNUSED2];
                                encryptedChat.future_key_fingerprint = 0;
                                encryptedChat.exchange_id = 0;
                                MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                                sendAbortKeyMessage(encryptedChat, null, tL_decryptedMessageService.action.exchange_id);
                            }
                        } else {
                            encryptedChat.future_auth_key = new byte[TLRPC.USER_FLAG_UNUSED2];
                            encryptedChat.future_key_fingerprint = 0;
                            encryptedChat.exchange_id = 0;
                            MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                            sendAbortKeyMessage(encryptedChat, null, tL_decryptedMessageService.action.exchange_id);
                            return null;
                        }
                    }
                    encryptedChat.future_auth_key = new byte[TLRPC.USER_FLAG_UNUSED2];
                    encryptedChat.future_key_fingerprint = 0;
                    encryptedChat.exchange_id = 0;
                    MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                    sendAbortKeyMessage(encryptedChat, null, tL_decryptedMessageService.action.exchange_id);
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionCommitKey) {
                    if (encryptedChat.exchange_id == tL_decryptedMessageService.action.exchange_id && encryptedChat.future_key_fingerprint == tL_decryptedMessageService.action.key_fingerprint) {
                        long j2 = encryptedChat.key_fingerprint;
                        r2 = encryptedChat.auth_key;
                        encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                        encryptedChat.auth_key = encryptedChat.future_auth_key;
                        encryptedChat.key_create_date = ConnectionsManager.getInstance().getCurrentTime();
                        encryptedChat.future_auth_key = r2;
                        encryptedChat.future_key_fingerprint = j2;
                        encryptedChat.key_use_count_in = (short) 0;
                        encryptedChat.key_use_count_out = (short) 0;
                        encryptedChat.exchange_id = 0;
                        MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                        sendNoopMessage(encryptedChat, null);
                    } else {
                        encryptedChat.future_auth_key = new byte[TLRPC.USER_FLAG_UNUSED2];
                        encryptedChat.future_key_fingerprint = 0;
                        encryptedChat.exchange_id = 0;
                        MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                        sendAbortKeyMessage(encryptedChat, null, tL_decryptedMessageService.action.exchange_id);
                    }
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionAbortKey) {
                    if (encryptedChat.exchange_id == tL_decryptedMessageService.action.exchange_id) {
                        encryptedChat.future_auth_key = new byte[TLRPC.USER_FLAG_UNUSED2];
                        encryptedChat.future_key_fingerprint = 0;
                        encryptedChat.exchange_id = 0;
                        MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
                    }
                } else if (!(tL_decryptedMessageService.action instanceof TL_decryptedMessageActionNoop)) {
                    if (!(tL_decryptedMessageService.action instanceof TL_decryptedMessageActionResend)) {
                        return null;
                    }
                    if (tL_decryptedMessageService.action.end_seq_no < encryptedChat.in_seq_no || tL_decryptedMessageService.action.end_seq_no < tL_decryptedMessageService.action.start_seq_no) {
                        return null;
                    }
                    if (tL_decryptedMessageService.action.start_seq_no < encryptedChat.in_seq_no) {
                        tL_decryptedMessageService.action.start_seq_no = encryptedChat.in_seq_no;
                    }
                    resendMessages(tL_decryptedMessageService.action.start_seq_no, tL_decryptedMessageService.action.end_seq_no, encryptedChat);
                }
            } else {
                FileLog.m16e("tmessages", "unknown message " + tLObject);
            }
        } else {
            FileLog.m16e("tmessages", "unknown TLObject");
        }
        return null;
    }

    protected void processPendingEncMessages() {
        if (!this.pendingEncMessagesToDelete.isEmpty()) {
            AndroidUtilities.runOnUIThread(new C06291(new ArrayList(this.pendingEncMessagesToDelete)));
            MessagesStorage.getInstance().markMessagesAsDeletedByRandoms(new ArrayList(this.pendingEncMessagesToDelete));
            this.pendingEncMessagesToDelete.clear();
        }
    }

    protected void processUpdateEncryption(TL_updateEncryption tL_updateEncryption, ConcurrentHashMap<Integer, User> concurrentHashMap) {
        EncryptedChat encryptedChat = tL_updateEncryption.chat;
        long j = ((long) encryptedChat.id) << 32;
        EncryptedChat encryptedChatDB = MessagesController.getInstance().getEncryptedChatDB(encryptedChat.id);
        if ((encryptedChat instanceof TL_encryptedChatRequested) && encryptedChatDB == null) {
            int i = encryptedChat.participant_id;
            int i2 = i == UserConfig.getClientUserId() ? encryptedChat.admin_id : i;
            User user = MessagesController.getInstance().getUser(Integer.valueOf(i2));
            if (user == null) {
                user = (User) concurrentHashMap.get(Integer.valueOf(i2));
            }
            encryptedChat.user_id = i2;
            TL_dialog tL_dialog = new TL_dialog();
            tL_dialog.id = j;
            tL_dialog.unread_count = 0;
            tL_dialog.top_message = 0;
            tL_dialog.last_message_date = tL_updateEncryption.date;
            AndroidUtilities.runOnUIThread(new C06302(tL_dialog, encryptedChat));
            MessagesStorage.getInstance().putEncryptedChat(encryptedChat, user, tL_dialog);
            getInstance().acceptSecretChat(encryptedChat);
        } else if (!(encryptedChat instanceof TL_encryptedChat)) {
            if (encryptedChatDB != null) {
                encryptedChat.user_id = encryptedChatDB.user_id;
                encryptedChat.auth_key = encryptedChatDB.auth_key;
                encryptedChat.key_create_date = encryptedChatDB.key_create_date;
                encryptedChat.key_use_count_in = encryptedChatDB.key_use_count_in;
                encryptedChat.key_use_count_out = encryptedChatDB.key_use_count_out;
                encryptedChat.ttl = encryptedChatDB.ttl;
                encryptedChat.seq_in = encryptedChatDB.seq_in;
                encryptedChat.seq_out = encryptedChatDB.seq_out;
            }
            AndroidUtilities.runOnUIThread(new C06313(encryptedChatDB, encryptedChat));
        } else if (encryptedChatDB != null && (encryptedChatDB instanceof TL_encryptedChatWaiting) && (encryptedChatDB.auth_key == null || encryptedChatDB.auth_key.length == 1)) {
            encryptedChat.a_or_b = encryptedChatDB.a_or_b;
            encryptedChat.user_id = encryptedChatDB.user_id;
            processAcceptedSecretChat(encryptedChat);
        } else if (encryptedChatDB == null && this.startingSecretChat) {
            this.delayedEncryptedChatUpdates.add(tL_updateEncryption);
        }
    }

    public void requestNewSecretChatKey(EncryptedChat encryptedChat) {
        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20) {
            byte[] bArr;
            byte[] bArr2 = new byte[TLRPC.USER_FLAG_UNUSED2];
            Utilities.random.nextBytes(bArr2);
            Object toByteArray = BigInteger.valueOf((long) MessagesStorage.secretG).modPow(new BigInteger(1, bArr2), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
            if (toByteArray.length > TLRPC.USER_FLAG_UNUSED2) {
                bArr = new byte[TLRPC.USER_FLAG_UNUSED2];
                System.arraycopy(toByteArray, 1, bArr, 0, TLRPC.USER_FLAG_UNUSED2);
            } else {
                Object obj = toByteArray;
            }
            encryptedChat.exchange_id = SendMessagesHelper.getInstance().getNextRandomId();
            encryptedChat.a_or_b = bArr2;
            encryptedChat.g_a = bArr;
            MessagesStorage.getInstance().updateEncryptedChat(encryptedChat);
            sendRequestKeyMessage(encryptedChat, null);
        }
    }

    public void sendAbortKeyMessage(EncryptedChat encryptedChat, Message message, long j) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionAbortKey();
                tL_decryptedMessageService.action.exchange_id = j;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendAcceptKeyMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionAcceptKey();
                tL_decryptedMessageService.action.exchange_id = encryptedChat.exchange_id;
                tL_decryptedMessageService.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                tL_decryptedMessageService.action.g_b = encryptedChat.g_a_or_b;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendClearHistoryMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionFlushHistory();
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendCommitKeyMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionCommitKey();
                tL_decryptedMessageService.action.exchange_id = encryptedChat.exchange_id;
                tL_decryptedMessageService.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendMessagesDeleteMessage(EncryptedChat encryptedChat, ArrayList<Long> arrayList, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionDeleteMessages();
                tL_decryptedMessageService.action.random_ids = arrayList;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendMessagesReadMessage(EncryptedChat encryptedChat, ArrayList<Long> arrayList, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionReadMessages();
                tL_decryptedMessageService.action.random_ids = arrayList;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendNoopMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionNoop();
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendNotifyLayerMessage(EncryptedChat encryptedChat, Message message) {
        if ((encryptedChat instanceof TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionNotifyLayer();
                tL_decryptedMessageService.action.layer = CURRENT_SECRET_CHAT_LAYER;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendRequestKeyMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionRequestKey();
                tL_decryptedMessageService.action.exchange_id = encryptedChat.exchange_id;
                tL_decryptedMessageService.action.g_a = encryptedChat.g_a;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendScreenshotMessage(EncryptedChat encryptedChat, ArrayList<Long> arrayList, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionScreenshotMessages();
                tL_decryptedMessageService.action.random_ids = arrayList;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
                MessageObject messageObject = new MessageObject(message2, null, false);
                messageObject.messageOwner.send_state = 1;
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(messageObject);
                MessagesController.getInstance().updateInterfaceWithMessages(message2.dialog_id, arrayList2);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendTTLMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService;
            Message message2;
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                tL_decryptedMessageService = new TL_decryptedMessageService();
            } else {
                tL_decryptedMessageService = new TL_decryptedMessageService_layer8();
                tL_decryptedMessageService.random_bytes = new byte[15];
                Utilities.random.nextBytes(tL_decryptedMessageService.random_bytes);
            }
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
                message2 = message;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionSetMessageTTL();
                tL_decryptedMessageService.action.ttl_seconds = encryptedChat.ttl;
                message2 = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
                MessageObject messageObject = new MessageObject(message2, null, false);
                messageObject.messageOwner.send_state = 1;
                ArrayList arrayList = new ArrayList();
                arrayList.add(messageObject);
                MessagesController.getInstance().updateInterfaceWithMessages(message2.dialog_id, arrayList);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void startSecretChat(Context context, User user) {
        if (user != null && context != null) {
            this.startingSecretChat = true;
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            TLObject tL_messages_getDhConfig = new TL_messages_getDhConfig();
            tL_messages_getDhConfig.random_length = TLRPC.USER_FLAG_UNUSED2;
            tL_messages_getDhConfig.version = MessagesStorage.lastSecretVersion;
            progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new AnonymousClass15(ConnectionsManager.getInstance().sendRequest(tL_messages_getDhConfig, new AnonymousClass14(context, progressDialog, user), 2)));
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }
}
