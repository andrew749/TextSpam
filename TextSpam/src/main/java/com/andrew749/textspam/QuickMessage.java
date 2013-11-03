package com.andrew749.textspam;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.andrew749.textspam.Adapters.ContactListAdapter;

import java.util.ArrayList;

/**
 * Created by andrew on 02/11/13.
 */
public class QuickMessage extends Fragment {
    private static final int CONTACT_PICKER_RESULT = 1001;
    public static ArrayList<Custom> item = new ArrayList<Custom>();
    static Alerts alert;
    private static int frequency;
    private static String message;
    EditText phonenumber_enter, frequency_enter, message_enter;
    ContactListAdapter contactListAdapter;
    Button add, contact;
    ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        contactListAdapter = new ContactListAdapter(getActivity(), R.id.contactlist, item);
        getActivity().getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Received Broadcast", "update listview");
                item.clear();
                contactListAdapter.notifyDataSetChanged();

            }
        }, new IntentFilter("update"));
        getActivity().getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Recieved Broadcast", "open contact menu");
                doLaunchContactPicker(new View(getActivity()));
            }
        }, new IntentFilter("opencontact"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.quickmessagefragment, container, false);
        phonenumber_enter = (EditText) v.findViewById(R.id.numberedit);
        frequency_enter = (EditText) v.findViewById(R.id.frequencyedit);
        message_enter = (EditText) v.findViewById(R.id.messageedit);
        contact = (Button) v.findViewById(R.id.button2);
        alert = new Alerts(getActivity());

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLaunchContactPicker(new View(getActivity()));
            }
        });
        lv = (ListView) v.findViewById(R.id.contactlist);
        lv.setAdapter(contactListAdapter);
        //where the user enters the message qualities

        lv.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                item.remove(position);
                contactListAdapter.notifyDataSetChanged();
                return true;
            }
        });


        //add contact to list
        add = (Button) v.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        getActivity().registerReceiver(new BroadcastReceiver() {
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
                        Toast.makeText(getActivity(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getActivity(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getActivity(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * adds a new phone number to the sending list
     */
    private void addItem() {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phonenumber_enter.getText().toString())) {
            try {
                item.add(new Custom(phonenumber_enter.getText().toString()));

                contactListAdapter.notifyDataSetChanged();
                phonenumber_enter.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "This is not a valid phone number", Toast.LENGTH_LONG).show();
        }
    }

    public void sendMessagesComplete() {
        //need to warn user if field has entry
        try {
            gatherInformation();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        Bundle info = new Bundle();
        info.putInt("freq", frequency);
        info.putString("message", message);
        info.putSerializable("contact", item);

        Intent intent = new Intent();
        intent.setAction("com.andrew749.textspam.sendmessages");
        intent.putExtra("information", info);
        getActivity().sendBroadcast(intent);
        Log.d("Sent Intent", "sendmessages");


    }

//TODO make tutorial with spotlight to showcase features

    public void onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            Toast.makeText(getActivity(), "Sorry but the fields are not entered correctly",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void doLaunchContactPicker(View view) {
        //alert.contactAlert();
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String phone;
        Cursor contacts = null;
        try {
            if (resultCode == getActivity().RESULT_OK) {
                switch (requestCode) {
                    case CONTACT_PICKER_RESULT:
                        // gets the uri of selected contact
                        Uri result = data.getData();
                        // get the contact id from the Uri (last part is contact id)
                        String id = result.getLastPathSegment();
                        //queries the contacts DB for phone no
                        contacts = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone
                                .CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
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
                            Toast.makeText(getActivity(), "error", 100).show();
                        }
                        break;
                }

            } else {
                // gracefully handle failure
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), 50).show();
            e.printStackTrace();
        } finally {
            if (contacts != null) {
                contacts.close();
            }
        }
    }
}
