package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class StickerEmojiCell extends FrameLayout {
    private static AccelerateInterpolator interpolator;
    private float alpha;
    private boolean changingAlpha;
    private TextView emojiTextView;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private float scale;
    private boolean scaled;
    private Document sticker;
    private long time;

    static {
        interpolator = new AccelerateInterpolator(0.5f);
    }

    public StickerEmojiCell(Context context) {
        super(context);
        this.alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.time = 0;
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        this.emojiTextView = new TextView(context);
        this.emojiTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.emojiTextView.setTextSize(1, 16.0f);
        addView(this.emojiTextView, LayoutHelper.createFrame(28, 28, 85));
    }

    public void disable() {
        this.changingAlpha = true;
        this.alpha = 0.5f;
        this.time = 0;
        this.imageView.getImageReceiver().setAlpha(this.alpha);
        this.imageView.invalidate();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && (this.changingAlpha || ((this.scaled && this.scale != DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) || !(this.scaled || this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.changingAlpha) {
                this.time += j2;
                if (this.time > 1050) {
                    this.time = 1050;
                }
                this.alpha = 0.5f + (interpolator.getInterpolation(((float) this.time) / 1050.0f) * 0.5f);
                if (this.alpha >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.changingAlpha = false;
                    this.alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha);
            } else if (!this.scaled || this.scale == DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                this.scale += ((float) j2) / 400.0f;
                if (this.scale > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
            } else {
                this.scale -= ((float) j2) / 400.0f;
                if (this.scale < DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                    this.scale = DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return drawChild;
    }

    public Document getSticker() {
        return this.sticker;
    }

    public void invalidate() {
        this.emojiTextView.invalidate();
        super.invalidate();
    }

    public boolean isDisabled() {
        return this.changingAlpha;
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setSticker(Document document, boolean z) {
        if (document != null) {
            this.sticker = document;
            if (document.thumb != null) {
                this.imageView.setImage(document.thumb.location, null, "webp", null);
            }
            if (z) {
                boolean z2;
                for (int i = 0; i < document.attributes.size(); i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeSticker) {
                        if (documentAttribute.alt != null && documentAttribute.alt.length() > 0) {
                            this.emojiTextView.setText(Emoji.replaceEmoji(documentAttribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                            z2 = true;
                            if (!z2) {
                                this.emojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                            }
                            this.emojiTextView.setVisibility(0);
                            return;
                        }
                        z2 = false;
                        if (z2) {
                            this.emojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                        }
                        this.emojiTextView.setVisibility(0);
                        return;
                    }
                }
                z2 = false;
                if (z2) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                }
                this.emojiTextView.setVisibility(0);
                return;
            }
            this.emojiTextView.setVisibility(4);
        }
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }
}
