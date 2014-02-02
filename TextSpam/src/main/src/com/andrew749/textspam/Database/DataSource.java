package com.andrew749.textspam.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 08/12/13.
 */
public class DataSource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_MESSAGE, DatabaseHelper.COLUMN_RECIPIENTS};

    public DataSource(Context context) {

        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createConversation(String message, ArrayList<String> numbers) {
        String recipientsString = "";
        for (int i = 0; i < numbers.size(); i++) {
            if (i < numbers.size() - 1) {
                recipientsString += numbers.get(i) + ",";

            } else {
                recipientsString += numbers.get(i);
            }
        }
        recipientsString = recipientsString.substring(0, recipientsString.length());
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MESSAGE, message);
        values.put(DatabaseHelper.COLUMN_RECIPIENTS, recipientsString);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        values.clear();
        cursor.close();
    }

    public void deleteConversation(ConversationModel model) {
        long id = model.getId();
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public void deleteConversationID(long id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }
    public void deleteAllConversations(){
    	database.execSQL("delete from "+ DatabaseHelper.TABLE_NAME+" where _id>=0;");
    }
    public List<ConversationModel> getAllConversations() {
        List<ConversationModel> conversations = new ArrayList<ConversationModel>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ConversationModel model = cursorToConversation(cursor);
            conversations.add(model);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return conversations;
    }

    private ConversationModel cursorToConversation(Cursor cursor) {
        ConversationModel model = new ConversationModel();
        try {
            model.setId(cursor.getLong(0));
            model.setSendingString(cursor.getString(1));
            Log.d("cursorToConversation", cursor.getString(2));
            model.setDBResult(cursor.getString(2));
            Log.d("cursorToConversation", "" + model.getPhoneNumbers());
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return model;
    }
   
}
