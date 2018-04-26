package com.janesbrain.cartracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.janesbrain.cartracker.model.AutoLocation;

@Database(entities =  {AutoLocation.class}, version = 1)
public abstract class AutoRoom extends RoomDatabase {
    public abstract AutoLocationDao autoLocationDao();
}
