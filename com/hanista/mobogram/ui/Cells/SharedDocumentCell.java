package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.ImageReceiver.ImageReceiverDelegate;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.LineProgressView;
import java.io.File;
import java.util.Date;

public class SharedDocumentCell extends FrameLayout implements FileDownloadProgressListener {
    private static Paint paint;
    private int TAG;
    private CheckBox checkBox;
    private TextView dateTextView;
    private TextView extTextView;
    private int[] icons;
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    private ImageView placeholderImabeView;
    private LineProgressView progressView;
    private ImageView statusImageView;
    private BackupImageView thumbImageView;

    /* renamed from: com.hanista.mobogram.ui.Cells.SharedDocumentCell.1 */
    class C11141 implements ImageReceiverDelegate {
        C11141() {
        }

        public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
            int i = 4;
            SharedDocumentCell.this.extTextView.setVisibility(z ? 4 : 0);
            ImageView access$100 = SharedDocumentCell.this.placeholderImabeView;
            if (!z) {
                i = 0;
            }
            access$100.setVisibility(i);
        }
    }

    public SharedDocumentCell(Context context) {
        super(context);
        this.icons = new int[]{C0338R.drawable.media_doc_blue, C0338R.drawable.media_doc_green, C0338R.drawable.media_doc_red, C0338R.drawable.media_doc_yellow};
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.TAG = MediaController.m71a().m178g();
        this.placeholderImabeView = new ImageView(context);
        addView(this.placeholderImabeView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.extTextView = new TextView(context);
        this.extTextView.setTextColor(-1);
        this.extTextView.setTextSize(1, 14.0f);
        this.extTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.extTextView.setLines(1);
        this.extTextView.setMaxLines(1);
        this.extTextView.setSingleLine(true);
        this.extTextView.setGravity(17);
        this.extTextView.setEllipsize(TruncateAt.END);
        addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        this.thumbImageView = new BackupImageView(context);
        addView(this.thumbImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.thumbImageView.getImageReceiver().setDelegate(new C11141());
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        this.statusImageView = new ImageView(context);
        this.statusImageView.setVisibility(4);
        addView(this.statusImageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 35.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        this.dateTextView = new TextView(context);
        this.dateTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.dateTextView.setTextSize(1, 14.0f);
        this.dateTextView.setLines(1);
        this.dateTextView.setMaxLines(1);
        this.dateTextView.setSingleLine(true);
        this.dateTextView.setEllipsize(TruncateAt.END);
        this.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, BitmapDescriptorFactory.HUE_ORANGE, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        this.progressView = new LineProgressView(context);
        addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0338R.drawable.round_check2);
        this.checkBox.setVisibility(4);
        addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 34.0f, BitmapDescriptorFactory.HUE_ORANGE, LocaleController.isRTL ? 34.0f : 0.0f, 0.0f));
    }

    private int getThumbForNameOrMime(String str, String str2) {
        if (str == null || str.length() == 0) {
            return this.icons[0];
        }
        int i = (str.contains(".doc") || str.contains(".txt") || str.contains(".psd")) ? 0 : (str.contains(".xls") || str.contains(".csv")) ? 1 : (str.contains(".pdf") || str.contains(".ppt") || str.contains(".key")) ? 2 : (str.contains(".zip") || str.contains(".rar") || str.contains(".ai") || str.contains(".mp3") || str.contains(".mov") || str.contains(".avi")) ? 3 : -1;
        if (i == -1) {
            i = str.lastIndexOf(46);
            String substring = i == -1 ? TtmlNode.ANONYMOUS_REGION_ID : str.substring(i + 1);
            i = substring.length() != 0 ? substring.charAt(0) % this.icons.length : str.charAt(0) % this.icons.length;
        }
        return this.icons[i];
    }

    public MessageObject getMessage() {
        return this.message;
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return this.loading;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MediaController.m71a().m149a((FileDownloadProgressListener) this);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    public void onFailedDownload(String str) {
        updateFileExistIcon();
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(56.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void onProgressDownload(String str, float f) {
        if (this.progressView.getVisibility() != 0) {
            updateFileExistIcon();
        }
        this.progressView.setProgress(f, true);
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public void onSuccessDownload(String str) {
        this.progressView.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
        updateFileExistIcon();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    public void setDocument(MessageObject messageObject, boolean z) {
        this.needDivider = z;
        this.message = messageObject;
        this.loaded = false;
        this.loading = false;
        if (messageObject == null || messageObject.getDocument() == null) {
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.extTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.dateTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.placeholderImabeView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap(null);
        } else {
            CharSequence charSequence;
            String str;
            if (messageObject.isMusic()) {
                Document document = messageObject.type == 0 ? messageObject.messageOwner.media.webpage.document : messageObject.messageOwner.media.document;
                int i = 0;
                charSequence = null;
                while (i < document.attributes.size()) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (!(documentAttribute instanceof TL_documentAttributeAudio) || ((documentAttribute.performer == null || documentAttribute.performer.length() == 0) && (documentAttribute.title == null || documentAttribute.title.length() == 0))) {
                        CharSequence charSequence2 = charSequence;
                    } else {
                        str = messageObject.getMusicAuthor() + " - " + messageObject.getMusicTitle();
                    }
                    i++;
                    Object obj = str;
                }
            } else {
                charSequence = null;
            }
            str = FileLoader.getDocumentFileName(messageObject.getDocument());
            if (charSequence == null) {
                charSequence = str;
            }
            this.nameTextView.setText(charSequence);
            this.placeholderImabeView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.placeholderImabeView.setImageResource(getThumbForNameOrMime(str, messageObject.getDocument().mime_type));
            TextView textView = this.extTextView;
            int lastIndexOf = str.lastIndexOf(46);
            textView.setText(lastIndexOf == -1 ? TtmlNode.ANONYMOUS_REGION_ID : str.substring(lastIndexOf + 1).toLowerCase());
            if ((messageObject.getDocument().thumb instanceof TL_photoSizeEmpty) || messageObject.getDocument().thumb == null) {
                this.thumbImageView.setVisibility(4);
                this.thumbImageView.setImageBitmap(null);
            } else {
                this.thumbImageView.setVisibility(0);
                this.thumbImageView.setImage(messageObject.getDocument().thumb.location, "40_40", (Drawable) null);
            }
            long j = ((long) messageObject.messageOwner.date) * 1000;
            textView = this.dateTextView;
            Object[] objArr = new Object[2];
            objArr[0] = AndroidUtilities.formatFileSize((long) messageObject.getDocument().size);
            objArr[1] = LocaleController.formatString("formatDateAtTime", C0338R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(j)), LocaleController.getInstance().formatterDay.format(new Date(j)));
            textView.setText(String.format("%s, %s", objArr));
        }
        setWillNotDraw(!this.needDivider);
        this.progressView.setProgress(0.0f, false);
        updateFileExistIcon();
    }

    public void setTextAndValueAndTypeAndThumb(String str, String str2, String str3, String str4, int i) {
        this.nameTextView.setText(str);
        this.dateTextView.setText(str2);
        if (str3 != null) {
            this.extTextView.setVisibility(0);
            this.extTextView.setText(str3);
        } else {
            this.extTextView.setVisibility(4);
        }
        if (i == 0) {
            this.placeholderImabeView.setImageResource(getThumbForNameOrMime(str, str3));
            this.placeholderImabeView.setVisibility(0);
        } else {
            this.placeholderImabeView.setVisibility(4);
        }
        if (str4 == null && i == 0) {
            this.thumbImageView.setVisibility(4);
            return;
        }
        if (str4 != null) {
            this.thumbImageView.setImage(str4, "40_40", null);
        } else {
            this.thumbImageView.setImageResource(i);
        }
        this.thumbImageView.setVisibility(0);
    }

    public void updateFileExistIcon() {
        if (this.message == null || this.message.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressView.setVisibility(4);
            this.progressView.setProgress(0.0f, false);
            this.statusImageView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            MediaController.m71a().m149a((FileDownloadProgressListener) this);
            return;
        }
        String str = null;
        if ((this.message.messageOwner.attachPath == null || this.message.messageOwner.attachPath.length() == 0 || !new File(this.message.messageOwner.attachPath).exists()) && !FileLoader.getPathToMessage(this.message.messageOwner).exists()) {
            str = FileLoader.getAttachFileName(this.message.getDocument());
        }
        this.loaded = false;
        if (str == null) {
            this.statusImageView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            this.loading = false;
            this.loaded = true;
            MediaController.m71a().m149a((FileDownloadProgressListener) this);
            return;
        }
        MediaController.m71a().m151a(str, (FileDownloadProgressListener) this);
        this.loading = FileLoader.getInstance().isLoadingFile(str);
        this.statusImageView.setVisibility(0);
        this.statusImageView.setImageResource(this.loading ? C0338R.drawable.media_doc_pause : C0338R.drawable.media_doc_load);
        this.dateTextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(14.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : 0, 0);
        if (this.loading) {
            this.progressView.setVisibility(0);
            Float fileProgress = ImageLoader.getInstance().getFileProgress(str);
            if (fileProgress == null) {
                fileProgress = Float.valueOf(0.0f);
            }
            this.progressView.setProgress(fileProgress.floatValue(), false);
            return;
        }
        this.progressView.setVisibility(4);
    }
}
