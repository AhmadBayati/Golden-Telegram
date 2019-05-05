package com.hanista.mobogram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.hanista.mobogram.messenger.FileLog;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressLint({"NewApi"})
public class ForegroundDetector implements ActivityLifecycleCallbacks {
    private static ForegroundDetector Instance;
    private long enterBackgroundTime;
    private CopyOnWriteArrayList<Listener> listeners;
    private int refs;
    private boolean wasInBackground;

    public interface Listener {
        void onBecameBackground();

        void onBecameForeground();
    }

    static {
        Instance = null;
    }

    public ForegroundDetector(Application application) {
        this.wasInBackground = true;
        this.enterBackgroundTime = 0;
        this.listeners = new CopyOnWriteArrayList();
        Instance = this;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static ForegroundDetector getInstance() {
        return Instance;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public boolean isBackground() {
        return this.refs == 0;
    }

    public boolean isForeground() {
        return this.refs > 0;
    }

    public boolean isWasInBackground(boolean z) {
        if (z && VERSION.SDK_INT >= 21 && System.currentTimeMillis() - this.enterBackgroundTime < 200) {
            this.wasInBackground = false;
        }
        return this.wasInBackground;
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityStarted(Activity activity) {
        int i = this.refs + 1;
        this.refs = i;
        if (i == 1) {
            if (System.currentTimeMillis() - this.enterBackgroundTime < 200) {
                this.wasInBackground = false;
            }
            FileLog.m16e("tmessages", "switch to foreground");
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                try {
                    ((Listener) it.next()).onBecameForeground();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    public void onActivityStopped(Activity activity) {
        int i = this.refs - 1;
        this.refs = i;
        if (i == 0) {
            this.enterBackgroundTime = System.currentTimeMillis();
            this.wasInBackground = true;
            FileLog.m16e("tmessages", "switch to background");
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                try {
                    ((Listener) it.next()).onBecameBackground();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void resetBackgroundVar() {
        this.wasInBackground = false;
    }
}
