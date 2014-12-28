package com.andrew749.textspam.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andrew749.textspam.Custom;
import com.andrew749.textspam.R;

import java.util.ArrayList;

/**
 * Created by andrew on 25/06/13.
 */
public class ContactListAdapter extends ArrayAdapter<Custom> {
    private ArrayList<Custom> entries;
    private Activity ac;

    public ContactListAdapter(Activity a, int textViewResourceId, ArrayList<Custom> list) {
        super(a, textViewResourceId, list);
        entries = list;
        ac = a;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater) ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listitem, parent, false);
            holder = new ViewHolder();
            holder.tv = (TextView) v.findViewById(R.id.numberoption);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();

        }
        Custom numbers = entries.get(position);
        if (numbers != null) {
            holder.tv.setText("" + numbers.getPhoneNumber());
        }
        return v;
    }

    public class ViewHolder {
        TextView tv;
    }
}
