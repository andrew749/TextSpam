package com.andrew749.textspam;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
    /**
     * This is the main class where all the methods are interconnected
     */
    static int item_position = 0;
    final String PREFS_NAME = "MyPrefsFile";
    public Fragment frag;
    Intent intent = new Intent();
    private String[] drawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * initializes all the elements of the main activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDrawer();
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
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        manager.beginTransaction();
        frag = new QuickMessageFragment();
        ft.add(R.id.content_frame, frag);
        ft.commit();
    }

    /*
    sets up the elements for the drawer
     */
    private void setupDrawer() {
        drawerTitles = getResources().getStringArray(R.array.listitems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //TODO remove in other version
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //  mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        if (item.getItemId() == R.id.tutorial_menu) {
            startActivity(intent);
            return true;
        }
        switch (item.getItemId()) {
            case R.id.sendmessage:
                ((QuickMessageFragment) frag).sendMessagesComplete();
                return true;
            case R.id.clearmessage:
                Intent i = new Intent();
                i.setAction("update");
                sendBroadcast(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {
        FragmentManager manager = getFragmentManager();
        switch (position) {
            case 0:
                Fragment quickMessageFragment = new QuickMessageFragment();
                manager.beginTransaction().replace(R.id.content_frame, quickMessageFragment).commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 1:
                Fragment conversationFragment = new Conversations();
                manager.beginTransaction().replace(R.id.content_frame, conversationFragment).commit();
                mDrawerLayout.closeDrawer(mDrawerList);

                break;
            case 2:
                break;
        }
    }

    class drawer_item_click_listener implements ListView.OnItemClickListener {

        /*
        0 will be quick message
        1 conversations
        2
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectItem(i);
        }


    }
}




