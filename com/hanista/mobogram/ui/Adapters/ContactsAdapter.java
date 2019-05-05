package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
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
import com.hanista.mobogram.ui.Cells.DividerCell;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.LetterSectionCell;
import com.hanista.mobogram.ui.Cells.TextCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.HashMap;

public class ContactsAdapter extends BaseSectionsAdapter {
    private HashMap<Integer, ?> checkedMap;
    private HashMap<Integer, User> ignoreUsers;
    private boolean isAdmin;
    private Context mContext;
    private boolean needPhonebook;
    private int onlyUsers;
    private boolean scrolling;

    public ContactsAdapter(Context context, int i, boolean z, HashMap<Integer, User> hashMap, boolean z2) {
        this.mContext = context;
        this.onlyUsers = i;
        this.needPhonebook = z;
        this.ignoreUsers = hashMap;
        this.isAdmin = z2;
    }

    private void updateListBG(ViewGroup viewGroup) {
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

    private void updateViewColor(View view) {
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

    public int getCountForSection(int i) {
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        int size;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                return (this.needPhonebook || this.isAdmin) ? 3 : 5;
            } else {
                if (i - 1 < arrayList.size()) {
                    size = ((ArrayList) hashMap.get(arrayList.get(i - 1))).size();
                    return (i + -1 != arrayList.size() + -1 || this.needPhonebook) ? size + 1 : size;
                }
            }
        } else if (i < arrayList.size()) {
            size = ((ArrayList) hashMap.get(arrayList.get(i))).size();
            return (i != arrayList.size() + -1 || this.needPhonebook) ? size + 1 : size;
        }
        return this.needPhonebook ? ContactsController.getInstance().phoneBookContacts.size() : 0;
    }

    public Object getItem(int i, int i2) {
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        ArrayList arrayList2;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (i < arrayList.size()) {
                arrayList2 = (ArrayList) hashMap.get(arrayList.get(i));
                if (i2 < arrayList2.size()) {
                    return MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) arrayList2.get(i2)).user_id));
                }
            }
            return null;
        } else if (i == 0) {
            return null;
        } else {
            if (i - 1 >= arrayList.size()) {
                return this.needPhonebook ? ContactsController.getInstance().phoneBookContacts.get(i2) : null;
            } else {
                arrayList2 = (ArrayList) hashMap.get(arrayList.get(i - 1));
                return i2 < arrayList2.size() ? MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) arrayList2.get(i2)).user_id)) : null;
            }
        }
    }

    public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
        View view2;
        boolean z = true;
        int itemViewType = getItemViewType(i, i2);
        int c = AdvanceTheme.m2286c(AdvanceTheme.aS, Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        int c2 = AdvanceTheme.m2286c(AdvanceTheme.aS, Theme.MSG_TEXT_COLOR);
        int c3 = !ThemeUtil.m2490b() ? ThemeUtil.m2485a().m2289c() : AdvanceTheme.aT;
        if (itemViewType == 4) {
            if (view == null) {
                view = new DividerCell(this.mContext);
                view.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 72.0f : 28.0f), 0);
                view.setTag("contacts_row_color");
            }
            updateViewColor(view);
            view2 = view;
        } else if (itemViewType == 3) {
            if (view == null) {
                view2 = new GreySectionCell(this.mContext);
                ((GreySectionCell) view2).setText(LocaleController.getString("Contacts", C0338R.string.Contacts).toUpperCase());
                if (ThemeUtil.m2490b()) {
                    ((GreySectionCell) view2).setBackgroundColor(AdvanceTheme.aU);
                    ((GreySectionCell) view2).setTextColor(c);
                }
            } else {
                view2 = view;
            }
            updateViewColor(view2);
        } else if (itemViewType == 2) {
            view2 = view == null ? new TextCell(this.mContext) : view;
            updateViewColor(view2);
            TextCell textCell = (TextCell) view2;
            if (ThemeUtil.m2490b()) {
                textCell.setTextColor(c2);
            }
            if (this.needPhonebook) {
                if (i2 == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteFriends", C0338R.string.InviteFriends), C0338R.drawable.menu_invite_telegram);
                } else if (i2 == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewContact", C0338R.string.NewContact), C0338R.drawable.menu_invite_telegram);
                }
            } else if (this.isAdmin) {
                if (i2 == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", C0338R.string.InviteToGroupByLink), C0338R.drawable.menu_invite_telegram);
                } else if (i2 == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewContact", C0338R.string.NewContact), C0338R.drawable.menu_invite_telegram);
                }
            } else if (i2 == 0) {
                textCell.setTextAndIcon(LocaleController.getString("NewGroup", C0338R.string.NewGroup), C0338R.drawable.menu_newgroup_telegram);
            } else if (i2 == 1) {
                textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", C0338R.string.NewSecretChat), C0338R.drawable.menu_secret_telegram);
            } else if (i2 == 2) {
                textCell.setTextAndIcon(LocaleController.getString("NewChannel", C0338R.string.NewChannel), C0338R.drawable.menu_broadcast_telegram);
            } else if (i2 == 3) {
                textCell.setTextAndIcon(LocaleController.getString("NewContact", C0338R.string.NewContact), C0338R.drawable.menu_invite_telegram);
            }
            textCell.setIconColor(c3);
        } else if (itemViewType == 1) {
            View view3;
            if (view == null) {
                view2 = new TextCell(this.mContext);
                if (ThemeUtil.m2490b()) {
                    ((TextCell) view2).setTextColor(c2);
                    ((TextCell) view2).setTextSize(AdvanceTheme.aX);
                }
                view3 = view2;
            } else {
                view3 = view;
            }
            updateViewColor(view3);
            Contact contact = (Contact) ContactsController.getInstance().phoneBookContacts.get(i2);
            TextCell textCell2 = (TextCell) view3;
            if (contact.first_name != null && contact.last_name != null) {
                textCell2.setText(contact.first_name + " " + contact.last_name);
            } else if (contact.first_name == null || contact.last_name != null) {
                textCell2.setText(contact.last_name);
            } else {
                textCell2.setText(contact.first_name);
            }
            view2 = view3;
        } else if (itemViewType == 0) {
            if (view == null) {
                view2 = new UserCell(this.mContext, 58, 1, false);
                ((UserCell) view2).setStatusColors(-5723992, -12876608);
                view2.setTag("Contacts");
            } else {
                view2 = view;
            }
            updateViewColor(view2);
            HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
            ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
            c = (this.onlyUsers == 0 || this.isAdmin) ? 1 : 0;
            TLObject user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) ((ArrayList) hashMap.get(arrayList.get(i - c))).get(i2)).user_id));
            ((UserCell) view2).setData(user, null, null, 0);
            if (this.checkedMap != null) {
                UserCell userCell = (UserCell) view2;
                boolean containsKey = this.checkedMap.containsKey(Integer.valueOf(user.id));
                if (this.scrolling) {
                    z = false;
                }
                userCell.setChecked(containsKey, z);
            }
            if (this.ignoreUsers != null) {
                if (this.ignoreUsers.containsKey(Integer.valueOf(user.id))) {
                    view2.setAlpha(0.5f);
                } else {
                    view2.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                }
            }
        } else {
            view2 = view;
        }
        updateListBG(viewGroup);
        return view2;
    }

    public int getItemViewType(int i, int i2) {
        int i3 = 0;
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            return i2 < ((ArrayList) hashMap.get(arrayList.get(i))).size() ? 0 : 4;
        } else {
            if (i == 0) {
                if (this.needPhonebook || this.isAdmin) {
                    if (i2 == 2) {
                        return 3;
                    }
                } else if (i2 == 4) {
                    return 3;
                }
                return 2;
            } else if (i - 1 >= arrayList.size()) {
                return 1;
            } else {
                if (i2 >= ((ArrayList) hashMap.get(arrayList.get(i - 1))).size()) {
                    i3 = 4;
                }
                return i3;
            }
        }
    }

    public int getSectionCount() {
        int size = (this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray).size();
        if (this.onlyUsers == 0) {
            size++;
        }
        if (this.isAdmin) {
            size++;
        }
        return this.needPhonebook ? size + 1 : size;
    }

    public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
        HashMap hashMap;
        if (this.onlyUsers == 2) {
            hashMap = ContactsController.getInstance().usersMutualSectionsDict;
        } else {
            hashMap = ContactsController.getInstance().usersSectionsDict;
        }
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        View letterSectionCell = view == null ? new LetterSectionCell(this.mContext) : view;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                ((LetterSectionCell) letterSectionCell).setLetter(TtmlNode.ANONYMOUS_REGION_ID);
            } else if (i - 1 < arrayList.size()) {
                ((LetterSectionCell) letterSectionCell).setLetter((String) arrayList.get(i - 1));
            } else {
                ((LetterSectionCell) letterSectionCell).setLetter(TtmlNode.ANONYMOUS_REGION_ID);
            }
        } else if (i < arrayList.size()) {
            ((LetterSectionCell) letterSectionCell).setLetter((String) arrayList.get(i));
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
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            return i == 0 ? (this.needPhonebook || this.isAdmin) ? i2 != 2 : i2 != 4 : i + -1 >= arrayList.size() || i2 < ((ArrayList) hashMap.get(arrayList.get(i - 1))).size();
        } else {
            return i2 < ((ArrayList) hashMap.get(arrayList.get(i))).size();
        }
    }

    public void setCheckedMap(HashMap<Integer, ?> hashMap) {
        this.checkedMap = hashMap;
    }

    public void setIsScrolling(boolean z) {
        this.scrolling = z;
    }
}
