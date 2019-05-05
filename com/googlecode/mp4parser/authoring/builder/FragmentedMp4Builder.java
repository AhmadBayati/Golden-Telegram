package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.EditListBox.Entry;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.HintMediaHeaderBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.NullMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessOffsetBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.boxes.dece.SampleEncryptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FragmentedMp4Builder implements Mp4Builder {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final Logger LOG;
    protected FragmentIntersectionFinder intersectionFinder;

    /* renamed from: com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder.1 */
    class C03201 implements Comparator<Track> {
        private final /* synthetic */ int val$cycle;
        private final /* synthetic */ Map val$intersectionMap;

        C03201(Map map, int i) {
            this.val$intersectionMap = map;
            this.val$cycle = i;
        }

        public int compare(Track track, Track track2) {
            int i;
            long j = ((long[]) this.val$intersectionMap.get(track))[this.val$cycle];
            long j2 = ((long[]) this.val$intersectionMap.get(track2))[this.val$cycle];
            long[] sampleDurations = track.getSampleDurations();
            long[] sampleDurations2 = track2.getSampleDurations();
            long j3 = 0;
            long j4 = 0;
            for (i = 1; ((long) i) < j; i++) {
                j3 += sampleDurations[i - 1];
            }
            for (i = 1; ((long) i) < j2; i++) {
                j4 += sampleDurations2[i - 1];
            }
            return (int) (((((double) j3) / ((double) track.getTrackMetaData().getTimescale())) - (((double) j4) / ((double) track2.getTrackMetaData().getTimescale()))) * 100.0d);
        }
    }

    /* renamed from: com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder.1Mdat */
    class AnonymousClass1Mdat implements Box {
        Container parent;
        long size_;
        private final /* synthetic */ long val$endSample;
        private final /* synthetic */ int val$i;
        private final /* synthetic */ long val$startSample;
        private final /* synthetic */ Track val$track;

        AnonymousClass1Mdat(long j, long j2, Track track, int i) {
            this.val$startSample = j;
            this.val$endSample = j2;
            this.val$track = track;
            this.val$i = i;
            this.size_ = -1;
        }

        public void getBox(WritableByteChannel writableByteChannel) {
            ByteBuffer allocate = ByteBuffer.allocate(8);
            IsoTypeWriter.writeUInt32(allocate, (long) CastUtils.l2i(getSize()));
            allocate.put(IsoFile.fourCCtoBytes(getType()));
            allocate.rewind();
            writableByteChannel.write(allocate);
            for (Sample writeTo : FragmentedMp4Builder.this.getSamples(this.val$startSample, this.val$endSample, this.val$track, this.val$i)) {
                writeTo.writeTo(writableByteChannel);
            }
        }

        public long getOffset() {
            throw new RuntimeException("Doesn't have any meaning for programmatically created boxes");
        }

        public Container getParent() {
            return this.parent;
        }

        public long getSize() {
            if (this.size_ != -1) {
                return this.size_;
            }
            long j = 8;
            for (Sample size : FragmentedMp4Builder.this.getSamples(this.val$startSample, this.val$endSample, this.val$track, this.val$i)) {
                j = size.getSize() + j;
            }
            this.size_ = j;
            return j;
        }

        public String getType() {
            return MediaDataBox.TYPE;
        }

        public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        }

        public void setParent(Container container) {
            this.parent = container;
        }
    }

    static {
        $assertionsDisabled = !FragmentedMp4Builder.class.desiredAssertionStatus();
        LOG = Logger.getLogger(FragmentedMp4Builder.class.getName());
    }

    private long getTrackDuration(Movie movie, Track track) {
        return (track.getDuration() * movie.getTimescale()) / track.getTrackMetaData().getTimescale();
    }

    public Container build(Movie movie) {
        LOG.fine("Creating movie " + movie);
        if (this.intersectionFinder == null) {
            for (Track track : movie.getTracks()) {
                if (track.getHandler().equals("vide")) {
                    break;
                }
            }
            Track track2 = null;
            this.intersectionFinder = new SyncSampleIntersectFinderImpl(movie, track2, -1);
        }
        Container basicContainer = new BasicContainer();
        basicContainer.addBox(createFtyp(movie));
        basicContainer.addBox(createMoov(movie));
        for (Box addBox : createMoofMdat(movie)) {
            basicContainer.addBox(addBox);
        }
        basicContainer.addBox(createMfra(movie, basicContainer));
        return basicContainer;
    }

    protected DataInformationBox createDinf(Movie movie, Track track) {
        DataInformationBox dataInformationBox = new DataInformationBox();
        Object dataReferenceBox = new DataReferenceBox();
        dataInformationBox.addBox(dataReferenceBox);
        Box dataEntryUrlBox = new DataEntryUrlBox();
        dataEntryUrlBox.setFlags(1);
        dataReferenceBox.addBox(dataEntryUrlBox);
        return dataInformationBox;
    }

    protected Box createEdts(Track track, Movie movie) {
        if (track.getEdits() == null || track.getEdits().size() <= 0) {
            return null;
        }
        Box editListBox = new EditListBox();
        editListBox.setVersion(1);
        List arrayList = new ArrayList();
        for (Edit edit : track.getEdits()) {
            arrayList.add(new Entry(editListBox, Math.round(edit.getSegmentDuration() * ((double) movie.getTimescale())), (edit.getMediaTime() * track.getTrackMetaData().getTimescale()) / edit.getTimeScale(), edit.getMediaRate()));
        }
        editListBox.setEntries(arrayList);
        Box editBox = new EditBox();
        editBox.addBox(editListBox);
        return editBox;
    }

    protected int createFragment(List<Box> list, Track track, long[] jArr, int i, int i2) {
        if (i >= jArr.length) {
            return i2;
        }
        long j = jArr[i];
        long size = i + 1 < jArr.length ? jArr[i + 1] : (long) (track.getSamples().size() + 1);
        if (j == size) {
            return i2;
        }
        list.add(createMoof(j, size, track, i2));
        int i3 = i2 + 1;
        list.add(createMdat(j, size, track, i2));
        return i3;
    }

    public Box createFtyp(Movie movie) {
        List linkedList = new LinkedList();
        linkedList.add("isom");
        linkedList.add("iso2");
        linkedList.add(VisualSampleEntry.TYPE3);
        return new FileTypeBox("isom", 0, linkedList);
    }

    protected Box createMdat(long j, long j2, Track track, int i) {
        return new AnonymousClass1Mdat(j, j2, track, i);
    }

    protected Box createMdhd(Movie movie, Track track) {
        Box mediaHeaderBox = new MediaHeaderBox();
        mediaHeaderBox.setCreationTime(track.getTrackMetaData().getCreationTime());
        mediaHeaderBox.setModificationTime(getDate());
        mediaHeaderBox.setDuration(0);
        mediaHeaderBox.setTimescale(track.getTrackMetaData().getTimescale());
        mediaHeaderBox.setLanguage(track.getTrackMetaData().getLanguage());
        return mediaHeaderBox;
    }

    protected Box createMdia(Track track, Movie movie) {
        Box mediaBox = new MediaBox();
        mediaBox.addBox(createMdhd(movie, track));
        mediaBox.addBox(createMdiaHdlr(track, movie));
        mediaBox.addBox(createMinf(track, movie));
        return mediaBox;
    }

    protected Box createMdiaHdlr(Track track, Movie movie) {
        Box handlerBox = new HandlerBox();
        handlerBox.setHandlerType(track.getHandler());
        return handlerBox;
    }

    protected void createMfhd(long j, long j2, Track track, int i, MovieFragmentBox movieFragmentBox) {
        Box movieFragmentHeaderBox = new MovieFragmentHeaderBox();
        movieFragmentHeaderBox.setSequenceNumber((long) i);
        movieFragmentBox.addBox(movieFragmentHeaderBox);
    }

    protected Box createMfra(Movie movie, Container container) {
        Box movieFragmentRandomAccessBox = new MovieFragmentRandomAccessBox();
        for (Track createTfra : movie.getTracks()) {
            movieFragmentRandomAccessBox.addBox(createTfra(createTfra, container));
        }
        Object movieFragmentRandomAccessOffsetBox = new MovieFragmentRandomAccessOffsetBox();
        movieFragmentRandomAccessBox.addBox(movieFragmentRandomAccessOffsetBox);
        movieFragmentRandomAccessOffsetBox.setMfraSize(movieFragmentRandomAccessBox.getSize());
        return movieFragmentRandomAccessBox;
    }

    protected Box createMinf(Track track, Movie movie) {
        MediaInformationBox mediaInformationBox = new MediaInformationBox();
        if (track.getHandler().equals("vide")) {
            mediaInformationBox.addBox(new VideoMediaHeaderBox());
        } else if (track.getHandler().equals("soun")) {
            mediaInformationBox.addBox(new SoundMediaHeaderBox());
        } else if (track.getHandler().equals(MimeTypes.BASE_TYPE_TEXT)) {
            mediaInformationBox.addBox(new NullMediaHeaderBox());
        } else if (track.getHandler().equals("subt")) {
            mediaInformationBox.addBox(new SubtitleMediaHeaderBox());
        } else if (track.getHandler().equals(TrackReferenceTypeBox.TYPE1)) {
            mediaInformationBox.addBox(new HintMediaHeaderBox());
        } else if (track.getHandler().equals("sbtl")) {
            mediaInformationBox.addBox(new NullMediaHeaderBox());
        }
        mediaInformationBox.addBox(createDinf(movie, track));
        mediaInformationBox.addBox(createStbl(movie, track));
        return mediaInformationBox;
    }

    protected Box createMoof(long j, long j2, Track track, int i) {
        Box movieFragmentBox = new MovieFragmentBox();
        createMfhd(j, j2, track, i, movieFragmentBox);
        createTraf(j, j2, track, i, movieFragmentBox);
        TrackRunBox trackRunBox = (TrackRunBox) movieFragmentBox.getTrackRunBoxes().get(0);
        trackRunBox.setDataOffset(1);
        trackRunBox.setDataOffset((int) (8 + movieFragmentBox.getSize()));
        return movieFragmentBox;
    }

    protected List<Box> createMoofMdat(Movie movie) {
        List<Box> linkedList = new LinkedList();
        Object hashMap = new HashMap();
        int i = 0;
        for (Track track : movie.getTracks()) {
            Object sampleNumbers = this.intersectionFinder.sampleNumbers(track);
            hashMap.put(track, sampleNumbers);
            i = Math.max(i, sampleNumbers.length);
        }
        int i2 = 1;
        for (int i3 = 0; i3 < i; i3++) {
            for (Track track2 : sortTracksInSequence(movie.getTracks(), i3, hashMap)) {
                i2 = createFragment(linkedList, track2, (long[]) hashMap.get(track2), i3, i2);
            }
        }
        return linkedList;
    }

    protected Box createMoov(Movie movie) {
        Box movieBox = new MovieBox();
        movieBox.addBox(createMvhd(movie));
        for (Track createTrak : movie.getTracks()) {
            movieBox.addBox(createTrak(createTrak, movie));
        }
        movieBox.addBox(createMvex(movie));
        return movieBox;
    }

    protected Box createMvex(Movie movie) {
        Box movieExtendsBox = new MovieExtendsBox();
        Box movieExtendsHeaderBox = new MovieExtendsHeaderBox();
        movieExtendsHeaderBox.setVersion(1);
        for (Track trackDuration : movie.getTracks()) {
            long trackDuration2 = getTrackDuration(movie, trackDuration);
            if (movieExtendsHeaderBox.getFragmentDuration() < trackDuration2) {
                movieExtendsHeaderBox.setFragmentDuration(trackDuration2);
            }
        }
        movieExtendsBox.addBox(movieExtendsHeaderBox);
        for (Track trackDuration3 : movie.getTracks()) {
            movieExtendsBox.addBox(createTrex(movie, trackDuration3));
        }
        return movieExtendsBox;
    }

    protected Box createMvhd(Movie movie) {
        Box movieHeaderBox = new MovieHeaderBox();
        movieHeaderBox.setVersion(1);
        movieHeaderBox.setCreationTime(getDate());
        movieHeaderBox.setModificationTime(getDate());
        movieHeaderBox.setDuration(0);
        movieHeaderBox.setTimescale(movie.getTimescale());
        long j = 0;
        for (Track track : movie.getTracks()) {
            j = j < track.getTrackMetaData().getTrackId() ? track.getTrackMetaData().getTrackId() : j;
        }
        movieHeaderBox.setNextTrackId(1 + j);
        return movieHeaderBox;
    }

    protected void createSaio(long j, long j2, CencEncryptedTrack cencEncryptedTrack, int i, TrackFragmentBox trackFragmentBox) {
        SchemeTypeBox schemeTypeBox = (SchemeTypeBox) Path.getPath(cencEncryptedTrack.getSampleDescriptionBox(), "enc.[0]/sinf[0]/schm[0]");
        SampleAuxiliaryInformationOffsetsBox sampleAuxiliaryInformationOffsetsBox = new SampleAuxiliaryInformationOffsetsBox();
        trackFragmentBox.addBox(sampleAuxiliaryInformationOffsetsBox);
        if ($assertionsDisabled || trackFragmentBox.getBoxes(TrackRunBox.class).size() == 1) {
            sampleAuxiliaryInformationOffsetsBox.setAuxInfoType("cenc");
            sampleAuxiliaryInformationOffsetsBox.setFlags(1);
            long j3 = 0 + 8;
            long j4 = j3;
            for (Box box : trackFragmentBox.getBoxes()) {
                if (box instanceof SampleEncryptionBox) {
                    j4 += (long) ((SampleEncryptionBox) box).getOffsetToFirstIV();
                    break;
                }
                j4 = box.getSize() + j4;
            }
            j4 += 16;
            for (Object obj : ((MovieFragmentBox) trackFragmentBox.getParent()).getBoxes()) {
                if (obj == trackFragmentBox) {
                    break;
                }
                j4 = obj.getSize() + j4;
            }
            sampleAuxiliaryInformationOffsetsBox.setOffsets(new long[]{j4});
            return;
        }
        throw new AssertionError("Don't know how to deal with multiple Track Run Boxes when encrypting");
    }

    protected void createSaiz(long j, long j2, CencEncryptedTrack cencEncryptedTrack, int i, TrackFragmentBox trackFragmentBox) {
        AbstractContainerBox sampleDescriptionBox = cencEncryptedTrack.getSampleDescriptionBox();
        SchemeTypeBox schemeTypeBox = (SchemeTypeBox) Path.getPath(sampleDescriptionBox, "enc.[0]/sinf[0]/schm[0]");
        TrackEncryptionBox trackEncryptionBox = (TrackEncryptionBox) Path.getPath(sampleDescriptionBox, "enc.[0]/sinf[0]/schi[0]/tenc[0]");
        SampleAuxiliaryInformationSizesBox sampleAuxiliaryInformationSizesBox = new SampleAuxiliaryInformationSizesBox();
        sampleAuxiliaryInformationSizesBox.setAuxInfoType("cenc");
        sampleAuxiliaryInformationSizesBox.setFlags(1);
        if (cencEncryptedTrack.hasSubSampleEncryption()) {
            short[] sArr = new short[CastUtils.l2i(j2 - j)];
            List subList = cencEncryptedTrack.getSampleEncryptionEntries().subList(CastUtils.l2i(j - 1), CastUtils.l2i(j2 - 1));
            for (int i2 = 0; i2 < sArr.length; i2++) {
                sArr[i2] = (short) ((CencSampleAuxiliaryDataFormat) subList.get(i2)).getSize();
            }
            sampleAuxiliaryInformationSizesBox.setSampleInfoSizes(sArr);
        } else {
            sampleAuxiliaryInformationSizesBox.setDefaultSampleInfoSize(trackEncryptionBox.getDefaultIvSize());
            sampleAuxiliaryInformationSizesBox.setSampleCount(CastUtils.l2i(j2 - j));
        }
        trackFragmentBox.addBox(sampleAuxiliaryInformationSizesBox);
    }

    protected void createSenc(long j, long j2, CencEncryptedTrack cencEncryptedTrack, int i, TrackFragmentBox trackFragmentBox) {
        Box sampleEncryptionBox = new SampleEncryptionBox();
        sampleEncryptionBox.setSubSampleEncryption(cencEncryptedTrack.hasSubSampleEncryption());
        sampleEncryptionBox.setEntries(cencEncryptedTrack.getSampleEncryptionEntries().subList(CastUtils.l2i(j - 1), CastUtils.l2i(j2 - 1)));
        trackFragmentBox.addBox(sampleEncryptionBox);
    }

    protected Box createStbl(Movie movie, Track track) {
        Box sampleTableBox = new SampleTableBox();
        createStsd(track, sampleTableBox);
        sampleTableBox.addBox(new TimeToSampleBox());
        sampleTableBox.addBox(new SampleToChunkBox());
        sampleTableBox.addBox(new SampleSizeBox());
        sampleTableBox.addBox(new StaticChunkOffsetBox());
        return sampleTableBox;
    }

    protected void createStsd(Track track, SampleTableBox sampleTableBox) {
        sampleTableBox.addBox(track.getSampleDescriptionBox());
    }

    protected void createTfdt(long j, Track track, TrackFragmentBox trackFragmentBox) {
        int i = 1;
        Box trackFragmentBaseMediaDecodeTimeBox = new TrackFragmentBaseMediaDecodeTimeBox();
        trackFragmentBaseMediaDecodeTimeBox.setVersion(1);
        long j2 = 0;
        long[] sampleDurations = track.getSampleDurations();
        while (((long) i) < j) {
            j2 += sampleDurations[i - 1];
            i++;
        }
        trackFragmentBaseMediaDecodeTimeBox.setBaseMediaDecodeTime(j2);
        trackFragmentBox.addBox(trackFragmentBaseMediaDecodeTimeBox);
    }

    protected void createTfhd(long j, long j2, Track track, int i, TrackFragmentBox trackFragmentBox) {
        Box trackFragmentHeaderBox = new TrackFragmentHeaderBox();
        trackFragmentHeaderBox.setDefaultSampleFlags(new SampleFlags());
        trackFragmentHeaderBox.setBaseDataOffset(-1);
        trackFragmentHeaderBox.setTrackId(track.getTrackMetaData().getTrackId());
        trackFragmentHeaderBox.setDefaultBaseIsMoof(true);
        trackFragmentBox.addBox(trackFragmentHeaderBox);
    }

    protected Box createTfra(Track track, Container container) {
        Box trackFragmentRandomAccessBox = new TrackFragmentRandomAccessBox();
        trackFragmentRandomAccessBox.setVersion(1);
        List linkedList = new LinkedList();
        TrackExtendsBox trackExtendsBox = null;
        for (TrackExtendsBox trackExtendsBox2 : Path.getPaths(container, "moov/mvex/trex")) {
            if (trackExtendsBox2.getTrackId() == track.getTrackMetaData().getTrackId()) {
                trackExtendsBox = trackExtendsBox2;
            }
        }
        long j = 0;
        long j2 = 0;
        for (Box box : container.getBoxes()) {
            if (box instanceof MovieFragmentBox) {
                List boxes = ((MovieFragmentBox) box).getBoxes(TrackFragmentBox.class);
                for (int i = 0; i < boxes.size(); i++) {
                    TrackFragmentBox trackFragmentBox = (TrackFragmentBox) boxes.get(i);
                    if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == track.getTrackMetaData().getTrackId()) {
                        List boxes2 = trackFragmentBox.getBoxes(TrackRunBox.class);
                        for (int i2 = 0; i2 < boxes2.size(); i2++) {
                            List linkedList2 = new LinkedList();
                            TrackRunBox trackRunBox = (TrackRunBox) boxes2.get(i2);
                            int i3 = 0;
                            while (i3 < trackRunBox.getEntries().size()) {
                                TrackRunBox.Entry entry = (TrackRunBox.Entry) trackRunBox.getEntries().get(i3);
                                SampleFlags firstSampleFlags = (i3 == 0 && trackRunBox.isFirstSampleFlagsPresent()) ? trackRunBox.getFirstSampleFlags() : trackRunBox.isSampleFlagsPresent() ? entry.getSampleFlags() : trackExtendsBox.getDefaultSampleFlags();
                                if (firstSampleFlags == null && track.getHandler().equals("vide")) {
                                    throw new RuntimeException("Cannot find SampleFlags for video track but it's required to build tfra");
                                }
                                if (firstSampleFlags == null || firstSampleFlags.getSampleDependsOn() == 2) {
                                    linkedList2.add(new TrackFragmentRandomAccessBox.Entry(j2, j, (long) (i + 1), (long) (i2 + 1), (long) (i3 + 1)));
                                }
                                j2 += entry.getSampleDuration();
                                i3++;
                            }
                            if (linkedList2.size() != trackRunBox.getEntries().size() || trackRunBox.getEntries().size() <= 0) {
                                linkedList.addAll(linkedList2);
                            } else {
                                linkedList.add((TrackFragmentRandomAccessBox.Entry) linkedList2.get(0));
                            }
                        }
                        continue;
                    }
                }
                continue;
            }
            j += box.getSize();
        }
        trackFragmentRandomAccessBox.setEntries(linkedList);
        trackFragmentRandomAccessBox.setTrackId(track.getTrackMetaData().getTrackId());
        return trackFragmentRandomAccessBox;
    }

    protected Box createTkhd(Movie movie, Track track) {
        Box trackHeaderBox = new TrackHeaderBox();
        trackHeaderBox.setVersion(1);
        trackHeaderBox.setFlags(7);
        trackHeaderBox.setAlternateGroup(track.getTrackMetaData().getGroup());
        trackHeaderBox.setCreationTime(track.getTrackMetaData().getCreationTime());
        trackHeaderBox.setDuration(0);
        trackHeaderBox.setHeight(track.getTrackMetaData().getHeight());
        trackHeaderBox.setWidth(track.getTrackMetaData().getWidth());
        trackHeaderBox.setLayer(track.getTrackMetaData().getLayer());
        trackHeaderBox.setModificationTime(getDate());
        trackHeaderBox.setTrackId(track.getTrackMetaData().getTrackId());
        trackHeaderBox.setVolume(track.getTrackMetaData().getVolume());
        return trackHeaderBox;
    }

    protected void createTraf(long j, long j2, Track track, int i, MovieFragmentBox movieFragmentBox) {
        TrackFragmentBox trackFragmentBox = new TrackFragmentBox();
        movieFragmentBox.addBox(trackFragmentBox);
        createTfhd(j, j2, track, i, trackFragmentBox);
        createTfdt(j, track, trackFragmentBox);
        createTrun(j, j2, track, i, trackFragmentBox);
        if (track instanceof CencEncryptedTrack) {
            createSaiz(j, j2, (CencEncryptedTrack) track, i, trackFragmentBox);
            createSenc(j, j2, (CencEncryptedTrack) track, i, trackFragmentBox);
            createSaio(j, j2, (CencEncryptedTrack) track, i, trackFragmentBox);
        }
        Map hashMap = new HashMap();
        for (Map.Entry entry : track.getSampleGroups().entrySet()) {
            String type = ((GroupEntry) entry.getKey()).getType();
            List list = (List) hashMap.get(type);
            if (list == null) {
                list = new ArrayList();
                hashMap.put(type, list);
            }
            list.add((GroupEntry) entry.getKey());
        }
        for (Map.Entry entry2 : hashMap.entrySet()) {
            Box sampleGroupDescriptionBox = new SampleGroupDescriptionBox();
            String str = (String) entry2.getKey();
            sampleGroupDescriptionBox.setGroupEntries((List) entry2.getValue());
            Box sampleToGroupBox = new SampleToGroupBox();
            sampleToGroupBox.setGroupingType(str);
            SampleToGroupBox.Entry entry3 = null;
            int l2i = CastUtils.l2i(j - 1);
            while (l2i < CastUtils.l2i(j2 - 1)) {
                SampleToGroupBox.Entry entry4;
                int i2 = 0;
                for (int i3 = 0; i3 < ((List) entry2.getValue()).size(); i3++) {
                    if (Arrays.binarySearch((long[]) track.getSampleGroups().get((GroupEntry) ((List) entry2.getValue()).get(i3)), (long) l2i) >= 0) {
                        i2 = i3 + 1;
                    }
                }
                if (entry3 == null || entry3.getGroupDescriptionIndex() != i2) {
                    entry4 = new SampleToGroupBox.Entry(1, i2);
                    sampleToGroupBox.getEntries().add(entry4);
                } else {
                    entry3.setSampleCount(entry3.getSampleCount() + 1);
                    entry4 = entry3;
                }
                l2i++;
                entry3 = entry4;
            }
            trackFragmentBox.addBox(sampleGroupDescriptionBox);
            trackFragmentBox.addBox(sampleToGroupBox);
        }
    }

    protected Box createTrak(Track track, Movie movie) {
        LOG.fine("Creating Track " + track);
        Box trackBox = new TrackBox();
        trackBox.addBox(createTkhd(movie, track));
        Box createEdts = createEdts(track, movie);
        if (createEdts != null) {
            trackBox.addBox(createEdts);
        }
        trackBox.addBox(createMdia(track, movie));
        return trackBox;
    }

    protected Box createTrex(Movie movie, Track track) {
        Box trackExtendsBox = new TrackExtendsBox();
        trackExtendsBox.setTrackId(track.getTrackMetaData().getTrackId());
        trackExtendsBox.setDefaultSampleDescriptionIndex(1);
        trackExtendsBox.setDefaultSampleDuration(0);
        trackExtendsBox.setDefaultSampleSize(0);
        SampleFlags sampleFlags = new SampleFlags();
        if ("soun".equals(track.getHandler()) || "subt".equals(track.getHandler())) {
            sampleFlags.setSampleDependsOn(2);
            sampleFlags.setSampleIsDependedOn(2);
        }
        trackExtendsBox.setDefaultSampleFlags(sampleFlags);
        return trackExtendsBox;
    }

    protected void createTrun(long j, long j2, Track track, int i, TrackFragmentBox trackFragmentBox) {
        long j3;
        Box trackRunBox = new TrackRunBox();
        trackRunBox.setVersion(1);
        long[] sampleSizes = getSampleSizes(j, j2, track, i);
        trackRunBox.setSampleDurationPresent(true);
        trackRunBox.setSampleSizePresent(true);
        List arrayList = new ArrayList(CastUtils.l2i(j2 - j));
        List compositionTimeEntries = track.getCompositionTimeEntries();
        CompositionTimeToSample.Entry[] entryArr = (compositionTimeEntries == null || compositionTimeEntries.size() <= 0) ? null : (CompositionTimeToSample.Entry[]) compositionTimeEntries.toArray(new CompositionTimeToSample.Entry[compositionTimeEntries.size()]);
        long count = (long) (entryArr != null ? entryArr[0].getCount() : -1);
        trackRunBox.setSampleCompositionTimeOffsetPresent(count > 0);
        long j4 = count;
        int i2 = 0;
        for (j3 = 1; j3 < j; j3++) {
            if (entryArr != null) {
                j4--;
                if (j4 == 0 && entryArr.length - i2 > 1) {
                    i2++;
                    j4 = (long) entryArr[i2].getCount();
                }
            }
        }
        boolean z = ((track.getSampleDependencies() == null || track.getSampleDependencies().isEmpty()) && (track.getSyncSamples() == null || track.getSyncSamples().length == 0)) ? false : true;
        trackRunBox.setSampleFlagsPresent(z);
        j3 = j4;
        for (int i3 = 0; i3 < sampleSizes.length; i3++) {
            TrackRunBox.Entry entry = new TrackRunBox.Entry();
            entry.setSampleSize(sampleSizes[i3]);
            if (z) {
                SampleFlags sampleFlags = new SampleFlags();
                if (!(track.getSampleDependencies() == null || track.getSampleDependencies().isEmpty())) {
                    SampleDependencyTypeBox.Entry entry2 = (SampleDependencyTypeBox.Entry) track.getSampleDependencies().get(i3);
                    sampleFlags.setSampleDependsOn(entry2.getSampleDependsOn());
                    sampleFlags.setSampleIsDependedOn(entry2.getSampleIsDependentOn());
                    sampleFlags.setSampleHasRedundancy(entry2.getSampleHasRedundancy());
                }
                if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                    if (Arrays.binarySearch(track.getSyncSamples(), ((long) i3) + j) >= 0) {
                        sampleFlags.setSampleIsDifferenceSample(false);
                        sampleFlags.setSampleDependsOn(2);
                    } else {
                        sampleFlags.setSampleIsDifferenceSample(true);
                        sampleFlags.setSampleDependsOn(1);
                    }
                }
                entry.setSampleFlags(sampleFlags);
            }
            entry.setSampleDuration(track.getSampleDurations()[CastUtils.l2i((((long) i3) + j) - 1)]);
            if (entryArr != null) {
                entry.setSampleCompositionTimeOffset(entryArr[i2].getOffset());
                j3--;
                if (j3 == 0 && entryArr.length - i2 > 1) {
                    i2++;
                    j3 = (long) entryArr[i2].getCount();
                }
            }
            arrayList.add(entry);
        }
        trackRunBox.setEntries(arrayList);
        trackFragmentBox.addBox(trackRunBox);
    }

    public Date getDate() {
        return new Date();
    }

    public FragmentIntersectionFinder getFragmentIntersectionFinder() {
        return this.intersectionFinder;
    }

    protected long[] getSampleSizes(long j, long j2, Track track, int i) {
        List samples = getSamples(j, j2, track, i);
        long[] jArr = new long[samples.size()];
        for (int i2 = 0; i2 < jArr.length; i2++) {
            jArr[i2] = ((Sample) samples.get(i2)).getSize();
        }
        return jArr;
    }

    protected List<Sample> getSamples(long j, long j2, Track track, int i) {
        return track.getSamples().subList(CastUtils.l2i(j) - 1, CastUtils.l2i(j2) - 1);
    }

    public void setIntersectionFinder(FragmentIntersectionFinder fragmentIntersectionFinder) {
        this.intersectionFinder = fragmentIntersectionFinder;
    }

    protected List<Track> sortTracksInSequence(List<Track> list, int i, Map<Track, long[]> map) {
        List<Track> linkedList = new LinkedList(list);
        Collections.sort(linkedList, new C03201(map, i));
        return linkedList;
    }
}
