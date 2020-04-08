package net.cararea.inspector.db;

import net.cararea.inspector.expenses.ExpenseType;

import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

@TypeConverters({Converters.class})
@Dao
public interface ExpenseDao {
    @Query("SELECT * FROM expense")
    LiveData<List<Expense>> getAll();

    @Query("SELECT * FROM expense WHERE type = :type")
    LiveData<List<Expense>> getByType(ExpenseType type);

    @Query("SELECT * FROM expense WHERE uid = :expenseId")
    LiveData<Expense> getById(int expenseId);

    @Query("SELECT * FROM expense WHERE car_id = :carId AND (date > :start AND date < :end)")
    LiveData<List<Expense>> getByRange(Calendar start, Calendar end, int carId);

    @Query("SELECT * FROM expense WHERE (date > :start AND date < :end) AND type = :type")
    LiveData<List<Expense>> getByRangeAndType(Calendar start, Calendar end, ExpenseType type);

    @Query("SELECT TOTAL(price) FROM expense WHERE type = :type AND car_id = :carId")
    LiveData<Float> getTotalPriceByType(ExpenseType type, int carId);

    @Query("SELECT TOTAL(price) FROM expense WHERE car_id = :carId AND (date > :start AND date < :end) AND type = :type")
    LiveData<Float> getTotalPriceByRangeAndType(Calendar start, Calendar end, ExpenseType type, int carId);

    @Query("SELECT COUNT(price) FROM expense WHERE type = :type")
    LiveData<Integer> getRowCountByType(ExpenseType type);

    @Insert
    void insertAll(Expense... expenses);

    @Delete
    void delete(Expense expense);
}
