package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.UUID;

public class ContentProtection {
    public final SchemeInitData data;
    public final String schemeUriId;
    public final UUID uuid;

    public ContentProtection(String str, UUID uuid, SchemeInitData schemeInitData) {
        this.schemeUriId = (String) Assertions.checkNotNull(str);
        this.uuid = uuid;
        this.data = schemeInitData;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ContentProtection)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        ContentProtection contentProtection = (ContentProtection) obj;
        return this.schemeUriId.equals(contentProtection.schemeUriId) && Util.areEqual(this.uuid, contentProtection.uuid) && Util.areEqual(this.data, contentProtection.data);
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.uuid != null ? this.uuid.hashCode() : 0) + (this.schemeUriId.hashCode() * 37)) * 37;
        if (this.data != null) {
            i = this.data.hashCode();
        }
        return hashCode + i;
    }
}
