package com.wherami.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.StreamCorruptedException;

import wherami.lbs.sdk.core.MapEngine;
import wherami.lbs.sdk.core.MapEngineFactory;
import wherami.lbs.sdk.data.Location;

public class LocationActivity extends AppCompatActivity implements
        MapEngine.LocationUpdateCallback { //Implements this interface to receive location update from the MapEngine instance


    private static MapEngine engine=null;
    private TextView textOut;
    private Handler handler;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        textOut = (TextView) findViewById(R.id.textView);
        handler = new Handler();
        if(engine == null) {
            engine = MapEngineFactory.Create(getApplicationContext());
            try {
                engine.initialize();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            }
        }
        engine.attachLocationUpdateCallback(this);
        engine.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        engine.stop();
    }

    @Override
    public void onLocationUpdated(final Location location) {
        new Thread() {
            @Override
            public void run() {
                mLocation = location;
                handler.post(runnableUi);
            }
        }.start();

    }

    Runnable runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            textOut.setText("Current Location: (" + mLocation.x + ", " + mLocation.y + ","+mLocation.areaId+")");
        }

    };
}
