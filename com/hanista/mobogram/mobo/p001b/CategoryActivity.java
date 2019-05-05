package com.hanista.mobogram.mobo.p001b;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.mobo.DialogSelectActivity.DialogSelectActivity;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p001b.CategoryNameActivity.CategoryNameActivity;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
import com.hanista.mobogram.ui.ActionBar.MenuDrawable;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.DialogsActivity;
import com.hanista.mobogram.ui.LaunchActivity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.b.b */
public class CategoryActivity extends BaseFragment implements NotificationCenterDelegate {
    private static boolean f174g;
    private CategoryAdapter f175a;
    private TextView f176b;
    private RecyclerListView f177c;
    private DataBaseAccess f178d;
    private List<Category> f179e;
    private LinearLayoutManager f180f;
    private boolean f181h;

    /* renamed from: com.hanista.mobogram.mobo.b.b.1 */
    class CategoryActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ CategoryActivity f162a;

        /* renamed from: com.hanista.mobogram.mobo.b.b.1.1 */
        class CategoryActivity implements CategoryNameActivity {
            final /* synthetic */ CategoryActivity f161a;

            CategoryActivity(CategoryActivity categoryActivity) {
                this.f161a = categoryActivity;
            }

            public void m288a(Category category) {
                this.f161a.f162a.m294a(category);
            }
        }

        CategoryActivity(CategoryActivity categoryActivity) {
            this.f162a = categoryActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                if (this.f162a.f181h) {
                    this.f162a.finishFragment();
                } else if (this.f162a.parentLayout != null) {
                    this.f162a.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                }
            } else if (i == 2) {
                BaseFragment categoryNameActivity = new CategoryNameActivity(null);
                categoryNameActivity.m329a(new CategoryActivity(this));
                this.f162a.presentFragment(categoryNameActivity);
            } else if (i == 3) {
                this.f162a.presentFragment(new CategorySettingsActivity());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.b.2 */
    class CategoryActivity implements OnTouchListener {
        final /* synthetic */ CategoryActivity f163a;

        CategoryActivity(CategoryActivity categoryActivity) {
            this.f163a = categoryActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.b.3 */
    class CategoryActivity extends LinearLayoutManager {
        final /* synthetic */ CategoryActivity f164a;

        CategoryActivity(CategoryActivity categoryActivity, Context context) {
            this.f164a = categoryActivity;
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.b.4 */
    class CategoryActivity implements OnItemClickListener {
        final /* synthetic */ CategoryActivity f165a;

        CategoryActivity(CategoryActivity categoryActivity) {
            this.f165a = categoryActivity;
        }

        public void onItemClick(View view, int i) {
            Category category = (Category) this.f165a.f179e.get(i);
            Bundle bundle = new Bundle();
            if (category.m276a() != null) {
                bundle.putLong("categoryId", category.m276a().longValue());
            }
            this.f165a.presentFragment(new DialogsActivity(bundle));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.b.5 */
    class CategoryActivity implements OnItemLongClickListener {
        final /* synthetic */ CategoryActivity f166a;

        CategoryActivity(CategoryActivity categoryActivity) {
            this.f166a = categoryActivity;
        }

        public boolean onItemClick(View view, int i) {
            if (((Category) this.f166a.f179e.get(i)).m276a() != null) {
                this.f166a.m293a(i);
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.b.6 */
    class CategoryActivity implements OnClickListener {
        final /* synthetic */ int f168a;
        final /* synthetic */ CategoryActivity f169b;

        /* renamed from: com.hanista.mobogram.mobo.b.b.6.1 */
        class CategoryActivity implements DialogSelectActivity {
            final /* synthetic */ CategoryActivity f167a;

            CategoryActivity(CategoryActivity categoryActivity) {
                this.f167a = categoryActivity;
            }

            public void m290a(List<Long> list) {
                for (Long f : list) {
                    this.f167a.f169b.f178d.m880f(f);
                }
                this.f167a.f169b.parentLayout.rebuildAllFragmentViews(false);
                this.f167a.f169b.m292a();
            }
        }

        CategoryActivity(CategoryActivity categoryActivity, int i) {
            this.f169b = categoryActivity;
            this.f168a = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Category category = (Category) this.f169b.f179e.get(this.f168a);
            Bundle bundle;
            if (i == 0) {
                bundle = new Bundle();
                bundle.putLong("categoryId", category.m276a().longValue());
                this.f169b.presentFragment(new CategoryNameActivity(bundle));
            } else if (i == 1) {
                this.f169b.m294a(category);
            } else if (i == 2) {
                bundle = new Bundle();
                bundle.putLong("categoryId", category.m276a().longValue());
                BaseFragment dialogSelectActivity = new com.hanista.mobogram.mobo.DialogSelectActivity(bundle);
                dialogSelectActivity.m536a(new CategoryActivity(this));
                this.f169b.presentFragment(dialogSelectActivity);
            } else if (i == 3 && MoboConstants.f1308A == 1) {
                if (this.f168a != 1) {
                    category = (Category) this.f169b.f179e.get(this.f168a);
                    r1 = (Category) this.f169b.f179e.get(this.f168a - 1);
                    r2 = r1.m283c();
                    r1.m278a(category.m283c());
                    category.m278a(r2);
                    CategoryUtil.m344a(category);
                    CategoryUtil.m344a(r1);
                    this.f169b.m292a();
                }
            } else if (i == 4 && MoboConstants.f1308A == 1) {
                if (this.f168a != this.f169b.f179e.size() - 1) {
                    category = (Category) this.f169b.f179e.get(this.f168a + 1);
                    r1 = (Category) this.f169b.f179e.get(this.f168a);
                    r2 = category.m283c();
                    category.m278a(r1.m283c());
                    r1.m278a(r2);
                    CategoryUtil.m344a(r1);
                    CategoryUtil.m344a(category);
                    this.f169b.m292a();
                }
            } else if ((i == 5 && MoboConstants.f1308A == 1) || (i == 3 && MoboConstants.f1308A != 1)) {
                this.f169b.m300b((Category) this.f169b.f179e.get(this.f168a));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.b.7 */
    class CategoryActivity implements DialogSelectActivity {
        final /* synthetic */ Category f170a;
        final /* synthetic */ CategoryActivity f171b;

        CategoryActivity(CategoryActivity categoryActivity, Category category) {
            this.f171b = categoryActivity;
            this.f170a = category;
        }

        public void m291a(List<Long> list) {
            for (Long a : list) {
                this.f171b.f178d.m848a(this.f170a.m276a(), a);
            }
            this.f171b.parentLayout.rebuildAllFragmentViews(false);
            this.f171b.m292a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.b.8 */
    class CategoryActivity implements OnClickListener {
        final /* synthetic */ Category f172a;
        final /* synthetic */ CategoryActivity f173b;

        CategoryActivity(CategoryActivity categoryActivity, Category category) {
            this.f173b = categoryActivity;
            this.f172a = category;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            CategoryUtil.m349a(this.f172a.m276a());
            if (this.f173b.parentLayout.fragmentsStack.size() > 0) {
                this.f173b.parentLayout.fragmentsStack.get(0);
            }
            this.f173b.parentLayout.rebuildAllFragmentViews(false);
            this.f173b.m292a();
        }
    }

    public CategoryActivity() {
        this.f179e = new ArrayList();
    }

    public CategoryActivity(Bundle bundle) {
        super(bundle);
        this.f179e = new ArrayList();
    }

    private void m292a() {
        m299b();
        this.f175a.m312a(this.f179e);
        this.f175a.notifyDataSetChanged();
        CategoryController.m317a(this.f179e);
    }

    private void m293a(int i) {
        Builder builder = new Builder(getParentActivity());
        List arrayList = new ArrayList();
        arrayList.add(LocaleController.getString("EditName", C0338R.string.EditName));
        arrayList.add(LocaleController.getString("AddChatToCategory", C0338R.string.AddChatToCategory));
        arrayList.add(LocaleController.getString("DeleteChatFromCategory", C0338R.string.DeleteChatFromCategory));
        if (MoboConstants.f1308A == 1) {
            arrayList.add(LocaleController.getString("GoUp", C0338R.string.GoUp));
            arrayList.add(LocaleController.getString("GoDown", C0338R.string.GoDown));
        }
        arrayList.add(LocaleController.getString("DeleteCategory", C0338R.string.DeleteCategory));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new CategoryActivity(this, i));
        showDialog(builder.create());
    }

    private void m294a(Category category) {
        Bundle bundle = new Bundle();
        bundle.putInt("dialogsType", 20);
        BaseFragment dialogSelectActivity = new com.hanista.mobogram.mobo.DialogSelectActivity(bundle);
        dialogSelectActivity.m536a(new CategoryActivity(this, category));
        presentFragment(dialogSelectActivity);
    }

    private void m299b() {
        this.f179e.clear();
        Collection i = this.f178d.m891i();
        if (i.size() == 0 && !this.f181h) {
            m303c();
        }
        if (this.f181h && this.parentLayout.fragmentsStack.size() > 0) {
            BaseFragment baseFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(0);
            if (i.size() == 0) {
                if (baseFragment instanceof CategoryActivity) {
                    this.parentLayout.fragmentsStack.remove(0);
                    if (this.parentLayout.fragmentsStack.size() == 1) {
                        this.parentLayout.fragmentsStack.add(0, new DialogsActivity(null));
                    }
                }
            } else if (!(baseFragment instanceof CategoryActivity) && MoboConstants.f1359z) {
                this.parentLayout.fragmentsStack.add(0, new CategoryActivity());
            }
        }
        if (i.size() > 0) {
            if (!this.f181h) {
                this.f179e.add(new Category(null, LocaleController.getString("All", C0338R.string.All), Integer.valueOf(-1)));
            }
            this.f179e.addAll(i);
        }
    }

    private void m300b(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.formatString("AreYouSureDeleteCategory", C0338R.string.AreYouSureDeleteCategory, new Object[0]));
        builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new CategoryActivity(this, category));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void m303c() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public View createView(Context context) {
        if (this.f181h) {
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
            this.actionBar.setTitle(LocaleController.getString("CategoryManagement", C0338R.string.CategoryManagement));
        } else {
            this.actionBar.setBackButtonDrawable(new MenuDrawable());
            this.actionBar.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new CategoryActivity(this));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_add_category);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            createMenu.addItemWithWidth(2, drawable, AndroidUtilities.dp(56.0f));
            drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_settings);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            createMenu.addItemWithWidth(3, drawable, AndroidUtilities.dp(56.0f));
        } else {
            createMenu.addItem(2, (int) C0338R.drawable.ic_add_category);
            createMenu.addItem(3, (int) C0338R.drawable.ic_ab_settings);
        }
        this.f178d = new DataBaseAccess();
        this.f175a = new CategoryAdapter(context, this.f179e);
        this.fragmentView = new FrameLayout(context);
        initThemeBackground(this.fragmentView);
        View linearLayout = new LinearLayout(context);
        linearLayout.setVisibility(4);
        linearLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new CategoryActivity(this));
        this.f176b = new TextView(context);
        this.f176b.setTypeface(FontUtil.m1176a().m1161d());
        this.f176b.setTextColor(-8355712);
        this.f176b.setTextSize(1, 18.0f);
        this.f176b.setGravity(17);
        this.f176b.setText(LocaleController.getString("NoCategoryHelp", C0338R.string.NoCategoryHelp));
        linearLayout.addView(this.f176b);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.f176b.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.f176b.setLayoutParams(layoutParams2);
        if (ThemeUtil.m2490b()) {
            linearLayout.setBackgroundColor(AdvanceTheme.f2514y);
            this.f176b.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.f2489Z, -8355712));
        }
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.f177c = new RecyclerListView(context);
        initThemeBackground(this.f177c);
        this.f177c.setEmptyView(linearLayout);
        this.f177c.setVerticalScrollBarEnabled(false);
        this.f177c.setScrollBarStyle(33554432);
        this.f177c.setAdapter(this.f175a);
        ((FrameLayout) this.fragmentView).addView(this.f177c);
        layoutParams = (LayoutParams) this.f177c.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.f177c.setLayoutParams(layoutParams);
        this.f180f = new CategoryActivity(this, context);
        this.f180f.setOrientation(1);
        this.f177c.setLayoutManager(this.f180f);
        this.f177c.setOnItemClickListener(new CategoryActivity(this));
        this.f177c.setOnItemLongClickListener(new CategoryActivity(this));
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            CategoryController.m317a(this.f179e);
        } else if (i == NotificationCenter.categoriesInfoDidLoaded && this.f175a != null) {
            this.f175a.notifyDataSetChanged();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.categoriesInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
        if (getArguments() != null) {
            this.f181h = this.arguments.getBoolean("fromMenu", false);
        }
        if (!(f174g || this.f181h)) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            f174g = true;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.categoriesInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    public void onResume() {
        super.onResume();
        if (getParentActivity() != null) {
            m292a();
        }
        initThemeActionBar();
    }
}
