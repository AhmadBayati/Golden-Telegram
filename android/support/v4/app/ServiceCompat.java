package android.support.v4.app;

import android.app.Service;
import android.support.v4.os.BuildCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ServiceCompat {
    static final ServiceCompatImpl IMPL;
    public static final int START_STICKY = 1;
    public static final int STOP_FOREGROUND_DETACH = 2;
    public static final int STOP_FOREGROUND_REMOVE = 1;

    interface ServiceCompatImpl {
        void stopForeground(Service service, int i);
    }

    static class Api24ServiceCompatImpl implements ServiceCompatImpl {
        Api24ServiceCompatImpl() {
        }

        public void stopForeground(Service service, int i) {
            ServiceCompatApi24.stopForeground(service, i);
        }
    }

    static class BaseServiceCompatImpl implements ServiceCompatImpl {
        BaseServiceCompatImpl() {
        }

        public void stopForeground(Service service, int i) {
            service.stopForeground((i & ServiceCompat.STOP_FOREGROUND_REMOVE) != 0);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface StopForegroundFlags {
    }

    static {
        if (BuildCompat.isAtLeastN()) {
            IMPL = new Api24ServiceCompatImpl();
        } else {
            IMPL = new BaseServiceCompatImpl();
        }
    }

    private ServiceCompat() {
    }

    public static void stopForeground(Service service, int i) {
        IMPL.stopForeground(service, i);
    }
}
