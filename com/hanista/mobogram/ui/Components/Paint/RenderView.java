package com.hanista.mobogram.ui.Components.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.DispatchQueue;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.Components.Paint.Painting.PaintingDelegate;
import com.hanista.mobogram.ui.Components.Size;
import java.util.concurrent.Semaphore;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class RenderView extends TextureView {
    private Bitmap bitmap;
    private Brush brush;
    private int color;
    private RenderViewDelegate delegate;
    private Input input;
    private CanvasInternal internal;
    private int orientation;
    private Painting painting;
    private DispatchQueue queue;
    private boolean shuttingDown;
    private boolean transformedBitmap;
    private UndoStore undoStore;
    private float weight;

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.1 */
    class C13741 implements SurfaceTextureListener {

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.1.1 */
        class C13721 implements Runnable {
            C13721() {
            }

            public void run() {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.requestRender();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.1.2 */
        class C13732 implements Runnable {
            C13732() {
            }

            public void run() {
                RenderView.this.internal.shutdown();
                RenderView.this.internal = null;
            }
        }

        C13741() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            if (surfaceTexture != null && RenderView.this.internal == null) {
                RenderView.this.internal = new CanvasInternal(surfaceTexture);
                RenderView.this.internal.setBufferSize(i, i2);
                RenderView.this.updateTransform();
                RenderView.this.internal.requestRender();
                if (RenderView.this.painting.isPaused()) {
                    RenderView.this.painting.onResume();
                }
            }
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (!(RenderView.this.internal == null || RenderView.this.shuttingDown)) {
                RenderView.this.painting.onPause(new C13732());
            }
            return true;
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            if (RenderView.this.internal != null) {
                RenderView.this.internal.setBufferSize(i, i2);
                RenderView.this.updateTransform();
                RenderView.this.internal.requestRender();
                RenderView.this.internal.postRunnable(new C13721());
            }
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.2 */
    class C13752 implements PaintingDelegate {
        C13752() {
        }

        public void contentChanged(RectF rectF) {
            if (RenderView.this.internal != null) {
                RenderView.this.internal.scheduleRedraw();
            }
        }

        public DispatchQueue requestDispatchQueue() {
            return RenderView.this.queue;
        }

        public UndoStore requestUndoStore() {
            return RenderView.this.undoStore;
        }

        public void strokeCommited() {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.3 */
    class C13763 implements Runnable {
        C13763() {
        }

        public void run() {
            RenderView.this.painting.cleanResources(RenderView.this.transformedBitmap);
            RenderView.this.internal.shutdown();
            RenderView.this.internal = null;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.4 */
    class C13774 implements Runnable {
        final /* synthetic */ Runnable val$action;

        C13774(Runnable runnable) {
            this.val$action = runnable;
        }

        public void run() {
            if (RenderView.this.internal != null && RenderView.this.internal.initialized) {
                RenderView.this.internal.setCurrentContext();
                this.val$action.run();
            }
        }
    }

    private class CanvasInternal extends DispatchQueue {
        private final int EGL_CONTEXT_CLIENT_VERSION;
        private final int EGL_OPENGL_ES2_BIT;
        private int bufferHeight;
        private int bufferWidth;
        private Runnable drawRunnable;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initialized;
        private long lastRenderCallTime;
        private boolean ready;
        private Runnable scheduledRunnable;
        private SurfaceTexture surfaceTexture;

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.CanvasInternal.1 */
        class C13791 implements Runnable {

            /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.CanvasInternal.1.1 */
            class C13781 implements Runnable {
                C13781() {
                }

                public void run() {
                    CanvasInternal.this.ready = true;
                }
            }

            C13791() {
            }

            public void run() {
                if (CanvasInternal.this.initialized && !RenderView.this.shuttingDown) {
                    CanvasInternal.this.setCurrentContext();
                    GLES20.glBindFramebuffer(36160, 0);
                    GLES20.glViewport(0, 0, CanvasInternal.this.bufferWidth, CanvasInternal.this.bufferHeight);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    GLES20.glClear(MessagesController.UPDATE_MASK_CHAT_ADMINS);
                    RenderView.this.painting.render();
                    GLES20.glBlendFunc(1, 771);
                    CanvasInternal.this.egl10.eglSwapBuffers(CanvasInternal.this.eglDisplay, CanvasInternal.this.eglSurface);
                    if (!CanvasInternal.this.ready) {
                        RenderView.this.queue.postRunnable(new C13781(), 200);
                    }
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.CanvasInternal.2 */
        class C13802 implements Runnable {
            C13802() {
            }

            public void run() {
                CanvasInternal.this.drawRunnable.run();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.CanvasInternal.3 */
        class C13813 implements Runnable {
            C13813() {
            }

            public void run() {
                CanvasInternal.this.scheduledRunnable = null;
                CanvasInternal.this.drawRunnable.run();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.CanvasInternal.4 */
        class C13824 implements Runnable {
            C13824() {
            }

            public void run() {
                CanvasInternal.this.finish();
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.RenderView.CanvasInternal.5 */
        class C13835 implements Runnable {
            final /* synthetic */ Bitmap[] val$object;
            final /* synthetic */ Semaphore val$semaphore;

            C13835(Bitmap[] bitmapArr, Semaphore semaphore) {
                this.val$object = bitmapArr;
                this.val$semaphore = semaphore;
            }

            public void run() {
                this.val$object[0] = RenderView.this.painting.getPaintingData(new RectF(0.0f, 0.0f, RenderView.this.painting.getSize().width, RenderView.this.painting.getSize().height), false).bitmap;
                this.val$semaphore.release();
            }
        }

        public CanvasInternal(SurfaceTexture surfaceTexture) {
            super("CanvasInternal");
            this.EGL_CONTEXT_CLIENT_VERSION = 12440;
            this.EGL_OPENGL_ES2_BIT = 4;
            this.drawRunnable = new C13791();
            this.surfaceTexture = surfaceTexture;
        }

        private void checkBitmap() {
            Size size = RenderView.this.painting.getSize();
            if (((float) RenderView.this.bitmap.getWidth()) != size.width || ((float) RenderView.this.bitmap.getHeight()) != size.height || RenderView.this.orientation != 0) {
                float width = (float) RenderView.this.bitmap.getWidth();
                if (RenderView.this.orientation % 360 == 90 || RenderView.this.orientation % 360 == 270) {
                    width = (float) RenderView.this.bitmap.getHeight();
                }
                RenderView.this.bitmap = createBitmap(RenderView.this.bitmap, size.width / width);
                RenderView.this.orientation = 0;
                RenderView.this.transformedBitmap = true;
            }
        }

        private Bitmap createBitmap(Bitmap bitmap, float f) {
            Matrix matrix = new Matrix();
            matrix.setScale(f, f);
            matrix.postRotate((float) RenderView.this.orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        private boolean initGL() {
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                FileLog.m16e("tmessages", "eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                int[] iArr = new int[1];
                EGLConfig[] eGLConfigArr = new EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                    FileLog.m16e("tmessages", "eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    finish();
                    return false;
                } else if (iArr[0] > 0) {
                    this.eglConfig = eGLConfigArr[0];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.eglContext == null) {
                        FileLog.m16e("tmessages", "eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        finish();
                        return false;
                    } else if (this.surfaceTexture instanceof SurfaceTexture) {
                        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
                        if (this.eglSurface == null || this.eglSurface == EGL10.EGL_NO_SURFACE) {
                            FileLog.m16e("tmessages", "createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            finish();
                            return false;
                        } else if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                            GLES20.glEnable(3042);
                            GLES20.glDisable(3024);
                            GLES20.glDisable(2960);
                            GLES20.glDisable(2929);
                            RenderView.this.painting.setupShaders();
                            checkBitmap();
                            RenderView.this.painting.setBitmap(RenderView.this.bitmap);
                            Utils.HasGLError();
                            return true;
                        } else {
                            FileLog.m16e("tmessages", "eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            finish();
                            return false;
                        }
                    } else {
                        finish();
                        return false;
                    }
                } else {
                    FileLog.m16e("tmessages", "eglConfig not initialized");
                    finish();
                    return false;
                }
            }
            FileLog.m16e("tmessages", "eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            finish();
            return false;
        }

        private boolean setCurrentContext() {
            return !this.initialized ? false : (this.eglContext.equals(this.egl10.eglGetCurrentContext()) && this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) || this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext);
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            if (this.eglContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
                this.eglContext = null;
            }
            if (this.eglDisplay != null) {
                this.egl10.eglTerminate(this.eglDisplay);
                this.eglDisplay = null;
            }
        }

        public Bitmap getTexture() {
            if (!this.initialized) {
                return null;
            }
            Semaphore semaphore = new Semaphore(0);
            Bitmap[] bitmapArr = new Bitmap[1];
            try {
                postRunnable(new C13835(bitmapArr, semaphore));
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            return bitmapArr[0];
        }

        public void requestRender() {
            postRunnable(new C13802());
        }

        public void run() {
            if (RenderView.this.bitmap != null && !RenderView.this.bitmap.isRecycled()) {
                this.initialized = initGL();
                super.run();
            }
        }

        public void scheduleRedraw() {
            if (this.scheduledRunnable != null) {
                cancelRunnable(this.scheduledRunnable);
                this.scheduledRunnable = null;
            }
            this.scheduledRunnable = new C13813();
            postRunnable(this.scheduledRunnable, 1);
        }

        public void setBufferSize(int i, int i2) {
            this.bufferWidth = i;
            this.bufferHeight = i2;
        }

        public void shutdown() {
            postRunnable(new C13824());
        }
    }

    public interface RenderViewDelegate {
        void onBeganDrawing();

        void onFinishedDrawing(boolean z);

        boolean shouldDraw();
    }

    public RenderView(Context context, Painting painting, Bitmap bitmap, int i) {
        super(context);
        this.bitmap = bitmap;
        this.orientation = i;
        this.painting = painting;
        this.painting.setRenderView(this);
        setSurfaceTextureListener(new C13741());
        this.input = new Input(this);
        this.painting.setDelegate(new C13752());
    }

    private float brushWeightForSize(float f) {
        float f2 = this.painting.getSize().width;
        return ((f2 * 0.043945312f) * f) + (0.00390625f * f2);
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();
        float width = this.painting != null ? ((float) getWidth()) / this.painting.getSize().width : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (width <= 0.0f) {
            width = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        Size size = getPainting().getSize();
        matrix.preTranslate(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
        matrix.preScale(width, -width);
        matrix.preTranslate((-size.width) / 2.0f, (-size.height) / 2.0f);
        this.input.setMatrix(matrix);
        this.painting.setRenderProjection(GLMatrix.MultiplyMat4f(GLMatrix.LoadOrtho(0.0f, (float) this.internal.bufferWidth, 0.0f, (float) this.internal.bufferHeight, Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), GLMatrix.LoadGraphicsMatrix(matrix)));
    }

    public Brush getCurrentBrush() {
        return this.brush;
    }

    public int getCurrentColor() {
        return this.color;
    }

    public float getCurrentWeight() {
        return this.weight;
    }

    public Painting getPainting() {
        return this.painting;
    }

    public Bitmap getResultBitmap() {
        return this.internal != null ? this.internal.getTexture() : null;
    }

    public void onBeganDrawing() {
        if (this.delegate != null) {
            this.delegate.onBeganDrawing();
        }
    }

    public void onFinishedDrawing(boolean z) {
        if (this.delegate != null) {
            this.delegate.onFinishedDrawing(z);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            return false;
        }
        if (this.internal == null || !this.internal.initialized || !this.internal.ready) {
            return true;
        }
        this.input.process(motionEvent);
        return true;
    }

    public void performInContext(Runnable runnable) {
        if (this.internal != null) {
            this.internal.postRunnable(new C13774(runnable));
        }
    }

    public void setBrush(Brush brush) {
        Painting painting = this.painting;
        this.brush = brush;
        painting.setBrush(brush);
    }

    public void setBrushSize(float f) {
        this.weight = brushWeightForSize(f);
    }

    public void setColor(int i) {
        this.color = i;
    }

    public void setDelegate(RenderViewDelegate renderViewDelegate) {
        this.delegate = renderViewDelegate;
    }

    public void setQueue(DispatchQueue dispatchQueue) {
        this.queue = dispatchQueue;
    }

    public void setUndoStore(UndoStore undoStore) {
        this.undoStore = undoStore;
    }

    public boolean shouldDraw() {
        return this.delegate == null || this.delegate.shouldDraw();
    }

    public void shutdown() {
        this.shuttingDown = true;
        if (this.internal != null) {
            performInContext(new C13763());
        }
        setVisibility(8);
    }
}
