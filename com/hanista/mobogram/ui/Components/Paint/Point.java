package com.hanista.mobogram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    public double f2679x;
    public double f2680y;
    public double f2681z;

    public Point(double d, double d2, double d3) {
        this.f2679x = d;
        this.f2680y = d2;
        this.f2681z = d3;
    }

    public Point(Point point) {
        this.f2679x = point.f2679x;
        this.f2680y = point.f2680y;
        this.f2681z = point.f2681z;
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f2679x * this.f2679x) + (this.f2680y * this.f2680y)) + (this.f2681z * this.f2681z));
    }

    Point add(Point point) {
        return new Point(this.f2679x + point.f2679x, this.f2680y + point.f2680y, this.f2681z + point.f2681z);
    }

    void alteringAddMultiplication(Point point, double d) {
        this.f2679x += point.f2679x * d;
        this.f2680y += point.f2680y * d;
        this.f2681z += point.f2681z * d;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point point = (Point) obj;
        if (!(this.f2679x == point.f2679x && this.f2680y == point.f2680y && this.f2681z == point.f2681z)) {
            z = false;
        }
        return z;
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f2679x - point.f2679x, 2.0d) + Math.pow(this.f2680y - point.f2680y, 2.0d)) + Math.pow(this.f2681z - point.f2681z, 2.0d));
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    Point multiplyAndAdd(double d, Point point) {
        return new Point((this.f2679x * d) + point.f2679x, (this.f2680y * d) + point.f2680y, (this.f2681z * d) + point.f2681z);
    }

    Point multiplyByScalar(double d) {
        return new Point(this.f2679x * d, this.f2680y * d, this.f2681z * d);
    }

    Point multiplySum(Point point, double d) {
        return new Point((this.f2679x + point.f2679x) * d, (this.f2680y + point.f2680y) * d, (this.f2681z + point.f2681z) * d);
    }

    Point substract(Point point) {
        return new Point(this.f2679x - point.f2679x, this.f2680y - point.f2680y, this.f2681z - point.f2681z);
    }

    PointF toPointF() {
        return new PointF((float) this.f2679x, (float) this.f2680y);
    }
}
