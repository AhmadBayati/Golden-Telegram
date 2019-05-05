package com.hanista.mobogram.messenger;

import com.hanista.mobogram.messenger.FileLoadOperation.FileLoadOperationDelegate;
import com.hanista.mobogram.messenger.FileUploadOperation.FileUploadOperationDelegate;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Audio;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputEncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeFilename;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageService;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoCachedSize;
import com.hanista.mobogram.tgnet.TLRPC.Video;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import com.hanista.mobogram.util.shamsicalendar.ShamsiDate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class FileLoader {
    private static volatile FileLoader Instance = null;
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_VIDEO = 2;
    private LinkedList<FileLoadOperation> audioLoadOperationQueue;
    private int currentAudioLoadOperationsCount;
    private int currentLoadOperationsCount;
    private int currentPhotoLoadOperationsCount;
    private int currentUploadOperationsCount;
    private int currentUploadSmallOperationsCount;
    private FileLoaderDelegate delegate;
    private volatile DispatchQueue fileLoaderQueue;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths;
    private LinkedList<FileLoadOperation> loadOperationQueue;
    private HashMap<Integer, File> mediaDirs;
    private LinkedList<FileLoadOperation> photoLoadOperationQueue;
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths;
    private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc;
    private LinkedList<FileUploadOperation> uploadOperationQueue;
    private HashMap<String, Long> uploadSizes;
    private LinkedList<FileUploadOperation> uploadSmallOperationQueue;

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.1 */
    class C03921 implements Runnable {
        final /* synthetic */ boolean val$enc;
        final /* synthetic */ String val$location;

        C03921(boolean z, String str) {
            this.val$enc = z;
            this.val$location = str;
        }

        public void run() {
            FileUploadOperation fileUploadOperation = !this.val$enc ? (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(this.val$location) : (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(this.val$location);
            FileLoader.this.uploadSizes.remove(this.val$location);
            if (fileUploadOperation != null) {
                FileLoader.this.uploadOperationPathsEnc.remove(this.val$location);
                FileLoader.this.uploadOperationQueue.remove(fileUploadOperation);
                FileLoader.this.uploadSmallOperationQueue.remove(fileUploadOperation);
                fileUploadOperation.cancel();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.2 */
    class C03932 implements Runnable {
        final /* synthetic */ boolean val$encrypted;
        final /* synthetic */ long val$finalSize;
        final /* synthetic */ String val$location;

        C03932(boolean z, String str, long j) {
            this.val$encrypted = z;
            this.val$location = str;
            this.val$finalSize = j;
        }

        public void run() {
            FileUploadOperation fileUploadOperation = this.val$encrypted ? (FileUploadOperation) FileLoader.this.uploadOperationPathsEnc.get(this.val$location) : (FileUploadOperation) FileLoader.this.uploadOperationPaths.get(this.val$location);
            if (fileUploadOperation != null) {
                fileUploadOperation.checkNewDataAvailable(this.val$finalSize);
            } else if (this.val$finalSize != 0) {
                FileLoader.this.uploadSizes.put(this.val$location, Long.valueOf(this.val$finalSize));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.3 */
    class C03973 implements Runnable {
        final /* synthetic */ boolean val$encrypted;
        final /* synthetic */ int val$estimatedSize;
        final /* synthetic */ String val$location;
        final /* synthetic */ boolean val$small;

        /* renamed from: com.hanista.mobogram.messenger.FileLoader.3.1 */
        class C03961 implements FileUploadOperationDelegate {

            /* renamed from: com.hanista.mobogram.messenger.FileLoader.3.1.1 */
            class C03941 implements Runnable {
                final /* synthetic */ InputEncryptedFile val$inputEncryptedFile;
                final /* synthetic */ InputFile val$inputFile;
                final /* synthetic */ byte[] val$iv;
                final /* synthetic */ byte[] val$key;
                final /* synthetic */ FileUploadOperation val$operation;

                C03941(InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, FileUploadOperation fileUploadOperation) {
                    this.val$inputFile = inputFile;
                    this.val$inputEncryptedFile = inputEncryptedFile;
                    this.val$key = bArr;
                    this.val$iv = bArr2;
                    this.val$operation = fileUploadOperation;
                }

                public void run() {
                    if (C03973.this.val$encrypted) {
                        FileLoader.this.uploadOperationPathsEnc.remove(C03973.this.val$location);
                    } else {
                        FileLoader.this.uploadOperationPaths.remove(C03973.this.val$location);
                    }
                    FileUploadOperation fileUploadOperation;
                    if (C03973.this.val$small) {
                        FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                        if (FileLoader.this.currentUploadSmallOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                            fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                            if (fileUploadOperation != null) {
                                FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                                fileUploadOperation.start();
                            }
                        }
                    } else {
                        FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                        if (FileLoader.this.currentUploadOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                            fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                            if (fileUploadOperation != null) {
                                FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                                fileUploadOperation.start();
                            }
                        }
                    }
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileDidUploaded(C03973.this.val$location, this.val$inputFile, this.val$inputEncryptedFile, this.val$key, this.val$iv, this.val$operation.getTotalFileSize());
                    }
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.FileLoader.3.1.2 */
            class C03952 implements Runnable {
                C03952() {
                }

                public void run() {
                    if (C03973.this.val$encrypted) {
                        FileLoader.this.uploadOperationPathsEnc.remove(C03973.this.val$location);
                    } else {
                        FileLoader.this.uploadOperationPaths.remove(C03973.this.val$location);
                    }
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileDidFailedUpload(C03973.this.val$location, C03973.this.val$encrypted);
                    }
                    FileUploadOperation fileUploadOperation;
                    if (C03973.this.val$small) {
                        FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount - 1;
                        if (FileLoader.this.currentUploadSmallOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                            fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll();
                            if (fileUploadOperation != null) {
                                FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                                fileUploadOperation.start();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount - 1;
                    if (FileLoader.this.currentUploadOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                        fileUploadOperation = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll();
                        if (fileUploadOperation != null) {
                            FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            fileUploadOperation.start();
                        }
                    }
                }
            }

            C03961() {
            }

            public void didChangedUploadProgress(FileUploadOperation fileUploadOperation, float f) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(C03973.this.val$location, f, C03973.this.val$encrypted);
                }
            }

            public void didFailedUploadingFile(FileUploadOperation fileUploadOperation) {
                FileLoader.this.fileLoaderQueue.postRunnable(new C03952());
            }

            public void didFinishUploadingFile(FileUploadOperation fileUploadOperation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2) {
                FileLoader.this.fileLoaderQueue.postRunnable(new C03941(inputFile, inputEncryptedFile, bArr, bArr2, fileUploadOperation));
            }
        }

        C03973(boolean z, String str, int i, boolean z2) {
            this.val$encrypted = z;
            this.val$location = str;
            this.val$estimatedSize = i;
            this.val$small = z2;
        }

        public void run() {
            int i;
            if (this.val$encrypted) {
                if (FileLoader.this.uploadOperationPathsEnc.containsKey(this.val$location)) {
                    return;
                }
            } else if (FileLoader.this.uploadOperationPaths.containsKey(this.val$location)) {
                return;
            }
            int i2 = this.val$estimatedSize;
            if (i2 == 0 || ((Long) FileLoader.this.uploadSizes.get(this.val$location)) == null) {
                i = i2;
            } else {
                i = FileLoader.MEDIA_DIR_IMAGE;
                FileLoader.this.uploadSizes.remove(this.val$location);
            }
            FileUploadOperation fileUploadOperation = new FileUploadOperation(this.val$location, this.val$encrypted, i);
            if (this.val$encrypted) {
                FileLoader.this.uploadOperationPathsEnc.put(this.val$location, fileUploadOperation);
            } else {
                FileLoader.this.uploadOperationPaths.put(this.val$location, fileUploadOperation);
            }
            fileUploadOperation.delegate = new C03961();
            if (this.val$small) {
                if (FileLoader.this.currentUploadSmallOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                    FileLoader.this.currentUploadSmallOperationsCount = FileLoader.this.currentUploadSmallOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                    fileUploadOperation.start();
                    return;
                }
                FileLoader.this.uploadSmallOperationQueue.add(fileUploadOperation);
            } else if (FileLoader.this.currentUploadOperationsCount < FileLoader.MEDIA_DIR_AUDIO) {
                FileLoader.this.currentUploadOperationsCount = FileLoader.this.currentUploadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                fileUploadOperation.start();
            } else {
                FileLoader.this.uploadOperationQueue.add(fileUploadOperation);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.4 */
    class C03984 implements Runnable {
        final /* synthetic */ Document val$document;
        final /* synthetic */ FileLocation val$location;
        final /* synthetic */ String val$locationExt;

        C03984(FileLocation fileLocation, String str, Document document) {
            this.val$location = fileLocation;
            this.val$locationExt = str;
            this.val$document = document;
        }

        public void run() {
            Object obj = null;
            if (this.val$location != null) {
                obj = FileLoader.getAttachFileName(this.val$location, this.val$locationExt);
            } else if (this.val$document != null) {
                obj = FileLoader.getAttachFileName(this.val$document);
            }
            if (obj != null) {
                FileLoadOperation fileLoadOperation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(obj);
                if (fileLoadOperation != null) {
                    if (MessageObject.isVoiceDocument(this.val$document)) {
                        if (!FileLoader.this.audioLoadOperationQueue.remove(fileLoadOperation)) {
                            FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount - 1;
                        }
                    } else if (this.val$location != null) {
                        if (!FileLoader.this.photoLoadOperationQueue.remove(fileLoadOperation)) {
                            FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount - 1;
                        }
                    } else if (!FileLoader.this.loadOperationQueue.remove(fileLoadOperation)) {
                        FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount - 1;
                    }
                    fileLoadOperation.cancel();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.5 */
    class C03995 implements Runnable {
        final /* synthetic */ String val$fileName;
        final /* synthetic */ Boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        C03995(Boolean[] boolArr, String str, Semaphore semaphore) {
            this.val$result = boolArr;
            this.val$fileName = str;
            this.val$semaphore = semaphore;
        }

        public void run() {
            this.val$result[FileLoader.MEDIA_DIR_IMAGE] = Boolean.valueOf(FileLoader.this.loadOperationPaths.containsKey(this.val$fileName));
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.6 */
    class C04016 implements Runnable {
        final /* synthetic */ boolean val$cacheOnly;
        final /* synthetic */ Document val$document;
        final /* synthetic */ boolean val$force;
        final /* synthetic */ FileLocation val$location;
        final /* synthetic */ String val$locationExt;
        final /* synthetic */ int val$locationSize;

        /* renamed from: com.hanista.mobogram.messenger.FileLoader.6.1 */
        class C04001 implements FileLoadOperationDelegate {
            final /* synthetic */ String val$finalFileName;
            final /* synthetic */ int val$finalType;

            C04001(String str, int i) {
                this.val$finalFileName = str;
                this.val$finalType = i;
            }

            public void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileLoadProgressChanged(this.val$finalFileName, f);
                }
            }

            public void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i) {
                FileLoader.this.checkDownloadQueue(C04016.this.val$document, C04016.this.val$location, this.val$finalFileName);
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedLoad(this.val$finalFileName, i);
                }
            }

            public void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidLoaded(this.val$finalFileName, file, this.val$finalType);
                }
                FileLoader.this.checkDownloadQueue(C04016.this.val$document, C04016.this.val$location, this.val$finalFileName);
            }
        }

        C04016(FileLocation fileLocation, String str, Document document, boolean z, int i, boolean z2) {
            this.val$location = fileLocation;
            this.val$locationExt = str;
            this.val$document = document;
            this.val$force = z;
            this.val$locationSize = i;
            this.val$cacheOnly = z2;
        }

        public void run() {
            boolean z = true;
            String attachFileName = this.val$location != null ? FileLoader.getAttachFileName(this.val$location, this.val$locationExt) : this.val$document != null ? FileLoader.getAttachFileName(this.val$document) : null;
            if (attachFileName != null && !attachFileName.contains("-2147483648")) {
                FileLoadOperation fileLoadOperation = (FileLoadOperation) FileLoader.this.loadOperationPaths.get(attachFileName);
                if (fileLoadOperation == null) {
                    FileLoadOperation fileLoadOperation2;
                    int i;
                    File directory = FileLoader.this.getDirectory(FileLoader.MEDIA_DIR_CACHE);
                    if (this.val$location != null) {
                        fileLoadOperation2 = new FileLoadOperation(this.val$location, this.val$locationExt, this.val$locationSize);
                        i = FileLoader.MEDIA_DIR_IMAGE;
                    } else if (this.val$document != null) {
                        fileLoadOperation2 = new FileLoadOperation(this.val$document);
                        i = MessageObject.isVoiceDocument(this.val$document) ? FileLoader.MEDIA_DIR_AUDIO : MessageObject.isVideoDocument(this.val$document) ? FileLoader.MEDIA_DIR_VIDEO : FileLoader.MEDIA_DIR_DOCUMENT;
                    } else {
                        fileLoadOperation2 = fileLoadOperation;
                        i = FileLoader.MEDIA_DIR_CACHE;
                    }
                    fileLoadOperation2.setPaths(!this.val$cacheOnly ? FileLoader.this.getDirectory(i) : directory, directory);
                    fileLoadOperation2.setDelegate(new C04001(attachFileName, i));
                    FileLoader.this.loadOperationPaths.put(attachFileName, fileLoadOperation2);
                    if (!this.val$force) {
                        z = true;
                    }
                    if (i == FileLoader.MEDIA_DIR_AUDIO) {
                        if (FileLoader.this.currentAudioLoadOperationsCount < z) {
                            if (fileLoadOperation2.start()) {
                                FileLoader.this.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            }
                        } else if (this.val$force) {
                            FileLoader.this.audioLoadOperationQueue.add(FileLoader.MEDIA_DIR_IMAGE, fileLoadOperation2);
                        } else {
                            FileLoader.this.audioLoadOperationQueue.add(fileLoadOperation2);
                        }
                    } else if (this.val$location != null) {
                        if (FileLoader.this.currentPhotoLoadOperationsCount < z) {
                            if (fileLoadOperation2.start()) {
                                FileLoader.this.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                            }
                        } else if (this.val$force) {
                            FileLoader.this.photoLoadOperationQueue.add(FileLoader.MEDIA_DIR_IMAGE, fileLoadOperation2);
                        } else {
                            FileLoader.this.photoLoadOperationQueue.add(fileLoadOperation2);
                        }
                    } else if (FileLoader.this.currentLoadOperationsCount < z) {
                        if (fileLoadOperation2.start()) {
                            FileLoader.this.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                        }
                    } else if (this.val$force) {
                        FileLoader.this.loadOperationQueue.add(FileLoader.MEDIA_DIR_IMAGE, fileLoadOperation2);
                    } else {
                        FileLoader.this.loadOperationQueue.add(fileLoadOperation2);
                    }
                } else if (this.val$force) {
                    fileLoadOperation.setForceRequest(true);
                    LinkedList access$1000 = MessageObject.isVoiceDocument(this.val$document) ? FileLoader.this.audioLoadOperationQueue : this.val$location != null ? FileLoader.this.photoLoadOperationQueue : FileLoader.this.loadOperationQueue;
                    if (access$1000 != null) {
                        int indexOf = access$1000.indexOf(fileLoadOperation);
                        if (indexOf > 0) {
                            access$1000.remove(indexOf);
                            access$1000.add(FileLoader.MEDIA_DIR_IMAGE, fileLoadOperation);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.7 */
    class C04027 implements Runnable {
        final /* synthetic */ String val$arg1;
        final /* synthetic */ Document val$document;
        final /* synthetic */ FileLocation val$location;

        C04027(String str, Document document, FileLocation fileLocation) {
            this.val$arg1 = str;
            this.val$document = document;
            this.val$location = fileLocation;
        }

        public void run() {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) FileLoader.this.loadOperationPaths.remove(this.val$arg1);
            if (MessageObject.isVoiceDocument(this.val$document)) {
                if (fileLoadOperation != null) {
                    if (fileLoadOperation.wasStarted()) {
                        FileLoader.this.this$0.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount - 1;
                    } else {
                        FileLoader.this.audioLoadOperationQueue.remove(fileLoadOperation);
                    }
                }
                while (!FileLoader.this.audioLoadOperationQueue.isEmpty()) {
                    if (FileLoader.this.currentAudioLoadOperationsCount < (((FileLoadOperation) FileLoader.this.audioLoadOperationQueue.get(FileLoader.MEDIA_DIR_IMAGE)).isForceRequest() ? FileLoader.MEDIA_DIR_DOCUMENT : FileLoader.MEDIA_DIR_AUDIO)) {
                        fileLoadOperation = (FileLoadOperation) FileLoader.this.audioLoadOperationQueue.poll();
                        if (fileLoadOperation != null && fileLoadOperation.start()) {
                            this.this$0.currentAudioLoadOperationsCount = FileLoader.this.currentAudioLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                        }
                    } else {
                        return;
                    }
                }
            } else if (this.val$location != null) {
                if (fileLoadOperation != null) {
                    if (fileLoadOperation.wasStarted()) {
                        FileLoader.this.this$0.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount - 1;
                    } else {
                        FileLoader.this.photoLoadOperationQueue.remove(fileLoadOperation);
                    }
                }
                while (!FileLoader.this.photoLoadOperationQueue.isEmpty()) {
                    if (FileLoader.this.currentPhotoLoadOperationsCount < (((FileLoadOperation) FileLoader.this.photoLoadOperationQueue.get(FileLoader.MEDIA_DIR_IMAGE)).isForceRequest() ? FileLoader.MEDIA_DIR_DOCUMENT : FileLoader.MEDIA_DIR_AUDIO)) {
                        fileLoadOperation = (FileLoadOperation) FileLoader.this.photoLoadOperationQueue.poll();
                        if (fileLoadOperation != null && fileLoadOperation.start()) {
                            this.this$0.currentPhotoLoadOperationsCount = FileLoader.this.currentPhotoLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                        }
                    } else {
                        return;
                    }
                }
            } else {
                if (fileLoadOperation != null) {
                    if (fileLoadOperation.wasStarted()) {
                        FileLoader.this.this$0.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount - 1;
                    } else {
                        FileLoader.this.loadOperationQueue.remove(fileLoadOperation);
                    }
                }
                while (!FileLoader.this.loadOperationQueue.isEmpty()) {
                    if (FileLoader.this.currentLoadOperationsCount < (((FileLoadOperation) FileLoader.this.loadOperationQueue.get(FileLoader.MEDIA_DIR_IMAGE)).isForceRequest() ? FileLoader.MEDIA_DIR_DOCUMENT : FileLoader.MEDIA_DIR_AUDIO)) {
                        fileLoadOperation = (FileLoadOperation) FileLoader.this.loadOperationQueue.poll();
                        if (fileLoadOperation != null && fileLoadOperation.start()) {
                            this.this$0.currentLoadOperationsCount = FileLoader.this.currentLoadOperationsCount + FileLoader.MEDIA_DIR_AUDIO;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoader.8 */
    class C04038 implements Runnable {
        final /* synthetic */ ArrayList val$files;
        final /* synthetic */ int val$type;

        C04038(ArrayList arrayList, int i) {
            this.val$files = arrayList;
            this.val$type = i;
        }

        public void run() {
            for (int i = FileLoader.MEDIA_DIR_IMAGE; i < this.val$files.size(); i += FileLoader.MEDIA_DIR_AUDIO) {
                File file = (File) this.val$files.get(i);
                if (file.exists()) {
                    try {
                        if (!file.delete()) {
                            file.deleteOnExit();
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
                try {
                    File file2 = new File(file.getParentFile(), "q_" + file.getName());
                    if (file2.exists() && !file2.delete()) {
                        file2.deleteOnExit();
                    }
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
            if (this.val$type == FileLoader.MEDIA_DIR_VIDEO) {
                ImageLoader.getInstance().clearMemory();
            }
        }
    }

    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, int i);

        void fileDidUploaded(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j);

        void fileLoadProgressChanged(String str, float f);

        void fileUploadProgressChanged(String str, float f, boolean z);
    }

    static {
        Instance = null;
    }

    public FileLoader() {
        this.mediaDirs = null;
        this.fileLoaderQueue = new DispatchQueue("fileUploadQueue");
        this.uploadOperationQueue = new LinkedList();
        this.uploadSmallOperationQueue = new LinkedList();
        this.loadOperationQueue = new LinkedList();
        this.audioLoadOperationQueue = new LinkedList();
        this.photoLoadOperationQueue = new LinkedList();
        this.uploadOperationPaths = new ConcurrentHashMap();
        this.uploadOperationPathsEnc = new ConcurrentHashMap();
        this.loadOperationPaths = new ConcurrentHashMap();
        this.uploadSizes = new HashMap();
        this.delegate = null;
        this.currentLoadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentAudioLoadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentPhotoLoadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentUploadOperationsCount = MEDIA_DIR_IMAGE;
        this.currentUploadSmallOperationsCount = MEDIA_DIR_IMAGE;
    }

    private void cancelLoadFile(Document document, FileLocation fileLocation, String str) {
        if (fileLocation != null || document != null) {
            this.fileLoaderQueue.postRunnable(new C03984(fileLocation, str, document));
        }
    }

    private void checkDownloadQueue(Document document, FileLocation fileLocation, String str) {
        this.fileLoaderQueue.postRunnable(new C04027(str, document, fileLocation));
    }

    public static String getAttachFileName(TLObject tLObject) {
        return getAttachFileName(tLObject, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getAttachFileName(com.hanista.mobogram.tgnet.TLObject r4, java.lang.String r5) {
        /*
        r1 = -1;
        r2 = 1;
        r0 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.Document;
        if (r0 == 0) goto L_0x0138;
    L_0x0006:
        r4 = (com.hanista.mobogram.tgnet.TLRPC.Document) r4;
        r0 = 0;
        if (r0 != 0) goto L_0x001c;
    L_0x000b:
        r0 = getDocumentFileName(r4);
        if (r0 == 0) goto L_0x0019;
    L_0x0011:
        r3 = 46;
        r3 = r0.lastIndexOf(r3);
        if (r3 != r1) goto L_0x0058;
    L_0x0019:
        r0 = "";
    L_0x001c:
        r3 = r0.length();
        if (r3 > r2) goto L_0x0036;
    L_0x0022:
        r0 = r4.mime_type;
        if (r0 == 0) goto L_0x007b;
    L_0x0026:
        r0 = r4.mime_type;
        r3 = r0.hashCode();
        switch(r3) {
            case 187091926: goto L_0x0068;
            case 1331848029: goto L_0x005d;
            default: goto L_0x002f;
        };
    L_0x002f:
        r0 = r1;
    L_0x0030:
        switch(r0) {
            case 0: goto L_0x0073;
            case 1: goto L_0x0077;
            default: goto L_0x0033;
        };
    L_0x0033:
        r0 = "";
    L_0x0036:
        r1 = r4.version;
        if (r1 != 0) goto L_0x00bd;
    L_0x003a:
        r1 = r0.length();
        if (r1 <= r2) goto L_0x00a0;
    L_0x0040:
        r1 = com.hanista.mobogram.mobo.MoboConstants.f1354u;
        if (r1 == 0) goto L_0x007f;
    L_0x0044:
        r1 = "webp";
        r1 = r0.contains(r1);
        if (r1 != 0) goto L_0x007f;
    L_0x004d:
        r1 = isInValidForOrgFileName(r4);
        if (r1 != 0) goto L_0x007f;
    L_0x0053:
        r0 = getDocName(r4);
    L_0x0057:
        return r0;
    L_0x0058:
        r0 = r0.substring(r3);
        goto L_0x001c;
    L_0x005d:
        r3 = "video/mp4";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x002f;
    L_0x0066:
        r0 = 0;
        goto L_0x0030;
    L_0x0068:
        r3 = "audio/ogg";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x002f;
    L_0x0071:
        r0 = r2;
        goto L_0x0030;
    L_0x0073:
        r0 = ".mp4";
        goto L_0x0036;
    L_0x0077:
        r0 = ".ogg";
        goto L_0x0036;
    L_0x007b:
        r0 = "";
        goto L_0x0036;
    L_0x007f:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r4.dc_id;
        r1 = r1.append(r2);
        r2 = "_";
        r1 = r1.append(r2);
        r2 = r4.id;
        r1 = r1.append(r2);
        r0 = r1.append(r0);
        r0 = r0.toString();
        goto L_0x0057;
    L_0x00a0:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r4.dc_id;
        r0 = r0.append(r1);
        r1 = "_";
        r0 = r0.append(r1);
        r2 = r4.id;
        r0 = r0.append(r2);
        r0 = r0.toString();
        goto L_0x0057;
    L_0x00bd:
        r1 = r0.length();
        if (r1 <= r2) goto L_0x010d;
    L_0x00c3:
        r1 = com.hanista.mobogram.mobo.MoboConstants.f1354u;
        if (r1 == 0) goto L_0x00de;
    L_0x00c7:
        r1 = "webp";
        r1 = r0.contains(r1);
        if (r1 != 0) goto L_0x00de;
    L_0x00d0:
        r1 = isInValidForOrgFileName(r4);
        if (r1 != 0) goto L_0x00de;
    L_0x00d6:
        r0 = r4.version;
        r0 = getDocName(r4, r0);
        goto L_0x0057;
    L_0x00de:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r4.dc_id;
        r1 = r1.append(r2);
        r2 = "_";
        r1 = r1.append(r2);
        r2 = r4.id;
        r1 = r1.append(r2);
        r2 = "_";
        r1 = r1.append(r2);
        r2 = r4.version;
        r1 = r1.append(r2);
        r0 = r1.append(r0);
        r0 = r0.toString();
        goto L_0x0057;
    L_0x010d:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r4.dc_id;
        r0 = r0.append(r1);
        r1 = "_";
        r0 = r0.append(r1);
        r2 = r4.id;
        r0 = r0.append(r2);
        r1 = "_";
        r0 = r0.append(r1);
        r1 = r4.version;
        r0 = r0.append(r1);
        r0 = r0.toString();
        goto L_0x0057;
    L_0x0138:
        r0 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
        if (r0 == 0) goto L_0x0180;
    L_0x013c:
        r4 = (com.hanista.mobogram.tgnet.TLRPC.PhotoSize) r4;
        r0 = r4.location;
        if (r0 == 0) goto L_0x0148;
    L_0x0142:
        r0 = r4.location;
        r0 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r0 == 0) goto L_0x014d;
    L_0x0148:
        r0 = "";
        goto L_0x0057;
    L_0x014d:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r4.location;
        r2 = r1.volume_id;
        r0 = r0.append(r2);
        r1 = "_";
        r0 = r0.append(r1);
        r1 = r4.location;
        r1 = r1.local_id;
        r0 = r0.append(r1);
        r1 = ".";
        r0 = r0.append(r1);
        if (r5 == 0) goto L_0x017c;
    L_0x0172:
        r0 = r0.append(r5);
        r0 = r0.toString();
        goto L_0x0057;
    L_0x017c:
        r5 = "jpg";
        goto L_0x0172;
    L_0x0180:
        r0 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.FileLocation;
        if (r0 == 0) goto L_0x01be;
    L_0x0184:
        r0 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r0 == 0) goto L_0x018d;
    L_0x0188:
        r0 = "";
        goto L_0x0057;
    L_0x018d:
        r4 = (com.hanista.mobogram.tgnet.TLRPC.FileLocation) r4;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r4.volume_id;
        r0 = r0.append(r2);
        r1 = "_";
        r0 = r0.append(r1);
        r1 = r4.local_id;
        r0 = r0.append(r1);
        r1 = ".";
        r0 = r0.append(r1);
        if (r5 == 0) goto L_0x01ba;
    L_0x01b0:
        r0 = r0.append(r5);
        r0 = r0.toString();
        goto L_0x0057;
    L_0x01ba:
        r5 = "jpg";
        goto L_0x01b0;
    L_0x01be:
        r0 = "";
        goto L_0x0057;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.FileLoader.getAttachFileName(com.hanista.mobogram.tgnet.TLObject, java.lang.String):java.lang.String");
    }

    public static String getAttachFileName(TLObject tLObject, String str, boolean z) {
        StringBuilder append;
        if (tLObject instanceof Video) {
            Video video = (Video) tLObject;
            append = new StringBuilder().append(video.dc_id).append("_").append(video.id).append(".");
            if (str == null) {
                str = "mp4";
            }
            return append.append(str).toString();
        } else if (tLObject instanceof Document) {
            Document document = (Document) tLObject;
            String str2 = null;
            if (MEDIA_DIR_IMAGE == null) {
                str2 = getDocumentFileName(document);
                if (str2 != null) {
                    int lastIndexOf = str2.lastIndexOf(".");
                    if (lastIndexOf != -1) {
                        str2 = str2.substring(lastIndexOf);
                    }
                }
                str2 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            return str2.length() > MEDIA_DIR_AUDIO ? (z || !MoboConstants.f1354u || str2.contains("webp") || isInValidForOrgFileName(document)) ? document.dc_id + "_" + document.id + str2 : getDocName(document) : document.dc_id + "_" + document.id;
        } else if (tLObject instanceof PhotoSize) {
            PhotoSize photoSize = (PhotoSize) tLObject;
            if (photoSize.location == null || (photoSize.location instanceof TL_fileLocationUnavailable)) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            append = new StringBuilder().append(photoSize.location.volume_id).append("_").append(photoSize.location.local_id).append(".");
            if (str == null) {
                str = "jpg";
            }
            return append.append(str).toString();
        } else if (tLObject instanceof Audio) {
            Audio audio = (Audio) tLObject;
            append = new StringBuilder().append(audio.dc_id).append("_").append(audio.id).append(".");
            if (str == null) {
                str = "ogg";
            }
            return append.append(str).toString();
        } else if (!(tLObject instanceof FileLocation)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            if (tLObject instanceof TL_fileLocationUnavailable) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            FileLocation fileLocation = (FileLocation) tLObject;
            append = new StringBuilder().append(fileLocation.volume_id).append("_").append(fileLocation.local_id).append(".");
            if (str == null) {
                str = "jpg";
            }
            return append.append(str).toString();
        }
    }

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> arrayList, int i) {
        return getClosestPhotoSizeWithSize(arrayList, i, false);
    }

    public static PhotoSize getClosestPhotoSizeWithSize(ArrayList<PhotoSize> arrayList, int i, boolean z) {
        PhotoSize photoSize = null;
        if (!(arrayList == null || arrayList.isEmpty())) {
            int i2 = MEDIA_DIR_IMAGE;
            for (int i3 = MEDIA_DIR_IMAGE; i3 < arrayList.size(); i3 += MEDIA_DIR_AUDIO) {
                PhotoSize photoSize2 = (PhotoSize) arrayList.get(i3);
                if (photoSize2 != null) {
                    int i4;
                    if (z) {
                        int i5;
                        i4 = photoSize2.f2663h >= photoSize2.f2664w ? photoSize2.f2664w : photoSize2.f2663h;
                        if (photoSize == null || ((i > 100 && photoSize.location != null && photoSize.location.dc_id == TLRPC.MESSAGE_FLAG_MEGAGROUP) || (photoSize2 instanceof TL_photoCachedSize) || (i > i2 && i2 < i4))) {
                            i5 = i4;
                        } else {
                            photoSize2 = photoSize;
                            i5 = i2;
                        }
                        i2 = i5;
                        photoSize = photoSize2;
                    } else {
                        i4 = photoSize2.f2664w >= photoSize2.f2663h ? photoSize2.f2664w : photoSize2.f2663h;
                        if (photoSize == null || ((i > 100 && photoSize.location != null && photoSize.location.dc_id == TLRPC.MESSAGE_FLAG_MEGAGROUP) || (photoSize2 instanceof TL_photoCachedSize) || (i4 <= i && i2 < i4))) {
                            photoSize = photoSize2;
                            i2 = i4;
                        }
                    }
                }
            }
        }
        return photoSize;
    }

    public static String getDocName(Document document) {
        return getDocName(document, -1);
    }

    public static String getDocName(Document document, int i) {
        String documentFileName = getDocumentFileName(document);
        if (documentFileName != null) {
            int lastIndexOf = documentFileName.lastIndexOf(".");
            if (lastIndexOf != -1) {
                String substring = documentFileName.substring(lastIndexOf);
                if (i != -1) {
                    substring = i + substring;
                }
                int lastIndexOf2 = documentFileName.lastIndexOf(".");
                ShamsiDate dateToShamsi = ShamsiCalendar.dateToShamsi(new Date(((long) document.date) * 1000));
                return lastIndexOf2 > 0 ? documentFileName.substring(MEDIA_DIR_IMAGE, lastIndexOf2) + "_" + (dateToShamsi.toDateString('-') + "-" + dateToShamsi.toTimeString('-')) + substring : documentFileName;
            }
        }
        return documentFileName;
    }

    public static String getDocumentExtension(Document document) {
        String documentFileName = getDocumentFileName(document);
        int lastIndexOf = documentFileName.lastIndexOf(46);
        String str = null;
        if (lastIndexOf != -1) {
            str = documentFileName.substring(lastIndexOf + MEDIA_DIR_AUDIO);
        }
        if (str == null || str.length() == 0) {
            str = document.mime_type;
        }
        if (str == null) {
            str = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return str.toUpperCase();
    }

    public static String getDocumentFileName(Document document) {
        if (document != null) {
            if (document.file_name != null) {
                return document.file_name;
            }
            for (int i = MEDIA_DIR_IMAGE; i < document.attributes.size(); i += MEDIA_DIR_AUDIO) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeFilename) {
                    return documentAttribute.file_name;
                }
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + MEDIA_DIR_AUDIO);
        } catch (Exception e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static FileLoader getInstance() {
        FileLoader fileLoader = Instance;
        if (fileLoader == null) {
            synchronized (FileLoader.class) {
                fileLoader = Instance;
                if (fileLoader == null) {
                    fileLoader = new FileLoader();
                    Instance = fileLoader;
                }
            }
        }
        return fileLoader;
    }

    public static String getMessageFileName(Message message) {
        if (message == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        ArrayList arrayList;
        TLObject closestPhotoSizeWithSize;
        if (message instanceof TL_messageService) {
            if (message.action.photo != null) {
                arrayList = message.action.photo.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getAttachFileName(closestPhotoSizeWithSize);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            return getAttachFileName(message.media.document);
        } else {
            if (message.media instanceof TL_messageMediaPhoto) {
                arrayList = message.media.photo.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getAttachFileName(closestPhotoSizeWithSize);
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                if (message.media.webpage.photo != null) {
                    arrayList = message.media.webpage.photo.sizes;
                    if (arrayList.size() > 0) {
                        closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                        if (closestPhotoSizeWithSize != null) {
                            return getAttachFileName(closestPhotoSizeWithSize);
                        }
                    }
                } else if (message.media.webpage.document != null) {
                    return getAttachFileName(message.media.webpage.document);
                }
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static File getPathToAttach(TLObject tLObject) {
        return getPathToAttach(tLObject, null, false);
    }

    public static File getPathToAttach(TLObject tLObject, String str, boolean z) {
        File directory;
        if (z) {
            directory = getInstance().getDirectory(MEDIA_DIR_CACHE);
        } else if (tLObject instanceof Document) {
            Document document = (Document) tLObject;
            r0 = document.key != null ? getInstance().getDirectory(MEDIA_DIR_CACHE) : MessageObject.isVoiceDocument(document) ? getInstance().getDirectory(MEDIA_DIR_AUDIO) : MessageObject.isVideoDocument(document) ? getInstance().getDirectory(MEDIA_DIR_VIDEO) : getInstance().getDirectory(MEDIA_DIR_DOCUMENT);
            directory = r0;
        } else if (tLObject instanceof PhotoSize) {
            PhotoSize photoSize = (PhotoSize) tLObject;
            r0 = (photoSize.location == null || photoSize.location.key != null || ((photoSize.location.volume_id == -2147483648L && photoSize.location.local_id < 0) || photoSize.size < 0)) ? getInstance().getDirectory(MEDIA_DIR_CACHE) : getInstance().getDirectory(MEDIA_DIR_IMAGE);
            directory = r0;
        } else if (tLObject instanceof FileLocation) {
            FileLocation fileLocation = (FileLocation) tLObject;
            directory = (fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) ? getInstance().getDirectory(MEDIA_DIR_CACHE) : getInstance().getDirectory(MEDIA_DIR_IMAGE);
        } else {
            directory = null;
        }
        return directory == null ? new File(TtmlNode.ANONYMOUS_REGION_ID) : new File(directory, getAttachFileName(tLObject, str));
    }

    public static File getPathToAttach(TLObject tLObject, boolean z) {
        return getPathToAttach(tLObject, null, z);
    }

    public static File getPathToMessage(Message message) {
        if (message == null) {
            return new File(TtmlNode.ANONYMOUS_REGION_ID);
        }
        ArrayList arrayList;
        TLObject closestPhotoSizeWithSize;
        if (message instanceof TL_messageService) {
            if (message.action.photo != null) {
                arrayList = message.action.photo.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getPathToAttach(closestPhotoSizeWithSize);
                    }
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            return getPathToAttach(message.media.document);
        } else {
            if (message.media instanceof TL_messageMediaPhoto) {
                arrayList = message.media.photo.sizes;
                if (arrayList.size() > 0) {
                    closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return getPathToAttach(closestPhotoSizeWithSize);
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getPathToAttach(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    arrayList = message.media.webpage.photo.sizes;
                    if (arrayList.size() > 0) {
                        closestPhotoSizeWithSize = getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                        if (closestPhotoSizeWithSize != null) {
                            return getPathToAttach(closestPhotoSizeWithSize);
                        }
                    }
                }
            }
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static boolean isInValidForOrgFileName(Document document) {
        return MessageObject.isNewGifDocument(document) || MessageObject.isGifDocument(document) || MessageObject.isVideoDocument(document) || MessageObject.isStickerDocument(document) || MessageObject.isVoiceDocument(document) || (document.mime_type != null && document.mime_type.equalsIgnoreCase("audio/ogg"));
    }

    private void loadFile(Document document, FileLocation fileLocation, String str, int i, boolean z, boolean z2) {
        this.fileLoaderQueue.postRunnable(new C04016(fileLocation, str, document, z, i, z2));
    }

    public static boolean renameTo(File file, File file2) {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Throwable e;
        FileChannel fileChannel;
        FileOutputStream fileOutputStream2;
        FileInputStream fileInputStream2;
        FileChannel fileChannel2;
        FileChannel fileChannel3 = null;
        if (file.renameTo(file2)) {
            return true;
        }
        try {
            fileInputStream = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
            } catch (Exception e2) {
                e = e2;
                fileChannel = null;
                fileOutputStream2 = null;
                fileInputStream2 = fileInputStream;
                try {
                    FileLog.m18e("tmessages", e);
                    if (fileChannel3 != null) {
                        try {
                            fileChannel3.close();
                        } catch (Exception e3) {
                            return false;
                        }
                    }
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    file.delete();
                    return false;
                } catch (Throwable th) {
                    e = th;
                    fileOutputStream = fileOutputStream2;
                    fileInputStream = fileInputStream2;
                    fileChannel2 = fileChannel;
                    fileChannel = fileChannel3;
                    fileChannel3 = fileChannel2;
                    if (fileChannel != null) {
                        try {
                            fileChannel.close();
                        } catch (Exception e4) {
                            throw e;
                        }
                    }
                    if (fileChannel3 != null) {
                        fileChannel3.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    file.delete();
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                fileChannel = null;
                fileOutputStream = null;
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileChannel3 != null) {
                    fileChannel3.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                file.delete();
                throw e;
            }
            try {
                fileChannel = fileInputStream.getChannel();
                try {
                    FileChannel channel = fileOutputStream.getChannel();
                    try {
                        channel.transferFrom(fileChannel, 0, fileChannel.size());
                        if (fileChannel != null) {
                            try {
                                fileChannel.close();
                            } catch (Exception e5) {
                            }
                        }
                        if (channel != null) {
                            channel.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        file.delete();
                        return true;
                    } catch (Throwable e6) {
                        fileOutputStream2 = fileOutputStream;
                        fileInputStream2 = fileInputStream;
                        fileChannel2 = fileChannel;
                        fileChannel = channel;
                        e = e6;
                        fileChannel3 = fileChannel2;
                        FileLog.m18e("tmessages", e);
                        if (fileChannel3 != null) {
                            fileChannel3.close();
                        }
                        if (fileChannel != null) {
                            fileChannel.close();
                        }
                        if (fileOutputStream2 != null) {
                            fileOutputStream2.close();
                        }
                        if (fileInputStream2 != null) {
                            fileInputStream2.close();
                        }
                        file.delete();
                        return false;
                    } catch (Throwable e62) {
                        Throwable th3 = e62;
                        fileChannel3 = channel;
                        e = th3;
                        if (fileChannel != null) {
                            fileChannel.close();
                        }
                        if (fileChannel3 != null) {
                            fileChannel3.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        file.delete();
                        throw e;
                    }
                } catch (Exception e7) {
                    e = e7;
                    fileOutputStream2 = fileOutputStream;
                    fileInputStream2 = fileInputStream;
                    fileChannel3 = fileChannel;
                    fileChannel = null;
                    FileLog.m18e("tmessages", e);
                    if (fileChannel3 != null) {
                        fileChannel3.close();
                    }
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    file.delete();
                    return false;
                } catch (Throwable th4) {
                    e = th4;
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileChannel3 != null) {
                        fileChannel3.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    file.delete();
                    throw e;
                }
            } catch (Exception e8) {
                e = e8;
                fileChannel = null;
                fileOutputStream2 = fileOutputStream;
                fileInputStream2 = fileInputStream;
                FileLog.m18e("tmessages", e);
                if (fileChannel3 != null) {
                    fileChannel3.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                file.delete();
                return false;
            } catch (Throwable th5) {
                e = th5;
                fileChannel = null;
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileChannel3 != null) {
                    fileChannel3.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                file.delete();
                throw e;
            }
        } catch (Exception e9) {
            e = e9;
            fileChannel = null;
            fileOutputStream2 = null;
            fileInputStream2 = null;
            FileLog.m18e("tmessages", e);
            if (fileChannel3 != null) {
                fileChannel3.close();
            }
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            file.delete();
            return false;
        } catch (Throwable th6) {
            e = th6;
            fileChannel = null;
            fileOutputStream = null;
            fileInputStream = null;
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (fileChannel3 != null) {
                fileChannel3.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            file.delete();
            throw e;
        }
    }

    public void cancelLoadFile(Document document) {
        cancelLoadFile(document, null, null);
    }

    public void cancelLoadFile(FileLocation fileLocation, String str) {
        cancelLoadFile(null, fileLocation, str);
    }

    public void cancelLoadFile(PhotoSize photoSize) {
        cancelLoadFile(null, photoSize.location, null);
    }

    public void cancelUploadFile(String str, boolean z) {
        this.fileLoaderQueue.postRunnable(new C03921(z, str));
    }

    public File checkDirectory(int i) {
        return (File) this.mediaDirs.get(Integer.valueOf(i));
    }

    public void checkUploadNewDataAvailable(String str, boolean z, long j) {
        this.fileLoaderQueue.postRunnable(new C03932(z, str, j));
    }

    public void deleteFiles(ArrayList<File> arrayList, int i) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.fileLoaderQueue.postRunnable(new C04038(arrayList, i));
        }
    }

    public File getDirectory(int i) {
        File file = (File) this.mediaDirs.get(Integer.valueOf(i));
        if (file == null && i != MEDIA_DIR_CACHE) {
            file = (File) this.mediaDirs.get(Integer.valueOf(MEDIA_DIR_CACHE));
        }
        try {
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        } catch (Exception e) {
        }
        return file;
    }

    public boolean isLoadingFile(String str) {
        Semaphore semaphore = new Semaphore(MEDIA_DIR_IMAGE);
        Boolean[] boolArr = new Boolean[MEDIA_DIR_AUDIO];
        this.fileLoaderQueue.postRunnable(new C03995(boolArr, str, semaphore));
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return boolArr[MEDIA_DIR_IMAGE].booleanValue();
    }

    public void loadFile(Document document, boolean z, boolean z2) {
        boolean z3 = z2 || !(document == null || document.key == null);
        loadFile(document, null, null, MEDIA_DIR_IMAGE, z, z3);
    }

    public void loadFile(FileLocation fileLocation, String str, int i, boolean z) {
        boolean z2 = z || i == 0 || !(fileLocation == null || fileLocation.key == null);
        loadFile(null, fileLocation, str, i, true, z2);
    }

    public void loadFile(PhotoSize photoSize, String str, boolean z) {
        FileLocation fileLocation = photoSize.location;
        int i = photoSize.size;
        boolean z2 = z || ((photoSize != null && photoSize.size == 0) || photoSize.location.key != null);
        loadFile(null, fileLocation, str, i, false, z2);
    }

    public void setDelegate(FileLoaderDelegate fileLoaderDelegate) {
        this.delegate = fileLoaderDelegate;
    }

    public void setMediaDirs(HashMap<Integer, File> hashMap) {
        this.mediaDirs = hashMap;
    }

    public void uploadFile(String str, boolean z, boolean z2) {
        uploadFile(str, z, z2, MEDIA_DIR_IMAGE);
    }

    public void uploadFile(String str, boolean z, boolean z2, int i) {
        if (str != null) {
            this.fileLoaderQueue.postRunnable(new C03973(z, str, i, z2));
        }
    }
}
