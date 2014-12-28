package com.andrew749.textspam.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.andrew749.textspam.Adapters.AutoCompleteCursorAdapter;
import com.andrew749.textspam.Adapters.ContactListAdapter;
import com.andrew749.textspam.Custom;
import com.andrew749.textspam.MainActivity;
import com.andrew749.textspam.R;
import com.andrew749.textspam.SwipeDismissListViewTouchListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Andrew Codispoti on 02/11/13.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class QuickMessageFragment extends Fragment {
    private static final int CONTACT_PICKER_RESULT = 1001;
    public static ArrayList<Custom> item = new ArrayList<Custom>();
    private static int frequency;
    private static String message;
    final String PREFS_NAME = "TextSpamPreferences";
    public int[] fields = {R.id.ccontName, R.id.ccontNo};
    EditText frequency_enter, message_enter;
    AutoCompleteTextView phonenumber_enter;
    ContactListAdapter contactListAdapter;
    Button add;
    ImageButton contact;
    ListView lv;
    // private SimpleAdapter mAdapter;
    MainActivity activity;
    AutoCompleteCursorAdapter ad;
    private ArrayList<Map<String, String>> mPeopleList;

    /**
     * @author Andrew Codispoti This is the main fragment where the majority of
     * messaging should occur. There is an option to add conversations
     * to the conversation list from here.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        contactListAdapter = new ContactListAdapter(getActivity(),
                R.id.contactlist, item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.quickmessagefragment, container,
                false);
        phonenumber_enter = (AutoCompleteTextView) v
                .findViewById(R.id.numberedit);
        frequency_enter = (EditText) v.findViewById(R.id.frequencyedit);
        message_enter = (EditText) v.findViewById(R.id.messageedit);
        contact = (ImageButton) v.findViewById(R.id.addcontact);

        ad = new AutoCompleteCursorAdapter(getActivity(), null);

        phonenumber_enter.setAdapter(ad);
        phonenumber_enter.setThreshold(1);

        phonenumber_enter.setHint("Start typing a number");
        phonenumber_enter
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> av, View arg1,
                                            int index, long arg3) {
                        Log.d("This is ", "");
                        Cursor cursor = (Cursor) av.getItemAtPosition(index);
                        String number = "";
                        try {
                            number = cursor.getString(cursor
                                    .getColumnIndex(CommonDataKinds.Phone.NUMBER));
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        phonenumber_enter.setText("" + number);
                        addItem();

                    }
                });
        phonenumber_enter.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    addItem();
                    return true;
                }
                return false;
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLaunchContactPicker(new View(getActivity()));
            }
        });
        lv = (ListView) v.findViewById(R.id.contactlist);
        lv.setAdapter(contactListAdapter);
        // where the user enters the message qualities

        lv.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                item.remove(position);
                contactListAdapter.notifyDataSetChanged();
                return true;
            }
        });
        SwipeDismissListViewTouchListener lvtouchlistener = new SwipeDismissListViewTouchListener(
                lv, new SwipeDismissListViewTouchListener.DismissCallbacks() {

            @Override
            public void onDismiss(ListView listView,
                                  int[] reverseSortedPositions) {
                Log.d("quickmessage", "reverse sorted position:"
                        + reverseSortedPositions);

                for (int position : reverseSortedPositions) {
                    Custom i = contactListAdapter.getItem(position);

                    contactListAdapter.remove(i);
                    item.remove(i);

                }
            }

            @Override
            public boolean canDismiss(int position) {
                return true;
            }
        }
        );
        lv.setOnTouchListener(lvtouchlistener);
        lv.setOnScrollListener(lvtouchlistener.makeScrollListener());
        // add contact to list
        add = (Button) v.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        return v;
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    /*
     * adds a new phone number to the sending list
     */
    private void addItem() {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phonenumber_enter.getText()
                .toString())) {
            try {
                item.add(new Custom(phonenumber_enter.getText().toString()));

                contactListAdapter.notifyDataSetChanged();
                phonenumber_enter.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "This is not a valid phone number",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * sends a broadcast to the message reciever which will in turn send all the
     * messages
     */
    public void sendMessagesComplete() {
        // need to warn user if field has entry
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getSupportLoaderManager().destroyLoader(0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,
                                    MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * gets all of the information from the fields in the main layout
     */
    public void gatherInformation() {
        try {
            message = message_enter.getText().toString();
            frequency = Integer.parseInt(frequency_enter.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Sorry but the fields are not entered correctly",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String phone;
        Cursor contacts = null;
        try {
            ;
            getActivity();
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case CONTACT_PICKER_RESULT:
                        // gets the uri of selected contact
                        Uri result = data.getData();
                        // get the contact id from the Uri (last part is contact id)
                        String id = result.getLastPathSegment();
                        // queries the contacts DB for phone no
                        contacts = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + "=?", new String[]{id}, null
                        );
                        // gets index of phone no
                        int phoneIdx = contacts
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        if (contacts.moveToFirst()) {
                            // gets the phone no
                            phone = contacts.getString(phoneIdx);
                            phone = phone.replace(" ", "");
                            // assigns phone no to EditText field phoneno
                            // no longer needed to streamline process
                            // phonenumber_enter.setText(phone);
                            item.add(new Custom(phone));
                            contactListAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            } else {
                // gracefully handle failure
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (contacts != null) {
                contacts.close();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activity.toggleDrawer();
        }
//        if (item.getItemId() == R.id.tutorial_menu) {
//            doTutorial();
//            return true;
//        }
        int itemId = item.getItemId();
        if (itemId == R.id.sendmessage) {
            sendMessagesComplete();
            return true;
        } else if (itemId == R.id.clearmessage) {
            QuickMessageFragment.item.clear();
            contactListAdapter.notifyDataSetChanged();
            return true;
        } else if (itemId == R.id.changes) {
            activity.openChangelogDialog();
            return true;
        } else if (itemId == R.id.addconversation) {
            QuickMessageFragment.message = message_enter.getEditableText()
                    .toString();
            activity.addConversation(message, QuickMessageFragment.item);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME,
                0);
        if (settings.getBoolean("my_first_time", true)) {
            /*
             * Run tutorial because app is being launched for the first time
			 */
            //doTutorial();
            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }
    }

//    public void doTutorial() {
//        Intent intent = new Intent();
//
//        intent.setClass(getActivity(), TutorialActivity.class);
//        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
//
//        co.hideOnClickOutside = true;
//        co.insert = ShowcaseView.INSERT_TO_DECOR;
//        // the app is being launched for first time, do something
//        Log.d("Comments", "First time");
//        startActivity(intent);
//        ShowcaseViews views = new ShowcaseViews(activity);
//        views.addView(new ItemViewProperties(R.id.messageedit,
//                R.string.message_title, R.string.message_field));
//        views.addView(new ItemViewProperties(R.id.frequencyedit,
//                R.string.frequency_title, R.string.frequency_field));
//        views.addView(new ItemViewProperties(R.id.numberedit,
//                R.string.recipient_title, R.string.recipient_field));
//        views.addView(new ShowcaseViews.ItemViewProperties(R.id.add,
//                R.string.add_title, R.string.add_tutorial));
//        views.addView(new ShowcaseViews.ItemViewProperties(R.id.addcontact,
//                R.string.contact_title, R.string.contact_field));
//        views.addView(new ShowcaseViews.ItemViewProperties(R.id.contactlist,
//                R.string.sending_list_title, R.string.sending_list_tutorial));
//        views.addView(new ItemViewProperties(R.id.contactlist,
//                R.string.sending_list_title,
//                R.string.sending_list_swipe_tutorial));
//        views.addAnimatedGestureToView(6, 0, 0, 200, 0);
//        views.addView(new ItemViewProperties(R.id.sendmessage,
//                R.string.send_title, R.string.send_tutorial,
//                ShowcaseView.ITEM_ACTION_ITEM));
//        views.addView(new ItemViewProperties(R.id.clearmessage,
//                R.string.clear_title, R.string.clear_tutorial,
//                ShowcaseView.ITEM_ACTION_ITEM));
//        if (Build.VERSION.SDK_INT >= 14) {
//            if (ViewConfiguration.get(
//                    getActivity().getApplicationContext())
//                    .hasPermanentMenuKey()) {
//                views.addView(new ItemViewProperties(
//                        R.string.conversation_title,
//                        R.string.conversation_tutorial));
//                views.addView(new ItemViewProperties(R.string.tutorial_title,
//                        R.string.tutorial_tutorial));
//            } else {
//                views.addView(new ItemViewProperties(R.id.addconversation,
//                        R.string.conversation_title,
//                        R.string.conversation_tutorial,
//                        ShowcaseView.ITEM_ACTION_OVERFLOW));
//                views.addView(new ItemViewProperties(R.id.tutorial_menu,
//                        R.string.tutorial_title, R.string.tutorial_tutorial,
//                        ShowcaseView.ITEM_ACTION_OVERFLOW));
//            }
//        } else {
//            views.addView(new ItemViewProperties(R.string.conversation_title,
//                    R.string.conversation_tutorial));
//            views.addView(new ItemViewProperties(R.string.tutorial_title,
//                    R.string.tutorial_tutorial));
//        }
//        views.addView(new ItemViewProperties(android.R.id.home,
//                R.string.navigation_drawer,
//                R.string.navigation_drawer_tutorial,
//                ShowcaseView.ITEM_ACTION_HOME));
//        views.addAnimatedGestureToView(11, 0, 0, 400, 0);
//
//        views.show();
//    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = (MainActivity) activity;
        super.onAttach(activity);
        setHasOptionsMenu(true);

    }

    public interface quickmessagecommunication {
        public void toggleDrawer();

        public void addConversation(String message, ArrayList<Custom> item);

        public void openChangelogDialog();

    }

}
