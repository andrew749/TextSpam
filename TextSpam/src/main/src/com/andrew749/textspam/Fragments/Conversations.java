package com.andrew749.textspam.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andrew749.textspam.Database.ConversationModel;
import com.andrew749.textspam.Database.DataSource;
import com.andrew749.textspam.R;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by andrew on 12/10/13.
 */
public class Conversations extends Fragment {
    private DataSource dataSource;
    ListView lv;
    ArrayAdapter<ConversationModel> adapter;
    List<ConversationModel> conversations;
    Button popupbutton;
    EditText editfield;
    TextView popuptextview;
    int frequency = 0;
    ConversationModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new DataSource(getActivity());
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conversations = dataSource.getAllConversations();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversationfragment, container, false);
        lv = (ListView) view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<ConversationModel>(getActivity(),
                android.R.layout.simple_list_item_1, conversations);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                dataSource.deleteConversationID(adapter.getItem(i).getId());
                conversations.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                model = new ConversationModel();

                model = conversations.get(i);

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.conversationpopup, null, true);
                PopupWindow pw = new PopupWindow(v, 500, 500,
                        true);
                pw.setBackgroundDrawable(new BitmapDrawable());
                pw.setOutsideTouchable(true);

                pw.showAtLocation(getView().findViewById(R.id.conversationparent), Gravity.CENTER, 0, 0);
                popupbutton = (Button) v.findViewById(R.id.popupbutton);
                editfield = (EditText) v.findViewById(R.id.popupedittext);
                popuptextview = (TextView) v.findViewById(R.id.popuptextview);
                for (String temp : model.getPhoneNumbers()) {
                    popuptextview.append(" " + temp);

                }

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        frequency = Integer.parseInt(editfield.getText().toString());
                        Intent intent = new Intent();
                        intent.setAction("com.andrew749.textspam.sendmessages");
                        Bundle information = new Bundle();
                        information.putString("message", model.getSendingString());
                        information.putInt("freq", frequency);
                        information.putSerializable("contact", model.getNumbersAsCustom());
                        intent.putExtra("information", information);
                        Log.d("Message", model.getSendingString());
                        Log.d("Frequency", "" + frequency);
                        Log.d("Recipient", "" + model.getNumbersAsCustom());
                        getActivity().sendBroadcast(intent);
                    }
                });
            }
        });
        lv.setAdapter(adapter);
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.addconversation).setVisible(false);
    }

    public void onClick() {

    }
}
