package com.hanista.mobogram.messenger.exoplayer.text.webvtt;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.SubtitleParser;
import com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCue.Builder;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.ArrayList;
import java.util.List;

public final class Mp4WebvttParser implements SubtitleParser {
    private static final int BOX_HEADER_SIZE = 8;
    private static final int TYPE_payl;
    private static final int TYPE_sttg;
    private static final int TYPE_vttc;
    private final Builder builder;
    private final ParsableByteArray sampleData;

    static {
        TYPE_payl = Util.getIntegerCodeForString("payl");
        TYPE_sttg = Util.getIntegerCodeForString("sttg");
        TYPE_vttc = Util.getIntegerCodeForString("vttc");
    }

    public Mp4WebvttParser() {
        this.sampleData = new ParsableByteArray();
        this.builder = new Builder();
    }

    private static Cue parseVttCueBox(ParsableByteArray parsableByteArray, Builder builder, int i) {
        builder.reset();
        while (i > 0) {
            if (i < BOX_HEADER_SIZE) {
                throw new ParserException("Incomplete vtt cue box header found.");
            }
            int readInt = parsableByteArray.readInt();
            int readInt2 = parsableByteArray.readInt();
            int i2 = i - 8;
            readInt -= 8;
            String str = new String(parsableByteArray.data, parsableByteArray.getPosition(), readInt);
            parsableByteArray.skipBytes(readInt);
            i = i2 - readInt;
            if (readInt2 == TYPE_sttg) {
                WebvttCueParser.parseCueSettingsList(str, builder);
            } else if (readInt2 == TYPE_payl) {
                WebvttCueParser.parseCueText(str.trim(), builder);
            }
        }
        return builder.build();
    }

    public boolean canParse(String str) {
        return MimeTypes.APPLICATION_MP4VTT.equals(str);
    }

    public Mp4WebvttSubtitle parse(byte[] bArr, int i, int i2) {
        this.sampleData.reset(bArr, i + i2);
        this.sampleData.setPosition(i);
        List arrayList = new ArrayList();
        while (this.sampleData.bytesLeft() > 0) {
            if (this.sampleData.bytesLeft() < BOX_HEADER_SIZE) {
                throw new ParserException("Incomplete Mp4Webvtt Top Level box header found.");
            }
            int readInt = this.sampleData.readInt();
            if (this.sampleData.readInt() == TYPE_vttc) {
                arrayList.add(parseVttCueBox(this.sampleData, this.builder, readInt - 8));
            } else {
                this.sampleData.skipBytes(readInt - 8);
            }
        }
        return new Mp4WebvttSubtitle(arrayList);
    }
}
