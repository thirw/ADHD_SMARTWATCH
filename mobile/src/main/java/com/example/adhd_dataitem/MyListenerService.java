package com.example.adhd_dataitem;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MyListenerService extends WearableListenerService {
    public static final String TAG = "MyDataMAP.....";
    public static final String WEARABLE_DATA_PATH = "/wearable/data/path";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        DataMap dataMap;
        for(DataEvent dataEvent: dataEvents){
            if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                String path = dataEvent.getDataItem().getUri().getPath();
                if(path.equalsIgnoreCase(WEARABLE_DATA_PATH)){
                    dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                    Log.v(TAG,"DataMap received on Wearable Device"+dataMap);

                    Intent startIntent = new Intent(this, ProfileActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//                    String[] myArray = new String[6];
//                    myArray[0] = String.valueOf(dataMap.getString("time"));
//                    myArray[1] = String.valueOf(dataMap.getString("timeCount"));
//                    myArray[2] = String.valueOf(dataMap.getInt("one"));
//                    myArray[3] = String.valueOf(dataMap.getInt("two"));
//                    myArray[4] = String.valueOf(dataMap.getInt("three"));
//                    myArray[5] = String.valueOf(dataMap.getInt("four"));

                    String[] timeDate = new String[2];
                    timeDate[0] = dataMap.getString("time");
                    timeDate[1] = dataMap.getString("timeCount");

                    int[] data = new int[2];
                    data[0] = dataMap.getInt("one");
                    data[1] = dataMap.getInt("two");

                    startIntent.putExtra("time", timeDate[0]);
                    startIntent.putExtra("timeCount", timeDate[1]);
                    startIntent.putExtra("one",data[0]);
                    startIntent.putExtra("two",data[1]);
                    startActivity(startIntent);

                }
            }
        }
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().equals(WEARABLE_DATA_PATH)){
            final String message = new String(messageEvent.getData());
            Intent startIntent = new Intent(this,MainActivity.class);
            startIntent.putExtra("message",message);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
            Log.d(TAG,"==========Main activity has been started=========");



        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}
