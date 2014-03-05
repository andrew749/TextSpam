package com.andrew749.textspam.Adapters;

import com.andrew749.textspam.R;
import com.andrew749.textspam.Fragments.QuickMessageFragment;
import com.google.analytics.tracking.android.Log;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

public class AutoCompleteCursorAdapter extends CursorAdapter implements
		Filterable {
	private ContentResolver mCR;
	private Cursor c, people;

	@SuppressWarnings("deprecation")
	public AutoCompleteCursorAdapter(Context context, Cursor c) {
		super(context, c);
		this.c = c;
		mCR = context.getContentResolver();
	}

	// TODO debug application to see what columns are passed with the cursor
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
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
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
		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}

		StringBuilder buffer = null;
		String[] args = null;
		if (constraint != null) {
			buffer = new StringBuilder();
			buffer.append("UPPER(");
			buffer.append(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
			buffer.append(") GLOB ?");
			args = new String[] { constraint.toString().toUpperCase() + "*" };
		}
		Cursor userdata = mCR.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				QuickMessageFragment.projection,"UPPER(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") LIKE '" + constraint.toString().toUpperCase() + "%'", null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		Log.d("");
		return userdata;
	}

}
