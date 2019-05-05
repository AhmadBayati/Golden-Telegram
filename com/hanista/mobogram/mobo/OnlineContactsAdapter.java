package com.hanista.mobogram.mobo;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.ContactsController.Contact;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_contact;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseSectionsAdapter;
import com.hanista.mobogram.ui.Cells.DividerCell;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.LetterSectionCell;
import com.hanista.mobogram.ui.Cells.TextCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.HashMap;

/* renamed from: com.hanista.mobogram.mobo.n */
public class OnlineContactsAdapter extends BaseSectionsAdapter {
    private Context f1955a;
    private boolean f1956b;
    private boolean f1957c;
    private HashMap<Integer, User> f1958d;
    private HashMap<Integer, ?> f1959e;
    private boolean f1960f;
    private boolean f1961g;

    public OnlineContactsAdapter(Context context, boolean z, boolean z2, HashMap<Integer, User> hashMap, boolean z3) {
        this.f1955a = context;
        this.f1956b = z;
        this.f1957c = z2;
        this.f1958d = hashMap;
        this.f1961g = z3;
    }

    private void m1944a(View view) {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.aU;
            i = AdvanceTheme.aV;
            if (i > 0) {
                view.setBackgroundColor(0);
            } else {
                view.setBackgroundColor(0);
            }
            if (i > 0) {
                view.setTag("Contacts00");
            }
        }
    }

    private void m1945a(ViewGroup viewGroup) {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.aU;
            int i2 = AdvanceTheme.aV;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.aW;
                viewGroup.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i3}));
                return;
            }
            viewGroup.setBackgroundColor(i);
        }
    }

    public int getCountForSection(int i) {
        int size;
        if (!this.f1956b || this.f1961g) {
            if (i == 0) {
                return (this.f1957c || this.f1961g) ? 2 : 4;
            } else {
                if (i - 1 < ContactsController.getInstance().onlineSortedUsersSectionsArray.size()) {
                    size = ((ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i - 1))).size();
                    return (i + -1 != ContactsController.getInstance().onlineSortedUsersSectionsArray.size() + -1 || this.f1957c) ? size + 1 : size;
                }
            }
        } else if (i < ContactsController.getInstance().onlineSortedUsersSectionsArray.size()) {
            size = ((ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i))).size();
            return (i != ContactsController.getInstance().onlineSortedUsersSectionsArray.size() + -1 || this.f1957c) ? size + 1 : size;
        }
        return this.f1957c ? ContactsController.getInstance().phoneBookContacts.size() : 0;
    }

    public Object getItem(int i, int i2) {
        ArrayList arrayList;
        if (this.f1956b && !this.f1961g) {
            if (i < ContactsController.getInstance().onlineSortedUsersSectionsArray.size()) {
                arrayList = (ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i));
                if (i2 < arrayList.size()) {
                    return MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) arrayList.get(i2)).user_id));
                }
            }
            return null;
        } else if (i == 0) {
            return null;
        } else {
            if (i - 1 >= ContactsController.getInstance().onlineSortedUsersSectionsArray.size()) {
                return this.f1957c ? ContactsController.getInstance().phoneBookContacts.get(i2) : null;
            } else {
                arrayList = (ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i - 1));
                return i2 < arrayList.size() ? MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) arrayList.get(i2)).user_id)) : null;
            }
        }
    }

    public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
        View view2;
        float f = 72.0f;
        boolean z = true;
        int itemViewType = getItemViewType(i, i2);
        int c = AdvanceTheme.m2286c(AdvanceTheme.aS, Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        int c2 = AdvanceTheme.m2286c(AdvanceTheme.aS, Theme.MSG_TEXT_COLOR);
        int i3 = AdvanceTheme.aT;
        if (itemViewType == 4) {
            if (view == null) {
                view = new DividerCell(this.f1955a);
                itemViewType = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
                if (!LocaleController.isRTL) {
                    f = 28.0f;
                }
                view.setPadding(itemViewType, 0, AndroidUtilities.dp(f), 0);
                view.setTag("contacts_row_color");
            }
            m1944a(view);
            view2 = view;
        } else if (itemViewType == 3) {
            if (view == null) {
                view2 = new GreySectionCell(this.f1955a);
                ((GreySectionCell) view2).setText(LocaleController.getString("Contacts", C0338R.string.Contacts).toUpperCase());
                if (ThemeUtil.m2490b()) {
                    ((GreySectionCell) view2).setBackgroundColor(AdvanceTheme.aU);
                    ((GreySectionCell) view2).setTextColor(c);
                }
            } else {
                view2 = view;
            }
            m1944a(view2);
        } else if (itemViewType == 2) {
            view2 = view == null ? new TextCell(this.f1955a) : view;
            m1944a(view2);
            TextCell textCell = (TextCell) view2;
            if (ThemeUtil.m2490b()) {
                textCell.setTextColor(c2);
            }
            if (this.f1957c) {
                textCell.setTextAndIcon(LocaleController.getString("InviteFriends", C0338R.string.InviteFriends), C0338R.drawable.menu_invite_telegram);
            } else if (this.f1961g) {
                textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", C0338R.string.InviteToGroupByLink), C0338R.drawable.menu_invite_telegram);
            } else if (i2 == 0) {
                textCell.setTextAndIcon(LocaleController.getString("NewGroup", C0338R.string.NewGroup), C0338R.drawable.menu_newgroup_telegram);
            } else if (i2 == 1) {
                textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", C0338R.string.NewSecretChat), C0338R.drawable.menu_secret_telegram);
            } else if (i2 == 2) {
                textCell.setTextAndIcon(LocaleController.getString("NewChannel", C0338R.string.NewChannel), C0338R.drawable.menu_broadcast_telegram);
            }
            if (ThemeUtil.m2490b()) {
                textCell.setIconColor(i3);
            }
        } else if (itemViewType == 1) {
            View view3;
            if (view == null) {
                view2 = new TextCell(this.f1955a);
                if (ThemeUtil.m2490b()) {
                    ((TextCell) view2).setTextColor(c2);
                    ((TextCell) view2).setTextSize(AdvanceTheme.aX);
                }
                view3 = view2;
            } else {
                view3 = view;
            }
            m1944a(view3);
            Contact contact = (Contact) ContactsController.getInstance().phoneBookContacts.get(i2);
            if (contact.first_name != null && contact.last_name != null) {
                ((TextCell) view3).setText(contact.first_name + " " + contact.last_name);
            } else if (contact.first_name == null || contact.last_name != null) {
                ((TextCell) view3).setText(contact.last_name);
            } else {
                ((TextCell) view3).setText(contact.first_name);
            }
            view2 = view3;
        } else if (itemViewType == 0) {
            if (view == null) {
                view2 = new UserCell(this.f1955a, 58, 1, false);
                ((UserCell) view2).setStatusColors(-5723992, -12876608);
                view2.setTag("Contacts");
            } else {
                view2 = view;
            }
            m1944a(view2);
            HashMap hashMap = ContactsController.getInstance().onlineUsersSectionsDict;
            ArrayList arrayList = ContactsController.getInstance().onlineSortedUsersSectionsArray;
            itemViewType = (!this.f1956b || this.f1961g) ? 1 : 0;
            TLObject user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) ((ArrayList) hashMap.get(arrayList.get(i - itemViewType))).get(i2)).user_id));
            ((UserCell) view2).setData(user, null, null, 0);
            if (this.f1959e != null) {
                UserCell userCell = (UserCell) view2;
                boolean containsKey = this.f1959e.containsKey(Integer.valueOf(user.id));
                if (this.f1960f || VERSION.SDK_INT <= 10) {
                    z = false;
                }
                userCell.setChecked(containsKey, z);
            }
            if (this.f1958d != null) {
                if (this.f1958d.containsKey(Integer.valueOf(user.id))) {
                    view2.setAlpha(0.5f);
                } else {
                    view2.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                }
            }
        } else {
            view2 = view;
        }
        m1945a(viewGroup);
        return view2;
    }

    public int getItemViewType(int i, int i2) {
        int i3 = 0;
        if (this.f1956b && !this.f1961g) {
            return i2 < ((ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i))).size() ? 0 : 4;
        } else {
            if (i == 0) {
                if (this.f1957c || this.f1961g) {
                    if (i2 == 1) {
                        return 3;
                    }
                } else if (i2 == 3) {
                    return 3;
                }
                return 2;
            } else if (i - 1 >= ContactsController.getInstance().onlineSortedUsersSectionsArray.size()) {
                return 1;
            } else {
                if (i2 >= ((ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i - 1))).size()) {
                    i3 = 4;
                }
                return i3;
            }
        }
    }

    public int getSectionCount() {
        int size = ContactsController.getInstance().onlineSortedUsersSectionsArray.size();
        if (!this.f1956b) {
            size++;
        }
        if (this.f1961g) {
            size++;
        }
        return this.f1957c ? size + 1 : size;
    }

    public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
        View letterSectionCell = view == null ? new LetterSectionCell(this.f1955a) : view;
        if (!this.f1956b || this.f1961g) {
            if (i == 0) {
                ((LetterSectionCell) letterSectionCell).setLetter(TtmlNode.ANONYMOUS_REGION_ID);
            } else if (i - 1 < ContactsController.getInstance().onlineSortedUsersSectionsArray.size()) {
                ((LetterSectionCell) letterSectionCell).setLetter((String) ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i - 1));
            } else {
                ((LetterSectionCell) letterSectionCell).setLetter(TtmlNode.ANONYMOUS_REGION_ID);
            }
        } else if (i < ContactsController.getInstance().onlineSortedUsersSectionsArray.size()) {
            ((LetterSectionCell) letterSectionCell).setLetter((String) ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i));
        } else {
            ((LetterSectionCell) letterSectionCell).setLetter(TtmlNode.ANONYMOUS_REGION_ID);
        }
        if (ThemeUtil.m2490b()) {
            ((LetterSectionCell) letterSectionCell).setLetterColor(AdvanceTheme.m2286c(AdvanceTheme.aS, -8355712));
        }
        return letterSectionCell;
    }

    public int getViewTypeCount() {
        return 5;
    }

    public boolean isRowEnabled(int i, int i2) {
        if (!this.f1956b || this.f1961g) {
            return i == 0 ? (this.f1957c || this.f1961g) ? i2 != 1 : i2 != 3 : i + -1 >= ContactsController.getInstance().onlineSortedUsersSectionsArray.size() || i2 < ((ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i - 1))).size();
        } else {
            return i2 < ((ArrayList) ContactsController.getInstance().onlineUsersSectionsDict.get(ContactsController.getInstance().onlineSortedUsersSectionsArray.get(i))).size();
        }
    }
}
