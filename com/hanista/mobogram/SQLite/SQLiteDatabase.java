package com.hanista.mobogram.SQLite;

import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;

public class SQLiteDatabase {
    private boolean inTransaction;
    private boolean isOpen;
    private final int sqliteHandle;

    public SQLiteDatabase(String str) {
        this.isOpen = false;
        this.inTransaction = false;
        this.sqliteHandle = opendb(str, ApplicationLoader.getFilesDirFixed().getPath());
        this.isOpen = true;
    }

    public void beginTransaction() {
        if (this.inTransaction) {
            throw new SQLiteException("database already in transaction");
        }
        this.inTransaction = true;
        beginTransaction(this.sqliteHandle);
    }

    native void beginTransaction(int i);

    void checkOpened() {
        if (!this.isOpen) {
            throw new SQLiteException("Database closed");
        }
    }

    public void close() {
        if (this.isOpen) {
            try {
                commitTransaction();
                closedb(this.sqliteHandle);
            } catch (Throwable e) {
                FileLog.m17e("tmessages", e.getMessage(), e);
            }
            this.isOpen = false;
        }
    }

    native void closedb(int i);

    public void commitTransaction() {
        if (this.inTransaction) {
            this.inTransaction = false;
            commitTransaction(this.sqliteHandle);
        }
    }

    native void commitTransaction(int i);

    public SQLitePreparedStatement executeFast(String str) {
        return new SQLitePreparedStatement(this, str, true);
    }

    public Integer executeInt(String str, Object... objArr) {
        checkOpened();
        SQLiteCursor queryFinalized = queryFinalized(str, objArr);
        try {
            if (!queryFinalized.next()) {
                return null;
            }
            Integer valueOf = Integer.valueOf(queryFinalized.intValue(0));
            queryFinalized.dispose();
            return valueOf;
        } finally {
            queryFinalized.dispose();
        }
    }

    public void finalize() {
        super.finalize();
        close();
    }

    public int getSQLiteHandle() {
        return this.sqliteHandle;
    }

    native int opendb(String str, String str2);

    public SQLiteCursor queryFinalized(String str, Object... objArr) {
        checkOpened();
        return new SQLitePreparedStatement(this, str, true).query(objArr);
    }

    public boolean tableExists(String str) {
        checkOpened();
        return executeInt("SELECT rowid FROM sqlite_master WHERE type='table' AND name=?;", str) != null;
    }
}
