package com.hanista.mobogram.mobo.p000a;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.ChatActivity.MessageSelectionDelegate;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.a.b */
public class ArchiveActivity extends BaseFragment implements NotificationCenterDelegate {
    private RecyclerListView f104a;
    private ArchiveActivity f105b;
    private int f106c;
    private int f107d;
    private int f108e;
    private int f109f;
    private List<Archive> f110g;

    /* renamed from: com.hanista.mobogram.mobo.a.b.1 */
    class ArchiveActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ArchiveActivity f85a;

        ArchiveActivity(ArchiveActivity archiveActivity) {
            this.f85a = archiveActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f85a.finishFragment();
            } else if (i == 2) {
                this.f85a.m218a(new Archive());
            } else if (i == 3) {
                this.f85a.presentFragment(new ArchiveSettingsActivity());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.b.2 */
    class ArchiveActivity implements OnItemClickListener {
        final /* synthetic */ ArchiveActivity f86a;

        ArchiveActivity(ArchiveActivity archiveActivity) {
            this.f86a = archiveActivity;
        }

        public void onItemClick(View view, int i) {
            if (i >= this.f86a.f106c && i < this.f86a.f107d && this.f86a.getParentActivity() != null) {
                Archive archive = (Archive) this.f86a.f110g.get(i);
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", UserConfig.getClientUserId());
                bundle.putLong("archive_id", archive.m204a().longValue());
                this.f86a.presentFragment(new ChatActivity(bundle));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.b.3 */
    class ArchiveActivity implements OnClickListener {
        final /* synthetic */ EditText f87a;
        final /* synthetic */ Archive f88b;
        final /* synthetic */ ArchiveActivity f89c;

        ArchiveActivity(ArchiveActivity archiveActivity, EditText editText, Archive archive) {
            this.f89c = archiveActivity;
            this.f87a = editText;
            this.f88b = archive;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            String obj = this.f87a.getText().toString();
            if (obj.length() > 0) {
                this.f88b.m207a(obj);
                ArchiveUtil.m258a(this.f88b);
                this.f89c.m221b();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.b.4 */
    class ArchiveActivity implements OnClickListener {
        final /* synthetic */ Archive f90a;
        final /* synthetic */ ArchiveActivity f91b;

        ArchiveActivity(ArchiveActivity archiveActivity, Archive archive) {
            this.f91b = archiveActivity;
            this.f90a = archive;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (this.f90a.m204a().longValue() != -1) {
                ArchiveUtil.m261a(this.f90a.m204a());
            }
            this.f91b.m221b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.b.5 */
    class ArchiveActivity implements OnClickListener {
        final /* synthetic */ ArchiveActivity f92a;

        ArchiveActivity(ArchiveActivity archiveActivity) {
            this.f92a = archiveActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.b.a */
    private class ArchiveActivity extends Adapter {
        final /* synthetic */ ArchiveActivity f101a;
        private Context f102b;

        /* renamed from: com.hanista.mobogram.mobo.a.b.a.1 */
        class ArchiveActivity implements MessageSelectionDelegate {
            final /* synthetic */ Archive f93a;
            final /* synthetic */ ArchiveActivity f94b;

            ArchiveActivity(ArchiveActivity archiveActivity, Archive archive) {
                this.f94b = archiveActivity;
                this.f93a = archive;
            }

            public void didSelectMessages(List<Integer> list) {
                for (Integer num : list) {
                    ArchiveMessageInfo a = ArchiveUtil.m257a(num.intValue());
                    if (a != null) {
                        a.m236a(this.f93a.m204a());
                        ArchiveUtil.m255a(a);
                    } else {
                        ArchiveUtil.m266b(this.f93a.m204a(), num.intValue());
                    }
                }
                this.f94b.f101a.m221b();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.a.b.a.2 */
        class ArchiveActivity implements MessageSelectionDelegate {
            final /* synthetic */ ArchiveActivity f95a;

            ArchiveActivity(ArchiveActivity archiveActivity) {
                this.f95a = archiveActivity;
            }

            public void didSelectMessages(List<Integer> list) {
                for (Integer intValue : list) {
                    ArchiveUtil.m268b(intValue.intValue());
                }
                this.f95a.f101a.m221b();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.a.b.a.3 */
        class ArchiveActivity implements View.OnClickListener {
            final /* synthetic */ ArchiveActivity f99a;

            /* renamed from: com.hanista.mobogram.mobo.a.b.a.3.1 */
            class ArchiveActivity implements OnClickListener {
                final /* synthetic */ int[] f96a;
                final /* synthetic */ Archive f97b;
                final /* synthetic */ ArchiveActivity f98c;

                ArchiveActivity(ArchiveActivity archiveActivity, int[] iArr, Archive archive) {
                    this.f98c = archiveActivity;
                    this.f96a = iArr;
                    this.f97b = archive;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    this.f98c.f99a.m213a(this.f96a[i], this.f97b);
                }
            }

            ArchiveActivity(ArchiveActivity archiveActivity) {
                this.f99a = archiveActivity;
            }

            public void onClick(View view) {
                Archive archive = ((ArchiveCell) view.getParent()).getArchive();
                Builder builder = new Builder(this.f99a.f101a.getParentActivity());
                builder.setTitle(archive.m208b());
                CharSequence[] charSequenceArr = new CharSequence[]{LocaleController.getString("AddMessageToCategory", C0338R.string.AddMessageToCategory), LocaleController.getString("RemoveMessageFromCategory", C0338R.string.RemoveMessageFromCategory), LocaleController.getString("EditName", C0338R.string.EditName), LocaleController.getString("Delete", C0338R.string.Delete)};
                builder.setItems(charSequenceArr, new ArchiveActivity(this, new int[]{0, 1, 2, 3}, archive));
                this.f99a.f101a.showDialog(builder.create());
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.a.b.a.a */
        private class ArchiveActivity extends ViewHolder {
            final /* synthetic */ ArchiveActivity f100a;

            public ArchiveActivity(ArchiveActivity archiveActivity, View view) {
                this.f100a = archiveActivity;
                super(view);
            }
        }

        public ArchiveActivity(ArchiveActivity archiveActivity, Context context) {
            this.f101a = archiveActivity;
            this.f102b = context;
        }

        private void m213a(int i, Archive archive) {
            Bundle bundle;
            BaseFragment chatActivity;
            if (i == 0) {
                bundle = new Bundle();
                bundle.putInt("user_id", UserConfig.getClientUserId());
                bundle.putBoolean("selection_mode", true);
                bundle.putLong("archive_id", -1);
                chatActivity = new ChatActivity(bundle);
                chatActivity.setSelectionDelegate(new ArchiveActivity(this, archive));
                this.f101a.presentFragment(chatActivity);
            } else if (i == 1) {
                bundle = new Bundle();
                bundle.putInt("user_id", UserConfig.getClientUserId());
                bundle.putBoolean("selection_mode", true);
                bundle.putLong("archive_id", archive.m204a().longValue());
                chatActivity = new ChatActivity(bundle);
                chatActivity.setSelectionDelegate(new ArchiveActivity(this));
                this.f101a.presentFragment(chatActivity);
            } else if (i == 2) {
                this.f101a.m218a(archive);
            } else if (i == 3) {
                this.f101a.m222b(archive);
            }
        }

        public void m215a(int i, int i2) {
            Archive archive = (Archive) this.f101a.f110g.get(i);
            Archive archive2 = (Archive) this.f101a.f110g.get(i2);
            if (!archive.m212f() && !archive2.m212f()) {
                Integer c = archive2.m209c();
                archive2.m205a(archive.m209c());
                archive.m205a(c);
                ArchiveUtil.m258a(archive);
                ArchiveUtil.m258a(archive2);
                notifyItemMoved(i, i2);
                this.f101a.m221b();
            }
        }

        public int getItemCount() {
            return this.f101a.f109f;
        }

        public long getItemId(int i) {
            return (i < this.f101a.f106c || i >= this.f101a.f107d) ? i == this.f101a.f108e ? -2147483648L : (long) i : ((Archive) this.f101a.f110g.get(i)).m204a().longValue();
        }

        public int getItemViewType(int i) {
            return ((i < this.f101a.f106c || i >= this.f101a.f107d) && i == this.f101a.f108e) ? 1 : 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ((ArchiveCell) viewHolder.itemView).m233a((Archive) this.f101a.f110g.get(i), i != this.f101a.f110g.size() + -1);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new ArchiveCell(this.f102b);
                    view.setBackgroundColor(-1);
                    view.setBackgroundResource(C0338R.drawable.list_selector_white);
                    ((ArchiveCell) view).setOnOptionsClick(new ArchiveActivity(this));
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new TextInfoPrivacyCell(this.f102b);
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("ArchiveHelp", C0338R.string.ArchiveHelp));
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new ArchiveActivity(this, view);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.b.b */
    public class ArchiveActivity extends Callback {
        final /* synthetic */ ArchiveActivity f103a;

        public ArchiveActivity(ArchiveActivity archiveActivity) {
            this.f103a = archiveActivity;
        }

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
            this.f103a.f105b.m215a(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (i != 0) {
                this.f103a.f104a.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }
    }

    private void m217a() {
        this.f110g = ArchiveUtil.m259a(true);
        if (this.f105b != null) {
            this.f105b.notifyDataSetChanged();
        }
    }

    private void m218a(Archive archive) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewCategory", C0338R.string.NewCategory));
        View editText = new EditText(getParentActivity());
        if (VERSION.SDK_INT < 11) {
            editText.setBackgroundResource(17301529);
        }
        editText.setTextSize(18.0f);
        editText.setText(archive.m208b());
        editText.setImeOptions(6);
        editText.setSingleLine(true);
        builder.setView(editText);
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ArchiveActivity(this, editText, archive));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
        if (editText != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) editText.getLayoutParams();
            if (marginLayoutParams != null) {
                if (marginLayoutParams instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) marginLayoutParams).gravity = 1;
                }
                int dp = AndroidUtilities.dp(10.0f);
                marginLayoutParams.leftMargin = dp;
                marginLayoutParams.rightMargin = dp;
                dp = AndroidUtilities.dp(10.0f);
                marginLayoutParams.bottomMargin = dp;
                marginLayoutParams.topMargin = dp;
                editText.setLayoutParams(marginLayoutParams);
            }
            editText.setSelection(editText.getText().length());
        }
    }

    private void m221b() {
        this.f109f = 0;
        m217a();
        if (this.f110g.isEmpty()) {
            this.f106c = -1;
            this.f107d = -1;
        } else {
            this.f106c = 0;
            this.f107d = this.f110g.size();
            this.f109f += this.f110g.size();
        }
        int i = this.f109f;
        this.f109f = i + 1;
        this.f108e = i;
        if (this.f105b != null) {
            this.f105b.notifyDataSetChanged();
        }
    }

    private void m222b(Archive archive) {
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(LocaleController.formatString("AreYouSureDeleteCategory", C0338R.string.AreYouSureDeleteCategory, new Object[0]));
        builder.setTitle(archive.m208b());
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ArchiveActivity(this, archive));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void m225c() {
        if (UserConfig.isClientActivated()) {
            SettingManager settingManager = new SettingManager();
            if (!settingManager.m944b("archiveHelpDisplayed")) {
                settingManager.m943a("archiveHelpDisplayed", true);
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("ArchiveHelpPersian", C0338R.string.ArchiveHelpPersian));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ArchiveActivity(this));
                builder.create().show();
            }
        }
    }

    private void m227d() {
        if (ThemeUtil.m2490b()) {
            this.actionBar.setBackgroundColor(AdvanceTheme.f2500k);
            int i = AdvanceTheme.bd;
            this.actionBar.setTitleColor(AdvanceTheme.f2501l);
            getParentActivity().getResources().getDrawable(C0338R.drawable.plus).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_settings).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("FavoriteMessages", C0338R.string.FavoriteMessages));
        this.actionBar.setActionBarMenuOnItemClick(new ArchiveActivity(this));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(2, (int) C0338R.drawable.plus);
        createMenu.addItem(3, (int) C0338R.drawable.ic_ab_settings);
        this.f105b = new ArchiveActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.f104a = new RecyclerListView(context);
        this.f104a.setFocusable(true);
        this.f104a.setTag(Integer.valueOf(7));
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(1);
        this.f104a.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(new ArchiveActivity(this)).attachToRecyclerView(this.f104a);
        frameLayout.addView(this.f104a, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.f104a.setAdapter(this.f105b);
        this.f104a.setOnItemClickListener(new ArchiveActivity(this));
        initThemeBackground(this.f104a);
        m225c();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoaded) {
            m221b();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        m221b();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        m217a();
        initThemeActionBar();
        m227d();
    }
}
