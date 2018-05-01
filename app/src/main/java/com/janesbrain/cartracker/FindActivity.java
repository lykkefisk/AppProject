package com.janesbrain.cartracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.janesbrain.cartracker.model.AutoLocation;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class FindActivity extends AppCompatActivity {

    CarService mService;
    FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = "FIND_ACTIVITY";
    private static final long MIN_TIME_BETWEEN_LOCATION_UPDATES = 5 * 1000;    // milisecs
    private static final float MIN_DISTANCE_MOVED_BETWEEN_LOCATION_UPDATES = 1;  // meters
    public static final String EXTRA_USER_LATITUDE = "location_latitude";
    public static final String EXTRA_USER_LONGITUDE = "location_longitude";

    private double latitude, longitude;
    private Location userLocation;
    private LocationManager locationMng;

    private boolean isTracking = false;
    public static final int PERMISSIONS_REQUEST_LOCATION = 189;

    public LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            userLocation = location;
            updateStatus();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        String stat = SetupLocationManager();
        Log.d(TAG,stat);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        isTracking = startTracking();
        if(isTracking){

        }
        else{

        }
        tryGetCurrentLocation();
    }



    private boolean startTracking() {
        try {
            if (locationMng == null) {
                locationMng = (LocationManager) getSystemService(LOCATION_SERVICE);
            }

            long minTime = MIN_TIME_BETWEEN_LOCATION_UPDATES;
            float minDistance = MIN_DISTANCE_MOVED_BETWEEN_LOCATION_UPDATES;
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

            if (locationMng != null) {
                try {
                    locationMng.requestLocationUpdates(minTime, minDistance, criteria, locListener, null);
                    Log.d("Main", "RequestLocationUpdates");
                    //Use criteria to chose best provider
                } catch (SecurityException ex) {
                    //TODO: user have disabled location permission - need to validate this permission for newer versions
                }
            } else {
                return false;
            }
            isTracking = true;
            return isTracking;
        } catch (Exception ex) {
            Log.e("TRACKER", "Error during start", ex);
            return false;
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
                    locationMng.requestLocationUpdates(60000, 1, criteria, locListener, null);
                    status = "LocationManager is setup";
                    Log.d("Main", status);
                    //Use criteria to chose best provider
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
    private void tryGetCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location l) {

                if(l != null)  {userLocation = l;}
                else Log.d(TAG,"Den vil ikke finde location fra mobile settings");

                updateStatus();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });

    }


    private void updateStatus() {
        String status = "";
        if (userLocation != null) {
            status = " location=known";
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
         //   locationTextView.setText("Lat: " + latitude + "\n" + "Long: " + longitude);

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

                    status += " \n" + "last_known_location=";
                    if (lastGps != null) {
                        status += "{" + lastGps.getLatitude() + ";" + lastGps.getLongitude() + "} (GPS) ";
                    }
                    if (lastNetwork != null) {
                        status += "{" + lastNetwork.getLatitude() + ";" + lastNetwork.getLongitude() + "} (Network) ";
                    }
                    // Create a Uri from an intent string. Use the result to create an Intent.
                    Uri gmmIntentUri = Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "?z=10");
                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");
                    // Attempt to start an activity that can handle the Intent
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            }// end if userLocation is null
             else {
                //locationTextView.setText("Unknown\n");
                status = " location=unknown";
                Log.d(TAG, "Update-metode. location manager er null");
            }
        }

/*
        if (isTracking) {
            status += " tracking=true";
        } else {
            status += " tracking=false";
        }
        //locationTextView.setText("Status: " + status);
*/
    }
    //Copied from ArnieExercizeFinder
    // er det ikke denne her der finder gps for hvor man er, når appen første gang starter ??
    private void UpdateLocation(Location current) {
        String status = "";

        Log.d(TAG, "NOW done with ::SetupLocationManager");
        if (current != null) {
            status = " location=known";
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
            //locationTextView.setText("Lat: " + latitude + "\n" + "Long: " + longitude);

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

                    status += " \n" + "last_known_location=";
                    if (lastGps != null) {
                        status += "{" + lastGps.getLatitude() + ";" + lastGps.getLongitude() + "} (GPS) ";
                    }
                    if (lastNetwork != null) {
                        status += "{" + lastNetwork.getLatitude() + ";" + lastNetwork.getLongitude() + "} (Network) ";
                    }
                    // Create a Uri from an intent string. Use the result to create an Intent.
                    Uri gmmIntentUri = Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "?z=10");
                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage(getString(R.string.google_package));
                    // Attempt to start an activity that can handle the Intent
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            } else {
                //locationTextView.setText("Unknown\n");
                status += " location=unknown";
                Log.d(TAG, "Update method. location manager is null");
            }
        } // end if current is null control statement
        else{
            checkPermission();
        }
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

}
