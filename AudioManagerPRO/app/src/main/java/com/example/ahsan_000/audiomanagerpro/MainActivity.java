package com.example.ahsan_000.audiomanagerpro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView t1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View v) {
        Toast.makeText(this, "Session Start", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getBaseContext(), MyService.class);
        startService(i);

    }

    public void stopService(View v) {
        Toast.makeText(this, "Session Stopped", Toast.LENGTH_LONG).show();
        stopService(new Intent(getBaseContext(), MyService.class));
    }
}