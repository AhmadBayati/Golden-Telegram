package com.hanista.mobogram.mobo.p019r;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.r.c */
public class TabsActivity extends BaseFragment {
    private RecyclerListView f2441a;
    private TabsActivity f2442b;
    private boolean f2443c;
    private int f2444d;
    private int f2445e;
    private int f2446f;
    private int f2447g;
    private List<TabData> f2448h;

    /* renamed from: com.hanista.mobogram.mobo.r.c.1 */
    class TabsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ TabsActivity f2435a;

        TabsActivity(TabsActivity tabsActivity) {
            this.f2435a = tabsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2435a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.c.2 */
    class TabsActivity implements OnItemClickListener {
        final /* synthetic */ TabsActivity f2436a;

        TabsActivity(TabsActivity tabsActivity) {
            this.f2436a = tabsActivity;
        }

        public void onItemClick(View view, int i) {
            if (i >= this.f2436a.f2444d && i < this.f2436a.f2445e && this.f2436a.getParentActivity() != null) {
                this.f2436a.m2245a();
                TabData tabData = (TabData) this.f2436a.f2448h.get(i - 1);
                int b = TabsUtil.m2263b(tabData.m2225a());
                int i2 = MoboConstants.f1344k;
                b = !tabData.m2231c() ? b | i2 : (b ^ -1) & i2;
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                if (b == 0) {
                    edit.putBoolean("show_tab", false);
                }
                edit.putInt("visible_tabs", b);
                edit.commit();
                MoboConstants.m1379a();
                if (MoboConstants.f1329V == tabData.m2225a() || MoboConstants.ak == tabData.m2225a()) {
                    TabsUtil.m2264b();
                }
                this.f2436a.m2248b();
                this.f2436a.parentLayout.rebuildAllFragmentViews(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.c.a */
    private class TabsActivity extends Adapter {
        final /* synthetic */ TabsActivity f2438a;
        private Context f2439b;

        /* renamed from: com.hanista.mobogram.mobo.r.c.a.a */
        private class TabsActivity extends ViewHolder {
            final /* synthetic */ TabsActivity f2437a;

            public TabsActivity(TabsActivity tabsActivity, View view) {
                this.f2437a = tabsActivity;
                super(view);
            }
        }

        public TabsActivity(TabsActivity tabsActivity, Context context) {
            this.f2438a = tabsActivity;
            this.f2439b = context;
        }

        public void m2243a(int i, int i2) {
            if (i != i2) {
                this.f2438a.f2443c = true;
            }
            TabData tabData = (TabData) this.f2438a.f2448h.get(i - 1);
            TabData tabData2 = (TabData) this.f2438a.f2448h.get(i2 - 1);
            int e = tabData.m2234e();
            tabData.m2227a(tabData2.m2234e());
            tabData2.m2227a(e);
            this.f2438a.f2448h.set(i - 1, tabData2);
            this.f2438a.f2448h.set(i2 - 1, tabData);
            notifyItemMoved(i, i2);
            this.f2438a.m2245a();
        }

        public int getItemCount() {
            return this.f2438a.f2447g;
        }

        public long getItemId(int i) {
            return (i < this.f2438a.f2444d || i >= this.f2438a.f2445e) ? i == this.f2438a.f2446f ? -2147483648L : (long) i : (long) ((TabData) this.f2438a.f2448h.get(i - 1)).m2225a();
        }

        public int getItemViewType(int i) {
            return ((i < this.f2438a.f2444d || i >= this.f2438a.f2445e) && i == this.f2438a.f2446f) ? 1 : 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ((TabCell) viewHolder.itemView).m2224a((TabData) this.f2438a.f2448h.get(i - 1), true);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new TabCell(this.f2439b);
                    view.setBackgroundColor(-1);
                    view.setBackgroundResource(C0338R.drawable.list_selector_white);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new TextInfoPrivacyCell(this.f2439b);
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("TabsOrderAndVisibilityHelp", C0338R.string.TabsOrderAndVisibilityHelp));
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new TabsActivity(this, view);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.c.b */
    public class TabsActivity extends Callback {
        final /* synthetic */ TabsActivity f2440a;

        public TabsActivity(TabsActivity tabsActivity) {
            this.f2440a = tabsActivity;
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
            this.f2440a.f2442b.m2243a(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (i != 0) {
                this.f2440a.f2441a.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }
    }

    private void m2245a() {
        if (this.f2443c) {
            this.f2443c = false;
            TabsUtil.m2262a(this.f2448h);
            this.parentLayout.rebuildAllFragmentViews(false);
        }
    }

    private void m2248b() {
        this.f2448h = TabsUtil.m2260a(false, false);
        if (this.f2442b != null) {
            this.f2442b.notifyDataSetChanged();
        }
    }

    private void m2250c() {
        this.f2447g = 0;
        m2248b();
        int i = this.f2447g;
        this.f2447g = i + 1;
        this.f2446f = i;
        if (this.f2448h.isEmpty()) {
            this.f2444d = -1;
            this.f2445e = -1;
        } else {
            this.f2444d = 1;
            this.f2445e = this.f2448h.size() + 1;
            this.f2447g += this.f2448h.size();
        }
        if (this.f2442b != null) {
            this.f2442b.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("TabsOrderAndVisibility", C0338R.string.TabsOrderAndVisibility));
        this.actionBar.setActionBarMenuOnItemClick(new TabsActivity(this));
        this.f2442b = new TabsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.f2441a = new RecyclerListView(context);
        this.f2441a.setFocusable(true);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(1);
        this.f2441a.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(new TabsActivity(this)).attachToRecyclerView(this.f2441a);
        frameLayout.addView(this.f2441a, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.f2441a.setAdapter(this.f2442b);
        this.f2441a.setOnItemClickListener(new TabsActivity(this));
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        m2250c();
        return true;
    }

    public void onResume() {
        super.onResume();
        if (this.f2442b != null) {
            this.f2442b.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}
