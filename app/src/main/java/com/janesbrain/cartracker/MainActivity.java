package com.janesbrain.cartracker;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.janesbrain.cartracker.model.ParkingData;

public class MainActivity extends AppCompatActivity {

    //Declare UI widgets
    Button parkButton;
    Button findButton;
    Button recentButton;

    public static final int PERMISSIONS_REQUEST_LOCATION = 189;

    private Dialog recentDialog;
    private  Dialog findDialog;
    public ParkingData parkingData;
    private static final String TAG = "MAIN_ACTIVITY";
    private LocationManager locationMng;

    //For background service
    private long task_time = 4*1000; //4 ms

    //For bind service
    CarService mService;
    boolean mBound = false;

    //Copied from ServiceDemo
    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CarService.LocalBinder binder = (CarService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate is called");

        //Get the data through Shareds prefrences
        final SharedPreferences SP = getApplicationContext().getSharedPreferences("PARKING", 0);



        parkButton = (Button) findViewById(R.id.parkButton);
        findButton = (Button) findViewById(R.id.findButton);
        recentButton = (Button) findViewById(R.id.recentButton);

        checkPermission();

        parkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Save the current position (auto or manual address) to database
                Toast.makeText(MainActivity.this, "Your parking data has been saved!", Toast.LENGTH_LONG).show();
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {bindService(new Intent(MainActivity.this, CarService.class), mConnection, BIND_AUTO_CREATE);}

                    Log.d(TAG, "Bound - findButton");
                    //Makes a dialog box
                    findDialog = new Dialog(MainActivity.this);
                    findDialog.setTitle("Find your car");
                    findDialog.setContentView(R.layout.find_car);
                    findDialog.show();
                    //TODO Find object with the saved (last saved) position, and show on map
            }
        });

        recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    //Makes a dialog box
                    recentDialog = new Dialog(MainActivity.this);
                recentDialog.setTitle("Recent parked location");
                recentDialog.setContentView(R.layout.recent_list);
                recentDialog.show();
                //TODO Show list with recent parked places (data from DB)
                //TODO Back button
            }
        });

    }

    //Copied from ArnieExercizeFinder
    //modified from: https://developer.android.com/training/permissions/requesting.html
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
        else{
            Log.d(TAG,"Permission is granted");
        }
    }

    //Copied from ArnieExercizeFinder
    //modified from: https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermission is called");

        switch (requestCode){
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                } else {
                    // permission denied
                    //in this case we just close the app
                    Toast.makeText(this, " You need to enable permission for Location to use the app", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                return;
            }
        }
    }

    //Copied from ServiceDemo
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart is called");

        //Registering broadcast receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction(CarService.BROADCAST_BACKGROUND_SERVICE_RESULT);
        Log.d(TAG, "Registering broadcast receivers");

        //Using local broadcasts for this service, but registerReceiver(...) could be used instead
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReciever, filter);
        Intent intent = new Intent(this, CarService.class);
        //Binds to the service
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService is called");
    }

    //Copied from ServiceDemo
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop is called");
        //Unregistering broadcast receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReciever);
        //Unbinding from the service
        unbindService(mConnection);
        Log.d(TAG, "Unregistering broadcast receivers");
        mBound = false;
    }

    //Copied from ServiceDemo
    //Starts background service, taskTime indicates desired sleep period in ms for broadcasts
    private void startBackgroundService(long taskTime){
        Log.d(TAG, "startBackgroundService is called");
        Intent backgroundServiceIntent = new Intent(MainActivity.this, CarService.class);
        backgroundServiceIntent.putExtra(CarService.EXTRA_TASK_TIME_MS, taskTime);
        // bindService(backgroundServiceIntent,mConnection,BIND_AUTO_CREATE);
        startService(backgroundServiceIntent);
    }

    //Copied from ServiceDemo
    //Define our broadcast receiver for (local) broadcasts.
    //Registered and unregistered in onStart() and onStop() methods
    private BroadcastReceiver myBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO Recieve data from backgroundservice
            Log.d(TAG, "Broadcast reveiced from background service");


            Log.d(TAG, "updateViews BroadcastReceiver");
        }
    };

    //TODO SLETTES???
    //Comes with a warning when the users presses the BACK button in MainActivity
    public void onBackPressed() {
        //Open messagebox
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit the app?");
        builder.setCancelable(true);

        //Press ok and the app exits
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                finish(); //App exits
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel(); //Cancel Exit
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Saves the data between rotations
    @Override
    protected void onSaveInstanceState (Bundle outState){
        Log.d(TAG, "onSaveInstanceState is called");
        //outState.putString();
        super.onSaveInstanceState(outState);
    }

    //Restores the data between rotations
    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState is called");
    }

    //TODO Breadcrums
    //Sharedprefs https://www.youtube.com/watch?v=3MqAUQDetz4
    public void SharedPrefesSAVE(String parking){
        Log.d(TAG, "SharedPrefesSAVE is called");
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PARKING", 0);
        SharedPreferences.Editor prefEDIT = prefs.edit();
        prefEDIT.putString("Parking", parking);
        prefEDIT.apply();
    }

}

