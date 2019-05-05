package android.support.v4.widget;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

public final class SearchViewCompat {
    private static final SearchViewCompatImpl IMPL;

    public interface OnCloseListener {
        boolean onClose();
    }

    @Deprecated
    public static abstract class OnCloseListenerCompat implements OnCloseListener {
        public boolean onClose() {
            return false;
        }
    }

    public interface OnQueryTextListener {
        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    @Deprecated
    public static abstract class OnQueryTextListenerCompat implements OnQueryTextListener {
        public boolean onQueryTextChange(String str) {
            return false;
        }

        public boolean onQueryTextSubmit(String str) {
            return false;
        }
    }

    interface SearchViewCompatImpl {
        CharSequence getQuery(View view);

        boolean isIconified(View view);

        boolean isQueryRefinementEnabled(View view);

        boolean isSubmitButtonEnabled(View view);

        Object newOnCloseListener(OnCloseListener onCloseListener);

        Object newOnQueryTextListener(OnQueryTextListener onQueryTextListener);

        View newSearchView(Context context);

        void setIconified(View view, boolean z);

        void setImeOptions(View view, int i);

        void setInputType(View view, int i);

        void setMaxWidth(View view, int i);

        void setOnCloseListener(View view, OnCloseListener onCloseListener);

        void setOnQueryTextListener(View view, OnQueryTextListener onQueryTextListener);

        void setQuery(View view, CharSequence charSequence, boolean z);

        void setQueryHint(View view, CharSequence charSequence);

        void setQueryRefinementEnabled(View view, boolean z);

        void setSearchableInfo(View view, ComponentName componentName);

        void setSubmitButtonEnabled(View view, boolean z);
    }

    static class SearchViewCompatStubImpl implements SearchViewCompatImpl {
        SearchViewCompatStubImpl() {
        }

        public CharSequence getQuery(View view) {
            return null;
        }

        public boolean isIconified(View view) {
            return true;
        }

        public boolean isQueryRefinementEnabled(View view) {
            return false;
        }

        public boolean isSubmitButtonEnabled(View view) {
            return false;
        }

        public Object newOnCloseListener(OnCloseListener onCloseListener) {
            return null;
        }

        public Object newOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
            return null;
        }

        public View newSearchView(Context context) {
            return null;
        }

        public void setIconified(View view, boolean z) {
        }

        public void setImeOptions(View view, int i) {
        }

        public void setInputType(View view, int i) {
        }

        public void setMaxWidth(View view, int i) {
        }

        public void setOnCloseListener(View view, OnCloseListener onCloseListener) {
        }

        public void setOnQueryTextListener(View view, OnQueryTextListener onQueryTextListener) {
        }

        public void setQuery(View view, CharSequence charSequence, boolean z) {
        }

        public void setQueryHint(View view, CharSequence charSequence) {
        }

        public void setQueryRefinementEnabled(View view, boolean z) {
        }

        public void setSearchableInfo(View view, ComponentName componentName) {
        }

        public void setSubmitButtonEnabled(View view, boolean z) {
        }
    }

    static class SearchViewCompatHoneycombImpl extends SearchViewCompatStubImpl {

        /* renamed from: android.support.v4.widget.SearchViewCompat.SearchViewCompatHoneycombImpl.1 */
        class C00651 implements OnQueryTextListenerCompatBridge {
            final /* synthetic */ OnQueryTextListener val$listener;

            C00651(OnQueryTextListener onQueryTextListener) {
                this.val$listener = onQueryTextListener;
            }

            public boolean onQueryTextChange(String str) {
                return this.val$listener.onQueryTextChange(str);
            }

            public boolean onQueryTextSubmit(String str) {
                return this.val$listener.onQueryTextSubmit(str);
            }
        }

        /* renamed from: android.support.v4.widget.SearchViewCompat.SearchViewCompatHoneycombImpl.2 */
        class C00662 implements OnCloseListenerCompatBridge {
            final /* synthetic */ OnCloseListener val$listener;

            C00662(OnCloseListener onCloseListener) {
                this.val$listener = onCloseListener;
            }

            public boolean onClose() {
                return this.val$listener.onClose();
            }
        }

        SearchViewCompatHoneycombImpl() {
        }

        protected void checkIfLegalArg(View view) {
            SearchViewCompatHoneycomb.checkIfLegalArg(view);
        }

        public CharSequence getQuery(View view) {
            checkIfLegalArg(view);
            return SearchViewCompatHoneycomb.getQuery(view);
        }

        public boolean isIconified(View view) {
            checkIfLegalArg(view);
            return SearchViewCompatHoneycomb.isIconified(view);
        }

        public boolean isQueryRefinementEnabled(View view) {
            checkIfLegalArg(view);
            return SearchViewCompatHoneycomb.isQueryRefinementEnabled(view);
        }

        public boolean isSubmitButtonEnabled(View view) {
            checkIfLegalArg(view);
            return SearchViewCompatHoneycomb.isSubmitButtonEnabled(view);
        }

        public Object newOnCloseListener(OnCloseListener onCloseListener) {
            return SearchViewCompatHoneycomb.newOnCloseListener(new C00662(onCloseListener));
        }

        public Object newOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
            return SearchViewCompatHoneycomb.newOnQueryTextListener(new C00651(onQueryTextListener));
        }

        public View newSearchView(Context context) {
            return SearchViewCompatHoneycomb.newSearchView(context);
        }

        public void setIconified(View view, boolean z) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setIconified(view, z);
        }

        public void setMaxWidth(View view, int i) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setMaxWidth(view, i);
        }

        public void setOnCloseListener(View view, OnCloseListener onCloseListener) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setOnCloseListener(view, newOnCloseListener(onCloseListener));
        }

        public void setOnQueryTextListener(View view, OnQueryTextListener onQueryTextListener) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setOnQueryTextListener(view, newOnQueryTextListener(onQueryTextListener));
        }

        public void setQuery(View view, CharSequence charSequence, boolean z) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setQuery(view, charSequence, z);
        }

        public void setQueryHint(View view, CharSequence charSequence) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setQueryHint(view, charSequence);
        }

        public void setQueryRefinementEnabled(View view, boolean z) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setQueryRefinementEnabled(view, z);
        }

        public void setSearchableInfo(View view, ComponentName componentName) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setSearchableInfo(view, componentName);
        }

        public void setSubmitButtonEnabled(View view, boolean z) {
            checkIfLegalArg(view);
            SearchViewCompatHoneycomb.setSubmitButtonEnabled(view, z);
        }
    }

    static class SearchViewCompatIcsImpl extends SearchViewCompatHoneycombImpl {
        SearchViewCompatIcsImpl() {
        }

        public View newSearchView(Context context) {
            return SearchViewCompatIcs.newSearchView(context);
        }

        public void setImeOptions(View view, int i) {
            checkIfLegalArg(view);
            SearchViewCompatIcs.setImeOptions(view, i);
        }

        public void setInputType(View view, int i) {
            checkIfLegalArg(view);
            SearchViewCompatIcs.setInputType(view, i);
        }
    }

    static {
        if (VERSION.SDK_INT >= 14) {
            IMPL = new SearchViewCompatIcsImpl();
        } else if (VERSION.SDK_INT >= 11) {
            IMPL = new SearchViewCompatHoneycombImpl();
        } else {
            IMPL = new SearchViewCompatStubImpl();
        }
    }

    private SearchViewCompat(Context context) {
    }

    public static CharSequence getQuery(View view) {
        return IMPL.getQuery(view);
    }

    public static boolean isIconified(View view) {
        return IMPL.isIconified(view);
    }

    public static boolean isQueryRefinementEnabled(View view) {
        return IMPL.isQueryRefinementEnabled(view);
    }

    public static boolean isSubmitButtonEnabled(View view) {
        return IMPL.isSubmitButtonEnabled(view);
    }

    public static View newSearchView(Context context) {
        return IMPL.newSearchView(context);
    }

    public static void setIconified(View view, boolean z) {
        IMPL.setIconified(view, z);
    }

    public static void setImeOptions(View view, int i) {
        IMPL.setImeOptions(view, i);
    }

    public static void setInputType(View view, int i) {
        IMPL.setInputType(view, i);
    }

    public static void setMaxWidth(View view, int i) {
        IMPL.setMaxWidth(view, i);
    }

    public static void setOnCloseListener(View view, OnCloseListener onCloseListener) {
        IMPL.setOnCloseListener(view, onCloseListener);
    }

    public static void setOnQueryTextListener(View view, OnQueryTextListener onQueryTextListener) {
        IMPL.setOnQueryTextListener(view, onQueryTextListener);
    }

    public static void setQuery(View view, CharSequence charSequence, boolean z) {
        IMPL.setQuery(view, charSequence, z);
    }

    public static void setQueryHint(View view, CharSequence charSequence) {
        IMPL.setQueryHint(view, charSequence);
    }

    public static void setQueryRefinementEnabled(View view, boolean z) {
        IMPL.setQueryRefinementEnabled(view, z);
    }

    public static void setSearchableInfo(View view, ComponentName componentName) {
        IMPL.setSearchableInfo(view, componentName);
    }

    public static void setSubmitButtonEnabled(View view, boolean z) {
        IMPL.setSubmitButtonEnabled(view, z);
    }
}
