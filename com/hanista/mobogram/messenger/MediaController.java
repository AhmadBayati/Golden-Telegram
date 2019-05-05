package com.hanista.mobogram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.PointerIconCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.audioinfo.AudioInfo;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.video.MP4Builder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.voicechanger.DataHelper;
import com.hanista.mobogram.mobo.voicechanger.FrameType;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.dsp.KissFFT;
import com.hanista.mobogram.mobo.voicechanger.dsp.Math;
import com.hanista.mobogram.mobo.voicechanger.dsp.p024a.StftPostprocessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.p024a.StftPreprocessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.DetuneProcessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.HoarsenessProcessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.NativeResampleProcessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.NativeTimescaleProcessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.RobotizeProcessor;
import com.hanista.mobogram.mobo.voicechanger.p021a.HeadsetMode;
import com.hanista.mobogram.mobo.voicechanger.p022b.AudioDevice;
import com.hanista.mobogram.mobo.voicechanger.p022b.p023a.PcmInDevice;
import com.hanista.mobogram.mobo.voicechanger.p022b.p023a.PcmOutDevice;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.InputDocument;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAnimated;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_messages;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.PhotoViewer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class MediaController implements SensorEventListener, OnAudioFocusChangeListener, NotificationCenterDelegate {
    public static int[] f27a;
    private static Runnable al;
    private static volatile MediaController bv;
    public static AlbumEntry f28e;
    private static final String[] f29f;
    private static final String[] f30g;
    private boolean f31A;
    private boolean f32B;
    private boolean f33C;
    private boolean f34D;
    private boolean f35E;
    private float f36F;
    private float[] f37G;
    private float[] f38H;
    private float[] f39I;
    private int f40J;
    private boolean f41K;
    private int f42L;
    private boolean f43M;
    private ArrayList<MessageObject> f44N;
    private final Object f45O;
    private boolean f46P;
    private boolean f47Q;
    private HashMap<String, MessageObject> f48R;
    private boolean f49S;
    private ArrayList<MessageObject> f50T;
    private HashMap<Integer, MessageObject> f51U;
    private int f52V;
    private ArrayList<DownloadObject> f53W;
    private ArrayList<DownloadObject> f54X;
    private ArrayList<DownloadObject> f55Y;
    private ArrayList<DownloadObject> f56Z;
    private long aA;
    private long aB;
    private int aC;
    private Timer aD;
    private final Object aE;
    private int aF;
    private ArrayList<MessageObject> aG;
    private ArrayList<MessageObject> aH;
    private int aI;
    private boolean aJ;
    private boolean aK;
    private boolean aL;
    private AudioInfo aM;
    private AudioRecord aN;
    private TL_document aO;
    private File aP;
    private long aQ;
    private long aR;
    private long aS;
    private MessageObject aT;
    private DispatchQueue aU;
    private DispatchQueue aV;
    private ArrayList<AudioBuffer> aW;
    private ArrayList<AudioBuffer> aX;
    private final Object aY;
    private final Object aZ;
    private ArrayList<DownloadObject> aa;
    private ArrayList<DownloadObject> ab;
    private HashMap<String, DownloadObject> ac;
    private boolean ad;
    private boolean ae;
    private boolean af;
    private boolean ag;
    private boolean ah;
    private boolean ai;
    private int aj;
    private Runnable ak;
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> am;
    private HashMap<String, ArrayList<MessageObject>> an;
    private HashMap<Integer, String> ao;
    private boolean ap;
    private HashMap<String, FileDownloadProgressListener> aq;
    private ArrayList<FileDownloadProgressListener> ar;
    private int as;
    private boolean at;
    private MediaPlayer au;
    private AudioTrack av;
    private int aw;
    private MessageObject ax;
    private int ay;
    private boolean az;
    public int f57b;
    private NativeResampleProcessor bA;
    private KissFFT bB;
    private float[] bC;
    private float[] bD;
    private Runnable bE;
    private short[] ba;
    private long bb;
    private final Object bc;
    private ArrayList<ByteBuffer> bd;
    private ByteBuffer be;
    private int bf;
    private int bg;
    private Runnable bh;
    private DispatchQueue bi;
    private DispatchQueue bj;
    private Runnable bk;
    private ExternalObserver bl;
    private InternalObserver bm;
    private long bn;
    private long bo;
    private long bp;
    private EncryptedChat bq;
    private ArrayList<Long> br;
    private int bs;
    private StopMediaObserverRunnable bt;
    private String[] bu;
    private float[] bw;
    private StftPreprocessor bx;
    private StftPostprocessor by;
    private NativeTimescaleProcessor bz;
    public int f58c;
    public int f59d;
    private final Object f60h;
    private HashMap<Long, Long> f61i;
    private SensorManager f62j;
    private WakeLock f63k;
    private Sensor f64l;
    private Sensor f65m;
    private Sensor f66n;
    private Sensor f67o;
    private boolean f68p;
    private ChatActivity f69q;
    private boolean f70r;
    private int f71s;
    private int f72t;
    private int f73u;
    private long f74v;
    private long f75w;
    private boolean f76x;
    private boolean f77y;
    private float f78z;

    /* renamed from: com.hanista.mobogram.messenger.MediaController.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ float val$progress;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.12.1 */
        class C04601 implements Runnable {
            C04601() {
            }

            public void run() {
                if (!MediaController.this.at) {
                    MediaController.this.aC = 3;
                    MediaController.this.aB = (long) (((float) MediaController.this.aA) * AnonymousClass12.this.val$progress);
                    if (MediaController.this.av != null) {
                        MediaController.this.av.play();
                    }
                    MediaController.this.aw = (int) ((((float) MediaController.this.aA) / 48.0f) * AnonymousClass12.this.val$progress);
                    MediaController.this.m41N();
                }
            }
        }

        AnonymousClass12(float f) {
            this.val$progress = f;
        }

        public void run() {
            MediaController.this.seekOpusFile(this.val$progress);
            synchronized (MediaController.this.aY) {
                MediaController.this.aX.addAll(MediaController.this.aW);
                MediaController.this.aW.clear();
            }
            AndroidUtilities.runOnUIThread(new C04601());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ File val$cacheFile;
        final /* synthetic */ Boolean[] val$result;
        final /* synthetic */ Semaphore val$semaphore;

        AnonymousClass13(Boolean[] boolArr, File file, Semaphore semaphore) {
            this.val$result = boolArr;
            this.val$cacheFile = file;
            this.val$semaphore = semaphore;
        }

        public void run() {
            this.val$result[0] = Boolean.valueOf(MediaController.this.openOpusFile(this.val$cacheFile.getAbsolutePath()) != 0);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.15 */
    class AnonymousClass15 implements OnCompletionListener {
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass15(MessageObject messageObject) {
            this.val$messageObject = messageObject;
        }

        public void onCompletion(MediaPlayer mediaPlayer) {
            if (MediaController.this.aG.isEmpty() || MediaController.this.aG.size() <= 1) {
                MediaController mediaController = MediaController.this;
                boolean z = this.val$messageObject != null && this.val$messageObject.isVoice();
                mediaController.m156a(true, true, z);
                return;
            }
            MediaController.this.m108d(true);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ MessageObject val$reply_to_msg;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.17.1 */
        class C04611 implements Runnable {
            C04611() {
            }

            public void run() {
                MediaController.this.bh = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MediaController.17.2 */
        class C04622 implements Runnable {
            C04622() {
            }

            public void run() {
                MediaController.this.bh = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MediaController.17.3 */
        class C04633 implements Runnable {
            C04633() {
            }

            public void run() {
                MediaController.this.bh = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MediaController.17.4 */
        class C04644 implements Runnable {
            C04644() {
            }

            public void run() {
                MediaController.this.bh = null;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
            }
        }

        AnonymousClass17(long j, MessageObject messageObject) {
            this.val$dialog_id = j;
            this.val$reply_to_msg = messageObject;
        }

        public void run() {
            if (MediaController.this.aN != null) {
                AndroidUtilities.runOnUIThread(new C04611());
                return;
            }
            MediaController.this.aO = new TL_document();
            MediaController.this.aO.dc_id = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            MediaController.this.aO.id = (long) UserConfig.lastLocalId;
            MediaController.this.aO.user_id = UserConfig.getClientUserId();
            MediaController.this.aO.mime_type = "audio/ogg";
            MediaController.this.aO.thumb = new TL_photoSizeEmpty();
            MediaController.this.aO.thumb.type = "s";
            UserConfig.lastLocalId--;
            UserConfig.saveConfig(false);
            MediaController.this.aP = new File(FileLoader.getInstance().getDirectory(4), FileLoader.getAttachFileName(MediaController.this.aO));
            try {
                if (MediaController.this.startRecord(MediaController.this.aP.getAbsolutePath()) == 0) {
                    AndroidUtilities.runOnUIThread(new C04622());
                    return;
                }
                int i = 16000;
                if (MoboConstants.aQ == 1) {
                    i = MoboConstants.aR;
                }
                MediaController.this.aN = new AudioRecord(1, i, 16, 2, MediaController.this.bf * 10);
                MediaController.this.aQ = System.currentTimeMillis();
                MediaController.this.aR = 0;
                MediaController.this.bb = 0;
                MediaController.this.aS = this.val$dialog_id;
                MediaController.this.aT = this.val$reply_to_msg;
                MediaController.this.be.rewind();
                MediaController.this.aN.startRecording();
                if (MoboConstants.aQ == 0 || MoboConstants.aQ == 1) {
                    MediaController.this.bi.postRunnable(MediaController.this.bk);
                } else {
                    MediaController.this.m51S();
                    MediaController.this.bi.postRunnable(MediaController.this.bE);
                }
                AndroidUtilities.runOnUIThread(new C04644());
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                MediaController.this.aO = null;
                MediaController.this.stopRecord();
                MediaController.this.aP.delete();
                MediaController.this.aP = null;
                try {
                    MediaController.this.aN.release();
                    MediaController.this.aN = null;
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
                AndroidUtilities.runOnUIThread(new C04633());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ String val$id;
        final /* synthetic */ String val$path;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.18.1 */
        class C04651 implements Runnable {
            final /* synthetic */ byte[] val$waveform;

            C04651(byte[] bArr) {
                this.val$waveform = bArr;
            }

            public void run() {
                MessageObject messageObject = (MessageObject) MediaController.this.f48R.remove(AnonymousClass18.this.val$id);
                if (messageObject != null && this.val$waveform != null) {
                    for (int i = 0; i < messageObject.getDocument().attributes.size(); i++) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) messageObject.getDocument().attributes.get(i);
                        if (documentAttribute instanceof TL_documentAttributeAudio) {
                            documentAttribute.waveform = this.val$waveform;
                            documentAttribute.flags |= 4;
                            break;
                        }
                    }
                    messages_Messages tL_messages_messages = new TL_messages_messages();
                    tL_messages_messages.messages.add(messageObject.messageOwner);
                    MessagesStorage.getInstance().putMessages(tL_messages_messages, messageObject.getDialogId(), -1, 0, false);
                    new ArrayList().add(messageObject);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), r0);
                }
            }
        }

        AnonymousClass18(String str, String str2) {
            this.val$path = str;
            this.val$id = str2;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C04651(MediaController.m71a().getWaveform(this.val$path)));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ TL_document val$audioToSend;
        final /* synthetic */ File val$recordingAudioFileToSend;
        final /* synthetic */ int val$send;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.19.1 */
        class C04661 implements Runnable {
            C04661() {
            }

            public void run() {
                VideoEditedInfo videoEditedInfo = null;
                AnonymousClass19.this.val$audioToSend.date = ConnectionsManager.getInstance().getCurrentTime();
                AnonymousClass19.this.val$audioToSend.size = (int) AnonymousClass19.this.val$recordingAudioFileToSend.length();
                TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                tL_documentAttributeAudio.voice = true;
                tL_documentAttributeAudio.waveform = MediaController.this.getWaveform2(MediaController.this.ba, MediaController.this.ba.length);
                if (tL_documentAttributeAudio.waveform != null) {
                    tL_documentAttributeAudio.flags |= 4;
                }
                long g = MediaController.this.aR;
                tL_documentAttributeAudio.duration = (int) (MediaController.this.aR / 1000);
                AnonymousClass19.this.val$audioToSend.attributes.add(tL_documentAttributeAudio);
                if (g > 700) {
                    if (AnonymousClass19.this.val$send == 1) {
                        SendMessagesHelper.getInstance().sendMessage(AnonymousClass19.this.val$audioToSend, null, AnonymousClass19.this.val$recordingAudioFileToSend.getAbsolutePath(), MediaController.this.aS, MediaController.this.aT, null, null);
                    }
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.audioDidSent;
                    Object[] objArr = new Object[2];
                    objArr[0] = AnonymousClass19.this.val$send == 2 ? AnonymousClass19.this.val$audioToSend : null;
                    if (AnonymousClass19.this.val$send == 2) {
                        videoEditedInfo = AnonymousClass19.this.val$recordingAudioFileToSend.getAbsolutePath();
                    }
                    objArr[1] = videoEditedInfo;
                    instance.postNotificationName(i, objArr);
                    return;
                }
                AnonymousClass19.this.val$recordingAudioFileToSend.delete();
            }
        }

        AnonymousClass19(TL_document tL_document, File file, int i) {
            this.val$audioToSend = tL_document;
            this.val$recordingAudioFileToSend = file;
            this.val$send = i;
        }

        public void run() {
            MediaController.this.stopRecord();
            AndroidUtilities.runOnUIThread(new C04661());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.1 */
    class C04671 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.MediaController.1.1 */
        class C04581 implements Runnable {
            final /* synthetic */ ByteBuffer val$finalBuffer;
            final /* synthetic */ boolean val$flush;

            /* renamed from: com.hanista.mobogram.messenger.MediaController.1.1.1 */
            class C04571 implements Runnable {
                C04571() {
                }

                public void run() {
                    MediaController.this.bd.add(C04581.this.val$finalBuffer);
                }
            }

            C04581(ByteBuffer byteBuffer, boolean z) {
                this.val$finalBuffer = byteBuffer;
                this.val$flush = z;
            }

            public void run() {
                while (this.val$finalBuffer.hasRemaining()) {
                    int limit;
                    if (this.val$finalBuffer.remaining() > MediaController.this.be.remaining()) {
                        limit = this.val$finalBuffer.limit();
                        this.val$finalBuffer.limit(MediaController.this.be.remaining() + this.val$finalBuffer.position());
                    } else {
                        limit = -1;
                    }
                    MediaController.this.be.put(this.val$finalBuffer);
                    if (MediaController.this.be.position() == MediaController.this.be.limit() || this.val$flush) {
                        if (MediaController.this.writeFrame(MediaController.this.be, !this.val$flush ? MediaController.this.be.limit() : this.val$finalBuffer.position()) != 0) {
                            MediaController.this.be.rewind();
                            MediaController.this.aR = MediaController.this.aR + ((long) ((MediaController.this.be.limit() / 2) / 16));
                        }
                    }
                    if (limit != -1) {
                        this.val$finalBuffer.limit(limit);
                    }
                }
                MediaController.this.bi.postRunnable(new C04571());
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MediaController.1.2 */
        class C04592 implements Runnable {
            final /* synthetic */ double val$amplitude;

            C04592(double d) {
                this.val$amplitude = d;
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.aQ), Double.valueOf(this.val$amplitude));
            }
        }

        C04671() {
        }

        public void run() {
            if (MediaController.this.aN != null) {
                ByteBuffer allocateDirect;
                if (MediaController.this.bd.isEmpty()) {
                    allocateDirect = ByteBuffer.allocateDirect(MediaController.this.bf);
                    allocateDirect.order(ByteOrder.nativeOrder());
                } else {
                    allocateDirect = (ByteBuffer) MediaController.this.bd.get(0);
                    MediaController.this.bd.remove(0);
                }
                allocateDirect.rewind();
                int read = MediaController.this.aN.read(allocateDirect, allocateDirect.capacity());
                if (read > 0) {
                    double d;
                    allocateDirect.limit(read);
                    double d2 = 0.0d;
                    try {
                        long d3 = ((long) (read / 2)) + MediaController.this.bb;
                        int d4 = (int) ((((double) MediaController.this.bb) / ((double) d3)) * ((double) MediaController.this.ba.length));
                        int length = MediaController.this.ba.length - d4;
                        if (d4 != 0) {
                            float length2 = ((float) MediaController.this.ba.length) / ((float) d4);
                            float f = 0.0f;
                            for (int i = 0; i < d4; i++) {
                                MediaController.this.ba[i] = MediaController.this.ba[(int) f];
                                f += length2;
                            }
                        }
                        float f2 = (((float) read) / 2.0f) / ((float) length);
                        float f3 = 0.0f;
                        int i2 = d4;
                        for (d4 = 0; d4 < read / 2; d4++) {
                            short s = allocateDirect.getShort();
                            if (s > (short) 2500) {
                                d2 += (double) (s * s);
                            }
                            if (d4 == ((int) f3) && i2 < MediaController.this.ba.length) {
                                MediaController.this.ba[i2] = s;
                                f3 += f2;
                                i2++;
                            }
                        }
                        MediaController.this.bb = d3;
                        d = d2;
                    } catch (Throwable e) {
                        d = d2;
                        FileLog.m18e("tmessages", e);
                    }
                    allocateDirect.position(0);
                    d = Math.sqrt((d / ((double) read)) / 2.0d);
                    boolean z = read != allocateDirect.capacity();
                    if (read != 0) {
                        MediaController.this.bj.postRunnable(new C04581(allocateDirect, z));
                    }
                    MediaController.this.bi.postRunnable(MediaController.this.bk);
                    AndroidUtilities.runOnUIThread(new C04592(d));
                    return;
                }
                MediaController.this.bd.add(allocateDirect);
                MediaController.this.m115g(MediaController.this.bg);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.20 */
    class AnonymousClass20 implements Runnable {
        final /* synthetic */ int val$send;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.20.1 */
        class C04681 implements Runnable {
            C04681() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
            }
        }

        AnonymousClass20(int i) {
            this.val$send = i;
        }

        public void run() {
            if (MediaController.this.aN != null) {
                try {
                    MediaController.this.bg = this.val$send;
                    MediaController.this.aN.stop();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    if (MediaController.this.aP != null) {
                        MediaController.this.aP.delete();
                    }
                }
                if (this.val$send == 0) {
                    MediaController.this.m115g(0);
                }
                try {
                    ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
                AndroidUtilities.runOnUIThread(new C04681());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.21 */
    static class AnonymousClass21 implements OnCancelListener {
        final /* synthetic */ boolean[] val$cancelled;

        AnonymousClass21(boolean[] zArr) {
            this.val$cancelled = zArr;
        }

        public void onCancel(DialogInterface dialogInterface) {
            this.val$cancelled[0] = true;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.22 */
    static class AnonymousClass22 implements Runnable {
        final /* synthetic */ boolean[] val$cancelled;
        final /* synthetic */ ProgressDialog val$finalProgress;
        final /* synthetic */ String val$mime;
        final /* synthetic */ String val$name;
        final /* synthetic */ File val$sourceFile;
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.22.1 */
        class C04691 implements Runnable {
            final /* synthetic */ int val$progress;

            C04691(int i) {
                this.val$progress = i;
            }

            public void run() {
                try {
                    AnonymousClass22.this.val$finalProgress.setProgress(this.val$progress);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MediaController.22.2 */
        class C04702 implements Runnable {
            C04702() {
            }

            public void run() {
                try {
                    AnonymousClass22.this.val$finalProgress.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        AnonymousClass22(int i, String str, File file, boolean[] zArr, ProgressDialog progressDialog, String str2) {
            this.val$type = i;
            this.val$name = str;
            this.val$sourceFile = file;
            this.val$cancelled = zArr;
            this.val$finalProgress = progressDialog;
            this.val$mime = str2;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r15 = this;
            r0 = 0;
            r1 = r15.val$type;	 Catch:{ Exception -> 0x0128 }
            if (r1 != 0) goto L_0x008f;
        L_0x0005:
            r0 = com.hanista.mobogram.messenger.AndroidUtilities.generatePicturePath();	 Catch:{ Exception -> 0x0128 }
            r9 = r0;
        L_0x000a:
            r0 = r9.exists();	 Catch:{ Exception -> 0x0128 }
            if (r0 != 0) goto L_0x0013;
        L_0x0010:
            r9.createNewFile();	 Catch:{ Exception -> 0x0128 }
        L_0x0013:
            r1 = 0;
            r2 = 0;
            r8 = 1;
            r4 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0128 }
            r6 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r6 = r4 - r6;
            r0 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0102, all -> 0x011c }
            r3 = r15.val$sourceFile;	 Catch:{ Exception -> 0x0102, all -> 0x011c }
            r0.<init>(r3);	 Catch:{ Exception -> 0x0102, all -> 0x011c }
            r1 = r0.getChannel();	 Catch:{ Exception -> 0x0102, all -> 0x011c }
            r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x014d, all -> 0x011c }
            r0.<init>(r9);	 Catch:{ Exception -> 0x014d, all -> 0x011c }
            r0 = r0.getChannel();	 Catch:{ Exception -> 0x014d, all -> 0x011c }
            r10 = r1.size();	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r2 = 0;
        L_0x0038:
            r4 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
            if (r4 >= 0) goto L_0x0043;
        L_0x003c:
            r4 = r15.val$cancelled;	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r5 = 0;
            r4 = r4[r5];	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            if (r4 == 0) goto L_0x00cb;
        L_0x0043:
            if (r1 == 0) goto L_0x0048;
        L_0x0045:
            r1.close();	 Catch:{ Exception -> 0x013a }
        L_0x0048:
            if (r0 == 0) goto L_0x004d;
        L_0x004a:
            r0.close();	 Catch:{ Exception -> 0x00fe }
        L_0x004d:
            r0 = r8;
        L_0x004e:
            r1 = r15.val$cancelled;	 Catch:{ Exception -> 0x0128 }
            r2 = 0;
            r1 = r1[r2];	 Catch:{ Exception -> 0x0128 }
            if (r1 == 0) goto L_0x0059;
        L_0x0055:
            r9.delete();	 Catch:{ Exception -> 0x0128 }
            r0 = 0;
        L_0x0059:
            if (r0 == 0) goto L_0x0082;
        L_0x005b:
            r0 = r15.val$type;	 Catch:{ Exception -> 0x0128 }
            r1 = 2;
            if (r0 != r1) goto L_0x0131;
        L_0x0060:
            r0 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0128 }
            r1 = "download";
            r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0128 }
            r0 = (android.app.DownloadManager) r0;	 Catch:{ Exception -> 0x0128 }
            r1 = r9.getName();	 Catch:{ Exception -> 0x0128 }
            r2 = r9.getName();	 Catch:{ Exception -> 0x0128 }
            r3 = 0;
            r4 = r15.val$mime;	 Catch:{ Exception -> 0x0128 }
            r5 = r9.getAbsolutePath();	 Catch:{ Exception -> 0x0128 }
            r6 = r9.length();	 Catch:{ Exception -> 0x0128 }
            r8 = 1;
            r0.addCompletedDownload(r1, r2, r3, r4, r5, r6, r8);	 Catch:{ Exception -> 0x0128 }
        L_0x0082:
            r0 = r15.val$finalProgress;
            if (r0 == 0) goto L_0x008e;
        L_0x0086:
            r0 = new com.hanista.mobogram.messenger.MediaController$22$2;
            r0.<init>();
            com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r0);
        L_0x008e:
            return;
        L_0x008f:
            r1 = r15.val$type;	 Catch:{ Exception -> 0x0128 }
            r2 = 1;
            if (r1 != r2) goto L_0x009b;
        L_0x0094:
            r0 = com.hanista.mobogram.messenger.AndroidUtilities.generateVideoPath();	 Catch:{ Exception -> 0x0128 }
            r9 = r0;
            goto L_0x000a;
        L_0x009b:
            r1 = r15.val$type;	 Catch:{ Exception -> 0x0128 }
            r2 = 2;
            if (r1 != r2) goto L_0x00b3;
        L_0x00a0:
            r0 = android.os.Environment.DIRECTORY_DOWNLOADS;	 Catch:{ Exception -> 0x0128 }
            r1 = android.os.Environment.getExternalStoragePublicDirectory(r0);	 Catch:{ Exception -> 0x0128 }
            r1.mkdir();	 Catch:{ Exception -> 0x0128 }
            r0 = new java.io.File;	 Catch:{ Exception -> 0x0128 }
            r2 = r15.val$name;	 Catch:{ Exception -> 0x0128 }
            r0.<init>(r1, r2);	 Catch:{ Exception -> 0x0128 }
            r9 = r0;
            goto L_0x000a;
        L_0x00b3:
            r1 = r15.val$type;	 Catch:{ Exception -> 0x0128 }
            r2 = 3;
            if (r1 != r2) goto L_0x015a;
        L_0x00b8:
            r0 = android.os.Environment.DIRECTORY_MUSIC;	 Catch:{ Exception -> 0x0128 }
            r1 = android.os.Environment.getExternalStoragePublicDirectory(r0);	 Catch:{ Exception -> 0x0128 }
            r1.mkdirs();	 Catch:{ Exception -> 0x0128 }
            r0 = new java.io.File;	 Catch:{ Exception -> 0x0128 }
            r2 = r15.val$name;	 Catch:{ Exception -> 0x0128 }
            r0.<init>(r1, r2);	 Catch:{ Exception -> 0x0128 }
            r9 = r0;
            goto L_0x000a;
        L_0x00cb:
            r4 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r12 = r10 - r2;
            r4 = java.lang.Math.min(r4, r12);	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r0.transferFrom(r1, r2, r4);	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r4 = r15.val$finalProgress;	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            if (r4 == 0) goto L_0x0158;
        L_0x00da:
            r4 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r12 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r4 = r4 - r12;
            r4 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
            if (r4 > 0) goto L_0x0158;
        L_0x00e5:
            r4 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r6 = (float) r2;	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r7 = (float) r10;	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r6 = r6 / r7;
            r7 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
            r6 = r6 * r7;
            r6 = (int) r6;	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r7 = new com.hanista.mobogram.messenger.MediaController$22$1;	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            r7.<init>(r6);	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
            com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x0152, all -> 0x0143 }
        L_0x00f8:
            r6 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r2 = r2 + r6;
            r6 = r4;
            goto L_0x0038;
        L_0x00fe:
            r0 = move-exception;
            r0 = r8;
            goto L_0x004e;
        L_0x0102:
            r0 = move-exception;
            r14 = r2;
            r2 = r1;
            r1 = r14;
        L_0x0106:
            r3 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ all -> 0x0148 }
            r0 = 0;
            if (r2 == 0) goto L_0x0112;
        L_0x010f:
            r2.close();	 Catch:{ Exception -> 0x013d }
        L_0x0112:
            if (r1 == 0) goto L_0x004e;
        L_0x0114:
            r1.close();	 Catch:{ Exception -> 0x0119 }
            goto L_0x004e;
        L_0x0119:
            r1 = move-exception;
            goto L_0x004e;
        L_0x011c:
            r0 = move-exception;
        L_0x011d:
            if (r1 == 0) goto L_0x0122;
        L_0x011f:
            r1.close();	 Catch:{ Exception -> 0x013f }
        L_0x0122:
            if (r2 == 0) goto L_0x0127;
        L_0x0124:
            r2.close();	 Catch:{ Exception -> 0x0141 }
        L_0x0127:
            throw r0;	 Catch:{ Exception -> 0x0128 }
        L_0x0128:
            r0 = move-exception;
            r1 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
            goto L_0x0082;
        L_0x0131:
            r0 = android.net.Uri.fromFile(r9);	 Catch:{ Exception -> 0x0128 }
            com.hanista.mobogram.messenger.AndroidUtilities.addMediaToGallery(r0);	 Catch:{ Exception -> 0x0128 }
            goto L_0x0082;
        L_0x013a:
            r1 = move-exception;
            goto L_0x0048;
        L_0x013d:
            r2 = move-exception;
            goto L_0x0112;
        L_0x013f:
            r1 = move-exception;
            goto L_0x0122;
        L_0x0141:
            r1 = move-exception;
            goto L_0x0127;
        L_0x0143:
            r2 = move-exception;
            r14 = r2;
            r2 = r0;
            r0 = r14;
            goto L_0x011d;
        L_0x0148:
            r0 = move-exception;
            r14 = r1;
            r1 = r2;
            r2 = r14;
            goto L_0x011d;
        L_0x014d:
            r0 = move-exception;
            r14 = r2;
            r2 = r1;
            r1 = r14;
            goto L_0x0106;
        L_0x0152:
            r2 = move-exception;
            r14 = r2;
            r2 = r1;
            r1 = r0;
            r0 = r14;
            goto L_0x0106;
        L_0x0158:
            r4 = r6;
            goto L_0x00f8;
        L_0x015a:
            r9 = r0;
            goto L_0x000a;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MediaController.22.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.23 */
    static class AnonymousClass23 implements Runnable {
        final /* synthetic */ int val$guid;

        AnonymousClass23(int i) {
            this.val$guid = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r26 = this;
            r16 = new java.util.ArrayList;
            r16.<init>();
            r17 = new java.util.ArrayList;
            r17.<init>();
            r18 = new java.util.HashMap;
            r18.<init>();
            r10 = 0;
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = android.os.Environment.DIRECTORY_DCIM;
            r3 = android.os.Environment.getExternalStoragePublicDirectory(r3);
            r3 = r3.getAbsolutePath();
            r2 = r2.append(r3);
            r3 = "/Camera/";
            r2 = r2.append(r3);
            r19 = r2.toString();
            r9 = 0;
            r14 = 0;
            r8 = 0;
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            r3 = 23;
            if (r2 < r3) goto L_0x0048;
        L_0x0037:
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            r3 = 23;
            if (r2 < r3) goto L_0x02da;
        L_0x003d:
            r2 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            r3 = "android.permission.READ_EXTERNAL_STORAGE";
            r2 = r2.checkSelfPermission(r3);	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            if (r2 != 0) goto L_0x02da;
        L_0x0048:
            r2 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            r3 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            r4 = com.hanista.mobogram.messenger.MediaController.f29f;	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            r5 = 0;
            r6 = 0;
            r7 = "datetaken DESC";
            r11 = android.provider.MediaStore.Images.Media.query(r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0222, all -> 0x0242 }
            if (r11 == 0) goto L_0x02d5;
        L_0x005f:
            r2 = "_id";
            r15 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02b3, all -> 0x02ae }
            r2 = "bucket_id";
            r20 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02b3, all -> 0x02ae }
            r2 = "bucket_display_name";
            r21 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02b3, all -> 0x02ae }
            r2 = "_data";
            r22 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02b3, all -> 0x02ae }
            r2 = "datetaken";
            r23 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02b3, all -> 0x02ae }
            r2 = "orientation";
            r24 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02b3, all -> 0x02ae }
            r12 = r9;
            r13 = r10;
        L_0x008b:
            r2 = r11.moveToNext();	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            if (r2 == 0) goto L_0x0125;
        L_0x0091:
            r5 = r11.getInt(r15);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r0 = r20;
            r4 = r11.getInt(r0);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r0 = r21;
            r25 = r11.getString(r0);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r0 = r22;
            r8 = r11.getString(r0);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r0 = r23;
            r6 = r11.getLong(r0);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r0 = r24;
            r9 = r11.getInt(r0);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            if (r8 == 0) goto L_0x008b;
        L_0x00b5:
            r2 = r8.length();	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            if (r2 == 0) goto L_0x008b;
        L_0x00bb:
            r3 = new com.hanista.mobogram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r10 = 0;
            r3.<init>(r4, r5, r6, r8, r9, r10);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            if (r13 != 0) goto L_0x02d2;
        L_0x00c3:
            r5 = new com.hanista.mobogram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r2 = 0;
            r6 = "AllPhotos";
            r7 = 2131165276; // 0x7f07005c float:1.7944765E38 double:1.0529355485E-314;
            r6 = com.hanista.mobogram.messenger.LocaleController.getString(r6, r7);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r7 = 0;
            r5.<init>(r2, r6, r3, r7);	 Catch:{ Throwable -> 0x02bd, all -> 0x02ae }
            r2 = 0;
            r0 = r16;
            r0.add(r2, r5);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
        L_0x00da:
            if (r5 == 0) goto L_0x00df;
        L_0x00dc:
            r5.addPhoto(r3);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
        L_0x00df:
            r2 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            r0 = r18;
            r2 = r0.get(r2);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            r2 = (com.hanista.mobogram.messenger.MediaController.AlbumEntry) r2;	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            if (r2 != 0) goto L_0x0123;
        L_0x00ed:
            r2 = new com.hanista.mobogram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            r6 = 0;
            r0 = r25;
            r2.<init>(r4, r0, r3, r6);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            r6 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            r0 = r18;
            r0.put(r6, r2);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            if (r12 != 0) goto L_0x011e;
        L_0x0100:
            if (r19 == 0) goto L_0x011e;
        L_0x0102:
            if (r8 == 0) goto L_0x011e;
        L_0x0104:
            r0 = r19;
            r6 = r8.startsWith(r0);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            if (r6 == 0) goto L_0x011e;
        L_0x010c:
            r6 = 0;
            r0 = r16;
            r0.add(r6, r2);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            r12 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
            r4 = r12;
        L_0x0117:
            r2.addPhoto(r3);	 Catch:{ Throwable -> 0x02b9, all -> 0x02ae }
            r12 = r4;
            r13 = r5;
            goto L_0x008b;
        L_0x011e:
            r0 = r16;
            r0.add(r2);	 Catch:{ Throwable -> 0x02c3, all -> 0x02ae }
        L_0x0123:
            r4 = r12;
            goto L_0x0117;
        L_0x0125:
            r3 = r11;
            r4 = r12;
            r5 = r13;
        L_0x0128:
            if (r3 == 0) goto L_0x02cd;
        L_0x012a:
            r3.close();	 Catch:{ Exception -> 0x0216 }
            r8 = r3;
            r13 = r4;
            r15 = r5;
        L_0x0130:
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r3 = 23;
            if (r2 < r3) goto L_0x0147;
        L_0x0136:
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r3 = 23;
            if (r2 < r3) goto L_0x02cb;
        L_0x013c:
            r2 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r3 = "android.permission.READ_EXTERNAL_STORAGE";
            r2 = r2.checkSelfPermission(r3);	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            if (r2 != 0) goto L_0x02cb;
        L_0x0147:
            r18.clear();	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r9 = 0;
            r2 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r3 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r4 = com.hanista.mobogram.messenger.MediaController.f30g;	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            r5 = 0;
            r6 = 0;
            r7 = "datetaken DESC";
            r11 = android.provider.MediaStore.Images.Media.query(r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0277, all -> 0x028e }
            if (r11 == 0) goto L_0x02c8;
        L_0x0162:
            r2 = "_id";
            r20 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02a3, all -> 0x029e }
            r2 = "bucket_id";
            r21 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02a3, all -> 0x029e }
            r2 = "bucket_display_name";
            r22 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02a3, all -> 0x029e }
            r2 = "_data";
            r23 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02a3, all -> 0x029e }
            r2 = "datetaken";
            r24 = r11.getColumnIndex(r2);	 Catch:{ Throwable -> 0x02a3, all -> 0x029e }
            r2 = r9;
            r12 = r14;
        L_0x0187:
            r3 = r11.moveToNext();	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            if (r3 == 0) goto L_0x0259;
        L_0x018d:
            r0 = r20;
            r5 = r11.getInt(r0);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r0 = r21;
            r4 = r11.getInt(r0);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r0 = r22;
            r14 = r11.getString(r0);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r0 = r23;
            r8 = r11.getString(r0);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r0 = r24;
            r6 = r11.getLong(r0);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            if (r8 == 0) goto L_0x0187;
        L_0x01ad:
            r3 = r8.length();	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            if (r3 == 0) goto L_0x0187;
        L_0x01b3:
            r3 = new com.hanista.mobogram.messenger.MediaController$PhotoEntry;	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r9 = 0;
            r10 = 1;
            r3.<init>(r4, r5, r6, r8, r9, r10);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            if (r2 != 0) goto L_0x01d3;
        L_0x01bc:
            r2 = new com.hanista.mobogram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r5 = 0;
            r6 = "AllVideo";
            r7 = 2131165277; // 0x7f07005d float:1.7944767E38 double:1.052935549E-314;
            r6 = com.hanista.mobogram.messenger.LocaleController.getString(r6, r7);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r7 = 1;
            r2.<init>(r5, r6, r3, r7);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r5 = 0;
            r0 = r17;
            r0.add(r5, r2);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
        L_0x01d3:
            r5 = r2;
            if (r5 == 0) goto L_0x01d9;
        L_0x01d6:
            r5.addPhoto(r3);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
        L_0x01d9:
            r2 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r0 = r18;
            r2 = r0.get(r2);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r2 = (com.hanista.mobogram.messenger.MediaController.AlbumEntry) r2;	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            if (r2 != 0) goto L_0x0257;
        L_0x01e7:
            r2 = new com.hanista.mobogram.messenger.MediaController$AlbumEntry;	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r6 = 1;
            r2.<init>(r4, r14, r3, r6);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r6 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r0 = r18;
            r0.put(r6, r2);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            if (r12 != 0) goto L_0x0252;
        L_0x01f8:
            if (r19 == 0) goto L_0x0252;
        L_0x01fa:
            if (r8 == 0) goto L_0x0252;
        L_0x01fc:
            r0 = r19;
            r6 = r8.startsWith(r0);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            if (r6 == 0) goto L_0x0252;
        L_0x0204:
            r6 = 0;
            r0 = r17;
            r0.add(r6, r2);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r12 = java.lang.Integer.valueOf(r4);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
            r6 = r12;
        L_0x020f:
            r2.addPhoto(r3);	 Catch:{ Throwable -> 0x02a7, all -> 0x029e }
            r2 = r5;
            r12 = r6;
            goto L_0x0187;
        L_0x0216:
            r2 = move-exception;
            r6 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r6, r2);
            r8 = r3;
            r13 = r4;
            r15 = r5;
            goto L_0x0130;
        L_0x0222:
            r2 = move-exception;
            r3 = r8;
            r4 = r9;
            r5 = r10;
        L_0x0226:
            r6 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r6, r2);	 Catch:{ all -> 0x02b0 }
            if (r3 == 0) goto L_0x02cd;
        L_0x022e:
            r3.close();	 Catch:{ Exception -> 0x0236 }
            r8 = r3;
            r13 = r4;
            r15 = r5;
            goto L_0x0130;
        L_0x0236:
            r2 = move-exception;
            r6 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r6, r2);
            r8 = r3;
            r13 = r4;
            r15 = r5;
            goto L_0x0130;
        L_0x0242:
            r2 = move-exception;
            r11 = r8;
        L_0x0244:
            if (r11 == 0) goto L_0x0249;
        L_0x0246:
            r11.close();	 Catch:{ Exception -> 0x024a }
        L_0x0249:
            throw r2;
        L_0x024a:
            r3 = move-exception;
            r4 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r4, r3);
            goto L_0x0249;
        L_0x0252:
            r0 = r17;
            r0.add(r2);	 Catch:{ Throwable -> 0x02aa, all -> 0x029e }
        L_0x0257:
            r6 = r12;
            goto L_0x020f;
        L_0x0259:
            r8 = r11;
            r6 = r12;
        L_0x025b:
            if (r8 == 0) goto L_0x0260;
        L_0x025d:
            r8.close();	 Catch:{ Exception -> 0x026f }
        L_0x0260:
            r0 = r26;
            r2 = r0.val$guid;
            r8 = 0;
            r3 = r16;
            r4 = r13;
            r5 = r17;
            r7 = r15;
            com.hanista.mobogram.messenger.MediaController.m94b(r2, r3, r4, r5, r6, r7, r8);
            return;
        L_0x026f:
            r2 = move-exception;
            r3 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r3, r2);
            goto L_0x0260;
        L_0x0277:
            r2 = move-exception;
            r3 = r8;
            r6 = r14;
        L_0x027a:
            r4 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r4, r2);	 Catch:{ all -> 0x02a0 }
            if (r3 == 0) goto L_0x0260;
        L_0x0282:
            r3.close();	 Catch:{ Exception -> 0x0286 }
            goto L_0x0260;
        L_0x0286:
            r2 = move-exception;
            r3 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r3, r2);
            goto L_0x0260;
        L_0x028e:
            r2 = move-exception;
            r11 = r8;
        L_0x0290:
            if (r11 == 0) goto L_0x0295;
        L_0x0292:
            r11.close();	 Catch:{ Exception -> 0x0296 }
        L_0x0295:
            throw r2;
        L_0x0296:
            r3 = move-exception;
            r4 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r4, r3);
            goto L_0x0295;
        L_0x029e:
            r2 = move-exception;
            goto L_0x0290;
        L_0x02a0:
            r2 = move-exception;
            r11 = r3;
            goto L_0x0290;
        L_0x02a3:
            r2 = move-exception;
            r3 = r11;
            r6 = r14;
            goto L_0x027a;
        L_0x02a7:
            r2 = move-exception;
            r3 = r11;
            goto L_0x027a;
        L_0x02aa:
            r2 = move-exception;
            r3 = r11;
            r6 = r12;
            goto L_0x027a;
        L_0x02ae:
            r2 = move-exception;
            goto L_0x0244;
        L_0x02b0:
            r2 = move-exception;
            r11 = r3;
            goto L_0x0244;
        L_0x02b3:
            r2 = move-exception;
            r3 = r11;
            r4 = r9;
            r5 = r10;
            goto L_0x0226;
        L_0x02b9:
            r2 = move-exception;
            r3 = r11;
            goto L_0x0226;
        L_0x02bd:
            r2 = move-exception;
            r3 = r11;
            r4 = r12;
            r5 = r13;
            goto L_0x0226;
        L_0x02c3:
            r2 = move-exception;
            r3 = r11;
            r4 = r12;
            goto L_0x0226;
        L_0x02c8:
            r8 = r11;
            r6 = r14;
            goto L_0x025b;
        L_0x02cb:
            r6 = r14;
            goto L_0x025b;
        L_0x02cd:
            r8 = r3;
            r13 = r4;
            r15 = r5;
            goto L_0x0130;
        L_0x02d2:
            r5 = r13;
            goto L_0x00da;
        L_0x02d5:
            r3 = r11;
            r4 = r9;
            r5 = r10;
            goto L_0x0128;
        L_0x02da:
            r3 = r8;
            r4 = r9;
            r5 = r10;
            goto L_0x0128;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MediaController.23.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.24 */
    static class AnonymousClass24 implements Runnable {
        final /* synthetic */ ArrayList val$albumsSorted;
        final /* synthetic */ AlbumEntry val$allPhotosAlbumFinal;
        final /* synthetic */ Integer val$cameraAlbumIdFinal;
        final /* synthetic */ Integer val$cameraAlbumVideoIdFinal;
        final /* synthetic */ int val$guid;
        final /* synthetic */ ArrayList val$videoAlbumsSorted;

        AnonymousClass24(int i, ArrayList arrayList, Integer num, ArrayList arrayList2, Integer num2, AlbumEntry albumEntry) {
            this.val$guid = i;
            this.val$albumsSorted = arrayList;
            this.val$cameraAlbumIdFinal = num;
            this.val$videoAlbumsSorted = arrayList2;
            this.val$cameraAlbumVideoIdFinal = num2;
            this.val$allPhotosAlbumFinal = albumEntry;
        }

        public void run() {
            if (PhotoViewer.getInstance().isVisible()) {
                MediaController.m94b(this.val$guid, this.val$albumsSorted, this.val$cameraAlbumIdFinal, this.val$videoAlbumsSorted, this.val$cameraAlbumVideoIdFinal, this.val$allPhotosAlbumFinal, PointerIconCompat.TYPE_DEFAULT);
                return;
            }
            MediaController.al = null;
            MediaController.f28e = this.val$allPhotosAlbumFinal;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.albumsDidLoaded, Integer.valueOf(this.val$guid), this.val$albumsSorted, this.val$cameraAlbumIdFinal, this.val$videoAlbumsSorted, this.val$cameraAlbumVideoIdFinal);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.25 */
    class AnonymousClass25 implements Runnable {
        final /* synthetic */ boolean val$error;
        final /* synthetic */ File val$file;
        final /* synthetic */ boolean val$firstWrite;
        final /* synthetic */ boolean val$last;
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass25(boolean z, MessageObject messageObject, File file, boolean z2, boolean z3) {
            this.val$error = z;
            this.val$messageObject = messageObject;
            this.val$file = file;
            this.val$firstWrite = z2;
            this.val$last = z3;
        }

        public void run() {
            if (this.val$error) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingFailed, this.val$messageObject, this.val$file.toString());
            } else {
                if (this.val$firstWrite) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingStarted, this.val$messageObject, this.val$file.toString());
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.FileNewChunkAvailable;
                Object[] objArr = new Object[3];
                objArr[0] = this.val$messageObject;
                objArr[1] = this.val$file.toString();
                objArr[2] = Long.valueOf(this.val$last ? this.val$file.length() : 0);
                instance.postNotificationName(i, objArr);
            }
            if (this.val$error || this.val$last) {
                synchronized (MediaController.this.f60h) {
                    MediaController.this.f46P = false;
                }
                MediaController.this.f44N.remove(this.val$messageObject);
                MediaController.this.m47Q();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.2 */
    class C04732 implements Runnable {
        C04732() {
        }

        public void run() {
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileUploadProgressChanged);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.httpFileDidLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.httpFileDidFailedLoad);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.3 */
    class C04743 extends BroadcastReceiver {
        C04743() {
        }

        public void onReceive(Context context, Intent intent) {
            MediaController.this.m170d();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.4 */
    class C04754 extends PhoneStateListener {
        C04754() {
        }

        public void onCallStateChanged(int i, String str) {
            if (i == 1) {
                if (MediaController.this.m172d(MediaController.this.m182j()) && !MediaController.this.m191s()) {
                    MediaController.this.m166b(MediaController.this.m182j());
                } else if (!(MediaController.this.bh == null && MediaController.this.aO == null)) {
                    MediaController.this.m171d(2);
                }
                MediaController.this.f41K = true;
            } else if (i == 0) {
                MediaController.this.f41K = false;
            } else if (i == 2) {
                MediaController.this.f41K = true;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.5 */
    class C04775 extends TimerTask {
        final /* synthetic */ MessageObject val$currentPlayingMessageObject;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.5.1 */
        class C04761 implements Runnable {
            C04761() {
            }

            public void run() {
                if (C04775.this.val$currentPlayingMessageObject == null) {
                    return;
                }
                if ((MediaController.this.au != null || MediaController.this.av != null) && !MediaController.this.at) {
                    try {
                        if (MediaController.this.aC != 0) {
                            MediaController.this.aC = MediaController.this.aC - 1;
                            return;
                        }
                        int currentPosition;
                        float y;
                        if (MediaController.this.au != null) {
                            currentPosition = MediaController.this.au.getCurrentPosition();
                            y = ((float) MediaController.this.aw) / ((float) MediaController.this.au.getDuration());
                            if (currentPosition <= MediaController.this.aw) {
                                return;
                            }
                        }
                        currentPosition = (int) (((float) MediaController.this.aB) / 48.0f);
                        y = ((float) MediaController.this.aB) / ((float) MediaController.this.aA);
                        if (currentPosition == MediaController.this.aw) {
                            return;
                        }
                        MediaController.this.aw = currentPosition;
                        C04775.this.val$currentPlayingMessageObject.audioProgress = y;
                        C04775.this.val$currentPlayingMessageObject.audioProgressSec = MediaController.this.aw / PointerIconCompat.TYPE_DEFAULT;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, Integer.valueOf(C04775.this.val$currentPlayingMessageObject.getId()), Float.valueOf(y));
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }

        C04775(MessageObject messageObject) {
            this.val$currentPlayingMessageObject = messageObject;
        }

        public void run() {
            synchronized (MediaController.this.bc) {
                AndroidUtilities.runOnUIThread(new C04761());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.6 */
    class C04786 implements Runnable {
        final /* synthetic */ ArrayList val$screenshotDates;

        C04786(ArrayList arrayList) {
            this.val$screenshotDates = arrayList;
        }

        public void run() {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
            MediaController.this.m85a(this.val$screenshotDates);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.7 */
    class C04797 implements Runnable {
        C04797() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r8 = this;
            r2 = 0;
            r3 = 1;
            r0 = com.hanista.mobogram.messenger.MediaController.this;
            r0 = r0.az;
            if (r0 == 0) goto L_0x0010;
        L_0x000a:
            r0 = com.hanista.mobogram.messenger.MediaController.this;
            r0.m41N();
        L_0x000f:
            return;
        L_0x0010:
            r1 = r2;
        L_0x0011:
            r0 = 0;
            r4 = com.hanista.mobogram.messenger.MediaController.this;
            r4 = r4.aY;
            monitor-enter(r4);
            r5 = com.hanista.mobogram.messenger.MediaController.this;	 Catch:{ all -> 0x009c }
            r5 = r5.aX;	 Catch:{ all -> 0x009c }
            r5 = r5.isEmpty();	 Catch:{ all -> 0x009c }
            if (r5 != 0) goto L_0x003c;
        L_0x0025:
            r0 = com.hanista.mobogram.messenger.MediaController.this;	 Catch:{ all -> 0x009c }
            r0 = r0.aX;	 Catch:{ all -> 0x009c }
            r5 = 0;
            r0 = r0.get(r5);	 Catch:{ all -> 0x009c }
            r0 = (com.hanista.mobogram.messenger.MediaController.AudioBuffer) r0;	 Catch:{ all -> 0x009c }
            r5 = com.hanista.mobogram.messenger.MediaController.this;	 Catch:{ all -> 0x009c }
            r5 = r5.aX;	 Catch:{ all -> 0x009c }
            r6 = 0;
            r5.remove(r6);	 Catch:{ all -> 0x009c }
        L_0x003c:
            r5 = com.hanista.mobogram.messenger.MediaController.this;	 Catch:{ all -> 0x009c }
            r5 = r5.aW;	 Catch:{ all -> 0x009c }
            r5 = r5.isEmpty();	 Catch:{ all -> 0x009c }
            if (r5 != 0) goto L_0x0049;
        L_0x0048:
            r1 = r3;
        L_0x0049:
            monitor-exit(r4);	 Catch:{ all -> 0x009c }
            if (r0 == 0) goto L_0x00b3;
        L_0x004c:
            r4 = com.hanista.mobogram.messenger.MediaController.this;
            r5 = r0.buffer;
            r6 = com.hanista.mobogram.messenger.MediaController.this;
            r6 = r6.ay;
            r7 = com.hanista.mobogram.messenger.MediaController.f27a;
            r4.readOpusFile(r5, r6, r7);
            r4 = com.hanista.mobogram.messenger.MediaController.f27a;
            r4 = r4[r2];
            r0.size = r4;
            r4 = com.hanista.mobogram.messenger.MediaController.f27a;
            r4 = r4[r3];
            r4 = (long) r4;
            r0.pcmOffset = r4;
            r4 = com.hanista.mobogram.messenger.MediaController.f27a;
            r5 = 2;
            r4 = r4[r5];
            r0.finished = r4;
            r4 = r0.finished;
            if (r4 != r3) goto L_0x0078;
        L_0x0073:
            r4 = com.hanista.mobogram.messenger.MediaController.this;
            r4.az = r3;
        L_0x0078:
            r4 = r0.size;
            if (r4 == 0) goto L_0x00a2;
        L_0x007c:
            r1 = r0.buffer;
            r1.rewind();
            r1 = r0.buffer;
            r4 = r0.bufferBytes;
            r1.get(r4);
            r1 = com.hanista.mobogram.messenger.MediaController.this;
            r1 = r1.aY;
            monitor-enter(r1);
            r4 = com.hanista.mobogram.messenger.MediaController.this;	 Catch:{ all -> 0x009f }
            r4 = r4.aW;	 Catch:{ all -> 0x009f }
            r4.add(r0);	 Catch:{ all -> 0x009f }
            monitor-exit(r1);	 Catch:{ all -> 0x009f }
            r1 = r3;
            goto L_0x0011;
        L_0x009c:
            r0 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x009c }
            throw r0;
        L_0x009f:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x009f }
            throw r0;
        L_0x00a2:
            r2 = com.hanista.mobogram.messenger.MediaController.this;
            r2 = r2.aY;
            monitor-enter(r2);
            r3 = com.hanista.mobogram.messenger.MediaController.this;	 Catch:{ all -> 0x00bc }
            r3 = r3.aX;	 Catch:{ all -> 0x00bc }
            r3.add(r0);	 Catch:{ all -> 0x00bc }
            monitor-exit(r2);	 Catch:{ all -> 0x00bc }
        L_0x00b3:
            if (r1 == 0) goto L_0x000f;
        L_0x00b5:
            r0 = com.hanista.mobogram.messenger.MediaController.this;
            r0.m41N();
            goto L_0x000f;
        L_0x00bc:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x00bc }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MediaController.7.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.8 */
    class C04818 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.MediaController.8.1 */
        class C04801 implements Runnable {
            final /* synthetic */ int val$finalBuffersWrited;
            final /* synthetic */ int val$marker;
            final /* synthetic */ long val$pcm;

            C04801(long j, int i, int i2) {
                this.val$pcm = j;
                this.val$marker = i;
                this.val$finalBuffersWrited = i2;
            }

            public void run() {
                MediaController.this.aB = this.val$pcm;
                if (this.val$marker != -1) {
                    if (MediaController.this.av != null) {
                        MediaController.this.av.setNotificationMarkerPosition(1);
                    }
                    if (this.val$finalBuffersWrited == 1) {
                        MediaController.this.m156a(true, true, true);
                    }
                }
            }
        }

        C04818() {
        }

        public void run() {
            synchronized (MediaController.this.aZ) {
                if (MediaController.this.av == null || MediaController.this.av.getPlayState() != 3) {
                    return;
                }
                AudioBuffer audioBuffer;
                synchronized (MediaController.this.aY) {
                    if (MediaController.this.aW.isEmpty()) {
                        audioBuffer = null;
                    } else {
                        AudioBuffer audioBuffer2 = (AudioBuffer) MediaController.this.aW.get(0);
                        MediaController.this.aW.remove(0);
                        audioBuffer = audioBuffer2;
                    }
                }
                if (audioBuffer != null) {
                    int write;
                    try {
                        write = MediaController.this.av.write(audioBuffer.bufferBytes, 0, audioBuffer.size);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        write = 0;
                    }
                    MediaController.this.aF = MediaController.this.aF + 1;
                    if (write > 0) {
                        AndroidUtilities.runOnUIThread(new C04801(audioBuffer.pcmOffset, audioBuffer.finished == 1 ? write : -1, MediaController.this.aF));
                    }
                    if (audioBuffer.finished != 1) {
                        MediaController.this.m41N();
                    }
                }
                if (audioBuffer == null || !(audioBuffer == null || audioBuffer.finished == 1)) {
                    MediaController.this.m39M();
                }
                if (audioBuffer != null) {
                    synchronized (MediaController.this.aY) {
                        MediaController.this.aX.add(audioBuffer);
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MediaController.9 */
    class C04829 implements Runnable {
        final /* synthetic */ MessageObject val$currentMessageObject;

        C04829(MessageObject messageObject) {
            this.val$currentMessageObject = messageObject;
        }

        public void run() {
            MediaController.this.m166b(this.val$currentMessageObject);
        }
    }

    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public boolean isVideo;
        public ArrayList<PhotoEntry> photos;
        public HashMap<Integer, PhotoEntry> photosByIds;

        public AlbumEntry(int i, String str, PhotoEntry photoEntry, boolean z) {
            this.photos = new ArrayList();
            this.photosByIds = new HashMap();
            this.bucketId = i;
            this.bucketName = str;
            this.coverPhoto = photoEntry;
            this.isVideo = z;
        }

        public void addPhoto(PhotoEntry photoEntry) {
            this.photos.add(photoEntry);
            this.photosByIds.put(Integer.valueOf(photoEntry.imageId), photoEntry);
        }
    }

    private class AudioBuffer {
        ByteBuffer buffer;
        byte[] bufferBytes;
        int finished;
        long pcmOffset;
        int size;

        public AudioBuffer(int i) {
            this.buffer = ByteBuffer.allocateDirect(i);
            this.bufferBytes = new byte[i];
        }
    }

    public static class AudioEntry {
        public String author;
        public int duration;
        public String genre;
        public long id;
        public MessageObject messageObject;
        public String path;
        public String title;
    }

    private class ExternalObserver extends ContentObserver {
        public ExternalObserver() {
            super(null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            MediaController.this.m148a(Media.EXTERNAL_CONTENT_URI);
        }
    }

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str);

        void onProgressDownload(String str, float f);

        void onProgressUpload(String str, float f, boolean z);

        void onSuccessDownload(String str);
    }

    private class GalleryObserverExternal extends ContentObserver {

        /* renamed from: com.hanista.mobogram.messenger.MediaController.GalleryObserverExternal.1 */
        class C04831 implements Runnable {
            C04831() {
            }

            public void run() {
                MediaController.this.ak = null;
                MediaController.m111e(0);
            }
        }

        public GalleryObserverExternal() {
            super(null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (MediaController.this.ak != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.this.ak);
            }
            AndroidUtilities.runOnUIThread(MediaController.this.ak = new C04831(), 2000);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {

        /* renamed from: com.hanista.mobogram.messenger.MediaController.GalleryObserverInternal.1 */
        class C04841 implements Runnable {
            C04841() {
            }

            public void run() {
                if (PhotoViewer.getInstance().isVisible()) {
                    GalleryObserverInternal.this.scheduleReloadRunnable();
                    return;
                }
                MediaController.this.ak = null;
                MediaController.m111e(0);
            }
        }

        public GalleryObserverInternal() {
            super(null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.this.ak = new C04841(), 2000);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (MediaController.this.ak != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.this.ak);
            }
            scheduleReloadRunnable();
        }
    }

    private class InternalObserver extends ContentObserver {
        public InternalObserver() {
            super(null);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            MediaController.this.m148a(Media.INTERNAL_CONTENT_URI);
        }
    }

    public static class PhotoEntry {
        public int bucketId;
        public CharSequence caption;
        public long dateTaken;
        public int imageId;
        public String imagePath;
        public boolean isVideo;
        public int orientation;
        public String path;
        public ArrayList<InputDocument> stickers;
        public String thumbPath;

        public PhotoEntry(int i, int i2, long j, String str, int i3, boolean z) {
            this.stickers = new ArrayList();
            this.bucketId = i;
            this.imageId = i2;
            this.dateTaken = j;
            this.path = str;
            this.orientation = i3;
            this.isVideo = z;
        }
    }

    public static class SearchImage {
        public CharSequence caption;
        public int date;
        public Document document;
        public int height;
        public String id;
        public String imagePath;
        public String imageUrl;
        public String localUrl;
        public int size;
        public ArrayList<InputDocument> stickers;
        public String thumbPath;
        public String thumbUrl;
        public int type;
        public int width;

        public SearchImage() {
            this.stickers = new ArrayList();
        }
    }

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = 0;
        }

        public void run() {
            if (this.currentObserverToken == MediaController.this.bs) {
                try {
                    if (MediaController.this.bm != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.bm);
                        MediaController.this.bm = null;
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                try {
                    if (MediaController.this.bl != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.bl);
                        MediaController.this.bl = null;
                    }
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        }
    }

    private static class VideoConvertRunnable implements Runnable {
        private MessageObject messageObject;

        /* renamed from: com.hanista.mobogram.messenger.MediaController.VideoConvertRunnable.1 */
        static class C04851 implements Runnable {
            final /* synthetic */ MessageObject val$obj;

            C04851(MessageObject messageObject) {
                this.val$obj = messageObject;
            }

            public void run() {
                try {
                    Thread thread = new Thread(new VideoConvertRunnable(null), "VideoConvertRunnable");
                    thread.start();
                    thread.join();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        private VideoConvertRunnable(MessageObject messageObject) {
            this.messageObject = messageObject;
        }

        public static void runConversion(MessageObject messageObject) {
            new Thread(new C04851(messageObject)).start();
        }

        public void run() {
            MediaController.m71a().m122j(this.messageObject);
        }
    }

    static {
        f27a = new int[3];
        f29f = new String[]{"_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation"};
        f30g = new String[]{"_id", "bucket_id", "bucket_display_name", "_data", "datetaken"};
        bv = null;
    }

    public MediaController() {
        this.f60h = new Object();
        this.f61i = new HashMap();
        this.f75w = 0;
        this.f78z = -100.0f;
        this.f37G = new float[3];
        this.f38H = new float[3];
        this.f39I = new float[3];
        this.f42L = 0;
        this.f44N = new ArrayList();
        this.f45O = new Object();
        this.f46P = false;
        this.f47Q = true;
        this.f48R = new HashMap();
        this.f57b = 0;
        this.f58c = 0;
        this.f59d = 0;
        this.f52V = 0;
        this.f53W = new ArrayList();
        this.f54X = new ArrayList();
        this.f55Y = new ArrayList();
        this.f56Z = new ArrayList();
        this.aa = new ArrayList();
        this.ab = new ArrayList();
        this.ac = new HashMap();
        this.ad = true;
        this.ae = true;
        this.af = true;
        this.ag = false;
        this.ah = true;
        this.am = new HashMap();
        this.an = new HashMap();
        this.ao = new HashMap();
        this.ap = false;
        this.aq = new HashMap();
        this.ar = new ArrayList();
        this.as = 0;
        this.at = false;
        this.au = null;
        this.av = null;
        this.aw = 0;
        this.ay = 0;
        this.az = false;
        this.aC = 0;
        this.aD = null;
        this.aE = new Object();
        this.aG = new ArrayList();
        this.aH = new ArrayList();
        this.aN = null;
        this.aO = null;
        this.aP = null;
        this.aW = new ArrayList();
        this.aX = new ArrayList();
        this.aY = new Object();
        this.aZ = new Object();
        this.ba = new short[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
        this.bc = new Object();
        this.bd = new ArrayList();
        this.bk = new C04671();
        this.bl = null;
        this.bm = null;
        this.bn = 0;
        this.bo = 0;
        this.bp = 0;
        this.bq = null;
        this.br = null;
        this.bs = 0;
        this.bt = null;
        this.bu = null;
        this.bx = null;
        this.by = null;
        this.bz = null;
        this.bA = null;
        this.bB = null;
        this.bE = new Runnable() {

            /* renamed from: com.hanista.mobogram.messenger.MediaController.26.1 */
            class C04711 implements Runnable {
                final /* synthetic */ ByteBuffer val$finalBuffer;
                final /* synthetic */ boolean val$flush;

                C04711(ByteBuffer byteBuffer, boolean z) {
                    this.val$finalBuffer = byteBuffer;
                    this.val$flush = z;
                }

                public void run() {
                    while (this.val$finalBuffer.hasRemaining()) {
                        int limit;
                        if (this.val$finalBuffer.remaining() > MediaController.this.be.remaining()) {
                            limit = this.val$finalBuffer.limit();
                            this.val$finalBuffer.limit(MediaController.this.be.remaining() + this.val$finalBuffer.position());
                        } else {
                            limit = -1;
                        }
                        MediaController.this.be.put(this.val$finalBuffer);
                        if (MediaController.this.be.position() == MediaController.this.be.limit() || this.val$flush) {
                            if (MediaController.this.writeFrame(MediaController.this.be, !this.val$flush ? MediaController.this.be.limit() : this.val$finalBuffer.position()) != 0) {
                                MediaController.this.be.rewind();
                                MediaController.this.aR = MediaController.this.aR + ((long) ((MediaController.this.be.limit() / 2) / 16));
                            }
                        }
                        if (limit != -1) {
                            this.val$finalBuffer.limit(limit);
                        }
                    }
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.MediaController.26.2 */
            class C04722 implements Runnable {
                final /* synthetic */ double val$amplitude;

                C04722(double d) {
                    this.val$amplitude = d;
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - MediaController.this.aQ), Double.valueOf(this.val$amplitude));
                }
            }

            public void run() {
                if (MediaController.this.aN != null) {
                    DataHelper dataHelper = new DataHelper(MediaController.this.aN);
                    short[] sArr = null;
                    while (sArr == null) {
                        dataHelper.f2573b = false;
                        if (MoboConstants.aQ == 2) {
                            MediaController.this.bx.m2570a(MediaController.this.bw, dataHelper);
                            RobotizeProcessor.m2585a(MediaController.this.bw);
                            sArr = MediaController.this.by.m2568a(MediaController.this.bw);
                        } else if (MoboConstants.aQ == 3) {
                            MediaController.this.bx.m2570a(MediaController.this.bw, dataHelper);
                            DetuneProcessor.m2582a(MediaController.this.bw);
                            sArr = MediaController.this.by.m2568a(MediaController.this.bw);
                        } else if (MoboConstants.aQ == 4) {
                            MediaController.this.bx.m2570a(MediaController.this.bw, dataHelper);
                            HoarsenessProcessor.m2583a(MediaController.this.bw);
                            sArr = MediaController.this.by.m2568a(MediaController.this.bw);
                        } else if (MoboConstants.aQ == 5) {
                            MediaController.this.bx.m2570a(MediaController.this.bC, dataHelper);
                            MediaController.this.bz.m2578a(MediaController.this.bC);
                            MediaController.this.bB.m2565b(MediaController.this.bC);
                            MediaController.this.bA.m2577a(MediaController.this.bC, MediaController.this.bD);
                            sArr = MediaController.this.by.m2568a(MediaController.this.bD);
                        }
                        if (sArr == null && dataHelper.f2572a < 0 && dataHelper.f2573b) {
                            sArr = new short[0];
                            break;
                        }
                    }
                    ByteBuffer allocateDirect = ByteBuffer.allocateDirect(sArr.length * 2);
                    allocateDirect.order(ByteOrder.nativeOrder());
                    allocateDirect.rewind();
                    allocateDirect.limit(sArr.length * 2);
                    for (short putShort : sArr) {
                        allocateDirect.putShort(putShort);
                    }
                    allocateDirect.position(0);
                    int i = dataHelper.f2572a * 2;
                    if (dataHelper.f2572a == -1) {
                        i = sArr.length * 2;
                    }
                    int min = Math.min(i, sArr.length * 2);
                    if (min > 0) {
                        double d;
                        allocateDirect.limit(min);
                        double d2 = 0.0d;
                        try {
                            long d3 = ((long) (min / 2)) + MediaController.this.bb;
                            int d4 = (int) ((((double) MediaController.this.bb) / ((double) d3)) * ((double) MediaController.this.ba.length));
                            int length = MediaController.this.ba.length - d4;
                            if (d4 != 0) {
                                float length2 = ((float) MediaController.this.ba.length) / ((float) d4);
                                float f = 0.0f;
                                for (int i2 = 0; i2 < d4; i2++) {
                                    MediaController.this.ba[i2] = MediaController.this.ba[(int) f];
                                    f += length2;
                                }
                            }
                            float f2 = (((float) min) / 2.0f) / ((float) length);
                            float f3 = 0.0f;
                            i = d4;
                            for (d4 = 0; d4 < min / 2; d4++) {
                                short s = allocateDirect.getShort();
                                if (s > (short) 2500) {
                                    d2 += (double) (s * s);
                                }
                                if (d4 == ((int) f3) && i < MediaController.this.ba.length) {
                                    MediaController.this.ba[i] = s;
                                    f3 += f2;
                                    i++;
                                }
                            }
                            MediaController.this.bb = d3;
                            d = d2;
                        } catch (Throwable e) {
                            Throwable th = e;
                            d = 0.0d;
                            Throwable th2 = th;
                            FileLog.m18e("tmessages", th2);
                            th2.printStackTrace();
                        }
                        allocateDirect.position(0);
                        d2 = Math.sqrt((d / ((double) min)) / 2.0d);
                        boolean z = min != allocateDirect.capacity();
                        if (min != 0) {
                            MediaController.this.bj.postRunnable(new C04711(allocateDirect, z));
                        }
                        MediaController.this.bi.postRunnable(MediaController.this.bE);
                        AndroidUtilities.runOnUIThread(new C04722(d2));
                        return;
                    }
                    MediaController.this.m115g(MediaController.this.bg);
                }
            }
        };
        try {
            int i;
            this.bf = AudioRecord.getMinBufferSize(16000, 16, 2);
            if (this.bf <= 0) {
                this.bf = 1280;
            }
            this.ay = AudioTrack.getMinBufferSize(48000, 4, 2);
            if (this.ay <= 0) {
                this.ay = 3840;
            }
            for (i = 0; i < 5; i++) {
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
                allocateDirect.order(ByteOrder.nativeOrder());
                this.bd.add(allocateDirect);
            }
            for (i = 0; i < 3; i++) {
                this.aX.add(new AudioBuffer(this.ay));
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            this.f62j = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
            this.f66n = this.f62j.getDefaultSensor(10);
            this.f67o = this.f62j.getDefaultSensor(9);
            if (this.f66n == null || this.f67o == null) {
                FileLog.m16e("tmessages", "gravity or linear sensor not found");
                this.f65m = this.f62j.getDefaultSensor(1);
                this.f66n = null;
                this.f67o = null;
            }
            this.f64l = this.f62j.getDefaultSensor(8);
            this.f63k = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        this.be = ByteBuffer.allocateDirect(1920);
        this.bi = new DispatchQueue("recordQueue");
        this.bi.setPriority(10);
        this.bj = new DispatchQueue("fileEncodingQueue");
        this.bj.setPriority(10);
        this.aV = new DispatchQueue("playerQueue");
        this.aU = new DispatchQueue("fileDecodingQueue");
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.f57b = sharedPreferences.getInt("mobileDataDownloadMask", 51);
        this.f58c = sharedPreferences.getInt("wifiDownloadMask", 51);
        this.f59d = sharedPreferences.getInt("roamingDownloadMask", 0);
        this.ad = sharedPreferences.getBoolean("save_gallery", false);
        this.ae = sharedPreferences.getBoolean("autoplay_gif", true);
        this.af = sharedPreferences.getBoolean("raise_to_speak", true);
        this.ag = sharedPreferences.getBoolean("custom_tabs", false);
        this.ah = sharedPreferences.getBoolean("direct_share", true);
        this.ai = sharedPreferences.getBoolean("shuffleMusic", false);
        this.aj = sharedPreferences.getInt("repeatMode", 0);
        AndroidUtilities.runOnUIThread(new C04732());
        ApplicationLoader.applicationContext.registerReceiver(new C04743(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (UserConfig.isClientActivated()) {
            m170d();
        }
        if (VERSION.SDK_INT >= 16) {
            this.bu = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height"};
        } else {
            this.bu = new String[]{"_data", "_display_name", "bucket_display_name", "datetaken", "title"};
        }
        try {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, false, new GalleryObserverExternal());
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
        }
        try {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(Media.INTERNAL_CONTENT_URI, false, new GalleryObserverInternal());
        } catch (Throwable e222) {
            FileLog.m18e("tmessages", e222);
        }
        try {
            PhoneStateListener c04754 = new C04754();
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                telephonyManager.listen(c04754, 32);
            }
        } catch (Throwable e2222) {
            FileLog.m18e("tmessages", e2222);
        }
    }

    private void m31I() {
        try {
            float f = this.f42L != 1 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
            if (this.au != null) {
                this.au.setVolume(f, f);
            } else if (this.av != null) {
                this.av.setStereoVolume(f, f);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private void m33J() {
        synchronized (this.aE) {
            if (this.aD != null) {
                try {
                    this.aD.cancel();
                    this.aD = null;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    private int m34K() {
        return ConnectionsManager.isConnectedToWiFi() ? this.f58c : ConnectionsManager.isRoaming() ? this.f59d : this.f57b;
    }

    private void m37L() {
        for (Entry entry : this.aq.entrySet()) {
            m151a((String) entry.getKey(), (FileDownloadProgressListener) entry.getValue());
        }
        this.aq.clear();
        Iterator it = this.ar.iterator();
        while (it.hasNext()) {
            m149a((FileDownloadProgressListener) it.next());
        }
        this.ar.clear();
    }

    private void m39M() {
        this.aU.postRunnable(new C04797());
    }

    private void m41N() {
        this.aV.postRunnable(new C04818());
    }

    private void m43O() {
        if (!this.aG.isEmpty()) {
            ArrayList arrayList = new ArrayList(this.aG);
            this.aH.clear();
            MessageObject messageObject = (MessageObject) this.aG.get(this.aI);
            arrayList.remove(this.aI);
            this.aH.add(messageObject);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                int nextInt = Utilities.random.nextInt(arrayList.size());
                this.aH.add(arrayList.get(nextInt));
                arrayList.remove(nextInt);
            }
        }
    }

    private void m45P() {
        File file = null;
        if ((m34K() & 16) != 0) {
            ArrayList arrayList = this.ai ? this.aH : this.aG;
            if (arrayList != null && arrayList.size() >= 2) {
                File file2;
                int i = this.aI + 1;
                if (i >= arrayList.size()) {
                    i = 0;
                }
                MessageObject messageObject = (MessageObject) arrayList.get(i);
                if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
                    file2 = new File(messageObject.messageOwner.attachPath);
                    if (file2.exists()) {
                        file = file2;
                    }
                }
                file2 = file != null ? file : FileLoader.getPathToMessage(messageObject.messageOwner);
                if (file2 == null || file2.exists()) {
                    if (file2 != null && file2 != file && !file2.exists() && messageObject.isMusic()) {
                        FileLoader.getInstance().loadFile(messageObject.getDocument(), false, false);
                    }
                } else if (file2 != null) {
                }
            }
        }
    }

    private void m47Q() {
        if (!this.f44N.isEmpty()) {
            synchronized (this.f60h) {
                this.f46P = false;
            }
            MessageObject messageObject = (MessageObject) this.f44N.get(0);
            Intent intent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
            intent.putExtra("path", messageObject.messageOwner.attachPath);
            if (messageObject.messageOwner.media.document != null) {
                for (int i = 0; i < messageObject.messageOwner.media.document.attributes.size(); i++) {
                    if (((DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(i)) instanceof TL_documentAttributeAnimated) {
                        intent.putExtra("gif", true);
                        break;
                    }
                }
            }
            ApplicationLoader.applicationContext.startService(intent);
            VideoConvertRunnable.runConversion(messageObject);
        }
    }

    private void m49R() {
        synchronized (this.f60h) {
            boolean z = this.f46P;
        }
        if (z) {
            throw new RuntimeException("canceled conversion");
        }
    }

    private void m51S() {
        Preferences preferences = new Preferences(ApplicationLoader.applicationContext);
        FrameType frameType = FrameType.Small;
        switch (MoboConstants.aQ) {
            case VideoPlayer.STATE_PREPARING /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
                frameType = FrameType.Medium;
                break;
            case VideoPlayer.STATE_READY /*4*/:
                frameType = FrameType.Small;
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                frameType = FrameType.Default;
                break;
        }
        int a = preferences.m2550a(frameType, 16000);
        int b = preferences.m2552b(frameType, 16000) * 2;
        this.bw = new float[a];
        try {
            AudioDevice pcmInDevice = new PcmInDevice(ApplicationLoader.applicationContext, HeadsetMode.WIRED_HEADPHONES);
            AudioDevice pcmOutDevice = new PcmOutDevice(ApplicationLoader.applicationContext, HeadsetMode.WIRED_HEADPHONES);
            if (MoboConstants.aQ == 5) {
                float pow = Math.pow(2.0f, ((float) MoboConstants.aS) / 12.0f);
                int a2 = Math.m2566a(((float) b) / pow);
                int a3 = Math.m2566a(((float) a) / pow);
                this.bx = new StftPreprocessor(pcmInDevice, a, a2, true);
                this.bz = new NativeTimescaleProcessor(a, a2, b);
                this.bB = new KissFFT(a);
                this.bA = new NativeResampleProcessor(a, a3);
                this.by = new StftPostprocessor(pcmOutDevice, a3, a2, false);
                this.bC = new float[a];
                this.bD = new float[a3];
            } else if (MoboConstants.aQ == 2 || MoboConstants.aQ == 3 || MoboConstants.aQ == 4) {
                this.bx = new StftPreprocessor(pcmInDevice, a, b, true);
                this.by = new StftPostprocessor(pcmOutDevice, a, b, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"NewApi"})
    public static int m59a(MediaCodecInfo mediaCodecInfo, String str) {
        int i = 0;
        CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(str);
        int i2 = 0;
        while (i < capabilitiesForType.colorFormats.length) {
            int i3 = capabilitiesForType.colorFormats[i];
            if (m118h(i3)) {
                if (!mediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder") || i3 != 19) {
                    return i3;
                }
                i2 = i3;
            }
            i++;
        }
        return i2;
    }

    @TargetApi(16)
    private int m60a(MediaExtractor mediaExtractor, boolean z) {
        int trackCount = mediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            String string = mediaExtractor.getTrackFormat(i).getString("mime");
            if (z) {
                if (string.startsWith("audio/")) {
                    return i;
                }
            } else if (string.startsWith("video/")) {
                return i;
            }
        }
        return -5;
    }

    @TargetApi(16)
    private long m65a(MessageObject messageObject, MediaExtractor mediaExtractor, MP4Builder mP4Builder, BufferInfo bufferInfo, long j, long j2, File file, boolean z) {
        int a = m60a(mediaExtractor, z);
        if (a < 0) {
            return -1;
        }
        mediaExtractor.selectTrack(a);
        MediaFormat trackFormat = mediaExtractor.getTrackFormat(a);
        int addTrack = mP4Builder.addTrack(trackFormat, z);
        int integer = trackFormat.getInteger("max-input-size");
        Object obj = null;
        if (j > 0) {
            mediaExtractor.seekTo(j, 0);
        } else {
            mediaExtractor.seekTo(0, 0);
        }
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(integer);
        long j3 = -1;
        m49R();
        long j4 = -100;
        while (obj == null) {
            long j5;
            m49R();
            Object obj2 = null;
            int sampleTrackIndex = mediaExtractor.getSampleTrackIndex();
            if (sampleTrackIndex == a) {
                bufferInfo.size = mediaExtractor.readSampleData(allocateDirect, 0);
                if (bufferInfo.size >= 0) {
                    bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                } else {
                    bufferInfo.size = 0;
                    obj2 = 1;
                }
                if (bufferInfo.size <= 0 || r4 != null) {
                    j5 = j4;
                    j4 = j3;
                    j3 = j5;
                } else {
                    if (j > 0 && j3 == -1) {
                        j3 = bufferInfo.presentationTimeUs;
                    }
                    if (j2 < 0 || bufferInfo.presentationTimeUs < j2) {
                        if (bufferInfo.presentationTimeUs > j4) {
                            bufferInfo.offset = 0;
                            bufferInfo.flags = mediaExtractor.getSampleFlags();
                            if (mP4Builder.writeSampleData(addTrack, allocateDirect, bufferInfo, z)) {
                                m82a(messageObject, file, false, false);
                            }
                        }
                        j4 = j3;
                        j3 = bufferInfo.presentationTimeUs;
                    } else {
                        obj2 = 1;
                        j5 = j4;
                        j4 = j3;
                        j3 = j5;
                    }
                }
                if (obj2 == null) {
                    mediaExtractor.advance();
                }
            } else if (sampleTrackIndex == -1) {
                obj2 = 1;
                j5 = j4;
                j4 = j3;
                j3 = j5;
            } else {
                mediaExtractor.advance();
                j5 = j4;
                j4 = j3;
                j3 = j5;
            }
            obj = obj2 != null ? 1 : obj;
            j5 = j4;
            j4 = j3;
            j3 = j5;
        }
        mediaExtractor.unselectTrack(a);
        return j3;
    }

    @SuppressLint({"NewApi"})
    public static MediaCodecInfo m68a(String str) {
        int codecCount = MediaCodecList.getCodecCount();
        MediaCodecInfo mediaCodecInfo = null;
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
            if (codecInfoAt.isEncoder()) {
                for (String equalsIgnoreCase : codecInfoAt.getSupportedTypes()) {
                    if (equalsIgnoreCase.equalsIgnoreCase(str)) {
                        if (!codecInfoAt.getName().equals("OMX.SEC.avc.enc") || codecInfoAt.getName().equals("OMX.SEC.AVC.Encoder")) {
                            return codecInfoAt;
                        }
                        mediaCodecInfo = codecInfoAt;
                    }
                }
                continue;
            }
        }
        return mediaCodecInfo;
    }

    public static MediaController m71a() {
        MediaController mediaController = bv;
        if (mediaController == null) {
            synchronized (MediaController.class) {
                mediaController = bv;
                if (mediaController == null) {
                    mediaController = new MediaController();
                    bv = mediaController;
                }
            }
        }
        return mediaController;
    }

    public static String m77a(Uri uri, String str) {
        InputStream openInputStream;
        FileOutputStream fileOutputStream;
        Throwable e;
        Throwable th;
        Object obj;
        String str2 = null;
        try {
            String d = m107d(uri);
            if (d == null) {
                int i = UserConfig.lastLocalId;
                UserConfig.lastLocalId--;
                UserConfig.saveConfig(false);
                d = String.format(Locale.US, "%d.%s", new Object[]{Integer.valueOf(i), str});
            }
            openInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            try {
                File file = new File(FileLoader.getInstance().getDirectory(4), d);
                fileOutputStream = new FileOutputStream(file);
                try {
                    byte[] bArr = new byte[20480];
                    while (true) {
                        int read = openInputStream.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, read);
                    }
                    str2 = file.getAbsolutePath();
                    if (openInputStream != null) {
                        try {
                            openInputStream.close();
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e22) {
                            FileLog.m18e("tmessages", e22);
                        }
                    }
                } catch (Exception e3) {
                    e22 = e3;
                    try {
                        FileLog.m18e("tmessages", e22);
                        if (openInputStream != null) {
                            try {
                                openInputStream.close();
                            } catch (Throwable e222) {
                                FileLog.m18e("tmessages", e222);
                            }
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e2222) {
                                FileLog.m18e("tmessages", e2222);
                            }
                        }
                        return str2;
                    } catch (Throwable th2) {
                        th = th2;
                        if (openInputStream != null) {
                            try {
                                openInputStream.close();
                            } catch (Throwable e22222) {
                                FileLog.m18e("tmessages", e22222);
                            }
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e222222) {
                                FileLog.m18e("tmessages", e222222);
                            }
                        }
                        throw th;
                    }
                }
            } catch (Exception e4) {
                e222222 = e4;
                obj = str2;
                FileLog.m18e("tmessages", e222222);
                if (openInputStream != null) {
                    openInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return str2;
            } catch (Throwable e2222222) {
                obj = str2;
                th = e2222222;
                if (openInputStream != null) {
                    openInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        } catch (Exception e5) {
            e2222222 = e5;
            obj = str2;
            Object obj2 = str2;
            FileLog.m18e("tmessages", e2222222);
            if (openInputStream != null) {
                openInputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            return str2;
        } catch (Throwable e22222222) {
            fileOutputStream = str2;
            openInputStream = str2;
            th = e22222222;
            if (openInputStream != null) {
                openInputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            throw th;
        }
        return str2;
    }

    private void m82a(MessageObject messageObject, File file, boolean z, boolean z2) {
        boolean z3 = this.f47Q;
        if (z3) {
            this.f47Q = false;
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass25(z2, messageObject, file, z3, z));
    }

    private void m83a(String str, int i) {
        DownloadObject downloadObject = (DownloadObject) this.ac.get(str);
        if (downloadObject != null) {
            this.ac.remove(str);
            if (i == 0 || i == 2) {
                MessagesStorage.getInstance().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            if (downloadObject.type == 1) {
                this.f53W.remove(downloadObject);
                if (this.f53W.isEmpty()) {
                    m163b(1);
                }
            } else if (downloadObject.type == 2) {
                this.f54X.remove(downloadObject);
                if (this.f54X.isEmpty()) {
                    m163b(2);
                }
            } else if (downloadObject.type == 4) {
                this.ab.remove(downloadObject);
                if (this.ab.isEmpty()) {
                    m163b(4);
                }
            } else if (downloadObject.type == 8) {
                this.f55Y.remove(downloadObject);
                if (this.f55Y.isEmpty()) {
                    m163b(8);
                }
            } else if (downloadObject.type == 16) {
                this.f56Z.remove(downloadObject);
                if (this.f56Z.isEmpty()) {
                    m163b(16);
                }
            } else if (downloadObject.type == 32) {
                this.aa.remove(downloadObject);
                if (this.aa.isEmpty()) {
                    m163b(32);
                }
            }
        }
    }

    public static void m84a(String str, Context context, int i, String str2, String str3) {
        Throwable e;
        if (str != null) {
            File file;
            if (str == null || str.length() == 0) {
                file = null;
            } else {
                file = new File(str);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file != null) {
                boolean[] zArr = new boolean[1];
                if (file.exists()) {
                    ProgressDialog progressDialog;
                    if (context != null) {
                        try {
                            progressDialog = new ProgressDialog(context);
                            try {
                                progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(true);
                                progressDialog.setProgressStyle(1);
                                progressDialog.setMax(100);
                                progressDialog.setOnCancelListener(new AnonymousClass21(zArr));
                                progressDialog.show();
                            } catch (Exception e2) {
                                e = e2;
                                FileLog.m18e("tmessages", e);
                                new Thread(new AnonymousClass22(i, str2, file, zArr, progressDialog, str3)).start();
                            }
                        } catch (Throwable e3) {
                            progressDialog = null;
                            e = e3;
                            FileLog.m18e("tmessages", e);
                            new Thread(new AnonymousClass22(i, str2, file, zArr, progressDialog, str3)).start();
                        }
                    }
                    progressDialog = null;
                    new Thread(new AnonymousClass22(i, str2, file, zArr, progressDialog, str3)).start();
                }
            }
        }
    }

    private void m85a(ArrayList<Long> arrayList) {
        if (arrayList != null && !arrayList.isEmpty() && this.bn != 0 && this.bq != null && (this.bq instanceof TL_encryptedChat)) {
            Iterator it = arrayList.iterator();
            Object obj = null;
            while (it.hasNext()) {
                Long l = (Long) it.next();
                if (this.bp == 0 || l.longValue() > this.bp) {
                    Object obj2;
                    if (l.longValue() < this.bn || (this.bo != 0 && l.longValue() > this.bo + 2000)) {
                        obj2 = obj;
                    } else {
                        this.bp = Math.max(this.bp, l.longValue());
                        obj2 = 1;
                    }
                    obj = obj2;
                }
            }
            if (obj != null) {
                SecretChatHelper.getInstance().sendScreenshotMessage(this.bq, this.br, null);
            }
        }
    }

    private boolean m86a(float f) {
        return f < 5.0f && f != this.f64l.getMaximumRange();
    }

    private void m93b(float f) {
        if (f != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            if (!this.at) {
                this.av.pause();
            }
            this.av.flush();
            this.aU.postRunnable(new AnonymousClass12(f));
        }
    }

    private static void m94b(int i, ArrayList<AlbumEntry> arrayList, Integer num, ArrayList<AlbumEntry> arrayList2, Integer num2, AlbumEntry albumEntry, int i2) {
        if (al != null) {
            AndroidUtilities.cancelRunOnUIThread(al);
        }
        Runnable anonymousClass24 = new AnonymousClass24(i, arrayList, num, arrayList2, num2, albumEntry);
        al = anonymousClass24;
        AndroidUtilities.runOnUIThread(anonymousClass24, (long) i2);
    }

    public static boolean m95b(Uri uri) {
        boolean z = false;
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] bArr = new byte[12];
            if (inputStream.read(bArr, 0, 12) == 12) {
                String str = new String(bArr);
                if (str != null) {
                    String toLowerCase = str.toLowerCase();
                    if (toLowerCase.startsWith("riff") && toLowerCase.endsWith("webp")) {
                        z = true;
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                        }
                        return z;
                    }
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m18e("tmessages", e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m18e("tmessages", e2222);
                }
            }
        }
        return z;
    }

    private void m102c(boolean z) {
        if (this.ax != null) {
            boolean z2 = this.au != null;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioRouteChanged, Boolean.valueOf(this.f31A));
            MessageObject messageObject = this.ax;
            float f = this.ax.audioProgress;
            m155a(false, true);
            messageObject.audioProgress = f;
            m158a(messageObject);
            if (!z) {
                return;
            }
            if (z2) {
                AndroidUtilities.runOnUIThread(new C04829(messageObject), 100);
            } else {
                m166b(messageObject);
            }
        }
    }

    public static boolean m103c(Uri uri) {
        boolean z = false;
        InputStream inputStream = null;
        try {
            inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] bArr = new byte[3];
            if (inputStream.read(bArr, 0, 3) == 3) {
                String str = new String(bArr);
                if (str != null && str.equalsIgnoreCase("gif")) {
                    z = true;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                    return z;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e222) {
                    FileLog.m18e("tmessages", e222);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable e2222) {
                    FileLog.m18e("tmessages", e2222);
                }
            }
        }
        return z;
    }

    public static String m107d(Uri uri) {
        Cursor query;
        Throwable e;
        String str;
        int lastIndexOf;
        String str2 = null;
        if (uri.getScheme().equals("content")) {
            try {
                query = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
            } catch (Exception e2) {
                e = e2;
                query = null;
                try {
                    FileLog.m18e("tmessages", e);
                    if (query != null) {
                        query.close();
                        str = null;
                        if (str == null) {
                            return str;
                        }
                        str = uri.getPath();
                        lastIndexOf = str.lastIndexOf(47);
                        return lastIndexOf == -1 ? str.substring(lastIndexOf + 1) : str;
                    }
                    str = null;
                    if (str == null) {
                        return str;
                    }
                    str = uri.getPath();
                    lastIndexOf = str.lastIndexOf(47);
                    if (lastIndexOf == -1) {
                    }
                } catch (Throwable th) {
                    e = th;
                    if (query != null) {
                        query.close();
                    }
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                query = null;
                if (query != null) {
                    query.close();
                }
                throw e;
            }
            try {
                if (query.moveToFirst()) {
                    str2 = query.getString(query.getColumnIndex("_display_name"));
                }
                if (query != null) {
                    query.close();
                    str = str2;
                } else {
                    str = str2;
                }
            } catch (Exception e3) {
                e = e3;
                FileLog.m18e("tmessages", e);
                if (query != null) {
                    query.close();
                    str = null;
                    if (str == null) {
                        return str;
                    }
                    str = uri.getPath();
                    lastIndexOf = str.lastIndexOf(47);
                    if (lastIndexOf == -1) {
                    }
                }
                str = null;
                if (str == null) {
                    return str;
                }
                str = uri.getPath();
                lastIndexOf = str.lastIndexOf(47);
                if (lastIndexOf == -1) {
                }
            }
            if (str == null) {
                return str;
            }
            str = uri.getPath();
            lastIndexOf = str.lastIndexOf(47);
            if (lastIndexOf == -1) {
            }
        }
        str = null;
        if (str == null) {
            return str;
        }
        str = uri.getPath();
        lastIndexOf = str.lastIndexOf(47);
        if (lastIndexOf == -1) {
        }
    }

    private void m108d(boolean z) {
        ArrayList arrayList = this.ai ? this.aH : this.aG;
        if (z && this.aj == 2 && !this.aJ) {
            m155a(false, false);
            m158a((MessageObject) arrayList.get(this.aI));
            return;
        }
        this.aI++;
        if (this.aI >= arrayList.size()) {
            this.aI = 0;
            if (z && this.aj == 0 && !this.aJ) {
                if (this.au != null || this.av != null) {
                    if (this.au != null) {
                        try {
                            this.au.reset();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        try {
                            this.au.stop();
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                        try {
                            this.au.release();
                            this.au = null;
                        } catch (Throwable e22) {
                            FileLog.m18e("tmessages", e22);
                        }
                    } else if (this.av != null) {
                        synchronized (this.aZ) {
                            try {
                                this.av.pause();
                                this.av.flush();
                            } catch (Throwable e222) {
                                FileLog.m18e("tmessages", e222);
                            }
                            try {
                                this.av.release();
                                this.av = null;
                            } catch (Throwable e2222) {
                                FileLog.m18e("tmessages", e2222);
                            }
                        }
                    }
                    m33J();
                    this.aw = 0;
                    this.aF = 0;
                    this.at = true;
                    this.ax.audioProgress = 0.0f;
                    this.ax.audioProgressSec = 0;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, Integer.valueOf(this.ax.getId()), Integer.valueOf(0));
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, Integer.valueOf(this.ax.getId()));
                    return;
                }
                return;
            }
        }
        if (this.aI >= 0 && this.aI < arrayList.size()) {
            this.aL = true;
            m158a((MessageObject) arrayList.get(this.aI));
        }
    }

    public static void m111e(int i) {
        Thread thread = new Thread(new AnonymousClass23(i));
        thread.setPriority(1);
        thread.start();
    }

    private void m115g(int i) {
        if (i != 0) {
            this.bj.postRunnable(new AnonymousClass19(this.aO, this.aP, i));
        }
        try {
            if (this.aN != null) {
                this.aN.release();
                this.aN = null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        this.aO = null;
        this.aP = null;
    }

    private native long getTotalPcmDuration();

    private void m117h(MessageObject messageObject) {
        synchronized (this.aE) {
            if (this.aD != null) {
                try {
                    this.aD.cancel();
                    this.aD = null;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            this.aD = new Timer();
            this.aD.schedule(new C04775(messageObject), 0, 17);
        }
    }

    private static boolean m118h(int i) {
        switch (i) {
            case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
            case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
            case C0338R.styleable.PromptView_secondaryTextSize /*21*/:
            case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
            case 2130706688:
                return true;
            default:
                return false;
        }
    }

    private void m120i(MessageObject messageObject) {
        int i = messageObject.isVoice() ? this.f31A ? 3 : 2 : 1;
        if (this.f40J != i) {
            this.f40J = i;
            if (i == 3) {
                i = NotificationsController.getInstance().audioManager.requestAudioFocus(this, 0, 1);
            } else {
                i = NotificationsController.getInstance().audioManager.requestAudioFocus(this, 3, i == 2 ? 3 : 1);
            }
            if (i == 1) {
                this.f42L = 2;
            }
        }
    }

    private native int isOpusFile(String str);

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.TargetApi(16)
    private boolean m122j(com.hanista.mobogram.messenger.MessageObject r55) {
        /*
        r54 = this;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r8 = r4.originalPath;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r0 = r4.startTime;
        r30 = r0;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r0 = r4.endTime;
        r46 = r0;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r6 = r4.resultWidth;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r7 = r4.resultHeight;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r5 = r4.rotationValue;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r9 = r4.originalWidth;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r10 = r4.originalHeight;
        r0 = r55;
        r4 = r0.videoEditedInfo;
        r0 = r4.bitrate;
        r42 = r0;
        r4 = 0;
        r45 = new java.io.File;
        r0 = r55;
        r11 = r0.messageOwner;
        r11 = r11.attachPath;
        r0 = r45;
        r0.<init>(r11);
        r11 = android.os.Build.VERSION.SDK_INT;
        r12 = 18;
        if (r11 >= r12) goto L_0x00af;
    L_0x0050:
        if (r7 <= r6) goto L_0x00af;
    L_0x0052:
        if (r6 == r9) goto L_0x00af;
    L_0x0054:
        if (r7 == r10) goto L_0x00af;
    L_0x0056:
        r5 = 90;
        r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r23 = r6;
        r24 = r7;
        r53 = r4;
        r4 = r5;
        r5 = r53;
    L_0x0063:
        r6 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;
        r7 = "videoconvert";
        r11 = 0;
        r48 = r6.getSharedPreferences(r7, r11);
        r6 = "isPreviousOk";
        r7 = 1;
        r0 = r48;
        r6 = r0.getBoolean(r6, r7);
        r7 = r48.edit();
        r11 = "isPreviousOk";
        r12 = 0;
        r7 = r7.putBoolean(r11, r12);
        r7.commit();
        r11 = new java.io.File;
        r11.<init>(r8);
        r7 = r11.canRead();
        if (r7 == 0) goto L_0x0093;
    L_0x0091:
        if (r6 != 0) goto L_0x00e9;
    L_0x0093:
        r4 = 1;
        r5 = 1;
        r0 = r54;
        r1 = r55;
        r2 = r45;
        r0.m82a(r1, r2, r4, r5);
        r4 = r48.edit();
        r5 = "isPreviousOk";
        r6 = 1;
        r4 = r4.putBoolean(r5, r6);
        r4.commit();
        r4 = 0;
    L_0x00ae:
        return r4;
    L_0x00af:
        r11 = android.os.Build.VERSION.SDK_INT;
        r12 = 20;
        if (r11 <= r12) goto L_0x0990;
    L_0x00b5:
        r11 = 90;
        if (r5 != r11) goto L_0x00c6;
    L_0x00b9:
        r5 = 0;
        r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r23 = r6;
        r24 = r7;
        r53 = r4;
        r4 = r5;
        r5 = r53;
        goto L_0x0063;
    L_0x00c6:
        r11 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        if (r5 != r11) goto L_0x00d7;
    L_0x00ca:
        r4 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r5 = 0;
        r23 = r7;
        r24 = r6;
        r53 = r4;
        r4 = r5;
        r5 = r53;
        goto L_0x0063;
    L_0x00d7:
        r11 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r5 != r11) goto L_0x0990;
    L_0x00db:
        r5 = 0;
        r4 = 90;
        r23 = r6;
        r24 = r7;
        r53 = r4;
        r4 = r5;
        r5 = r53;
        goto L_0x0063;
    L_0x00e9:
        r6 = 1;
        r0 = r54;
        r0.f47Q = r6;
        r27 = 0;
        r50 = java.lang.System.currentTimeMillis();
        if (r24 == 0) goto L_0x090b;
    L_0x00f6:
        if (r23 == 0) goto L_0x090b;
    L_0x00f8:
        r7 = 0;
        r6 = 0;
        r49 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r49.<init>();	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r8 = new com.hanista.mobogram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r8.<init>();	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r0 = r45;
        r8.setCacheFile(r0);	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r8.setRotation(r4);	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r0 = r24;
        r1 = r23;
        r8.setSize(r0, r1);	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r4 = new com.hanista.mobogram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r4.<init>();	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r35 = r4.createMovie(r8);	 Catch:{ Exception -> 0x08c4, all -> 0x0928 }
        r34 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x093b, all -> 0x092f }
        r34.<init>();	 Catch:{ Exception -> 0x093b, all -> 0x092f }
        r4 = r11.toString();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        r0 = r34;
        r0.setDataSource(r4);	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        r54.m49R();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        r0 = r24;
        if (r0 != r9) goto L_0x0137;
    L_0x0131:
        r0 = r23;
        if (r0 != r10) goto L_0x0137;
    L_0x0135:
        if (r5 == 0) goto L_0x089c;
    L_0x0137:
        r4 = 0;
        r0 = r54;
        r1 = r34;
        r52 = r0.m60a(r1, r4);	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        if (r52 < 0) goto L_0x098c;
    L_0x0142:
        r9 = 0;
        r12 = 0;
        r7 = 0;
        r6 = 0;
        r16 = -1;
        r39 = 0;
        r13 = 0;
        r14 = 0;
        r11 = 0;
        r38 = -5;
        r4 = 0;
        r8 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r10 = r8.toLowerCase();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r8 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r15 = 18;
        if (r8 >= r15) goto L_0x044f;
    L_0x015c:
        r8 = "video/avc";
        r15 = m68a(r8);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r8 = "video/avc";
        r8 = m59a(r15, r8);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r8 != 0) goto L_0x020c;
    L_0x016c:
        r4 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r5 = "no supported color format";
        r4.<init>(r5);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        throw r4;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
    L_0x0175:
        r4 = move-exception;
        r5 = r6;
        r6 = r7;
        r7 = r9;
    L_0x0179:
        r8 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r8, r4);	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        r4 = 1;
        r32 = r5;
        r33 = r6;
    L_0x0184:
        r0 = r34;
        r1 = r52;
        r0.unselectTrack(r1);	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        if (r32 == 0) goto L_0x0190;
    L_0x018d:
        r32.release();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
    L_0x0190:
        if (r33 == 0) goto L_0x0195;
    L_0x0192:
        r33.release();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
    L_0x0195:
        if (r7 == 0) goto L_0x019d;
    L_0x0197:
        r7.stop();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        r7.release();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
    L_0x019d:
        if (r12 == 0) goto L_0x01a5;
    L_0x019f:
        r12.stop();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        r12.release();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
    L_0x01a5:
        r54.m49R();	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
    L_0x01a8:
        r10 = r30;
    L_0x01aa:
        if (r4 != 0) goto L_0x01c3;
    L_0x01ac:
        r5 = -1;
        r0 = r42;
        if (r0 == r5) goto L_0x01c3;
    L_0x01b1:
        r15 = 1;
        r5 = r54;
        r6 = r55;
        r7 = r34;
        r8 = r35;
        r9 = r49;
        r12 = r46;
        r14 = r45;
        r5.m65a(r6, r7, r8, r9, r10, r12, r14, r15);	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
    L_0x01c3:
        if (r34 == 0) goto L_0x01c8;
    L_0x01c5:
        r34.release();
    L_0x01c8:
        if (r35 == 0) goto L_0x01d0;
    L_0x01ca:
        r5 = 0;
        r0 = r35;
        r0.finishMovie(r5);	 Catch:{ Exception -> 0x08bb }
    L_0x01d0:
        r5 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "time = ";
        r6 = r6.append(r7);
        r8 = java.lang.System.currentTimeMillis();
        r8 = r8 - r50;
        r6 = r6.append(r8);
        r6 = r6.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r5, r6);
    L_0x01f0:
        r5 = r48.edit();
        r6 = "isPreviousOk";
        r7 = 1;
        r5 = r5.putBoolean(r6, r7);
        r5.commit();
        r5 = 1;
        r0 = r54;
        r1 = r55;
        r2 = r45;
        r0.m82a(r1, r2, r5, r4);
        r4 = 1;
        goto L_0x00ae;
    L_0x020c:
        r18 = r15.getName();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r19 = "OMX.qcom.";
        r19 = r18.contains(r19);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r19 == 0) goto L_0x041e;
    L_0x0219:
        r4 = 1;
        r18 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r19 = 16;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x023b;
    L_0x0224:
        r18 = "lge";
        r0 = r18;
        r18 = r10.equals(r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r18 != 0) goto L_0x023a;
    L_0x022f:
        r18 = "nokia";
        r0 = r18;
        r18 = r10.equals(r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r18 == 0) goto L_0x023b;
    L_0x023a:
        r11 = 1;
    L_0x023b:
        r18 = "tmessages";
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r19.<init>();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r20 = "codec = ";
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r15 = r15.getName();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r0 = r19;
        r15 = r0.append(r15);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r19 = " manufacturer = ";
        r0 = r19;
        r15 = r15.append(r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r15 = r15.append(r10);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r19 = "device = ";
        r0 = r19;
        r15 = r15.append(r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r19 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r0 = r19;
        r15 = r15.append(r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r15 = r15.toString();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r0 = r18;
        com.hanista.mobogram.messenger.FileLog.m16e(r0, r15);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r44 = r8;
        r8 = r4;
    L_0x027e:
        r4 = "tmessages";
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r15.<init>();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r18 = "colorFormat = ";
        r0 = r18;
        r15 = r15.append(r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r0 = r44;
        r15 = r15.append(r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r15 = r15.toString();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        com.hanista.mobogram.messenger.FileLog.m16e(r4, r15);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = 0;
        r15 = r24 * r23;
        r15 = r15 * 3;
        r15 = r15 / 2;
        if (r8 != 0) goto L_0x0457;
    L_0x02a5:
        r8 = r23 % 16;
        if (r8 == 0) goto L_0x0988;
    L_0x02a9:
        r4 = r23 % 16;
        r4 = 16 - r4;
        r4 = r4 + r23;
        r4 = r4 - r23;
        r4 = r4 * r24;
        r8 = r4 * 5;
        r8 = r8 / 4;
        r15 = r15 + r8;
        r43 = r4;
    L_0x02ba:
        r0 = r34;
        r1 = r52;
        r0.selectTrack(r1);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r18 = 0;
        r4 = (r30 > r18 ? 1 : (r30 == r18 ? 0 : -1));
        if (r4 <= 0) goto L_0x04a4;
    L_0x02c7:
        r4 = 0;
        r0 = r34;
        r1 = r30;
        r0.seekTo(r1, r4);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
    L_0x02cf:
        r0 = r34;
        r1 = r52;
        r10 = r0.getTrackFormat(r1);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = "video/avc";
        r0 = r24;
        r1 = r23;
        r8 = android.media.MediaFormat.createVideoFormat(r4, r0, r1);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = "color-format";
        r0 = r44;
        r8.setInteger(r4, r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r18 = "bitrate";
        if (r42 <= 0) goto L_0x04df;
    L_0x02ef:
        r4 = r42;
    L_0x02f1:
        r0 = r18;
        r8.setInteger(r0, r4);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = "frame-rate";
        r18 = 25;
        r0 = r18;
        r8.setInteger(r4, r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = "i-frame-interval";
        r18 = 10;
        r0 = r18;
        r8.setInteger(r4, r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r18 = 18;
        r0 = r18;
        if (r4 >= r0) goto L_0x0324;
    L_0x0312:
        r4 = "stride";
        r18 = r24 + 32;
        r0 = r18;
        r8.setInteger(r4, r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = "slice-height";
        r0 = r23;
        r8.setInteger(r4, r0);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
    L_0x0324:
        r4 = "video/avc";
        r12 = android.media.MediaCodec.createEncoderByType(r4);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = 0;
        r18 = 0;
        r19 = 1;
        r0 = r18;
        r1 = r19;
        r12.configure(r8, r4, r0, r1);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r8 = 18;
        if (r4 < r8) goto L_0x0984;
    L_0x033d:
        r8 = new com.hanista.mobogram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r4 = r12.createInputSurface();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r8.<init>(r4);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r8.makeCurrent();	 Catch:{ Exception -> 0x0947, all -> 0x04b0 }
        r33 = r8;
    L_0x034b:
        r12.start();	 Catch:{ Exception -> 0x094d, all -> 0x04b0 }
        r4 = "mime";
        r4 = r10.getString(r4);	 Catch:{ Exception -> 0x094d, all -> 0x04b0 }
        r4 = android.media.MediaCodec.createDecoderByType(r4);	 Catch:{ Exception -> 0x094d, all -> 0x04b0 }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x04f1, all -> 0x04b0 }
        r8 = 18;
        if (r7 < r8) goto L_0x04e4;
    L_0x035f:
        r32 = new com.hanista.mobogram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x04f1, all -> 0x04b0 }
        r32.<init>();	 Catch:{ Exception -> 0x04f1, all -> 0x04b0 }
    L_0x0364:
        r5 = r32.getSurface();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = 0;
        r7 = 0;
        r4.configure(r10, r5, r6, r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r4.start();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = 0;
        r37 = 0;
        r5 = 0;
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r8 = 21;
        if (r7 >= r8) goto L_0x097e;
    L_0x037a:
        r6 = r4.getInputBuffers();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r37 = r12.getOutputBuffers();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r8 = 18;
        if (r7 >= r8) goto L_0x0978;
    L_0x0388:
        r5 = r12.getInputBuffers();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r40 = r5;
        r41 = r6;
    L_0x0390:
        r54.m49R();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x0393:
        if (r39 != 0) goto L_0x088d;
    L_0x0395:
        r54.m49R();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r13 != 0) goto L_0x0975;
    L_0x039a:
        r18 = 0;
        r5 = r34.getSampleTrackIndex();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r52;
        if (r5 != r0) goto L_0x050e;
    L_0x03a4:
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = r4.dequeueInputBuffer(r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r5 < 0) goto L_0x050b;
    L_0x03ac:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = 21;
        if (r6 >= r7) goto L_0x04f9;
    L_0x03b2:
        r6 = r41[r5];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x03b4:
        r7 = 0;
        r0 = r34;
        r7 = r0.readSampleData(r6, r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r7 >= 0) goto L_0x04ff;
    L_0x03bd:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r10 = 4;
        r4.queueInputBuffer(r5, r6, r7, r8, r10);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5 = 1;
    L_0x03c6:
        r7 = r5;
        r5 = r18;
    L_0x03c9:
        if (r5 == 0) goto L_0x03dc;
    L_0x03cb:
        r8 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = r4.dequeueInputBuffer(r8);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r5 < 0) goto L_0x03dc;
    L_0x03d3:
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r10 = 4;
        r4.queueInputBuffer(r5, r6, r7, r8, r10);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = 1;
    L_0x03dc:
        if (r14 != 0) goto L_0x0515;
    L_0x03de:
        r5 = 1;
    L_0x03df:
        r36 = 1;
        r10 = r36;
        r19 = r5;
        r13 = r37;
        r6 = r14;
        r8 = r16;
        r14 = r39;
        r5 = r38;
    L_0x03ee:
        if (r19 != 0) goto L_0x03f2;
    L_0x03f0:
        if (r10 == 0) goto L_0x0881;
    L_0x03f2:
        r54.m49R();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r49;
        r1 = r16;
        r17 = r12.dequeueOutputBuffer(r0, r1);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = -1;
        r0 = r17;
        r1 = r16;
        if (r0 != r1) goto L_0x0518;
    L_0x0407:
        r10 = 0;
        r36 = r10;
        r37 = r13;
        r38 = r5;
        r39 = r14;
    L_0x0410:
        r5 = -1;
        r0 = r17;
        if (r0 == r5) goto L_0x06dc;
    L_0x0415:
        r10 = r36;
        r13 = r37;
        r5 = r38;
        r14 = r39;
        goto L_0x03ee;
    L_0x041e:
        r19 = "OMX.Intel.";
        r19 = r18.contains(r19);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r19 == 0) goto L_0x042a;
    L_0x0427:
        r4 = 2;
        goto L_0x023b;
    L_0x042a:
        r19 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r19 = r18.equals(r19);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r19 == 0) goto L_0x0436;
    L_0x0433:
        r4 = 3;
        goto L_0x023b;
    L_0x0436:
        r19 = "OMX.SEC.AVC.Encoder";
        r19 = r18.equals(r19);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r19 == 0) goto L_0x0443;
    L_0x043f:
        r4 = 4;
        r11 = 1;
        goto L_0x023b;
    L_0x0443:
        r19 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r18 = r18.equals(r19);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r18 == 0) goto L_0x023b;
    L_0x044c:
        r4 = 5;
        goto L_0x023b;
    L_0x044f:
        r8 = 2130708361; // 0x7f000789 float:1.701803E38 double:1.0527098025E-314;
        r44 = r8;
        r8 = r4;
        goto L_0x027e;
    L_0x0457:
        r18 = 1;
        r0 = r18;
        if (r8 != r0) goto L_0x0478;
    L_0x045d:
        r8 = r10.toLowerCase();	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        r10 = "lge";
        r8 = r8.equals(r10);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r8 != 0) goto L_0x0988;
    L_0x046a:
        r4 = r24 * r23;
        r4 = r4 + 2047;
        r4 = r4 & -2048;
        r8 = r24 * r23;
        r4 = r4 - r8;
        r15 = r15 + r4;
        r43 = r4;
        goto L_0x02ba;
    L_0x0478:
        r18 = 5;
        r0 = r18;
        if (r8 != r0) goto L_0x0482;
    L_0x047e:
        r43 = r4;
        goto L_0x02ba;
    L_0x0482:
        r18 = 3;
        r0 = r18;
        if (r8 != r0) goto L_0x0988;
    L_0x0488:
        r8 = "baidu";
        r8 = r10.equals(r8);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        if (r8 == 0) goto L_0x0988;
    L_0x0491:
        r4 = r23 % 16;
        r4 = 16 - r4;
        r4 = r4 + r23;
        r4 = r4 - r23;
        r4 = r4 * r24;
        r8 = r4 * 5;
        r8 = r8 / 4;
        r15 = r15 + r8;
        r43 = r4;
        goto L_0x02ba;
    L_0x04a4:
        r18 = 0;
        r4 = 0;
        r0 = r34;
        r1 = r18;
        r0.seekTo(r1, r4);	 Catch:{ Exception -> 0x0175, all -> 0x04b0 }
        goto L_0x02cf;
    L_0x04b0:
        r4 = move-exception;
    L_0x04b1:
        if (r34 == 0) goto L_0x04b6;
    L_0x04b3:
        r34.release();
    L_0x04b6:
        if (r35 == 0) goto L_0x04be;
    L_0x04b8:
        r5 = 0;
        r0 = r35;
        r0.finishMovie(r5);	 Catch:{ Exception -> 0x0902 }
    L_0x04be:
        r5 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "time = ";
        r6 = r6.append(r7);
        r8 = java.lang.System.currentTimeMillis();
        r8 = r8 - r50;
        r6 = r6.append(r8);
        r6 = r6.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r5, r6);
        throw r4;
    L_0x04df:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x02f1;
    L_0x04e4:
        r32 = new com.hanista.mobogram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x04f1, all -> 0x04b0 }
        r0 = r32;
        r1 = r24;
        r2 = r23;
        r0.<init>(r1, r2, r5);	 Catch:{ Exception -> 0x04f1, all -> 0x04b0 }
        goto L_0x0364;
    L_0x04f1:
        r5 = move-exception;
        r7 = r4;
        r4 = r5;
        r5 = r6;
        r6 = r33;
        goto L_0x0179;
    L_0x04f9:
        r6 = r4.getInputBuffer(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        goto L_0x03b4;
    L_0x04ff:
        r6 = 0;
        r8 = r34.getSampleTime();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r10 = 0;
        r4.queueInputBuffer(r5, r6, r7, r8, r10);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r34.advance();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x050b:
        r5 = r13;
        goto L_0x03c6;
    L_0x050e:
        r6 = -1;
        if (r5 != r6) goto L_0x0970;
    L_0x0511:
        r5 = 1;
        r7 = r13;
        goto L_0x03c9;
    L_0x0515:
        r5 = 0;
        goto L_0x03df;
    L_0x0518:
        r16 = -3;
        r0 = r17;
        r1 = r16;
        if (r0 != r1) goto L_0x0538;
    L_0x0520:
        r16 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r18 = 21;
        r0 = r16;
        r1 = r18;
        if (r0 >= r1) goto L_0x0600;
    L_0x052a:
        r13 = r12.getOutputBuffers();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r36 = r10;
        r37 = r13;
        r38 = r5;
        r39 = r14;
        goto L_0x0410;
    L_0x0538:
        r16 = -2;
        r0 = r17;
        r1 = r16;
        if (r0 != r1) goto L_0x055d;
    L_0x0540:
        r16 = r12.getOutputFormat();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r18 = -5;
        r0 = r18;
        if (r5 != r0) goto L_0x0553;
    L_0x054a:
        r5 = 0;
        r0 = r35;
        r1 = r16;
        r5 = r0.addTrack(r1, r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x0553:
        r36 = r10;
        r37 = r13;
        r38 = r5;
        r39 = r14;
        goto L_0x0410;
    L_0x055d:
        if (r17 >= 0) goto L_0x0584;
    L_0x055f:
        r5 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6.<init>();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r6 = r6.append(r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r17;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5.<init>(r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        throw r5;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x057b:
        r5 = move-exception;
        r6 = r33;
        r7 = r4;
        r4 = r5;
        r5 = r32;
        goto L_0x0179;
    L_0x0584:
        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = 21;
        r0 = r16;
        if (r14 >= r0) goto L_0x05b3;
    L_0x058c:
        r14 = r13[r17];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x058e:
        if (r14 != 0) goto L_0x05ba;
    L_0x0590:
        r5 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6.<init>();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = "encoderOutputBuffer ";
        r6 = r6.append(r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r17;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = " was null";
        r6 = r6.append(r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5.<init>(r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        throw r5;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x05b3:
        r0 = r17;
        r14 = r12.getOutputBuffer(r0);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        goto L_0x058e;
    L_0x05ba:
        r0 = r49;
        r0 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r18 = 1;
        r0 = r16;
        r1 = r18;
        if (r0 <= r1) goto L_0x05ee;
    L_0x05c8:
        r0 = r49;
        r0 = r0.flags;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r16 = r16 & 2;
        if (r16 != 0) goto L_0x060a;
    L_0x05d2:
        r16 = 0;
        r0 = r35;
        r1 = r49;
        r2 = r16;
        r14 = r0.writeSampleData(r5, r14, r1, r2);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r14 == 0) goto L_0x05ee;
    L_0x05e0:
        r14 = 0;
        r16 = 0;
        r0 = r54;
        r1 = r55;
        r2 = r45;
        r3 = r16;
        r0.m82a(r1, r2, r14, r3);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x05ee:
        r0 = r49;
        r14 = r0.flags;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r14 = r14 & 4;
        if (r14 == 0) goto L_0x06d9;
    L_0x05f6:
        r14 = 1;
    L_0x05f7:
        r16 = 0;
        r0 = r17;
        r1 = r16;
        r12.releaseOutputBuffer(r0, r1);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x0600:
        r36 = r10;
        r37 = r13;
        r38 = r5;
        r39 = r14;
        goto L_0x0410;
    L_0x060a:
        r16 = -5;
        r0 = r16;
        if (r5 != r0) goto L_0x05ee;
    L_0x0610:
        r0 = r49;
        r5 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = new byte[r5];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r18 = r0;
        r0 = r49;
        r5 = r0.offset;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r49;
        r0 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r5 = r5 + r16;
        r14.limit(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r49;
        r5 = r0.offset;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r14.position(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r18;
        r14.get(r0);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r14 = 0;
        r5 = 0;
        r0 = r49;
        r0 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r16 = r16 + -1;
    L_0x063d:
        if (r16 < 0) goto L_0x06a7;
    L_0x063f:
        r20 = 3;
        r0 = r16;
        r1 = r20;
        if (r0 <= r1) goto L_0x06a7;
    L_0x0647:
        r20 = r18[r16];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r21 = 1;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x06d5;
    L_0x0651:
        r20 = r16 + -1;
        r20 = r18[r20];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r20 != 0) goto L_0x06d5;
    L_0x0657:
        r20 = r16 + -2;
        r20 = r18[r20];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r20 != 0) goto L_0x06d5;
    L_0x065d:
        r20 = r16 + -3;
        r20 = r18[r20];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r20 != 0) goto L_0x06d5;
    L_0x0663:
        r5 = r16 + -3;
        r14 = java.nio.ByteBuffer.allocate(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r49;
        r5 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r20 = r16 + -3;
        r5 = r5 - r20;
        r5 = java.nio.ByteBuffer.allocate(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r20 = 0;
        r21 = r16 + -3;
        r0 = r18;
        r1 = r20;
        r2 = r21;
        r20 = r14.put(r0, r1, r2);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r21 = 0;
        r20.position(r21);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r20 = r16 + -3;
        r0 = r49;
        r0 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r21 = r0;
        r16 = r16 + -3;
        r16 = r21 - r16;
        r0 = r18;
        r1 = r20;
        r2 = r16;
        r16 = r5.put(r0, r1, r2);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r18 = 0;
        r0 = r16;
        r1 = r18;
        r0.position(r1);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x06a7:
        r16 = "video/avc";
        r0 = r16;
        r1 = r24;
        r2 = r23;
        r16 = android.media.MediaFormat.createVideoFormat(r0, r1, r2);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r14 == 0) goto L_0x06ca;
    L_0x06b6:
        if (r5 == 0) goto L_0x06ca;
    L_0x06b8:
        r18 = "csd-0";
        r0 = r16;
        r1 = r18;
        r0.setByteBuffer(r1, r14);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r14 = "csd-1";
        r0 = r16;
        r0.setByteBuffer(r14, r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x06ca:
        r5 = 0;
        r0 = r35;
        r1 = r16;
        r5 = r0.addTrack(r1, r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        goto L_0x05ee;
    L_0x06d5:
        r16 = r16 + -1;
        goto L_0x063d;
    L_0x06d9:
        r14 = 0;
        goto L_0x05f7;
    L_0x06dc:
        if (r6 != 0) goto L_0x096c;
    L_0x06de:
        r16 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r0 = r49;
        r1 = r16;
        r10 = r4.dequeueOutputBuffer(r0, r1);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5 = -1;
        if (r10 != r5) goto L_0x06f8;
    L_0x06eb:
        r5 = 0;
    L_0x06ec:
        r10 = r36;
        r19 = r5;
        r13 = r37;
        r14 = r39;
        r5 = r38;
        goto L_0x03ee;
    L_0x06f8:
        r5 = -3;
        if (r10 != r5) goto L_0x06fe;
    L_0x06fb:
        r5 = r19;
        goto L_0x06ec;
    L_0x06fe:
        r5 = -2;
        if (r10 != r5) goto L_0x0722;
    L_0x0701:
        r5 = r4.getOutputFormat();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r10 = "tmessages";
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r13.<init>();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r14 = "newFormat = ";
        r13 = r13.append(r14);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5 = r13.append(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        com.hanista.mobogram.messenger.FileLog.m16e(r10, r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5 = r19;
        goto L_0x06ec;
    L_0x0722:
        if (r10 >= 0) goto L_0x073e;
    L_0x0724:
        r5 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6.<init>();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r6 = r6.append(r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = r6.append(r10);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5.<init>(r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        throw r5;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x073e:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r13 = 18;
        if (r5 < r13) goto L_0x07fe;
    L_0x0744:
        r0 = r49;
        r5 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r5 == 0) goto L_0x07fb;
    L_0x074a:
        r5 = 1;
    L_0x074b:
        r16 = 0;
        r13 = (r46 > r16 ? 1 : (r46 == r16 ? 0 : -1));
        if (r13 <= 0) goto L_0x0768;
    L_0x0751:
        r0 = r49;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r13 = (r16 > r46 ? 1 : (r16 == r46 ? 0 : -1));
        if (r13 < 0) goto L_0x0768;
    L_0x075b:
        r7 = 1;
        r6 = 1;
        r5 = 0;
        r0 = r49;
        r13 = r0.flags;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r13 = r13 | 4;
        r0 = r49;
        r0.flags = r13;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x0768:
        r25 = r6;
        r26 = r7;
        r6 = 0;
        r6 = (r30 > r6 ? 1 : (r30 == r6 ? 0 : -1));
        if (r6 <= 0) goto L_0x0968;
    L_0x0772:
        r6 = -1;
        r6 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r6 != 0) goto L_0x0968;
    L_0x0778:
        r0 = r49;
        r6 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = (r6 > r30 ? 1 : (r6 == r30 ? 0 : -1));
        if (r6 >= 0) goto L_0x0816;
    L_0x0780:
        r5 = 0;
        r6 = "tmessages";
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7.<init>();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r13 = "drop frame startTime = ";
        r7 = r7.append(r13);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r30;
        r7 = r7.append(r0);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r13 = " present time = ";
        r7 = r7.append(r13);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r49;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r0 = r16;
        r7 = r7.append(r0);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        com.hanista.mobogram.messenger.FileLog.m16e(r6, r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r28 = r8;
    L_0x07b2:
        r4.releaseOutputBuffer(r10, r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r5 == 0) goto L_0x07d8;
    L_0x07b7:
        r5 = 0;
        r32.awaitNewImage();	 Catch:{ Exception -> 0x081d, all -> 0x04b0 }
    L_0x07bb:
        if (r5 != 0) goto L_0x07d8;
    L_0x07bd:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = 18;
        if (r5 < r6) goto L_0x0826;
    L_0x07c3:
        r5 = 0;
        r0 = r32;
        r0.drawImage(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r0 = r49;
        r6 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r6 * r8;
        r0 = r33;
        r0.setPresentationTime(r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r33.swapBuffers();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x07d8:
        r0 = r49;
        r5 = r0.flags;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5 = r5 & 4;
        if (r5 == 0) goto L_0x095e;
    L_0x07e0:
        r5 = 0;
        r6 = "tmessages";
        r7 = "decoder stream end";
        com.hanista.mobogram.messenger.FileLog.m16e(r6, r7);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = 18;
        if (r6 < r7) goto L_0x0860;
    L_0x07f0:
        r12.signalEndOfInputStream();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = r25;
        r7 = r26;
        r8 = r28;
        goto L_0x06ec;
    L_0x07fb:
        r5 = 0;
        goto L_0x074b;
    L_0x07fe:
        r0 = r49;
        r5 = r0.size;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r5 != 0) goto L_0x0810;
    L_0x0804:
        r0 = r49;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r20 = 0;
        r5 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1));
        if (r5 == 0) goto L_0x0813;
    L_0x0810:
        r5 = 1;
        goto L_0x074b;
    L_0x0813:
        r5 = 0;
        goto L_0x074b;
    L_0x0816:
        r0 = r49;
        r8 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r28 = r8;
        goto L_0x07b2;
    L_0x081d:
        r6 = move-exception;
        r5 = 1;
        r7 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r7, r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        goto L_0x07bb;
    L_0x0826:
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r13 = r12.dequeueInputBuffer(r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r13 < 0) goto L_0x0855;
    L_0x082e:
        r5 = 1;
        r0 = r32;
        r0.drawImage(r5);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r5 = r32.getFrame();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6 = r40[r13];	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r6.clear();	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r7 = r44;
        r8 = r24;
        r9 = r23;
        r10 = r43;
        com.hanista.mobogram.messenger.Utilities.convertVideoFrame(r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r14 = 0;
        r0 = r49;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r16 = r0;
        r18 = 0;
        r12.queueInputBuffer(r13, r14, r15, r16, r18);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        goto L_0x07d8;
    L_0x0855:
        r5 = "tmessages";
        r6 = "input buffer not available";
        com.hanista.mobogram.messenger.FileLog.m16e(r5, r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        goto L_0x07d8;
    L_0x0860:
        r6 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r17 = r12.dequeueInputBuffer(r6);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        if (r17 < 0) goto L_0x0879;
    L_0x0868:
        r18 = 0;
        r19 = 1;
        r0 = r49;
        r0 = r0.presentationTimeUs;	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
        r20 = r0;
        r22 = 4;
        r16 = r12;
        r16.queueInputBuffer(r17, r18, r19, r20, r22);	 Catch:{ Exception -> 0x057b, all -> 0x04b0 }
    L_0x0879:
        r6 = r25;
        r7 = r26;
        r8 = r28;
        goto L_0x06ec;
    L_0x0881:
        r37 = r13;
        r38 = r5;
        r39 = r14;
        r16 = r8;
        r14 = r6;
        r13 = r7;
        goto L_0x0393;
    L_0x088d:
        r6 = -1;
        r5 = (r16 > r6 ? 1 : (r16 == r6 ? 0 : -1));
        if (r5 == 0) goto L_0x095a;
    L_0x0893:
        r6 = r16;
    L_0x0895:
        r30 = r6;
        r7 = r4;
        r4 = r27;
        goto L_0x0184;
    L_0x089c:
        r15 = 0;
        r5 = r54;
        r6 = r55;
        r7 = r34;
        r8 = r35;
        r9 = r49;
        r10 = r30;
        r12 = r46;
        r14 = r45;
        r10 = r5.m65a(r6, r7, r8, r9, r10, r12, r14, r15);	 Catch:{ Exception -> 0x0940, all -> 0x04b0 }
        r4 = -1;
        r4 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1));
        if (r4 == 0) goto L_0x0954;
    L_0x08b7:
        r4 = r27;
        goto L_0x01aa;
    L_0x08bb:
        r5 = move-exception;
        r6 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r6, r5);
        goto L_0x01d0;
    L_0x08c4:
        r4 = move-exception;
        r5 = r4;
    L_0x08c6:
        r4 = 1;
        r8 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r8, r5);	 Catch:{ all -> 0x0934 }
        if (r6 == 0) goto L_0x08d2;
    L_0x08cf:
        r6.release();
    L_0x08d2:
        if (r7 == 0) goto L_0x08d8;
    L_0x08d4:
        r5 = 0;
        r7.finishMovie(r5);	 Catch:{ Exception -> 0x08fa }
    L_0x08d8:
        r5 = "tmessages";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "time = ";
        r6 = r6.append(r7);
        r8 = java.lang.System.currentTimeMillis();
        r8 = r8 - r50;
        r6 = r6.append(r8);
        r6 = r6.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r5, r6);
        goto L_0x01f0;
    L_0x08fa:
        r5 = move-exception;
        r6 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r6, r5);
        goto L_0x08d8;
    L_0x0902:
        r5 = move-exception;
        r6 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r6, r5);
        goto L_0x04be;
    L_0x090b:
        r4 = r48.edit();
        r5 = "isPreviousOk";
        r6 = 1;
        r4 = r4.putBoolean(r5, r6);
        r4.commit();
        r4 = 1;
        r5 = 1;
        r0 = r54;
        r1 = r55;
        r2 = r45;
        r0.m82a(r1, r2, r4, r5);
        r4 = 0;
        goto L_0x00ae;
    L_0x0928:
        r4 = move-exception;
        r34 = r6;
        r35 = r7;
        goto L_0x04b1;
    L_0x092f:
        r4 = move-exception;
        r34 = r6;
        goto L_0x04b1;
    L_0x0934:
        r4 = move-exception;
        r34 = r6;
        r35 = r7;
        goto L_0x04b1;
    L_0x093b:
        r4 = move-exception;
        r5 = r4;
        r7 = r35;
        goto L_0x08c6;
    L_0x0940:
        r4 = move-exception;
        r5 = r4;
        r6 = r34;
        r7 = r35;
        goto L_0x08c6;
    L_0x0947:
        r4 = move-exception;
        r5 = r6;
        r7 = r9;
        r6 = r8;
        goto L_0x0179;
    L_0x094d:
        r4 = move-exception;
        r5 = r6;
        r7 = r9;
        r6 = r33;
        goto L_0x0179;
    L_0x0954:
        r10 = r30;
        r4 = r27;
        goto L_0x01aa;
    L_0x095a:
        r6 = r30;
        goto L_0x0895;
    L_0x095e:
        r5 = r19;
        r6 = r25;
        r7 = r26;
        r8 = r28;
        goto L_0x06ec;
    L_0x0968:
        r28 = r8;
        goto L_0x07b2;
    L_0x096c:
        r5 = r19;
        goto L_0x06ec;
    L_0x0970:
        r5 = r18;
        r7 = r13;
        goto L_0x03c9;
    L_0x0975:
        r7 = r13;
        goto L_0x03dc;
    L_0x0978:
        r40 = r5;
        r41 = r6;
        goto L_0x0390;
    L_0x097e:
        r40 = r5;
        r41 = r6;
        goto L_0x0390;
    L_0x0984:
        r33 = r7;
        goto L_0x034b;
    L_0x0988:
        r43 = r4;
        goto L_0x02ba;
    L_0x098c:
        r4 = r27;
        goto L_0x01a8;
    L_0x0990:
        r23 = r7;
        r24 = r6;
        r53 = r4;
        r4 = r5;
        r5 = r53;
        goto L_0x0063;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MediaController.j(com.hanista.mobogram.messenger.MessageObject):boolean");
    }

    private native int openOpusFile(String str);

    private native void readOpusFile(ByteBuffer byteBuffer, int i, int[] iArr);

    private native int seekOpusFile(float f);

    private native int startRecord(String str);

    private native void stopRecord();

    private native int writeFrame(ByteBuffer byteBuffer, int i);

    public boolean m139A() {
        return this.ad;
    }

    public boolean m140B() {
        return this.ae;
    }

    public boolean m141C() {
        return this.af;
    }

    public boolean m142D() {
        return this.ag;
    }

    public boolean m143E() {
        return this.ah;
    }

    public long m144F() {
        return this.aR;
    }

    public void m145a(int i, ArrayList<DownloadObject> arrayList) {
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = i == 1 ? this.f53W : i == 2 ? this.f54X : i == 4 ? this.ab : i == 8 ? this.f55Y : i == 16 ? this.f56Z : i == 32 ? this.aa : null;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Object attachFileName;
                DownloadObject downloadObject = (DownloadObject) arrayList.get(i2);
                if (downloadObject.object instanceof Document) {
                    attachFileName = FileLoader.getAttachFileName((Document) downloadObject.object);
                } else {
                    String attachFileName2 = FileLoader.getAttachFileName(downloadObject.object);
                }
                if (!this.ac.containsKey(attachFileName)) {
                    boolean z;
                    if (downloadObject.object instanceof PhotoSize) {
                        FileLoader.getInstance().loadFile((PhotoSize) downloadObject.object, null, false);
                        z = true;
                    } else if (downloadObject.object instanceof Document) {
                        FileLoader.getInstance().loadFile((Document) downloadObject.object, false, false);
                        z = true;
                    } else {
                        z = false;
                    }
                    if (z) {
                        arrayList2.add(downloadObject);
                        this.ac.put(attachFileName, downloadObject);
                    }
                }
            }
        }
    }

    public void m146a(long j, long j2, EncryptedChat encryptedChat, ArrayList<Long> arrayList) {
        this.bn = j;
        this.bo = j2;
        this.bq = encryptedChat;
        this.br = arrayList;
    }

    public void m147a(long j, MessageObject messageObject) {
        Object obj = null;
        if (!(this.ax == null || !m172d(this.ax) || m191s())) {
            obj = 1;
            m166b(this.ax);
        }
        Object obj2 = obj;
        try {
            ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        DispatchQueue dispatchQueue = this.bi;
        Runnable anonymousClass17 = new AnonymousClass17(j, messageObject);
        this.bh = anonymousClass17;
        dispatchQueue.postRunnable(anonymousClass17, obj2 != null ? 500 : 50);
    }

    public void m148a(Uri uri) {
        try {
            Point realScreenSize = AndroidUtilities.getRealScreenSize();
            Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(uri, this.bu, null, null, "date_added DESC LIMIT 1");
            ArrayList arrayList = new ArrayList();
            if (query != null) {
                while (query.moveToNext()) {
                    int i;
                    int i2;
                    String str = TtmlNode.ANONYMOUS_REGION_ID;
                    String string = query.getString(0);
                    String string2 = query.getString(1);
                    String string3 = query.getString(2);
                    long j = query.getLong(3);
                    String string4 = query.getString(4);
                    if (VERSION.SDK_INT >= 16) {
                        i = query.getInt(5);
                        i2 = query.getInt(6);
                    } else {
                        i2 = 0;
                        i = 0;
                    }
                    if ((string != null && string.toLowerCase().contains("screenshot")) || ((string2 != null && string2.toLowerCase().contains("screenshot")) || ((string3 != null && string3.toLowerCase().contains("screenshot")) || (string4 != null && string4.toLowerCase().contains("screenshot"))))) {
                        if (i == 0 || r0 == 0) {
                            try {
                                Options options = new Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(string, options);
                                i = options.outWidth;
                                i2 = options.outHeight;
                            } catch (Exception e) {
                                arrayList.add(Long.valueOf(j));
                            }
                        }
                        if (i <= 0 || r0 <= 0 || ((i == realScreenSize.x && r0 == realScreenSize.y) || (r0 == realScreenSize.x && i == realScreenSize.y))) {
                            arrayList.add(Long.valueOf(j));
                        }
                    }
                }
                query.close();
            }
            if (!arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new C04786(arrayList));
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
    }

    public void m149a(FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.ap) {
            this.ar.add(fileDownloadProgressListener);
            return;
        }
        String str = (String) this.ao.get(Integer.valueOf(fileDownloadProgressListener.getObserverTag()));
        if (str != null) {
            ArrayList arrayList = (ArrayList) this.am.get(str);
            if (arrayList != null) {
                int i = 0;
                while (i < arrayList.size()) {
                    WeakReference weakReference = (WeakReference) arrayList.get(i);
                    if (weakReference.get() == null || weakReference.get() == fileDownloadProgressListener) {
                        arrayList.remove(i);
                        i--;
                    }
                    i++;
                }
                if (arrayList.isEmpty()) {
                    this.am.remove(str);
                }
            }
            this.ao.remove(Integer.valueOf(fileDownloadProgressListener.getObserverTag()));
        }
    }

    public void m150a(ChatActivity chatActivity) {
        if (chatActivity == null) {
            return;
        }
        if ((this.f65m != null || (this.f67o != null && this.f39I != null)) && this.f64l != null) {
            this.f69q = chatActivity;
            if ((this.af || (this.ax != null && this.ax.isVoice())) && !this.f35E) {
                float[] fArr = this.f37G;
                float[] fArr2 = this.f37G;
                this.f37G[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr[0] = 0.0f;
                fArr = this.f39I;
                fArr2 = this.f39I;
                this.f39I[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr[0] = 0.0f;
                fArr = this.f38H;
                fArr2 = this.f38H;
                this.f38H[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr[0] = 0.0f;
                this.f75w = 0;
                this.f36F = 0.0f;
                this.f71s = 0;
                this.f73u = 0;
                this.f72t = 0;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public void run() {
                        if (MediaController.this.f67o != null) {
                            MediaController.this.f62j.registerListener(MediaController.this, MediaController.this.f67o, DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
                        }
                        if (MediaController.this.f66n != null) {
                            MediaController.this.f62j.registerListener(MediaController.this, MediaController.this.f66n, DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
                        }
                        if (MediaController.this.f65m != null) {
                            MediaController.this.f62j.registerListener(MediaController.this, MediaController.this.f65m, DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
                        }
                        MediaController.this.f62j.registerListener(MediaController.this, MediaController.this.f64l, 3);
                    }
                });
                this.f35E = true;
            }
        }
    }

    public void m151a(String str, FileDownloadProgressListener fileDownloadProgressListener) {
        m152a(str, null, fileDownloadProgressListener);
    }

    public void m152a(String str, MessageObject messageObject, FileDownloadProgressListener fileDownloadProgressListener) {
        if (this.ap) {
            this.aq.put(str, fileDownloadProgressListener);
            return;
        }
        m149a(fileDownloadProgressListener);
        ArrayList arrayList = (ArrayList) this.am.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.am.put(str, arrayList);
        }
        arrayList.add(new WeakReference(fileDownloadProgressListener));
        if (messageObject != null) {
            arrayList = (ArrayList) this.an.get(str);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.an.put(str, arrayList);
            }
            arrayList.add(messageObject);
        }
        this.ao.put(Integer.valueOf(fileDownloadProgressListener.getObserverTag()), str);
    }

    public void m153a(ArrayList<MessageObject> arrayList, boolean z) {
        this.f50T = arrayList;
        if (this.f50T != null) {
            this.f49S = z;
            this.f51U = new HashMap();
            for (int i = 0; i < this.f50T.size(); i++) {
                MessageObject messageObject = (MessageObject) this.f50T.get(i);
                this.f51U.put(Integer.valueOf(messageObject.getId()), messageObject);
            }
        }
    }

    public void m154a(boolean z) {
        this.f32B = z;
    }

    public void m155a(boolean z, boolean z2) {
        m156a(z, z2, false);
    }

    public void m156a(boolean z, boolean z2, boolean z3) {
        if (this.au != null) {
            try {
                this.au.reset();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                this.au.stop();
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            try {
                this.au.release();
                this.au = null;
            } catch (Throwable e22) {
                FileLog.m18e("tmessages", e22);
            }
        } else if (this.av != null) {
            synchronized (this.aZ) {
                try {
                    this.av.pause();
                    this.av.flush();
                } catch (Throwable e222) {
                    FileLog.m18e("tmessages", e222);
                }
                try {
                    this.av.release();
                    this.av = null;
                } catch (Throwable e2222) {
                    FileLog.m18e("tmessages", e2222);
                }
            }
        }
        m33J();
        this.aw = 0;
        this.aF = 0;
        this.at = false;
        if (this.ax != null) {
            if (this.aK) {
                FileLoader.getInstance().cancelLoadFile(this.ax.getDocument());
            }
            MessageObject messageObject = this.ax;
            this.ax.audioProgress = 0.0f;
            this.ax.audioProgressSec = 0;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, Integer.valueOf(this.ax.getId()), Integer.valueOf(0));
            this.ax = null;
            this.aK = false;
            if (z) {
                NotificationsController.getInstance().audioManager.abandonAudioFocus(this);
                this.f40J = 0;
                if (this.f50T != null) {
                    if (z3 && this.f50T.get(0) == messageObject) {
                        this.f50T.remove(0);
                        this.f51U.remove(Integer.valueOf(messageObject.getId()));
                        if (this.f50T.isEmpty()) {
                            this.f50T = null;
                            this.f51U = null;
                        }
                    } else {
                        this.f50T = null;
                        this.f51U = null;
                    }
                }
                if (this.f50T != null) {
                    m158a((MessageObject) this.f50T.get(0));
                } else {
                    if (messageObject.isVoice() && messageObject.getId() != 0) {
                        m181i();
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioDidReset, Integer.valueOf(messageObject.getId()), Boolean.valueOf(z2));
                }
            }
            if (z2) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
        }
        if (!this.f31A && !this.af) {
            ChatActivity chatActivity = this.f69q;
            m164b(this.f69q);
            this.f69q = chatActivity;
        }
    }

    public boolean m157a(int i) {
        return (m34K() & i) != 0;
    }

    public boolean m158a(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        if ((this.av == null && this.au == null) || this.ax == null || messageObject.getId() != this.ax.getId()) {
            File file;
            if (!messageObject.isOut() && messageObject.isContentUnread() && messageObject.messageOwner.to_id.channel_id == 0) {
                MessagesController.getInstance().markMessageContentAsRead(messageObject);
            }
            boolean z = !this.aL;
            if (this.ax != null) {
                z = false;
            }
            m155a(z, false);
            this.aL = false;
            if (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() <= 0) {
                file = null;
            } else {
                file = new File(messageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            File pathToMessage = file != null ? file : FileLoader.getPathToMessage(messageObject.messageOwner);
            if (pathToMessage == null || pathToMessage == file || pathToMessage.exists() || !messageObject.isMusic()) {
                this.aK = false;
                if (messageObject.isMusic()) {
                    m45P();
                }
                if (isOpusFile(pathToMessage.getAbsolutePath()) == 1) {
                    this.aG.clear();
                    this.aH.clear();
                    synchronized (this.aZ) {
                        try {
                            this.aC = 3;
                            Semaphore semaphore = new Semaphore(0);
                            Boolean[] boolArr = new Boolean[1];
                            this.aU.postRunnable(new AnonymousClass13(boolArr, pathToMessage, semaphore));
                            semaphore.acquire();
                            if (boolArr[0].booleanValue()) {
                                this.aA = getTotalPcmDuration();
                                this.av = new AudioTrack(this.f31A ? 0 : 3, 48000, 4, 2, this.ay, 1);
                                this.av.setStereoVolume(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                this.av.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
                                    public void onMarkerReached(AudioTrack audioTrack) {
                                        MediaController.this.m156a(true, true, true);
                                    }

                                    public void onPeriodicNotification(AudioTrack audioTrack) {
                                    }
                                });
                                this.av.play();
                            } else {
                                return false;
                            }
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                            if (this.av != null) {
                                this.av.release();
                                this.av = null;
                                this.at = false;
                                this.ax = null;
                                this.aK = false;
                            }
                            return false;
                        }
                    }
                } else {
                    try {
                        this.au = new MediaPlayer();
                        this.au.setAudioStreamType(this.f31A ? 0 : 3);
                        this.au.setDataSource(pathToMessage.getAbsolutePath());
                        this.au.setOnCompletionListener(new AnonymousClass15(messageObject));
                        this.au.prepare();
                        this.au.start();
                        if (messageObject.isVoice()) {
                            this.aM = null;
                            this.aG.clear();
                            this.aH.clear();
                        } else {
                            try {
                                this.aM = AudioInfo.getAudioInfo(pathToMessage);
                            } catch (Throwable e2) {
                                FileLog.m18e("tmessages", e2);
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m18e("tmessages", e22);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.audioPlayStateChanged;
                        Object[] objArr = new Object[1];
                        objArr[0] = Integer.valueOf(this.ax != null ? this.ax.getId() : 0);
                        instance.postNotificationName(i, objArr);
                        if (this.au == null) {
                            return false;
                        }
                        this.au.release();
                        this.au = null;
                        this.at = false;
                        this.ax = null;
                        this.aK = false;
                        return false;
                    }
                }
                m120i(messageObject);
                m31I();
                this.at = false;
                this.aw = 0;
                this.aB = 0;
                this.ax = messageObject;
                if (!this.af) {
                    m150a(this.f69q);
                }
                m117h(this.ax);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioDidStarted, messageObject);
                if (this.au != null) {
                    try {
                        if (this.ax.audioProgress != 0.0f) {
                            this.au.seekTo((int) (((float) this.au.getDuration()) * this.ax.audioProgress));
                        }
                    } catch (Throwable e222) {
                        this.ax.audioProgress = 0.0f;
                        this.ax.audioProgressSec = 0;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, Integer.valueOf(this.ax.getId()), Integer.valueOf(0));
                        FileLog.m18e("tmessages", e222);
                    }
                } else if (this.av != null) {
                    if (this.ax.audioProgress == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        this.ax.audioProgress = 0.0f;
                    }
                    this.aU.postRunnable(new Runnable() {
                        public void run() {
                            try {
                                if (!(MediaController.this.ax == null || MediaController.this.ax.audioProgress == 0.0f)) {
                                    MediaController.this.aB = (long) (((float) MediaController.this.aA) * MediaController.this.ax.audioProgress);
                                    MediaController.this.seekOpusFile(MediaController.this.ax.audioProgress);
                                }
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                            synchronized (MediaController.this.aY) {
                                MediaController.this.aX.addAll(MediaController.this.aW);
                                MediaController.this.aW.clear();
                            }
                            MediaController.this.az = false;
                            MediaController.this.m41N();
                        }
                    });
                }
                if (this.ax.isMusic()) {
                    ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                } else {
                    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
                }
                return true;
            }
            FileLoader.getInstance().loadFile(messageObject.getDocument(), false, false);
            this.aK = true;
            this.at = false;
            this.aw = 0;
            this.aB = 0;
            this.aM = null;
            this.ax = messageObject;
            if (this.ax.getDocument() != null) {
                ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            } else {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, Integer.valueOf(this.ax.getId()));
            return true;
        }
        if (this.at) {
            m169c(messageObject);
        }
        if (!this.af) {
            m150a(this.f69q);
        }
        return true;
    }

    public boolean m159a(MessageObject messageObject, float f) {
        if ((this.av == null && this.au == null) || messageObject == null || this.ax == null) {
            return false;
        }
        if (this.ax != null && this.ax.getId() != messageObject.getId()) {
            return false;
        }
        try {
            if (this.au != null) {
                int duration = (int) (((float) this.au.getDuration()) * f);
                this.au.seekTo(duration);
                this.aw = duration;
            } else if (this.av != null) {
                m93b(f);
            }
            return true;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }

    public boolean m160a(ArrayList<MessageObject> arrayList, MessageObject messageObject) {
        return m161a((ArrayList) arrayList, messageObject, true);
    }

    public boolean m161a(ArrayList<MessageObject> arrayList, MessageObject messageObject, boolean z) {
        boolean z2 = true;
        if (this.ax == messageObject) {
            return m158a(messageObject);
        }
        this.aJ = !z;
        if (this.aG.isEmpty()) {
            z2 = false;
        }
        this.aL = z2;
        this.aG.clear();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            MessageObject messageObject2 = (MessageObject) arrayList.get(size);
            if (messageObject2.isMusic()) {
                this.aG.add(messageObject2);
            }
        }
        this.aI = this.aG.indexOf(messageObject);
        if (this.aI == -1) {
            this.aG.clear();
            this.aH.clear();
            this.aI = this.aG.size();
            this.aG.add(messageObject);
        }
        if (messageObject.isMusic()) {
            if (this.ai) {
                m43O();
                this.aI = 0;
            }
            if (z) {
                SharedMediaQuery.loadMusic(messageObject.getDialogId(), ((MessageObject) this.aG.get(0)).getId());
            }
        }
        return m158a(messageObject);
    }

    public void m162b() {
        m155a(false, true);
        this.aM = null;
        this.aL = false;
        this.f53W.clear();
        this.f54X.clear();
        this.f55Y.clear();
        this.ab.clear();
        this.f56Z.clear();
        this.aa.clear();
        this.ac.clear();
        this.f44N.clear();
        this.aG.clear();
        this.aH.clear();
        this.f48R.clear();
        this.f61i.clear();
        this.f50T = null;
        this.f51U = null;
        m179g(null);
    }

    public void m163b(int i) {
        int K = m34K();
        if (!((K & 1) == 0 || (i & 1) == 0 || !this.f53W.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(1);
        }
        if (!((K & 2) == 0 || (i & 2) == 0 || !this.f54X.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(2);
        }
        if (!((K & 4) == 0 || (i & 4) == 0 || !this.ab.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(4);
        }
        if (!((K & 8) == 0 || (i & 8) == 0 || !this.f55Y.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(8);
        }
        if (!((K & 16) == 0 || (i & 16) == 0 || !this.f56Z.isEmpty())) {
            MessagesStorage.getInstance().getDownloadQueue(16);
        }
        if ((K & 32) != 0 && (i & 32) != 0 && this.aa.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(32);
        }
    }

    public void m164b(ChatActivity chatActivity) {
        if (this.f34D) {
            this.f34D = false;
        } else if (this.f35E && !this.f34D) {
            if ((this.f65m != null || (this.f67o != null && this.f39I != null)) && this.f64l != null && this.f69q == chatActivity) {
                this.f69q = null;
                m171d(0);
                this.f35E = false;
                this.f70r = false;
                this.f76x = false;
                this.f68p = false;
                this.f31A = false;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public void run() {
                        if (MediaController.this.f66n != null) {
                            MediaController.this.f62j.unregisterListener(MediaController.this, MediaController.this.f66n);
                        }
                        if (MediaController.this.f67o != null) {
                            MediaController.this.f62j.unregisterListener(MediaController.this, MediaController.this.f67o);
                        }
                        if (MediaController.this.f65m != null) {
                            MediaController.this.f62j.unregisterListener(MediaController.this, MediaController.this.f65m);
                        }
                        MediaController.this.f62j.unregisterListener(MediaController.this, MediaController.this.f64l);
                    }
                });
                if (this.f77y && this.f63k != null && this.f63k.isHeld()) {
                    this.f63k.release();
                }
            }
        }
    }

    public void m165b(boolean z) {
        this.f33C = z;
    }

    public boolean m166b(MessageObject messageObject) {
        if ((this.av == null && this.au == null) || messageObject == null || this.ax == null) {
            return false;
        }
        if (this.ax != null && this.ax.getId() != messageObject.getId()) {
            return false;
        }
        m33J();
        try {
            if (this.au != null) {
                this.au.pause();
            } else if (this.av != null) {
                this.av.pause();
            }
            this.at = true;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, Integer.valueOf(this.ax.getId()));
            return true;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            this.at = false;
            return false;
        }
    }

    protected int m167c() {
        int i = 0;
        if (!((this.f57b & 1) == 0 && (this.f58c & 1) == 0 && (this.f59d & 1) == 0)) {
            i = 1;
        }
        if (!((this.f57b & 2) == 0 && (this.f58c & 2) == 0 && (this.f59d & 2) == 0)) {
            i |= 2;
        }
        if (!((this.f57b & 4) == 0 && (this.f58c & 4) == 0 && (this.f59d & 4) == 0)) {
            i |= 4;
        }
        if (!((this.f57b & 8) == 0 && (this.f58c & 8) == 0 && (this.f59d & 8) == 0)) {
            i |= 8;
        }
        if (!((this.f57b & 16) == 0 && (this.f58c & 16) == 0 && (this.f59d & 16) == 0)) {
            i |= 16;
        }
        return ((this.f57b & 32) == 0 && (this.f58c & 32) == 0 && (this.f59d & 32) == 0) ? i : i | 32;
    }

    public void m168c(int i) {
        if (this.aI >= 0 && this.aI < this.aG.size()) {
            this.aI = i;
            this.aL = true;
            m158a((MessageObject) this.aG.get(this.aI));
        }
    }

    public boolean m169c(MessageObject messageObject) {
        if ((this.av == null && this.au == null) || messageObject == null || this.ax == null) {
            return false;
        }
        if (this.ax != null && this.ax.getId() != messageObject.getId()) {
            return false;
        }
        try {
            m117h(messageObject);
            if (this.au != null) {
                this.au.start();
            } else if (this.av != null) {
                this.av.play();
                m41N();
            }
            m120i(messageObject);
            this.at = false;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, Integer.valueOf(this.ax.getId()));
            return true;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }

    public void m170d() {
        int K = m34K();
        if (K != this.f52V) {
            int i;
            this.f52V = K;
            if ((K & 1) == 0) {
                for (i = 0; i < this.f53W.size(); i++) {
                    FileLoader.getInstance().cancelLoadFile((PhotoSize) ((DownloadObject) this.f53W.get(i)).object);
                }
                this.f53W.clear();
            } else if (this.f53W.isEmpty()) {
                m163b(1);
            }
            if ((K & 2) == 0) {
                for (i = 0; i < this.f54X.size(); i++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.f54X.get(i)).object);
                }
                this.f54X.clear();
            } else if (this.f54X.isEmpty()) {
                m163b(2);
            }
            if ((K & 8) == 0) {
                for (i = 0; i < this.f55Y.size(); i++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.f55Y.get(i)).object);
                }
                this.f55Y.clear();
            } else if (this.f55Y.isEmpty()) {
                m163b(8);
            }
            if ((K & 4) == 0) {
                for (i = 0; i < this.ab.size(); i++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.ab.get(i)).object);
                }
                this.ab.clear();
            } else if (this.ab.isEmpty()) {
                m163b(4);
            }
            if ((K & 16) == 0) {
                for (i = 0; i < this.f56Z.size(); i++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.f56Z.get(i)).object);
                }
                this.f56Z.clear();
            } else if (this.f56Z.isEmpty()) {
                m163b(16);
            }
            if ((K & 32) == 0) {
                for (i = 0; i < this.aa.size(); i++) {
                    FileLoader.getInstance().cancelLoadFile((Document) ((DownloadObject) this.aa.get(i)).object);
                }
                this.aa.clear();
            } else if (this.aa.isEmpty()) {
                m163b(32);
            }
            int c = m167c();
            if (c == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(0);
                return;
            }
            if ((c & 1) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(1);
            }
            if ((c & 2) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(2);
            }
            if ((c & 4) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(4);
            }
            if ((c & 8) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(8);
            }
            if ((c & 16) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(16);
            }
            if ((c & 32) == 0) {
                MessagesStorage.getInstance().clearDownloadQueue(32);
            }
        }
    }

    public void m171d(int i) {
        if (this.bh != null) {
            this.bi.cancelRunnable(this.bh);
            this.bh = null;
        }
        this.bi.postRunnable(new AnonymousClass20(i));
    }

    public boolean m172d(MessageObject messageObject) {
        return ((this.av == null && this.au == null) || messageObject == null || this.ax == null || (this.ax != null && (this.ax.getId() != messageObject.getId() || this.aK))) ? false : true;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int i2 = false;
        String str;
        ArrayList arrayList;
        int i3;
        WeakReference weakReference;
        if (i == NotificationCenter.FileDidFailedLoad || i == NotificationCenter.httpFileDidFailedLoad) {
            this.ap = true;
            str = (String) objArr[0];
            arrayList = (ArrayList) this.am.get(str);
            if (arrayList != null) {
                for (i3 = 0; i3 < arrayList.size(); i3++) {
                    weakReference = (WeakReference) arrayList.get(i3);
                    if (weakReference.get() != null) {
                        ((FileDownloadProgressListener) weakReference.get()).onFailedDownload(str);
                        this.ao.remove(Integer.valueOf(((FileDownloadProgressListener) weakReference.get()).getObserverTag()));
                    }
                }
                this.am.remove(str);
            }
            this.ap = false;
            m37L();
            m83a(str, ((Integer) objArr[1]).intValue());
        } else if (i == NotificationCenter.FileDidLoaded || i == NotificationCenter.httpFileDidLoaded) {
            this.ap = true;
            str = (String) objArr[0];
            if (this.aK && this.ax != null && FileLoader.getAttachFileName(this.ax.getDocument()).equals(str)) {
                this.aL = true;
                m158a(this.ax);
            }
            arrayList = (ArrayList) this.an.get(str);
            if (arrayList != null) {
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    ((MessageObject) arrayList.get(i4)).mediaExists = true;
                }
                this.an.remove(str);
            }
            arrayList = (ArrayList) this.am.get(str);
            if (arrayList != null) {
                for (i3 = 0; i3 < arrayList.size(); i3++) {
                    weakReference = (WeakReference) arrayList.get(i3);
                    if (weakReference.get() != null) {
                        ((FileDownloadProgressListener) weakReference.get()).onSuccessDownload(str);
                        this.ao.remove(Integer.valueOf(((FileDownloadProgressListener) weakReference.get()).getObserverTag()));
                    }
                }
                this.am.remove(str);
            }
            this.ap = false;
            m37L();
            m83a(str, 0);
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            this.ap = true;
            str = (String) objArr[0];
            arrayList = (ArrayList) this.am.get(str);
            if (arrayList != null) {
                r2 = (Float) objArr[1];
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    r1 = (WeakReference) it.next();
                    if (r1.get() != null) {
                        ((FileDownloadProgressListener) r1.get()).onProgressDownload(str, r2.floatValue());
                    }
                }
            }
            this.ap = false;
            m37L();
        } else if (i == NotificationCenter.FileUploadProgressChanged) {
            this.ap = true;
            str = (String) objArr[0];
            arrayList = (ArrayList) this.am.get(str);
            if (arrayList != null) {
                r2 = (Float) objArr[1];
                Boolean bool = (Boolean) objArr[2];
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    r1 = (WeakReference) it2.next();
                    if (r1.get() != null) {
                        ((FileDownloadProgressListener) r1.get()).onProgressUpload(str, r2.floatValue(), bool.booleanValue());
                    }
                }
            }
            this.ap = false;
            m37L();
            try {
                ArrayList delayedMessages = SendMessagesHelper.getInstance().getDelayedMessages(str);
                if (delayedMessages != null) {
                    while (i2 < delayedMessages.size()) {
                        DelayedMessage delayedMessage = (DelayedMessage) delayedMessages.get(i2);
                        if (delayedMessage.encryptedChat == null) {
                            long dialogId = delayedMessage.obj.getDialogId();
                            Long l = (Long) this.f61i.get(Long.valueOf(dialogId));
                            if (l == null || l.longValue() + 4000 < System.currentTimeMillis()) {
                                if (MessageObject.isVideoDocument(delayedMessage.documentLocation)) {
                                    MessagesController.getInstance().sendTyping(dialogId, 5, 0);
                                } else if (delayedMessage.documentLocation != null) {
                                    MessagesController.getInstance().sendTyping(dialogId, 3, 0);
                                } else if (delayedMessage.location != null) {
                                    MessagesController.getInstance().sendTyping(dialogId, 4, 0);
                                }
                                this.f61i.put(Long.valueOf(dialogId), Long.valueOf(System.currentTimeMillis()));
                            }
                        }
                        i2++;
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            int intValue = ((Integer) objArr[1]).intValue();
            r0 = (ArrayList) objArr[0];
            if (this.ax != null && intValue == this.ax.messageOwner.to_id.channel_id && r0.contains(Integer.valueOf(this.ax.getId()))) {
                m155a(true, true);
            }
            if (this.f50T != null && !this.f50T.isEmpty() && intValue == ((MessageObject) this.f50T.get(0)).messageOwner.to_id.channel_id) {
                while (i2 < r0.size()) {
                    r1 = (MessageObject) this.f51U.remove(r0.get(i2));
                    if (r1 != null) {
                        this.f50T.remove(r1);
                    }
                    i2++;
                }
            }
        } else if (i == NotificationCenter.removeAllMessagesFromDialog) {
            r0 = ((Long) objArr[0]).longValue();
            if (this.ax != null && this.ax.getDialogId() == r0) {
                m155a(false, true);
            }
        } else if (i == NotificationCenter.musicDidLoaded) {
            r0 = ((Long) objArr[0]).longValue();
            if (this.ax != null && this.ax.isMusic() && this.ax.getDialogId() == r0) {
                r0 = (ArrayList) objArr[1];
                this.aG.addAll(0, r0);
                if (this.ai) {
                    m43O();
                    this.aI = 0;
                    return;
                }
                this.aI = r0.size() + this.aI;
            }
        } else if (i == NotificationCenter.didReceivedNewMessages && this.f50T != null && !this.f50T.isEmpty()) {
            if (((Long) objArr[0]).longValue() == ((MessageObject) this.f50T.get(0)).getDialogId()) {
                r0 = (ArrayList) objArr[1];
                while (i2 < r0.size()) {
                    r1 = (MessageObject) r0.get(i2);
                    if (r1.isVoice() && (!this.f49S || (r1.isContentUnread() && !r1.isOut()))) {
                        this.f50T.add(r1);
                        this.f51U.put(Integer.valueOf(r1.getId()), r1);
                    }
                    i2++;
                }
            }
        }
    }

    public void m173e() {
        ApplicationLoader.applicationHandler.removeCallbacks(this.bt);
        this.bs++;
        try {
            if (this.bm == null) {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri = Media.EXTERNAL_CONTENT_URI;
                ContentObserver externalObserver = new ExternalObserver();
                this.bl = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            if (this.bl == null) {
                contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                uri = Media.INTERNAL_CONTENT_URI;
                externalObserver = new InternalObserver();
                this.bm = externalObserver;
                contentResolver.registerContentObserver(uri, false, externalObserver);
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
    }

    public void m174e(MessageObject messageObject) {
        String str = messageObject.getId() + "_" + messageObject.getDialogId();
        String absolutePath = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.f48R.containsKey(str)) {
            this.f48R.put(str, messageObject);
            Utilities.globalQueue.postRunnable(new AnonymousClass18(absolutePath, str));
        }
    }

    public void m175f() {
        if (this.bt == null) {
            this.bt = new StopMediaObserverRunnable();
        }
        this.bt.currentObserverToken = this.bs;
        ApplicationLoader.applicationHandler.postDelayed(this.bt, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    public void m176f(int i) {
        if ((i & 1) != 0 && this.f53W.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(1);
        }
        if ((i & 2) != 0 && this.f54X.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(2);
        }
        if ((i & 4) != 0 && this.ab.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(4);
        }
        if ((i & 8) != 0 && this.f55Y.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(8);
        }
        if ((i & 16) != 0 && this.f56Z.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(16);
        }
        if ((i & 32) != 0 && this.aa.isEmpty()) {
            MessagesStorage.getInstance().getDownloadQueue(32);
        }
    }

    public void m177f(MessageObject messageObject) {
        this.f44N.add(messageObject);
        if (this.f44N.size() == 1) {
            m47Q();
        }
    }

    public int m178g() {
        int i = this.as;
        this.as = i + 1;
        return i;
    }

    public void m179g(MessageObject messageObject) {
        if (messageObject == null) {
            synchronized (this.f60h) {
                this.f46P = true;
            }
        } else if (!this.f44N.isEmpty()) {
            if (this.f44N.get(0) == messageObject) {
                synchronized (this.f60h) {
                    this.f46P = true;
                }
            }
            this.f44N.remove(messageObject);
        }
    }

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    public boolean m180h() {
        return (this.bh == null && this.aO == null) ? false : true;
    }

    public void m181i() {
        if (this.f31A && this.f69q != null && this.f33C) {
            this.f68p = true;
            m147a(this.f69q.getDialogId(), null);
            this.f34D = true;
        }
    }

    public MessageObject m182j() {
        return this.ax;
    }

    public int m183k() {
        return this.aI;
    }

    public void m184l() {
        m108d(false);
    }

    public void m185m() {
        ArrayList arrayList = this.ai ? this.aH : this.aG;
        this.aI--;
        if (this.aI < 0) {
            this.aI = arrayList.size() - 1;
        }
        if (this.aI >= 0 && this.aI < arrayList.size()) {
            this.aL = true;
            m158a((MessageObject) arrayList.get(this.aI));
        }
    }

    public AudioInfo m186n() {
        return this.aM;
    }

    public boolean m187o() {
        return this.ai;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onAudioFocusChange(int i) {
        if (i == -1) {
            if (m172d(m182j()) && !m191s()) {
                m166b(m182j());
            }
            this.f40J = 0;
            this.f42L = 0;
        } else if (i == 1) {
            this.f42L = 2;
            if (this.f43M) {
                this.f43M = false;
                if (m172d(m182j()) && m191s()) {
                    m158a(m182j());
                }
            }
        } else if (i == -3) {
            this.f42L = 1;
        } else if (i == -2) {
            this.f42L = 0;
            if (m172d(m182j()) && !m191s()) {
                m166b(m182j());
                this.f43M = true;
            }
        }
        m31I();
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (this.f35E) {
            if (sensorEvent.sensor == this.f64l) {
                FileLog.m16e("tmessages", "proximity changed to " + sensorEvent.values[0]);
                if (this.f78z == -100.0f) {
                    this.f78z = sensorEvent.values[0];
                } else if (this.f78z != sensorEvent.values[0]) {
                    this.f77y = true;
                }
                if (this.f77y) {
                    this.f76x = m86a(sensorEvent.values[0]);
                }
            } else if (sensorEvent.sensor == this.f65m) {
                double d = this.f75w == 0 ? 0.9800000190734863d : 1.0d / (1.0d + (((double) (sensorEvent.timestamp - this.f75w)) / 1.0E9d));
                this.f75w = sensorEvent.timestamp;
                this.f37G[0] = (float) ((((double) this.f37G[0]) * d) + ((1.0d - d) * ((double) sensorEvent.values[0])));
                this.f37G[1] = (float) ((((double) this.f37G[1]) * d) + ((1.0d - d) * ((double) sensorEvent.values[1])));
                this.f37G[2] = (float) (((1.0d - d) * ((double) sensorEvent.values[2])) + (((double) this.f37G[2]) * d));
                this.f38H[0] = (DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD * this.f37G[0]) + (0.19999999f * sensorEvent.values[0]);
                this.f38H[1] = (DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD * this.f37G[1]) + (0.19999999f * sensorEvent.values[1]);
                this.f38H[2] = (DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD * this.f37G[2]) + (0.19999999f * sensorEvent.values[2]);
                this.f39I[0] = sensorEvent.values[0] - this.f37G[0];
                this.f39I[1] = sensorEvent.values[1] - this.f37G[1];
                this.f39I[2] = sensorEvent.values[2] - this.f37G[2];
            } else if (sensorEvent.sensor == this.f66n) {
                this.f39I[0] = sensorEvent.values[0];
                this.f39I[1] = sensorEvent.values[1];
                this.f39I[2] = sensorEvent.values[2];
            } else if (sensorEvent.sensor == this.f67o) {
                float[] fArr = this.f38H;
                float[] fArr2 = this.f37G;
                float f = sensorEvent.values[0];
                fArr2[0] = f;
                fArr[0] = f;
                fArr = this.f38H;
                fArr2 = this.f37G;
                f = sensorEvent.values[1];
                fArr2[1] = f;
                fArr[1] = f;
                fArr = this.f38H;
                fArr2 = this.f37G;
                f = sensorEvent.values[2];
                fArr2[2] = f;
                fArr[2] = f;
            }
            if (sensorEvent.sensor == this.f66n || sensorEvent.sensor == this.f67o || sensorEvent.sensor == this.f65m) {
                float f2 = ((this.f37G[0] * this.f39I[0]) + (this.f37G[1] * this.f39I[1])) + (this.f37G[2] * this.f39I[2]);
                if (this.f72t != 6) {
                    if (f2 <= 0.0f || this.f36F <= 0.0f) {
                        if (f2 < 0.0f && this.f36F < 0.0f) {
                            if (this.f71s != 6 || f2 >= -15.0f) {
                                if (f2 > -15.0f) {
                                    this.f73u++;
                                }
                                if (!(this.f73u != 10 && this.f71s == 6 && this.f72t == 0)) {
                                    this.f71s = 0;
                                    this.f72t = 0;
                                    this.f73u = 0;
                                }
                            } else if (this.f72t < 6) {
                                this.f72t++;
                                if (this.f72t == 6) {
                                    this.f71s = 0;
                                    this.f73u = 0;
                                    this.f74v = System.currentTimeMillis();
                                }
                            }
                        }
                    } else if (f2 <= 15.0f || this.f72t != 0) {
                        if (f2 < 15.0f) {
                            this.f73u++;
                        }
                        if (!(this.f73u != 10 && this.f71s == 6 && this.f72t == 0)) {
                            this.f72t = 0;
                            this.f71s = 0;
                            this.f73u = 0;
                        }
                    } else if (this.f71s < 6 && !this.f76x) {
                        this.f71s++;
                        if (this.f71s == 6) {
                            this.f73u = 0;
                        }
                    }
                }
                this.f36F = f2;
                boolean z = this.f38H[1] > 2.5f && Math.abs(this.f38H[2]) < 4.0f && Math.abs(this.f38H[0]) > 1.5f;
                this.f70r = z;
            }
            if (this.f72t == 6 && this.f70r && this.f76x && !NotificationsController.getInstance().audioManager.isWiredHeadsetOn()) {
                FileLog.m16e("tmessages", "sensor values reached");
                if (this.ax == null && this.bh == null && this.aO == null && !PhotoViewer.getInstance().isVisible() && ApplicationLoader.isScreenOn && !this.f32B && this.f33C && this.f69q != null && !this.f41K) {
                    if (!this.f68p) {
                        FileLog.m16e("tmessages", "start record");
                        this.f31A = true;
                        if (!this.f69q.playFirstUnreadVoiceMessage()) {
                            this.f68p = true;
                            this.f31A = false;
                            m147a(this.f69q.getDialogId(), null);
                        }
                        this.f34D = true;
                        if (!(!this.f77y || this.f63k == null || this.f63k.isHeld())) {
                            this.f63k.acquire();
                        }
                    }
                } else if (this.ax != null && this.ax.isVoice() && MoboConstants.f1321N && !this.f31A) {
                    FileLog.m16e("tmessages", "start listen");
                    if (!(!this.f77y || this.f63k == null || this.f63k.isHeld())) {
                        this.f63k.acquire();
                    }
                    this.f31A = true;
                    m102c(false);
                    this.f34D = true;
                }
                this.f72t = 0;
                this.f71s = 0;
                this.f73u = 0;
            } else if (this.f76x) {
                if (this.ax != null && this.ax.isVoice() && MoboConstants.f1321N && !this.f31A) {
                    FileLog.m16e("tmessages", "start listen by proximity only");
                    if (!(!this.f77y || this.f63k == null || this.f63k.isHeld())) {
                        this.f63k.acquire();
                    }
                    this.f31A = true;
                    m102c(false);
                    this.f34D = true;
                }
            } else if (!this.f76x) {
                if (this.f68p) {
                    FileLog.m16e("tmessages", "stop record");
                    m171d(2);
                    this.f68p = false;
                    this.f34D = false;
                    if (this.f77y && this.f63k != null && this.f63k.isHeld()) {
                        this.f63k.release();
                    }
                } else if (this.f31A) {
                    FileLog.m16e("tmessages", "stop listen");
                    this.f31A = false;
                    m102c(true);
                    this.f34D = false;
                    if (this.f77y && this.f63k != null && this.f63k.isHeld()) {
                        this.f63k.release();
                    }
                }
            }
            if (this.f74v != 0 && this.f72t == 6 && Math.abs(System.currentTimeMillis() - this.f74v) > 1000) {
                this.f72t = 0;
                this.f71s = 0;
                this.f73u = 0;
                this.f74v = 0;
            }
        }
    }

    public int m188p() {
        return this.aj;
    }

    public void m189q() {
        this.ai = !this.ai;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putBoolean("shuffleMusic", this.ai);
        edit.commit();
        if (this.ai) {
            m43O();
            this.aI = 0;
        } else if (this.ax != null) {
            this.aI = this.aG.indexOf(this.ax);
            if (this.aI == -1) {
                this.aG.clear();
                this.aH.clear();
                m155a(true, true);
            }
        }
    }

    public void m190r() {
        this.aj++;
        if (this.aj > 2) {
            this.aj = 0;
        }
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putInt("repeatMode", this.aj);
        edit.commit();
    }

    public boolean m191s() {
        return this.at || this.aK;
    }

    public boolean m192t() {
        return this.aK;
    }

    public void m193u() {
        this.ad = !this.ad;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putBoolean("save_gallery", this.ad);
        edit.commit();
        m198z();
    }

    public void m194v() {
        this.ae = !this.ae;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putBoolean("autoplay_gif", this.ae);
        edit.commit();
    }

    public void m195w() {
        this.af = !this.af;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putBoolean("raise_to_speak", this.af);
        edit.commit();
    }

    public void m196x() {
        this.ag = !this.ag;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putBoolean("custom_tabs", this.ag);
        edit.commit();
    }

    public void m197y() {
        this.ah = !this.ah;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putBoolean("direct_share", this.ah);
        edit.commit();
    }

    public void m198z() {
        try {
            File file = new File(MoboConstants.m1381b(), MoboConstants.f1325R);
            File file2 = new File(file, "Telegram Images");
            file2.mkdir();
            File file3 = new File(file, "Telegram Video");
            file3.mkdir();
            if (this.ad) {
                if (file2.isDirectory()) {
                    new File(file2, ".nomedia").delete();
                }
                if (file3.isDirectory()) {
                    new File(file3, ".nomedia").delete();
                    return;
                }
                return;
            }
            if (file2.isDirectory()) {
                new File(file2, ".nomedia").createNewFile();
            }
            if (file3.isDirectory()) {
                new File(file3, ".nomedia").createNewFile();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }
}
