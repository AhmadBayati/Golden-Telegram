package com.hanista.mobogram.mobo.p001b;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.DialogsActivity;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.b.g */
public class CategorySettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f235a;
    private CategorySettingsActivity f236b;
    private int f237c;
    private int f238d;
    private int f239e;
    private int f240f;
    private int f241g;

    /* renamed from: com.hanista.mobogram.mobo.b.g.1 */
    class CategorySettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ CategorySettingsActivity f230a;

        CategorySettingsActivity(CategorySettingsActivity categorySettingsActivity) {
            this.f230a = categorySettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f230a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.g.2 */
    class CategorySettingsActivity implements OnItemClickListener {
        final /* synthetic */ CategorySettingsActivity f232a;

        /* renamed from: com.hanista.mobogram.mobo.b.g.2.1 */
        class CategorySettingsActivity implements OnClickListener {
            final /* synthetic */ CategorySettingsActivity f231a;

            CategorySettingsActivity(CategorySettingsActivity categorySettingsActivity) {
                this.f231a = categorySettingsActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                String str = "category_list_order";
                if (i == 0) {
                    edit.putInt(str, 1);
                } else if (i == 1) {
                    edit.putInt(str, 2);
                } else if (i == 2) {
                    edit.putInt(str, 3);
                }
                edit.commit();
                if (this.f231a.f232a.f235a != null) {
                    this.f231a.f232a.f235a.invalidateViews();
                }
                MoboConstants.m1379a();
                this.f231a.f232a.parentLayout.rebuildAllFragmentViews(false);
            }
        }

        CategorySettingsActivity(CategorySettingsActivity categorySettingsActivity) {
            this.f232a = categorySettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z2;
            if (i == this.f232a.f238d) {
                z2 = MoboConstants.f1359z;
                edit.putBoolean("show_categories_at_startup", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z2);
                }
                MoboConstants.m1379a();
                if (this.f232a.parentLayout.fragmentsStack.size() > 0) {
                    BaseFragment baseFragment = (BaseFragment) this.f232a.parentLayout.fragmentsStack.get(0);
                    if (CategoryUtil.m352c()) {
                        if (!(baseFragment instanceof CategoryActivity) && MoboConstants.f1359z) {
                            this.f232a.parentLayout.fragmentsStack.add(0, new CategoryActivity());
                        }
                    } else if (baseFragment instanceof CategoryActivity) {
                        this.f232a.parentLayout.fragmentsStack.remove(0);
                        if (this.f232a.parentLayout.fragmentsStack.size() == 1) {
                            this.f232a.parentLayout.fragmentsStack.add(0, new DialogsActivity(null));
                        }
                    }
                }
                if (this.f232a.f235a != null) {
                    this.f232a.f235a.invalidateViews();
                }
            } else if (i == this.f232a.f240f) {
                Builder builder = new Builder(this.f232a.getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.getString("CategoryOrderManual", C0338R.string.CategoryOrderManual), LocaleController.getString("CategoryOrderUnreadCount", C0338R.string.CategoryOrderUnreadCount), LocaleController.getString("CategoryOrderUnreadMutedCount", C0338R.string.CategoryOrderUnreadMutedCount)}, new CategorySettingsActivity(this));
                this.f232a.showDialog(builder.create());
            } else if (i == this.f232a.f239e) {
                z2 = MoboConstants.f1358y;
                edit.putBoolean("show_category_icon", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
                MoboConstants.m1379a();
                this.f232a.parentLayout.rebuildAllFragmentViews(false);
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.b.g.a */
    private class CategorySettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ CategorySettingsActivity f233a;
        private Context f234b;

        public CategorySettingsActivity(CategorySettingsActivity categorySettingsActivity, Context context) {
            this.f233a = categorySettingsActivity;
            this.f234b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f233a.f241g;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f233a.f238d || i == this.f233a.f239e) ? 8 : i == this.f233a.f240f ? 6 : i == this.f233a.f237c ? 7 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f234b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f234b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f234b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f234b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f234b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        if (i == this.f233a.f238d) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowCategoriesOnStartup", C0338R.string.ShowCategoriesOnStartup), LocaleController.getString("ShowCategoriesOnStartupDetail", C0338R.string.ShowCategoriesOnStartupDetail), MoboConstants.f1359z, true);
                        } else if (i == this.f233a.f239e) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowCategoryIcon", C0338R.string.ShowCategoryIcon), LocaleController.getString("ShowCategoryIconDetail", C0338R.string.ShowCategoryIconDetail), MoboConstants.f1358y, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f234b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f234b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f234b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f234b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            if (i == this.f233a.f240f) {
                                textDetailSettingsCell.setMultilineDetail(false);
                                int i2 = MoboConstants.f1308A;
                                if (i2 == 1) {
                                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("CategoryOrder", C0338R.string.CategoryOrder), LocaleController.getString("CategoryOrderManual", C0338R.string.CategoryOrderManual), true);
                                } else if (i2 == 2) {
                                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("CategoryOrder", C0338R.string.CategoryOrder), LocaleController.getString("CategoryOrderUnreadCount", C0338R.string.CategoryOrderUnreadCount), true);
                                } else if (i2 == 3) {
                                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("CategoryOrder", C0338R.string.CategoryOrder), LocaleController.getString("CategoryOrderUnreadMutedCount", C0338R.string.CategoryOrderUnreadMutedCount), true);
                                }
                            }
                            return textSettingsCell;
                        }
                    }
                }
            }
        }

        public int getViewTypeCount() {
            return 9;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == this.f233a.f238d || i == this.f233a.f240f || i == this.f233a.f239e;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CategorySettings", C0338R.string.CategorySettings));
        this.actionBar.setActionBarMenuOnItemClick(new CategorySettingsActivity(this));
        this.f236b = new CategorySettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f235a = new ListView(context);
        initThemeBackground(this.f235a);
        this.f235a.setDivider(null);
        this.f235a.setDividerHeight(0);
        this.f235a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f235a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f235a, LayoutHelper.createFrame(-1, -1, 51));
        this.f235a.setAdapter(this.f236b);
        this.f235a.setOnItemClickListener(new CategorySettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f235a.invalidateViews();
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.f241g = 0;
        int i = this.f241g;
        this.f241g = i + 1;
        this.f238d = i;
        i = this.f241g;
        this.f241g = i + 1;
        this.f239e = i;
        i = this.f241g;
        this.f241g = i + 1;
        this.f240f = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
