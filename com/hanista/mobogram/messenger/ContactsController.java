package com.hanista.mobogram.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.SparseArray;
import com.hanista.mobogram.BuildConfig;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.InputUser;
import com.hanista.mobogram.tgnet.TLRPC.PrivacyRule;
import com.hanista.mobogram.tgnet.TLRPC.TL_accountDaysTTL;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_getAccountTTL;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_getPrivacy;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_privacyRules;
import com.hanista.mobogram.tgnet.TLRPC.TL_contact;
import com.hanista.mobogram.tgnet.TLRPC.TL_contactStatus;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_contactsNotModified;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_deleteContacts;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_getContacts;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_getStatuses;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_importContacts;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_importedContacts;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_help_getInviteText;
import com.hanista.mobogram.tgnet.TLRPC.TL_help_inviteText;
import com.hanista.mobogram.tgnet.TLRPC.TL_importedContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPhoneContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastMonth;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastWeek;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusRecently;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.Vector;
import com.hanista.mobogram.tgnet.TLRPC.contacts_Contacts;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ContactsController {
    private static volatile ContactsController Instance;
    private static final Object loadContactsSync;
    private int completedRequestsCount;
    public ArrayList<TL_contact> contacts;
    public HashMap<Integer, Contact> contactsBook;
    private boolean contactsBookLoaded;
    public HashMap<String, Contact> contactsBookSPhones;
    public HashMap<String, TL_contact> contactsByPhone;
    public SparseArray<TL_contact> contactsDict;
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private Account currentAccount;
    private ArrayList<Integer> delayedContactsUpdate;
    private int deleteAccountTTL;
    private ArrayList<PrivacyRule> groupPrivacyRules;
    private boolean ignoreChanges;
    private String inviteText;
    private String lastContactsVersions;
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int loadingGroupInfo;
    private int loadingLastSeenInfo;
    private final Object observerLock;
    public ArrayList<String> onlineSortedUsersSectionsArray;
    public HashMap<String, ArrayList<TL_contact>> onlineUsersSectionsDict;
    public ArrayList<Contact> phoneBookContacts;
    private ArrayList<PrivacyRule> privacyRules;
    private String[] projectionNames;
    private String[] projectionPhones;
    private HashMap<String, String> sectionsToReplace;
    public ArrayList<String> sortedUsersMutualSectionsArray;
    public ArrayList<String> sortedUsersSectionsArray;
    private boolean updatingInviteText;
    public HashMap<String, ArrayList<TL_contact>> usersMutualSectionsDict;
    public HashMap<String, ArrayList<TL_contact>> usersSectionsDict;

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ ArrayList val$contactsArray;

        AnonymousClass12(ArrayList arrayList) {
            this.val$contactsArray = arrayList;
        }

        public void run() {
            ContactsController.this.performWriteContactsToPhoneBookInternal(this.val$contactsArray);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ Integer val$uid;

        AnonymousClass13(Integer num) {
            this.val$uid = num;
        }

        public void run() {
            ContactsController.this.deleteContactFromPhoneBook(this.val$uid.intValue());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.15 */
    class AnonymousClass15 implements Runnable {
        final /* synthetic */ ArrayList val$contactsToDelete;
        final /* synthetic */ ArrayList val$newContacts;

        AnonymousClass15(ArrayList arrayList, ArrayList arrayList2) {
            this.val$newContacts = arrayList;
            this.val$contactsToDelete = arrayList2;
        }

        public void run() {
            for (int i = 0; i < this.val$newContacts.size(); i++) {
                TL_contact tL_contact = (TL_contact) this.val$newContacts.get(i);
                if (ContactsController.this.contactsDict.get(tL_contact.user_id) == null) {
                    ContactsController.this.contacts.add(tL_contact);
                    ContactsController.this.contactsDict.put(tL_contact.user_id, tL_contact);
                }
            }
            for (int i2 = 0; i2 < this.val$contactsToDelete.size(); i2++) {
                Integer num = (Integer) this.val$contactsToDelete.get(i2);
                TL_contact tL_contact2 = (TL_contact) ContactsController.this.contactsDict.get(num.intValue());
                if (tL_contact2 != null) {
                    ContactsController.this.contacts.remove(tL_contact2);
                    ContactsController.this.contactsDict.remove(num.intValue());
                }
            }
            if (!this.val$newContacts.isEmpty()) {
                ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                ContactsController.this.performWriteContactsToPhoneBook();
            }
            ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), false, false, false, false);
            ContactsController.this.buildContactsSectionsArrays(!this.val$newContacts.isEmpty());
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.16 */
    class AnonymousClass16 implements Runnable {
        final /* synthetic */ String val$contactId;

        AnonymousClass16(String str) {
            this.val$contactId = str;
        }

        public void run() {
            Uri parse = Uri.parse(this.val$contactId);
            ContentValues contentValues = new ContentValues();
            contentValues.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
            ApplicationLoader.applicationContext.getContentResolver().update(parse, contentValues, null, null);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.18 */
    class AnonymousClass18 implements RequestDelegate {
        final /* synthetic */ ArrayList val$uids;
        final /* synthetic */ ArrayList val$users;

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.18.1 */
        class C03541 implements Runnable {
            C03541() {
            }

            public void run() {
                Iterator it = AnonymousClass18.this.val$users.iterator();
                while (it.hasNext()) {
                    ContactsController.this.deleteContactFromPhoneBook(((User) it.next()).id);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.18.2 */
        class C03552 implements Runnable {
            C03552() {
            }

            public void run() {
                Iterator it = AnonymousClass18.this.val$users.iterator();
                boolean z = false;
                while (it.hasNext()) {
                    boolean z2;
                    User user = (User) it.next();
                    TL_contact tL_contact = (TL_contact) ContactsController.this.contactsDict.get(user.id);
                    if (tL_contact != null) {
                        ContactsController.this.contacts.remove(tL_contact);
                        ContactsController.this.contactsDict.remove(user.id);
                        z2 = true;
                    } else {
                        z2 = z;
                    }
                    z = z2;
                }
                if (z) {
                    ContactsController.this.buildContactsSectionsArrays(false);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
            }
        }

        AnonymousClass18(ArrayList arrayList, ArrayList arrayList2) {
            this.val$uids = arrayList;
            this.val$users = arrayList2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesStorage.getInstance().deleteContacts(this.val$uids);
                Utilities.phoneBookQueue.postRunnable(new C03541());
                Iterator it = this.val$users.iterator();
                while (it.hasNext()) {
                    User user = (User) it.next();
                    if (user.phone != null && user.phone.length() > 0) {
                        UserObject.getUserName(user);
                        MessagesStorage.getInstance().applyPhoneBookUpdates(user.phone, TtmlNode.ANONYMOUS_REGION_ID);
                        Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(user.phone);
                        if (contact != null) {
                            int indexOf = contact.shortPhones.indexOf(user.phone);
                            if (indexOf != -1) {
                                contact.phoneDeleted.set(indexOf, Integer.valueOf(1));
                            }
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C03552());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.19 */
    class AnonymousClass19 implements RequestDelegate {
        final /* synthetic */ Editor val$editor;

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.19.1 */
        class C03561 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C03561(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                AnonymousClass19.this.val$editor.remove("needGetStatuses").commit();
                Vector vector = (Vector) this.val$response;
                if (!vector.objects.isEmpty()) {
                    ArrayList arrayList = new ArrayList();
                    Iterator it = vector.objects.iterator();
                    while (it.hasNext()) {
                        Object next = it.next();
                        User user = new User();
                        TL_contactStatus tL_contactStatus = (TL_contactStatus) next;
                        if (tL_contactStatus != null) {
                            if (tL_contactStatus.status instanceof TL_userStatusRecently) {
                                tL_contactStatus.status.expires = -100;
                            } else if (tL_contactStatus.status instanceof TL_userStatusLastWeek) {
                                tL_contactStatus.status.expires = -101;
                            } else if (tL_contactStatus.status instanceof TL_userStatusLastMonth) {
                                tL_contactStatus.status.expires = -102;
                            }
                            User user2 = MessagesController.getInstance().getUser(Integer.valueOf(tL_contactStatus.user_id));
                            if (user2 != null) {
                                user2.status = tL_contactStatus.status;
                            }
                            user.status = tL_contactStatus.status;
                            arrayList.add(user);
                        }
                    }
                    MessagesStorage.getInstance().updateUsers(arrayList, true, true, true);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
            }
        }

        AnonymousClass19(Editor editor) {
            this.val$editor = editor;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                AndroidUtilities.runOnUIThread(new C03561(tLObject));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.1 */
    class C03571 implements Runnable {
        C03571() {
        }

        public void run() {
            ContactsController.this.completedRequestsCount = 0;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.2 */
    class C03622 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.2.1 */
        class C03581 implements Runnable {
            final /* synthetic */ TL_help_inviteText val$res;

            C03581(TL_help_inviteText tL_help_inviteText) {
                this.val$res = tL_help_inviteText;
            }

            public void run() {
                ContactsController.this.updatingInviteText = false;
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                edit.putString("invitetext", this.val$res.message);
                edit.putInt("invitetexttime", (int) (System.currentTimeMillis() / 1000));
                edit.commit();
            }
        }

        C03622() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                TL_help_inviteText tL_help_inviteText = (TL_help_inviteText) tLObject;
                if (tL_help_inviteText.message.length() != 0) {
                    AndroidUtilities.runOnUIThread(new C03581(tL_help_inviteText));
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.3 */
    class C03633 implements Runnable {
        C03633() {
        }

        public void run() {
            if (ContactsController.this.checkContactsInternal()) {
                FileLog.m16e("tmessages", "detected contacts change");
                ContactsController.getInstance().performSyncPhoneBook(ContactsController.getInstance().getContactsCopy(ContactsController.getInstance().contactsBook), true, false, true, false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.4 */
    class C03644 implements Runnable {
        C03644() {
        }

        public void run() {
            ContactsController.getInstance().performSyncPhoneBook(new HashMap(), true, true, true, true);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.5 */
    class C03655 implements Runnable {
        C03655() {
        }

        public void run() {
            if (!ContactsController.this.contacts.isEmpty() || ContactsController.this.contactsLoaded) {
                synchronized (ContactsController.loadContactsSync) {
                    ContactsController.this.loadingContacts = false;
                }
                return;
            }
            ContactsController.this.loadContacts(true, false);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.6 */
    class C03726 implements Runnable {
        final /* synthetic */ HashMap val$contactHashMap;
        final /* synthetic */ boolean val$first;
        final /* synthetic */ boolean val$force;
        final /* synthetic */ boolean val$request;
        final /* synthetic */ boolean val$schedule;

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.6.1 */
        class C03661 implements Runnable {
            C03661() {
            }

            public void run() {
                ArrayList arrayList = new ArrayList();
                if (!(C03726.this.val$contactHashMap == null || C03726.this.val$contactHashMap.isEmpty())) {
                    try {
                        int i;
                        HashMap hashMap = new HashMap();
                        for (i = 0; i < ContactsController.this.contacts.size(); i++) {
                            User user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) ContactsController.this.contacts.get(i)).user_id));
                            if (!(user == null || user.phone == null || user.phone.length() == 0)) {
                                hashMap.put(user.phone, user);
                            }
                        }
                        int i2 = 0;
                        for (Entry value : C03726.this.val$contactHashMap.entrySet()) {
                            Contact contact = (Contact) value.getValue();
                            int i3 = 0;
                            Object obj = null;
                            while (i3 < contact.shortPhones.size()) {
                                Object obj2;
                                User user2 = (User) hashMap.get((String) contact.shortPhones.get(i3));
                                if (user2 != null) {
                                    arrayList.add(user2);
                                    contact.shortPhones.remove(i3);
                                    i = i3 - 1;
                                    obj2 = 1;
                                } else {
                                    i = i3;
                                    obj2 = obj;
                                }
                                obj = obj2;
                                i3 = i + 1;
                            }
                            int i4 = (obj == null || contact.shortPhones.size() == 0) ? i2 + 1 : i2;
                            i2 = i4;
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
                if (!arrayList.isEmpty()) {
                    ContactsController.this.deleteContact(arrayList);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.6.2 */
        class C03682 implements RequestDelegate {
            final /* synthetic */ HashMap val$contactsBookShort;
            final /* synthetic */ HashMap val$contactsMap;
            final /* synthetic */ HashMap val$contactsMapToSave;
            final /* synthetic */ int val$count;

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.6.2.1 */
            class C03671 implements Runnable {
                C03671() {
                }

                public void run() {
                    ContactsController.this.contactsBookSPhones = C03682.this.val$contactsBookShort;
                    ContactsController.this.contactsBook = C03682.this.val$contactsMap;
                    ContactsController.this.contactsSyncInProgress = false;
                    ContactsController.this.contactsBookLoaded = true;
                    if (C03726.this.val$first) {
                        ContactsController.this.contactsLoaded = true;
                    }
                    if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                        ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                        ContactsController.this.delayedContactsUpdate.clear();
                    }
                }
            }

            C03682(HashMap hashMap, int i, HashMap hashMap2, HashMap hashMap3) {
                this.val$contactsMapToSave = hashMap;
                this.val$count = i;
                this.val$contactsBookShort = hashMap2;
                this.val$contactsMap = hashMap3;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                int i = 0;
                ContactsController.this.completedRequestsCount = ContactsController.this.completedRequestsCount + 1;
                if (tL_error == null) {
                    FileLog.m16e("tmessages", "contacts imported");
                    TL_contacts_importedContacts tL_contacts_importedContacts = (TL_contacts_importedContacts) tLObject;
                    if (!tL_contacts_importedContacts.retry_contacts.isEmpty()) {
                        for (int i2 = 0; i2 < tL_contacts_importedContacts.retry_contacts.size(); i2++) {
                            this.val$contactsMapToSave.remove(Integer.valueOf((int) ((Long) tL_contacts_importedContacts.retry_contacts.get(i2)).longValue()));
                        }
                    }
                    if (ContactsController.this.completedRequestsCount == this.val$count && !this.val$contactsMapToSave.isEmpty()) {
                        MessagesStorage.getInstance().putCachedPhoneBook(this.val$contactsMapToSave);
                    }
                    MessagesStorage.getInstance().putUsersAndChats(tL_contacts_importedContacts.users, null, true, true);
                    ArrayList arrayList = new ArrayList();
                    while (i < tL_contacts_importedContacts.imported.size()) {
                        TL_contact tL_contact = new TL_contact();
                        tL_contact.user_id = ((TL_importedContact) tL_contacts_importedContacts.imported.get(i)).user_id;
                        arrayList.add(tL_contact);
                        i++;
                    }
                    ContactsController.this.processLoadedContacts(arrayList, tL_contacts_importedContacts.users, 2);
                } else {
                    FileLog.m16e("tmessages", "import contacts error " + tL_error.text);
                }
                if (ContactsController.this.completedRequestsCount == this.val$count) {
                    Utilities.stageQueue.postRunnable(new C03671());
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.6.3 */
        class C03703 implements Runnable {
            final /* synthetic */ HashMap val$contactsBookShort;
            final /* synthetic */ HashMap val$contactsMap;

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.6.3.1 */
            class C03691 implements Runnable {
                C03691() {
                }

                public void run() {
                    ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                }
            }

            C03703(HashMap hashMap, HashMap hashMap2) {
                this.val$contactsBookShort = hashMap;
                this.val$contactsMap = hashMap2;
            }

            public void run() {
                ContactsController.this.contactsBookSPhones = this.val$contactsBookShort;
                ContactsController.this.contactsBook = this.val$contactsMap;
                ContactsController.this.contactsSyncInProgress = false;
                ContactsController.this.contactsBookLoaded = true;
                if (C03726.this.val$first) {
                    ContactsController.this.contactsLoaded = true;
                }
                if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                    ContactsController.this.delayedContactsUpdate.clear();
                }
                AndroidUtilities.runOnUIThread(new C03691());
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.6.4 */
        class C03714 implements Runnable {
            final /* synthetic */ HashMap val$contactsBookShort;
            final /* synthetic */ HashMap val$contactsMap;

            C03714(HashMap hashMap, HashMap hashMap2) {
                this.val$contactsBookShort = hashMap;
                this.val$contactsMap = hashMap2;
            }

            public void run() {
                ContactsController.this.contactsBookSPhones = this.val$contactsBookShort;
                ContactsController.this.contactsBook = this.val$contactsMap;
                ContactsController.this.contactsSyncInProgress = false;
                ContactsController.this.contactsBookLoaded = true;
                if (C03726.this.val$first) {
                    ContactsController.this.contactsLoaded = true;
                }
                if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded && ContactsController.this.contactsBookLoaded) {
                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                    ContactsController.this.delayedContactsUpdate.clear();
                }
            }
        }

        C03726(HashMap hashMap, boolean z, boolean z2, boolean z3, boolean z4) {
            this.val$contactHashMap = hashMap;
            this.val$schedule = z;
            this.val$request = z2;
            this.val$first = z3;
            this.val$force = z4;
        }

        public void run() {
            int i;
            int i2;
            HashMap hashMap = new HashMap();
            for (Entry value : this.val$contactHashMap.entrySet()) {
                Contact contact = (Contact) value.getValue();
                for (int i3 = 0; i3 < contact.shortPhones.size(); i3++) {
                    hashMap.put(contact.shortPhones.get(i3), contact);
                }
            }
            FileLog.m16e("tmessages", "start read contacts from phone");
            if (!this.val$schedule) {
                ContactsController.this.checkContactsInternal();
            }
            HashMap access$500 = ContactsController.this.readContactsFromPhoneBook();
            HashMap hashMap2 = new HashMap();
            int size = this.val$contactHashMap.size();
            ArrayList arrayList = new ArrayList();
            int indexOf;
            String str;
            TL_inputPhoneContact tL_inputPhoneContact;
            if (!this.val$contactHashMap.isEmpty()) {
                for (Entry value2 : access$500.entrySet()) {
                    Contact contact2;
                    Object valueOf;
                    Integer num = (Integer) value2.getKey();
                    contact = (Contact) value2.getValue();
                    Contact contact3 = (Contact) this.val$contactHashMap.get(num);
                    if (contact3 == null) {
                        for (i = 0; i < contact.shortPhones.size(); i++) {
                            Contact contact4 = (Contact) hashMap.get(contact.shortPhones.get(i));
                            if (contact4 != null) {
                                contact2 = contact4;
                                valueOf = Integer.valueOf(contact4.id);
                                break;
                            }
                        }
                    }
                    contact2 = contact3;
                    Integer num2 = num;
                    Object obj = (contact2 == null || ((!TextUtils.isEmpty(contact.first_name) || contact2.first_name.equals(contact.first_name)) && (TextUtils.isEmpty(contact.last_name) || contact2.last_name.equals(contact.last_name)))) ? null : 1;
                    String str2;
                    if (contact2 == null || obj != null) {
                        for (i2 = 0; i2 < contact.phones.size(); i2++) {
                            str2 = (String) contact.shortPhones.get(i2);
                            hashMap2.put(str2, contact);
                            if (contact2 != null) {
                                indexOf = contact2.shortPhones.indexOf(str2);
                                if (indexOf != -1) {
                                    Integer num3 = (Integer) contact2.phoneDeleted.get(indexOf);
                                    contact.phoneDeleted.set(i2, num3);
                                    if (num3.intValue() == 1) {
                                    }
                                }
                            }
                            if (this.val$request && !(obj == null && ContactsController.this.contactsByPhone.containsKey(str2))) {
                                TL_inputPhoneContact tL_inputPhoneContact2 = new TL_inputPhoneContact();
                                tL_inputPhoneContact2.client_id = (long) contact.id;
                                tL_inputPhoneContact2.client_id |= ((long) i2) << 32;
                                tL_inputPhoneContact2.first_name = contact.first_name;
                                tL_inputPhoneContact2.last_name = contact.last_name;
                                tL_inputPhoneContact2.phone = (String) contact.phones.get(i2);
                                arrayList.add(tL_inputPhoneContact2);
                            }
                        }
                        if (contact2 != null) {
                            this.val$contactHashMap.remove(valueOf);
                        }
                    } else {
                        for (indexOf = 0; indexOf < contact.phones.size(); indexOf++) {
                            str2 = (String) contact.shortPhones.get(indexOf);
                            hashMap2.put(str2, contact);
                            i2 = contact2.shortPhones.indexOf(str2);
                            if (i2 != -1) {
                                contact.phoneDeleted.set(indexOf, contact2.phoneDeleted.get(i2));
                                contact2.phones.remove(i2);
                                contact2.shortPhones.remove(i2);
                                contact2.phoneDeleted.remove(i2);
                                contact2.phoneTypes.remove(i2);
                            } else if (this.val$request) {
                                TL_contact tL_contact = (TL_contact) ContactsController.this.contactsByPhone.get(str2);
                                if (tL_contact != null) {
                                    User user = MessagesController.getInstance().getUser(Integer.valueOf(tL_contact.user_id));
                                    if (user != null) {
                                        str2 = user.first_name != null ? user.first_name : TtmlNode.ANONYMOUS_REGION_ID;
                                        str = user.last_name != null ? user.last_name : TtmlNode.ANONYMOUS_REGION_ID;
                                        if (user != null) {
                                            if (str2.equals(contact.first_name)) {
                                                if (str.equals(contact.last_name)) {
                                                }
                                            }
                                            if (TextUtils.isEmpty(contact.first_name) && TextUtils.isEmpty(contact.last_name)) {
                                            }
                                        }
                                    }
                                }
                                tL_inputPhoneContact = new TL_inputPhoneContact();
                                tL_inputPhoneContact.client_id = (long) contact.id;
                                tL_inputPhoneContact.client_id |= ((long) indexOf) << 32;
                                tL_inputPhoneContact.first_name = contact.first_name;
                                tL_inputPhoneContact.last_name = contact.last_name;
                                tL_inputPhoneContact.phone = (String) contact.phones.get(indexOf);
                                arrayList.add(tL_inputPhoneContact);
                            }
                        }
                        if (contact2.phones.isEmpty()) {
                            this.val$contactHashMap.remove(valueOf);
                        }
                    }
                }
                if (!this.val$first && this.val$contactHashMap.isEmpty() && arrayList.isEmpty() && size == access$500.size()) {
                    FileLog.m16e("tmessages", "contacts not changed!");
                    return;
                } else if (this.val$request && !this.val$contactHashMap.isEmpty() && !access$500.isEmpty() && arrayList.isEmpty()) {
                    MessagesStorage.getInstance().putCachedPhoneBook(access$500);
                }
            } else if (this.val$request) {
                for (Entry value22 : access$500.entrySet()) {
                    Contact contact5 = (Contact) value22.getValue();
                    int intValue = ((Integer) value22.getKey()).intValue();
                    for (indexOf = 0; indexOf < contact5.phones.size(); indexOf++) {
                        if (!this.val$force) {
                            TL_contact tL_contact2 = (TL_contact) ContactsController.this.contactsByPhone.get((String) contact5.shortPhones.get(indexOf));
                            if (tL_contact2 != null) {
                                User user2 = MessagesController.getInstance().getUser(Integer.valueOf(tL_contact2.user_id));
                                if (user2 != null) {
                                    String str3 = user2.first_name != null ? user2.first_name : TtmlNode.ANONYMOUS_REGION_ID;
                                    str = user2.last_name != null ? user2.last_name : TtmlNode.ANONYMOUS_REGION_ID;
                                    if (user2 != null) {
                                        if (str3.equals(contact5.first_name)) {
                                            if (str.equals(contact5.last_name)) {
                                            }
                                        }
                                        if (TextUtils.isEmpty(contact5.first_name) && TextUtils.isEmpty(contact5.last_name)) {
                                        }
                                    }
                                }
                            }
                        }
                        tL_inputPhoneContact = new TL_inputPhoneContact();
                        tL_inputPhoneContact.client_id = (long) intValue;
                        tL_inputPhoneContact.client_id |= ((long) indexOf) << 32;
                        tL_inputPhoneContact.first_name = contact5.first_name;
                        tL_inputPhoneContact.last_name = contact5.last_name;
                        tL_inputPhoneContact.phone = (String) contact5.phones.get(indexOf);
                        arrayList.add(tL_inputPhoneContact);
                    }
                }
            }
            FileLog.m16e("tmessages", "done processing contacts");
            if (!this.val$request) {
                Utilities.stageQueue.postRunnable(new C03714(hashMap2, access$500));
                if (!access$500.isEmpty()) {
                    MessagesStorage.getInstance().putCachedPhoneBook(access$500);
                }
            } else if (arrayList.isEmpty()) {
                Utilities.stageQueue.postRunnable(new C03703(hashMap2, access$500));
            } else {
                HashMap hashMap3 = new HashMap(access$500);
                ContactsController.this.completedRequestsCount = 0;
                i2 = (int) Math.ceil((double) (((float) arrayList.size()) / 500.0f));
                for (i = 0; i < i2; i++) {
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.addAll(arrayList.subList(i * 500, Math.min((i + 1) * 500, arrayList.size())));
                    TLObject tL_contacts_importContacts = new TL_contacts_importContacts();
                    tL_contacts_importContacts.contacts = arrayList2;
                    tL_contacts_importContacts.replace = false;
                    ConnectionsManager.getInstance().sendRequest(tL_contacts_importContacts, new C03682(hashMap3, i2, hashMap2, access$500), 6);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.7 */
    class C03747 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.7.1 */
        class C03731 implements Runnable {
            C03731() {
            }

            public void run() {
                synchronized (ContactsController.loadContactsSync) {
                    ContactsController.this.loadingContacts = false;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
            }
        }

        C03747() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                contacts_Contacts com_hanista_mobogram_tgnet_TLRPC_contacts_Contacts = (contacts_Contacts) tLObject;
                if (com_hanista_mobogram_tgnet_TLRPC_contacts_Contacts instanceof TL_contacts_contactsNotModified) {
                    ContactsController.this.contactsLoaded = true;
                    if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsBookLoaded) {
                        ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                        ContactsController.this.delayedContactsUpdate.clear();
                    }
                    AndroidUtilities.runOnUIThread(new C03731());
                    FileLog.m16e("tmessages", "load contacts don't change");
                    return;
                }
                ContactsController.this.processLoadedContacts(com_hanista_mobogram_tgnet_TLRPC_contacts_Contacts.contacts, com_hanista_mobogram_tgnet_TLRPC_contacts_Contacts.users, 0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.8 */
    class C03838 implements Runnable {
        final /* synthetic */ ArrayList val$contactsArr;
        final /* synthetic */ int val$from;
        final /* synthetic */ ArrayList val$usersArr;

        /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1 */
        class C03821 implements Runnable {
            final /* synthetic */ boolean val$isEmpty;
            final /* synthetic */ HashMap val$usersDict;

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1.1 */
            class C03751 implements Comparator<TL_contact> {
                C03751() {
                }

                public int compare(TL_contact tL_contact, TL_contact tL_contact2) {
                    return tL_contact.user_id > tL_contact2.user_id ? 1 : tL_contact.user_id < tL_contact2.user_id ? -1 : 0;
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1.2 */
            class C03762 implements Comparator<TL_contact> {
                C03762() {
                }

                public int compare(TL_contact tL_contact, TL_contact tL_contact2) {
                    return UserObject.getFirstName((User) C03821.this.val$usersDict.get(Integer.valueOf(tL_contact.user_id))).compareTo(UserObject.getFirstName((User) C03821.this.val$usersDict.get(Integer.valueOf(tL_contact2.user_id))));
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1.3 */
            class C03773 implements Comparator<String> {
                C03773() {
                }

                public int compare(String str, String str2) {
                    return str.charAt(0) == '#' ? 1 : str2.charAt(0) == '#' ? -1 : str.compareTo(str2);
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1.4 */
            class C03784 implements Comparator<String> {
                C03784() {
                }

                public int compare(String str, String str2) {
                    return str.charAt(0) == '#' ? 1 : str2.charAt(0) == '#' ? -1 : str.compareTo(str2);
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1.5 */
            class C03795 implements Runnable {
                final /* synthetic */ SparseArray val$contactsDictionary;
                final /* synthetic */ HashMap val$sectionsDict;
                final /* synthetic */ HashMap val$sectionsDictMutual;
                final /* synthetic */ ArrayList val$sortedSectionsArray;
                final /* synthetic */ ArrayList val$sortedSectionsArrayMutual;

                C03795(SparseArray sparseArray, HashMap hashMap, HashMap hashMap2, ArrayList arrayList, ArrayList arrayList2) {
                    this.val$contactsDictionary = sparseArray;
                    this.val$sectionsDict = hashMap;
                    this.val$sectionsDictMutual = hashMap2;
                    this.val$sortedSectionsArray = arrayList;
                    this.val$sortedSectionsArrayMutual = arrayList2;
                }

                public void run() {
                    ContactsController.this.contacts = C03838.this.val$contactsArr;
                    ContactsController.this.contactsDict = this.val$contactsDictionary;
                    ContactsController.this.usersSectionsDict = this.val$sectionsDict;
                    ContactsController.this.usersMutualSectionsDict = this.val$sectionsDictMutual;
                    ContactsController.this.sortedUsersSectionsArray = this.val$sortedSectionsArray;
                    ContactsController.this.sortedUsersMutualSectionsArray = this.val$sortedSectionsArrayMutual;
                    if (C03838.this.val$from != 2) {
                        synchronized (ContactsController.loadContactsSync) {
                            ContactsController.this.loadingContacts = false;
                        }
                    }
                    ContactsController.this.performWriteContactsToPhoneBook();
                    ContactsController.this.updateUnregisteredContacts(C03838.this.val$contactsArr);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                    if (C03838.this.val$from == 1 || C03821.this.val$isEmpty) {
                        ContactsController.this.reloadContactsStatusesMaybe();
                    } else {
                        ContactsController.this.saveContactsLoadTime();
                    }
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1.6 */
            class C03816 implements Runnable {
                final /* synthetic */ HashMap val$contactsByPhonesDictFinal;

                /* renamed from: com.hanista.mobogram.messenger.ContactsController.8.1.6.1 */
                class C03801 implements Runnable {
                    C03801() {
                    }

                    public void run() {
                        ContactsController.this.contactsByPhone = C03816.this.val$contactsByPhonesDictFinal;
                    }
                }

                C03816(HashMap hashMap) {
                    this.val$contactsByPhonesDictFinal = hashMap;
                }

                public void run() {
                    Utilities.globalQueue.postRunnable(new C03801());
                    if (!ContactsController.this.contactsSyncInProgress) {
                        ContactsController.this.contactsSyncInProgress = true;
                        MessagesStorage.getInstance().getCachedPhoneBook();
                    }
                }
            }

            C03821(HashMap hashMap, boolean z) {
                this.val$usersDict = hashMap;
                this.val$isEmpty = z;
            }

            public void run() {
                FileLog.m16e("tmessages", "done loading contacts");
                if (C03838.this.val$from != 1 || (!C03838.this.val$contactsArr.isEmpty() && Math.abs((System.currentTimeMillis() / 1000) - ((long) UserConfig.lastContactsSyncTime)) < 86400)) {
                    TL_contact tL_contact;
                    if (C03838.this.val$from == 0) {
                        UserConfig.lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
                        UserConfig.saveConfig(false);
                    }
                    Iterator it = C03838.this.val$contactsArr.iterator();
                    while (it.hasNext()) {
                        tL_contact = (TL_contact) it.next();
                        if (this.val$usersDict.get(Integer.valueOf(tL_contact.user_id)) == null && tL_contact.user_id != UserConfig.getClientUserId()) {
                            ContactsController.this.loadContacts(false, true);
                            FileLog.m16e("tmessages", "contacts are broken, load from server");
                            return;
                        }
                    }
                    if (C03838.this.val$from != 1) {
                        MessagesStorage.getInstance().putUsersAndChats(C03838.this.val$usersArr, null, true, true);
                        MessagesStorage.getInstance().putContacts(C03838.this.val$contactsArr, C03838.this.val$from != 2);
                        Collections.sort(C03838.this.val$contactsArr, new C03751());
                        StringBuilder stringBuilder = new StringBuilder();
                        Iterator it2 = C03838.this.val$contactsArr.iterator();
                        while (it2.hasNext()) {
                            tL_contact = (TL_contact) it2.next();
                            if (stringBuilder.length() != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(tL_contact.user_id);
                        }
                        UserConfig.contactsHash = Utilities.MD5(stringBuilder.toString());
                        UserConfig.saveConfig(false);
                    }
                    Collections.sort(C03838.this.val$contactsArr, new C03762());
                    SparseArray sparseArray = new SparseArray();
                    HashMap hashMap = new HashMap();
                    HashMap hashMap2 = new HashMap();
                    Object arrayList = new ArrayList();
                    Object arrayList2 = new ArrayList();
                    HashMap hashMap3 = !ContactsController.this.contactsBookLoaded ? new HashMap() : null;
                    for (int i = 0; i < C03838.this.val$contactsArr.size(); i++) {
                        tL_contact = (TL_contact) C03838.this.val$contactsArr.get(i);
                        User user = (User) this.val$usersDict.get(Integer.valueOf(tL_contact.user_id));
                        if (user != null) {
                            Object obj;
                            sparseArray.put(tL_contact.user_id, tL_contact);
                            if (hashMap3 != null) {
                                hashMap3.put(user.phone, tL_contact);
                            }
                            String firstName = UserObject.getFirstName(user);
                            if (firstName.length() > 1) {
                                firstName = firstName.substring(0, 1);
                            }
                            if (firstName.length() == 0) {
                                obj = "#";
                            } else {
                                String toUpperCase = firstName.toUpperCase();
                            }
                            firstName = (String) ContactsController.this.sectionsToReplace.get(obj);
                            if (firstName != null) {
                                obj = firstName;
                            }
                            ArrayList arrayList3 = (ArrayList) hashMap.get(obj);
                            if (arrayList3 == null) {
                                arrayList3 = new ArrayList();
                                hashMap.put(obj, arrayList3);
                                arrayList.add(obj);
                            }
                            arrayList3.add(tL_contact);
                            if (user.mutual_contact) {
                                ArrayList arrayList4 = (ArrayList) hashMap2.get(obj);
                                if (arrayList4 == null) {
                                    arrayList4 = new ArrayList();
                                    hashMap2.put(obj, arrayList4);
                                    arrayList2.add(obj);
                                }
                                arrayList4.add(tL_contact);
                            }
                        }
                    }
                    Collections.sort(arrayList, new C03773());
                    Collections.sort(arrayList2, new C03784());
                    AndroidUtilities.runOnUIThread(new C03795(sparseArray, hashMap, hashMap2, arrayList, arrayList2));
                    if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded && ContactsController.this.contactsBookLoaded) {
                        ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                        ContactsController.this.delayedContactsUpdate.clear();
                    }
                    if (hashMap3 != null) {
                        AndroidUtilities.runOnUIThread(new C03816(hashMap3));
                        return;
                    } else {
                        ContactsController.this.contactsLoaded = true;
                        return;
                    }
                }
                ContactsController.this.loadContacts(false, true);
            }
        }

        C03838(ArrayList arrayList, int i, ArrayList arrayList2) {
            this.val$usersArr = arrayList;
            this.val$from = i;
            this.val$contactsArr = arrayList2;
        }

        public void run() {
            boolean z = true;
            int i = 0;
            MessagesController instance = MessagesController.getInstance();
            ArrayList arrayList = this.val$usersArr;
            if (this.val$from != 1) {
                z = false;
            }
            instance.putUsers(arrayList, z);
            HashMap hashMap = new HashMap();
            boolean isEmpty = this.val$contactsArr.isEmpty();
            if (!ContactsController.this.contacts.isEmpty()) {
                int i2 = 0;
                while (i2 < this.val$contactsArr.size()) {
                    if (ContactsController.this.contactsDict.get(((TL_contact) this.val$contactsArr.get(i2)).user_id) != null) {
                        this.val$contactsArr.remove(i2);
                        i2--;
                    }
                    i2++;
                }
                this.val$contactsArr.addAll(ContactsController.this.contacts);
            }
            while (i < this.val$contactsArr.size()) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) this.val$contactsArr.get(i)).user_id));
                if (user != null) {
                    hashMap.put(Integer.valueOf(user.id), user);
                }
                i++;
            }
            Utilities.stageQueue.postRunnable(new C03821(hashMap, isEmpty));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.ContactsController.9 */
    class C03849 implements Comparator<Contact> {
        C03849() {
        }

        public int compare(Contact contact, Contact contact2) {
            String str = contact.first_name;
            if (str.length() == 0) {
                str = contact.last_name;
            }
            String str2 = contact2.first_name;
            if (str2.length() == 0) {
                str2 = contact2.last_name;
            }
            return str.compareTo(str2);
        }
    }

    public static class Contact {
        public String first_name;
        public int id;
        public String last_name;
        public ArrayList<Integer> phoneDeleted;
        public ArrayList<String> phoneTypes;
        public ArrayList<String> phones;
        public ArrayList<String> shortPhones;

        public Contact() {
            this.phones = new ArrayList();
            this.phoneTypes = new ArrayList();
            this.shortPhones = new ArrayList();
            this.phoneDeleted = new ArrayList();
        }
    }

    static {
        loadContactsSync = new Object();
        Instance = null;
    }

    public ContactsController() {
        this.loadingContacts = false;
        this.ignoreChanges = false;
        this.contactsSyncInProgress = false;
        this.observerLock = new Object();
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = TtmlNode.ANONYMOUS_REGION_ID;
        this.delayedContactsUpdate = new ArrayList();
        this.updatingInviteText = false;
        this.sectionsToReplace = new HashMap();
        this.loadingDeleteInfo = 0;
        this.loadingLastSeenInfo = 0;
        this.loadingGroupInfo = 0;
        this.privacyRules = null;
        this.groupPrivacyRules = null;
        this.projectionPhones = new String[]{"contact_id", "data1", "data2", "data3"};
        this.projectionNames = new String[]{"contact_id", "data2", "data3", "display_name", "data5"};
        this.contactsBook = new HashMap();
        this.contactsBookSPhones = new HashMap();
        this.phoneBookContacts = new ArrayList();
        this.contacts = new ArrayList();
        this.contactsDict = new SparseArray();
        this.usersSectionsDict = new HashMap();
        this.sortedUsersSectionsArray = new ArrayList();
        this.usersMutualSectionsDict = new HashMap();
        this.sortedUsersMutualSectionsArray = new ArrayList();
        this.onlineUsersSectionsDict = new HashMap();
        this.onlineSortedUsersSectionsArray = new ArrayList();
        this.contactsByPhone = new HashMap();
        if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("needGetStatuses", false)) {
            reloadContactsStatuses();
        }
        this.sectionsToReplace.put("\u00c0", "A");
        this.sectionsToReplace.put("\u00c1", "A");
        this.sectionsToReplace.put("\u00c4", "A");
        this.sectionsToReplace.put("\u00d9", "U");
        this.sectionsToReplace.put("\u00da", "U");
        this.sectionsToReplace.put("\u00dc", "U");
        this.sectionsToReplace.put("\u00cc", "I");
        this.sectionsToReplace.put("\u00cd", "I");
        this.sectionsToReplace.put("\u00cf", "I");
        this.sectionsToReplace.put("\u00c8", "E");
        this.sectionsToReplace.put("\u00c9", "E");
        this.sectionsToReplace.put("\u00ca", "E");
        this.sectionsToReplace.put("\u00cb", "E");
        this.sectionsToReplace.put("\u00d2", "O");
        this.sectionsToReplace.put("\u00d3", "O");
        this.sectionsToReplace.put("\u00d6", "O");
        this.sectionsToReplace.put("\u00c7", "C");
        this.sectionsToReplace.put("\u00d1", "N");
        this.sectionsToReplace.put("\u0178", "Y");
        this.sectionsToReplace.put("\u00dd", "Y");
        this.sectionsToReplace.put("\u0162", "Y");
    }

    private void applyContactsUpdates(ArrayList<Integer> arrayList, ConcurrentHashMap<Integer, User> concurrentHashMap, ArrayList<TL_contact> arrayList2, ArrayList<Integer> arrayList3) {
        ArrayList arrayList4;
        ArrayList arrayList5;
        Integer num;
        Contact contact;
        int indexOf;
        if (arrayList2 == null || arrayList3 == null) {
            arrayList4 = new ArrayList();
            arrayList5 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                num = (Integer) arrayList.get(i);
                if (num.intValue() > 0) {
                    TL_contact tL_contact = new TL_contact();
                    tL_contact.user_id = num.intValue();
                    arrayList4.add(tL_contact);
                } else if (num.intValue() < 0) {
                    arrayList5.add(Integer.valueOf(-num.intValue()));
                }
            }
        }
        FileLog.m16e("tmessages", "process update - contacts add = " + arrayList4.size() + " delete = " + arrayList5.size());
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        int i2 = 0;
        Object obj = null;
        while (i2 < arrayList4.size()) {
            Object obj2;
            TL_contact tL_contact2 = (TL_contact) arrayList4.get(i2);
            User user = null;
            if (concurrentHashMap != null) {
                user = (User) concurrentHashMap.get(Integer.valueOf(tL_contact2.user_id));
            }
            if (user == null) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(tL_contact2.user_id));
            } else {
                MessagesController.getInstance().putUser(user, true);
            }
            if (user == null || user.phone == null || user.phone.length() == 0) {
                obj2 = 1;
            } else {
                contact = (Contact) this.contactsBookSPhones.get(user.phone);
                if (contact != null) {
                    indexOf = contact.shortPhones.indexOf(user.phone);
                    if (indexOf != -1) {
                        contact.phoneDeleted.set(indexOf, Integer.valueOf(0));
                    }
                }
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(user.phone);
                obj2 = obj;
            }
            i2++;
            obj = obj2;
        }
        for (i2 = 0; i2 < arrayList5.size(); i2++) {
            num = (Integer) arrayList5.get(i2);
            Utilities.phoneBookQueue.postRunnable(new AnonymousClass13(num));
            user = null;
            if (concurrentHashMap != null) {
                user = (User) concurrentHashMap.get(num);
            }
            if (user == null) {
                user = MessagesController.getInstance().getUser(num);
            } else {
                MessagesController.getInstance().putUser(user, true);
            }
            if (user == null) {
                obj = 1;
            } else if (user.phone != null && user.phone.length() > 0) {
                contact = (Contact) this.contactsBookSPhones.get(user.phone);
                if (contact != null) {
                    indexOf = contact.shortPhones.indexOf(user.phone);
                    if (indexOf != -1) {
                        contact.phoneDeleted.set(indexOf, Integer.valueOf(1));
                    }
                }
                if (stringBuilder2.length() != 0) {
                    stringBuilder2.append(",");
                }
                stringBuilder2.append(user.phone);
            }
        }
        if (!(stringBuilder.length() == 0 && stringBuilder2.length() == 0)) {
            MessagesStorage.getInstance().applyPhoneBookUpdates(stringBuilder.toString(), stringBuilder2.toString());
        }
        if (obj != null) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    ContactsController.this.loadContacts(false, true);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new AnonymousClass15(arrayList4, arrayList5));
        }
    }

    private void buildContactsSectionsArrays(boolean z) {
        if (z) {
            Collections.sort(this.contacts, new Comparator<TL_contact>() {
                public int compare(TL_contact tL_contact, TL_contact tL_contact2) {
                    return UserObject.getFirstName(MessagesController.getInstance().getUser(Integer.valueOf(tL_contact.user_id))).compareTo(UserObject.getFirstName(MessagesController.getInstance().getUser(Integer.valueOf(tL_contact2.user_id))));
                }
            });
        }
        StringBuilder stringBuilder = new StringBuilder();
        HashMap hashMap = new HashMap();
        Object arrayList = new ArrayList();
        Iterator it = this.contacts.iterator();
        while (it.hasNext()) {
            TL_contact tL_contact = (TL_contact) it.next();
            User user = MessagesController.getInstance().getUser(Integer.valueOf(tL_contact.user_id));
            if (user != null) {
                Object obj;
                String firstName = UserObject.getFirstName(user);
                if (firstName.length() > 1) {
                    firstName = firstName.substring(0, 1);
                }
                if (firstName.length() == 0) {
                    obj = "#";
                } else {
                    String toUpperCase = firstName.toUpperCase();
                }
                firstName = (String) this.sectionsToReplace.get(obj);
                if (firstName != null) {
                    obj = firstName;
                }
                ArrayList arrayList2 = (ArrayList) hashMap.get(obj);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    hashMap.put(obj, arrayList2);
                    arrayList.add(obj);
                }
                arrayList2.add(tL_contact);
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(tL_contact.user_id);
            }
        }
        UserConfig.contactsHash = Utilities.MD5(stringBuilder.toString());
        UserConfig.saveConfig(false);
        Collections.sort(arrayList, new Comparator<String>() {
            public int compare(String str, String str2) {
                return str.charAt(0) == '#' ? 1 : str2.charAt(0) == '#' ? -1 : str.compareTo(str2);
            }
        });
        this.usersSectionsDict = hashMap;
        this.sortedUsersSectionsArray = arrayList;
    }

    private boolean checkContactsInternal() {
        Cursor query;
        Cursor cursor;
        Throwable th;
        Throwable e;
        boolean z;
        boolean z2 = false;
        if (!MoboConstants.f1311D) {
            return false;
        }
        try {
            if (!hasContactsPermission()) {
                return false;
            }
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            try {
                query = contentResolver.query(RawContacts.CONTENT_URI, new String[]{"version"}, null, null, null);
                if (query != null) {
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        while (query.moveToNext()) {
                            stringBuilder.append(query.getString(query.getColumnIndex("version")));
                        }
                        String stringBuilder2 = stringBuilder.toString();
                        if (!(this.lastContactsVersions.length() == 0 || this.lastContactsVersions.equals(stringBuilder2))) {
                            z2 = true;
                        }
                        this.lastContactsVersions = stringBuilder2;
                    } catch (Throwable e2) {
                        cursor = query;
                        th = e2;
                        z = false;
                        try {
                            FileLog.m18e("tmessages", th);
                            if (cursor != null) {
                                return z;
                            }
                            try {
                                cursor.close();
                                return z;
                            } catch (Exception e3) {
                                th = e3;
                                FileLog.m18e("tmessages", th);
                                return z;
                            }
                        } catch (Throwable th2) {
                            z2 = z;
                            e2 = th2;
                            query = cursor;
                            if (query != null) {
                                query.close();
                            }
                            throw e2;
                        }
                    } catch (Throwable th3) {
                        e2 = th3;
                        if (query != null) {
                            query.close();
                        }
                        throw e2;
                    }
                }
                z = z2;
                if (query == null) {
                    return z;
                }
                query.close();
                return z;
            } catch (Throwable e22) {
                th2 = e22;
                cursor = null;
                z = false;
                FileLog.m18e("tmessages", th2);
                if (cursor != null) {
                    return z;
                }
                cursor.close();
                return z;
            } catch (Throwable th4) {
                e22 = th4;
                query = null;
                if (query != null) {
                    query.close();
                }
                throw e22;
            }
        } catch (Throwable e222) {
            th2 = e222;
            z = z2;
            FileLog.m18e("tmessages", th2);
            return z;
        }
    }

    private void deleteContactFromPhoneBook(int i) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ApplicationLoader.applicationContext.getContentResolver().delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + i, null);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    public static String formatName(String str, String str2) {
        int i = 0;
        if (str != null) {
            str = str.trim();
        }
        if (str2 != null) {
            str2 = str2.trim();
        }
        int length = str != null ? str.length() : 0;
        if (str2 != null) {
            i = str2.length();
        }
        StringBuilder stringBuilder = new StringBuilder((i + length) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (str != null && str.length() > 0) {
                stringBuilder.append(str);
                if (str2 != null && str2.length() > 0) {
                    stringBuilder.append(" ");
                    stringBuilder.append(str2);
                }
            } else if (str2 != null && str2.length() > 0) {
                stringBuilder.append(str2);
            }
        } else if (str2 != null && str2.length() > 0) {
            stringBuilder.append(str2);
            if (str != null && str.length() > 0) {
                stringBuilder.append(" ");
                stringBuilder.append(str);
            }
        } else if (str != null && str.length() > 0) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    public static ContactsController getInstance() {
        ContactsController contactsController = Instance;
        if (contactsController == null) {
            synchronized (ContactsController.class) {
                contactsController = Instance;
                if (contactsController == null) {
                    contactsController = new ContactsController();
                    Instance = contactsController;
                }
            }
        }
        return contactsController;
    }

    private boolean hasContactsPermission() {
        Throwable e;
        Cursor cursor = null;
        if (VERSION.SDK_INT >= 23) {
            return ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0;
        } else {
            Cursor query;
            try {
                query = ApplicationLoader.applicationContext.getContentResolver().query(Phone.CONTENT_URI, this.projectionPhones, null, null, null);
                if (query != null) {
                    try {
                        if (query.getCount() != 0) {
                            if (query != null) {
                                try {
                                    query.close();
                                } catch (Throwable e2) {
                                    FileLog.m18e("tmessages", e2);
                                }
                            }
                            return true;
                        }
                    } catch (Throwable th) {
                        e2 = th;
                        try {
                            FileLog.m18e("tmessages", e2);
                            if (query != null) {
                                try {
                                    query.close();
                                } catch (Throwable e22) {
                                    FileLog.m18e("tmessages", e22);
                                }
                            }
                            return true;
                        } catch (Throwable th2) {
                            e22 = th2;
                            cursor = query;
                            if (cursor != null) {
                                try {
                                    cursor.close();
                                } catch (Throwable e3) {
                                    FileLog.m18e("tmessages", e3);
                                }
                            }
                            throw e22;
                        }
                    }
                }
                if (query != null) {
                    try {
                        query.close();
                    } catch (Throwable e222) {
                        FileLog.m18e("tmessages", e222);
                    }
                }
                return false;
            } catch (Throwable th3) {
                e222 = th3;
                if (cursor != null) {
                    cursor.close();
                }
                throw e222;
            }
        }
    }

    private void performWriteContactsToPhoneBook() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.contacts);
        Utilities.phoneBookQueue.postRunnable(new AnonymousClass12(arrayList));
    }

    private void performWriteContactsToPhoneBookInternal(ArrayList<TL_contact> arrayList) {
        try {
            if (hasContactsPermission()) {
                Uri build = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build();
                Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(build, new String[]{"_id", "sync2"}, null, null, null);
                HashMap hashMap = new HashMap();
                if (query != null) {
                    while (query.moveToNext()) {
                        hashMap.put(Integer.valueOf(query.getInt(1)), Long.valueOf(query.getLong(0)));
                    }
                    query.close();
                    for (int i = 0; i < arrayList.size(); i++) {
                        TL_contact tL_contact = (TL_contact) arrayList.get(i);
                        if (!hashMap.containsKey(Integer.valueOf(tL_contact.user_id))) {
                            addContactToPhoneBook(MessagesController.getInstance().getUser(Integer.valueOf(tL_contact.user_id)), false);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private HashMap<Integer, Contact> readContactsFromPhoneBook() {
        HashMap<Integer, Contact> hashMap = new HashMap();
        try {
            if (!hasContactsPermission()) {
                return hashMap;
            }
            String string;
            String stripExceptNumbers;
            Contact contact;
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            HashMap hashMap2 = new HashMap();
            Iterable arrayList = new ArrayList();
            Cursor query = contentResolver.query(Phone.CONTENT_URI, this.projectionPhones, null, null, null);
            if (query != null) {
                if (query.getCount() > 0) {
                    while (query.moveToNext()) {
                        string = query.getString(1);
                        if (!(string == null || string.length() == 0)) {
                            stripExceptNumbers = PhoneFormat.stripExceptNumbers(string, true);
                            if (stripExceptNumbers.length() == 0) {
                                continue;
                            } else {
                                Object substring;
                                if (stripExceptNumbers.startsWith("+")) {
                                    substring = stripExceptNumbers.substring(1);
                                } else {
                                    String str = stripExceptNumbers;
                                }
                                if (hashMap2.containsKey(substring)) {
                                    continue;
                                } else {
                                    Integer valueOf = Integer.valueOf(query.getInt(0));
                                    if (!arrayList.contains(valueOf)) {
                                        arrayList.add(valueOf);
                                    }
                                    int i = query.getInt(2);
                                    contact = (Contact) hashMap.get(valueOf);
                                    if (contact == null) {
                                        contact = new Contact();
                                        contact.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                                        contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                        contact.id = valueOf.intValue();
                                        hashMap.put(valueOf, contact);
                                    }
                                    contact.shortPhones.add(substring);
                                    contact.phones.add(stripExceptNumbers);
                                    contact.phoneDeleted.add(Integer.valueOf(0));
                                    if (i == 0) {
                                        contact.phoneTypes.add(query.getString(3));
                                    } else if (i == 1) {
                                        contact.phoneTypes.add(LocaleController.getString("PhoneHome", C0338R.string.PhoneHome));
                                    } else if (i == 2) {
                                        contact.phoneTypes.add(LocaleController.getString("PhoneMobile", C0338R.string.PhoneMobile));
                                    } else if (i == 3) {
                                        contact.phoneTypes.add(LocaleController.getString("PhoneWork", C0338R.string.PhoneWork));
                                    } else if (i == 12) {
                                        contact.phoneTypes.add(LocaleController.getString("PhoneMain", C0338R.string.PhoneMain));
                                    } else {
                                        contact.phoneTypes.add(LocaleController.getString("PhoneOther", C0338R.string.PhoneOther));
                                    }
                                    hashMap2.put(substring, contact);
                                }
                            }
                        }
                    }
                }
                query.close();
            }
            Cursor query2 = contentResolver.query(Data.CONTENT_URI, this.projectionNames, "contact_id IN (" + TextUtils.join(",", arrayList) + ") AND " + "mimetype" + " = '" + "vnd.android.cursor.item/name" + "'", null, null);
            if (query2 != null && query2.getCount() > 0) {
                while (query2.moveToNext()) {
                    int i2 = query2.getInt(0);
                    stripExceptNumbers = query2.getString(1);
                    String string2 = query2.getString(2);
                    String string3 = query2.getString(3);
                    String string4 = query2.getString(4);
                    contact = (Contact) hashMap.get(Integer.valueOf(i2));
                    if (contact != null && contact.first_name.length() == 0 && contact.last_name.length() == 0) {
                        contact.first_name = stripExceptNumbers;
                        contact.last_name = string2;
                        if (contact.first_name == null) {
                            contact.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        if (!(string4 == null || string4.length() == 0)) {
                            if (contact.first_name.length() != 0) {
                                contact.first_name += " " + string4;
                            } else {
                                contact.first_name = string4;
                            }
                        }
                        if (contact.last_name == null) {
                            contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        if (contact.last_name.length() == 0 && contact.first_name.length() == 0 && string3 != null && string3.length() != 0) {
                            contact.first_name = string3;
                        }
                    }
                }
                query2.close();
            }
            try {
                query2 = contentResolver.query(RawContacts.CONTENT_URI, new String[]{"display_name", "sync1", "contact_id"}, "account_type = 'com.whatsapp'", null, null);
                if (query2 != null) {
                    while (query2.moveToNext()) {
                        String string5 = query2.getString(1);
                        if (!(string5 == null || string5.length() == 0)) {
                            boolean startsWith = string5.startsWith("+");
                            string = Utilities.parseIntToString(string5);
                            if (!(string == null || string.length() == 0)) {
                                Object obj;
                                if (startsWith) {
                                    string5 = string;
                                } else {
                                    obj = "+" + string;
                                }
                                if (!hashMap2.containsKey(string)) {
                                    Object string6 = query2.getString(0);
                                    if (!TextUtils.isEmpty(string6)) {
                                        Contact contact2 = new Contact();
                                        contact2.first_name = string6;
                                        contact2.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                        contact2.id = query2.getInt(2);
                                        hashMap.put(Integer.valueOf(contact2.id), contact2);
                                        contact2.phoneDeleted.add(Integer.valueOf(0));
                                        contact2.shortPhones.add(string);
                                        contact2.phones.add(obj);
                                        contact2.phoneTypes.add(LocaleController.getString("PhoneMobile", C0338R.string.PhoneMobile));
                                        hashMap2.put(string, contact2);
                                    }
                                }
                            }
                        }
                    }
                    query2.close();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            return hashMap;
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
            hashMap.clear();
        }
    }

    private void reloadContactsStatusesMaybe() {
        try {
            if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getLong("lastReloadStatusTime", 0) < System.currentTimeMillis() - 86400000) {
                reloadContactsStatuses();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private void saveContactsLoadTime() {
        try {
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private void updateUnregisteredContacts(ArrayList<TL_contact> arrayList) {
        HashMap hashMap = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            TL_contact tL_contact = (TL_contact) it.next();
            User user = MessagesController.getInstance().getUser(Integer.valueOf(tL_contact.user_id));
            if (!(user == null || user.phone == null || user.phone.length() == 0)) {
                hashMap.put(user.phone, tL_contact);
            }
        }
        Object arrayList2 = new ArrayList();
        for (Entry entry : this.contactsBook.entrySet()) {
            Object obj;
            Contact contact = (Contact) entry.getValue();
            ((Integer) entry.getKey()).intValue();
            int i = 0;
            while (i < contact.phones.size()) {
                if (hashMap.containsKey((String) contact.shortPhones.get(i)) || ((Integer) contact.phoneDeleted.get(i)).intValue() == 1) {
                    obj = 1;
                    break;
                }
                i++;
            }
            obj = null;
            if (obj == null) {
                arrayList2.add(contact);
            }
        }
        Collections.sort(arrayList2, new C03849());
        this.phoneBookContacts = arrayList2;
    }

    public void addContact(User user) {
        if (user != null && user.phone != null) {
            TLObject tL_contacts_importContacts = new TL_contacts_importContacts();
            ArrayList arrayList = new ArrayList();
            TL_inputPhoneContact tL_inputPhoneContact = new TL_inputPhoneContact();
            tL_inputPhoneContact.phone = user.phone;
            if (!tL_inputPhoneContact.phone.startsWith("+")) {
                tL_inputPhoneContact.phone = "+" + tL_inputPhoneContact.phone;
            }
            tL_inputPhoneContact.first_name = user.first_name;
            tL_inputPhoneContact.last_name = user.last_name;
            tL_inputPhoneContact.client_id = 0;
            arrayList.add(tL_inputPhoneContact);
            tL_contacts_importContacts.contacts = arrayList;
            tL_contacts_importContacts.replace = false;
            ConnectionsManager.getInstance().sendRequest(tL_contacts_importContacts, new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.messenger.ContactsController.17.1 */
                class C03521 implements Runnable {
                    final /* synthetic */ User val$u;

                    C03521(User user) {
                        this.val$u = user;
                    }

                    public void run() {
                        ContactsController.this.addContactToPhoneBook(this.val$u, true);
                    }
                }

                /* renamed from: com.hanista.mobogram.messenger.ContactsController.17.2 */
                class C03532 implements Runnable {
                    final /* synthetic */ TL_contacts_importedContacts val$res;

                    C03532(TL_contacts_importedContacts tL_contacts_importedContacts) {
                        this.val$res = tL_contacts_importedContacts;
                    }

                    public void run() {
                        Iterator it = this.val$res.users.iterator();
                        while (it.hasNext()) {
                            User user = (User) it.next();
                            MessagesController.getInstance().putUser(user, false);
                            if (ContactsController.this.contactsDict.get(user.id) == null) {
                                TL_contact tL_contact = new TL_contact();
                                tL_contact.user_id = user.id;
                                ContactsController.this.contacts.add(tL_contact);
                                ContactsController.this.contactsDict.put(tL_contact.user_id, tL_contact);
                            }
                        }
                        ContactsController.this.buildContactsSectionsArrays(true);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        TL_contacts_importedContacts tL_contacts_importedContacts = (TL_contacts_importedContacts) tLObject;
                        MessagesStorage.getInstance().putUsersAndChats(tL_contacts_importedContacts.users, null, true, true);
                        for (int i = 0; i < tL_contacts_importedContacts.users.size(); i++) {
                            User user = (User) tL_contacts_importedContacts.users.get(i);
                            Utilities.phoneBookQueue.postRunnable(new C03521(user));
                            TL_contact tL_contact = new TL_contact();
                            tL_contact.user_id = user.id;
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(tL_contact);
                            MessagesStorage.getInstance().putContacts(arrayList, false);
                            if (user.phone != null && user.phone.length() > 0) {
                                ContactsController.formatName(user.first_name, user.last_name);
                                MessagesStorage.getInstance().applyPhoneBookUpdates(user.phone, TtmlNode.ANONYMOUS_REGION_ID);
                                Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(user.phone);
                                if (contact != null) {
                                    int indexOf = contact.shortPhones.indexOf(user.phone);
                                    if (indexOf != -1) {
                                        contact.phoneDeleted.set(indexOf, Integer.valueOf(0));
                                    }
                                }
                            }
                        }
                        AndroidUtilities.runOnUIThread(new C03532(tL_contacts_importedContacts));
                    }
                }
            }, 6);
        }
    }

    public long addContactToPhoneBook(User user, boolean z) {
        long j = -1;
        if (!(this.currentAccount == null || user == null || user.phone == null || user.phone.length() == 0 || !hasContactsPermission())) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            if (z) {
                try {
                    contentResolver.delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + user.id, null);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            ArrayList arrayList = new ArrayList();
            Builder newInsert = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
            newInsert.withValue("account_name", this.currentAccount.name);
            newInsert.withValue("account_type", this.currentAccount.type);
            newInsert.withValue("sync1", user.phone);
            newInsert.withValue("sync2", Integer.valueOf(user.id));
            arrayList.add(newInsert.build());
            newInsert = ContentProviderOperation.newInsert(Data.CONTENT_URI);
            newInsert.withValueBackReference("raw_contact_id", 0);
            newInsert.withValue("mimetype", "vnd.android.cursor.item/name");
            newInsert.withValue("data2", user.first_name);
            newInsert.withValue("data3", user.last_name);
            arrayList.add(newInsert.build());
            newInsert = ContentProviderOperation.newInsert(Data.CONTENT_URI);
            newInsert.withValueBackReference("raw_contact_id", 0);
            newInsert.withValue("mimetype", "vnd.android.cursor.item/vnd.com.hanista.mobogram.messenger.android.profile");
            newInsert.withValue("data1", Integer.valueOf(user.id));
            newInsert.withValue("data2", "Telegram Profile");
            newInsert.withValue("data3", "+" + user.phone);
            newInsert.withValue("data4", Integer.valueOf(user.id));
            arrayList.add(newInsert.build());
            try {
                ContentProviderResult[] applyBatch = contentResolver.applyBatch("com.android.contacts", arrayList);
                if (!(applyBatch == null || applyBatch.length <= 0 || applyBatch[0].uri == null)) {
                    j = Long.parseLong(applyBatch[0].uri.getLastPathSegment());
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
        return j;
    }

    public void checkAppAccount() {
        int i = 1;
        int i2 = 0;
        AccountManager accountManager = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accountsByType = accountManager.getAccountsByType("org.mobogram.account");
            if (accountsByType != null && accountsByType.length > 0) {
                for (Account removeAccount : accountsByType) {
                    accountManager.removeAccount(removeAccount, null, null);
                }
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        Account[] accountsByType2 = accountManager.getAccountsByType("org.mobogram.messenger");
        if (UserConfig.isClientActivated()) {
            if (accountsByType2.length == 1) {
                Account account = accountsByType2[0];
                if (account.name.equals(TtmlNode.ANONYMOUS_REGION_ID + UserConfig.getClientUserId())) {
                    this.currentAccount = account;
                    i = 0;
                }
            }
            readContacts();
        } else if (accountsByType2.length <= 0) {
            i = 0;
        }
        if (i != 0) {
            while (i2 < accountsByType2.length) {
                try {
                    accountManager.removeAccount(accountsByType2[i2], null, null);
                    i2++;
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
            if (UserConfig.isClientActivated()) {
                try {
                    this.currentAccount = new Account(TtmlNode.ANONYMOUS_REGION_ID + UserConfig.getClientUserId(), BuildConfig.APPLICATION_ID);
                    accountManager.addAccountExplicitly(this.currentAccount, TtmlNode.ANONYMOUS_REGION_ID, null);
                } catch (Throwable e22) {
                    FileLog.m18e("tmessages", e22);
                }
            }
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new C03633());
    }

    public void checkInviteText() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.inviteText = sharedPreferences.getString("invitetext", null);
        if (!UserConfig.isRobot) {
            int i = sharedPreferences.getInt("invitetexttime", 0);
            if (!this.updatingInviteText) {
                if (this.inviteText == null || i + 86400 < ((int) (System.currentTimeMillis() / 1000))) {
                    this.updatingInviteText = true;
                    ConnectionsManager.getInstance().sendRequest(new TL_help_getInviteText(), new C03622(), 2);
                }
            }
        }
    }

    public void cleanup() {
        this.contactsBook.clear();
        this.contactsBookSPhones.clear();
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.contactsByPhone.clear();
        this.loadingContacts = false;
        this.contactsSyncInProgress = false;
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = TtmlNode.ANONYMOUS_REGION_ID;
        this.loadingDeleteInfo = 0;
        this.deleteAccountTTL = 0;
        this.loadingLastSeenInfo = 0;
        this.loadingGroupInfo = 0;
        Utilities.globalQueue.postRunnable(new C03571());
        this.privacyRules = null;
    }

    public void deleteAllAppAccounts() {
        try {
            AccountManager accountManager = AccountManager.get(ApplicationLoader.applicationContext);
            Account[] accountsByType = accountManager.getAccountsByType(BuildConfig.APPLICATION_ID);
            for (Account removeAccount : accountsByType) {
                accountManager.removeAccount(removeAccount, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteContact(ArrayList<User> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TLObject tL_contacts_deleteContacts = new TL_contacts_deleteContacts();
            ArrayList arrayList2 = new ArrayList();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                User user = (User) it.next();
                try {
                    new DataBaseAccess().m852a(user.id);
                } catch (Exception e) {
                }
                InputUser inputUser = MessagesController.getInputUser(user);
                if (inputUser != null) {
                    arrayList2.add(Integer.valueOf(user.id));
                    tL_contacts_deleteContacts.id.add(inputUser);
                }
            }
            ConnectionsManager.getInstance().sendRequest(tL_contacts_deleteContacts, new AnonymousClass18(arrayList2, arrayList));
        }
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new C03644());
    }

    public HashMap<Integer, Contact> getContactsCopy(HashMap<Integer, Contact> hashMap) {
        HashMap<Integer, Contact> hashMap2 = new HashMap();
        for (Entry entry : hashMap.entrySet()) {
            Contact contact = new Contact();
            Contact contact2 = (Contact) entry.getValue();
            contact.phoneDeleted.addAll(contact2.phoneDeleted);
            contact.phones.addAll(contact2.phones);
            contact.phoneTypes.addAll(contact2.phoneTypes);
            contact.shortPhones.addAll(contact2.shortPhones);
            contact.first_name = contact2.first_name;
            contact.last_name = contact2.last_name;
            contact.id = contact2.id;
            hashMap2.put(Integer.valueOf(contact.id), contact);
        }
        return hashMap2;
    }

    public int getDeleteAccountTTL() {
        return this.deleteAccountTTL;
    }

    public String getInviteText() {
        return LocaleController.getString("InviteText", C0338R.string.InviteText);
    }

    public boolean getLoadingDeleteInfo() {
        return this.loadingDeleteInfo != 2;
    }

    public boolean getLoadingGroupInfo() {
        return this.loadingGroupInfo != 2;
    }

    public boolean getLoadingLastSeenInfo() {
        return this.loadingLastSeenInfo != 2;
    }

    public ArrayList<PrivacyRule> getPrivacyRules(boolean z) {
        return z ? this.groupPrivacyRules : this.privacyRules;
    }

    public void initOnlineUsersSectionsDict() {
        reloadContactsStatuses();
        this.onlineUsersSectionsDict = new HashMap();
        this.onlineSortedUsersSectionsArray = new ArrayList();
        for (String str : this.usersSectionsDict.keySet()) {
            ArrayList arrayList = new ArrayList();
            Iterator it = ((ArrayList) this.usersSectionsDict.get(str)).iterator();
            while (it.hasNext()) {
                TL_contact tL_contact = (TL_contact) it.next();
                User user = MessagesController.getInstance().getUser(Integer.valueOf(tL_contact.user_id));
                if (user.id == UserConfig.getClientUserId() || (user.status != null && user.status.expires > ConnectionsManager.getInstance().getCurrentTime())) {
                    arrayList.add(tL_contact);
                }
            }
            if (arrayList.size() > 0) {
                this.onlineUsersSectionsDict.put(str, arrayList);
                if (!this.onlineSortedUsersSectionsArray.contains(str)) {
                    this.onlineSortedUsersSectionsArray.add(str);
                }
            }
        }
        Collections.sort(this.onlineSortedUsersSectionsArray, new Comparator<String>() {
            public int compare(String str, String str2) {
                return str.charAt(0) == '#' ? 1 : str2.charAt(0) == '#' ? -1 : str.compareTo(str2);
            }
        });
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    public void loadContacts(boolean z, boolean z2) {
        synchronized (loadContactsSync) {
            this.loadingContacts = true;
        }
        if (z) {
            FileLog.m16e("tmessages", "load contacts from cache");
            MessagesStorage.getInstance().getContacts();
            if (UserConfig.isRobot) {
                this.contactsLoaded = true;
                this.loadingContacts = false;
            }
        } else if (!UserConfig.isRobot) {
            FileLog.m16e("tmessages", "load contacts from server");
            TLObject tL_contacts_getContacts = new TL_contacts_getContacts();
            tL_contacts_getContacts.hash = z2 ? TtmlNode.ANONYMOUS_REGION_ID : UserConfig.contactsHash;
            ConnectionsManager.getInstance().sendRequest(tL_contacts_getContacts, new C03747());
        }
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            ConnectionsManager.getInstance().sendRequest(new TL_account_getAccountTTL(), new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.messenger.ContactsController.20.1 */
                class C03591 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C03591(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            ContactsController.this.deleteAccountTTL = ((TL_accountDaysTTL) this.val$response).days;
                            ContactsController.this.loadingDeleteInfo = 2;
                        } else {
                            ContactsController.this.loadingDeleteInfo = 0;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C03591(tL_error, tLObject));
                }
            });
        }
        if (this.loadingLastSeenInfo == 0) {
            this.loadingLastSeenInfo = 1;
            TLObject tL_account_getPrivacy = new TL_account_getPrivacy();
            tL_account_getPrivacy.key = new TL_inputPrivacyKeyStatusTimestamp();
            ConnectionsManager.getInstance().sendRequest(tL_account_getPrivacy, new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.messenger.ContactsController.21.1 */
                class C03601 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C03601(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) this.val$response;
                            MessagesController.getInstance().putUsers(tL_account_privacyRules.users, false);
                            ContactsController.this.privacyRules = tL_account_privacyRules.rules;
                            ContactsController.this.loadingLastSeenInfo = 2;
                        } else {
                            ContactsController.this.loadingLastSeenInfo = 0;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C03601(tL_error, tLObject));
                }
            });
        }
        if (this.loadingGroupInfo == 0) {
            this.loadingGroupInfo = 1;
            tL_account_getPrivacy = new TL_account_getPrivacy();
            tL_account_getPrivacy.key = new TL_inputPrivacyKeyChatInvite();
            ConnectionsManager.getInstance().sendRequest(tL_account_getPrivacy, new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.messenger.ContactsController.22.1 */
                class C03611 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C03611(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            TL_account_privacyRules tL_account_privacyRules = (TL_account_privacyRules) this.val$response;
                            MessagesController.getInstance().putUsers(tL_account_privacyRules.users, false);
                            ContactsController.this.groupPrivacyRules = tL_account_privacyRules.rules;
                            ContactsController.this.loadingGroupInfo = 2;
                        } else {
                            ContactsController.this.loadingGroupInfo = 0;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C03611(tL_error, tLObject));
                }
            });
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    protected void markAsContacted(String str) {
        if (str != null) {
            Utilities.phoneBookQueue.postRunnable(new AnonymousClass16(str));
        }
    }

    protected void performSyncPhoneBook(HashMap<Integer, Contact> hashMap, boolean z, boolean z2, boolean z3, boolean z4) {
        if (!MoboConstants.f1311D) {
            return;
        }
        if (z2 || this.contactsBookLoaded) {
            Utilities.globalQueue.postRunnable(new C03726(hashMap, z3, z, z2, z4));
        }
    }

    public void processContactsUpdates(ArrayList<Integer> arrayList, ConcurrentHashMap<Integer, User> concurrentHashMap) {
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            int indexOf;
            if (num.intValue() > 0) {
                TL_contact tL_contact = new TL_contact();
                tL_contact.user_id = num.intValue();
                arrayList2.add(tL_contact);
                if (!this.delayedContactsUpdate.isEmpty()) {
                    indexOf = this.delayedContactsUpdate.indexOf(Integer.valueOf(-num.intValue()));
                    if (indexOf != -1) {
                        this.delayedContactsUpdate.remove(indexOf);
                    }
                }
            } else if (num.intValue() < 0) {
                arrayList3.add(Integer.valueOf(-num.intValue()));
                if (!this.delayedContactsUpdate.isEmpty()) {
                    indexOf = this.delayedContactsUpdate.indexOf(Integer.valueOf(-num.intValue()));
                    if (indexOf != -1) {
                        this.delayedContactsUpdate.remove(indexOf);
                    }
                }
            }
        }
        if (!arrayList3.isEmpty()) {
            MessagesStorage.getInstance().deleteContacts(arrayList3);
        }
        if (!arrayList2.isEmpty()) {
            MessagesStorage.getInstance().putContacts(arrayList2, false);
        }
        if (this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(arrayList, concurrentHashMap, arrayList2, arrayList3);
            return;
        }
        this.delayedContactsUpdate.addAll(arrayList);
        FileLog.m16e("tmessages", "delay update - contacts add = " + arrayList2.size() + " delete = " + arrayList3.size());
    }

    public void processLoadedContacts(ArrayList<TL_contact> arrayList, ArrayList<User> arrayList2, int i) {
        AndroidUtilities.runOnUIThread(new C03838(arrayList2, i, arrayList));
    }

    public void readContacts() {
        synchronized (loadContactsSync) {
            if (this.loadingContacts) {
                return;
            }
            this.loadingContacts = true;
            Utilities.stageQueue.postRunnable(new C03655());
        }
    }

    public void reloadContactsStatuses() {
        saveContactsLoadTime();
        MessagesController.getInstance().clearFullUsers();
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        edit.putBoolean("needGetStatuses", true).commit();
        ConnectionsManager.getInstance().sendRequest(new TL_contacts_getStatuses(), new AnonymousClass19(edit));
    }

    public void setDeleteAccountTTL(int i) {
        this.deleteAccountTTL = i;
    }

    public void setPrivacyRules(ArrayList<PrivacyRule> arrayList, boolean z) {
        if (z) {
            this.groupPrivacyRules = arrayList;
        } else {
            this.privacyRules = arrayList;
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }
}
