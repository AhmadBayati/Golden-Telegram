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
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
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
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultMp4Builder implements Mp4Builder {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static Logger LOG;
    Set<StaticChunkOffsetBox> chunkOffsetBoxes;
    private FragmentIntersectionFinder intersectionFinder;
    Set<SampleAuxiliaryInformationOffsetsBox> sampleAuxiliaryInformationOffsetsBoxes;
    HashMap<Track, List<Sample>> track2Sample;
    HashMap<Track, long[]> track2SampleSizes;

    private class InterleaveChunkMdat implements Box {
        List<List<Sample>> chunkList;
        long contentSize;
        Container parent;
        List<Track> tracks;

        private InterleaveChunkMdat(Movie movie, Map<Track, int[]> map, long j) {
            this.chunkList = new ArrayList();
            this.contentSize = j;
            this.tracks = movie.getTracks();
            for (int i = 0; i < ((int[]) map.values().iterator().next()).length; i++) {
                for (Track track : this.tracks) {
                    int[] iArr = (int[]) map.get(track);
                    long j2 = 0;
                    for (int i2 = 0; i2 < i; i2++) {
                        j2 += (long) iArr[i2];
                    }
                    this.chunkList.add(((List) DefaultMp4Builder.this.track2Sample.get(track)).subList(CastUtils.l2i(j2), CastUtils.l2i(j2 + ((long) iArr[i]))));
                }
            }
        }

        private boolean isSmallBox(long j) {
            return 8 + j < 4294967296L;
        }

        public void getBox(WritableByteChannel writableByteChannel) {
            ByteBuffer allocate = ByteBuffer.allocate(16);
            long size = getSize();
            if (isSmallBox(size)) {
                IsoTypeWriter.writeUInt32(allocate, size);
            } else {
                IsoTypeWriter.writeUInt32(allocate, 1);
            }
            allocate.put(IsoFile.fourCCtoBytes(MediaDataBox.TYPE));
            if (isSmallBox(size)) {
                allocate.put(new byte[8]);
            } else {
                IsoTypeWriter.writeUInt64(allocate, size);
            }
            allocate.rewind();
            writableByteChannel.write(allocate);
            for (List<Sample> it : this.chunkList) {
                for (Sample writeTo : it) {
                    writeTo.writeTo(writableByteChannel);
                }
            }
        }

        public long getDataOffset() {
            long j = 16;
            for (Container container = this; container instanceof Box; container = ((Box) container).getParent()) {
                for (Object obj : ((Box) container).getParent().getBoxes()) {
                    if (container == obj) {
                        break;
                    }
                    j += obj.getSize();
                }
            }
            return j;
        }

        public long getOffset() {
            throw new RuntimeException("Doesn't have any meaning for programmatically created boxes");
        }

        public Container getParent() {
            return this.parent;
        }

        public long getSize() {
            return 16 + this.contentSize;
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
        $assertionsDisabled = !DefaultMp4Builder.class.desiredAssertionStatus();
        LOG = Logger.getLogger(DefaultMp4Builder.class.getName());
    }

    public DefaultMp4Builder() {
        this.chunkOffsetBoxes = new HashSet();
        this.sampleAuxiliaryInformationOffsetsBoxes = new HashSet();
        this.track2Sample = new HashMap();
        this.track2SampleSizes = new HashMap();
    }

    public static long gcd(long j, long j2) {
        return j2 == 0 ? j : gcd(j2, j % j2);
    }

    private static long sum(int[] iArr) {
        long j = 0;
        for (int i : iArr) {
            j += (long) i;
        }
        return j;
    }

    private static long sum(long[] jArr) {
        long j = 0;
        for (long j2 : jArr) {
            j += j2;
        }
        return j;
    }

    public Container build(Movie movie) {
        if (this.intersectionFinder == null) {
            this.intersectionFinder = new TwoSecondIntersectionFinder(movie, 2);
        }
        LOG.fine("Creating movie " + movie);
        for (Track track : movie.getTracks()) {
            List samples = track.getSamples();
            putSamples(track, samples);
            Object obj = new long[samples.size()];
            for (int i = 0; i < obj.length; i++) {
                obj[i] = ((Sample) samples.get(i)).getSize();
            }
            this.track2SampleSizes.put(track, obj);
        }
        Container basicContainer = new BasicContainer();
        basicContainer.addBox(createFileTypeBox(movie));
        Map hashMap = new HashMap();
        for (Track track2 : movie.getTracks()) {
            hashMap.put(track2, getChunkSizes(track2, movie));
        }
        Box createMovieBox = createMovieBox(movie, hashMap);
        basicContainer.addBox(createMovieBox);
        long j = 0;
        for (SampleSizeBox sampleSizes : Path.getPaths(createMovieBox, "trak/mdia/minf/stbl/stsz")) {
            j += sum(sampleSizes.getSampleSizes());
        }
        Object interleaveChunkMdat = new InterleaveChunkMdat(movie, hashMap, j, null);
        basicContainer.addBox(interleaveChunkMdat);
        long dataOffset = interleaveChunkMdat.getDataOffset();
        for (StaticChunkOffsetBox chunkOffsets : this.chunkOffsetBoxes) {
            long[] chunkOffsets2 = chunkOffsets.getChunkOffsets();
            for (int i2 = 0; i2 < chunkOffsets2.length; i2++) {
                chunkOffsets2[i2] = chunkOffsets2[i2] + dataOffset;
            }
        }
        for (SampleAuxiliaryInformationOffsetsBox sampleAuxiliaryInformationOffsetsBox : this.sampleAuxiliaryInformationOffsetsBoxes) {
            j = sampleAuxiliaryInformationOffsetsBox.getSize() + 44;
            Box box = sampleAuxiliaryInformationOffsetsBox;
            while (true) {
                Container parent = box.getParent();
                for (Box box2 : parent.getBoxes()) {
                    if (box2 == box) {
                        break;
                    }
                    j += box2.getSize();
                }
                if (!(parent instanceof Box)) {
                    break;
                }
                Object obj2 = parent;
            }
            long[] offsets = sampleAuxiliaryInformationOffsetsBox.getOffsets();
            for (int i3 = 0; i3 < offsets.length; i3++) {
                offsets[i3] = offsets[i3] + j;
            }
            sampleAuxiliaryInformationOffsetsBox.setOffsets(offsets);
        }
        return basicContainer;
    }

    protected void createCencBoxes(CencEncryptedTrack cencEncryptedTrack, SampleTableBox sampleTableBox, int[] iArr) {
        int i;
        SampleAuxiliaryInformationSizesBox sampleAuxiliaryInformationSizesBox = new SampleAuxiliaryInformationSizesBox();
        sampleAuxiliaryInformationSizesBox.setAuxInfoType("cenc");
        sampleAuxiliaryInformationSizesBox.setFlags(1);
        List sampleEncryptionEntries = cencEncryptedTrack.getSampleEncryptionEntries();
        if (cencEncryptedTrack.hasSubSampleEncryption()) {
            short[] sArr = new short[sampleEncryptionEntries.size()];
            for (i = 0; i < sArr.length; i++) {
                sArr[i] = (short) ((CencSampleAuxiliaryDataFormat) sampleEncryptionEntries.get(i)).getSize();
            }
            sampleAuxiliaryInformationSizesBox.setSampleInfoSizes(sArr);
        } else {
            sampleAuxiliaryInformationSizesBox.setDefaultSampleInfoSize(8);
            sampleAuxiliaryInformationSizesBox.setSampleCount(cencEncryptedTrack.getSamples().size());
        }
        Box sampleAuxiliaryInformationOffsetsBox = new SampleAuxiliaryInformationOffsetsBox();
        Box sampleEncryptionBox = new SampleEncryptionBox();
        sampleEncryptionBox.setSubSampleEncryption(cencEncryptedTrack.hasSubSampleEncryption());
        sampleEncryptionBox.setEntries(sampleEncryptionEntries);
        long offsetToFirstIV = (long) sampleEncryptionBox.getOffsetToFirstIV();
        long[] jArr = new long[iArr.length];
        int i2 = 0;
        i = 0;
        while (i < iArr.length) {
            jArr[i] = offsetToFirstIV;
            long j = offsetToFirstIV;
            int i3 = i2;
            int i4 = 0;
            while (i4 < iArr[i]) {
                j += (long) ((CencSampleAuxiliaryDataFormat) sampleEncryptionEntries.get(i3)).getSize();
                i4++;
                i3++;
            }
            i++;
            i2 = i3;
            offsetToFirstIV = j;
        }
        sampleAuxiliaryInformationOffsetsBox.setOffsets(jArr);
        sampleTableBox.addBox(sampleAuxiliaryInformationSizesBox);
        sampleTableBox.addBox(sampleAuxiliaryInformationOffsetsBox);
        sampleTableBox.addBox(sampleEncryptionBox);
        this.sampleAuxiliaryInformationOffsetsBoxes.add(sampleAuxiliaryInformationOffsetsBox);
    }

    protected void createCtts(Track track, SampleTableBox sampleTableBox) {
        List compositionTimeEntries = track.getCompositionTimeEntries();
        if (compositionTimeEntries != null && !compositionTimeEntries.isEmpty()) {
            Box compositionTimeToSample = new CompositionTimeToSample();
            compositionTimeToSample.setEntries(compositionTimeEntries);
            sampleTableBox.addBox(compositionTimeToSample);
        }
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

    protected FileTypeBox createFileTypeBox(Movie movie) {
        List linkedList = new LinkedList();
        linkedList.add("isom");
        linkedList.add("iso2");
        linkedList.add(VisualSampleEntry.TYPE3);
        return new FileTypeBox("isom", 0, linkedList);
    }

    protected MovieBox createMovieBox(Movie movie, Map<Track, int[]> map) {
        MovieBox movieBox = new MovieBox();
        Box movieHeaderBox = new MovieHeaderBox();
        movieHeaderBox.setCreationTime(new Date());
        movieHeaderBox.setModificationTime(new Date());
        movieHeaderBox.setMatrix(movie.getMatrix());
        long timescale = getTimescale(movie);
        long j = 0;
        for (Track track : movie.getTracks()) {
            long duration;
            if (track.getEdits() == null || track.getEdits().isEmpty()) {
                duration = (track.getDuration() * getTimescale(movie)) / track.getTrackMetaData().getTimescale();
            } else {
                long j2 = 0;
                for (Edit segmentDuration : track.getEdits()) {
                    j2 = ((long) segmentDuration.getSegmentDuration()) + j2;
                }
                duration = getTimescale(movie) * j2;
            }
            if (duration > j) {
                j = duration;
            }
        }
        movieHeaderBox.setDuration(j);
        movieHeaderBox.setTimescale(timescale);
        j = 0;
        for (Track track2 : movie.getTracks()) {
            j = j < track2.getTrackMetaData().getTrackId() ? track2.getTrackMetaData().getTrackId() : j;
        }
        movieHeaderBox.setNextTrackId(1 + j);
        movieBox.addBox(movieHeaderBox);
        for (Track track22 : movie.getTracks()) {
            movieBox.addBox(createTrackBox(track22, movie, map));
        }
        Box createUdta = createUdta(movie);
        if (createUdta != null) {
            movieBox.addBox(createUdta);
        }
        return movieBox;
    }

    protected void createSdtp(Track track, SampleTableBox sampleTableBox) {
        if (track.getSampleDependencies() != null && !track.getSampleDependencies().isEmpty()) {
            Box sampleDependencyTypeBox = new SampleDependencyTypeBox();
            sampleDependencyTypeBox.setEntries(track.getSampleDependencies());
            sampleTableBox.addBox(sampleDependencyTypeBox);
        }
    }

    protected Box createStbl(Track track, Movie movie, Map<Track, int[]> map) {
        Box sampleTableBox = new SampleTableBox();
        createStsd(track, sampleTableBox);
        createStts(track, sampleTableBox);
        createCtts(track, sampleTableBox);
        createStss(track, sampleTableBox);
        createSdtp(track, sampleTableBox);
        createStsc(track, map, sampleTableBox);
        createStsz(track, sampleTableBox);
        createStco(track, movie, map, sampleTableBox);
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
            int i = 0;
            while (i < track.getSamples().size()) {
                SampleToGroupBox.Entry entry4;
                int i2 = 0;
                for (int i3 = 0; i3 < ((List) entry2.getValue()).size(); i3++) {
                    if (Arrays.binarySearch((long[]) track.getSampleGroups().get((GroupEntry) ((List) entry2.getValue()).get(i3)), (long) i) >= 0) {
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
                i++;
                entry3 = entry4;
            }
            sampleTableBox.addBox(sampleGroupDescriptionBox);
            sampleTableBox.addBox(sampleToGroupBox);
        }
        if (track instanceof CencEncryptedTrack) {
            createCencBoxes((CencEncryptedTrack) track, sampleTableBox, (int[]) map.get(track));
        }
        createSubs(track, sampleTableBox);
        return sampleTableBox;
    }

    protected void createStco(Track track, Movie movie, Map<Track, int[]> map, SampleTableBox sampleTableBox) {
        int[] iArr = (int[]) map.get(track);
        Box staticChunkOffsetBox = new StaticChunkOffsetBox();
        this.chunkOffsetBoxes.add(staticChunkOffsetBox);
        long j = 0;
        long[] jArr = new long[iArr.length];
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId());
        }
        int i = 0;
        while (i < iArr.length) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId() + " chunk " + i);
            }
            long j2 = j;
            for (Track track2 : movie.getTracks()) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Adding offsets of track_" + track2.getTrackMetaData().getTrackId());
                }
                int[] iArr2 = (int[]) map.get(track2);
                long j3 = 0;
                for (int i2 = 0; i2 < i; i2++) {
                    j3 += (long) iArr2[i2];
                }
                if (track2 == track) {
                    jArr[i] = j2;
                }
                int l2i = CastUtils.l2i(j3);
                while (true) {
                    if (((long) l2i) < ((long) iArr2[i]) + j3) {
                        j2 += ((long[]) this.track2SampleSizes.get(track2))[l2i];
                        l2i++;
                    }
                }
            }
            i++;
            j = j2;
        }
        staticChunkOffsetBox.setChunkOffsets(jArr);
        sampleTableBox.addBox(staticChunkOffsetBox);
    }

    protected void createStsc(Track track, Map<Track, int[]> map, SampleTableBox sampleTableBox) {
        int[] iArr = (int[]) map.get(track);
        Box sampleToChunkBox = new SampleToChunkBox();
        sampleToChunkBox.setEntries(new LinkedList());
        long j = -2147483648L;
        for (int i = 0; i < iArr.length; i++) {
            if (j != ((long) iArr[i])) {
                sampleToChunkBox.getEntries().add(new SampleToChunkBox.Entry((long) (i + 1), (long) iArr[i], 1));
                j = (long) iArr[i];
            }
        }
        sampleTableBox.addBox(sampleToChunkBox);
    }

    protected void createStsd(Track track, SampleTableBox sampleTableBox) {
        sampleTableBox.addBox(track.getSampleDescriptionBox());
    }

    protected void createStss(Track track, SampleTableBox sampleTableBox) {
        long[] syncSamples = track.getSyncSamples();
        if (syncSamples != null && syncSamples.length > 0) {
            Box syncSampleBox = new SyncSampleBox();
            syncSampleBox.setSampleNumber(syncSamples);
            sampleTableBox.addBox(syncSampleBox);
        }
    }

    protected void createStsz(Track track, SampleTableBox sampleTableBox) {
        Box sampleSizeBox = new SampleSizeBox();
        sampleSizeBox.setSampleSizes((long[]) this.track2SampleSizes.get(track));
        sampleTableBox.addBox(sampleSizeBox);
    }

    protected void createStts(Track track, SampleTableBox sampleTableBox) {
        List arrayList = new ArrayList();
        TimeToSampleBox.Entry entry = null;
        for (long j : track.getSampleDurations()) {
            if (entry == null || entry.getDelta() != j) {
                entry = new TimeToSampleBox.Entry(1, j);
                arrayList.add(entry);
            } else {
                entry.setCount(entry.getCount() + 1);
            }
        }
        Box timeToSampleBox = new TimeToSampleBox();
        timeToSampleBox.setEntries(arrayList);
        sampleTableBox.addBox(timeToSampleBox);
    }

    protected void createSubs(Track track, SampleTableBox sampleTableBox) {
        if (track.getSubsampleInformationBox() != null) {
            sampleTableBox.addBox(track.getSubsampleInformationBox());
        }
    }

    protected TrackBox createTrackBox(Track track, Movie movie, Map<Track, int[]> map) {
        TrackBox trackBox = new TrackBox();
        Box trackHeaderBox = new TrackHeaderBox();
        trackHeaderBox.setEnabled(true);
        trackHeaderBox.setInMovie(true);
        trackHeaderBox.setInPreview(true);
        trackHeaderBox.setInPoster(true);
        trackHeaderBox.setMatrix(track.getTrackMetaData().getMatrix());
        trackHeaderBox.setAlternateGroup(track.getTrackMetaData().getGroup());
        trackHeaderBox.setCreationTime(track.getTrackMetaData().getCreationTime());
        if (track.getEdits() == null || track.getEdits().isEmpty()) {
            trackHeaderBox.setDuration((track.getDuration() * getTimescale(movie)) / track.getTrackMetaData().getTimescale());
        } else {
            long j = 0;
            for (Edit segmentDuration : track.getEdits()) {
                j = ((long) segmentDuration.getSegmentDuration()) + j;
            }
            trackHeaderBox.setDuration(track.getTrackMetaData().getTimescale() * j);
        }
        trackHeaderBox.setHeight(track.getTrackMetaData().getHeight());
        trackHeaderBox.setWidth(track.getTrackMetaData().getWidth());
        trackHeaderBox.setLayer(track.getTrackMetaData().getLayer());
        trackHeaderBox.setModificationTime(new Date());
        trackHeaderBox.setTrackId(track.getTrackMetaData().getTrackId());
        trackHeaderBox.setVolume(track.getTrackMetaData().getVolume());
        trackBox.addBox(trackHeaderBox);
        trackBox.addBox(createEdts(track, movie));
        MediaBox mediaBox = new MediaBox();
        trackBox.addBox(mediaBox);
        Box mediaHeaderBox = new MediaHeaderBox();
        mediaHeaderBox.setCreationTime(track.getTrackMetaData().getCreationTime());
        mediaHeaderBox.setDuration(track.getDuration());
        mediaHeaderBox.setTimescale(track.getTrackMetaData().getTimescale());
        mediaHeaderBox.setLanguage(track.getTrackMetaData().getLanguage());
        mediaBox.addBox(mediaHeaderBox);
        Object handlerBox = new HandlerBox();
        mediaBox.addBox(handlerBox);
        handlerBox.setHandlerType(track.getHandler());
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
        Box dataInformationBox = new DataInformationBox();
        Object dataReferenceBox = new DataReferenceBox();
        dataInformationBox.addBox(dataReferenceBox);
        trackHeaderBox = new DataEntryUrlBox();
        trackHeaderBox.setFlags(1);
        dataReferenceBox.addBox(trackHeaderBox);
        mediaInformationBox.addBox(dataInformationBox);
        mediaInformationBox.addBox(createStbl(track, movie, map));
        mediaBox.addBox(mediaInformationBox);
        return trackBox;
    }

    protected Box createUdta(Movie movie) {
        return null;
    }

    int[] getChunkSizes(Track track, Movie movie) {
        long[] sampleNumbers = this.intersectionFinder.sampleNumbers(track);
        int[] iArr = new int[sampleNumbers.length];
        for (int i = 0; i < sampleNumbers.length; i++) {
            iArr[i] = CastUtils.l2i((sampleNumbers.length == i + 1 ? (long) track.getSamples().size() : sampleNumbers[i + 1] - 1) - (sampleNumbers[i] - 1));
        }
        if ($assertionsDisabled || ((long) ((List) this.track2Sample.get(track)).size()) == sum(iArr)) {
            return iArr;
        }
        throw new AssertionError("The number of samples and the sum of all chunk lengths must be equal");
    }

    public long getTimescale(Movie movie) {
        long timescale = ((Track) movie.getTracks().iterator().next()).getTrackMetaData().getTimescale();
        long j = timescale;
        for (Track trackMetaData : movie.getTracks()) {
            j = gcd(trackMetaData.getTrackMetaData().getTimescale(), j);
        }
        return j;
    }

    protected List<Sample> putSamples(Track track, List<Sample> list) {
        return (List) this.track2Sample.put(track, list);
    }

    public void setIntersectionFinder(FragmentIntersectionFinder fragmentIntersectionFinder) {
        this.intersectionFinder = fragmentIntersectionFinder;
    }
}
