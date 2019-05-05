package com.hanista.mobogram.ui.Components.Paint;

import android.graphics.RectF;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.DispatchQueue;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Slice {
    private RectF bounds;
    private File file;

    public Slice(ByteBuffer byteBuffer, RectF rectF, DispatchQueue dispatchQueue) {
        this.bounds = rectF;
        try {
            this.file = File.createTempFile("paint", ".bin", ApplicationLoader.applicationContext.getCacheDir());
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        if (this.file != null) {
            storeData(byteBuffer);
        }
    }

    private void storeData(ByteBuffer byteBuffer) {
        try {
            byte[] array = byteBuffer.array();
            FileOutputStream fileOutputStream = new FileOutputStream(this.file);
            Deflater deflater = new Deflater(1, true);
            deflater.setInput(array, byteBuffer.arrayOffset(), byteBuffer.remaining());
            deflater.finish();
            array = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
            while (!deflater.finished()) {
                fileOutputStream.write(array, 0, deflater.deflate(array));
            }
            deflater.end();
            fileOutputStream.close();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void cleanResources() {
        if (this.file != null) {
            this.file.delete();
            this.file = null;
        }
    }

    public RectF getBounds() {
        return new RectF(this.bounds);
    }

    public ByteBuffer getData() {
        try {
            byte[] bArr = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
            byte[] bArr2 = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
            FileInputStream fileInputStream = new FileInputStream(this.file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Inflater inflater = new Inflater(true);
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read != -1) {
                    inflater.setInput(bArr, 0, read);
                }
                while (true) {
                    read = inflater.inflate(bArr2, 0, bArr2.length);
                    if (read == 0) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr2, 0, read);
                }
                if (inflater.finished()) {
                    inflater.end();
                    ByteBuffer wrap = ByteBuffer.wrap(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
                    byteArrayOutputStream.close();
                    fileInputStream.close();
                    return wrap;
                } else if (inflater.needsInput()) {
                }
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    public int getHeight() {
        return (int) this.bounds.height();
    }

    public int getWidth() {
        return (int) this.bounds.width();
    }

    public int getX() {
        return (int) this.bounds.left;
    }

    public int getY() {
        return (int) this.bounds.top;
    }
}
