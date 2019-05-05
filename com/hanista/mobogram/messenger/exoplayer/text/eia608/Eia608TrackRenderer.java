package com.hanista.mobogram.messenger.exoplayer.text.eia608;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.messenger.SecretChatHelper;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.MediaFormatHolder;
import com.hanista.mobogram.messenger.exoplayer.SampleHolder;
import com.hanista.mobogram.messenger.exoplayer.SampleSource;
import com.hanista.mobogram.messenger.exoplayer.SampleSourceTrackRenderer;
import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.TextRenderer;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.Collections;
import java.util.TreeSet;

public final class Eia608TrackRenderer extends SampleSourceTrackRenderer implements Callback {
    private static final int CC_MODE_PAINT_ON = 3;
    private static final int CC_MODE_POP_ON = 2;
    private static final int CC_MODE_ROLL_UP = 1;
    private static final int CC_MODE_UNKNOWN = 0;
    private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
    private static final int MAX_SAMPLE_READAHEAD_US = 5000000;
    private static final int MSG_INVOKE_RENDERER = 0;
    private String caption;
    private int captionMode;
    private int captionRowCount;
    private final StringBuilder captionStringBuilder;
    private final Eia608Parser eia608Parser;
    private final MediaFormatHolder formatHolder;
    private boolean inputStreamEnded;
    private String lastRenderedCaption;
    private final TreeSet<ClosedCaptionList> pendingCaptionLists;
    private ClosedCaptionCtrl repeatableControl;
    private final SampleHolder sampleHolder;
    private final TextRenderer textRenderer;
    private final Handler textRendererHandler;

    public Eia608TrackRenderer(SampleSource sampleSource, TextRenderer textRenderer, Looper looper) {
        SampleSource[] sampleSourceArr = new SampleSource[CC_MODE_ROLL_UP];
        sampleSourceArr[CC_MODE_UNKNOWN] = sampleSource;
        super(sampleSourceArr);
        this.textRenderer = (TextRenderer) Assertions.checkNotNull(textRenderer);
        this.textRendererHandler = looper == null ? null : new Handler(looper, this);
        this.eia608Parser = new Eia608Parser();
        this.formatHolder = new MediaFormatHolder();
        this.sampleHolder = new SampleHolder(CC_MODE_ROLL_UP);
        this.captionStringBuilder = new StringBuilder();
        this.pendingCaptionLists = new TreeSet();
    }

    private void clearPendingSample() {
        this.sampleHolder.timeUs = -1;
        this.sampleHolder.clearData();
    }

    private void consumeCaptionList(ClosedCaptionList closedCaptionList) {
        int length = closedCaptionList.captions.length;
        if (length != 0) {
            int i = CC_MODE_UNKNOWN;
            Object obj = CC_MODE_UNKNOWN;
            while (i < length) {
                Object obj2;
                ClosedCaption closedCaption = closedCaptionList.captions[i];
                if (closedCaption.type == 0) {
                    ClosedCaptionCtrl closedCaptionCtrl = (ClosedCaptionCtrl) closedCaption;
                    obj = (length == CC_MODE_ROLL_UP && closedCaptionCtrl.isRepeatable()) ? CC_MODE_ROLL_UP : CC_MODE_UNKNOWN;
                    if (obj == null || this.repeatableControl == null || this.repeatableControl.cc1 != closedCaptionCtrl.cc1 || this.repeatableControl.cc2 != closedCaptionCtrl.cc2) {
                        if (obj != null) {
                            this.repeatableControl = closedCaptionCtrl;
                        }
                        if (closedCaptionCtrl.isMiscCode()) {
                            handleMiscCode(closedCaptionCtrl);
                        } else if (closedCaptionCtrl.isPreambleAddressCode()) {
                            handlePreambleAddressCode();
                        }
                        obj2 = obj;
                    } else {
                        this.repeatableControl = null;
                        obj2 = obj;
                    }
                } else {
                    handleText((ClosedCaptionText) closedCaption);
                    obj2 = obj;
                }
                i += CC_MODE_ROLL_UP;
                obj = obj2;
            }
            if (obj == null) {
                this.repeatableControl = null;
            }
            if (this.captionMode == CC_MODE_ROLL_UP || this.captionMode == CC_MODE_PAINT_ON) {
                this.caption = getDisplayCaption();
            }
        }
    }

    private String getDisplayCaption() {
        int length = this.captionStringBuilder.length();
        if (length == 0) {
            return null;
        }
        int i = this.captionStringBuilder.charAt(length + -1) == '\n' ? CC_MODE_ROLL_UP : CC_MODE_UNKNOWN;
        if (length == CC_MODE_ROLL_UP && i != 0) {
            return null;
        }
        if (i != 0) {
            length--;
        }
        if (this.captionMode != CC_MODE_ROLL_UP) {
            return this.captionStringBuilder.substring(CC_MODE_UNKNOWN, length);
        }
        int i2;
        i = length;
        for (i2 = CC_MODE_UNKNOWN; i2 < this.captionRowCount && i != -1; i2 += CC_MODE_ROLL_UP) {
            i = this.captionStringBuilder.lastIndexOf("\n", i - 1);
        }
        i2 = i != -1 ? i + CC_MODE_ROLL_UP : CC_MODE_UNKNOWN;
        this.captionStringBuilder.delete(CC_MODE_UNKNOWN, i2);
        return this.captionStringBuilder.substring(CC_MODE_UNKNOWN, length - i2);
    }

    private void handleMiscCode(ClosedCaptionCtrl closedCaptionCtrl) {
        switch (closedCaptionCtrl.cc2) {
            case TLRPC.USER_FLAG_PHOTO /*32*/:
                setCaptionMode(CC_MODE_POP_ON);
            case NalUnitTypes.NAL_TYPE_EOB_NUT /*37*/:
                this.captionRowCount = CC_MODE_POP_ON;
                setCaptionMode(CC_MODE_ROLL_UP);
            case NalUnitTypes.NAL_TYPE_FD_NUT /*38*/:
                this.captionRowCount = CC_MODE_PAINT_ON;
                setCaptionMode(CC_MODE_ROLL_UP);
            case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                this.captionRowCount = DEFAULT_CAPTIONS_ROW_COUNT;
                setCaptionMode(CC_MODE_ROLL_UP);
            case NalUnitTypes.NAL_TYPE_RSV_NVCL41 /*41*/:
                setCaptionMode(CC_MODE_PAINT_ON);
            default:
                if (this.captionMode != 0) {
                    switch (closedCaptionCtrl.cc2) {
                        case NalUnitTypes.NAL_TYPE_SPS_NUT /*33*/:
                            if (this.captionStringBuilder.length() > 0) {
                                this.captionStringBuilder.setLength(this.captionStringBuilder.length() - 1);
                            }
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL44 /*44*/:
                            this.caption = null;
                            if (this.captionMode == CC_MODE_ROLL_UP || this.captionMode == CC_MODE_PAINT_ON) {
                                this.captionStringBuilder.setLength(CC_MODE_UNKNOWN);
                            }
                        case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                            maybeAppendNewline();
                        case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*46*/:
                            this.captionStringBuilder.setLength(CC_MODE_UNKNOWN);
                        case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                            this.caption = getDisplayCaption();
                            this.captionStringBuilder.setLength(CC_MODE_UNKNOWN);
                        default:
                    }
                }
        }
    }

    private void handlePreambleAddressCode() {
        maybeAppendNewline();
    }

    private void handleText(ClosedCaptionText closedCaptionText) {
        if (this.captionMode != 0) {
            this.captionStringBuilder.append(closedCaptionText.text);
        }
    }

    private void invokeRenderer(String str) {
        if (!Util.areEqual(this.lastRenderedCaption, str)) {
            this.lastRenderedCaption = str;
            if (this.textRendererHandler != null) {
                this.textRendererHandler.obtainMessage(CC_MODE_UNKNOWN, str).sendToTarget();
            } else {
                invokeRendererInternal(str);
            }
        }
    }

    private void invokeRendererInternal(String str) {
        if (str == null) {
            this.textRenderer.onCues(Collections.emptyList());
        } else {
            this.textRenderer.onCues(Collections.singletonList(new Cue(str)));
        }
    }

    private boolean isSamplePending() {
        return this.sampleHolder.timeUs != -1;
    }

    private void maybeAppendNewline() {
        int length = this.captionStringBuilder.length();
        if (length > 0 && this.captionStringBuilder.charAt(length - 1) != '\n') {
            this.captionStringBuilder.append('\n');
        }
    }

    private void maybeParsePendingSample(long j) {
        if (this.sampleHolder.timeUs <= 5000000 + j) {
            ClosedCaptionList parse = this.eia608Parser.parse(this.sampleHolder);
            clearPendingSample();
            if (parse != null) {
                this.pendingCaptionLists.add(parse);
            }
        }
    }

    private void setCaptionMode(int i) {
        if (this.captionMode != i) {
            this.captionMode = i;
            this.captionStringBuilder.setLength(CC_MODE_UNKNOWN);
            if (i == CC_MODE_ROLL_UP || i == 0) {
                this.caption = null;
            }
        }
    }

    protected void doSomeWork(long j, long j2, boolean z) {
        if (isSamplePending()) {
            maybeParsePendingSample(j);
        }
        int i = this.inputStreamEnded ? -1 : -3;
        while (!isSamplePending() && r0 == -3) {
            i = readSource(j, this.formatHolder, this.sampleHolder);
            if (i == -3) {
                maybeParsePendingSample(j);
            } else if (i == -1) {
                this.inputStreamEnded = true;
            }
        }
        while (!this.pendingCaptionLists.isEmpty() && ((ClosedCaptionList) this.pendingCaptionLists.first()).timeUs <= j) {
            ClosedCaptionList closedCaptionList = (ClosedCaptionList) this.pendingCaptionLists.pollFirst();
            consumeCaptionList(closedCaptionList);
            if (!closedCaptionList.decodeOnly) {
                invokeRenderer(this.caption);
            }
        }
    }

    protected long getBufferedPositionUs() {
        return -3;
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case CC_MODE_UNKNOWN /*0*/:
                invokeRendererInternal((String) message.obj);
                return true;
            default:
                return false;
        }
    }

    protected boolean handlesTrack(MediaFormat mediaFormat) {
        return this.eia608Parser.canParse(mediaFormat.mimeType);
    }

    protected boolean isEnded() {
        return this.inputStreamEnded;
    }

    protected boolean isReady() {
        return true;
    }

    protected void onDiscontinuity(long j) {
        this.inputStreamEnded = false;
        this.repeatableControl = null;
        this.pendingCaptionLists.clear();
        clearPendingSample();
        this.captionRowCount = DEFAULT_CAPTIONS_ROW_COUNT;
        setCaptionMode(CC_MODE_UNKNOWN);
        invokeRenderer(null);
    }

    protected void onEnabled(int i, long j, boolean z) {
        super.onEnabled(i, j, z);
    }
}
