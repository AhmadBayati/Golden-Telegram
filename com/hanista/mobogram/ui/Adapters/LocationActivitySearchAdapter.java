package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.ui.Cells.LocationCell;

public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private Context mContext;

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public int getCount() {
        return this.places.size();
    }

    public TL_messageMediaVenue getItem(int i) {
        return (i < 0 || i >= this.places.size()) ? null : (TL_messageMediaVenue) this.places.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View locationCell = view == null ? new LocationCell(this.mContext) : view;
        ((LocationCell) locationCell).setLocation((TL_messageMediaVenue) this.places.get(i), (String) this.iconUrls.get(i), i != this.places.size() + -1);
        return locationCell;
    }

    public int getViewTypeCount() {
        return 4;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return this.places.isEmpty();
    }

    public boolean isEnabled(int i) {
        return true;
    }
}
