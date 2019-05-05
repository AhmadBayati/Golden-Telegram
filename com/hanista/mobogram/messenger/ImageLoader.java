package com.hanista.mobogram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Thumbnails;
import com.hanista.mobogram.messenger.FileLoader.FileLoaderDelegate;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Factory;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputEncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoCachedSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSize;
import com.hanista.mobogram.ui.Components.AnimatedFileDrawable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageLoader {
    private static volatile ImageLoader Instance;
    private static byte[] bytes;
    private static byte[] bytesThumb;
    private static byte[] header;
    private static byte[] headerThumb;
    private HashMap<String, Integer> bitmapUseCounts;
    private DispatchQueue cacheOutQueue;
    private DispatchQueue cacheThumbOutQueue;
    private int currentHttpFileLoadTasksCount;
    private int currentHttpTasksCount;
    private ConcurrentHashMap<String, Float> fileProgresses;
    private LinkedList<HttpFileTask> httpFileLoadTasks;
    private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys;
    private LinkedList<HttpImageTask> httpTasks;
    private String ignoreRemoval;
    private DispatchQueue imageLoadQueue;
    private HashMap<String, CacheImage> imageLoadingByKeys;
    private HashMap<Integer, CacheImage> imageLoadingByTag;
    private HashMap<String, CacheImage> imageLoadingByUrl;
    private volatile long lastCacheOutTime;
    private int lastImageNum;
    private long lastProgressUpdateTime;
    private LruCache memCache;
    private HashMap<String, Runnable> retryHttpsTasks;
    private File telegramPath;
    private HashMap<String, ThumbGenerateTask> thumbGenerateTasks;
    private DispatchQueue thumbGeneratingQueue;
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb;
    private HashMap<Integer, String> waitingForQualityThumbByTag;

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ String val$location;

        AnonymousClass10(String str) {
            this.val$location = str;
        }

        public void run() {
            CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(this.val$location);
            if (cacheImage != null) {
                cacheImage.setImageAndClear(null);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ HttpFileTask val$oldTask;
        final /* synthetic */ int val$reason;

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.11.1 */
        class C04181 implements Runnable {
            final /* synthetic */ HttpFileTask val$newTask;

            C04181(HttpFileTask httpFileTask) {
                this.val$newTask = httpFileTask;
            }

            public void run() {
                ImageLoader.this.httpFileLoadTasks.add(this.val$newTask);
                ImageLoader.this.runHttpFileLoadTasks(null, 0);
            }
        }

        AnonymousClass11(HttpFileTask httpFileTask, int i) {
            this.val$oldTask = httpFileTask;
            this.val$reason = i;
        }

        public void run() {
            if (this.val$oldTask != null) {
                ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount - 1;
            }
            if (this.val$oldTask != null) {
                if (this.val$reason == 1) {
                    if (this.val$oldTask.canRetry) {
                        Runnable c04181 = new C04181(new HttpFileTask(this.val$oldTask.url, this.val$oldTask.tempFile, this.val$oldTask.ext));
                        ImageLoader.this.retryHttpsTasks.put(this.val$oldTask.url, c04181);
                        AndroidUtilities.runOnUIThread(c04181, 1000);
                    } else {
                        ImageLoader.this.httpFileLoadTasksByKeys.remove(this.val$oldTask.url);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidFailedLoad, this.val$oldTask.url, Integer.valueOf(0));
                    }
                } else if (this.val$reason == 2) {
                    ImageLoader.this.httpFileLoadTasksByKeys.remove(this.val$oldTask.url);
                    File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$oldTask.url) + "." + this.val$oldTask.ext);
                    String file2 = FileLoader.renameTo(this.val$oldTask.tempFile, file) ? file.toString() : this.val$oldTask.tempFile.toString();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidLoaded, this.val$oldTask.url, file2);
                }
            }
            while (ImageLoader.this.currentHttpFileLoadTasksCount < 2 && !ImageLoader.this.httpFileLoadTasks.isEmpty()) {
                ((HttpFileTask) ImageLoader.this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount + 1;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.1 */
    class C04191 extends LruCache {
        C04191(int i) {
            super(i);
        }

        protected void entryRemoved(boolean z, String str, BitmapDrawable bitmapDrawable, BitmapDrawable bitmapDrawable2) {
            if (ImageLoader.this.ignoreRemoval == null || str == null || !ImageLoader.this.ignoreRemoval.equals(str)) {
                Integer num = (Integer) ImageLoader.this.bitmapUseCounts.get(str);
                if (num == null || num.intValue() == 0) {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
            }
        }

        protected int sizeOf(String str, BitmapDrawable bitmapDrawable) {
            return bitmapDrawable.getBitmap().getByteCount();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2 */
    class C04282 implements FileLoaderDelegate {

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.1 */
        class C04201 implements Runnable {
            final /* synthetic */ boolean val$isEncrypted;
            final /* synthetic */ String val$location;
            final /* synthetic */ float val$progress;

            C04201(String str, float f, boolean z) {
                this.val$location = str;
                this.val$progress = f;
                this.val$isEncrypted = z;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileUploadProgressChanged, this.val$location, Float.valueOf(this.val$progress), Boolean.valueOf(this.val$isEncrypted));
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.2 */
        class C04222 implements Runnable {
            final /* synthetic */ InputEncryptedFile val$inputEncryptedFile;
            final /* synthetic */ InputFile val$inputFile;
            final /* synthetic */ byte[] val$iv;
            final /* synthetic */ byte[] val$key;
            final /* synthetic */ String val$location;
            final /* synthetic */ long val$totalFileSize;

            /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.2.1 */
            class C04211 implements Runnable {
                C04211() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidUpload, C04222.this.val$location, C04222.this.val$inputFile, C04222.this.val$inputEncryptedFile, C04222.this.val$key, C04222.this.val$iv, Long.valueOf(C04222.this.val$totalFileSize));
                }
            }

            C04222(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                this.val$location = str;
                this.val$inputFile = inputFile;
                this.val$inputEncryptedFile = inputEncryptedFile;
                this.val$key = bArr;
                this.val$iv = bArr2;
                this.val$totalFileSize = j;
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C04211());
                ImageLoader.this.fileProgresses.remove(this.val$location);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.3 */
        class C04243 implements Runnable {
            final /* synthetic */ boolean val$isEncrypted;
            final /* synthetic */ String val$location;

            /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.3.1 */
            class C04231 implements Runnable {
                C04231() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailUpload, C04243.this.val$location, Boolean.valueOf(C04243.this.val$isEncrypted));
                }
            }

            C04243(String str, boolean z) {
                this.val$location = str;
                this.val$isEncrypted = z;
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C04231());
                ImageLoader.this.fileProgresses.remove(this.val$location);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.4 */
        class C04254 implements Runnable {
            final /* synthetic */ File val$finalFile;
            final /* synthetic */ String val$location;
            final /* synthetic */ int val$type;

            C04254(File file, String str, int i) {
                this.val$finalFile = file;
                this.val$location = str;
                this.val$type = i;
            }

            public void run() {
                if (MediaController.m71a().m139A() && ImageLoader.this.telegramPath != null && this.val$finalFile != null && ((this.val$location.endsWith(".mp4") || this.val$location.endsWith(".jpg")) && this.val$finalFile.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                    AndroidUtilities.addMediaToGallery(this.val$finalFile.toString());
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, this.val$location);
                ImageLoader.this.fileDidLoaded(this.val$location, this.val$finalFile, this.val$type);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.5 */
        class C04265 implements Runnable {
            final /* synthetic */ int val$canceled;
            final /* synthetic */ String val$location;

            C04265(String str, int i) {
                this.val$location = str;
                this.val$canceled = i;
            }

            public void run() {
                ImageLoader.this.fileDidFailedLoad(this.val$location, this.val$canceled);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, this.val$location, Integer.valueOf(this.val$canceled));
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.2.6 */
        class C04276 implements Runnable {
            final /* synthetic */ String val$location;
            final /* synthetic */ float val$progress;

            C04276(String str, float f) {
                this.val$location = str;
                this.val$progress = f;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, this.val$location, Float.valueOf(this.val$progress));
            }
        }

        C04282() {
        }

        public void fileDidFailedLoad(String str, int i) {
            ImageLoader.this.fileProgresses.remove(str);
            AndroidUtilities.runOnUIThread(new C04265(str, i));
        }

        public void fileDidFailedUpload(String str, boolean z) {
            Utilities.stageQueue.postRunnable(new C04243(str, z));
        }

        public void fileDidLoaded(String str, File file, int i) {
            ImageLoader.this.fileProgresses.remove(str);
            AndroidUtilities.runOnUIThread(new C04254(file, str, i));
        }

        public void fileDidUploaded(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
            Utilities.stageQueue.postRunnable(new C04222(str, inputFile, inputEncryptedFile, bArr, bArr2, j));
        }

        public void fileLoadProgressChanged(String str, float f) {
            ImageLoader.this.fileProgresses.put(str, Float.valueOf(f));
            long currentTimeMillis = System.currentTimeMillis();
            if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500) {
                ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C04276(str, f));
            }
        }

        public void fileUploadProgressChanged(String str, float f, boolean z) {
            ImageLoader.this.fileProgresses.put(str, Float.valueOf(f));
            long currentTimeMillis = System.currentTimeMillis();
            if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500) {
                ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                AndroidUtilities.runOnUIThread(new C04201(str, f, z));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.3 */
    class C04303 extends BroadcastReceiver {

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.3.1 */
        class C04291 implements Runnable {
            C04291() {
            }

            public void run() {
                ImageLoader.this.checkMediaPaths();
            }
        }

        C04303() {
        }

        public void onReceive(Context context, Intent intent) {
            FileLog.m16e("tmessages", "file system changed");
            Runnable c04291 = new C04291();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                AndroidUtilities.runOnUIThread(c04291, 1000);
            } else {
                c04291.run();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.4 */
    class C04324 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.4.1 */
        class C04311 implements Runnable {
            final /* synthetic */ HashMap val$paths;

            C04311(HashMap hashMap) {
                this.val$paths = hashMap;
            }

            public void run() {
                FileLoader.getInstance().setMediaDirs(this.val$paths);
            }
        }

        C04324() {
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C04311(ImageLoader.this.createMediaPaths()));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.5 */
    class C04335 implements Runnable {
        final /* synthetic */ ImageReceiver val$imageReceiver;
        final /* synthetic */ int val$type;

        C04335(int i, ImageReceiver imageReceiver) {
            this.val$type = i;
            this.val$imageReceiver = imageReceiver;
        }

        public void run() {
            int i;
            int i2;
            if (this.val$type == 1) {
                i = 1;
                i2 = 0;
            } else if (this.val$type == 2) {
                i = 2;
                i2 = 1;
            } else {
                i = 2;
                i2 = 0;
            }
            int i3 = i2;
            while (i3 < i) {
                Integer tag = this.val$imageReceiver.getTag(i3 == 0);
                if (i3 == 0) {
                    ImageLoader.this.removeFromWaitingForThumb(tag);
                }
                if (tag != null) {
                    CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByTag.get(tag);
                    if (cacheImage != null) {
                        cacheImage.removeImageReceiver(this.val$imageReceiver);
                    }
                }
                i3++;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.6 */
    class C04346 implements Runnable {
        final /* synthetic */ String val$newKey;
        final /* synthetic */ FileLocation val$newLocation;
        final /* synthetic */ String val$oldKey;

        C04346(String str, String str2, FileLocation fileLocation) {
            this.val$oldKey = str;
            this.val$newKey = str2;
            this.val$newLocation = fileLocation;
        }

        public void run() {
            ImageLoader.this.replaceImageInCacheInternal(this.val$oldKey, this.val$newKey, this.val$newLocation);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.7 */
    class C04357 implements Runnable {
        final /* synthetic */ boolean val$cacheOnly;
        final /* synthetic */ String val$ext;
        final /* synthetic */ String val$filter;
        final /* synthetic */ boolean val$finalIsNeedsQualityThumb;
        final /* synthetic */ Integer val$finalTag;
        final /* synthetic */ String val$httpLocation;
        final /* synthetic */ TLObject val$imageLocation;
        final /* synthetic */ ImageReceiver val$imageReceiver;
        final /* synthetic */ String val$key;
        final /* synthetic */ MessageObject val$parentMessageObject;
        final /* synthetic */ boolean val$shouldGenerateQualityThumb;
        final /* synthetic */ int val$size;
        final /* synthetic */ int val$thumb;
        final /* synthetic */ String val$url;

        C04357(int i, String str, String str2, Integer num, ImageReceiver imageReceiver, String str3, String str4, boolean z, MessageObject messageObject, TLObject tLObject, boolean z2, boolean z3, int i2, String str5) {
            this.val$thumb = i;
            this.val$url = str;
            this.val$key = str2;
            this.val$finalTag = num;
            this.val$imageReceiver = imageReceiver;
            this.val$filter = str3;
            this.val$httpLocation = str4;
            this.val$finalIsNeedsQualityThumb = z;
            this.val$parentMessageObject = messageObject;
            this.val$imageLocation = tLObject;
            this.val$shouldGenerateQualityThumb = z2;
            this.val$cacheOnly = z3;
            this.val$size = i2;
            this.val$ext = str5;
        }

        public void run() {
            boolean z;
            boolean z2;
            boolean z3 = false;
            if (this.val$thumb != 2) {
                boolean z4;
                CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(this.val$url);
                CacheImage cacheImage2 = (CacheImage) ImageLoader.this.imageLoadingByKeys.get(this.val$key);
                CacheImage cacheImage3 = (CacheImage) ImageLoader.this.imageLoadingByTag.get(this.val$finalTag);
                if (cacheImage3 != null) {
                    if (cacheImage3 == cacheImage || cacheImage3 == cacheImage2) {
                        z = true;
                        if (!z || cacheImage2 == null) {
                            z4 = z;
                        } else {
                            cacheImage2.addImageReceiver(this.val$imageReceiver, this.val$key, this.val$filter);
                            z4 = true;
                        }
                        if (!z4 || cacheImage == null) {
                            z2 = z4;
                        } else {
                            cacheImage.addImageReceiver(this.val$imageReceiver, this.val$key, this.val$filter);
                            z2 = true;
                        }
                    } else {
                        cacheImage3.removeImageReceiver(this.val$imageReceiver);
                    }
                }
                z = false;
                if (z) {
                }
                z4 = z;
                if (z4) {
                }
                z2 = z4;
            } else {
                z2 = false;
            }
            if (!z2) {
                File file;
                if (this.val$httpLocation != null) {
                    if (!this.val$httpLocation.startsWith("http")) {
                        int indexOf;
                        if (this.val$httpLocation.startsWith("thumb://")) {
                            indexOf = this.val$httpLocation.indexOf(":", 8);
                            file = indexOf >= 0 ? new File(this.val$httpLocation.substring(indexOf + 1)) : null;
                            z = true;
                        } else if (this.val$httpLocation.startsWith("vthumb://")) {
                            indexOf = this.val$httpLocation.indexOf(":", 9);
                            file = indexOf >= 0 ? new File(this.val$httpLocation.substring(indexOf + 1)) : null;
                            z = true;
                        } else {
                            file = new File(this.val$httpLocation);
                            z = true;
                        }
                    }
                    file = null;
                    z = false;
                } else {
                    if (this.val$thumb != 0) {
                        File file2;
                        if (this.val$finalIsNeedsQualityThumb) {
                            file2 = new File(FileLoader.getInstance().getDirectory(4), "q_" + this.val$url);
                            file = !file2.exists() ? null : file2;
                        } else {
                            file = null;
                        }
                        if (this.val$parentMessageObject != null) {
                            if (this.val$parentMessageObject.messageOwner.attachPath == null || this.val$parentMessageObject.messageOwner.attachPath.length() <= 0) {
                                file2 = null;
                            } else {
                                file2 = new File(this.val$parentMessageObject.messageOwner.attachPath);
                                if (!file2.exists()) {
                                    file2 = null;
                                }
                            }
                            File pathToMessage = file2 == null ? FileLoader.getPathToMessage(this.val$parentMessageObject.messageOwner) : file2;
                            if (this.val$finalIsNeedsQualityThumb && r1 == null) {
                                String fileName = this.val$parentMessageObject.getFileName();
                                ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) ImageLoader.this.waitingForQualityThumb.get(fileName);
                                if (thumbGenerateInfo == null) {
                                    ThumbGenerateInfo thumbGenerateInfo2 = new ThumbGenerateInfo(null);
                                    thumbGenerateInfo2.fileLocation = (TL_fileLocation) this.val$imageLocation;
                                    thumbGenerateInfo2.filter = this.val$filter;
                                    ImageLoader.this.waitingForQualityThumb.put(fileName, thumbGenerateInfo2);
                                    thumbGenerateInfo = thumbGenerateInfo2;
                                }
                                thumbGenerateInfo.count = thumbGenerateInfo.count + 1;
                                ImageLoader.this.waitingForQualityThumbByTag.put(this.val$finalTag, fileName);
                            }
                            if (pathToMessage.exists() && this.val$shouldGenerateQualityThumb) {
                                ImageLoader.this.generateThumb(this.val$parentMessageObject.getFileType(), pathToMessage, (TL_fileLocation) this.val$imageLocation, this.val$filter);
                            }
                        }
                        z = false;
                    }
                    file = null;
                    z = false;
                }
                if (this.val$thumb != 2) {
                    CacheImage cacheImage4 = new CacheImage(null);
                    if (!(this.val$httpLocation == null || this.val$httpLocation.startsWith("vthumb") || this.val$httpLocation.startsWith("thumb") || (!this.val$httpLocation.endsWith("mp4") && !this.val$httpLocation.endsWith("gif"))) || ((this.val$imageLocation instanceof Document) && MessageObject.isGifDocument((Document) this.val$imageLocation))) {
                        cacheImage4.animatedFile = true;
                    }
                    if (file == null) {
                        file = (this.val$cacheOnly || this.val$size == 0 || this.val$httpLocation != null) ? new File(FileLoader.getInstance().getDirectory(4), this.val$url) : this.val$imageLocation instanceof Document ? new File(FileLoader.getInstance().getDirectory(3), this.val$url) : new File(FileLoader.getInstance().getDirectory(0), this.val$url);
                    }
                    cacheImage4.thumb = this.val$thumb != 0;
                    cacheImage4.key = this.val$key;
                    cacheImage4.filter = this.val$filter;
                    cacheImage4.httpUrl = this.val$httpLocation;
                    cacheImage4.ext = this.val$ext;
                    cacheImage4.addImageReceiver(this.val$imageReceiver, this.val$key, this.val$filter);
                    if (z || file.exists()) {
                        cacheImage4.finalFilePath = file;
                        cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                        ImageLoader.this.imageLoadingByKeys.put(this.val$key, cacheImage4);
                        if (this.val$thumb != 0) {
                            ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                            return;
                        } else {
                            ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                            return;
                        }
                    }
                    cacheImage4.url = this.val$url;
                    cacheImage4.location = this.val$imageLocation;
                    ImageLoader.this.imageLoadingByUrl.put(this.val$url, cacheImage4);
                    if (this.val$httpLocation != null) {
                        cacheImage4.tempFilePath = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$httpLocation) + "_temp.jpg");
                        cacheImage4.finalFilePath = file;
                        cacheImage4.httpTask = new HttpImageTask(cacheImage4, this.val$size);
                        ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                        ImageLoader.this.runHttpTasks(false);
                    } else if (this.val$imageLocation instanceof FileLocation) {
                        FileLocation fileLocation = (FileLocation) this.val$imageLocation;
                        FileLoader instance = FileLoader.getInstance();
                        String str = this.val$ext;
                        int i = this.val$size;
                        if (this.val$size == 0 || fileLocation.key != null || this.val$cacheOnly) {
                            z3 = true;
                        }
                        instance.loadFile(fileLocation, str, i, z3);
                    } else if (this.val$imageLocation instanceof Document) {
                        FileLoader.getInstance().loadFile((Document) this.val$imageLocation, true, this.val$cacheOnly);
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.8 */
    class C04368 implements Runnable {
        final /* synthetic */ String val$location;

        C04368(String str) {
            this.val$location = str;
        }

        public void run() {
            CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(this.val$location);
            if (cacheImage != null) {
                HttpImageTask httpImageTask = cacheImage.httpTask;
                cacheImage.httpTask = new HttpImageTask(httpImageTask.cacheImage, httpImageTask.imageSize);
                ImageLoader.this.httpTasks.add(cacheImage.httpTask);
                ImageLoader.this.runHttpTasks(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ImageLoader.9 */
    class C04379 implements Runnable {
        final /* synthetic */ File val$finalFile;
        final /* synthetic */ String val$location;
        final /* synthetic */ int val$type;

        C04379(String str, int i, File file) {
            this.val$location = str;
            this.val$type = i;
            this.val$finalFile = file;
        }

        public void run() {
            int i = 0;
            ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) ImageLoader.this.waitingForQualityThumb.get(this.val$location);
            if (thumbGenerateInfo != null) {
                ImageLoader.this.generateThumb(this.val$type, this.val$finalFile, thumbGenerateInfo.fileLocation, thumbGenerateInfo.filter);
                ImageLoader.this.waitingForQualityThumb.remove(this.val$location);
            }
            CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(this.val$location);
            if (cacheImage != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.val$location);
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < cacheImage.imageReceiverArray.size(); i2++) {
                    String str = (String) cacheImage.keys.get(i2);
                    String str2 = (String) cacheImage.filters.get(i2);
                    ImageReceiver imageReceiver = (ImageReceiver) cacheImage.imageReceiverArray.get(i2);
                    CacheImage cacheImage2 = (CacheImage) ImageLoader.this.imageLoadingByKeys.get(str);
                    if (cacheImage2 == null) {
                        cacheImage2 = new CacheImage(null);
                        cacheImage2.finalFilePath = this.val$finalFile;
                        cacheImage2.key = str;
                        cacheImage2.httpUrl = cacheImage.httpUrl;
                        cacheImage2.thumb = cacheImage.thumb;
                        cacheImage2.ext = cacheImage.ext;
                        cacheImage2.cacheTask = new CacheOutTask(cacheImage2);
                        cacheImage2.filter = str2;
                        cacheImage2.animatedFile = cacheImage.animatedFile;
                        ImageLoader.this.imageLoadingByKeys.put(str, cacheImage2);
                        arrayList.add(cacheImage2.cacheTask);
                    }
                    cacheImage2.addImageReceiver(imageReceiver, str, str2);
                }
                while (i < arrayList.size()) {
                    if (cacheImage.thumb) {
                        ImageLoader.this.cacheThumbOutQueue.postRunnable((Runnable) arrayList.get(i));
                    } else {
                        ImageLoader.this.cacheOutQueue.postRunnable((Runnable) arrayList.get(i));
                    }
                    i++;
                }
            }
        }
    }

    private class CacheImage {
        protected boolean animatedFile;
        protected CacheOutTask cacheTask;
        protected String ext;
        protected String filter;
        protected ArrayList<String> filters;
        protected File finalFilePath;
        protected HttpImageTask httpTask;
        protected String httpUrl;
        protected ArrayList<ImageReceiver> imageReceiverArray;
        protected String key;
        protected ArrayList<String> keys;
        protected TLObject location;
        protected File tempFilePath;
        protected boolean thumb;
        protected String url;

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.CacheImage.1 */
        class C04381 implements Runnable {
            final /* synthetic */ ArrayList val$finalImageReceiverArray;
            final /* synthetic */ BitmapDrawable val$image;

            C04381(BitmapDrawable bitmapDrawable, ArrayList arrayList) {
                this.val$image = bitmapDrawable;
                this.val$finalImageReceiverArray = arrayList;
            }

            public void run() {
                if (this.val$image instanceof AnimatedFileDrawable) {
                    BitmapDrawable bitmapDrawable = (AnimatedFileDrawable) this.val$image;
                    int i = 0;
                    boolean z = false;
                    while (i < this.val$finalImageReceiverArray.size()) {
                        if (((ImageReceiver) this.val$finalImageReceiverArray.get(i)).setImageBitmapByKey(i == 0 ? bitmapDrawable : bitmapDrawable.makeCopy(), CacheImage.this.key, CacheImage.this.thumb, false)) {
                            z = true;
                        }
                        i++;
                    }
                    if (!z) {
                        ((AnimatedFileDrawable) this.val$image).recycle();
                        return;
                    }
                    return;
                }
                for (int i2 = 0; i2 < this.val$finalImageReceiverArray.size(); i2++) {
                    ((ImageReceiver) this.val$finalImageReceiverArray.get(i2)).setImageBitmapByKey(this.val$image, CacheImage.this.key, CacheImage.this.thumb, false);
                }
            }
        }

        private CacheImage() {
            this.imageReceiverArray = new ArrayList();
            this.keys = new ArrayList();
            this.filters = new ArrayList();
        }

        public void addImageReceiver(ImageReceiver imageReceiver, String str, String str2) {
            if (!this.imageReceiverArray.contains(imageReceiver)) {
                this.imageReceiverArray.add(imageReceiver);
                this.keys.add(str);
                this.filters.add(str2);
                ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(this.thumb), this);
            }
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int i = 0;
            int i2 = 0;
            while (i2 < this.imageReceiverArray.size()) {
                ImageReceiver imageReceiver2 = (ImageReceiver) this.imageReceiverArray.get(i2);
                if (imageReceiver2 == null || imageReceiver2 == imageReceiver) {
                    this.imageReceiverArray.remove(i2);
                    this.keys.remove(i2);
                    this.filters.remove(i2);
                    if (imageReceiver2 != null) {
                        ImageLoader.this.imageLoadingByTag.remove(imageReceiver2.getTag(this.thumb));
                    }
                    i2--;
                }
                i2++;
            }
            if (this.imageReceiverArray.size() == 0) {
                while (i < this.imageReceiverArray.size()) {
                    ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(i)).getTag(this.thumb));
                    i++;
                }
                this.imageReceiverArray.clear();
                if (this.location != null) {
                    if (this.location instanceof FileLocation) {
                        FileLoader.getInstance().cancelLoadFile((FileLocation) this.location, this.ext);
                    } else if (this.location instanceof Document) {
                        FileLoader.getInstance().cancelLoadFile((Document) this.location);
                    }
                }
                if (this.cacheTask != null) {
                    if (this.thumb) {
                        ImageLoader.this.cacheThumbOutQueue.cancelRunnable(this.cacheTask);
                    } else {
                        ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
                    }
                    this.cacheTask.cancel();
                    this.cacheTask = null;
                }
                if (this.httpTask != null) {
                    ImageLoader.this.httpTasks.remove(this.httpTask);
                    this.httpTask.cancel(true);
                    this.httpTask = null;
                }
                if (this.url != null) {
                    ImageLoader.this.imageLoadingByUrl.remove(this.url);
                }
                if (this.key != null) {
                    ImageLoader.this.imageLoadingByKeys.remove(this.key);
                }
            }
        }

        public void setImageAndClear(BitmapDrawable bitmapDrawable) {
            if (bitmapDrawable != null) {
                AndroidUtilities.runOnUIThread(new C04381(bitmapDrawable, new ArrayList(this.imageReceiverArray)));
            }
            for (int i = 0; i < this.imageReceiverArray.size(); i++) {
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(i)).getTag(this.thumb));
            }
            this.imageReceiverArray.clear();
            if (this.url != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.url);
            }
            if (this.key != null) {
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }
    }

    private class CacheOutTask implements Runnable {
        private CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync;

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.CacheOutTask.1 */
        class C04401 implements Runnable {
            final /* synthetic */ BitmapDrawable val$bitmapDrawable;

            /* renamed from: com.hanista.mobogram.messenger.ImageLoader.CacheOutTask.1.1 */
            class C04391 implements Runnable {
                final /* synthetic */ BitmapDrawable val$toSetFinal;

                C04391(BitmapDrawable bitmapDrawable) {
                    this.val$toSetFinal = bitmapDrawable;
                }

                public void run() {
                    CacheOutTask.this.cacheImage.setImageAndClear(this.val$toSetFinal);
                }
            }

            C04401(BitmapDrawable bitmapDrawable) {
                this.val$bitmapDrawable = bitmapDrawable;
            }

            public void run() {
                BitmapDrawable bitmapDrawable = null;
                if (this.val$bitmapDrawable instanceof AnimatedFileDrawable) {
                    bitmapDrawable = this.val$bitmapDrawable;
                } else if (this.val$bitmapDrawable != null) {
                    bitmapDrawable = ImageLoader.this.memCache.get(CacheOutTask.this.cacheImage.key);
                    if (bitmapDrawable == null) {
                        ImageLoader.this.memCache.put(CacheOutTask.this.cacheImage.key, this.val$bitmapDrawable);
                        bitmapDrawable = this.val$bitmapDrawable;
                    } else {
                        this.val$bitmapDrawable.getBitmap().recycle();
                    }
                }
                ImageLoader.this.imageLoadQueue.postRunnable(new C04391(bitmapDrawable));
            }
        }

        public CacheOutTask(CacheImage cacheImage) {
            this.sync = new Object();
            this.cacheImage = cacheImage;
        }

        private void onPostExecute(BitmapDrawable bitmapDrawable) {
            AndroidUtilities.runOnUIThread(new C04401(bitmapDrawable));
        }

        public void cancel() {
            synchronized (this.sync) {
                try {
                    this.isCancelled = true;
                    if (this.runningThread != null) {
                        this.runningThread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        }

        public void run() {
            Throwable e;
            Object obj;
            Object obj2;
            Long l;
            int i;
            float f;
            Bitmap bitmap;
            RandomAccessFile randomAccessFile;
            ByteBuffer map;
            InputStream fileInputStream;
            RandomAccessFile randomAccessFile2;
            int length;
            byte[] access$1600;
            Bitmap createScaledBitmap;
            int i2;
            int i3;
            RandomAccessFile randomAccessFile3;
            ByteBuffer map2;
            Bitmap createBitmap;
            Throwable th;
            Throwable th2;
            synchronized (this.sync) {
                this.runningThread = Thread.currentThread();
                Thread.interrupted();
                if (this.isCancelled) {
                    return;
                }
                if (this.cacheImage.animatedFile) {
                    synchronized (this.sync) {
                        if (this.isCancelled) {
                            return;
                        }
                        File file = this.cacheImage.finalFilePath;
                        boolean z = this.cacheImage.filter != null && this.cacheImage.filter.equals("d");
                        BitmapDrawable animatedFileDrawable = new AnimatedFileDrawable(file, z);
                        Thread.interrupted();
                        onPostExecute(animatedFileDrawable);
                        return;
                    }
                }
                int indexOf;
                Options options;
                float f2;
                String[] split;
                InputStream fileInputStream2;
                Object obj3;
                float f3;
                Options options2;
                float height;
                Options options3;
                Long l2 = null;
                Object obj4 = null;
                Bitmap bitmap2 = null;
                File file2 = this.cacheImage.finalFilePath;
                Object obj5 = null;
                if (VERSION.SDK_INT < 19) {
                    RandomAccessFile randomAccessFile4;
                    try {
                        randomAccessFile4 = new RandomAccessFile(file2, "r");
                        try {
                            byte[] access$1200 = this.cacheImage.thumb ? ImageLoader.headerThumb : ImageLoader.header;
                            randomAccessFile4.readFully(access$1200, 0, access$1200.length);
                            String toLowerCase = new String(access$1200).toLowerCase().toLowerCase();
                            if (toLowerCase.startsWith("riff") && toLowerCase.endsWith("webp")) {
                                obj5 = 1;
                            }
                            randomAccessFile4.close();
                            if (randomAccessFile4 != null) {
                                try {
                                    randomAccessFile4.close();
                                } catch (Throwable e2) {
                                    FileLog.m18e("tmessages", e2);
                                }
                            }
                        } catch (Exception e3) {
                            e2 = e3;
                            try {
                                FileLog.m18e("tmessages", e2);
                                if (randomAccessFile4 != null) {
                                    try {
                                        randomAccessFile4.close();
                                    } catch (Throwable e22) {
                                        FileLog.m18e("tmessages", e22);
                                    }
                                }
                                if (this.cacheImage.thumb) {
                                    try {
                                        if (this.cacheImage.httpUrl != null) {
                                            if (!this.cacheImage.httpUrl.startsWith("thumb://")) {
                                                indexOf = this.cacheImage.httpUrl.indexOf(":", 8);
                                                if (indexOf >= 0) {
                                                    l2 = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(8, indexOf)));
                                                    obj4 = null;
                                                }
                                                obj = null;
                                                obj2 = obj4;
                                                l = l2;
                                            } else if (!this.cacheImage.httpUrl.startsWith("vthumb://")) {
                                                indexOf = this.cacheImage.httpUrl.indexOf(":", 9);
                                                if (indexOf >= 0) {
                                                    l2 = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(9, indexOf)));
                                                    obj4 = 1;
                                                }
                                                obj = null;
                                                obj2 = obj4;
                                                l = l2;
                                            } else if (!this.cacheImage.httpUrl.startsWith("http")) {
                                                obj = null;
                                                obj2 = null;
                                                l = null;
                                            }
                                            i = 20;
                                            if (l != null) {
                                                i = 0;
                                            }
                                            Thread.sleep((long) i);
                                            ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                                            synchronized (this.sync) {
                                                if (this.isCancelled) {
                                                    options = new Options();
                                                    options.inSampleSize = 1;
                                                    f = 0.0f;
                                                    f2 = 0.0f;
                                                    obj4 = null;
                                                    if (this.cacheImage.filter != null) {
                                                        split = this.cacheImage.filter.split("_");
                                                        if (split.length >= 2) {
                                                            f = AndroidUtilities.density * Float.parseFloat(split[0]);
                                                            f2 = Float.parseFloat(split[1]) * AndroidUtilities.density;
                                                        }
                                                        if (this.cacheImage.filter.contains("b")) {
                                                            obj4 = 1;
                                                        }
                                                        options.inJustDecodeBounds = true;
                                                        if (l != null) {
                                                            fileInputStream2 = new FileInputStream(file2);
                                                            bitmap2 = BitmapFactory.decodeStream(fileInputStream2, null, options);
                                                            fileInputStream2.close();
                                                        } else if (obj2 == null) {
                                                            Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                                        } else {
                                                            Images.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                                        }
                                                        try {
                                                            f2 = Math.max(((float) options.outWidth) / f, ((float) options.outHeight) / f2);
                                                            if (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                                                f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                                                            }
                                                            options.inJustDecodeBounds = false;
                                                            options.inSampleSize = (int) f2;
                                                        } catch (Throwable th3) {
                                                            bitmap = bitmap2;
                                                        }
                                                    }
                                                    obj3 = obj4;
                                                    f3 = f;
                                                    synchronized (this.sync) {
                                                        if (this.isCancelled) {
                                                            if (this.cacheImage.filter == null) {
                                                            }
                                                            options.inPreferredConfig = Config.ARGB_8888;
                                                            if (VERSION.SDK_INT < 21) {
                                                                options.inPurgeable = true;
                                                            }
                                                            options.inDither = false;
                                                            if (l != null) {
                                                                bitmap2 = obj2 == null ? Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options) : Images.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                                            }
                                                            if (bitmap2 == null) {
                                                                if (obj5 != null) {
                                                                    randomAccessFile = new RandomAccessFile(file2, "r");
                                                                    map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                                                    options2 = new Options();
                                                                    options2.inJustDecodeBounds = true;
                                                                    Utilities.loadWebpImage(null, map, map.limit(), options2, true);
                                                                    bitmap2 = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                                                    Utilities.loadWebpImage(bitmap2, map, map.limit(), null, options.inPurgeable);
                                                                    randomAccessFile.close();
                                                                    bitmap = bitmap2;
                                                                } else if (options.inPurgeable) {
                                                                    fileInputStream = new FileInputStream(file2);
                                                                    bitmap2 = BitmapFactory.decodeStream(fileInputStream, null, options);
                                                                    fileInputStream.close();
                                                                } else {
                                                                    randomAccessFile2 = new RandomAccessFile(file2, "r");
                                                                    length = (int) randomAccessFile2.length();
                                                                    if (ImageLoader.bytes != null) {
                                                                    }
                                                                    if (access$1600 == null) {
                                                                        access$1600 = new byte[length];
                                                                        ImageLoader.bytes = access$1600;
                                                                    }
                                                                    randomAccessFile2.readFully(access$1600, 0, length);
                                                                    bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options);
                                                                }
                                                                if (bitmap != null) {
                                                                    obj4 = null;
                                                                    if (this.cacheImage.filter != null) {
                                                                        f2 = (float) bitmap.getWidth();
                                                                        height = (float) bitmap.getHeight();
                                                                        createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                                                        if (bitmap != createScaledBitmap) {
                                                                            bitmap.recycle();
                                                                            bitmap = createScaledBitmap;
                                                                        }
                                                                        if (bitmap.getConfig() == Config.ARGB_8888) {
                                                                            Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                                        }
                                                                        obj4 = 1;
                                                                    }
                                                                    Utilities.pinBitmap(bitmap);
                                                                } else if (obj != null) {
                                                                    try {
                                                                        file2.delete();
                                                                    } catch (Throwable th4) {
                                                                    }
                                                                }
                                                            }
                                                            bitmap = bitmap2;
                                                            if (bitmap != null) {
                                                                obj4 = null;
                                                                if (this.cacheImage.filter != null) {
                                                                    f2 = (float) bitmap.getWidth();
                                                                    height = (float) bitmap.getHeight();
                                                                    createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                                                    if (bitmap != createScaledBitmap) {
                                                                        bitmap.recycle();
                                                                        bitmap = createScaledBitmap;
                                                                    }
                                                                    if (bitmap.getConfig() == Config.ARGB_8888) {
                                                                        if (options.inPurgeable) {
                                                                        }
                                                                        Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                                    }
                                                                    obj4 = 1;
                                                                }
                                                                Utilities.pinBitmap(bitmap);
                                                            } else if (obj != null) {
                                                                file2.delete();
                                                            }
                                                        } else {
                                                            return;
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                        }
                                        i2 = 1;
                                        obj2 = null;
                                        l = null;
                                        i = 20;
                                        if (l != null) {
                                            i = 0;
                                        }
                                        Thread.sleep((long) i);
                                        ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                                        synchronized (this.sync) {
                                            if (this.isCancelled) {
                                                options = new Options();
                                                options.inSampleSize = 1;
                                                f = 0.0f;
                                                f2 = 0.0f;
                                                obj4 = null;
                                                if (this.cacheImage.filter != null) {
                                                    split = this.cacheImage.filter.split("_");
                                                    if (split.length >= 2) {
                                                        f = AndroidUtilities.density * Float.parseFloat(split[0]);
                                                        f2 = Float.parseFloat(split[1]) * AndroidUtilities.density;
                                                    }
                                                    if (this.cacheImage.filter.contains("b")) {
                                                        obj4 = 1;
                                                    }
                                                    options.inJustDecodeBounds = true;
                                                    if (l != null) {
                                                        fileInputStream2 = new FileInputStream(file2);
                                                        bitmap2 = BitmapFactory.decodeStream(fileInputStream2, null, options);
                                                        fileInputStream2.close();
                                                    } else if (obj2 == null) {
                                                        Images.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                                    } else {
                                                        Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                                    }
                                                    f2 = Math.max(((float) options.outWidth) / f, ((float) options.outHeight) / f2);
                                                    if (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                                        f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                                                    }
                                                    options.inJustDecodeBounds = false;
                                                    options.inSampleSize = (int) f2;
                                                }
                                                obj3 = obj4;
                                                f3 = f;
                                                synchronized (this.sync) {
                                                    if (this.isCancelled) {
                                                        if (this.cacheImage.filter == null) {
                                                        }
                                                        options.inPreferredConfig = Config.ARGB_8888;
                                                        if (VERSION.SDK_INT < 21) {
                                                            options.inPurgeable = true;
                                                        }
                                                        options.inDither = false;
                                                        if (l != null) {
                                                            if (obj2 == null) {
                                                            }
                                                        }
                                                        if (bitmap2 == null) {
                                                            if (obj5 != null) {
                                                                randomAccessFile = new RandomAccessFile(file2, "r");
                                                                map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                                                options2 = new Options();
                                                                options2.inJustDecodeBounds = true;
                                                                Utilities.loadWebpImage(null, map, map.limit(), options2, true);
                                                                bitmap2 = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                                                if (options.inPurgeable) {
                                                                }
                                                                Utilities.loadWebpImage(bitmap2, map, map.limit(), null, options.inPurgeable);
                                                                randomAccessFile.close();
                                                                bitmap = bitmap2;
                                                            } else if (options.inPurgeable) {
                                                                fileInputStream = new FileInputStream(file2);
                                                                bitmap2 = BitmapFactory.decodeStream(fileInputStream, null, options);
                                                                fileInputStream.close();
                                                            } else {
                                                                randomAccessFile2 = new RandomAccessFile(file2, "r");
                                                                length = (int) randomAccessFile2.length();
                                                                if (ImageLoader.bytes != null) {
                                                                }
                                                                if (access$1600 == null) {
                                                                    access$1600 = new byte[length];
                                                                    ImageLoader.bytes = access$1600;
                                                                }
                                                                randomAccessFile2.readFully(access$1600, 0, length);
                                                                bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options);
                                                            }
                                                            if (bitmap != null) {
                                                                obj4 = null;
                                                                if (this.cacheImage.filter != null) {
                                                                    f2 = (float) bitmap.getWidth();
                                                                    height = (float) bitmap.getHeight();
                                                                    createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                                                    if (bitmap != createScaledBitmap) {
                                                                        bitmap.recycle();
                                                                        bitmap = createScaledBitmap;
                                                                    }
                                                                    if (bitmap.getConfig() == Config.ARGB_8888) {
                                                                        if (options.inPurgeable) {
                                                                        }
                                                                        Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                                    }
                                                                    obj4 = 1;
                                                                }
                                                                Utilities.pinBitmap(bitmap);
                                                            } else if (obj != null) {
                                                                file2.delete();
                                                            }
                                                        }
                                                        bitmap = bitmap2;
                                                        if (bitmap != null) {
                                                            obj4 = null;
                                                            if (this.cacheImage.filter != null) {
                                                                f2 = (float) bitmap.getWidth();
                                                                height = (float) bitmap.getHeight();
                                                                createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                                                if (bitmap != createScaledBitmap) {
                                                                    bitmap.recycle();
                                                                    bitmap = createScaledBitmap;
                                                                }
                                                                if (bitmap.getConfig() == Config.ARGB_8888) {
                                                                    if (options.inPurgeable) {
                                                                    }
                                                                    Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                                }
                                                                obj4 = 1;
                                                            }
                                                            Utilities.pinBitmap(bitmap);
                                                        } else if (obj != null) {
                                                            file2.delete();
                                                        }
                                                    } else {
                                                        return;
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                    } catch (Throwable th5) {
                                        bitmap = null;
                                    }
                                } else {
                                    if (this.cacheImage.filter != null) {
                                        if (!this.cacheImage.filter.contains("b2")) {
                                            i3 = 3;
                                        } else if (!this.cacheImage.filter.contains("b1")) {
                                            i3 = 2;
                                        } else if (this.cacheImage.filter.contains("b")) {
                                            i3 = 1;
                                        }
                                        ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                                        synchronized (this.sync) {
                                            if (this.isCancelled) {
                                                options3 = new Options();
                                                options3.inSampleSize = 1;
                                                if (VERSION.SDK_INT < 21) {
                                                    options3.inPurgeable = true;
                                                }
                                                if (obj5 != null) {
                                                    randomAccessFile3 = new RandomAccessFile(file2, "r");
                                                    map2 = randomAccessFile3.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                                    options2 = new Options();
                                                    options2.inJustDecodeBounds = true;
                                                    Utilities.loadWebpImage(null, map2, map2.limit(), options2, true);
                                                    createBitmap = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                                    try {
                                                        Utilities.loadWebpImage(createBitmap, map2, map2.limit(), null, options3.inPurgeable);
                                                        randomAccessFile3.close();
                                                        bitmap = createBitmap;
                                                    } catch (Throwable th6) {
                                                        Throwable th7 = th6;
                                                        bitmap = createBitmap;
                                                        th2 = th7;
                                                        FileLog.m18e("tmessages", th2);
                                                        Thread.interrupted();
                                                        onPostExecute(bitmap != null ? null : new BitmapDrawable(bitmap));
                                                    }
                                                } else if (options3.inPurgeable) {
                                                    fileInputStream = new FileInputStream(file2);
                                                    createBitmap = BitmapFactory.decodeStream(fileInputStream, null, options3);
                                                    fileInputStream.close();
                                                    bitmap = createBitmap;
                                                } else {
                                                    randomAccessFile2 = new RandomAccessFile(file2, "r");
                                                    length = (int) randomAccessFile2.length();
                                                    if (ImageLoader.bytesThumb != null) {
                                                    }
                                                    if (access$1600 == null) {
                                                        access$1600 = new byte[length];
                                                        ImageLoader.bytesThumb = access$1600;
                                                    }
                                                    randomAccessFile2.readFully(access$1600, 0, length);
                                                    bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options3);
                                                }
                                                if (bitmap == null) {
                                                    try {
                                                        file2.delete();
                                                    } catch (Throwable th8) {
                                                        th2 = th8;
                                                        FileLog.m18e("tmessages", th2);
                                                        Thread.interrupted();
                                                        if (bitmap != null) {
                                                        }
                                                        onPostExecute(bitmap != null ? null : new BitmapDrawable(bitmap));
                                                    }
                                                } else if (i3 != 1) {
                                                    if (bitmap.getConfig() == Config.ARGB_8888) {
                                                        Utilities.blurBitmap(bitmap, 3, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                    }
                                                } else if (i3 != 2) {
                                                    if (bitmap.getConfig() == Config.ARGB_8888) {
                                                        Utilities.blurBitmap(bitmap, 1, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                    }
                                                } else if (i3 != 3) {
                                                    Utilities.pinBitmap(bitmap);
                                                } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                                    Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                    Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                    Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                }
                                            } else {
                                                return;
                                            }
                                        }
                                    }
                                    i3 = 0;
                                    try {
                                        ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                                        synchronized (this.sync) {
                                            if (this.isCancelled) {
                                                options3 = new Options();
                                                options3.inSampleSize = 1;
                                                if (VERSION.SDK_INT < 21) {
                                                    options3.inPurgeable = true;
                                                }
                                                if (obj5 != null) {
                                                    randomAccessFile3 = new RandomAccessFile(file2, "r");
                                                    map2 = randomAccessFile3.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                                    options2 = new Options();
                                                    options2.inJustDecodeBounds = true;
                                                    Utilities.loadWebpImage(null, map2, map2.limit(), options2, true);
                                                    createBitmap = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                                    if (options3.inPurgeable) {
                                                    }
                                                    Utilities.loadWebpImage(createBitmap, map2, map2.limit(), null, options3.inPurgeable);
                                                    randomAccessFile3.close();
                                                    bitmap = createBitmap;
                                                } else if (options3.inPurgeable) {
                                                    fileInputStream = new FileInputStream(file2);
                                                    createBitmap = BitmapFactory.decodeStream(fileInputStream, null, options3);
                                                    fileInputStream.close();
                                                    bitmap = createBitmap;
                                                } else {
                                                    randomAccessFile2 = new RandomAccessFile(file2, "r");
                                                    length = (int) randomAccessFile2.length();
                                                    if (ImageLoader.bytesThumb != null) {
                                                    }
                                                    if (access$1600 == null) {
                                                        access$1600 = new byte[length];
                                                        ImageLoader.bytesThumb = access$1600;
                                                    }
                                                    randomAccessFile2.readFully(access$1600, 0, length);
                                                    bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options3);
                                                }
                                                if (bitmap == null) {
                                                    file2.delete();
                                                } else if (i3 != 1) {
                                                    if (i3 != 2) {
                                                        if (i3 != 3) {
                                                            Utilities.pinBitmap(bitmap);
                                                        } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                                            if (options3.inPurgeable) {
                                                            }
                                                            Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                            if (options3.inPurgeable) {
                                                            }
                                                            Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                            if (options3.inPurgeable) {
                                                            }
                                                            Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                        }
                                                    } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                                        if (options3.inPurgeable) {
                                                        }
                                                        Utilities.blurBitmap(bitmap, 1, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                    }
                                                } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                                    if (options3.inPurgeable) {
                                                    }
                                                    Utilities.blurBitmap(bitmap, 3, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                }
                                            } else {
                                                return;
                                            }
                                        }
                                    } catch (Throwable th62) {
                                        th2 = th62;
                                        bitmap = null;
                                    }
                                }
                                Thread.interrupted();
                                if (bitmap != null) {
                                }
                                onPostExecute(bitmap != null ? null : new BitmapDrawable(bitmap));
                            } catch (Throwable th9) {
                                th62 = th9;
                                if (randomAccessFile4 != null) {
                                    try {
                                        randomAccessFile4.close();
                                    } catch (Throwable th22) {
                                        FileLog.m18e("tmessages", th22);
                                    }
                                }
                                throw th62;
                            }
                        }
                    } catch (Exception e4) {
                        e22 = e4;
                        randomAccessFile4 = null;
                        FileLog.m18e("tmessages", e22);
                        if (randomAccessFile4 != null) {
                            randomAccessFile4.close();
                        }
                        if (this.cacheImage.thumb) {
                            if (this.cacheImage.filter != null) {
                                if (!this.cacheImage.filter.contains("b2")) {
                                    i3 = 3;
                                } else if (!this.cacheImage.filter.contains("b1")) {
                                    i3 = 2;
                                } else if (this.cacheImage.filter.contains("b")) {
                                    i3 = 1;
                                }
                                ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                                synchronized (this.sync) {
                                    if (this.isCancelled) {
                                        return;
                                    }
                                    options3 = new Options();
                                    options3.inSampleSize = 1;
                                    if (VERSION.SDK_INT < 21) {
                                        options3.inPurgeable = true;
                                    }
                                    if (obj5 != null) {
                                        randomAccessFile3 = new RandomAccessFile(file2, "r");
                                        map2 = randomAccessFile3.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                        options2 = new Options();
                                        options2.inJustDecodeBounds = true;
                                        Utilities.loadWebpImage(null, map2, map2.limit(), options2, true);
                                        createBitmap = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.loadWebpImage(createBitmap, map2, map2.limit(), null, options3.inPurgeable);
                                        randomAccessFile3.close();
                                        bitmap = createBitmap;
                                    } else if (options3.inPurgeable) {
                                        randomAccessFile2 = new RandomAccessFile(file2, "r");
                                        length = (int) randomAccessFile2.length();
                                        if (ImageLoader.bytesThumb != null) {
                                        }
                                        if (access$1600 == null) {
                                            access$1600 = new byte[length];
                                            ImageLoader.bytesThumb = access$1600;
                                        }
                                        randomAccessFile2.readFully(access$1600, 0, length);
                                        bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options3);
                                    } else {
                                        fileInputStream = new FileInputStream(file2);
                                        createBitmap = BitmapFactory.decodeStream(fileInputStream, null, options3);
                                        fileInputStream.close();
                                        bitmap = createBitmap;
                                    }
                                    if (bitmap == null) {
                                        file2.delete();
                                    } else if (i3 != 1) {
                                        if (bitmap.getConfig() == Config.ARGB_8888) {
                                            if (options3.inPurgeable) {
                                            }
                                            Utilities.blurBitmap(bitmap, 3, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        }
                                    } else if (i3 != 2) {
                                        if (bitmap.getConfig() == Config.ARGB_8888) {
                                            if (options3.inPurgeable) {
                                            }
                                            Utilities.blurBitmap(bitmap, 1, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        }
                                    } else if (i3 != 3) {
                                        Utilities.pinBitmap(bitmap);
                                    } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                    }
                                }
                            }
                            i3 = 0;
                            ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                            synchronized (this.sync) {
                                if (this.isCancelled) {
                                    options3 = new Options();
                                    options3.inSampleSize = 1;
                                    if (VERSION.SDK_INT < 21) {
                                        options3.inPurgeable = true;
                                    }
                                    if (obj5 != null) {
                                        randomAccessFile3 = new RandomAccessFile(file2, "r");
                                        map2 = randomAccessFile3.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                        options2 = new Options();
                                        options2.inJustDecodeBounds = true;
                                        Utilities.loadWebpImage(null, map2, map2.limit(), options2, true);
                                        createBitmap = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.loadWebpImage(createBitmap, map2, map2.limit(), null, options3.inPurgeable);
                                        randomAccessFile3.close();
                                        bitmap = createBitmap;
                                    } else if (options3.inPurgeable) {
                                        fileInputStream = new FileInputStream(file2);
                                        createBitmap = BitmapFactory.decodeStream(fileInputStream, null, options3);
                                        fileInputStream.close();
                                        bitmap = createBitmap;
                                    } else {
                                        randomAccessFile2 = new RandomAccessFile(file2, "r");
                                        length = (int) randomAccessFile2.length();
                                        if (ImageLoader.bytesThumb != null) {
                                        }
                                        if (access$1600 == null) {
                                            access$1600 = new byte[length];
                                            ImageLoader.bytesThumb = access$1600;
                                        }
                                        randomAccessFile2.readFully(access$1600, 0, length);
                                        bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options3);
                                    }
                                    if (bitmap == null) {
                                        file2.delete();
                                    } else if (i3 != 1) {
                                        if (i3 != 2) {
                                            if (i3 != 3) {
                                                Utilities.pinBitmap(bitmap);
                                            } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                                if (options3.inPurgeable) {
                                                }
                                                Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                if (options3.inPurgeable) {
                                                }
                                                Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                if (options3.inPurgeable) {
                                                }
                                                Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                            }
                                        } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                            if (options3.inPurgeable) {
                                            }
                                            Utilities.blurBitmap(bitmap, 1, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        }
                                    } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 3, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                        if (this.cacheImage.httpUrl != null) {
                            if (!this.cacheImage.httpUrl.startsWith("thumb://")) {
                                indexOf = this.cacheImage.httpUrl.indexOf(":", 8);
                                if (indexOf >= 0) {
                                    l2 = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(8, indexOf)));
                                    obj4 = null;
                                }
                                obj = null;
                                obj2 = obj4;
                                l = l2;
                            } else if (!this.cacheImage.httpUrl.startsWith("vthumb://")) {
                                indexOf = this.cacheImage.httpUrl.indexOf(":", 9);
                                if (indexOf >= 0) {
                                    l2 = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(9, indexOf)));
                                    obj4 = 1;
                                }
                                obj = null;
                                obj2 = obj4;
                                l = l2;
                            } else if (this.cacheImage.httpUrl.startsWith("http")) {
                                obj = null;
                                obj2 = null;
                                l = null;
                            }
                            i = 20;
                            if (l != null) {
                                i = 0;
                            }
                            Thread.sleep((long) i);
                            ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                            synchronized (this.sync) {
                                if (this.isCancelled) {
                                    return;
                                }
                                options = new Options();
                                options.inSampleSize = 1;
                                f = 0.0f;
                                f2 = 0.0f;
                                obj4 = null;
                                if (this.cacheImage.filter != null) {
                                    split = this.cacheImage.filter.split("_");
                                    if (split.length >= 2) {
                                        f = AndroidUtilities.density * Float.parseFloat(split[0]);
                                        f2 = Float.parseFloat(split[1]) * AndroidUtilities.density;
                                    }
                                    if (this.cacheImage.filter.contains("b")) {
                                        obj4 = 1;
                                    }
                                    options.inJustDecodeBounds = true;
                                    if (l != null) {
                                        fileInputStream2 = new FileInputStream(file2);
                                        bitmap2 = BitmapFactory.decodeStream(fileInputStream2, null, options);
                                        fileInputStream2.close();
                                    } else if (obj2 == null) {
                                        Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                    } else {
                                        Images.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                    }
                                    f2 = Math.max(((float) options.outWidth) / f, ((float) options.outHeight) / f2);
                                    if (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                        f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                                    }
                                    options.inJustDecodeBounds = false;
                                    options.inSampleSize = (int) f2;
                                }
                                obj3 = obj4;
                                f3 = f;
                                synchronized (this.sync) {
                                    if (this.isCancelled) {
                                        return;
                                    }
                                    if (this.cacheImage.filter == null) {
                                    }
                                    options.inPreferredConfig = Config.ARGB_8888;
                                    if (VERSION.SDK_INT < 21) {
                                        options.inPurgeable = true;
                                    }
                                    options.inDither = false;
                                    if (l != null) {
                                        if (obj2 == null) {
                                        }
                                    }
                                    if (bitmap2 == null) {
                                        if (obj5 != null) {
                                            randomAccessFile = new RandomAccessFile(file2, "r");
                                            map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                            options2 = new Options();
                                            options2.inJustDecodeBounds = true;
                                            Utilities.loadWebpImage(null, map, map.limit(), options2, true);
                                            bitmap2 = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                            if (options.inPurgeable) {
                                            }
                                            Utilities.loadWebpImage(bitmap2, map, map.limit(), null, options.inPurgeable);
                                            randomAccessFile.close();
                                            bitmap = bitmap2;
                                        } else if (options.inPurgeable) {
                                            randomAccessFile2 = new RandomAccessFile(file2, "r");
                                            length = (int) randomAccessFile2.length();
                                            if (ImageLoader.bytes != null) {
                                            }
                                            if (access$1600 == null) {
                                                access$1600 = new byte[length];
                                                ImageLoader.bytes = access$1600;
                                            }
                                            randomAccessFile2.readFully(access$1600, 0, length);
                                            bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options);
                                        } else {
                                            fileInputStream = new FileInputStream(file2);
                                            bitmap2 = BitmapFactory.decodeStream(fileInputStream, null, options);
                                            fileInputStream.close();
                                        }
                                        if (bitmap != null) {
                                            obj4 = null;
                                            if (this.cacheImage.filter != null) {
                                                f2 = (float) bitmap.getWidth();
                                                height = (float) bitmap.getHeight();
                                                createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                                if (bitmap != createScaledBitmap) {
                                                    bitmap.recycle();
                                                    bitmap = createScaledBitmap;
                                                }
                                                if (bitmap.getConfig() == Config.ARGB_8888) {
                                                    if (options.inPurgeable) {
                                                    }
                                                    Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                }
                                                obj4 = 1;
                                            }
                                            Utilities.pinBitmap(bitmap);
                                        } else if (obj != null) {
                                            file2.delete();
                                        }
                                    }
                                    bitmap = bitmap2;
                                    if (bitmap != null) {
                                        obj4 = null;
                                        if (this.cacheImage.filter != null) {
                                            f2 = (float) bitmap.getWidth();
                                            height = (float) bitmap.getHeight();
                                            createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                            if (bitmap != createScaledBitmap) {
                                                bitmap.recycle();
                                                bitmap = createScaledBitmap;
                                            }
                                            if (bitmap.getConfig() == Config.ARGB_8888) {
                                                if (options.inPurgeable) {
                                                }
                                                Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                            }
                                            obj4 = 1;
                                        }
                                        Utilities.pinBitmap(bitmap);
                                    } else if (obj != null) {
                                        file2.delete();
                                    }
                                }
                            }
                        }
                        i2 = 1;
                        obj2 = null;
                        l = null;
                        i = 20;
                        if (l != null) {
                            i = 0;
                        }
                        Thread.sleep((long) i);
                        ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                        synchronized (this.sync) {
                            if (this.isCancelled) {
                                options = new Options();
                                options.inSampleSize = 1;
                                f = 0.0f;
                                f2 = 0.0f;
                                obj4 = null;
                                if (this.cacheImage.filter != null) {
                                    split = this.cacheImage.filter.split("_");
                                    if (split.length >= 2) {
                                        f = AndroidUtilities.density * Float.parseFloat(split[0]);
                                        f2 = Float.parseFloat(split[1]) * AndroidUtilities.density;
                                    }
                                    if (this.cacheImage.filter.contains("b")) {
                                        obj4 = 1;
                                    }
                                    options.inJustDecodeBounds = true;
                                    if (l != null) {
                                        fileInputStream2 = new FileInputStream(file2);
                                        bitmap2 = BitmapFactory.decodeStream(fileInputStream2, null, options);
                                        fileInputStream2.close();
                                    } else if (obj2 == null) {
                                        Images.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                    } else {
                                        Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                    }
                                    f2 = Math.max(((float) options.outWidth) / f, ((float) options.outHeight) / f2);
                                    if (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                        f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                                    }
                                    options.inJustDecodeBounds = false;
                                    options.inSampleSize = (int) f2;
                                }
                                obj3 = obj4;
                                f3 = f;
                                synchronized (this.sync) {
                                    if (this.isCancelled) {
                                        if (this.cacheImage.filter == null) {
                                        }
                                        options.inPreferredConfig = Config.ARGB_8888;
                                        if (VERSION.SDK_INT < 21) {
                                            options.inPurgeable = true;
                                        }
                                        options.inDither = false;
                                        if (l != null) {
                                            if (obj2 == null) {
                                            }
                                        }
                                        if (bitmap2 == null) {
                                            if (obj5 != null) {
                                                randomAccessFile = new RandomAccessFile(file2, "r");
                                                map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                                options2 = new Options();
                                                options2.inJustDecodeBounds = true;
                                                Utilities.loadWebpImage(null, map, map.limit(), options2, true);
                                                bitmap2 = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                                if (options.inPurgeable) {
                                                }
                                                Utilities.loadWebpImage(bitmap2, map, map.limit(), null, options.inPurgeable);
                                                randomAccessFile.close();
                                                bitmap = bitmap2;
                                            } else if (options.inPurgeable) {
                                                fileInputStream = new FileInputStream(file2);
                                                bitmap2 = BitmapFactory.decodeStream(fileInputStream, null, options);
                                                fileInputStream.close();
                                            } else {
                                                randomAccessFile2 = new RandomAccessFile(file2, "r");
                                                length = (int) randomAccessFile2.length();
                                                if (ImageLoader.bytes != null) {
                                                }
                                                if (access$1600 == null) {
                                                    access$1600 = new byte[length];
                                                    ImageLoader.bytes = access$1600;
                                                }
                                                randomAccessFile2.readFully(access$1600, 0, length);
                                                bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options);
                                            }
                                            if (bitmap != null) {
                                                obj4 = null;
                                                if (this.cacheImage.filter != null) {
                                                    f2 = (float) bitmap.getWidth();
                                                    height = (float) bitmap.getHeight();
                                                    createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                                    if (bitmap != createScaledBitmap) {
                                                        bitmap.recycle();
                                                        bitmap = createScaledBitmap;
                                                    }
                                                    if (bitmap.getConfig() == Config.ARGB_8888) {
                                                        if (options.inPurgeable) {
                                                        }
                                                        Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                    }
                                                    obj4 = 1;
                                                }
                                                Utilities.pinBitmap(bitmap);
                                            } else if (obj != null) {
                                                file2.delete();
                                            }
                                        }
                                        bitmap = bitmap2;
                                        if (bitmap != null) {
                                            obj4 = null;
                                            if (this.cacheImage.filter != null) {
                                                f2 = (float) bitmap.getWidth();
                                                height = (float) bitmap.getHeight();
                                                createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                                if (bitmap != createScaledBitmap) {
                                                    bitmap.recycle();
                                                    bitmap = createScaledBitmap;
                                                }
                                                if (bitmap.getConfig() == Config.ARGB_8888) {
                                                    if (options.inPurgeable) {
                                                    }
                                                    Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                                }
                                                obj4 = 1;
                                            }
                                            Utilities.pinBitmap(bitmap);
                                        } else if (obj != null) {
                                            file2.delete();
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            }
                            return;
                        }
                        Thread.interrupted();
                        if (bitmap != null) {
                        }
                        onPostExecute(bitmap != null ? null : new BitmapDrawable(bitmap));
                    } catch (Throwable th10) {
                        th62 = th10;
                        randomAccessFile4 = null;
                        if (randomAccessFile4 != null) {
                            randomAccessFile4.close();
                        }
                        throw th62;
                    }
                }
                if (this.cacheImage.thumb) {
                    if (this.cacheImage.filter != null) {
                        if (!this.cacheImage.filter.contains("b2")) {
                            i3 = 3;
                        } else if (!this.cacheImage.filter.contains("b1")) {
                            i3 = 2;
                        } else if (this.cacheImage.filter.contains("b")) {
                            i3 = 1;
                        }
                        ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                        synchronized (this.sync) {
                            if (this.isCancelled) {
                                return;
                            }
                            options3 = new Options();
                            options3.inSampleSize = 1;
                            if (VERSION.SDK_INT < 21) {
                                options3.inPurgeable = true;
                            }
                            if (obj5 != null) {
                                randomAccessFile3 = new RandomAccessFile(file2, "r");
                                map2 = randomAccessFile3.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                options2 = new Options();
                                options2.inJustDecodeBounds = true;
                                Utilities.loadWebpImage(null, map2, map2.limit(), options2, true);
                                createBitmap = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                if (options3.inPurgeable) {
                                }
                                Utilities.loadWebpImage(createBitmap, map2, map2.limit(), null, options3.inPurgeable);
                                randomAccessFile3.close();
                                bitmap = createBitmap;
                            } else if (options3.inPurgeable) {
                                randomAccessFile2 = new RandomAccessFile(file2, "r");
                                length = (int) randomAccessFile2.length();
                                access$1600 = (ImageLoader.bytesThumb != null || ImageLoader.bytesThumb.length < length) ? null : ImageLoader.bytesThumb;
                                if (access$1600 == null) {
                                    access$1600 = new byte[length];
                                    ImageLoader.bytesThumb = access$1600;
                                }
                                randomAccessFile2.readFully(access$1600, 0, length);
                                bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options3);
                            } else {
                                fileInputStream = new FileInputStream(file2);
                                createBitmap = BitmapFactory.decodeStream(fileInputStream, null, options3);
                                fileInputStream.close();
                                bitmap = createBitmap;
                            }
                            if (bitmap == null) {
                                if (file2.length() == 0 || this.cacheImage.filter == null) {
                                    file2.delete();
                                }
                            } else if (i3 != 1) {
                                if (bitmap.getConfig() == Config.ARGB_8888) {
                                    if (options3.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(bitmap, 3, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                }
                            } else if (i3 != 2) {
                                if (bitmap.getConfig() == Config.ARGB_8888) {
                                    if (options3.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(bitmap, 1, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                }
                            } else if (i3 != 3) {
                                if (bitmap.getConfig() == Config.ARGB_8888) {
                                    if (options3.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                    if (options3.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                    if (options3.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                }
                            } else if (i3 == 0 && options3.inPurgeable) {
                                Utilities.pinBitmap(bitmap);
                            }
                        }
                    }
                    i3 = 0;
                    ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                    synchronized (this.sync) {
                        if (this.isCancelled) {
                            options3 = new Options();
                            options3.inSampleSize = 1;
                            if (VERSION.SDK_INT < 21) {
                                options3.inPurgeable = true;
                            }
                            if (obj5 != null) {
                                randomAccessFile3 = new RandomAccessFile(file2, "r");
                                map2 = randomAccessFile3.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                options2 = new Options();
                                options2.inJustDecodeBounds = true;
                                Utilities.loadWebpImage(null, map2, map2.limit(), options2, true);
                                createBitmap = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                if (options3.inPurgeable) {
                                }
                                Utilities.loadWebpImage(createBitmap, map2, map2.limit(), null, options3.inPurgeable);
                                randomAccessFile3.close();
                                bitmap = createBitmap;
                            } else if (options3.inPurgeable) {
                                fileInputStream = new FileInputStream(file2);
                                createBitmap = BitmapFactory.decodeStream(fileInputStream, null, options3);
                                fileInputStream.close();
                                bitmap = createBitmap;
                            } else {
                                randomAccessFile2 = new RandomAccessFile(file2, "r");
                                length = (int) randomAccessFile2.length();
                                if (ImageLoader.bytesThumb != null) {
                                }
                                if (access$1600 == null) {
                                    access$1600 = new byte[length];
                                    ImageLoader.bytesThumb = access$1600;
                                }
                                randomAccessFile2.readFully(access$1600, 0, length);
                                bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options3);
                            }
                            if (bitmap == null) {
                                file2.delete();
                            } else if (i3 != 1) {
                                if (i3 != 2) {
                                    if (i3 != 3) {
                                        Utilities.pinBitmap(bitmap);
                                    } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        if (options3.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 7, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                    }
                                } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                    if (options3.inPurgeable) {
                                    }
                                    Utilities.blurBitmap(bitmap, 1, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                }
                            } else if (bitmap.getConfig() == Config.ARGB_8888) {
                                if (options3.inPurgeable) {
                                }
                                Utilities.blurBitmap(bitmap, 3, options3.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                            }
                        } else {
                            return;
                        }
                    }
                }
                if (this.cacheImage.httpUrl != null) {
                    if (!this.cacheImage.httpUrl.startsWith("thumb://")) {
                        indexOf = this.cacheImage.httpUrl.indexOf(":", 8);
                        if (indexOf >= 0) {
                            l2 = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(8, indexOf)));
                            obj4 = null;
                        }
                        obj = null;
                        obj2 = obj4;
                        l = l2;
                    } else if (!this.cacheImage.httpUrl.startsWith("vthumb://")) {
                        indexOf = this.cacheImage.httpUrl.indexOf(":", 9);
                        if (indexOf >= 0) {
                            l2 = Long.valueOf(Long.parseLong(this.cacheImage.httpUrl.substring(9, indexOf)));
                            obj4 = 1;
                        }
                        obj = null;
                        obj2 = obj4;
                        l = l2;
                    } else if (this.cacheImage.httpUrl.startsWith("http")) {
                        obj = null;
                        obj2 = null;
                        l = null;
                    }
                    i = 20;
                    if (l != null) {
                        i = 0;
                    }
                    if (!(i == 0 || ImageLoader.this.lastCacheOutTime == 0)) {
                        if (ImageLoader.this.lastCacheOutTime > System.currentTimeMillis() - ((long) i) && VERSION.SDK_INT < 21) {
                            Thread.sleep((long) i);
                        }
                    }
                    ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                    synchronized (this.sync) {
                        if (this.isCancelled) {
                            return;
                        }
                        options = new Options();
                        options.inSampleSize = 1;
                        f = 0.0f;
                        f2 = 0.0f;
                        obj4 = null;
                        if (this.cacheImage.filter != null) {
                            split = this.cacheImage.filter.split("_");
                            if (split.length >= 2) {
                                f = AndroidUtilities.density * Float.parseFloat(split[0]);
                                f2 = Float.parseFloat(split[1]) * AndroidUtilities.density;
                            }
                            if (this.cacheImage.filter.contains("b")) {
                                obj4 = 1;
                            }
                            if (!(f == 0.0f || f2 == 0.0f)) {
                                options.inJustDecodeBounds = true;
                                if (l != null) {
                                    fileInputStream2 = new FileInputStream(file2);
                                    bitmap2 = BitmapFactory.decodeStream(fileInputStream2, null, options);
                                    fileInputStream2.close();
                                } else if (obj2 == null) {
                                    Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                } else {
                                    Images.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                                }
                                f2 = Math.max(((float) options.outWidth) / f, ((float) options.outHeight) / f2);
                                if (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                    f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                                }
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = (int) f2;
                            }
                        }
                        obj3 = obj4;
                        f3 = f;
                        synchronized (this.sync) {
                            if (this.isCancelled) {
                                return;
                            }
                            if (this.cacheImage.filter == null && obj3 == null && this.cacheImage.httpUrl == null) {
                                options.inPreferredConfig = Config.RGB_565;
                            } else {
                                options.inPreferredConfig = Config.ARGB_8888;
                            }
                            if (VERSION.SDK_INT < 21) {
                                options.inPurgeable = true;
                            }
                            options.inDither = false;
                            if (l != null) {
                                if (obj2 == null) {
                                }
                            }
                            if (bitmap2 == null) {
                                if (obj5 != null) {
                                    randomAccessFile = new RandomAccessFile(file2, "r");
                                    map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                    options2 = new Options();
                                    options2.inJustDecodeBounds = true;
                                    Utilities.loadWebpImage(null, map, map.limit(), options2, true);
                                    bitmap2 = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                    if (options.inPurgeable) {
                                    }
                                    Utilities.loadWebpImage(bitmap2, map, map.limit(), null, options.inPurgeable);
                                    randomAccessFile.close();
                                    bitmap = bitmap2;
                                } else if (options.inPurgeable) {
                                    randomAccessFile2 = new RandomAccessFile(file2, "r");
                                    length = (int) randomAccessFile2.length();
                                    access$1600 = (ImageLoader.bytes != null || ImageLoader.bytes.length < length) ? null : ImageLoader.bytes;
                                    if (access$1600 == null) {
                                        access$1600 = new byte[length];
                                        ImageLoader.bytes = access$1600;
                                    }
                                    randomAccessFile2.readFully(access$1600, 0, length);
                                    bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options);
                                } else {
                                    fileInputStream = new FileInputStream(file2);
                                    bitmap2 = BitmapFactory.decodeStream(fileInputStream, null, options);
                                    fileInputStream.close();
                                }
                                if (bitmap != null) {
                                    obj4 = null;
                                    if (this.cacheImage.filter != null) {
                                        f2 = (float) bitmap.getWidth();
                                        height = (float) bitmap.getHeight();
                                        if (!(options.inPurgeable || f3 == 0.0f || f2 == f3 || f2 <= 20.0f + f3)) {
                                            createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                            if (bitmap != createScaledBitmap) {
                                                bitmap.recycle();
                                                bitmap = createScaledBitmap;
                                            }
                                        }
                                        if (bitmap != null && obj3 != null && height < 100.0f && f2 < 100.0f) {
                                            if (bitmap.getConfig() == Config.ARGB_8888) {
                                                if (options.inPurgeable) {
                                                }
                                                Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                            }
                                            obj4 = 1;
                                        }
                                    }
                                    if (obj4 == null && options.inPurgeable) {
                                        Utilities.pinBitmap(bitmap);
                                    }
                                } else if (obj != null) {
                                    if (file2.length() == 0 || this.cacheImage.filter == null) {
                                        file2.delete();
                                    }
                                }
                            }
                            bitmap = bitmap2;
                            if (bitmap != null) {
                                obj4 = null;
                                if (this.cacheImage.filter != null) {
                                    f2 = (float) bitmap.getWidth();
                                    height = (float) bitmap.getHeight();
                                    createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                    if (bitmap != createScaledBitmap) {
                                        bitmap.recycle();
                                        bitmap = createScaledBitmap;
                                    }
                                    if (bitmap.getConfig() == Config.ARGB_8888) {
                                        if (options.inPurgeable) {
                                        }
                                        Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                    }
                                    obj4 = 1;
                                }
                                Utilities.pinBitmap(bitmap);
                            } else if (obj != null) {
                                file2.delete();
                            }
                        }
                    }
                }
                i2 = 1;
                obj2 = null;
                l = null;
                i = 20;
                if (l != null) {
                    i = 0;
                }
                Thread.sleep((long) i);
                ImageLoader.this.lastCacheOutTime = System.currentTimeMillis();
                synchronized (this.sync) {
                    if (this.isCancelled) {
                        options = new Options();
                        options.inSampleSize = 1;
                        f = 0.0f;
                        f2 = 0.0f;
                        obj4 = null;
                        if (this.cacheImage.filter != null) {
                            split = this.cacheImage.filter.split("_");
                            if (split.length >= 2) {
                                f = AndroidUtilities.density * Float.parseFloat(split[0]);
                                f2 = Float.parseFloat(split[1]) * AndroidUtilities.density;
                            }
                            if (this.cacheImage.filter.contains("b")) {
                                obj4 = 1;
                            }
                            options.inJustDecodeBounds = true;
                            if (l != null) {
                                fileInputStream2 = new FileInputStream(file2);
                                bitmap2 = BitmapFactory.decodeStream(fileInputStream2, null, options);
                                fileInputStream2.close();
                            } else if (obj2 == null) {
                                Images.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                            } else {
                                Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), l.longValue(), 1, options);
                            }
                            f2 = Math.max(((float) options.outWidth) / f, ((float) options.outHeight) / f2);
                            if (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            }
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = (int) f2;
                        }
                        obj3 = obj4;
                        f3 = f;
                        synchronized (this.sync) {
                            if (this.isCancelled) {
                                if (this.cacheImage.filter == null) {
                                }
                                options.inPreferredConfig = Config.ARGB_8888;
                                if (VERSION.SDK_INT < 21) {
                                    options.inPurgeable = true;
                                }
                                options.inDither = false;
                                if (l != null) {
                                    if (obj2 == null) {
                                    }
                                }
                                if (bitmap2 == null) {
                                    if (obj5 != null) {
                                        randomAccessFile = new RandomAccessFile(file2, "r");
                                        map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, file2.length());
                                        options2 = new Options();
                                        options2.inJustDecodeBounds = true;
                                        Utilities.loadWebpImage(null, map, map.limit(), options2, true);
                                        bitmap2 = Bitmaps.createBitmap(options2.outWidth, options2.outHeight, Config.ARGB_8888);
                                        if (options.inPurgeable) {
                                        }
                                        Utilities.loadWebpImage(bitmap2, map, map.limit(), null, options.inPurgeable);
                                        randomAccessFile.close();
                                        bitmap = bitmap2;
                                    } else if (options.inPurgeable) {
                                        fileInputStream = new FileInputStream(file2);
                                        bitmap2 = BitmapFactory.decodeStream(fileInputStream, null, options);
                                        fileInputStream.close();
                                    } else {
                                        randomAccessFile2 = new RandomAccessFile(file2, "r");
                                        length = (int) randomAccessFile2.length();
                                        if (ImageLoader.bytes != null) {
                                        }
                                        if (access$1600 == null) {
                                            access$1600 = new byte[length];
                                            ImageLoader.bytes = access$1600;
                                        }
                                        randomAccessFile2.readFully(access$1600, 0, length);
                                        bitmap = BitmapFactory.decodeByteArray(access$1600, 0, length, options);
                                    }
                                    if (bitmap != null) {
                                        obj4 = null;
                                        if (this.cacheImage.filter != null) {
                                            f2 = (float) bitmap.getWidth();
                                            height = (float) bitmap.getHeight();
                                            createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                            if (bitmap != createScaledBitmap) {
                                                bitmap.recycle();
                                                bitmap = createScaledBitmap;
                                            }
                                            if (bitmap.getConfig() == Config.ARGB_8888) {
                                                if (options.inPurgeable) {
                                                }
                                                Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                            }
                                            obj4 = 1;
                                        }
                                        Utilities.pinBitmap(bitmap);
                                    } else if (obj != null) {
                                        file2.delete();
                                    }
                                }
                                bitmap = bitmap2;
                                if (bitmap != null) {
                                    obj4 = null;
                                    if (this.cacheImage.filter != null) {
                                        f2 = (float) bitmap.getWidth();
                                        height = (float) bitmap.getHeight();
                                        createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) f3, (int) (height / (f2 / f3)), true);
                                        if (bitmap != createScaledBitmap) {
                                            bitmap.recycle();
                                            bitmap = createScaledBitmap;
                                        }
                                        if (bitmap.getConfig() == Config.ARGB_8888) {
                                            if (options.inPurgeable) {
                                            }
                                            Utilities.blurBitmap(bitmap, 3, options.inPurgeable ? 0 : 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                                        }
                                        obj4 = 1;
                                    }
                                    Utilities.pinBitmap(bitmap);
                                } else if (obj != null) {
                                    file2.delete();
                                }
                            } else {
                                return;
                            }
                        }
                    }
                    return;
                }
                Thread.interrupted();
                if (bitmap != null) {
                }
                onPostExecute(bitmap != null ? null : new BitmapDrawable(bitmap));
            }
        }
    }

    private class HttpFileTask extends AsyncTask<Void, Void, Boolean> {
        private boolean canRetry;
        private String ext;
        private RandomAccessFile fileOutputStream;
        private int fileSize;
        private long lastProgressTime;
        private File tempFile;
        private String url;

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpFileTask.1 */
        class C04421 implements Runnable {
            final /* synthetic */ float val$progress;

            /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpFileTask.1.1 */
            class C04411 implements Runnable {
                C04411() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpFileTask.this.url, Float.valueOf(C04421.this.val$progress));
                }
            }

            C04421(float f) {
                this.val$progress = f;
            }

            public void run() {
                ImageLoader.this.fileProgresses.put(HttpFileTask.this.url, Float.valueOf(this.val$progress));
                AndroidUtilities.runOnUIThread(new C04411());
            }
        }

        public HttpFileTask(String str, File file, String str2) {
            this.fileOutputStream = null;
            this.canRetry = true;
            this.url = str;
            this.tempFile = file;
            this.ext = str2;
        }

        private void reportProgress(float f) {
            long currentTimeMillis = System.currentTimeMillis();
            if (f == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || this.lastProgressTime == 0 || this.lastProgressTime < currentTimeMillis - 500) {
                this.lastProgressTime = currentTimeMillis;
                Utilities.stageQueue.postRunnable(new C04421(f));
            }
        }

        protected Boolean doInBackground(Void... voidArr) {
            URLConnection openConnection;
            int responseCode;
            String headerField;
            InputStream inputStream;
            Throwable th;
            Throwable th2;
            Throwable th3;
            InputStream inputStream2;
            int responseCode2;
            Map headerFields;
            List list;
            byte[] bArr;
            boolean z;
            Throwable th4;
            boolean z2 = false;
            try {
                openConnection = new URL(this.url).openConnection();
                try {
                    openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                    openConnection.addRequestProperty("Referer", "google.com");
                    openConnection.setConnectTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                    openConnection.setReadTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                    if (openConnection instanceof HttpURLConnection) {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
                        httpURLConnection.setInstanceFollowRedirects(true);
                        responseCode = httpURLConnection.getResponseCode();
                        if (responseCode == 302 || responseCode == 301 || responseCode == 303) {
                            String headerField2 = httpURLConnection.getHeaderField("Location");
                            headerField = httpURLConnection.getHeaderField("Set-Cookie");
                            openConnection = new URL(headerField2).openConnection();
                            openConnection.setRequestProperty("Cookie", headerField);
                            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                            openConnection.addRequestProperty("Referer", "google.com");
                        }
                    }
                    openConnection.connect();
                    inputStream = openConnection.getInputStream();
                } catch (Throwable th22) {
                    th = th22;
                    inputStream = null;
                    th3 = th;
                    if (th3 instanceof SocketTimeoutException) {
                        if (ConnectionsManager.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                    } else if (!(th3 instanceof UnknownHostException)) {
                        this.canRetry = false;
                    } else if (!(th3 instanceof SocketException)) {
                        if (th3.getMessage() != null && th3.getMessage().contains("ECONNRESET")) {
                            this.canRetry = false;
                        }
                    } else if (th3 instanceof FileNotFoundException) {
                        this.canRetry = false;
                    }
                    FileLog.m18e("tmessages", th3);
                    inputStream2 = inputStream;
                    if (this.canRetry) {
                        if (openConnection != null) {
                            try {
                                if (openConnection instanceof HttpURLConnection) {
                                    responseCode2 = ((HttpURLConnection) openConnection).getResponseCode();
                                    this.canRetry = false;
                                }
                            } catch (Throwable th222) {
                                FileLog.m18e("tmessages", th222);
                            }
                        }
                        if (openConnection != null) {
                            try {
                                headerFields = openConnection.getHeaderFields();
                                if (headerFields != null) {
                                    list = (List) headerFields.get("content-Length");
                                    headerField = (String) list.get(0);
                                    if (headerField != null) {
                                        this.fileSize = Utilities.parseInt(headerField).intValue();
                                    }
                                }
                            } catch (Throwable th2222) {
                                FileLog.m18e("tmessages", th2222);
                            }
                        }
                        if (inputStream2 != null) {
                            try {
                                bArr = new byte[TLRPC.MESSAGE_FLAG_EDITED];
                                responseCode2 = 0;
                                while (!isCancelled()) {
                                    try {
                                        responseCode = inputStream2.read(bArr);
                                        if (responseCode <= 0) {
                                            if (responseCode != -1) {
                                                z = false;
                                            } else {
                                                try {
                                                    if (this.fileSize != 0) {
                                                        reportProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                                    }
                                                    z = true;
                                                } catch (Throwable th22222) {
                                                    th4 = th22222;
                                                    z = true;
                                                    try {
                                                        FileLog.m18e("tmessages", th4);
                                                        z2 = z;
                                                    } catch (Throwable th42) {
                                                        z2 = z;
                                                        th22222 = th42;
                                                        FileLog.m18e("tmessages", th22222);
                                                        if (this.fileOutputStream != null) {
                                                            this.fileOutputStream.close();
                                                            this.fileOutputStream = null;
                                                        }
                                                        if (inputStream2 != null) {
                                                            try {
                                                                inputStream2.close();
                                                            } catch (Throwable th222222) {
                                                                FileLog.m18e("tmessages", th222222);
                                                            }
                                                        }
                                                        return Boolean.valueOf(z2);
                                                    }
                                                    if (this.fileOutputStream != null) {
                                                        this.fileOutputStream.close();
                                                        this.fileOutputStream = null;
                                                    }
                                                    if (inputStream2 != null) {
                                                        inputStream2.close();
                                                    }
                                                    return Boolean.valueOf(z2);
                                                } catch (Throwable th5) {
                                                    th222222 = th5;
                                                    z2 = true;
                                                    FileLog.m18e("tmessages", th222222);
                                                    if (this.fileOutputStream != null) {
                                                        this.fileOutputStream.close();
                                                        this.fileOutputStream = null;
                                                    }
                                                    if (inputStream2 != null) {
                                                        inputStream2.close();
                                                    }
                                                    return Boolean.valueOf(z2);
                                                }
                                            }
                                            z2 = z;
                                        } else {
                                            this.fileOutputStream.write(bArr, 0, responseCode);
                                            responseCode2 += responseCode;
                                            if (this.fileSize <= 0) {
                                                reportProgress(((float) responseCode2) / ((float) this.fileSize));
                                            }
                                        }
                                    } catch (Throwable th2222222) {
                                        th42 = th2222222;
                                        z = false;
                                    }
                                }
                                z = false;
                                z2 = z;
                            } catch (Throwable th6) {
                                th2222222 = th6;
                                FileLog.m18e("tmessages", th2222222);
                                if (this.fileOutputStream != null) {
                                    this.fileOutputStream.close();
                                    this.fileOutputStream = null;
                                }
                                if (inputStream2 != null) {
                                    inputStream2.close();
                                }
                                return Boolean.valueOf(z2);
                            }
                        }
                        try {
                            if (this.fileOutputStream != null) {
                                this.fileOutputStream.close();
                                this.fileOutputStream = null;
                            }
                        } catch (Throwable th22222222) {
                            FileLog.m18e("tmessages", th22222222);
                        }
                        if (inputStream2 != null) {
                            inputStream2.close();
                        }
                    }
                    return Boolean.valueOf(z2);
                }
                try {
                    this.fileOutputStream = new RandomAccessFile(this.tempFile, "rws");
                    inputStream2 = inputStream;
                } catch (Throwable th7) {
                    th3 = th7;
                    if (th3 instanceof SocketTimeoutException) {
                        if (!(th3 instanceof UnknownHostException)) {
                            this.canRetry = false;
                        } else if (!(th3 instanceof SocketException)) {
                            this.canRetry = false;
                        } else if (th3 instanceof FileNotFoundException) {
                            this.canRetry = false;
                        }
                    } else if (ConnectionsManager.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                    FileLog.m18e("tmessages", th3);
                    inputStream2 = inputStream;
                    if (this.canRetry) {
                        if (openConnection != null) {
                            if (openConnection instanceof HttpURLConnection) {
                                responseCode2 = ((HttpURLConnection) openConnection).getResponseCode();
                                this.canRetry = false;
                            }
                        }
                        if (openConnection != null) {
                            headerFields = openConnection.getHeaderFields();
                            if (headerFields != null) {
                                list = (List) headerFields.get("content-Length");
                                headerField = (String) list.get(0);
                                if (headerField != null) {
                                    this.fileSize = Utilities.parseInt(headerField).intValue();
                                }
                            }
                        }
                        if (inputStream2 != null) {
                            bArr = new byte[TLRPC.MESSAGE_FLAG_EDITED];
                            responseCode2 = 0;
                            while (!isCancelled()) {
                                responseCode = inputStream2.read(bArr);
                                if (responseCode <= 0) {
                                    this.fileOutputStream.write(bArr, 0, responseCode);
                                    responseCode2 += responseCode;
                                    if (this.fileSize <= 0) {
                                        reportProgress(((float) responseCode2) / ((float) this.fileSize));
                                    }
                                } else {
                                    if (responseCode != -1) {
                                        if (this.fileSize != 0) {
                                            reportProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                        }
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    z2 = z;
                                }
                            }
                            z = false;
                            z2 = z;
                        }
                        if (this.fileOutputStream != null) {
                            this.fileOutputStream.close();
                            this.fileOutputStream = null;
                        }
                        if (inputStream2 != null) {
                            inputStream2.close();
                        }
                    }
                    return Boolean.valueOf(z2);
                }
            } catch (Throwable th222222222) {
                openConnection = null;
                th = th222222222;
                inputStream = null;
                th3 = th;
                if (th3 instanceof SocketTimeoutException) {
                    if (ConnectionsManager.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                } else if (!(th3 instanceof UnknownHostException)) {
                    this.canRetry = false;
                } else if (!(th3 instanceof SocketException)) {
                    this.canRetry = false;
                } else if (th3 instanceof FileNotFoundException) {
                    this.canRetry = false;
                }
                FileLog.m18e("tmessages", th3);
                inputStream2 = inputStream;
                if (this.canRetry) {
                    if (openConnection != null) {
                        if (openConnection instanceof HttpURLConnection) {
                            responseCode2 = ((HttpURLConnection) openConnection).getResponseCode();
                            this.canRetry = false;
                        }
                    }
                    if (openConnection != null) {
                        headerFields = openConnection.getHeaderFields();
                        if (headerFields != null) {
                            list = (List) headerFields.get("content-Length");
                            headerField = (String) list.get(0);
                            if (headerField != null) {
                                this.fileSize = Utilities.parseInt(headerField).intValue();
                            }
                        }
                    }
                    if (inputStream2 != null) {
                        bArr = new byte[TLRPC.MESSAGE_FLAG_EDITED];
                        responseCode2 = 0;
                        while (!isCancelled()) {
                            responseCode = inputStream2.read(bArr);
                            if (responseCode <= 0) {
                                if (responseCode != -1) {
                                    z = false;
                                } else {
                                    if (this.fileSize != 0) {
                                        reportProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                    }
                                    z = true;
                                }
                                z2 = z;
                            } else {
                                this.fileOutputStream.write(bArr, 0, responseCode);
                                responseCode2 += responseCode;
                                if (this.fileSize <= 0) {
                                    reportProgress(((float) responseCode2) / ((float) this.fileSize));
                                }
                            }
                        }
                        z = false;
                        z2 = z;
                    }
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                    if (inputStream2 != null) {
                        inputStream2.close();
                    }
                }
                return Boolean.valueOf(z2);
            }
            if (this.canRetry) {
                if (openConnection != null) {
                    if (openConnection instanceof HttpURLConnection) {
                        responseCode2 = ((HttpURLConnection) openConnection).getResponseCode();
                        if (!(responseCode2 == Callback.DEFAULT_DRAG_ANIMATION_DURATION || responseCode2 == 202 || responseCode2 == 304)) {
                            this.canRetry = false;
                        }
                    }
                }
                if (openConnection != null) {
                    headerFields = openConnection.getHeaderFields();
                    if (headerFields != null) {
                        list = (List) headerFields.get("content-Length");
                        if (!(list == null || list.isEmpty())) {
                            headerField = (String) list.get(0);
                            if (headerField != null) {
                                this.fileSize = Utilities.parseInt(headerField).intValue();
                            }
                        }
                    }
                }
                if (inputStream2 != null) {
                    bArr = new byte[TLRPC.MESSAGE_FLAG_EDITED];
                    responseCode2 = 0;
                    while (!isCancelled()) {
                        responseCode = inputStream2.read(bArr);
                        if (responseCode <= 0) {
                            this.fileOutputStream.write(bArr, 0, responseCode);
                            responseCode2 += responseCode;
                            if (this.fileSize <= 0) {
                                reportProgress(((float) responseCode2) / ((float) this.fileSize));
                            }
                        } else {
                            if (responseCode != -1) {
                                if (this.fileSize != 0) {
                                    reportProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                }
                                z = true;
                            } else {
                                z = false;
                            }
                            z2 = z;
                        }
                    }
                    z = false;
                    z2 = z;
                }
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
                if (inputStream2 != null) {
                    inputStream2.close();
                }
            }
            return Boolean.valueOf(z2);
        }

        protected void onCancelled() {
            ImageLoader.this.runHttpFileLoadTasks(this, 2);
        }

        protected void onPostExecute(Boolean bool) {
            ImageLoader.this.runHttpFileLoadTasks(this, bool.booleanValue() ? 2 : 1);
        }
    }

    private class HttpImageTask extends AsyncTask<Void, Void, Boolean> {
        private CacheImage cacheImage;
        private boolean canRetry;
        private RandomAccessFile fileOutputStream;
        private URLConnection httpConnection;
        private int imageSize;
        private long lastProgressTime;

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.1 */
        class C04441 implements Runnable {
            final /* synthetic */ float val$progress;

            /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.1.1 */
            class C04431 implements Runnable {
                C04431() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpImageTask.this.cacheImage.url, Float.valueOf(C04441.this.val$progress));
                }
            }

            C04441(float f) {
                this.val$progress = f;
            }

            public void run() {
                ImageLoader.this.fileProgresses.put(HttpImageTask.this.cacheImage.url, Float.valueOf(this.val$progress));
                AndroidUtilities.runOnUIThread(new C04431());
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.2 */
        class C04462 implements Runnable {
            final /* synthetic */ Boolean val$result;

            /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.2.1 */
            class C04451 implements Runnable {
                C04451() {
                }

                public void run() {
                    if (C04462.this.val$result.booleanValue()) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, HttpImageTask.this.cacheImage.url);
                        return;
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, HttpImageTask.this.cacheImage.url, Integer.valueOf(2));
                }
            }

            C04462(Boolean bool) {
                this.val$result = bool;
            }

            public void run() {
                ImageLoader.this.fileProgresses.remove(HttpImageTask.this.cacheImage.url);
                AndroidUtilities.runOnUIThread(new C04451());
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.3 */
        class C04473 implements Runnable {
            C04473() {
            }

            public void run() {
                ImageLoader.this.runHttpTasks(true);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.4 */
        class C04484 implements Runnable {
            C04484() {
            }

            public void run() {
                ImageLoader.this.runHttpTasks(true);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.5 */
        class C04505 implements Runnable {

            /* renamed from: com.hanista.mobogram.messenger.ImageLoader.HttpImageTask.5.1 */
            class C04491 implements Runnable {
                C04491() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, HttpImageTask.this.cacheImage.url, Integer.valueOf(1));
                }
            }

            C04505() {
            }

            public void run() {
                ImageLoader.this.fileProgresses.remove(HttpImageTask.this.cacheImage.url);
                AndroidUtilities.runOnUIThread(new C04491());
            }
        }

        public HttpImageTask(CacheImage cacheImage, int i) {
            this.cacheImage = null;
            this.fileOutputStream = null;
            this.canRetry = true;
            this.httpConnection = null;
            this.cacheImage = cacheImage;
            this.imageSize = i;
        }

        private void reportProgress(float f) {
            long currentTimeMillis = System.currentTimeMillis();
            if (f == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || this.lastProgressTime == 0 || this.lastProgressTime < currentTimeMillis - 500) {
                this.lastProgressTime = currentTimeMillis;
                Utilities.stageQueue.postRunnable(new C04441(f));
            }
        }

        protected Boolean doInBackground(Void... voidArr) {
            InputStream inputStream;
            Throwable th;
            int responseCode;
            Throwable e;
            Map headerFields;
            List list;
            String str;
            byte[] bArr;
            int read;
            boolean z;
            Throwable th2;
            InputStream inputStream2 = null;
            boolean z2 = false;
            if (!isCancelled()) {
                try {
                    this.httpConnection = new URL(this.cacheImage.httpUrl).openConnection();
                    this.httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                    this.httpConnection.addRequestProperty("Referer", "google.com");
                    this.httpConnection.setConnectTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                    this.httpConnection.setReadTimeout(Factory.DEFAULT_MIN_REBUFFER_MS);
                    if (this.httpConnection instanceof HttpURLConnection) {
                        ((HttpURLConnection) this.httpConnection).setInstanceFollowRedirects(true);
                    }
                    if (isCancelled()) {
                        inputStream = null;
                    } else {
                        this.httpConnection.connect();
                        inputStream = this.httpConnection.getInputStream();
                        try {
                            this.fileOutputStream = new RandomAccessFile(this.cacheImage.tempFilePath, "rws");
                        } catch (Throwable th3) {
                            th = th3;
                            if (th instanceof SocketTimeoutException) {
                                if (ConnectionsManager.isNetworkOnline()) {
                                    this.canRetry = false;
                                }
                            } else if (th instanceof UnknownHostException) {
                                this.canRetry = false;
                            } else if (th instanceof SocketException) {
                                this.canRetry = false;
                            } else if (th instanceof FileNotFoundException) {
                                this.canRetry = false;
                            }
                            FileLog.m18e("tmessages", th);
                            inputStream2 = inputStream;
                            if (!isCancelled()) {
                                try {
                                    responseCode = ((HttpURLConnection) this.httpConnection).getResponseCode();
                                    this.canRetry = false;
                                } catch (Throwable e2) {
                                    FileLog.m18e("tmessages", e2);
                                }
                                try {
                                    headerFields = this.httpConnection.getHeaderFields();
                                    if (headerFields != null) {
                                        list = (List) headerFields.get("content-Length");
                                        str = (String) list.get(0);
                                        if (str != null) {
                                            this.imageSize = Utilities.parseInt(str).intValue();
                                        }
                                    }
                                } catch (Throwable e22) {
                                    FileLog.m18e("tmessages", e22);
                                }
                                if (inputStream2 != null) {
                                    try {
                                        bArr = new byte[MessagesController.UPDATE_MASK_CHANNEL];
                                        responseCode = 0;
                                        while (!isCancelled()) {
                                            try {
                                                read = inputStream2.read(bArr);
                                                if (read > 0) {
                                                    responseCode += read;
                                                    this.fileOutputStream.write(bArr, 0, read);
                                                    if (this.imageSize == 0) {
                                                        reportProgress(((float) responseCode) / ((float) this.imageSize));
                                                    }
                                                } else {
                                                    if (read == -1) {
                                                        try {
                                                            if (this.imageSize != 0) {
                                                                reportProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                                            }
                                                            z = true;
                                                        } catch (Throwable e222) {
                                                            r2 = e222;
                                                            z = true;
                                                            Throwable th4;
                                                            try {
                                                                FileLog.m18e("tmessages", th4);
                                                                z2 = z;
                                                            } catch (Throwable th42) {
                                                                th2 = th42;
                                                                z2 = z;
                                                                e222 = th2;
                                                                FileLog.m18e("tmessages", e222);
                                                                if (this.fileOutputStream != null) {
                                                                    this.fileOutputStream.close();
                                                                    this.fileOutputStream = null;
                                                                }
                                                                if (inputStream2 != null) {
                                                                    try {
                                                                        inputStream2.close();
                                                                    } catch (Throwable e2222) {
                                                                        FileLog.m18e("tmessages", e2222);
                                                                    }
                                                                }
                                                                this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                                                                return Boolean.valueOf(z2);
                                                            }
                                                            if (this.fileOutputStream != null) {
                                                                this.fileOutputStream.close();
                                                                this.fileOutputStream = null;
                                                            }
                                                            if (inputStream2 != null) {
                                                                inputStream2.close();
                                                            }
                                                            this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                                                            return Boolean.valueOf(z2);
                                                        } catch (Throwable th5) {
                                                            e2222 = th5;
                                                            z2 = true;
                                                            FileLog.m18e("tmessages", e2222);
                                                            if (this.fileOutputStream != null) {
                                                                this.fileOutputStream.close();
                                                                this.fileOutputStream = null;
                                                            }
                                                            if (inputStream2 != null) {
                                                                inputStream2.close();
                                                            }
                                                            this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                                                            return Boolean.valueOf(z2);
                                                        }
                                                    }
                                                    z = false;
                                                    z2 = z;
                                                }
                                            } catch (Throwable e22222) {
                                                th2 = e22222;
                                                z = false;
                                                th42 = th2;
                                            }
                                        }
                                        z = false;
                                        z2 = z;
                                    } catch (Throwable th6) {
                                        e22222 = th6;
                                        FileLog.m18e("tmessages", e22222);
                                        if (this.fileOutputStream != null) {
                                            this.fileOutputStream.close();
                                            this.fileOutputStream = null;
                                        }
                                        if (inputStream2 != null) {
                                            inputStream2.close();
                                        }
                                        this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                                        return Boolean.valueOf(z2);
                                    }
                                }
                            }
                            if (this.fileOutputStream != null) {
                                this.fileOutputStream.close();
                                this.fileOutputStream = null;
                            }
                            if (inputStream2 != null) {
                                inputStream2.close();
                            }
                            this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                            return Boolean.valueOf(z2);
                        }
                    }
                    inputStream2 = inputStream;
                } catch (Throwable e222222) {
                    th2 = e222222;
                    inputStream = null;
                    th = th2;
                    if (th instanceof SocketTimeoutException) {
                        if (ConnectionsManager.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                    } else if (th instanceof UnknownHostException) {
                        this.canRetry = false;
                    } else if (th instanceof SocketException) {
                        if (th.getMessage() != null && th.getMessage().contains("ECONNRESET")) {
                            this.canRetry = false;
                        }
                    } else if (th instanceof FileNotFoundException) {
                        this.canRetry = false;
                    }
                    FileLog.m18e("tmessages", th);
                    inputStream2 = inputStream;
                    if (isCancelled()) {
                        responseCode = ((HttpURLConnection) this.httpConnection).getResponseCode();
                        this.canRetry = false;
                        headerFields = this.httpConnection.getHeaderFields();
                        if (headerFields != null) {
                            list = (List) headerFields.get("content-Length");
                            str = (String) list.get(0);
                            if (str != null) {
                                this.imageSize = Utilities.parseInt(str).intValue();
                            }
                        }
                        if (inputStream2 != null) {
                            bArr = new byte[MessagesController.UPDATE_MASK_CHANNEL];
                            responseCode = 0;
                            while (!isCancelled()) {
                                read = inputStream2.read(bArr);
                                if (read > 0) {
                                    if (read == -1) {
                                        z = false;
                                    } else {
                                        if (this.imageSize != 0) {
                                            reportProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                        }
                                        z = true;
                                    }
                                    z2 = z;
                                } else {
                                    responseCode += read;
                                    this.fileOutputStream.write(bArr, 0, read);
                                    if (this.imageSize == 0) {
                                        reportProgress(((float) responseCode) / ((float) this.imageSize));
                                    }
                                }
                            }
                            z = false;
                            z2 = z;
                        }
                    }
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                    if (inputStream2 != null) {
                        inputStream2.close();
                    }
                    this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                    return Boolean.valueOf(z2);
                }
            }
            if (isCancelled()) {
                if (this.httpConnection != null && (this.httpConnection instanceof HttpURLConnection)) {
                    responseCode = ((HttpURLConnection) this.httpConnection).getResponseCode();
                    if (!(responseCode == Callback.DEFAULT_DRAG_ANIMATION_DURATION || responseCode == 202 || responseCode == 304)) {
                        this.canRetry = false;
                    }
                }
                if (this.imageSize == 0 && this.httpConnection != null) {
                    headerFields = this.httpConnection.getHeaderFields();
                    if (headerFields != null) {
                        list = (List) headerFields.get("content-Length");
                        if (!(list == null || list.isEmpty())) {
                            str = (String) list.get(0);
                            if (str != null) {
                                this.imageSize = Utilities.parseInt(str).intValue();
                            }
                        }
                    }
                }
                if (inputStream2 != null) {
                    bArr = new byte[MessagesController.UPDATE_MASK_CHANNEL];
                    responseCode = 0;
                    while (!isCancelled()) {
                        read = inputStream2.read(bArr);
                        if (read > 0) {
                            responseCode += read;
                            this.fileOutputStream.write(bArr, 0, read);
                            if (this.imageSize == 0) {
                                reportProgress(((float) responseCode) / ((float) this.imageSize));
                            }
                        } else {
                            if (read == -1) {
                                if (this.imageSize != 0) {
                                    reportProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                }
                                z = true;
                            } else {
                                z = false;
                            }
                            z2 = z;
                        }
                    }
                    z = false;
                    z2 = z;
                }
            }
            try {
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e2222222) {
                FileLog.m18e("tmessages", e2222222);
            }
            if (inputStream2 != null) {
                inputStream2.close();
            }
            if (!(!z2 || this.cacheImage.tempFilePath == null || FileLoader.renameTo(this.cacheImage.tempFilePath, this.cacheImage.finalFilePath))) {
                this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
            }
            return Boolean.valueOf(z2);
        }

        protected void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new C04484());
            Utilities.stageQueue.postRunnable(new C04505());
        }

        protected void onPostExecute(Boolean bool) {
            if (bool.booleanValue() || !this.canRetry) {
                ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new C04462(bool));
            ImageLoader.this.imageLoadQueue.postRunnable(new C04473());
        }
    }

    private class ThumbGenerateInfo {
        private int count;
        private FileLocation fileLocation;
        private String filter;

        private ThumbGenerateInfo() {
        }
    }

    private class ThumbGenerateTask implements Runnable {
        private String filter;
        private int mediaType;
        private File originalPath;
        private FileLocation thumbLocation;

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.ThumbGenerateTask.1 */
        class C04511 implements Runnable {
            final /* synthetic */ String val$name;

            C04511(String str) {
                this.val$name = str;
            }

            public void run() {
                ImageLoader.this.thumbGenerateTasks.remove(this.val$name);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ImageLoader.ThumbGenerateTask.2 */
        class C04522 implements Runnable {
            final /* synthetic */ BitmapDrawable val$bitmapDrawable;
            final /* synthetic */ String val$key;

            C04522(String str, BitmapDrawable bitmapDrawable) {
                this.val$key = str;
                this.val$bitmapDrawable = bitmapDrawable;
            }

            public void run() {
                ThumbGenerateTask.this.removeTask();
                String str = this.val$key;
                if (ThumbGenerateTask.this.filter != null) {
                    str = str + "@" + ThumbGenerateTask.this.filter;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageThumbGenerated, this.val$bitmapDrawable, str);
                ImageLoader.this.memCache.put(str, this.val$bitmapDrawable);
            }
        }

        public ThumbGenerateTask(int i, File file, FileLocation fileLocation, String str) {
            this.mediaType = i;
            this.originalPath = file;
            this.thumbLocation = fileLocation;
            this.filter = str;
        }

        private void removeTask() {
            if (this.thumbLocation != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new C04511(FileLoader.getAttachFileName(this.thumbLocation)));
            }
        }

        public void run() {
            Bitmap bitmap = null;
            try {
                if (this.thumbLocation == null) {
                    removeTask();
                    return;
                }
                String str = this.thumbLocation.volume_id + "_" + this.thumbLocation.local_id;
                File file = new File(FileLoader.getInstance().getDirectory(4), "q_" + str + ".jpg");
                if (file.exists() || !this.originalPath.exists()) {
                    removeTask();
                    return;
                }
                int min = Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                if (this.mediaType == 0) {
                    bitmap = ImageLoader.loadBitmap(this.originalPath.toString(), null, (float) min, (float) min, false);
                } else if (this.mediaType == 2) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), 1);
                } else if (this.mediaType == 3) {
                    String toLowerCase = this.originalPath.toString().toLowerCase();
                    if (toLowerCase.endsWith(".jpg") || toLowerCase.endsWith(".jpeg") || toLowerCase.endsWith(".png") || toLowerCase.endsWith(".gif")) {
                        bitmap = ImageLoader.loadBitmap(toLowerCase, null, (float) min, (float) min, false);
                    } else {
                        removeTask();
                        return;
                    }
                }
                if (bitmap == null) {
                    removeTask();
                    return;
                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                if (width == 0 || height == 0) {
                    removeTask();
                    return;
                }
                float min2 = Math.min(((float) width) / ((float) min), ((float) height) / ((float) min));
                Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) (((float) width) / min2), (int) (((float) height) / min2), true);
                if (createScaledBitmap != bitmap) {
                    bitmap.recycle();
                }
                OutputStream fileOutputStream = new FileOutputStream(file);
                createScaledBitmap.compress(CompressFormat.JPEG, 60, fileOutputStream);
                fileOutputStream.close();
                AndroidUtilities.runOnUIThread(new C04522(str, new BitmapDrawable(createScaledBitmap)));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
                removeTask();
            }
        }
    }

    public class VMRuntimeHack {
        private Object runtime;
        private Method trackAllocation;
        private Method trackFree;

        public VMRuntimeHack() {
            this.runtime = null;
            this.trackAllocation = null;
            this.trackFree = null;
            try {
                Class cls = Class.forName("dalvik.system.VMRuntime");
                this.runtime = cls.getMethod("getRuntime", new Class[0]).invoke(null, new Object[0]);
                this.trackAllocation = cls.getMethod("trackExternalAllocation", new Class[]{Long.TYPE});
                this.trackFree = cls.getMethod("trackExternalFree", new Class[]{Long.TYPE});
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                this.runtime = null;
                this.trackAllocation = null;
                this.trackFree = null;
            }
        }

        public boolean trackAlloc(long j) {
            if (this.runtime == null) {
                return false;
            }
            try {
                Object invoke = this.trackAllocation.invoke(this.runtime, new Object[]{Long.valueOf(j)});
                return invoke instanceof Boolean ? ((Boolean) invoke).booleanValue() : true;
            } catch (Exception e) {
                return false;
            }
        }

        public boolean trackFree(long j) {
            if (this.runtime == null) {
                return false;
            }
            try {
                Object invoke = this.trackFree.invoke(this.runtime, new Object[]{Long.valueOf(j)});
                return invoke instanceof Boolean ? ((Boolean) invoke).booleanValue() : true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    static {
        header = new byte[12];
        headerThumb = new byte[12];
        Instance = null;
    }

    public ImageLoader() {
        this.bitmapUseCounts = new HashMap();
        this.imageLoadingByUrl = new HashMap();
        this.imageLoadingByKeys = new HashMap();
        this.imageLoadingByTag = new HashMap();
        this.waitingForQualityThumb = new HashMap();
        this.waitingForQualityThumbByTag = new HashMap();
        this.httpTasks = new LinkedList();
        this.cacheOutQueue = new DispatchQueue("cacheOutQueue");
        this.cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
        this.thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
        this.imageLoadQueue = new DispatchQueue("imageLoadQueue");
        this.fileProgresses = new ConcurrentHashMap();
        this.thumbGenerateTasks = new HashMap();
        this.currentHttpTasksCount = 0;
        this.httpFileLoadTasks = new LinkedList();
        this.httpFileLoadTasksByKeys = new HashMap();
        this.retryHttpsTasks = new HashMap();
        this.currentHttpFileLoadTasksCount = 0;
        this.ignoreRemoval = null;
        this.lastCacheOutTime = 0;
        this.lastImageNum = 0;
        this.lastProgressUpdateTime = 0;
        this.telegramPath = null;
        this.cacheOutQueue.setPriority(1);
        this.cacheThumbOutQueue.setPriority(1);
        this.thumbGeneratingQueue.setPriority(1);
        this.imageLoadQueue.setPriority(1);
        this.memCache = new C04191((Math.min(15, ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass() / 7) * TLRPC.MESSAGE_FLAG_HAS_VIEWS) * TLRPC.MESSAGE_FLAG_HAS_VIEWS);
        FileLoader.getInstance().setDelegate(new C04282());
        BroadcastReceiver c04303 = new C04303();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_NOFS");
        intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        intentFilter.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addDataScheme("file");
        ApplicationLoader.applicationContext.registerReceiver(c04303, intentFilter);
        HashMap hashMap = new HashMap();
        File cacheDir = AndroidUtilities.getCacheDir();
        if (!cacheDir.isDirectory()) {
            try {
                cacheDir.mkdirs();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        try {
            new File(cacheDir, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        hashMap.put(Integer.valueOf(4), cacheDir);
        FileLoader.getInstance().setMediaDirs(hashMap);
        checkMediaPaths();
    }

    private boolean canMoveFiles(File file, File file2, int i) {
        File file3;
        File file4;
        Throwable e;
        RandomAccessFile randomAccessFile = null;
        if (i == 0) {
            try {
                file3 = new File(file, "000000000_999999_temp.jpg");
                file4 = file3;
                file3 = new File(file2, "000000000_999999.jpg");
            } catch (Exception e2) {
                e = e2;
                try {
                    FileLog.m18e("tmessages", e);
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (Throwable e3) {
                            FileLog.m18e("tmessages", e3);
                        }
                    }
                    return false;
                } catch (Throwable th) {
                    e3 = th;
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (Throwable e4) {
                            FileLog.m18e("tmessages", e4);
                        }
                    }
                    throw e3;
                }
            }
        } else if (i == 3) {
            file3 = new File(file, "000000000_999999_temp.doc");
            file4 = file3;
            file3 = new File(file2, "000000000_999999.doc");
        } else if (i == 1) {
            file3 = new File(file, "000000000_999999_temp.ogg");
            file4 = file3;
            file3 = new File(file2, "000000000_999999.ogg");
        } else if (i == 2) {
            file4 = new File(file, "000000000_999999_temp.mp4");
            file3 = new File(file2, "000000000_999999.mp4");
        } else {
            file3 = null;
            file4 = null;
        }
        byte[] bArr = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
        file4.createNewFile();
        RandomAccessFile randomAccessFile2 = new RandomAccessFile(file4, "rws");
        try {
            randomAccessFile2.write(bArr);
            randomAccessFile2.close();
            randomAccessFile2 = null;
            boolean renameTo = FileLoader.renameTo(file4, file3);
            file4.delete();
            file3.delete();
            if (!renameTo) {
                if (null != null) {
                    try {
                        randomAccessFile2.close();
                    } catch (Throwable e32) {
                        FileLog.m18e("tmessages", e32);
                    }
                }
                return false;
            } else if (null == null) {
                return true;
            } else {
                try {
                    randomAccessFile2.close();
                    return true;
                } catch (Throwable e42) {
                    FileLog.m18e("tmessages", e42);
                    return true;
                }
            }
        } catch (Exception e5) {
            e32 = e5;
            randomAccessFile = randomAccessFile2;
            FileLog.m18e("tmessages", e32);
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            return false;
        } catch (Throwable th2) {
            e32 = th2;
            randomAccessFile = randomAccessFile2;
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            throw e32;
        }
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String str, String str2, String str3, TLObject tLObject, String str4, String str5, int i, boolean z, int i2) {
        if (imageReceiver != null && str2 != null && str != null) {
            Integer tag = imageReceiver.getTag(i2 != 0);
            if (tag == null) {
                tag = Integer.valueOf(this.lastImageNum);
                imageReceiver.setTag(tag, i2 != 0);
                this.lastImageNum++;
                if (this.lastImageNum == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    this.lastImageNum = 0;
                }
            }
            this.imageLoadQueue.postRunnable(new C04357(i2, str2, str, tag, imageReceiver, str5, str4, imageReceiver.isNeedsQualityThumb(), imageReceiver.getParentMessageObject(), tLObject, imageReceiver.isShouldGenerateQualityThumb(), z, i, str3));
        }
    }

    private void fileDidFailedLoad(String str, int i) {
        if (i != 1) {
            this.imageLoadQueue.postRunnable(new AnonymousClass10(str));
        }
    }

    private void fileDidLoaded(String str, File file, int i) {
        this.imageLoadQueue.postRunnable(new C04379(str, i, file));
    }

    public static void fillPhotoSizeWithBytes(PhotoSize photoSize) {
        if (photoSize != null && photoSize.bytes == null) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(FileLoader.getPathToAttach(photoSize, true), "r");
                if (((int) randomAccessFile.length()) < 20000) {
                    photoSize.bytes = new byte[((int) randomAccessFile.length())];
                    randomAccessFile.readFully(photoSize.bytes, 0, photoSize.bytes.length);
                }
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
        }
    }

    private void generateThumb(int i, File file, FileLocation fileLocation, String str) {
        if ((i == 0 || i == 2 || i == 3) && file != null && fileLocation != null) {
            if (((ThumbGenerateTask) this.thumbGenerateTasks.get(FileLoader.getAttachFileName(fileLocation))) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(i, file, fileLocation, str));
            }
        }
    }

    public static String getHttpUrlExtension(String str, String str2) {
        String str3 = null;
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf != -1) {
            str3 = str.substring(lastIndexOf + 1);
        }
        return (str3 == null || str3.length() == 0 || str3.length() > 4) ? str2 : str3;
    }

    public static ImageLoader getInstance() {
        ImageLoader imageLoader = Instance;
        if (imageLoader == null) {
            synchronized (ImageLoader.class) {
                imageLoader = Instance;
                if (imageLoader == null) {
                    imageLoader = new ImageLoader();
                    Instance = imageLoader;
                }
            }
        }
        return imageLoader;
    }

    private void httpFileLoadError(String str) {
        this.imageLoadQueue.postRunnable(new C04368(str));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r11, android.net.Uri r12, float r13, float r14, boolean r15) {
        /*
        r2 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r3 = 1;
        r0 = 0;
        r4 = 0;
        r9 = new android.graphics.BitmapFactory$Options;
        r9.<init>();
        r9.inJustDecodeBounds = r3;
        if (r11 != 0) goto L_0x009c;
    L_0x000e:
        if (r12 == 0) goto L_0x009c;
    L_0x0010:
        r1 = r12.getScheme();
        if (r1 == 0) goto L_0x009c;
    L_0x0016:
        r1 = r12.getScheme();
        r5 = "file";
        r1 = r1.contains(r5);
        if (r1 == 0) goto L_0x008f;
    L_0x0023:
        r11 = r12.getPath();
        r8 = r11;
    L_0x0028:
        if (r8 == 0) goto L_0x009e;
    L_0x002a:
        android.graphics.BitmapFactory.decodeFile(r8, r9);
        r7 = r0;
    L_0x002e:
        r1 = r9.outWidth;
        r1 = (float) r1;
        r5 = r9.outHeight;
        r5 = (float) r5;
        if (r15 == 0) goto L_0x00c6;
    L_0x0036:
        r1 = r1 / r13;
        r5 = r5 / r14;
        r1 = java.lang.Math.max(r1, r5);
    L_0x003c:
        r5 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x0041;
    L_0x0040:
        r1 = r2;
    L_0x0041:
        r9.inJustDecodeBounds = r4;
        r1 = (int) r1;
        r9.inSampleSize = r1;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r1 >= r2) goto L_0x00ce;
    L_0x004c:
        r1 = r3;
    L_0x004d:
        r9.inPurgeable = r1;
        if (r8 == 0) goto L_0x00d1;
    L_0x0051:
        r1 = r8;
    L_0x0052:
        if (r1 == 0) goto L_0x019d;
    L_0x0054:
        r2 = new android.media.ExifInterface;	 Catch:{ Throwable -> 0x0199 }
        r2.<init>(r1);	 Catch:{ Throwable -> 0x0199 }
        r1 = "Orientation";
        r3 = 1;
        r1 = r2.getAttributeInt(r1, r3);	 Catch:{ Throwable -> 0x0199 }
        r2 = new android.graphics.Matrix;	 Catch:{ Throwable -> 0x0199 }
        r2.<init>();	 Catch:{ Throwable -> 0x0199 }
        switch(r1) {
            case 3: goto L_0x00e8;
            case 4: goto L_0x0069;
            case 5: goto L_0x0069;
            case 6: goto L_0x00d9;
            case 7: goto L_0x0069;
            case 8: goto L_0x00ef;
            default: goto L_0x0069;
        };
    L_0x0069:
        r5 = r2;
    L_0x006a:
        if (r8 == 0) goto L_0x013c;
    L_0x006c:
        r0 = android.graphics.BitmapFactory.decodeFile(r8, r9);	 Catch:{ Throwable -> 0x00f6 }
        if (r0 == 0) goto L_0x008e;
    L_0x0072:
        r1 = r9.inPurgeable;	 Catch:{ Throwable -> 0x0193 }
        if (r1 == 0) goto L_0x0079;
    L_0x0076:
        com.hanista.mobogram.messenger.Utilities.pinBitmap(r0);	 Catch:{ Throwable -> 0x0193 }
    L_0x0079:
        r1 = 0;
        r2 = 0;
        r3 = r0.getWidth();	 Catch:{ Throwable -> 0x0193 }
        r4 = r0.getHeight();	 Catch:{ Throwable -> 0x0193 }
        r6 = 1;
        r1 = com.hanista.mobogram.messenger.Bitmaps.createBitmap(r0, r1, r2, r3, r4, r5, r6);	 Catch:{ Throwable -> 0x0193 }
        if (r1 == r0) goto L_0x008e;
    L_0x008a:
        r0.recycle();	 Catch:{ Throwable -> 0x0193 }
        r0 = r1;
    L_0x008e:
        return r0;
    L_0x008f:
        r11 = com.hanista.mobogram.messenger.AndroidUtilities.getPath(r12);	 Catch:{ Throwable -> 0x0095 }
        r8 = r11;
        goto L_0x0028;
    L_0x0095:
        r1 = move-exception;
        r5 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r5, r1);
    L_0x009c:
        r8 = r11;
        goto L_0x0028;
    L_0x009e:
        if (r12 == 0) goto L_0x01a3;
    L_0x00a0:
        r1 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x00be }
        r1 = r1.getContentResolver();	 Catch:{ Throwable -> 0x00be }
        r1 = r1.openInputStream(r12);	 Catch:{ Throwable -> 0x00be }
        r5 = 0;
        android.graphics.BitmapFactory.decodeStream(r1, r5, r9);	 Catch:{ Throwable -> 0x00be }
        r1.close();	 Catch:{ Throwable -> 0x00be }
        r1 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x00be }
        r1 = r1.getContentResolver();	 Catch:{ Throwable -> 0x00be }
        r1 = r1.openInputStream(r12);	 Catch:{ Throwable -> 0x00be }
        r7 = r1;
        goto L_0x002e;
    L_0x00be:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x008e;
    L_0x00c6:
        r1 = r1 / r13;
        r5 = r5 / r14;
        r1 = java.lang.Math.min(r1, r5);
        goto L_0x003c;
    L_0x00ce:
        r1 = r4;
        goto L_0x004d;
    L_0x00d1:
        if (r12 == 0) goto L_0x01a0;
    L_0x00d3:
        r1 = com.hanista.mobogram.messenger.AndroidUtilities.getPath(r12);
        goto L_0x0052;
    L_0x00d9:
        r1 = 1119092736; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r2.postRotate(r1);	 Catch:{ Throwable -> 0x00df }
        goto L_0x0069;
    L_0x00df:
        r1 = move-exception;
    L_0x00e0:
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r1);
        r5 = r2;
        goto L_0x006a;
    L_0x00e8:
        r1 = 1127481344; // 0x43340000 float:180.0 double:5.570497984E-315;
        r2.postRotate(r1);	 Catch:{ Throwable -> 0x00df }
        goto L_0x0069;
    L_0x00ef:
        r1 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2.postRotate(r1);	 Catch:{ Throwable -> 0x00df }
        goto L_0x0069;
    L_0x00f6:
        r1 = move-exception;
        r10 = r1;
        r1 = r0;
        r0 = r10;
    L_0x00fa:
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);
        r0 = getInstance();
        r0.clearMemory();
        if (r1 != 0) goto L_0x0116;
    L_0x0109:
        r1 = android.graphics.BitmapFactory.decodeFile(r8, r9);	 Catch:{ Throwable -> 0x0130 }
        if (r1 == 0) goto L_0x0116;
    L_0x010f:
        r0 = r9.inPurgeable;	 Catch:{ Throwable -> 0x0130 }
        if (r0 == 0) goto L_0x0116;
    L_0x0113:
        com.hanista.mobogram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x0130 }
    L_0x0116:
        r0 = r1;
        if (r0 == 0) goto L_0x008e;
    L_0x0119:
        r1 = 0;
        r2 = 0;
        r3 = r0.getWidth();	 Catch:{ Throwable -> 0x0191 }
        r4 = r0.getHeight();	 Catch:{ Throwable -> 0x0191 }
        r6 = 1;
        r1 = com.hanista.mobogram.messenger.Bitmaps.createBitmap(r0, r1, r2, r3, r4, r5, r6);	 Catch:{ Throwable -> 0x0191 }
        if (r1 == r0) goto L_0x008e;
    L_0x012a:
        r0.recycle();	 Catch:{ Throwable -> 0x0191 }
        r0 = r1;
        goto L_0x008e;
    L_0x0130:
        r0 = move-exception;
        r10 = r0;
        r0 = r1;
        r1 = r10;
    L_0x0134:
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x008e;
    L_0x013c:
        if (r12 == 0) goto L_0x008e;
    L_0x013e:
        r1 = 0;
        r0 = android.graphics.BitmapFactory.decodeStream(r7, r1, r9);	 Catch:{ Throwable -> 0x016f }
        if (r0 == 0) goto L_0x0161;
    L_0x0145:
        r1 = r9.inPurgeable;	 Catch:{ Throwable -> 0x016f }
        if (r1 == 0) goto L_0x014c;
    L_0x0149:
        com.hanista.mobogram.messenger.Utilities.pinBitmap(r0);	 Catch:{ Throwable -> 0x016f }
    L_0x014c:
        r1 = 0;
        r2 = 0;
        r3 = r0.getWidth();	 Catch:{ Throwable -> 0x016f }
        r4 = r0.getHeight();	 Catch:{ Throwable -> 0x016f }
        r6 = 1;
        r1 = com.hanista.mobogram.messenger.Bitmaps.createBitmap(r0, r1, r2, r3, r4, r5, r6);	 Catch:{ Throwable -> 0x016f }
        if (r1 == r0) goto L_0x0161;
    L_0x015d:
        r0.recycle();	 Catch:{ Throwable -> 0x016f }
        r0 = r1;
    L_0x0161:
        r7.close();	 Catch:{ Throwable -> 0x0166 }
        goto L_0x008e;
    L_0x0166:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x008e;
    L_0x016f:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);	 Catch:{ all -> 0x0184 }
        r7.close();	 Catch:{ Throwable -> 0x017b }
        goto L_0x008e;
    L_0x017b:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x008e;
    L_0x0184:
        r0 = move-exception;
        r7.close();	 Catch:{ Throwable -> 0x0189 }
    L_0x0188:
        throw r0;
    L_0x0189:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x0188;
    L_0x0191:
        r1 = move-exception;
        goto L_0x0134;
    L_0x0193:
        r1 = move-exception;
        r10 = r1;
        r1 = r0;
        r0 = r10;
        goto L_0x00fa;
    L_0x0199:
        r1 = move-exception;
        r2 = r0;
        goto L_0x00e0;
    L_0x019d:
        r5 = r0;
        goto L_0x006a;
    L_0x01a0:
        r1 = r0;
        goto L_0x0052;
    L_0x01a3:
        r7 = r0;
        goto L_0x002e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    private void performReplace(String str, String str2) {
        BitmapDrawable bitmapDrawable = this.memCache.get(str);
        if (bitmapDrawable != null) {
            this.ignoreRemoval = str;
            this.memCache.remove(str);
            this.memCache.put(str2, bitmapDrawable);
            this.ignoreRemoval = null;
        }
        Integer num = (Integer) this.bitmapUseCounts.get(str);
        if (num != null) {
            this.bitmapUseCounts.put(str2, num);
            this.bitmapUseCounts.remove(str);
        }
    }

    private void removeFromWaitingForThumb(Integer num) {
        String str = (String) this.waitingForQualityThumbByTag.get(num);
        if (str != null) {
            ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) this.waitingForQualityThumb.get(str);
            if (thumbGenerateInfo != null) {
                thumbGenerateInfo.count = thumbGenerateInfo.count - 1;
                if (thumbGenerateInfo.count == 0) {
                    this.waitingForQualityThumb.remove(str);
                }
            }
            this.waitingForQualityThumbByTag.remove(num);
        }
    }

    private void replaceImageInCacheInternal(String str, String str2, FileLocation fileLocation) {
        ArrayList filterKeys = this.memCache.getFilterKeys(str);
        if (filterKeys != null) {
            for (int i = 0; i < filterKeys.size(); i++) {
                String str3 = (String) filterKeys.get(i);
                performReplace(str + "@" + str3, str2 + "@" + str3);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, r4, str3, fileLocation);
            }
            return;
        }
        performReplace(str, str2);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str, str2, fileLocation);
    }

    private void runHttpFileLoadTasks(HttpFileTask httpFileTask, int i) {
        AndroidUtilities.runOnUIThread(new AnonymousClass11(httpFileTask, i));
    }

    private void runHttpTasks(boolean z) {
        if (z) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            ((HttpImageTask) this.httpTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpTasksCount++;
        }
    }

    public static void saveMessageThumbs(Message message) {
        int i = 0;
        TLObject tLObject = null;
        Iterator it;
        TLObject tLObject2;
        if (!(message.media instanceof TL_messageMediaPhoto)) {
            if (!(message.media instanceof TL_messageMediaDocument)) {
                if ((message.media instanceof TL_messageMediaWebPage) && message.media.webpage.photo != null) {
                    it = message.media.webpage.photo.sizes.iterator();
                    while (it.hasNext()) {
                        tLObject2 = (PhotoSize) it.next();
                        if (tLObject2 instanceof TL_photoCachedSize) {
                            tLObject = tLObject2;
                            break;
                        }
                    }
                }
            } else if (message.media.document.thumb instanceof TL_photoCachedSize) {
                tLObject = message.media.document.thumb;
            }
        } else {
            it = message.media.photo.sizes.iterator();
            while (it.hasNext()) {
                tLObject2 = (PhotoSize) it.next();
                if (tLObject2 instanceof TL_photoCachedSize) {
                    break;
                }
            }
            tLObject2 = null;
            tLObject = tLObject2;
        }
        if (tLObject != null && tLObject.bytes != null && tLObject.bytes.length != 0) {
            if (tLObject.location instanceof TL_fileLocationUnavailable) {
                tLObject.location = new TL_fileLocation();
                tLObject.location.volume_id = -2147483648L;
                tLObject.location.dc_id = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                tLObject.location.local_id = UserConfig.lastLocalId;
                UserConfig.lastLocalId--;
            }
            File pathToAttach = FileLoader.getPathToAttach(tLObject, true);
            if (!pathToAttach.exists()) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(pathToAttach, "rws");
                    randomAccessFile.write(tLObject.bytes);
                    randomAccessFile.close();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            PhotoSize tL_photoSize = new TL_photoSize();
            tL_photoSize.w = tLObject.f2664w;
            tL_photoSize.h = tLObject.f2663h;
            tL_photoSize.location = tLObject.location;
            tL_photoSize.size = tLObject.size;
            tL_photoSize.type = tLObject.type;
            if (message.media instanceof TL_messageMediaPhoto) {
                for (int i2 = 0; i2 < message.media.photo.sizes.size(); i2++) {
                    if (message.media.photo.sizes.get(i2) instanceof TL_photoCachedSize) {
                        message.media.photo.sizes.set(i2, tL_photoSize);
                        return;
                    }
                }
            } else if (message.media instanceof TL_messageMediaDocument) {
                message.media.document.thumb = tL_photoSize;
            } else if (message.media instanceof TL_messageMediaWebPage) {
                while (i < message.media.webpage.photo.sizes.size()) {
                    if (message.media.webpage.photo.sizes.get(i) instanceof TL_photoCachedSize) {
                        message.media.webpage.photo.sizes.set(i, tL_photoSize);
                        return;
                    }
                    i++;
                }
            }
        }
    }

    public static void saveMessagesThumbs(ArrayList<Message> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                saveMessageThumbs((Message) arrayList.get(i));
            }
        }
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage(bitmap, f, f2, i, z, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        if (bitmap == null) {
            return null;
        }
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        if (width == 0.0f || height == 0.0f) {
            return null;
        }
        boolean z2 = false;
        float max = Math.max(width / f, height / f2);
        if (!(i2 == 0 || i3 == 0 || (width >= ((float) i2) && height >= ((float) i3)))) {
            float max2 = (width >= ((float) i2) || height <= ((float) i3)) ? (width <= ((float) i2) || height >= ((float) i3)) ? Math.max(width / ((float) i2), height / ((float) i3)) : height / ((float) i3) : width / ((float) i2);
            z2 = true;
            max = max2;
        }
        int i4 = (int) (width / max);
        int i5 = (int) (height / max);
        if (i5 == 0 || i4 == 0) {
            return null;
        }
        try {
            return scaleAndSaveImageInternal(bitmap, i4, i5, width, height, max, i, z, z2);
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
            return null;
        }
    }

    private static PhotoSize scaleAndSaveImageInternal(Bitmap bitmap, int i, int i2, float f, float f2, float f3, int i3, boolean z, boolean z2) {
        Bitmap createScaledBitmap = (f3 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || z2) ? Bitmaps.createScaledBitmap(bitmap, i, i2, true) : bitmap;
        FileLocation tL_fileLocation = new TL_fileLocation();
        tL_fileLocation.volume_id = -2147483648L;
        tL_fileLocation.dc_id = TLRPC.MESSAGE_FLAG_MEGAGROUP;
        tL_fileLocation.local_id = UserConfig.lastLocalId;
        UserConfig.lastLocalId--;
        PhotoSize tL_photoSize = new TL_photoSize();
        tL_photoSize.location = tL_fileLocation;
        tL_photoSize.f2664w = createScaledBitmap.getWidth();
        tL_photoSize.f2663h = createScaledBitmap.getHeight();
        if (tL_photoSize.f2664w <= 100 && tL_photoSize.f2663h <= 100) {
            tL_photoSize.type = "s";
        } else if (tL_photoSize.f2664w <= 320 && tL_photoSize.f2663h <= 320) {
            tL_photoSize.type = "m";
        } else if (tL_photoSize.f2664w <= 800 && tL_photoSize.f2663h <= 800) {
            tL_photoSize.type = "x";
        } else if (tL_photoSize.f2664w > 1280 || tL_photoSize.f2663h > 1280) {
            tL_photoSize.type = "w";
        } else {
            tL_photoSize.type = "y";
        }
        OutputStream fileOutputStream = new FileOutputStream(new File(FileLoader.getInstance().getDirectory(4), tL_fileLocation.volume_id + "_" + tL_fileLocation.local_id + ".jpg"));
        createScaledBitmap.compress(CompressFormat.JPEG, i3, fileOutputStream);
        if (z) {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            createScaledBitmap.compress(CompressFormat.JPEG, i3, byteArrayOutputStream);
            tL_photoSize.bytes = byteArrayOutputStream.toByteArray();
            tL_photoSize.size = tL_photoSize.bytes.length;
            byteArrayOutputStream.close();
        } else {
            tL_photoSize.size = (int) fileOutputStream.getChannel().size();
        }
        fileOutputStream.close();
        if (createScaledBitmap != bitmap) {
            createScaledBitmap.recycle();
        }
        return tL_photoSize;
    }

    public void cancelLoadHttpFile(String str) {
        HttpFileTask httpFileTask = (HttpFileTask) this.httpFileLoadTasksByKeys.get(str);
        if (httpFileTask != null) {
            httpFileTask.cancel(true);
            this.httpFileLoadTasksByKeys.remove(str);
            this.httpFileLoadTasks.remove(httpFileTask);
        }
        Runnable runnable = (Runnable) this.retryHttpsTasks.get(str);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks(null, 0);
    }

    public void cancelLoadingForImageReceiver(ImageReceiver imageReceiver, int i) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new C04335(i, imageReceiver));
        }
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new C04324());
    }

    public void clearMemory() {
        this.memCache.evictAll();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.HashMap<java.lang.Integer, java.io.File> createMediaPaths() {
        /*
        r6 = this;
        r1 = new java.util.HashMap;
        r1.<init>();
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getCacheDir();
        r0 = r2.isDirectory();
        if (r0 != 0) goto L_0x0012;
    L_0x000f:
        r2.mkdirs();	 Catch:{ Exception -> 0x016d }
    L_0x0012:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x0176 }
        r3 = ".nomedia";
        r0.<init>(r2, r3);	 Catch:{ Exception -> 0x0176 }
        r0.createNewFile();	 Catch:{ Exception -> 0x0176 }
    L_0x001d:
        r0 = 4;
        r0 = java.lang.Integer.valueOf(r0);
        r1.put(r0, r2);
        r0 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "cache path = ";
        r3 = r3.append(r4);
        r3 = r3.append(r2);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r0, r3);
        r0 = com.hanista.mobogram.mobo.MoboConstants.m1382c();	 Catch:{ Exception -> 0x0188 }
        if (r0 == 0) goto L_0x01aa;
    L_0x0045:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x0188 }
        r3 = com.hanista.mobogram.mobo.MoboConstants.m1381b();	 Catch:{ Exception -> 0x0188 }
        r4 = com.hanista.mobogram.mobo.MoboConstants.f1325R;	 Catch:{ Exception -> 0x0188 }
        r0.<init>(r3, r4);	 Catch:{ Exception -> 0x0188 }
        r6.telegramPath = r0;	 Catch:{ Exception -> 0x0188 }
        r0 = r6.telegramPath;	 Catch:{ Exception -> 0x0188 }
        r0.mkdirs();	 Catch:{ Exception -> 0x0188 }
        r0 = r6.telegramPath;	 Catch:{ Exception -> 0x0188 }
        r0 = r0.isDirectory();	 Catch:{ Exception -> 0x0188 }
        if (r0 == 0) goto L_0x0165;
    L_0x005f:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x017f }
        r3 = r6.telegramPath;	 Catch:{ Exception -> 0x017f }
        r4 = "Telegram Images";
        r0.<init>(r3, r4);	 Catch:{ Exception -> 0x017f }
        r0.mkdir();	 Catch:{ Exception -> 0x017f }
        r3 = r0.isDirectory();	 Catch:{ Exception -> 0x017f }
        if (r3 == 0) goto L_0x009b;
    L_0x0072:
        r3 = 0;
        r3 = r6.canMoveFiles(r2, r0, r3);	 Catch:{ Exception -> 0x017f }
        if (r3 == 0) goto L_0x009b;
    L_0x0079:
        r3 = 0;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x017f }
        r1.put(r3, r0);	 Catch:{ Exception -> 0x017f }
        r3 = "tmessages";
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x017f }
        r4.<init>();	 Catch:{ Exception -> 0x017f }
        r5 = "image path = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x017f }
        r0 = r4.append(r0);	 Catch:{ Exception -> 0x017f }
        r0 = r0.toString();	 Catch:{ Exception -> 0x017f }
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r0);	 Catch:{ Exception -> 0x017f }
    L_0x009b:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x0190 }
        r3 = r6.telegramPath;	 Catch:{ Exception -> 0x0190 }
        r4 = "Telegram Video";
        r0.<init>(r3, r4);	 Catch:{ Exception -> 0x0190 }
        r0.mkdir();	 Catch:{ Exception -> 0x0190 }
        r3 = r0.isDirectory();	 Catch:{ Exception -> 0x0190 }
        if (r3 == 0) goto L_0x00d7;
    L_0x00ae:
        r3 = 2;
        r3 = r6.canMoveFiles(r2, r0, r3);	 Catch:{ Exception -> 0x0190 }
        if (r3 == 0) goto L_0x00d7;
    L_0x00b5:
        r3 = 2;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x0190 }
        r1.put(r3, r0);	 Catch:{ Exception -> 0x0190 }
        r3 = "tmessages";
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0190 }
        r4.<init>();	 Catch:{ Exception -> 0x0190 }
        r5 = "video path = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0190 }
        r0 = r4.append(r0);	 Catch:{ Exception -> 0x0190 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0190 }
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r0);	 Catch:{ Exception -> 0x0190 }
    L_0x00d7:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x0199 }
        r3 = r6.telegramPath;	 Catch:{ Exception -> 0x0199 }
        r4 = "Telegram Audio";
        r0.<init>(r3, r4);	 Catch:{ Exception -> 0x0199 }
        r0.mkdir();	 Catch:{ Exception -> 0x0199 }
        r3 = r0.isDirectory();	 Catch:{ Exception -> 0x0199 }
        if (r3 == 0) goto L_0x011e;
    L_0x00ea:
        r3 = 1;
        r3 = r6.canMoveFiles(r2, r0, r3);	 Catch:{ Exception -> 0x0199 }
        if (r3 == 0) goto L_0x011e;
    L_0x00f1:
        r3 = new java.io.File;	 Catch:{ Exception -> 0x0199 }
        r4 = ".nomedia";
        r3.<init>(r0, r4);	 Catch:{ Exception -> 0x0199 }
        r3.createNewFile();	 Catch:{ Exception -> 0x0199 }
        r3 = 1;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x0199 }
        r1.put(r3, r0);	 Catch:{ Exception -> 0x0199 }
        r3 = "tmessages";
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0199 }
        r4.<init>();	 Catch:{ Exception -> 0x0199 }
        r5 = "audio path = ";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0199 }
        r0 = r4.append(r0);	 Catch:{ Exception -> 0x0199 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0199 }
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r0);	 Catch:{ Exception -> 0x0199 }
    L_0x011e:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x01a2 }
        r3 = r6.telegramPath;	 Catch:{ Exception -> 0x01a2 }
        r4 = "Telegram Documents";
        r0.<init>(r3, r4);	 Catch:{ Exception -> 0x01a2 }
        r0.mkdir();	 Catch:{ Exception -> 0x01a2 }
        r3 = r0.isDirectory();	 Catch:{ Exception -> 0x01a2 }
        if (r3 == 0) goto L_0x0165;
    L_0x0131:
        r3 = 3;
        r2 = r6.canMoveFiles(r2, r0, r3);	 Catch:{ Exception -> 0x01a2 }
        if (r2 == 0) goto L_0x0165;
    L_0x0138:
        r2 = new java.io.File;	 Catch:{ Exception -> 0x01a2 }
        r3 = ".nomedia";
        r2.<init>(r0, r3);	 Catch:{ Exception -> 0x01a2 }
        r2.createNewFile();	 Catch:{ Exception -> 0x01a2 }
        r2 = 3;
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x01a2 }
        r1.put(r2, r0);	 Catch:{ Exception -> 0x01a2 }
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01a2 }
        r3.<init>();	 Catch:{ Exception -> 0x01a2 }
        r4 = "documents path = ";
        r3 = r3.append(r4);	 Catch:{ Exception -> 0x01a2 }
        r0 = r3.append(r0);	 Catch:{ Exception -> 0x01a2 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x01a2 }
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r0);	 Catch:{ Exception -> 0x01a2 }
    L_0x0165:
        r0 = com.hanista.mobogram.messenger.MediaController.m71a();	 Catch:{ Exception -> 0x0188 }
        r0.m198z();	 Catch:{ Exception -> 0x0188 }
    L_0x016c:
        return r1;
    L_0x016d:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);
        goto L_0x0012;
    L_0x0176:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);
        goto L_0x001d;
    L_0x017f:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ Exception -> 0x0188 }
        goto L_0x009b;
    L_0x0188:
        r0 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);
        goto L_0x016c;
    L_0x0190:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ Exception -> 0x0188 }
        goto L_0x00d7;
    L_0x0199:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ Exception -> 0x0188 }
        goto L_0x011e;
    L_0x01a2:
        r0 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);	 Catch:{ Exception -> 0x0188 }
        goto L_0x0165;
    L_0x01aa:
        r0 = "tmessages";
        r2 = "this Android can't rename files";
        com.hanista.mobogram.messenger.FileLog.m16e(r0, r2);	 Catch:{ Exception -> 0x0188 }
        goto L_0x0165;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.ImageLoader.createMediaPaths():java.util.HashMap<java.lang.Integer, java.io.File>");
    }

    public boolean decrementUseCount(String str) {
        Integer num = (Integer) this.bitmapUseCounts.get(str);
        if (num == null) {
            return true;
        }
        if (num.intValue() == 1) {
            this.bitmapUseCounts.remove(str);
            return true;
        }
        this.bitmapUseCounts.put(str, Integer.valueOf(num.intValue() - 1));
        return false;
    }

    public Float getFileProgress(String str) {
        return str == null ? null : (Float) this.fileProgresses.get(str);
    }

    public BitmapDrawable getImageFromMemory(TLObject tLObject, String str, String str2) {
        String str3 = null;
        if (tLObject == null && str == null) {
            return null;
        }
        if (str != null) {
            str3 = Utilities.MD5(str);
        } else if (tLObject instanceof FileLocation) {
            FileLocation fileLocation = (FileLocation) tLObject;
            str3 = fileLocation.volume_id + "_" + fileLocation.local_id;
        } else if (tLObject instanceof Document) {
            Document document = (Document) tLObject;
            str3 = document.version == 0 ? document.dc_id + "_" + document.id : document.dc_id + "_" + document.id + "_" + document.version;
        }
        if (str2 != null) {
            str3 = str3 + "@" + str2;
        }
        return this.memCache.get(str3);
    }

    public BitmapDrawable getImageFromMemory(String str) {
        return this.memCache.get(str);
    }

    public void incrementUseCount(String str) {
        Integer num = (Integer) this.bitmapUseCounts.get(str);
        if (num == null) {
            this.bitmapUseCounts.put(str, Integer.valueOf(1));
        } else {
            this.bitmapUseCounts.put(str, Integer.valueOf(num.intValue() + 1));
        }
    }

    public boolean isInCache(String str) {
        return this.memCache.get(str) != null;
    }

    public boolean isLoadingHttpFile(String str) {
        return this.httpFileLoadTasksByKeys.containsKey(str);
    }

    public void loadHttpFile(String str, String str2) {
        if (str != null && str.length() != 0 && !this.httpFileLoadTasksByKeys.containsKey(str)) {
            String httpUrlExtension = getHttpUrlExtension(str, str2);
            File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(str) + "_temp." + httpUrlExtension);
            file.delete();
            HttpFileTask httpFileTask = new HttpFileTask(str, file, httpUrlExtension);
            this.httpFileLoadTasks.add(httpFileTask);
            this.httpFileLoadTasksByKeys.put(str, httpFileTask);
            runHttpFileLoadTasks(null, 0);
        }
    }

    public void loadImageForImageReceiver(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            Object obj;
            TLObject thumbLocation;
            TLObject imageLocation;
            String httpImageLocation;
            Object obj2;
            String str;
            String str2;
            String ext;
            String str3;
            Object obj3;
            TLObject tLObject;
            FileLocation fileLocation;
            String str4;
            Object obj4;
            Document document;
            String str5;
            int lastIndexOf;
            String filter;
            String thumbFilter;
            String str6;
            boolean z;
            String key = imageReceiver.getKey();
            if (key != null) {
                BitmapDrawable bitmapDrawable = this.memCache.get(key);
                if (bitmapDrawable != null) {
                    cancelLoadingForImageReceiver(imageReceiver, 0);
                    if (!imageReceiver.isForcePreview()) {
                        imageReceiver.setImageBitmapByKey(bitmapDrawable, key, false, true);
                        return;
                    }
                }
            }
            String thumbKey = imageReceiver.getThumbKey();
            if (thumbKey != null) {
                BitmapDrawable bitmapDrawable2 = this.memCache.get(thumbKey);
                if (bitmapDrawable2 != null) {
                    imageReceiver.setImageBitmapByKey(bitmapDrawable2, thumbKey, true, true);
                    cancelLoadingForImageReceiver(imageReceiver, 1);
                    obj = 1;
                    thumbLocation = imageReceiver.getThumbLocation();
                    imageLocation = imageReceiver.getImageLocation();
                    httpImageLocation = imageReceiver.getHttpImageLocation();
                    obj2 = null;
                    key = null;
                    str = null;
                    str2 = null;
                    ext = imageReceiver.getExt();
                    if (ext == null) {
                        ext = "jpg";
                    }
                    if (httpImageLocation != null) {
                        str2 = Utilities.MD5(httpImageLocation);
                        str3 = str2 + "." + getHttpUrlExtension(httpImageLocation, "jpg");
                        obj3 = null;
                        tLObject = imageLocation;
                        thumbKey = str2;
                        str2 = null;
                    } else if (imageLocation == null) {
                        if (imageLocation instanceof FileLocation) {
                            fileLocation = (FileLocation) imageLocation;
                            str4 = fileLocation.volume_id + "_" + fileLocation.local_id;
                            str2 = str4 + "." + ext;
                            obj4 = (imageReceiver.getExt() == null || fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) ? 1 : null;
                            obj2 = obj4;
                            key = str2;
                            str2 = str4;
                        } else if (imageLocation instanceof Document) {
                            document = (Document) imageLocation;
                            if (document.id != 0 && document.dc_id != 0) {
                                str5 = document.version == 0 ? document.dc_id + "_" + document.id : document.dc_id + "_" + document.id + "_" + document.version;
                                str2 = FileLoader.getDocumentFileName(document);
                                if (str2 != null) {
                                    lastIndexOf = str2.lastIndexOf(46);
                                    if (lastIndexOf != -1) {
                                        str2 = str2.substring(lastIndexOf);
                                        if (str2.length() <= 1) {
                                            str2 = (document.mime_type == null && document.mime_type.equals(MimeTypes.VIDEO_MP4)) ? ".mp4" : TtmlNode.ANONYMOUS_REGION_ID;
                                        }
                                        str4 = str5 + str2;
                                        str = null == null ? null + "." + ext : null;
                                        str2 = str5;
                                        obj2 = MessageObject.isGifDocument(document) ? 1 : null;
                                        key = str4;
                                    }
                                }
                                str2 = TtmlNode.ANONYMOUS_REGION_ID;
                                if (str2.length() <= 1) {
                                    if (document.mime_type == null) {
                                    }
                                }
                                str4 = str5 + str2;
                                if (null == null) {
                                }
                                if (MessageObject.isGifDocument(document)) {
                                }
                                str = null == null ? null + "." + ext : null;
                                str2 = str5;
                                obj2 = MessageObject.isGifDocument(document) ? 1 : null;
                                key = str4;
                            } else {
                                return;
                            }
                        }
                        if (imageLocation != thumbLocation) {
                            str3 = null;
                            obj3 = obj2;
                            tLObject = null;
                            thumbKey = null;
                            str2 = str;
                        } else {
                            str3 = key;
                            obj3 = obj2;
                            tLObject = imageLocation;
                            thumbKey = str2;
                            str2 = str;
                        }
                    } else {
                        str3 = null;
                        obj3 = null;
                        tLObject = imageLocation;
                        thumbKey = null;
                        str2 = null;
                    }
                    if (thumbLocation == null) {
                        key = thumbLocation.volume_id + "_" + thumbLocation.local_id;
                        str2 = key + "." + ext;
                    } else {
                        key = null;
                    }
                    filter = imageReceiver.getFilter();
                    thumbFilter = imageReceiver.getThumbFilter();
                    str6 = (thumbKey != null || filter == null) ? thumbKey : thumbKey + "@" + filter;
                    str5 = (key != null || thumbFilter == null) ? key : key + "@" + thumbFilter;
                    if (httpImageLocation == null) {
                        createLoadOperationForImageReceiver(imageReceiver, str5, str2, ext, thumbLocation, null, thumbFilter, 0, true, obj == null ? 2 : 1);
                        createLoadOperationForImageReceiver(imageReceiver, str6, str3, ext, null, httpImageLocation, filter, 0, true, 0);
                    }
                    createLoadOperationForImageReceiver(imageReceiver, str5, str2, ext, thumbLocation, null, thumbFilter, 0, true, obj == null ? 2 : 1);
                    lastIndexOf = imageReceiver.getSize();
                    z = obj3 == null || imageReceiver.getCacheOnly();
                    createLoadOperationForImageReceiver(imageReceiver, str6, str3, ext, tLObject, null, filter, lastIndexOf, z, 0);
                    return;
                }
            }
            obj = null;
            thumbLocation = imageReceiver.getThumbLocation();
            imageLocation = imageReceiver.getImageLocation();
            httpImageLocation = imageReceiver.getHttpImageLocation();
            obj2 = null;
            key = null;
            str = null;
            str2 = null;
            ext = imageReceiver.getExt();
            if (ext == null) {
                ext = "jpg";
            }
            if (httpImageLocation != null) {
                str2 = Utilities.MD5(httpImageLocation);
                str3 = str2 + "." + getHttpUrlExtension(httpImageLocation, "jpg");
                obj3 = null;
                tLObject = imageLocation;
                thumbKey = str2;
                str2 = null;
            } else if (imageLocation == null) {
                str3 = null;
                obj3 = null;
                tLObject = imageLocation;
                thumbKey = null;
                str2 = null;
            } else {
                if (imageLocation instanceof FileLocation) {
                    fileLocation = (FileLocation) imageLocation;
                    str4 = fileLocation.volume_id + "_" + fileLocation.local_id;
                    str2 = str4 + "." + ext;
                    if (imageReceiver.getExt() == null) {
                    }
                    obj2 = obj4;
                    key = str2;
                    str2 = str4;
                } else if (imageLocation instanceof Document) {
                    document = (Document) imageLocation;
                    if (document.id != 0) {
                        return;
                    }
                    return;
                }
                if (imageLocation != thumbLocation) {
                    str3 = key;
                    obj3 = obj2;
                    tLObject = imageLocation;
                    thumbKey = str2;
                    str2 = str;
                } else {
                    str3 = null;
                    obj3 = obj2;
                    tLObject = null;
                    thumbKey = null;
                    str2 = str;
                }
            }
            if (thumbLocation == null) {
                key = null;
            } else {
                key = thumbLocation.volume_id + "_" + thumbLocation.local_id;
                str2 = key + "." + ext;
            }
            filter = imageReceiver.getFilter();
            thumbFilter = imageReceiver.getThumbFilter();
            if (thumbKey != null) {
            }
            if (key != null) {
            }
            if (httpImageLocation == null) {
                if (obj == null) {
                }
                createLoadOperationForImageReceiver(imageReceiver, str5, str2, ext, thumbLocation, null, thumbFilter, 0, true, obj == null ? 2 : 1);
                lastIndexOf = imageReceiver.getSize();
                if (obj3 == null) {
                }
                createLoadOperationForImageReceiver(imageReceiver, str6, str3, ext, tLObject, null, filter, lastIndexOf, z, 0);
                return;
            }
            if (obj == null) {
            }
            createLoadOperationForImageReceiver(imageReceiver, str5, str2, ext, thumbLocation, null, thumbFilter, 0, true, obj == null ? 2 : 1);
            createLoadOperationForImageReceiver(imageReceiver, str6, str3, ext, null, httpImageLocation, filter, 0, true, 0);
        }
    }

    public void putImageToCache(BitmapDrawable bitmapDrawable, String str) {
        this.memCache.put(str, bitmapDrawable);
    }

    public void removeImage(String str) {
        this.bitmapUseCounts.remove(str);
        this.memCache.remove(str);
    }

    public void replaceImageInCache(String str, String str2, FileLocation fileLocation, boolean z) {
        if (z) {
            AndroidUtilities.runOnUIThread(new C04346(str, str2, fileLocation));
        } else {
            replaceImageInCacheInternal(str, str2, fileLocation);
        }
    }
}
