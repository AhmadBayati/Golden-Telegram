package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoCachedSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_wallPaperSolid;
import com.hanista.mobogram.tgnet.TLRPC.WallPaper;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class WallpaperCell extends FrameLayout {
    private BackupImageView imageView;
    private ImageView imageView2;
    private View selectionView;

    public WallpaperCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        addView(this.imageView, LayoutHelper.createFrame(100, 100, 83));
        this.imageView2 = new ImageView(context);
        this.imageView2.setImageResource(C0338R.drawable.ic_gallery_background);
        this.imageView2.setScaleType(ScaleType.CENTER);
        addView(this.imageView2, LayoutHelper.createFrame(100, 100, 83));
        this.selectionView = new View(context);
        this.selectionView.setBackgroundResource(C0338R.drawable.wall_selection);
        addView(this.selectionView, LayoutHelper.createFrame(100, 102.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(102.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setWallpaper(WallPaper wallPaper, int i) {
        int i2 = 4;
        int i3 = 0;
        if (wallPaper == null) {
            this.imageView.setVisibility(4);
            this.imageView2.setVisibility(0);
            View view = this.selectionView;
            if (i != -1) {
                i3 = 4;
            }
            view.setVisibility(i3);
            ImageView imageView = this.imageView2;
            i3 = (i == -1 || i == 1000001) ? 1514625126 : 1509949440;
            imageView.setBackgroundColor(i3);
            return;
        }
        this.imageView.setVisibility(0);
        this.imageView2.setVisibility(4);
        View view2 = this.selectionView;
        if (i == wallPaper.id) {
            i2 = 0;
        }
        view2.setVisibility(i2);
        if (wallPaper instanceof TL_wallPaperSolid) {
            this.imageView.setImageBitmap(null);
            this.imageView.setBackgroundColor(Theme.MSG_TEXT_COLOR | wallPaper.bg_color);
            return;
        }
        int dp = AndroidUtilities.dp(100.0f);
        int i4 = 0;
        PhotoSize photoSize = null;
        while (i4 < wallPaper.sizes.size()) {
            PhotoSize photoSize2 = (PhotoSize) wallPaper.sizes.get(i4);
            if (photoSize2 == null) {
                photoSize2 = photoSize;
            } else {
                int i5 = photoSize2.f2664w >= photoSize2.f2663h ? photoSize2.f2664w : photoSize2.f2663h;
                if (photoSize != null && ((dp <= 100 || photoSize.location == null || photoSize.location.dc_id != TLRPC.MESSAGE_FLAG_MEGAGROUP) && !(photoSize2 instanceof TL_photoCachedSize) && i5 > dp)) {
                    photoSize2 = photoSize;
                }
            }
            i4++;
            photoSize = photoSize2;
        }
        if (!(photoSize == null || photoSize.location == null)) {
            this.imageView.setImage(photoSize.location, "100_100", (Drawable) null);
        }
        this.imageView.setBackgroundColor(1514625126);
    }
}
