package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.ui.Adapters.CountryAdapter.Country;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class CountrySearchAdapter extends BaseFragmentAdapter {
    private HashMap<String, ArrayList<Country>> countries;
    private Context mContext;
    private ArrayList<Country> searchResult;
    private Timer searchTimer;

    /* renamed from: com.hanista.mobogram.ui.Adapters.CountrySearchAdapter.1 */
    class C10101 extends TimerTask {
        final /* synthetic */ String val$query;

        C10101(String str) {
            this.val$query = str;
        }

        public void run() {
            try {
                CountrySearchAdapter.this.searchTimer.cancel();
                CountrySearchAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            CountrySearchAdapter.this.processSearch(this.val$query);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.CountrySearchAdapter.2 */
    class C10112 implements Runnable {
        final /* synthetic */ String val$query;

        C10112(String str) {
            this.val$query = str;
        }

        public void run() {
            if (this.val$query.trim().toLowerCase().length() == 0) {
                CountrySearchAdapter.this.updateSearchResults(new ArrayList());
                return;
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = (ArrayList) CountrySearchAdapter.this.countries.get(this.val$query.substring(0, 1).toUpperCase());
            if (arrayList2 != null) {
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    Country country = (Country) it.next();
                    if (country.name.toLowerCase().startsWith(this.val$query)) {
                        arrayList.add(country);
                    }
                }
            }
            CountrySearchAdapter.this.updateSearchResults(arrayList);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.CountrySearchAdapter.3 */
    class C10123 implements Runnable {
        final /* synthetic */ ArrayList val$arrCounties;

        C10123(ArrayList arrayList) {
            this.val$arrCounties = arrayList;
        }

        public void run() {
            CountrySearchAdapter.this.searchResult = this.val$arrCounties;
            CountrySearchAdapter.this.notifyDataSetChanged();
        }
    }

    public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> hashMap) {
        this.mContext = context;
        this.countries = hashMap;
    }

    private void processSearch(String str) {
        Utilities.searchQueue.postRunnable(new C10112(str));
    }

    private void updateSearchResults(ArrayList<Country> arrayList) {
        AndroidUtilities.runOnUIThread(new C10123(arrayList));
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public int getCount() {
        return this.searchResult == null ? 0 : this.searchResult.size();
    }

    public Country getItem(int i) {
        return (i < 0 || i >= this.searchResult.size()) ? null : (Country) this.searchResult.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View textSettingsCell = view == null ? new TextSettingsCell(this.mContext) : view;
        Country country = (Country) this.searchResult.get(i);
        ((TextSettingsCell) textSettingsCell).setTextAndValue(country.name, "+" + country.code, i != this.searchResult.size() + -1);
        return textSettingsCell;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return this.searchResult == null || this.searchResult.size() == 0;
    }

    public boolean isEnabled(int i) {
        return true;
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
        this.searchTimer.schedule(new C10101(str), 100, 300);
    }
}
