package com.hanista.mobogram.messenger.camera;

import android.hardware.Camera;
import java.util.ArrayList;

public class CameraInfo {
    protected Camera camera;
    protected int cameraId;
    protected final int frontCamera;
    protected ArrayList<Size> pictureSizes;
    protected ArrayList<Size> previewSizes;

    public CameraInfo(int i, android.hardware.Camera.CameraInfo cameraInfo) {
        this.pictureSizes = new ArrayList();
        this.previewSizes = new ArrayList();
        this.cameraId = i;
        this.frontCamera = cameraInfo.facing;
    }

    private Camera getCamera() {
        return this.camera;
    }

    public int getCameraId() {
        return this.cameraId;
    }

    public ArrayList<Size> getPictureSizes() {
        return this.pictureSizes;
    }

    public ArrayList<Size> getPreviewSizes() {
        return this.previewSizes;
    }
}
