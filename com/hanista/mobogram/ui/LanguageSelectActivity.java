package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.LocaleController.LocaleInfo;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class LanguageSelectActivity extends BaseFragment {
    private TextView emptyTextView;
    private BaseFragmentAdapter listAdapter;
    private ListView listView;
    private BaseFragmentAdapter searchListViewAdapter;
    public ArrayList<LocaleInfo> searchResult;
    private Timer searchTimer;
    private boolean searchWas;
    private boolean searching;

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.1 */
    class C15891 extends ActionBarMenuOnItemClick {
        C15891() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                LanguageSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.2 */
    class C15902 extends ActionBarMenuItemSearchListener {
        C15902() {
        }

        public void onSearchCollapse() {
            LanguageSelectActivity.this.search(null);
            LanguageSelectActivity.this.searching = false;
            LanguageSelectActivity.this.searchWas = false;
            if (LanguageSelectActivity.this.listView != null) {
                LanguageSelectActivity.this.emptyTextView.setVisibility(8);
                LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
            }
        }

        public void onSearchExpand() {
            LanguageSelectActivity.this.searching = true;
        }

        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            LanguageSelectActivity.this.search(obj);
            if (obj.length() != 0) {
                LanguageSelectActivity.this.searchWas = true;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.3 */
    class C15913 implements OnTouchListener {
        C15913() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.4 */
    class C15924 implements OnItemClickListener {
        C15924() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            LocaleInfo localeInfo = null;
            if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                if (i >= 0 && i < LanguageSelectActivity.this.searchResult.size()) {
                    localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(i);
                }
            } else if (i >= 0 && i < LocaleController.getInstance().sortedLanguages.size()) {
                localeInfo = (LocaleInfo) LocaleController.getInstance().sortedLanguages.get(i);
            }
            if (localeInfo != null) {
                LocaleController.getInstance().applyLanguage(localeInfo, true);
                LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.menuSettingsChanged, new Object[0]);
            LanguageSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.5 */
    class C15945 implements OnItemLongClickListener {

        /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.5.1 */
        class C15931 implements OnClickListener {
            final /* synthetic */ LocaleInfo val$finalLocaleInfo;

            C15931(LocaleInfo localeInfo) {
                this.val$finalLocaleInfo = localeInfo;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (LocaleController.getInstance().deleteLanguage(this.val$finalLocaleInfo)) {
                    if (LanguageSelectActivity.this.searchResult != null) {
                        LanguageSelectActivity.this.searchResult.remove(this.val$finalLocaleInfo);
                    }
                    if (LanguageSelectActivity.this.listAdapter != null) {
                        LanguageSelectActivity.this.listAdapter.notifyDataSetChanged();
                    }
                    if (LanguageSelectActivity.this.searchListViewAdapter != null) {
                        LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        C15945() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            LocaleInfo localeInfo;
            if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                if (i >= 0 && i < LanguageSelectActivity.this.searchResult.size()) {
                    localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(i);
                }
                localeInfo = null;
            } else {
                if (i >= 0 && i < LocaleController.getInstance().sortedLanguages.size()) {
                    localeInfo = (LocaleInfo) LocaleController.getInstance().sortedLanguages.get(i);
                }
                localeInfo = null;
            }
            if (localeInfo == null || localeInfo.pathToFile == null || LanguageSelectActivity.this.getParentActivity() == null) {
                return false;
            }
            Builder builder = new Builder(LanguageSelectActivity.this.getParentActivity());
            builder.setMessage(LocaleController.getString("DeleteLocalization", C0338R.string.DeleteLocalization));
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("Delete", C0338R.string.Delete), new C15931(localeInfo));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            LanguageSelectActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.6 */
    class C15956 implements OnScrollListener {
        C15956() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1 && LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.7 */
    class C15967 extends TimerTask {
        final /* synthetic */ String val$query;

        C15967(String str) {
            this.val$query = str;
        }

        public void run() {
            try {
                LanguageSelectActivity.this.searchTimer.cancel();
                LanguageSelectActivity.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            LanguageSelectActivity.this.processSearch(this.val$query);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.8 */
    class C15978 implements Runnable {
        final /* synthetic */ String val$query;

        C15978(String str) {
            this.val$query = str;
        }

        public void run() {
            if (this.val$query.trim().toLowerCase().length() == 0) {
                LanguageSelectActivity.this.updateSearchResults(new ArrayList());
                return;
            }
            System.currentTimeMillis();
            ArrayList arrayList = new ArrayList();
            Iterator it = LocaleController.getInstance().sortedLanguages.iterator();
            while (it.hasNext()) {
                LocaleInfo localeInfo = (LocaleInfo) it.next();
                if (localeInfo.name.toLowerCase().startsWith(this.val$query) || localeInfo.nameEnglish.toLowerCase().startsWith(this.val$query)) {
                    arrayList.add(localeInfo);
                }
            }
            LanguageSelectActivity.this.updateSearchResults(arrayList);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LanguageSelectActivity.9 */
    class C15989 implements Runnable {
        final /* synthetic */ ArrayList val$arrCounties;

        C15989(ArrayList arrayList) {
            this.val$arrCounties = arrayList;
        }

        public void run() {
            LanguageSelectActivity.this.searchResult = this.val$arrCounties;
            LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public int getCount() {
            return LocaleController.getInstance().sortedLanguages == null ? 0 : LocaleController.getInstance().sortedLanguages.size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View textSettingsCell = view == null ? new TextSettingsCell(this.mContext) : view;
            ((TextSettingsCell) textSettingsCell).setText(((LocaleInfo) LocaleController.getInstance().sortedLanguages.get(i)).name, i != LocaleController.getInstance().sortedLanguages.size() + -1);
            return textSettingsCell;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return LocaleController.getInstance().sortedLanguages == null || LocaleController.getInstance().sortedLanguages.size() == 0;
        }

        public boolean isEnabled(int i) {
            return true;
        }
    }

    private class SearchAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public int getCount() {
            return LanguageSelectActivity.this.searchResult == null ? 0 : LanguageSelectActivity.this.searchResult.size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View textSettingsCell = view == null ? new TextSettingsCell(this.mContext) : view;
            ((TextSettingsCell) textSettingsCell).setText(((LocaleInfo) LanguageSelectActivity.this.searchResult.get(i)).name, i != LanguageSelectActivity.this.searchResult.size() + -1);
            return textSettingsCell;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return LanguageSelectActivity.this.searchResult == null || LanguageSelectActivity.this.searchResult.size() == 0;
        }

        public boolean isEnabled(int i) {
            return true;
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            initThemeActionBar();
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_search).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
        }
    }

    private void processSearch(String str) {
        Utilities.searchQueue.postRunnable(new C15978(str));
    }

    private void updateSearchResults(ArrayList<LocaleInfo> arrayList) {
        AndroidUtilities.runOnUIThread(new C15989(arrayList));
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", C0338R.string.Language));
        this.actionBar.setActionBarMenuOnItemClick(new C15891());
        this.actionBar.createMenu().addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C15902()).getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        this.listAdapter = new ListAdapter(context);
        this.searchListViewAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        View linearLayout = new LinearLayout(context);
        linearLayout.setVisibility(4);
        linearLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new C15913());
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
        linearLayout.addView(this.emptyTextView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.emptyTextView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.emptyTextView.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.listView = new ListView(context);
        initThemeBackground(this.listView);
        this.listView.setEmptyView(linearLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setAdapter(this.listAdapter);
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new C15924());
        this.listView.setOnItemLongClickListener(new C15945());
        this.listView.setOnScrollListener(new C15956());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        initTheme();
    }

    public void search(String str) {
        if (str == null) {
            this.searchResult = null;
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new C15967(str), 100, 300);
    }
}
