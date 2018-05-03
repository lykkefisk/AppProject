package com.janesbrain.cartracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.janesbrain.cartracker.tracking.BackTracker;
import com.janesbrain.cartracker.tracking.IUpdateBackTracking;
import com.janesbrain.cartracker.tracking.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
   to use google maps in android systems, see link below
   https://developers.google.com/maps/documentation/android-api/map

   to have an api key for using google maps, see link below
   https://developers.google.com/maps/documentation/android-api/config
 */
public class FindActivity extends FragmentActivity implements IUpdateBackTracking {

    CarService mService;
    FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = "FIND_ACTIVITY";
    private static final long MIN_TIME_BETWEEN_LOCATION_UPDATES = 5 * 1000;    // 5 sec
    private static final float MIN_DISTANCE_MOVED_BETWEEN_LOCATION_UPDATES = 1;  // meters
    public static final String EXTRA_USER_LATITUDE = "location_latitude";
    public static final String EXTRA_USER_LONGITUDE = "location_longitude";
    public static final int PERMISSIONS_REQUEST_LOCATION = 189;

    private double latitude, longitude;
    private Location userLocation;
    private LocationManager locationMng;
    private LocationListener notTheServiceLocationListener;

    private BackTracker tracker;
    private ProgressDialog prDialog;

    private BroadcastReceiver myBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(R.string.broadcast_action)) {

                // intent.getData holds a Uri object as one used for api calls
                Intent viewmap = new Intent(Intent.ACTION_VIEW, intent.getData());
                viewmap.setPackage("com.google.android.apps.maps");

                if (viewmap.resolveActivity(getPackageManager()) != null) {
                    startActivity(viewmap);
                }
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CarService.LocalBinder binder = (CarService.LocalBinder) service;
            mService = binder.getService();
            Log.d(TAG, "onServiceConnected called");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected called");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

     //   SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
       //         .findFragmentById(R.id.mapFragment);
       // mapFragment.getMapAsync(this);

        // havent fixed this tracker yet
        // tracker = new BackTracker(this,"current", "parked");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // checking permission
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            // should now start the action to call onRequestPermissionresult
        } else {
            Log.d(TAG, "The packagemanager is not permitted to use mobile locator");
            Toast.makeText(this,
                    R.string.note_permission_denied + "\r\n" + R.string.note_manual_saving,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart is called");
    }

    //Copied from ServiceDemo
    @Override
    protected void onStop() {

        super.onStop();
    }

    //Copied from ArnieExercizeFinder
    //modified from: https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermission is called");


    }

    //TODO SLETTES???  kun hvis det har indflydelse på noget der kører i forgrunden (jane)
    //Comes with a warning when the users presses the BACK button in MainActivity
    public void onBackPressed() {
        //Open messagebox
        AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
        builder.setMessage(getString(R.string.note_back_pressed));
        builder.setCancelable(true);

        //Press ok and the app exits
        builder.setPositiveButton("Close Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                // TODO more saving and control statements before finishing
                finish(); //App exits
            }
        });
        builder.setNegativeButton("Resume Tracking", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel(); //Cancel Exit
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // implements the interfaces for tracking back to car
    // TODO ..finish this another day
    @Override
    public void OnStartTracking() {
        prDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

    }

    @Override
    public void OnSuccess(List<Route> items) {


    }

}
