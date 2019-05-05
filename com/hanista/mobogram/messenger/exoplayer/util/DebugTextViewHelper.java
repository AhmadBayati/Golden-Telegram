package com.hanista.mobogram.messenger.exoplayer.util;

import android.widget.TextView;
import com.hanista.mobogram.messenger.exoplayer.CodecCounters;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.upstream.BandwidthMeter;

public final class DebugTextViewHelper implements Runnable {
    private static final int REFRESH_INTERVAL_MS = 1000;
    private final Provider debuggable;
    private final TextView textView;

    public interface Provider {
        BandwidthMeter getBandwidthMeter();

        CodecCounters getCodecCounters();

        long getCurrentPosition();

        Format getFormat();
    }

    public DebugTextViewHelper(Provider provider, TextView textView) {
        this.debuggable = provider;
        this.textView = textView;
    }

    private String getBandwidthString() {
        BandwidthMeter bandwidthMeter = this.debuggable.getBandwidthMeter();
        return (bandwidthMeter == null || bandwidthMeter.getBitrateEstimate() == -1) ? "bw:?" : "bw:" + (bandwidthMeter.getBitrateEstimate() / 1000);
    }

    private String getQualityString() {
        Format format = this.debuggable.getFormat();
        return format == null ? "id:? br:? h:?" : "id:" + format.id + " br:" + format.bitrate + " h:" + format.height;
    }

    private String getRenderString() {
        return getTimeString() + " " + getQualityString() + " " + getBandwidthString() + " " + getVideoCodecCountersString();
    }

    private String getTimeString() {
        return "ms(" + this.debuggable.getCurrentPosition() + ")";
    }

    private String getVideoCodecCountersString() {
        CodecCounters codecCounters = this.debuggable.getCodecCounters();
        return codecCounters == null ? TtmlNode.ANONYMOUS_REGION_ID : codecCounters.getDebugString();
    }

    public void run() {
        this.textView.setText(getRenderString());
        this.textView.postDelayed(this, 1000);
    }

    public void start() {
        stop();
        run();
    }

    public void stop() {
        this.textView.removeCallbacks(this);
    }
}
