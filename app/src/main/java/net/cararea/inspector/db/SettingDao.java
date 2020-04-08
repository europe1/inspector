package net.cararea.inspector.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SettingDao {
    @Query("SELECT * FROM setting WHERE uid = :id")
    LiveData<Setting> getById(int id);

    @Query("SELECT * FROM setting WHERE name = :name")
    LiveData<Setting> getByName(String name);

    @Insert
    void insertAll(Setting... settings);

    @Update
    void updateAll(Setting... settings);
}
