package com.hanista.mobogram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.InputEncryptedFile;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputEncryptedFileBigUploaded;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputEncryptedFileUploaded;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputFile;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputFileBig;
import com.hanista.mobogram.tgnet.TLRPC.TL_upload_saveBigFilePart;
import com.hanista.mobogram.tgnet.TLRPC.TL_upload_saveFilePart;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Locale;

public class FileUploadOperation {
    private long currentFileId;
    private int currentPartNum;
    private long currentUploaded;
    public FileUploadOperationDelegate delegate;
    private int estimatedSize;
    private String fileKey;
    private int fingerprint;
    private boolean isBigFile;
    private boolean isEncrypted;
    private boolean isLastPart;
    private byte[] iv;
    private byte[] ivChange;
    private byte[] key;
    private MessageDigest mdEnc;
    private byte[] readBuffer;
    private int requestToken;
    private int saveInfoTimes;
    private boolean started;
    public int state;
    private FileInputStream stream;
    private long totalFileSize;
    private int totalPartsCount;
    private int uploadChunkSize;
    private int uploadStartTime;
    private String uploadingFilePath;

    public interface FileUploadOperationDelegate {
        void didChangedUploadProgress(FileUploadOperation fileUploadOperation, float f);

        void didFailedUploadingFile(FileUploadOperation fileUploadOperation);

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    /* renamed from: com.hanista.mobogram.messenger.FileUploadOperation.1 */
    class C04091 implements Runnable {
        C04091() {
        }

        public void run() {
            FileUploadOperation.this.startUploadRequest();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileUploadOperation.2 */
    class C04102 implements Runnable {
        final /* synthetic */ long val$finalSize;

        C04102(long j) {
            this.val$finalSize = j;
        }

        public void run() {
            if (!(FileUploadOperation.this.estimatedSize == 0 || this.val$finalSize == 0)) {
                FileUploadOperation.this.estimatedSize = 0;
                FileUploadOperation.this.totalFileSize = this.val$finalSize;
                FileUploadOperation.this.totalPartsCount = ((int) ((FileUploadOperation.this.totalFileSize + ((long) FileUploadOperation.this.uploadChunkSize)) - 1)) / FileUploadOperation.this.uploadChunkSize;
                if (FileUploadOperation.this.started) {
                    FileUploadOperation.this.storeFileUploadInfo(ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0));
                }
            }
            if (FileUploadOperation.this.requestToken == 0) {
                FileUploadOperation.this.startUploadRequest();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.FileUploadOperation.3 */
    class C04113 implements RequestDelegate {
        C04113() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            FileUploadOperation.this.requestToken = 0;
            if (tL_error != null) {
                FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
                FileUploadOperation.this.cleanup();
            } else if (tLObject instanceof TL_boolTrue) {
                FileUploadOperation.this.currentPartNum = FileUploadOperation.this.currentPartNum + 1;
                FileUploadOperation.this.delegate.didChangedUploadProgress(FileUploadOperation.this, ((float) FileUploadOperation.this.currentUploaded) / ((float) FileUploadOperation.this.totalFileSize));
                if (FileUploadOperation.this.isLastPart) {
                    FileUploadOperation.this.state = 3;
                    if (FileUploadOperation.this.key == null) {
                        InputFile tL_inputFileBig;
                        if (FileUploadOperation.this.isBigFile) {
                            tL_inputFileBig = new TL_inputFileBig();
                        } else {
                            tL_inputFileBig = new TL_inputFile();
                            tL_inputFileBig.md5_checksum = String.format(Locale.US, "%32s", new Object[]{new BigInteger(1, FileUploadOperation.this.mdEnc.digest()).toString(16)}).replace(' ', '0');
                        }
                        tL_inputFileBig.parts = FileUploadOperation.this.currentPartNum;
                        tL_inputFileBig.id = FileUploadOperation.this.currentFileId;
                        tL_inputFileBig.name = FileUploadOperation.this.uploadingFilePath.substring(FileUploadOperation.this.uploadingFilePath.lastIndexOf("/") + 1);
                        FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, tL_inputFileBig, null, null, null);
                        FileUploadOperation.this.cleanup();
                        return;
                    }
                    InputEncryptedFile tL_inputEncryptedFileBigUploaded;
                    if (FileUploadOperation.this.isBigFile) {
                        tL_inputEncryptedFileBigUploaded = new TL_inputEncryptedFileBigUploaded();
                    } else {
                        tL_inputEncryptedFileBigUploaded = new TL_inputEncryptedFileUploaded();
                        tL_inputEncryptedFileBigUploaded.md5_checksum = String.format(Locale.US, "%32s", new Object[]{new BigInteger(1, FileUploadOperation.this.mdEnc.digest()).toString(16)}).replace(' ', '0');
                    }
                    tL_inputEncryptedFileBigUploaded.parts = FileUploadOperation.this.currentPartNum;
                    tL_inputEncryptedFileBigUploaded.id = FileUploadOperation.this.currentFileId;
                    tL_inputEncryptedFileBigUploaded.key_fingerprint = FileUploadOperation.this.fingerprint;
                    FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, null, tL_inputEncryptedFileBigUploaded, FileUploadOperation.this.key, FileUploadOperation.this.iv);
                    FileUploadOperation.this.cleanup();
                    return;
                }
                FileUploadOperation.this.startUploadRequest();
            } else {
                FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
                FileUploadOperation.this.cleanup();
            }
        }
    }

    public FileUploadOperation(String str, boolean z, int i) {
        this.uploadChunkSize = TLRPC.MESSAGE_FLAG_EDITED;
        this.state = 0;
        this.requestToken = 0;
        this.currentPartNum = 0;
        this.isLastPart = false;
        this.totalFileSize = 0;
        this.totalPartsCount = 0;
        this.currentUploaded = 0;
        this.saveInfoTimes = 0;
        this.isEncrypted = false;
        this.fingerprint = 0;
        this.isBigFile = false;
        this.estimatedSize = 0;
        this.uploadStartTime = 0;
        this.mdEnc = null;
        this.started = false;
        this.uploadingFilePath = str;
        this.isEncrypted = z;
        this.estimatedSize = i;
    }

    private void cleanup() {
        ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0).edit().remove(this.fileKey + "_time").remove(this.fileKey + "_size").remove(this.fileKey + "_uploaded").remove(this.fileKey + "_id").remove(this.fileKey + "_iv").remove(this.fileKey + "_key").remove(this.fileKey + "_ivc").commit();
        try {
            if (this.stream != null) {
                this.stream.close();
                this.stream = null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private void startUploadRequest() {
        int i = 1;
        if (this.state == 1) {
            try {
                int i2;
                int i3;
                TLObject tL_upload_saveBigFilePart;
                this.started = true;
                if (this.stream == null) {
                    int i4;
                    File file = new File(this.uploadingFilePath);
                    this.stream = new FileInputStream(file);
                    if (this.estimatedSize != 0) {
                        this.totalFileSize = (long) this.estimatedSize;
                    } else {
                        this.totalFileSize = file.length();
                    }
                    if (this.totalFileSize > 10485760) {
                        this.isBigFile = true;
                    } else {
                        try {
                            this.mdEnc = MessageDigest.getInstance("MD5");
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                    this.uploadChunkSize = (int) Math.max(32, ((this.totalFileSize + 3072000) - 1) / 3072000);
                    if (TLRPC.MESSAGE_FLAG_HAS_VIEWS % this.uploadChunkSize != 0) {
                        i4 = 64;
                        while (this.uploadChunkSize > i4) {
                            i4 *= 2;
                        }
                        this.uploadChunkSize = i4;
                    }
                    this.uploadChunkSize *= TLRPC.MESSAGE_FLAG_HAS_VIEWS;
                    this.totalPartsCount = ((int) ((this.totalFileSize + ((long) this.uploadChunkSize)) - 1)) / this.uploadChunkSize;
                    this.readBuffer = new byte[this.uploadChunkSize];
                    this.fileKey = Utilities.MD5(this.uploadingFilePath + (this.isEncrypted ? "enc" : TtmlNode.ANONYMOUS_REGION_ID));
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
                    long j = sharedPreferences.getLong(this.fileKey + "_size", 0);
                    this.uploadStartTime = (int) (System.currentTimeMillis() / 1000);
                    if (this.estimatedSize == 0 && j == this.totalFileSize) {
                        this.currentFileId = sharedPreferences.getLong(this.fileKey + "_id", 0);
                        i4 = sharedPreferences.getInt(this.fileKey + "_time", 0);
                        j = sharedPreferences.getLong(this.fileKey + "_uploaded", 0);
                        if (this.isEncrypted) {
                            String string = sharedPreferences.getString(this.fileKey + "_iv", null);
                            String string2 = sharedPreferences.getString(this.fileKey + "_key", null);
                            if (string == null || string2 == null) {
                                i2 = 1;
                            } else {
                                this.key = Utilities.hexToBytes(string2);
                                this.iv = Utilities.hexToBytes(string);
                                if (this.key == null || this.iv == null || this.key.length != 32 || this.iv.length != 32) {
                                    i2 = 1;
                                } else {
                                    this.ivChange = new byte[32];
                                    System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
                                    i2 = 0;
                                }
                            }
                        } else {
                            i2 = 0;
                        }
                        if (i2 != 0 || i4 == 0) {
                            i2 = 1;
                        } else {
                            if (this.isBigFile && i4 < this.uploadStartTime - 86400) {
                                i4 = 0;
                            } else if (!this.isBigFile && ((float) i4) < ((float) this.uploadStartTime) - 5400.0f) {
                                i4 = 0;
                            }
                            if (i4 != 0) {
                                if (j > 0) {
                                    this.currentUploaded = j;
                                    this.currentPartNum = (int) (j / ((long) this.uploadChunkSize));
                                    if (this.isBigFile) {
                                        this.stream.skip(j);
                                        if (this.isEncrypted) {
                                            String string3 = sharedPreferences.getString(this.fileKey + "_ivc", null);
                                            if (string3 != null) {
                                                this.ivChange = Utilities.hexToBytes(string3);
                                                if (this.ivChange == null || this.ivChange.length != 32) {
                                                    this.currentUploaded = 0;
                                                    this.currentPartNum = 0;
                                                } else {
                                                    i = i2;
                                                }
                                            } else {
                                                this.currentUploaded = 0;
                                                this.currentPartNum = 0;
                                            }
                                            i2 = i;
                                        }
                                    } else {
                                        for (int i5 = 0; ((long) i5) < this.currentUploaded / ((long) this.uploadChunkSize); i5++) {
                                            int read = this.stream.read(this.readBuffer);
                                            i3 = (!this.isEncrypted || read % 16 == 0) ? 0 : (16 - (read % 16)) + 0;
                                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(read + i3);
                                            if (read != this.uploadChunkSize || this.totalPartsCount == this.currentPartNum + 1) {
                                                this.isLastPart = true;
                                            }
                                            nativeByteBuffer.writeBytes(this.readBuffer, 0, read);
                                            if (this.isEncrypted) {
                                                for (i = 0; i < i3; i++) {
                                                    nativeByteBuffer.writeByte(0);
                                                }
                                                Utilities.aesIgeEncryption(nativeByteBuffer.buffer, this.key, this.ivChange, true, true, 0, i3 + read);
                                            }
                                            nativeByteBuffer.rewind();
                                            this.mdEnc.update(nativeByteBuffer.buffer);
                                            nativeByteBuffer.reuse();
                                        }
                                    }
                                } else {
                                    i2 = 1;
                                }
                            }
                        }
                    } else {
                        i2 = 1;
                    }
                    if (i2 != 0) {
                        if (this.isEncrypted) {
                            this.iv = new byte[32];
                            this.key = new byte[32];
                            this.ivChange = new byte[32];
                            Utilities.random.nextBytes(this.iv);
                            Utilities.random.nextBytes(this.key);
                            System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
                        }
                        this.currentFileId = Utilities.random.nextLong();
                        if (this.estimatedSize == 0) {
                            storeFileUploadInfo(sharedPreferences);
                        }
                    }
                    if (this.isEncrypted) {
                        try {
                            MessageDigest instance = MessageDigest.getInstance("MD5");
                            Object obj = new byte[64];
                            System.arraycopy(this.key, 0, obj, 0, 32);
                            System.arraycopy(this.iv, 0, obj, 32, 32);
                            byte[] digest = instance.digest(obj);
                            for (i = 0; i < 4; i++) {
                                this.fingerprint |= ((digest[i] ^ digest[i + 4]) & NalUnitUtil.EXTENDED_SAR) << (i * 8);
                            }
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                } else if (this.estimatedSize == 0) {
                    if (this.saveInfoTimes >= 4) {
                        this.saveInfoTimes = 0;
                    }
                    if ((this.isBigFile && this.currentUploaded % 1048576 == 0) || (!this.isBigFile && this.saveInfoTimes == 0)) {
                        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0).edit();
                        edit.putLong(this.fileKey + "_uploaded", this.currentUploaded);
                        if (this.isEncrypted) {
                            edit.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
                        }
                        edit.commit();
                    }
                    this.saveInfoTimes++;
                }
                if (this.estimatedSize != 0) {
                    if (this.currentUploaded + ((long) this.uploadChunkSize) > this.stream.getChannel().size()) {
                        return;
                    }
                }
                i2 = this.stream.read(this.readBuffer);
                i3 = (!this.isEncrypted || i2 % 16 == 0) ? 0 : (16 - (i2 % 16)) + 0;
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(i2 + i3);
                if (i2 != this.uploadChunkSize || (this.estimatedSize == 0 && this.totalPartsCount == this.currentPartNum + 1)) {
                    this.isLastPart = true;
                }
                nativeByteBuffer2.writeBytes(this.readBuffer, 0, i2);
                if (this.isEncrypted) {
                    for (i = 0; i < i3; i++) {
                        nativeByteBuffer2.writeByte(0);
                    }
                    Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, this.key, this.ivChange, true, true, 0, i3 + i2);
                }
                nativeByteBuffer2.rewind();
                if (!this.isBigFile) {
                    this.mdEnc.update(nativeByteBuffer2.buffer);
                }
                if (this.isBigFile) {
                    tL_upload_saveBigFilePart = new TL_upload_saveBigFilePart();
                    tL_upload_saveBigFilePart.file_part = this.currentPartNum;
                    tL_upload_saveBigFilePart.file_id = this.currentFileId;
                    if (this.estimatedSize != 0) {
                        tL_upload_saveBigFilePart.file_total_parts = -1;
                    } else {
                        tL_upload_saveBigFilePart.file_total_parts = this.totalPartsCount;
                    }
                    tL_upload_saveBigFilePart.bytes = nativeByteBuffer2;
                } else {
                    tL_upload_saveBigFilePart = new TL_upload_saveFilePart();
                    tL_upload_saveBigFilePart.file_part = this.currentPartNum;
                    tL_upload_saveBigFilePart.file_id = this.currentFileId;
                    tL_upload_saveBigFilePart.bytes = nativeByteBuffer2;
                }
                this.currentUploaded += (long) i2;
                this.requestToken = ConnectionsManager.getInstance().sendRequest(tL_upload_saveBigFilePart, new C04113(), 0, 4);
            } catch (Throwable e22) {
                FileLog.m18e("tmessages", e22);
                this.delegate.didFailedUploadingFile(this);
                cleanup();
            }
        }
    }

    private void storeFileUploadInfo(SharedPreferences sharedPreferences) {
        Editor edit = sharedPreferences.edit();
        edit.putInt(this.fileKey + "_time", this.uploadStartTime);
        edit.putLong(this.fileKey + "_size", this.totalFileSize);
        edit.putLong(this.fileKey + "_id", this.currentFileId);
        edit.remove(this.fileKey + "_uploaded");
        if (this.isEncrypted) {
            edit.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
            edit.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
            edit.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
        }
        edit.commit();
    }

    public void cancel() {
        if (this.state != 3) {
            this.state = 2;
            if (this.requestToken != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.requestToken, true);
            }
            this.delegate.didFailedUploadingFile(this);
            cleanup();
        }
    }

    protected void checkNewDataAvailable(long j) {
        Utilities.stageQueue.postRunnable(new C04102(j));
    }

    public long getTotalFileSize() {
        return this.totalFileSize;
    }

    public void start() {
        if (this.state == 0) {
            this.state = 1;
            Utilities.stageQueue.postRunnable(new C04091());
        }
    }
}
