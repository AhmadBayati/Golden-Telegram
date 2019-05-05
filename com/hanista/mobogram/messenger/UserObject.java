package com.hanista.mobogram.messenger;

import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.tgnet.TLRPC.TL_userContact_old2;
import com.hanista.mobogram.tgnet.TLRPC.TL_userDeleted_old2;
import com.hanista.mobogram.tgnet.TLRPC.TL_userEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_userSelf_old3;
import com.hanista.mobogram.tgnet.TLRPC.User;

public class UserObject {
    public static String getFirstName(User user) {
        if (user == null || isDeleted(user)) {
            return "DELETED";
        }
        String str = user.first_name;
        if (str == null || str.length() == 0) {
            str = user.last_name;
        }
        return (str == null || str.length() <= 0) ? LocaleController.getString("HiddenName", C0338R.string.HiddenName) : str;
    }

    public static String getUserName(User user) {
        if (user == null || isDeleted(user)) {
            return LocaleController.getString("HiddenName", C0338R.string.HiddenName);
        }
        String formatName = ContactsController.formatName(user.first_name, user.last_name);
        return (formatName.length() != 0 || user.phone == null || user.phone.length() == 0) ? formatName : PhoneFormat.getInstance().format("+" + user.phone);
    }

    public static boolean isContact(User user) {
        return (user instanceof TL_userContact_old2) || user.contact || user.mutual_contact;
    }

    public static boolean isDeleted(User user) {
        return user == null || (user instanceof TL_userDeleted_old2) || (user instanceof TL_userEmpty) || user.deleted;
    }

    public static boolean isUserSelf(User user) {
        return (user instanceof TL_userSelf_old3) || user.self;
    }
}
