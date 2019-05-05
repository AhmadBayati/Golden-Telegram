package com.googlecode.mp4parser.authoring.tracks;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.ProtectionSchemeInformationBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SchemeInformationBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.cenc.CencEncryptingSampleList;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.crypto.SecretKey;

public class CencEncryptingTrackImpl implements CencEncryptedTrack {
    List<CencSampleAuxiliaryDataFormat> cencSampleAuxiliaryData;
    UUID defaultKeyId;
    boolean dummyIvs;
    private final String encryptionAlgo;
    RangeStartMap<Integer, SecretKey> indexToKey;
    Map<UUID, SecretKey> keys;
    Map<GroupEntry, long[]> sampleGroups;
    List<Sample> samples;
    Track source;
    SampleDescriptionBox stsd;
    boolean subSampleEncryption;

    /* renamed from: com.googlecode.mp4parser.authoring.tracks.CencEncryptingTrackImpl.1 */
    class C03261 extends HashMap<GroupEntry, long[]> {
        C03261(Map map) {
            super(map);
        }

        public long[] put(GroupEntry groupEntry, long[] jArr) {
            if (!(groupEntry instanceof CencSampleEncryptionInformationGroupEntry)) {
                return (long[]) super.put(groupEntry, jArr);
            }
            throw new RuntimeException("Please supply CencSampleEncryptionInformationGroupEntries in the constructor");
        }
    }

    public CencEncryptingTrackImpl(Track track, UUID uuid, Map<UUID, SecretKey> map, Map<CencSampleEncryptionInformationGroupEntry, long[]> map2, String str, boolean z) {
        this(track, uuid, map, map2, str, z, false);
    }

    public CencEncryptingTrackImpl(Track track, UUID uuid, Map<UUID, SecretKey> map, Map<CencSampleEncryptionInformationGroupEntry, long[]> map2, String str, boolean z, boolean z2) {
        int i;
        int i2;
        this.keys = new HashMap();
        this.dummyIvs = false;
        this.subSampleEncryption = false;
        this.stsd = null;
        this.source = track;
        this.keys = map;
        this.defaultKeyId = uuid;
        this.dummyIvs = z;
        this.encryptionAlgo = str;
        this.sampleGroups = new HashMap();
        for (Entry entry : track.getSampleGroups().entrySet()) {
            if (!(entry.getKey() instanceof CencSampleEncryptionInformationGroupEntry)) {
                this.sampleGroups.put((GroupEntry) entry.getKey(), (long[]) entry.getValue());
            }
        }
        if (map2 != null) {
            for (Entry entry2 : map2.entrySet()) {
                this.sampleGroups.put((GroupEntry) entry2.getKey(), (long[]) entry2.getValue());
            }
        }
        this.sampleGroups = new C03261(this.sampleGroups);
        this.samples = track.getSamples();
        this.cencSampleAuxiliaryData = new ArrayList();
        BigInteger bigInteger = new BigInteger("1");
        byte[] bArr = new byte[8];
        if (!z) {
            new SecureRandom().nextBytes(bArr);
        }
        BigInteger bigInteger2 = new BigInteger(1, bArr);
        List arrayList = new ArrayList();
        if (map2 != null) {
            arrayList.addAll(map2.keySet());
        }
        this.indexToKey = new RangeStartMap();
        int i3 = -1;
        for (i = 0; i < track.getSamples().size(); i++) {
            int i4 = 0;
            for (i2 = 0; i2 < arrayList.size(); i2++) {
                if (Arrays.binarySearch((long[]) getSampleGroups().get((GroupEntry) arrayList.get(i2)), (long) i) >= 0) {
                    i4 = i2 + 1;
                }
            }
            if (i3 != i4) {
                if (i4 == 0) {
                    this.indexToKey.put(Integer.valueOf(i), (SecretKey) map.get(uuid));
                } else if (((CencSampleEncryptionInformationGroupEntry) arrayList.get(i4 - 1)).getKid() != null) {
                    Object obj = (SecretKey) map.get(((CencSampleEncryptionInformationGroupEntry) arrayList.get(i4 - 1)).getKid());
                    if (obj == null) {
                        throw new RuntimeException("Key " + ((CencSampleEncryptionInformationGroupEntry) arrayList.get(i4 - 1)).getKid() + " was not supplied for decryption");
                    }
                    this.indexToKey.put(Integer.valueOf(i), obj);
                } else {
                    this.indexToKey.put(Integer.valueOf(i), null);
                }
                i3 = i4;
            }
        }
        i = -1;
        for (Box box : track.getSampleDescriptionBox().getSampleEntry().getBoxes()) {
            if (box instanceof AvcConfigurationBox) {
                AvcConfigurationBox avcConfigurationBox = (AvcConfigurationBox) box;
                this.subSampleEncryption = true;
                i = avcConfigurationBox.getLengthSizeMinusOne() + 1;
            }
            if (box instanceof HevcConfigurationBox) {
                HevcConfigurationBox hevcConfigurationBox = (HevcConfigurationBox) box;
                this.subSampleEncryption = true;
                i = hevcConfigurationBox.getLengthSizeMinusOne() + 1;
            }
        }
        int i5 = 0;
        while (i5 < this.samples.size()) {
            BigInteger add;
            Sample sample = (Sample) this.samples.get(i5);
            CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat = new CencSampleAuxiliaryDataFormat();
            this.cencSampleAuxiliaryData.add(cencSampleAuxiliaryDataFormat);
            if (this.indexToKey.get(Integer.valueOf(i5)) != null) {
                Object toByteArray = bigInteger2.toByteArray();
                Object obj2 = new byte[8];
                System.arraycopy(toByteArray, toByteArray.length + -8 > 0 ? toByteArray.length - 8 : 0, obj2, 8 - toByteArray.length < 0 ? 0 : 8 - toByteArray.length, toByteArray.length > 8 ? 8 : toByteArray.length);
                cencSampleAuxiliaryDataFormat.iv = obj2;
                ByteBuffer byteBuffer = (ByteBuffer) sample.asByteBuffer().rewind();
                if (this.subSampleEncryption) {
                    if (z2) {
                        cencSampleAuxiliaryDataFormat.pairs = new Pair[]{cencSampleAuxiliaryDataFormat.createPair(byteBuffer.remaining(), 0)};
                    } else {
                        List arrayList2 = new ArrayList(5);
                        while (byteBuffer.remaining() > 0) {
                            int l2i = CastUtils.l2i(IsoTypeReaderVariable.read(byteBuffer, i));
                            i2 = l2i + i;
                            i3 = i2 >= 112 ? (i2 % 16) + 96 : i2;
                            arrayList2.add(cencSampleAuxiliaryDataFormat.createPair(i3, (long) (i2 - i3)));
                            byteBuffer.position(byteBuffer.position() + l2i);
                        }
                        cencSampleAuxiliaryDataFormat.pairs = (Pair[]) arrayList2.toArray(new Pair[arrayList2.size()]);
                    }
                }
                add = bigInteger2.add(bigInteger);
            } else {
                add = bigInteger2;
            }
            i5++;
            bigInteger2 = add;
        }
        System.err.println(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public CencEncryptingTrackImpl(Track track, UUID uuid, SecretKey secretKey, boolean z) {
        this(track, uuid, Collections.singletonMap(uuid, secretKey), null, "cenc", z);
    }

    public void close() {
        this.source.close();
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.source.getCompositionTimeEntries();
    }

    public UUID getDefaultKeyId() {
        return this.defaultKeyId;
    }

    public long getDuration() {
        return this.source.getDuration();
    }

    public List<Edit> getEdits() {
        return this.source.getEdits();
    }

    public String getHandler() {
        return this.source.getHandler();
    }

    public String getName() {
        return "enc(" + this.source.getName() + ")";
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.source.getSampleDependencies();
    }

    public synchronized SampleDescriptionBox getSampleDescriptionBox() {
        if (this.stsd == null) {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                this.source.getSampleDescriptionBox().getBox(Channels.newChannel(byteArrayOutputStream));
                this.stsd = (SampleDescriptionBox) new IsoFile(new MemoryDataSourceImpl(byteArrayOutputStream.toByteArray())).getBoxes().get(0);
                Box originalFormatBox = new OriginalFormatBox();
                originalFormatBox.setDataFormat(this.stsd.getSampleEntry().getType());
                if (this.stsd.getSampleEntry() instanceof AudioSampleEntry) {
                    ((AudioSampleEntry) this.stsd.getSampleEntry()).setType(AudioSampleEntry.TYPE_ENCRYPTED);
                } else if (this.stsd.getSampleEntry() instanceof VisualSampleEntry) {
                    ((VisualSampleEntry) this.stsd.getSampleEntry()).setType(VisualSampleEntry.TYPE_ENCRYPTED);
                } else {
                    throw new RuntimeException("I don't know how to cenc " + this.stsd.getSampleEntry().getType());
                }
                Box protectionSchemeInformationBox = new ProtectionSchemeInformationBox();
                protectionSchemeInformationBox.addBox(originalFormatBox);
                Box schemeTypeBox = new SchemeTypeBox();
                schemeTypeBox.setSchemeType(this.encryptionAlgo);
                schemeTypeBox.setSchemeVersion(AccessibilityNodeInfoCompat.ACTION_CUT);
                protectionSchemeInformationBox.addBox(schemeTypeBox);
                originalFormatBox = new SchemeInformationBox();
                Box trackEncryptionBox = new TrackEncryptionBox();
                trackEncryptionBox.setDefaultIvSize(this.defaultKeyId == null ? 0 : 8);
                trackEncryptionBox.setDefaultAlgorithmId(this.defaultKeyId == null ? 0 : 1);
                trackEncryptionBox.setDefault_KID(this.defaultKeyId == null ? new UUID(0, 0) : this.defaultKeyId);
                originalFormatBox.addBox(trackEncryptionBox);
                protectionSchemeInformationBox.addBox(originalFormatBox);
                this.stsd.getSampleEntry().addBox(protectionSchemeInformationBox);
            } catch (IOException e) {
                throw new RuntimeException("Dumping stsd to memory failed");
            }
        }
        return this.stsd;
    }

    public long[] getSampleDurations() {
        return this.source.getSampleDurations();
    }

    public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries() {
        return this.cencSampleAuxiliaryData;
    }

    public Map<GroupEntry, long[]> getSampleGroups() {
        return this.sampleGroups;
    }

    public List<Sample> getSamples() {
        return new CencEncryptingSampleList(this.indexToKey, this.source.getSamples(), this.cencSampleAuxiliaryData, this.encryptionAlgo);
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.source.getSubsampleInformationBox();
    }

    public long[] getSyncSamples() {
        return this.source.getSyncSamples();
    }

    public TrackMetaData getTrackMetaData() {
        return this.source.getTrackMetaData();
    }

    public boolean hasSubSampleEncryption() {
        return this.subSampleEncryption;
    }
}
