package com.hanista.mobogram.messenger.exoplayer.text.tx3g;

import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.Subtitle;
import com.hanista.mobogram.messenger.exoplayer.text.SubtitleParser;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

public final class Tx3gParser implements SubtitleParser {
    private final ParsableByteArray parsableByteArray;

    public Tx3gParser() {
        this.parsableByteArray = new ParsableByteArray();
    }

    public boolean canParse(String str) {
        return MimeTypes.APPLICATION_TX3G.equals(str);
    }

    public Subtitle parse(byte[] bArr, int i, int i2) {
        this.parsableByteArray.reset(bArr, i2);
        int readUnsignedShort = this.parsableByteArray.readUnsignedShort();
        return readUnsignedShort == 0 ? Tx3gSubtitle.EMPTY : new Tx3gSubtitle(new Cue(this.parsableByteArray.readString(readUnsignedShort)));
    }
}
