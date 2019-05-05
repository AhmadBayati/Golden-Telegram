package com.hanista.mobogram.messenger;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build.VERSION;
import android.system.Os;
import android.system.StructStat;
import java.io.File;
import java.util.Map.Entry;

public class ClearCacheService extends IntentService {

    /* renamed from: com.hanista.mobogram.messenger.ClearCacheService.1 */
    class C03511 implements Runnable {
        final /* synthetic */ int val$keepMedia;

        C03511(int i) {
            this.val$keepMedia = i;
        }

        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            long j = (long) ((this.val$keepMedia == 0 ? 7 : 30) * 86400000);
            for (Entry entry : ImageLoader.getInstance().createMediaPaths().entrySet()) {
                if (((Integer) entry.getKey()).intValue() != 4) {
                    File[] listFiles = ((File) entry.getValue()).listFiles();
                    if (listFiles != null) {
                        for (File file : listFiles) {
                            if (file.isFile() && !file.getName().equals(".nomedia")) {
                                if (VERSION.SDK_INT >= 21) {
                                    try {
                                        StructStat stat = Os.stat(file.getPath());
                                        if (stat.st_atime != 0) {
                                            if (stat.st_atime + j < currentTimeMillis) {
                                                file.delete();
                                            }
                                        } else if (stat.st_mtime + j < currentTimeMillis) {
                                            file.delete();
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m18e("tmessages", e);
                                    } catch (Throwable e2) {
                                        FileLog.m18e("tmessages", e2);
                                    }
                                } else if (file.lastModified() + j < currentTimeMillis) {
                                    file.delete();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ClearCacheService() {
        super("ClearCacheService");
    }

    protected void onHandleIntent(Intent intent) {
        ApplicationLoader.postInitApplication();
        int i = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
        if (i != 2) {
            Utilities.globalQueue.postRunnable(new C03511(i));
        }
    }
}
