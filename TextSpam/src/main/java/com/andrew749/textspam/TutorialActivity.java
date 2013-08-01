package com.andrew749.textspam;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TutorialActivity extends Activity {
    Button exitTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
        exitTutorial = (Button) findViewById(R.id.exitTutorial);
        exitTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
