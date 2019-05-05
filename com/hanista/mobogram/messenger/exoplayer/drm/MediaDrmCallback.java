package com.hanista.mobogram.messenger.exoplayer.drm;

import android.annotation.TargetApi;
import com.hanista.mobogram.messenger.exoplayer.drm.ExoMediaDrm.KeyRequest;
import com.hanista.mobogram.messenger.exoplayer.drm.ExoMediaDrm.ProvisionRequest;
import java.util.UUID;

@TargetApi(18)
public interface MediaDrmCallback {
    byte[] executeKeyRequest(UUID uuid, KeyRequest keyRequest);

    byte[] executeProvisionRequest(UUID uuid, ProvisionRequest provisionRequest);
}
