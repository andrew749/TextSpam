package com.andrew749.textspam;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by andrew on 11/10/13.
 */
public class drawer_item_click_listener implements ListView.OnItemClickListener {

    /*
    0 will be quick message
    1 conversations
    2
     */
    Intent i;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

}
