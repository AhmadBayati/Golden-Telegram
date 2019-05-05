package com.hanista.mobogram.mobo.dialogdm;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.dialogdm.d */
public class DialogDmDetailActivity extends BaseFragment {
    private ListView f615a;
    private DialogDmDetailActivity f616b;
    private int f617c;
    private int f618d;
    private int f619e;
    private int f620f;
    private DialogDmDetailActivity f621g;
    private int f622h;
    private int f623i;

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.a */
    public interface DialogDmDetailActivity {
        void m569a(int i, int i2);
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.1 */
    class DialogDmDetailActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ DialogDmDetailActivity f605a;

        DialogDmDetailActivity(DialogDmDetailActivity dialogDmDetailActivity) {
            this.f605a = dialogDmDetailActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f605a.finishFragment();
            } else if (i == 1) {
                if (this.f605a.f621g != null) {
                    this.f605a.f621g.m569a(this.f605a.f622h, this.f605a.f623i);
                }
                this.f605a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.2 */
    class DialogDmDetailActivity implements OnItemClickListener {
        final /* synthetic */ DialogDmDetailActivity f611a;

        /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.2.1 */
        class DialogDmDetailActivity implements OnClickListener {
            final /* synthetic */ DialogDmDetailActivity f606a;

            DialogDmDetailActivity(DialogDmDetailActivity dialogDmDetailActivity) {
                this.f606a = dialogDmDetailActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        this.f606a.f611a.f623i = -1;
                        break;
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        this.f606a.f611a.f623i = -2;
                        break;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        this.f606a.f611a.f623i = -3;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        this.f606a.f611a.f623i = -4;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        this.f606a.f611a.f623i = -5;
                        break;
                    case VideoPlayer.STATE_ENDED /*5*/:
                        this.f606a.f611a.f623i = -6;
                        break;
                    case Method.TRACE /*6*/:
                        this.f606a.f611a.f623i = 0;
                        break;
                }
                if (this.f606a.f611a.f615a != null) {
                    this.f606a.f611a.f615a.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.2.2 */
        class DialogDmDetailActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f607a;
            final /* synthetic */ DialogDmDetailActivity f608b;

            DialogDmDetailActivity(DialogDmDetailActivity dialogDmDetailActivity, boolean[] zArr) {
                this.f608b = dialogDmDetailActivity;
                this.f607a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f607a[intValue] = !this.f607a[intValue];
                checkBoxCell.setChecked(this.f607a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.2.3 */
        class DialogDmDetailActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f609a;
            final /* synthetic */ DialogDmDetailActivity f610b;

            DialogDmDetailActivity(DialogDmDetailActivity dialogDmDetailActivity, boolean[] zArr) {
                this.f610b = dialogDmDetailActivity;
                this.f609a = zArr;
            }

            public void onClick(View view) {
                int i = 0;
                try {
                    if (this.f610b.f611a.visibleDialog != null) {
                        this.f610b.f611a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i2 = 0;
                while (i < 6) {
                    if (this.f609a[i]) {
                        if (i == 0) {
                            i2 |= 1;
                        } else if (i == 1) {
                            i2 |= 2;
                        } else if (i == 2) {
                            i2 |= 4;
                        } else if (i == 3) {
                            i2 |= 8;
                        } else if (i == 4) {
                            i2 |= 16;
                        } else if (i == 5) {
                            i2 |= 32;
                        }
                    }
                    i++;
                }
                this.f610b.f611a.f622h = i2;
                if (this.f610b.f611a.f615a != null) {
                    this.f610b.f611a.f615a.invalidateViews();
                }
            }
        }

        DialogDmDetailActivity(DialogDmDetailActivity dialogDmDetailActivity) {
            this.f611a = dialogDmDetailActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i == this.f611a.f617c) {
                Builder builder = new Builder(this.f611a.getParentActivity());
                List arrayList = new ArrayList();
                arrayList.add(LocaleController.getString("Last10Messages", C0338R.string.Last10Messages));
                arrayList.add(LocaleController.getString("Last20Messages", C0338R.string.Last20Messages));
                arrayList.add(LocaleController.getString("Last50Messages", C0338R.string.Last50Messages));
                arrayList.add(LocaleController.getString("Last100Messages", C0338R.string.Last100Messages));
                arrayList.add(LocaleController.getString("Last200Messages", C0338R.string.Last200Messages));
                arrayList.add(LocaleController.getString("Last500Messages", C0338R.string.Last500Messages));
                arrayList.add(LocaleController.getString("AllMessages", C0338R.string.AllMessages));
                builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new DialogDmDetailActivity(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                this.f611a.showDialog(builder.create());
            } else if (i == this.f611a.f618d) {
                BottomSheet.Builder builder2 = new BottomSheet.Builder(this.f611a.getParentActivity());
                builder2.setApplyTopPadding(false);
                builder2.setApplyBottomPadding(false);
                View linearLayout = new LinearLayout(this.f611a.getParentActivity());
                linearLayout.setOrientation(1);
                boolean[] zArr = new boolean[6];
                for (int i2 = 0; i2 < 6; i2++) {
                    String str = null;
                    if (i2 == 0) {
                        zArr[i2] = (this.f611a.f622h & 1) != 0;
                        str = LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
                    } else if (i2 == 1) {
                        zArr[i2] = (this.f611a.f622h & 2) != 0;
                        str = LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
                    } else if (i2 == 2) {
                        zArr[i2] = (this.f611a.f622h & 4) != 0;
                        str = LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
                    } else if (i2 == 3) {
                        zArr[i2] = (this.f611a.f622h & 8) != 0;
                        str = LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                    } else if (i2 == 4) {
                        zArr[i2] = (this.f611a.f622h & 16) != 0;
                        str = LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
                    } else if (i2 == 5) {
                        zArr[i2] = (this.f611a.f622h & 32) != 0;
                        str = LocaleController.getString("AttachGif", C0338R.string.AttachGif);
                    }
                    View checkBoxCell = new CheckBoxCell(this.f611a.getParentActivity());
                    checkBoxCell.setTag(Integer.valueOf(i2));
                    checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                    checkBoxCell.setText(str, TtmlNode.ANONYMOUS_REGION_ID, zArr[i2], true);
                    checkBoxCell.setOnClickListener(new DialogDmDetailActivity(this, zArr));
                }
                View bottomSheetCell = new BottomSheetCell(this.f611a.getParentActivity(), 1);
                bottomSheetCell.setBackgroundResource(C0338R.drawable.list_selector);
                bottomSheetCell.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                bottomSheetCell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                bottomSheetCell.setOnClickListener(new DialogDmDetailActivity(this, zArr));
                linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                builder2.setCustomView(linearLayout);
                this.f611a.showDialog(builder2.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.3 */
    class DialogDmDetailActivity implements OnClickListener {
        final /* synthetic */ DialogDmDetailActivity f612a;

        DialogDmDetailActivity(DialogDmDetailActivity dialogDmDetailActivity) {
            this.f612a = dialogDmDetailActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.d.b */
    private class DialogDmDetailActivity extends BaseFragmentAdapter {
        final /* synthetic */ DialogDmDetailActivity f613a;
        private Context f614b;

        public DialogDmDetailActivity(DialogDmDetailActivity dialogDmDetailActivity, Context context) {
            this.f613a = dialogDmDetailActivity;
            this.f614b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f613a.f620f;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == this.f613a.f619e ? 1 : (i == this.f613a.f617c || i == this.f613a.f618d) ? 6 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                if (view == null) {
                    return new EmptyCell(this.f614b);
                }
            } else if (itemViewType == 1) {
                r1 = view == null ? new TextInfoPrivacyCell(this.f614b) : view;
                if (i != this.f613a.f619e) {
                    return r1;
                }
                ((TextInfoPrivacyCell) r1).setText(LocaleController.getString("DialogDmDetailSaveHelp", C0338R.string.DialogDmDetailSaveHelp));
                r1.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                return r1;
            } else if (itemViewType == 2) {
                r1 = view == null ? new TextSettingsCell(this.f614b) : view;
                TextSettingsCell textSettingsCell = (TextSettingsCell) r1;
                return r1;
            } else if (itemViewType == 3) {
                r1 = view == null ? new TextCheckCell(this.f614b) : view;
                TextCheckCell textCheckCell = (TextCheckCell) r1;
                return r1;
            } else if (itemViewType == 8) {
                r1 = view == null ? new TextDetailCheckCell(this.f614b) : view;
                TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) r1;
                return r1;
            } else if (itemViewType == 4) {
                if (view == null) {
                    return new HeaderCell(this.f614b);
                }
            } else if (itemViewType == 6) {
                r1 = view == null ? new TextDetailSettingsCell(this.f614b) : view;
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) r1;
                if (i == this.f613a.f617c) {
                    textDetailSettingsCell.setMultilineDetail(false);
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("HowManyMessagesToBeDownloaded", C0338R.string.HowManyMessagesToBeDownloaded), DialogDmUtil.m615a(this.f613a.f623i), true);
                    return r1;
                } else if (i != this.f613a.f618d) {
                    return r1;
                } else {
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("WhichMessageTypesToBeDownloaded", C0338R.string.WhichMessageTypesToBeDownloaded), DialogDmUtil.m616b(this.f613a.f622h), true);
                    return r1;
                }
            } else if (itemViewType == 7 && view == null) {
                return new ShadowSectionCell(this.f614b);
            }
            return view;
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
            return i == this.f613a.f617c || i == this.f613a.f618d;
        }
    }

    public DialogDmDetailActivity(Bundle bundle) {
        super(bundle);
    }

    private void m595a() {
        if (UserConfig.isClientActivated()) {
            SettingManager settingManager = new SettingManager();
            if (!settingManager.m944b("dialogDmDetailHelpDisplayed")) {
                settingManager.m943a("dialogDmDetailHelpDisplayed", true);
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("DialogDmDetailHelp", C0338R.string.DialogDmDetailHelp));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new DialogDmDetailActivity(this));
                builder.create().show();
            }
        }
    }

    public void m606a(DialogDmDetailActivity dialogDmDetailActivity) {
        this.f621g = dialogDmDetailActivity;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("DownloadSettings", C0338R.string.DownloadSettings));
        this.actionBar.setActionBarMenuOnItemClick(new DialogDmDetailActivity(this));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            createMenu.addItemWithWidth(1, drawable, AndroidUtilities.dp(56.0f));
        } else {
            createMenu.addItemWithWidth(1, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        this.f616b = new DialogDmDetailActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f615a = new ListView(context);
        initThemeBackground(this.f615a);
        this.f615a.setDivider(null);
        this.f615a.setDividerHeight(0);
        this.f615a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f615a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f615a, LayoutHelper.createFrame(-1, -1, 51));
        this.f615a.setAdapter(this.f616b);
        this.f615a.setOnItemClickListener(new DialogDmDetailActivity(this));
        frameLayout.addView(this.actionBar);
        m595a();
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.f622h = this.arguments.getInt("docType", 62);
            this.f623i = this.arguments.getInt("messageCount", -3);
        }
        this.f620f = 0;
        int i = this.f620f;
        this.f620f = i + 1;
        this.f617c = i;
        i = this.f620f;
        this.f620f = i + 1;
        this.f618d = i;
        i = this.f620f;
        this.f620f = i + 1;
        this.f619e = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
