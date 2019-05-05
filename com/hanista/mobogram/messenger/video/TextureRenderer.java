package com.hanista.mobogram.messenger.video;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

@TargetApi(16)
public class TextureRenderer {
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private static final float[] mTriangleVerticesData;
    private float[] mMVPMatrix;
    private int mProgram;
    private float[] mSTMatrix;
    private int mTextureID;
    private FloatBuffer mTriangleVertices;
    private int maPositionHandle;
    private int maTextureHandle;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int rotationAngle;

    static {
        mTriangleVerticesData = new float[]{Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY, 0.0f, 0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Face.UNCOMPUTED_PROBABILITY, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT};
    }

    public TextureRenderer(int i) {
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.mTextureID = -12345;
        this.rotationAngle = TRIANGLE_VERTICES_DATA_POS_OFFSET;
        this.rotationAngle = i;
        this.mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mTriangleVertices.put(mTriangleVerticesData).position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        Matrix.setIdentityM(this.mSTMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
    }

    private int createProgram(String str, String str2) {
        int loadShader = loadShader(35633, str);
        if (loadShader == 0) {
            return TRIANGLE_VERTICES_DATA_POS_OFFSET;
        }
        int loadShader2 = loadShader(35632, str2);
        if (loadShader2 == 0) {
            return TRIANGLE_VERTICES_DATA_POS_OFFSET;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (glCreateProgram == 0) {
            return TRIANGLE_VERTICES_DATA_POS_OFFSET;
        }
        GLES20.glAttachShader(glCreateProgram, loadShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(glCreateProgram, loadShader2);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(glCreateProgram);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, TRIANGLE_VERTICES_DATA_POS_OFFSET);
        if (iArr[TRIANGLE_VERTICES_DATA_POS_OFFSET] == 1) {
            return glCreateProgram;
        }
        GLES20.glDeleteProgram(glCreateProgram);
        return TRIANGLE_VERTICES_DATA_POS_OFFSET;
    }

    private int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        checkGlError("glCreateShader type=" + i);
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, TRIANGLE_VERTICES_DATA_POS_OFFSET);
        if (iArr[TRIANGLE_VERTICES_DATA_POS_OFFSET] != 0) {
            return glCreateShader;
        }
        GLES20.glDeleteShader(glCreateShader);
        return TRIANGLE_VERTICES_DATA_POS_OFFSET;
    }

    public void changeFragmentShader(String str) {
        GLES20.glDeleteProgram(this.mProgram);
        this.mProgram = createProgram(VERTEX_SHADER, str);
        if (this.mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
    }

    public void checkGlError(String str) {
        int glGetError = GLES20.glGetError();
        if (glGetError != 0) {
            throw new RuntimeException(str + ": glError " + glGetError);
        }
    }

    public void drawFrame(SurfaceTexture surfaceTexture, boolean z) {
        checkGlError("onDrawFrame start");
        surfaceTexture.getTransformMatrix(this.mSTMatrix);
        if (z) {
            this.mSTMatrix[5] = -this.mSTMatrix[5];
            this.mSTMatrix[13] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.mSTMatrix[13];
        }
        GLES20.glUseProgram(this.mProgram);
        checkGlError("glUseProgram");
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, this.mTextureID);
        this.mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(this.maPositionHandle, TRIANGLE_VERTICES_DATA_UV_OFFSET, 5126, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, this.mTriangleVertices);
        checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(this.maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
        this.mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, this.mTriangleVertices);
        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(this.maTextureHandle);
        checkGlError("glEnableVertexAttribArray maTextureHandle");
        GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glDrawArrays(5, TRIANGLE_VERTICES_DATA_POS_OFFSET, FLOAT_SIZE_BYTES);
        checkGlError("glDrawArrays");
        GLES20.glFinish();
    }

    public int getTextureId() {
        return this.mTextureID;
    }

    public void surfaceCreated() {
        this.mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (this.mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
        this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (this.maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (this.maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (this.muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (this.muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, TRIANGLE_VERTICES_DATA_POS_OFFSET);
        this.mTextureID = iArr[TRIANGLE_VERTICES_DATA_POS_OFFSET];
        GLES20.glBindTexture(36197, this.mTextureID);
        checkGlError("glBindTexture mTextureID");
        GLES20.glTexParameterf(36197, 10241, 9728.0f);
        GLES20.glTexParameterf(36197, Task.EXTRAS_LIMIT_BYTES, 9729.0f);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        checkGlError("glTexParameter");
        Matrix.setIdentityM(this.mMVPMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
        if (this.rotationAngle != 0) {
            Matrix.rotateM(this.mMVPMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET, (float) this.rotationAngle, 0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
    }
}
