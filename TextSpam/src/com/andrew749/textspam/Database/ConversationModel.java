package com.andrew749.textspam.Database;

import android.util.Log;

import com.andrew749.textspam.Custom;

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
        phoneNumbers = parseDbresult(dbResult);
    }

    public String getNumberString() {
        return numberlist;
    }

    public ArrayList<String> getResult() {
        return phoneNumbers;
    }

    public ArrayList<String> parseDbresult(String dbResult) {

        ArrayList<String> tempmodel = new ArrayList<String>();
        String tempString = "";
        numberlist = dbResult;
        for (int i = 0; i < dbResult.length(); i++) {
            if (dbResult.indexOf(",", i) > 0) {
                tempString = dbResult.substring(i, dbResult.indexOf(",", i));
                Log.d("number:", tempString);

                tempmodel.add(tempString);
                i = dbResult.indexOf(",", i);
            } else {
                tempString = dbResult.substring(i);
                tempmodel.add(tempString);
                i = dbResult.length();
            }
        }
        return tempmodel;
    }

    public ArrayList<Custom> getNumbersAsCustom() {
        ArrayList<Custom> temparray = new ArrayList<Custom>();
        for (String i : phoneNumbers) {
            temparray.add(new Custom(i));

        }
        return temparray;
    }

    @Override
    public String toString() {
        return sendingString + ": " + numberlist;
    }

}
