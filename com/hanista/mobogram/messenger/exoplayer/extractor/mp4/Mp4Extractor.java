package com.hanista.mobogram.messenger.exoplayer.extractor.mp4;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.GaplessInfo;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class Mp4Extractor implements Extractor, SeekMap {
    private static final int BRAND_QUICKTIME;
    private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144;
    private static final int STATE_AFTER_SEEK = 0;
    private static final int STATE_READING_ATOM_HEADER = 1;
    private static final int STATE_READING_ATOM_PAYLOAD = 2;
    private static final int STATE_READING_SAMPLE = 3;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final Stack<ContainerAtom> containerAtoms;
    private ExtractorOutput extractorOutput;
    private boolean isQuickTime;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private Mp4Track[] tracks;

    private static final class Mp4Track {
        public int sampleIndex;
        public final TrackSampleTable sampleTable;
        public final Track track;
        public final TrackOutput trackOutput;

        public Mp4Track(Track track, TrackSampleTable trackSampleTable, TrackOutput trackOutput) {
            this.track = track;
            this.sampleTable = trackSampleTable;
            this.trackOutput = trackOutput;
        }
    }

    static {
        BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
    }

    public Mp4Extractor() {
        this.atomHeader = new ParsableByteArray(16);
        this.containerAtoms = new Stack();
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        enterReadingAtomHeaderState();
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = STATE_READING_ATOM_HEADER;
        this.atomHeaderBytesRead = STATE_AFTER_SEEK;
    }

    private int getTrackIndexOfEarliestCurrentSample() {
        int i = -1;
        long j = PtsTimestampAdjuster.DO_NOT_OFFSET;
        for (int i2 = STATE_AFTER_SEEK; i2 < this.tracks.length; i2 += STATE_READING_ATOM_HEADER) {
            Mp4Track mp4Track = this.tracks[i2];
            int i3 = mp4Track.sampleIndex;
            if (i3 != mp4Track.sampleTable.sampleCount) {
                long j2 = mp4Track.sampleTable.offsets[i3];
                if (j2 < j) {
                    j = j2;
                    i = i2;
                }
            }
        }
        return i;
    }

    private void processAtomEnded(long j) {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == j) {
            ContainerAtom containerAtom = (ContainerAtom) this.containerAtoms.pop();
            if (containerAtom.type == Atom.TYPE_moov) {
                processMoovAtom(containerAtom);
                this.containerAtoms.clear();
                this.parserState = STATE_READING_SAMPLE;
            } else if (!this.containerAtoms.isEmpty()) {
                ((ContainerAtom) this.containerAtoms.peek()).add(containerAtom);
            }
        }
        if (this.parserState != STATE_READING_SAMPLE) {
            enterReadingAtomHeaderState();
        }
    }

    private static boolean processFtypAtom(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(8);
        if (parsableByteArray.readInt() == BRAND_QUICKTIME) {
            return true;
        }
        parsableByteArray.skipBytes(4);
        while (parsableByteArray.bytesLeft() > 0) {
            if (parsableByteArray.readInt() == BRAND_QUICKTIME) {
                return true;
            }
        }
        return false;
    }

    private void processMoovAtom(ContainerAtom containerAtom) {
        List arrayList = new ArrayList();
        long j = PtsTimestampAdjuster.DO_NOT_OFFSET;
        LeafAtom leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_udta);
        GaplessInfo parseUdta = leafAtomOfType != null ? AtomParsers.parseUdta(leafAtomOfType, this.isQuickTime) : null;
        for (int i = STATE_AFTER_SEEK; i < containerAtom.containerChildren.size(); i += STATE_READING_ATOM_HEADER) {
            ContainerAtom containerAtom2 = (ContainerAtom) containerAtom.containerChildren.get(i);
            if (containerAtom2.type == Atom.TYPE_trak) {
                Track parseTrak = AtomParsers.parseTrak(containerAtom2, containerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), -1, this.isQuickTime);
                if (parseTrak != null) {
                    TrackSampleTable parseStbl = AtomParsers.parseStbl(parseTrak, containerAtom2.getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl));
                    if (parseStbl.sampleCount != 0) {
                        Mp4Track mp4Track = new Mp4Track(parseTrak, parseStbl, this.extractorOutput.track(i));
                        MediaFormat copyWithMaxInputSize = parseTrak.mediaFormat.copyWithMaxInputSize(parseStbl.maximumSize + 30);
                        if (parseUdta != null) {
                            copyWithMaxInputSize = copyWithMaxInputSize.copyWithGaplessInfo(parseUdta.encoderDelay, parseUdta.encoderPadding);
                        }
                        mp4Track.trackOutput.format(copyWithMaxInputSize);
                        arrayList.add(mp4Track);
                        long j2 = parseStbl.offsets[STATE_AFTER_SEEK];
                        if (j2 < j) {
                            j = j2;
                        }
                    }
                }
            }
        }
        this.tracks = (Mp4Track[]) arrayList.toArray(new Mp4Track[STATE_AFTER_SEEK]);
        this.extractorOutput.endTracks();
        this.extractorOutput.seekMap(this);
    }

    private boolean readAtomHeader(ExtractorInput extractorInput) {
        if (this.atomHeaderBytesRead == 0) {
            if (!extractorInput.readFully(this.atomHeader.data, STATE_AFTER_SEEK, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(STATE_AFTER_SEEK);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        if (this.atomSize == 1) {
            extractorInput.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long position = (extractorInput.getPosition() + this.atomSize) - ((long) this.atomHeaderBytesRead);
            this.containerAtoms.add(new ContainerAtom(this.atomType, position));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(position);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            Assertions.checkState(this.atomHeaderBytesRead == 8);
            Assertions.checkState(this.atomSize <= 2147483647L);
            this.atomData = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.data, STATE_AFTER_SEEK, this.atomData.data, STATE_AFTER_SEEK, 8);
            this.parserState = STATE_READING_ATOM_PAYLOAD;
        } else {
            this.atomData = null;
            this.parserState = STATE_READING_ATOM_PAYLOAD;
        }
        return true;
    }

    private boolean readAtomPayload(ExtractorInput extractorInput, PositionHolder positionHolder) {
        boolean z;
        long j = this.atomSize - ((long) this.atomHeaderBytesRead);
        long position = extractorInput.getPosition() + j;
        if (this.atomData != null) {
            extractorInput.readFully(this.atomData.data, this.atomHeaderBytesRead, (int) j);
            if (this.atomType == Atom.TYPE_ftyp) {
                this.isQuickTime = processFtypAtom(this.atomData);
                z = STATE_AFTER_SEEK;
            } else if (this.containerAtoms.isEmpty()) {
                z = STATE_AFTER_SEEK;
            } else {
                ((ContainerAtom) this.containerAtoms.peek()).add(new LeafAtom(this.atomType, this.atomData));
                z = STATE_AFTER_SEEK;
            }
        } else if (j < RELOAD_MINIMUM_SEEK_DISTANCE) {
            extractorInput.skipFully((int) j);
            z = STATE_AFTER_SEEK;
        } else {
            positionHolder.position = j + extractorInput.getPosition();
            z = true;
        }
        processAtomEnded(position);
        return z && this.parserState != STATE_READING_SAMPLE;
    }

    private int readSample(ExtractorInput extractorInput, PositionHolder positionHolder) {
        int trackIndexOfEarliestCurrentSample = getTrackIndexOfEarliestCurrentSample();
        if (trackIndexOfEarliestCurrentSample == -1) {
            return -1;
        }
        Mp4Track mp4Track = this.tracks[trackIndexOfEarliestCurrentSample];
        TrackOutput trackOutput = mp4Track.trackOutput;
        int i = mp4Track.sampleIndex;
        long j = mp4Track.sampleTable.offsets[i];
        long position = (j - extractorInput.getPosition()) + ((long) this.sampleBytesWritten);
        if (position < 0 || position >= RELOAD_MINIMUM_SEEK_DISTANCE) {
            positionHolder.position = j;
            return STATE_READING_ATOM_HEADER;
        }
        extractorInput.skipFully((int) position);
        this.sampleSize = mp4Track.sampleTable.sizes[i];
        int i2;
        if (mp4Track.track.nalUnitLengthFieldLength != -1) {
            byte[] bArr = this.nalLength.data;
            bArr[STATE_AFTER_SEEK] = (byte) 0;
            bArr[STATE_READING_ATOM_HEADER] = (byte) 0;
            bArr[STATE_READING_ATOM_PAYLOAD] = (byte) 0;
            i2 = mp4Track.track.nalUnitLengthFieldLength;
            int i3 = 4 - mp4Track.track.nalUnitLengthFieldLength;
            while (this.sampleBytesWritten < this.sampleSize) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    extractorInput.readFully(this.nalLength.data, i3, i2);
                    this.nalLength.setPosition(STATE_AFTER_SEEK);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(STATE_AFTER_SEEK);
                    trackOutput.sampleData(this.nalStartCode, 4);
                    this.sampleBytesWritten += 4;
                    this.sampleSize += i3;
                } else {
                    int sampleData = trackOutput.sampleData(extractorInput, this.sampleCurrentNalBytesRemaining, false);
                    this.sampleBytesWritten += sampleData;
                    this.sampleCurrentNalBytesRemaining -= sampleData;
                }
            }
        } else {
            while (this.sampleBytesWritten < this.sampleSize) {
                i2 = trackOutput.sampleData(extractorInput, this.sampleSize - this.sampleBytesWritten, false);
                this.sampleBytesWritten += i2;
                this.sampleCurrentNalBytesRemaining -= i2;
            }
        }
        trackOutput.sampleMetadata(mp4Track.sampleTable.timestampsUs[i], mp4Track.sampleTable.flags[i], this.sampleSize, STATE_AFTER_SEEK, null);
        mp4Track.sampleIndex += STATE_READING_ATOM_HEADER;
        this.sampleBytesWritten = STATE_AFTER_SEEK;
        this.sampleCurrentNalBytesRemaining = STATE_AFTER_SEEK;
        return STATE_AFTER_SEEK;
    }

    private static boolean shouldParseContainerAtom(int i) {
        return i == Atom.TYPE_moov || i == Atom.TYPE_trak || i == Atom.TYPE_mdia || i == Atom.TYPE_minf || i == Atom.TYPE_stbl || i == Atom.TYPE_edts;
    }

    private static boolean shouldParseLeafAtom(int i) {
        return i == Atom.TYPE_mdhd || i == Atom.TYPE_mvhd || i == Atom.TYPE_hdlr || i == Atom.TYPE_stsd || i == Atom.TYPE_stts || i == Atom.TYPE_stss || i == Atom.TYPE_ctts || i == Atom.TYPE_elst || i == Atom.TYPE_stsc || i == Atom.TYPE_stsz || i == Atom.TYPE_stz2 || i == Atom.TYPE_stco || i == Atom.TYPE_co64 || i == Atom.TYPE_tkhd || i == Atom.TYPE_ftyp || i == Atom.TYPE_udta;
    }

    public long getPosition(long j) {
        long j2 = PtsTimestampAdjuster.DO_NOT_OFFSET;
        for (int i = STATE_AFTER_SEEK; i < this.tracks.length; i += STATE_READING_ATOM_HEADER) {
            TrackSampleTable trackSampleTable = this.tracks[i].sampleTable;
            int indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(j);
            if (indexOfEarlierOrEqualSynchronizationSample == -1) {
                indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(j);
            }
            this.tracks[i].sampleIndex = indexOfEarlierOrEqualSynchronizationSample;
            long j3 = trackSampleTable.offsets[indexOfEarlierOrEqualSynchronizationSample];
            if (j3 < j2) {
                j2 = j3;
            }
        }
        return j2;
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
    }

    public boolean isSeekable() {
        return true;
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        while (true) {
            switch (this.parserState) {
                case STATE_AFTER_SEEK /*0*/:
                    if (extractorInput.getPosition() != 0) {
                        this.parserState = STATE_READING_SAMPLE;
                        break;
                    }
                    enterReadingAtomHeaderState();
                    break;
                case STATE_READING_ATOM_HEADER /*1*/:
                    if (readAtomHeader(extractorInput)) {
                        break;
                    }
                    return -1;
                case STATE_READING_ATOM_PAYLOAD /*2*/:
                    if (!readAtomPayload(extractorInput, positionHolder)) {
                        break;
                    }
                    return STATE_READING_ATOM_HEADER;
                default:
                    return readSample(extractorInput, positionHolder);
            }
        }
    }

    public void release() {
    }

    public void seek() {
        this.containerAtoms.clear();
        this.atomHeaderBytesRead = STATE_AFTER_SEEK;
        this.sampleBytesWritten = STATE_AFTER_SEEK;
        this.sampleCurrentNalBytesRemaining = STATE_AFTER_SEEK;
        this.parserState = STATE_AFTER_SEEK;
    }

    public boolean sniff(ExtractorInput extractorInput) {
        return Sniffer.sniffUnfragmented(extractorInput);
    }
}
