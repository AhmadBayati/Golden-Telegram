package com.hanista.mobogram.messenger.exoplayer.drm;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import android.media.MediaDrm;
import android.media.MediaDrm.OnEventListener;
import com.hanista.mobogram.messenger.exoplayer.drm.ExoMediaDrm.KeyRequest;
import com.hanista.mobogram.messenger.exoplayer.drm.ExoMediaDrm.ProvisionRequest;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@TargetApi(18)
public final class FrameworkMediaDrm implements ExoMediaDrm<FrameworkMediaCrypto> {
    private final MediaDrm mediaDrm;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.drm.FrameworkMediaDrm.1 */
    class C07281 implements OnEventListener {
        final /* synthetic */ ExoMediaDrm.OnEventListener val$listener;

        C07281(ExoMediaDrm.OnEventListener onEventListener) {
            this.val$listener = onEventListener;
        }

        public void onEvent(MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2) {
            this.val$listener.onEvent(FrameworkMediaDrm.this, bArr, i, i2, bArr2);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.drm.FrameworkMediaDrm.2 */
    class C07292 implements KeyRequest {
        final /* synthetic */ MediaDrm.KeyRequest val$request;

        C07292(MediaDrm.KeyRequest keyRequest) {
            this.val$request = keyRequest;
        }

        public byte[] getData() {
            return this.val$request.getData();
        }

        public String getDefaultUrl() {
            return this.val$request.getDefaultUrl();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.drm.FrameworkMediaDrm.3 */
    class C07303 implements ProvisionRequest {
        final /* synthetic */ MediaDrm.ProvisionRequest val$provisionRequest;

        C07303(MediaDrm.ProvisionRequest provisionRequest) {
            this.val$provisionRequest = provisionRequest;
        }

        public byte[] getData() {
            return this.val$provisionRequest.getData();
        }

        public String getDefaultUrl() {
            return this.val$provisionRequest.getDefaultUrl();
        }
    }

    public FrameworkMediaDrm(UUID uuid) {
        this.mediaDrm = new MediaDrm((UUID) Assertions.checkNotNull(uuid));
    }

    public void closeSession(byte[] bArr) {
        this.mediaDrm.closeSession(bArr);
    }

    public FrameworkMediaCrypto createMediaCrypto(UUID uuid, byte[] bArr) {
        return new FrameworkMediaCrypto(new MediaCrypto(uuid, bArr));
    }

    public KeyRequest getKeyRequest(byte[] bArr, byte[] bArr2, String str, int i, HashMap<String, String> hashMap) {
        return new C07292(this.mediaDrm.getKeyRequest(bArr, bArr2, str, i, hashMap));
    }

    public byte[] getPropertyByteArray(String str) {
        return this.mediaDrm.getPropertyByteArray(str);
    }

    public String getPropertyString(String str) {
        return this.mediaDrm.getPropertyString(str);
    }

    public ProvisionRequest getProvisionRequest() {
        return new C07303(this.mediaDrm.getProvisionRequest());
    }

    public byte[] openSession() {
        return this.mediaDrm.openSession();
    }

    public byte[] provideKeyResponse(byte[] bArr, byte[] bArr2) {
        return this.mediaDrm.provideKeyResponse(bArr, bArr2);
    }

    public void provideProvisionResponse(byte[] bArr) {
        this.mediaDrm.provideProvisionResponse(bArr);
    }

    public Map<String, String> queryKeyStatus(byte[] bArr) {
        return this.mediaDrm.queryKeyStatus(bArr);
    }

    public void release() {
        this.mediaDrm.release();
    }

    public void restoreKeys(byte[] bArr, byte[] bArr2) {
        this.mediaDrm.restoreKeys(bArr, bArr2);
    }

    public void setOnEventListener(ExoMediaDrm.OnEventListener<? super FrameworkMediaCrypto> onEventListener) {
        this.mediaDrm.setOnEventListener(onEventListener == null ? null : new C07281(onEventListener));
    }

    public void setPropertyByteArray(String str, byte[] bArr) {
        this.mediaDrm.setPropertyByteArray(str, bArr);
    }

    public void setPropertyString(String str, String str2) {
        this.mediaDrm.setPropertyString(str, str2);
    }
}
