package android.support.v4.view.accessibility;

import android.os.Build.VERSION;
import android.view.accessibility.AccessibilityEvent;

public final class AccessibilityEventCompat {
    public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 4;
    public static final int CONTENT_CHANGE_TYPE_SUBTREE = 1;
    public static final int CONTENT_CHANGE_TYPE_TEXT = 2;
    public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0;
    private static final AccessibilityEventVersionImpl IMPL;
    public static final int TYPES_ALL_MASK = -1;
    public static final int TYPE_ANNOUNCEMENT = 16384;
    public static final int TYPE_ASSIST_READING_CONTEXT = 16777216;
    public static final int TYPE_GESTURE_DETECTION_END = 524288;
    public static final int TYPE_GESTURE_DETECTION_START = 262144;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512;
    public static final int TYPE_TOUCH_INTERACTION_END = 2097152;
    public static final int TYPE_TOUCH_INTERACTION_START = 1048576;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 32768;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 65536;
    public static final int TYPE_VIEW_CONTEXT_CLICKED = 8388608;
    public static final int TYPE_VIEW_HOVER_ENTER = 128;
    public static final int TYPE_VIEW_HOVER_EXIT = 256;
    public static final int TYPE_VIEW_SCROLLED = 4096;
    public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192;
    public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 131072;
    public static final int TYPE_WINDOWS_CHANGED = 4194304;
    public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;

    interface AccessibilityEventVersionImpl {
        void appendRecord(AccessibilityEvent accessibilityEvent, Object obj);

        int getAction(AccessibilityEvent accessibilityEvent);

        int getContentChangeTypes(AccessibilityEvent accessibilityEvent);

        int getMovementGranularity(AccessibilityEvent accessibilityEvent);

        Object getRecord(AccessibilityEvent accessibilityEvent, int i);

        int getRecordCount(AccessibilityEvent accessibilityEvent);

        void setAction(AccessibilityEvent accessibilityEvent, int i);

        void setContentChangeTypes(AccessibilityEvent accessibilityEvent, int i);

        void setMovementGranularity(AccessibilityEvent accessibilityEvent, int i);
    }

    static class AccessibilityEventStubImpl implements AccessibilityEventVersionImpl {
        AccessibilityEventStubImpl() {
        }

        public void appendRecord(AccessibilityEvent accessibilityEvent, Object obj) {
        }

        public int getAction(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompat.CONTENT_CHANGE_TYPE_UNDEFINED;
        }

        public int getContentChangeTypes(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompat.CONTENT_CHANGE_TYPE_UNDEFINED;
        }

        public int getMovementGranularity(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompat.CONTENT_CHANGE_TYPE_UNDEFINED;
        }

        public Object getRecord(AccessibilityEvent accessibilityEvent, int i) {
            return null;
        }

        public int getRecordCount(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompat.CONTENT_CHANGE_TYPE_UNDEFINED;
        }

        public void setAction(AccessibilityEvent accessibilityEvent, int i) {
        }

        public void setContentChangeTypes(AccessibilityEvent accessibilityEvent, int i) {
        }

        public void setMovementGranularity(AccessibilityEvent accessibilityEvent, int i) {
        }
    }

    static class AccessibilityEventIcsImpl extends AccessibilityEventStubImpl {
        AccessibilityEventIcsImpl() {
        }

        public void appendRecord(AccessibilityEvent accessibilityEvent, Object obj) {
            AccessibilityEventCompatIcs.appendRecord(accessibilityEvent, obj);
        }

        public Object getRecord(AccessibilityEvent accessibilityEvent, int i) {
            return AccessibilityEventCompatIcs.getRecord(accessibilityEvent, i);
        }

        public int getRecordCount(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompatIcs.getRecordCount(accessibilityEvent);
        }
    }

    static class AccessibilityEventJellyBeanImpl extends AccessibilityEventIcsImpl {
        AccessibilityEventJellyBeanImpl() {
        }

        public int getAction(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompatJellyBean.getAction(accessibilityEvent);
        }

        public int getMovementGranularity(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompatJellyBean.getMovementGranularity(accessibilityEvent);
        }

        public void setAction(AccessibilityEvent accessibilityEvent, int i) {
            AccessibilityEventCompatJellyBean.setAction(accessibilityEvent, i);
        }

        public void setMovementGranularity(AccessibilityEvent accessibilityEvent, int i) {
            AccessibilityEventCompatJellyBean.setMovementGranularity(accessibilityEvent, i);
        }
    }

    static class AccessibilityEventKitKatImpl extends AccessibilityEventJellyBeanImpl {
        AccessibilityEventKitKatImpl() {
        }

        public int getContentChangeTypes(AccessibilityEvent accessibilityEvent) {
            return AccessibilityEventCompatKitKat.getContentChangeTypes(accessibilityEvent);
        }

        public void setContentChangeTypes(AccessibilityEvent accessibilityEvent, int i) {
            AccessibilityEventCompatKitKat.setContentChangeTypes(accessibilityEvent, i);
        }
    }

    static {
        if (VERSION.SDK_INT >= 19) {
            IMPL = new AccessibilityEventKitKatImpl();
        } else if (VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityEventJellyBeanImpl();
        } else if (VERSION.SDK_INT >= 14) {
            IMPL = new AccessibilityEventIcsImpl();
        } else {
            IMPL = new AccessibilityEventStubImpl();
        }
    }

    private AccessibilityEventCompat() {
    }

    public static void appendRecord(AccessibilityEvent accessibilityEvent, AccessibilityRecordCompat accessibilityRecordCompat) {
        IMPL.appendRecord(accessibilityEvent, accessibilityRecordCompat.getImpl());
    }

    public static AccessibilityRecordCompat asRecord(AccessibilityEvent accessibilityEvent) {
        return new AccessibilityRecordCompat(accessibilityEvent);
    }

    public static int getContentChangeTypes(AccessibilityEvent accessibilityEvent) {
        return IMPL.getContentChangeTypes(accessibilityEvent);
    }

    public static AccessibilityRecordCompat getRecord(AccessibilityEvent accessibilityEvent, int i) {
        return new AccessibilityRecordCompat(IMPL.getRecord(accessibilityEvent, i));
    }

    public static int getRecordCount(AccessibilityEvent accessibilityEvent) {
        return IMPL.getRecordCount(accessibilityEvent);
    }

    public static void setContentChangeTypes(AccessibilityEvent accessibilityEvent, int i) {
        IMPL.setContentChangeTypes(accessibilityEvent, i);
    }

    public int getAction(AccessibilityEvent accessibilityEvent) {
        return IMPL.getAction(accessibilityEvent);
    }

    public int getMovementGranularity(AccessibilityEvent accessibilityEvent) {
        return IMPL.getMovementGranularity(accessibilityEvent);
    }

    public void setAction(AccessibilityEvent accessibilityEvent, int i) {
        IMPL.setAction(accessibilityEvent, i);
    }

    public void setMovementGranularity(AccessibilityEvent accessibilityEvent, int i) {
        IMPL.setMovementGranularity(accessibilityEvent, i);
    }
}
