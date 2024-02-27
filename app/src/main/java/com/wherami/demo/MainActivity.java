package com.wherami.demo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import wherami.lbs.sdk.Client;
//import mtrec.*;

public class MainActivity extends AppCompatActivity {
    private Button mainButton;
    private Button mapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButton = (Button) findViewById(R.id.main_button);
        mapButton = (Button) findViewById(R.id.map_button);

        Log.d("MainActivity", "onCreate");

        String[] allPermissions = null;
        try {
            allPermissions = getPackageManager()
                    .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        for (String permission : allPermissions){
//            Log.i("permisssion", permission);
//            if (ContextCompat.checkSelfPermission(
//                    this, permission) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                // You can use the API that requires the permission.
//                performAction(...);
//            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this, permission)) {
//                // In an educational UI, explain to the user why your app requires this
//                // permission for a specific feature to behave as expected, and what
//                // features are disabled if it's declined. In this UI, include a
//                // "cancel" or "no thanks" button that lets the user continue
//                // using your app without granting the permission.
//                showInContextUI(...);
//            } else {
//                // You can directly ask for the permission.
//                requestPermissions(new String[]{permission}, 10001);
//            }
//        }

        if (allPermissions != null) {
            boolean allPermissionsAlreadyGranted = true;

            if (Build.VERSION.SDK_INT >= 23) {
                List<String> permissions2request = new ArrayList<>();
                for (String permission : allPermissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("permission", "onCreate: ");
                        allPermissionsAlreadyGranted = false;
                        permissions2request.add(permission);
                    }
                }

                if (!permissions2request.isEmpty()) {
                    this.requestPermissions(
                            permissions2request.toArray(new String[0]),
                            0xFFF);
                }
            }

            Log.d("MainActivity", "allPermissionsAlreadyGranted = " + allPermissionsAlreadyGranted);
            if(allPermissionsAlreadyGranted){
                initialize();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initialize();
    }

    public void onButtonClicked(View view){
        switch (view.getId()) {
            case R.id.main_button:
                // Button 1 is clicked, start Activity 1
                Intent intent1 = new Intent(this, LocationActivity.class);
                startActivity(intent1);
                break;

            case R.id.map_button:
                // Button 2 is clicked, start Activity 2
                Intent intent2 = new Intent(this, MapboxActivity.class);
                startActivity(intent2);
                break;
        }
    }
    private void initialize(){
        Log.d("MainActivity", "initialize");

        mainButton.setEnabled(true);
//        try {
//            // The Dataset is downloaded from a server
//            // This is a development server serves as debug purpose only
//            // One should setup a http(s) server and host the files under
//            // http(s)://<host>/generated_assets/SciencePark-1719W/offline_data/
//            Client.Configure("http://43.252.40.60", "HKUST_fusion", this);
//            Map<String, Object> config = new HashMap<>();
//            config.put("wherami.lbs.sdk.core.MapEngineFactory:EngineType", "wherami.lbs.sdk.core.NativeMapEngine");
//            Client.ConfigExtra(config);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (StreamCorruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        checkDataUpdate();
    }

    private void checkDataUpdate(){
        //Start only when the app has the latest data
        mainButton.setEnabled(false);
        mainButton.setText("Checking Update...");
        Log.d("MainActivity", "checkDataUpdate");
        Client.CheckDataUpdate(new Client.DataUpdateQueryCallback() {
            @Override
            public void onQueryFailed(Exception e) {
                Log.d("MainActivity", "onQueryFailed");
                Toast.makeText(MainActivity.this, "Failed to check data update", Toast.LENGTH_LONG);
                if(Client.GetDataVersion() != null){
                    //It is possible to continue by using old data
                    mainButton.setEnabled(true);
                }else{
                    //If no data is downloaded previously, it is impossible to continue. Please retry under network environment
                }
            }

            @Override
            public void onUpdateAvailable(String s) {
                mainButton.setText("Updating data...");
                Log.d("MainActivity", "onUpdateAvailable");

                Client.UpdateData(new Client.DataUpdateCallback() {
                    @Override
                    public void onProgressUpdated(int i) {
                        Log.d("MainActivity", "onProgressUpdated");
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("MainActivity", "onCompleted");
                        Toast.makeText(MainActivity.this, "Update succeeded", Toast.LENGTH_SHORT);
                        mainButton.setEnabled(true);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.d("MainActivity", "onFailed");
                        Toast.makeText(MainActivity.this, "Failed to update data", Toast.LENGTH_LONG);
                        if(Client.GetDataVersion() != null){
                            //It is possible to continue by using old data
                            mainButton.setEnabled(true);
                        }else{
                            //If no data is downloaded previously, it is impossible to continue. Please retry under network environment
                        }
                    }
                }, MainActivity.this);
            }

            @Override
            public void onLatestVersion() {
                Log.d("MainActivity", "onLatestVersion");
                Toast.makeText(MainActivity.this, "On latest version", Toast.LENGTH_SHORT);
                mainButton.setEnabled(true);
            }
        }, this);
    }
}
