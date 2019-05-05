package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.AudioEntry;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.ArrayList;

public class AudioCell extends FrameLayout {
    private static Paint paint;
    private AudioEntry audioEntry;
    private TextView authorTextView;
    private CheckBox checkBox;
    private AudioCellDelegate delegate;
    private TextView genreTextView;
    private boolean needDivider;
    private ImageView playButton;
    private TextView timeTextView;
    private TextView titleTextView;

    public interface AudioCellDelegate {
        void startedPlayingAudio(MessageObject messageObject);
    }

    /* renamed from: com.hanista.mobogram.ui.Cells.AudioCell.1 */
    class C11011 implements OnClickListener {
        C11011() {
        }

        public void onClick(View view) {
            if (AudioCell.this.audioEntry == null) {
                return;
            }
            if (!MediaController.m71a().m172d(AudioCell.this.audioEntry.messageObject) || MediaController.m71a().m191s()) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(AudioCell.this.audioEntry.messageObject);
                if (MediaController.m71a().m160a(arrayList, AudioCell.this.audioEntry.messageObject)) {
                    AudioCell.this.playButton.setImageResource(C0338R.drawable.audiosend_pause);
                    if (AudioCell.this.delegate != null) {
                        AudioCell.this.delegate.startedPlayingAudio(AudioCell.this.audioEntry.messageObject);
                        return;
                    }
                    return;
                }
                return;
            }
            MediaController.m71a().m166b(AudioCell.this.audioEntry.messageObject);
            AudioCell.this.playButton.setImageResource(C0338R.drawable.audiosend_play);
        }
    }

    public AudioCell(Context context) {
        int i = 3;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        addView(this.playButton, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 13.0f, 13.0f, LocaleController.isRTL ? 13.0f : 0.0f, 0.0f));
        this.playButton.setOnClickListener(new C11011());
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.titleTextView.setTextSize(1, 16.0f);
        this.titleTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.titleTextView.setLines(1);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 7.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        this.genreTextView = new TextView(context);
        this.genreTextView.setTextColor(-7697782);
        this.genreTextView.setTextSize(1, 14.0f);
        this.genreTextView.setLines(1);
        this.genreTextView.setMaxLines(1);
        this.genreTextView.setSingleLine(true);
        this.genreTextView.setEllipsize(TruncateAt.END);
        this.genreTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.genreTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 28.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        this.authorTextView = new TextView(context);
        this.authorTextView.setTextColor(-7697782);
        this.authorTextView.setTextSize(1, 14.0f);
        this.authorTextView.setLines(1);
        this.authorTextView.setMaxLines(1);
        this.authorTextView.setSingleLine(true);
        this.authorTextView.setEllipsize(TruncateAt.END);
        this.authorTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.authorTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 44.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        this.timeTextView = new TextView(context);
        this.timeTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.timeTextView.setTextSize(1, 13.0f);
        this.timeTextView.setLines(1);
        this.timeTextView.setMaxLines(1);
        this.timeTextView.setSingleLine(true);
        this.timeTextView.setEllipsize(TruncateAt.END);
        this.timeTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(this.timeTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 18.0f : 0.0f, 11.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0338R.drawable.round_check2);
        this.checkBox.setVisibility(0);
        this.checkBox.setColor(-14043401);
        View view = this.checkBox;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i | 48, LocaleController.isRTL ? 18.0f : 0.0f, 39.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
    }

    public AudioEntry getAudioEntry() {
        return this.audioEntry;
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(72.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setAudio(AudioEntry audioEntry, boolean z, boolean z2) {
        this.audioEntry = audioEntry;
        this.titleTextView.setText(this.audioEntry.title);
        this.genreTextView.setText(this.audioEntry.genre);
        this.authorTextView.setText(this.audioEntry.author);
        this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(this.audioEntry.duration / 60), Integer.valueOf(this.audioEntry.duration % 60)}));
        ImageView imageView = this.playButton;
        int i = (!MediaController.m71a().m172d(this.audioEntry.messageObject) || MediaController.m71a().m191s()) ? C0338R.drawable.audiosend_play : C0338R.drawable.audiosend_pause;
        imageView.setImageResource(i);
        this.needDivider = z;
        setWillNotDraw(!z);
        this.checkBox.setChecked(z2, false);
    }

    public void setChecked(boolean z) {
        this.checkBox.setChecked(z, true);
    }

    public void setDelegate(AudioCellDelegate audioCellDelegate) {
        this.delegate = audioCellDelegate;
    }
}
