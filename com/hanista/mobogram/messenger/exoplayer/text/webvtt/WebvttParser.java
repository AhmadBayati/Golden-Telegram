package com.hanista.mobogram.messenger.exoplayer.text.webvtt;

import android.text.TextUtils;
import com.hanista.mobogram.messenger.exoplayer.text.SubtitleParser;
import com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCue.Builder;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.List;

public final class WebvttParser implements SubtitleParser {
    private final WebvttCueParser cueParser;
    private final ParsableByteArray parsableWebvttData;
    private final Builder webvttCueBuilder;

    public WebvttParser() {
        this.cueParser = new WebvttCueParser();
        this.parsableWebvttData = new ParsableByteArray();
        this.webvttCueBuilder = new Builder();
    }

    public final boolean canParse(String str) {
        return MimeTypes.TEXT_VTT.equals(str);
    }

    public final WebvttSubtitle parse(byte[] bArr, int i, int i2) {
        this.parsableWebvttData.reset(bArr, i + i2);
        this.parsableWebvttData.setPosition(i);
        this.webvttCueBuilder.reset();
        WebvttParserUtil.validateWebvttHeaderLine(this.parsableWebvttData);
        do {
        } while (!TextUtils.isEmpty(this.parsableWebvttData.readLine()));
        List arrayList = new ArrayList();
        while (this.cueParser.parseNextValidCue(this.parsableWebvttData, this.webvttCueBuilder)) {
            arrayList.add(this.webvttCueBuilder.build());
            this.webvttCueBuilder.reset();
        }
        return new WebvttSubtitle(arrayList);
    }
}
