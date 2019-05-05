package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.GridLayoutManager;
import com.hanista.mobogram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.StickerEmojiCell;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate;
import com.hanista.mobogram.ui.StickerPreviewViewer;
import java.util.ArrayList;
import java.util.HashMap;

public class StickerMasksView extends FrameLayout implements NotificationCenterDelegate {
    private int currentType;
    private int lastNotifyWidth;
    private Listener listener;
    private ArrayList<Document>[] recentStickers;
    private int recentTabBum;
    private ScrollSlidingTabStrip scrollSlidingTabStrip;
    private ArrayList<TL_messages_stickerSet>[] stickerSets;
    private TextView stickersEmptyView;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private OnItemClickListener stickersOnItemClickListener;
    private int stickersTabOffset;

    public interface Listener {
        void onStickerSelected(Document document);

        void onTypeChanged();
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.1 */
    class C14721 extends RecyclerListView {
        C14721(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.2 */
    class C14732 extends SpanSizeLookup {
        C14732() {
        }

        public int getSpanSize(int i) {
            return i == StickerMasksView.this.stickersGridAdapter.totalItems ? StickerMasksView.this.stickersGridAdapter.stickersPerRow : 1;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.3 */
    class C14743 implements OnTouchListener {
        C14743() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return StickerPreviewViewer.getInstance().onTouch(motionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), StickerMasksView.this.stickersOnItemClickListener);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.4 */
    class C14754 implements OnItemClickListener {
        C14754() {
        }

        public void onItemClick(View view, int i) {
            if (view instanceof StickerEmojiCell) {
                StickerPreviewViewer.getInstance().reset();
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                if (!stickerEmojiCell.isDisabled()) {
                    Document sticker = stickerEmojiCell.getSticker();
                    StickerMasksView.this.listener.onStickerSelected(sticker);
                    StickersQuery.addRecentSticker(1, sticker, (int) (System.currentTimeMillis() / 1000));
                    MessagesController.getInstance().saveRecentSticker(sticker, true);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.5 */
    class C14765 implements ScrollSlidingTabStripDelegate {
        C14765() {
        }

        public void onPageSelected(int i) {
            if (i == 0) {
                if (StickerMasksView.this.currentType == 0) {
                    StickerMasksView.this.currentType = 1;
                } else {
                    StickerMasksView.this.currentType = 0;
                }
                if (StickerMasksView.this.listener != null) {
                    StickerMasksView.this.listener.onTypeChanged();
                }
                StickerMasksView.this.recentStickers[StickerMasksView.this.currentType] = StickersQuery.getRecentStickers(StickerMasksView.this.currentType);
                StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                StickerMasksView.this.updateStickerTabs();
                StickerMasksView.this.reloadStickersAdapter();
                StickerMasksView.this.checkDocuments();
                StickerMasksView.this.checkPanels();
            } else if (i == StickerMasksView.this.recentTabBum + 1) {
                StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                int access$1400 = (i - 1) - StickerMasksView.this.stickersTabOffset;
                if (access$1400 >= StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size()) {
                    access$1400 = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size() - 1;
                }
                StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(StickerMasksView.this.stickersGridAdapter.getPositionForPack((TL_messages_stickerSet) StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].get(access$1400)), 0);
                StickerMasksView.this.checkScroll();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.6 */
    class C14776 extends OnScrollListener {
        C14776() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            StickerMasksView.this.checkScroll();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.7 */
    class C14787 implements Runnable {
        C14787() {
        }

        public void run() {
            StickerMasksView.this.updateStickerTabs();
            StickerMasksView.this.reloadStickersAdapter();
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    private class StickersGridAdapter extends Adapter {
        private HashMap<Integer, Document> cache;
        private Context context;
        private HashMap<TL_messages_stickerSet, Integer> packStartRow;
        private HashMap<Integer, TL_messages_stickerSet> rowStartPack;
        private int stickersPerRow;
        private int totalItems;

        /* renamed from: com.hanista.mobogram.ui.Components.StickerMasksView.StickersGridAdapter.1 */
        class C14791 extends StickerEmojiCell {
            C14791(Context context) {
                super(context);
            }

            public void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C0700C.ENCODING_PCM_32BIT));
            }
        }

        public StickersGridAdapter(Context context) {
            this.rowStartPack = new HashMap();
            this.packStartRow = new HashMap();
            this.cache = new HashMap();
            this.context = context;
        }

        public Object getItem(int i) {
            return this.cache.get(Integer.valueOf(i));
        }

        public int getItemCount() {
            return this.totalItems != 0 ? this.totalItems + 1 : 0;
        }

        public int getItemViewType(int i) {
            return this.cache.get(Integer.valueOf(i)) != null ? 0 : 1;
        }

        public int getPositionForPack(TL_messages_stickerSet tL_messages_stickerSet) {
            return ((Integer) this.packStartRow.get(tL_messages_stickerSet)).intValue() * this.stickersPerRow;
        }

        public int getTabForPosition(int i) {
            if (this.stickersPerRow == 0) {
                int measuredWidth = StickerMasksView.this.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf(i / this.stickersPerRow));
            return tL_messages_stickerSet == null ? StickerMasksView.this.recentTabBum : StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].indexOf(tL_messages_stickerSet) + StickerMasksView.this.stickersTabOffset;
        }

        public void notifyDataSetChanged() {
            int measuredWidth = StickerMasksView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            StickerMasksView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList arrayList = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType];
            for (int i = -1; i < arrayList.size(); i++) {
                ArrayList arrayList2;
                Object obj = null;
                int i2 = this.totalItems / this.stickersPerRow;
                if (i == -1) {
                    arrayList2 = StickerMasksView.this.recentStickers[StickerMasksView.this.currentType];
                } else {
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) arrayList.get(i);
                    arrayList2 = tL_messages_stickerSet.documents;
                    this.packStartRow.put(tL_messages_stickerSet, Integer.valueOf(i2));
                }
                if (!arrayList2.isEmpty()) {
                    int ceil = (int) Math.ceil((double) (((float) arrayList2.size()) / ((float) this.stickersPerRow)));
                    for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                        this.cache.put(Integer.valueOf(this.totalItems + i3), arrayList2.get(i3));
                    }
                    this.totalItems += this.stickersPerRow * ceil;
                    for (int i4 = 0; i4 < ceil; i4++) {
                        this.rowStartPack.put(Integer.valueOf(i2 + i4), obj);
                    }
                }
            }
            super.notifyDataSetChanged();
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(Integer.valueOf(i)), false);
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    if (i == this.totalItems) {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf((i - 1) / this.stickersPerRow));
                        if (tL_messages_stickerSet == null) {
                            ((EmptyCell) viewHolder.itemView).setHeight(1);
                            return;
                        }
                        int measuredHeight = StickerMasksView.this.stickersGridView.getMeasuredHeight() - (((int) Math.ceil((double) (((float) tL_messages_stickerSet.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                        EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                        if (measuredHeight <= 0) {
                            measuredHeight = 1;
                        }
                        emptyCell.setHeight(measuredHeight);
                        return;
                    }
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                default:
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new C14791(this.context);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new EmptyCell(this.context);
                    break;
            }
            return new Holder(view);
        }
    }

    public StickerMasksView(Context context) {
        super(context);
        this.stickerSets = new ArrayList[]{new ArrayList(), new ArrayList()};
        this.recentStickers = new ArrayList[]{new ArrayList(), new ArrayList()};
        this.currentType = 1;
        this.recentTabBum = -2;
        setBackgroundColor(Theme.REPLY_PANEL_MESSAGE_TEXT_COLOR);
        setClickable(true);
        StickersQuery.checkStickers(0);
        StickersQuery.checkStickers(1);
        this.stickersGridView = new C14721(context);
        RecyclerListView recyclerListView = this.stickersGridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
        this.stickersLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new C14732());
        this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        this.stickersGridView.setClipToPadding(false);
        recyclerListView = this.stickersGridView;
        Adapter stickersGridAdapter = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter;
        recyclerListView.setAdapter(stickersGridAdapter);
        this.stickersGridView.setOnTouchListener(new C14743());
        this.stickersOnItemClickListener = new C14754();
        this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
        this.stickersGridView.setGlowColor(-657673);
        addView(this.stickersGridView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.stickersEmptyView = new TextView(context);
        this.stickersEmptyView.setTextSize(1, 18.0f);
        this.stickersEmptyView.setTextColor(-7829368);
        addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
        this.stickersGridView.setEmptyView(this.stickersEmptyView);
        this.scrollSlidingTabStrip = new ScrollSlidingTabStrip(context);
        this.scrollSlidingTabStrip.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.scrollSlidingTabStrip.setIndicatorColor(-10305560);
        this.scrollSlidingTabStrip.setUnderlineColor(-15066598);
        this.scrollSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) + 1);
        addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
        updateStickerTabs();
        this.scrollSlidingTabStrip.setDelegate(new C14765());
        this.stickersGridView.setOnScrollListener(new C14776());
    }

    private void checkDocuments() {
        int size = this.recentStickers[this.currentType].size();
        this.recentStickers[this.currentType] = StickersQuery.getRecentStickers(this.currentType);
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (size != this.recentStickers[this.currentType].size()) {
            updateStickerTabs();
        }
    }

    private void checkPanels() {
        if (this.scrollSlidingTabStrip != null) {
            int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1) {
                this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
            }
        }
    }

    private void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1) {
            checkStickersScroll(findFirstVisibleItemPosition);
        }
    }

    private void checkStickersScroll(int i) {
        if (this.stickersGridView != null) {
            this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(i) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        }
    }

    private void reloadStickersAdapter() {
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (StickerPreviewViewer.getInstance().isVisible()) {
            StickerPreviewViewer.getInstance().close();
        }
        StickerPreviewViewer.getInstance().reset();
    }

    private void updateStickerTabs() {
        if (this.scrollSlidingTabStrip != null) {
            int i;
            this.recentTabBum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = this.scrollSlidingTabStrip.getCurrentPosition();
            this.scrollSlidingTabStrip.removeTabs();
            if (this.currentType == 0) {
                this.scrollSlidingTabStrip.addIconTab(C0338R.drawable.ic_masks_msk1);
                this.stickersEmptyView.setText(LocaleController.getString("NoStickers", C0338R.string.NoStickers));
            } else {
                this.scrollSlidingTabStrip.addIconTab(C0338R.drawable.ic_masks_sticker1);
                this.stickersEmptyView.setText(LocaleController.getString("NoMasks", C0338R.string.NoMasks));
            }
            if (!this.recentStickers[this.currentType].isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.scrollSlidingTabStrip.addIconTab(C0338R.drawable.ic_masks_recent);
            }
            this.stickerSets[this.currentType].clear();
            ArrayList stickerSets = StickersQuery.getStickerSets(this.currentType);
            for (i = 0; i < stickerSets.size(); i++) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i);
                if (!(tL_messages_stickerSet.set.archived || tL_messages_stickerSet.documents == null || tL_messages_stickerSet.documents.isEmpty())) {
                    this.stickerSets[this.currentType].add(tL_messages_stickerSet);
                }
            }
            for (i = 0; i < this.stickerSets[this.currentType].size(); i++) {
                this.scrollSlidingTabStrip.addStickerTab((Document) ((TL_messages_stickerSet) this.stickerSets[this.currentType].get(i)).documents.get(0), null, null);
            }
            this.scrollSlidingTabStrip.updateTabStyles();
            if (currentPosition != 0) {
                this.scrollSlidingTabStrip.onPageScrolled(currentPosition, currentPosition);
            }
            checkPanels();
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            StickersQuery.addRecentSticker(this.currentType, document, (int) (System.currentTimeMillis() / 1000));
            boolean isEmpty = this.recentStickers[this.currentType].isEmpty();
            this.recentStickers[this.currentType] = StickersQuery.getRecentStickers(this.currentType);
            if (this.stickersGridAdapter != null) {
                this.stickersGridAdapter.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoaded) {
            if (((Integer) objArr[0]).intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoaded && !((Boolean) objArr[0]).booleanValue() && ((Integer) objArr[1]).intValue() == this.currentType) {
            checkDocuments();
        }
    }

    public int getCurrentType() {
        return this.currentType;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
        AndroidUtilities.runOnUIThread(new C14787());
    }

    public void onDestroy() {
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.lastNotifyWidth != i3 - i) {
            this.lastNotifyWidth = i3 - i;
            reloadStickersAdapter();
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
            updateStickerTabs();
            reloadStickersAdapter();
            checkDocuments();
            StickersQuery.loadRecents(0, false, true);
            StickersQuery.loadRecents(1, false, true);
        }
    }
}
