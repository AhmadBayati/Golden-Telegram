package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class PhotoEditToolCell extends FrameLayout {
    private ImageView iconImage;
    private TextView nameTextView;
    private TextView valueTextView;

    public PhotoEditToolCell(Context context) {
        super(context);
        this.iconImage = new ImageView(context);
        this.iconImage.setScaleType(ScaleType.CENTER);
        addView(this.iconImage, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, 12.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setGravity(17);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 10.0f);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 4.0f, 0.0f, 4.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(-9649153);
        this.valueTextView.setTextSize(1, 11.0f);
        this.valueTextView.setTypeface(FontUtil.m1176a().m1160c());
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 57.0f, 3.0f, 0.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW), C0700C.ENCODING_PCM_32BIT));
    }

    public void setIconAndTextAndValue(int i, String str, float f) {
        this.iconImage.setImageResource(i);
        this.nameTextView.setText(str.toUpperCase());
        if (f == 0.0f) {
            this.valueTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
        } else if (f > 0.0f) {
            this.valueTextView.setText("+" + ((int) f));
        } else {
            this.valueTextView.setText(TtmlNode.ANONYMOUS_REGION_ID + ((int) f));
        }
    }

    public void setIconAndTextAndValue(int i, String str, String str2) {
        this.iconImage.setImageResource(i);
        this.nameTextView.setText(str.toUpperCase());
        this.valueTextView.setText(str2);
    }
}
