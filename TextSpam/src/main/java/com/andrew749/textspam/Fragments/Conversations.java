package com.andrew749.textspam.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
                dataSource.deleteConversationID(adapter.getItem((int) l).getId());
                adapter.notifyDataSetChanged();
                return true;
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
}
