package net.cararea.inspector.db;

import java.util.Calendar;
import java.util.UUID;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Trip {
    @PrimaryKey
    public UUID uid;

    public float distance;

    @ColumnInfo(name = "tc")
    public String troubleCodes;

    @ColumnInfo(name = "perm_tc")
    public String permanentTroubleCodes;

    @ColumnInfo(name = "max_speed")
    public int maxSpeed;

    @ColumnInfo(name = "max_rpm")
    public int maxRpm;

    @TypeConverters({Converters.class})
    @ColumnInfo(name = "start_time")
    public Calendar timeStart;

    @TypeConverters({Converters.class})
    @ColumnInfo(name = "end_time")
    public Calendar timeEnd;
}
