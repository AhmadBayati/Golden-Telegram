package com.hanista.mobogram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class IntroActivity extends Activity {
    private ViewGroup bottomPages;
    private int[] icons;
    private boolean justCreated;
    private int lastPage;
    private int[] messages;
    private boolean startPressed;
    private int[] titles;
    private ImageView topImage1;
    private ImageView topImage2;
    private ViewPager viewPager;

    /* renamed from: com.hanista.mobogram.ui.IntroActivity.1 */
    class C15861 implements OnPageChangeListener {

        /* renamed from: com.hanista.mobogram.ui.IntroActivity.1.1 */
        class C15841 implements AnimationListener {
            final /* synthetic */ ImageView val$fadeoutImage;

            C15841(ImageView imageView) {
                this.val$fadeoutImage = imageView;
            }

            public void onAnimationEnd(Animation animation) {
                this.val$fadeoutImage.setVisibility(8);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        }

        /* renamed from: com.hanista.mobogram.ui.IntroActivity.1.2 */
        class C15852 implements AnimationListener {
            final /* synthetic */ ImageView val$fadeinImage;

            C15852(ImageView imageView) {
                this.val$fadeinImage = imageView;
            }

            public void onAnimationEnd(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                this.val$fadeinImage.setVisibility(0);
            }
        }

        C15861() {
        }

        public void onPageScrollStateChanged(int i) {
            if ((i == 0 || i == 2) && IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                ImageView access$300;
                ImageView access$400;
                IntroActivity.this.lastPage = IntroActivity.this.viewPager.getCurrentItem();
                if (IntroActivity.this.topImage1.getVisibility() == 0) {
                    access$300 = IntroActivity.this.topImage1;
                    access$400 = IntroActivity.this.topImage2;
                } else {
                    access$300 = IntroActivity.this.topImage2;
                    access$400 = IntroActivity.this.topImage1;
                }
                access$400.bringToFront();
                access$400.setImageResource(IntroActivity.this.icons[IntroActivity.this.lastPage]);
                access$400.clearAnimation();
                access$300.clearAnimation();
                Animation loadAnimation = AnimationUtils.loadAnimation(IntroActivity.this, C0338R.anim.icon_anim_fade_out);
                loadAnimation.setAnimationListener(new C15841(access$300));
                Animation loadAnimation2 = AnimationUtils.loadAnimation(IntroActivity.this, C0338R.anim.icon_anim_fade_in);
                loadAnimation2.setAnimationListener(new C15852(access$400));
                access$300.startAnimation(loadAnimation);
                access$400.startAnimation(loadAnimation2);
            }
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.IntroActivity.2 */
    class C15872 implements OnClickListener {
        C15872() {
        }

        public void onClick(View view) {
            if (!IntroActivity.this.startPressed) {
                IntroActivity.this.startPressed = true;
                Intent intent = new Intent(IntroActivity.this, LaunchActivity.class);
                intent.putExtra("fromIntro", true);
                IntroActivity.this.startActivity(intent);
                IntroActivity.this.finish();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.IntroActivity.3 */
    class C15883 implements OnLongClickListener {
        C15883() {
        }

        public boolean onLongClick(View view) {
            ConnectionsManager.getInstance().switchBackend();
            return true;
        }
    }

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public int getCount() {
            return 7;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View inflate = View.inflate(viewGroup.getContext(), C0338R.layout.intro_view_layout, null);
            TextView textView = (TextView) inflate.findViewById(C0338R.id.header_text);
            TextView textView2 = (TextView) inflate.findViewById(C0338R.id.message_text);
            viewGroup.addView(inflate, 0);
            textView.setText(IntroActivity.this.getString(IntroActivity.this.titles[i]));
            textView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.getString(IntroActivity.this.messages[i])));
            return inflate;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }

        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
            int childCount = IntroActivity.this.bottomPages.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = IntroActivity.this.bottomPages.getChildAt(i2);
                if (i2 == i) {
                    childAt.setBackgroundColor(-13851168);
                } else {
                    childAt.setBackgroundColor(-4473925);
                }
            }
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    public IntroActivity() {
        this.lastPage = 0;
        this.justCreated = false;
        this.startPressed = false;
    }

    protected void onCreate(Bundle bundle) {
        setTheme(C0338R.style.Theme_TMessages);
        super.onCreate(bundle);
        Theme.loadRecources(this);
        requestWindowFeature(1);
        if (AndroidUtilities.isTablet()) {
            setContentView(C0338R.layout.intro_layout_tablet);
            View findViewById = findViewById(C0338R.id.background_image_intro);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(C0338R.drawable.catstile);
            bitmapDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            findViewById.setBackgroundDrawable(bitmapDrawable);
        } else {
            setRequestedOrientation(1);
            setContentView(C0338R.layout.intro_layout);
        }
        if (LocaleController.isRTL) {
            this.icons = new int[]{C0338R.drawable.intro7, C0338R.drawable.intro6, C0338R.drawable.intro5, C0338R.drawable.intro4, C0338R.drawable.intro3, C0338R.drawable.intro2, C0338R.drawable.intro1};
            this.titles = new int[]{C0338R.string.Page7Title, C0338R.string.Page6Title, C0338R.string.Page5Title, C0338R.string.Page4Title, C0338R.string.Page3Title, C0338R.string.Page2Title, C0338R.string.Page1Title};
            this.messages = new int[]{C0338R.string.Page7Message, C0338R.string.Page6Message, C0338R.string.Page5Message, C0338R.string.Page4Message, C0338R.string.Page3Message, C0338R.string.Page2Message, C0338R.string.Page1Message};
        } else {
            this.icons = new int[]{C0338R.drawable.intro1, C0338R.drawable.intro2, C0338R.drawable.intro3, C0338R.drawable.intro4, C0338R.drawable.intro5, C0338R.drawable.intro6, C0338R.drawable.intro7};
            this.titles = new int[]{C0338R.string.Page1Title, C0338R.string.Page2Title, C0338R.string.Page3Title, C0338R.string.Page4Title, C0338R.string.Page5Title, C0338R.string.Page6Title, C0338R.string.Page7Title};
            this.messages = new int[]{C0338R.string.Page1Message, C0338R.string.Page2Message, C0338R.string.Page3Message, C0338R.string.Page4Message, C0338R.string.Page5Message, C0338R.string.Page6Message, C0338R.string.Page7Message};
        }
        this.viewPager = (ViewPager) findViewById(C0338R.id.intro_view_pager);
        TextView textView = (TextView) findViewById(C0338R.id.start_messaging_button);
        textView.setText(LocaleController.getString("StartMessaging", C0338R.string.StartMessaging).toUpperCase());
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(textView, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(textView, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            textView.setStateListAnimator(stateListAnimator);
        }
        this.topImage1 = (ImageView) findViewById(C0338R.id.icon_image1);
        this.topImage2 = (ImageView) findViewById(C0338R.id.icon_image2);
        this.bottomPages = (ViewGroup) findViewById(C0338R.id.bottom_pages);
        this.topImage2.setVisibility(8);
        this.viewPager.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        this.viewPager.addOnPageChangeListener(new C15861());
        textView.setOnClickListener(new C15872());
        if (BuildVars.DEBUG_VERSION) {
            textView.setOnLongClickListener(new C15883());
        }
        this.justCreated = true;
    }

    protected void onPause() {
        super.onPause();
        AndroidUtilities.unregisterUpdates();
    }

    protected void onResume() {
        super.onResume();
        if (this.justCreated) {
            if (LocaleController.isRTL) {
                this.viewPager.setCurrentItem(6);
                this.lastPage = 6;
            } else {
                this.viewPager.setCurrentItem(0);
                this.lastPage = 0;
            }
            this.justCreated = false;
        }
        AndroidUtilities.checkForCrashes(this);
        AndroidUtilities.checkForUpdates(this);
    }
}
