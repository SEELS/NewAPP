package com.example.sameh.pushnotification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        TextView text = (TextView) findViewById(R.id.text_about_us);
        String s = "SEELS (Smart End-to-End Logistic System), " +
                "an IoT based solution, provides transportation companies with methods to track the trucks and products " +
                "spatially and temporally, " +
                "and procedures to rate the driver based on his behavior and adherence to basic driving rules while driving. ";
        text.setText(s);
    }
}
