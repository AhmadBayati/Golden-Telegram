package com.google.android.gms.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.lang.Thread.State;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraSource {
    @SuppressLint({"InlinedApi"})
    public static final int CAMERA_FACING_BACK = 0;
    @SuppressLint({"InlinedApi"})
    public static final int CAMERA_FACING_FRONT = 1;
    private final Object aJR;
    private Camera aJS;
    private int aJT;
    private Size aJU;
    private float aJV;
    private int aJW;
    private int aJX;
    private boolean aJY;
    private SurfaceView aJZ;
    private SurfaceTexture aKa;
    private boolean aKb;
    private Thread aKc;
    private zzb aKd;
    private Map<byte[], ByteBuffer> aKe;
    private Context mContext;
    private int zzbvy;

    public static class Builder {
        private final Detector<?> aKf;
        private CameraSource aKg;

        public Builder(Context context, Detector<?> detector) {
            this.aKg = new CameraSource();
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (detector == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.aKf = detector;
                this.aKg.mContext = context;
            }
        }

        public CameraSource build() {
            CameraSource cameraSource = this.aKg;
            CameraSource cameraSource2 = this.aKg;
            cameraSource2.getClass();
            cameraSource.aKd = new zzb(cameraSource2, this.aKf);
            return this.aKg;
        }

        public Builder setAutoFocusEnabled(boolean z) {
            this.aKg.aJY = z;
            return this;
        }

        public Builder setFacing(int i) {
            if (i == 0 || i == CameraSource.CAMERA_FACING_FRONT) {
                this.aKg.aJT = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid camera: " + i);
        }

        public Builder setRequestedFps(float f) {
            if (f <= 0.0f) {
                throw new IllegalArgumentException("Invalid fps: " + f);
            }
            this.aKg.aJV = f;
            return this;
        }

        public Builder setRequestedPreviewSize(int i, int i2) {
            if (i <= 0 || i > 1000000 || i2 <= 0 || i2 > 1000000) {
                throw new IllegalArgumentException("Invalid preview size: " + i + "x" + i2);
            }
            this.aKg.aJW = i;
            this.aKg.aJX = i2;
            return this;
        }
    }

    public interface PictureCallback {
        void onPictureTaken(byte[] bArr);
    }

    public interface ShutterCallback {
        void onShutter();
    }

    private class zza implements PreviewCallback {
        final /* synthetic */ CameraSource aKh;

        private zza(CameraSource cameraSource) {
            this.aKh = cameraSource;
        }

        public void onPreviewFrame(byte[] bArr, Camera camera) {
            this.aKh.aKd.zza(bArr, camera);
        }
    }

    private class zzb implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled;
        private Detector<?> aKf;
        final /* synthetic */ CameraSource aKh;
        private boolean aKi;
        private long aKj;
        private int aKk;
        private ByteBuffer aKl;
        private long bZ;
        private final Object zzakd;

        static {
            $assertionsDisabled = !CameraSource.class.desiredAssertionStatus();
        }

        zzb(CameraSource cameraSource, Detector<?> detector) {
            this.aKh = cameraSource;
            this.bZ = SystemClock.elapsedRealtime();
            this.zzakd = new Object();
            this.aKi = true;
            this.aKk = CameraSource.CAMERA_FACING_BACK;
            this.aKf = detector;
        }

        @SuppressLint({"Assert"})
        void release() {
            if ($assertionsDisabled || this.aKh.aKc.getState() == State.TERMINATED) {
                this.aKf.release();
                this.aKf = null;
                return;
            }
            throw new AssertionError();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @android.annotation.SuppressLint({"InlinedApi"})
        public void run() {
            /*
            r6 = this;
        L_0x0000:
            r1 = r6.zzakd;
            monitor-enter(r1);
        L_0x0003:
            r0 = r6.aKi;	 Catch:{ all -> 0x0023 }
            if (r0 == 0) goto L_0x001d;
        L_0x0007:
            r0 = r6.aKl;	 Catch:{ all -> 0x0023 }
            if (r0 != 0) goto L_0x001d;
        L_0x000b:
            r0 = r6.zzakd;	 Catch:{ InterruptedException -> 0x0011 }
            r0.wait();	 Catch:{ InterruptedException -> 0x0011 }
            goto L_0x0003;
        L_0x0011:
            r0 = move-exception;
            r2 = "CameraSource";
            r3 = "Frame processing loop terminated.";
            android.util.Log.d(r2, r3, r0);	 Catch:{ all -> 0x0023 }
            monitor-exit(r1);	 Catch:{ all -> 0x0023 }
        L_0x001c:
            return;
        L_0x001d:
            r0 = r6.aKi;	 Catch:{ all -> 0x0023 }
            if (r0 != 0) goto L_0x0026;
        L_0x0021:
            monitor-exit(r1);	 Catch:{ all -> 0x0023 }
            goto L_0x001c;
        L_0x0023:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0023 }
            throw r0;
        L_0x0026:
            r0 = new com.google.android.gms.vision.Frame$Builder;	 Catch:{ all -> 0x0023 }
            r0.<init>();	 Catch:{ all -> 0x0023 }
            r2 = r6.aKl;	 Catch:{ all -> 0x0023 }
            r3 = r6.aKh;	 Catch:{ all -> 0x0023 }
            r3 = r3.aJU;	 Catch:{ all -> 0x0023 }
            r3 = r3.getWidth();	 Catch:{ all -> 0x0023 }
            r4 = r6.aKh;	 Catch:{ all -> 0x0023 }
            r4 = r4.aJU;	 Catch:{ all -> 0x0023 }
            r4 = r4.getHeight();	 Catch:{ all -> 0x0023 }
            r5 = 17;
            r0 = r0.setImageData(r2, r3, r4, r5);	 Catch:{ all -> 0x0023 }
            r2 = r6.aKk;	 Catch:{ all -> 0x0023 }
            r0 = r0.setId(r2);	 Catch:{ all -> 0x0023 }
            r2 = r6.aKj;	 Catch:{ all -> 0x0023 }
            r0 = r0.setTimestampMillis(r2);	 Catch:{ all -> 0x0023 }
            r2 = r6.aKh;	 Catch:{ all -> 0x0023 }
            r2 = r2.zzbvy;	 Catch:{ all -> 0x0023 }
            r0 = r0.setRotation(r2);	 Catch:{ all -> 0x0023 }
            r0 = r0.build();	 Catch:{ all -> 0x0023 }
            r2 = r6.aKl;	 Catch:{ all -> 0x0023 }
            r3 = 0;
            r6.aKl = r3;	 Catch:{ all -> 0x0023 }
            monitor-exit(r1);	 Catch:{ all -> 0x0023 }
            r1 = r6.aKf;	 Catch:{ Throwable -> 0x007a }
            r1.receiveFrame(r0);	 Catch:{ Throwable -> 0x007a }
            r0 = r6.aKh;
            r0 = r0.aJS;
            r1 = r2.array();
            r0.addCallbackBuffer(r1);
            goto L_0x0000;
        L_0x007a:
            r0 = move-exception;
            r1 = "CameraSource";
            r3 = "Exception thrown from receiver.";
            android.util.Log.e(r1, r3, r0);	 Catch:{ all -> 0x0093 }
            r0 = r6.aKh;
            r0 = r0.aJS;
            r1 = r2.array();
            r0.addCallbackBuffer(r1);
            goto L_0x0000;
        L_0x0093:
            r0 = move-exception;
            r1 = r6.aKh;
            r1 = r1.aJS;
            r2 = r2.array();
            r1.addCallbackBuffer(r2);
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.vision.CameraSource.zzb.run():void");
        }

        void setActive(boolean z) {
            synchronized (this.zzakd) {
                this.aKi = z;
                this.zzakd.notifyAll();
            }
        }

        void zza(byte[] bArr, Camera camera) {
            synchronized (this.zzakd) {
                if (this.aKl != null) {
                    camera.addCallbackBuffer(this.aKl.array());
                    this.aKl = null;
                }
                if (this.aKh.aKe.containsKey(bArr)) {
                    this.aKj = SystemClock.elapsedRealtime() - this.bZ;
                    this.aKk += CameraSource.CAMERA_FACING_FRONT;
                    this.aKl = (ByteBuffer) this.aKh.aKe.get(bArr);
                    this.zzakd.notifyAll();
                    return;
                }
                Log.d("CameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
            }
        }
    }

    private class zzc implements android.hardware.Camera.PictureCallback {
        final /* synthetic */ CameraSource aKh;
        private PictureCallback aKm;

        private zzc(CameraSource cameraSource) {
            this.aKh = cameraSource;
        }

        public void onPictureTaken(byte[] bArr, Camera camera) {
            if (this.aKm != null) {
                this.aKm.onPictureTaken(bArr);
            }
            synchronized (this.aKh.aJR) {
                if (this.aKh.aJS != null) {
                    this.aKh.aJS.startPreview();
                }
            }
        }
    }

    private class zzd implements android.hardware.Camera.ShutterCallback {
        final /* synthetic */ CameraSource aKh;
        private ShutterCallback aKn;

        private zzd(CameraSource cameraSource) {
            this.aKh = cameraSource;
        }

        public void onShutter() {
            if (this.aKn != null) {
                this.aKn.onShutter();
            }
        }
    }

    static class zze {
        private Size aKo;
        private Size aKp;

        public zze(Camera.Size size, Camera.Size size2) {
            this.aKo = new Size(size.width, size.height);
            if (size2 != null) {
                this.aKp = new Size(size2.width, size2.height);
            }
        }

        public Size zzclm() {
            return this.aKo;
        }

        public Size zzcln() {
            return this.aKp;
        }
    }

    private CameraSource() {
        this.aJR = new Object();
        this.aJT = CAMERA_FACING_BACK;
        this.aJV = BitmapDescriptorFactory.HUE_ORANGE;
        this.aJW = TLRPC.MESSAGE_FLAG_HAS_VIEWS;
        this.aJX = 768;
        this.aJY = false;
        this.aKe = new HashMap();
    }

    static zze zza(Camera camera, int i, int i2) {
        List<zze> zza = zza(camera);
        zze com_google_android_gms_vision_CameraSource_zze = null;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (zze com_google_android_gms_vision_CameraSource_zze2 : zza) {
            zze com_google_android_gms_vision_CameraSource_zze3;
            int i4;
            Size zzclm = com_google_android_gms_vision_CameraSource_zze2.zzclm();
            int abs = Math.abs(zzclm.getHeight() - i2) + Math.abs(zzclm.getWidth() - i);
            if (abs < i3) {
                int i5 = abs;
                com_google_android_gms_vision_CameraSource_zze3 = com_google_android_gms_vision_CameraSource_zze2;
                i4 = i5;
            } else {
                i4 = i3;
                com_google_android_gms_vision_CameraSource_zze3 = com_google_android_gms_vision_CameraSource_zze;
            }
            i3 = i4;
            com_google_android_gms_vision_CameraSource_zze = com_google_android_gms_vision_CameraSource_zze3;
        }
        return com_google_android_gms_vision_CameraSource_zze;
    }

    static List<zze> zza(Camera camera) {
        Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        List<zze> arrayList = new ArrayList();
        for (Camera.Size size : supportedPreviewSizes) {
            float f = ((float) size.width) / ((float) size.height);
            for (Camera.Size size2 : supportedPictureSizes) {
                if (Math.abs(f - (((float) size2.width) / ((float) size2.height))) < 0.01f) {
                    arrayList.add(new zze(size, size2));
                    break;
                }
            }
        }
        if (arrayList.size() == 0) {
            Log.w("CameraSource", "No preview sizes have a corresponding same-aspect-ratio picture size");
            for (Camera.Size size3 : supportedPreviewSizes) {
                arrayList.add(new zze(size3, null));
            }
        }
        return arrayList;
    }

    private void zza(Camera camera, Parameters parameters, int i) {
        int i2;
        int rotation = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation();
        switch (rotation) {
            case CAMERA_FACING_BACK /*0*/:
                rotation = CAMERA_FACING_BACK;
                break;
            case CAMERA_FACING_FRONT /*1*/:
                rotation = 90;
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                rotation = 180;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                rotation = 270;
                break;
            default:
                Log.e("CameraSource", "Bad rotation value: " + rotation);
                rotation = CAMERA_FACING_BACK;
                break;
        }
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(i, cameraInfo);
        if (cameraInfo.facing == CAMERA_FACING_FRONT) {
            i2 = (rotation + cameraInfo.orientation) % 360;
            rotation = (360 - i2) % 360;
        } else {
            rotation = ((cameraInfo.orientation - rotation) + 360) % 360;
            i2 = rotation;
        }
        this.zzbvy = i2 / 90;
        camera.setDisplayOrientation(rotation);
        parameters.setRotation(i2);
    }

    @SuppressLint({"InlinedApi"})
    private byte[] zza(Size size) {
        Object obj = new byte[(((int) Math.ceil(((double) ((long) (ImageFormat.getBitsPerPixel(17) * (size.getHeight() * size.getWidth())))) / 8.0d)) + CAMERA_FACING_FRONT)];
        ByteBuffer wrap = ByteBuffer.wrap(obj);
        if (wrap.hasArray() && wrap.array() == obj) {
            this.aKe.put(obj, wrap);
            return obj;
        }
        throw new IllegalStateException("Failed to create valid buffer for camera source.");
    }

    @SuppressLint({"InlinedApi"})
    static int[] zza(Camera camera, float f) {
        int i = (int) (1000.0f * f);
        int[] iArr = null;
        int i2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (int[] iArr2 : camera.getParameters().getSupportedPreviewFpsRange()) {
            int[] iArr3;
            int i3;
            int abs = Math.abs(i - iArr2[CAMERA_FACING_BACK]) + Math.abs(i - iArr2[CAMERA_FACING_FRONT]);
            if (abs < i2) {
                int i4 = abs;
                iArr3 = iArr2;
                i3 = i4;
            } else {
                i3 = i2;
                iArr3 = iArr;
            }
            i2 = i3;
            iArr = iArr3;
        }
        return iArr;
    }

    private static int zzaaz(int i) {
        CameraInfo cameraInfo = new CameraInfo();
        for (int i2 = CAMERA_FACING_BACK; i2 < Camera.getNumberOfCameras(); i2 += CAMERA_FACING_FRONT) {
            Camera.getCameraInfo(i2, cameraInfo);
            if (cameraInfo.facing == i) {
                return i2;
            }
        }
        return -1;
    }

    @SuppressLint({"InlinedApi"})
    private Camera zzcll() {
        int zzaaz = zzaaz(this.aJT);
        if (zzaaz == -1) {
            throw new RuntimeException("Could not find requested camera.");
        }
        Camera open = Camera.open(zzaaz);
        zze zza = zza(open, this.aJW, this.aJX);
        if (zza == null) {
            throw new RuntimeException("Could not find suitable preview size.");
        }
        Size zzcln = zza.zzcln();
        this.aJU = zza.zzclm();
        int[] zza2 = zza(open, this.aJV);
        if (zza2 == null) {
            throw new RuntimeException("Could not find suitable preview frames per second range.");
        }
        Parameters parameters = open.getParameters();
        if (zzcln != null) {
            parameters.setPictureSize(zzcln.getWidth(), zzcln.getHeight());
        }
        parameters.setPreviewSize(this.aJU.getWidth(), this.aJU.getHeight());
        parameters.setPreviewFpsRange(zza2[CAMERA_FACING_BACK], zza2[CAMERA_FACING_FRONT]);
        parameters.setPreviewFormat(17);
        zza(open, parameters, zzaaz);
        if (this.aJY) {
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.i("CameraSource", "Camera auto focus is not supported on this device.");
            }
        }
        open.setParameters(parameters);
        open.setPreviewCallbackWithBuffer(new zza());
        open.addCallbackBuffer(zza(this.aJU));
        open.addCallbackBuffer(zza(this.aJU));
        open.addCallbackBuffer(zza(this.aJU));
        open.addCallbackBuffer(zza(this.aJU));
        return open;
    }

    public int getCameraFacing() {
        return this.aJT;
    }

    public Size getPreviewSize() {
        return this.aJU;
    }

    public void release() {
        synchronized (this.aJR) {
            stop();
            this.aKd.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start() {
        synchronized (this.aJR) {
            if (this.aJS != null) {
            } else {
                this.aJS = zzcll();
                if (VERSION.SDK_INT >= 11) {
                    this.aKa = new SurfaceTexture(100);
                    this.aJS.setPreviewTexture(this.aKa);
                    this.aKb = true;
                } else {
                    this.aJZ = new SurfaceView(this.mContext);
                    this.aJS.setPreviewDisplay(this.aJZ.getHolder());
                    this.aKb = false;
                }
                this.aJS.startPreview();
                this.aKc = new Thread(this.aKd);
                this.aKd.setActive(true);
                this.aKc.start();
            }
        }
        return this;
    }

    @RequiresPermission("android.permission.CAMERA")
    public CameraSource start(SurfaceHolder surfaceHolder) {
        synchronized (this.aJR) {
            if (this.aJS != null) {
            } else {
                this.aJS = zzcll();
                this.aJS.setPreviewDisplay(surfaceHolder);
                this.aJS.startPreview();
                this.aKc = new Thread(this.aKd);
                this.aKd.setActive(true);
                this.aKc.start();
                this.aKb = false;
            }
        }
        return this;
    }

    public void stop() {
        synchronized (this.aJR) {
            this.aKd.setActive(false);
            if (this.aKc != null) {
                try {
                    this.aKc.join();
                } catch (InterruptedException e) {
                    Log.d("CameraSource", "Frame processing thread interrupted on release.");
                }
                this.aKc = null;
            }
            if (this.aJS != null) {
                this.aJS.stopPreview();
                this.aJS.setPreviewCallbackWithBuffer(null);
                try {
                    if (this.aKb) {
                        this.aJS.setPreviewTexture(null);
                    } else {
                        this.aJS.setPreviewDisplay(null);
                    }
                } catch (Exception e2) {
                    String valueOf = String.valueOf(e2);
                    Log.e("CameraSource", new StringBuilder(String.valueOf(valueOf).length() + 32).append("Failed to clear camera preview: ").append(valueOf).toString());
                }
                this.aJS.release();
                this.aJS = null;
            }
            this.aKe.clear();
        }
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback pictureCallback) {
        synchronized (this.aJR) {
            if (this.aJS != null) {
                android.hardware.Camera.ShutterCallback com_google_android_gms_vision_CameraSource_zzd = new zzd();
                com_google_android_gms_vision_CameraSource_zzd.aKn = shutterCallback;
                android.hardware.Camera.PictureCallback com_google_android_gms_vision_CameraSource_zzc = new zzc();
                com_google_android_gms_vision_CameraSource_zzc.aKm = pictureCallback;
                this.aJS.takePicture(com_google_android_gms_vision_CameraSource_zzd, null, null, com_google_android_gms_vision_CameraSource_zzc);
            }
        }
    }
}
