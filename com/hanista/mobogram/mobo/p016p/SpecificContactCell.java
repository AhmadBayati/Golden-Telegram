package com.hanista.mobogram.mobo.p016p;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.CheckBoxSquare;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import com.hanista.mobogram.ui.ProfileActivity;

/* renamed from: com.hanista.mobogram.mobo.p.e */
public class SpecificContactCell extends FrameLayout implements PhotoViewerProvider {
    private int f2060A;
    private BackupImageView f2061a;
    private SimpleTextView f2062b;
    private SimpleTextView f2063c;
    private ImageView f2064d;
    private CheckBox f2065e;
    private CheckBoxSquare f2066f;
    private ImageView f2067g;
    private AvatarDrawable f2068h;
    private TLObject f2069i;
    private SpecificContact f2070j;
    private CharSequence f2071k;
    private CharSequence f2072l;
    private int f2073m;
    private String f2074n;
    private int f2075o;
    private FileLocation f2076p;
    private int f2077q;
    private int f2078r;
    private ImageView f2079s;
    private ImageView f2080t;
    private ImageView f2081u;
    private ImageView f2082v;
    private ImageView f2083w;
    private ImageView f2084x;
    private Drawable f2085y;
    private int f2086z;

    /* renamed from: com.hanista.mobogram.mobo.p.e.1 */
    class SpecificContactCell implements OnClickListener {
        final /* synthetic */ SpecificContactCell f2059a;

        SpecificContactCell(SpecificContactCell specificContactCell) {
            this.f2059a = specificContactCell;
        }

        public void onClick(View view) {
            if (this.f2059a.f2069i instanceof User) {
                User user = (User) this.f2059a.f2069i;
                if (MoboConstants.aB == 2) {
                    if (user.photo != null && user.photo.photo_big != null) {
                        PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.f2059a);
                    }
                } else if (MoboConstants.aB == 1 && PhotoViewer.getInstance().getParentActivity() != null && (PhotoViewer.getInstance().getParentActivity() instanceof LaunchActivity)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", user.id);
                    ((LaunchActivity) PhotoViewer.getInstance().getParentActivity()).presentFragment(new ProfileActivity(bundle));
                }
            }
        }
    }

    public SpecificContactCell(Context context, int i, int i2, boolean z) {
        float f;
        float f2;
        super(context);
        this.f2069i = null;
        this.f2070j = null;
        this.f2074n = null;
        this.f2075o = 0;
        this.f2076p = null;
        this.f2077q = -5723992;
        this.f2078r = -12876608;
        this.f2085y = null;
        this.f2086z = Theme.MSG_TEXT_COLOR;
        this.f2060A = 32;
        this.f2068h = new AvatarDrawable();
        this.f2061a = new BackupImageView(context);
        this.f2061a.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.f2061a, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 8.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        this.f2062b = new SimpleTextView(context);
        this.f2062b.setTypeface(FontUtil.m1176a().m1161d());
        this.f2062b.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f2062b.setTextSize(17);
        this.f2062b.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = this.f2062b;
        int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = (float) ((i2 == 2 ? 18 : 0) + 28);
        } else {
            f = (float) (i + 68);
        }
        if (LocaleController.isRTL) {
            f2 = (float) (i + 68);
        } else {
            f2 = (float) ((i2 == 2 ? 18 : 0) + 28);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i3, f, 11.5f, f2, 0.0f));
        this.f2079s = new ImageView(context);
        this.f2079s.setScaleType(ScaleType.CENTER);
        this.f2079s.setVisibility(8);
        this.f2079s.setImageResource(ThemeUtil.m2485a().m2299m());
        addView(this.f2079s, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 7.0f : 0.0f, 8.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));
        this.f2063c = new SimpleTextView(context);
        this.f2063c.setTypeface(FontUtil.m1176a().m1161d());
        this.f2063c.setTextSize(14);
        this.f2063c.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.f2063c, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (i + 68), 34.5f, LocaleController.isRTL ? (float) (i + 68) : 28.0f, 0.0f));
        this.f2064d = new ImageView(context);
        this.f2064d.setScaleType(ScaleType.CENTER);
        this.f2064d.setVisibility(8);
        addView(this.f2064d, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i2 == 2) {
            this.f2066f = new CheckBoxSquare(context);
            addView(this.f2066f, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i2 == 1) {
            this.f2065e = new CheckBox(context, C0338R.drawable.round_check2);
            this.f2065e.setVisibility(4);
            addView(this.f2065e, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 37), 38.0f, LocaleController.isRTL ? (float) (i + 37) : 0.0f, 0.0f));
        }
        setBackgroundResource(ThemeUtil.m2485a().m2293g());
        if (z) {
            this.f2067g = new ImageView(context);
            this.f2067g.setImageResource(C0338R.drawable.admin_star);
            addView(this.f2067g, LayoutHelper.createFrame(16, 16.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 24.0f : 0.0f, 13.5f, LocaleController.isRTL ? 0.0f : 24.0f, 0.0f));
        }
        this.f2080t = new ImageView(context);
        this.f2080t.setScaleType(ScaleType.CENTER);
        this.f2080t.setVisibility(8);
        this.f2080t.setImageResource(C0338R.drawable.change_contact_type_online);
        addView(this.f2080t, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 7.0f : 0.0f, 55.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));
        this.f2081u = new ImageView(context);
        this.f2081u.setScaleType(ScaleType.CENTER);
        this.f2081u.setVisibility(8);
        this.f2081u.setImageResource(C0338R.drawable.change_contact_type_offline);
        addView(this.f2081u, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 33.0f : 0.0f, 55.0f, LocaleController.isRTL ? 0.0f : 33.0f, 0.0f));
        this.f2082v = new ImageView(context);
        this.f2082v.setScaleType(ScaleType.CENTER);
        this.f2082v.setVisibility(8);
        this.f2082v.setImageResource(C0338R.drawable.change_contact_type_photo);
        addView(this.f2082v, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 59.0f : 0.0f, 55.0f, LocaleController.isRTL ? 0.0f : 59.0f, 0.0f));
        this.f2083w = new ImageView(context);
        this.f2083w.setScaleType(ScaleType.CENTER);
        this.f2083w.setVisibility(8);
        this.f2083w.setImageResource(C0338R.drawable.change_contact_type_name);
        addView(this.f2083w, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 85.0f : 0.0f, 55.0f, LocaleController.isRTL ? 0.0f : 85.0f, 0.0f));
        this.f2084x = new ImageView(context);
        this.f2084x.setScaleType(ScaleType.CENTER);
        this.f2084x.setVisibility(8);
        this.f2084x.setImageResource(C0338R.drawable.change_contact_type_phone);
        addView(this.f2084x, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 111.0f : 0.0f, 55.0f, LocaleController.isRTL ? 0.0f : 111.0f, 0.0f));
        m2019a();
    }

    private void m2019a() {
        if (MoboConstants.aB != 0) {
            this.f2061a.setOnClickListener(new SpecificContactCell(this));
        }
    }

    private void m2020b() {
        if (ThemeUtil.m2490b()) {
            m2022a(AdvanceTheme.aP, AdvanceTheme.aQ);
            this.f2086z = AdvanceTheme.aR;
            this.f2062b.setTextColor(this.f2086z);
            this.f2062b.setTextSize(AdvanceTheme.aX);
            setStatusSize(AdvanceTheme.aZ);
            setAvatarRadius(AdvanceTheme.aY);
        }
    }

    public void m2021a(int i) {
        int i2 = 1;
        if (this.f2069i != null && this.f2070j != null) {
            TLObject tLObject;
            User user;
            Chat chat;
            String str;
            if (this.f2069i instanceof User) {
                User user2 = (User) this.f2069i;
                if (user2.photo != null) {
                    tLObject = user2.photo.photo_small;
                    user = user2;
                    chat = null;
                } else {
                    user = user2;
                    tLObject = null;
                    chat = null;
                }
            } else {
                chat = (Chat) this.f2069i;
                if (chat.photo != null) {
                    tLObject = chat.photo.photo_small;
                    user = null;
                } else {
                    user = null;
                    tLObject = null;
                }
            }
            m2020b();
            if (i != 0) {
                int i3 = ((i & 2) == 0 || ((this.f2076p == null || tLObject != null) && (this.f2076p != null || tLObject == null || this.f2076p == null || tLObject == null || (this.f2076p.volume_id == tLObject.volume_id && this.f2076p.local_id == tLObject.local_id)))) ? 0 : 1;
                if (!(user == null || i3 != 0 || (i & 4) == 0)) {
                    if ((user.status != null ? user.status.expires : 0) != this.f2075o) {
                        i3 = 1;
                    }
                }
                if (i3 != 0 || this.f2071k != null || this.f2074n == null || (i & 1) == 0) {
                    i2 = i3;
                    str = null;
                } else {
                    str = user != null ? UserObject.getUserName(user) : chat.title;
                    if (str.equals(this.f2074n)) {
                        i2 = i3;
                    }
                }
                if (i2 == 0) {
                    return;
                }
            }
            str = null;
            if (user != null) {
                this.f2068h.setInfo(user);
                if (user.status != null) {
                    this.f2075o = user.status.expires;
                } else {
                    this.f2075o = 0;
                }
            } else {
                this.f2068h.setInfo(chat);
            }
            if (this.f2071k != null) {
                this.f2074n = null;
                this.f2062b.setText(this.f2071k);
            } else {
                if (user != null) {
                    this.f2074n = str == null ? UserObject.getUserName(user) : str;
                } else {
                    if (str == null) {
                        str = chat.title;
                    }
                    this.f2074n = str;
                }
                this.f2062b.setText(this.f2074n);
            }
            if (this.f2072l != null) {
                this.f2063c.setTextColor(this.f2077q);
                this.f2063c.setText(this.f2072l);
            } else if (user != null) {
                if (user.bot) {
                    this.f2063c.setTextColor(this.f2077q);
                    if (user.bot_chat_history) {
                        this.f2063c.setText(LocaleController.getString("BotStatusRead", C0338R.string.BotStatusRead));
                    } else {
                        this.f2063c.setText(LocaleController.getString("BotStatusCantRead", C0338R.string.BotStatusCantRead));
                    }
                } else if (user.id == UserConfig.getClientUserId() || ((user.status != null && user.status.expires > ConnectionsManager.getInstance().getCurrentTime()) || MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(user.id)))) {
                    this.f2063c.setTextColor(this.f2078r);
                    this.f2063c.setText(LocaleController.getString("Online", C0338R.string.Online));
                } else {
                    this.f2063c.setTextColor(this.f2077q);
                    this.f2063c.setText(LocaleController.formatUserStatus(user));
                }
            }
            if ((this.f2064d.getVisibility() == 0 && this.f2073m == 0) || (this.f2064d.getVisibility() == 8 && this.f2073m != 0)) {
                this.f2064d.setVisibility(this.f2073m == 0 ? 8 : 0);
                this.f2064d.setImageResource(this.f2073m);
            }
            if (ThemeUtil.m2490b()) {
                if (this.f2085y != null) {
                    this.f2064d.setImageDrawable(this.f2085y);
                }
                this.f2061a.getImageReceiver().setRoundRadius(AndroidUtilities.dp((float) this.f2060A));
                this.f2068h.setRadius(AndroidUtilities.dp((float) this.f2060A));
            }
            this.f2061a.setImage(tLObject, "50_50", this.f2068h);
            if (user != null && user.mutual_contact && MoboConstants.f1336c) {
                this.f2079s.setVisibility(8);
            } else {
                this.f2079s.setVisibility(8);
            }
            if ((this.f2070j.m2001c() & 1) != 0) {
                this.f2080t.setVisibility(0);
            } else {
                this.f2080t.setVisibility(8);
            }
            if ((this.f2070j.m2001c() & 2) != 0) {
                this.f2081u.setVisibility(0);
            } else {
                this.f2081u.setVisibility(8);
            }
            if ((this.f2070j.m2001c() & 4) != 0) {
                this.f2082v.setVisibility(0);
            } else {
                this.f2082v.setVisibility(8);
            }
            if ((this.f2070j.m2001c() & 8) != 0) {
                this.f2083w.setVisibility(0);
            } else {
                this.f2083w.setVisibility(8);
            }
            if ((this.f2070j.m2001c() & 16) != 0) {
                this.f2084x.setVisibility(0);
            } else {
                this.f2084x.setVisibility(8);
            }
            if (user != null && user.id == UserConfig.getClientUserId() && MoboConstants.f1338e) {
                this.f2063c.setTextColor(this.f2077q);
                this.f2063c.setText(LocaleController.getString("Hidden", C0338R.string.Hidden));
            }
        }
    }

    public void m2022a(int i, int i2) {
        this.f2077q = i;
        this.f2078r = i2;
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (fileLocation == null || this.f2069i == null) {
            return null;
        }
        FileLocation fileLocation2;
        int i2;
        if (this.f2069i instanceof User) {
            User user = (User) this.f2069i;
            int i3 = user.id;
            FileLocation fileLocation3 = (user == null || user.photo == null || user.photo.photo_big == null) ? null : user.photo.photo_big;
            int i4 = i3;
            fileLocation2 = fileLocation3;
            i2 = i4;
        } else {
            Chat chat = (Chat) this.f2069i;
            if (chat == null || chat.photo == null || chat.photo.photo_big == null) {
                i2 = 0;
                fileLocation2 = null;
            } else {
                fileLocation2 = chat.photo.photo_big;
                i2 = 0;
            }
        }
        if (fileLocation2 == null || fileLocation2.local_id != fileLocation.local_id || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.dc_id != fileLocation.dc_id) {
            return null;
        }
        int[] iArr = new int[2];
        this.f2061a.getLocationInWindow(iArr);
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        placeProviderObject.viewX = iArr[0];
        placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
        placeProviderObject.parentView = this.f2061a;
        placeProviderObject.imageReceiver = this.f2061a.getImageReceiver();
        placeProviderObject.dialogId = i2;
        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
        placeProviderObject.size = -1;
        placeProviderObject.radius = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        return placeProviderObject;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(104.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setAvatarRadius(int i) {
        this.f2060A = i;
    }

    public void setCheckDisabled(boolean z) {
        if (this.f2066f != null) {
            this.f2066f.setDisabled(z);
        }
    }

    public void setData(SpecificContact specificContact) {
        this.f2069i = MessagesController.getInstance().getUser(Integer.valueOf(specificContact.m1999b()));
        this.f2070j = specificContact;
        m2021a(0);
    }

    public void setImageDrawable(Drawable drawable) {
        this.f2085y = drawable;
    }

    public void setPhotoChecked(int i) {
    }

    public void setStatusSize(int i) {
        this.f2063c.setTextSize(i);
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
