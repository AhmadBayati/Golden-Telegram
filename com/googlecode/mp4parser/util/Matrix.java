package com.googlecode.mp4parser.util;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class Matrix {
    public static final Matrix ROTATE_0;
    public static final Matrix ROTATE_180;
    public static final Matrix ROTATE_270;
    public static final Matrix ROTATE_90;
    double f20a;
    double f21b;
    double f22c;
    double f23d;
    double tx;
    double ty;
    double f24u;
    double f25v;
    double f26w;

    static {
        ROTATE_0 = new Matrix(1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
        ROTATE_90 = new Matrix(0.0d, 1.0d, -1.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
        ROTATE_180 = new Matrix(-1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
        ROTATE_270 = new Matrix(0.0d, -1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d);
    }

    public Matrix(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
        this.f24u = d5;
        this.f25v = d6;
        this.f26w = d7;
        this.f20a = d;
        this.f21b = d2;
        this.f22c = d3;
        this.f23d = d4;
        this.tx = d8;
        this.ty = d9;
    }

    public static Matrix fromByteBuffer(ByteBuffer byteBuffer) {
        return fromFileOrder(IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer));
    }

    public static Matrix fromFileOrder(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
        return new Matrix(d, d2, d4, d5, d3, d6, d9, d7, d8);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Matrix matrix = (Matrix) obj;
        return Double.compare(matrix.f20a, this.f20a) != 0 ? false : Double.compare(matrix.f21b, this.f21b) != 0 ? false : Double.compare(matrix.f22c, this.f22c) != 0 ? false : Double.compare(matrix.f23d, this.f23d) != 0 ? false : Double.compare(matrix.tx, this.tx) != 0 ? false : Double.compare(matrix.ty, this.ty) != 0 ? false : Double.compare(matrix.f24u, this.f24u) != 0 ? false : Double.compare(matrix.f25v, this.f25v) != 0 ? false : Double.compare(matrix.f26w, this.f26w) == 0;
    }

    public void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f20a);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f21b);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.f24u);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f22c);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.f23d);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.f25v);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.tx);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.ty);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.f26w);
    }

    public int hashCode() {
        long doubleToLongBits = Double.doubleToLongBits(this.f24u);
        int i = (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
        long doubleToLongBits2 = Double.doubleToLongBits(this.f25v);
        i = (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
        doubleToLongBits2 = Double.doubleToLongBits(this.f26w);
        i = (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
        doubleToLongBits2 = Double.doubleToLongBits(this.f20a);
        i = (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
        doubleToLongBits2 = Double.doubleToLongBits(this.f21b);
        i = (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
        doubleToLongBits2 = Double.doubleToLongBits(this.f22c);
        i = (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
        doubleToLongBits2 = Double.doubleToLongBits(this.f23d);
        i = (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
        doubleToLongBits2 = Double.doubleToLongBits(this.tx);
        i = (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
        doubleToLongBits2 = Double.doubleToLongBits(this.ty);
        return (i * 31) + ((int) (doubleToLongBits2 ^ (doubleToLongBits2 >>> 32)));
    }

    public String toString() {
        return equals(ROTATE_0) ? "Rotate 0\u00b0" : equals(ROTATE_90) ? "Rotate 90\u00b0" : equals(ROTATE_180) ? "Rotate 180\u00b0" : equals(ROTATE_270) ? "Rotate 270\u00b0" : "Matrix{u=" + this.f24u + ", v=" + this.f25v + ", w=" + this.f26w + ", a=" + this.f20a + ", b=" + this.f21b + ", c=" + this.f22c + ", d=" + this.f23d + ", tx=" + this.tx + ", ty=" + this.ty + '}';
    }
}
