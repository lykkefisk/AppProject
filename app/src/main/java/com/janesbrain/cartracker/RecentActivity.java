package com.janesbrain.cartracker;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.janesbrain.cartracker.database.AutoLocationDao;
import com.janesbrain.cartracker.database.AutoRoom;
import com.janesbrain.cartracker.model.AutoLocation;

import java.util.List;

public class RecentActivity extends AppCompatActivity {

    TextView recentTextView;
    AutoLocationDao autoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);



        recentTextView = (TextView) findViewById(R.id.recentTextView);
        getRoomData();
    }

    public void getRoomData(){

        final AutoRoom autoRoom = Room.databaseBuilder(getApplicationContext(), AutoRoom.class, "production")
                .allowMainThreadQueries() //allow the database to read/write in the main UI thread (not good)
                .fallbackToDestructiveMigration()
                .build();

        //For fetching data from the Room database
        //https://www.youtube.com/watch?v=Dik-sGDWTrE&feature=youtu.be
        List<AutoLocation> autoLocations = autoRoom.autoLocationDao().getAllAutoLocation();
        if(autoLocations != null){
        String info = "";

        for (AutoLocation autoLocation : autoLocations){
            String addressLine = autoLocation.getAddressLine();
            String timeStamp = autoLocation.getTimeStamp();
            Double lat = autoLocation.getLatitude();
            Double lon = autoLocation.getLongitude();
            info = info+ "\n\n" +
                    "Time: " + timeStamp + "\n"
                    + "AddresLine: " + addressLine;

            recentTextView.setText(info);
        }}

    }
}
