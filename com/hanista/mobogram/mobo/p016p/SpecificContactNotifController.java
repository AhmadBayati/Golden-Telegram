package com.hanista.mobogram.mobo.p016p;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Settings.System;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.PointerIconCompat;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserName;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserPhone;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserStatus;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusOnline;
import com.hanista.mobogram.tgnet.TLRPC.Update;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.LaunchActivity;

/* renamed from: com.hanista.mobogram.mobo.p.f */
public class SpecificContactNotifController {
    public static void m2023a(int i) {
        if (SpecificContactBiz.f2058a.containsKey(Integer.valueOf(i))) {
            NotificationManagerCompat from = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            from.cancel(i + 1);
            from.cancel(i + 2);
        }
    }

    public static void m2024a(Update update) {
        if (MoboConstants.f1323P && SpecificContactBiz.f2058a.containsKey(Integer.valueOf(update.user_id))) {
            CharSequence string;
            int intValue = ((Integer) SpecificContactBiz.f2058a.get(Integer.valueOf(update.user_id))).intValue();
            if ((intValue & 1) != 0 && (update instanceof TL_updateUserStatus) && (update.status instanceof TL_userStatusOnline)) {
                string = LocaleController.getString("get_online", C0338R.string.get_online);
                intValue = update.user_id + 1;
            } else if ((intValue & 2) != 0 && (update instanceof TL_updateUserStatus) && !(update.status instanceof TL_userStatusOnline)) {
                string = LocaleController.getString("get_offline", C0338R.string.get_offline);
                intValue = update.user_id + 2;
            } else if ((intValue & 4) != 0 && (update instanceof TL_updateUserPhoto)) {
                string = LocaleController.getString("changed_photo", C0338R.string.changed_photo);
                intValue = update.user_id + 3;
            } else if ((intValue & 16) != 0 && (update instanceof TL_updateUserPhone)) {
                string = LocaleController.getString("changed_phone", C0338R.string.changed_phone);
                intValue = update.user_id + 4;
            } else if ((intValue & 8) != 0 && (update instanceof TL_updateUserName)) {
                string = LocaleController.getString("changed_name", C0338R.string.changed_name) + "\n" + ContactsController.formatName(update.first_name, update.last_name);
                intValue = update.user_id + 5;
            } else {
                return;
            }
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
            intent.putExtra("userId", update.user_id);
            User user = MessagesController.getInstance().getUser(Integer.valueOf(update.user_id));
            if (user != null) {
                CharSequence userName = UserObject.getUserName(user);
                Builder color = new Builder(ApplicationLoader.applicationContext).setContentTitle(userName).setSmallIcon(C0338R.drawable.notification).setAutoCancel(true).setContentIntent(PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, C0700C.ENCODING_PCM_32BIT)).setGroup("messages").setGroupSummary(true).setColor(-13851168);
                color.setCategory(NotificationCompatApi24.CATEGORY_MESSAGE);
                if (!(user == null || user.phone == null || user.phone.length() <= 0)) {
                    color.addPerson("tel:+" + user.phone);
                }
                color.setContentText(string);
                color.setStyle(new BigTextStyle().bigText(string));
                if (!(user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                    TLObject tLObject = user.photo.photo_small;
                    if (tLObject != null) {
                        BitmapDrawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(tLObject, null, "50_50");
                        if (imageFromMemory != null) {
                            color.setLargeIcon(imageFromMemory.getBitmap());
                        }
                    }
                }
                color.setTicker(LocaleController.getString("MobogramSpecificContact", C0338R.string.MobogramSpecificContact));
                String path = System.DEFAULT_NOTIFICATION_URI.getPath();
                String string2 = sharedPreferences.getString("GlobalSoundPath", path);
                int i = sharedPreferences.getInt("vibrate_messages", 0);
                int i2 = sharedPreferences.getInt("MessagesLed", -16711936);
                if (!(MediaController.m71a().m180h() || string2 == null || string2.equals("NoSound"))) {
                    if (string2.equals(path)) {
                        color.setSound(System.DEFAULT_NOTIFICATION_URI, 5);
                    } else {
                        color.setSound(Uri.parse(string2), 5);
                    }
                }
                if (i2 != 0) {
                    color.setLights(i2, PointerIconCompat.TYPE_DEFAULT, PointerIconCompat.TYPE_DEFAULT);
                }
                if (i == 2 || MediaController.m71a().m180h()) {
                    color.setVibrate(new long[]{0, 0});
                } else if (i == 1) {
                    color.setVibrate(new long[]{0, 100, 0, 100});
                } else if (i == 0 || i == 4) {
                    color.setDefaults(2);
                } else if (i == 3) {
                    color.setVibrate(new long[]{0, 1000});
                }
                NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(intValue, color.build());
            }
        }
    }
}
