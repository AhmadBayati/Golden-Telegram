package com.hanista.mobogram.ui.ActionBar;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;

public class BaseFragment {
    protected ActionBar actionBar;
    protected Bundle arguments;
    protected int classGuid;
    protected View fragmentView;
    protected boolean hasOwnBackground;
    private boolean isFinished;
    protected ActionBarLayout parentLayout;
    protected boolean swipeBackEnabled;
    protected Dialog visibleDialog;

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BaseFragment.1 */
    class C09731 implements OnDismissListener {
        C09731() {
        }

        public void onDismiss(DialogInterface dialogInterface) {
            BaseFragment.this.onDialogDismiss(BaseFragment.this.visibleDialog);
            BaseFragment.this.visibleDialog = null;
        }
    }

    public BaseFragment() {
        this.isFinished = false;
        this.visibleDialog = null;
        this.classGuid = 0;
        this.swipeBackEnabled = true;
        this.hasOwnBackground = false;
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
    }

    public BaseFragment(Bundle bundle) {
        this.isFinished = false;
        this.visibleDialog = null;
        this.classGuid = 0;
        this.swipeBackEnabled = true;
        this.hasOwnBackground = false;
        this.arguments = bundle;
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2493d;
            TextView textView = (TextView) this.visibleDialog.findViewById(this.visibleDialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null));
            if (textView != null) {
                textView.setTextColor(i);
            }
            View findViewById = this.visibleDialog.findViewById(this.visibleDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null));
            if (findViewById != null) {
                findViewById.setBackgroundColor(i);
            }
            Button button = (Button) this.visibleDialog.findViewById(16908313);
            if (button != null) {
                button.setTextColor(i);
            }
            button = (Button) this.visibleDialog.findViewById(16908314);
            if (button != null) {
                button.setTextColor(i);
            }
            button = (Button) this.visibleDialog.findViewById(16908315);
            if (button != null) {
                button.setTextColor(i);
            }
        }
    }

    protected void clearViews() {
        ViewGroup viewGroup;
        if (this.fragmentView != null) {
            viewGroup = (ViewGroup) this.fragmentView.getParent();
            if (viewGroup != null) {
                try {
                    viewGroup.removeView(this.fragmentView);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            this.fragmentView = null;
        }
        if (this.actionBar != null) {
            viewGroup = (ViewGroup) this.actionBar.getParent();
            if (viewGroup != null) {
                try {
                    viewGroup.removeView(this.actionBar);
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
            this.actionBar = null;
        }
        this.parentLayout = null;
    }

    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackgroundColor(Theme.ACTION_BAR_COLOR);
        actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_SELECTOR_COLOR);
        if (ThemeUtil.m2490b()) {
            actionBar.setItemsBackgroundColor(AdvanceTheme.m2276a(AdvanceTheme.f2491b, -64));
        }
        if (ThemeUtil.m2490b()) {
            actionBar.setBackgroundResource(C0338R.color.header);
        }
        return actionBar;
    }

    public View createView(Context context) {
        return null;
    }

    public void dismissCurrentDialig() {
        if (this.visibleDialog != null) {
            try {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public void finishFragment() {
        finishFragment(true);
    }

    public void finishFragment(boolean z) {
        if (!this.isFinished && this.parentLayout != null) {
            this.parentLayout.closeLastFragment(z);
        }
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public View getFragmentView() {
        return this.fragmentView;
    }

    public Activity getParentActivity() {
        return this.parentLayout != null ? this.parentLayout.parentActivity : null;
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    protected void initThemeActionBar() {
        if (ThemeUtil.m2490b()) {
            this.actionBar.setBackgroundColor(AdvanceTheme.f2500k);
            this.actionBar.setTitleColor(AdvanceTheme.f2501l);
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.actionBar.setBackButtonDrawable(drawable);
        }
    }

    protected void initThemeBackground(View view) {
        if (ThemeUtil.m2490b()) {
            view.setBackgroundColor(AdvanceTheme.f2497h);
        }
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
    }

    public boolean onBackPressed() {
        return true;
    }

    protected void onBecomeFullyVisible() {
    }

    public void onBeginSlide() {
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        if (this.actionBar != null) {
            this.actionBar.onPause();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    protected AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        return null;
    }

    protected void onDialogDismiss(Dialog dialog) {
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onFragmentDestroy() {
        ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
        this.isFinished = true;
        if (this.actionBar != null) {
            this.actionBar.setEnabled(false);
        }
    }

    public void onLowMemory() {
    }

    public void onPause() {
        if (this.actionBar != null) {
            this.actionBar.onPause();
        }
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing() && dismissDialogOnPause(this.visibleDialog)) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
    }

    public void onResume() {
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
    }

    protected void onTransitionAnimationStart(boolean z, boolean z2) {
    }

    public boolean presentFragment(BaseFragment baseFragment) {
        return this.parentLayout != null && this.parentLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        return this.parentLayout != null && this.parentLayout.presentFragment(baseFragment, z);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return this.parentLayout != null && this.parentLayout.presentFragment(baseFragment, z, z2, true);
    }

    public void removeSelfFromStack() {
        if (!this.isFinished && this.parentLayout != null) {
            this.parentLayout.removeFragmentFromStack(this);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
    }

    public void saveSelfArgs(Bundle bundle) {
    }

    protected void setParentLayout(ActionBarLayout actionBarLayout) {
        if (this.parentLayout != actionBarLayout) {
            ViewGroup viewGroup;
            this.parentLayout = actionBarLayout;
            if (this.fragmentView != null) {
                viewGroup = (ViewGroup) this.fragmentView.getParent();
                if (viewGroup != null) {
                    try {
                        viewGroup.removeView(this.fragmentView);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
                if (!(this.parentLayout == null || this.parentLayout.getContext() == this.fragmentView.getContext())) {
                    this.fragmentView = null;
                }
            }
            if (this.actionBar != null) {
                Object obj = (this.parentLayout == null || this.parentLayout.getContext() == this.actionBar.getContext()) ? null : 1;
                if (this.actionBar.getAddToContainer() || obj != null) {
                    viewGroup = (ViewGroup) this.actionBar.getParent();
                    if (viewGroup != null) {
                        try {
                            viewGroup.removeView(this.actionBar);
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                }
                if (obj != null) {
                    this.actionBar = null;
                }
            }
            if (this.parentLayout != null && this.actionBar == null) {
                this.actionBar = createActionBar(this.parentLayout.getContext());
                this.actionBar.parentFragment = this;
            }
        }
    }

    public void setVisibleDialog(Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false);
    }

    public Dialog showDialog(Dialog dialog, boolean z) {
        Dialog dialog2 = null;
        if (!(dialog == null || this.parentLayout == null || this.parentLayout.animationInProgress || this.parentLayout.startedTracking || (!z && this.parentLayout.checkTransitionAnimation()))) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                this.visibleDialog = dialog;
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new C09731());
                this.visibleDialog.show();
                initTheme();
                dialog2 = this.visibleDialog;
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
        return dialog2;
    }

    public void startActivityForResult(Intent intent, int i) {
        if (this.parentLayout != null) {
            this.parentLayout.startActivityForResult(intent, i);
        }
    }
}
