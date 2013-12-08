package com.andrew749.textspam;

import android.app.PendingIntent;
import android.os.AsyncTask;
import android.telephony.SmsManager;

import java.util.ArrayList;

/**
 * Created by andrew on 19/08/13.
 */
public class Messager {
    SmsManager sm;
    PendingIntent intent;

    public Messager() {
        sm = SmsManager.getDefault();
    }

    public void sendMessage(String address, String message) {
        try {
            sm.sendTextMessage(address, null, message, intent, null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }

    }

    //TODO figure out way to log failures
    public void sendMessageToContact(String address, String message, int number) {
        for (int i = 0; i < number; i++) {
            sendMessage(address, message);
            // Toast.makeText(context, "Sending Text " + (i + 1) + " of " + number + " to " + address,
            //Toast.LENGTH_SHORT).show();

        }
    }

    public void sendMessagesToAll(ArrayList<Custom> item, int number, String message) {
        for (int i = 0; i < item.size(); i++) {
            sendMessageToContact(item.get(i).getPhoneNumber().toString(), message, number);
        }
    }
}

//tast to run in background and send messages
class SendMessagesTask extends AsyncTask<Void, Void, Void> {
    ArrayList<Custom> item;
    int frequency;
    String message;
    Messager messager;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    protected SendMessagesTask(ArrayList<Custom> item, int frequency, String message) {
        this.item = item;
        this.frequency = frequency;
        this.message = message;
        messager = new Messager();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        messager.sendMessagesToAll(item, frequency, message);
        return null;
    }
}
