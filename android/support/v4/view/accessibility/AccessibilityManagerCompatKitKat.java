package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;

class AccessibilityManagerCompatKitKat {

    interface TouchExplorationStateChangeListenerBridge {
        void onTouchExplorationStateChanged(boolean z);
    }

    /* renamed from: android.support.v4.view.accessibility.AccessibilityManagerCompatKitKat.1 */
    static class C00501 implements TouchExplorationStateChangeListener {
        final /* synthetic */ TouchExplorationStateChangeListenerBridge val$bridge;

        C00501(TouchExplorationStateChangeListenerBridge touchExplorationStateChangeListenerBridge) {
            this.val$bridge = touchExplorationStateChangeListenerBridge;
        }

        public void onTouchExplorationStateChanged(boolean z) {
            this.val$bridge.onTouchExplorationStateChanged(z);
        }
    }

    public static class TouchExplorationStateChangeListenerWrapper implements TouchExplorationStateChangeListener {
        final Object mListener;
        final TouchExplorationStateChangeListenerBridge mListenerBridge;

        public TouchExplorationStateChangeListenerWrapper(Object obj, TouchExplorationStateChangeListenerBridge touchExplorationStateChangeListenerBridge) {
            this.mListener = obj;
            this.mListenerBridge = touchExplorationStateChangeListenerBridge;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            TouchExplorationStateChangeListenerWrapper touchExplorationStateChangeListenerWrapper = (TouchExplorationStateChangeListenerWrapper) obj;
            return this.mListener == null ? touchExplorationStateChangeListenerWrapper.mListener == null : this.mListener.equals(touchExplorationStateChangeListenerWrapper.mListener);
        }

        public int hashCode() {
            return this.mListener == null ? 0 : this.mListener.hashCode();
        }

        public void onTouchExplorationStateChanged(boolean z) {
            this.mListenerBridge.onTouchExplorationStateChanged(z);
        }
    }

    AccessibilityManagerCompatKitKat() {
    }

    public static boolean addTouchExplorationStateChangeListener(AccessibilityManager accessibilityManager, Object obj) {
        return accessibilityManager.addTouchExplorationStateChangeListener((TouchExplorationStateChangeListener) obj);
    }

    public static Object newTouchExplorationStateChangeListener(TouchExplorationStateChangeListenerBridge touchExplorationStateChangeListenerBridge) {
        return new C00501(touchExplorationStateChangeListenerBridge);
    }

    public static boolean removeTouchExplorationStateChangeListener(AccessibilityManager accessibilityManager, Object obj) {
        return accessibilityManager.removeTouchExplorationStateChangeListener((TouchExplorationStateChangeListener) obj);
    }
}
