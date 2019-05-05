package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.C0083R;

public class zzaj {
    private final Resources Dc;
    private final String Dd;

    public zzaj(Context context) {
        zzac.zzy(context);
        this.Dc = context.getResources();
        this.Dd = this.Dc.getResourcePackageName(C0083R.string.common_google_play_services_unknown_issue);
    }

    public String getString(String str) {
        int identifier = this.Dc.getIdentifier(str, "string", this.Dd);
        return identifier == 0 ? null : this.Dc.getString(identifier);
    }
}
