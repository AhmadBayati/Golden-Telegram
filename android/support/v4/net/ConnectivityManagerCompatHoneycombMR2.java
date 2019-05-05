package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;

class ConnectivityManagerCompatHoneycombMR2 {
    ConnectivityManagerCompatHoneycombMR2() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return true;
        }
        switch (activeNetworkInfo.getType()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
            case Method.TRACE /*6*/:
                return true;
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case Method.PATCH /*7*/:
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return false;
            default:
                return true;
        }
    }
}
