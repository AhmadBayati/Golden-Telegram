package com.hanista.mobogram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.text.TextUtils;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.LaunchActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

@TargetApi(23)
public class TgChooserTargetService extends ChooserTargetService {
    private RectF bitmapRect;
    private Paint roundPaint;

    /* renamed from: com.hanista.mobogram.messenger.TgChooserTargetService.1 */
    class C06831 implements Runnable {
        final /* synthetic */ ComponentName val$componentName;
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ List val$targets;

        C06831(List list, ComponentName componentName, Semaphore semaphore) {
            this.val$targets = list;
            this.val$componentName = componentName;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            try {
                Iterable arrayList4 = new ArrayList();
                arrayList4.add(Integer.valueOf(UserConfig.getClientUserId()));
                Iterable arrayList5 = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", new Object[]{Integer.valueOf(0), Integer.valueOf(30)}), new Object[0]);
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(0);
                    int i = (int) longValue;
                    int i2 = (int) (longValue >> 32);
                    if (!(i == 0 || i2 == 1)) {
                        if (i > 0) {
                            if (!arrayList4.contains(Integer.valueOf(i))) {
                                arrayList4.add(Integer.valueOf(i));
                            }
                        } else if (!arrayList5.contains(Integer.valueOf(-i))) {
                            arrayList5.add(Integer.valueOf(-i));
                        }
                        arrayList.add(Integer.valueOf(i));
                        if (arrayList.size() == 8) {
                            break;
                        }
                    }
                }
                queryFinalized.dispose();
                if (!arrayList5.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList5), arrayList2);
                }
                if (!arrayList4.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList4), arrayList3);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                Icon icon;
                CharSequence charSequence;
                Bundle bundle = new Bundle();
                int intValue = ((Integer) arrayList.get(i3)).intValue();
                int i4;
                Icon access$000;
                if (intValue > 0) {
                    String formatName;
                    i4 = 0;
                    while (i4 < arrayList3.size()) {
                        User user = (User) arrayList3.get(i4);
                        if (user.id == intValue) {
                            if (!user.bot) {
                                bundle.putLong("dialogId", (long) intValue);
                                access$000 = (user.photo == null || user.photo.photo_small == null) ? null : TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(user.photo.photo_small, true));
                                formatName = ContactsController.formatName(user.first_name, user.last_name);
                                icon = access$000;
                                charSequence = formatName;
                            }
                            formatName = null;
                            access$000 = null;
                            icon = access$000;
                            charSequence = formatName;
                        } else {
                            i4++;
                        }
                    }
                    formatName = null;
                    access$000 = null;
                    icon = access$000;
                    charSequence = formatName;
                } else {
                    i4 = 0;
                    while (i4 < arrayList2.size()) {
                        Chat chat = (Chat) arrayList2.get(i4);
                        if (chat.id == (-intValue)) {
                            if (!ChatObject.isNotInChat(chat) && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                                bundle.putLong("dialogId", (long) intValue);
                                access$000 = (chat.photo == null || chat.photo.photo_small == null) ? null : TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(chat.photo.photo_small, true));
                                icon = access$000;
                                Object obj = chat.title;
                            }
                            charSequence = null;
                            icon = null;
                        } else {
                            i4++;
                        }
                    }
                    charSequence = null;
                    icon = null;
                }
                if (charSequence != null) {
                    if (icon == null) {
                        icon = Icon.createWithResource(ApplicationLoader.applicationContext, C0338R.drawable.logo_avatar);
                    }
                    this.val$targets.add(new ChooserTarget(charSequence, icon, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.val$componentName, bundle));
                }
            }
            this.val$semaphore.release();
        }
    }

    private Icon createRoundBitmap(File file) {
        try {
            Bitmap decodeFile = BitmapFactory.decodeFile(file.toString());
            if (decodeFile != null) {
                Bitmap createBitmap = Bitmap.createBitmap(decodeFile.getWidth(), decodeFile.getHeight(), Config.ARGB_8888);
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                Shader bitmapShader = new BitmapShader(decodeFile, TileMode.CLAMP, TileMode.CLAMP);
                if (this.roundPaint == null) {
                    this.roundPaint = new Paint(1);
                    this.bitmapRect = new RectF();
                }
                this.roundPaint.setShader(bitmapShader);
                this.bitmapRect.set(0.0f, 0.0f, (float) decodeFile.getWidth(), (float) decodeFile.getHeight());
                canvas.drawRoundRect(this.bitmapRect, (float) decodeFile.getWidth(), (float) decodeFile.getHeight(), this.roundPaint);
                return Icon.createWithBitmap(createBitmap);
            }
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
        return null;
    }

    public List<ChooserTarget> onGetChooserTargets(ComponentName componentName, IntentFilter intentFilter) {
        List arrayList = new ArrayList();
        if (UserConfig.isClientActivated() && ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("direct_share", true)) {
            ImageLoader.getInstance();
            Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C06831(arrayList, new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName()), semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        return arrayList;
    }
}
