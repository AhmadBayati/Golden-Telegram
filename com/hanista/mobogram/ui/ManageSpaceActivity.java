package com.hanista.mobogram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.ui.ActionBar.ActionBarLayout;
import com.hanista.mobogram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.DrawerLayoutContainer;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.ArrayList;

public class ManageSpaceActivity extends Activity implements ActionBarLayoutDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack;
    private static ArrayList<BaseFragment> mainFragmentsStack;
    private ActionBarLayout actionBarLayout;
    private int currentConnectionState;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    private ActionBarLayout layersActionBarLayout;

    /* renamed from: com.hanista.mobogram.ui.ManageSpaceActivity.1 */
    class C16911 implements OnTouchListener {
        C16911() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (ManageSpaceActivity.this.actionBarLayout.fragmentsStack.isEmpty() || motionEvent.getAction() != 1) {
                return false;
            }
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int[] iArr = new int[2];
            ManageSpaceActivity.this.layersActionBarLayout.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (ManageSpaceActivity.this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) i) && x < ((float) (i + ManageSpaceActivity.this.layersActionBarLayout.getWidth())) && y > ((float) i2) && y < ((float) (ManageSpaceActivity.this.layersActionBarLayout.getHeight() + i2)))) {
                return false;
            }
            if (!ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                while (ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                    ManageSpaceActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                }
                ManageSpaceActivity.this.layersActionBarLayout.closeLastFragment(true);
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ManageSpaceActivity.2 */
    class C16922 implements OnClickListener {
        C16922() {
        }

        public void onClick(View view) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ManageSpaceActivity.3 */
    class C16933 implements OnGlobalLayoutListener {
        C16933() {
        }

        public void onGlobalLayout() {
            ManageSpaceActivity.this.needLayout();
            if (ManageSpaceActivity.this.actionBarLayout == null) {
                return;
            }
            if (VERSION.SDK_INT < 16) {
                ManageSpaceActivity.this.actionBarLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                ManageSpaceActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    static {
        mainFragmentsStack = new ArrayList();
        layerFragmentsStack = new ArrayList();
    }

    private boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3) {
        if (AndroidUtilities.isTablet()) {
            if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                this.layersActionBarLayout.addFragmentToStack(new CacheControlActivity());
            }
        } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.addFragmentToStack(new CacheControlActivity());
        }
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
        }
        intent.setAction(null);
        return false;
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
        }
    }

    private void updateCurrentConnectionState() {
        String str = null;
        if (this.currentConnectionState == 2) {
            str = LocaleController.getString("WaitingForNetwork", C0338R.string.WaitingForNetwork);
        } else if (this.currentConnectionState == 1) {
            str = LocaleController.getString("Connecting", C0338R.string.Connecting);
        } else if (this.currentConnectionState == 4) {
            str = LocaleController.getString("Updating", C0338R.string.Updating);
        }
        this.actionBarLayout.setTitleOverlayText(str);
    }

    public void fixLayout() {
        if (AndroidUtilities.isTablet() && this.actionBarLayout != null) {
            this.actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new C16933());
        }
    }

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout) {
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            if (actionBarLayout == this.actionBarLayout && actionBarLayout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (actionBarLayout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (actionBarLayout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    public void needLayout() {
        if (AndroidUtilities.isTablet()) {
            LayoutParams layoutParams = (LayoutParams) this.layersActionBarLayout.getLayoutParams();
            layoutParams.leftMargin = (AndroidUtilities.displaySize.x - layoutParams.width) / 2;
            int i = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            layoutParams.topMargin = i + (((AndroidUtilities.displaySize.y - layoutParams.height) - i) / 2);
            this.layersActionBarLayout.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2) {
                int i2 = (AndroidUtilities.displaySize.x / 100) * 35;
                i = i2 < AndroidUtilities.dp(320.0f) ? AndroidUtilities.dp(320.0f) : i2;
                layoutParams = (LayoutParams) this.actionBarLayout.getLayoutParams();
                layoutParams.width = i;
                layoutParams.height = -1;
                this.actionBarLayout.setLayoutParams(layoutParams);
                if (AndroidUtilities.isSmallTablet() && this.actionBarLayout.fragmentsStack.size() == 2) {
                    ((BaseFragment) this.actionBarLayout.fragmentsStack.get(1)).onPause();
                    this.actionBarLayout.fragmentsStack.remove(1);
                    this.actionBarLayout.showLastFragment();
                    return;
                }
                return;
            }
            layoutParams = (LayoutParams) this.actionBarLayout.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            this.actionBarLayout.setLayoutParams(layoutParams);
        }
    }

    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout) {
        return true;
    }

    public void onBackPressed() {
        if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (!AndroidUtilities.isTablet()) {
            this.actionBarLayout.onBackPressed();
        } else if (this.layersActionBarLayout.getVisibility() == 0) {
            this.layersActionBarLayout.onBackPressed();
        } else {
            this.actionBarLayout.onBackPressed();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    protected void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        requestWindowFeature(1);
        setTheme(C0338R.style.Theme_TMessages);
        getWindow().setBackgroundDrawableResource(C0338R.drawable.transparent);
        super.onCreate(bundle);
        Theme.loadRecources(this);
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(identifier);
        }
        this.actionBarLayout = new ActionBarLayout(this);
        this.drawerLayoutContainer = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            View relativeLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(relativeLayout);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            relativeLayout.setLayoutParams(layoutParams);
            View view = new View(this);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(C0338R.drawable.catstile);
            bitmapDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            view.setBackgroundDrawable(bitmapDrawable);
            relativeLayout.addView(view, LayoutHelper.createRelative(-1, -1));
            relativeLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
            View frameLayout = new FrameLayout(this);
            frameLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            relativeLayout.addView(frameLayout, LayoutHelper.createRelative(-1, -1));
            frameLayout.setOnTouchListener(new C16911());
            frameLayout.setOnClickListener(new C16922());
            this.layersActionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(frameLayout);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(C0338R.drawable.boxshadow);
            relativeLayout.addView(this.layersActionBarLayout, LayoutHelper.createRelative(530, 528));
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        } else {
            this.drawerLayoutContainer.addView(this.actionBarLayout, new ViewGroup.LayoutParams(-1, -1));
        }
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.currentConnectionState = ConnectionsManager.getInstance().getConnectionState();
        handleIntent(getIntent(), false, bundle != null, false);
        needLayout();
    }

    protected void onDestroy() {
        super.onDestroy();
        onFinish();
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onLowMemory();
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    protected void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onPause();
        }
    }

    public boolean onPreIme() {
        return false;
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet() && actionBarLayout == this.layersActionBarLayout) {
            this.actionBarLayout.rebuildAllFragmentViews(true);
            this.actionBarLayout.showLastFragment();
        }
    }

    protected void onResume() {
        super.onResume();
        this.actionBarLayout.onResume();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onResume();
        }
    }

    public void presentFragment(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return this.actionBarLayout.presentFragment(baseFragment, z, z2, true);
    }
}
