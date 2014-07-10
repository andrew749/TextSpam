package com.andrew749.textspam.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andrew749.textspam.MainActivity;
import com.andrew749.textspam.R;

/**
 * Created by andrew on 08/07/14.
 */
public class TestActivity extends ActivityInstrumentationTestCase2<MainActivity> {
    public TestActivity(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    MainActivity activity;
    EditText name,message,frequency;
    Button button;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        name= (EditText) activity.findViewById(R.id.numberedit);
        message= (EditText) activity.findViewById(R.id.messageedit);
        frequency= (EditText) activity.findViewById(R.id.frequencyedit);
        button= (Button) activity.findViewById(R.id.add);
    }
    @SmallTest
    public void testInput(){
        message.requestFocus();
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("Hello Android!");
        getInstrumentation().waitForIdleSync();
    }
}
