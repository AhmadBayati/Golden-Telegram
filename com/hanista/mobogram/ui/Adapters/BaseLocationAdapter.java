package com.hanista.mobogram.ui.Adapters;

import android.location.Location;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.RequestQueue;
import com.hanista.mobogram.messenger.volley.Response.ErrorListener;
import com.hanista.mobogram.messenger.volley.Response.Listener;
import com.hanista.mobogram.messenger.volley.VolleyError;
import com.hanista.mobogram.messenger.volley.toolbox.JsonObjectRequest;
import com.hanista.mobogram.messenger.volley.toolbox.Volley;
import com.hanista.mobogram.tgnet.TLRPC.TL_geoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseLocationAdapter extends BaseFragmentAdapter {
    private BaseLocationAdapterDelegate delegate;
    protected ArrayList<String> iconUrls;
    private Location lastSearchLocation;
    protected ArrayList<TL_messageMediaVenue> places;
    private RequestQueue requestQueue;
    private Timer searchTimer;
    protected boolean searching;

    /* renamed from: com.hanista.mobogram.ui.Adapters.BaseLocationAdapter.1 */
    class C09911 extends TimerTask {
        final /* synthetic */ Location val$coordinate;
        final /* synthetic */ String val$query;

        /* renamed from: com.hanista.mobogram.ui.Adapters.BaseLocationAdapter.1.1 */
        class C09901 implements Runnable {
            C09901() {
            }

            public void run() {
                BaseLocationAdapter.this.lastSearchLocation = null;
                BaseLocationAdapter.this.searchGooglePlacesWithQuery(C09911.this.val$query, C09911.this.val$coordinate);
            }
        }

        C09911(String str, Location location) {
            this.val$query = str;
            this.val$coordinate = location;
        }

        public void run() {
            try {
                BaseLocationAdapter.this.searchTimer.cancel();
                BaseLocationAdapter.this.searchTimer = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            AndroidUtilities.runOnUIThread(new C09901());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.BaseLocationAdapter.2 */
    class C09922 implements Listener<JSONObject> {
        C09922() {
        }

        public void onResponse(JSONObject jSONObject) {
            try {
                BaseLocationAdapter.this.places.clear();
                BaseLocationAdapter.this.iconUrls.clear();
                JSONArray jSONArray = jSONObject.getJSONObject("response").getJSONArray("venues");
                for (int i = 0; i < jSONArray.length(); i++) {
                    try {
                        JSONObject jSONObject2;
                        JSONObject jSONObject3 = jSONArray.getJSONObject(i);
                        Object obj = null;
                        if (jSONObject3.has("categories")) {
                            JSONArray jSONArray2 = jSONObject3.getJSONArray("categories");
                            if (jSONArray2.length() > 0) {
                                JSONObject jSONObject4 = jSONArray2.getJSONObject(0);
                                if (jSONObject4.has("icon")) {
                                    jSONObject2 = jSONObject4.getJSONObject("icon");
                                    obj = String.format(Locale.US, "%s64%s", new Object[]{jSONObject2.getString("prefix"), jSONObject2.getString("suffix")});
                                }
                            }
                        }
                        BaseLocationAdapter.this.iconUrls.add(obj);
                        jSONObject2 = jSONObject3.getJSONObject("location");
                        TL_messageMediaVenue tL_messageMediaVenue = new TL_messageMediaVenue();
                        tL_messageMediaVenue.geo = new TL_geoPoint();
                        tL_messageMediaVenue.geo.lat = jSONObject2.getDouble("lat");
                        tL_messageMediaVenue.geo._long = jSONObject2.getDouble("lng");
                        if (jSONObject2.has("address")) {
                            tL_messageMediaVenue.address = jSONObject2.getString("address");
                        } else if (jSONObject2.has("city")) {
                            tL_messageMediaVenue.address = jSONObject2.getString("city");
                        } else if (jSONObject2.has("state")) {
                            tL_messageMediaVenue.address = jSONObject2.getString("state");
                        } else if (jSONObject2.has("country")) {
                            tL_messageMediaVenue.address = jSONObject2.getString("country");
                        } else {
                            tL_messageMediaVenue.address = String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(tL_messageMediaVenue.geo.lat), Double.valueOf(tL_messageMediaVenue.geo._long)});
                        }
                        if (jSONObject3.has("name")) {
                            tL_messageMediaVenue.title = jSONObject3.getString("name");
                        }
                        tL_messageMediaVenue.venue_id = jSONObject3.getString(TtmlNode.ATTR_ID);
                        tL_messageMediaVenue.provider = "foursquare";
                        BaseLocationAdapter.this.places.add(tL_messageMediaVenue);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            BaseLocationAdapter.this.searching = false;
            BaseLocationAdapter.this.notifyDataSetChanged();
            if (BaseLocationAdapter.this.delegate != null) {
                BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.BaseLocationAdapter.3 */
    class C09933 implements ErrorListener {
        C09933() {
        }

        public void onErrorResponse(VolleyError volleyError) {
            FileLog.m16e("tmessages", "Error: " + volleyError.getMessage());
            BaseLocationAdapter.this.searching = false;
            BaseLocationAdapter.this.notifyDataSetChanged();
            if (BaseLocationAdapter.this.delegate != null) {
                BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
            }
        }
    }

    public interface BaseLocationAdapterDelegate {
        void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> arrayList);
    }

    public BaseLocationAdapter() {
        this.places = new ArrayList();
        this.iconUrls = new ArrayList();
        this.requestQueue = Volley.newRequestQueue(ApplicationLoader.applicationContext);
    }

    public void destroy() {
        if (this.requestQueue != null) {
            this.requestQueue.cancelAll((Object) "search");
            this.requestQueue.stop();
        }
    }

    public void searchDelayed(String str, Location location) {
        if (str == null || str.length() == 0) {
            this.places.clear();
            notifyDataSetChanged();
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
        this.searchTimer.schedule(new C09911(str, location), 200, 500);
    }

    public void searchGooglePlacesWithQuery(String str, Location location) {
        if (this.lastSearchLocation == null || location.distanceTo(this.lastSearchLocation) >= 200.0f) {
            this.lastSearchLocation = location;
            if (this.searching) {
                this.searching = false;
                this.requestQueue.cancelAll((Object) "search");
            }
            try {
                this.searching = true;
                Object[] objArr = new Object[4];
                objArr[0] = BuildVars.FOURSQUARE_API_VERSION;
                objArr[1] = BuildVars.FOURSQUARE_API_ID;
                objArr[2] = BuildVars.FOURSQUARE_API_KEY;
                objArr[3] = String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
                String format = String.format(Locale.US, "https://api.foursquare.com/v2/venues/search/?v=%s&locale=en&limit=25&client_id=%s&client_secret=%s&ll=%s", objArr);
                if (str != null && str.length() > 0) {
                    format = format + "&query=" + URLEncoder.encode(str, C0700C.UTF8_NAME);
                }
                Request jsonObjectRequest = new JsonObjectRequest(0, format, null, new C09922(), new C09933());
                jsonObjectRequest.setShouldCache(false);
                jsonObjectRequest.setTag("search");
                this.requestQueue.add(jsonObjectRequest);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                this.searching = false;
                if (this.delegate != null) {
                    this.delegate.didLoadedSearchResult(this.places);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setDelegate(BaseLocationAdapterDelegate baseLocationAdapterDelegate) {
        this.delegate = baseLocationAdapterDelegate;
    }
}
