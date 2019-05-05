package com.hanista.mobogram.messenger.exoplayer.text;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.hanista.mobogram.messenger.exoplayer.ExoPlaybackException;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.MediaFormatHolder;
import com.hanista.mobogram.messenger.exoplayer.SampleHolder;
import com.hanista.mobogram.messenger.exoplayer.SampleSource;
import com.hanista.mobogram.messenger.exoplayer.SampleSourceTrackRenderer;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TargetApi(16)
public final class TextTrackRenderer extends SampleSourceTrackRenderer implements Callback {
    private static final List<Class<? extends SubtitleParser>> DEFAULT_PARSER_CLASSES;
    private static final int MSG_UPDATE_OVERLAY = 0;
    private final MediaFormatHolder formatHolder;
    private boolean inputStreamEnded;
    private PlayableSubtitle nextSubtitle;
    private int nextSubtitleEventIndex;
    private SubtitleParserHelper parserHelper;
    private int parserIndex;
    private HandlerThread parserThread;
    private PlayableSubtitle subtitle;
    private final SubtitleParser[] subtitleParsers;
    private final TextRenderer textRenderer;
    private final Handler textRendererHandler;

    static {
        DEFAULT_PARSER_CLASSES = new ArrayList();
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.text.ttml.TtmlParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e2) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.text.webvtt.Mp4WebvttParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e3) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.text.subrip.SubripParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e4) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.text.tx3g.Tx3gParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e5) {
        }
    }

    public TextTrackRenderer(SampleSource sampleSource, TextRenderer textRenderer, Looper looper, SubtitleParser... subtitleParserArr) {
        this(new SampleSource[]{sampleSource}, textRenderer, looper, subtitleParserArr);
    }

    public TextTrackRenderer(SampleSource[] sampleSourceArr, TextRenderer textRenderer, Looper looper, SubtitleParser... subtitleParserArr) {
        super(sampleSourceArr);
        this.textRenderer = (TextRenderer) Assertions.checkNotNull(textRenderer);
        this.textRendererHandler = looper == null ? null : new Handler(looper, this);
        if (subtitleParserArr == null || subtitleParserArr.length == 0) {
            subtitleParserArr = new SubtitleParser[DEFAULT_PARSER_CLASSES.size()];
            int i = 0;
            while (i < subtitleParserArr.length) {
                try {
                    subtitleParserArr[i] = (SubtitleParser) ((Class) DEFAULT_PARSER_CLASSES.get(i)).newInstance();
                    i++;
                } catch (Throwable e) {
                    throw new IllegalStateException("Unexpected error creating default parser", e);
                } catch (Throwable e2) {
                    throw new IllegalStateException("Unexpected error creating default parser", e2);
                }
            }
        }
        this.subtitleParsers = subtitleParserArr;
        this.formatHolder = new MediaFormatHolder();
    }

    private void clearTextRenderer() {
        updateTextRenderer(Collections.emptyList());
    }

    private long getNextEventTime() {
        return (this.nextSubtitleEventIndex == -1 || this.nextSubtitleEventIndex >= this.subtitle.getEventTimeCount()) ? PtsTimestampAdjuster.DO_NOT_OFFSET : this.subtitle.getEventTime(this.nextSubtitleEventIndex);
    }

    private int getParserIndex(MediaFormat mediaFormat) {
        for (int i = 0; i < this.subtitleParsers.length; i++) {
            if (this.subtitleParsers[i].canParse(mediaFormat.mimeType)) {
                return i;
            }
        }
        return -1;
    }

    private void invokeRendererInternalCues(List<Cue> list) {
        this.textRenderer.onCues(list);
    }

    private void updateTextRenderer(List<Cue> list) {
        if (this.textRendererHandler != null) {
            this.textRendererHandler.obtainMessage(0, list).sendToTarget();
        } else {
            invokeRendererInternalCues(list);
        }
    }

    protected void doSomeWork(long j, long j2, boolean z) {
        if (this.nextSubtitle == null) {
            try {
                this.nextSubtitle = this.parserHelper.getAndClearResult();
            } catch (Throwable e) {
                throw new ExoPlaybackException(e);
            }
        }
        if (getState() == 3) {
            boolean z2 = false;
            if (this.subtitle != null) {
                long nextEventTime = getNextEventTime();
                while (nextEventTime <= j) {
                    this.nextSubtitleEventIndex++;
                    nextEventTime = getNextEventTime();
                    z2 = true;
                }
            }
            if (this.nextSubtitle != null && this.nextSubtitle.startTimeUs <= j) {
                this.subtitle = this.nextSubtitle;
                this.nextSubtitle = null;
                this.nextSubtitleEventIndex = this.subtitle.getNextEventTimeIndex(j);
                z2 = true;
            }
            if (z2) {
                updateTextRenderer(this.subtitle.getCues(j));
            }
            if (!this.inputStreamEnded && this.nextSubtitle == null && !this.parserHelper.isParsing()) {
                SampleHolder sampleHolder = this.parserHelper.getSampleHolder();
                sampleHolder.clearData();
                int readSource = readSource(j, this.formatHolder, sampleHolder);
                if (readSource == -4) {
                    this.parserHelper.setFormat(this.formatHolder.format);
                } else if (readSource == -3) {
                    this.parserHelper.startParseOperation();
                } else if (readSource == -1) {
                    this.inputStreamEnded = true;
                }
            }
        }
    }

    protected long getBufferedPositionUs() {
        return -3;
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                invokeRendererInternalCues((List) message.obj);
                return true;
            default:
                return false;
        }
    }

    protected boolean handlesTrack(MediaFormat mediaFormat) {
        return getParserIndex(mediaFormat) != -1;
    }

    protected boolean isEnded() {
        return this.inputStreamEnded && (this.subtitle == null || getNextEventTime() == PtsTimestampAdjuster.DO_NOT_OFFSET);
    }

    protected boolean isReady() {
        return true;
    }

    protected void onDisabled() {
        this.subtitle = null;
        this.nextSubtitle = null;
        this.parserThread.quit();
        this.parserThread = null;
        this.parserHelper = null;
        clearTextRenderer();
        super.onDisabled();
    }

    protected void onDiscontinuity(long j) {
        this.inputStreamEnded = false;
        this.subtitle = null;
        this.nextSubtitle = null;
        clearTextRenderer();
        if (this.parserHelper != null) {
            this.parserHelper.flush();
        }
    }

    protected void onEnabled(int i, long j, boolean z) {
        super.onEnabled(i, j, z);
        this.parserIndex = getParserIndex(getFormat(i));
        this.parserThread = new HandlerThread("textParser");
        this.parserThread.start();
        this.parserHelper = new SubtitleParserHelper(this.parserThread.getLooper(), this.subtitleParsers[this.parserIndex]);
    }
}
