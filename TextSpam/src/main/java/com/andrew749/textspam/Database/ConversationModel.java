package com.andrew749.textspam.Database;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by andrew on 08/12/13.
 */
public class ConversationModel {
    public long id;
    private String sendingString;
    private ArrayList<String> phoneNumbers = new ArrayList<String>();
    private String numberlist;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addphoneNumber(String phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    public String getSendingString() {
        return sendingString;
    }

    public void setSendingString(String input) {
        sendingString = input;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setDBResult(String dbResult) {
        parseDbresult(dbResult);
    }

    public void parseDbresult(String dbResult) {
        String tempString = "";
        numberlist = dbResult;
        for (int i = 0; i < dbResult.length() - 1; i++) {
            if (dbResult.indexOf(",") >= 0) {
                tempString = dbResult.substring(i, dbResult.indexOf(","));
                Log.d("number:", tempString);

                addphoneNumber(tempString);
                i = dbResult.indexOf(",");
            }

        }
    }

    @Override
    public String toString() {
        return sendingString + ": " + numberlist;
    }
}
