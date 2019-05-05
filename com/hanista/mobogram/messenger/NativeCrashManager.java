package com.hanista.mobogram.messenger;

import android.app.Activity;

public class NativeCrashManager {
    public static String createLogFile() {
        return null;
    }

    public static void handleDumpFiles(Activity activity) {
        for (String str : searchForDumpFiles()) {
            String createLogFile = createLogFile();
            if (createLogFile != null) {
                uploadDumpAndLog(activity, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH, str, createLogFile);
            }
        }
    }

    private static String[] searchForDumpFiles() {
        return new String[0];
    }

    public static void uploadDumpAndLog(Activity activity, String str, String str2, String str3) {
    }
}
