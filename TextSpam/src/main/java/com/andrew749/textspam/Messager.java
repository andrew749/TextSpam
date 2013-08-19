package com.andrew749.textspam;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by andrew on 19/08/13.
 */
public class Messager {
    SmsManager sm;
    Context context;
    public void Messager(Context context){
        sm=SmsManager.getDefault();
        this.context=context;
    }
    public void sendMessage(String address, String message){
        sm.sendTextMessage(address,null,message,null,null);
    }
    public void sendMessageToContact(String address, String message,int number){
        for (int i=0;i<number;i++){
            sendMessage(address,message);
            Toast.makeText(context, "Sending Text " + (i + 1) + " of " + number+ " to " + address, Toast.LENGTH_SHORT).show();

        }
    }
    public void sendMessagesToAll(ArrayList<Custom> item, int number, String message){
        for (int i = 0; i < item.size(); i++) {
            sendMessageToContact(item.get(i).getPhoneNumber().toString(),message,number);
        }
    }
}
