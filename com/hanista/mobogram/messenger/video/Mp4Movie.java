package com.hanista.mobogram.messenger.video;

import android.annotation.TargetApi;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.googlecode.mp4parser.util.Matrix;
import java.io.File;
import java.util.ArrayList;

@TargetApi(16)
public class Mp4Movie {
    private File cacheFile;
    private int height;
    private Matrix matrix;
    private ArrayList<Track> tracks;
    private int width;

    public Mp4Movie() {
        this.matrix = Matrix.ROTATE_0;
        this.tracks = new ArrayList();
    }

    public void addSample(int i, long j, BufferInfo bufferInfo) {
        if (i >= 0 && i < this.tracks.size()) {
            ((Track) this.tracks.get(i)).addSample(j, bufferInfo);
        }
    }

    public int addTrack(MediaFormat mediaFormat, boolean z) {
        this.tracks.add(new Track(this.tracks.size(), mediaFormat, z));
        return this.tracks.size() - 1;
    }

    public File getCacheFile() {
        return this.cacheFile;
    }

    public int getHeight() {
        return this.height;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public ArrayList<Track> getTracks() {
        return this.tracks;
    }

    public int getWidth() {
        return this.width;
    }

    public void setCacheFile(File file) {
        this.cacheFile = file;
    }

    public void setRotation(int i) {
        if (i == 0) {
            this.matrix = Matrix.ROTATE_0;
        } else if (i == 90) {
            this.matrix = Matrix.ROTATE_90;
        } else if (i == 180) {
            this.matrix = Matrix.ROTATE_180;
        } else if (i == 270) {
            this.matrix = Matrix.ROTATE_270;
        }
    }

    public void setSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }
}
