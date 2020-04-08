package net.cararea.inspector.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Setting {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    public String name;
    public int value;
}
