package com.hanista.mobogram.messenger.exoplayer.text.webvtt;

import android.text.Layout.Alignment;
import android.util.Log;
import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;

final class WebvttCue extends Cue {
    public final long endTime;
    public final long startTime;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.text.webvtt.WebvttCue.1 */
    static /* synthetic */ class C07481 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            $SwitchMap$android$text$Layout$Alignment = new int[Alignment.values().length];
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_CENTER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_OPPOSITE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static final class Builder {
        private static final String TAG = "WebvttCueBuilder";
        private long endTime;
        private float line;
        private int lineAnchor;
        private int lineType;
        private float position;
        private int positionAnchor;
        private long startTime;
        private CharSequence text;
        private Alignment textAlignment;
        private float width;

        public Builder() {
            reset();
        }

        private Builder derivePositionAnchorFromAlignment() {
            if (this.textAlignment != null) {
                switch (C07481.$SwitchMap$android$text$Layout$Alignment[this.textAlignment.ordinal()]) {
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        this.positionAnchor = 0;
                        break;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        this.positionAnchor = 1;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        this.positionAnchor = 2;
                        break;
                    default:
                        Log.w(TAG, "Unrecognized alignment: " + this.textAlignment);
                        this.positionAnchor = 0;
                        break;
                }
            }
            this.positionAnchor = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            return this;
        }

        public WebvttCue build() {
            if (this.position != Cue.DIMEN_UNSET && this.positionAnchor == TLRPC.MESSAGE_FLAG_MEGAGROUP) {
                derivePositionAnchorFromAlignment();
            }
            return new WebvttCue(this.startTime, this.endTime, this.text, this.textAlignment, this.line, this.lineType, this.lineAnchor, this.position, this.positionAnchor, this.width);
        }

        public void reset() {
            this.startTime = 0;
            this.endTime = 0;
            this.text = null;
            this.textAlignment = null;
            this.line = Cue.DIMEN_UNSET;
            this.lineType = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            this.lineAnchor = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            this.position = Cue.DIMEN_UNSET;
            this.positionAnchor = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            this.width = Cue.DIMEN_UNSET;
        }

        public Builder setEndTime(long j) {
            this.endTime = j;
            return this;
        }

        public Builder setLine(float f) {
            this.line = f;
            return this;
        }

        public Builder setLineAnchor(int i) {
            this.lineAnchor = i;
            return this;
        }

        public Builder setLineType(int i) {
            this.lineType = i;
            return this;
        }

        public Builder setPosition(float f) {
            this.position = f;
            return this;
        }

        public Builder setPositionAnchor(int i) {
            this.positionAnchor = i;
            return this;
        }

        public Builder setStartTime(long j) {
            this.startTime = j;
            return this;
        }

        public Builder setText(CharSequence charSequence) {
            this.text = charSequence;
            return this;
        }

        public Builder setTextAlignment(Alignment alignment) {
            this.textAlignment = alignment;
            return this;
        }

        public Builder setWidth(float f) {
            this.width = f;
            return this;
        }
    }

    public WebvttCue(long j, long j2, CharSequence charSequence) {
        this(j, j2, charSequence, null, Cue.DIMEN_UNSET, TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP, Cue.DIMEN_UNSET, TLRPC.MESSAGE_FLAG_MEGAGROUP, Cue.DIMEN_UNSET);
    }

    public WebvttCue(long j, long j2, CharSequence charSequence, Alignment alignment, float f, int i, int i2, float f2, int i3, float f3) {
        super(charSequence, alignment, f, i, i2, f2, i3, f3);
        this.startTime = j;
        this.endTime = j2;
    }

    public WebvttCue(CharSequence charSequence) {
        this(0, 0, charSequence);
    }

    public boolean isNormalCue() {
        return this.line == Cue.DIMEN_UNSET && this.position == Cue.DIMEN_UNSET;
    }
}
