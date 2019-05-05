package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build.VERSION;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.DispatchQueue;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.PhotoEditToolCell;
import com.hanista.mobogram.ui.Components.PhotoEditorSeekBar.PhotoEditorSeekBarDelegate;
import com.hanista.mobogram.ui.Components.PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate;
import com.hanista.mobogram.ui.Components.PhotoFilterCurvesControl.PhotoFilterCurvesControlDelegate;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

@SuppressLint({"NewApi"})
public class PhotoFilterView extends FrameLayout {
    private static final int curveDataStep = 2;
    private static final int curveGranularity = 100;
    private Bitmap bitmapToEdit;
    private float blurAngle;
    private PhotoFilterBlurControl blurControl;
    private float blurExcludeBlurSize;
    private Point blurExcludePoint;
    private float blurExcludeSize;
    private FrameLayout blurLayout;
    private TextView blurLinearButton;
    private TextView blurOffButton;
    private TextView blurRadialButton;
    private int blurTool;
    private int blurType;
    private TextView cancelTextView;
    private int contrastTool;
    private float contrastValue;
    private FrameLayout curveLayout;
    private TextView[] curveTextView;
    private PhotoFilterCurvesControl curvesControl;
    private int curvesTool;
    private CurvesToolValue curvesToolValue;
    private TextView doneTextView;
    private FrameLayout editView;
    private EGLThread eglThread;
    private int enhanceTool;
    private float enhanceValue;
    private int exposureTool;
    private float exposureValue;
    private int fadeTool;
    private float fadeValue;
    private int grainTool;
    private float grainValue;
    private int highlightsTool;
    private float highlightsValue;
    private TextView infoTextView;
    private int orientation;
    private TextView paramTextView;
    private float previousValue;
    private int previousValueInt;
    private int previousValueInt2;
    private RecyclerListView recyclerListView;
    private int saturationTool;
    private float saturationValue;
    private int selectedTintMode;
    private int selectedTool;
    private int shadowsTool;
    private float shadowsValue;
    private int sharpenTool;
    private float sharpenValue;
    private boolean showOriginal;
    private TextureView textureView;
    private LinearLayout tintButtonsContainer;
    private final int[] tintHighlighsColors;
    private TextView tintHighlightsButton;
    private int tintHighlightsColor;
    private FrameLayout tintLayout;
    private final int[] tintShadowColors;
    private TextView tintShadowsButton;
    private int tintShadowsColor;
    private int tintTool;
    private ToolsAdapter toolsAdapter;
    private FrameLayout toolsView;
    private PhotoEditorSeekBar valueSeekBar;
    private TextView valueTextView;
    private int vignetteTool;
    private float vignetteValue;
    private int warmthTool;
    private float warmthValue;

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.15 */
    class AnonymousClass15 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ View val$viewFrom;
        final /* synthetic */ View val$viewTo;

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.15.1 */
        class C14051 extends AnimatorListenerAdapterProxy {
            C14051() {
            }

            public void onAnimationEnd(Animator animator) {
                if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.enhanceTool) {
                    PhotoFilterView.this.checkEnhance();
                }
            }
        }

        AnonymousClass15(View view, View view2) {
            this.val$viewFrom = view;
            this.val$viewTo = view2;
        }

        public void onAnimationEnd(Animator animator) {
            this.val$viewFrom.setVisibility(8);
            this.val$viewTo.setVisibility(0);
            this.val$viewTo.setTranslationY((float) AndroidUtilities.dp(126.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.val$viewTo, "translationY", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new C14051());
            animatorSet.setDuration(200);
            animatorSet.start();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.1 */
    class C14061 implements SurfaceTextureListener {

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.1.1 */
        class C14041 implements Runnable {
            C14041() {
            }

            public void run() {
                if (PhotoFilterView.this.eglThread != null) {
                    PhotoFilterView.this.eglThread.requestRender(false, true);
                }
            }
        }

        C14061() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            if (PhotoFilterView.this.eglThread == null && surfaceTexture != null) {
                PhotoFilterView.this.eglThread = new EGLThread(surfaceTexture, PhotoFilterView.this.bitmapToEdit);
                PhotoFilterView.this.eglThread.setSurfaceTextureSize(i, i2);
                PhotoFilterView.this.eglThread.requestRender(true, true);
            }
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.shutdown();
                PhotoFilterView.this.eglThread = null;
            }
            return true;
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.setSurfaceTextureSize(i, i2);
                PhotoFilterView.this.eglThread.requestRender(false, true);
                PhotoFilterView.this.eglThread.postRunnable(new C14041());
            }
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.2 */
    class C14072 implements PhotoFilterLinearBlurControlDelegate {
        C14072() {
        }

        public void valueChanged(Point point, float f, float f2, float f3) {
            PhotoFilterView.this.blurExcludeSize = f2;
            PhotoFilterView.this.blurExcludePoint = point;
            PhotoFilterView.this.blurExcludeBlurSize = f;
            PhotoFilterView.this.blurAngle = f3;
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.3 */
    class C14083 implements PhotoFilterCurvesControlDelegate {
        C14083() {
        }

        public void valueChanged() {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.4 */
    class C14094 implements OnItemClickListener {
        C14094() {
        }

        public void onItemClick(View view, int i) {
            PhotoFilterView.this.selectedTool = i;
            if (i == PhotoFilterView.this.enhanceTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.enhanceValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(0, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Enhance", C0338R.string.Enhance));
            } else if (i == PhotoFilterView.this.highlightsTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.highlightsValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(-100, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Highlights", C0338R.string.Highlights));
            } else if (i == PhotoFilterView.this.contrastTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.contrastValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(-100, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Contrast", C0338R.string.Contrast));
            } else if (i == PhotoFilterView.this.exposureTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.exposureValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(-100, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Exposure", C0338R.string.Exposure));
            } else if (i == PhotoFilterView.this.warmthTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.warmthValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(-100, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Warmth", C0338R.string.Warmth));
            } else if (i == PhotoFilterView.this.saturationTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.saturationValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(-100, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Saturation", C0338R.string.Saturation));
            } else if (i == PhotoFilterView.this.vignetteTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.vignetteValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(0, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Vignette", C0338R.string.Vignette));
            } else if (i == PhotoFilterView.this.shadowsTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.shadowsValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(-100, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Shadows", C0338R.string.Shadows));
            } else if (i == PhotoFilterView.this.grainTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.grainValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(0, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Grain", C0338R.string.Grain));
            } else if (i == PhotoFilterView.this.fadeTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.fadeValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(0, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Fade", C0338R.string.Fade));
            } else if (i == PhotoFilterView.this.sharpenTool) {
                PhotoFilterView.this.previousValue = PhotoFilterView.this.sharpenValue;
                PhotoFilterView.this.valueSeekBar.setMinMax(0, PhotoFilterView.curveGranularity);
                PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Sharpen", C0338R.string.Sharpen));
            } else if (i == PhotoFilterView.this.blurTool) {
                PhotoFilterView.this.previousValueInt = PhotoFilterView.this.blurType;
            } else if (i == PhotoFilterView.this.tintTool) {
                PhotoFilterView.this.previousValueInt = PhotoFilterView.this.tintShadowsColor;
                PhotoFilterView.this.previousValueInt2 = PhotoFilterView.this.tintHighlightsColor;
            } else if (i == PhotoFilterView.this.curvesTool) {
                PhotoFilterView.this.curvesToolValue.luminanceCurve.saveValues();
                PhotoFilterView.this.curvesToolValue.redCurve.saveValues();
                PhotoFilterView.this.curvesToolValue.greenCurve.saveValues();
                PhotoFilterView.this.curvesToolValue.blueCurve.saveValues();
            }
            PhotoFilterView.this.valueSeekBar.setProgress((int) PhotoFilterView.this.previousValue, false);
            PhotoFilterView.this.updateValueTextView();
            PhotoFilterView.this.switchToOrFromEditMode();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.5 */
    class C14105 implements OnClickListener {
        C14105() {
        }

        public void onClick(View view) {
            if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.enhanceTool) {
                PhotoFilterView.this.enhanceValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.highlightsTool) {
                PhotoFilterView.this.highlightsValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.contrastTool) {
                PhotoFilterView.this.contrastValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.exposureTool) {
                PhotoFilterView.this.exposureValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.warmthTool) {
                PhotoFilterView.this.warmthValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.saturationTool) {
                PhotoFilterView.this.saturationValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.vignetteTool) {
                PhotoFilterView.this.vignetteValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.shadowsTool) {
                PhotoFilterView.this.shadowsValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.grainTool) {
                PhotoFilterView.this.grainValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.sharpenTool) {
                PhotoFilterView.this.sharpenValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.fadeTool) {
                PhotoFilterView.this.fadeValue = PhotoFilterView.this.previousValue;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.blurTool) {
                PhotoFilterView.this.blurType = PhotoFilterView.this.previousValueInt;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.tintTool) {
                PhotoFilterView.this.tintShadowsColor = PhotoFilterView.this.previousValueInt;
                PhotoFilterView.this.tintHighlightsColor = PhotoFilterView.this.previousValueInt2;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.curvesTool) {
                PhotoFilterView.this.curvesToolValue.luminanceCurve.restoreValues();
                PhotoFilterView.this.curvesToolValue.redCurve.restoreValues();
                PhotoFilterView.this.curvesToolValue.greenCurve.restoreValues();
                PhotoFilterView.this.curvesToolValue.blueCurve.restoreValues();
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(PhotoFilterView.this.selectedTool != PhotoFilterView.this.blurTool);
            }
            PhotoFilterView.this.switchToOrFromEditMode();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.6 */
    class C14116 implements OnClickListener {
        C14116() {
        }

        public void onClick(View view) {
            PhotoFilterView.this.toolsAdapter.notifyDataSetChanged();
            PhotoFilterView.this.switchToOrFromEditMode();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.7 */
    class C14127 implements PhotoEditorSeekBarDelegate {
        C14127() {
        }

        public void onProgressChanged() {
            int progress = PhotoFilterView.this.valueSeekBar.getProgress();
            if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.enhanceTool) {
                PhotoFilterView.this.enhanceValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.highlightsTool) {
                PhotoFilterView.this.highlightsValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.contrastTool) {
                PhotoFilterView.this.contrastValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.exposureTool) {
                PhotoFilterView.this.exposureValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.warmthTool) {
                PhotoFilterView.this.warmthValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.saturationTool) {
                PhotoFilterView.this.saturationValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.vignetteTool) {
                PhotoFilterView.this.vignetteValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.shadowsTool) {
                PhotoFilterView.this.shadowsValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.grainTool) {
                PhotoFilterView.this.grainValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.sharpenTool) {
                PhotoFilterView.this.sharpenValue = (float) progress;
            } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.fadeTool) {
                PhotoFilterView.this.fadeValue = (float) progress;
            }
            PhotoFilterView.this.updateValueTextView();
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.8 */
    class C14138 implements OnClickListener {
        C14138() {
        }

        public void onClick(View view) {
            int intValue = ((Integer) view.getTag()).intValue();
            int i = 0;
            while (i < 4) {
                PhotoFilterView.this.curveTextView[i].setTextColor(i == intValue ? -1 : -8355712);
                i++;
            }
            PhotoFilterView.this.curvesToolValue.activeType = intValue;
            PhotoFilterView.this.curvesControl.invalidate();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.9 */
    class C14149 implements OnClickListener {
        C14149() {
        }

        public void onClick(View view) {
            PhotoFilterView.this.selectedTintMode = 0;
            PhotoFilterView.this.updateSelectedTintButton(true);
        }
    }

    public static class CurvesToolValue {
        public static final int CurvesTypeBlue = 3;
        public static final int CurvesTypeGreen = 2;
        public static final int CurvesTypeLuminance = 0;
        public static final int CurvesTypeRed = 1;
        public int activeType;
        public CurvesValue blueCurve;
        public ByteBuffer curveBuffer;
        public CurvesValue greenCurve;
        public CurvesValue luminanceCurve;
        public CurvesValue redCurve;

        public CurvesToolValue() {
            this.luminanceCurve = new CurvesValue();
            this.redCurve = new CurvesValue();
            this.greenCurve = new CurvesValue();
            this.blueCurve = new CurvesValue();
            this.curveBuffer = null;
            this.curveBuffer = ByteBuffer.allocateDirect(800);
            this.curveBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        public void fillBuffer() {
            this.curveBuffer.position(CurvesTypeLuminance);
            float[] dataPoints = this.luminanceCurve.getDataPoints();
            float[] dataPoints2 = this.redCurve.getDataPoints();
            float[] dataPoints3 = this.greenCurve.getDataPoints();
            float[] dataPoints4 = this.blueCurve.getDataPoints();
            for (int i = CurvesTypeLuminance; i < Callback.DEFAULT_DRAG_ANIMATION_DURATION; i += CurvesTypeRed) {
                this.curveBuffer.put((byte) ((int) (dataPoints2[i] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (dataPoints3[i] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (dataPoints4[i] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (dataPoints[i] * 255.0f)));
            }
            this.curveBuffer.position(CurvesTypeLuminance);
        }

        public boolean shouldBeSkipped() {
            return this.luminanceCurve.isDefault() && this.redCurve.isDefault() && this.greenCurve.isDefault() && this.blueCurve.isDefault();
        }
    }

    public static class CurvesValue {
        public float blacksLevel;
        public float[] cachedDataPoints;
        public float highlightsLevel;
        public float midtonesLevel;
        public float previousBlacksLevel;
        public float previousHighlightsLevel;
        public float previousMidtonesLevel;
        public float previousShadowsLevel;
        public float previousWhitesLevel;
        public float shadowsLevel;
        public float whitesLevel;

        public CurvesValue() {
            this.blacksLevel = 0.0f;
            this.shadowsLevel = 25.0f;
            this.midtonesLevel = 50.0f;
            this.highlightsLevel = 75.0f;
            this.whitesLevel = 100.0f;
            this.previousBlacksLevel = 0.0f;
            this.previousShadowsLevel = 25.0f;
            this.previousMidtonesLevel = 50.0f;
            this.previousHighlightsLevel = 75.0f;
            this.previousWhitesLevel = 100.0f;
        }

        public float[] getDataPoints() {
            if (this.cachedDataPoints == null) {
                interpolateCurve();
            }
            return this.cachedDataPoints;
        }

        public float[] interpolateCurve() {
            int i;
            float[] fArr = new float[]{-0.001f, this.blacksLevel / 100.0f, 0.0f, this.blacksLevel / 100.0f, 0.25f, this.shadowsLevel / 100.0f, 0.5f, this.midtonesLevel / 100.0f, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION, this.highlightsLevel / 100.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.whitesLevel / 100.0f, 1.001f, this.whitesLevel / 100.0f};
            ArrayList arrayList = new ArrayList(PhotoFilterView.curveGranularity);
            ArrayList arrayList2 = new ArrayList(PhotoFilterView.curveGranularity);
            arrayList2.add(Float.valueOf(fArr[0]));
            arrayList2.add(Float.valueOf(fArr[1]));
            for (int i2 = 1; i2 < (fArr.length / PhotoFilterView.curveDataStep) - 2; i2++) {
                float f = fArr[(i2 - 1) * PhotoFilterView.curveDataStep];
                float f2 = fArr[((i2 - 1) * PhotoFilterView.curveDataStep) + 1];
                float f3 = fArr[i2 * PhotoFilterView.curveDataStep];
                float f4 = fArr[(i2 * PhotoFilterView.curveDataStep) + 1];
                float f5 = fArr[(i2 + 1) * PhotoFilterView.curveDataStep];
                float f6 = fArr[((i2 + 1) * PhotoFilterView.curveDataStep) + 1];
                float f7 = fArr[(i2 + PhotoFilterView.curveDataStep) * PhotoFilterView.curveDataStep];
                float f8 = fArr[((i2 + PhotoFilterView.curveDataStep) * PhotoFilterView.curveDataStep) + 1];
                for (i = 1; i < PhotoFilterView.curveGranularity; i++) {
                    float f9 = ((float) i) * 0.01f;
                    float f10 = f9 * f9;
                    float f11 = f10 * f9;
                    float f12 = 0.5f * ((((2.0f * f3) + ((f5 - f) * f9)) + (((((2.0f * f) - (5.0f * f3)) + (4.0f * f5)) - f7) * f10)) + (((((3.0f * f3) - f) - (3.0f * f5)) + f7) * f11));
                    float f13 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    f9 = Math.max(0.0f, Math.min(f13, ((((f9 * (f6 - f2)) + (2.0f * f4)) + (f10 * ((((2.0f * f2) - (5.0f * f4)) + (4.0f * f6)) - f8))) + (((((3.0f * f4) - f2) - (3.0f * f6)) + f8) * f11)) * 0.5f));
                    if (f12 > f) {
                        arrayList2.add(Float.valueOf(f12));
                        arrayList2.add(Float.valueOf(f9));
                    }
                    if ((i - 1) % PhotoFilterView.curveDataStep == 0) {
                        arrayList.add(Float.valueOf(f9));
                    }
                }
                arrayList2.add(Float.valueOf(f5));
                arrayList2.add(Float.valueOf(f6));
            }
            arrayList2.add(Float.valueOf(fArr[12]));
            arrayList2.add(Float.valueOf(fArr[13]));
            this.cachedDataPoints = new float[arrayList.size()];
            for (i = 0; i < this.cachedDataPoints.length; i++) {
                this.cachedDataPoints[i] = ((Float) arrayList.get(i)).floatValue();
            }
            fArr = new float[arrayList2.size()];
            for (i = 0; i < fArr.length; i++) {
                fArr[i] = ((Float) arrayList2.get(i)).floatValue();
            }
            return fArr;
        }

        public boolean isDefault() {
            return ((double) Math.abs(this.blacksLevel - 0.0f)) < 1.0E-5d && ((double) Math.abs(this.shadowsLevel - 25.0f)) < 1.0E-5d && ((double) Math.abs(this.midtonesLevel - 50.0f)) < 1.0E-5d && ((double) Math.abs(this.highlightsLevel - 75.0f)) < 1.0E-5d && ((double) Math.abs(this.whitesLevel - 100.0f)) < 1.0E-5d;
        }

        public void restoreValues() {
            this.blacksLevel = this.previousBlacksLevel;
            this.shadowsLevel = this.previousShadowsLevel;
            this.midtonesLevel = this.previousMidtonesLevel;
            this.highlightsLevel = this.previousHighlightsLevel;
            this.whitesLevel = this.previousWhitesLevel;
            interpolateCurve();
        }

        public void saveValues() {
            this.previousBlacksLevel = this.blacksLevel;
            this.previousShadowsLevel = this.shadowsLevel;
            this.previousMidtonesLevel = this.midtonesLevel;
            this.previousHighlightsLevel = this.highlightsLevel;
            this.previousWhitesLevel = this.whitesLevel;
        }
    }

    public class EGLThread extends DispatchQueue {
        private static final int PGPhotoEnhanceHistogramBins = 256;
        private static final int PGPhotoEnhanceSegments = 4;
        private static final String blurFragmentShaderCode = "uniform sampler2D sourceImage;varying highp vec2 blurCoordinates[9];void main() {lowp vec4 sum = vec4(0.0);sum += texture2D(sourceImage, blurCoordinates[0]) * 0.133571;sum += texture2D(sourceImage, blurCoordinates[1]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[2]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[3]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[4]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[5]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[6]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[7]) * 0.012595;sum += texture2D(sourceImage, blurCoordinates[8]) * 0.012595;gl_FragColor = sum;}";
        private static final String blurVertexShaderCode = "attribute vec4 position;attribute vec4 inputTexCoord;uniform highp float texelWidthOffset;uniform highp float texelHeightOffset;varying vec2 blurCoordinates[9];void main() {gl_Position = position;vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);blurCoordinates[0] = inputTexCoord.xy;blurCoordinates[1] = inputTexCoord.xy + singleStepOffset * 1.458430;blurCoordinates[2] = inputTexCoord.xy - singleStepOffset * 1.458430;blurCoordinates[3] = inputTexCoord.xy + singleStepOffset * 3.403985;blurCoordinates[4] = inputTexCoord.xy - singleStepOffset * 3.403985;blurCoordinates[5] = inputTexCoord.xy + singleStepOffset * 5.351806;blurCoordinates[6] = inputTexCoord.xy - singleStepOffset * 5.351806;blurCoordinates[7] = inputTexCoord.xy + singleStepOffset * 7.302940;blurCoordinates[8] = inputTexCoord.xy - singleStepOffset * 7.302940;}";
        private static final String enhanceFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.001953125, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 c00 = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 c01 = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 c10 = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 c11 = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((c00.r - c00.g) / (c00.b - c00.g));float c2 = ((c01.r - c01.g) / (c01.b - c01.g));float c3 = ((c10.r - c10.g) / (c10.b - c10.g));float c4 = ((c11.r - c11.g) / (c11.b - c11.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}";
        private static final String linearBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
        private static final String radialBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
        private static final String rgbToHsvFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}";
        private static final String sharpenFragmentShaderCode = "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}";
        private static final String sharpenVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}";
        private static final String simpleFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}";
        private static final String simpleVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}";
        private static final String toolsFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;} return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}";
        private final int EGL_CONTEXT_CLIENT_VERSION;
        private final int EGL_OPENGL_ES2_BIT;
        private int blurHeightHandle;
        private int blurInputTexCoordHandle;
        private int blurPositionHandle;
        private int blurShaderProgram;
        private int blurSourceImageHandle;
        private int blurWidthHandle;
        private boolean blured;
        private int contrastHandle;
        private Bitmap currentBitmap;
        private int[] curveTextures;
        private int curvesImageHandle;
        private Runnable drawRunnable;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private int enhanceInputImageTexture2Handle;
        private int enhanceInputTexCoordHandle;
        private int enhanceIntensityHandle;
        private int enhancePositionHandle;
        private int enhanceShaderProgram;
        private int enhanceSourceImageHandle;
        private int[] enhanceTextures;
        private int exposureHandle;
        private int fadeAmountHandle;
        private GL gl;
        private int grainHandle;
        private int heightHandle;
        private int highlightsHandle;
        private int highlightsTintColorHandle;
        private int highlightsTintIntensityHandle;
        private boolean hsvGenerated;
        private boolean initied;
        private int inputTexCoordHandle;
        private long lastRenderCallTime;
        private int linearBlurAngleHandle;
        private int linearBlurAspectRatioHandle;
        private int linearBlurExcludeBlurSizeHandle;
        private int linearBlurExcludePointHandle;
        private int linearBlurExcludeSizeHandle;
        private int linearBlurInputTexCoordHandle;
        private int linearBlurPositionHandle;
        private int linearBlurShaderProgram;
        private int linearBlurSourceImage2Handle;
        private int linearBlurSourceImageHandle;
        private boolean needUpdateBlurTexture;
        private int positionHandle;
        private int radialBlurAspectRatioHandle;
        private int radialBlurExcludeBlurSizeHandle;
        private int radialBlurExcludePointHandle;
        private int radialBlurExcludeSizeHandle;
        private int radialBlurInputTexCoordHandle;
        private int radialBlurPositionHandle;
        private int radialBlurShaderProgram;
        private int radialBlurSourceImage2Handle;
        private int radialBlurSourceImageHandle;
        private int renderBufferHeight;
        private int renderBufferWidth;
        private int[] renderFrameBuffer;
        private int[] renderTexture;
        private int rgbToHsvInputTexCoordHandle;
        private int rgbToHsvPositionHandle;
        private int rgbToHsvShaderProgram;
        private int rgbToHsvSourceImageHandle;
        private int saturationHandle;
        private int shadowsHandle;
        private int shadowsTintColorHandle;
        private int shadowsTintIntensityHandle;
        private int sharpenHandle;
        private int sharpenHeightHandle;
        private int sharpenInputTexCoordHandle;
        private int sharpenPositionHandle;
        private int sharpenShaderProgram;
        private int sharpenSourceImageHandle;
        private int sharpenWidthHandle;
        private int simpleInputTexCoordHandle;
        private int simplePositionHandle;
        private int simpleShaderProgram;
        private int simpleSourceImageHandle;
        private int skipToneHandle;
        private int sourceImageHandle;
        private volatile int surfaceHeight;
        private SurfaceTexture surfaceTexture;
        private volatile int surfaceWidth;
        private FloatBuffer textureBuffer;
        private int toolsShaderProgram;
        private FloatBuffer vertexBuffer;
        private FloatBuffer vertexInvertBuffer;
        private int vignetteHandle;
        private int warmthHandle;
        private int widthHandle;

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.EGLThread.1 */
        class C14151 implements Runnable {
            C14151() {
            }

            public void run() {
                if (!EGLThread.this.initied) {
                    return;
                }
                if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                    GLES20.glViewport(0, 0, EGLThread.this.renderBufferWidth, EGLThread.this.renderBufferHeight);
                    EGLThread.this.drawEnhancePass();
                    EGLThread.this.drawSharpenPass();
                    EGLThread.this.drawCustomParamsPass();
                    EGLThread.this.blured = EGLThread.this.drawBlurPass();
                    GLES20.glViewport(0, 0, EGLThread.this.surfaceWidth, EGLThread.this.surfaceHeight);
                    GLES20.glBindFramebuffer(36160, 0);
                    GLES20.glClear(0);
                    GLES20.glUseProgram(EGLThread.this.simpleShaderProgram);
                    GLES20.glActiveTexture(33984);
                    GLES20.glBindTexture(3553, EGLThread.this.renderTexture[EGLThread.this.blured ? 0 : 1]);
                    GLES20.glUniform1i(EGLThread.this.simpleSourceImageHandle, 0);
                    GLES20.glEnableVertexAttribArray(EGLThread.this.simpleInputTexCoordHandle);
                    GLES20.glVertexAttribPointer(EGLThread.this.simpleInputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, EGLThread.this.textureBuffer);
                    GLES20.glEnableVertexAttribArray(EGLThread.this.simplePositionHandle);
                    GLES20.glVertexAttribPointer(EGLThread.this.simplePositionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, EGLThread.this.vertexBuffer);
                    GLES20.glDrawArrays(5, 0, EGLThread.PGPhotoEnhanceSegments);
                    EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                    return;
                }
                FileLog.m16e("tmessages", "eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.EGLThread.2 */
        class C14162 implements Runnable {
            final /* synthetic */ Bitmap[] val$object;
            final /* synthetic */ Semaphore val$semaphore;

            C14162(Bitmap[] bitmapArr, Semaphore semaphore) {
                this.val$object = bitmapArr;
                this.val$semaphore = semaphore;
            }

            public void run() {
                int i = 1;
                GLES20.glBindFramebuffer(36160, EGLThread.this.renderFrameBuffer[1]);
                int[] access$3700 = EGLThread.this.renderTexture;
                if (EGLThread.this.blured) {
                    i = 0;
                }
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, access$3700[i], 0);
                GLES20.glClear(0);
                this.val$object[0] = EGLThread.this.getRenderBufferBitmap();
                this.val$semaphore.release();
                GLES20.glBindFramebuffer(36160, 0);
                GLES20.glClear(0);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.EGLThread.3 */
        class C14173 implements Runnable {
            C14173() {
            }

            public void run() {
                EGLThread.this.finish();
                EGLThread.this.currentBitmap = null;
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoFilterView.EGLThread.4 */
        class C14184 implements Runnable {
            final /* synthetic */ boolean val$force;
            final /* synthetic */ boolean val$updateBlur;

            C14184(boolean z, boolean z2) {
                this.val$updateBlur = z;
                this.val$force = z2;
            }

            public void run() {
                if (!EGLThread.this.needUpdateBlurTexture) {
                    EGLThread.this.needUpdateBlurTexture = this.val$updateBlur;
                }
                long currentTimeMillis = System.currentTimeMillis();
                if (this.val$force || Math.abs(EGLThread.this.lastRenderCallTime - currentTimeMillis) > 30) {
                    EGLThread.this.lastRenderCallTime = currentTimeMillis;
                    EGLThread.this.drawRunnable.run();
                }
            }
        }

        public EGLThread(SurfaceTexture surfaceTexture, Bitmap bitmap) {
            super("EGLThread");
            this.EGL_CONTEXT_CLIENT_VERSION = 12440;
            this.EGL_OPENGL_ES2_BIT = PGPhotoEnhanceSegments;
            this.needUpdateBlurTexture = true;
            this.enhanceTextures = new int[PhotoFilterView.curveDataStep];
            this.renderTexture = new int[3];
            this.renderFrameBuffer = new int[3];
            this.curveTextures = new int[1];
            this.drawRunnable = new C14151();
            this.surfaceTexture = surfaceTexture;
            this.currentBitmap = bitmap;
        }

        private Bitmap createBitmap(Bitmap bitmap, int i, int i2, float f) {
            Matrix matrix = new Matrix();
            matrix.setScale(f, f);
            matrix.postRotate((float) PhotoFilterView.this.orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        private boolean drawBlurPass() {
            if (PhotoFilterView.this.showOriginal || PhotoFilterView.this.blurType == 0) {
                return false;
            }
            if (this.needUpdateBlurTexture) {
                GLES20.glUseProgram(this.blurShaderProgram);
                GLES20.glUniform1i(this.blurSourceImageHandle, 0);
                GLES20.glEnableVertexAttribArray(this.blurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.blurInputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.blurPositionHandle);
                GLES20.glVertexAttribPointer(this.blurPositionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.vertexInvertBuffer);
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
                GLES20.glClear(0);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[1]);
                GLES20.glUniform1f(this.blurWidthHandle, 0.0f);
                GLES20.glUniform1f(this.blurHeightHandle, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) this.renderBufferHeight));
                GLES20.glDrawArrays(5, 0, PGPhotoEnhanceSegments);
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[PhotoFilterView.curveDataStep]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[PhotoFilterView.curveDataStep], 0);
                GLES20.glClear(0);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[0]);
                GLES20.glUniform1f(this.blurWidthHandle, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) this.renderBufferWidth));
                GLES20.glUniform1f(this.blurHeightHandle, 0.0f);
                GLES20.glDrawArrays(5, 0, PGPhotoEnhanceSegments);
                this.needUpdateBlurTexture = false;
            }
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glClear(0);
            if (PhotoFilterView.this.blurType == 1) {
                GLES20.glUseProgram(this.radialBlurShaderProgram);
                GLES20.glUniform1i(this.radialBlurSourceImageHandle, 0);
                GLES20.glUniform1i(this.radialBlurSourceImage2Handle, 1);
                GLES20.glUniform1f(this.radialBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                GLES20.glUniform1f(this.radialBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                GLES20.glUniform2f(this.radialBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.f2683x, PhotoFilterView.this.blurExcludePoint.f2684y);
                GLES20.glUniform1f(this.radialBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
                GLES20.glEnableVertexAttribArray(this.radialBlurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.radialBlurInputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.radialBlurPositionHandle);
                GLES20.glVertexAttribPointer(this.radialBlurPositionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.vertexInvertBuffer);
            } else if (PhotoFilterView.this.blurType == PhotoFilterView.curveDataStep) {
                GLES20.glUseProgram(this.linearBlurShaderProgram);
                GLES20.glUniform1i(this.linearBlurSourceImageHandle, 0);
                GLES20.glUniform1i(this.linearBlurSourceImage2Handle, 1);
                GLES20.glUniform1f(this.linearBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                GLES20.glUniform1f(this.linearBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                GLES20.glUniform1f(this.linearBlurAngleHandle, PhotoFilterView.this.blurAngle);
                GLES20.glUniform2f(this.linearBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.f2683x, PhotoFilterView.this.blurExcludePoint.f2684y);
                GLES20.glUniform1f(this.linearBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
                GLES20.glEnableVertexAttribArray(this.linearBlurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.linearBlurInputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.linearBlurPositionHandle);
                GLES20.glVertexAttribPointer(this.linearBlurPositionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.vertexInvertBuffer);
            }
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, this.renderTexture[PhotoFilterView.curveDataStep]);
            GLES20.glDrawArrays(5, 0, PGPhotoEnhanceSegments);
            return true;
        }

        private void drawCustomParamsPass() {
            float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.toolsShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glUniform1i(this.sourceImageHandle, 0);
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.shadowsHandle, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                GLES20.glUniform1f(this.highlightsHandle, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                GLES20.glUniform1f(this.exposureHandle, 0.0f);
                GLES20.glUniform1f(this.contrastHandle, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                GLES20.glUniform1f(this.saturationHandle, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                GLES20.glUniform1f(this.warmthHandle, 0.0f);
                GLES20.glUniform1f(this.vignetteHandle, 0.0f);
                GLES20.glUniform1f(this.grainHandle, 0.0f);
                GLES20.glUniform1f(this.fadeAmountHandle, 0.0f);
                GLES20.glUniform3f(this.highlightsTintColorHandle, 0.0f, 0.0f, 0.0f);
                GLES20.glUniform1f(this.highlightsTintIntensityHandle, 0.0f);
                GLES20.glUniform3f(this.shadowsTintColorHandle, 0.0f, 0.0f, 0.0f);
                GLES20.glUniform1f(this.shadowsTintIntensityHandle, 0.0f);
                GLES20.glUniform1f(this.skipToneHandle, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            } else {
                GLES20.glUniform1f(this.shadowsHandle, PhotoFilterView.this.getShadowsValue());
                GLES20.glUniform1f(this.highlightsHandle, PhotoFilterView.this.getHighlightsValue());
                GLES20.glUniform1f(this.exposureHandle, PhotoFilterView.this.getExposureValue());
                GLES20.glUniform1f(this.contrastHandle, PhotoFilterView.this.getContrastValue());
                GLES20.glUniform1f(this.saturationHandle, PhotoFilterView.this.getSaturationValue());
                GLES20.glUniform1f(this.warmthHandle, PhotoFilterView.this.getWarmthValue());
                GLES20.glUniform1f(this.vignetteHandle, PhotoFilterView.this.getVignetteValue());
                GLES20.glUniform1f(this.grainHandle, PhotoFilterView.this.getGrainValue());
                GLES20.glUniform1f(this.fadeAmountHandle, PhotoFilterView.this.getFadeValue());
                GLES20.glUniform3f(this.highlightsTintColorHandle, ((float) ((PhotoFilterView.this.tintHighlightsColor >> 16) & NalUnitUtil.EXTENDED_SAR)) / 255.0f, ((float) ((PhotoFilterView.this.tintHighlightsColor >> 8) & NalUnitUtil.EXTENDED_SAR)) / 255.0f, ((float) (PhotoFilterView.this.tintHighlightsColor & NalUnitUtil.EXTENDED_SAR)) / 255.0f);
                GLES20.glUniform1f(this.highlightsTintIntensityHandle, PhotoFilterView.this.getTintHighlightsIntensityValue());
                GLES20.glUniform3f(this.shadowsTintColorHandle, ((float) ((PhotoFilterView.this.tintShadowsColor >> 16) & NalUnitUtil.EXTENDED_SAR)) / 255.0f, ((float) ((PhotoFilterView.this.tintShadowsColor >> 8) & NalUnitUtil.EXTENDED_SAR)) / 255.0f, ((float) (PhotoFilterView.this.tintShadowsColor & NalUnitUtil.EXTENDED_SAR)) / 255.0f);
                GLES20.glUniform1f(this.shadowsTintIntensityHandle, PhotoFilterView.this.getTintShadowsIntensityValue());
                boolean shouldBeSkipped = PhotoFilterView.this.curvesToolValue.shouldBeSkipped();
                int i = this.skipToneHandle;
                if (!shouldBeSkipped) {
                    f = 0.0f;
                }
                GLES20.glUniform1f(i, f);
                if (!shouldBeSkipped) {
                    PhotoFilterView.this.curvesToolValue.fillBuffer();
                    GLES20.glActiveTexture(33985);
                    GLES20.glBindTexture(3553, this.curveTextures[0]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLES20.glTexImage2D(3553, 0, 6408, Callback.DEFAULT_DRAG_ANIMATION_DURATION, 1, 0, 6408, 5121, PhotoFilterView.this.curvesToolValue.curveBuffer);
                    GLES20.glUniform1i(this.curvesImageHandle, 1);
                }
            }
            GLES20.glUniform1f(this.widthHandle, (float) this.renderBufferWidth);
            GLES20.glUniform1f(this.heightHandle, (float) this.renderBufferHeight);
            GLES20.glEnableVertexAttribArray(this.inputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.inputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.positionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glDrawArrays(5, 0, PGPhotoEnhanceSegments);
        }

        private void drawEnhancePass() {
            if (!this.hsvGenerated) {
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
                GLES20.glClear(0);
                GLES20.glUseProgram(this.rgbToHsvShaderProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[1]);
                GLES20.glUniform1i(this.rgbToHsvSourceImageHandle, 0);
                GLES20.glEnableVertexAttribArray(this.rgbToHsvInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.rgbToHsvInputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.rgbToHsvPositionHandle);
                GLES20.glVertexAttribPointer(this.rgbToHsvPositionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.vertexBuffer);
                GLES20.glDrawArrays(5, 0, PGPhotoEnhanceSegments);
                Buffer allocateDirect = ByteBuffer.allocateDirect((this.renderBufferWidth * this.renderBufferHeight) * PGPhotoEnhanceSegments);
                GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, allocateDirect);
                GLES20.glBindTexture(3553, this.enhanceTextures[0]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, allocateDirect);
                Buffer buffer = null;
                try {
                    buffer = ByteBuffer.allocateDirect(MessagesController.UPDATE_MASK_CHAT_ADMINS);
                    Utilities.calcCDT(allocateDirect, this.renderBufferWidth, this.renderBufferHeight, buffer);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                GLES20.glBindTexture(3553, this.enhanceTextures[1]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLES20.glTexImage2D(3553, 0, 6408, PGPhotoEnhanceHistogramBins, 16, 0, 6408, 5121, buffer);
                this.hsvGenerated = true;
            }
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.enhanceShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.enhanceTextures[0]);
            GLES20.glUniform1i(this.enhanceSourceImageHandle, 0);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, this.enhanceTextures[1]);
            GLES20.glUniform1i(this.enhanceInputImageTexture2Handle, 1);
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.enhanceIntensityHandle, 0.0f);
            } else {
                GLES20.glUniform1f(this.enhanceIntensityHandle, PhotoFilterView.this.getEnhanceValue());
            }
            GLES20.glEnableVertexAttribArray(this.enhanceInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.enhanceInputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.enhancePositionHandle);
            GLES20.glVertexAttribPointer(this.enhancePositionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.vertexBuffer);
            GLES20.glDrawArrays(5, 0, PGPhotoEnhanceSegments);
        }

        private void drawSharpenPass() {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.sharpenShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glUniform1i(this.sharpenSourceImageHandle, 0);
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.sharpenHandle, 0.0f);
            } else {
                GLES20.glUniform1f(this.sharpenHandle, PhotoFilterView.this.getSharpenValue());
            }
            GLES20.glUniform1f(this.sharpenWidthHandle, (float) this.renderBufferWidth);
            GLES20.glUniform1f(this.sharpenHeightHandle, (float) this.renderBufferHeight);
            GLES20.glEnableVertexAttribArray(this.sharpenInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.sharpenInputTexCoordHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.sharpenPositionHandle);
            GLES20.glVertexAttribPointer(this.sharpenPositionHandle, PhotoFilterView.curveDataStep, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glDrawArrays(5, 0, PGPhotoEnhanceSegments);
        }

        private Bitmap getRenderBufferBitmap() {
            Buffer allocateDirect = ByteBuffer.allocateDirect((this.renderBufferWidth * this.renderBufferHeight) * PGPhotoEnhanceSegments);
            GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, allocateDirect);
            Bitmap createBitmap = Bitmap.createBitmap(this.renderBufferWidth, this.renderBufferHeight, Config.ARGB_8888);
            createBitmap.copyPixelsFromBuffer(allocateDirect);
            return createBitmap;
        }

        private boolean initGL() {
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                FileLog.m16e("tmessages", "eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(this.eglDisplay, new int[PhotoFilterView.curveDataStep])) {
                int[] iArr = new int[1];
                EGLConfig[] eGLConfigArr = new EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, PGPhotoEnhanceSegments, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                    FileLog.m16e("tmessages", "eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    finish();
                    return false;
                } else if (iArr[0] > 0) {
                    this.eglConfig = eGLConfigArr[0];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, PhotoFilterView.curveDataStep, 12344});
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
                            this.gl = this.eglContext.getGL();
                            float[] fArr = new float[]{Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Face.UNCOMPUTED_PROBABILITY};
                            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * PGPhotoEnhanceSegments);
                            allocateDirect.order(ByteOrder.nativeOrder());
                            this.vertexBuffer = allocateDirect.asFloatBuffer();
                            this.vertexBuffer.put(fArr);
                            this.vertexBuffer.position(0);
                            fArr = new float[]{Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Face.UNCOMPUTED_PROBABILITY, Face.UNCOMPUTED_PROBABILITY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT};
                            allocateDirect = ByteBuffer.allocateDirect(fArr.length * PGPhotoEnhanceSegments);
                            allocateDirect.order(ByteOrder.nativeOrder());
                            this.vertexInvertBuffer = allocateDirect.asFloatBuffer();
                            this.vertexInvertBuffer.put(fArr);
                            this.vertexInvertBuffer.position(0);
                            fArr = new float[]{0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT};
                            allocateDirect = ByteBuffer.allocateDirect(fArr.length * PGPhotoEnhanceSegments);
                            allocateDirect.order(ByteOrder.nativeOrder());
                            this.textureBuffer = allocateDirect.asFloatBuffer();
                            this.textureBuffer.put(fArr);
                            this.textureBuffer.position(0);
                            GLES20.glGenTextures(1, this.curveTextures, 0);
                            GLES20.glGenTextures(PhotoFilterView.curveDataStep, this.enhanceTextures, 0);
                            int loadShader = loadShader(35633, simpleVertexShaderCode);
                            int loadShader2 = loadShader(35632, toolsFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.toolsShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.toolsShaderProgram, loadShader);
                            GLES20.glAttachShader(this.toolsShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.toolsShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.toolsShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.toolsShaderProgram);
                            int[] iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.toolsShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.toolsShaderProgram);
                                this.toolsShaderProgram = 0;
                            } else {
                                this.positionHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "position");
                                this.inputTexCoordHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "inputTexCoord");
                                this.sourceImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "sourceImage");
                                this.shadowsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadows");
                                this.highlightsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlights");
                                this.exposureHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "exposure");
                                this.contrastHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "contrast");
                                this.saturationHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "saturation");
                                this.warmthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "warmth");
                                this.vignetteHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "vignette");
                                this.grainHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "grain");
                                this.widthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "width");
                                this.heightHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "height");
                                this.curvesImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "curvesImage");
                                this.skipToneHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "skipTone");
                                this.fadeAmountHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "fadeAmount");
                                this.shadowsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintIntensity");
                                this.highlightsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintIntensity");
                                this.shadowsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintColor");
                                this.highlightsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintColor");
                            }
                            loadShader = loadShader(35633, sharpenVertexShaderCode);
                            loadShader2 = loadShader(35632, sharpenFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.sharpenShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.sharpenShaderProgram, loadShader);
                            GLES20.glAttachShader(this.sharpenShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.sharpenShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.sharpenShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.sharpenShaderProgram);
                            iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.sharpenShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.sharpenShaderProgram);
                                this.sharpenShaderProgram = 0;
                            } else {
                                this.sharpenPositionHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "position");
                                this.sharpenInputTexCoordHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "inputTexCoord");
                                this.sharpenSourceImageHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sourceImage");
                                this.sharpenWidthHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputWidth");
                                this.sharpenHeightHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputHeight");
                                this.sharpenHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sharpen");
                            }
                            loadShader = loadShader(35633, blurVertexShaderCode);
                            loadShader2 = loadShader(35632, blurFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.blurShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.blurShaderProgram, loadShader);
                            GLES20.glAttachShader(this.blurShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.blurShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.blurShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.blurShaderProgram);
                            iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.blurShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.blurShaderProgram);
                                this.blurShaderProgram = 0;
                            } else {
                                this.blurPositionHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "position");
                                this.blurInputTexCoordHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "inputTexCoord");
                                this.blurSourceImageHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "sourceImage");
                                this.blurWidthHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelWidthOffset");
                                this.blurHeightHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelHeightOffset");
                            }
                            loadShader = loadShader(35633, simpleVertexShaderCode);
                            loadShader2 = loadShader(35632, linearBlurFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.linearBlurShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.linearBlurShaderProgram, loadShader);
                            GLES20.glAttachShader(this.linearBlurShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.linearBlurShaderProgram);
                            iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.linearBlurShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.linearBlurShaderProgram);
                                this.linearBlurShaderProgram = 0;
                            } else {
                                this.linearBlurPositionHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "position");
                                this.linearBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "inputTexCoord");
                                this.linearBlurSourceImageHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "sourceImage");
                                this.linearBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "inputImageTexture2");
                                this.linearBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeSize");
                                this.linearBlurExcludePointHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludePoint");
                                this.linearBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeBlurSize");
                                this.linearBlurAngleHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "angle");
                                this.linearBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "aspectRatio");
                            }
                            loadShader = loadShader(35633, simpleVertexShaderCode);
                            loadShader2 = loadShader(35632, radialBlurFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.radialBlurShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.radialBlurShaderProgram, loadShader);
                            GLES20.glAttachShader(this.radialBlurShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.radialBlurShaderProgram);
                            iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.radialBlurShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.radialBlurShaderProgram);
                                this.radialBlurShaderProgram = 0;
                            } else {
                                this.radialBlurPositionHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "position");
                                this.radialBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "inputTexCoord");
                                this.radialBlurSourceImageHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "sourceImage");
                                this.radialBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "inputImageTexture2");
                                this.radialBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeSize");
                                this.radialBlurExcludePointHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludePoint");
                                this.radialBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeBlurSize");
                                this.radialBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "aspectRatio");
                            }
                            loadShader = loadShader(35633, simpleVertexShaderCode);
                            loadShader2 = loadShader(35632, rgbToHsvFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.rgbToHsvShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.rgbToHsvShaderProgram, loadShader);
                            GLES20.glAttachShader(this.rgbToHsvShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.rgbToHsvShaderProgram);
                            iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.rgbToHsvShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.rgbToHsvShaderProgram);
                                this.rgbToHsvShaderProgram = 0;
                            } else {
                                this.rgbToHsvPositionHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "position");
                                this.rgbToHsvInputTexCoordHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "inputTexCoord");
                                this.rgbToHsvSourceImageHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram, "sourceImage");
                            }
                            loadShader = loadShader(35633, simpleVertexShaderCode);
                            loadShader2 = loadShader(35632, enhanceFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.enhanceShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.enhanceShaderProgram, loadShader);
                            GLES20.glAttachShader(this.enhanceShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.enhanceShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.enhanceShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.enhanceShaderProgram);
                            iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.enhanceShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.enhanceShaderProgram);
                                this.enhanceShaderProgram = 0;
                            } else {
                                this.enhancePositionHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "position");
                                this.enhanceInputTexCoordHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "inputTexCoord");
                                this.enhanceSourceImageHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "sourceImage");
                                this.enhanceIntensityHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "intensity");
                                this.enhanceInputImageTexture2Handle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "inputImageTexture2");
                            }
                            loadShader = loadShader(35633, simpleVertexShaderCode);
                            loadShader2 = loadShader(35632, simpleFragmentShaderCode);
                            if (loadShader == 0 || loadShader2 == 0) {
                                finish();
                                return false;
                            }
                            this.simpleShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.simpleShaderProgram, loadShader);
                            GLES20.glAttachShader(this.simpleShaderProgram, loadShader2);
                            GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.simpleShaderProgram);
                            iArr2 = new int[1];
                            GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, iArr2, 0);
                            if (iArr2[0] == 0) {
                                GLES20.glDeleteProgram(this.simpleShaderProgram);
                                this.simpleShaderProgram = 0;
                            } else {
                                this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                                this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                                this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                            }
                            if (this.currentBitmap == null) {
                                return true;
                            }
                            loadTexture(this.currentBitmap);
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

        private int loadShader(int i, String str) {
            int glCreateShader = GLES20.glCreateShader(i);
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] != 0) {
                return glCreateShader;
            }
            FileLog.m16e("tmessages", GLES20.glGetShaderInfoLog(glCreateShader));
            GLES20.glDeleteShader(glCreateShader);
            return 0;
        }

        private void loadTexture(Bitmap bitmap) {
            this.renderBufferWidth = bitmap.getWidth();
            this.renderBufferHeight = bitmap.getHeight();
            float photoSize = (float) AndroidUtilities.getPhotoSize();
            if (((float) this.renderBufferWidth) > photoSize || ((float) this.renderBufferHeight) > photoSize || PhotoFilterView.this.orientation % 360 != 0) {
                float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                if (((float) this.renderBufferWidth) > photoSize || ((float) this.renderBufferHeight) > photoSize) {
                    f = photoSize / ((float) bitmap.getWidth());
                    float height = photoSize / ((float) bitmap.getHeight());
                    if (f < height) {
                        this.renderBufferWidth = (int) photoSize;
                        this.renderBufferHeight = (int) (((float) bitmap.getHeight()) * f);
                    } else {
                        this.renderBufferHeight = (int) photoSize;
                        this.renderBufferWidth = (int) (((float) bitmap.getWidth()) * height);
                        f = height;
                    }
                }
                if (PhotoFilterView.this.orientation % 360 == 90 || PhotoFilterView.this.orientation % 360 == 270) {
                    int i = this.renderBufferWidth;
                    this.renderBufferWidth = this.renderBufferHeight;
                    this.renderBufferHeight = i;
                }
                this.currentBitmap = createBitmap(bitmap, this.renderBufferWidth, this.renderBufferHeight, f);
            }
            GLES20.glGenFramebuffers(3, this.renderFrameBuffer, 0);
            GLES20.glGenTextures(3, this.renderTexture, 0);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, null);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, this.currentBitmap, 0);
            GLES20.glBindTexture(3553, this.renderTexture[PhotoFilterView.curveDataStep]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, Task.EXTRAS_LIMIT_BYTES, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, null);
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
            if (!this.initied) {
                return null;
            }
            Semaphore semaphore = new Semaphore(0);
            Bitmap[] bitmapArr = new Bitmap[1];
            try {
                postRunnable(new C14162(bitmapArr, semaphore));
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            return bitmapArr[0];
        }

        public void requestRender(boolean z) {
            requestRender(z, false);
        }

        public void requestRender(boolean z, boolean z2) {
            postRunnable(new C14184(z, z2));
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void setSurfaceTextureSize(int i, int i2) {
            this.surfaceWidth = i;
            this.surfaceHeight = i2;
        }

        public void shutdown() {
            postRunnable(new C14173());
        }
    }

    public class ToolsAdapter extends Adapter {
        private Context mContext;

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public ToolsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return 14;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Holder holder = (Holder) viewHolder;
            if (i == PhotoFilterView.this.enhanceTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_enhance, LocaleController.getString("Enhance", C0338R.string.Enhance), PhotoFilterView.this.enhanceValue);
            } else if (i == PhotoFilterView.this.highlightsTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_highlights, LocaleController.getString("Highlights", C0338R.string.Highlights), PhotoFilterView.this.highlightsValue);
            } else if (i == PhotoFilterView.this.contrastTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_contrast, LocaleController.getString("Contrast", C0338R.string.Contrast), PhotoFilterView.this.contrastValue);
            } else if (i == PhotoFilterView.this.exposureTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_brightness, LocaleController.getString("Exposure", C0338R.string.Exposure), PhotoFilterView.this.exposureValue);
            } else if (i == PhotoFilterView.this.warmthTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_warmth, LocaleController.getString("Warmth", C0338R.string.Warmth), PhotoFilterView.this.warmthValue);
            } else if (i == PhotoFilterView.this.saturationTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_saturation, LocaleController.getString("Saturation", C0338R.string.Saturation), PhotoFilterView.this.saturationValue);
            } else if (i == PhotoFilterView.this.vignetteTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_vignette, LocaleController.getString("Vignette", C0338R.string.Vignette), PhotoFilterView.this.vignetteValue);
            } else if (i == PhotoFilterView.this.shadowsTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_shadows, LocaleController.getString("Shadows", C0338R.string.Shadows), PhotoFilterView.this.shadowsValue);
            } else if (i == PhotoFilterView.this.grainTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_grain, LocaleController.getString("Grain", C0338R.string.Grain), PhotoFilterView.this.grainValue);
            } else if (i == PhotoFilterView.this.sharpenTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_details, LocaleController.getString("Sharpen", C0338R.string.Sharpen), PhotoFilterView.this.sharpenValue);
            } else if (i == PhotoFilterView.this.tintTool) {
                PhotoEditToolCell photoEditToolCell = (PhotoEditToolCell) holder.itemView;
                String string = LocaleController.getString("Tint", C0338R.string.Tint);
                r1 = (PhotoFilterView.this.tintHighlightsColor == 0 && PhotoFilterView.this.tintShadowsColor == 0) ? TtmlNode.ANONYMOUS_REGION_ID : "\u25c6";
                photoEditToolCell.setIconAndTextAndValue((int) C0338R.drawable.tool_tint, string, r1);
            } else if (i == PhotoFilterView.this.fadeTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_fade, LocaleController.getString("Fade", C0338R.string.Fade), PhotoFilterView.this.fadeValue);
            } else if (i == PhotoFilterView.this.curvesTool) {
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_curve, LocaleController.getString("Curves", C0338R.string.Curves), PhotoFilterView.this.curvesToolValue.shouldBeSkipped() ? TtmlNode.ANONYMOUS_REGION_ID : "\u25c6");
            } else if (i == PhotoFilterView.this.blurTool) {
                r1 = PhotoFilterView.this.blurType == 1 ? "R" : PhotoFilterView.this.blurType == PhotoFilterView.curveDataStep ? "L" : TtmlNode.ANONYMOUS_REGION_ID;
                ((PhotoEditToolCell) holder.itemView).setIconAndTextAndValue((int) C0338R.drawable.tool_blur, LocaleController.getString("Blur", C0338R.string.Blur), r1);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new PhotoEditToolCell(this.mContext));
        }
    }

    public PhotoFilterView(Context context, Bitmap bitmap, int i) {
        super(context);
        this.tintShadowColors = new int[]{0, -45747, -753630, -13056, -8269183, -9321002, -16747844, -10080879};
        this.tintHighlighsColors = new int[]{0, -1076602, -1388894, -859780, -5968466, -7742235, -13726776, -3303195};
        this.selectedTool = -1;
        this.enhanceTool = 0;
        this.exposureTool = 1;
        this.contrastTool = curveDataStep;
        this.warmthTool = 3;
        this.saturationTool = 4;
        this.tintTool = 5;
        this.fadeTool = 6;
        this.highlightsTool = 7;
        this.shadowsTool = 8;
        this.vignetteTool = 9;
        this.grainTool = 10;
        this.blurTool = 11;
        this.sharpenTool = 12;
        this.curvesTool = 13;
        this.enhanceValue = 0.0f;
        this.exposureValue = 0.0f;
        this.contrastValue = 0.0f;
        this.warmthValue = 0.0f;
        this.saturationValue = 0.0f;
        this.fadeValue = 0.0f;
        this.tintShadowsColor = 0;
        this.tintHighlightsColor = 0;
        this.highlightsValue = 0.0f;
        this.shadowsValue = 0.0f;
        this.vignetteValue = 0.0f;
        this.grainValue = 0.0f;
        this.blurType = 0;
        this.sharpenValue = 0.0f;
        this.curvesToolValue = new CurvesToolValue();
        this.blurExcludeSize = 0.35f;
        this.blurExcludePoint = new Point(0.5f, 0.5f);
        this.blurExcludeBlurSize = 0.15f;
        this.blurAngle = 1.5707964f;
        this.curveTextView = new TextView[4];
        this.bitmapToEdit = bitmap;
        this.orientation = i;
        this.textureView = new TextureView(context);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1, 51));
        this.textureView.setVisibility(4);
        this.textureView.setSurfaceTextureListener(new C14061());
        this.blurControl = new PhotoFilterBlurControl(context);
        this.blurControl.setVisibility(4);
        addView(this.blurControl, LayoutHelper.createFrame(-1, -1, 51));
        this.blurControl.setDelegate(new C14072());
        this.curvesControl = new PhotoFilterCurvesControl(context, this.curvesToolValue);
        this.curvesControl.setDelegate(new C14083());
        this.curvesControl.setVisibility(4);
        addView(this.curvesControl, LayoutHelper.createFrame(-1, -1, 51));
        this.toolsView = new FrameLayout(context);
        addView(this.toolsView, LayoutHelper.createFrame(-1, 126, 83));
        View frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        this.toolsView.addView(frameLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.cancelTextView = new TextView(context);
        this.cancelTextView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", C0338R.string.Cancel).toUpperCase());
        this.cancelTextView.setTypeface(FontUtil.m1176a().m1160c());
        frameLayout.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        this.doneTextView = new TextView(context);
        this.doneTextView.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(-11420173);
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", C0338R.string.Done).toUpperCase());
        this.doneTextView.setTypeface(FontUtil.m1176a().m1160c());
        frameLayout.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        this.recyclerListView = new RecyclerListView(context);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(0);
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.setTag(Integer.valueOf(12));
        this.recyclerListView.setOverScrollMode(curveDataStep);
        RecyclerListView recyclerListView = this.recyclerListView;
        Adapter toolsAdapter = new ToolsAdapter(context);
        this.toolsAdapter = toolsAdapter;
        recyclerListView.setAdapter(toolsAdapter);
        this.toolsView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, 60, 51));
        this.recyclerListView.setOnItemClickListener(new C14094());
        this.editView = new FrameLayout(context);
        this.editView.setVisibility(8);
        addView(this.editView, LayoutHelper.createFrame(-1, 126, 83));
        View frameLayout2 = new FrameLayout(context);
        frameLayout2.setBackgroundColor(-15066598);
        this.editView.addView(frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        frameLayout = new ImageView(context);
        frameLayout.setImageResource(C0338R.drawable.edit_cancel);
        frameLayout.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false));
        frameLayout.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        frameLayout2.addView(frameLayout, LayoutHelper.createFrame(-2, -1, 51));
        frameLayout.setOnClickListener(new C14105());
        frameLayout = new ImageView(context);
        frameLayout.setImageResource(C0338R.drawable.edit_doneblue);
        frameLayout.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false));
        frameLayout.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), AndroidUtilities.dp(22.0f), 0);
        frameLayout2.addView(frameLayout, LayoutHelper.createFrame(-2, -1, 53));
        frameLayout.setOnClickListener(new C14116());
        this.infoTextView = new TextView(context);
        this.infoTextView.setTextSize(1, 20.0f);
        this.infoTextView.setTextColor(-1);
        frameLayout2.addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 9.0f, 0.0f, 0.0f));
        this.paramTextView = new TextView(context);
        this.paramTextView.setTextSize(1, 12.0f);
        this.paramTextView.setTextColor(-8355712);
        frameLayout2.addView(this.paramTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 26.0f, 0.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextSize(1, 20.0f);
        this.valueTextView.setTextColor(-1);
        frameLayout2.addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 3.0f, 0.0f, 0.0f));
        this.valueSeekBar = new PhotoEditorSeekBar(context);
        this.valueSeekBar.setDelegate(new C14127());
        this.editView.addView(this.valueSeekBar, LayoutHelper.createFrame(AndroidUtilities.isTablet() ? 498 : -1, BitmapDescriptorFactory.HUE_YELLOW, AndroidUtilities.isTablet() ? 49 : 51, 14.0f, 10.0f, 14.0f, 0.0f));
        this.curveLayout = new FrameLayout(context);
        this.editView.addView(this.curveLayout, LayoutHelper.createFrame(-1, 78, 1));
        frameLayout2 = new LinearLayout(context);
        frameLayout2.setOrientation(0);
        this.curveLayout.addView(frameLayout2, LayoutHelper.createFrame(-2, 28, 1));
        int i2 = 0;
        while (i2 < 4) {
            this.curveTextView[i2] = new TextView(context);
            this.curveTextView[i2].setTextSize(1, 14.0f);
            this.curveTextView[i2].setGravity(16);
            this.curveTextView[i2].setTag(Integer.valueOf(i2));
            if (i2 == 0) {
                this.curveTextView[i2].setText(LocaleController.getString("CurvesAll", C0338R.string.CurvesAll).toUpperCase());
            } else if (i2 == 1) {
                this.curveTextView[i2].setText(LocaleController.getString("CurvesRed", C0338R.string.CurvesRed).toUpperCase());
            } else if (i2 == curveDataStep) {
                this.curveTextView[i2].setText(LocaleController.getString("CurvesGreen", C0338R.string.CurvesGreen).toUpperCase());
            } else if (i2 == 3) {
                this.curveTextView[i2].setText(LocaleController.getString("CurvesBlue", C0338R.string.CurvesBlue).toUpperCase());
            }
            this.curveTextView[i2].setTypeface(FontUtil.m1176a().m1160c());
            frameLayout2.addView(this.curveTextView[i2], LayoutHelper.createLinear(-2, 28, i2 == 0 ? 0.0f : BitmapDescriptorFactory.HUE_ORANGE, 0.0f, 0.0f, 0.0f));
            this.curveTextView[i2].setOnClickListener(new C14138());
            i2++;
        }
        this.tintLayout = new FrameLayout(context);
        this.editView.addView(this.tintLayout, LayoutHelper.createFrame(-1, 78, 1));
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        this.tintLayout.addView(linearLayout, LayoutHelper.createFrame(-2, 28, 1));
        this.tintShadowsButton = new TextView(context);
        this.tintShadowsButton.setTextSize(1, 14.0f);
        this.tintShadowsButton.setGravity(16);
        this.tintShadowsButton.setText(LocaleController.getString("TintShadows", C0338R.string.TintShadows).toUpperCase());
        this.tintShadowsButton.setTypeface(FontUtil.m1176a().m1160c());
        linearLayout.addView(this.tintShadowsButton, LayoutHelper.createLinear(-2, 28));
        this.tintShadowsButton.setOnClickListener(new C14149());
        this.tintHighlightsButton = new TextView(context);
        this.tintHighlightsButton.setTextSize(1, 14.0f);
        this.tintHighlightsButton.setGravity(16);
        this.tintHighlightsButton.setText(LocaleController.getString("TintHighlights", C0338R.string.TintHighlights).toUpperCase());
        this.tintHighlightsButton.setTypeface(FontUtil.m1176a().m1160c());
        linearLayout.addView(this.tintHighlightsButton, LayoutHelper.createLinear(-2, 28, 100.0f, 0.0f, 0.0f, 0.0f));
        this.tintHighlightsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoFilterView.this.selectedTintMode = 1;
                PhotoFilterView.this.updateSelectedTintButton(true);
            }
        });
        this.tintButtonsContainer = new LinearLayout(context);
        this.tintButtonsContainer.setOrientation(0);
        this.tintButtonsContainer.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.tintLayout.addView(this.tintButtonsContainer, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 24.0f, 0.0f, 0.0f));
        for (int i3 = 0; i3 < this.tintShadowColors.length; i3++) {
            View radioButton = new RadioButton(context);
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            radioButton.setTag(Integer.valueOf(i3));
            this.tintButtonsContainer.addView(radioButton, LayoutHelper.createLinear(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) this.tintShadowColors.length)));
            radioButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RadioButton radioButton = (RadioButton) view;
                    if (PhotoFilterView.this.selectedTintMode == 0) {
                        PhotoFilterView.this.tintShadowsColor = PhotoFilterView.this.tintShadowColors[((Integer) radioButton.getTag()).intValue()];
                    } else {
                        PhotoFilterView.this.tintHighlightsColor = PhotoFilterView.this.tintHighlighsColors[((Integer) radioButton.getTag()).intValue()];
                    }
                    PhotoFilterView.this.updateSelectedTintButton(true);
                    if (PhotoFilterView.this.eglThread != null) {
                        PhotoFilterView.this.eglThread.requestRender(false);
                    }
                }
            });
        }
        this.blurLayout = new FrameLayout(context);
        this.editView.addView(this.blurLayout, LayoutHelper.createFrame(280, BitmapDescriptorFactory.HUE_YELLOW, 1, 0.0f, 10.0f, 0.0f, 0.0f));
        this.blurOffButton = new TextView(context);
        this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_off_active, 0, 0);
        this.blurOffButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurOffButton.setTextSize(1, 13.0f);
        this.blurOffButton.setTextColor(-11420173);
        this.blurOffButton.setGravity(1);
        this.blurOffButton.setText(LocaleController.getString("BlurOff", C0338R.string.BlurOff));
        this.blurLayout.addView(this.blurOffButton, LayoutHelper.createFrame(80, BitmapDescriptorFactory.HUE_YELLOW));
        this.blurOffButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoFilterView.this.blurType = 0;
                PhotoFilterView.this.updateSelectedBlurType();
                PhotoFilterView.this.blurControl.setVisibility(4);
                if (PhotoFilterView.this.eglThread != null) {
                    PhotoFilterView.this.eglThread.requestRender(false);
                }
            }
        });
        this.blurRadialButton = new TextView(context);
        this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_radial, 0, 0);
        this.blurRadialButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurRadialButton.setTextSize(1, 13.0f);
        this.blurRadialButton.setTextColor(-1);
        this.blurRadialButton.setGravity(1);
        this.blurRadialButton.setText(LocaleController.getString("BlurRadial", C0338R.string.BlurRadial));
        this.blurLayout.addView(this.blurRadialButton, LayoutHelper.createFrame(80, 80.0f, 51, 100.0f, 0.0f, 0.0f, 0.0f));
        this.blurRadialButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoFilterView.this.blurType = 1;
                PhotoFilterView.this.updateSelectedBlurType();
                PhotoFilterView.this.blurControl.setVisibility(0);
                PhotoFilterView.this.blurControl.setType(1);
                if (PhotoFilterView.this.eglThread != null) {
                    PhotoFilterView.this.eglThread.requestRender(false);
                }
            }
        });
        this.blurLinearButton = new TextView(context);
        this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_linear, 0, 0);
        this.blurLinearButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurLinearButton.setTextSize(1, 13.0f);
        this.blurLinearButton.setTextColor(-1);
        this.blurLinearButton.setGravity(1);
        this.blurLinearButton.setText(LocaleController.getString("BlurLinear", C0338R.string.BlurLinear));
        this.blurLayout.addView(this.blurLinearButton, LayoutHelper.createFrame(80, 80.0f, 51, 200.0f, 0.0f, 0.0f, 0.0f));
        this.blurLinearButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoFilterView.this.blurType = PhotoFilterView.curveDataStep;
                PhotoFilterView.this.updateSelectedBlurType();
                PhotoFilterView.this.blurControl.setVisibility(0);
                PhotoFilterView.this.blurControl.setType(0);
                if (PhotoFilterView.this.eglThread != null) {
                    PhotoFilterView.this.eglThread.requestRender(false);
                }
            }
        });
        if (VERSION.SDK_INT >= 21) {
            ((LayoutParams) this.textureView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
            ((LayoutParams) this.curvesControl.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
        }
    }

    private void checkEnhance() {
        if (this.enhanceValue == 0.0f) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200);
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofInt(this.valueSeekBar, NotificationCompatApi24.CATEGORY_PROGRESS, new int[]{50});
            animatorSet.playTogether(animatorArr);
            animatorSet.start();
        }
    }

    private void fixLayout(int i, int i2) {
        if (this.bitmapToEdit != null) {
            float height;
            float width;
            int dp = i - AndroidUtilities.dp(28.0f);
            int dp2 = i2 - (AndroidUtilities.dp(154.0f) + AndroidUtilities.statusBarHeight);
            if (this.orientation % 360 == 90 || this.orientation % 360 == 270) {
                height = (float) this.bitmapToEdit.getHeight();
                width = (float) this.bitmapToEdit.getWidth();
            } else {
                height = (float) this.bitmapToEdit.getWidth();
                width = (float) this.bitmapToEdit.getHeight();
            }
            float f = ((float) dp) / height;
            float f2 = ((float) dp2) / width;
            if (f > f2) {
                f = (float) ((int) Math.ceil((double) (height * f2)));
                height = (float) dp2;
            } else {
                f = (float) dp;
                height = (float) ((int) Math.ceil((double) (width * f)));
            }
            int ceil = (int) Math.ceil((double) (((((float) dp) - f) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))));
            int ceil2 = (int) Math.ceil((double) ((((((float) dp2) - height) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))) + ((float) AndroidUtilities.statusBarHeight)));
            LayoutParams layoutParams = (LayoutParams) this.textureView.getLayoutParams();
            layoutParams.leftMargin = ceil;
            layoutParams.topMargin = ceil2;
            layoutParams.width = (int) f;
            layoutParams.height = (int) height;
            this.curvesControl.setActualArea((float) ceil, (float) ceil2, (float) layoutParams.width, (float) layoutParams.height);
            this.blurControl.setActualAreaSize((float) layoutParams.width, (float) layoutParams.height);
            ((LayoutParams) this.blurControl.getLayoutParams()).height = AndroidUtilities.dp(28.0f) + dp2;
            ((LayoutParams) this.curvesControl.getLayoutParams()).height = AndroidUtilities.dp(28.0f) + dp2;
            if (AndroidUtilities.isTablet()) {
                int dp3 = AndroidUtilities.dp(86.0f) * 10;
                layoutParams = (LayoutParams) this.recyclerListView.getLayoutParams();
                if (dp3 < dp) {
                    layoutParams.width = dp3;
                    layoutParams.leftMargin = (dp - dp3) / curveDataStep;
                    return;
                }
                layoutParams.width = -1;
                layoutParams.leftMargin = 0;
            }
        }
    }

    private float getContrastValue() {
        return ((this.contrastValue / 100.0f) * 0.3f) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    }

    private float getEnhanceValue() {
        return this.enhanceValue / 100.0f;
    }

    private float getExposureValue() {
        return this.exposureValue / 100.0f;
    }

    private float getFadeValue() {
        return this.fadeValue / 100.0f;
    }

    private float getGrainValue() {
        return (this.grainValue / 100.0f) * 0.04f;
    }

    private float getHighlightsValue() {
        return ((this.highlightsValue * AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION) + 100.0f) / 100.0f;
    }

    private float getSaturationValue() {
        float f = this.saturationValue / 100.0f;
        if (f > 0.0f) {
            f *= 1.05f;
        }
        return f + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    }

    private float getShadowsValue() {
        return ((this.shadowsValue * 0.55f) + 100.0f) / 100.0f;
    }

    private float getSharpenValue() {
        return 0.11f + ((this.sharpenValue / 100.0f) * 0.6f);
    }

    private float getTintHighlightsIntensityValue() {
        return this.tintHighlightsColor == 0 ? 0.0f : 50.0f / 100.0f;
    }

    private float getTintShadowsIntensityValue() {
        return this.tintShadowsColor == 0 ? 0.0f : 50.0f / 100.0f;
    }

    private float getVignetteValue() {
        return this.vignetteValue / 100.0f;
    }

    private float getWarmthValue() {
        return this.warmthValue / 100.0f;
    }

    private void setShowOriginal(boolean z) {
        if (this.showOriginal != z) {
            this.showOriginal = z;
            if (this.eglThread != null) {
                this.eglThread.requestRender(false);
            }
        }
    }

    private void updateSelectedBlurType() {
        if (this.blurType == 0) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_off_active, 0, 0);
            this.blurOffButton.setTextColor(-11420173);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_radial, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_linear, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (this.blurType == 1) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_off, 0, 0);
            this.blurOffButton.setTextColor(-1);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_radial_active, 0, 0);
            this.blurRadialButton.setTextColor(-11420173);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_linear, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (this.blurType == curveDataStep) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_off, 0, 0);
            this.blurOffButton.setTextColor(-1);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_radial, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, C0338R.drawable.blur_linear_active, 0, 0);
            this.blurLinearButton.setTextColor(-11420173);
        }
    }

    private void updateSelectedTintButton(boolean z) {
        int i = -8355712;
        this.tintHighlightsButton.setTextColor(this.selectedTintMode == 1 ? -1 : -8355712);
        TextView textView = this.tintShadowsButton;
        if (this.selectedTintMode != 1) {
            i = -1;
        }
        textView.setTextColor(i);
        int childCount = this.tintButtonsContainer.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.tintButtonsContainer.getChildAt(i2);
            if (childAt instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) childAt;
                int intValue = ((Integer) radioButton.getTag()).intValue();
                radioButton.setChecked((this.selectedTintMode == 0 ? this.tintShadowsColor : this.tintHighlightsColor) == (this.selectedTintMode == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue]), z);
                int i3 = intValue == 0 ? -1 : this.selectedTintMode == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue];
                i = intValue == 0 ? -1 : this.selectedTintMode == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue];
                radioButton.setColor(i3, i);
            }
        }
    }

    private void updateValueTextView() {
        int i = 0;
        if (this.selectedTool == this.enhanceTool) {
            i = (int) this.enhanceValue;
        } else if (this.selectedTool == this.highlightsTool) {
            i = (int) this.highlightsValue;
        } else if (this.selectedTool == this.contrastTool) {
            i = (int) this.contrastValue;
        } else if (this.selectedTool == this.exposureTool) {
            i = (int) this.exposureValue;
        } else if (this.selectedTool == this.warmthTool) {
            i = (int) this.warmthValue;
        } else if (this.selectedTool == this.saturationTool) {
            i = (int) this.saturationValue;
        } else if (this.selectedTool == this.vignetteTool) {
            i = (int) this.vignetteValue;
        } else if (this.selectedTool == this.shadowsTool) {
            i = (int) this.shadowsValue;
        } else if (this.selectedTool == this.grainTool) {
            i = (int) this.grainValue;
        } else if (this.selectedTool == this.sharpenTool) {
            i = (int) this.sharpenValue;
        } else if (this.selectedTool == this.fadeTool) {
            i = (int) this.fadeValue;
        }
        if (i > 0) {
            this.valueTextView.setText("+" + i);
        } else {
            this.valueTextView.setText(TtmlNode.ANONYMOUS_REGION_ID + i);
        }
    }

    public Bitmap getBitmap() {
        return this.eglThread != null ? this.eglThread.getTexture() : null;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public FrameLayout getEditView() {
        return this.editView;
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public boolean hasChanges() {
        return (this.enhanceValue == 0.0f && this.contrastValue == 0.0f && this.highlightsValue == 0.0f && this.exposureValue == 0.0f && this.warmthValue == 0.0f && this.saturationValue == 0.0f && this.vignetteValue == 0.0f && this.shadowsValue == 0.0f && this.grainValue == 0.0f && this.sharpenValue == 0.0f && this.fadeValue == 0.0f && this.tintHighlightsColor == 0 && this.tintShadowsColor == 0 && this.curvesToolValue.shouldBeSkipped()) ? false : true;
    }

    public void init() {
        this.textureView.setVisibility(0);
    }

    protected void onMeasure(int i, int i2) {
        fixLayout(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
        super.onMeasure(i, i2);
    }

    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            LayoutParams layoutParams = (LayoutParams) this.textureView.getLayoutParams();
            if (layoutParams != null && motionEvent.getX() >= ((float) layoutParams.leftMargin) && motionEvent.getY() >= ((float) layoutParams.topMargin) && motionEvent.getX() <= ((float) (layoutParams.leftMargin + layoutParams.width))) {
                if (motionEvent.getY() <= ((float) (layoutParams.height + layoutParams.topMargin))) {
                    setShowOriginal(true);
                }
            }
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
            setShowOriginal(false);
        }
    }

    public void setEditViewFirst() {
        this.selectedTool = 0;
        this.previousValue = this.enhanceValue;
        this.enhanceValue = 50.0f;
        this.valueSeekBar.setMinMax(0, curveGranularity);
        this.paramTextView.setText(LocaleController.getString("Enhance", C0338R.string.Enhance));
        this.editView.setVisibility(0);
        this.toolsView.setVisibility(8);
        this.valueSeekBar.setProgress(50, false);
        updateValueTextView();
    }

    public void shutdown() {
        if (this.eglThread != null) {
            this.eglThread.shutdown();
            this.eglThread = null;
        }
        this.textureView.setVisibility(8);
    }

    public void switchToOrFromEditMode() {
        View view;
        View view2;
        if (this.editView.getVisibility() == 8) {
            View view3 = this.toolsView;
            View view4 = this.editView;
            if (this.selectedTool == this.blurTool || this.selectedTool == this.tintTool || this.selectedTool == this.curvesTool) {
                this.blurLayout.setVisibility(this.selectedTool == this.blurTool ? 0 : 4);
                this.tintLayout.setVisibility(this.selectedTool == this.tintTool ? 0 : 4);
                this.curveLayout.setVisibility(this.selectedTool == this.curvesTool ? 0 : 4);
                if (this.selectedTool == this.blurTool) {
                    this.infoTextView.setText(LocaleController.getString("Blur", C0338R.string.Blur));
                    if (this.blurType != 0) {
                        this.blurControl.setVisibility(0);
                    }
                } else if (this.selectedTool == this.curvesTool) {
                    this.infoTextView.setText(LocaleController.getString("Curves", C0338R.string.Curves));
                    this.curvesControl.setVisibility(0);
                    this.curvesToolValue.activeType = 0;
                    int i = 0;
                    while (i < 4) {
                        this.curveTextView[i].setTextColor(i == 0 ? -1 : -8355712);
                        i++;
                    }
                } else {
                    this.selectedTintMode = 0;
                    updateSelectedTintButton(false);
                    this.infoTextView.setText(LocaleController.getString("Tint", C0338R.string.Tint));
                }
                this.infoTextView.setVisibility(0);
                this.valueSeekBar.setVisibility(4);
                this.paramTextView.setVisibility(4);
                this.valueTextView.setVisibility(4);
                updateSelectedBlurType();
                view = view4;
                view2 = view3;
            } else {
                this.tintLayout.setVisibility(4);
                this.curveLayout.setVisibility(4);
                this.blurLayout.setVisibility(4);
                this.valueSeekBar.setVisibility(0);
                this.infoTextView.setVisibility(4);
                this.paramTextView.setVisibility(0);
                this.valueTextView.setVisibility(0);
                this.blurControl.setVisibility(4);
                this.curvesControl.setVisibility(4);
                view = view4;
                view2 = view3;
            }
        } else {
            this.selectedTool = -1;
            View view5 = this.editView;
            view = this.toolsView;
            this.blurControl.setVisibility(4);
            this.curvesControl.setVisibility(4);
            view2 = view5;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        float[] fArr = new float[curveDataStep];
        fArr[0] = 0.0f;
        fArr[1] = (float) AndroidUtilities.dp(126.0f);
        animatorArr[0] = ObjectAnimator.ofFloat(view2, "translationY", fArr);
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new AnonymousClass15(view2, view));
        animatorSet.setDuration(200);
        animatorSet.start();
    }
}
