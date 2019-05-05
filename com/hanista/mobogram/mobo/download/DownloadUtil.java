package com.hanista.mobogram.mobo.download;

import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import java.io.File;

/* renamed from: com.hanista.mobogram.mobo.download.d */
public class DownloadUtil {
    public static boolean m824a(MessageObject messageObject) {
        return (messageObject == null || DownloadUtil.m825b(messageObject) != 2 || messageObject.messageOwner.media == null) ? false : (messageObject.isMusic() || (messageObject.messageOwner.media instanceof TL_messageMediaDocument) || (messageObject.messageOwner.media instanceof TL_messageMediaPhoto)) && !messageObject.isSticker();
    }

    public static int m825b(MessageObject messageObject) {
        Object obj = 1;
        if (messageObject == null) {
            return -1;
        }
        Object obj2 = (messageObject.getId() > 0 || !messageObject.isSendError()) ? null : 1;
        if (obj2 != null) {
            return messageObject.isSendError() ? !messageObject.isMediaEmpty() ? 0 : 20 : -1;
        } else {
            if (messageObject.type == 6) {
                return -1;
            }
            if (messageObject.type == 10 || messageObject.type == 11) {
                return messageObject.getId() != 0 ? 1 : -1;
            } else {
                if (messageObject.isMediaEmpty()) {
                    return 3;
                }
                if (messageObject.isVoice()) {
                    return 2;
                }
                if (messageObject.isSticker()) {
                    InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                    if (inputStickerSet instanceof TL_inputStickerSetID) {
                        if (!StickersQuery.isStickerPackInstalled(inputStickerSet.id)) {
                            return 7;
                        }
                    } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                        return 7;
                    }
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || (messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                    Object obj3 = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists()) ? null : 1;
                    if (!(obj3 == null && FileLoader.getPathToMessage(messageObject.messageOwner).exists())) {
                        obj = obj3;
                    }
                    if (obj != null) {
                        if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                            String str = messageObject.messageOwner.media.document.mime_type;
                            if (str != null) {
                                if (str.endsWith("/xml")) {
                                    return 5;
                                }
                                if (str.endsWith("/png") || str.endsWith("/jpg") || str.endsWith("/jpeg")) {
                                    return 6;
                                }
                            }
                        }
                        return 4;
                    }
                }
                return 2;
            }
        }
    }
}
