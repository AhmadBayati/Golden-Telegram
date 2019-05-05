package com.hanista.mobogram.mobo.voicechanger.p022b.p023a;

import android.content.Context;
import android.media.AudioRecord;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.Utils;
import com.hanista.mobogram.mobo.voicechanger.p021a.HeadsetMode;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.IOException;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.b.a.b */
public class PcmInDevice extends PcmDevice {
    private AudioRecord f2568b;

    /* renamed from: com.hanista.mobogram.mobo.voicechanger.b.a.b.1 */
    static /* synthetic */ class PcmInDevice {
        static final /* synthetic */ int[] f2567a;

        static {
            f2567a = new int[HeadsetMode.values().length];
            try {
                f2567a[HeadsetMode.WIRED_HEADPHONES.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f2567a[HeadsetMode.WIRED_HEADSET.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f2567a[HeadsetMode.BLUETOOTH_HEADSET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public PcmInDevice(Context context, HeadsetMode headsetMode) {
        super(context);
        this.f2568b = null;
        switch (PcmInDevice.f2567a[headsetMode.ordinal()]) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                m2532b(5);
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                m2532b(0);
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                m2529a(UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS);
                new Utils(context).m2589a("Sample rate changed to 8000 Hz.");
                m2532b(0);
                break;
            default:
                throw new IOException("Unknown HeadsetMode!");
        }
        m2541a(context);
    }

    private void m2541a(Context context) {
        m2533c(16);
        m2535d(2);
        m2537e(AudioRecord.getMinBufferSize(m2530b(), m2534d(), m2536e()));
        if (m2538f() == -2 || m2538f() == -1) {
            throw new IOException("Unable to determine the MinBufferSize for AudioRecord!");
        }
        m2539f(new Preferences(context).m2549a(m2530b()));
        new Utils(context).m2590a("PCM IN buffer size is %s.", Integer.valueOf(m2540g()));
    }

    public void m2542c() {
        if (this.f2568b != null) {
            this.f2568b.release();
            this.f2568b = null;
        }
    }
}
