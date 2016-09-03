package com.example.ahsan_000.audiomanagerpro;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import android.widget.TextView;
import android.widget.Toast;

/*
 * Created by ahsan_000 on 14-Aug-16.
 * last edited on September 02, 2016
 */

public class MyService extends Service implements SensorEventListener{

    private static final int SHAKE_THRESHOLD = 800;

    SensorManager sensorManager;
    Sensor proxSensor, lightSensor, motionSensor;
    boolean proxSensorFlag = false /*Close = true, Far = False*/, lightSensorFlag = false; /*Dark = true, Bright = False*/
    boolean profileCommonCondition = false;

    long curTime, diffTime, lastUpdate = (long) 0.0;
    double lastX = 0.0, lastY = 0.0, lastZ = 0.0, x1, y1, z1, speed, lightReading, proxValue;
    double[] gData = new double[3];

    AudioManager manager;
    Vibrator myVibrator;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Sesnors
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        motionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        myVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //audio manager
        manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        //unregistering sensors
        sensorManager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        //registration of sensors
        sensorManager.registerListener(this,proxSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,motionSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;

        if(sensor.getType() == Sensor.TYPE_PROXIMITY) //proximity sensor
        {
            proxValue = sensorEvent.values[0];
            if(proxValue < 6.0) //close
            {
                proxSensorFlag = true;

            }
            else //far
            {
                proxSensorFlag = false;
            }
        }

        if(sensor.getType() == Sensor.TYPE_LIGHT) //light sensor
        {
            lightReading = sensorEvent.values[0];
            if(lightReading < 10.0) //dark
            {
                //OBSTACLE IN FRONT OF PHONE AND LIGHT DARK
                profileCommonCondition = proxSensorFlag = lightSensorFlag = true;
            }
            else //bright
                profileCommonCondition = proxSensorFlag = lightSensorFlag = false;
        }

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) //accelerometer
        {
            double x = sensorEvent.values[0], y = sensorEvent.values[1], z = sensorEvent.values[2];

            if(profileCommonCondition)
            {
                String s = String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z);
                curTime = System.currentTimeMillis();
                if((x > -2.0 || y > -2.0) && z < 0)  //face down
                {
                    //face down - prox near and dark || SILENT / ONLY VIBRATION
                    manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                }
                if ((curTime - lastUpdate) > 100)
                {
                    diffTime = curTime - lastUpdate;
                    lastUpdate = curTime;

                    gData[0] = x;
                    gData[1] = y;
                    gData[2] = z;
                    x1 = Math.abs(gData[0]);
                    y1 = Math.abs(gData[1]);
                    z1 = Math.abs(gData[2]);
                    speed = ((x1 + y1 + z1 - lastX - lastY - lastZ) / diffTime) * 10000;
                    if ((x1 <= 1.5 && x1 >= -1.5) && (y1 <= 1 && y1 >= 0) && (z1 >= 9.5))
                    {
                        if (speed > SHAKE_THRESHOLD) //shake detected
                        {
                            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            //it requires user permission
                            myVibrator.vibrate(500); //0.5 seconds
                        }
                    }
                }
            }
            if(!profileCommonCondition)
            {
                if((x > -2.0 || y > -2.0) && z > 0)  //face UP
                {
                    //face UP - prox FAR and bright || HOME / NORMAL
                    myVibrator.cancel();
                    manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void makeToast(CharSequence text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}