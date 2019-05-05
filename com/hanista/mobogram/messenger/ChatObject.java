package com.hanista.mobogram.messenger;

import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_channel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatForbidden;

public class ChatObject {
    public static final int CHAT_TYPE_BROADCAST = 1;
    public static final int CHAT_TYPE_CHANNEL = 2;
    public static final int CHAT_TYPE_CHAT = 0;
    public static final int CHAT_TYPE_MEGAGROUP = 4;
    public static final int CHAT_TYPE_USER = 3;

    public static boolean canWriteToChat(Chat chat) {
        return !isChannel(chat) || chat.creator || chat.editor || !chat.broadcast;
    }

    public static Chat getChatByDialog(long j) {
        int i = (int) j;
        int i2 = (int) (j >> 32);
        return i < 0 ? MessagesController.getInstance().getChat(Integer.valueOf(-i)) : null;
    }

    public static boolean isCanWriteToChannel(int i) {
        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(i));
        return chat != null && (chat.creator || chat.editor || chat.megagroup);
    }

    public static boolean isChannel(int i) {
        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(i));
        return (chat instanceof TL_channel) || (chat instanceof TL_channelForbidden);
    }

    public static boolean isChannel(Chat chat) {
        return (chat instanceof TL_channel) || (chat instanceof TL_channelForbidden);
    }

    public static boolean isKickedFromChat(Chat chat) {
        return chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.kicked || chat.deactivated;
    }

    public static boolean isLeftFromChat(Chat chat) {
        return chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.left || chat.deactivated;
    }

    public static boolean isNotInChat(Chat chat) {
        return chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.left || chat.kicked || chat.deactivated;
    }
}
