package com.hanista.mobogram.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_archivedStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.ArchivedStickerSetCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Components.EmptyTextProgressView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.StickersAlert;
import com.hanista.mobogram.ui.Components.StickersAlert.StickersAlertInstallDelegate;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;

public class ArchivedStickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int currentType;
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private boolean loadingStickers;
    private int rowCount;
    private ArrayList<StickerSetCovered> sets;
    private int stickersEndRow;
    private int stickersLoadingRow;
    private int stickersShadowRow;
    private int stickersStartRow;

    /* renamed from: com.hanista.mobogram.ui.ArchivedStickersActivity.1 */
    class C10431 extends ActionBarMenuOnItemClick {
        C10431() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ArchivedStickersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ArchivedStickersActivity.2 */
    class C10452 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.ArchivedStickersActivity.2.1 */
        class C10441 implements StickersAlertInstallDelegate {
            final /* synthetic */ View val$view;

            C10441(View view) {
                this.val$view = view;
            }

            public void onStickerSetInstalled() {
                ((ArchivedStickerSetCell) this.val$view).setChecked(true);
            }

            public void onStickerSetUninstalled() {
                ((ArchivedStickerSetCell) this.val$view).setChecked(false);
            }
        }

        C10452() {
        }

        public void onItemClick(View view, int i) {
            if (i >= ArchivedStickersActivity.this.stickersStartRow && i < ArchivedStickersActivity.this.stickersEndRow && ArchivedStickersActivity.this.getParentActivity() != null) {
                InputStickerSet tL_inputStickerSetID;
                StickerSetCovered stickerSetCovered = (StickerSetCovered) ArchivedStickersActivity.this.sets.get(i);
                if (stickerSetCovered.set.id != 0) {
                    tL_inputStickerSetID = new TL_inputStickerSetID();
                    tL_inputStickerSetID.id = stickerSetCovered.set.id;
                } else {
                    tL_inputStickerSetID = new TL_inputStickerSetShortName();
                    tL_inputStickerSetID.short_name = stickerSetCovered.set.short_name;
                }
                tL_inputStickerSetID.access_hash = stickerSetCovered.set.access_hash;
                Dialog stickersAlert = new StickersAlert(ArchivedStickersActivity.this.getParentActivity(), ArchivedStickersActivity.this, tL_inputStickerSetID, null, null);
                stickersAlert.setInstallDelegate(new C10441(view));
                ArchivedStickersActivity.this.showDialog(stickersAlert);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ArchivedStickersActivity.3 */
    class C10463 extends OnScrollListener {
        C10463() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (!ArchivedStickersActivity.this.loadingStickers && !ArchivedStickersActivity.this.endReached && ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2) {
                ArchivedStickersActivity.this.getStickers();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ArchivedStickersActivity.4 */
    class C10484 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.ArchivedStickersActivity.4.1 */
        class C10471 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C10471(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    TL_messages_archivedStickers tL_messages_archivedStickers = (TL_messages_archivedStickers) this.val$response;
                    ArchivedStickersActivity.this.sets.addAll(tL_messages_archivedStickers.sets);
                    ArchivedStickersActivity.this.endReached = tL_messages_archivedStickers.sets.size() != 15;
                    ArchivedStickersActivity.this.loadingStickers = false;
                    ArchivedStickersActivity.this.firstLoaded = true;
                    if (ArchivedStickersActivity.this.emptyView != null) {
                        ArchivedStickersActivity.this.emptyView.showTextView();
                    }
                    ArchivedStickersActivity.this.updateRows();
                }
            }
        }

        C10484() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C10471(tL_error, tLObject));
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.ArchivedStickersActivity.ListAdapter.1 */
        class C10491 implements OnCheckedChangeListener {
            C10491() {
            }

            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                StickersQuery.removeStickersSet(ArchivedStickersActivity.this.getParentActivity(), ((StickerSetCovered) ArchivedStickersActivity.this.sets.get(((Integer) ((ArchivedStickerSetCell) compoundButton.getParent()).getTag()).intValue())).set, !z ? 1 : 2, ArchivedStickersActivity.this, false);
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
            return ArchivedStickersActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            return (i < ArchivedStickersActivity.this.stickersStartRow || i >= ArchivedStickersActivity.this.stickersEndRow) ? i == ArchivedStickersActivity.this.stickersLoadingRow ? 1 : i == ArchivedStickersActivity.this.stickersShadowRow ? 2 : 0 : 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (getItemViewType(i) == 0) {
                ArchivedStickerSetCell archivedStickerSetCell = (ArchivedStickerSetCell) viewHolder.itemView;
                archivedStickerSetCell.setTag(Integer.valueOf(i));
                StickerSetCovered stickerSetCovered = (StickerSetCovered) ArchivedStickersActivity.this.sets.get(i);
                archivedStickerSetCell.setStickersSet(stickerSetCovered, i != ArchivedStickersActivity.this.sets.size() + -1, false);
                archivedStickerSetCell.setChecked(StickersQuery.isStickerPackInstalled(stickerSetCovered.set.id));
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new ArchivedStickerSetCell(this.mContext, true);
                    view.setBackgroundResource(C0338R.drawable.list_selector_white);
                    ((ArchivedStickerSetCell) view).setOnCheckClick(new C10491());
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new LoadingCell(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }
    }

    public ArchivedStickersActivity(int i) {
        this.sets = new ArrayList();
        this.currentType = i;
    }

    private void getStickers() {
        if (!this.loadingStickers && !this.endReached) {
            this.loadingStickers = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
            TLObject tL_messages_getArchivedStickers = new TL_messages_getArchivedStickers();
            tL_messages_getArchivedStickers.offset_id = this.sets.isEmpty() ? 0 : ((StickerSetCovered) this.sets.get(this.sets.size() - 1)).set.id;
            tL_messages_getArchivedStickers.limit = 15;
            tL_messages_getArchivedStickers.masks = this.currentType == 1;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_messages_getArchivedStickers, new C10484()), this.classGuid);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.sets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersLoadingRow = -1;
            this.stickersShadowRow = -1;
        } else {
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + this.sets.size();
            this.rowCount += this.sets.size();
            int i;
            if (this.endReached) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.stickersShadowRow = i;
                this.stickersLoadingRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.stickersLoadingRow = i;
                this.stickersShadowRow = -1;
            }
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", C0338R.string.ArchivedStickers));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", C0338R.string.ArchivedMasks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C10431());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.emptyView = new EmptyTextProgressView(context);
        if (this.currentType == 0) {
            this.emptyView.setText(LocaleController.getString("ArchivedStickersEmpty", C0338R.string.ArchivedStickersEmpty));
        } else {
            this.emptyView.setText(LocaleController.getString("ArchivedMasksEmpty", C0338R.string.ArchivedMasksEmpty));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        if (this.loadingStickers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        View recyclerListView = new RecyclerListView(context);
        recyclerListView.setFocusable(true);
        recyclerListView.setEmptyView(this.emptyView);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        recyclerListView.setAdapter(this.listAdapter);
        recyclerListView.setOnItemClickListener(new C10452());
        recyclerListView.setOnScrollListener(new C10463());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.needReloadArchivedStickers) {
            this.firstLoaded = false;
            this.endReached = false;
            this.sets.clear();
            updateRows();
            if (this.emptyView != null) {
                this.emptyView.showProgress();
            }
            getStickers();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getStickers();
        updateRows();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadArchivedStickers);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadArchivedStickers);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
