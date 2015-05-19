package com.example.bhaskar.locationtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by bhaskar on 11/4/15.
 */
public class TwoWays extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twoways);
        Button start = (Button) findViewById(R.id.startlocationservice);
        Button get = (Button) findViewById(R.id.getuserdata);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getBaseContext(), MainService.class));
                Toast.makeText(getApplicationContext(), "service started", Toast.LENGTH_SHORT).show();
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TwoWays.this, CheckUser.class);
                startActivity(intent);
            }
        });
    }
}
