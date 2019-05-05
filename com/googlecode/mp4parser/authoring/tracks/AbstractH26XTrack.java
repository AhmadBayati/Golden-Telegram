package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.io.EOFException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractH26XTrack extends AbstractTrack {
    static int BUFFER;
    protected List<Entry> ctts;
    private DataSource dataSource;
    protected long[] decodingTimes;
    protected List<SampleDependencyTypeBox.Entry> sdtp;
    protected List<Integer> stss;
    TrackMetaData trackMetaData;

    public static class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos;
        DataSource dataSource;
        int inBufferPos;
        long start;

        public LookAhead(DataSource dataSource) {
            this.bufferStartPos = 0;
            this.inBufferPos = 0;
            this.dataSource = dataSource;
            fillBuffer();
        }

        public void discardByte() {
            this.inBufferPos++;
        }

        public void discardNext3AndMarkStart() {
            this.inBufferPos += 3;
            this.start = this.bufferStartPos + ((long) this.inBufferPos);
        }

        public void fillBuffer() {
            this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataSource.size() - this.bufferStartPos, (long) AbstractH26XTrack.BUFFER));
        }

        public ByteBuffer getNal() {
            if (this.start >= this.bufferStartPos) {
                this.buffer.position((int) (this.start - this.bufferStartPos));
                Buffer slice = this.buffer.slice();
                slice.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return (ByteBuffer) slice;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }

        public boolean nextThreeEquals000or001orEof() {
            if (this.buffer.limit() - this.inBufferPos >= 3) {
                return this.buffer.get(this.inBufferPos) == null && this.buffer.get(this.inBufferPos + 1) == null && (this.buffer.get(this.inBufferPos + 2) == null || this.buffer.get(this.inBufferPos + 2) == (byte) 1);
            } else {
                if ((this.bufferStartPos + ((long) this.inBufferPos)) + 3 > this.dataSource.size()) {
                    return this.bufferStartPos + ((long) this.inBufferPos) == this.dataSource.size();
                } else {
                    this.bufferStartPos = this.start;
                    this.inBufferPos = 0;
                    fillBuffer();
                    return nextThreeEquals000or001orEof();
                }
            }
        }

        public boolean nextThreeEquals001() {
            if (this.buffer.limit() - this.inBufferPos >= 3) {
                return this.buffer.get(this.inBufferPos) == null && this.buffer.get(this.inBufferPos + 1) == null && this.buffer.get(this.inBufferPos + 2) == (byte) 1;
            } else {
                if ((this.bufferStartPos + ((long) this.inBufferPos)) + 3 < this.dataSource.size()) {
                    return false;
                }
                throw new EOFException();
            }
        }
    }

    static {
        BUFFER = 67107840;
    }

    public AbstractH26XTrack(DataSource dataSource) {
        super(dataSource.toString());
        this.ctts = new ArrayList();
        this.sdtp = new ArrayList();
        this.stss = new ArrayList();
        this.trackMetaData = new TrackMetaData();
        this.dataSource = dataSource;
    }

    static InputStream cleanBuffer(InputStream inputStream) {
        return new CleanInputStream(inputStream);
    }

    protected static byte[] toArray(ByteBuffer byteBuffer) {
        ByteBuffer duplicate = byteBuffer.duplicate();
        byte[] bArr = new byte[duplicate.remaining()];
        duplicate.get(bArr, 0, bArr.length);
        return bArr;
    }

    public void close() {
        this.dataSource.close();
    }

    protected Sample createSampleObject(List<? extends ByteBuffer> list) {
        byte[] bArr = new byte[(list.size() * 4)];
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        for (ByteBuffer remaining : list) {
            wrap.putInt(remaining.remaining());
        }
        ByteBuffer[] byteBufferArr = new ByteBuffer[(list.size() * 2)];
        for (int i = 0; i < list.size(); i++) {
            byteBufferArr[i * 2] = ByteBuffer.wrap(bArr, i * 4, 4);
            byteBufferArr[(i * 2) + 1] = (ByteBuffer) list.get(i);
        }
        return new SampleImpl(byteBufferArr);
    }

    protected ByteBuffer findNextNal(LookAhead lookAhead) {
        while (!lookAhead.nextThreeEquals001()) {
            try {
                lookAhead.discardByte();
            } catch (EOFException e) {
                return null;
            }
        }
        lookAhead.discardNext3AndMarkStart();
        while (!lookAhead.nextThreeEquals000or001orEof()) {
            lookAhead.discardByte();
        }
        return lookAhead.getNal();
    }

    public List<Entry> getCompositionTimeEntries() {
        return this.ctts;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.sdtp;
    }

    public long[] getSampleDurations() {
        return this.decodingTimes;
    }

    public long[] getSyncSamples() {
        long[] jArr = new long[this.stss.size()];
        for (int i = 0; i < this.stss.size(); i++) {
            jArr[i] = (long) ((Integer) this.stss.get(i)).intValue();
        }
        return jArr;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }
}
