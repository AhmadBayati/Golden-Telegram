package com.hanista.mobogram.tgnet;

import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.tgnet.TLRPC.TL_config;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageLayer;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageService;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageService_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessage_layer17;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessage_layer8;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret;
import com.hanista.mobogram.tgnet.TLRPC.TL_message_secret_old;
import com.hanista.mobogram.tgnet.TLRPC.TL_null;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateShort;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateShortChatMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateShortMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateShortSentMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates;
import com.hanista.mobogram.tgnet.TLRPC.TL_updatesCombined;
import com.hanista.mobogram.tgnet.TLRPC.TL_updatesTooLong;
import java.util.HashMap;

public class TLClassStore {
    static TLClassStore store;
    private HashMap<Integer, Class> classStore;

    static {
        store = null;
    }

    public TLClassStore() {
        this.classStore = new HashMap();
        this.classStore.put(Integer.valueOf(TL_error.constructor), TL_error.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessageService.constructor), TL_decryptedMessageService.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessage.constructor), TL_decryptedMessage.class);
        this.classStore.put(Integer.valueOf(TL_config.constructor), TL_config.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessageLayer.constructor), TL_decryptedMessageLayer.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessage_layer17.constructor), TL_decryptedMessage.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessageService_layer8.constructor), TL_decryptedMessageService_layer8.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessage_layer8.constructor), TL_decryptedMessage_layer8.class);
        this.classStore.put(Integer.valueOf(TL_message_secret.constructor), TL_message_secret.class);
        this.classStore.put(Integer.valueOf(TL_message_secret_old.constructor), TL_message_secret_old.class);
        this.classStore.put(Integer.valueOf(TL_messageEncryptedAction.constructor), TL_messageEncryptedAction.class);
        this.classStore.put(Integer.valueOf(TL_null.constructor), TL_null.class);
        this.classStore.put(Integer.valueOf(TL_updateShortChatMessage.constructor), TL_updateShortChatMessage.class);
        this.classStore.put(Integer.valueOf(TL_updates.constructor), TL_updates.class);
        this.classStore.put(Integer.valueOf(TL_updateShortMessage.constructor), TL_updateShortMessage.class);
        this.classStore.put(Integer.valueOf(TL_updateShort.constructor), TL_updateShort.class);
        this.classStore.put(Integer.valueOf(TL_updatesCombined.constructor), TL_updatesCombined.class);
        this.classStore.put(Integer.valueOf(TL_updateShortSentMessage.constructor), TL_updateShortSentMessage.class);
        this.classStore.put(Integer.valueOf(TL_updatesTooLong.constructor), TL_updatesTooLong.class);
    }

    public static TLClassStore Instance() {
        if (store == null) {
            store = new TLClassStore();
        }
        return store;
    }

    public TLObject TLdeserialize(NativeByteBuffer nativeByteBuffer, int i, boolean z) {
        Class cls = (Class) this.classStore.get(Integer.valueOf(i));
        if (cls == null) {
            return null;
        }
        try {
            TLObject tLObject = (TLObject) cls.newInstance();
            tLObject.readParams(nativeByteBuffer, z);
            return tLObject;
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
            return null;
        }
    }
}
