package com.hanista.mobogram.ui.Adapters;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseSectionsAdapter extends BaseFragmentAdapter {
    private int count;
    private SparseArray<Integer> sectionCache;
    private int sectionCount;
    private SparseArray<Integer> sectionCountCache;
    private SparseArray<Integer> sectionPositionCache;

    public BaseSectionsAdapter() {
        cleanupCache();
    }

    private void cleanupCache() {
        this.sectionCache = new SparseArray();
        this.sectionPositionCache = new SparseArray();
        this.sectionCountCache = new SparseArray();
        this.count = -1;
        this.sectionCount = -1;
    }

    private int internalGetCountForSection(int i) {
        Integer num = (Integer) this.sectionCountCache.get(i);
        if (num != null) {
            return num.intValue();
        }
        int countForSection = getCountForSection(i);
        this.sectionCountCache.put(i, Integer.valueOf(countForSection));
        return countForSection;
    }

    private int internalGetSectionCount() {
        if (this.sectionCount >= 0) {
            return this.sectionCount;
        }
        this.sectionCount = getSectionCount();
        return this.sectionCount;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public final int getCount() {
        int i = 0;
        if (this.count >= 0) {
            return this.count;
        }
        this.count = 0;
        while (i < internalGetSectionCount()) {
            this.count += internalGetCountForSection(i);
            i++;
        }
        return this.count;
    }

    public abstract int getCountForSection(int i);

    public final Object getItem(int i) {
        return getItem(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }

    public abstract Object getItem(int i, int i2);

    public final long getItemId(int i) {
        return (long) i;
    }

    public abstract View getItemView(int i, int i2, View view, ViewGroup viewGroup);

    public final int getItemViewType(int i) {
        return getItemViewType(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }

    public abstract int getItemViewType(int i, int i2);

    public int getPositionInSectionForPosition(int i) {
        int i2 = 0;
        Integer num = (Integer) this.sectionPositionCache.get(i);
        if (num != null) {
            return num.intValue();
        }
        int i3 = 0;
        while (i3 < internalGetSectionCount()) {
            int internalGetCountForSection = internalGetCountForSection(i3) + i2;
            if (i < i2 || i >= internalGetCountForSection) {
                i3++;
                i2 = internalGetCountForSection;
            } else {
                i3 = i - i2;
                this.sectionPositionCache.put(i, Integer.valueOf(i3));
                return i3;
            }
        }
        return -1;
    }

    public abstract int getSectionCount();

    public final int getSectionForPosition(int i) {
        int i2 = 0;
        Integer num = (Integer) this.sectionCache.get(i);
        if (num != null) {
            return num.intValue();
        }
        int i3 = 0;
        while (i3 < internalGetSectionCount()) {
            int internalGetCountForSection = internalGetCountForSection(i3) + i2;
            if (i < i2 || i >= internalGetCountForSection) {
                i3++;
                i2 = internalGetCountForSection;
            } else {
                this.sectionCache.put(i, Integer.valueOf(i3));
                return i3;
            }
        }
        return -1;
    }

    public abstract View getSectionHeaderView(int i, View view, ViewGroup viewGroup);

    public final View getView(int i, View view, ViewGroup viewGroup) {
        return getItemView(getSectionForPosition(i), getPositionInSectionForPosition(i), view, viewGroup);
    }

    public boolean isEnabled(int i) {
        return isRowEnabled(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }

    public abstract boolean isRowEnabled(int i, int i2);

    public void notifyDataSetChanged() {
        cleanupCache();
        super.notifyDataSetChanged();
    }

    public void notifyDataSetInvalidated() {
        cleanupCache();
        super.notifyDataSetInvalidated();
    }
}
