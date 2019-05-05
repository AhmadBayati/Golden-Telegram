package com.hanista.mobogram.messenger.exoplayer.extractor.ogg;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ogg.OggUtil.PageHeader;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

public class OggExtractor implements Extractor {
    private StreamReader streamReader;

    public void init(ExtractorOutput extractorOutput) {
        TrackOutput track = extractorOutput.track(0);
        extractorOutput.endTracks();
        this.streamReader.init(extractorOutput, track);
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) {
        return this.streamReader.read(extractorInput, positionHolder);
    }

    public void release() {
    }

    public void seek() {
        this.streamReader.seek();
    }

    public boolean sniff(ExtractorInput extractorInput) {
        try {
            ParsableByteArray parsableByteArray = new ParsableByteArray(new byte[27], 0);
            PageHeader pageHeader = new PageHeader();
            if (!OggUtil.populatePageHeader(extractorInput, pageHeader, parsableByteArray, true) || (pageHeader.type & 2) != 2 || pageHeader.bodySize < 7) {
                return false;
            }
            parsableByteArray.reset();
            extractorInput.peekFully(parsableByteArray.data, 0, 7);
            if (FlacReader.verifyBitstreamType(parsableByteArray)) {
                this.streamReader = new FlacReader();
            } else {
                parsableByteArray.reset();
                if (!VorbisReader.verifyBitstreamType(parsableByteArray)) {
                    return false;
                }
                this.streamReader = new VorbisReader();
            }
            return true;
        } catch (ParserException e) {
            return false;
        }
    }
}
