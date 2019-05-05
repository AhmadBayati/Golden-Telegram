package com.hanista.mobogram.mobo.voicechanger;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.preference.PreferenceManager;
import com.hanista.mobogram.C0338R;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.d */
public final class Preferences {
    private final SharedPreferences f2581a;

    public Preferences(Context context) {
        this.f2581a = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceManager.setDefaultValues(context, C0338R.xml.voice_preferences, false);
    }

    public int m2548a() {
        return Integer.parseInt(this.f2581a.getString("SignalAmplification", "0"));
    }

    public int m2549a(int i) {
        return Math.max(AudioRecord.getMinBufferSize(i, 16, 2), AudioTrack.getMinBufferSize(i, 4, 2));
    }

    public int m2550a(FrameType frameType, int i) {
        int i2 = (int) (frameType.f2580e * (((double) i) * 0.046439909297052155d));
        return i2 % 2 != 0 ? i2 + 1 : i2;
    }

    public int m2551b() {
        return Integer.parseInt(this.f2581a.getString("SampleRate", "44100"));
    }

    public int m2552b(FrameType frameType, int i) {
        return m2550a(frameType, i) / 4;
    }

    public boolean m2553c() {
        return this.f2581a.getBoolean("CorrectOffset", true);
    }

    public boolean m2554d() {
        return this.f2581a.getBoolean("SpectralNoiseGate", true);
    }

    public int m2555e() {
        return Integer.parseInt(this.f2581a.getString("NoiseGateCoeffExponent", "3"));
    }

    public boolean m2556f() {
        return this.f2581a.getBoolean("BandpassFilter", false);
    }

    public int m2557g() {
        return Integer.parseInt(this.f2581a.getString("BandpassLowerFreq", "100"));
    }

    public int m2558h() {
        return Integer.parseInt(this.f2581a.getString("BandpassUpperFreq", "8000"));
    }

    public boolean m2559i() {
        return this.f2581a.getBoolean("AutoMute", false);
    }

    public int m2560j() {
        return Math.min(0, Math.max(Integer.parseInt(this.f2581a.getString("AutoMuteHighThreshold", "-20")), Integer.parseInt(this.f2581a.getString("AutoMuteLowThreshold", "-25"))));
    }

    public int m2561k() {
        return Math.min(0, Math.min(Integer.parseInt(this.f2581a.getString("AutoMuteHighThreshold", "-20")), Integer.parseInt(this.f2581a.getString("AutoMuteLowThreshold", "-25"))));
    }

    public int m2562l() {
        return Integer.parseInt(this.f2581a.getString("AutoMuteHangover", "5"));
    }

    public boolean m2563m() {
        return this.f2581a.getBoolean("Logging", false);
    }
}
