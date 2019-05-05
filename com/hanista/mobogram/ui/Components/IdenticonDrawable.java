package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;

public class IdenticonDrawable extends Drawable {
    private int[] colors;
    private byte[] data;
    private Paint paint;

    public IdenticonDrawable() {
        this.paint = new Paint();
        this.colors = new int[]{-1, -2758925, -13805707, -13657655};
    }

    private int getBits(int i) {
        return (this.data[i / 8] >> (i % 8)) & 3;
    }

    public void draw(Canvas canvas) {
        if (this.data != null) {
            float floor;
            float max;
            float max2;
            int i;
            int i2;
            int i3;
            int i4;
            if (this.data.length == 16) {
                floor = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 8.0f));
                max = Math.max(0.0f, (((float) getBounds().width()) - (floor * 8.0f)) / 2.0f);
                max2 = Math.max(0.0f, (((float) getBounds().height()) - (floor * 8.0f)) / 2.0f);
                i = 0;
                for (i2 = 0; i2 < 8; i2++) {
                    i3 = 0;
                    while (i3 < 8) {
                        i4 = i + 2;
                        this.paint.setColor(this.colors[Math.abs(getBits(i)) % 4]);
                        canvas.drawRect(max + (((float) i3) * floor), (((float) i2) * floor) + max2, ((((float) i3) * floor) + max) + floor, ((((float) i2) * floor) + floor) + max2, this.paint);
                        i3++;
                        i = i4;
                    }
                }
                return;
            }
            floor = (float) Math.floor((double) (((float) Math.min(getBounds().width(), getBounds().height())) / 12.0f));
            max = Math.max(0.0f, (((float) getBounds().width()) - (floor * 12.0f)) / 2.0f);
            max2 = Math.max(0.0f, (((float) getBounds().height()) - (floor * 12.0f)) / 2.0f);
            i2 = 0;
            i = 0;
            while (i2 < 12) {
                i3 = 0;
                i4 = i;
                while (i3 < 12) {
                    this.paint.setColor(this.colors[Math.abs(getBits(i4)) % 4]);
                    canvas.drawRect(max + (((float) i3) * floor), (((float) i2) * floor) + max2, ((((float) i3) * floor) + max) + floor, ((((float) i2) * floor) + floor) + max2, this.paint);
                    i3++;
                    i4 += 2;
                }
                i2++;
                i = i4;
            }
        }
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setEncryptedChat(EncryptedChat encryptedChat) {
        this.data = encryptedChat.key_hash;
        if (this.data == null) {
            byte[] calcAuthKeyHash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
            this.data = calcAuthKeyHash;
            encryptedChat.key_hash = calcAuthKeyHash;
        }
        invalidateSelf();
    }
}
