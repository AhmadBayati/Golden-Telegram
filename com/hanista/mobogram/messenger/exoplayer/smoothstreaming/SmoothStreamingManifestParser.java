package com.hanista.mobogram.messenger.exoplayer.smoothstreaming;

import android.util.Base64;
import android.util.Pair;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.boxes.AC3SpecificBox;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp4.PsshAtomUtil;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.ProtectionElement;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.StreamElement;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.TrackElement;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriLoadable.Parser;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.CodecSpecificDataUtil;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class SmoothStreamingManifestParser implements Parser<SmoothStreamingManifest> {
    private final XmlPullParserFactory xmlParserFactory;

    private static abstract class ElementParser {
        private final String baseUri;
        private final List<Pair<String, Object>> normalizedAttributes;
        private final ElementParser parent;
        private final String tag;

        public ElementParser(ElementParser elementParser, String str, String str2) {
            this.parent = elementParser;
            this.baseUri = str;
            this.tag = str2;
            this.normalizedAttributes = new LinkedList();
        }

        private ElementParser newChildParser(ElementParser elementParser, String str, String str2) {
            return TrackElementParser.TAG.equals(str) ? new TrackElementParser(elementParser, str2) : ProtectionElementParser.TAG.equals(str) ? new ProtectionElementParser(elementParser, str2) : StreamElementParser.TAG.equals(str) ? new StreamElementParser(elementParser, str2) : null;
        }

        protected void addChild(Object obj) {
        }

        protected abstract Object build();

        protected final Object getNormalizedAttribute(String str) {
            for (int i = 0; i < this.normalizedAttributes.size(); i++) {
                Pair pair = (Pair) this.normalizedAttributes.get(i);
                if (((String) pair.first).equals(str)) {
                    return pair.second;
                }
            }
            return this.parent == null ? null : this.parent.getNormalizedAttribute(str);
        }

        protected boolean handleChildInline(String str) {
            return false;
        }

        public final Object parse(XmlPullParser xmlPullParser) {
            int i = 0;
            int i2 = 0;
            while (true) {
                String name;
                switch (xmlPullParser.getEventType()) {
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        return null;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        name = xmlPullParser.getName();
                        if (!this.tag.equals(name)) {
                            if (i2 != 0) {
                                if (i <= 0) {
                                    if (!handleChildInline(name)) {
                                        ElementParser newChildParser = newChildParser(this, name, this.baseUri);
                                        if (newChildParser != null) {
                                            addChild(newChildParser.parse(xmlPullParser));
                                            break;
                                        }
                                        i = 1;
                                        break;
                                    }
                                    parseStartTag(xmlPullParser);
                                    break;
                                }
                                i++;
                                break;
                            }
                            break;
                        }
                        parseStartTag(xmlPullParser);
                        i2 = 1;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        if (i2 != 0) {
                            if (i <= 0) {
                                name = xmlPullParser.getName();
                                parseEndTag(xmlPullParser);
                                if (handleChildInline(name)) {
                                    break;
                                }
                                return build();
                            }
                            i--;
                            break;
                        }
                        continue;
                    case VideoPlayer.STATE_READY /*4*/:
                        if (i2 != 0 && i == 0) {
                            parseText(xmlPullParser);
                            break;
                        }
                    default:
                        break;
                }
                xmlPullParser.next();
            }
        }

        protected final boolean parseBoolean(XmlPullParser xmlPullParser, String str, boolean z) {
            String attributeValue = xmlPullParser.getAttributeValue(null, str);
            return attributeValue != null ? Boolean.parseBoolean(attributeValue) : z;
        }

        protected void parseEndTag(XmlPullParser xmlPullParser) {
        }

        protected final int parseInt(XmlPullParser xmlPullParser, String str, int i) {
            String attributeValue = xmlPullParser.getAttributeValue(null, str);
            if (attributeValue != null) {
                try {
                    i = Integer.parseInt(attributeValue);
                } catch (Throwable e) {
                    throw new ParserException(e);
                }
            }
            return i;
        }

        protected final long parseLong(XmlPullParser xmlPullParser, String str, long j) {
            String attributeValue = xmlPullParser.getAttributeValue(null, str);
            if (attributeValue != null) {
                try {
                    j = Long.parseLong(attributeValue);
                } catch (Throwable e) {
                    throw new ParserException(e);
                }
            }
            return j;
        }

        protected final int parseRequiredInt(XmlPullParser xmlPullParser, String str) {
            String attributeValue = xmlPullParser.getAttributeValue(null, str);
            if (attributeValue != null) {
                try {
                    return Integer.parseInt(attributeValue);
                } catch (Throwable e) {
                    throw new ParserException(e);
                }
            }
            throw new MissingFieldException(str);
        }

        protected final long parseRequiredLong(XmlPullParser xmlPullParser, String str) {
            String attributeValue = xmlPullParser.getAttributeValue(null, str);
            if (attributeValue != null) {
                try {
                    return Long.parseLong(attributeValue);
                } catch (Throwable e) {
                    throw new ParserException(e);
                }
            }
            throw new MissingFieldException(str);
        }

        protected final String parseRequiredString(XmlPullParser xmlPullParser, String str) {
            String attributeValue = xmlPullParser.getAttributeValue(null, str);
            if (attributeValue != null) {
                return attributeValue;
            }
            throw new MissingFieldException(str);
        }

        protected void parseStartTag(XmlPullParser xmlPullParser) {
        }

        protected void parseText(XmlPullParser xmlPullParser) {
        }

        protected final void putNormalizedAttribute(String str, Object obj) {
            this.normalizedAttributes.add(Pair.create(str, obj));
        }
    }

    public static class MissingFieldException extends ParserException {
        public MissingFieldException(String str) {
            super("Missing required field: " + str);
        }
    }

    private static class ProtectionElementParser extends ElementParser {
        public static final String KEY_SYSTEM_ID = "SystemID";
        public static final String TAG = "Protection";
        public static final String TAG_PROTECTION_HEADER = "ProtectionHeader";
        private boolean inProtectionHeader;
        private byte[] initData;
        private UUID uuid;

        public ProtectionElementParser(ElementParser elementParser, String str) {
            super(elementParser, str, TAG);
        }

        private static String stripCurlyBraces(String str) {
            return (str.charAt(0) == '{' && str.charAt(str.length() - 1) == '}') ? str.substring(1, str.length() - 1) : str;
        }

        public Object build() {
            return new ProtectionElement(this.uuid, PsshAtomUtil.buildPsshAtom(this.uuid, this.initData));
        }

        public boolean handleChildInline(String str) {
            return TAG_PROTECTION_HEADER.equals(str);
        }

        public void parseEndTag(XmlPullParser xmlPullParser) {
            if (TAG_PROTECTION_HEADER.equals(xmlPullParser.getName())) {
                this.inProtectionHeader = false;
            }
        }

        public void parseStartTag(XmlPullParser xmlPullParser) {
            if (TAG_PROTECTION_HEADER.equals(xmlPullParser.getName())) {
                this.inProtectionHeader = true;
                this.uuid = UUID.fromString(stripCurlyBraces(xmlPullParser.getAttributeValue(null, KEY_SYSTEM_ID)));
            }
        }

        public void parseText(XmlPullParser xmlPullParser) {
            if (this.inProtectionHeader) {
                this.initData = Base64.decode(xmlPullParser.getText(), 0);
            }
        }
    }

    private static class SmoothStreamMediaParser extends ElementParser {
        private static final String KEY_DURATION = "Duration";
        private static final String KEY_DVR_WINDOW_LENGTH = "DVRWindowLength";
        private static final String KEY_IS_LIVE = "IsLive";
        private static final String KEY_LOOKAHEAD_COUNT = "LookaheadCount";
        private static final String KEY_MAJOR_VERSION = "MajorVersion";
        private static final String KEY_MINOR_VERSION = "MinorVersion";
        private static final String KEY_TIME_SCALE = "TimeScale";
        public static final String TAG = "SmoothStreamingMedia";
        private long duration;
        private long dvrWindowLength;
        private boolean isLive;
        private int lookAheadCount;
        private int majorVersion;
        private int minorVersion;
        private ProtectionElement protectionElement;
        private List<StreamElement> streamElements;
        private long timescale;

        public SmoothStreamMediaParser(ElementParser elementParser, String str) {
            super(elementParser, str, TAG);
            this.lookAheadCount = -1;
            this.protectionElement = null;
            this.streamElements = new LinkedList();
        }

        public void addChild(Object obj) {
            if (obj instanceof StreamElement) {
                this.streamElements.add((StreamElement) obj);
            } else if (obj instanceof ProtectionElement) {
                Assertions.checkState(this.protectionElement == null);
                this.protectionElement = (ProtectionElement) obj;
            }
        }

        public Object build() {
            StreamElement[] streamElementArr = new StreamElement[this.streamElements.size()];
            this.streamElements.toArray(streamElementArr);
            return new SmoothStreamingManifest(this.majorVersion, this.minorVersion, this.timescale, this.duration, this.dvrWindowLength, this.lookAheadCount, this.isLive, this.protectionElement, streamElementArr);
        }

        public void parseStartTag(XmlPullParser xmlPullParser) {
            this.majorVersion = parseRequiredInt(xmlPullParser, KEY_MAJOR_VERSION);
            this.minorVersion = parseRequiredInt(xmlPullParser, KEY_MINOR_VERSION);
            this.timescale = parseLong(xmlPullParser, KEY_TIME_SCALE, 10000000);
            this.duration = parseRequiredLong(xmlPullParser, KEY_DURATION);
            this.dvrWindowLength = parseLong(xmlPullParser, KEY_DVR_WINDOW_LENGTH, 0);
            this.lookAheadCount = parseInt(xmlPullParser, KEY_LOOKAHEAD_COUNT, -1);
            this.isLive = parseBoolean(xmlPullParser, KEY_IS_LIVE, false);
            putNormalizedAttribute(KEY_TIME_SCALE, Long.valueOf(this.timescale));
        }
    }

    private static class StreamElementParser extends ElementParser {
        private static final String KEY_DISPLAY_HEIGHT = "DisplayHeight";
        private static final String KEY_DISPLAY_WIDTH = "DisplayWidth";
        private static final String KEY_FRAGMENT_DURATION = "d";
        private static final String KEY_FRAGMENT_REPEAT_COUNT = "r";
        private static final String KEY_FRAGMENT_START_TIME = "t";
        private static final String KEY_LANGUAGE = "Language";
        private static final String KEY_MAX_HEIGHT = "MaxHeight";
        private static final String KEY_MAX_WIDTH = "MaxWidth";
        private static final String KEY_NAME = "Name";
        private static final String KEY_QUALITY_LEVELS = "QualityLevels";
        private static final String KEY_SUB_TYPE = "Subtype";
        private static final String KEY_TIME_SCALE = "TimeScale";
        private static final String KEY_TYPE = "Type";
        private static final String KEY_TYPE_AUDIO = "audio";
        private static final String KEY_TYPE_TEXT = "text";
        private static final String KEY_TYPE_VIDEO = "video";
        private static final String KEY_URL = "Url";
        public static final String TAG = "StreamIndex";
        private static final String TAG_STREAM_FRAGMENT = "c";
        private final String baseUri;
        private int displayHeight;
        private int displayWidth;
        private String language;
        private long lastChunkDuration;
        private int maxHeight;
        private int maxWidth;
        private String name;
        private int qualityLevels;
        private ArrayList<Long> startTimes;
        private String subType;
        private long timescale;
        private final List<TrackElement> tracks;
        private int type;
        private String url;

        public StreamElementParser(ElementParser elementParser, String str) {
            super(elementParser, str, TAG);
            this.baseUri = str;
            this.tracks = new LinkedList();
        }

        private void parseStreamElementStartTag(XmlPullParser xmlPullParser) {
            this.type = parseType(xmlPullParser);
            putNormalizedAttribute(KEY_TYPE, Integer.valueOf(this.type));
            if (this.type == 2) {
                this.subType = parseRequiredString(xmlPullParser, KEY_SUB_TYPE);
            } else {
                this.subType = xmlPullParser.getAttributeValue(null, KEY_SUB_TYPE);
            }
            this.name = xmlPullParser.getAttributeValue(null, KEY_NAME);
            this.qualityLevels = parseInt(xmlPullParser, KEY_QUALITY_LEVELS, -1);
            this.url = parseRequiredString(xmlPullParser, KEY_URL);
            this.maxWidth = parseInt(xmlPullParser, KEY_MAX_WIDTH, -1);
            this.maxHeight = parseInt(xmlPullParser, KEY_MAX_HEIGHT, -1);
            this.displayWidth = parseInt(xmlPullParser, KEY_DISPLAY_WIDTH, -1);
            this.displayHeight = parseInt(xmlPullParser, KEY_DISPLAY_HEIGHT, -1);
            this.language = xmlPullParser.getAttributeValue(null, KEY_LANGUAGE);
            putNormalizedAttribute(KEY_LANGUAGE, this.language);
            this.timescale = (long) parseInt(xmlPullParser, KEY_TIME_SCALE, -1);
            if (this.timescale == -1) {
                this.timescale = ((Long) getNormalizedAttribute(KEY_TIME_SCALE)).longValue();
            }
            this.startTimes = new ArrayList();
        }

        private void parseStreamFragmentStartTag(XmlPullParser xmlPullParser) {
            int size = this.startTimes.size();
            long parseLong = parseLong(xmlPullParser, KEY_FRAGMENT_START_TIME, -1);
            if (parseLong == -1) {
                if (size == 0) {
                    parseLong = 0;
                } else if (this.lastChunkDuration != -1) {
                    parseLong = ((Long) this.startTimes.get(size - 1)).longValue() + this.lastChunkDuration;
                } else {
                    throw new ParserException("Unable to infer start time");
                }
            }
            int i = size + 1;
            this.startTimes.add(Long.valueOf(parseLong));
            this.lastChunkDuration = parseLong(xmlPullParser, KEY_FRAGMENT_DURATION, -1);
            long parseLong2 = parseLong(xmlPullParser, KEY_FRAGMENT_REPEAT_COUNT, 1);
            if (parseLong2 <= 1 || this.lastChunkDuration != -1) {
                for (size = 1; ((long) size) < parseLong2; size++) {
                    i++;
                    this.startTimes.add(Long.valueOf((this.lastChunkDuration * ((long) size)) + parseLong));
                }
                return;
            }
            throw new ParserException("Repeated chunk with unspecified duration");
        }

        private int parseType(XmlPullParser xmlPullParser) {
            String attributeValue = xmlPullParser.getAttributeValue(null, KEY_TYPE);
            if (attributeValue == null) {
                throw new MissingFieldException(KEY_TYPE);
            } else if (KEY_TYPE_AUDIO.equalsIgnoreCase(attributeValue)) {
                return 0;
            } else {
                if (KEY_TYPE_VIDEO.equalsIgnoreCase(attributeValue)) {
                    return 1;
                }
                if (KEY_TYPE_TEXT.equalsIgnoreCase(attributeValue)) {
                    return 2;
                }
                throw new ParserException("Invalid key value[" + attributeValue + "]");
            }
        }

        public void addChild(Object obj) {
            if (obj instanceof TrackElement) {
                this.tracks.add((TrackElement) obj);
            }
        }

        public Object build() {
            TrackElement[] trackElementArr = new TrackElement[this.tracks.size()];
            this.tracks.toArray(trackElementArr);
            return new StreamElement(this.baseUri, this.url, this.type, this.subType, this.timescale, this.name, this.qualityLevels, this.maxWidth, this.maxHeight, this.displayWidth, this.displayHeight, this.language, trackElementArr, this.startTimes, this.lastChunkDuration);
        }

        public boolean handleChildInline(String str) {
            return TAG_STREAM_FRAGMENT.equals(str);
        }

        public void parseStartTag(XmlPullParser xmlPullParser) {
            if (TAG_STREAM_FRAGMENT.equals(xmlPullParser.getName())) {
                parseStreamFragmentStartTag(xmlPullParser);
            } else {
                parseStreamElementStartTag(xmlPullParser);
            }
        }
    }

    private static class TrackElementParser extends ElementParser {
        private static final String KEY_BITRATE = "Bitrate";
        private static final String KEY_CHANNELS = "Channels";
        private static final String KEY_CODEC_PRIVATE_DATA = "CodecPrivateData";
        private static final String KEY_FOUR_CC = "FourCC";
        private static final String KEY_INDEX = "Index";
        private static final String KEY_LANGUAGE = "Language";
        private static final String KEY_MAX_HEIGHT = "MaxHeight";
        private static final String KEY_MAX_WIDTH = "MaxWidth";
        private static final String KEY_SAMPLING_RATE = "SamplingRate";
        private static final String KEY_TYPE = "Type";
        public static final String TAG = "QualityLevel";
        private int bitrate;
        private int channels;
        private final List<byte[]> csd;
        private int index;
        private String language;
        private int maxHeight;
        private int maxWidth;
        private String mimeType;
        private int samplingRate;

        public TrackElementParser(ElementParser elementParser, String str) {
            super(elementParser, str, TAG);
            this.csd = new LinkedList();
        }

        private static String fourCCToMimeType(String str) {
            return (str.equalsIgnoreCase("H264") || str.equalsIgnoreCase("X264") || str.equalsIgnoreCase("AVC1") || str.equalsIgnoreCase("DAVC")) ? MimeTypes.VIDEO_H264 : (str.equalsIgnoreCase("AAC") || str.equalsIgnoreCase("AACL") || str.equalsIgnoreCase("AACH") || str.equalsIgnoreCase("AACP")) ? MimeTypes.AUDIO_AAC : str.equalsIgnoreCase("TTML") ? MimeTypes.APPLICATION_TTML : (str.equalsIgnoreCase(AudioSampleEntry.TYPE8) || str.equalsIgnoreCase(AC3SpecificBox.TYPE)) ? MimeTypes.AUDIO_AC3 : (str.equalsIgnoreCase(AudioSampleEntry.TYPE9) || str.equalsIgnoreCase(EC3SpecificBox.TYPE)) ? MimeTypes.AUDIO_E_AC3 : str.equalsIgnoreCase("dtsc") ? MimeTypes.AUDIO_DTS : (str.equalsIgnoreCase(AudioSampleEntry.TYPE12) || str.equalsIgnoreCase(AudioSampleEntry.TYPE11)) ? MimeTypes.AUDIO_DTS_HD : str.equalsIgnoreCase(AudioSampleEntry.TYPE13) ? MimeTypes.AUDIO_DTS_EXPRESS : str.equalsIgnoreCase("opus") ? MimeTypes.AUDIO_OPUS : null;
        }

        public Object build() {
            byte[][] bArr;
            byte[][] bArr2 = (byte[][]) null;
            if (this.csd.isEmpty()) {
                bArr = bArr2;
            } else {
                bArr = new byte[this.csd.size()][];
                this.csd.toArray(bArr);
            }
            return new TrackElement(this.index, this.bitrate, this.mimeType, bArr, this.maxWidth, this.maxHeight, this.samplingRate, this.channels, this.language);
        }

        public void parseStartTag(XmlPullParser xmlPullParser) {
            String attributeValue;
            int intValue = ((Integer) getNormalizedAttribute(KEY_TYPE)).intValue();
            this.index = parseInt(xmlPullParser, KEY_INDEX, -1);
            this.bitrate = parseRequiredInt(xmlPullParser, KEY_BITRATE);
            this.language = (String) getNormalizedAttribute(KEY_LANGUAGE);
            if (intValue == 1) {
                this.maxHeight = parseRequiredInt(xmlPullParser, KEY_MAX_HEIGHT);
                this.maxWidth = parseRequiredInt(xmlPullParser, KEY_MAX_WIDTH);
                this.mimeType = fourCCToMimeType(parseRequiredString(xmlPullParser, KEY_FOUR_CC));
            } else {
                this.maxHeight = -1;
                this.maxWidth = -1;
                attributeValue = xmlPullParser.getAttributeValue(null, KEY_FOUR_CC);
                attributeValue = attributeValue != null ? fourCCToMimeType(attributeValue) : intValue == 0 ? MimeTypes.AUDIO_AAC : null;
                this.mimeType = attributeValue;
            }
            if (intValue == 0) {
                this.samplingRate = parseRequiredInt(xmlPullParser, KEY_SAMPLING_RATE);
                this.channels = parseRequiredInt(xmlPullParser, KEY_CHANNELS);
            } else {
                this.samplingRate = -1;
                this.channels = -1;
            }
            attributeValue = xmlPullParser.getAttributeValue(null, KEY_CODEC_PRIVATE_DATA);
            if (attributeValue != null && attributeValue.length() > 0) {
                Object bytesFromHexString = Util.getBytesFromHexString(attributeValue);
                byte[][] splitNalUnits = CodecSpecificDataUtil.splitNalUnits(bytesFromHexString);
                if (splitNalUnits == null) {
                    this.csd.add(bytesFromHexString);
                    return;
                }
                for (Object add : splitNalUnits) {
                    this.csd.add(add);
                }
            }
        }
    }

    public SmoothStreamingManifestParser() {
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    public SmoothStreamingManifest parse(String str, InputStream inputStream) {
        try {
            XmlPullParser newPullParser = this.xmlParserFactory.newPullParser();
            newPullParser.setInput(inputStream, null);
            return (SmoothStreamingManifest) new SmoothStreamMediaParser(null, str).parse(newPullParser);
        } catch (Throwable e) {
            throw new ParserException(e);
        }
    }
}
