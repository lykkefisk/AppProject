package com.janesbrain.cartracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.janesbrain.cartracker.model.ManualLocation;

@Database(entities =  {ManualLocation.class}, version = 1)
public abstract class ManualRoom extends RoomDatabase {
    public abstract ManualLocationDao autoPositionDao();
}