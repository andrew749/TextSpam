package com.andrew749.textspam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MessageReciever extends BroadcastReceiver {
    /**
     * action is .sendmessages
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        int frequency;
        ArrayList<Custom> item;
        String message;
        Bundle information = intent.getExtras().getBundle("information");
        frequency = information.getInt("freq");
        message = information.getString("message");
        item = (ArrayList<Custom>) information.getSerializable("contact");
        Log.d("Success", "got itmes");
        new SendMessagesTask(item, frequency, message).execute();

    }
}
