package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.ui.Cells.StickerCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class StickersAdapter extends Adapter implements NotificationCenterDelegate {
    private StickersAdapterDelegate delegate;
    private String lastSticker;
    private Context mContext;
    private ArrayList<Document> stickers;
    private ArrayList<String> stickersToLoad;
    private boolean visible;

    /* renamed from: com.hanista.mobogram.ui.Adapters.StickersAdapter.1 */
    class C10421 implements Comparator<Document> {
        final /* synthetic */ ArrayList val$recentStickers;

        C10421(ArrayList arrayList) {
            this.val$recentStickers = arrayList;
        }

        private int getIndex(long j) {
            for (int i = 0; i < this.val$recentStickers.size(); i++) {
                if (((Document) this.val$recentStickers.get(i)).id == j) {
                    return i;
                }
            }
            return -1;
        }

        public int compare(Document document, Document document2) {
            int index = getIndex(document.id);
            int index2 = getIndex(document2.id);
            return index > index2 ? -1 : index < index2 ? 1 : 0;
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    public StickersAdapter(Context context, StickersAdapterDelegate stickersAdapterDelegate) {
        this.stickersToLoad = new ArrayList();
        this.mContext = context;
        this.delegate = stickersAdapterDelegate;
        StickersQuery.checkStickers(0);
        StickersQuery.checkStickers(1);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    private boolean checkStickerFilesExistAndDownload() {
        if (this.stickers == null) {
            return false;
        }
        this.stickersToLoad.clear();
        int min = Math.min(10, this.stickers.size());
        for (int i = 0; i < min; i++) {
            Document document = (Document) this.stickers.get(i);
            if (!FileLoader.getPathToAttach(document.thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(document.thumb, "webp"));
                FileLoader.getInstance().loadFile(document.thumb.location, "webp", 0, true);
            }
        }
        return this.stickersToLoad.isEmpty();
    }

    public void clearStickers() {
        this.lastSticker = null;
        this.stickers = null;
        this.stickersToLoad.clear();
        notifyDataSetChanged();
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if ((i == NotificationCenter.FileDidLoaded || i == NotificationCenter.FileDidFailedLoad) && this.stickers != null && !this.stickers.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
            this.stickersToLoad.remove((String) objArr[0]);
            if (this.stickersToLoad.isEmpty()) {
                StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                boolean z = (this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty()) ? false : true;
                stickersAdapterDelegate.needChangePanelVisibility(z);
            }
        }
    }

    public Document getItem(int i) {
        return (this.stickers == null || i < 0 || i >= this.stickers.size()) ? null : (Document) this.stickers.get(i);
    }

    public int getItemCount() {
        return this.stickers != null ? this.stickers.size() : 0;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public void loadStikersForEmoji(CharSequence charSequence) {
        boolean z = charSequence != null && charSequence.length() > 0 && charSequence.length() <= 14;
        if (z) {
            int length = charSequence.length();
            int i = 0;
            CharSequence charSequence2 = charSequence;
            while (i < length) {
                if (i < length - 1 && ((charSequence2.charAt(i) == '\ud83c' && charSequence2.charAt(i + 1) >= '\udffb' && charSequence2.charAt(i + 1) <= '\udfff') || (charSequence2.charAt(i) == '\u200d' && (charSequence2.charAt(i + 1) == '\u2640' || charSequence2.charAt(i + 1) == '\u2642')))) {
                    charSequence2 = TextUtils.concat(new CharSequence[]{charSequence2.subSequence(0, i), charSequence2.subSequence(i + 2, charSequence2.length())});
                    length -= 2;
                    i--;
                } else if (charSequence2.charAt(i) == '\ufe0f') {
                    charSequence2 = TextUtils.concat(new CharSequence[]{charSequence2.subSequence(0, i), charSequence2.subSequence(i + 1, charSequence2.length())});
                    length--;
                    i--;
                }
                i++;
            }
            this.lastSticker = charSequence2.toString();
            HashMap allStickers = StickersQuery.getAllStickers();
            if (allStickers != null) {
                ArrayList arrayList = (ArrayList) allStickers.get(this.lastSticker);
                if (this.stickers == null || arrayList != null) {
                    arrayList = (arrayList == null || arrayList.isEmpty()) ? null : new ArrayList(arrayList);
                    this.stickers = arrayList;
                    if (this.stickers != null) {
                        arrayList = StickersQuery.getRecentStickersNoCopy(0);
                        if (!arrayList.isEmpty()) {
                            Collections.sort(this.stickers, new C10421(arrayList));
                        }
                    }
                    checkStickerFilesExistAndDownload();
                    StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                    boolean z2 = (this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty()) ? false : true;
                    stickersAdapterDelegate.needChangePanelVisibility(z2);
                    notifyDataSetChanged();
                    this.visible = true;
                } else if (this.visible) {
                    this.delegate.needChangePanelVisibility(false);
                    this.visible = false;
                }
            }
        }
        if (!z && this.visible && this.stickers != null) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int i2 = i == 0 ? this.stickers.size() == 1 ? 2 : -1 : i == this.stickers.size() + -1 ? 1 : 0;
        ((StickerCell) viewHolder.itemView).setSticker((Document) this.stickers.get(i), i2);
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(new StickerCell(this.mContext));
    }

    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
    }
}
