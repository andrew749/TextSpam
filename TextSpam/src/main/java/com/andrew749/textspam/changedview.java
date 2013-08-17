package com.andrew749.textspam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by andrew on 08/08/13.
 */
public class changedview extends LinearLayout {
    public changedview(Context context, int resourceId) {
        super(context);
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(resourceId,this);
    }

}
