package com.janesbrain.cartracker.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.janesbrain.cartracker.model.AutoLocation;

import java.util.List;

@Dao
public interface AutoLocationDao {
    @Query("SELECT * FROM AutoLocation")
    List<AutoLocation> getAllAutoLocation();

    @Insert
    void insertAll(AutoLocation... autoLocations);

    @Delete
    void delete(AutoLocation... autoLocations);


}
