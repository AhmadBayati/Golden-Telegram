package com.hanista.mobogram.ui.ActionBar;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.lang.reflect.Field;

public class ActionBarMenuItem extends FrameLayout {
    private boolean allowCloseAnimation;
    private ImageView clearButton;
    private ActionBarMenuItemDelegate delegate;
    protected ImageView iconView;
    private boolean isSearchField;
    private ActionBarMenuItemSearchListener listener;
    private int[] location;
    private int menuHeight;
    protected boolean overrideMenuClick;
    private ActionBarMenu parentMenu;
    private ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private Rect rect;
    private FrameLayout searchContainer;
    private EditText searchField;
    private View selectedMenuView;
    private boolean showFromBottom;
    private Runnable showMenuRunnable;
    private int subMenuOpenSide;

    public static class ActionBarMenuItemSearchListener {
        public boolean canCollapseSearch() {
            return true;
        }

        public void onSearchCollapse() {
        }

        public void onSearchExpand() {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onTextChanged(EditText editText) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.1 */
    class C09611 implements Runnable {
        C09611() {
        }

        public void run() {
            if (ActionBarMenuItem.this.getParent() != null) {
                ActionBarMenuItem.this.getParent().requestDisallowInterceptTouchEvent(true);
            }
            ActionBarMenuItem.this.toggleSubMenu();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.2 */
    class C09622 implements OnTouchListener {
        C09622() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() == 0 && ActionBarMenuItem.this.popupWindow != null && ActionBarMenuItem.this.popupWindow.isShowing()) {
                view.getHitRect(ActionBarMenuItem.this.rect);
                if (!ActionBarMenuItem.this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    ActionBarMenuItem.this.popupWindow.dismiss();
                }
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.3 */
    class C09633 implements OnDispatchKeyEventListener {
        C09633() {
        }

        public void onDispatchKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && ActionBarMenuItem.this.popupWindow != null && ActionBarMenuItem.this.popupWindow.isShowing()) {
                ActionBarMenuItem.this.popupWindow.dismiss();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.4 */
    class C09644 implements OnClickListener {
        C09644() {
        }

        public void onClick(View view) {
            if (ActionBarMenuItem.this.popupWindow != null && ActionBarMenuItem.this.popupWindow.isShowing()) {
                if (!ActionBarMenuItem.this.processedPopupClick) {
                    ActionBarMenuItem.this.processedPopupClick = true;
                    ActionBarMenuItem.this.popupWindow.dismiss(ActionBarMenuItem.this.allowCloseAnimation);
                } else {
                    return;
                }
            }
            if (ActionBarMenuItem.this.parentMenu != null) {
                ActionBarMenuItem.this.parentMenu.onItemClick(((Integer) view.getTag()).intValue());
            } else if (ActionBarMenuItem.this.delegate != null) {
                ActionBarMenuItem.this.delegate.onItemClick(((Integer) view.getTag()).intValue());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.5 */
    class C09655 implements OnKeyListener {
        C09655() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || ActionBarMenuItem.this.popupWindow == null || !ActionBarMenuItem.this.popupWindow.isShowing()) {
                return false;
            }
            ActionBarMenuItem.this.popupWindow.dismiss();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.6 */
    class C09666 implements Callback {
        C09666() {
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.7 */
    class C09677 implements OnEditorActionListener {
        C09677() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent != null && ((keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 84) || (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66))) {
                AndroidUtilities.hideKeyboard(ActionBarMenuItem.this.searchField);
                if (ActionBarMenuItem.this.listener != null) {
                    ActionBarMenuItem.this.listener.onSearchPressed(ActionBarMenuItem.this.searchField);
                }
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.8 */
    class C09688 implements TextWatcher {
        C09688() {
        }

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (ActionBarMenuItem.this.listener != null) {
                ActionBarMenuItem.this.listener.onTextChanged(ActionBarMenuItem.this.searchField);
            }
            if (ActionBarMenuItem.this.clearButton != null) {
                ImageView access$800 = ActionBarMenuItem.this.clearButton;
                float f = (charSequence == null || charSequence.length() == 0) ? 0.6f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                access$800.setAlpha(f);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.9 */
    class C09699 implements OnClickListener {
        C09699() {
        }

        public void onClick(View view) {
            ActionBarMenuItem.this.searchField.setText(TtmlNode.ANONYMOUS_REGION_ID);
            ActionBarMenuItem.this.searchField.requestFocus();
            AndroidUtilities.showKeyboard(ActionBarMenuItem.this.searchField);
        }
    }

    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i) {
        super(context);
        this.isSearchField = false;
        this.menuHeight = AndroidUtilities.dp(16.0f);
        this.subMenuOpenSide = 0;
        this.allowCloseAnimation = true;
        if (i != 0) {
            setBackgroundDrawable(Theme.createBarSelectorDrawable(i));
        }
        this.parentMenu = actionBarMenu;
        this.iconView = new ImageView(context);
        this.iconView.setScaleType(ScaleType.CENTER);
        addView(this.iconView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
    }

    private void updateOrShowPopup(boolean z, boolean z2) {
        int i;
        if (this.showFromBottom) {
            getLocationOnScreen(this.location);
            int measuredHeight = ((this.location[1] - AndroidUtilities.statusBarHeight) + getMeasuredHeight()) - this.menuHeight;
            int i2 = -this.menuHeight;
            if (measuredHeight < 0) {
                i2 -= measuredHeight;
            }
            i = i2;
        } else {
            i = (this.parentMenu == null || this.subMenuOpenSide != 0) ? -getMeasuredHeight() : (-this.parentMenu.parentActionBar.getMeasuredHeight()) + this.parentMenu.getTop();
        }
        if (z) {
            this.popupLayout.scrollToTop();
        }
        if (this.subMenuOpenSide != 0) {
            if (z) {
                this.popupWindow.showAsDropDown(this, -AndroidUtilities.dp(8.0f), i);
            }
            if (z2) {
                this.popupWindow.update(this, -AndroidUtilities.dp(8.0f), i, -1, -1);
            }
        } else if (this.showFromBottom) {
            if (z) {
                this.popupWindow.showAsDropDown(this, (-this.popupLayout.getMeasuredWidth()) + getMeasuredWidth(), i);
            }
            if (z2) {
                this.popupWindow.update(this, getMeasuredWidth() + (-this.popupLayout.getMeasuredWidth()), i, -1, -1);
            }
        } else if (this.parentMenu != null) {
            r1 = this.parentMenu.parentActionBar;
            if (z) {
                this.popupWindow.showAsDropDown(r1, ((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth(), i);
            }
            if (z2) {
                this.popupWindow.update(r1, ((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth(), i, -1, -1);
            }
        } else if (getParent() != null) {
            r1 = (View) getParent();
            if (z) {
                this.popupWindow.showAsDropDown(r1, ((r1.getMeasuredWidth() - this.popupLayout.getMeasuredWidth()) - getLeft()) - r1.getLeft(), i);
            }
            if (z2) {
                this.popupWindow.update(r1, ((r1.getMeasuredWidth() - this.popupLayout.getMeasuredWidth()) - getLeft()) - r1.getLeft(), i, -1, -1);
            }
        }
    }

    public void addSubDividerItem() {
        if (this.popupLayout != null) {
            View imageView = new ImageView(getContext());
            if (!ThemeUtil.m2490b()) {
                imageView.setBackgroundResource(ThemeUtil.m2485a().m2293g());
            }
            imageView.setPadding(0, 0, 0, 0);
            Drawable drawable = getContext().getResources().getDrawable(C0338R.drawable.numberpicker_selection_divider);
            drawable.setColorFilter(ThemeUtil.m2490b() ? AdvanceTheme.f2491b : ThemeUtil.m2485a().m2289c(), Mode.SRC_IN);
            MoboUtils.m1701a(imageView, drawable);
            this.popupLayout.setShowedFromBotton(this.showFromBottom);
            this.popupLayout.addView(imageView);
            LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
            if (LocaleController.isRTL) {
                layoutParams.gravity = 5;
            }
            layoutParams.width = -1;
            layoutParams.height = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            imageView.setLayoutParams(layoutParams);
            imageView.setClickable(false);
            this.menuHeight = layoutParams.height + this.menuHeight;
        }
    }

    public TextView addSubItem(int i, String str, int i2) {
        if (this.popupLayout == null) {
            this.rect = new Rect();
            this.location = new int[2];
            this.popupLayout = new ActionBarPopupWindowLayout(getContext());
            this.popupLayout.setOnTouchListener(new C09622());
            this.popupLayout.setDispatchKeyEventListener(new C09633());
        }
        View textView = new TextView(getContext());
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        if (!ThemeUtil.m2490b()) {
            textView.setBackgroundResource(ThemeUtil.m2485a().m2293g());
        }
        if (LocaleController.isRTL) {
            textView.setGravity(21);
        } else {
            textView.setGravity(16);
        }
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView.setTextSize(1, 18.0f);
        textView.setMinWidth(AndroidUtilities.dp(196.0f));
        textView.setTag(Integer.valueOf(i));
        textView.setText(str);
        if (i2 != 0) {
            textView.setCompoundDrawablePadding(AndroidUtilities.dp(12.0f));
            if (LocaleController.isRTL) {
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(i2), null);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(i2), null, null, null);
            }
        }
        this.popupLayout.setShowedFromBotton(this.showFromBottom);
        this.popupLayout.addView(textView);
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new C09644());
        this.menuHeight = layoutParams.height + this.menuHeight;
        return textView;
    }

    public void closeSubMenu() {
        if (this.popupWindow != null && this.popupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public ImageView getClearButton() {
        return this.clearButton;
    }

    public ImageView getImageView() {
        return this.iconView;
    }

    public EditText getSearchField() {
        return this.searchField;
    }

    public boolean hasSubMenu() {
        return this.popupLayout != null;
    }

    public void hideSubItem(int i) {
        View findViewWithTag = this.popupLayout.findViewWithTag(Integer.valueOf(i));
        if (findViewWithTag != null) {
            findViewWithTag.setVisibility(8);
        }
    }

    public boolean isSearchField() {
        return this.isSearchField;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.popupWindow != null && this.popupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            if (hasSubMenu() && (this.popupWindow == null || !(this.popupWindow == null || this.popupWindow.isShowing()))) {
                this.showMenuRunnable = new C09611();
                AndroidUtilities.runOnUIThread(this.showMenuRunnable, 200);
            }
        } else if (motionEvent.getActionMasked() == 2) {
            if (!hasSubMenu() || (this.popupWindow != null && (this.popupWindow == null || this.popupWindow.isShowing()))) {
                if (this.popupWindow != null && this.popupWindow.isShowing()) {
                    getLocationOnScreen(this.location);
                    float x = motionEvent.getX() + ((float) this.location[0]);
                    float y = motionEvent.getY() + ((float) this.location[1]);
                    this.popupLayout.getLocationOnScreen(this.location);
                    float f = x - ((float) this.location[0]);
                    float f2 = y - ((float) this.location[1]);
                    this.selectedMenuView = null;
                    for (int i = 0; i < this.popupLayout.getItemsCount(); i++) {
                        View itemAt = this.popupLayout.getItemAt(i);
                        itemAt.getHitRect(this.rect);
                        if (((Integer) itemAt.getTag()).intValue() < 100) {
                            if (this.rect.contains((int) f, (int) f2)) {
                                itemAt.setPressed(true);
                                itemAt.setSelected(true);
                                if (VERSION.SDK_INT >= 21) {
                                    if (VERSION.SDK_INT == 21) {
                                        itemAt.getBackground().setVisible(true, false);
                                    }
                                    itemAt.drawableHotspotChanged(f, f2 - ((float) itemAt.getTop()));
                                }
                                this.selectedMenuView = itemAt;
                            } else {
                                itemAt.setPressed(false);
                                itemAt.setSelected(false);
                                if (VERSION.SDK_INT == 21) {
                                    itemAt.getBackground().setVisible(false, false);
                                }
                            }
                        }
                    }
                }
            } else if (motionEvent.getY() > ((float) getHeight())) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                toggleSubMenu();
                return true;
            }
        } else if (this.popupWindow != null && this.popupWindow.isShowing() && motionEvent.getActionMasked() == 1) {
            if (this.selectedMenuView != null) {
                this.selectedMenuView.setSelected(false);
                if (this.parentMenu != null) {
                    this.parentMenu.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                } else if (this.delegate != null) {
                    this.delegate.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                }
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                this.popupWindow.dismiss();
            }
        } else if (this.selectedMenuView != null) {
            this.selectedMenuView.setSelected(false);
            this.selectedMenuView = null;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void openSearch(boolean z) {
        if (this.searchContainer != null && this.searchContainer.getVisibility() != 0 && this.parentMenu != null) {
            this.parentMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(z));
        }
    }

    public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener actionBarMenuItemSearchListener) {
        this.listener = actionBarMenuItemSearchListener;
        return this;
    }

    public ActionBarMenuItem setAllowCloseAnimation(boolean z) {
        this.allowCloseAnimation = z;
        return this;
    }

    public void setDelegate(ActionBarMenuItemDelegate actionBarMenuItemDelegate) {
        this.delegate = actionBarMenuItemDelegate;
    }

    public void setIcon(int i) {
        this.iconView.setImageResource(i);
    }

    public void setIcon(Drawable drawable) {
        this.iconView.setImageDrawable(drawable);
    }

    public void setIconColor(int i) {
        this.iconView.setColorFilter(i, Mode.SRC_IN);
    }

    public ActionBarMenuItem setIsSearchField(boolean z) {
        if (this.parentMenu != null) {
            if (z && this.searchContainer == null) {
                this.searchContainer = new FrameLayout(getContext());
                this.parentMenu.addView(this.searchContainer, 0);
                LayoutParams layoutParams = (LayoutParams) this.searchContainer.getLayoutParams();
                layoutParams.weight = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                layoutParams.width = 0;
                layoutParams.height = -1;
                layoutParams.leftMargin = AndroidUtilities.dp(6.0f);
                this.searchContainer.setLayoutParams(layoutParams);
                this.searchContainer.setVisibility(8);
                this.searchField = new EditText(getContext());
                this.searchField.setTextSize(1, 18.0f);
                this.searchField.setHintTextColor(-1996488705);
                this.searchField.setTextColor(-1);
                this.searchField.setSingleLine(true);
                this.searchField.setBackgroundResource(0);
                this.searchField.setPadding(0, 0, 0, 0);
                this.searchField.setInputType(this.searchField.getInputType() | AccessibilityNodeInfoCompat.ACTION_COLLAPSE);
                this.searchField.setCustomSelectionActionModeCallback(new C09666());
                this.searchField.setOnEditorActionListener(new C09677());
                this.searchField.addTextChangedListener(new C09688());
                try {
                    Field declaredField = TextView.class.getDeclaredField("mCursorDrawableRes");
                    declaredField.setAccessible(true);
                    declaredField.set(this.searchField, Integer.valueOf(C0338R.drawable.search_carret));
                } catch (Exception e) {
                }
                this.searchField.setImeOptions(33554435);
                this.searchField.setTextIsSelectable(false);
                this.searchContainer.addView(this.searchField);
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.searchField.getLayoutParams();
                layoutParams2.width = -1;
                layoutParams2.gravity = 16;
                layoutParams2.height = AndroidUtilities.dp(36.0f);
                layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
                this.searchField.setLayoutParams(layoutParams2);
                this.clearButton = new ImageView(getContext());
                this.clearButton.setImageResource(C0338R.drawable.ic_close_white);
                this.clearButton.setScaleType(ScaleType.CENTER);
                this.clearButton.setOnClickListener(new C09699());
                this.searchContainer.addView(this.clearButton);
                layoutParams2 = (FrameLayout.LayoutParams) this.clearButton.getLayoutParams();
                layoutParams2.width = AndroidUtilities.dp(48.0f);
                layoutParams2.gravity = 21;
                layoutParams2.height = -1;
                this.clearButton.setLayoutParams(layoutParams2);
            }
            this.isSearchField = z;
        }
        return this;
    }

    public ActionBarMenuItem setOverrideMenuClick(boolean z) {
        this.overrideMenuClick = z;
        return this;
    }

    public void setShowFromBottom(boolean z) {
        this.showFromBottom = z;
        if (this.popupLayout != null) {
            this.popupLayout.setShowedFromBotton(this.showFromBottom);
        }
    }

    public void setSubMenuOpenSide(int i) {
        this.subMenuOpenSide = i;
    }

    public void showSubItem(int i) {
        View findViewWithTag = this.popupLayout.findViewWithTag(Integer.valueOf(i));
        if (findViewWithTag != null) {
            findViewWithTag.setVisibility(0);
        }
    }

    public boolean toggleSearch(boolean z) {
        if (this.searchContainer == null) {
            return false;
        }
        if (this.searchContainer.getVisibility() != 0) {
            this.searchContainer.setVisibility(0);
            setVisibility(8);
            this.searchField.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.searchField.requestFocus();
            if (z) {
                AndroidUtilities.showKeyboard(this.searchField);
            }
            if (this.listener != null) {
                this.listener.onSearchExpand();
            }
            return true;
        } else if (this.listener != null && (this.listener == null || !this.listener.canCollapseSearch())) {
            return false;
        } else {
            this.searchContainer.setVisibility(8);
            this.searchField.clearFocus();
            setVisibility(0);
            AndroidUtilities.hideKeyboard(this.searchField);
            if (this.listener == null) {
                return false;
            }
            this.listener.onSearchCollapse();
            return false;
        }
    }

    public void toggleSubMenu() {
        if (this.popupLayout != null) {
            if (this.showMenuRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.showMenuRunnable);
                this.showMenuRunnable = null;
            }
            if (this.popupWindow == null || !this.popupWindow.isShowing()) {
                if (this.popupWindow == null) {
                    this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                    if (VERSION.SDK_INT >= 19) {
                        this.popupWindow.setAnimationStyle(0);
                    } else {
                        this.popupWindow.setAnimationStyle(C0338R.style.PopupAnimation);
                    }
                    this.popupWindow.setOutsideTouchable(true);
                    this.popupWindow.setClippingEnabled(true);
                    this.popupWindow.setInputMethodMode(2);
                    this.popupWindow.setSoftInputMode(0);
                    this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
                    this.popupWindow.getContentView().setFocusableInTouchMode(true);
                    this.popupWindow.getContentView().setOnKeyListener(new C09655());
                }
                this.processedPopupClick = false;
                this.popupWindow.setFocusable(true);
                if (this.popupLayout.getMeasuredWidth() == 0) {
                    updateOrShowPopup(true, true);
                } else {
                    updateOrShowPopup(true, false);
                }
                this.popupWindow.startAnimation();
                return;
            }
            this.popupWindow.dismiss();
        }
    }
}
