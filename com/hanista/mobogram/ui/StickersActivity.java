package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.StickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_reorderStickerSets;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.StickerSetCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.StickersAlert;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderline;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Locale;

public class StickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int archivedInfoRow;
    private int archivedRow;
    private int currentType;
    private int featuredInfoRow;
    private int featuredRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int masksInfoRow;
    private int masksRow;
    private boolean needReorder;
    private int rowCount;
    private int stickersEndRow;
    private int stickersShadowRow;
    private int stickersStartRow;

    /* renamed from: com.hanista.mobogram.ui.StickersActivity.1 */
    class C19171 extends ActionBarMenuOnItemClick {
        C19171() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                StickersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.StickersActivity.2 */
    class C19182 implements OnItemClickListener {
        C19182() {
        }

        public void onItemClick(View view, int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow && StickersActivity.this.getParentActivity() != null) {
                StickersActivity.this.sendReorder();
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) StickersQuery.getStickerSets(StickersActivity.this.currentType).get(i - StickersActivity.this.stickersStartRow);
                ArrayList arrayList = tL_messages_stickerSet.documents;
                if (arrayList != null && !arrayList.isEmpty()) {
                    StickersActivity.this.showDialog(new StickersAlert(StickersActivity.this.getParentActivity(), StickersActivity.this, null, tL_messages_stickerSet, null));
                }
            } else if (i == StickersActivity.this.featuredRow) {
                StickersActivity.this.presentFragment(new FeaturedStickersActivity());
            } else if (i == StickersActivity.this.archivedRow) {
                StickersActivity.this.presentFragment(new ArchivedStickersActivity(StickersActivity.this.currentType));
            } else if (i == StickersActivity.this.masksRow) {
                StickersActivity.this.presentFragment(new StickersActivity(1));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.StickersActivity.3 */
    class C19193 implements RequestDelegate {
        C19193() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.StickersActivity.ListAdapter.1 */
        class C19201 extends URLSpanNoUnderline {
            C19201(String str) {
                super(str);
            }

            public void onClick(View view) {
                MessagesController.openByUserName("stickers", StickersActivity.this, 1);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.StickersActivity.ListAdapter.2 */
        class C19222 implements OnClickListener {

            /* renamed from: com.hanista.mobogram.ui.StickersActivity.ListAdapter.2.1 */
            class C19211 implements DialogInterface.OnClickListener {
                final /* synthetic */ int[] val$options;
                final /* synthetic */ TL_messages_stickerSet val$stickerSet;

                C19211(int[] iArr, TL_messages_stickerSet tL_messages_stickerSet) {
                    this.val$options = iArr;
                    this.val$stickerSet = tL_messages_stickerSet;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    ListAdapter.this.processSelectionOption(this.val$options[i], this.val$stickerSet);
                }
            }

            C19222() {
            }

            public void onClick(View view) {
                int[] iArr;
                CharSequence[] charSequenceArr;
                StickersActivity.this.sendReorder();
                TL_messages_stickerSet stickersSet = ((StickerSetCell) view.getParent()).getStickersSet();
                Builder builder = new Builder(StickersActivity.this.getParentActivity());
                builder.setTitle(stickersSet.set.title);
                if (StickersActivity.this.currentType == 0) {
                    if (stickersSet.set.official) {
                        iArr = new int[]{0};
                        charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0338R.string.StickersHide)};
                    } else {
                        iArr = new int[]{0, 1, 2, 3};
                        charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0338R.string.StickersHide), LocaleController.getString("StickersRemove", C0338R.string.StickersRemove), LocaleController.getString("StickersShare", C0338R.string.StickersShare), LocaleController.getString("StickersCopy", C0338R.string.StickersCopy)};
                    }
                } else if (stickersSet.set.official) {
                    iArr = new int[]{0};
                    charSequenceArr = new CharSequence[]{LocaleController.getString("StickersRemove", C0338R.string.StickersHide)};
                } else {
                    iArr = new int[]{0, 1, 2, 3};
                    charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0338R.string.StickersHide), LocaleController.getString("StickersRemove", C0338R.string.StickersRemove), LocaleController.getString("StickersShare", C0338R.string.StickersShare), LocaleController.getString("StickersCopy", C0338R.string.StickersCopy)};
                }
                builder.setItems(charSequenceArr, new C19211(iArr, stickersSet));
                StickersActivity.this.showDialog(builder.create());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.StickersActivity.ListAdapter.3 */
        class C19233 extends TextSettingsCell {
            C19233(Context context) {
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
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

        private void processSelectionOption(int i, TL_messages_stickerSet tL_messages_stickerSet) {
            int i2 = 2;
            if (i == 0) {
                Context parentActivity = StickersActivity.this.getParentActivity();
                StickerSet stickerSet = tL_messages_stickerSet.set;
                if (!tL_messages_stickerSet.set.archived) {
                    i2 = 1;
                }
                StickersQuery.removeStickersSet(parentActivity, stickerSet, i2, StickersActivity.this, true);
            } else if (i == 1) {
                StickersQuery.removeStickersSet(StickersActivity.this.getParentActivity(), tL_messages_stickerSet.set, 0, StickersActivity.this, true);
            } else if (i == 2) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name}));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", C0338R.string.StickersShare)), 500);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            } else if (i == 3) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name})));
                    Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        }

        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            return (i < StickersActivity.this.stickersStartRow || i >= StickersActivity.this.stickersEndRow) ? (i == StickersActivity.this.archivedRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.featuredRow || i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.masksInfoRow) ? -2147483648L : (long) i : ((TL_messages_stickerSet) StickersQuery.getStickerSets(StickersActivity.this.currentType).get(i - StickersActivity.this.stickersStartRow)).set.id;
        }

        public int getItemViewType(int i) {
            return (i < StickersActivity.this.stickersStartRow || i >= StickersActivity.this.stickersEndRow) ? (i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.masksInfoRow) ? 1 : (i == StickersActivity.this.featuredRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow) ? 2 : i == StickersActivity.this.stickersShadowRow ? 3 : 0 : 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            switch (viewHolder.getItemViewType()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    ArrayList stickerSets = StickersQuery.getStickerSets(StickersActivity.this.currentType);
                    int access$200 = i - StickersActivity.this.stickersStartRow;
                    StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(access$200);
                    if (access$200 == stickerSets.size() - 1) {
                        z = false;
                    }
                    stickerSetCell.setStickersSet(tL_messages_stickerSet, z);
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    if (i == StickersActivity.this.featuredInfoRow) {
                        CharSequence string = LocaleController.getString("FeaturedStickersInfo", C0338R.string.FeaturedStickersInfo);
                        String str = "@stickers";
                        int indexOf = string.indexOf(str);
                        if (indexOf != -1) {
                            try {
                                CharSequence spannableStringBuilder = new SpannableStringBuilder(string);
                                spannableStringBuilder.setSpan(new C19201("@stickers"), indexOf, str.length() + indexOf, 18);
                                ((TextInfoPrivacyCell) viewHolder.itemView).setText(spannableStringBuilder);
                                return;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
                                return;
                            }
                        }
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
                    } else if (i == StickersActivity.this.archivedInfoRow) {
                        if (StickersActivity.this.currentType == 0) {
                            ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickersInfo", C0338R.string.ArchivedStickersInfo));
                        } else {
                            ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasksInfo", C0338R.string.ArchivedMasksInfo));
                        }
                    } else if (i == StickersActivity.this.masksInfoRow) {
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("MasksInfo", C0338R.string.MasksInfo));
                    }
                case VideoPlayer.STATE_PREPARING /*2*/:
                    if (i == StickersActivity.this.featuredRow) {
                        String format;
                        int size = StickersQuery.getUnreadStickerSets().size();
                        TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                        String string2 = LocaleController.getString("FeaturedStickers", C0338R.string.FeaturedStickers);
                        if (size != 0) {
                            format = String.format("%d", new Object[]{Integer.valueOf(size)});
                        } else {
                            format = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        textSettingsCell.setTextAndValue(string2, format, false);
                    } else if (i == StickersActivity.this.archivedRow) {
                        if (StickersActivity.this.currentType == 0) {
                            ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", C0338R.string.ArchivedStickers), false);
                        } else {
                            ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", C0338R.string.ArchivedMasks), false);
                        }
                    } else if (i == StickersActivity.this.masksRow) {
                        ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("Masks", C0338R.string.Masks), true);
                    }
                default:
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new StickerSetCell(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.list_selector_white);
                    ((StickerSetCell) view).setOnOptionsClick(new C19222());
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    view = new C19233(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.list_selector_white);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void swapElements(int i, int i2) {
            if (i != i2) {
                StickersActivity.this.needReorder = true;
            }
            ArrayList stickerSets = StickersQuery.getStickerSets(StickersActivity.this.currentType);
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i - StickersActivity.this.stickersStartRow);
            stickerSets.set(i - StickersActivity.this.stickersStartRow, stickerSets.get(i2 - StickersActivity.this.stickersStartRow));
            stickerSets.set(i2 - StickersActivity.this.stickersStartRow, tL_messages_stickerSet);
            notifyItemMoved(i, i2);
        }
    }

    public class TouchHelperCallback extends Callback {
        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 0 ? Callback.makeMovementFlags(0, 0) : Callback.makeMovementFlags(3, 0);
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (i != 0) {
                StickersActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }
    }

    public StickersActivity(int i) {
        this.currentType = i;
    }

    private void sendReorder() {
        if (this.needReorder) {
            StickersQuery.calcNewHash(this.currentType);
            this.needReorder = false;
            TLObject tL_messages_reorderStickerSets = new TL_messages_reorderStickerSets();
            ArrayList stickerSets = StickersQuery.getStickerSets(this.currentType);
            for (int i = 0; i < stickerSets.size(); i++) {
                tL_messages_reorderStickerSets.order.add(Long.valueOf(((TL_messages_stickerSet) stickerSets.get(i)).set.id));
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_reorderStickerSets, new C19193());
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(this.currentType));
        }
    }

    private void updateRows() {
        int i;
        this.rowCount = 0;
        if (this.currentType != 0 || UserConfig.isRobot) {
            this.featuredRow = -1;
            this.featuredInfoRow = -1;
            this.masksRow = -1;
            this.masksInfoRow = -1;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.archivedRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.archivedInfoRow = i;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.featuredRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.featuredInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.masksRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.masksInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.archivedRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.archivedInfoRow = i;
        }
        ArrayList stickerSets = StickersQuery.getStickerSets(this.currentType);
        if (stickerSets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        } else {
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + stickerSets.size();
            this.rowCount = stickerSets.size() + this.rowCount;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersShadowRow = i;
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("Stickers", C0338R.string.Stickers));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Masks", C0338R.string.Masks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19171());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.listView = new RecyclerListView(context);
        this.listView.setFocusable(true);
        this.listView.setTag(Integer.valueOf(7));
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(1);
        this.listView.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C19182());
        initThemeBackground(this.listView);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoaded) {
            if (((Integer) objArr[0]).intValue() == this.currentType) {
                updateRows();
            }
        } else if (i == NotificationCenter.featuredStickersDidLoaded && this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(0);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        StickersQuery.checkStickers(this.currentType);
        if (this.currentType == 0) {
            StickersQuery.checkFeaturedStickers();
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        sendReorder();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}
