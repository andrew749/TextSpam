package com.andrew749.textspam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int CONTACT_PICKER_RESULT = 1001;
    public static ArrayList<Custom> item = new ArrayList<Custom>();
    static int itemposition = 0;
    private static int frequency, totalfreq;
    private static String message;
    adapter adapter;
    Intent intent = new Intent();
    EditText phonenumberenter, frequencyenter, messageenter;
    Button add;
    SmsManager sm;
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;
    ListView lv;
    Handler handler = new Handler();

    public void sendMessageContact() {

        for (int i = 0; i < totalfreq; i++) {
            sm.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Sending Text " + (i+1) + " of " + totalfreq + " to " + phoneNumber, Toast.LENGTH_SHORT).show();
            frequency--;
                Log.d("Remaining", frequency + "");


        }


    }

    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        adapter = new adapter(this, R.id.contactlist, item);
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
        initializeAlert();


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
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);


        // Setting Dialog Title
        alertDialogBuilder.setTitle("Warning");

        // Setting Dialog Message
        alertDialogBuilder.setMessage("The built in contact picker will currently only return results properly for " +
                "North American numbers, please adjust the number accordingly for the time being if you are texting " +
                "any other region" +
                ".");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void gatherInformation() {
        try {
            message = messageenter.getText().toString();
            frequency = Integer.parseInt(frequencyenter.getText()
                    .toString());
            totalfreq = frequency;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Sorry but the fields are not entered correctly",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeAlert() {
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);


        // Setting Dialog Title
        alertDialogBuilder.setTitle("Warning");

        // Setting Dialog Message
        alertDialogBuilder.setMessage("NOTE: Most Android phones cannot handle more than 30 messages sent at " +
                "one time, Android has a built in mechanism to limit the number of texts.");

        // Setting OK Button
        alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendMessagesToAll();


            }
        });
        alertDialogBuilder.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog = alertDialogBuilder.create();

    }

    private void sendMessagesToAll() {
        for (int i = 0; i < item.size(); i++) {
            phoneNumber = ((item.get(i)).getPhoneNumber()) + "";
            sendMessageContact();
        }
    }

    private void ClearAlert() {
        item.clear();
        adapter.notifyDataSetChanged();
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);


        // Setting Dialog Title
        alertDialogBuilder.setTitle("Notice");

        // Setting Dialog Message
        alertDialogBuilder.setMessage("If you want to only remove one item just long click on the item.");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("Success", "cleared all entries");
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

            initializeAlert();
            alertDialog.show();
        } else {
            sendMessagesToAll();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    public void  ChangedAlert(){
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);


        // Setting Dialog Title
        alertDialogBuilder.setTitle("What's Changed");
       // possible added changes view changedview view=new changedview(getApplicationContext());
        // Setting Dialog Message
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                ClearAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
