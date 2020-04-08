package net.cararea.inspector.db;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Expense.class, Car.class, Setting.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ExpenseDao expenseDao();
    public abstract CarDao carDao();
    public abstract SettingDao settingDao();

    private static final String DATABASE_NAME = "inspector";
    private static AppDatabase sInstance;
    private final MutableLiveData<Boolean> isDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext());
                    sInstance.setDatabaseCreated();
                }
            }
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration() // Remove in production
                .build();
    }

    private void setDatabaseCreated() {
        isDatabaseCreated.postValue(true);
    }

    public MutableLiveData<Boolean> getDatabaseCreated() {
        return isDatabaseCreated;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (sInstance != null)
            sInstance.close();
    }
}
