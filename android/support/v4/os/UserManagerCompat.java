package android.support.v4.os;

import android.content.Context;

public class UserManagerCompat {
    private UserManagerCompat() {
    }

    @Deprecated
    public static boolean isUserRunningAndLocked(Context context) {
        return !isUserUnlocked(context);
    }

    @Deprecated
    public static boolean isUserRunningAndUnlocked(Context context) {
        return isUserUnlocked(context);
    }

    public static boolean isUserUnlocked(Context context) {
        return BuildCompat.isAtLeastN() ? UserManagerCompatApi24.isUserUnlocked(context) : true;
    }
}
