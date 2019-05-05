package com.hanista.mobogram.messenger.exoplayer.hls;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.Arrays;
import java.util.regex.Pattern;

final class WebvttExtractor implements Extractor {
    private static final Pattern LOCAL_TIMESTAMP;
    private static final Pattern MEDIA_TIMESTAMP;
    private ExtractorOutput output;
    private final PtsTimestampAdjuster ptsTimestampAdjuster;
    private byte[] sampleData;
    private final ParsableByteArray sampleDataWrapper;
    private int sampleSize;

    static {
        LOCAL_TIMESTAMP = Pattern.compile("LOCAL:([^,]+)");
        MEDIA_TIMESTAMP = Pattern.compile("MPEGTS:(\\d+)");
    }

    public WebvttExtractor(PtsTimestampAdjuster ptsTimestampAdjuster) {
        this.ptsTimestampAdjuster = ptsTimestampAdjuster;
        this.sampleDataWrapper = new ParsableByteArray();
        this.sampleData = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
    }

    private TrackOutput buildTrackOutput(long j) {
        TrackOutput track = this.output.track(0);
        track.format(MediaFormat.createTextFormat(TtmlNode.ATTR_ID, MimeTypes.TEXT_VTT, -1, -1, "en", j));
        this.output.endTracks();
        return track;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processSample() {
        /*
        r10 = this;
        r2 = 0;
        r4 = 1;
        r5 = new com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
        r0 = r10.sampleData;
        r5.<init>(r0);
        com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttParserUtil.validateWebvttHeaderLine(r5);
        r0 = r2;
        r6 = r2;
    L_0x000f:
        r8 = r5.readLine();
        r9 = android.text.TextUtils.isEmpty(r8);
        if (r9 != 0) goto L_0x0083;
    L_0x0019:
        r9 = "X-TIMESTAMP-MAP";
        r9 = r8.startsWith(r9);
        if (r9 == 0) goto L_0x000f;
    L_0x0022:
        r0 = LOCAL_TIMESTAMP;
        r0 = r0.matcher(r8);
        r1 = r0.find();
        if (r1 != 0) goto L_0x0048;
    L_0x002e:
        r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "X-TIMESTAMP-MAP doesn't contain local timestamp: ";
        r1 = r1.append(r2);
        r1 = r1.append(r8);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0048:
        r1 = MEDIA_TIMESTAMP;
        r1 = r1.matcher(r8);
        r6 = r1.find();
        if (r6 != 0) goto L_0x006e;
    L_0x0054:
        r0 = new com.hanista.mobogram.messenger.exoplayer.ParserException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "X-TIMESTAMP-MAP doesn't contain media timestamp: ";
        r1 = r1.append(r2);
        r1 = r1.append(r8);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x006e:
        r0 = r0.group(r4);
        r6 = com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r0);
        r0 = r1.group(r4);
        r0 = java.lang.Long.parseLong(r0);
        r0 = com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster.ptsToUs(r0);
        goto L_0x000f;
    L_0x0083:
        r5 = com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCueParser.findNextCueHeader(r5);
        if (r5 != 0) goto L_0x008d;
    L_0x0089:
        r10.buildTrackOutput(r2);
    L_0x008c:
        return;
    L_0x008d:
        r2 = r5.group(r4);
        r8 = com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttParserUtil.parseTimestampUs(r2);
        r2 = r10.ptsTimestampAdjuster;
        r0 = r0 + r8;
        r0 = r0 - r6;
        r0 = com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster.usToPts(r0);
        r2 = r2.adjustTimestamp(r0);
        r0 = r2 - r8;
        r1 = r10.buildTrackOutput(r0);
        r0 = r10.sampleDataWrapper;
        r5 = r10.sampleData;
        r6 = r10.sampleSize;
        r0.reset(r5, r6);
        r0 = r10.sampleDataWrapper;
        r5 = r10.sampleSize;
        r1.sampleData(r0, r5);
        r5 = r10.sampleSize;
        r6 = 0;
        r7 = 0;
        r1.sampleMetadata(r2, r4, r5, r6, r7);
        goto L_0x008c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.hls.WebvttExtractor.processSample():void");
    }

    public void init(ExtractorOutput extractorOutput) {
        this.output = extractorOutput;
        extractorOutput.seekMap(SeekMap.UNSEEKABLE);
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        int length = (int) extractorInput.getLength();
        if (this.sampleSize == this.sampleData.length) {
            this.sampleData = Arrays.copyOf(this.sampleData, ((length != -1 ? length : this.sampleData.length) * 3) / 2);
        }
        int read = extractorInput.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
        if (read != -1) {
            this.sampleSize = read + this.sampleSize;
            if (length == -1 || this.sampleSize != length) {
                return 0;
            }
        }
        processSample();
        return -1;
    }

    public void release() {
    }

    public void seek() {
        throw new IllegalStateException();
    }

    public boolean sniff(ExtractorInput extractorInput) {
        throw new IllegalStateException();
    }
}
