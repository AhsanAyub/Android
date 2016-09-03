package com.example.ahsan_000.currencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MyBroadcastReceiver mybrrcv;
    public String givenValue;
    public Boolean bdtous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mybrrcv = new MyBroadcastReceiver();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter filter = new IntentFilter(MyBroadcastReceiver.RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mybrrcv, filter);
    }

    public void bdTOus(View v){
        bdtous = true;
        EditText et = (EditText)findViewById(R.id.editText1);
        givenValue = et.getText().toString();
        Intent i = new Intent(MainActivity.this, HttpConnection.class);
        i.putExtra(HttpConnection.GIVENVALUE, givenValue);
        i.putExtra(HttpConnection.TYPE, bdtous);
        startService(i);
    }

    public void usTObd(View v){
        bdtous = false;
        EditText et = (EditText)findViewById(R.id.editText1);
        givenValue = et.getText().toString();
        Intent i = new Intent(MainActivity.this, HttpConnection.class);
        i.putExtra(HttpConnection.GIVENVALUE, givenValue);
        i.putExtra(HttpConnection.TYPE, bdtous);
        startService(i);
    }
    public class MyBroadcastReceiver extends BroadcastReceiver{
        public static final String RESPONSE = "nia.service.android.intent.action.RESPONSE";
        @Override
        public void onReceive(Context c, Intent i) {
            String convertedValue = i.getStringExtra(HttpConnection.VALUE);
            TextView displayResult = (TextView) findViewById(R.id.display);
            displayResult.setText(convertedValue);
        }
    }
}

