package com.wherami.demo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import wherami.lbs.sdk.adaptive.IALocation;
import wherami.lbs.sdk.adaptive.IALocationListener;
import wherami.lbs.sdk.adaptive.IALocationManager;
import wherami.lbs.sdk.adaptive.IALocationRequest;
import wherami.lbs.sdk.adaptive.IAOrientationListener;
import wherami.lbs.sdk.adaptive.IAOrientationRequest;

public class LocationActivity extends AppCompatActivity implements
//        MapEngine.LocationUpdateCallback
        IALocationListener,
        IAOrientationListener
{ //Implements this interface to receive location update from the MapEngine instance


//    private static MapEngine engine=null;
    IALocationManager mIALocationManager;
    private TextView textOut;
    private Handler handler;
    private IALocation mLocation;
    private float latestHeading;
    String TAG = "IALocationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        textOut = (TextView) findViewById(R.id.textView);
        handler = new Handler();

        Bundle extra = new Bundle();
        extra.putString(IALocationManager.EXTRA_SITENAME,"WKCD_xiqu");
        mIALocationManager = IALocationManager.create(getApplicationContext(), extra);
        Log.i(TAG, "onCreate: created WheramiIALocationManager");
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(),this);
        Log.i(TAG, "onCreate: reg location update");
        mIALocationManager.registerOrientationListener(new IAOrientationRequest(5.0D, 5.0D), this);
    }

    @Override
    protected void onDestroy() {
        mIALocationManager.removeLocationUpdates(this);
        mIALocationManager.unregisterOrientationListener(this);
        mIALocationManager.destroy();
        super.onDestroy();
    }
    Runnable runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            Log.i(TAG, "run: "+mLocation.toString());
            textOut.setText("Current Location: "+ mLocation.toString().replace(",",",\n"));
        }
    };

    @Override
    public void onLocationChanged(IALocation iaLocation) {
        new Thread() {
            @Override
            public void run() {
                mLocation = iaLocation;
                handler.post(runnableUi);
            }
        }.start();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged: "+provider+" "+status);
    }

    @Override
    public void onHeadingChanged(long timestamp, double heading) {
        Log.i(TAG, "onHeadingChanged: "+timestamp+" "+heading);
        latestHeading = (float) heading;
    }

    @Override
    public void onOrientationChange(long timestamp, double[] quaternion) {

    }

//    @Override
//    public void onLocationUpdated(final Location location) {
//        new Thread() {
//            @Override
//            public void run() {
//                mLocation = location;
//                handler.post(runnableUi);
//            }
//        }.start();
//
//    }
}
