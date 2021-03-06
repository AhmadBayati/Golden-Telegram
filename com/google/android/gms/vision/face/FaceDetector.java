package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.internal.client.FaceSettingsParcel;
import com.google.android.gms.vision.internal.client.FrameMetadataParcel;
import com.google.android.gms.vision.zza;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public final class FaceDetector extends Detector<Face> {
    public static final int ACCURATE_MODE = 1;
    public static final int ALL_CLASSIFICATIONS = 1;
    public static final int ALL_LANDMARKS = 1;
    public static final int FAST_MODE = 0;
    public static final int NO_CLASSIFICATIONS = 0;
    public static final int NO_LANDMARKS = 0;
    private final zza aKW;
    private final com.google.android.gms.vision.face.internal.client.zza aKX;
    private boolean aKY;
    private final Object zzakd;

    public static class Builder {
        private int Hh;
        private int aKZ;
        private boolean aLa;
        private int aLb;
        private boolean aLc;
        private float aLd;
        private final Context mContext;

        public Builder(Context context) {
            this.aKZ = 0;
            this.aLa = false;
            this.aLb = 0;
            this.aLc = true;
            this.Hh = 0;
            this.aLd = Face.UNCOMPUTED_PROBABILITY;
            this.mContext = context;
        }

        public FaceDetector build() {
            FaceSettingsParcel faceSettingsParcel = new FaceSettingsParcel();
            faceSettingsParcel.mode = this.Hh;
            faceSettingsParcel.aLm = this.aKZ;
            faceSettingsParcel.aLn = this.aLb;
            faceSettingsParcel.aLo = this.aLa;
            faceSettingsParcel.aLp = this.aLc;
            faceSettingsParcel.aLq = this.aLd;
            return new FaceDetector(null);
        }

        public Builder setClassificationType(int i) {
            if (i == 0 || i == FaceDetector.ALL_LANDMARKS) {
                this.aLb = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid classification type: " + i);
        }

        public Builder setLandmarkType(int i) {
            if (i == 0 || i == FaceDetector.ALL_LANDMARKS) {
                this.aKZ = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid landmark type: " + i);
        }

        public Builder setMinFaceSize(float f) {
            if (f < 0.0f || f > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                throw new IllegalArgumentException("Invalid proportional face size: " + f);
            }
            this.aLd = f;
            return this;
        }

        public Builder setMode(int i) {
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                case FaceDetector.ALL_LANDMARKS /*1*/:
                    this.Hh = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + i);
            }
        }

        public Builder setProminentFaceOnly(boolean z) {
            this.aLa = z;
            return this;
        }

        public Builder setTrackingEnabled(boolean z) {
            this.aLc = z;
            return this;
        }
    }

    private FaceDetector() {
        this.aKW = new zza();
        this.zzakd = new Object();
        this.aKY = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(com.google.android.gms.vision.face.internal.client.zza com_google_android_gms_vision_face_internal_client_zza) {
        this.aKW = new zza();
        this.zzakd = new Object();
        this.aKY = true;
        this.aKX = com_google_android_gms_vision_face_internal_client_zza;
    }

    public SparseArray<Face> detect(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        Face[] zzb;
        ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
        synchronized (this.zzakd) {
            if (this.aKY) {
                zzb = this.aKX.zzb(grayscaleImageData, FrameMetadataParcel.zzc(frame));
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        Set hashSet = new HashSet();
        SparseArray<Face> sparseArray = new SparseArray(zzb.length);
        int length = zzb.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2 += ALL_LANDMARKS) {
            Face face = zzb[i2];
            int id = face.getId();
            i = Math.max(i, id);
            if (hashSet.contains(Integer.valueOf(id))) {
                id = i + ALL_LANDMARKS;
                i = id;
            }
            hashSet.add(Integer.valueOf(id));
            sparseArray.append(this.aKW.zzabb(id), face);
        }
        return sparseArray;
    }

    protected void finalize() {
        try {
            synchronized (this.zzakd) {
                if (this.aKY) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
        } finally {
            super.finalize();
        }
    }

    public boolean isOperational() {
        return this.aKX.isOperational();
    }

    public void release() {
        super.release();
        synchronized (this.zzakd) {
            if (this.aKY) {
                this.aKX.zzcls();
                this.aKY = false;
                return;
            }
        }
    }

    public boolean setFocus(int i) {
        boolean zzabr;
        int zzabc = this.aKW.zzabc(i);
        synchronized (this.zzakd) {
            if (this.aKY) {
                zzabr = this.aKX.zzabr(zzabc);
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        return zzabr;
    }
}
