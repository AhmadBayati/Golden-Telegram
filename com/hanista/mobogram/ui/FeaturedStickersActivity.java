package com.hanista.mobogram.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.FeaturedStickerSetCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.StickersAlert;
import com.hanista.mobogram.ui.Components.StickersAlert.StickersAlertInstallDelegate;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class FeaturedStickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private HashMap<Long, StickerSetCovered> installingStickerSets;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private int rowCount;
    private int stickersEndRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private ArrayList<Long> unreadStickers;

    /* renamed from: com.hanista.mobogram.ui.FeaturedStickersActivity.1 */
    class C15581 extends ActionBarMenuOnItemClick {
        C15581() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                FeaturedStickersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.FeaturedStickersActivity.2 */
    class C15592 extends LinearLayoutManager {
        C15592(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.FeaturedStickersActivity.3 */
    class C15613 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.FeaturedStickersActivity.3.1 */
        class C15601 implements StickersAlertInstallDelegate {
            final /* synthetic */ StickerSetCovered val$stickerSet;
            final /* synthetic */ View val$view;

            C15601(View view, StickerSetCovered stickerSetCovered) {
                this.val$view = view;
                this.val$stickerSet = stickerSetCovered;
            }

            public void onStickerSetInstalled() {
                ((FeaturedStickerSetCell) this.val$view).setDrawProgress(true);
                FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(this.val$stickerSet.set.id), this.val$stickerSet);
            }

            public void onStickerSetUninstalled() {
            }
        }

        C15613() {
        }

        public void onItemClick(View view, int i) {
            if (i >= FeaturedStickersActivity.this.stickersStartRow && i < FeaturedStickersActivity.this.stickersEndRow && FeaturedStickersActivity.this.getParentActivity() != null) {
                InputStickerSet tL_inputStickerSetID;
                StickerSetCovered stickerSetCovered = (StickerSetCovered) StickersQuery.getFeaturedStickerSets().get(i);
                if (stickerSetCovered.set.id != 0) {
                    tL_inputStickerSetID = new TL_inputStickerSetID();
                    tL_inputStickerSetID.id = stickerSetCovered.set.id;
                } else {
                    tL_inputStickerSetID = new TL_inputStickerSetShortName();
                    tL_inputStickerSetID.short_name = stickerSetCovered.set.short_name;
                }
                tL_inputStickerSetID.access_hash = stickerSetCovered.set.access_hash;
                Dialog stickersAlert = new StickersAlert(FeaturedStickersActivity.this.getParentActivity(), FeaturedStickersActivity.this, tL_inputStickerSetID, null, null);
                stickersAlert.setInstallDelegate(new C15601(view, stickerSetCovered));
                FeaturedStickersActivity.this.showDialog(stickersAlert);
            }
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.FeaturedStickersActivity.ListAdapter.1 */
        class C15621 implements OnClickListener {
            C15621() {
            }

            public void onClick(View view) {
                FeaturedStickerSetCell featuredStickerSetCell = (FeaturedStickerSetCell) view.getParent();
                StickerSetCovered stickerSet = featuredStickerSetCell.getStickerSet();
                if (!FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(stickerSet.set.id))) {
                    FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(stickerSet.set.id), stickerSet);
                    StickersQuery.removeStickersSet(FeaturedStickersActivity.this.getParentActivity(), stickerSet.set, 2, FeaturedStickersActivity.this, false);
                    featuredStickerSetCell.setDrawProgress(true);
                }
            }
        }

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FeaturedStickersActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            return ((i < FeaturedStickersActivity.this.stickersStartRow || i >= FeaturedStickersActivity.this.stickersEndRow) && i == FeaturedStickersActivity.this.stickersShadowRow) ? 1 : 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            boolean z2 = false;
            if (getItemViewType(i) == 0) {
                ArrayList featuredStickerSets = StickersQuery.getFeaturedStickerSets();
                FeaturedStickerSetCell featuredStickerSetCell = (FeaturedStickerSetCell) viewHolder.itemView;
                featuredStickerSetCell.setTag(Integer.valueOf(i));
                StickerSetCovered stickerSetCovered = (StickerSetCovered) featuredStickerSets.get(i);
                boolean z3 = i != featuredStickerSets.size() + -1;
                if (FeaturedStickersActivity.this.unreadStickers == null || !FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id))) {
                    z = false;
                }
                featuredStickerSetCell.setStickersSet(stickerSetCovered, z3, z);
                z3 = FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(stickerSetCovered.set.id));
                if (z3 && featuredStickerSetCell.isInstalled()) {
                    FeaturedStickersActivity.this.installingStickerSets.remove(Long.valueOf(stickerSetCovered.set.id));
                    featuredStickerSetCell.setDrawProgress(false);
                } else {
                    z2 = z3;
                }
                featuredStickerSetCell.setDrawProgress(z2);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new FeaturedStickerSetCell(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.list_selector_white);
                    ((FeaturedStickerSetCell) view).setAddOnClickListener(new C15621());
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }
    }

    public FeaturedStickersActivity() {
        this.unreadStickers = null;
        this.installingStickerSets = new HashMap();
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList featuredStickerSets = StickersQuery.getFeaturedStickerSets();
        if (featuredStickerSets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        } else {
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + featuredStickerSets.size();
            this.rowCount = featuredStickerSets.size() + this.rowCount;
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersShadowRow = i;
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        StickersQuery.markFaturedStickersAsRead(true);
    }

    private void updateVisibleTrendingSets() {
        if (this.layoutManager != null) {
            int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1) {
                int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
                if (findLastVisibleItemPosition != -1) {
                    this.listAdapter.notifyItemRangeChanged(findFirstVisibleItemPosition, (findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1);
                }
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", C0338R.string.FeaturedStickers));
        this.actionBar.setActionBarMenuOnItemClick(new C15581());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        View recyclerListView = new RecyclerListView(context);
        recyclerListView.setItemAnimator(null);
        recyclerListView.setLayoutAnimation(null);
        recyclerListView.setFocusable(true);
        recyclerListView.setTag(Integer.valueOf(14));
        this.layoutManager = new C15592(context);
        this.layoutManager.setOrientation(1);
        recyclerListView.setLayoutManager(this.layoutManager);
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        recyclerListView.setAdapter(this.listAdapter);
        recyclerListView.setOnItemClickListener(new C15613());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.featuredStickersDidLoaded) {
            if (this.unreadStickers == null) {
                this.unreadStickers = StickersQuery.getUnreadStickerSets();
            }
            updateRows();
        } else if (i == NotificationCenter.stickersDidLoaded) {
            updateVisibleTrendingSets();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        StickersQuery.checkFeaturedStickers();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        Collection unreadStickerSets = StickersQuery.getUnreadStickerSets();
        if (unreadStickerSets != null) {
            this.unreadStickers = new ArrayList(unreadStickerSets);
        }
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
