package com.hanista.mobogram.ui.Components.Paint;

import android.graphics.Matrix;
import android.view.MotionEvent;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.Vector;

public class Input {
    private boolean beganDrawing;
    private boolean clearBuffer;
    private boolean hasMoved;
    private Matrix invertMatrix;
    private boolean isFirst;
    private Point lastLocation;
    private double lastRemainder;
    private Point[] points;
    private int pointsCount;
    private RenderView renderView;
    private float[] tempPoint;

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.Input.1 */
    class C13651 implements Runnable {
        final /* synthetic */ Path val$path;

        /* renamed from: com.hanista.mobogram.ui.Components.Paint.Input.1.1 */
        class C13641 implements Runnable {
            C13641() {
            }

            public void run() {
                Input.this.lastRemainder = C13651.this.val$path.remainder;
                Input.this.clearBuffer = false;
            }
        }

        C13651(Path path) {
            this.val$path = path;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C13641());
        }
    }

    public Input(RenderView renderView) {
        this.points = new Point[3];
        this.tempPoint = new float[2];
        this.renderView = renderView;
    }

    private void paintPath(Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = 0.0d;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new C13651(path));
    }

    private void reset() {
        this.pointsCount = 0;
    }

    private Point smoothPoint(Point point, Point point2, Point point3, float f) {
        double pow = Math.pow((double) (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f), 2.0d);
        double d = (double) ((2.0f * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f)) * f);
        double d2 = (double) (f * f);
        return new Point(((point.f2679x * pow) + (point3.f2679x * d)) + (point2.f2679x * d2), ((pow * point.f2680y) + (d * point3.f2680y)) + (point2.f2680y * d2), 1.0d);
    }

    private void smoothenAndPaintPoints(boolean z) {
        if (this.pointsCount > 2) {
            Vector vector = new Vector();
            Point point = this.points[0];
            Point point2 = this.points[1];
            Point point3 = this.points[2];
            if (point3 != null && point2 != null && point != null) {
                Point multiplySum = point2.multiplySum(point, 0.5d);
                Point multiplySum2 = point3.multiplySum(point2, 0.5d);
                int min = (int) Math.min(48.0d, Math.max(Math.floor((double) (multiplySum.getDistanceTo(multiplySum2) / ((float) 1))), 24.0d));
                float f = 0.0f;
                float f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) min);
                for (int i = 0; i < min; i++) {
                    Point smoothPoint = smoothPoint(multiplySum, multiplySum2, point2, f);
                    if (this.isFirst) {
                        smoothPoint.edge = true;
                        this.isFirst = false;
                    }
                    vector.add(smoothPoint);
                    f += f2;
                }
                if (z) {
                    multiplySum2.edge = true;
                }
                vector.add(multiplySum2);
                Point[] pointArr = new Point[vector.size()];
                vector.toArray(pointArr);
                paintPath(new Path(pointArr));
                System.arraycopy(this.points, 1, this.points, 0, 2);
                if (z) {
                    this.pointsCount = 0;
                    return;
                } else {
                    this.pointsCount = 2;
                    return;
                }
            }
            return;
        }
        pointArr = new Point[this.pointsCount];
        System.arraycopy(this.points, 0, pointArr, 0, this.pointsCount);
        paintPath(new Path(pointArr));
    }

    public void process(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        float height = ((float) this.renderView.getHeight()) - motionEvent.getY();
        this.tempPoint[0] = motionEvent.getX();
        this.tempPoint[1] = height;
        this.invertMatrix.mapPoints(this.tempPoint);
        Point point = new Point((double) this.tempPoint[0], (double) this.tempPoint[1], 1.0d);
        switch (actionMasked) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
                if (!this.beganDrawing) {
                    this.beganDrawing = true;
                    this.hasMoved = false;
                    this.isFirst = true;
                    this.lastLocation = point;
                    this.points[0] = point;
                    this.pointsCount = 1;
                    this.clearBuffer = true;
                } else if (point.getDistanceTo(this.lastLocation) >= ((float) AndroidUtilities.dp(5.0f))) {
                    if (!this.hasMoved) {
                        this.renderView.onBeganDrawing();
                        this.hasMoved = true;
                    }
                    this.points[this.pointsCount] = point;
                    this.pointsCount++;
                    if (this.pointsCount == 3) {
                        smoothenAndPaintPoints(false);
                    }
                    this.lastLocation = point;
                }
            case VideoPlayer.TYPE_AUDIO /*1*/:
                if (!this.hasMoved) {
                    if (this.renderView.shouldDraw()) {
                        point.edge = true;
                        paintPath(new Path(point));
                    }
                    reset();
                } else if (this.pointsCount > 0) {
                    smoothenAndPaintPoints(true);
                }
                this.pointsCount = 0;
                this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
                this.beganDrawing = false;
                this.renderView.onFinishedDrawing(this.hasMoved);
            default:
        }
    }

    public void setMatrix(Matrix matrix) {
        this.invertMatrix = new Matrix();
        matrix.invert(this.invertMatrix);
    }
}
