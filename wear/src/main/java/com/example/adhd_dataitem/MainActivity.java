package com.example.adhd_dataitem;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener {

    GoogleApiClient googleApiClient = null;
    public static final String TAG = "MyDataMAP.....";
    public static final String WEARABLE_DATA_PATH = "/wearable/data/path";

    private TextView mTextViewHeart, mTextViewStepCount;
    private ImageButton btnStart;
    private ImageButton btnPause;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor, mStepCountSensor;
    public Chronometer chronometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewHeart = (TextView) findViewById(R.id.heartRateText);
        mTextViewStepCount = (TextView) findViewById(R.id.stepCount);
        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnPause = (ImageButton) findViewById(R.id.btnPause);
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient = builder.build();

        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(ImageButton.GONE);
                btnPause.setVisibility(View.VISIBLE);
                mTextViewHeart.setText("Wait...");
                mTextViewStepCount.setText("Wait...");
                chronometer.start();
                startMeasure();
                onStart();


            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPause.setVisibility(ImageButton.GONE);
                btnStart.setVisibility(ImageButton.VISIBLE);
                mTextViewHeart.setText("--");
                mTextViewStepCount.setText("--");
                chronometer.stop();
                stopMeasure();
                onStop();

                Toast.makeText(v.getContext(),"Up"+chronometer.getText(),Toast.LENGTH_SHORT).show();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Enables Always-on
        setAmbientEnabled();
    }

    //###############Data Start/Stop Sensor####################

    private void startMeasure() {
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));

    }

    private void stopMeasure() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //HeartRate
        float mHeartRateFloat = event.values[0];
        int mHeartRate = Math.round(mHeartRateFloat);
        mTextViewHeart.setText(Integer.toString(mHeartRate));

        //StepCounter
        String mStepCounter = "" + (int)event.values[0];
        mTextViewStepCount.setText(mStepCounter);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    //########################################################################


    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Send DataItem %%%%%%%%%%%%%%%%%%%%%%%%%%%%
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(googleApiClient!=null && googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        sendDataMapToDataLayer();
    }

    private DataMap createDataMap(){
        int heart = 80;
        int cal = 120;
        int pressure = 12;
        int blood = 13;
        String timeCount = String.valueOf(chronometer.getText());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(formatter.format(date));
        Log.v(TAG,""+date);

        DataMap dataMap = new DataMap();
        dataMap.putString("time", String.valueOf(date));
        dataMap.putString("timeCount",timeCount);
        dataMap.putInt("one",heart);
        dataMap.putInt("two", cal);
        dataMap.putInt("three", pressure);
        dataMap.putInt("four",blood);
        return dataMap;
    }
    public void sendDataMapToDataLayer(){
        if(googleApiClient.isConnected()){
            DataMap dataMap = createDataMap();
            new SendDataMapToDataLayer(WEARABLE_DATA_PATH, dataMap).start();
        }else{

        }
    }
    public void sendDataMapOnClick(View view){
        sendDataMapToDataLayer();
    }
    public class SendDataMapToDataLayer extends  Thread{
        String path;
        DataMap dataMap;

        public SendDataMapToDataLayer(String path, DataMap dataMap){
            this.path = path;
            this.dataMap = dataMap;

        }

        @Override
        public void run() {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WEARABLE_DATA_PATH);
            putDataMapRequest.getDataMap().putAll(dataMap);

            PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
            DataApi.DataItemResult dataItemResult = Wearable.DataApi.putDataItem(googleApiClient,putDataRequest).await();
            if(dataItemResult.getStatus().isSuccess()){
                //print success log
                Log.v(TAG,"################DataItem: successfully sent##############");
            }
            else{
                // print failure log
                Log.v(TAG, "Error while sending Message");
            }
        }
        }

        @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
