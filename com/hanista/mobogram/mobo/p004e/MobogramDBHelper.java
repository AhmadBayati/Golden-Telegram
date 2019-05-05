package com.hanista.mobogram.mobo.p004e;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.mobo.MoboUtils;

/* renamed from: com.hanista.mobogram.mobo.e.b */
public class MobogramDBHelper extends SQLiteOpenHelper {
    public MobogramDBHelper(Context context) {
        super(context, MoboUtils.m1712c(context) + ".db", null, MoboUtils.m1692a(context));
    }

    private void m920a(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_update ( _id integer primary key autoincrement, type integer,old_value text,new_value text,user_id integer,is_new integer,change_date integer default (strftime('%s','now') * 1000))");
    }

    private void m921b(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_specific_contact ( _id integer primary key autoincrement, user_id integer,change_type integer,change_date integer default (strftime('%s','now') * 1000))");
    }

    private void m922c(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_specific_contact_log ( _id integer primary key autoincrement, type integer,old_value text,new_value text,user_id integer,is_new integer,change_date integer default (strftime('%s','now') * 1000))");
    }

    private void m923d(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_dialog_dm ( _id integer primary key autoincrement, dialog_id integer,doc_type integer,message_count integer,priority integer,change_date integer default (strftime('%s','now') * 1000))");
    }

    private void m924e(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_dialog_settings ( _id integer primary key autoincrement, dialog_id integer,auto_dl_mask integer,background integer,hide_typing_state integer,not_send_read_state integer,marker integer,change_date integer default (strftime('%s','now') * 1000))");
    }

    private void m925f(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_setting ( _id integer primary key autoincrement, key text, value text)");
        sQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (1,'notifyChanges','true')");
        sQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (2,'notifyNameChanges','true')");
        sQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (3,'notifyStatusChanges','true')");
        sQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (4,'notifyPhotoChanges','true')");
        sQLiteDatabase.execSQL("INSERT INTO tbl_setting VALUES (5,'notifyPhoneChanges','true')");
    }

    private void m926g(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_alarm ( _id integer primary key autoincrement, title text,message text,imageUrl text,positiveBtnText text,positiveBtnAction text,positiveBtnUrl text,negativeBtnText text,negativeBtnAction text,negativeBtnUrl text,showCount integer,exitOnDismiss integer,targetNetwork integer,displayCount integer,targetVersion integer)");
    }

    private void m927h(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_favorite ( _id integer primary key autoincrement, chatID integer)");
    }

    private void m928i(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_hidden ( _id integer primary key autoincrement, dialogID integer)");
    }

    private void m929j(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_category ( _id integer primary key autoincrement, name text,priority integer)");
    }

    private void m930k(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_cat_dlg_info ( _id integer primary key autoincrement, dialogId integer,categoryId integer, foreign key( categoryId ) references tbl_category ( _id ) ON DELETE CASCADE )");
    }

    private void m931l(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TRIGGER trg_category_priority_from_id AFTER INSERT ON tbl_category FOR EACH ROW  WHEN NEW.priority IS NULL  BEGIN  UPDATE tbl_category SET priority= NEW._id WHERE rowid = NEW.rowid;END;");
    }

    private void m932m(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_favorite_stickers ( _id integer primary key autoincrement, doc_id integer,emoji text,priority integer)");
    }

    private void m933n(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TRIGGER trg_dialogdm_priority_from_id AFTER INSERT ON tbl_dialog_dm FOR EACH ROW  WHEN NEW.priority IS NULL  BEGIN  UPDATE tbl_dialog_dm SET priority= NEW._id WHERE rowid = NEW.rowid;END;");
    }

    private void m934o(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TRIGGER trg_fav_stickers_priority_from_id AFTER INSERT ON tbl_favorite_stickers FOR EACH ROW  WHEN NEW.priority IS NULL  BEGIN  UPDATE tbl_favorite_stickers SET priority= NEW._id WHERE rowid = NEW.rowid;END;");
    }

    private void m935p(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_archive ( _id integer primary key autoincrement, name text,priority integer)");
        sQLiteDatabase.execSQL("INSERT INTO tbl_archive VALUES (-1,'No Archive', 99999999)");
    }

    private void m936q(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table tbl_arch_msg_info ( _id integer primary key autoincrement, message_id integer,org_message_id integer, org_dialog_id integer, archive_id integer, change_date integer default (strftime('%s','now') * 1000) )");
    }

    private void m937r(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TRIGGER trg_archive_priority_from_id AFTER INSERT ON tbl_archive FOR EACH ROW  WHEN NEW.priority IS NULL  BEGIN  UPDATE tbl_archive SET priority= NEW._id WHERE rowid = NEW.rowid;END;");
    }

    boolean m938a(SQLiteDatabase sQLiteDatabase, String str) {
        boolean z = true;
        if (str == null || sQLiteDatabase == null || !sQLiteDatabase.isOpen()) {
            return false;
        }
        Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", str});
        if (!rawQuery.moveToFirst()) {
            return false;
        }
        int i = rawQuery.getInt(0);
        rawQuery.close();
        if (i <= 0) {
            z = false;
        }
        return z;
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        m920a(sQLiteDatabase);
        m925f(sQLiteDatabase);
        m926g(sQLiteDatabase);
        m927h(sQLiteDatabase);
        m928i(sQLiteDatabase);
        m929j(sQLiteDatabase);
        m930k(sQLiteDatabase);
        m931l(sQLiteDatabase);
        m932m(sQLiteDatabase);
        m934o(sQLiteDatabase);
        m921b(sQLiteDatabase);
        m922c(sQLiteDatabase);
        m923d(sQLiteDatabase);
        m933n(sQLiteDatabase);
        m924e(sQLiteDatabase);
        m935p(sQLiteDatabase);
        m936q(sQLiteDatabase);
        m937r(sQLiteDatabase);
    }

    public void onOpen(SQLiteDatabase sQLiteDatabase) {
        super.onOpen(sQLiteDatabase);
        if (!sQLiteDatabase.isReadOnly()) {
            sQLiteDatabase.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        int i3 = i + 1;
        if (i3 == 1) {
            i3++;
        }
        if (i3 <= 65420) {
            i3 = 65421;
        }
        if (i3 == 65421) {
            i3++;
            m926g(sQLiteDatabase);
        }
        if (i3 == 65422) {
            i3++;
        }
        if (i3 == 65423) {
            i3++;
        }
        if (i3 == 65424) {
            i3++;
            sQLiteDatabase.execSQL("drop table tbl_alarm");
            m926g(sQLiteDatabase);
            m927h(sQLiteDatabase);
        }
        if (i3 <= 68528) {
            i3 = 68529;
        }
        if (i3 == 68529) {
            i3++;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            if (sharedPreferences.getInt("default_tab", 0) == 2) {
                sharedPreferences.edit().putInt("default_tab", 7).commit();
            }
        }
        if (i3 <= 71944) {
            i3 = 71945;
        }
        if (i3 == 71945) {
            i3++;
            m928i(sQLiteDatabase);
        }
        if (i3 <= 71955) {
            i3 = 71956;
        }
        if (i3 == 71956) {
            i3++;
            m929j(sQLiteDatabase);
            m930k(sQLiteDatabase);
            m931l(sQLiteDatabase);
        }
        if (i3 == 71957) {
            i3++;
            m932m(sQLiteDatabase);
            m934o(sQLiteDatabase);
        }
        if (i3 <= 71963) {
            i3 = 71964;
        }
        if (i3 == 71964) {
            i3++;
            if (!m938a(sQLiteDatabase, "tbl_category")) {
                m929j(sQLiteDatabase);
                m930k(sQLiteDatabase);
                m931l(sQLiteDatabase);
            }
            if (!m938a(sQLiteDatabase, "tbl_favorite_stickers")) {
                m932m(sQLiteDatabase);
                m934o(sQLiteDatabase);
            }
        }
        if (i3 <= 76795) {
            i3 = 76796;
        }
        if (i3 == 76796) {
            i3++;
            m921b(sQLiteDatabase);
            m922c(sQLiteDatabase);
        }
        if (i3 <= 80332) {
            i3 = 80333;
        }
        if (i3 == 80333) {
            i3++;
            m923d(sQLiteDatabase);
            m933n(sQLiteDatabase);
        }
        if (i3 == 80334) {
            i3++;
        }
        if (i3 == 80335) {
            i3++;
            m924e(sQLiteDatabase);
        }
        if (i3 == 80336) {
            i3++;
            sQLiteDatabase.execSQL("ALTER TABLE tbl_favorite_stickers ADD COLUMN emoji text");
        }
        if (i3 <= 82181) {
            i3 = 82182;
        }
        if (i3 == 82182) {
            i3++;
            m935p(sQLiteDatabase);
            m936q(sQLiteDatabase);
            m937r(sQLiteDatabase);
        }
    }
}
