package com.hanista.mobogram.messenger.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;

public class CameraSession {
    protected CameraInfo cameraInfo;
    private String currentFlashMode;
    private boolean initied;
    private boolean isVideo;
    private int lastOrientation;
    private OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraSession.1 */
    class C06981 extends OrientationEventListener {
        C06981(Context context) {
            super(context);
        }

        public void onOrientationChanged(int i) {
            if (CameraSession.this.orientationEventListener != null && CameraSession.this.initied) {
                int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                if (CameraSession.this.lastOrientation != rotation) {
                    if (!CameraSession.this.isVideo) {
                        CameraSession.this.configurePhotoCamera();
                    }
                    CameraSession.this.lastOrientation = rotation;
                }
            }
        }
    }

    public CameraSession(CameraInfo cameraInfo, Size size, Size size2, int i) {
        this.currentFlashMode = "off";
        this.lastOrientation = -1;
        this.previewSize = size;
        this.pictureSize = size2;
        this.pictureFormat = i;
        this.cameraInfo = cameraInfo;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        this.orientationEventListener = new C06981(ApplicationLoader.applicationContext);
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    private int getDisplayOrientation(CameraInfo cameraInfo, boolean z) {
        int i;
        switch (((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                i = 0;
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                i = 90;
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                i = 180;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                i = 270;
                break;
            default:
                i = 0;
                break;
        }
        if (cameraInfo.facing != 1) {
            return ((cameraInfo.orientation - i) + 360) % 360;
        }
        i = (360 - ((i + cameraInfo.orientation) % 360)) % 360;
        if (!z && i == 90) {
            i = 270;
        }
        return (!z && "Huawei".equals(Build.MANUFACTURER) && "angler".equals(Build.PRODUCT) && i == 270) ? 90 : i;
    }

    private int getHigh() {
        return ("LGE".equals(Build.MANUFACTURER) && "g3_tmo_us".equals(Build.PRODUCT)) ? 4 : 1;
    }

    public void checkFlashMode(String str) {
        if (!CameraController.getInstance().availableFlashModes.contains(this.currentFlashMode)) {
            this.currentFlashMode = str;
            configurePhotoCamera();
            ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", str).commit();
        }
    }

    protected void configurePhotoCamera() {
        int i = 0;
        Camera camera = this.cameraInfo.camera;
        if (camera != null) {
            Parameters parameters;
            CameraInfo cameraInfo = new CameraInfo();
            Parameters parameters2 = null;
            try {
                parameters = camera.getParameters();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                parameters = parameters2;
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
                return;
            }
            Camera.getCameraInfo(this.cameraInfo.getCameraId(), cameraInfo);
            int displayOrientation = getDisplayOrientation(cameraInfo, true);
            if (!("samsung".equals(Build.MANUFACTURER) && "sf2wifixx".equals(Build.PRODUCT))) {
                switch (displayOrientation) {
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        i = 90;
                        break;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        i = 180;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        i = 270;
                        break;
                }
                if (cameraInfo.orientation % 90 != 0) {
                    cameraInfo.orientation = 0;
                }
                i = cameraInfo.facing == 1 ? (360 - ((i + cameraInfo.orientation) % 360)) % 360 : ((cameraInfo.orientation - i) + 360) % 360;
            }
            camera.setDisplayOrientation(i);
            if (parameters != null) {
                parameters.setPreviewSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                parameters.setPictureSize(this.pictureSize.getWidth(), this.pictureSize.getHeight());
                parameters.setPictureFormat(this.pictureFormat);
                String str = "continuous-picture";
                if (parameters.getSupportedFocusModes().contains(str)) {
                    parameters.setFocusMode(str);
                }
                try {
                    parameters.setRotation(cameraInfo.facing == 1 ? (360 - displayOrientation) % 360 : displayOrientation);
                } catch (Exception e2) {
                }
                parameters.setFlashMode(this.currentFlashMode);
                try {
                    camera.setParameters(parameters);
                } catch (Exception e3) {
                }
            }
        }
    }

    protected void configureRecorder(int i, MediaRecorder mediaRecorder) {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(this.cameraInfo.cameraId, cameraInfo);
        mediaRecorder.setOrientationHint(getDisplayOrientation(cameraInfo, false));
        int high = getHigh();
        boolean hasProfile = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, high);
        boolean hasProfile2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (hasProfile && (i == 1 || !hasProfile2)) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, high));
        } else if (hasProfile2) {
            mediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
        } else {
            throw new IllegalStateException("cannot find valid CamcorderProfile");
        }
        this.isVideo = true;
    }

    public void destroy() {
        this.initied = false;
        if (this.orientationEventListener != null) {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }

    public String getCurrentFlashMode() {
        return this.currentFlashMode;
    }

    public String getNextFlashMode() {
        ArrayList arrayList = CameraController.getInstance().availableFlashModes;
        int i = 0;
        while (i < arrayList.size()) {
            if (((String) arrayList.get(i)).equals(this.currentFlashMode)) {
                return i < arrayList.size() + -1 ? (String) arrayList.get(i + 1) : (String) arrayList.get(0);
            } else {
                i++;
            }
        }
        return this.currentFlashMode;
    }

    protected boolean isInitied() {
        return this.initied;
    }

    public void setCurrentFlashMode(String str) {
        this.currentFlashMode = str;
        configurePhotoCamera();
        ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", str).commit();
    }

    protected void setInitied() {
        this.initied = true;
    }

    protected void stopVideoRecording() {
        this.isVideo = false;
        configurePhotoCamera();
    }
}
