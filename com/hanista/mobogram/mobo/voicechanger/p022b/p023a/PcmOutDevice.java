package com.hanista.mobogram.mobo.voicechanger.p022b.p023a;

import android.content.Context;
import android.media.AudioTrack;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.Utils;
import com.hanista.mobogram.mobo.voicechanger.p021a.HeadsetMode;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.FileOutputStream;
import java.io.IOException;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.b.a.c */
public final class PcmOutDevice extends PcmDevice {
    private AudioTrack f2570b;
    private FileOutputStream f2571c;

    /* renamed from: com.hanista.mobogram.mobo.voicechanger.b.a.c.1 */
    static /* synthetic */ class PcmOutDevice {
        static final /* synthetic */ int[] f2569a;

        static {
            f2569a = new int[HeadsetMode.values().length];
            try {
                f2569a[HeadsetMode.WIRED_HEADPHONES.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f2569a[HeadsetMode.WIRED_HEADSET.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f2569a[HeadsetMode.BLUETOOTH_HEADSET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public PcmOutDevice(Context context, HeadsetMode headsetMode) {
        super(context);
        this.f2570b = null;
        this.f2571c = null;
        switch (PcmOutDevice.f2569a[headsetMode.ordinal()]) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                m2532b(3);
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                m2532b(3);
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                m2529a(UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS);
                new Utils(context).m2589a("Sample rate changed to 8000 Hz.");
                m2532b(0);
                break;
            default:
                throw new IOException("Unknown HeadsetMode!");
        }
        m2543a(context);
    }

    private void m2543a(Context context) {
        m2533c(4);
        m2535d(2);
        m2537e(AudioTrack.getMinBufferSize(m2530b(), m2534d(), m2536e()));
        if (m2538f() == -2 || m2538f() == -1) {
            throw new IOException("Unable to determine the MinBufferSize for AudioTrack!");
        }
        m2539f(new Preferences(context).m2549a(m2530b()));
        new Utils(context).m2590a("PCM OUT buffer size is %s.", Integer.valueOf(m2540g()));
        this.f2570b = new AudioTrack(0, 44100, 4, 2, m2540g(), 1);
        if (this.f2570b.getState() != 1) {
            m2544c();
            throw new IOException("Unable to initialize an AudioTrack instance!");
        }
    }

    public void m2544c() {
        if (this.f2570b != null) {
            this.f2570b.release();
            this.f2570b = null;
        }
    }
}
