package com.hanista.mobogram.messenger.exoplayer.extractor.mp4;

import android.support.v4.view.PointerIconCompat;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.Mapped;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.ChunkIndex;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class FragmentedMp4Extractor implements Extractor {
    private static final int FLAG_SIDELOADED = 4;
    public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
    public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
    private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE;
    private static final int SAMPLE_GROUP_TYPE_seig;
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_ENCRYPTION_DATA = 2;
    private static final int STATE_READING_SAMPLE_CONTINUE = 4;
    private static final int STATE_READING_SAMPLE_START = 3;
    private static final String TAG = "FragmentedMp4Extractor";
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final Stack<ContainerAtom> containerAtoms;
    private TrackBundle currentTrackBundle;
    private final ParsableByteArray encryptionSignalByte;
    private long endOfMdatPosition;
    private final byte[] extendedTypeScratch;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private boolean haveOutputSeekMap;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private final Track sideloadedTrack;
    private final SparseArray<TrackBundle> trackBundles;

    private static final class TrackBundle {
        public int currentSampleIndex;
        public DefaultSampleValues defaultSampleValues;
        public final TrackFragment fragment;
        public final TrackOutput output;
        public Track track;

        public TrackBundle(TrackOutput trackOutput) {
            this.fragment = new TrackFragment();
            this.output = trackOutput;
        }

        public void init(Track track, DefaultSampleValues defaultSampleValues) {
            this.track = (Track) Assertions.checkNotNull(track);
            this.defaultSampleValues = (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValues);
            this.output.format(track.mediaFormat);
            reset();
        }

        public void reset() {
            this.fragment.reset();
            this.currentSampleIndex = FragmentedMp4Extractor.STATE_READING_ATOM_HEADER;
        }
    }

    static {
        SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString(CencSampleEncryptionInformationGroupEntry.TYPE);
        PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = new byte[]{(byte) -94, (byte) 57, (byte) 79, (byte) 82, (byte) 90, (byte) -101, (byte) 79, ClosedCaptionCtrl.MISC_CHAN_1, (byte) -94, (byte) 68, (byte) 108, (byte) 66, (byte) 124, (byte) 100, (byte) -115, (byte) -12};
    }

    public FragmentedMp4Extractor() {
        this(STATE_READING_ATOM_HEADER);
    }

    public FragmentedMp4Extractor(int i) {
        this(i, null);
    }

    public FragmentedMp4Extractor(int i, Track track) {
        this.sideloadedTrack = track;
        this.flags = (track != null ? STATE_READING_SAMPLE_CONTINUE : STATE_READING_ATOM_HEADER) | i;
        this.atomHeader = new ParsableByteArray(16);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray((int) STATE_READING_SAMPLE_CONTINUE);
        this.encryptionSignalByte = new ParsableByteArray((int) STATE_READING_ATOM_PAYLOAD);
        this.extendedTypeScratch = new byte[16];
        this.containerAtoms = new Stack();
        this.trackBundles = new SparseArray();
        enterReadingAtomHeaderState();
    }

    private int appendSampleEncryptionData(TrackBundle trackBundle) {
        TrackFragment trackFragment = trackBundle.fragment;
        ParsableByteArray parsableByteArray = trackFragment.sampleEncryptionData;
        int i = (trackFragment.trackEncryptionBox != null ? trackFragment.trackEncryptionBox : trackBundle.track.sampleDescriptionEncryptionBoxes[trackFragment.header.sampleDescriptionIndex]).initializationVectorSize;
        boolean z = trackFragment.sampleHasSubsampleEncryptionTable[trackBundle.currentSampleIndex];
        this.encryptionSignalByte.data[STATE_READING_ATOM_HEADER] = (byte) ((z ? TLRPC.USER_FLAG_UNUSED : STATE_READING_ATOM_HEADER) | i);
        this.encryptionSignalByte.setPosition(STATE_READING_ATOM_HEADER);
        TrackOutput trackOutput = trackBundle.output;
        trackOutput.sampleData(this.encryptionSignalByte, STATE_READING_ATOM_PAYLOAD);
        trackOutput.sampleData(parsableByteArray, i);
        if (!z) {
            return i + STATE_READING_ATOM_PAYLOAD;
        }
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        parsableByteArray.skipBytes(-2);
        readUnsignedShort = (readUnsignedShort * 6) + STATE_READING_ENCRYPTION_DATA;
        trackOutput.sampleData(parsableByteArray, readUnsignedShort);
        return (i + STATE_READING_ATOM_PAYLOAD) + readUnsignedShort;
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = STATE_READING_ATOM_HEADER;
        this.atomHeaderBytesRead = STATE_READING_ATOM_HEADER;
    }

    private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> sparseArray) {
        TrackBundle trackBundle = null;
        long j = PtsTimestampAdjuster.DO_NOT_OFFSET;
        int size = sparseArray.size();
        int i = STATE_READING_ATOM_HEADER;
        while (i < size) {
            TrackBundle trackBundle2;
            long j2;
            TrackBundle trackBundle3 = (TrackBundle) sparseArray.valueAt(i);
            long j3;
            if (trackBundle3.currentSampleIndex == trackBundle3.fragment.length) {
                j3 = j;
                trackBundle2 = trackBundle;
                j2 = j3;
            } else {
                long j4 = trackBundle3.fragment.dataPosition;
                if (j4 < j) {
                    trackBundle2 = trackBundle3;
                    j2 = j4;
                } else {
                    j3 = j;
                    trackBundle2 = trackBundle;
                    j2 = j3;
                }
            }
            i += STATE_READING_ATOM_PAYLOAD;
            trackBundle = trackBundle2;
            j = j2;
        }
        return trackBundle;
    }

    private void onContainerAtomRead(ContainerAtom containerAtom) {
        if (containerAtom.type == Atom.TYPE_moov) {
            onMoovContainerAtomRead(containerAtom);
        } else if (containerAtom.type == Atom.TYPE_moof) {
            onMoofContainerAtomRead(containerAtom);
        } else if (!this.containerAtoms.isEmpty()) {
            ((ContainerAtom) this.containerAtoms.peek()).add(containerAtom);
        }
    }

    private void onLeafAtomRead(LeafAtom leafAtom, long j) {
        if (!this.containerAtoms.isEmpty()) {
            ((ContainerAtom) this.containerAtoms.peek()).add(leafAtom);
        } else if (leafAtom.type == Atom.TYPE_sidx) {
            this.extractorOutput.seekMap(parseSidx(leafAtom.data, j));
            this.haveOutputSeekMap = true;
        } else if (leafAtom.type == Atom.TYPE_emsg) {
            parseEmsg(leafAtom.data, j);
        }
    }

    private void onMoofContainerAtomRead(ContainerAtom containerAtom) {
        parseMoof(containerAtom, this.trackBundles, this.flags, this.extendedTypeScratch);
    }

    private void onMoovContainerAtomRead(ContainerAtom containerAtom) {
        int i;
        int i2;
        boolean z = true;
        Assertions.checkState(this.sideloadedTrack == null, "Unexpected moov box.");
        List list = containerAtom.leafChildren;
        int size = list.size();
        DrmInitData drmInitData = null;
        for (i = STATE_READING_ATOM_HEADER; i < size; i += STATE_READING_ATOM_PAYLOAD) {
            LeafAtom leafAtom = (LeafAtom) list.get(i);
            if (leafAtom.type == Atom.TYPE_pssh) {
                if (drmInitData == null) {
                    drmInitData = new Mapped();
                }
                byte[] bArr = leafAtom.data.data;
                if (PsshAtomUtil.parseUuid(bArr) == null) {
                    Log.w(TAG, "Skipped pssh atom (failed to extract uuid)");
                } else {
                    drmInitData.put(PsshAtomUtil.parseUuid(bArr), new SchemeInitData(MimeTypes.VIDEO_MP4, bArr));
                }
            }
        }
        if (drmInitData != null) {
            this.extractorOutput.drmInitData(drmInitData);
        }
        ContainerAtom containerAtomOfType = containerAtom.getContainerAtomOfType(Atom.TYPE_mvex);
        SparseArray sparseArray = new SparseArray();
        long j = -1;
        int size2 = containerAtomOfType.leafChildren.size();
        for (i2 = STATE_READING_ATOM_HEADER; i2 < size2; i2 += STATE_READING_ATOM_PAYLOAD) {
            leafAtom = (LeafAtom) containerAtomOfType.leafChildren.get(i2);
            if (leafAtom.type == Atom.TYPE_trex) {
                Pair parseTrex = parseTrex(leafAtom.data);
                sparseArray.put(((Integer) parseTrex.first).intValue(), parseTrex.second);
            } else if (leafAtom.type == Atom.TYPE_mehd) {
                j = parseMehd(leafAtom.data);
            }
        }
        SparseArray sparseArray2 = new SparseArray();
        size2 = containerAtom.containerChildren.size();
        for (i2 = STATE_READING_ATOM_HEADER; i2 < size2; i2 += STATE_READING_ATOM_PAYLOAD) {
            Track parseTrak;
            ContainerAtom containerAtom2 = (ContainerAtom) containerAtom.containerChildren.get(i2);
            if (containerAtom2.type == Atom.TYPE_trak) {
                parseTrak = AtomParsers.parseTrak(containerAtom2, containerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), j, false);
                if (parseTrak != null) {
                    sparseArray2.put(parseTrak.id, parseTrak);
                }
            }
        }
        i = sparseArray2.size();
        if (this.trackBundles.size() == 0) {
            for (int i3 = STATE_READING_ATOM_HEADER; i3 < i; i3 += STATE_READING_ATOM_PAYLOAD) {
                this.trackBundles.put(((Track) sparseArray2.valueAt(i3)).id, new TrackBundle(this.extractorOutput.track(i3)));
            }
            this.extractorOutput.endTracks();
        } else {
            if (this.trackBundles.size() != i) {
                z = false;
            }
            Assertions.checkState(z);
        }
        for (i2 = STATE_READING_ATOM_HEADER; i2 < i; i2 += STATE_READING_ATOM_PAYLOAD) {
            parseTrak = (Track) sparseArray2.valueAt(i2);
            ((TrackBundle) this.trackBundles.get(parseTrak.id)).init(parseTrak, (DefaultSampleValues) sparseArray.get(parseTrak.id));
        }
    }

    private static long parseMehd(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(8);
        return Atom.parseFullAtomVersion(parsableByteArray.readInt()) == 0 ? parsableByteArray.readUnsignedInt() : parsableByteArray.readUnsignedLongToLong();
    }

    private static void parseMoof(ContainerAtom containerAtom, SparseArray<TrackBundle> sparseArray, int i, byte[] bArr) {
        int size = containerAtom.containerChildren.size();
        for (int i2 = STATE_READING_ATOM_HEADER; i2 < size; i2 += STATE_READING_ATOM_PAYLOAD) {
            ContainerAtom containerAtom2 = (ContainerAtom) containerAtom.containerChildren.get(i2);
            if (containerAtom2.type == Atom.TYPE_traf) {
                parseTraf(containerAtom2, sparseArray, i, bArr);
            }
        }
    }

    private static void parseSaio(ParsableByteArray parsableByteArray, TrackFragment trackFragment) {
        parsableByteArray.setPosition(8);
        int readInt = parsableByteArray.readInt();
        if ((Atom.parseFullAtomFlags(readInt) & STATE_READING_ATOM_PAYLOAD) == STATE_READING_ATOM_PAYLOAD) {
            parsableByteArray.skipBytes(8);
        }
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        if (readUnsignedIntToInt != STATE_READING_ATOM_PAYLOAD) {
            throw new ParserException("Unexpected saio entry count: " + readUnsignedIntToInt);
        }
        readInt = Atom.parseFullAtomVersion(readInt);
        trackFragment.auxiliaryDataPosition = (readInt == 0 ? parsableByteArray.readUnsignedInt() : parsableByteArray.readUnsignedLongToLong()) + trackFragment.auxiliaryDataPosition;
    }

    private static void parseSaiz(TrackEncryptionBox trackEncryptionBox, ParsableByteArray parsableByteArray, TrackFragment trackFragment) {
        boolean z = true;
        int i = trackEncryptionBox.initializationVectorSize;
        parsableByteArray.setPosition(8);
        if ((Atom.parseFullAtomFlags(parsableByteArray.readInt()) & STATE_READING_ATOM_PAYLOAD) == STATE_READING_ATOM_PAYLOAD) {
            parsableByteArray.skipBytes(8);
        }
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        if (readUnsignedIntToInt != trackFragment.length) {
            throw new ParserException("Length mismatch: " + readUnsignedIntToInt + ", " + trackFragment.length);
        }
        if (readUnsignedByte == 0) {
            boolean[] zArr = trackFragment.sampleHasSubsampleEncryptionTable;
            int i2 = STATE_READING_ATOM_HEADER;
            readUnsignedByte = STATE_READING_ATOM_HEADER;
            while (i2 < readUnsignedIntToInt) {
                int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                int i3 = readUnsignedByte + readUnsignedByte2;
                zArr[i2] = readUnsignedByte2 > i;
                i2 += STATE_READING_ATOM_PAYLOAD;
                readUnsignedByte = i3;
            }
        } else {
            if (readUnsignedByte <= i) {
                z = false;
            }
            readUnsignedByte = (readUnsignedByte * readUnsignedIntToInt) + STATE_READING_ATOM_HEADER;
            Arrays.fill(trackFragment.sampleHasSubsampleEncryptionTable, STATE_READING_ATOM_HEADER, readUnsignedIntToInt, z);
        }
        trackFragment.initEncryptionData(readUnsignedByte);
    }

    private static void parseSenc(ParsableByteArray parsableByteArray, int i, TrackFragment trackFragment) {
        parsableByteArray.setPosition(i + 8);
        int parseFullAtomFlags = Atom.parseFullAtomFlags(parsableByteArray.readInt());
        if ((parseFullAtomFlags & STATE_READING_ATOM_PAYLOAD) != 0) {
            throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
        }
        boolean z = (parseFullAtomFlags & STATE_READING_ENCRYPTION_DATA) != 0;
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        if (readUnsignedIntToInt != trackFragment.length) {
            throw new ParserException("Length mismatch: " + readUnsignedIntToInt + ", " + trackFragment.length);
        }
        Arrays.fill(trackFragment.sampleHasSubsampleEncryptionTable, STATE_READING_ATOM_HEADER, readUnsignedIntToInt, z);
        trackFragment.initEncryptionData(parsableByteArray.bytesLeft());
        trackFragment.fillEncryptionData(parsableByteArray);
    }

    private static void parseSenc(ParsableByteArray parsableByteArray, TrackFragment trackFragment) {
        parseSenc(parsableByteArray, STATE_READING_ATOM_HEADER, trackFragment);
    }

    private static void parseSgpd(ParsableByteArray parsableByteArray, ParsableByteArray parsableByteArray2, TrackFragment trackFragment) {
        parsableByteArray.setPosition(8);
        int readInt = parsableByteArray.readInt();
        if (parsableByteArray.readInt() == SAMPLE_GROUP_TYPE_seig) {
            if (Atom.parseFullAtomVersion(readInt) == STATE_READING_ATOM_PAYLOAD) {
                parsableByteArray.skipBytes(STATE_READING_SAMPLE_CONTINUE);
            }
            if (parsableByteArray.readInt() != STATE_READING_ATOM_PAYLOAD) {
                throw new ParserException("Entry count in sbgp != 1 (unsupported).");
            }
            parsableByteArray2.setPosition(8);
            readInt = parsableByteArray2.readInt();
            if (parsableByteArray2.readInt() == SAMPLE_GROUP_TYPE_seig) {
                readInt = Atom.parseFullAtomVersion(readInt);
                if (readInt == STATE_READING_ATOM_PAYLOAD) {
                    if (parsableByteArray2.readUnsignedInt() == 0) {
                        throw new ParserException("Variable length decription in sgpd found (unsupported)");
                    }
                } else if (readInt >= STATE_READING_ENCRYPTION_DATA) {
                    parsableByteArray2.skipBytes(STATE_READING_SAMPLE_CONTINUE);
                }
                if (parsableByteArray2.readUnsignedInt() != 1) {
                    throw new ParserException("Entry count in sgpd != 1 (unsupported).");
                }
                parsableByteArray2.skipBytes(STATE_READING_ENCRYPTION_DATA);
                boolean z = parsableByteArray2.readUnsignedByte() == STATE_READING_ATOM_PAYLOAD;
                if (z) {
                    int readUnsignedByte = parsableByteArray2.readUnsignedByte();
                    byte[] bArr = new byte[16];
                    parsableByteArray2.readBytes(bArr, STATE_READING_ATOM_HEADER, bArr.length);
                    trackFragment.definesEncryptionData = true;
                    trackFragment.trackEncryptionBox = new TrackEncryptionBox(z, readUnsignedByte, bArr);
                }
            }
        }
    }

    private static ChunkIndex parseSidx(ParsableByteArray parsableByteArray, long j) {
        long readUnsignedInt;
        long readUnsignedInt2;
        parsableByteArray.setPosition(8);
        int parseFullAtomVersion = Atom.parseFullAtomVersion(parsableByteArray.readInt());
        parsableByteArray.skipBytes(STATE_READING_SAMPLE_CONTINUE);
        long readUnsignedInt3 = parsableByteArray.readUnsignedInt();
        if (parseFullAtomVersion == 0) {
            readUnsignedInt = parsableByteArray.readUnsignedInt() + j;
            readUnsignedInt2 = parsableByteArray.readUnsignedInt();
        } else {
            readUnsignedInt = parsableByteArray.readUnsignedLongToLong() + j;
            readUnsignedInt2 = parsableByteArray.readUnsignedLongToLong();
        }
        parsableByteArray.skipBytes(STATE_READING_ENCRYPTION_DATA);
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int[] iArr = new int[readUnsignedShort];
        long[] jArr = new long[readUnsignedShort];
        long[] jArr2 = new long[readUnsignedShort];
        long[] jArr3 = new long[readUnsignedShort];
        long j2 = readUnsignedInt;
        int i = STATE_READING_ATOM_HEADER;
        long j3 = readUnsignedInt2;
        readUnsignedInt2 = Util.scaleLargeTimestamp(readUnsignedInt2, C0700C.MICROS_PER_SECOND, readUnsignedInt3);
        while (i < readUnsignedShort) {
            int readInt = parsableByteArray.readInt();
            if ((TLRPC.MESSAGE_FLAG_MEGAGROUP & readInt) != 0) {
                throw new ParserException("Unhandled indirect reference");
            }
            long readUnsignedInt4 = parsableByteArray.readUnsignedInt();
            iArr[i] = readInt & ConnectionsManager.DEFAULT_DATACENTER_ID;
            jArr[i] = j2;
            jArr3[i] = readUnsignedInt2;
            readUnsignedInt2 = j3 + readUnsignedInt4;
            readUnsignedInt4 = Util.scaleLargeTimestamp(readUnsignedInt2, C0700C.MICROS_PER_SECOND, readUnsignedInt3);
            jArr2[i] = readUnsignedInt4 - jArr3[i];
            parsableByteArray.skipBytes(STATE_READING_SAMPLE_CONTINUE);
            j2 += (long) iArr[i];
            i += STATE_READING_ATOM_PAYLOAD;
            j3 = readUnsignedInt2;
            readUnsignedInt2 = readUnsignedInt4;
        }
        return new ChunkIndex(iArr, jArr, jArr2, jArr3);
    }

    private static long parseTfdt(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(8);
        return Atom.parseFullAtomVersion(parsableByteArray.readInt()) == STATE_READING_ATOM_PAYLOAD ? parsableByteArray.readUnsignedLongToLong() : parsableByteArray.readUnsignedInt();
    }

    private static TrackBundle parseTfhd(ParsableByteArray parsableByteArray, SparseArray<TrackBundle> sparseArray, int i) {
        parsableByteArray.setPosition(8);
        int parseFullAtomFlags = Atom.parseFullAtomFlags(parsableByteArray.readInt());
        int readInt = parsableByteArray.readInt();
        if ((i & STATE_READING_SAMPLE_CONTINUE) != 0) {
            readInt = STATE_READING_ATOM_HEADER;
        }
        TrackBundle trackBundle = (TrackBundle) sparseArray.get(readInt);
        if (trackBundle == null) {
            return null;
        }
        if ((parseFullAtomFlags & STATE_READING_ATOM_PAYLOAD) != 0) {
            long readUnsignedLongToLong = parsableByteArray.readUnsignedLongToLong();
            trackBundle.fragment.dataPosition = readUnsignedLongToLong;
            trackBundle.fragment.auxiliaryDataPosition = readUnsignedLongToLong;
        }
        DefaultSampleValues defaultSampleValues = trackBundle.defaultSampleValues;
        trackBundle.fragment.header = new DefaultSampleValues((parseFullAtomFlags & STATE_READING_ENCRYPTION_DATA) != 0 ? parsableByteArray.readUnsignedIntToInt() - 1 : defaultSampleValues.sampleDescriptionIndex, (parseFullAtomFlags & 8) != 0 ? parsableByteArray.readUnsignedIntToInt() : defaultSampleValues.duration, (parseFullAtomFlags & 16) != 0 ? parsableByteArray.readUnsignedIntToInt() : defaultSampleValues.size, (parseFullAtomFlags & 32) != 0 ? parsableByteArray.readUnsignedIntToInt() : defaultSampleValues.flags);
        return trackBundle;
    }

    private static void parseTraf(ContainerAtom containerAtom, SparseArray<TrackBundle> sparseArray, int i, byte[] bArr) {
        if (containerAtom.getChildAtomOfTypeCount(Atom.TYPE_trun) != STATE_READING_ATOM_PAYLOAD) {
            throw new ParserException("Trun count in traf != 1 (unsupported).");
        }
        TrackBundle parseTfhd = parseTfhd(containerAtom.getLeafAtomOfType(Atom.TYPE_tfhd).data, sparseArray, i);
        if (parseTfhd != null) {
            TrackFragment trackFragment = parseTfhd.fragment;
            long j = trackFragment.nextFragmentDecodeTime;
            parseTfhd.reset();
            if (containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt) != null && (i & STATE_READING_ENCRYPTION_DATA) == 0) {
                j = parseTfdt(containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt).data);
            }
            parseTrun(parseTfhd, j, i, containerAtom.getLeafAtomOfType(Atom.TYPE_trun).data);
            LeafAtom leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
            if (leafAtomOfType != null) {
                parseSaiz(parseTfhd.track.sampleDescriptionEncryptionBoxes[trackFragment.header.sampleDescriptionIndex], leafAtomOfType.data, trackFragment);
            }
            leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_saio);
            if (leafAtomOfType != null) {
                parseSaio(leafAtomOfType.data, trackFragment);
            }
            leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_senc);
            if (leafAtomOfType != null) {
                parseSenc(leafAtomOfType.data, trackFragment);
            }
            leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
            LeafAtom leafAtomOfType2 = containerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
            if (!(leafAtomOfType == null || leafAtomOfType2 == null)) {
                parseSgpd(leafAtomOfType.data, leafAtomOfType2.data, trackFragment);
            }
            int size = containerAtom.leafChildren.size();
            for (int i2 = STATE_READING_ATOM_HEADER; i2 < size; i2 += STATE_READING_ATOM_PAYLOAD) {
                leafAtomOfType = (LeafAtom) containerAtom.leafChildren.get(i2);
                if (leafAtomOfType.type == Atom.TYPE_uuid) {
                    parseUuid(leafAtomOfType.data, trackFragment, bArr);
                }
            }
        }
    }

    private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(12);
        return Pair.create(Integer.valueOf(parsableByteArray.readInt()), new DefaultSampleValues(parsableByteArray.readUnsignedIntToInt() - 1, parsableByteArray.readUnsignedIntToInt(), parsableByteArray.readUnsignedIntToInt(), parsableByteArray.readInt()));
    }

    private static void parseTrun(TrackBundle trackBundle, long j, int i, ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(8);
        int parseFullAtomFlags = Atom.parseFullAtomFlags(parsableByteArray.readInt());
        Track track = trackBundle.track;
        TrackFragment trackFragment = trackBundle.fragment;
        DefaultSampleValues defaultSampleValues = trackFragment.header;
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        if ((parseFullAtomFlags & STATE_READING_ATOM_PAYLOAD) != 0) {
            trackFragment.dataPosition += (long) parsableByteArray.readInt();
        }
        Object obj = (parseFullAtomFlags & STATE_READING_SAMPLE_CONTINUE) != 0 ? STATE_READING_ATOM_PAYLOAD : STATE_READING_ATOM_HEADER;
        int i2 = defaultSampleValues.flags;
        if (obj != null) {
            i2 = parsableByteArray.readUnsignedIntToInt();
        }
        Object obj2 = (parseFullAtomFlags & TLRPC.USER_FLAG_UNUSED2) != 0 ? STATE_READING_ATOM_PAYLOAD : STATE_READING_ATOM_HEADER;
        Object obj3 = (parseFullAtomFlags & TLRPC.USER_FLAG_UNUSED3) != 0 ? STATE_READING_ATOM_PAYLOAD : STATE_READING_ATOM_HEADER;
        Object obj4 = (parseFullAtomFlags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0 ? STATE_READING_ATOM_PAYLOAD : STATE_READING_ATOM_HEADER;
        Object obj5 = (parseFullAtomFlags & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0 ? STATE_READING_ATOM_PAYLOAD : STATE_READING_ATOM_HEADER;
        long scaleLargeTimestamp = (track.editListDurations != null && track.editListDurations.length == STATE_READING_ATOM_PAYLOAD && track.editListDurations[STATE_READING_ATOM_HEADER] == 0) ? Util.scaleLargeTimestamp(track.editListMediaTimes[STATE_READING_ATOM_HEADER], 1000, track.timescale) : 0;
        trackFragment.initTables(readUnsignedIntToInt);
        int[] iArr = trackFragment.sampleSizeTable;
        int[] iArr2 = trackFragment.sampleCompositionTimeOffsetTable;
        long[] jArr = trackFragment.sampleDecodingTimeTable;
        boolean[] zArr = trackFragment.sampleIsSyncFrameTable;
        long j2 = track.timescale;
        Object obj6 = (track.type != Track.TYPE_vide || (i & STATE_READING_ATOM_PAYLOAD) == 0) ? STATE_READING_ATOM_HEADER : STATE_READING_ATOM_PAYLOAD;
        int i3 = STATE_READING_ATOM_HEADER;
        long j3 = j;
        while (i3 < readUnsignedIntToInt) {
            int readUnsignedIntToInt2 = obj2 != null ? parsableByteArray.readUnsignedIntToInt() : defaultSampleValues.duration;
            int readUnsignedIntToInt3 = obj3 != null ? parsableByteArray.readUnsignedIntToInt() : defaultSampleValues.size;
            int readInt = (i3 != 0 || obj == null) ? obj4 != null ? parsableByteArray.readInt() : defaultSampleValues.flags : i2;
            if (obj5 != null) {
                iArr2[i3] = (int) (((long) (parsableByteArray.readInt() * PointerIconCompat.TYPE_DEFAULT)) / j2);
            } else {
                iArr2[i3] = STATE_READING_ATOM_HEADER;
            }
            jArr[i3] = Util.scaleLargeTimestamp(j3, 1000, j2) - scaleLargeTimestamp;
            iArr[i3] = readUnsignedIntToInt3;
            boolean z = ((readInt >> 16) & STATE_READING_ATOM_PAYLOAD) == 0 && (obj6 == null || i3 == 0);
            zArr[i3] = z;
            i3 += STATE_READING_ATOM_PAYLOAD;
            j3 += (long) readUnsignedIntToInt2;
        }
        trackFragment.nextFragmentDecodeTime = j3;
    }

    private static void parseUuid(ParsableByteArray parsableByteArray, TrackFragment trackFragment, byte[] bArr) {
        parsableByteArray.setPosition(8);
        parsableByteArray.readBytes(bArr, STATE_READING_ATOM_HEADER, 16);
        if (Arrays.equals(bArr, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
            parseSenc(parsableByteArray, 16, trackFragment);
        }
    }

    private void processAtomEnded(long j) {
        while (!this.containerAtoms.isEmpty() && ((ContainerAtom) this.containerAtoms.peek()).endPosition == j) {
            onContainerAtomRead((ContainerAtom) this.containerAtoms.pop());
        }
        enterReadingAtomHeaderState();
    }

    private boolean readAtomHeader(ExtractorInput extractorInput) {
        if (this.atomHeaderBytesRead == 0) {
            if (!extractorInput.readFully(this.atomHeader.data, STATE_READING_ATOM_HEADER, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(STATE_READING_ATOM_HEADER);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        if (this.atomSize == 1) {
            extractorInput.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        }
        long position = extractorInput.getPosition() - ((long) this.atomHeaderBytesRead);
        if (this.atomType == Atom.TYPE_moof) {
            int size = this.trackBundles.size();
            for (int i = STATE_READING_ATOM_HEADER; i < size; i += STATE_READING_ATOM_PAYLOAD) {
                TrackFragment trackFragment = ((TrackBundle) this.trackBundles.valueAt(i)).fragment;
                trackFragment.auxiliaryDataPosition = position;
                trackFragment.dataPosition = position;
            }
        }
        if (this.atomType == Atom.TYPE_mdat) {
            this.currentTrackBundle = null;
            this.endOfMdatPosition = this.atomSize + position;
            if (!this.haveOutputSeekMap) {
                this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
                this.haveOutputSeekMap = true;
            }
            this.parserState = STATE_READING_ENCRYPTION_DATA;
            return true;
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long position2 = (extractorInput.getPosition() + this.atomSize) - 8;
            this.containerAtoms.add(new ContainerAtom(this.atomType, position2));
            if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                processAtomEnded(position2);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            if (this.atomHeaderBytesRead != 8) {
                throw new ParserException("Leaf atom defines extended atom size (unsupported).");
            } else if (this.atomSize > 2147483647L) {
                throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
            } else {
                this.atomData = new ParsableByteArray((int) this.atomSize);
                System.arraycopy(this.atomHeader.data, STATE_READING_ATOM_HEADER, this.atomData.data, STATE_READING_ATOM_HEADER, 8);
                this.parserState = STATE_READING_ATOM_PAYLOAD;
            }
        } else if (this.atomSize > 2147483647L) {
            throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
        } else {
            this.atomData = null;
            this.parserState = STATE_READING_ATOM_PAYLOAD;
        }
        return true;
    }

    private void readAtomPayload(ExtractorInput extractorInput) {
        int i = ((int) this.atomSize) - this.atomHeaderBytesRead;
        if (this.atomData != null) {
            extractorInput.readFully(this.atomData.data, 8, i);
            onLeafAtomRead(new LeafAtom(this.atomType, this.atomData), extractorInput.getPosition());
        } else {
            extractorInput.skipFully(i);
        }
        processAtomEnded(extractorInput.getPosition());
    }

    private void readEncryptionData(ExtractorInput extractorInput) {
        TrackBundle trackBundle = null;
        long j = PtsTimestampAdjuster.DO_NOT_OFFSET;
        int size = this.trackBundles.size();
        int i = STATE_READING_ATOM_HEADER;
        while (i < size) {
            TrackBundle trackBundle2;
            long j2;
            TrackFragment trackFragment = ((TrackBundle) this.trackBundles.valueAt(i)).fragment;
            long j3;
            if (!trackFragment.sampleEncryptionDataNeedsFill || trackFragment.auxiliaryDataPosition >= j) {
                j3 = j;
                trackBundle2 = trackBundle;
                j2 = j3;
            } else {
                j3 = trackFragment.auxiliaryDataPosition;
                trackBundle2 = (TrackBundle) this.trackBundles.valueAt(i);
                j2 = j3;
            }
            i += STATE_READING_ATOM_PAYLOAD;
            trackBundle = trackBundle2;
            j = j2;
        }
        if (trackBundle == null) {
            this.parserState = STATE_READING_SAMPLE_START;
            return;
        }
        int position = (int) (j - extractorInput.getPosition());
        if (position < 0) {
            throw new ParserException("Offset to encryption data was negative.");
        }
        extractorInput.skipFully(position);
        trackBundle.fragment.fillEncryptionData(extractorInput);
    }

    private boolean readSample(ExtractorInput extractorInput) {
        int i = STATE_READING_ENCRYPTION_DATA;
        if (this.parserState == STATE_READING_SAMPLE_START) {
            if (this.currentTrackBundle == null) {
                this.currentTrackBundle = getNextFragmentRun(this.trackBundles);
                if (this.currentTrackBundle == null) {
                    i = (int) (this.endOfMdatPosition - extractorInput.getPosition());
                    if (i < 0) {
                        throw new ParserException("Offset to end of mdat was negative.");
                    }
                    extractorInput.skipFully(i);
                    enterReadingAtomHeaderState();
                    return false;
                }
                int position = (int) (this.currentTrackBundle.fragment.dataPosition - extractorInput.getPosition());
                if (position < 0) {
                    throw new ParserException("Offset to sample data was negative.");
                }
                extractorInput.skipFully(position);
            }
            this.sampleSize = this.currentTrackBundle.fragment.sampleSizeTable[this.currentTrackBundle.currentSampleIndex];
            if (this.currentTrackBundle.fragment.definesEncryptionData) {
                this.sampleBytesWritten = appendSampleEncryptionData(this.currentTrackBundle);
                this.sampleSize += this.sampleBytesWritten;
            } else {
                this.sampleBytesWritten = STATE_READING_ATOM_HEADER;
            }
            this.parserState = STATE_READING_SAMPLE_CONTINUE;
            this.sampleCurrentNalBytesRemaining = STATE_READING_ATOM_HEADER;
        }
        TrackFragment trackFragment = this.currentTrackBundle.fragment;
        Track track = this.currentTrackBundle.track;
        TrackOutput trackOutput = this.currentTrackBundle.output;
        int i2 = this.currentTrackBundle.currentSampleIndex;
        if (track.nalUnitLengthFieldLength != -1) {
            byte[] bArr = this.nalLength.data;
            bArr[STATE_READING_ATOM_HEADER] = (byte) 0;
            bArr[STATE_READING_ATOM_PAYLOAD] = (byte) 0;
            bArr[STATE_READING_ENCRYPTION_DATA] = (byte) 0;
            int i3 = track.nalUnitLengthFieldLength;
            int i4 = 4 - track.nalUnitLengthFieldLength;
            while (this.sampleBytesWritten < this.sampleSize) {
                if (this.sampleCurrentNalBytesRemaining == 0) {
                    extractorInput.readFully(this.nalLength.data, i4, i3);
                    this.nalLength.setPosition(STATE_READING_ATOM_HEADER);
                    this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
                    this.nalStartCode.setPosition(STATE_READING_ATOM_HEADER);
                    trackOutput.sampleData(this.nalStartCode, STATE_READING_SAMPLE_CONTINUE);
                    this.sampleBytesWritten += STATE_READING_SAMPLE_CONTINUE;
                    this.sampleSize += i4;
                } else {
                    int sampleData = trackOutput.sampleData(extractorInput, this.sampleCurrentNalBytesRemaining, false);
                    this.sampleBytesWritten += sampleData;
                    this.sampleCurrentNalBytesRemaining -= sampleData;
                }
            }
        } else {
            while (this.sampleBytesWritten < this.sampleSize) {
                this.sampleBytesWritten = trackOutput.sampleData(extractorInput, this.sampleSize - this.sampleBytesWritten, false) + this.sampleBytesWritten;
            }
        }
        long samplePresentationTime = trackFragment.getSamplePresentationTime(i2) * 1000;
        if (!trackFragment.definesEncryptionData) {
            i = STATE_READING_ATOM_HEADER;
        }
        i2 = (trackFragment.sampleIsSyncFrameTable[i2] ? STATE_READING_ATOM_PAYLOAD : STATE_READING_ATOM_HEADER) | i;
        i = trackFragment.header.sampleDescriptionIndex;
        byte[] bArr2 = null;
        if (trackFragment.definesEncryptionData) {
            bArr2 = trackFragment.trackEncryptionBox != null ? trackFragment.trackEncryptionBox.keyId : track.sampleDescriptionEncryptionBoxes[i].keyId;
        }
        trackOutput.sampleMetadata(samplePresentationTime, i2, this.sampleSize, STATE_READING_ATOM_HEADER, bArr2);
        TrackBundle trackBundle = this.currentTrackBundle;
        trackBundle.currentSampleIndex += STATE_READING_ATOM_PAYLOAD;
        if (this.currentTrackBundle.currentSampleIndex == trackFragment.length) {
            this.currentTrackBundle = null;
        }
        this.parserState = STATE_READING_SAMPLE_START;
        return true;
    }

    private static boolean shouldParseContainerAtom(int i) {
        return i == Atom.TYPE_moov || i == Atom.TYPE_trak || i == Atom.TYPE_mdia || i == Atom.TYPE_minf || i == Atom.TYPE_stbl || i == Atom.TYPE_moof || i == Atom.TYPE_traf || i == Atom.TYPE_mvex || i == Atom.TYPE_edts;
    }

    private static boolean shouldParseLeafAtom(int i) {
        return i == Atom.TYPE_hdlr || i == Atom.TYPE_mdhd || i == Atom.TYPE_mvhd || i == Atom.TYPE_sidx || i == Atom.TYPE_stsd || i == Atom.TYPE_tfdt || i == Atom.TYPE_tfhd || i == Atom.TYPE_tkhd || i == Atom.TYPE_trex || i == Atom.TYPE_trun || i == Atom.TYPE_pssh || i == Atom.TYPE_saiz || i == Atom.TYPE_saio || i == Atom.TYPE_senc || i == Atom.TYPE_sbgp || i == Atom.TYPE_sgpd || i == Atom.TYPE_uuid || i == Atom.TYPE_elst || i == Atom.TYPE_mehd || i == Atom.TYPE_emsg;
    }

    public final void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        if (this.sideloadedTrack != null) {
            TrackBundle trackBundle = new TrackBundle(extractorOutput.track(STATE_READING_ATOM_HEADER));
            trackBundle.init(this.sideloadedTrack, new DefaultSampleValues(STATE_READING_ATOM_HEADER, STATE_READING_ATOM_HEADER, STATE_READING_ATOM_HEADER, STATE_READING_ATOM_HEADER));
            this.trackBundles.put(STATE_READING_ATOM_HEADER, trackBundle);
            this.extractorOutput.endTracks();
        }
    }

    protected void parseEmsg(ParsableByteArray parsableByteArray, long j) {
    }

    public final int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        while (true) {
            switch (this.parserState) {
                case STATE_READING_ATOM_HEADER /*0*/:
                    if (readAtomHeader(extractorInput)) {
                        break;
                    }
                    return -1;
                case STATE_READING_ATOM_PAYLOAD /*1*/:
                    readAtomPayload(extractorInput);
                    break;
                case STATE_READING_ENCRYPTION_DATA /*2*/:
                    readEncryptionData(extractorInput);
                    break;
                default:
                    if (!readSample(extractorInput)) {
                        break;
                    }
                    return STATE_READING_ATOM_HEADER;
            }
        }
    }

    public final void release() {
    }

    public final void seek() {
        int size = this.trackBundles.size();
        for (int i = STATE_READING_ATOM_HEADER; i < size; i += STATE_READING_ATOM_PAYLOAD) {
            ((TrackBundle) this.trackBundles.valueAt(i)).reset();
        }
        this.containerAtoms.clear();
        enterReadingAtomHeaderState();
    }

    public final boolean sniff(ExtractorInput extractorInput) {
        return Sniffer.sniffFragmented(extractorInput);
    }
}
