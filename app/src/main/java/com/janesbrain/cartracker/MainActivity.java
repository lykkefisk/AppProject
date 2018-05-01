package com.janesbrain.cartracker;

import android.Manifest;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.janesbrain.cartracker.database.AutoRoom;
import com.janesbrain.cartracker.model.AutoLocation;
import com.janesbrain.cartracker.model.ParkingData;
import com.janesbrain.cartracker.model.absLocation;
import com.janesbrain.cartracker.database.AutoLocationDao;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Declare UI widgets
    ImageButton settingButton;
    Button findButton;
    Button recentButton;
    Button parkButton;
    TextView recentTextView;
    ListView recentListView;
    //private String lastUpdated;

    public static final int PERMISSIONS_REQUEST_LOCATION = 189;

    private Dialog recentDialog;
    public ParkingData parkingData;
    private static final String TAG = "MAIN_ACTIVITY";
    private LocationManager locationMng;
    private ListViewAdaptor mAdapter;
    private List<absLocation> locationList;
    FusedLocationProviderClient mFusedLocationClient;
    private AutoRoom autoRoom;

    AutoLocationDao autoDao;

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

         /* Stetho initialization - allows for debugging features in Chrome browser
           See http://facebook.github.io/stetho/ for details
           1) Open chrome://inspect/ in a Chrome browse
           2) select 'inspect' on your app under the specific device/emulator
           3) select resources tab
           4) browse database tables under Web SQL
         */
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                        Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this))
                .build());
        /* end Stethos */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Get the data through Shareds prefrences
        final SharedPreferences SP = getApplicationContext().getSharedPreferences("PARKING", 0);

        settingButton = (ImageButton) findViewById(R.id.settingsButton);
        parkButton = (Button) findViewById(R.id.parkButton);
        findButton = (Button) findViewById(R.id.findButton);
        recentButton = (Button) findViewById(R.id.recentButton);


        checkPermission();

        parkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Save the current position (auto or manual address) to database
               // tryGetCurrentLocation();
                //TODO Save to database

            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                Log.d(TAG, "Bound - findButton");

                Intent findIntent = new Intent(MainActivity.this, FindActivity.class);
                startActivity(findIntent);

                //TODO Find object with the saved (last saved) position, and show on map

            }
        }
        });

        recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            recentTextView = (TextView) findViewById(R.id.recentTextView);

            //For fetching data from the Room database
            List<AutoLocation> autoLocations = autoDao.getAllAutoLocation();
            String info = "";

            for (AutoLocation autoLocation : autoLocations){
                String addressLine = autoLocation.getAddressLine();
                String timeStamp = autoLocation.getTimeStamp();
                Double lat = autoLocation.getLatitude();
                Double lon = autoLocation.getLongitude();
                info = info+ "\n\n" +
                        "Time: " + timeStamp + "\n"
                        + "AddresLine: " + addressLine;

            }

                //Makes a dialog box
                //recentDialog = new Dialog(MainActivity.this);
                //recentDialog.setTitle("Recent parked location");
                //recentDialog.setContentView(R.layout.recent_list);
                //recentTextView.setText(info);
                //recentListView = (ListView) findViewById(R.id.recentListView);

                //recentListView.setEnabled(true);

                //mAdapter = new ListViewAdaptor(MainActivity.this, locationList);
                //recentListView.setAdapter(mAdapter);

                //recentDialog.show();
                //TODO Show list with recent parked places (data from DB)
                //TODO Back button
            }
        });

    }

    private void createDataBase(){
        //Database for the auto generated address from lat and long
        //Add the database (WRAP IN BACKGROUND THREAD) final because it is accessed from an inner class
        final AutoRoom autoRoom = Room.databaseBuilder(getApplicationContext(), AutoRoom.class, "production")
                .allowMainThreadQueries() //allow the database to read/write in the main UI thread (not good)
                .fallbackToDestructiveMigration()
                .build();

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

                updateLocation(l);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });

    }

    private void updateLocation(Location location) {
        if(location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            //From Kasper via mail
            String a = "";
            Geocoder coder = new Geocoder(this);

            List<Address>addresses = null;
            try {
                addresses = coder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addresses!=null && addresses.size()>0){
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    a += address.getAddressLine(i) + "\n";
                }
                a+= address.getCountryName();

                //Specifies the lastupdated (Date/Time)
                //https://stackoverflow.com/questions/907170/java-getminutes-and-gethours
                String lastUpdated;
                Calendar timeNow = Calendar.getInstance();
                int hour = timeNow.get(Calendar.HOUR_OF_DAY);
                int minut = timeNow.get(Calendar.MINUTE);
                int sec = timeNow.get(Calendar.SECOND);
                int date = timeNow.get(Calendar.DATE);
                int month = timeNow.get(Calendar.MONTH)+1;
                int year = timeNow.get(Calendar.YEAR);
                lastUpdated = date + "-" + month + "-" + year + " " + hour + ":" + minut + ":" + sec;

                AutoLocation autoLocation = new AutoLocation(a, latitude, longitude, lastUpdated);
                autoRoom.autoLocationDao().insertAll(autoLocation);

                Toast.makeText(this, "Your parking data has been saved. \n" +"\n Address: \n" + a, Toast.LENGTH_LONG).show();
            }
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

