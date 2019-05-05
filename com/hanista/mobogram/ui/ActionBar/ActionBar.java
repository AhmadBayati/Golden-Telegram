package com.hanista.mobogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Collection;

public class ActionBar extends FrameLayout {
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    private ActionBarMenu actionMode;
    private AnimatorSet actionModeAnimation;
    private View actionModeTop;
    private boolean actionModeVisible;
    private boolean addToContainer;
    private boolean allowOverlayTitle;
    private ImageView backButtonImageView;
    private boolean castShadows;
    private int extraHeight;
    private ImageView ghostView;
    private HorizontalScrollView hsView;
    private boolean interceptTouches;
    private boolean isBackOverlayVisible;
    protected boolean isSearchFieldVisible;
    protected int itemsBackgroundColor;
    private CharSequence lastTitle;
    private ActionBarMenu menu;
    private boolean occupyStatusBar;
    protected BaseFragment parentFragment;
    private SimpleTextView subtitleTextView;
    private SimpleTextView titleTextView;

    public static class ActionBarMenuOnItemClick {
        public boolean canOpenMenu() {
            return true;
        }

        public void onItemClick(int i) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBar.1 */
    class C09471 implements OnClickListener {
        C09471() {
        }

        public void onClick(View view) {
            if (ActionBar.this.isSearchFieldVisible) {
                ActionBar.this.closeSearchField();
            } else if (ActionBar.this.actionBarMenuOnItemClick != null) {
                ActionBar.this.actionBarMenuOnItemClick.onItemClick(-1);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBar.2 */
    class C09482 extends AnimatorListenerAdapterProxy {
        C09482() {
        }

        public void onAnimationCancel(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                ActionBar.this.actionModeAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                ActionBar.this.actionModeAnimation = null;
                if (ActionBar.this.titleTextView != null) {
                    ActionBar.this.titleTextView.setVisibility(4);
                }
                if (ActionBar.this.subtitleTextView != null) {
                    ActionBar.this.subtitleTextView.setVisibility(4);
                }
                if (ActionBar.this.menu != null) {
                    ActionBar.this.menu.setVisibility(4);
                }
            }
        }

        public void onAnimationStart(Animator animator) {
            ActionBar.this.actionMode.setVisibility(0);
            ActionBar.this.hsView.setVisibility(0);
            if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null) {
                ActionBar.this.actionModeTop.setVisibility(0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBar.3 */
    class C09493 extends AnimatorListenerAdapterProxy {
        C09493() {
        }

        public void onAnimationCancel(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                ActionBar.this.actionModeAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animator)) {
                ActionBar.this.actionModeAnimation = null;
                ActionBar.this.actionMode.setVisibility(4);
                ActionBar.this.hsView.setVisibility(4);
                if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null) {
                    ActionBar.this.actionModeTop.setVisibility(4);
                }
            }
        }
    }

    public ActionBar(Context context) {
        super(context);
        this.occupyStatusBar = VERSION.SDK_INT >= 21;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.castShadows = true;
        createGhostModeImage();
        changeGhostModeVisibility();
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView == null) {
            this.backButtonImageView = new ImageView(getContext());
            this.backButtonImageView.setScaleType(ScaleType.CENTER);
            this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
            this.backButtonImageView.setPadding(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), 0, 0, 0);
            addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
            this.backButtonImageView.setOnClickListener(new C09471());
        }
    }

    private void createSubtitleTextView() {
        if (this.subtitleTextView == null) {
            this.subtitleTextView = new SimpleTextView(getContext());
            this.subtitleTextView.setGravity(3);
            this.subtitleTextView.setTextColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
            this.subtitleTextView.setTypeface(FontUtil.m1176a().m1161d());
            addView(this.subtitleTextView, 1, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    private void createTitleTextView() {
        if (this.titleTextView == null) {
            this.titleTextView = new SimpleTextView(getContext());
            this.titleTextView.setGravity(3);
            this.titleTextView.setTextColor(-1);
            this.titleTextView.setTypeface(FontUtil.m1176a().m1160c());
            addView(this.titleTextView, 1, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    public static int getCurrentActionBarHeight() {
        return AndroidUtilities.isTablet() ? AndroidUtilities.dp(64.0f) : AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? AndroidUtilities.dp(48.0f) : AndroidUtilities.dp(56.0f);
    }

    private void initThemeGhostIcon() {
        if (ThemeUtil.m2490b() && getContext() != null) {
            int a = AdvanceTheme.m2276a(AdvanceTheme.f2491b, 21);
            if (this.ghostView != null) {
                this.ghostView.setColorFilter(a, Mode.SRC_IN);
            }
        }
    }

    public void changeGhostModeVisibility() {
        if (MoboConstants.f1338e && MoboConstants.f1349p) {
            this.ghostView.setVisibility(0);
            initThemeGhostIcon();
            return;
        }
        this.ghostView.setVisibility(8);
    }

    public void closeSearchField() {
        if (this.isSearchFieldVisible && this.menu != null) {
            this.menu.closeSearchField();
        }
    }

    public ActionBarMenu createActionMode() {
        if (this.actionMode != null) {
            return this.actionMode;
        }
        this.actionMode = new ActionBarMenu(getContext(), this);
        this.actionMode.setBackgroundColor(-1);
        this.hsView = new HorizontalScrollView(getContext());
        this.hsView.setFillViewport(true);
        this.hsView.addView(this.actionMode);
        addView(this.hsView, indexOfChild(this.backButtonImageView));
        LayoutParams layoutParams = (LayoutParams) this.hsView.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        this.hsView.setLayoutParams(layoutParams);
        this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        layoutParams = (LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        this.hsView.setVisibility(4);
        if (this.occupyStatusBar && this.actionModeTop == null) {
            this.actionModeTop = new View(getContext());
            this.actionModeTop.setBackgroundColor(-1728053248);
            addView(this.actionModeTop);
            layoutParams = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
            this.actionModeTop.setVisibility(4);
        }
        return this.actionMode;
    }

    public void createGhostModeImage() {
        this.ghostView = new ImageView(getContext());
        this.ghostView.setImageResource(ThemeUtil.m2485a().m2298l());
        initThemeGhostIcon();
        this.ghostView.setPadding(20, 20, 20, 20);
        addView(this.ghostView, 0, LayoutHelper.createFrame(-2, -2, 17));
    }

    public ActionBarMenu createMenu() {
        if (this.menu != null) {
            return this.menu;
        }
        this.menu = new ActionBarMenu(getContext(), this);
        addView(this.menu, 1, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public boolean getAddToContainer() {
        return this.addToContainer;
    }

    public ImageView getBackButtonImageView() {
        return this.backButtonImageView;
    }

    public boolean getCastShadows() {
        return this.castShadows;
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public String getSubtitle() {
        return this.subtitleTextView == null ? null : this.subtitleTextView.getText().toString();
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public String getTitle() {
        return this.titleTextView == null ? null : this.titleTextView.getText().toString();
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void hideActionMode() {
        if (this.actionMode != null && this.actionModeVisible) {
            this.actionModeVisible = false;
            Collection arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f}));
            if (this.occupyStatusBar && this.actionModeTop != null) {
                arrayList.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f}));
            }
            if (this.actionModeAnimation != null) {
                this.actionModeAnimation.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(arrayList);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new C09493());
            this.actionModeAnimation.start();
            if (this.titleTextView != null) {
                this.titleTextView.setVisibility(0);
            }
            if (this.subtitleTextView != null) {
                this.subtitleTextView.setVisibility(0);
            }
            if (this.menu != null) {
                this.menu.setVisibility(0);
            }
            if (this.backButtonImageView != null) {
                Drawable drawable = this.backButtonImageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(0.0f, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
            }
        }
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int dp;
        int dp2;
        int currentActionBarHeight;
        int i5 = this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0;
        if (this.backButtonImageView == null || this.backButtonImageView.getVisibility() == 8) {
            dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 6.0f);
        } else {
            this.backButtonImageView.layout(0, i5, this.backButtonImageView.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + i5);
            dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : BitmapDescriptorFactory.HUE_YELLOW);
        }
        if (!(this.menu == null || this.menu.getVisibility() == 8)) {
            if (this.isSearchFieldVisible) {
                dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f);
            } else {
                dp2 = (i3 - i) - this.menu.getMeasuredWidth();
            }
            this.menu.layout(dp2, i5, this.menu.getMeasuredWidth() + dp2, this.menu.getMeasuredHeight() + i5);
        }
        if (!(this.titleTextView == null || this.titleTextView.getVisibility() == 8)) {
            if (this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8) {
                dp2 = (getCurrentActionBarHeight() - this.titleTextView.getTextHeight()) / 2;
            } else {
                currentActionBarHeight = ((getCurrentActionBarHeight() / 2) - this.titleTextView.getTextHeight()) / 2;
                float f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 3.0f : 2.0f;
                dp2 = AndroidUtilities.dp(f) + currentActionBarHeight;
            }
            this.titleTextView.layout(dp, i5 + dp2, this.titleTextView.getMeasuredWidth() + dp, (dp2 + i5) + this.titleTextView.getTextHeight());
        }
        if (!(this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8)) {
            currentActionBarHeight = (((getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2) + (getCurrentActionBarHeight() / 2);
            f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            dp2 = currentActionBarHeight - AndroidUtilities.dp(f);
            this.subtitleTextView.layout(dp, i5 + dp2, this.subtitleTextView.getMeasuredWidth() + dp, (i5 + dp2) + this.subtitleTextView.getTextHeight());
        }
        currentActionBarHeight = getChildCount();
        for (dp2 = 0; dp2 < currentActionBarHeight; dp2++) {
            View childAt = getChildAt(dp2);
            if (!(childAt.getVisibility() == 8 || childAt == this.titleTextView || childAt == this.subtitleTextView || childAt == this.menu || childAt == this.backButtonImageView)) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                dp = layoutParams.gravity;
                if (dp == -1) {
                    dp = 51;
                }
                int i6 = dp & 112;
                switch ((dp & 7) & 7) {
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        dp = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                        break;
                    case VideoPlayer.STATE_ENDED /*5*/:
                        dp = (i3 - measuredWidth) - layoutParams.rightMargin;
                        break;
                    default:
                        dp = layoutParams.leftMargin;
                        break;
                }
                switch (i6) {
                    case TLRPC.USER_FLAG_PHONE /*16*/:
                        i5 = ((((i4 - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        break;
                    case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                        i5 = layoutParams.topMargin;
                        break;
                    case 80:
                        i5 = ((i4 - i2) - measuredHeight) - layoutParams.bottomMargin;
                        break;
                    default:
                        i5 = layoutParams.topMargin;
                        break;
                }
                childAt.layout(dp, i5, measuredWidth + dp, measuredHeight + i5);
            }
        }
    }

    protected void onMeasure(int i, int i2) {
        int dp;
        int size = MeasureSpec.getSize(i);
        MeasureSpec.getSize(i2);
        int currentActionBarHeight = getCurrentActionBarHeight();
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(currentActionBarHeight, C0700C.ENCODING_PCM_32BIT);
        setMeasuredDimension(size, ((this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + currentActionBarHeight) + this.extraHeight);
        if (this.backButtonImageView == null || this.backButtonImageView.getVisibility() == 8) {
            dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        } else {
            this.backButtonImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), C0700C.ENCODING_PCM_32BIT), makeMeasureSpec);
            dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        }
        if (!(this.menu == null || this.menu.getVisibility() == 8)) {
            if (this.isSearchFieldVisible) {
                currentActionBarHeight = MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f), C0700C.ENCODING_PCM_32BIT);
            } else {
                currentActionBarHeight = MeasureSpec.makeMeasureSpec(size, TLRPC.MESSAGE_FLAG_MEGAGROUP);
            }
            this.menu.measure(currentActionBarHeight, makeMeasureSpec);
        }
        if (!((this.titleTextView == null || this.titleTextView.getVisibility() == 8) && (this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8))) {
            SimpleTextView simpleTextView;
            currentActionBarHeight = ((size - (this.menu != null ? this.menu.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - dp;
            if (!(this.titleTextView == null || this.titleTextView.getVisibility() == 8)) {
                simpleTextView = this.titleTextView;
                dp = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18;
                simpleTextView.setTextSize(dp);
                this.titleTextView.measure(MeasureSpec.makeMeasureSpec(currentActionBarHeight, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
            }
            if (!(this.subtitleTextView == null || this.subtitleTextView.getVisibility() == 8)) {
                simpleTextView = this.subtitleTextView;
                dp = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14;
                simpleTextView.setTextSize(dp);
                this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(currentActionBarHeight, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
            }
        }
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (!(childAt.getVisibility() == 8 || childAt == this.titleTextView || childAt == this.subtitleTextView || childAt == this.menu || childAt == this.backButtonImageView)) {
                measureChildWithMargins(childAt, i, 0, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT), 0);
            }
        }
    }

    public void onMenuButtonPressed() {
        if (this.menu != null) {
            this.menu.onMenuButtonPressed();
        }
    }

    protected void onPause() {
        if (this.menu != null) {
            this.menu.hideAllPopupMenus();
        }
    }

    protected void onSearchFieldVisibilityChanged(boolean z) {
        int i = 4;
        this.isSearchFieldVisible = z;
        if (this.titleTextView != null) {
            this.titleTextView.setVisibility(z ? 4 : 0);
        }
        if (this.subtitleTextView != null) {
            SimpleTextView simpleTextView = this.subtitleTextView;
            if (!z) {
                i = 0;
            }
            simpleTextView.setVisibility(i);
        }
        Drawable drawable = this.backButtonImageView.getDrawable();
        if (drawable != null && (drawable instanceof MenuDrawable)) {
            ((MenuDrawable) drawable).setRotation(z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f, true);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent) || this.interceptTouches;
    }

    public void openSearchField(String str) {
        if (this.menu != null && str != null) {
            this.menu.openSearchField(!this.isSearchFieldVisible, str);
        }
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick actionBarMenuOnItemClick) {
        this.actionBarMenuOnItemClick = actionBarMenuOnItemClick;
    }

    public void setAddToContainer(boolean z) {
        this.addToContainer = z;
    }

    public void setAllowOverlayTitle(boolean z) {
        this.allowOverlayTitle = z;
    }

    public void setBackButtonDrawable(Drawable drawable) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(drawable == null ? 8 : 0);
        this.backButtonImageView.setImageDrawable(drawable);
        if (drawable instanceof BackDrawable) {
            ((BackDrawable) drawable).setRotation(isActionModeShowed() ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f, false);
        }
    }

    public void setBackButtonImage(int i) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(i == 0 ? 8 : 0);
        this.backButtonImageView.setImageResource(i);
    }

    public void setCastShadows(boolean z) {
        this.castShadows = z;
    }

    public void setExtraHeight(int i) {
        this.extraHeight = i;
    }

    public void setInterceptTouches(boolean z) {
        this.interceptTouches = z;
    }

    public void setItemsBackgroundColor(int i) {
        this.itemsBackgroundColor = i;
        if (this.backButtonImageView != null) {
            this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
        }
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2491b);
        }
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
        if (this.actionMode != null) {
            this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
    }

    public void setSubtitle(CharSequence charSequence) {
        if (charSequence != null && this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        if (this.subtitleTextView != null) {
            SimpleTextView simpleTextView = this.subtitleTextView;
            int i = (charSequence == null || this.isSearchFieldVisible) ? 8 : 0;
            simpleTextView.setVisibility(i);
            this.subtitleTextView.setText(charSequence);
        }
    }

    public void setSubtitleColor(int i) {
        if (this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        this.subtitleTextView.setTextColor(i);
    }

    public void setTitle(CharSequence charSequence) {
        if (charSequence != null && this.titleTextView == null) {
            createTitleTextView();
        }
        if (this.titleTextView != null) {
            this.lastTitle = charSequence;
            SimpleTextView simpleTextView = this.titleTextView;
            int i = (charSequence == null || this.isSearchFieldVisible) ? 4 : 0;
            simpleTextView.setVisibility(i);
            this.titleTextView.setText(charSequence);
        }
    }

    public void setTitleColor(int i) {
        if (this.titleTextView == null) {
            createTitleTextView();
        }
        if (this.titleTextView != null) {
            this.titleTextView.setTextColor(i);
        }
    }

    public void setTitleOverlayText(String str) {
        if (this.allowOverlayTitle && this.parentFragment.parentLayout != null) {
            CharSequence charSequence;
            if (str == null) {
                charSequence = this.lastTitle;
            }
            if (charSequence != null && this.titleTextView == null) {
                createTitleTextView();
            }
            if (this.titleTextView != null) {
                SimpleTextView simpleTextView = this.titleTextView;
                int i = (charSequence == null || this.isSearchFieldVisible) ? 4 : 0;
                simpleTextView.setVisibility(i);
                this.titleTextView.setText(charSequence);
            }
        }
    }

    public void showActionMode() {
        if (this.actionMode != null && !this.actionModeVisible) {
            this.actionModeVisible = true;
            Collection arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            if (this.occupyStatusBar && this.actionModeTop != null) {
                arrayList.add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            }
            if (this.actionModeAnimation != null) {
                this.actionModeAnimation.cancel();
            }
            this.actionModeAnimation = new AnimatorSet();
            this.actionModeAnimation.playTogether(arrayList);
            this.actionModeAnimation.setDuration(200);
            this.actionModeAnimation.addListener(new C09482());
            this.actionModeAnimation.start();
            if (this.backButtonImageView != null) {
                Drawable drawable = this.backButtonImageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_MODE_SELECTOR_COLOR));
            }
        }
    }

    public void showActionModeTop() {
        if (this.occupyStatusBar && this.actionModeTop == null) {
            this.actionModeTop = new View(getContext());
            this.actionModeTop.setBackgroundColor(-1728053248);
            addView(this.actionModeTop);
            LayoutParams layoutParams = (LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
        }
    }
}
