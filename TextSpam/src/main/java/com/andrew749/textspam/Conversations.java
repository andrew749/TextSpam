package com.andrew749.textspam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.andrew749.textspam.Database.ConversationModel;
import com.andrew749.textspam.Database.DataSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by andrew on 12/10/13.
 */
public class Conversations extends Fragment {
    private DataSource dataSource;
    ListView lv;
    ArrayAdapter<ConversationModel> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new DataSource(getActivity());
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<ConversationModel> conversations = dataSource.getAllConversations();
        adapter = new ArrayAdapter<ConversationModel>(getActivity(),
                android.R.layout.simple_list_item_1, conversations);
        lv.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversationfragment, container, false);
        lv = (ListView) view.findViewById(R.id.listView);
        return view;
    }
}
