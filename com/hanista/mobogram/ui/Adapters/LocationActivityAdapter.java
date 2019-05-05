package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.LocationCell;
import com.hanista.mobogram.ui.Cells.LocationLoadingCell;
import com.hanista.mobogram.ui.Cells.LocationPoweredCell;
import com.hanista.mobogram.ui.Cells.SendLocationCell;
import java.util.Locale;

public class LocationActivityAdapter extends BaseLocationAdapter {
    private Location customLocation;
    private Location gpsLocation;
    private Context mContext;
    private int overScrollHeight;
    private SendLocationCell sendLocationCell;

    public LocationActivityAdapter(Context context) {
        this.mContext = context;
    }

    private void updateCell() {
        if (this.sendLocationCell == null) {
            return;
        }
        if (this.customLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", C0338R.string.SendSelectedLocation), String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())}));
        } else if (this.gpsLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", C0338R.string.SendLocation), LocaleController.formatString("AccurateTo", C0338R.string.AccurateTo, LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy())));
        } else {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", C0338R.string.SendLocation), LocaleController.getString("Loading", C0338R.string.Loading));
        }
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public int getCount() {
        if (this.searching || (!this.searching && this.places.isEmpty())) {
            return 4;
        }
        return (this.places.isEmpty() ? 0 : 1) + (this.places.size() + 3);
    }

    public TL_messageMediaVenue getItem(int i) {
        return (i <= 2 || i >= this.places.size() + 3) ? null : (TL_messageMediaVenue) this.places.get(i - 3);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return i == 0 ? 0 : i != 1 ? i == 2 ? 2 : (this.searching || (!this.searching && this.places.isEmpty())) ? 4 : i == this.places.size() + 3 ? 5 : 3 : 1;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View emptyCell;
        if (i == 0) {
            emptyCell = view == null ? new EmptyCell(this.mContext) : view;
            ((EmptyCell) emptyCell).setHeight(this.overScrollHeight);
            return emptyCell;
        } else if (i == 1) {
            emptyCell = view == null ? new SendLocationCell(this.mContext) : view;
            this.sendLocationCell = (SendLocationCell) emptyCell;
            updateCell();
            return emptyCell;
        } else if (i == 2) {
            emptyCell = view == null ? new GreySectionCell(this.mContext) : view;
            ((GreySectionCell) emptyCell).setText(LocaleController.getString("NearbyPlaces", C0338R.string.NearbyPlaces));
            return emptyCell;
        } else if (this.searching || (!this.searching && this.places.isEmpty())) {
            emptyCell = view == null ? new LocationLoadingCell(this.mContext) : view;
            ((LocationLoadingCell) emptyCell).setLoading(this.searching);
            return emptyCell;
        } else if (i == this.places.size() + 3) {
            return view == null ? new LocationPoweredCell(this.mContext) : view;
        } else {
            emptyCell = view == null ? new LocationCell(this.mContext) : view;
            ((LocationCell) emptyCell).setLocation((TL_messageMediaVenue) this.places.get(i - 3), (String) this.iconUrls.get(i - 3), true);
            return emptyCell;
        }
    }

    public int getViewTypeCount() {
        return 6;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isEnabled(int i) {
        return (i == 2 || i == 0 || ((i == 3 && (this.searching || (!this.searching && this.places.isEmpty()))) || i == this.places.size() + 3)) ? false : true;
    }

    public void setCustomLocation(Location location) {
        this.customLocation = location;
        updateCell();
    }

    public void setGpsLocation(Location location) {
        this.gpsLocation = location;
        updateCell();
    }

    public void setOverScrollHeight(int i) {
        this.overScrollHeight = i;
    }
}
