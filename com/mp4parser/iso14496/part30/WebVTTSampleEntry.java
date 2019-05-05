package com.mp4parser.iso14496.part30;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.Path;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class WebVTTSampleEntry extends AbstractSampleEntry {
    public static final String TYPE = "wvtt";

    public WebVTTSampleEntry() {
        super(TYPE);
    }

    public void getBox(WritableByteChannel writableByteChannel) {
        writableByteChannel.write(getHeader());
        writeContainer(writableByteChannel);
    }

    public WebVTTConfigurationBox getConfig() {
        return (WebVTTConfigurationBox) Path.getPath((AbstractContainerBox) this, WebVTTConfigurationBox.TYPE);
    }

    public WebVTTSourceLabelBox getSourceLabel() {
        return (WebVTTSourceLabelBox) Path.getPath((AbstractContainerBox) this, WebVTTSourceLabelBox.TYPE);
    }

    public void parse(DataSource dataSource, ByteBuffer byteBuffer, long j, BoxParser boxParser) {
        initContainer(dataSource, j, boxParser);
    }
}
