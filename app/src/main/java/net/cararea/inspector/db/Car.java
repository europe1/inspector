package net.cararea.inspector.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Car {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    public String name;
    public String make;
    public String image;
}
