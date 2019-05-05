package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.FrameLayout;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.HapticFeedbackController;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.Calendar;

public class RadialPickerLayout extends FrameLayout implements OnTouchListener {
    private AccessibilityManager f2255A;
    private Handler f2256B;
    private final int f2257a;
    private final int f2258b;
    private int f2259c;
    private HapticFeedbackController f2260d;
    private C0925a f2261e;
    private boolean f2262f;
    private int f2263g;
    private int f2264h;
    private boolean f2265i;
    private boolean f2266j;
    private int f2267k;
    private CircleView f2268l;
    private AmPmCirclesView f2269m;
    private RadialTextsView f2270n;
    private RadialTextsView f2271o;
    private RadialSelectorView f2272p;
    private RadialSelectorView f2273q;
    private View f2274r;
    private int[] f2275s;
    private boolean f2276t;
    private int f2277u;
    private boolean f2278v;
    private boolean f2279w;
    private int f2280x;
    private float f2281y;
    private float f2282z;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.RadialPickerLayout.1 */
    class C09231 implements Runnable {
        final /* synthetic */ RadialPickerLayout f2252a;

        C09231(RadialPickerLayout radialPickerLayout) {
            this.f2252a = radialPickerLayout;
        }

        public void run() {
            this.f2252a.f2269m.setAmOrPmPressed(this.f2252a.f2277u);
            this.f2252a.f2269m.invalidate();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.RadialPickerLayout.2 */
    class C09242 implements Runnable {
        final /* synthetic */ Boolean[] f2253a;
        final /* synthetic */ RadialPickerLayout f2254b;

        C09242(RadialPickerLayout radialPickerLayout, Boolean[] boolArr) {
            this.f2254b = radialPickerLayout;
            this.f2253a = boolArr;
        }

        public void run() {
            this.f2254b.f2278v = true;
            int a = this.f2254b.m2169a(this.f2254b.f2280x, this.f2253a[0].booleanValue(), false, true);
            this.f2254b.f2259c = a;
            this.f2254b.f2261e.m2167a(this.f2254b.getCurrentItemShowing(), a, false);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.RadialPickerLayout.a */
    public interface C0925a {
        void m2167a(int i, int i2, boolean z);
    }

    public RadialPickerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f2277u = -1;
        this.f2256B = new Handler();
        setOnTouchListener(this);
        this.f2257a = ViewConfiguration.get(context).getScaledTouchSlop();
        this.f2258b = ViewConfiguration.getTapTimeout();
        this.f2278v = false;
        this.f2268l = new CircleView(context);
        addView(this.f2268l);
        this.f2269m = new AmPmCirclesView(context);
        addView(this.f2269m);
        this.f2272p = new RadialSelectorView(context);
        addView(this.f2272p);
        this.f2273q = new RadialSelectorView(context);
        addView(this.f2273q);
        this.f2270n = new RadialTextsView(context);
        addView(this.f2270n);
        this.f2271o = new RadialTextsView(context);
        addView(this.f2271o);
        m2173a();
        this.f2259c = -1;
        this.f2276t = true;
        this.f2274r = new View(context);
        this.f2274r.setLayoutParams(new LayoutParams(-1, -1));
        this.f2274r.setBackgroundColor(getResources().getColor(C0338R.color.mdtp_transparent_black));
        this.f2274r.setVisibility(4);
        addView(this.f2274r);
        this.f2255A = (AccessibilityManager) context.getSystemService("accessibility");
        this.f2262f = false;
    }

    private int m2168a(float f, float f2, boolean z, Boolean[] boolArr) {
        int currentItemShowing = getCurrentItemShowing();
        return currentItemShowing == 0 ? this.f2272p.m2184a(f, f2, z, boolArr) : currentItemShowing == 1 ? this.f2273q.m2184a(f, f2, z, boolArr) : -1;
    }

    private int m2169a(int i, boolean z, boolean z2, boolean z3) {
        int i2 = -1;
        if (i != -1) {
            RadialSelectorView radialSelectorView;
            int i3;
            int currentItemShowing = getCurrentItemShowing();
            i2 = (z2 || currentItemShowing != 1) ? 0 : 1;
            int b = i2 != 0 ? m2177b(i) : m2180c(i, 0);
            if (currentItemShowing == 0) {
                radialSelectorView = this.f2272p;
                i3 = 30;
            } else {
                radialSelectorView = this.f2273q;
                i3 = 6;
            }
            radialSelectorView.m2185a(b, z, z3);
            radialSelectorView.invalidate();
            if (currentItemShowing != 0) {
                if (b == 360 && currentItemShowing == 1) {
                    i2 = 0;
                }
                i2 = b;
            } else if (!this.f2265i) {
                if (b == 0) {
                    i2 = 360;
                }
                i2 = b;
            } else if (b == 0 && z) {
                i2 = 360;
            } else {
                if (b == 360 && !z) {
                    i2 = 0;
                }
                i2 = b;
            }
            i3 = i2 / i3;
            i2 = (currentItemShowing != 0 || !this.f2265i || z || i2 == 0) ? i3 : i3 + 12;
            if (getCurrentItemShowing() == 0) {
                this.f2270n.setSelection(i2);
                this.f2270n.invalidate();
            } else if (getCurrentItemShowing() == 1) {
                this.f2271o.setSelection(i2);
                this.f2271o.invalidate();
            }
        }
        return i2;
    }

    private void m2173a() {
        this.f2275s = new int[361];
        int i = 0;
        int i2 = 8;
        int i3 = 1;
        for (int i4 = 0; i4 < 361; i4++) {
            this.f2275s[i4] = i;
            if (i3 == i2) {
                i3 = i + 6;
                i2 = i3 == 360 ? 7 : i3 % 30 == 0 ? 14 : 4;
                i = i3;
                i3 = 1;
            } else {
                i3++;
            }
        }
    }

    private void m2174a(int i, int i2) {
        if (i == 0) {
            m2179b(0, i2);
            this.f2272p.m2185a((i2 % 12) * 30, m2175a(i2), false);
            this.f2272p.invalidate();
            this.f2270n.setSelection(i2);
            this.f2270n.invalidate();
        } else if (i == 1) {
            m2179b(1, i2);
            this.f2273q.m2185a(i2 * 6, false, false);
            this.f2273q.invalidate();
            this.f2271o.setSelection(i2);
            this.f2270n.invalidate();
        }
    }

    private boolean m2175a(int i) {
        return this.f2265i && i <= 12 && i != 0;
    }

    private int m2177b(int i) {
        return this.f2275s == null ? -1 : this.f2275s[i];
    }

    private void m2179b(int i, int i2) {
        if (i == 0) {
            this.f2263g = i2;
        } else if (i == 1) {
            this.f2264h = i2;
        } else if (i != 2) {
        } else {
            if (i2 == 0) {
                this.f2263g %= 12;
            } else if (i2 == 1) {
                this.f2263g = (this.f2263g % 12) + 12;
            }
        }
    }

    private static int m2180c(int i, int i2) {
        int i3 = (i / 30) * 30;
        int i4 = i3 + 30;
        return i2 == 1 ? i4 : i2 == -1 ? i == i3 ? i3 - 30 : i3 : i - i3 >= i4 - i ? i4 : i3;
    }

    private int getCurrentlyShowingValue() {
        int currentItemShowing = getCurrentItemShowing();
        return currentItemShowing == 0 ? this.f2263g : currentItemShowing == 1 ? this.f2264h : -1;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() != 32) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        accessibilityEvent.getText().clear();
        Calendar instance = Calendar.getInstance();
        instance.set(10, getHours());
        instance.set(12, getMinutes());
        accessibilityEvent.getText().add(LanguageUtils.m2043a(DateUtils.formatDateTime(getContext(), instance.getTimeInMillis(), this.f2265i ? 129 : 1)));
        return true;
    }

    public int getCurrentItemShowing() {
        if (this.f2267k == 0 || this.f2267k == 1) {
            return this.f2267k;
        }
        Log.e("RadialPickerLayout", "Current item showing was unfortunately set to " + this.f2267k);
        return -1;
    }

    public int getHours() {
        return this.f2263g;
    }

    public int getIsCurrentlyAmOrPm() {
        return this.f2263g < 12 ? 0 : this.f2263g < 24 ? 1 : -1;
    }

    public int getMinutes() {
        return this.f2264h;
    }

    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_SCROLL_BACKWARD);
            accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_SCROLL_FORWARD);
            return;
        }
        accessibilityNodeInfo.addAction(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
        accessibilityNodeInfo.addAction(MessagesController.UPDATE_MASK_CHANNEL);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Boolean[] boolArr = new Boolean[]{Boolean.valueOf(false)};
        int a;
        switch (motionEvent.getAction()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                if (!this.f2276t) {
                    return true;
                }
                this.f2281y = x;
                this.f2282z = y;
                this.f2259c = -1;
                this.f2278v = false;
                this.f2279w = true;
                if (this.f2266j) {
                    this.f2277u = -1;
                } else {
                    this.f2277u = this.f2269m.m2183a(x, y);
                }
                if (this.f2277u == 0 || this.f2277u == 1) {
                    this.f2260d.m2068c();
                    this.f2280x = -1;
                    this.f2256B.postDelayed(new C09231(this), (long) this.f2258b);
                    return true;
                }
                this.f2280x = m2168a(x, y, this.f2255A.isTouchExplorationEnabled(), boolArr);
                if (this.f2280x == -1) {
                    return true;
                }
                this.f2260d.m2068c();
                this.f2256B.postDelayed(new C09242(this, boolArr), (long) this.f2258b);
                return true;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                if (this.f2276t) {
                    this.f2256B.removeCallbacksAndMessages(null);
                    this.f2279w = false;
                    if (this.f2277u == 0 || this.f2277u == 1) {
                        a = this.f2269m.m2183a(x, y);
                        this.f2269m.setAmOrPmPressed(-1);
                        this.f2269m.invalidate();
                        if (a == this.f2277u) {
                            this.f2269m.setAmOrPm(a);
                            if (getIsCurrentlyAmOrPm() != a) {
                                this.f2261e.m2167a(2, this.f2277u, false);
                                m2179b(2, a);
                            }
                        }
                        this.f2277u = -1;
                        break;
                    }
                    if (this.f2280x != -1) {
                        int a2 = m2168a(x, y, this.f2278v, boolArr);
                        if (a2 != -1) {
                            a = m2169a(a2, boolArr[0].booleanValue(), !this.f2278v, false);
                            if (getCurrentItemShowing() == 0 && !this.f2265i) {
                                a2 = getIsCurrentlyAmOrPm();
                                if (a2 == 0 && a == 12) {
                                    a = 0;
                                } else if (a2 == 1 && a != 12) {
                                    a += 12;
                                }
                            }
                            m2179b(getCurrentItemShowing(), a);
                            this.f2261e.m2167a(getCurrentItemShowing(), a, true);
                        }
                    }
                    this.f2278v = false;
                    return true;
                }
                Log.d("RadialPickerLayout", "Input was disabled, but received ACTION_UP.");
                this.f2261e.m2167a(3, 1, false);
                return true;
            case VideoPlayer.STATE_PREPARING /*2*/:
                if (this.f2276t) {
                    float abs = Math.abs(y - this.f2282z);
                    float abs2 = Math.abs(x - this.f2281y);
                    if (this.f2278v || abs2 > ((float) this.f2257a) || abs > ((float) this.f2257a)) {
                        if (this.f2277u == 0 || this.f2277u == 1) {
                            this.f2256B.removeCallbacksAndMessages(null);
                            if (this.f2269m.m2183a(x, y) != this.f2277u) {
                                this.f2269m.setAmOrPmPressed(-1);
                                this.f2269m.invalidate();
                                this.f2277u = -1;
                                break;
                            }
                        } else if (this.f2280x != -1) {
                            this.f2278v = true;
                            this.f2256B.removeCallbacksAndMessages(null);
                            a = m2168a(x, y, true, boolArr);
                            if (a == -1) {
                                return true;
                            }
                            a = m2169a(a, boolArr[0].booleanValue(), false, true);
                            if (a == this.f2259c) {
                                return true;
                            }
                            this.f2260d.m2068c();
                            this.f2259c = a;
                            this.f2261e.m2167a(getCurrentItemShowing(), a, false);
                            return true;
                        }
                    }
                }
                Log.e("RadialPickerLayout", "Input was disabled, but received ACTION_MOVE.");
                return true;
                break;
        }
        return false;
    }

    @SuppressLint({"NewApi"})
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (super.performAccessibilityAction(i, bundle)) {
            return true;
        }
        int i2 = i == ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT ? 1 : i == MessagesController.UPDATE_MASK_CHANNEL ? -1 : 0;
        if (i2 == 0) {
            return false;
        }
        int i3;
        int currentlyShowingValue = getCurrentlyShowingValue();
        int currentItemShowing = getCurrentItemShowing();
        if (currentItemShowing == 0) {
            i3 = 30;
            currentlyShowingValue %= 12;
        } else {
            i3 = currentItemShowing == 1 ? 6 : 0;
        }
        i2 = m2180c(currentlyShowingValue * i3, i2) / i3;
        if (currentItemShowing != 0) {
            currentlyShowingValue = 55;
            i3 = 0;
        } else if (this.f2265i) {
            currentlyShowingValue = 23;
            i3 = 0;
        } else {
            currentlyShowingValue = 12;
            i3 = 1;
        }
        if (i2 <= currentlyShowingValue) {
            i3 = i2 < i3 ? currentlyShowingValue : i2;
        }
        m2174a(currentItemShowing, i3);
        this.f2261e.m2167a(currentItemShowing, i3, false);
        return true;
    }

    public void setAmOrPm(int i) {
        this.f2269m.setAmOrPm(i);
        this.f2269m.invalidate();
        m2179b(2, i);
    }

    public void setOnValueSelectedListener(C0925a c0925a) {
        this.f2261e = c0925a;
    }
}
