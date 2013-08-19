package com.andrew749.textspam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int CONTACT_PICKER_RESULT = 1001;
    public static ArrayList<Custom> item = new ArrayList<Custom>();
    static int itemposition = 0;
    private static int frequency;
    private static String message;
    Adapter adapter;
    Intent intent = new Intent();
    EditText phonenumberenter, frequencyenter, messageenter;
    Button add;
    SmsManager sm;
    Messager messager;
    ListView lv;
    Alerts alert;
 String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         messager=new Messager();
         alert=new Alerts(this);
        final String PREFS_NAME = "MyPrefsFile";


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        intent.setClass(getApplicationContext(), TutorialActivity.class);

        if (settings.getBoolean("my_first_time", true)) {

            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task is run
            startActivity(intent);


            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();


        }


        lv = (ListView) findViewById(R.id.contactlist);
        adapter = new Adapter(this, R.id.contactlist, item);
        lv.setAdapter(adapter);
        //where the user enters the message qualiites
        phonenumberenter = (EditText) findViewById(R.id.numberedit);
        frequencyenter = (EditText) findViewById(R.id.frequencyedit);
        messageenter = (EditText) findViewById(R.id.messageedit);
        lv.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                item.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });


        sm = SmsManager.getDefault();


        //add contact to list
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String phone = "";
        Cursor contacts = null;
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CONTACT_PICKER_RESULT:
                        // gets the uri of selected contact
                        Uri result = data.getData();
                        // get the contact id from the Uri (last part is contact id)
                        String id = result.getLastPathSegment();
                        //queries the contacts DB for phone no
                        contacts = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                        //gets index of phone no
                        int phoneIdx = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                        if (contacts.moveToFirst()) {
                            //gets the phone no
                            phone = contacts.getString(phoneIdx);
                            phone = phone.substring(1, phone.length());
                            //assigns phone no to EditText field phoneno
                            phonenumberenter.setText(phone);
                        } else {
                            Toast.makeText(this, "error", 100).show();
                        }
                        break;
                }

            } else {
                // gracefully handle failure
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), 50).show();
        } finally {
            if (contacts != null) {
                contacts.close();
            }
        }
    }

    public void doLaunchContactPicker(View view) {
        alert.contactAlert();
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    public void gatherInformation() {
        try {
            message = messageenter.getText().toString();
            frequency = Integer.parseInt(frequencyenter.getText()
                    .toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Sorry but the fields are not entered correctly",
                    Toast.LENGTH_SHORT).show();
        }
    }





    private void addItem() {
        try {
            item.add(new Custom(phonenumberenter.getText().toString()));
            Log.d("Success", "added phone number " + item.get(itemposition).getPhoneNumber());

            itemposition++;
            for (int i = 0; i < itemposition; i++) {
                Log.d("entry:", "Entry " + i + " == " + item.get(i).getPhoneNumber());
            }
            adapter.notifyDataSetChanged();
            phonenumberenter.setText("");
        } catch (Exception e) {
        }
    }

    public void sendMessagesComplete() {
        gatherInformation();
        if (frequency > 30) {

            if(alert.warningAlert()){
messager.sendMessagesToAll(item,frequency,message);
            }else {
                //when stop is clicked
            }

        } else {
            messager.sendMessagesToAll(item,frequency,message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }



    public void dataParser() {
        ArrayList<Version> changes = new ArrayList<Version>();
        //a loop to go over the elements and add them to the list

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tutorial_menu:
                startActivity(intent);
                return true;
            case R.id.sendmessage:
                sendMessagesComplete();
                return true;
            case R.id.clearmessage:
                alert.clearAlerts(this.item);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.changes:
                alert.changedAlert();
                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
