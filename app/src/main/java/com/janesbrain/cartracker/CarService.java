package com.janesbrain.cartracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.janesbrain.cartracker.model.ParkingData;

import java.net.URI;
import java.util.Set;

public class CarService extends Service {
    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT = "com.janesbrain.cartracker.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";
    public static final String EXTRA_TASK_TIME_MS = "task_time";
    private static final String TAG = "CAR_SERVICE";
    private static final String KEY_LONGITUDE = "LONGITUDE";
    private static final String KEY_LATITUDE = "LATITUDE";

    private boolean started = false;
    private boolean isTracking = false;
    private long wait;
    private double latitude, longitude;

    //Whether to run as a ForegroundService (with permanent notification, harder to kill)
    private boolean runAsForegroundService = true;
    private LocationManager locationMng;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            UpdateLocation(location);
            Log.d(TAG, "onLocationChanged called");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //Class used for the client Binder.  Because we know this service always runs in the same process as its clients, we don't need to deal with IPC.
    public class LocalBinder extends Binder {
        CarService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CarService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Background service onCreate");
        SetupLocationManager();
    }

    //Copied from ServiceDemo
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //in this case we only start the background running loop once
        if (!started && intent != null) {
            wait = intent.getLongExtra(EXTRA_TASK_TIME_MS, R.integer.SLEEP_2minutes);
            Log.d(TAG, "Background service onStartCommand with wait: " + wait + "ms");
            started = true;

            if (runAsForegroundService) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // Do not do this at home :)
                    NotificationChannel mChannel = new NotificationChannel("myChannel", "Visible myChannel", NotificationManager.IMPORTANCE_LOW);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.createNotificationChannel(mChannel);
                }

                Notification notification =
                        new NotificationCompat.Builder(this, "myChannel")
                                .setContentTitle(getText(R.string.service1))
                                .setContentText(getText(R.string.service3))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                //        .setContentIntent(pendingIntent)
                                .setTicker(getText(R.string.service3))
                                .setChannelId("myChannel")
                                .build();
                //calling Android to
                startForeground(R.integer.NOTIFY_ID, notification);
            }
        } else {
            Log.d(TAG, "Background service onStartCommand - already started!");
        }
        return START_STICKY;
    }

    //Copied from ServiceDemo
    //Send local broadcast
    private void broadcastTaskResult(ParkingData result) { //TODO Database navn??
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_BACKGROUND_SERVICE_RESULT);
        //broadcastIntent.putExtra(EXTRA_TASK_RESULT, result);
        Log.d(TAG, "Broadcasting:" + result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    //Copied from ArnieExercizeFinder
    private boolean stopTracking() {

        try {
            locationMng.removeUpdates(locListener);
            isTracking = false;
        } catch (SecurityException ex) {
            //TODO: user have disabled location permission - need to validate this permission for newer versions

        } catch (Exception ex) {
            Log.e("TRACKER", "Error during start", ex);

        }
        return isTracking;
    }

    //Copied from ArnieExercizeFinder
    private void broadcastLocation(Location location) {
        Intent updateIntent = new Intent("LOCATION_UPDATE");
        updateIntent.putExtra(KEY_LATITUDE, location.getLatitude());
        updateIntent.putExtra(KEY_LONGITUDE, location.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
        Log.d(TAG, "BroadcastLocationUpdate is called");
    }

    //Copied from ArnieExercizeFinder
    // this call is started from onLocationChanged, when the locationListener picks up a new position
    public void UpdateLocation(Location current) {

        String status = "";
        if (current == null) return;
        else {

            latitude = current.getLatitude();
            longitude = current.getLongitude();
            broadcastLocation(current);

            // maybe this is a good idea instead
            //broadcastLocation(current);

            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "?z=10");
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            // THE ACTION VIEW, MUST BE IN THE MANIFEST WITH A CLASS THAT HANDLES VIEW ACTIONS!!!! (JANE)
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");
            // Attempt to start an activity that can handle the Intent
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }

            if (locationMng != null) {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    //do nothing... her kommer den lille popup frem efter første klik.
                    Toast.makeText(this, "Need permission for location", Toast.LENGTH_LONG).show();
                } else {
                    Location lastGps = locationMng.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location lastNetwork = locationMng.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    // TODO why is this done?!?! jane wonders again

                    // maybe this is where a broadcast to find activity
                    // is supposed to be?? (jane wonders)

                    // should we start the activity for map everytime there is a location update???
                    // seems rather over the top, and battery draining for an app
                }
            } // end if: location manager is not null
            else {
                SetupLocationManager();
                // I Guuess ???
                // TODO anyone know why this is checked everytime the update is coming in??
            }
        }
    }

    //Copied from ArnieExercizeFinder
    private String SetupLocationManager() {
        String status = "";

        try {
            if (locationMng == null) {
                locationMng = (LocationManager) getSystemService(LOCATION_SERVICE);
            }

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

            if (locationMng != null) {
                try {
                    locationMng.requestLocationUpdates(R.integer.SLEEP_1minute, R.integer.DISTANCE_1M , criteria, locListener, null);
                    status = "LocationManager is setup with a listener";
                    Log.d("Main", status);

                } catch (SecurityException ex) {

                    //TODO: user have disabled location permission - need to validate this permission for newer versions
                    // make this in a private method og a dialog fragment
                    // that can be started by intent (explicitly)
                    status = "Security Exception is thrown";
                }
            } else {
                status = "Location Manager is not set up correctly";
                Log.d(TAG, status);
            }

        } catch (Exception ex) {
            status = "Error in TRY-CATCH UpdateLocationManager()";
            Log.e(TAG, status, ex);

        }
        return status;
    }

}

