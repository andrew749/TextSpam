package com.andrew749.textspam;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.andrew749.textspam.Adapters.ContactListAdapter;

import java.util.ArrayList;

public class MainActivity extends Activity {
    /**
     * This is the main class where all the methods are interconnected
     */
    private static final int CONTACT_PICKER_RESULT = 1001;
    public static ArrayList<Custom> item = new ArrayList<Custom>();
    static int item_position = 0;
    static Alerts alert;
    private static int frequency;
    private static String message;
    ContactListAdapter contactListAdapter;
    Intent intent = new Intent();
    EditText phonenumber_enter, frequency_enter, message_enter;
    Button add;
    SmsManager sm;
    Messager messager;
    ListView lv;
    private String[] drawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * initializes all the elements of the main activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDrawer();
        messager = new Messager(this);
        alert = new Alerts(this);
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

        contactListAdapter = new ContactListAdapter(this, R.id.contactlist, item);
        lv = (ListView) findViewById(R.id.contactlist);
        lv.setAdapter(contactListAdapter);
        //where the user enters the message qualities
        phonenumber_enter = (EditText) findViewById(R.id.numberedit);
        frequency_enter = (EditText) findViewById(R.id.frequencyedit);
        message_enter = (EditText) findViewById(R.id.messageedit);
        lv.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                item.remove(position);
                contactListAdapter.notifyDataSetChanged();
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
        //TODO fix generic failure, delay sms add count of failure, put on backgroud thread
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Toast.makeText(MainActivity.this, "SMS sent",
                        //    Toast.LENGTH_SHORT).show();
                        Log.d("Message sent", "");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        //Toast.makeText(MainActivity.this, "Generic failure",
                        //      Toast.LENGTH_SHORT).show();
                        Log.d("Failure in sending message", "");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(MainActivity.this, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(MainActivity.this, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));
    }

    public Alerts getAlerts() {
        return alert;
    }

    /*
    sets up the elements for the drawer
     */
    private void setupDrawer() {
        drawerTitles = getResources().getStringArray(R.array.listitems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawerlistitem, drawerTitles));
        mDrawerList.setOnItemClickListener(new drawer_item_click_listener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String phone;
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
                            // no longer needed to streamline process phonenumber_enter.setText(phone);
                            item.add(new Custom(phone));
                            contactListAdapter.notifyDataSetChanged();
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
            e.printStackTrace();
        } finally {
            if (contacts != null) {
                contacts.close();
            }
        }
    }

    public void doLaunchContactPicker(View view) {
        //alert.contactAlert();
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

    }

    /**
     * gets all of the information from the fields in the main layout
     */
    public void gatherInformation() {
        try {
            message = message_enter.getText().toString();
            frequency = Integer.parseInt(frequency_enter.getText()
                    .toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Sorry but the fields are not entered correctly",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * adds a new phone number to the sending list
     */
    private void addItem() {

        try {
            item.add(new Custom(phonenumber_enter.getText().toString()));
            Log.d("Success", "added phone number " + item.get(item_position).getPhoneNumber());

            item_position++;
            for (int i = 0; i < item_position; i++) {
                Log.d("entry:", "Entry " + i + " == " + item.get(i).getPhoneNumber());
            }
            contactListAdapter.notifyDataSetChanged();
            phonenumber_enter.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessagesComplete() {
        //need to warn user if field has entry
        try {
            gatherInformation();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        new SendMessagesTask(item, frequency, message, messager, this).execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.tutorial_menu:
                startActivity(intent);
                return true;
            case R.id.sendmessage:
                sendMessagesComplete();
                return true;
            case R.id.clearmessage:
                alert.clearAlerts(this.item);
                contactListAdapter.notifyDataSetChanged();
                return true;
            case R.id.changes:
                alert.changedAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
