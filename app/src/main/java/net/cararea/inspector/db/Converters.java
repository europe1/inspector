package net.cararea.inspector.db;

import android.util.Log;

import net.cararea.inspector.expenses.ExpenseType;

import java.util.Calendar;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static String fromInstance(ExpenseType instance) {
        return instance == null ? null : instance.getClass().getName();
    }

    @TypeConverter
    public static ExpenseType toInstance(String className) {
        if (className == null) {
            return null;
        } else {
            ExpenseType typeInstance;
            try{
                typeInstance = (ExpenseType) Class.forName(className).newInstance();
            } catch (Exception e) {
                Log.e("toInstance", e.getMessage());
                return null;
            }
            return typeInstance;
        }
    }

    @TypeConverter
    public static Long fromCalendar(Calendar calendar) {
        return calendar == null ? null : calendar.getTimeInMillis();
    }

    @TypeConverter
    public static Calendar toCalendar(Long millis) {
        if (millis == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
}
