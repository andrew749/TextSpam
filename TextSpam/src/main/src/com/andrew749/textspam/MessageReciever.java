package com.andrew749.textspam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

public class MessageReciever extends BroadcastReceiver {
    /**
     * action is .sendmessages
     *
     * @param context
     * @param intent
     */
    Object[] messages;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("resend") != null) {
            //if the message is one being resent
            ArrayList<Custom> item = new ArrayList<Custom>();
            Log.d("Error", "some messages error .... resending");
            try {
                messages = (Object[]) intent.getBundleExtra("unsent").get("pdus");
                SmsMessage[] sms = new SmsMessage[messages.length];
                //iterate over all the messages that were unsent
                for (int i = 0; i < messages.length; i++) {
                    sms[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
                    Log.d("Error recipient ", "" + sms[i].getOriginatingAddress());
                    item.add(new Custom(sms[i].getOriginatingAddress()));
                    new SendMessagesTask(item, 1, sms[i].getMessageBody(), context);
                    //clear the array as to not waste memory
                    item.clear();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            //if the message isnt being resent
            int frequency;
            ArrayList<Custom> item;
            String message;
            Bundle information = intent.getExtras().getBundle("information");
            frequency = information.getInt("freq");
            message = information.getString("message");
            item = (ArrayList<Custom>) information.getSerializable("contact");
            Log.d("Success", "got items");
            new SendMessagesTask(item, frequency, message, context).execute();

        }
    }
}
