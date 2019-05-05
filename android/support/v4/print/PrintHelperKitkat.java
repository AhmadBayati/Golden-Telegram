package android.support.v4.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument.Page;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class PrintHelperKitkat {
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    private static final String LOG_TAG = "PrintHelperKitkat";
    private static final int MAX_PRINT_SIZE = 3500;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    int mColorMode;
    final Context mContext;
    Options mDecodeOptions;
    protected boolean mIsMinMarginsHandlingCorrect;
    private final Object mLock;
    int mOrientation;
    protected boolean mPrintActivityRespectsOrientation;
    int mScaleMode;

    public interface OnPrintFinishCallback {
        void onFinish();
    }

    /* renamed from: android.support.v4.print.PrintHelperKitkat.1 */
    class C00201 extends PrintDocumentAdapter {
        private PrintAttributes mAttributes;
        final /* synthetic */ Bitmap val$bitmap;
        final /* synthetic */ OnPrintFinishCallback val$callback;
        final /* synthetic */ int val$fittingMode;
        final /* synthetic */ String val$jobName;

        C00201(String str, int i, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
            this.val$jobName = str;
            this.val$fittingMode = i;
            this.val$bitmap = bitmap;
            this.val$callback = onPrintFinishCallback;
        }

        public void onFinish() {
            if (this.val$callback != null) {
                this.val$callback.onFinish();
            }
        }

        public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes2, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
            boolean z = true;
            this.mAttributes = printAttributes2;
            PrintDocumentInfo build = new Builder(this.val$jobName).setContentType(PrintHelperKitkat.SCALE_MODE_FIT).setPageCount(PrintHelperKitkat.SCALE_MODE_FIT).build();
            if (printAttributes2.equals(printAttributes)) {
                z = false;
            }
            layoutResultCallback.onLayoutFinished(build, z);
        }

        public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
            PrintHelperKitkat.this.writeBitmap(this.mAttributes, this.val$fittingMode, this.val$bitmap, parcelFileDescriptor, cancellationSignal, writeResultCallback);
        }
    }

    /* renamed from: android.support.v4.print.PrintHelperKitkat.2 */
    class C00212 extends AsyncTask<Void, Void, Throwable> {
        final /* synthetic */ PrintAttributes val$attributes;
        final /* synthetic */ Bitmap val$bitmap;
        final /* synthetic */ CancellationSignal val$cancellationSignal;
        final /* synthetic */ ParcelFileDescriptor val$fileDescriptor;
        final /* synthetic */ int val$fittingMode;
        final /* synthetic */ PrintAttributes val$pdfAttributes;
        final /* synthetic */ WriteResultCallback val$writeResultCallback;

        C00212(CancellationSignal cancellationSignal, PrintAttributes printAttributes, Bitmap bitmap, PrintAttributes printAttributes2, int i, ParcelFileDescriptor parcelFileDescriptor, WriteResultCallback writeResultCallback) {
            this.val$cancellationSignal = cancellationSignal;
            this.val$pdfAttributes = printAttributes;
            this.val$bitmap = bitmap;
            this.val$attributes = printAttributes2;
            this.val$fittingMode = i;
            this.val$fileDescriptor = parcelFileDescriptor;
            this.val$writeResultCallback = writeResultCallback;
        }

        protected Throwable doInBackground(Void... voidArr) {
            Bitmap access$100;
            Throwable th = null;
            PrintedPdfDocument printedPdfDocument;
            try {
                if (!this.val$cancellationSignal.isCanceled()) {
                    printedPdfDocument = new PrintedPdfDocument(PrintHelperKitkat.this.mContext, this.val$pdfAttributes);
                    access$100 = PrintHelperKitkat.this.convertBitmapForColorMode(this.val$bitmap, this.val$pdfAttributes.getColorMode());
                    if (!this.val$cancellationSignal.isCanceled()) {
                        RectF rectF;
                        Page startPage = printedPdfDocument.startPage(PrintHelperKitkat.SCALE_MODE_FIT);
                        if (PrintHelperKitkat.this.mIsMinMarginsHandlingCorrect) {
                            rectF = new RectF(startPage.getInfo().getContentRect());
                        } else {
                            PrintedPdfDocument printedPdfDocument2 = new PrintedPdfDocument(PrintHelperKitkat.this.mContext, this.val$attributes);
                            Page startPage2 = printedPdfDocument2.startPage(PrintHelperKitkat.SCALE_MODE_FIT);
                            rectF = new RectF(startPage2.getInfo().getContentRect());
                            printedPdfDocument2.finishPage(startPage2);
                            printedPdfDocument2.close();
                        }
                        Matrix access$200 = PrintHelperKitkat.this.getMatrix(access$100.getWidth(), access$100.getHeight(), rectF, this.val$fittingMode);
                        if (!PrintHelperKitkat.this.mIsMinMarginsHandlingCorrect) {
                            access$200.postTranslate(rectF.left, rectF.top);
                            startPage.getCanvas().clipRect(rectF);
                        }
                        startPage.getCanvas().drawBitmap(access$100, access$200, null);
                        printedPdfDocument.finishPage(startPage);
                        if (this.val$cancellationSignal.isCanceled()) {
                            printedPdfDocument.close();
                            if (this.val$fileDescriptor != null) {
                                try {
                                    this.val$fileDescriptor.close();
                                } catch (IOException e) {
                                }
                            }
                            if (access$100 != this.val$bitmap) {
                                access$100.recycle();
                            }
                        } else {
                            printedPdfDocument.writeTo(new FileOutputStream(this.val$fileDescriptor.getFileDescriptor()));
                            printedPdfDocument.close();
                            if (this.val$fileDescriptor != null) {
                                try {
                                    this.val$fileDescriptor.close();
                                } catch (IOException e2) {
                                }
                            }
                            if (access$100 != this.val$bitmap) {
                                access$100.recycle();
                            }
                        }
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
            return th;
        }

        protected void onPostExecute(Throwable th) {
            if (this.val$cancellationSignal.isCanceled()) {
                this.val$writeResultCallback.onWriteCancelled();
            } else if (th == null) {
                WriteResultCallback writeResultCallback = this.val$writeResultCallback;
                PageRange[] pageRangeArr = new PageRange[PrintHelperKitkat.SCALE_MODE_FIT];
                pageRangeArr[0] = PageRange.ALL_PAGES;
                writeResultCallback.onWriteFinished(pageRangeArr);
            } else {
                Log.e(PrintHelperKitkat.LOG_TAG, "Error writing printed content", th);
                this.val$writeResultCallback.onWriteFailed(null);
            }
        }
    }

    /* renamed from: android.support.v4.print.PrintHelperKitkat.3 */
    class C00243 extends PrintDocumentAdapter {
        private PrintAttributes mAttributes;
        Bitmap mBitmap;
        AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
        final /* synthetic */ OnPrintFinishCallback val$callback;
        final /* synthetic */ int val$fittingMode;
        final /* synthetic */ Uri val$imageFile;
        final /* synthetic */ String val$jobName;

        /* renamed from: android.support.v4.print.PrintHelperKitkat.3.1 */
        class C00231 extends AsyncTask<Uri, Boolean, Bitmap> {
            final /* synthetic */ CancellationSignal val$cancellationSignal;
            final /* synthetic */ LayoutResultCallback val$layoutResultCallback;
            final /* synthetic */ PrintAttributes val$newPrintAttributes;
            final /* synthetic */ PrintAttributes val$oldPrintAttributes;

            /* renamed from: android.support.v4.print.PrintHelperKitkat.3.1.1 */
            class C00221 implements OnCancelListener {
                C00221() {
                }

                public void onCancel() {
                    C00243.this.cancelLoad();
                    C00231.this.cancel(false);
                }
            }

            C00231(CancellationSignal cancellationSignal, PrintAttributes printAttributes, PrintAttributes printAttributes2, LayoutResultCallback layoutResultCallback) {
                this.val$cancellationSignal = cancellationSignal;
                this.val$newPrintAttributes = printAttributes;
                this.val$oldPrintAttributes = printAttributes2;
                this.val$layoutResultCallback = layoutResultCallback;
            }

            protected Bitmap doInBackground(Uri... uriArr) {
                try {
                    return PrintHelperKitkat.this.loadConstrainedBitmap(C00243.this.val$imageFile, PrintHelperKitkat.MAX_PRINT_SIZE);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }

            protected void onCancelled(Bitmap bitmap) {
                this.val$layoutResultCallback.onLayoutCancelled();
                C00243.this.mLoadBitmap = null;
            }

            protected void onPostExecute(Bitmap bitmap) {
                boolean z = true;
                super.onPostExecute(bitmap);
                if (bitmap != null && (!PrintHelperKitkat.this.mPrintActivityRespectsOrientation || PrintHelperKitkat.this.mOrientation == 0)) {
                    MediaSize mediaSize;
                    synchronized (this) {
                        mediaSize = C00243.this.mAttributes.getMediaSize();
                    }
                    if (!(mediaSize == null || mediaSize.isPortrait() == PrintHelperKitkat.isPortrait(bitmap))) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90.0f);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    }
                }
                C00243.this.mBitmap = bitmap;
                if (bitmap != null) {
                    PrintDocumentInfo build = new Builder(C00243.this.val$jobName).setContentType(PrintHelperKitkat.SCALE_MODE_FIT).setPageCount(PrintHelperKitkat.SCALE_MODE_FIT).build();
                    if (this.val$newPrintAttributes.equals(this.val$oldPrintAttributes)) {
                        z = false;
                    }
                    this.val$layoutResultCallback.onLayoutFinished(build, z);
                } else {
                    this.val$layoutResultCallback.onLayoutFailed(null);
                }
                C00243.this.mLoadBitmap = null;
            }

            protected void onPreExecute() {
                this.val$cancellationSignal.setOnCancelListener(new C00221());
            }
        }

        C00243(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback, int i) {
            this.val$jobName = str;
            this.val$imageFile = uri;
            this.val$callback = onPrintFinishCallback;
            this.val$fittingMode = i;
            this.mBitmap = null;
        }

        private void cancelLoad() {
            synchronized (PrintHelperKitkat.this.mLock) {
                if (PrintHelperKitkat.this.mDecodeOptions != null) {
                    PrintHelperKitkat.this.mDecodeOptions.requestCancelDecode();
                    PrintHelperKitkat.this.mDecodeOptions = null;
                }
            }
        }

        public void onFinish() {
            super.onFinish();
            cancelLoad();
            if (this.mLoadBitmap != null) {
                this.mLoadBitmap.cancel(true);
            }
            if (this.val$callback != null) {
                this.val$callback.onFinish();
            }
            if (this.mBitmap != null) {
                this.mBitmap.recycle();
                this.mBitmap = null;
            }
        }

        public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes2, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
            boolean z = true;
            synchronized (this) {
                this.mAttributes = printAttributes2;
            }
            if (cancellationSignal.isCanceled()) {
                layoutResultCallback.onLayoutCancelled();
            } else if (this.mBitmap != null) {
                PrintDocumentInfo build = new Builder(this.val$jobName).setContentType(PrintHelperKitkat.SCALE_MODE_FIT).setPageCount(PrintHelperKitkat.SCALE_MODE_FIT).build();
                if (printAttributes2.equals(printAttributes)) {
                    z = false;
                }
                layoutResultCallback.onLayoutFinished(build, z);
            } else {
                this.mLoadBitmap = new C00231(cancellationSignal, printAttributes2, printAttributes, layoutResultCallback).execute(new Uri[0]);
            }
        }

        public void onWrite(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
            PrintHelperKitkat.this.writeBitmap(this.mAttributes, this.val$fittingMode, this.mBitmap, parcelFileDescriptor, cancellationSignal, writeResultCallback);
        }
    }

    PrintHelperKitkat(Context context) {
        this.mDecodeOptions = null;
        this.mLock = new Object();
        this.mScaleMode = SCALE_MODE_FILL;
        this.mColorMode = SCALE_MODE_FILL;
        this.mPrintActivityRespectsOrientation = true;
        this.mIsMinMarginsHandlingCorrect = true;
        this.mContext = context;
    }

    private Bitmap convertBitmapForColorMode(Bitmap bitmap, int i) {
        if (i != SCALE_MODE_FIT) {
            return bitmap;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        canvas.setBitmap(null);
        return createBitmap;
    }

    private Matrix getMatrix(int i, int i2, RectF rectF, int i3) {
        Matrix matrix = new Matrix();
        float width = rectF.width() / ((float) i);
        width = i3 == SCALE_MODE_FILL ? Math.max(width, rectF.height() / ((float) i2)) : Math.min(width, rectF.height() / ((float) i2));
        matrix.postScale(width, width);
        matrix.postTranslate((rectF.width() - (((float) i) * width)) / 2.0f, (rectF.height() - (width * ((float) i2))) / 2.0f);
        return matrix;
    }

    private static boolean isPortrait(Bitmap bitmap) {
        return bitmap.getWidth() <= bitmap.getHeight();
    }

    private Bitmap loadBitmap(Uri uri, Options options) {
        InputStream inputStream = null;
        if (uri == null || this.mContext == null) {
            throw new IllegalArgumentException("bad argument to loadBitmap");
        }
        try {
            inputStream = this.mContext.getContentResolver().openInputStream(uri);
            Bitmap decodeStream = BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e) {
                    Log.w(LOG_TAG, "close fail ", e);
                }
            }
            return decodeStream;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2) {
                    Log.w(LOG_TAG, "close fail ", e2);
                }
            }
        }
    }

    private Bitmap loadConstrainedBitmap(Uri uri, int i) {
        int i2 = SCALE_MODE_FIT;
        Bitmap bitmap = null;
        if (i <= 0 || uri == null || this.mContext == null) {
            throw new IllegalArgumentException("bad argument to getScaledBitmap");
        }
        Options options = new Options();
        options.inJustDecodeBounds = true;
        loadBitmap(uri, options);
        int i3 = options.outWidth;
        int i4 = options.outHeight;
        if (i3 > 0 && i4 > 0) {
            int max = Math.max(i3, i4);
            while (max > i) {
                max >>>= SCALE_MODE_FIT;
                i2 <<= SCALE_MODE_FIT;
            }
            if (i2 > 0 && Math.min(i3, i4) / i2 > 0) {
                Options options2;
                synchronized (this.mLock) {
                    this.mDecodeOptions = new Options();
                    this.mDecodeOptions.inMutable = true;
                    this.mDecodeOptions.inSampleSize = i2;
                    options2 = this.mDecodeOptions;
                }
                try {
                    bitmap = loadBitmap(uri, options2);
                    synchronized (this.mLock) {
                        this.mDecodeOptions = null;
                    }
                } catch (Throwable th) {
                    synchronized (this.mLock) {
                    }
                    this.mDecodeOptions = null;
                }
            }
        }
        return bitmap;
    }

    private void writeBitmap(PrintAttributes printAttributes, int i, Bitmap bitmap, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
        new C00212(cancellationSignal, this.mIsMinMarginsHandlingCorrect ? printAttributes : copyAttributes(printAttributes).setMinMargins(new Margins(0, 0, 0, 0)).build(), bitmap, printAttributes, i, parcelFileDescriptor, writeResultCallback).execute(new Void[0]);
    }

    protected PrintAttributes.Builder copyAttributes(PrintAttributes printAttributes) {
        PrintAttributes.Builder minMargins = new PrintAttributes.Builder().setMediaSize(printAttributes.getMediaSize()).setResolution(printAttributes.getResolution()).setMinMargins(printAttributes.getMinMargins());
        if (printAttributes.getColorMode() != 0) {
            minMargins.setColorMode(printAttributes.getColorMode());
        }
        return minMargins;
    }

    public int getColorMode() {
        return this.mColorMode;
    }

    public int getOrientation() {
        return this.mOrientation == 0 ? SCALE_MODE_FIT : this.mOrientation;
    }

    public int getScaleMode() {
        return this.mScaleMode;
    }

    public void printBitmap(String str, Bitmap bitmap, OnPrintFinishCallback onPrintFinishCallback) {
        if (bitmap != null) {
            ((PrintManager) this.mContext.getSystemService("print")).print(str, new C00201(str, this.mScaleMode, bitmap, onPrintFinishCallback), new PrintAttributes.Builder().setMediaSize(isPortrait(bitmap) ? MediaSize.UNKNOWN_PORTRAIT : MediaSize.UNKNOWN_LANDSCAPE).setColorMode(this.mColorMode).build());
        }
    }

    public void printBitmap(String str, Uri uri, OnPrintFinishCallback onPrintFinishCallback) {
        PrintDocumentAdapter c00243 = new C00243(str, uri, onPrintFinishCallback, this.mScaleMode);
        PrintManager printManager = (PrintManager) this.mContext.getSystemService("print");
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(this.mColorMode);
        if (this.mOrientation == SCALE_MODE_FIT || this.mOrientation == 0) {
            builder.setMediaSize(MediaSize.UNKNOWN_LANDSCAPE);
        } else if (this.mOrientation == SCALE_MODE_FILL) {
            builder.setMediaSize(MediaSize.UNKNOWN_PORTRAIT);
        }
        printManager.print(str, c00243, builder.build());
    }

    public void setColorMode(int i) {
        this.mColorMode = i;
    }

    public void setOrientation(int i) {
        this.mOrientation = i;
    }

    public void setScaleMode(int i) {
        this.mScaleMode = i;
    }
}
