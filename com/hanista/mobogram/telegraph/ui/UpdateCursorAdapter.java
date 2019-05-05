package com.hanista.mobogram.telegraph.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;

public class UpdateCursorAdapter extends CursorAdapter {
    private DataBaseAccess dataBaseAccess;

    public UpdateCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.dataBaseAccess = new DataBaseAccess();
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ((UpdateCell) view).setData(this.dataBaseAccess.m836a(cursor));
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return new UpdateCell(this.mContext, 10);
    }
}
