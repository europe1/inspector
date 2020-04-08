package net.cararea.inspector.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CarDao {
    @Query("SELECT * FROM car")
    LiveData<List<Car>> getAll();

    @Query("SELECT * FROM car WHERE uid = :carId")
    LiveData<Car> getById(int carId);

    @Insert
    void insertAll(Car... cars);
}
