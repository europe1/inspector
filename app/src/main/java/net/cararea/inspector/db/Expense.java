package net.cararea.inspector.db;

import net.cararea.inspector.expenses.ExpenseType;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    public String name;

    @TypeConverters({Converters.class})
    public ExpenseType type;

    public float price;

    @TypeConverters({Converters.class})
    public Calendar date;

    @ForeignKey(entity = Car.class, parentColumns = "uid", childColumns = "carId",
            onDelete = ForeignKey.CASCADE)
    @ColumnInfo(name = "car_id")
    public int carId;

    @NonNull
    @Override
    public String toString() {
        return String.format("(%s) %s - %s [%s]", type, name, price, date.toString());
    }
}
