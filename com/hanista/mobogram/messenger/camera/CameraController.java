package com.hanista.mobogram.messenger.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.os.Build;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Bitmaps;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CameraController implements OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE;
    protected ArrayList<String> availableFlashModes;
    protected ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
    private ThreadPoolExecutor threadPool;

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.1 */
    class C06901 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.1.1 */
        class C06891 implements Runnable {
            C06891() {
            }

            public void run() {
                CameraController.this.cameraInitied = true;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
            }
        }

        C06901() {
        }

        public void run() {
            try {
                if (CameraController.this.cameraInfos == null) {
                    int numberOfCameras = Camera.getNumberOfCameras();
                    ArrayList arrayList = new ArrayList();
                    CameraInfo cameraInfo = new CameraInfo();
                    for (int i = 0; i < numberOfCameras; i += CameraController.CORE_POOL_SIZE) {
                        int i2;
                        Size size;
                        Camera.getCameraInfo(i, cameraInfo);
                        CameraInfo cameraInfo2 = new CameraInfo(i, cameraInfo);
                        Camera open = Camera.open(cameraInfo2.getCameraId());
                        Parameters parameters = open.getParameters();
                        List supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                        for (i2 = 0; i2 < supportedPreviewSizes.size(); i2 += CameraController.CORE_POOL_SIZE) {
                            size = (Size) supportedPreviewSizes.get(i2);
                            if (size.height < 2160 && size.width < 2160) {
                                cameraInfo2.previewSizes.add(new Size(size.width, size.height));
                            }
                        }
                        List supportedPictureSizes = parameters.getSupportedPictureSizes();
                        for (i2 = 0; i2 < supportedPictureSizes.size(); i2 += CameraController.CORE_POOL_SIZE) {
                            size = (Size) supportedPictureSizes.get(i2);
                            if (!"samsung".equals(Build.MANUFACTURER) || !"jflteuc".equals(Build.PRODUCT) || size.width < TLRPC.MESSAGE_FLAG_HAS_BOT_ID) {
                                cameraInfo2.pictureSizes.add(new Size(size.width, size.height));
                            }
                        }
                        open.release();
                        arrayList.add(cameraInfo2);
                    }
                    CameraController.this.cameraInfos = arrayList;
                }
                AndroidUtilities.runOnUIThread(new C06891());
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.2 */
    class C06912 implements Runnable {
        C06912() {
        }

        public void run() {
            if (CameraController.this.cameraInfos != null && !CameraController.this.cameraInfos.isEmpty()) {
                for (int i = 0; i < CameraController.this.cameraInfos.size(); i += CameraController.CORE_POOL_SIZE) {
                    CameraInfo cameraInfo = (CameraInfo) CameraController.this.cameraInfos.get(i);
                    if (cameraInfo.camera != null) {
                        cameraInfo.camera.stopPreview();
                        cameraInfo.camera.release();
                        cameraInfo.camera = null;
                    }
                }
                CameraController.this.cameraInfos = null;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.3 */
    class C06923 implements Runnable {
        final /* synthetic */ Camera val$camera;
        final /* synthetic */ Semaphore val$semaphore;

        C06923(Camera camera, Semaphore semaphore) {
            this.val$camera = camera;
            this.val$semaphore = semaphore;
        }

        public void run() {
            try {
                if (this.val$camera != null) {
                    this.val$camera.stopPreview();
                    this.val$camera.release();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.val$semaphore != null) {
                this.val$semaphore.release();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.4 */
    class C06934 implements PictureCallback {
        final /* synthetic */ Runnable val$callback;
        final /* synthetic */ CameraInfo val$info;
        final /* synthetic */ File val$path;

        C06934(File file, CameraInfo cameraInfo, Runnable runnable) {
            this.val$path = file;
            this.val$info = cameraInfo;
            this.val$callback = runnable;
        }

        public void onPictureTaken(byte[] bArr, Camera camera) {
            Bitmap decodeByteArray;
            int photoSize = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
            String format = String.format(Locale.US, "%s@%d_%d", new Object[]{Utilities.MD5(this.val$path.getAbsolutePath()), Integer.valueOf(photoSize), Integer.valueOf(photoSize)});
            try {
                Options options = new Options();
                options.inPurgeable = true;
                decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
                decodeByteArray = null;
            }
            try {
                if (this.val$info.frontCamera != 0) {
                    try {
                        Matrix matrix = new Matrix();
                        matrix.setRotate((float) CameraController.getOrientation(bArr));
                        matrix.postScale(Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        Bitmap createBitmap = Bitmaps.createBitmap(decodeByteArray, 0, 0, decodeByteArray.getWidth(), decodeByteArray.getHeight(), matrix, false);
                        decodeByteArray.recycle();
                        OutputStream fileOutputStream = new FileOutputStream(this.val$path);
                        createBitmap.compress(CompressFormat.JPEG, 80, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.getFD().sync();
                        fileOutputStream.close();
                        if (createBitmap != null) {
                            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(createBitmap), format);
                        }
                        if (this.val$callback != null) {
                            this.val$callback.run();
                            return;
                        }
                        return;
                    } catch (Throwable th2) {
                        FileLog.m18e("tmessages", th2);
                    }
                }
                FileOutputStream fileOutputStream2 = new FileOutputStream(this.val$path);
                fileOutputStream2.write(bArr);
                fileOutputStream2.flush();
                fileOutputStream2.getFD().sync();
                fileOutputStream2.close();
                if (decodeByteArray != null) {
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(decodeByteArray), format);
                }
            } catch (Throwable th3) {
                FileLog.m18e("tmessages", th3);
            }
            if (this.val$callback != null) {
                this.val$callback.run();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.5 */
    class C06945 implements Runnable {
        final /* synthetic */ CameraSession val$session;

        C06945(CameraSession cameraSession) {
            this.val$session = cameraSession;
        }

        @SuppressLint({"NewApi"})
        public void run() {
            Camera camera = this.val$session.cameraInfo.camera;
            if (camera == null) {
                try {
                    CameraInfo cameraInfo = this.val$session.cameraInfo;
                    Camera open = Camera.open(this.val$session.cameraInfo.cameraId);
                    cameraInfo.camera = open;
                    camera = open;
                } catch (Throwable e) {
                    this.val$session.cameraInfo.camera = null;
                    if (camera != null) {
                        camera.release();
                    }
                    FileLog.m18e("tmessages", e);
                    return;
                }
            }
            camera.startPreview();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.6 */
    class C06956 implements Runnable {
        final /* synthetic */ Runnable val$callback;
        final /* synthetic */ CameraSession val$session;
        final /* synthetic */ SurfaceTexture val$texture;

        C06956(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable) {
            this.val$session = cameraSession;
            this.val$texture = surfaceTexture;
            this.val$callback = runnable;
        }

        @SuppressLint({"NewApi"})
        public void run() {
            Camera camera = this.val$session.cameraInfo.camera;
            if (camera == null) {
                try {
                    CameraInfo cameraInfo = this.val$session.cameraInfo;
                    Camera open = Camera.open(this.val$session.cameraInfo.cameraId);
                    cameraInfo.camera = open;
                    camera = open;
                } catch (Throwable e) {
                    this.val$session.cameraInfo.camera = null;
                    if (camera != null) {
                        camera.release();
                    }
                    FileLog.m18e("tmessages", e);
                    return;
                }
            }
            List supportedFlashModes = camera.getParameters().getSupportedFlashModes();
            CameraController.this.availableFlashModes.clear();
            if (supportedFlashModes != null) {
                for (int i = 0; i < supportedFlashModes.size(); i += CameraController.CORE_POOL_SIZE) {
                    String str = (String) supportedFlashModes.get(i);
                    if (str.equals("off") || str.equals("on") || str.equals("auto")) {
                        CameraController.this.availableFlashModes.add(str);
                    }
                }
                this.val$session.checkFlashMode((String) CameraController.this.availableFlashModes.get(0));
            }
            this.val$session.configurePhotoCamera();
            camera.setPreviewTexture(this.val$texture);
            camera.startPreview();
            if (this.val$callback != null) {
                AndroidUtilities.runOnUIThread(this.val$callback);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.7 */
    class C06967 implements Runnable {
        final /* synthetic */ Bitmap val$bitmap;

        C06967(Bitmap bitmap) {
            this.val$bitmap = bitmap;
        }

        public void run() {
            CameraController.this.onVideoTakeCallback.onFinishVideoRecording(this.val$bitmap);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.camera.CameraController.8 */
    class C06978 implements Runnable {
        final /* synthetic */ Bitmap val$bitmap;

        C06978(Bitmap bitmap) {
            this.val$bitmap = bitmap;
        }

        public void run() {
            CameraController.this.onVideoTakeCallback.onFinishVideoRecording(this.val$bitmap);
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size size, Size size2) {
            return Long.signum((((long) size.getWidth()) * ((long) size.getHeight())) - (((long) size2.getWidth()) * ((long) size2.getHeight())));
        }
    }

    public interface VideoTakeCallback {
        void onFinishVideoRecording(Bitmap bitmap);
    }

    static {
        MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
        Instance = null;
    }

    public CameraController() {
        this.availableFlashModes = new ArrayList();
        this.cameraInfos = null;
        this.threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    public static Size chooseOptimalSize(List<Size> list, int i, int i2, Size size) {
        Collection arrayList = new ArrayList();
        int width = size.getWidth();
        int height = size.getHeight();
        for (int i3 = 0; i3 < list.size(); i3 += CORE_POOL_SIZE) {
            Size size2 = (Size) list.get(i3);
            if (size2.getHeight() == (size2.getWidth() * height) / width && size2.getWidth() >= i && size2.getHeight() >= i2) {
                arrayList.add(size2);
            }
        }
        return arrayList.size() > 0 ? (Size) Collections.min(arrayList, new CompareSizesByArea()) : (Size) Collections.max(list, new CompareSizesByArea());
    }

    public static CameraController getInstance() {
        CameraController cameraController = Instance;
        if (cameraController == null) {
            synchronized (CameraController.class) {
                cameraController = Instance;
                if (cameraController == null) {
                    cameraController = new CameraController();
                    Instance = cameraController;
                }
            }
        }
        return cameraController;
    }

    private static int getOrientation(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        int i;
        int i2;
        int pack;
        int i3 = 0;
        while (i3 + 3 < bArr.length) {
            i = i3 + CORE_POOL_SIZE;
            if ((bArr[i3] & NalUnitUtil.EXTENDED_SAR) != NalUnitUtil.EXTENDED_SAR) {
                i2 = i;
                i = 0;
                break;
            }
            i2 = bArr[i] & NalUnitUtil.EXTENDED_SAR;
            if (i2 != NalUnitUtil.EXTENDED_SAR) {
                i3 = i + CORE_POOL_SIZE;
                if (!(i2 == 216 || i2 == CORE_POOL_SIZE)) {
                    if (i2 != 217) {
                        if (i2 != 218) {
                            pack = pack(bArr, i3, 2, false);
                            if (pack >= 2 && i3 + pack <= bArr.length) {
                                if (i2 == 225 && pack >= 8 && pack(bArr, i3 + 2, 4, false) == 1165519206 && pack(bArr, i3 + 6, 2, false) == 0) {
                                    i2 = i3 + 8;
                                    i = pack - 8;
                                    break;
                                }
                                i3 += pack;
                            } else {
                                return 0;
                            }
                        }
                        i = 0;
                        i2 = i3;
                        break;
                    }
                    break;
                }
            }
            i3 = i;
        }
        i = 0;
        i2 = i3;
        if (i <= 8) {
            return 0;
        }
        i3 = pack(bArr, i2, 4, false);
        if (i3 != 1229531648 && i3 != 1296891946) {
            return 0;
        }
        boolean z = i3 == 1229531648;
        int pack2 = pack(bArr, i2 + 4, 4, z) + 2;
        if (pack2 < 10 || pack2 > i) {
            return 0;
        }
        i2 += pack2;
        pack2 = i - pack2;
        i = pack(bArr, i2 - 2, 2, z);
        pack = i2;
        i2 = pack2;
        while (true) {
            pack2 = i - 1;
            if (i <= 0 || i2 < 12) {
                return 0;
            }
            if (pack(bArr, pack, 2, z) == 274) {
                break;
            }
            pack += 12;
            i2 -= 12;
            i = pack2;
        }
        switch (pack(bArr, pack + 8, 2, z)) {
            case CORE_POOL_SIZE /*1*/:
                return 0;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 180;
            case Method.TRACE /*6*/:
                return 90;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return 270;
            default:
                return 0;
        }
    }

    private static int pack(byte[] bArr, int i, int i2, boolean z) {
        int i3 = CORE_POOL_SIZE;
        if (z) {
            i += i2 - 1;
            i3 = -1;
        }
        int i4 = 0;
        while (true) {
            int i5 = i2 - 1;
            if (i2 <= 0) {
                return i4;
            }
            i4 = (i4 << 8) | (bArr[i] & NalUnitUtil.EXTENDED_SAR);
            i += i3;
            i2 = i5;
        }
    }

    public void cleanup() {
        this.threadPool.execute(new C06912());
    }

    public void close(CameraSession cameraSession, Semaphore semaphore) {
        cameraSession.destroy();
        Camera camera = cameraSession.cameraInfo.camera;
        cameraSession.cameraInfo.camera = null;
        this.threadPool.execute(new C06923(camera, semaphore));
        if (semaphore != null) {
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    public void initCamera() {
        if (!this.cameraInitied) {
            this.threadPool.execute(new C06901());
        }
    }

    public boolean isCameraInitied() {
        return (!this.cameraInitied || this.cameraInfos == null || this.cameraInfos.isEmpty()) ? false : true;
    }

    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        if (i == 800 || i == 801 || i == CORE_POOL_SIZE) {
            MediaRecorder mediaRecorder2 = this.recorder;
            this.recorder = null;
            if (mediaRecorder2 != null) {
                mediaRecorder2.stop();
                mediaRecorder2.release();
            }
            AndroidUtilities.runOnUIThread(new C06967(ThumbnailUtils.createVideoThumbnail(this.recordedFile, CORE_POOL_SIZE)));
        }
    }

    public void open(CameraSession cameraSession, SurfaceTexture surfaceTexture, Runnable runnable) {
        if (cameraSession != null && surfaceTexture != null) {
            this.threadPool.execute(new C06956(cameraSession, surfaceTexture, runnable));
        }
    }

    public void recordVideo(CameraSession cameraSession, File file, VideoTakeCallback videoTakeCallback) {
        if (cameraSession != null) {
            try {
                CameraInfo cameraInfo = cameraSession.cameraInfo;
                Camera camera = cameraInfo.camera;
                if (camera != null) {
                    camera.stopPreview();
                    camera.unlock();
                    try {
                        this.recorder = new MediaRecorder();
                        this.recorder.setCamera(camera);
                        this.recorder.setVideoSource(CORE_POOL_SIZE);
                        this.recorder.setAudioSource(5);
                        cameraSession.configureRecorder(CORE_POOL_SIZE, this.recorder);
                        this.recorder.setOutputFile(file.getAbsolutePath());
                        this.recorder.setMaxFileSize(1073741824);
                        this.recorder.setVideoFrameRate(30);
                        this.recorder.setMaxDuration(0);
                        Size chooseOptimalSize = chooseOptimalSize(cameraInfo.getPictureSizes(), 720, 480, new Size(16, 9));
                        this.recorder.setVideoSize(chooseOptimalSize.getWidth(), chooseOptimalSize.getHeight());
                        this.recorder.setVideoEncodingBitRate(1800000);
                        this.recorder.setOnInfoListener(this);
                        this.recorder.prepare();
                        this.recorder.start();
                        this.onVideoTakeCallback = videoTakeCallback;
                        this.recordedFile = file.getAbsolutePath();
                    } catch (Throwable e) {
                        this.recorder.release();
                        this.recorder = null;
                        FileLog.m18e("tmessages", e);
                    }
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
    }

    public void startPreview(CameraSession cameraSession) {
        if (cameraSession != null) {
            this.threadPool.execute(new C06945(cameraSession));
        }
    }

    public void stopVideoRecording(CameraSession cameraSession, boolean z) {
        try {
            Camera camera = cameraSession.cameraInfo.camera;
            if (!(camera == null || this.recorder == null)) {
                MediaRecorder mediaRecorder = this.recorder;
                this.recorder = null;
                mediaRecorder.stop();
                mediaRecorder.release();
                camera.reconnect();
                camera.startPreview();
                cameraSession.stopVideoRecording();
            }
            if (!z) {
                AndroidUtilities.runOnUIThread(new C06978(ThumbnailUtils.createVideoThumbnail(this.recordedFile, CORE_POOL_SIZE)));
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public boolean takePicture(File file, CameraSession cameraSession, Runnable runnable) {
        if (cameraSession == null) {
            return false;
        }
        CameraInfo cameraInfo = cameraSession.cameraInfo;
        try {
            cameraInfo.camera.takePicture(null, null, new C06934(file, cameraInfo, runnable));
            return true;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }
}
