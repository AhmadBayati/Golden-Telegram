package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import android.net.Uri;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.UriUtil;

public final class RangedUri {
    private final String baseUri;
    private int hashCode;
    public final long length;
    private final String referenceUri;
    public final long start;

    public RangedUri(String str, String str2, long j, long j2) {
        boolean z = (str == null && str2 == null) ? false : true;
        Assertions.checkArgument(z);
        this.baseUri = str;
        this.referenceUri = str2;
        this.start = j;
        this.length = j2;
    }

    public RangedUri attemptMerge(RangedUri rangedUri) {
        RangedUri rangedUri2 = null;
        long j = -1;
        if (rangedUri != null && getUriString().equals(rangedUri.getUriString())) {
            String str;
            String str2;
            long j2;
            if (this.length != -1 && this.start + this.length == rangedUri.start) {
                str = this.baseUri;
                str2 = this.referenceUri;
                j2 = this.start;
                if (rangedUri.length != -1) {
                    j = this.length + rangedUri.length;
                }
                rangedUri2 = new RangedUri(str, str2, j2, j);
            } else if (rangedUri.length != -1 && rangedUri.start + rangedUri.length == this.start) {
                str = this.baseUri;
                str2 = this.referenceUri;
                j2 = rangedUri.start;
                if (this.length != -1) {
                    j = rangedUri.length + this.length;
                }
                rangedUri2 = new RangedUri(str, str2, j2, j);
            }
        }
        return rangedUri2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RangedUri rangedUri = (RangedUri) obj;
        return this.start == rangedUri.start && this.length == rangedUri.length && getUriString().equals(rangedUri.getUriString());
    }

    public Uri getUri() {
        return UriUtil.resolveToUri(this.baseUri, this.referenceUri);
    }

    public String getUriString() {
        return UriUtil.resolve(this.baseUri, this.referenceUri);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = ((((((int) this.start) + 527) * 31) + ((int) this.length)) * 31) + getUriString().hashCode();
        }
        return this.hashCode;
    }
}
