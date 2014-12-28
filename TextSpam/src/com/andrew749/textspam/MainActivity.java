package com.andrew749.textspam;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.andrew749.textspam.Database.DataSource;
import com.andrew749.textspam.Fragments.Conversations;
import com.andrew749.textspam.Fragments.Conversations.conversationCommunication;
import com.andrew749.textspam.Fragments.QuickMessageFragment;
import com.andrew749.textspam.Fragments.QuickMessageFragment.quickmessagecommunication;
import com.google.analytics.tracking.android.EasyTracker;
import com.inscription.ChangeLogDialog;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements
        quickmessagecommunication, conversationCommunication {
    private final int URL_LOADER = 0;
    /**
     * This is the main class where all the methods are interconnected
     */
    public QuickMessageFragment frag;
    public String[] projection = {ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.NUMBER};
    //static ShowcaseViews views;
    Activity activity;
    private String[] drawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/258436447677720"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/textspam"));
        }
    }

    /**
     * initializes all the elements of the main activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        setupDrawer();

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = manager
                .beginTransaction();
        manager.beginTransaction();
        frag = new QuickMessageFragment();
        ft.add(R.id.content_frame, frag);
        ft.commit();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Message unsent",
                                Toast.LENGTH_SHORT);
                        Bundle bundle = arg1.getExtras();

                        // send the messages that didn't send correctly
                        Intent i = new Intent();
                        i.putExtra("unsent", bundle);
                        i.setAction("com.andrew749.textspam.sendmessages");
                        i.putExtra("resend", "yes");
                        sendBroadcast(i);
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));
    }

    /*
     * sets up the elements for the drawer
     */
    private void setupDrawer() {
        drawerTitles = getResources().getStringArray(R.array.listitems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawerlistitem, drawerTitles));
        mDrawerList.setOnItemClickListener(new drawer_item_click_listener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

    private void selectItem(int position) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        switch (position) {
            case 0:
                Fragment quickMessageFragment = new QuickMessageFragment();
                manager.beginTransaction()
                        .replace(R.id.content_frame, quickMessageFragment).commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 1:
                Fragment conversationFragment = new Conversations();
                manager.beginTransaction()
                        .replace(R.id.content_frame, conversationFragment).commit();

                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 2:
                handleTwitter();
                break;
            case 3:
                startActivity(getOpenFacebookIntent(this));
                break;
        }
    }

    public void handleTwitter() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=andrewcod749"));
            startActivity(intent);

        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/andrewcod749")));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this); // Add this method.

    }

    @Override
    protected void onStop() {

        super.onStop();
        EasyTracker.getInstance(this).activityStop(this); // Add this method.

    }

    @Override
    public void addConversation(String message, ArrayList<Custom> item) {
        DataSource datasource = new DataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        datasource.createConversation(message, getStringArrayContacts(item));
        datasource.close();
    }

    @Override
    public void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }

    @Override
    public void openChangelogDialog() {
        ChangeLogDialog dialog = new ChangeLogDialog(this);
        dialog.show();
    }

    public ArrayList<String> getStringArrayContacts(ArrayList<Custom> object) {
        ArrayList<String> tempnames = new ArrayList<String>();
        for (int i = 0; i < object.size(); i++) {
            tempnames.add(i, object.get(i).getPhoneNumber());
        }
        return tempnames;
    }

    class drawer_item_click_listener implements ListView.OnItemClickListener {

        /*
         * 0 will be quick message 1 conversations 2
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                long l) {
            selectItem(i);
        }

    }

}
