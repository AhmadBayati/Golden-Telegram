package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.ui.Cells.DividerCell;
import com.hanista.mobogram.ui.Cells.LetterSectionCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CountryAdapter extends BaseSectionsAdapter {
    private HashMap<String, ArrayList<Country>> countries;
    private Context mContext;
    private ArrayList<String> sortedCountries;

    /* renamed from: com.hanista.mobogram.ui.Adapters.CountryAdapter.1 */
    class C10081 implements Comparator<String> {
        C10081() {
        }

        public int compare(String str, String str2) {
            return str.compareTo(str2);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.CountryAdapter.2 */
    class C10092 implements Comparator<Country> {
        C10092() {
        }

        public int compare(Country country, Country country2) {
            return country.name.compareTo(country2.name);
        }
    }

    public static class Country {
        public String code;
        public String name;
        public String shortname;
    }

    public CountryAdapter(Context context) {
        ArrayList arrayList;
        this.countries = new HashMap();
        this.sortedCountries = new ArrayList();
        this.mContext = context;
        try {
            InputStream open = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                Country country = new Country();
                country.name = split[2];
                country.code = split[0];
                country.shortname = split[1];
                String toUpperCase = country.name.substring(0, 1).toUpperCase();
                arrayList = (ArrayList) this.countries.get(toUpperCase);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.countries.put(toUpperCase, arrayList);
                    this.sortedCountries.add(toUpperCase);
                }
                arrayList.add(country);
            }
            bufferedReader.close();
            open.close();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        Collections.sort(this.sortedCountries, new C10081());
        for (ArrayList arrayList2 : this.countries.values()) {
            Collections.sort(arrayList2, new C10092());
        }
    }

    public int getCountForSection(int i) {
        int size = ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size();
        return i != this.sortedCountries.size() + -1 ? size + 1 : size;
    }

    public HashMap<String, ArrayList<Country>> getCountries() {
        return this.countries;
    }

    public Country getItem(int i, int i2) {
        if (i < 0 || i >= this.sortedCountries.size()) {
            return null;
        }
        ArrayList arrayList = (ArrayList) this.countries.get(this.sortedCountries.get(i));
        return (i2 < 0 || i2 >= arrayList.size()) ? null : (Country) arrayList.get(i2);
    }

    public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
        float f = 72.0f;
        float f2 = 54.0f;
        int itemViewType = getItemViewType(i, i2);
        if (itemViewType == 1) {
            if (view != null) {
                return view;
            }
            view = new DividerCell(this.mContext);
            itemViewType = AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 72.0f);
            if (!LocaleController.isRTL) {
                f = 24.0f;
            }
            view.setPadding(itemViewType, 0, AndroidUtilities.dp(f), 0);
            return view;
        } else if (itemViewType != 0) {
            return view;
        } else {
            View textSettingsCell;
            if (view == null) {
                textSettingsCell = new TextSettingsCell(this.mContext);
                itemViewType = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 54.0f);
                if (!LocaleController.isRTL) {
                    f2 = 16.0f;
                }
                textSettingsCell.setPadding(itemViewType, 0, AndroidUtilities.dp(f2), 0);
            } else {
                textSettingsCell = view;
            }
            Country country = (Country) ((ArrayList) this.countries.get(this.sortedCountries.get(i))).get(i2);
            ((TextSettingsCell) textSettingsCell).setTextAndValue(country.name, "+" + country.code, false);
            return textSettingsCell;
        }
    }

    public int getItemViewType(int i, int i2) {
        return i2 < ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size() ? 0 : 1;
    }

    public int getSectionCount() {
        return this.sortedCountries.size();
    }

    public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
        View letterSectionCell;
        if (view == null) {
            letterSectionCell = new LetterSectionCell(this.mContext);
            ((LetterSectionCell) letterSectionCell).setCellHeight(AndroidUtilities.dp(48.0f));
        } else {
            letterSectionCell = view;
        }
        ((LetterSectionCell) letterSectionCell).setLetter(((String) this.sortedCountries.get(i)).toUpperCase());
        return letterSectionCell;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public boolean isRowEnabled(int i, int i2) {
        return i2 < ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size();
    }
}
