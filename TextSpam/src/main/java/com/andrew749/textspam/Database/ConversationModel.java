package com.andrew749.textspam.Database;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by andrew on 08/12/13.
 */
public class ConversationModel {
    private String sendingString;
    private ArrayList<String> phoneNumbers = new ArrayList<String>();
    public long id;

    public void setSendingString(String input) {
        sendingString = input;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void addphoneNumber(String phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    public String getSendingString() {
        return sendingString;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setDBResult(String dbResult) {
        parseDbresult(dbResult);
    }

    public void parseDbresult(String dbResult) {
        String tempString;
        for (int i = 0; i < dbResult.length(); i++) {
            tempString = dbResult.substring(i, dbResult.indexOf(","));
            Log.d("number:", tempString);

            addphoneNumber(tempString);
            i = dbResult.indexOf(",");
        }
    }
}
