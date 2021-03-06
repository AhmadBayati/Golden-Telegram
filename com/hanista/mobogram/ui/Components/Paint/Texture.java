package com.hanista.mobogram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.google.android.gms.gcm.Task;
import com.hanista.mobogram.ui.Components.Size;

public class Texture {
    private Bitmap bitmap;
    private int texture;

    public Texture(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static int generateTexture(Size size) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        int i = iArr[0];
        GLES20.glBindTexture(3553, i);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexImage2D(3553, 0, 6408, (int) size.width, (int) size.height, 0, 6408, 5121, null);
        return i;
    }

    private boolean isPOT(int i) {
        return ((i + -1) & i) == 0;
    }

    public void cleanResources(boolean z) {
        if (this.texture != 0) {
            GLES20.glDeleteTextures(1, new int[]{this.texture}, 0);
            this.texture = 0;
            if (z) {
                this.bitmap.recycle();
            }
        }
    }

    public int texture() {
        int i = 9729;
        int i2 = 1;
        if (this.texture != 0) {
            return this.texture;
        }
        if (this.bitmap.isRecycled()) {
            return 0;
        }
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        this.texture = iArr[0];
        GLES20.glBindTexture(3553, this.texture);
        if (!(isPOT(this.bitmap.getWidth()) && isPOT(this.bitmap.getHeight()))) {
            i2 = 0;
        }
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
        if (i2 != 0) {
            i = 9987;
        }
        GLES20.glTexParameteri(3553, 10241, i);
        GLUtils.texImage2D(3553, 0, this.bitmap, 0);
        if (i2 != 0) {
            GLES20.glGenerateMipmap(3553);
        }
        Utils.HasGLError();
        return this.texture;
    }
}
