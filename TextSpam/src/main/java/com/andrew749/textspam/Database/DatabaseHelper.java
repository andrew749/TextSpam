package com.andrew749.textspam.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by andrew on 01/07/13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "conversations";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_RECIPIENTS = "RECIPIENTS";
    private static final String DATABASE_NAME = "conversations.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table" + TABLE_NAME + "(" + COLUMN_ID + " integer primary key " +
            "autoincrement, " + COLUMN_MESSAGE + " text not null, " + COLUMN_RECIPIENTS + " text not null " + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.d(DatabaseHelper.class.getName(), "Upgrading database from version " + i + " to " + i2 + ", " +
                "which will destroy all data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

