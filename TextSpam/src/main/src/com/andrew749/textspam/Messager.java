package com.andrew749.textspam;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsManager;

/**
 * Created by andrew on 19/08/13.
 */
public class Messager {
    SmsManager sm;
    PendingIntent intent;
    int failedMessages = 0;
    Context context;

    public Messager(Context context) {
    	this.context=context;
        sm = SmsManager.getDefault();
        intent = PendingIntent.getBroadcast(context, 0,
                new Intent("SMS_SENT"), 0);
    }

    public synchronized void sendMessage(String address, String message) {
        sm.sendTextMessage(address, null, message, intent, null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void sendMessageToContact(String address, String message, int number) {
        for (int i = 0; i < number; i++) {
            sendMessage(address, message);
             //Toast.makeText(context, "Sending Text " + (i + 1) + " of " + number + " to " + address,Toast.LENGTH_SHORT).show();

        }
    }

    public synchronized void sendMessagesToAll(ArrayList<Custom> item, int number, String message) {
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
    Context context;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected SendMessagesTask(ArrayList<Custom> item, int frequency, String message, Context context) {
        this.item = item;
        this.frequency = frequency;
        this.message = message;
        this.context=context;
        messager = new Messager(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        messager.sendMessagesToAll(item, frequency, message);
        return null;
    }

	
    
}
