package com.hanista.mobogram.mobo.p006g;

import android.content.Context;
import android.widget.FrameLayout;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.ui.Components.EmojiView.Listener;

/* renamed from: com.hanista.mobogram.mobo.g.d */
public abstract class EmojiViewInf extends FrameLayout {
    public EmojiViewInf(Context context) {
        super(context);
    }

    public abstract void addRecentGif(Document document);

    public abstract void addRecentSticker(Document document);

    public abstract void clearRecentEmoji();

    public abstract int getCurrentPage();

    public abstract void invalidateViews();

    public abstract void onDestroy();

    public abstract void onOpen(boolean z);

    public abstract void setListener(Listener listener);

    public abstract void switchToGifRecent();
}
