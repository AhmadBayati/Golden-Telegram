package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p015o.MenuData;
import com.hanista.mobogram.mobo.p015o.MenuUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.Cells.DividerCell;
import com.hanista.mobogram.ui.Cells.DrawerActionCell;
import com.hanista.mobogram.ui.Cells.DrawerProfileCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import java.util.List;

public class DrawerLayoutAdapter extends BaseAdapter {
    private Context mContext;
    private List<MenuData> menuDatas;

    public DrawerLayoutAdapter(Context context) {
        this.mContext = context;
        reloadMenuDatas();
    }

    private void updateViewColor(View view) {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2484U;
            if (AdvanceTheme.f2485V <= 0) {
            }
        }
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public int getCount() {
        return UserConfig.isClientActivated() ? this.menuDatas.size() : 0;
    }

    public Object getItem(int i) {
        return this.menuDatas.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return ((MenuData) this.menuDatas.get(i)).m1978g();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        MenuData menuData = (MenuData) this.menuDatas.get(i);
        if (menuData.m1978g() == 0) {
            DrawerProfileCell drawerProfileCell;
            View view2;
            if (view == null) {
                drawerProfileCell = new DrawerProfileCell(this.mContext);
                view2 = drawerProfileCell;
            } else {
                drawerProfileCell = (DrawerProfileCell) view;
                view2 = view;
            }
            drawerProfileCell.setUser(MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())));
            if (ThemeUtil.m2490b()) {
                ((DrawerProfileCell) view2).refreshAvatar(AdvanceTheme.f2471H, AdvanceTheme.f2482S);
            }
            updateViewColor(view2);
            return view2;
        } else if (menuData.m1978g() == 1) {
            if (view == null) {
                view = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
            }
            updateViewColor(view);
            return view;
        } else if (menuData.m1978g() == 2) {
            if (view == null) {
                view = new DividerCell(this.mContext);
                view.setTag("drawerListDividerColor");
            }
            updateViewColor(view);
            return view;
        } else if (menuData.m1978g() != 3) {
            return view;
        } else {
            View drawerActionCell = view == null ? new DrawerActionCell(this.mContext) : view;
            drawerActionCell.setTag(Integer.valueOf(menuData.m1973b()));
            updateViewColor(drawerActionCell);
            DrawerActionCell drawerActionCell2 = (DrawerActionCell) drawerActionCell;
            int c = ThemeUtil.m2490b() ? AdvanceTheme.f2483T : ThemeUtil.m2485a().m2289c();
            Drawable drawable;
            if (menuData.m1973b() == 2) {
                if (MoboUtils.m1726i(this.mContext) && !MoboUtils.m1724h(this.mContext)) {
                    drawable = this.mContext.getResources().getDrawable(C0338R.drawable.menu_invite_telegram);
                    drawable.setColorFilter(c, Mode.SRC_IN);
                    drawerActionCell2.setTextAndIcon(LocaleController.getString("AddUser", C0338R.string.AddUser), drawable);
                    return drawerActionCell;
                } else if (!MoboUtils.m1724h(this.mContext)) {
                    return drawerActionCell;
                } else {
                    drawable = this.mContext.getResources().getDrawable(C0338R.drawable.menu_newgroup_telegram);
                    drawable.setColorFilter(c, Mode.SRC_IN);
                    drawerActionCell2.setTextAndIcon(LocaleController.getString("ChangeUser", C0338R.string.ChangeUser), drawable);
                    return drawerActionCell;
                }
            } else if (menuData.m1973b() == 11) {
                String e = menuData.m1976e();
                int c2 = new DataBaseAccess().m862c();
                if (c2 > 0) {
                    e = e + " (" + c2 + ")";
                }
                drawable = this.mContext.getResources().getDrawable(menuData.m1974c());
                drawable.setColorFilter(c, Mode.SRC_IN);
                drawerActionCell2.setTextAndIcon(e, drawable);
                return drawerActionCell;
            } else if (menuData.m1973b() == 26) {
                r4 = this.mContext.getResources().getDrawable(menuData.m1974c());
                r4.setColorFilter(c, Mode.SRC_IN);
                drawerActionCell2.setTextAndIcon(MoboConstants.aO ? LocaleController.getString("TurnOn", C0338R.string.TurnOn) : LocaleController.getString("TurnOff", C0338R.string.TurnOff), r4);
                return drawerActionCell;
            } else {
                r4 = this.mContext.getResources().getDrawable(menuData.m1974c());
                r4.setColorFilter(c, Mode.SRC_IN);
                drawerActionCell2.setTextAndIcon(menuData.m1976e(), r4);
                return drawerActionCell;
            }
        }
    }

    public int getViewTypeCount() {
        return 4;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return !UserConfig.isClientActivated();
    }

    public boolean isEnabled(int i) {
        return ((MenuData) this.menuDatas.get(i)).m1972a();
    }

    public void reloadMenuDatas() {
        this.menuDatas = MenuUtil.m1994a(true);
    }
}
