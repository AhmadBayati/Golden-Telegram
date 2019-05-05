package com.hanista.mobogram.telegraph.biz;

import android.annotation.SuppressLint;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.telegraph.model.UpdateModel;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserName;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserPhone;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.Update;
import com.hanista.mobogram.tgnet.TLRPC.User;

public class UpdateBiz {
    public static int USER_TO_IGNORE_LINK;
    private DataBaseAccess dba;

    public UpdateBiz() {
        this.dba = new DataBaseAccess();
    }

    @SuppressLint({"DefaultLocale"})
    private String formatUserSearchName(String str, String str2, String str3) {
        StringBuilder stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        if (str2 != null && str2.length() > 0) {
            stringBuilder.append(str2);
        }
        if (str3 != null && str3.length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(str3);
        }
        if (str != null && str.length() > 0) {
            stringBuilder.append(";;;");
            stringBuilder.append(str);
        }
        return stringBuilder.toString().toLowerCase();
    }

    public boolean insertUpdate(Update update) {
        User user = MessagesController.getInstance().getUser(Integer.valueOf(update.user_id));
        if (update.user_id == UserConfig.getClientUserId() || user == null) {
            return false;
        }
        UpdateModel updateModel = new UpdateModel();
        updateModel.setUserId(user.id);
        updateModel.setNew(true);
        if (update.date != 0) {
            updateModel.setChangeDate((((long) update.date) * 1000) + TtmlNode.ANONYMOUS_REGION_ID);
        }
        if (update instanceof TL_updateUserName) {
            updateModel.setOldValue(formatUserSearchName(user.username, user.first_name, user.last_name));
            updateModel.setNewValue(formatUserSearchName(update.username, update.first_name, update.last_name));
            updateModel.setType(2);
        } else if (update instanceof TL_updateUserPhone) {
            updateModel.setOldValue(user.phone);
            updateModel.setNewValue(update.phone);
            updateModel.setType(4);
        } else if (!(update instanceof TL_updateUserPhoto)) {
            return false;
        } else {
            updateModel.setType(3);
            if (update.previous && (update.photo instanceof TL_userProfilePhotoEmpty)) {
                updateModel.setNewValue("0");
            } else if (update.previous) {
                updateModel.setNewValue("1");
            } else {
                updateModel.setNewValue("2");
            }
            if (!(updateModel.getNewValue().equals("1") || update.photo == null || update.photo.photo_small == null || update.photo.photo_big == null)) {
                updateModel.setOldValue(update.photo.photo_small.dc_id + "#" + update.photo.photo_small.local_id + "#" + update.photo.photo_small.volume_id + "#" + update.photo.photo_small.secret + "#" + update.photo.photo_big.dc_id + "#" + update.photo.photo_big.local_id + "#" + update.photo.photo_big.volume_id + "#" + update.photo.photo_big.secret);
            }
        }
        this.dba.m847a(updateModel);
        return true;
    }
}
