package com.hanista.mobogram.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.CountryAdapter;
import com.hanista.mobogram.ui.Adapters.CountryAdapter.Country;
import com.hanista.mobogram.ui.Adapters.CountrySearchAdapter;
import com.hanista.mobogram.ui.Components.LetterSectionsListView;

public class CountrySelectActivity extends BaseFragment {
    private CountrySelectActivityDelegate delegate;
    private TextView emptyTextView;
    private LetterSectionsListView listView;
    private CountryAdapter listViewAdapter;
    private CountrySearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    public interface CountrySelectActivityDelegate {
        void didSelectCountry(String str);
    }

    /* renamed from: com.hanista.mobogram.ui.CountrySelectActivity.1 */
    class C15271 extends ActionBarMenuOnItemClick {
        C15271() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                CountrySelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CountrySelectActivity.2 */
    class C15282 extends ActionBarMenuItemSearchListener {
        C15282() {
        }

        public void onSearchCollapse() {
            CountrySelectActivity.this.searchListViewAdapter.search(null);
            CountrySelectActivity.this.searching = false;
            CountrySelectActivity.this.searchWas = false;
            CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
            CountrySelectActivity.this.listView.setFastScrollAlwaysVisible(true);
            CountrySelectActivity.this.listView.setFastScrollEnabled(true);
            CountrySelectActivity.this.listView.setVerticalScrollBarEnabled(false);
            CountrySelectActivity.this.emptyTextView.setText(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
        }

        public void onSearchExpand() {
            CountrySelectActivity.this.searching = true;
        }

        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            CountrySelectActivity.this.searchListViewAdapter.search(obj);
            if (obj.length() != 0) {
                CountrySelectActivity.this.searchWas = true;
                if (CountrySelectActivity.this.listView != null) {
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollAlwaysVisible(false);
                    CountrySelectActivity.this.listView.setFastScrollEnabled(false);
                    CountrySelectActivity.this.listView.setVerticalScrollBarEnabled(true);
                }
                if (CountrySelectActivity.this.emptyTextView == null) {
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CountrySelectActivity.3 */
    class C15293 implements OnTouchListener {
        C15293() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CountrySelectActivity.4 */
    class C15304 implements OnItemClickListener {
        C15304() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Country item;
            if (CountrySelectActivity.this.searching && CountrySelectActivity.this.searchWas) {
                item = CountrySelectActivity.this.searchListViewAdapter.getItem(i);
            } else {
                int sectionForPosition = CountrySelectActivity.this.listViewAdapter.getSectionForPosition(i);
                int positionInSectionForPosition = CountrySelectActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
                if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                    item = CountrySelectActivity.this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                } else {
                    return;
                }
            }
            if (i >= 0) {
                CountrySelectActivity.this.finishFragment();
                if (item != null && CountrySelectActivity.this.delegate != null) {
                    CountrySelectActivity.this.delegate.didSelectCountry(item.name);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CountrySelectActivity.5 */
    class C15315 implements OnScrollListener {
        C15315() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            if (absListView.isFastScrollEnabled()) {
                AndroidUtilities.clearDrawableAnimation(absListView);
            }
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1 && CountrySelectActivity.this.searching && CountrySelectActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
        this.actionBar.setActionBarMenuOnItemClick(new C15271());
        this.actionBar.createMenu().addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C15282()).getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        this.searching = false;
        this.searchWas = false;
        this.listViewAdapter = new CountryAdapter(context);
        this.searchListViewAdapter = new CountrySearchAdapter(context, this.listViewAdapter.getCountries());
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
        linearLayout.setOnTouchListener(new C15293());
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setTypeface(FontUtil.m1176a().m1161d());
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
        this.listView = new LetterSectionsListView(context);
        this.listView.setEmptyView(linearLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setFastScrollAlwaysVisible(true);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new C15304());
        this.listView.setOnScrollListener(new C15315());
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        initThemeActionBar();
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate countrySelectActivityDelegate) {
        this.delegate = countrySelectActivityDelegate;
    }
}
