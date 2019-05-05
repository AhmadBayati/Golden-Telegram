package com.hanista.mobogram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentEncrypted;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileEncryptedLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocation;
import com.hanista.mobogram.ui.Components.AnimatedFileDrawable;

public class ImageReceiver implements NotificationCenterDelegate {
    private static Paint roundPaint;
    private static PorterDuffColorFilter selectedColorFilter;
    private boolean allowStartAnimation;
    private RectF bitmapRect;
    private BitmapShader bitmapShader;
    private BitmapShader bitmapShaderThumb;
    private boolean canceledLoading;
    private boolean centerRotation;
    private ColorFilter colorFilter;
    private byte crossfadeAlpha;
    private boolean crossfadeWithThumb;
    private float currentAlpha;
    private boolean currentCacheOnly;
    private String currentExt;
    private String currentFilter;
    private String currentHttpUrl;
    private Drawable currentImage;
    private TLObject currentImageLocation;
    private String currentKey;
    private int currentSize;
    private Drawable currentThumb;
    private String currentThumbFilter;
    private String currentThumbKey;
    private FileLocation currentThumbLocation;
    private ImageReceiverDelegate delegate;
    private Rect drawRegion;
    private boolean forcePreview;
    public int imageH;
    public int imageW;
    public int imageX;
    public int imageY;
    private boolean invalidateAll;
    private boolean isAspectFit;
    private boolean isPressed;
    private boolean isVisible;
    private long lastUpdateAlphaTime;
    private boolean needsQualityThumb;
    private int orientation;
    private float overrideAlpha;
    private MessageObject parentMessageObject;
    private View parentView;
    private int roundRadius;
    private RectF roundRect;
    private SetImageBackup setImageBackup;
    private Matrix shaderMatrix;
    private boolean shouldGenerateQualityThumb;
    private Drawable staticThumb;
    private Integer tag;
    private Integer thumbTag;

    public interface ImageReceiverDelegate {
        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2);
    }

    private class SetImageBackup {
        public boolean cacheOnly;
        public String ext;
        public TLObject fileLocation;
        public String filter;
        public String httpUrl;
        public int size;
        public Drawable thumb;
        public String thumbFilter;
        public FileLocation thumbLocation;

        private SetImageBackup() {
        }
    }

    static {
        selectedColorFilter = new PorterDuffColorFilter(-2236963, Mode.MULTIPLY);
    }

    public ImageReceiver() {
        this(null);
    }

    public ImageReceiver(View view) {
        this.allowStartAnimation = true;
        this.drawRegion = new Rect();
        this.isVisible = true;
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.overrideAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.crossfadeAlpha = (byte) 1;
        this.parentView = view;
        if (roundPaint == null) {
            roundPaint = new Paint(1);
        }
    }

    private void checkAlphaAnimation(boolean z) {
        long j = 18;
        if (this.currentAlpha != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            if (!z) {
                long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateAlphaTime;
                if (currentTimeMillis <= 18) {
                    j = currentTimeMillis;
                }
                this.currentAlpha = (((float) j) / 150.0f) + this.currentAlpha;
                if (this.currentAlpha > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.currentAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
            }
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            if (this.parentView == null) {
                return;
            }
            if (this.invalidateAll) {
                this.parentView.invalidate();
            } else {
                this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
            }
        }
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader) {
        Throwable th;
        if (drawable instanceof BitmapDrawable) {
            int intrinsicHeight;
            int intrinsicWidth;
            drawable = (BitmapDrawable) drawable;
            Paint paint = bitmapShader != null ? roundPaint : drawable.getPaint();
            Object obj = (paint == null || paint.getColorFilter() == null) ? null : 1;
            if (obj == null || this.isPressed) {
                if (obj == null && this.isPressed) {
                    if (bitmapShader != null) {
                        roundPaint.setColorFilter(selectedColorFilter);
                    } else {
                        drawable.setColorFilter(selectedColorFilter);
                    }
                }
            } else if (bitmapShader != null) {
                roundPaint.setColorFilter(null);
            } else {
                drawable.setColorFilter(null);
            }
            if (this.colorFilter != null) {
                if (bitmapShader != null) {
                    roundPaint.setColorFilter(this.colorFilter);
                } else {
                    drawable.setColorFilter(this.colorFilter);
                }
            }
            if (drawable instanceof AnimatedFileDrawable) {
                if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
                    intrinsicHeight = drawable.getIntrinsicHeight();
                    intrinsicWidth = drawable.getIntrinsicWidth();
                } else {
                    intrinsicHeight = drawable.getIntrinsicWidth();
                    intrinsicWidth = drawable.getIntrinsicHeight();
                }
            } else if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
                intrinsicHeight = drawable.getBitmap().getHeight();
                intrinsicWidth = drawable.getBitmap().getWidth();
            } else {
                intrinsicHeight = drawable.getBitmap().getWidth();
                intrinsicWidth = drawable.getBitmap().getHeight();
            }
            float f = ((float) intrinsicHeight) / ((float) this.imageW);
            float f2 = ((float) intrinsicWidth) / ((float) this.imageH);
            int floor;
            int floor2;
            if (bitmapShader != null) {
                roundPaint.setShader(bitmapShader);
                float min = Math.min(f, f2);
                this.roundRect.set((float) this.imageX, (float) this.imageY, (float) (this.imageX + this.imageW), (float) (this.imageY + this.imageH));
                this.shaderMatrix.reset();
                if (Math.abs(f - f2) <= 1.0E-5f) {
                    this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                } else if (((float) intrinsicHeight) / f2 > ((float) this.imageW)) {
                    this.drawRegion.set(this.imageX - ((((int) (((float) intrinsicHeight) / f2)) - this.imageW) / 2), this.imageY, this.imageX + ((((int) (((float) intrinsicHeight) / f2)) + this.imageW) / 2), this.imageY + this.imageH);
                } else {
                    this.drawRegion.set(this.imageX, this.imageY - ((((int) (((float) intrinsicWidth) / f)) - this.imageH) / 2), this.imageX + this.imageW, this.imageY + ((((int) (((float) intrinsicWidth) / f)) + this.imageH) / 2));
                }
                if (this.isVisible) {
                    if (Math.abs(f - f2) > 1.0E-5f) {
                        floor = (int) Math.floor((double) (((float) this.imageW) * min));
                        floor2 = (int) Math.floor((double) (((float) this.imageH) * min));
                        this.bitmapRect.set((float) ((intrinsicHeight - floor) / 2), (float) ((intrinsicWidth - floor2) / 2), (float) ((intrinsicHeight + floor) / 2), (float) ((intrinsicWidth + floor2) / 2));
                        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, ScaleToFit.START);
                    } else {
                        this.bitmapRect.set(0.0f, 0.0f, (float) intrinsicHeight, (float) intrinsicWidth);
                        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, ScaleToFit.FILL);
                    }
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    roundPaint.setAlpha(i);
                    canvas.drawRoundRect(this.roundRect, (float) this.roundRadius, (float) this.roundRadius, roundPaint);
                    return;
                }
                return;
            } else if (this.isAspectFit) {
                f = Math.max(f, f2);
                canvas.save();
                intrinsicHeight = (int) (((float) intrinsicHeight) / f);
                intrinsicWidth = (int) (((float) intrinsicWidth) / f);
                this.drawRegion.set(this.imageX + ((this.imageW - intrinsicHeight) / 2), this.imageY + ((this.imageH - intrinsicWidth) / 2), ((intrinsicHeight + this.imageW) / 2) + this.imageX, ((intrinsicWidth + this.imageH) / 2) + this.imageY);
                drawable.setBounds(this.drawRegion);
                try {
                    drawable.setAlpha(i);
                    drawable.draw(canvas);
                } catch (Throwable e) {
                    th = e;
                    if (drawable == this.currentImage && this.currentKey != null) {
                        ImageLoader.getInstance().removeImage(this.currentKey);
                        this.currentKey = null;
                    } else if (drawable == this.currentThumb && this.currentThumbKey != null) {
                        ImageLoader.getInstance().removeImage(this.currentThumbKey);
                        this.currentThumbKey = null;
                    }
                    setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
                    FileLog.m18e("tmessages", th);
                }
                canvas.restore();
                return;
            } else if (Math.abs(f - f2) > 1.0E-5f) {
                canvas.save();
                canvas.clipRect(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                if (this.orientation % 360 != 0) {
                    if (this.centerRotation) {
                        canvas.rotate((float) this.orientation, (float) (this.imageW / 2), (float) (this.imageH / 2));
                    } else {
                        canvas.rotate((float) this.orientation, 0.0f, 0.0f);
                    }
                }
                if (((float) intrinsicHeight) / f2 > ((float) this.imageW)) {
                    intrinsicWidth = (int) (((float) intrinsicHeight) / f2);
                    this.drawRegion.set(this.imageX - ((intrinsicWidth - this.imageW) / 2), this.imageY, ((intrinsicWidth + this.imageW) / 2) + this.imageX, this.imageY + this.imageH);
                } else {
                    intrinsicWidth = (int) (((float) intrinsicWidth) / f);
                    this.drawRegion.set(this.imageX, this.imageY - ((intrinsicWidth - this.imageH) / 2), this.imageX + this.imageW, ((intrinsicWidth + this.imageH) / 2) + this.imageY);
                }
                if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
                    intrinsicWidth = (this.drawRegion.right - this.drawRegion.left) / 2;
                    intrinsicHeight = (this.drawRegion.bottom - this.drawRegion.top) / 2;
                    floor = (this.drawRegion.right + this.drawRegion.left) / 2;
                    floor2 = (this.drawRegion.top + this.drawRegion.bottom) / 2;
                    drawable.setBounds(floor - intrinsicHeight, floor2 - intrinsicWidth, intrinsicHeight + floor, intrinsicWidth + floor2);
                } else {
                    drawable.setBounds(this.drawRegion);
                }
                if (this.isVisible) {
                    try {
                        drawable.setAlpha(i);
                        drawable.draw(canvas);
                    } catch (Throwable e2) {
                        th = e2;
                        if (drawable == this.currentImage && this.currentKey != null) {
                            ImageLoader.getInstance().removeImage(this.currentKey);
                            this.currentKey = null;
                        } else if (drawable == this.currentThumb && this.currentThumbKey != null) {
                            ImageLoader.getInstance().removeImage(this.currentThumbKey);
                            this.currentThumbKey = null;
                        }
                        setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
                        FileLog.m18e("tmessages", th);
                    }
                }
                canvas.restore();
                return;
            } else {
                canvas.save();
                if (this.orientation % 360 != 0) {
                    if (this.centerRotation) {
                        canvas.rotate((float) this.orientation, (float) (this.imageW / 2), (float) (this.imageH / 2));
                    } else {
                        canvas.rotate((float) this.orientation, 0.0f, 0.0f);
                    }
                }
                this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
                    intrinsicWidth = (this.drawRegion.right - this.drawRegion.left) / 2;
                    intrinsicHeight = (this.drawRegion.bottom - this.drawRegion.top) / 2;
                    floor = (this.drawRegion.right + this.drawRegion.left) / 2;
                    floor2 = (this.drawRegion.top + this.drawRegion.bottom) / 2;
                    drawable.setBounds(floor - intrinsicHeight, floor2 - intrinsicWidth, intrinsicHeight + floor, intrinsicWidth + floor2);
                } else {
                    drawable.setBounds(this.drawRegion);
                }
                if (this.isVisible) {
                    try {
                        drawable.setAlpha(i);
                        drawable.draw(canvas);
                    } catch (Throwable e22) {
                        th = e22;
                        if (drawable == this.currentImage && this.currentKey != null) {
                            ImageLoader.getInstance().removeImage(this.currentKey);
                            this.currentKey = null;
                        } else if (drawable == this.currentThumb && this.currentThumbKey != null) {
                            ImageLoader.getInstance().removeImage(this.currentThumbKey);
                            this.currentThumbKey = null;
                        }
                        setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
                        FileLog.m18e("tmessages", th);
                    }
                }
                canvas.restore();
                return;
            }
        }
        this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        drawable.setBounds(this.drawRegion);
        if (this.isVisible) {
            try {
                drawable.setAlpha(i);
                drawable.draw(canvas);
            } catch (Throwable e222) {
                FileLog.m18e("tmessages", e222);
            }
        }
    }

    private void recycleBitmap(String str, boolean z) {
        String str2;
        Drawable drawable;
        if (z) {
            str2 = this.currentThumbKey;
            drawable = this.currentThumb;
        } else {
            str2 = this.currentKey;
            drawable = this.currentImage;
        }
        if (str2 != null && ((str == null || !str.equals(str2)) && drawable != null)) {
            if (drawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable).recycle();
            } else if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInCache(str2) && decrementUseCount) {
                    bitmap.recycle();
                }
            }
        }
        if (z) {
            this.currentThumb = null;
            this.currentThumbKey = null;
            return;
        }
        this.currentImage = null;
        this.currentKey = null;
    }

    public void cancelLoadImage() {
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
        this.canceledLoading = true;
    }

    public void clearImage() {
        recycleBitmap(null, false);
        recycleBitmap(null, true);
        if (this.needsQualityThumb) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
            ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        String str;
        if (i == NotificationCenter.messageThumbGenerated) {
            str = (String) objArr[1];
            if (this.currentThumbKey != null && this.currentThumbKey.equals(str)) {
                if (this.currentThumb == null) {
                    ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
                }
                this.currentThumb = (BitmapDrawable) objArr[0];
                if (this.roundRadius == 0 || this.currentImage != null || !(this.currentThumb instanceof BitmapDrawable) || (this.currentThumb instanceof AnimatedFileDrawable)) {
                    this.bitmapShaderThumb = null;
                } else {
                    this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable) this.currentThumb).getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                }
                if (this.staticThumb instanceof BitmapDrawable) {
                    this.staticThumb = null;
                }
                if (this.parentView == null) {
                    return;
                }
                if (this.invalidateAll) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
        } else if (i == NotificationCenter.didReplacedPhotoInMemCache) {
            str = (String) objArr[0];
            if (this.currentKey != null && this.currentKey.equals(str)) {
                this.currentKey = (String) objArr[1];
                this.currentImageLocation = (FileLocation) objArr[2];
            }
            if (this.currentThumbKey != null && this.currentThumbKey.equals(str)) {
                this.currentThumbKey = (String) objArr[1];
                this.currentThumbLocation = (FileLocation) objArr[2];
            }
            if (this.setImageBackup != null) {
                if (this.currentKey != null && this.currentKey.equals(str)) {
                    this.currentKey = (String) objArr[1];
                    this.currentImageLocation = (FileLocation) objArr[2];
                }
                if (this.currentThumbKey != null && this.currentThumbKey.equals(str)) {
                    this.currentThumbKey = (String) objArr[1];
                    this.currentThumbLocation = (FileLocation) objArr[2];
                }
            }
        }
    }

    public boolean draw(Canvas canvas) {
        try {
            Object obj;
            Drawable drawable;
            boolean z = (this.currentImage instanceof AnimatedFileDrawable) && !((AnimatedFileDrawable) this.currentImage).hasBitmap();
            if (!this.forcePreview && this.currentImage != null && !z) {
                obj = null;
                drawable = this.currentImage;
            } else if (this.staticThumb instanceof BitmapDrawable) {
                r4 = 1;
                drawable = this.staticThumb;
            } else if (this.currentThumb != null) {
                r4 = 1;
                drawable = this.currentThumb;
            } else {
                obj = null;
                drawable = null;
            }
            if (drawable != null) {
                if (this.crossfadeAlpha == null) {
                    drawDrawable(canvas, drawable, (int) (this.overrideAlpha * 255.0f), obj != null ? this.bitmapShaderThumb : this.bitmapShader);
                } else if (this.crossfadeWithThumb && z) {
                    drawDrawable(canvas, drawable, (int) (this.overrideAlpha * 255.0f), this.bitmapShaderThumb);
                } else {
                    if (this.crossfadeWithThumb && this.currentAlpha != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        Drawable drawable2;
                        if (drawable != this.currentImage) {
                            if (drawable == this.currentThumb && this.staticThumb != null) {
                                drawable2 = this.staticThumb;
                            }
                            drawable2 = null;
                        } else if (this.staticThumb != null) {
                            drawable2 = this.staticThumb;
                        } else {
                            if (this.currentThumb != null) {
                                drawable2 = this.currentThumb;
                            }
                            drawable2 = null;
                        }
                        if (drawable2 != null) {
                            drawDrawable(canvas, drawable2, (int) (this.overrideAlpha * 255.0f), this.bitmapShaderThumb);
                        }
                    }
                    drawDrawable(canvas, drawable, (int) ((this.overrideAlpha * this.currentAlpha) * 255.0f), obj != null ? this.bitmapShaderThumb : this.bitmapShader);
                }
                boolean z2 = z && this.crossfadeWithThumb;
                checkAlphaAnimation(z2);
                return true;
            } else if (this.staticThumb != null) {
                drawDrawable(canvas, this.staticThumb, NalUnitUtil.EXTENDED_SAR, null);
                checkAlphaAnimation(z);
                return true;
            } else {
                checkAlphaAnimation(z);
                return false;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public int getAnimatedOrientation() {
        return this.currentImage instanceof AnimatedFileDrawable ? ((AnimatedFileDrawable) this.currentImage).getOrientation() : this.staticThumb instanceof AnimatedFileDrawable ? ((AnimatedFileDrawable) this.staticThumb).getOrientation() : 0;
    }

    public AnimatedFileDrawable getAnimation() {
        return this.currentImage instanceof AnimatedFileDrawable ? (AnimatedFileDrawable) this.currentImage : null;
    }

    public Bitmap getBitmap() {
        return this.currentImage instanceof AnimatedFileDrawable ? ((AnimatedFileDrawable) this.currentImage).getAnimatedBitmap() : this.staticThumb instanceof AnimatedFileDrawable ? ((AnimatedFileDrawable) this.staticThumb).getAnimatedBitmap() : this.currentImage instanceof BitmapDrawable ? ((BitmapDrawable) this.currentImage).getBitmap() : this.currentThumb instanceof BitmapDrawable ? ((BitmapDrawable) this.currentThumb).getBitmap() : this.staticThumb instanceof BitmapDrawable ? ((BitmapDrawable) this.staticThumb).getBitmap() : null;
    }

    public int getBitmapHeight() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            return (this.orientation % 360 == 0 || this.orientation % 360 == 180) ? this.currentImage.getIntrinsicHeight() : this.currentImage.getIntrinsicWidth();
        } else {
            if (this.staticThumb instanceof AnimatedFileDrawable) {
                return (this.orientation % 360 == 0 || this.orientation % 360 == 180) ? this.staticThumb.getIntrinsicHeight() : this.staticThumb.getIntrinsicWidth();
            } else {
                Bitmap bitmap = getBitmap();
                return (this.orientation % 360 == 0 || this.orientation % 360 == 180) ? bitmap.getHeight() : bitmap.getWidth();
            }
        }
    }

    public int getBitmapWidth() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            return (this.orientation % 360 == 0 || this.orientation % 360 == 180) ? this.currentImage.getIntrinsicWidth() : this.currentImage.getIntrinsicHeight();
        } else {
            if (this.staticThumb instanceof AnimatedFileDrawable) {
                return (this.orientation % 360 == 0 || this.orientation % 360 == 180) ? this.staticThumb.getIntrinsicWidth() : this.staticThumb.getIntrinsicHeight();
            } else {
                Bitmap bitmap = getBitmap();
                return (this.orientation % 360 == 0 || this.orientation % 360 == 180) ? bitmap.getWidth() : bitmap.getHeight();
            }
        }
    }

    public boolean getCacheOnly() {
        return this.currentCacheOnly;
    }

    public Rect getDrawRegion() {
        return this.drawRegion;
    }

    public String getExt() {
        return this.currentExt;
    }

    public String getFilter() {
        return this.currentFilter;
    }

    public String getHttpImageLocation() {
        return this.currentHttpUrl;
    }

    public int getImageHeight() {
        return this.imageH;
    }

    public TLObject getImageLocation() {
        return this.currentImageLocation;
    }

    public int getImageWidth() {
        return this.imageW;
    }

    public int getImageX() {
        return this.imageX;
    }

    public int getImageX2() {
        return this.imageX + this.imageW;
    }

    public int getImageY() {
        return this.imageY;
    }

    public int getImageY2() {
        return this.imageY + this.imageH;
    }

    public String getKey() {
        return this.currentKey;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public MessageObject getParentMessageObject() {
        return this.parentMessageObject;
    }

    public boolean getPressed() {
        return this.isPressed;
    }

    public int getRoundRadius() {
        return this.roundRadius;
    }

    public int getSize() {
        return this.currentSize;
    }

    protected Integer getTag(boolean z) {
        return z ? this.thumbTag : this.tag;
    }

    public String getThumbFilter() {
        return this.currentThumbFilter;
    }

    public String getThumbKey() {
        return this.currentThumbKey;
    }

    public FileLocation getThumbLocation() {
        return this.currentThumbLocation;
    }

    public boolean getVisible() {
        return this.isVisible;
    }

    public boolean hasBitmapImage() {
        return (this.currentImage == null && this.currentThumb == null && this.staticThumb == null) ? false : true;
    }

    public boolean hasImage() {
        return (this.currentImage == null && this.currentThumb == null && this.currentKey == null && this.currentHttpUrl == null && this.staticThumb == null) ? false : true;
    }

    public boolean isAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public boolean isAnimationRunning() {
        return (this.currentImage instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) this.currentImage).isRunning();
    }

    public boolean isForcePreview() {
        return this.forcePreview;
    }

    public boolean isInsideImage(float f, float f2) {
        return f >= ((float) this.imageX) && f <= ((float) (this.imageX + this.imageW)) && f2 >= ((float) this.imageY) && f2 <= ((float) (this.imageY + this.imageH));
    }

    public boolean isNeedsQualityThumb() {
        return this.needsQualityThumb;
    }

    public boolean isShouldGenerateQualityThumb() {
        return this.shouldGenerateQualityThumb;
    }

    public boolean onAttachedToWindow() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        if (this.setImageBackup == null || (this.setImageBackup.fileLocation == null && this.setImageBackup.httpUrl == null && this.setImageBackup.thumbLocation == null && this.setImageBackup.thumb == null)) {
            return false;
        }
        setImage(this.setImageBackup.fileLocation, this.setImageBackup.httpUrl, this.setImageBackup.filter, this.setImageBackup.thumb, this.setImageBackup.thumbLocation, this.setImageBackup.thumbFilter, this.setImageBackup.size, this.setImageBackup.ext, this.setImageBackup.cacheOnly);
        return true;
    }

    public void onDetachedFromWindow() {
        if (!(this.currentImageLocation == null && this.currentHttpUrl == null && this.currentThumbLocation == null && this.staticThumb == null)) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            this.setImageBackup.fileLocation = this.currentImageLocation;
            this.setImageBackup.httpUrl = this.currentHttpUrl;
            this.setImageBackup.filter = this.currentFilter;
            this.setImageBackup.thumb = this.staticThumb;
            this.setImageBackup.thumbLocation = this.currentThumbLocation;
            this.setImageBackup.thumbFilter = this.currentThumbFilter;
            this.setImageBackup.size = this.currentSize;
            this.setImageBackup.ext = this.currentExt;
            this.setImageBackup.cacheOnly = this.currentCacheOnly;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        clearImage();
    }

    public void setAllowStartAnimation(boolean z) {
        this.allowStartAnimation = z;
    }

    public void setAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void setAspectFit(boolean z) {
        this.isAspectFit = z;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
    }

    public void setCrossfadeAlpha(byte b) {
        this.crossfadeAlpha = b;
    }

    public void setDelegate(ImageReceiverDelegate imageReceiverDelegate) {
        this.delegate = imageReceiverDelegate;
    }

    public void setForcePreview(boolean z) {
        this.forcePreview = z;
    }

    public void setImage(TLObject tLObject, String str, Drawable drawable, int i, String str2, boolean z) {
        setImage(tLObject, null, str, drawable, null, null, i, str2, z);
    }

    public void setImage(TLObject tLObject, String str, Drawable drawable, String str2, boolean z) {
        setImage(tLObject, null, str, drawable, null, null, 0, str2, z);
    }

    public void setImage(TLObject tLObject, String str, FileLocation fileLocation, String str2, int i, String str3, boolean z) {
        setImage(tLObject, null, str, null, fileLocation, str2, i, str3, z);
    }

    public void setImage(TLObject tLObject, String str, FileLocation fileLocation, String str2, String str3, boolean z) {
        setImage(tLObject, null, str, null, fileLocation, str2, 0, str3, z);
    }

    public void setImage(TLObject tLObject, String str, String str2, Drawable drawable, FileLocation fileLocation, String str3, int i, String str4, boolean z) {
        if (this.setImageBackup != null) {
            this.setImageBackup.fileLocation = null;
            this.setImageBackup.httpUrl = null;
            this.setImageBackup.thumbLocation = null;
            this.setImageBackup.thumb = null;
        }
        if (!(tLObject == null && str == null && fileLocation == null) && (tLObject == null || (tLObject instanceof TL_fileLocation) || (tLObject instanceof TL_fileEncryptedLocation) || (tLObject instanceof TL_document) || (tLObject instanceof TL_documentEncrypted))) {
            String MD5;
            if (!(fileLocation instanceof TL_fileLocation)) {
                fileLocation = null;
            }
            if (tLObject == null) {
                MD5 = str != null ? Utilities.MD5(str) : null;
            } else if (tLObject instanceof FileLocation) {
                FileLocation fileLocation2 = (FileLocation) tLObject;
                MD5 = fileLocation2.volume_id + "_" + fileLocation2.local_id;
            } else {
                Document document = (Document) tLObject;
                if (document.dc_id != 0) {
                    MD5 = document.version == 0 ? document.dc_id + "_" + document.id : document.dc_id + "_" + document.id + "_" + document.version;
                } else {
                    tLObject = null;
                    MD5 = null;
                }
            }
            if (!(MD5 == null || str2 == null)) {
                MD5 = MD5 + "@" + str2;
            }
            if (!(this.currentKey == null || MD5 == null || !this.currentKey.equals(MD5))) {
                if (this.delegate != null) {
                    ImageReceiverDelegate imageReceiverDelegate = this.delegate;
                    boolean z2 = (this.currentImage == null && this.currentThumb == null && this.staticThumb == null) ? false : true;
                    imageReceiverDelegate.didSetImage(this, z2, this.currentImage == null);
                }
                if (!(this.canceledLoading || this.forcePreview)) {
                    return;
                }
            }
            String str5 = null;
            if (fileLocation != null) {
                str5 = fileLocation.volume_id + "_" + fileLocation.local_id;
                if (str3 != null) {
                    str5 = str5 + "@" + str3;
                }
            }
            recycleBitmap(MD5, false);
            recycleBitmap(str5, true);
            this.currentThumbKey = str5;
            this.currentKey = MD5;
            this.currentExt = str4;
            this.currentImageLocation = tLObject;
            this.currentHttpUrl = str;
            this.currentFilter = str2;
            this.currentThumbFilter = str3;
            this.currentSize = i;
            this.currentCacheOnly = z;
            this.currentThumbLocation = fileLocation;
            this.staticThumb = drawable;
            this.bitmapShader = null;
            this.bitmapShaderThumb = null;
            this.currentAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (this.delegate != null) {
                ImageReceiverDelegate imageReceiverDelegate2 = this.delegate;
                boolean z3 = (this.currentImage == null && this.currentThumb == null && this.staticThumb == null) ? false : true;
                imageReceiverDelegate2.didSetImage(this, z3, this.currentImage == null);
            }
            ImageLoader.getInstance().loadImageForImageReceiver(this);
            if (this.parentView == null) {
                return;
            }
            if (this.invalidateAll) {
                this.parentView.invalidate();
                return;
            } else {
                this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                return;
            }
        }
        recycleBitmap(null, false);
        recycleBitmap(null, true);
        this.currentKey = null;
        this.currentExt = str4;
        this.currentThumbKey = null;
        this.currentThumbFilter = null;
        this.currentImageLocation = null;
        this.currentHttpUrl = null;
        this.currentFilter = null;
        this.currentCacheOnly = false;
        this.staticThumb = drawable;
        this.currentAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.currentThumbLocation = null;
        this.currentSize = 0;
        this.currentImage = null;
        this.bitmapShader = null;
        this.bitmapShaderThumb = null;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
        if (this.parentView != null) {
            if (this.invalidateAll) {
                this.parentView.invalidate();
            } else {
                this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
            }
        }
        if (this.delegate != null) {
            imageReceiverDelegate2 = this.delegate;
            z3 = (this.currentImage == null && this.currentThumb == null && this.staticThumb == null) ? false : true;
            imageReceiverDelegate2.didSetImage(this, z3, this.currentImage == null);
        }
    }

    public void setImage(String str, String str2, Drawable drawable, String str3, int i) {
        setImage(null, str, str2, drawable, null, null, i, str3, true);
    }

    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap != null ? new BitmapDrawable(null, bitmap) : null);
    }

    public void setImageBitmap(Drawable drawable) {
        boolean z = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
        recycleBitmap(null, false);
        recycleBitmap(null, true);
        this.staticThumb = drawable;
        this.currentThumbLocation = null;
        this.currentKey = null;
        this.currentExt = null;
        this.currentThumbKey = null;
        this.currentImage = null;
        this.currentThumbFilter = null;
        this.currentImageLocation = null;
        this.currentHttpUrl = null;
        this.currentFilter = null;
        this.currentSize = 0;
        this.currentCacheOnly = false;
        this.bitmapShader = null;
        this.bitmapShaderThumb = null;
        if (this.setImageBackup != null) {
            this.setImageBackup.fileLocation = null;
            this.setImageBackup.httpUrl = null;
            this.setImageBackup.thumbLocation = null;
            this.setImageBackup.thumb = null;
        }
        this.currentAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (this.delegate != null) {
            ImageReceiverDelegate imageReceiverDelegate = this.delegate;
            if (!(this.currentThumb == null && this.staticThumb == null)) {
                z = true;
            }
            imageReceiverDelegate.didSetImage(this, z, true);
        }
        if (this.parentView == null) {
            return;
        }
        if (this.invalidateAll) {
            this.parentView.invalidate();
        } else {
            this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        }
    }

    protected boolean setImageBitmapByKey(BitmapDrawable bitmapDrawable, String str, boolean z, boolean z2) {
        boolean z3 = false;
        if (bitmapDrawable == null || str == null) {
            return false;
        }
        boolean z4;
        if (z) {
            if (this.currentThumb == null && (this.currentImage == null || (((this.currentImage instanceof AnimatedFileDrawable) && !((AnimatedFileDrawable) this.currentImage).hasBitmap()) || this.forcePreview))) {
                if (this.currentThumbKey == null || !str.equals(this.currentThumbKey)) {
                    return false;
                }
                ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
                this.currentThumb = bitmapDrawable;
                if (this.roundRadius == 0 || this.currentImage != null || !(bitmapDrawable instanceof BitmapDrawable)) {
                    this.bitmapShaderThumb = null;
                } else if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) bitmapDrawable).setRoundRadius(this.roundRadius);
                } else {
                    this.bitmapShaderThumb = new BitmapShader(bitmapDrawable.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                }
                if (z2 || this.crossfadeAlpha == 2) {
                    this.currentAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                } else {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    z4 = this.staticThumb != null && this.currentKey == null;
                    this.crossfadeWithThumb = z4;
                }
                if (!((this.staticThumb instanceof BitmapDrawable) || this.parentView == null)) {
                    if (this.invalidateAll) {
                        this.parentView.invalidate();
                    } else {
                        this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                    }
                }
            }
        } else if (this.currentKey == null || !str.equals(this.currentKey)) {
            return false;
        } else {
            if (!(bitmapDrawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentKey);
            }
            this.currentImage = bitmapDrawable;
            if (this.roundRadius == 0 || !(bitmapDrawable instanceof BitmapDrawable)) {
                this.bitmapShader = null;
            } else if (bitmapDrawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) bitmapDrawable).setRoundRadius(this.roundRadius);
            } else {
                this.bitmapShader = new BitmapShader(bitmapDrawable.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
            }
            if (z2 || this.forcePreview) {
                this.currentAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            } else if ((this.currentThumb == null && this.staticThumb == null) || this.currentAlpha == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.currentAlpha = 0.0f;
                this.lastUpdateAlphaTime = System.currentTimeMillis();
                z4 = (this.currentThumb == null && this.staticThumb == null) ? false : true;
                this.crossfadeWithThumb = z4;
            }
            if (bitmapDrawable instanceof AnimatedFileDrawable) {
                AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) bitmapDrawable;
                animatedFileDrawable.setParentView(this.parentView);
                if (this.allowStartAnimation) {
                    animatedFileDrawable.start();
                }
            }
            if (this.parentView != null) {
                if (this.invalidateAll) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
        }
        if (this.delegate != null) {
            ImageReceiverDelegate imageReceiverDelegate = this.delegate;
            z4 = (this.currentImage == null && this.currentThumb == null && this.staticThumb == null) ? false : true;
            if (this.currentImage == null) {
                z3 = true;
            }
            imageReceiverDelegate.didSetImage(this, z4, z3);
        }
        return true;
    }

    public void setImageCoords(int i, int i2, int i3, int i4) {
        this.imageX = i;
        this.imageY = i2;
        this.imageW = i3;
        this.imageH = i4;
    }

    public void setInvalidateAll(boolean z) {
        this.invalidateAll = z;
    }

    public void setNeedsQualityThumb(boolean z) {
        this.needsQualityThumb = z;
        if (this.needsQualityThumb) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
        } else {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
        }
    }

    public void setOrientation(int i, boolean z) {
        int i2 = i;
        while (i2 < 0) {
            i2 += 360;
        }
        while (i2 > 360) {
            i2 -= 360;
        }
        this.orientation = i2;
        this.centerRotation = z;
    }

    public void setParentMessageObject(MessageObject messageObject) {
        this.parentMessageObject = messageObject;
    }

    public void setParentView(View view) {
        this.parentView = view;
        if (this.currentImage instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) this.currentImage).setParentView(this.parentView);
        }
    }

    public void setPressed(boolean z) {
        this.isPressed = z;
    }

    public void setRoundRadius(int i) {
        this.roundRadius = i;
    }

    public void setShouldGenerateQualityThumb(boolean z) {
        this.shouldGenerateQualityThumb = z;
    }

    protected void setTag(Integer num, boolean z) {
        if (z) {
            this.thumbTag = num;
        } else {
            this.tag = num;
        }
    }

    public void setVisible(boolean z, boolean z2) {
        if (this.isVisible != z) {
            this.isVisible = z;
            if (z2 && this.parentView != null) {
                if (this.invalidateAll) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
        }
    }

    public void startAnimation() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) this.currentImage).start();
        }
    }

    public void stopAnimation() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) this.currentImage).stop();
        }
    }
}
