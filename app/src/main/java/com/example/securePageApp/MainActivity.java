package com.example.securePageApp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Button and sensors.
    private Button loginBtn;
    private SensorManager sensorManager;
    private Sensor proximitySensor, stepCounterSensor, tiltSensor, magnetometerSensor;
    private SensorEventListener sensorEventListener;

    // Variable for proximity sensor.
    private float distance;

    // Variables for phone's brightness case.
    private int curBrigthLight;
    private final int MAX_LIGHT = 255;

    // Variables for step counter sensor.
    private float stepCounter = 0, initialStepCounter = 0;
    private boolean firstStep = true;

    // Variable for tilt sensor.
    private double xTilt = 0;

    // Variables for magnetometer sensor.
    private int mAzimuth = 0;
    private float[] gData = new float[3], mData = new float[3],
            rMat = new float[9], iMat = new float[9], orientation = new float[3];



    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.loginBtn);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        tiltSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){
                    distance = sensorEvent.values[0];
                }
                if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                    if(firstStep){
                        initialStepCounter = sensorEvent.values[0];
                        firstStep = false;
                    }
                    stepCounter = sensorEvent.values[0] - initialStepCounter;
                }
                if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    xTilt = sensorEvent.values[0];
                    gData = sensorEvent.values.clone();
                }

                if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                    mData = sensorEvent.values.clone();
                }

                if ( SensorManager.getRotationMatrix( rMat, iMat, gData, mData ) ) {
                    mAzimuth= (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        registerAllListeners();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.loginBtn:

                // If the bright is on the max level it will sign in.
                try {
                    curBrigthLight = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if(curBrigthLight == MAX_LIGHT){
                    Toast.makeText(this, "Light - Login!", Toast.LENGTH_LONG).show();
                }


                // If proximity sensor is cover it will sign in.
                if(distance == 0){
                    Toast.makeText(this, "Distance - Login!", Toast.LENGTH_LONG).show();
                }


                // If the phone is tilted to the left it will sign in.
                if(xTilt > 3 && xTilt < 10){
                    Toast.makeText(this, "Tilt - Login!", Toast.LENGTH_LONG).show();

                }


                // If the phone is point to the north it will sign in.
                if(mAzimuth < 3 || mAzimuth > 357){
                    Toast.makeText(this, "North - Login!", Toast.LENGTH_LONG).show();
                }


                // If you make more then 10 steps it will sign in.
                if(stepCounter > 10){
                    Toast.makeText(this, "Steps - Login!", Toast.LENGTH_LONG).show();
                }



                break;
        }
    }


    private void registerAllListeners(){
        loginBtn.setOnClickListener(this);
        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, tiltSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}





