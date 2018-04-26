package com.janesbrain.cartracker.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.janesbrain.cartracker.model.ManualLocation;

import java.util.List;

@Dao
public interface ManualLocationDao {
    @Query("SELECT * FROM manuallocation")
    List<ManualLocation> getAllManualLocations();

    @Insert
    void insertAll(ManualLocation... manualLocations);

    @Delete
    void delete(ManualLocation... manualLocations);

}
