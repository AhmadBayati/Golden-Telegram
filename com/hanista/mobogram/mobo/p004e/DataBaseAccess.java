package com.hanista.mobogram.mobo.p004e;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.mobo.alarmservice.AlarmResponse;
import com.hanista.mobogram.mobo.dialogdm.DialogDm;
import com.hanista.mobogram.mobo.p000a.Archive;
import com.hanista.mobogram.mobo.p000a.ArchiveMessageInfo;
import com.hanista.mobogram.mobo.p001b.Category;
import com.hanista.mobogram.mobo.p005f.DialogSettings;
import com.hanista.mobogram.mobo.p007h.Favorite;
import com.hanista.mobogram.mobo.p012l.Hidden;
import com.hanista.mobogram.mobo.p016p.SpecificContact;
import com.hanista.mobogram.mobo.p018q.FavoriteSticker;
import com.hanista.mobogram.telegraph.model.UpdateModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.e.a */
public class DataBaseAccess {
    private MobogramDBHelper f873a;

    public DataBaseAccess() {
        this.f873a = ApplicationLoader.getOpenHelper();
    }

    public void m831A() {
        m911r();
        m913t();
        m859b();
        m879f();
        m905o();
        m906p();
        m893j();
        m916w();
    }

    public Cursor m832a(int i, int i2, int i3) {
        String str = i != 0 ? "type=" + i : null;
        if (i3 != 0) {
            str = str == null ? "user_id=" + i3 : str + " and user_id=" + i3;
        }
        return this.f873a.getReadableDatabase().query("tbl_update", null, str, null, null, null, "change_date DESC", i2 + TtmlNode.ANONYMOUS_REGION_ID);
    }

    public ArchiveMessageInfo m833a(long j, int i) {
        List h = m888h("org_dialog_id=" + j + " and " + "org_message_id" + "=" + i);
        return h.size() > 0 ? (ArchiveMessageInfo) h.get(0) : null;
    }

    public AlarmResponse m834a(long j) {
        Throwable th;
        Cursor cursor = null;
        try {
            Cursor query = this.f873a.getReadableDatabase().query("tbl_alarm", null, "_id=" + j, null, null, null, "_id");
            try {
                if (query.moveToFirst()) {
                    AlarmResponse b = m857b(query);
                    if (query == null) {
                        return b;
                    }
                    query.close();
                    return b;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public Category m835a(Long l, boolean z) {
        List a = m850a("_id=" + l, z);
        return a.size() > 0 ? (Category) a.get(0) : null;
    }

    public UpdateModel m836a(Cursor cursor) {
        boolean z = false;
        Long valueOf = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
        int i = cursor.getInt(cursor.getColumnIndex("type"));
        String string = cursor.getString(cursor.getColumnIndex("old_value"));
        String string2 = cursor.getString(cursor.getColumnIndex("new_value"));
        int i2 = cursor.getInt(cursor.getColumnIndex("user_id"));
        if (!cursor.isNull(cursor.getColumnIndex("is_new")) && cursor.getLong(cursor.getColumnIndex("is_new")) > 0) {
            z = true;
        }
        return new UpdateModel(valueOf, i, string, string2, i2, z, cursor.getString(cursor.getColumnIndex("change_date")));
    }

    public Long m837a(Archive archive) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", archive.m208b());
            contentValues.put("priority", archive.m209c());
            if (archive.m204a() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_archive", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_archive", contentValues, "_id=" + archive.m204a().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = archive.m204a();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m838a(ArchiveMessageInfo archiveMessageInfo) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("message_id", archiveMessageInfo.m237b());
            contentValues.put("org_message_id", archiveMessageInfo.m238c());
            contentValues.put("org_dialog_id", archiveMessageInfo.m239d());
            contentValues.put("archive_id", archiveMessageInfo.m240e());
            if (archiveMessageInfo.m234a() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_arch_msg_info", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_arch_msg_info", contentValues, "_id=" + archiveMessageInfo.m234a().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = archiveMessageInfo.m234a();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m839a(AlarmResponse alarmResponse) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            if (alarmResponse.getId() != null) {
                contentValues.put("_id", alarmResponse.getId());
            }
            contentValues.put("title", alarmResponse.getTitle());
            contentValues.put("message", alarmResponse.getMessage());
            contentValues.put("imageUrl", alarmResponse.getImageUrl());
            contentValues.put("positiveBtnText", alarmResponse.getPositiveBtnText());
            contentValues.put("positiveBtnAction", alarmResponse.getPositiveBtnAction());
            contentValues.put("positiveBtnUrl", alarmResponse.getPositiveBtnUrl());
            contentValues.put("negativeBtnText", alarmResponse.getNegativeBtnText());
            contentValues.put("negativeBtnAction", alarmResponse.getNegativeBtnAction());
            contentValues.put("negativeBtnUrl", alarmResponse.getNegativeBtnUrl());
            contentValues.put("showCount", alarmResponse.getShowCount());
            contentValues.put("exitOnDismiss", Integer.valueOf(alarmResponse.getExitOnDismiss().booleanValue() ? 1 : 0));
            contentValues.put("targetNetwork", alarmResponse.getTargetNetwork());
            if (alarmResponse.getDisplayCount() != null) {
                contentValues.put("displayCount", alarmResponse.getDisplayCount());
            }
            contentValues.put("targetVersion", alarmResponse.getTargetVersion());
            Long valueOf;
            if (alarmResponse.getId() == null || m834a(alarmResponse.getId().longValue()) == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_alarm", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
                return valueOf;
            }
            writableDatabase.update("tbl_alarm", contentValues, "_id=" + alarmResponse.getId().longValue(), null);
            writableDatabase.setTransactionSuccessful();
            valueOf = alarmResponse.getId();
            writableDatabase.endTransaction();
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m840a(Category category) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", category.m281b());
            contentValues.put("priority", category.m283c());
            if (category.m276a() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_category", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_category", contentValues, "_id=" + category.m276a().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = category.m276a();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m841a(DialogDm dialogDm) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("dialog_id", Long.valueOf(dialogDm.m561b()));
            contentValues.put("doc_type", Integer.valueOf(dialogDm.m563c()));
            contentValues.put("message_count", Integer.valueOf(dialogDm.m564d()));
            contentValues.put("priority", dialogDm.m565e());
            if (dialogDm.m566f() != null) {
                contentValues.put("change_date", dialogDm.m566f());
            }
            if (dialogDm.m556a() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_dialog_dm", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_dialog_dm", contentValues, "_id=" + dialogDm.m556a().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = dialogDm.m556a();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m842a(DialogSettings dialogSettings) {
        int i = 1;
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("dialog_id", dialogSettings.m976b());
            contentValues.put("auto_dl_mask", Integer.valueOf(dialogSettings.m980c()));
            contentValues.put("background", Integer.valueOf(dialogSettings.m981d()));
            contentValues.put("hide_typing_state", Integer.valueOf(dialogSettings.m982e() ? 1 : 0));
            String str = "not_send_read_state";
            if (!dialogSettings.m983f()) {
                i = 0;
            }
            contentValues.put(str, Integer.valueOf(i));
            contentValues.put("marker", Integer.valueOf(dialogSettings.m984g()));
            if (dialogSettings.m985h() != null) {
                contentValues.put("change_date", dialogSettings.m985h());
            }
            if (dialogSettings.m972a() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_dialog_settings", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_dialog_settings", contentValues, "_id=" + dialogSettings.m972a().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = dialogSettings.m972a();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m843a(Favorite favorite) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("chatID", favorite.m1139a());
            long insertOrThrow = writableDatabase.insertOrThrow("tbl_favorite", null, contentValues);
            writableDatabase.setTransactionSuccessful();
            Long valueOf = Long.valueOf(insertOrThrow);
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m844a(Hidden hidden) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("dialogID", hidden.m1391a());
            long insertOrThrow = writableDatabase.insertOrThrow("tbl_hidden", null, contentValues);
            writableDatabase.setTransactionSuccessful();
            Long valueOf = Long.valueOf(insertOrThrow);
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m845a(SpecificContact specificContact) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", Integer.valueOf(specificContact.m1999b()));
            contentValues.put("change_type", Integer.valueOf(specificContact.m2001c()));
            if (specificContact.m2002d() != null) {
                contentValues.put("change_date", specificContact.m2002d());
            }
            if (specificContact.m1997a() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_specific_contact", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_specific_contact", contentValues, "_id=" + specificContact.m1997a().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = specificContact.m1997a();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m846a(FavoriteSticker favoriteSticker) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("doc_id", favoriteSticker.m2193c());
            contentValues.put("emoji", favoriteSticker.m2194d());
            contentValues.put("priority", favoriteSticker.m2192b());
            if (favoriteSticker.m2189a() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_favorite_stickers", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_favorite_stickers", contentValues, "_id=" + favoriteSticker.m2189a().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = favoriteSticker.m2189a();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m847a(UpdateModel updateModel) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Long valueOf;
            ContentValues contentValues = new ContentValues();
            contentValues.put("type", Integer.valueOf(updateModel.getType()));
            contentValues.put("old_value", updateModel.getOldValue());
            contentValues.put("new_value", updateModel.getNewValue());
            contentValues.put("user_id", Integer.valueOf(updateModel.getUserId()));
            contentValues.put("is_new", Integer.valueOf(updateModel.isNew() ? 1 : 0));
            if (updateModel.getChangeDate() != null) {
                contentValues.put("change_date", updateModel.getChangeDate());
            }
            if (updateModel.getId() == null) {
                long insertOrThrow = writableDatabase.insertOrThrow("tbl_update", null, contentValues);
                writableDatabase.setTransactionSuccessful();
                valueOf = Long.valueOf(insertOrThrow);
            } else {
                writableDatabase.update("tbl_update", contentValues, "_id=" + updateModel.getId().longValue(), null);
                writableDatabase.setTransactionSuccessful();
                valueOf = updateModel.getId();
                writableDatabase.endTransaction();
            }
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Long m848a(Long l, Long l2) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("categoryId", l);
            contentValues.put("dialogId", l2);
            long insertOrThrow = writableDatabase.insertOrThrow("tbl_cat_dlg_info", null, contentValues);
            writableDatabase.setTransactionSuccessful();
            Long valueOf = Long.valueOf(insertOrThrow);
            return valueOf;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public List<Favorite> m849a(String str) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<Favorite> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_favorite", null, str, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    arrayList.add(m863c(query));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public List<Category> m850a(String str, boolean z) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<Category> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_category", null, str, null, null, null, "priority");
            while (query.moveToNext()) {
                try {
                    Category e = m873e(query);
                    if (z) {
                        e.m284d().addAll(m875e(e.m276a()));
                    }
                    arrayList.add(e);
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public void m851a() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.putNull("is_new");
            writableDatabase.update("tbl_update", contentValues, null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m852a(int i) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "user_id = " + i;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_update", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m853a(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_update", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m854a(Long l, Integer num) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "org_dialog_id = " + l + " and " + "org_message_id" + " = " + num;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_arch_msg_info", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m855a(List<Integer> list) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String join = TextUtils.join(", ", list);
        writableDatabase.beginTransaction();
        try {
            writableDatabase.execSQL(String.format("DELETE FROM tbl_arch_msg_info WHERE message_id IN (%s);", new Object[]{join}));
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public AlarmResponse m856b(int i) {
        Throwable th;
        Cursor cursor = null;
        try {
            Cursor query = this.f873a.getReadableDatabase().query("tbl_alarm", null, "targetVersion = " + i, null, null, null, "_id");
            try {
                if (query.moveToLast()) {
                    AlarmResponse b = m857b(query);
                    if (query == null) {
                        return b;
                    }
                    query.close();
                    return b;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public AlarmResponse m857b(Cursor cursor) {
        boolean z;
        Long valueOf = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
        String string = cursor.getString(cursor.getColumnIndex("title"));
        String string2 = cursor.getString(cursor.getColumnIndex("message"));
        String string3 = cursor.getString(cursor.getColumnIndex("imageUrl"));
        String string4 = cursor.getString(cursor.getColumnIndex("positiveBtnText"));
        String string5 = cursor.getString(cursor.getColumnIndex("positiveBtnAction"));
        String string6 = cursor.getString(cursor.getColumnIndex("positiveBtnUrl"));
        String string7 = cursor.getString(cursor.getColumnIndex("negativeBtnText"));
        String string8 = cursor.getString(cursor.getColumnIndex("negativeBtnAction"));
        String string9 = cursor.getString(cursor.getColumnIndex("negativeBtnUrl"));
        int i = cursor.getInt(cursor.getColumnIndex("showCount"));
        if (cursor.isNull(cursor.getColumnIndex("exitOnDismiss"))) {
            z = false;
        } else {
            z = cursor.getLong(cursor.getColumnIndex("exitOnDismiss")) > 0;
        }
        return new AlarmResponse(valueOf, string, string2, string3, string4, string5, string6, string7, string8, string9, Integer.valueOf(i), Boolean.valueOf(z), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("targetNetwork"))), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("displayCount"))), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("targetVersion"))));
    }

    public List<Hidden> m858b(String str) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<Hidden> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_hidden", null, str, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    arrayList.add(m869d(query));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public void m859b() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_update", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m860b(long j) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "dialog_id = " + j;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_dialog_dm", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m861b(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "chatID = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_favorite", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public int m862c() {
        Cursor query;
        Throwable th;
        try {
            query = this.f873a.getReadableDatabase().query("tbl_update", null, "is_new=1", null, null, null, "_id");
            try {
                int count = query.getCount();
                if (query != null) {
                    query.close();
                }
                return count;
            } catch (Throwable th2) {
                th = th2;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public Favorite m863c(Cursor cursor) {
        return new Favorite(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), Long.valueOf(cursor.getLong(cursor.getColumnIndex("chatID"))));
    }

    public SpecificContact m864c(int i) {
        List d = m871d("user_id=" + i);
        return d.size() == 1 ? (SpecificContact) d.get(0) : null;
    }

    public List<FavoriteSticker> m865c(String str) {
        Cursor query;
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<FavoriteSticker> arrayList = new ArrayList();
        try {
            query = readableDatabase.query("tbl_favorite_stickers", null, str, null, null, null, "priority");
            while (query.moveToNext()) {
                try {
                    arrayList.add(m877f(query));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public void m866c(long j) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("archive_id", Integer.valueOf(-1));
            writableDatabase.update("tbl_arch_msg_info", contentValues, "archive_id=" + j, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m867c(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "dialogID = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_hidden", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public ArchiveMessageInfo m868d(int i) {
        List h = m888h("message_id=" + i);
        return h.size() > 0 ? (ArchiveMessageInfo) h.get(0) : null;
    }

    public Hidden m869d(Cursor cursor) {
        return new Hidden(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), Long.valueOf(cursor.getLong(cursor.getColumnIndex("dialogID"))));
    }

    public List<Favorite> m870d() {
        return m849a(null);
    }

    public List<SpecificContact> m871d(String str) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<SpecificContact> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_specific_contact", null, str, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    arrayList.add(m881g(query));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public void m872d(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_category", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Category m873e(Cursor cursor) {
        return new Category(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), cursor.getString(cursor.getColumnIndex("name")), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("priority"))));
    }

    public List<Hidden> m874e() {
        return m858b(null);
    }

    public List<Long> m875e(Long l) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<Long> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_cat_dlg_info", null, "categoryId=" + l, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    arrayList.add(Long.valueOf(query.getLong(query.getColumnIndex("dialogId"))));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public List<DialogDm> m876e(String str) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<DialogDm> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_dialog_dm", null, str, null, null, null, "priority");
            while (query.moveToNext()) {
                try {
                    arrayList.add(m886h(query));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public FavoriteSticker m877f(Cursor cursor) {
        return new FavoriteSticker(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), Long.valueOf(cursor.getLong(cursor.getColumnIndex("doc_id"))), cursor.getString(cursor.getColumnIndex("emoji")), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("priority"))));
    }

    public List<DialogSettings> m878f(String str) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<DialogSettings> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_dialog_settings", null, str, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    arrayList.add(m890i(query));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public void m879f() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_hidden", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m880f(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "dialogId = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_cat_dlg_info", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public SpecificContact m881g(Cursor cursor) {
        return new SpecificContact(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), cursor.getInt(cursor.getColumnIndex("user_id")), cursor.getInt(cursor.getColumnIndex("change_type")), Long.valueOf(cursor.getLong(cursor.getColumnIndex("change_date"))));
    }

    public List<Category> m882g() {
        return m850a(null, false);
    }

    public List<Archive> m883g(String str) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<Archive> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_archive", null, str, null, null, null, "priority");
            while (query.moveToNext()) {
                try {
                    Archive j = m892j(query);
                    j.m210d().addAll(m910r(j.m204a()));
                    arrayList.add(j);
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public boolean m884g(Long l) {
        return m887h(l) != null;
    }

    public int m885h() {
        Cursor query;
        Throwable th;
        try {
            query = this.f873a.getReadableDatabase().query("tbl_category", null, null, null, null, null, "_id");
            try {
                int count = query.getCount();
                if (query != null) {
                    query.close();
                }
                return count;
            } catch (Throwable th2) {
                th = th2;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public DialogDm m886h(Cursor cursor) {
        Long valueOf = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
        long j = cursor.getLong(cursor.getColumnIndex("dialog_id"));
        return new DialogDm(valueOf, Long.valueOf(j), cursor.getInt(cursor.getColumnIndex("doc_type")), cursor.getInt(cursor.getColumnIndex("message_count")), cursor.getInt(cursor.getColumnIndex("priority")), Long.valueOf(cursor.getLong(cursor.getColumnIndex("change_date"))));
    }

    public Long m887h(Long l) {
        Throwable th;
        Cursor cursor = null;
        try {
            Cursor query = this.f873a.getReadableDatabase().query("tbl_cat_dlg_info", null, "dialogId = " + l, null, null, null, "_id");
            try {
                if (query.moveToNext()) {
                    Long valueOf = Long.valueOf(query.getLong(query.getColumnIndex("categoryId")));
                    if (query == null) {
                        return valueOf;
                    }
                    query.close();
                    return valueOf;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public List<ArchiveMessageInfo> m888h(String str) {
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<ArchiveMessageInfo> arrayList = new ArrayList();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_arch_msg_info", null, str, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    arrayList.add(m895k(query));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public Category m889i(Long l) {
        Cursor rawQuery;
        Throwable th;
        Category category = null;
        try {
            rawQuery = this.f873a.getReadableDatabase().rawQuery("select cat.* from tbl_category cat join tbl_cat_dlg_info info on (cat._id = info.categoryId) where info.dialogId=" + l, null);
            try {
                if (rawQuery.moveToNext()) {
                    category = m873e(rawQuery);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                } else if (rawQuery != null) {
                    rawQuery.close();
                }
                return category;
            } catch (Throwable th2) {
                th = th2;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            Throwable th4 = th3;
            rawQuery = null;
            th = th4;
            if (rawQuery != null) {
                rawQuery.close();
            }
            throw th;
        }
    }

    public DialogSettings m890i(Cursor cursor) {
        Long valueOf = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
        long j = cursor.getLong(cursor.getColumnIndex("dialog_id"));
        int i = cursor.getInt(cursor.getColumnIndex("auto_dl_mask"));
        int i2 = cursor.getInt(cursor.getColumnIndex("background"));
        boolean z = cursor.isNull(cursor.getColumnIndex("hide_typing_state")) ? false : cursor.getLong(cursor.getColumnIndex("hide_typing_state")) > 0;
        boolean z2 = cursor.isNull(cursor.getColumnIndex("not_send_read_state")) ? false : cursor.getLong(cursor.getColumnIndex("not_send_read_state")) > 0;
        return new DialogSettings(valueOf, Long.valueOf(j), i, i2, z, z2, cursor.getInt(cursor.getColumnIndex("marker")), Long.valueOf(cursor.getLong(cursor.getColumnIndex("change_date"))));
    }

    public List<Category> m891i() {
        return m850a(null, true);
    }

    public Archive m892j(Cursor cursor) {
        return new Archive(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), cursor.getString(cursor.getColumnIndex("name")), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("priority"))));
    }

    public void m893j() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_category", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m894j(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_favorite_stickers", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public ArchiveMessageInfo m895k(Cursor cursor) {
        return new ArchiveMessageInfo(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("message_id"))), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("org_message_id"))), Long.valueOf(cursor.getLong(cursor.getColumnIndex("org_dialog_id"))), Long.valueOf(cursor.getLong(cursor.getColumnIndex("archive_id"))));
    }

    public List<Long> m896k() {
        Cursor query;
        Throwable th;
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        List<Long> arrayList = new ArrayList();
        try {
            query = readableDatabase.query("tbl_cat_dlg_info", null, null, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    arrayList.add(Long.valueOf(query.getLong(query.getColumnIndex("dialogId"))));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public void m897k(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "doc_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_favorite_stickers", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public List<FavoriteSticker> m898l() {
        return m865c("doc_id is not null");
    }

    public void m899l(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_specific_contact", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public List<FavoriteSticker> m900m() {
        return m865c("emoji is not null");
    }

    public void m901m(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_dialog_dm", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m902n() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "emoji is not null";
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_favorite_stickers", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m903n(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_dialog_settings", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Archive m904o(Long l) {
        List g = m883g("_id=" + l);
        return g.size() > 0 ? (Archive) g.get(0) : null;
    }

    public void m905o() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_specific_contact_log", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m906p() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_specific_contact", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m907p(Long l) {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        String str = "_id = " + l;
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_archive", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public ArchiveMessageInfo m908q(Long l) {
        List h = m888h("_id=" + l);
        return h.size() > 0 ? (ArchiveMessageInfo) h.get(0) : null;
    }

    public List<SpecificContact> m909q() {
        return m871d(null);
    }

    public List<ArchiveMessageInfo> m910r(Long l) {
        return m888h("archive_id=" + l);
    }

    public void m911r() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_dialog_dm", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public List<DialogDm> m912s() {
        return m876e(null);
    }

    public void m913t() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_dialog_settings", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public List<DialogSettings> m914u() {
        return m878f(null);
    }

    public List<Archive> m915v() {
        return m883g("_id> 0");
    }

    public void m916w() {
        String str = "_id > 0";
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_archive", str, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void m917x() {
        SQLiteDatabase writableDatabase = this.f873a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("tbl_arch_msg_info", null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public HashSet<Integer> m918y() {
        Throwable th;
        String str = "archive_id > 0";
        SQLiteDatabase readableDatabase = this.f873a.getReadableDatabase();
        HashSet<Integer> hashSet = new HashSet();
        Cursor query;
        try {
            query = readableDatabase.query("tbl_arch_msg_info", null, str, null, null, null, "_id");
            while (query.moveToNext()) {
                try {
                    hashSet.add(Integer.valueOf(query.getInt(query.getColumnIndex("message_id"))));
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (query != null) {
                query.close();
            }
            return hashSet;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public List<ArchiveMessageInfo> m919z() {
        return m888h(null);
    }
}
