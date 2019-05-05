package com.hanista.mobogram.mobo.p002c;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.DialogObject;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayout;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: com.hanista.mobogram.mobo.c.a */
public class ChatBarUtil {
    public ImageView f271a;
    private ChatBarUtil f272b;
    private boolean f273c;
    private FrameLayout f274d;
    private TextView f275e;
    private ImageView f276f;
    private TextView f277g;
    private boolean f278h;
    private boolean f279i;
    private BaseFragment f280j;
    private long f281k;

    /* renamed from: com.hanista.mobogram.mobo.c.a.1 */
    class ChatBarUtil extends RecyclerListView {
        final /* synthetic */ ChatBarUtil f261a;

        ChatBarUtil(ChatBarUtil chatBarUtil, Context context) {
            this.f261a = chatBarUtil;
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!(getParent() == null || getParent().getParent() == null)) {
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a.2 */
    class ChatBarUtil extends LinearLayoutManager {
        final /* synthetic */ ChatBarUtil f262a;

        ChatBarUtil(ChatBarUtil chatBarUtil, Context context) {
            this.f262a = chatBarUtil;
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a.3 */
    class ChatBarUtil implements OnItemClickListener {
        final /* synthetic */ ChatBarUtil f263a;

        ChatBarUtil(ChatBarUtil chatBarUtil) {
            this.f263a = chatBarUtil;
        }

        public void onItemClick(View view, int i) {
            long itemId = this.f263a.f272b.getItemId(i);
            Bundle bundle = new Bundle();
            int i2 = (int) itemId;
            int i3 = (int) (itemId >> 32);
            if (i2 == 0) {
                bundle.putInt("enc_id", i3);
            } else if (i3 == 1) {
                bundle.putInt("chat_id", i2);
            } else if (i2 > 0) {
                bundle.putInt("user_id", i2);
            } else if (i2 < 0) {
                bundle.putInt("chat_id", -i2);
            }
            this.f263a.f280j.presentFragment(new ChatActivity(bundle));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a.4 */
    class ChatBarUtil implements OnClickListener {
        final /* synthetic */ ChatBarUtil f264a;

        ChatBarUtil(ChatBarUtil chatBarUtil) {
            this.f264a = chatBarUtil;
        }

        public void onClick(View view) {
            this.f264a.m366d();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a.5 */
    class ChatBarUtil implements OnClickListener {
        final /* synthetic */ ChatBarUtil f265a;

        ChatBarUtil(ChatBarUtil chatBarUtil) {
            this.f265a = chatBarUtil;
        }

        public void onClick(View view) {
            this.f265a.m366d();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a.6 */
    class ChatBarUtil extends AnimatorListenerAdapterProxy {
        final /* synthetic */ ChatBarUtil f266a;

        ChatBarUtil(ChatBarUtil chatBarUtil) {
            this.f266a = chatBarUtil;
        }

        public void onAnimationEnd(Animator animator) {
            this.f266a.f278h = false;
            this.f266a.f273c = !this.f266a.f273c;
            this.f266a.f276f.setImageResource(this.f266a.f273c ? C0338R.drawable.ic_close_bar : C0338R.drawable.ic_open_bar);
            if (this.f266a.f277g != null) {
                LayoutParams layoutParams = (LayoutParams) this.f266a.f277g.getLayoutParams();
                layoutParams.topMargin = AndroidUtilities.dp(this.f266a.f273c ? (float) (MoboConstants.aA + 2) : 2.0f);
                this.f266a.f277g.setLayoutParams(layoutParams);
            }
            if (!this.f266a.f278h && this.f266a.f279i && this.f266a.f273c) {
                this.f266a.f279i = false;
                this.f266a.m366d();
            }
            this.f266a.m371f();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a.a */
    private class ChatBarUtil extends Adapter {
        ArrayList<TL_dialog> f267a;
        final /* synthetic */ ChatBarUtil f268b;
        private Context f269c;

        public ChatBarUtil(ChatBarUtil chatBarUtil, Context context) {
            this.f268b = chatBarUtil;
            this.f267a = new ArrayList();
            this.f269c = context;
        }

        public void m359a() {
            this.f267a = this.f268b.m369e();
            if (this.f267a.size() > 0) {
                this.f268b.f275e.setVisibility(8);
            } else {
                this.f268b.f275e.setVisibility(0);
            }
        }

        public int getItemCount() {
            return this.f267a.size();
        }

        public long getItemId(int i) {
            return (i >= this.f267a.size() || i < 0) ? 0 : ((TL_dialog) this.f267a.get(i)).id;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ((RecentDialogCell) viewHolder.itemView).m396a(((TL_dialog) this.f267a.get(i)).id, false, null);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View recentDialogCell = new RecentDialogCell(this.f269c, MoboConstants.aA);
            recentDialogCell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp((float) ((int) (((double) MoboConstants.aA) * 0.8d))), AndroidUtilities.dp((float) MoboConstants.aA)));
            return new ChatBarUtil(this.f268b, recentDialogCell);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a.b */
    private class ChatBarUtil extends ViewHolder {
        final /* synthetic */ ChatBarUtil f270a;

        public ChatBarUtil(ChatBarUtil chatBarUtil, View view) {
            this.f270a = chatBarUtil;
            super(view);
        }
    }

    private void m366d() {
        float f = 0.0f;
        if (!this.f278h) {
            if (!(this.f273c || this.f272b == null)) {
                this.f272b.m359a();
                this.f272b.notifyDataSetChanged();
            }
            FrameLayout frameLayout = this.f274d;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.f273c ? 0.0f : (float) AndroidUtilities.dp((float) MoboConstants.aA);
            ObjectAnimator.ofFloat(frameLayout, str, fArr).setDuration(300).start();
            ImageView imageView = this.f276f;
            str = "translationY";
            fArr = new float[1];
            fArr[0] = this.f273c ? 0.0f : (float) AndroidUtilities.dp((float) MoboConstants.aA);
            ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300);
            duration.start();
            imageView = this.f271a;
            str = "translationY";
            fArr = new float[1];
            if (!this.f273c) {
                f = (float) AndroidUtilities.dp((float) MoboConstants.aA);
            }
            fArr[0] = f;
            ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300).start();
            this.f278h = true;
            duration.addListener(new ChatBarUtil(this));
        }
    }

    private ArrayList<TL_dialog> m369e() {
        ArrayList<TL_dialog> arrayList = new ArrayList();
        Iterator it = MessagesController.getInstance().dialogs.iterator();
        while (it.hasNext()) {
            TL_dialog tL_dialog = (TL_dialog) it.next();
            if ((!HiddenConfig.m1399b(Long.valueOf(tL_dialog.id)) || HiddenConfig.f1402e) && tL_dialog.id != this.f281k) {
                if (arrayList.size() >= MoboConstants.ay) {
                    break;
                }
                int i = 0;
                int i2 = (int) (tL_dialog.id >> 32);
                int i3 = (int) tL_dialog.id;
                if (!(i3 == 0 || i2 == 1)) {
                    if (DialogObject.isChannel(tL_dialog)) {
                        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-i3));
                        if (chat != null) {
                            i = chat.megagroup ? 4 : 8;
                        }
                    } else if (i3 < 0) {
                        i = 2;
                    }
                    if (i3 > 0) {
                        User user = MessagesController.getInstance().getUser(Integer.valueOf((int) tL_dialog.id));
                        i = (user == null || !user.bot) ? 1 : 16;
                    }
                }
                if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i2)) instanceof TL_encryptedChat) {
                    i = 1;
                }
                if (!((i & MoboConstants.ax) == 0 || (tL_dialog.unread_count == 0 && (MoboConstants.aw & 4) == 0))) {
                    if (MessagesController.getInstance().isDialogMuted(tL_dialog.id)) {
                        if ((MoboConstants.aw & 2) != 0) {
                            arrayList.add(tL_dialog);
                        }
                    } else if ((MoboConstants.aw & 1) != 0) {
                        arrayList.add(tL_dialog);
                    }
                }
            }
        }
        return arrayList;
    }

    private void m371f() {
        if (ThemeUtil.m2490b()) {
            ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.ic_bar).setColorFilter(AdvanceTheme.bg, Mode.SRC_IN);
            int i = AdvanceTheme.bi;
            Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.ic_close_bar_white);
            drawable.setColorFilter(i, Mode.SRC_IN);
            Drawable drawable2 = ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.ic_open_bar_white);
            drawable2.setColorFilter(i, Mode.SRC_IN);
            ImageView imageView = this.f276f;
            if (!this.f273c) {
                drawable = drawable2;
            }
            imageView.setImageDrawable(drawable);
        }
    }

    public void m377a() {
        if (this.f274d != null) {
            this.f274d.setVisibility(8);
        }
        if (this.f271a != null) {
            this.f271a.setVisibility(8);
        }
        if (this.f276f != null) {
            this.f276f.setVisibility(8);
        }
    }

    public void m378a(Context context, BaseFragment baseFragment, SizeNotifierFrameLayout sizeNotifierFrameLayout, TextView textView, long j) {
        if (MoboConstants.av && MoboConstants.aw != 0 && MoboConstants.ax != 0) {
            this.f277g = textView;
            this.f280j = baseFragment;
            this.f281k = j;
            this.f274d = new FrameLayout(context);
            this.f274d.setBackgroundColor(-1);
            if (ThemeUtil.m2490b()) {
                this.f274d.setBackgroundColor(AdvanceTheme.f2497h);
            }
            sizeNotifierFrameLayout.addView(this.f274d, LayoutHelper.createFrame(-1, (float) MoboConstants.aA, 48, 0.0f, (float) (-MoboConstants.aA), 0.0f, 0.0f));
            this.f275e = new TextView(context);
            this.f275e.setTypeface(FontUtil.m1176a().m1161d());
            this.f275e.setText(LocaleController.getString("NoRecentChats", C0338R.string.NoRecentChats));
            this.f275e.setTextColor(-6974059);
            this.f275e.setGravity(17);
            this.f275e.setTextSize(1, (float) (MoboConstants.aA / 5));
            this.f274d.addView(this.f275e, LayoutHelper.createLinear(-1, -1));
            View chatBarUtil = new ChatBarUtil(this, context);
            chatBarUtil.setLayoutAnimation(null);
            LayoutManager chatBarUtil2 = new ChatBarUtil(this, context);
            chatBarUtil2.setOrientation(0);
            chatBarUtil2.setReverseLayout(LocaleController.isRTL);
            chatBarUtil.setLayoutManager(chatBarUtil2);
            Adapter chatBarUtil3 = new ChatBarUtil(this, context);
            this.f272b = chatBarUtil3;
            chatBarUtil.setAdapter(chatBarUtil3);
            chatBarUtil.setOnItemClickListener(new ChatBarUtil(this));
            this.f274d.addView(chatBarUtil, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.f271a = new ImageView(context);
            if (ThemeUtil.m2490b()) {
                Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.ic_bar);
                drawable.setColorFilter(AdvanceTheme.bg, Mode.SRC_IN);
                this.f271a.setImageDrawable(drawable);
            } else {
                this.f271a.setImageResource(C0338R.drawable.ic_bar);
            }
            sizeNotifierFrameLayout.addView(this.f271a, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE, 0.0f));
            this.f271a.setOnClickListener(new ChatBarUtil(this));
            this.f276f = new ImageView(context);
            this.f276f.setImageResource(C0338R.drawable.ic_open_bar);
            sizeNotifierFrameLayout.addView(this.f276f, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE, 0.0f));
            this.f276f.setOnClickListener(new ChatBarUtil(this));
            if (MoboConstants.az) {
                this.f272b.m359a();
                if (this.f272b.getItemCount() > 0) {
                    m366d();
                }
            }
            m371f();
        }
    }

    public void m379b() {
        if (this.f274d != null) {
            if (this.f273c && !this.f278h) {
                m366d();
            } else if (!this.f273c && this.f278h) {
                this.f279i = true;
            }
        }
    }

    public void m380c() {
        if (this.f272b != null && this.f273c) {
            this.f272b.m359a();
            this.f272b.notifyDataSetChanged();
        }
    }
}
