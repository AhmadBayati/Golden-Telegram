package com.hanista.mobogram.mobo.p015o;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
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

/* renamed from: com.hanista.mobogram.mobo.o.c */
public class MenuSettingsActivity extends BaseFragment {
    private RecyclerListView f2000a;
    private MenuSettingsActivity f2001b;
    private boolean f2002c;
    private int f2003d;
    private int f2004e;
    private int f2005f;
    private int f2006g;
    private List<MenuData> f2007h;

    /* renamed from: com.hanista.mobogram.mobo.o.c.1 */
    class MenuSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ MenuSettingsActivity f1994a;

        MenuSettingsActivity(MenuSettingsActivity menuSettingsActivity) {
            this.f1994a = menuSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1994a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.o.c.2 */
    class MenuSettingsActivity implements OnItemClickListener {
        final /* synthetic */ MenuSettingsActivity f1995a;

        MenuSettingsActivity(MenuSettingsActivity menuSettingsActivity) {
            this.f1995a = menuSettingsActivity;
        }

        public void onItemClick(View view, int i) {
            if (i >= this.f1995a.f2003d && i < this.f1995a.f2004e && this.f1995a.getParentActivity() != null) {
                this.f1995a.m1981a();
                MenuData menuData = (MenuData) this.f1995a.f2007h.get(i - 1);
                if (menuData.m1973b() == 12) {
                    Toast.makeText(this.f1995a.getParentActivity(), LocaleController.getString("YouCannotHideMoboSettingsItem", C0338R.string.YouCannotHideMoboSettingsItem), 0).show();
                    return;
                }
                int a = MenuUtil.m1993a(menuData.m1973b());
                int i2 = MoboConstants.f1345l;
                int i3 = !menuData.m1975d() ? i2 | a : (a ^ -1) & i2;
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("visible_menus", i3);
                edit.commit();
                MoboConstants.m1379a();
                this.f1995a.m1984b();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.menuSettingsChanged, new Object[0]);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.o.c.a */
    private class MenuSettingsActivity extends Adapter {
        final /* synthetic */ MenuSettingsActivity f1997a;
        private Context f1998b;

        /* renamed from: com.hanista.mobogram.mobo.o.c.a.a */
        private class MenuSettingsActivity extends ViewHolder {
            final /* synthetic */ MenuSettingsActivity f1996a;

            public MenuSettingsActivity(MenuSettingsActivity menuSettingsActivity, View view) {
                this.f1996a = menuSettingsActivity;
                super(view);
            }
        }

        public MenuSettingsActivity(MenuSettingsActivity menuSettingsActivity, Context context) {
            this.f1997a = menuSettingsActivity;
            this.f1998b = context;
        }

        public void m1979a(int i, int i2) {
            if (i != i2) {
                this.f1997a.f2002c = true;
            }
            MenuData menuData = (MenuData) this.f1997a.f2007h.get(i - 1);
            MenuData menuData2 = (MenuData) this.f1997a.f2007h.get(i2 - 1);
            int f = menuData.m1977f();
            menuData.m1971a(menuData2.m1977f());
            menuData2.m1971a(f);
            this.f1997a.f2007h.set(i - 1, menuData2);
            this.f1997a.f2007h.set(i2 - 1, menuData);
            notifyItemMoved(i, i2);
            this.f1997a.m1981a();
        }

        public int getItemCount() {
            return this.f1997a.f2006g;
        }

        public long getItemId(int i) {
            return (i < this.f1997a.f2003d || i >= this.f1997a.f2004e) ? i == this.f1997a.f2005f ? -2147483648L : (long) i : (long) ((MenuData) this.f1997a.f2007h.get(i - 1)).m1973b();
        }

        public int getItemViewType(int i) {
            return ((i < this.f1997a.f2003d || i >= this.f1997a.f2004e) && i == this.f1997a.f2005f) ? 1 : 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ((MenuCell) viewHolder.itemView).m1969a((MenuData) this.f1997a.f2007h.get(i - 1), true);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new MenuCell(this.f1998b);
                    view.setBackgroundColor(-1);
                    view.setBackgroundResource(C0338R.drawable.list_selector_white);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new TextInfoPrivacyCell(this.f1998b);
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("MenuItemsOrderAndVisibilityHelp", C0338R.string.MenuItemsOrderAndVisibilityHelp));
                    view.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new MenuSettingsActivity(this, view);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.o.c.b */
    public class MenuSettingsActivity extends Callback {
        final /* synthetic */ MenuSettingsActivity f1999a;

        public MenuSettingsActivity(MenuSettingsActivity menuSettingsActivity) {
            this.f1999a = menuSettingsActivity;
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
            this.f1999a.f2001b.m1979a(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (i != 0) {
                this.f1999a.f2000a.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }
    }

    private void m1981a() {
        if (this.f2002c) {
            this.f2002c = false;
            MenuUtil.m1996a(this.f2007h);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.menuSettingsChanged, new Object[0]);
        }
    }

    private void m1984b() {
        this.f2007h = MenuUtil.m1994a(false);
        if (this.f2001b != null) {
            this.f2001b.notifyDataSetChanged();
        }
    }

    private void m1986c() {
        this.f2006g = 0;
        m1984b();
        int i = this.f2006g;
        this.f2006g = i + 1;
        this.f2005f = i;
        if (this.f2007h.isEmpty()) {
            this.f2003d = -1;
            this.f2004e = -1;
        } else {
            this.f2003d = 1;
            this.f2004e = this.f2007h.size() + 1;
            this.f2006g += this.f2007h.size();
        }
        if (this.f2001b != null) {
            this.f2001b.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("MenuItemsOrderAndVisibility", C0338R.string.MenuItemsOrderAndVisibility));
        this.actionBar.setActionBarMenuOnItemClick(new MenuSettingsActivity(this));
        this.f2001b = new MenuSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.f2000a = new RecyclerListView(context);
        this.f2000a.setFocusable(true);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(1);
        this.f2000a.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(new MenuSettingsActivity(this)).attachToRecyclerView(this.f2000a);
        frameLayout.addView(this.f2000a, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.f2000a.setAdapter(this.f2001b);
        this.f2000a.setOnItemClickListener(new MenuSettingsActivity(this));
        initThemeBackground(this.f2000a);
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        m1986c();
        return true;
    }

    public void onResume() {
        super.onResume();
        if (this.f2001b != null) {
            this.f2001b.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}
