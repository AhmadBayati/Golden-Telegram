package com.googlecode.mp4parser.authoring.tracks.h265;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack.LookAhead;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.tgnet.TLRPC;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord.Array;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class H265TrackImpl extends AbstractH26XTrack implements NalUnitTypes {
    ArrayList<ByteBuffer> pps;
    ArrayList<Sample> samples;
    ArrayList<ByteBuffer> sps;
    SampleDescriptionBox stsd;
    ArrayList<ByteBuffer> vps;

    public H265TrackImpl(DataSource dataSource) {
        super(dataSource);
        this.sps = new ArrayList();
        this.pps = new ArrayList();
        this.vps = new ArrayList();
        this.samples = new ArrayList();
        Object arrayList = new ArrayList();
        LookAhead lookAhead = new LookAhead(dataSource);
        boolean[] zArr = new boolean[1];
        boolean[] zArr2 = new boolean[]{true};
        while (true) {
            ByteBuffer findNextNal = findNextNal(lookAhead);
            if (findNextNal == null) {
                this.stsd = createSampleDescriptionBox();
                this.decodingTimes = new long[this.samples.size()];
                getTrackMetaData().setTimescale(25);
                Arrays.fill(this.decodingTimes, 1);
                return;
            }
            NalUnitHeader nalUnitHeader = getNalUnitHeader(findNextNal);
            if (zArr[0]) {
                if (!isVcl(nalUnitHeader)) {
                    switch (nalUnitHeader.nalUnitType) {
                        case TLRPC.USER_FLAG_PHOTO /*32*/:
                        case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                        case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                        case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                        case NalUnitTypes.NAL_TYPE_EOS_NUT /*36*/:
                        case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
                        case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL41 /*41*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL43 /*43*/:
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL44 /*44*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC49 /*49*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC50 /*50*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC51 /*51*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC52 /*52*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC53 /*53*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC54 /*54*/:
                        case NalUnitTypes.NAL_TYPE_UNSPEC55 /*55*/:
                            wrapUp(arrayList, zArr, zArr2);
                            break;
                        default:
                            break;
                    }
                } else if ((findNextNal.get(2) & -128) != 0) {
                    wrapUp(arrayList, zArr, zArr2);
                }
            }
            switch (nalUnitHeader.nalUnitType) {
                case TLRPC.USER_FLAG_PHOTO /*32*/:
                    findNextNal.position(2);
                    this.vps.add(findNextNal.slice());
                    System.err.println("Stored VPS");
                    break;
                case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                    findNextNal.position(2);
                    this.sps.add(findNextNal.slice());
                    findNextNal.position(1);
                    SequenceParameterSetRbsp sequenceParameterSetRbsp = new SequenceParameterSetRbsp(Channels.newInputStream(new ByteBufferByteChannel(findNextNal.slice())));
                    System.err.println("Stored SPS");
                    break;
                case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                    findNextNal.position(2);
                    this.pps.add(findNextNal.slice());
                    System.err.println("Stored PPS");
                    break;
                case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                    SEIMessage sEIMessage = new SEIMessage(new BitReaderBuffer(findNextNal.slice()));
                    break;
            }
            switch (nalUnitHeader.nalUnitType) {
                case TLRPC.USER_FLAG_PHOTO /*32*/:
                case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                case NalUnitTypes.NAL_TYPE_EOS_NUT /*36*/:
                case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
                case NalUnitTypes.NAL_TYPE_FD_NUT /*38*/:
                    break;
                default:
                    System.err.println("Adding " + nalUnitHeader.nalUnitType);
                    arrayList.add(findNextNal);
                    break;
            }
            if (isVcl(nalUnitHeader)) {
                switch (nalUnitHeader.nalUnitType) {
                    case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
                    case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
                        zArr2[0] = zArr2[0] & 1;
                        break;
                    default:
                        zArr2[0] = false;
                        break;
                }
            }
            zArr[0] = zArr[0] | isVcl(nalUnitHeader);
        }
    }

    private SampleDescriptionBox createSampleDescriptionBox() {
        this.stsd = new SampleDescriptionBox();
        Box visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE6);
        visualSampleEntry.setDataReferenceIndex(1);
        visualSampleEntry.setDepth(24);
        visualSampleEntry.setFrameCount(1);
        visualSampleEntry.setHorizresolution(72.0d);
        visualSampleEntry.setVertresolution(72.0d);
        visualSampleEntry.setWidth(640);
        visualSampleEntry.setHeight(480);
        visualSampleEntry.setCompressorname("HEVC Coding");
        Box hevcConfigurationBox = new HevcConfigurationBox();
        Array array = new Array();
        array.array_completeness = true;
        array.nal_unit_type = 33;
        array.nalUnits = new ArrayList();
        Iterator it = this.sps.iterator();
        while (it.hasNext()) {
            array.nalUnits.add(AbstractH26XTrack.toArray((ByteBuffer) it.next()));
        }
        Array array2 = new Array();
        array2.array_completeness = true;
        array2.nal_unit_type = 34;
        array2.nalUnits = new ArrayList();
        Iterator it2 = this.pps.iterator();
        while (it2.hasNext()) {
            array2.nalUnits.add(AbstractH26XTrack.toArray((ByteBuffer) it2.next()));
        }
        Array array3 = new Array();
        array3.array_completeness = true;
        array3.nal_unit_type = 34;
        array3.nalUnits = new ArrayList();
        Iterator it3 = this.vps.iterator();
        while (it3.hasNext()) {
            array3.nalUnits.add(AbstractH26XTrack.toArray((ByteBuffer) it3.next()));
        }
        hevcConfigurationBox.getArrays().addAll(Arrays.asList(new Array[]{array, array3, array2}));
        visualSampleEntry.addBox(hevcConfigurationBox);
        this.stsd.addBox(visualSampleEntry);
        return this.stsd;
    }

    public static void main(String[] strArr) {
        Track h265TrackImpl = new H265TrackImpl(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
        Movie movie = new Movie();
        movie.addTrack(h265TrackImpl);
        new DefaultMp4Builder().build(movie).writeContainer(new FileOutputStream("output.mp4").getChannel());
    }

    public String getHandler() {
        return "vide";
    }

    public NalUnitHeader getNalUnitHeader(ByteBuffer byteBuffer) {
        byteBuffer.position(0);
        int readUInt16 = IsoTypeReader.readUInt16(byteBuffer);
        NalUnitHeader nalUnitHeader = new NalUnitHeader();
        nalUnitHeader.forbiddenZeroFlag = (TLRPC.MESSAGE_FLAG_EDITED & readUInt16) >> 15;
        nalUnitHeader.nalUnitType = (readUInt16 & 32256) >> 9;
        nalUnitHeader.nuhLayerId = (readUInt16 & 504) >> 3;
        nalUnitHeader.nuhTemporalIdPlusOne = readUInt16 & 7;
        return nalUnitHeader;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return null;
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    boolean isVcl(NalUnitHeader nalUnitHeader) {
        return nalUnitHeader.nalUnitType >= 0 && nalUnitHeader.nalUnitType <= 31;
    }

    public void wrapUp(List<ByteBuffer> list, boolean[] zArr, boolean[] zArr2) {
        this.samples.add(createSampleObject(list));
        System.err.print("Create AU from " + list.size() + " NALs");
        if (zArr2[0]) {
            System.err.println("  IDR");
        } else {
            System.err.println();
        }
        zArr[0] = false;
        zArr2[0] = true;
        list.clear();
    }
}
