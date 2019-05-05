package com.hanista.mobogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.BotInlineResult;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.Peer;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeFilename;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_photo;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LetterDrawable;
import com.hanista.mobogram.ui.Components.RadialProgress;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class ContextLinkCell extends View implements FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static TextPaint descriptionTextPaint;
    private static AccelerateInterpolator interpolator;
    private static Paint paint;
    private static Drawable shadowDrawable;
    private static TextPaint titleTextPaint;
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private MessageObject currentMessageObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY;
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private RadialProgress radialProgress;
    private float scale;
    private boolean scaled;
    private long time;
    private StaticLayout titleLayout;
    private int titleY;

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    static {
        interpolator = new AccelerateInterpolator(0.5f);
    }

    public ContextLinkCell(Context context) {
        super(context);
        this.titleY = AndroidUtilities.dp(7.0f);
        this.descriptionY = AndroidUtilities.dp(27.0f);
        this.time = 0;
        if (titleTextPaint == null) {
            titleTextPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            titleTextPaint.setTypeface(FontUtil.m1176a().m1160c());
            titleTextPaint.setColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            descriptionTextPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            descriptionTextPaint.setTypeface(FontUtil.m1176a().m1161d());
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        titleTextPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.linkImageView = new ImageReceiver(this);
        this.letterDrawable = new LetterDrawable();
        this.radialProgress = new RadialProgress(this);
        this.TAG = MediaController.m71a().m178g();
    }

    private void didPressedButton() {
        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
            return;
        }
        if (this.buttonState == 0) {
            if (MediaController.m71a().m158a(this.currentMessageObject)) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (MediaController.m71a().m166b(this.currentMessageObject)) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_GIF) {
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttach != null) {
                FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            } else {
                ImageLoader.getInstance().loadHttpFile(this.inlineResult.content_url, this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC ? "mp3" : "ogg");
            }
            this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.documentAttach != null) {
                FileLoader.getInstance().cancelLoadFile(this.documentAttach);
            } else {
                ImageLoader.getInstance().cancelLoadHttpFile(this.inlineResult.content_url);
            }
            this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
        }
    }

    private Drawable getDrawableForCurrentState() {
        int i = DOCUMENT_ATTACH_TYPE_DOCUMENT;
        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
            return this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT ? Theme.photoStatesDrawables[DOCUMENT_ATTACH_TYPE_MUSIC][DOCUMENT_ATTACH_TYPE_NONE] : null;
        } else {
            if (this.buttonState == -1) {
                return null;
            }
            this.radialProgress.setAlphaForPrevious(false);
            Drawable[] drawableArr = Theme.fileStatesDrawable[this.buttonState + DOCUMENT_ATTACH_TYPE_MUSIC];
            if (!this.buttonPressed) {
                i = DOCUMENT_ATTACH_TYPE_NONE;
            }
            return drawableArr[i];
        }
    }

    private void setAttachType() {
        this.currentMessageObject = null;
        this.documentAttachType = DOCUMENT_ATTACH_TYPE_NONE;
        if (this.documentAttach != null) {
            if (MessageObject.isGifDocument(this.documentAttach)) {
                this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
            } else if (MessageObject.isStickerDocument(this.documentAttach)) {
                this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
            } else if (MessageObject.isMusicDocument(this.documentAttach)) {
                this.documentAttachType = DOCUMENT_ATTACH_TYPE_MUSIC;
            } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
                this.documentAttachType = DOCUMENT_ATTACH_TYPE_AUDIO;
            }
        } else if (this.inlineResult != null) {
            if (this.inlineResult.photo != null) {
                this.documentAttachType = DOCUMENT_ATTACH_TYPE_PHOTO;
            } else if (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                this.documentAttachType = DOCUMENT_ATTACH_TYPE_MUSIC;
            } else if (this.inlineResult.type.equals("voice")) {
                this.documentAttachType = DOCUMENT_ATTACH_TYPE_AUDIO;
            }
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            Message tL_message = new TL_message();
            tL_message.out = true;
            tL_message.id = -Utilities.random.nextInt();
            tL_message.to_id = new TL_peerUser();
            Peer peer = tL_message.to_id;
            int clientUserId = UserConfig.getClientUserId();
            tL_message.from_id = clientUserId;
            peer.user_id = clientUserId;
            tL_message.date = (int) (System.currentTimeMillis() / 1000);
            tL_message.message = "-1";
            tL_message.media = new TL_messageMediaDocument();
            tL_message.media.document = new TL_document();
            tL_message.flags |= 768;
            if (this.documentAttach != null) {
                tL_message.media.document = this.documentAttach;
                tL_message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                String httpUrlExtension = ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC ? "mp3" : "ogg");
                tL_message.media.document.id = 0;
                tL_message.media.document.access_hash = 0;
                tL_message.media.document.date = tL_message.date;
                tL_message.media.document.mime_type = "audio/" + httpUrlExtension;
                tL_message.media.document.size = DOCUMENT_ATTACH_TYPE_NONE;
                tL_message.media.document.thumb = new TL_photoSizeEmpty();
                tL_message.media.document.thumb.type = "s";
                tL_message.media.document.dc_id = DOCUMENT_ATTACH_TYPE_NONE;
                TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                tL_documentAttributeAudio.duration = this.inlineResult.duration;
                tL_documentAttributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : TtmlNode.ANONYMOUS_REGION_ID;
                tL_documentAttributeAudio.performer = this.inlineResult.description != null ? this.inlineResult.description : TtmlNode.ANONYMOUS_REGION_ID;
                tL_documentAttributeAudio.flags |= DOCUMENT_ATTACH_TYPE_AUDIO;
                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    tL_documentAttributeAudio.voice = true;
                }
                tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                tL_documentAttributeFilename.file_name = Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC ? "mp3" : "ogg");
                tL_message.media.document.attributes.add(tL_documentAttributeFilename);
                tL_message.attachPath = new File(FileLoader.getInstance().getDirectory(DOCUMENT_ATTACH_TYPE_VIDEO), Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC ? "mp3" : "ogg")).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(tL_message, null, false);
        }
    }

    public Document getDocument() {
        return this.documentAttach;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public boolean isSticker() {
        return this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        MediaController.m71a().m149a((FileDownloadProgressListener) this);
    }

    protected void onDraw(Canvas canvas) {
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            descriptionTextPaint.setColor(-7697782);
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            descriptionTextPaint.setColor(Theme.MSG_LINK_TEXT_COLOR);
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int intrinsicWidth;
        int intrinsicHeight;
        int imageX;
        int imageY;
        if (this.mediaWebpage) {
            if (this.inlineResult != null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue))) {
                intrinsicWidth = Theme.inlineLocationDrawable.getIntrinsicWidth();
                intrinsicHeight = Theme.inlineLocationDrawable.getIntrinsicHeight();
                imageX = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - intrinsicWidth) / DOCUMENT_ATTACH_TYPE_GIF);
                imageY = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - intrinsicHeight) / DOCUMENT_ATTACH_TYPE_GIF);
                canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                Theme.inlineLocationDrawable.setBounds(imageX, imageY, imageX + intrinsicWidth, imageY + intrinsicHeight);
                Theme.inlineLocationDrawable.draw(canvas);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            this.radialProgress.setProgressColor(this.buttonPressed ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : -1);
            this.radialProgress.draw(canvas);
        } else if (this.inlineResult != null && this.inlineResult.type.equals("file")) {
            intrinsicWidth = Theme.inlineDocDrawable.getIntrinsicWidth();
            intrinsicHeight = Theme.inlineDocDrawable.getIntrinsicHeight();
            imageX = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / DOCUMENT_ATTACH_TYPE_GIF);
            imageY = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / DOCUMENT_ATTACH_TYPE_GIF);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.inlineDocDrawable.setBounds(imageX, imageY, imageX + intrinsicWidth, imageY + intrinsicHeight);
            Theme.inlineDocDrawable.draw(canvas);
        } else if (this.inlineResult != null && (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO) || this.inlineResult.type.equals("voice"))) {
            intrinsicWidth = Theme.inlineAudioDrawable.getIntrinsicWidth();
            intrinsicHeight = Theme.inlineAudioDrawable.getIntrinsicHeight();
            imageX = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / DOCUMENT_ATTACH_TYPE_GIF);
            imageY = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / DOCUMENT_ATTACH_TYPE_GIF);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.inlineAudioDrawable.setBounds(imageX, imageY, imageX + intrinsicWidth, imageY + intrinsicHeight);
            Theme.inlineAudioDrawable.draw(canvas);
        } else if (this.inlineResult == null || !(this.inlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
            this.letterDrawable.draw(canvas);
        } else {
            intrinsicWidth = Theme.inlineLocationDrawable.getIntrinsicWidth();
            intrinsicHeight = Theme.inlineLocationDrawable.getIntrinsicHeight();
            imageX = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / DOCUMENT_ATTACH_TYPE_GIF);
            imageY = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - intrinsicHeight) / DOCUMENT_ATTACH_TYPE_GIF);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.inlineLocationDrawable.setBounds(imageX, imageY, imageX + intrinsicWidth, imageY + intrinsicHeight);
            Theme.inlineLocationDrawable.draw(canvas);
        }
        if (this.drawLinkImageView) {
            canvas.save();
            if ((this.scaled && this.scale != DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) || !(this.scaled || this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                this.lastUpdateTime = currentTimeMillis;
                if (!this.scaled || this.scale == DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                    this.scale += ((float) j) / 400.0f;
                    if (this.scale > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    }
                } else {
                    this.scale -= ((float) j) / 400.0f;
                    if (this.scale < DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) {
                        this.scale = DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD;
                    }
                }
                invalidate();
            }
            canvas.scale(this.scale, this.scale, (float) (getMeasuredWidth() / DOCUMENT_ATTACH_TYPE_GIF), (float) (getMeasuredHeight() / DOCUMENT_ATTACH_TYPE_GIF));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && (this.documentAttachType == DOCUMENT_ATTACH_TYPE_PHOTO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF)) {
            this.radialProgress.setProgressColor(-1);
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), paint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), paint);
            }
        }
        if (this.needShadow && shadowDrawable != null) {
            shadowDrawable.setBounds(DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_NONE, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            shadowDrawable.draw(canvas);
        }
    }

    public void onFailedDownload(String str) {
        updateButtonState(false);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.linkLayout = null;
        this.linkY = AndroidUtilities.dp(27.0f);
        if (this.inlineResult == null && this.documentAttach == null) {
            setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
            return;
        }
        ArrayList arrayList;
        PhotoSize closestPhotoSizeWithSize;
        PhotoSize photoSize;
        String str;
        String str2;
        int i3;
        int i4;
        int size = MeasureSpec.getSize(i);
        int dp = (size - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        if (this.documentAttach != null) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(this.documentAttach.thumb);
            arrayList = arrayList2;
        } else {
            arrayList = (this.inlineResult == null || this.inlineResult.photo == null) ? null : new ArrayList(this.inlineResult.photo.sizes);
        }
        if (!(this.mediaWebpage || this.inlineResult == null)) {
            if (this.inlineResult.title != null) {
                try {
                    this.titleLayout = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(this.inlineResult.title.replace('\n', ' '), titleTextPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), titleTextPaint, (float) Math.min((int) Math.ceil((double) titleTextPaint.measureText(this.inlineResult.title)), dp), TruncateAt.END), titleTextPaint, AndroidUtilities.dp(4.0f) + dp, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                this.letterDrawable.setTitle(this.inlineResult.title);
            }
            if (this.inlineResult.description != null) {
                try {
                    this.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(this.inlineResult.description, descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.dp(13.0f), false), descriptionTextPaint, dp, dp, DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_AUDIO);
                    if (this.descriptionLayout.getLineCount() > 0) {
                        this.linkY = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
            if (this.inlineResult.url != null) {
                try {
                    this.linkLayout = new StaticLayout(TextUtils.ellipsize(this.inlineResult.url.replace('\n', ' '), descriptionTextPaint, (float) Math.min((int) Math.ceil((double) descriptionTextPaint.measureText(this.inlineResult.url)), dp), TruncateAt.MIDDLE), descriptionTextPaint, dp, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                } catch (Throwable e22) {
                    FileLog.m18e("tmessages", e22);
                }
            }
        }
        String str3 = null;
        if (this.documentAttach == null) {
            if (!(this.inlineResult == null || this.inlineResult.photo == null)) {
                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize(), true);
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 80);
                if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                    photoSize = closestPhotoSizeWithSize;
                    closestPhotoSizeWithSize = null;
                } else {
                    photoSize = closestPhotoSizeWithSize;
                    closestPhotoSizeWithSize = closestPhotoSizeWithSize2;
                }
            }
            closestPhotoSizeWithSize = null;
            photoSize = null;
        } else if (MessageObject.isGifDocument(this.documentAttach)) {
            closestPhotoSizeWithSize = null;
            photoSize = this.documentAttach.thumb;
        } else if (MessageObject.isStickerDocument(this.documentAttach)) {
            str3 = "webp";
            closestPhotoSizeWithSize = null;
            photoSize = this.documentAttach.thumb;
        } else {
            if (!(this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO)) {
                closestPhotoSizeWithSize = null;
                photoSize = this.documentAttach.thumb;
            }
            closestPhotoSizeWithSize = null;
            photoSize = null;
        }
        if (this.inlineResult != null) {
            if (!(this.inlineResult.content_url == null || this.inlineResult.type == null)) {
                if (this.inlineResult.type.startsWith("gif")) {
                    if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF) {
                        str = this.inlineResult.content_url;
                        this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                        str2 = str;
                        if (str2 == null && this.inlineResult.thumb_url != null) {
                            str2 = this.inlineResult.thumb_url;
                        }
                    }
                } else if (this.inlineResult.type.equals("photo")) {
                    str = this.inlineResult.thumb_url;
                    str2 = str == null ? this.inlineResult.content_url : str;
                    str2 = this.inlineResult.thumb_url;
                }
            }
            str2 = null;
            str2 = this.inlineResult.thumb_url;
        } else {
            str2 = null;
        }
        if (str2 == null && photoSize == null && closestPhotoSizeWithSize == null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo))) {
            double d = this.inlineResult.send_message.geo.lat;
            double d2 = this.inlineResult.send_message.geo._long;
            Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_MUSIC];
            objArr[DOCUMENT_ATTACH_TYPE_NONE] = Double.valueOf(d);
            objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Double.valueOf(d2);
            objArr[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(Math.min(DOCUMENT_ATTACH_TYPE_GIF, (int) Math.ceil((double) AndroidUtilities.density)));
            objArr[DOCUMENT_ATTACH_TYPE_AUDIO] = Double.valueOf(d);
            objArr[DOCUMENT_ATTACH_TYPE_VIDEO] = Double.valueOf(d2);
            str2 = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", objArr);
        }
        if (this.documentAttach != null) {
            for (i3 = DOCUMENT_ATTACH_TYPE_NONE; i3 < this.documentAttach.attributes.size(); i3 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                DocumentAttribute documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i3);
                if ((documentAttribute instanceof TL_documentAttributeImageSize) || (documentAttribute instanceof TL_documentAttributeVideo)) {
                    i3 = documentAttribute.f2659w;
                    i4 = documentAttribute.f2658h;
                    break;
                }
            }
        }
        i4 = DOCUMENT_ATTACH_TYPE_NONE;
        i3 = DOCUMENT_ATTACH_TYPE_NONE;
        if (i3 == 0 || r2 == 0) {
            if (photoSize != null) {
                if (closestPhotoSizeWithSize != null) {
                    closestPhotoSizeWithSize.size = -1;
                }
                i3 = photoSize.f2664w;
                i4 = photoSize.f2663h;
            } else if (this.inlineResult != null) {
                i3 = this.inlineResult.f2655w;
                i4 = this.inlineResult.f2654h;
            }
        }
        if (i3 == 0 || r2 == 0) {
            i4 = AndroidUtilities.dp(80.0f);
            i3 = i4;
        }
        if (!(this.documentAttach == null && photoSize == null && str2 == null)) {
            String str4;
            str = "52_52_b";
            if (this.mediaWebpage) {
                i4 = (int) (((float) i3) / (((float) i4) / ((float) AndroidUtilities.dp(80.0f))));
                Object[] objArr2;
                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
                    objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                    objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf((int) (((float) i4) / AndroidUtilities.density));
                    objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(80);
                    str = String.format(Locale.US, "%d_%d_b", objArr2);
                    str4 = str;
                } else {
                    objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                    objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf((int) (((float) i4) / AndroidUtilities.density));
                    objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(80);
                    str4 = String.format(Locale.US, "%d_%d", objArr2);
                    str = str4 + "_b";
                }
            } else {
                str4 = "52_52";
            }
            this.linkImageView.setAspectFit(this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER);
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
                if (this.documentAttach != null) {
                    this.linkImageView.setImage(this.documentAttach, null, photoSize != null ? photoSize.location : null, str4, this.documentAttach.size, str3, false);
                } else {
                    this.linkImageView.setImage(null, str2, null, null, photoSize != null ? photoSize.location : null, str4, -1, str3, true);
                }
            } else if (photoSize != null) {
                this.linkImageView.setImage(photoSize.location, str4, closestPhotoSizeWithSize != null ? closestPhotoSizeWithSize.location : null, str, photoSize.size, str3, false);
            } else {
                this.linkImageView.setImage(null, str2, str4, null, closestPhotoSizeWithSize != null ? closestPhotoSizeWithSize.location : null, str, -1, str3, true);
            }
            this.drawLinkImageView = true;
        }
        if (this.mediaWebpage) {
            setBackgroundDrawable(null);
            i4 = MeasureSpec.getSize(i2);
            if (i4 == 0) {
                i4 = AndroidUtilities.dp(100.0f);
            }
            setMeasuredDimension(size, i4);
            int dp2 = (size - AndroidUtilities.dp(24.0f)) / DOCUMENT_ATTACH_TYPE_GIF;
            i3 = (i4 - AndroidUtilities.dp(24.0f)) / DOCUMENT_ATTACH_TYPE_GIF;
            this.radialProgress.setProgressRect(dp2, i3, AndroidUtilities.dp(24.0f) + dp2, AndroidUtilities.dp(24.0f) + i3);
            this.linkImageView.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_NONE, size, i4);
            return;
        }
        setBackgroundResource(C0338R.drawable.list_selector);
        i4 = DOCUMENT_ATTACH_TYPE_NONE;
        if (!(this.titleLayout == null || this.titleLayout.getLineCount() == 0)) {
            i4 = DOCUMENT_ATTACH_TYPE_NONE + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
        }
        if (!(this.descriptionLayout == null || this.descriptionLayout.getLineCount() == 0)) {
            i4 += this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
        }
        if (this.linkLayout != null && this.linkLayout.getLineCount() > 0) {
            i4 += this.linkLayout.getLineBottom(this.linkLayout.getLineCount() - 1);
        }
        setMeasuredDimension(MeasureSpec.getSize(i), (this.needDivider ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE) + Math.max(AndroidUtilities.dp(68.0f), Math.max(AndroidUtilities.dp(52.0f), i4) + AndroidUtilities.dp(16.0f)));
        dp2 = AndroidUtilities.dp(52.0f);
        i4 = LocaleController.isRTL ? (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - dp2 : AndroidUtilities.dp(8.0f);
        this.letterDrawable.setBounds(i4, AndroidUtilities.dp(8.0f), i4 + dp2, AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
        this.linkImageView.setImageCoords(i4, AndroidUtilities.dp(8.0f), dp2, dp2);
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            this.radialProgress.setProgressRect(AndroidUtilities.dp(4.0f) + i4, AndroidUtilities.dp(12.0f), i4 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(56.0f));
        }
    }

    public void onProgressDownload(String str, float f) {
        this.radialProgress.setProgress(f, true);
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (this.buttonState != DOCUMENT_ATTACH_TYPE_VIDEO) {
                updateButtonState(false);
            }
        } else if (this.buttonState != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            updateButtonState(false);
        }
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
        updateButtonState(true);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = true;
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(motionEvent);
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        AndroidUtilities.dp(48.0f);
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            boolean contains = this.letterDrawable.getBounds().contains(x, y);
            if (motionEvent.getAction() == 0) {
                if (contains) {
                    this.buttonPressed = true;
                    invalidate();
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                }
            } else if (this.buttonPressed) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.buttonPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    didPressedButton();
                    invalidate();
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.buttonPressed = false;
                    invalidate();
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF && !contains) {
                    this.buttonPressed = false;
                    invalidate();
                }
                this.radialProgress.swapBackground(getDrawableForCurrentState());
            }
            z = false;
        } else {
            if (!(this.inlineResult == null || this.inlineResult.content_url == null || this.inlineResult.content_url.length() <= 0)) {
                if (motionEvent.getAction() == 0) {
                    if (this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = true;
                    }
                } else if (this.buttonPressed) {
                    if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        this.buttonPressed = false;
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        this.delegate.didPressedImage(this);
                        z = false;
                    } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                        this.buttonPressed = false;
                        z = false;
                    } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF && !this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = false;
                    }
                }
            }
            z = false;
        }
        return !z ? super.onTouchEvent(motionEvent) : z;
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public void setGif(Document document, boolean z) {
        this.needDivider = z;
        this.needShadow = false;
        this.inlineResult = null;
        this.documentAttach = document;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public void setLink(BotInlineResult botInlineResult, boolean z, boolean z2, boolean z3) {
        this.needDivider = z2;
        this.needShadow = z3;
        if (this.needShadow && shadowDrawable == null) {
            shadowDrawable = getContext().getResources().getDrawable(C0338R.drawable.header_shadow);
        }
        this.inlineResult = botInlineResult;
        if (this.inlineResult == null || this.inlineResult.document == null) {
            this.documentAttach = null;
        } else {
            this.documentAttach = this.inlineResult.document;
        }
        this.mediaWebpage = z;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public void updateButtonState(boolean z) {
        String str = null;
        File file = null;
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            if (this.documentAttach != null) {
                str = FileLoader.getAttachFileName(this.documentAttach);
                file = FileLoader.getPathToAttach(this.documentAttach);
            } else {
                String str2 = this.inlineResult.content_url;
                file = new File(FileLoader.getInstance().getDirectory(DOCUMENT_ATTACH_TYPE_VIDEO), Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC ? "mp3" : "ogg"));
                str = str2;
            }
        } else if (this.mediaWebpage) {
            if (this.inlineResult != null) {
                if (this.inlineResult.document instanceof TL_document) {
                    str = FileLoader.getAttachFileName(this.inlineResult.document);
                    file = FileLoader.getPathToAttach(this.inlineResult.document);
                } else if (this.inlineResult.photo instanceof TL_photo) {
                    TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                    str = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                    file = FileLoader.getPathToAttach(closestPhotoSizeWithSize);
                } else if (this.inlineResult.content_url != null) {
                    str = Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, "jpg");
                    file = new File(FileLoader.getInstance().getDirectory(DOCUMENT_ATTACH_TYPE_VIDEO), str);
                } else if (this.inlineResult.thumb_url != null) {
                    str = Utilities.MD5(this.inlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb_url, "jpg");
                    file = new File(FileLoader.getInstance().getDirectory(DOCUMENT_ATTACH_TYPE_VIDEO), str);
                }
            } else if (this.documentAttach != null) {
                str = FileLoader.getAttachFileName(this.documentAttach);
                file = FileLoader.getPathToAttach(this.documentAttach);
            }
        }
        if (TextUtils.isEmpty(str)) {
            this.radialProgress.setBackground(null, false, false);
            return;
        }
        if (file.exists() && file.length() == 0) {
            file.delete();
        }
        if (file.exists()) {
            MediaController.m71a().m149a((FileDownloadProgressListener) this);
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                boolean d = MediaController.m71a().m172d(this.currentMessageObject);
                if (!d || (d && MediaController.m71a().m191s())) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
            } else {
                this.buttonState = -1;
            }
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            invalidate();
            return;
        }
        MediaController.m71a().m151a(str, (FileDownloadProgressListener) this);
        Float fileProgress;
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            if (this.documentAttach != null ? FileLoader.getInstance().isLoadingFile(str) : ImageLoader.getInstance().isLoadingHttpFile(str)) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
                fileProgress = ImageLoader.getInstance().getFileProgress(str);
                if (fileProgress != null) {
                    this.radialProgress.setProgress(fileProgress.floatValue(), z);
                } else {
                    this.radialProgress.setProgress(0.0f, z);
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
            } else {
                this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                this.radialProgress.setProgress(0.0f, z);
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            }
        } else {
            this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            fileProgress = ImageLoader.getInstance().getFileProgress(str);
            this.radialProgress.setProgress(fileProgress != null ? fileProgress.floatValue() : 0.0f, false);
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
        }
        invalidate();
    }
}
