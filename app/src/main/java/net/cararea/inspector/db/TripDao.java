package net.cararea.inspector.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TripDao {
    @Query("SELECT * FROM trip WHERE uid = :id")
    LiveData<Trip> getById(int id);

    @Insert
    void insertAll(Trip... trips);

    @Update
    void updateAll(Trip... trips);
}
