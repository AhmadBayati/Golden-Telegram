package com.hanista.mobogram.messenger;

import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputFileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentEncrypted;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileEncryptedLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputFileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_upload_file;
import com.hanista.mobogram.tgnet.TLRPC.TL_upload_getFile;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 2;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int datacenter_id;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadedBytes;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile fiv;
    private boolean isForceRequest;
    private byte[] iv;
    private byte[] key;
    private InputFileLocation location;
    private int nextDownloadOffset;
    private String orgName;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestsCount;
    private boolean started;
    private volatile int state;
    private File storePath;
    private File tempPath;
    private int totalBytesCount;

    /* renamed from: com.hanista.mobogram.messenger.FileLoadOperation.1 */
    class C03871 implements Runnable {
        C03871() {
        }

        public void run() {
            if (FileLoadOperation.this.totalBytesCount == 0 || FileLoadOperation.this.downloadedBytes != FileLoadOperation.this.totalBytesCount) {
                FileLoadOperation.this.startDownloadRequest();
                return;
            }
            try {
                FileLoadOperation.this.onFinishLoadingFile();
            } catch (Exception e) {
                FileLoadOperation.this.onFail(true, 0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoadOperation.2 */
    class C03882 implements Runnable {
        C03882() {
        }

        public void run() {
            if (FileLoadOperation.this.state != FileLoadOperation.stateFinished && FileLoadOperation.this.state != FileLoadOperation.stateFailed) {
                if (FileLoadOperation.this.requestInfos != null) {
                    for (int i = 0; i < FileLoadOperation.this.requestInfos.size(); i += FileLoadOperation.stateDownloading) {
                        RequestInfo requestInfo = (RequestInfo) FileLoadOperation.this.requestInfos.get(i);
                        if (requestInfo.requestToken != 0) {
                            ConnectionsManager.getInstance().cancelRequest(requestInfo.requestToken, true);
                        }
                    }
                }
                FileLoadOperation.this.onFail(false, FileLoadOperation.stateDownloading);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoadOperation.3 */
    class C03893 implements Runnable {
        C03893() {
        }

        public void run() {
            try {
                FileLoadOperation.this.onFinishLoadingFile();
            } catch (Exception e) {
                FileLoadOperation.this.onFail(false, 0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoadOperation.4 */
    class C03904 implements Runnable {
        final /* synthetic */ int val$reason;

        C03904(int i) {
            this.val$reason = i;
        }

        public void run() {
            FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, this.val$reason);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileLoadOperation.5 */
    class C03915 implements RequestDelegate {
        final /* synthetic */ RequestInfo val$requestInfo;

        C03915(RequestInfo requestInfo) {
            this.val$requestInfo = requestInfo;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            this.val$requestInfo.response = (TL_upload_file) tLObject;
            FileLoadOperation.this.processRequestResult(this.val$requestInfo, tL_error);
        }
    }

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    private static class RequestInfo {
        private int offset;
        private int requestToken;
        private TL_upload_file response;

        private RequestInfo() {
        }
    }

    public FileLoadOperation(Document document) {
        int i = -1;
        this.state = 0;
        try {
            String str;
            boolean z;
            if (document instanceof TL_documentEncrypted) {
                this.location = new TL_inputEncryptedFileLocation();
                this.location.id = document.id;
                this.location.access_hash = document.access_hash;
                this.datacenter_id = document.dc_id;
                this.iv = new byte[32];
                System.arraycopy(document.iv, 0, this.iv, 0, this.iv.length);
                this.key = document.key;
            } else if (document instanceof TL_document) {
                this.location = new TL_inputDocumentFileLocation();
                this.location.id = document.id;
                this.location.access_hash = document.access_hash;
                this.datacenter_id = document.dc_id;
            }
            this.totalBytesCount = document.size;
            if (!(this.key == null || this.totalBytesCount % 16 == 0)) {
                this.bytesCountPadding = 16 - (this.totalBytesCount % 16);
                this.totalBytesCount += this.bytesCountPadding;
            }
            this.ext = FileLoader.getDocumentFileName(document);
            if (this.ext != null) {
                int lastIndexOf = this.ext.lastIndexOf(46);
                if (lastIndexOf != -1) {
                    this.ext = this.ext.substring(lastIndexOf);
                    if (this.ext.length() <= stateDownloading) {
                        if (document.mime_type != null) {
                            str = document.mime_type;
                            switch (str.hashCode()) {
                                case 187091926:
                                    if (str.equals("audio/ogg")) {
                                        z = true;
                                        break;
                                    }
                                    break;
                                case 1331848029:
                                    if (str.equals(MimeTypes.VIDEO_MP4)) {
                                        i = 0;
                                        break;
                                    }
                                    break;
                            }
                            switch (i) {
                                case VideoPlayer.TRACK_DEFAULT /*0*/:
                                    this.ext = ".mp4";
                                    break;
                                case stateDownloading /*1*/:
                                    this.ext = ".ogg";
                                    break;
                                default:
                                    this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                                    break;
                            }
                        }
                        this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    if (MoboConstants.f1354u && !this.ext.contains("webp") && !FileLoader.isInValidForOrgFileName(document)) {
                        this.orgName = FileLoader.getDocName(document);
                        return;
                    }
                    return;
                }
            }
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
            if (this.ext.length() <= stateDownloading) {
                if (document.mime_type != null) {
                    str = document.mime_type;
                    switch (str.hashCode()) {
                        case 187091926:
                            if (str.equals("audio/ogg")) {
                                z = true;
                                break;
                            }
                            break;
                        case 1331848029:
                            if (str.equals(MimeTypes.VIDEO_MP4)) {
                                i = 0;
                                break;
                            }
                            break;
                    }
                    switch (i) {
                        case VideoPlayer.TRACK_DEFAULT /*0*/:
                            this.ext = ".mp4";
                            break;
                        case stateDownloading /*1*/:
                            this.ext = ".ogg";
                            break;
                        default:
                            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                            break;
                    }
                }
                this.ext = TtmlNode.ANONYMOUS_REGION_ID;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            onFail(true, 0);
        }
        if (MoboConstants.f1354u) {
        }
    }

    public FileLoadOperation(FileLocation fileLocation, String str, int i) {
        this.state = 0;
        if (fileLocation instanceof TL_fileEncryptedLocation) {
            this.location = new TL_inputEncryptedFileLocation();
            this.location.id = fileLocation.volume_id;
            this.location.volume_id = fileLocation.volume_id;
            this.location.access_hash = fileLocation.secret;
            this.location.local_id = fileLocation.local_id;
            this.iv = new byte[32];
            System.arraycopy(fileLocation.iv, 0, this.iv, 0, this.iv.length);
            this.key = fileLocation.key;
            this.datacenter_id = fileLocation.dc_id;
        } else if (fileLocation instanceof TL_fileLocation) {
            this.location = new TL_inputFileLocation();
            this.location.volume_id = fileLocation.volume_id;
            this.location.secret = fileLocation.secret;
            this.location.local_id = fileLocation.local_id;
            this.datacenter_id = fileLocation.dc_id;
        }
        this.totalBytesCount = i;
        if (str == null) {
            str = "jpg";
        }
        this.ext = str;
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
        }
        if (this.delayedRequestInfos != null) {
            for (int i = 0; i < this.delayedRequestInfos.size(); i += stateDownloading) {
                RequestInfo requestInfo = (RequestInfo) this.delayedRequestInfos.get(i);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void onFail(boolean z, int i) {
        cleanup();
        this.state = stateFailed;
        if (z) {
            Utilities.stageQueue.postRunnable(new C03904(i));
        } else {
            this.delegate.didFailedLoadingFile(this, i);
        }
    }

    private void onFinishLoadingFile() {
        if (this.state == stateDownloading) {
            this.state = stateFinished;
            cleanup();
            if (this.cacheIvTemp != null) {
                this.cacheIvTemp.delete();
                this.cacheIvTemp = null;
            }
            if (!(this.cacheFileTemp == null || FileLoader.renameTo(this.cacheFileTemp, this.cacheFileFinal))) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.m16e("tmessages", "unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                }
                this.renameRetryCount += stateDownloading;
                if (this.renameRetryCount < stateFinished) {
                    this.state = stateDownloading;
                    Utilities.stageQueue.postRunnable(new C03893(), 200);
                    return;
                }
                this.cacheFileFinal = this.cacheFileTemp;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.m16e("tmessages", "finished downloading file to " + this.cacheFileFinal);
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
        }
    }

    private void processRequestResult(RequestInfo requestInfo, TL_error tL_error) {
        Integer num = null;
        this.requestInfos.remove(requestInfo);
        if (tL_error == null) {
            try {
                if (this.downloadedBytes != requestInfo.offset) {
                    if (this.state == stateDownloading) {
                        this.delayedRequestInfos.add(requestInfo);
                        requestInfo.response.disableFree = true;
                    }
                } else if (requestInfo.response.bytes == null || requestInfo.response.bytes.limit() == 0) {
                    onFinishLoadingFile();
                } else {
                    int limit = requestInfo.response.bytes.limit();
                    this.downloadedBytes += limit;
                    boolean z = (limit != this.currentDownloadChunkSize || ((this.totalBytesCount == this.downloadedBytes || this.downloadedBytes % this.currentDownloadChunkSize != 0) && (this.totalBytesCount <= 0 || this.totalBytesCount <= this.downloadedBytes))) ? stateDownloading : false;
                    if (this.key != null) {
                        Utilities.aesIgeEncryption(requestInfo.response.bytes.buffer, this.key, this.iv, false, true, 0, requestInfo.response.bytes.limit());
                        if (z && this.bytesCountPadding != 0) {
                            requestInfo.response.bytes.limit(requestInfo.response.bytes.limit() - this.bytesCountPadding);
                        }
                    }
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.getChannel().write(requestInfo.response.bytes.buffer);
                    }
                    if (this.fiv != null) {
                        this.fiv.seek(0);
                        this.fiv.write(this.iv);
                    }
                    if (this.totalBytesCount > 0 && this.state == stateDownloading) {
                        this.delegate.didChangedLoadProgress(this, Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, ((float) this.downloadedBytes) / ((float) this.totalBytesCount)));
                    }
                    for (int i = 0; i < this.delayedRequestInfos.size(); i += stateDownloading) {
                        RequestInfo requestInfo2 = (RequestInfo) this.delayedRequestInfos.get(i);
                        if (this.downloadedBytes == requestInfo2.offset) {
                            this.delayedRequestInfos.remove(i);
                            processRequestResult(requestInfo2, null);
                            requestInfo2.response.disableFree = false;
                            requestInfo2.response.freeResources();
                            break;
                        }
                    }
                    if (z) {
                        onFinishLoadingFile();
                    } else {
                        startDownloadRequest();
                    }
                }
            } catch (Throwable e) {
                onFail(false, 0);
                FileLog.m18e("tmessages", e);
            }
        } else if (tL_error.text.contains("FILE_MIGRATE_")) {
            Scanner scanner = new Scanner(tL_error.text.replace("FILE_MIGRATE_", TtmlNode.ANONYMOUS_REGION_ID));
            scanner.useDelimiter(TtmlNode.ANONYMOUS_REGION_ID);
            try {
                num = Integer.valueOf(scanner.nextInt());
            } catch (Exception e2) {
            }
            if (num == null) {
                onFail(false, 0);
                return;
            }
            this.datacenter_id = num.intValue();
            this.nextDownloadOffset = 0;
            startDownloadRequest();
        } else if (tL_error.text.contains("OFFSET_INVALID")) {
            if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
                try {
                    onFinishLoadingFile();
                    return;
                } catch (Throwable e3) {
                    FileLog.m18e("tmessages", e3);
                    onFail(false, 0);
                    return;
                }
            }
            onFail(false, 0);
        } else if (tL_error.text.contains("RETRY_LIMIT")) {
            onFail(false, stateFailed);
        } else {
            if (this.location != null) {
                FileLog.m16e("tmessages", TtmlNode.ANONYMOUS_REGION_ID + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
            }
            onFail(false, 0);
        }
    }

    private void startDownloadRequest() {
        if (this.state != stateDownloading) {
            return;
        }
        if ((this.totalBytesCount <= 0 || this.nextDownloadOffset < this.totalBytesCount) && this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) {
            int max = this.totalBytesCount > 0 ? Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size()) : stateDownloading;
            int i = 0;
            while (i < max) {
                if (this.totalBytesCount <= 0 || this.nextDownloadOffset < this.totalBytesCount) {
                    boolean z = this.totalBytesCount <= 0 || i == max - 1 || (this.totalBytesCount > 0 && this.nextDownloadOffset + this.currentDownloadChunkSize >= this.totalBytesCount);
                    TLObject tL_upload_getFile = new TL_upload_getFile();
                    tL_upload_getFile.location = this.location;
                    tL_upload_getFile.offset = this.nextDownloadOffset;
                    tL_upload_getFile.limit = this.currentDownloadChunkSize;
                    this.nextDownloadOffset += this.currentDownloadChunkSize;
                    RequestInfo requestInfo = new RequestInfo();
                    this.requestInfos.add(requestInfo);
                    requestInfo.offset = tL_upload_getFile.offset;
                    requestInfo.requestToken = ConnectionsManager.getInstance().sendRequest(tL_upload_getFile, new C03915(requestInfo), null, (this.isForceRequest ? 32 : 0) | stateFailed, this.datacenter_id, this.requestsCount % stateFailed == 0 ? stateFailed : ConnectionsManager.ConnectionTypeDownload2, z);
                    this.requestsCount += stateDownloading;
                    i += stateDownloading;
                } else {
                    return;
                }
            }
        }
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new C03882());
    }

    public String getFileName() {
        return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setDelegate(FileLoadOperationDelegate fileLoadOperationDelegate) {
        this.delegate = fileLoadOperationDelegate;
    }

    public void setForceRequest(boolean z) {
        this.isForceRequest = z;
    }

    public void setPaths(File file, File file2) {
        this.storePath = file;
        this.tempPath = file2;
    }

    public boolean start() {
        if (this.state != 0) {
            return false;
        }
        if (this.location == null) {
            onFail(true, 0);
            return false;
        }
        String str;
        String str2;
        String str3 = null;
        if (this.location.volume_id == 0 || this.location.local_id == 0) {
            if (this.datacenter_id == 0 || this.location.id == 0) {
                onFail(true, 0);
                return false;
            }
            str = this.datacenter_id + "_" + this.location.id + ".temp";
            str2 = this.datacenter_id + "_" + this.location.id + this.ext;
            if (this.key != null) {
                str3 = this.datacenter_id + "_" + this.location.id + ".iv";
            }
        } else if (this.datacenter_id == TLRPC.MESSAGE_FLAG_MEGAGROUP || this.location.volume_id == -2147483648L || this.datacenter_id == 0) {
            onFail(true, 0);
            return false;
        } else {
            str = this.location.volume_id + "_" + this.location.local_id + ".temp";
            str2 = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
            if (this.key != null) {
                str3 = this.location.volume_id + "_" + this.location.local_id + ".iv";
            }
        }
        if (MoboConstants.f1354u && this.orgName != null) {
            str2 = this.orgName;
        }
        this.currentDownloadChunkSize = this.totalBytesCount >= bigFileSizeFrom ? downloadChunkSizeBig : downloadChunkSize;
        this.currentMaxDownloadRequests = this.totalBytesCount >= bigFileSizeFrom ? stateFailed : maxDownloadRequests;
        this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
        this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
        this.state = stateDownloading;
        this.cacheFileFinal = new File(this.storePath, str2);
        boolean exists = this.cacheFileFinal.exists();
        if (exists && this.orgName != null && MoboConstants.f1354u) {
            this.cacheFileFinal.delete();
            exists = false;
        }
        if (!(!exists || this.totalBytesCount == 0 || ((long) this.totalBytesCount) == this.cacheFileFinal.length())) {
            this.cacheFileFinal.delete();
        }
        if (this.cacheFileFinal.exists()) {
            this.started = true;
            try {
                onFinishLoadingFile();
            } catch (Exception e) {
                onFail(true, 0);
            }
        } else {
            this.cacheFileTemp = new File(this.tempPath, str);
            if (this.cacheFileTemp.exists()) {
                this.downloadedBytes = (int) this.cacheFileTemp.length();
                int i = (this.downloadedBytes / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
                this.downloadedBytes = i;
                this.nextDownloadOffset = i;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.m15d("tmessages", "start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
            }
            if (str3 != null) {
                this.cacheIvTemp = new File(this.tempPath, str3);
                try {
                    this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
                    long length = this.cacheIvTemp.length();
                    if (length <= 0 || length % 32 != 0) {
                        this.downloadedBytes = 0;
                    } else {
                        this.fiv.read(this.iv, 0, 32);
                    }
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                    this.downloadedBytes = 0;
                }
            }
            try {
                this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
                if (this.downloadedBytes != 0) {
                    this.fileOutputStream.seek((long) this.downloadedBytes);
                }
            } catch (Throwable e22) {
                FileLog.m18e("tmessages", e22);
            }
            if (this.fileOutputStream == null) {
                onFail(true, 0);
                return false;
            }
            this.started = true;
            Utilities.stageQueue.postRunnable(new C03871());
        }
        return true;
    }

    public boolean wasStarted() {
        return this.started;
    }
}
