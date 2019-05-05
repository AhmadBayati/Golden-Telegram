package com.hanista.mobogram.mobo.p001b;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.b.f */
public class CategoryNameActivity extends BaseFragment {
    private EditText f225a;
    private View f226b;
    private Category f227c;
    private DataBaseAccess f228d;
    private CategoryNameActivity f229e;

    /* renamed from: com.hanista.mobogram.mobo.b.f.a */
    public interface CategoryNameActivity {
        void m287a(Category category);
    }

    /* renamed from: com.hanista.mobogram.mobo.b.f.1 */
    class CategoryNameActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ CategoryNameActivity f221a;

        CategoryNameActivity(CategoryNameActivity categoryNameActivity) {
            this.f221a = categoryNameActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f221a.finishFragment();
            } else if (i == 1 && this.f221a.f225a.getText().length() != 0) {
                this.f221a.m325a();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.f.2 */
    class CategoryNameActivity implements OnTouchListener {
        final /* synthetic */ CategoryNameActivity f222a;

        CategoryNameActivity(CategoryNameActivity categoryNameActivity) {
            this.f222a = categoryNameActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.f.3 */
    class CategoryNameActivity implements OnEditorActionListener {
        final /* synthetic */ CategoryNameActivity f223a;

        CategoryNameActivity(CategoryNameActivity categoryNameActivity) {
            this.f223a = categoryNameActivity;
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            this.f223a.f226b.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.f.4 */
    class CategoryNameActivity implements Runnable {
        final /* synthetic */ CategoryNameActivity f224a;

        CategoryNameActivity(CategoryNameActivity categoryNameActivity) {
            this.f224a = categoryNameActivity;
        }

        public void run() {
            if (this.f224a.f225a != null) {
                this.f224a.f225a.requestFocus();
                AndroidUtilities.showKeyboard(this.f224a.f225a);
            }
        }
    }

    public CategoryNameActivity(Bundle bundle) {
        super(bundle);
    }

    private void m325a() {
        if (this.f225a.getText() == null) {
            AndroidUtilities.shakeView(this.f225a, 2.0f, 0);
            return;
        }
        this.f227c.m280a(this.f225a.getText().toString());
        this.f227c.m279a(CategoryUtil.m344a(this.f227c));
        this.parentLayout.rebuildAllFragmentViews(false);
        if (this.f229e != null) {
            this.f229e.m287a(this.f227c);
            removeSelfFromStack();
            return;
        }
        finishFragment();
    }

    private void m326b() {
        if (ThemeUtil.m2490b()) {
            if (this.fragmentView != null) {
                this.fragmentView.setBackgroundColor(AdvanceTheme.f2497h);
            }
            if (this.f225a != null) {
                if (this.f225a.getBackground() != null) {
                    this.f225a.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.f225a.setHintTextColor(AdvanceTheme.f2495f);
                this.f225a.setTextColor(AdvanceTheme.f2494e);
            }
        }
    }

    public void m329a(CategoryNameActivity categoryNameActivity) {
        this.f229e = categoryNameActivity;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.f227c.m276a() == null) {
            this.actionBar.setTitle(LocaleController.getString("NewCategory", C0338R.string.NewCategory));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditCategory", C0338R.string.EditCategory));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CategoryNameActivity(this));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.f226b = createMenu.addItemWithWidth(1, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.f226b = createMenu.addItemWithWidth(1, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        View linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new CategoryNameActivity(this));
        this.f225a = new EditText(context);
        this.f225a.setTextSize(1, 18.0f);
        this.f225a.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.f225a.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f225a.setMaxLines(1);
        this.f225a.setLines(1);
        this.f225a.setSingleLine(true);
        this.f225a.setGravity(LocaleController.isRTL ? 5 : 3);
        this.f225a.setInputType(49152);
        this.f225a.setImeOptions(5);
        this.f225a.setHint(LocaleController.getString("CategoryName", C0338R.string.CategoryName));
        AndroidUtilities.clearCursorDrawable(this.f225a);
        linearLayout.addView(this.f225a, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.f225a.setOnEditorActionListener(new CategoryNameActivity(this));
        if (this.f227c.m276a() != null) {
            this.f225a.setText(this.f227c.m281b());
            this.f225a.setSelection(this.f225a.length());
        }
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f228d = new DataBaseAccess();
        if (getArguments() != null) {
            long j = this.arguments.getLong("categoryId", 0);
            if (j == 0) {
                this.f227c = new Category();
            } else {
                this.f227c = this.f228d.m835a(Long.valueOf(j), false);
            }
        } else {
            this.f227c = new Category();
        }
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).getBoolean("view_animations", true)) {
            this.f225a.requestFocus();
            AndroidUtilities.showKeyboard(this.f225a);
        }
        initThemeActionBar();
        m326b();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new CategoryNameActivity(this), 100);
        }
    }
}
