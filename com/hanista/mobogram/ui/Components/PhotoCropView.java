package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class PhotoCropView extends FrameLayout {
    private RectF animationEndValues;
    private Runnable animationRunnable;
    private RectF animationStartValues;
    private float bitmapGlobalScale;
    private float bitmapGlobalX;
    private float bitmapGlobalY;
    private int bitmapHeight;
    private Bitmap bitmapToEdit;
    private int bitmapWidth;
    private int bitmapX;
    private int bitmapY;
    private Paint circlePaint;
    private PhotoCropViewDelegate delegate;
    private int draggingState;
    private boolean freeformCrop;
    private Paint halfPaint;
    private float oldX;
    private float oldY;
    private int orientation;
    private Paint rectPaint;
    private float rectSizeX;
    private float rectSizeY;
    private float rectX;
    private float rectY;
    private Paint shadowPaint;

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoCropView.1 */
    class C14021 implements Runnable {
        C14021() {
        }

        public void run() {
            if (PhotoCropView.this.animationRunnable == this) {
                PhotoCropView.this.animationRunnable = null;
                PhotoCropView.this.moveToFill(true);
            }
        }
    }

    public interface PhotoCropViewDelegate {
        Bitmap getBitmap();

        void needMoveImageTo(float f, float f2, float f3, boolean z);
    }

    public PhotoCropView(Context context) {
        super(context);
        this.freeformCrop = true;
        this.rectSizeX = 600.0f;
        this.rectSizeY = 600.0f;
        this.draggingState = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.bitmapWidth = 1;
        this.bitmapHeight = 1;
        this.rectX = Face.UNCOMPUTED_PROBABILITY;
        this.rectY = Face.UNCOMPUTED_PROBABILITY;
        this.bitmapGlobalScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.bitmapGlobalX = 0.0f;
        this.bitmapGlobalY = 0.0f;
        this.rectPaint = new Paint();
        this.rectPaint.setColor(-1291845633);
        this.rectPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.rectPaint.setStyle(Style.STROKE);
        this.circlePaint = new Paint();
        this.circlePaint.setColor(-1);
        this.halfPaint = new Paint();
        this.halfPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.shadowPaint = new Paint();
        this.shadowPaint.setColor(436207616);
        setWillNotDraw(false);
    }

    private Bitmap createBitmap(int i, int i2, int i3, int i4) {
        Bitmap bitmap = this.delegate.getBitmap();
        if (bitmap != null) {
            this.bitmapToEdit = bitmap;
        }
        bitmap = Bitmap.createBitmap(i3, i4, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(6);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) ((-this.bitmapToEdit.getWidth()) / 2), (float) ((-this.bitmapToEdit.getHeight()) / 2));
        matrix.postRotate((float) this.orientation);
        if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
            matrix.postTranslate((float) ((this.bitmapToEdit.getHeight() / 2) - i), (float) ((this.bitmapToEdit.getWidth() / 2) - i2));
        } else {
            matrix.postTranslate((float) ((this.bitmapToEdit.getWidth() / 2) - i), (float) ((this.bitmapToEdit.getHeight() / 2) - i2));
        }
        canvas.drawBitmap(this.bitmapToEdit, matrix, paint);
        try {
            canvas.setBitmap(null);
        } catch (Exception e) {
        }
        return bitmap;
    }

    public void cancelAnimationRunnable() {
        if (this.animationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animationRunnable = null;
            this.animationStartValues = null;
            this.animationEndValues = null;
        }
    }

    public Bitmap getBitmap() {
        int height;
        int width;
        int i;
        Bitmap bitmap = this.delegate.getBitmap();
        if (bitmap != null) {
            this.bitmapToEdit = bitmap;
        }
        float f = this.bitmapGlobalScale * ((float) this.bitmapWidth);
        float f2 = this.bitmapGlobalScale * ((float) this.bitmapHeight);
        float width2 = (this.rectX - (((((float) getWidth()) - f) / 2.0f) + this.bitmapGlobalX)) / f;
        float height2 = (this.rectY - (((((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) + (((float) getHeight()) - f2)) / 2.0f) + this.bitmapGlobalY)) / f2;
        float f3 = this.rectSizeX / f;
        float f4 = this.rectSizeY / f2;
        if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
            height = this.bitmapToEdit.getHeight();
            width = this.bitmapToEdit.getWidth();
            i = height;
        } else {
            height = this.bitmapToEdit.getWidth();
            width = this.bitmapToEdit.getHeight();
            i = height;
        }
        int i2 = (int) (((float) i) * width2);
        int i3 = (int) (((float) width) * height2);
        height = (int) (((float) i) * f3);
        int i4 = (int) (((float) width) * f4);
        if (i2 < 0) {
            i2 = 0;
        }
        if (i3 < 0) {
            i3 = 0;
        }
        int i5 = i2 + height > i ? i - i2 : height;
        if (i3 + i4 > width) {
            i4 = width - i3;
        }
        try {
            return createBitmap(i2, i3, i5, i4);
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
            return null;
        }
    }

    public float getBitmapX() {
        return (float) (this.bitmapX - AndroidUtilities.dp(14.0f));
    }

    public float getBitmapY() {
        return ((float) (this.bitmapY - AndroidUtilities.dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public float getLimitHeight() {
        float f = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        return (((((float) (getHeight() - AndroidUtilities.dp(14.0f))) - f) - this.rectY) - ((float) ((int) Math.max(0.0d, Math.ceil((double) (((((float) (getHeight() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapHeight) * this.bitmapGlobalScale)) - f) / 2.0f)))))) - this.rectSizeY;
    }

    public float getLimitWidth() {
        return ((((float) (getWidth() - AndroidUtilities.dp(14.0f))) - this.rectX) - ((float) ((int) Math.max(0.0d, Math.ceil((double) ((((float) (getWidth() - AndroidUtilities.dp(28.0f))) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)))))) - this.rectSizeX;
    }

    public float getLimitX() {
        return this.rectX - Math.max(0.0f, (float) Math.ceil((double) ((((float) getWidth()) - (((float) this.bitmapWidth) * this.bitmapGlobalScale)) / 2.0f)));
    }

    public float getLimitY() {
        return this.rectY - Math.max(0.0f, (float) Math.ceil((double) ((((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) + (((float) getHeight()) - (((float) this.bitmapHeight) * this.bitmapGlobalScale))) / 2.0f)));
    }

    public float getRectSizeX() {
        return this.rectSizeX;
    }

    public float getRectSizeY() {
        return this.rectSizeY;
    }

    public float getRectX() {
        return this.rectX - ((float) AndroidUtilities.dp(14.0f));
    }

    public float getRectY() {
        return (this.rectY - ((float) AndroidUtilities.dp(14.0f))) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public void moveToFill(boolean z) {
        float f = ((float) this.bitmapWidth) / this.rectSizeX;
        float f2 = ((float) this.bitmapHeight) / this.rectSizeY;
        if (f <= f2) {
            f2 = f;
        }
        if (f2 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.bitmapGlobalScale * f2 > 3.0f) {
            f2 = 3.0f / this.bitmapGlobalScale;
        } else if (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.bitmapGlobalScale * f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / this.bitmapGlobalScale;
        }
        float f3 = this.rectSizeX * f2;
        float f4 = this.rectSizeY * f2;
        f = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float width = (((float) getWidth()) - f3) / 2.0f;
        float height = ((((float) getHeight()) - f4) + f) / 2.0f;
        this.animationStartValues = new RectF(this.rectX, this.rectY, this.rectSizeX, this.rectSizeY);
        this.animationEndValues = new RectF(width, height, f3, f4);
        this.delegate.needMoveImageTo(((((float) (getWidth() / 2)) * (f2 - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + width) + ((this.bitmapGlobalX - this.rectX) * f2), ((((f + ((float) getHeight())) / 2.0f) * (f2 - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + height) + ((this.bitmapGlobalY - this.rectY) * f2), f2 * this.bitmapGlobalScale, z);
    }

    protected void onDraw(Canvas canvas) {
        int i;
        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), this.rectY, this.halfPaint);
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, this.rectY, this.rectX, this.rectSizeY + this.rectY, this.halfPaint);
        canvas.drawRect(this.rectX + this.rectSizeX, this.rectY, (float) getWidth(), this.rectY + this.rectSizeY, this.halfPaint);
        canvas2 = canvas;
        canvas2.drawRect(0.0f, this.rectSizeY + this.rectY, (float) getWidth(), (float) getHeight(), this.halfPaint);
        int dp = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (dp * 2)), this.rectY - ((float) (dp * 2)), ((float) AndroidUtilities.dp(20.0f)) + (this.rectX - ((float) (dp * 2))), this.rectY, this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (dp * 2)), this.rectY - ((float) (dp * 2)), this.rectX, ((float) AndroidUtilities.dp(20.0f)) + (this.rectY - ((float) (dp * 2))), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(((this.rectX + this.rectSizeX) + ((float) (dp * 2))) - ((float) AndroidUtilities.dp(20.0f)), this.rectY - ((float) (dp * 2)), ((float) (dp * 2)) + (this.rectX + this.rectSizeX), this.rectY, this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectSizeX + this.rectX, this.rectY - ((float) (dp * 2)), ((float) (dp * 2)) + (this.rectX + this.rectSizeX), ((float) AndroidUtilities.dp(20.0f)) + (this.rectY - ((float) (dp * 2))), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (dp * 2)), ((this.rectY + this.rectSizeY) + ((float) (dp * 2))) - ((float) AndroidUtilities.dp(20.0f)), this.rectX, ((float) (dp * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectX - ((float) (dp * 2)), this.rectSizeY + this.rectY, ((float) AndroidUtilities.dp(20.0f)) + (this.rectX - ((float) (dp * 2))), ((float) (dp * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(((this.rectX + this.rectSizeX) + ((float) (dp * 2))) - ((float) AndroidUtilities.dp(20.0f)), this.rectSizeY + this.rectY, ((float) (dp * 2)) + (this.rectX + this.rectSizeX), ((float) (dp * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        canvas2 = canvas;
        canvas2.drawRect(this.rectSizeX + this.rectX, ((this.rectY + this.rectSizeY) + ((float) (dp * 2))) - ((float) AndroidUtilities.dp(20.0f)), ((float) (dp * 2)) + (this.rectX + this.rectSizeX), ((float) (dp * 2)) + (this.rectY + this.rectSizeY), this.circlePaint);
        for (i = 1; i < 3; i++) {
            canvas2 = canvas;
            canvas2.drawRect((this.rectX + ((this.rectSizeX / 3.0f) * ((float) i))) - ((float) dp), this.rectY, ((this.rectSizeX / 3.0f) * ((float) i)) + (this.rectX + ((float) (dp * 2))), this.rectSizeY + this.rectY, this.shadowPaint);
            canvas2 = canvas;
            canvas2.drawRect(this.rectX, (this.rectY + ((this.rectSizeY / 3.0f) * ((float) i))) - ((float) dp), this.rectSizeX + this.rectX, ((float) (dp * 2)) + (this.rectY + ((this.rectSizeY / 3.0f) * ((float) i))), this.shadowPaint);
        }
        for (i = 1; i < 3; i++) {
            canvas2 = canvas;
            canvas2.drawRect(((this.rectSizeX / 3.0f) * ((float) i)) + this.rectX, this.rectY, ((this.rectSizeX / 3.0f) * ((float) i)) + (this.rectX + ((float) dp)), this.rectSizeY + this.rectY, this.circlePaint);
            canvas2 = canvas;
            canvas2.drawRect(this.rectX, ((this.rectSizeY / 3.0f) * ((float) i)) + this.rectY, this.rectSizeX + this.rectX, ((float) dp) + (this.rectY + ((this.rectSizeY / 3.0f) * ((float) i))), this.circlePaint);
        }
        canvas2 = canvas;
        canvas2.drawRect(this.rectX, this.rectY, this.rectSizeX + this.rectX, this.rectSizeY + this.rectY, this.rectPaint);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Bitmap bitmap = this.delegate.getBitmap();
        if (bitmap != null) {
            this.bitmapToEdit = bitmap;
        }
        if (this.bitmapToEdit != null) {
            float height;
            float width;
            int width2 = getWidth() - AndroidUtilities.dp(28.0f);
            int height2 = (getHeight() - AndroidUtilities.dp(28.0f)) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
                height = (float) this.bitmapToEdit.getHeight();
                width = (float) this.bitmapToEdit.getWidth();
            } else {
                height = (float) this.bitmapToEdit.getWidth();
                width = (float) this.bitmapToEdit.getHeight();
            }
            float f = ((float) width2) / height;
            float f2 = ((float) height2) / width;
            if (f > f2) {
                width = (float) height2;
                height = (float) ((int) Math.ceil((double) (height * f2)));
            } else {
                height = (float) width2;
                width = (float) ((int) Math.ceil((double) (width * f)));
            }
            f = (this.rectX - ((float) this.bitmapX)) / ((float) this.bitmapWidth);
            f2 = (this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight);
            float f3 = this.rectSizeX / ((float) this.bitmapWidth);
            float f4 = this.rectSizeY / ((float) this.bitmapHeight);
            this.bitmapWidth = (int) height;
            this.bitmapHeight = (int) width;
            this.bitmapX = (int) Math.ceil((double) (((width2 - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f)));
            this.bitmapY = (int) Math.ceil((double) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + (AndroidUtilities.dp(14.0f) + ((height2 - this.bitmapHeight) / 2))));
            if (this.rectX != Face.UNCOMPUTED_PROBABILITY || this.rectY != Face.UNCOMPUTED_PROBABILITY) {
                this.rectX = (((float) this.bitmapWidth) * f) + ((float) this.bitmapX);
                this.rectY = (((float) this.bitmapHeight) * f2) + ((float) this.bitmapY);
                this.rectSizeX = ((float) this.bitmapWidth) * f3;
                this.rectSizeY = ((float) this.bitmapHeight) * f4;
            } else if (this.freeformCrop) {
                this.rectY = (float) this.bitmapY;
                this.rectX = (float) this.bitmapX;
                this.rectSizeX = (float) this.bitmapWidth;
                this.rectSizeY = (float) this.bitmapHeight;
            } else if (this.bitmapWidth > this.bitmapHeight) {
                this.rectY = (float) this.bitmapY;
                this.rectX = (float) (((width2 - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f));
                this.rectSizeX = (float) this.bitmapHeight;
                this.rectSizeY = (float) this.bitmapHeight;
            } else {
                this.rectX = (float) this.bitmapX;
                this.rectY = (float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + (AndroidUtilities.dp(14.0f) + ((height2 - this.bitmapWidth) / 2)));
                this.rectSizeX = (float) this.bitmapWidth;
                this.rectSizeY = (float) this.bitmapWidth;
            }
        }
    }

    public boolean onTouch(MotionEvent motionEvent) {
        if (motionEvent == null) {
            this.draggingState = 0;
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int dp = AndroidUtilities.dp(20.0f);
        if (motionEvent.getAction() == 0) {
            if (this.rectX - ((float) dp) < x && this.rectX + ((float) dp) > x && this.rectY - ((float) dp) < y && this.rectY + ((float) dp) > y) {
                this.draggingState = 1;
            } else if ((this.rectX - ((float) dp)) + this.rectSizeX < x && (this.rectX + ((float) dp)) + this.rectSizeX > x && this.rectY - ((float) dp) < y && this.rectY + ((float) dp) > y) {
                this.draggingState = 2;
            } else if (this.rectX - ((float) dp) < x && this.rectX + ((float) dp) > x && (this.rectY - ((float) dp)) + this.rectSizeY < y && (this.rectY + ((float) dp)) + this.rectSizeY > y) {
                this.draggingState = 3;
            } else if ((this.rectX - ((float) dp)) + this.rectSizeX < x && (this.rectX + ((float) dp)) + this.rectSizeX > x && (this.rectY - ((float) dp)) + this.rectSizeY < y && (this.rectY + ((float) dp)) + this.rectSizeY > y) {
                this.draggingState = 4;
            } else if (!this.freeformCrop) {
                this.draggingState = 0;
            } else if (this.rectX + ((float) dp) < x && (this.rectX - ((float) dp)) + this.rectSizeX > x && this.rectY - ((float) dp) < y && this.rectY + ((float) dp) > y) {
                this.draggingState = 5;
            } else if (this.rectY + ((float) dp) < y && (this.rectY - ((float) dp)) + this.rectSizeY > y && (this.rectX - ((float) dp)) + this.rectSizeX < x && (this.rectX + ((float) dp)) + this.rectSizeX > x) {
                this.draggingState = 6;
            } else if (this.rectY + ((float) dp) < y && (this.rectY - ((float) dp)) + this.rectSizeY > y && this.rectX - ((float) dp) < x && this.rectX + ((float) dp) > x) {
                this.draggingState = 7;
            } else if (this.rectX + ((float) dp) < x && (this.rectX - ((float) dp)) + this.rectSizeX > x && (this.rectY - ((float) dp)) + this.rectSizeY < y) {
                if ((((float) dp) + this.rectY) + this.rectSizeY > y) {
                    this.draggingState = 8;
                }
            }
            if (this.draggingState != 0) {
                cancelAnimationRunnable();
                requestDisallowInterceptTouchEvent(true);
            }
            this.oldX = x;
            this.oldY = y;
        } else if (motionEvent.getAction() == 1) {
            if (this.draggingState != 0) {
                this.draggingState = 0;
                startAnimationRunnable();
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.draggingState != 0) {
            float f = x - this.oldX;
            float f2 = y - this.oldY;
            float f3 = this.bitmapGlobalScale * ((float) this.bitmapWidth);
            float f4 = this.bitmapGlobalScale * ((float) this.bitmapHeight);
            float width = ((((float) getWidth()) - f3) / 2.0f) + this.bitmapGlobalX;
            float height = this.bitmapGlobalY + ((((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) + (((float) getHeight()) - f4)) / 2.0f);
            float f5 = width + f3;
            f3 = height + f4;
            f4 = AndroidUtilities.getPixelsInCM(0.9f, true);
            if (this.draggingState == 1 || this.draggingState == 5) {
                if (this.draggingState != 5) {
                    f5 = this.rectSizeX - f < f4 ? this.rectSizeX - f4 : f;
                    if (this.rectX + f5 < ((float) this.bitmapX)) {
                        f5 = ((float) this.bitmapX) - this.rectX;
                    }
                    if (this.rectX + f5 < width) {
                        this.bitmapGlobalX -= (width - this.rectX) - f5;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                } else {
                    f5 = f;
                }
                if (this.freeformCrop) {
                    f = this.rectSizeY - f2 < f4 ? this.rectSizeY - f4 : f2;
                    if (this.rectY + f < ((float) this.bitmapY)) {
                        f = ((float) this.bitmapY) - this.rectY;
                    }
                    if (this.rectY + f < height) {
                        this.bitmapGlobalY -= (height - this.rectY) - f;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    if (this.draggingState != 5) {
                        this.rectX += f5;
                        this.rectSizeX -= f5;
                    }
                    this.rectY += f;
                    this.rectSizeY -= f;
                } else {
                    if (this.rectY + f5 < ((float) this.bitmapY)) {
                        f5 = ((float) this.bitmapY) - this.rectY;
                    }
                    if (this.rectY + f5 < height) {
                        this.bitmapGlobalY -= (height - this.rectY) - f5;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectX += f5;
                    this.rectY += f5;
                    this.rectSizeX -= f5;
                    this.rectSizeY -= f5;
                }
            } else if (this.draggingState == 2 || this.draggingState == 6) {
                if (this.rectSizeX + f < f4) {
                    f = -(this.rectSizeX - f4);
                }
                if ((this.rectX + this.rectSizeX) + f > ((float) (this.bitmapX + this.bitmapWidth))) {
                    f = (((float) (this.bitmapX + this.bitmapWidth)) - this.rectX) - this.rectSizeX;
                }
                if ((this.rectX + this.rectSizeX) + f > f5) {
                    this.bitmapGlobalX -= ((f5 - this.rectX) - this.rectSizeX) - f;
                    this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                }
                if (this.freeformCrop) {
                    if (this.draggingState != 6) {
                        if (this.rectSizeY - f2 < f4) {
                            f2 = this.rectSizeY - f4;
                        }
                        if (this.rectY + f2 < ((float) this.bitmapY)) {
                            f2 = ((float) this.bitmapY) - this.rectY;
                        }
                        if (this.rectY + f2 < height) {
                            this.bitmapGlobalY -= (height - this.rectY) - f2;
                            this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                        }
                        this.rectY += f2;
                        this.rectSizeY -= f2;
                    }
                    this.rectSizeX += f;
                } else {
                    if (this.rectY - f < ((float) this.bitmapY)) {
                        f = this.rectY - ((float) this.bitmapY);
                    }
                    if (this.rectY - f < height) {
                        this.bitmapGlobalY -= (height - this.rectY) + f;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectY -= f;
                    this.rectSizeX += f;
                    this.rectSizeY += f;
                }
            } else if (this.draggingState == 3 || this.draggingState == 7) {
                if (this.rectSizeX - f < f4) {
                    f = this.rectSizeX - f4;
                }
                if (this.rectX + f < ((float) this.bitmapX)) {
                    f = ((float) this.bitmapX) - this.rectX;
                }
                if (this.rectX + f < width) {
                    this.bitmapGlobalX -= (width - this.rectX) - f;
                    this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                }
                if (this.freeformCrop) {
                    if (this.draggingState != 7) {
                        if ((this.rectY + this.rectSizeY) + f2 > ((float) (this.bitmapY + this.bitmapHeight))) {
                            f2 = (((float) (this.bitmapY + this.bitmapHeight)) - this.rectY) - this.rectSizeY;
                        }
                        if ((this.rectY + this.rectSizeY) + f2 > f3) {
                            this.bitmapGlobalY -= ((f3 - this.rectY) - this.rectSizeY) - f2;
                            this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                        }
                        this.rectSizeY += f2;
                        if (this.rectSizeY < f4) {
                            this.rectSizeY = f4;
                        }
                    }
                    this.rectX += f;
                    this.rectSizeX -= f;
                } else {
                    if ((this.rectY + this.rectSizeX) - f > ((float) (this.bitmapY + this.bitmapHeight))) {
                        f = ((this.rectY + this.rectSizeX) - ((float) this.bitmapY)) - ((float) this.bitmapHeight);
                    }
                    if ((this.rectY + this.rectSizeX) - f > f3) {
                        this.bitmapGlobalY -= ((f3 - this.rectY) - this.rectSizeX) + f;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectX += f;
                    this.rectSizeX -= f;
                    this.rectSizeY -= f;
                }
            } else if (this.draggingState == 4 || this.draggingState == 8) {
                if (this.draggingState != 8) {
                    if ((this.rectX + this.rectSizeX) + f > ((float) (this.bitmapX + this.bitmapWidth))) {
                        f = (((float) (this.bitmapX + this.bitmapWidth)) - this.rectX) - this.rectSizeX;
                    }
                    if ((this.rectX + this.rectSizeX) + f > f5) {
                        this.bitmapGlobalX -= ((f5 - this.rectX) - this.rectSizeX) - f;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                }
                if (this.freeformCrop) {
                    if ((this.rectY + this.rectSizeY) + f2 > ((float) (this.bitmapY + this.bitmapHeight))) {
                        f2 = (((float) (this.bitmapY + this.bitmapHeight)) - this.rectY) - this.rectSizeY;
                    }
                    if ((this.rectY + this.rectSizeY) + f2 > f3) {
                        this.bitmapGlobalY -= ((f3 - this.rectY) - this.rectSizeY) - f2;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    if (this.draggingState != 8) {
                        this.rectSizeX += f;
                    }
                    this.rectSizeY += f2;
                } else {
                    if ((this.rectY + this.rectSizeX) + f > ((float) (this.bitmapY + this.bitmapHeight))) {
                        f = (((float) (this.bitmapY + this.bitmapHeight)) - this.rectY) - this.rectSizeX;
                    }
                    if ((this.rectY + this.rectSizeX) + f > f3) {
                        this.bitmapGlobalY -= ((f3 - this.rectY) - this.rectSizeX) - f;
                        this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectSizeX += f;
                    this.rectSizeY += f;
                }
                if (this.rectSizeX < f4) {
                    this.rectSizeX = f4;
                }
                if (this.rectSizeY < f4) {
                    this.rectSizeY = f4;
                }
            }
            this.oldX = x;
            this.oldY = y;
            invalidate();
        }
        return this.draggingState != 0;
    }

    public void setAnimationProgress(float f) {
        if (this.animationStartValues != null) {
            if (f == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.rectX = this.animationEndValues.left;
                this.rectY = this.animationEndValues.top;
                this.rectSizeX = this.animationEndValues.right;
                this.rectSizeY = this.animationEndValues.bottom;
                this.animationStartValues = null;
                this.animationEndValues = null;
            } else {
                this.rectX = this.animationStartValues.left + ((this.animationEndValues.left - this.animationStartValues.left) * f);
                this.rectY = this.animationStartValues.top + ((this.animationEndValues.top - this.animationStartValues.top) * f);
                this.rectSizeX = this.animationStartValues.right + ((this.animationEndValues.right - this.animationStartValues.right) * f);
                this.rectSizeY = this.animationStartValues.bottom + ((this.animationEndValues.bottom - this.animationStartValues.bottom) * f);
            }
            invalidate();
        }
    }

    public void setBitmap(Bitmap bitmap, int i, boolean z) {
        this.bitmapToEdit = bitmap;
        this.rectSizeX = 600.0f;
        this.rectSizeY = 600.0f;
        this.draggingState = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.bitmapWidth = 1;
        this.bitmapHeight = 1;
        this.rectX = Face.UNCOMPUTED_PROBABILITY;
        this.rectY = Face.UNCOMPUTED_PROBABILITY;
        this.freeformCrop = z;
        this.orientation = i;
        requestLayout();
    }

    public void setBitmapParams(float f, float f2, float f3) {
        this.bitmapGlobalScale = f;
        this.bitmapGlobalX = f2;
        this.bitmapGlobalY = f3;
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        this.delegate = photoCropViewDelegate;
    }

    public void setOrientation(int i) {
        this.orientation = i;
        this.rectX = Face.UNCOMPUTED_PROBABILITY;
        this.rectY = Face.UNCOMPUTED_PROBABILITY;
        this.rectSizeX = 600.0f;
        this.rectSizeY = 600.0f;
        this.delegate.needMoveImageTo(0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, false);
        requestLayout();
    }

    public void startAnimationRunnable() {
        if (this.animationRunnable == null) {
            this.animationRunnable = new C14021();
            AndroidUtilities.runOnUIThread(this.animationRunnable, 1500);
        }
    }
}
