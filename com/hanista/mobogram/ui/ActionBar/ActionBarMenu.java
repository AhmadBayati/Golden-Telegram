package com.hanista.mobogram.ui.ActionBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.hanista.mobogram.messenger.AndroidUtilities;

public class ActionBarMenu extends LinearLayout {
    protected ActionBar parentActionBar;

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenu.1 */
    class C09591 implements OnClickListener {
        C09591() {
        }

        public void onClick(View view) {
            ActionBarMenu.this.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenu.2 */
    class C09602 implements OnClickListener {
        C09602() {
        }

        public void onClick(View view) {
            ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) view;
            if (actionBarMenuItem.hasSubMenu()) {
                if (ActionBarMenu.this.parentActionBar.actionBarMenuOnItemClick.canOpenMenu()) {
                    actionBarMenuItem.toggleSubMenu();
                }
            } else if (actionBarMenuItem.isSearchField()) {
                ActionBarMenu.this.parentActionBar.onSearchFieldVisibilityChanged(actionBarMenuItem.toggleSearch(true));
            } else {
                ActionBarMenu.this.onItemClick(((Integer) view.getTag()).intValue());
            }
        }
    }

    public ActionBarMenu(Context context) {
        super(context);
    }

    public ActionBarMenu(Context context, ActionBar actionBar) {
        super(context);
        setOrientation(0);
        this.parentActionBar = actionBar;
    }

    public ActionBarMenuItem addItem(int i, int i2) {
        return addItem(i, i2, this.parentActionBar.itemsBackgroundColor);
    }

    public ActionBarMenuItem addItem(int i, int i2, int i3) {
        return addItem(i, i2, i3, null, AndroidUtilities.dp(48.0f));
    }

    public ActionBarMenuItem addItem(int i, int i2, int i3, Drawable drawable, int i4) {
        View actionBarMenuItem = new ActionBarMenuItem(getContext(), this, i3);
        actionBarMenuItem.setTag(Integer.valueOf(i));
        if (drawable != null) {
            actionBarMenuItem.iconView.setImageDrawable(drawable);
        } else {
            actionBarMenuItem.iconView.setImageResource(i2);
        }
        addView(actionBarMenuItem);
        LayoutParams layoutParams = (LayoutParams) actionBarMenuItem.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = i4;
        actionBarMenuItem.setLayoutParams(layoutParams);
        actionBarMenuItem.setOnClickListener(new C09602());
        return actionBarMenuItem;
    }

    public ActionBarMenuItem addItem(int i, Drawable drawable) {
        return addItem(i, 0, this.parentActionBar.itemsBackgroundColor, drawable, AndroidUtilities.dp(48.0f));
    }

    public View addItemResource(int i, int i2) {
        View inflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(i2, null);
        inflate.setTag(Integer.valueOf(i));
        addView(inflate);
        LayoutParams layoutParams = (LayoutParams) inflate.getLayoutParams();
        layoutParams.height = -1;
        inflate.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.parentActionBar.itemsBackgroundColor));
        inflate.setLayoutParams(layoutParams);
        inflate.setOnClickListener(new C09591());
        return inflate;
    }

    public ActionBarMenuItem addItemWithWidth(int i, int i2, int i3) {
        return addItem(i, i2, this.parentActionBar.itemsBackgroundColor, null, i3);
    }

    public ActionBarMenuItem addItemWithWidth(int i, Drawable drawable, int i2) {
        return addItem(i, 0, this.parentActionBar.itemsBackgroundColor, drawable, i2);
    }

    public void clearItems() {
        removeAllViews();
    }

    public void closeSearchField() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    this.parentActionBar.onSearchFieldVisibilityChanged(actionBarMenuItem.toggleSearch(false));
                    return;
                }
            }
        }
    }

    public ActionBarMenuItem getItem(int i) {
        View findViewWithTag = findViewWithTag(Integer.valueOf(i));
        return findViewWithTag instanceof ActionBarMenuItem ? (ActionBarMenuItem) findViewWithTag : null;
    }

    public void hideAllPopupMenus() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) childAt).closeSubMenu();
            }
        }
    }

    public void onItemClick(int i) {
        if (this.parentActionBar.actionBarMenuOnItemClick != null) {
            this.parentActionBar.actionBarMenuOnItemClick.onItemClick(i);
        }
    }

    public void onMenuButtonPressed() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.getVisibility() != 0) {
                    continue;
                } else if (actionBarMenuItem.hasSubMenu()) {
                    actionBarMenuItem.toggleSubMenu();
                    return;
                } else if (actionBarMenuItem.overrideMenuClick) {
                    onItemClick(((Integer) actionBarMenuItem.getTag()).intValue());
                    return;
                }
            }
        }
    }

    public void openSearchField(boolean z, String str) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ActionBarMenuItem) {
                ActionBarMenuItem actionBarMenuItem = (ActionBarMenuItem) childAt;
                if (actionBarMenuItem.isSearchField()) {
                    if (z) {
                        this.parentActionBar.onSearchFieldVisibilityChanged(actionBarMenuItem.toggleSearch(true));
                    }
                    actionBarMenuItem.getSearchField().setText(str);
                    actionBarMenuItem.getSearchField().setSelection(str.length());
                    return;
                }
            }
        }
    }
}
