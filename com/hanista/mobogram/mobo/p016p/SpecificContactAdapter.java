package com.hanista.mobogram.mobo.p016p;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.p.c */
public class SpecificContactAdapter extends BaseFragmentAdapter {
    private Context f2056a;
    private List<SpecificContact> f2057b;

    public SpecificContactAdapter(Context context, List<SpecificContact> list) {
        this.f2056a = context;
        this.f2057b = list;
    }

    public SpecificContact m2015a(int i) {
        return (SpecificContact) this.f2057b.get(i);
    }

    public void m2016a(List<SpecificContact> list) {
        this.f2057b = list;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public int getCount() {
        return this.f2057b.size();
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
        View specificContactCell = view == null ? new SpecificContactCell(this.f2056a, 0, 0, false) : view;
        ((SpecificContactCell) specificContactCell).setData((SpecificContact) this.f2057b.get(i));
        return specificContactCell;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isEmpty() {
        return this.f2057b.isEmpty();
    }

    public boolean isEnabled(int i) {
        return true;
    }
}
