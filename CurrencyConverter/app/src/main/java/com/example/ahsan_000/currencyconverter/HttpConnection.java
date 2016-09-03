package com.example.ahsan_000.currencyconverter;

import android.app.IntentService;
import android.content.Intent;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ahsan_000 on 04-Sep-16.
 */
public class HttpConnection extends IntentService {

    public String val;
    public static String VALUE = "convertedValue";
    public static String GIVENVALUE = "myvalue";
    public static String TYPE = "type";
    public Boolean bdtous;
    private String dataString = "";
    private String rate = "";
    int a,b;
    private String[] convRate = new String[2];

    public HttpConnection() {
        super("Connection");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            bdtous = intent.getExtras().getBoolean(HttpConnection.TYPE);
            val = intent.getExtras().getString(HttpConnection.GIVENVALUE);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://www.hrhafiz.com/converter/index.php");

            HttpResponse response = httpclient.execute(httpget);

            InputStream content = response.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                dataString += s;
            }
            int i=0;
            for(String temp:dataString.split(",", 2)){
                convRate[i] = temp;
                i++;
            }

            if(bdtous){
                rate = convRate[1].substring(convRate[1].indexOf(":") + 1, convRate[1].indexOf("}"));
                val = String.valueOf(Double.valueOf(val)*Double.valueOf(rate));

            }
            else{
                rate = convRate[0].substring(convRate[0].indexOf(":") + 1, convRate[0].indexOf("}"));
                val = String.valueOf(Double.valueOf(val)*Double.valueOf(rate));

            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.MyBroadcastReceiver.RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(VALUE,val);
        sendBroadcast(broadcastIntent);
    }
}