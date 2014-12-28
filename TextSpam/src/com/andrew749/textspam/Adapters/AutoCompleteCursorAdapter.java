package com.andrew749.textspam.Adapters;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.andrew749.textspam.R;

public class AutoCompleteCursorAdapter extends CursorAdapter implements
        Filterable {
    public static final String[] projection = {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE};
    private ContentResolver mCR;
    private Cursor c, people;

    @SuppressWarnings("deprecation")
    public AutoCompleteCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.c = c;
        mCR = context.getContentResolver();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String nameString = cursor
                .getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        TextView name = (TextView) view.findViewById(R.id.ccontName);
        TextView number = (TextView) view.findViewById(R.id.ccontNo);
        name.setText(nameString);
        String idstring = cursor.getString(cursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
        if (Integer
                .parseInt(cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER))) > 0) {

            String Phone = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number.setText(Phone);

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.customcontactview, null);

        return v;
    }

    @Override
    public String convertToString(Cursor cursor) {

        return cursor.getString(1);
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Log.d("TextSpam", "runningquery");
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }
        Cursor userdata;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            userdata = mCR
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            projection,
                            "UPPER("
                                    + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                    + ") LIKE '"
                                    + constraint.toString().toUpperCase()
                                    + "%'", null,
                            ContactsContract.Contacts.DISPLAY_NAME
                                    + " COLLATE LOCALIZED ASC");
        } else {
            userdata = mCR
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            projection,
                            "UPPER("
                                    + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                    + ") LIKE '"
                                    + constraint.toString().toUpperCase()
                                    + "%'", null,
                            ContactsContract.Contacts.DISPLAY_NAME
                                    + " COLLATE LOCALIZED ASC");
        }
        return userdata;
    }
}
