package com.hanista.mobogram.messenger.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

@SuppressLint({"NewApi"})
public class CameraView extends FrameLayout implements SurfaceTextureListener {
    private CameraSession cameraSession;
    private int clipLeft;
    private int clipTop;
    private CameraViewDelegate delegate;
    private boolean initied;
    private boolean isFrontface;
    private boolean mirror;
    private Size previewSize;
    private TextureView textureView;
    private Matrix txform;

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraView.1 */
    class C06991 implements Runnable {
        C06991() {
        }

        public void run() {
            if (CameraView.this.cameraSession != null) {
                CameraView.this.cameraSession.setInitied();
            }
            CameraView.this.checkPreviewMatrix();
        }
    }

    public interface CameraViewDelegate {
        void onCameraInit();
    }

    public CameraView(Context context) {
        super(context, null);
        this.txform = new Matrix();
        this.textureView = new TextureView(context);
        this.textureView.setSurfaceTextureListener(this);
        addView(this.textureView);
    }

    private void adjustAspectRatio(int i, int i2, int i3) {
        this.txform.reset();
        int width = getWidth();
        int height = getHeight();
        float f = (float) (width / 2);
        float f2 = (float) (height / 2);
        float max = (i3 == 0 || i3 == 2) ? Math.max(((float) (this.clipTop + height)) / ((float) i), ((float) (this.clipLeft + width)) / ((float) i2)) : Math.max(((float) (this.clipTop + height)) / ((float) i2), ((float) (this.clipLeft + width)) / ((float) i));
        this.txform.postScale((max * ((float) i2)) / ((float) width), (((float) i) * max) / ((float) height), f, f2);
        if (1 == i3 || 3 == i3) {
            this.txform.postRotate((float) ((i3 - 2) * 90), f, f2);
        } else if (2 == i3) {
            this.txform.postRotate(BitmapDescriptorFactory.HUE_CYAN, f, f2);
        }
        if (this.mirror) {
            this.txform.postScale(Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, f, f2);
        }
        if (!(this.clipTop == 0 && this.clipLeft == 0)) {
            this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
        }
        this.textureView.setTransform(this.txform);
    }

    private void checkPreviewMatrix() {
        if (this.previewSize != null) {
            adjustAspectRatio(this.previewSize.getWidth(), this.previewSize.getHeight(), ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    private void initCamera(boolean z) {
        CameraInfo cameraInfo;
        ArrayList cameras = CameraController.getInstance().getCameras();
        for (int i = 0; i < cameras.size(); i++) {
            CameraInfo cameraInfo2 = (CameraInfo) cameras.get(i);
            if ((this.isFrontface && cameraInfo2.frontCamera != 0) || (!this.isFrontface && cameraInfo2.frontCamera == 0)) {
                cameraInfo = cameraInfo2;
                break;
            }
        }
        cameraInfo = null;
        if (cameraInfo != null) {
            Size size = Math.abs((((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))) - 1.3333334f) < 0.1f ? new Size(4, 3) : new Size(16, 9);
            if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                int min = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                this.previewSize = CameraController.chooseOptimalSize(cameraInfo.getPreviewSizes(), min, (size.getHeight() * min) / size.getWidth(), size);
            }
            size = CameraController.chooseOptimalSize(cameraInfo.getPictureSizes(), 1280, 1280, size);
            if (this.previewSize != null && this.textureView.getSurfaceTexture() != null) {
                this.textureView.getSurfaceTexture().setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                this.cameraSession = new CameraSession(cameraInfo, this.previewSize, size, TLRPC.USER_FLAG_UNUSED2);
                CameraController.getInstance().open(this.cameraSession, this.textureView.getSurfaceTexture(), new C06991());
            }
        }
    }

    public void destroy(boolean z) {
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !z ? new Semaphore(0) : null);
        }
    }

    public CameraSession getCameraSession() {
        return this.cameraSession;
    }

    public boolean hasFrontFaceCamera() {
        ArrayList cameras = CameraController.getInstance().getCameras();
        for (int i = 0; i < cameras.size(); i++) {
            if (((CameraInfo) cameras.get(i)).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public boolean isInitied() {
        return this.initied;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        checkPreviewMatrix();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        initCamera(this.isFrontface);
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null);
        }
        return false;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        checkPreviewMatrix();
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (!this.initied && this.cameraSession != null && this.cameraSession.isInitied()) {
            if (this.delegate != null) {
                this.delegate.onCameraInit();
            }
            this.initied = true;
        }
    }

    public void setClipLeft(int i) {
        this.clipLeft = i;
    }

    public void setClipTop(int i) {
        this.clipTop = i;
    }

    public void setDelegate(CameraViewDelegate cameraViewDelegate) {
        this.delegate = cameraViewDelegate;
    }

    public void setMirror(boolean z) {
        this.mirror = z;
    }

    public void switchCamera() {
        boolean z = false;
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null);
            this.cameraSession = null;
        }
        this.initied = false;
        if (!this.isFrontface) {
            z = true;
        }
        this.isFrontface = z;
        initCamera(this.isFrontface);
    }
}
