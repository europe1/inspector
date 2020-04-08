package net.cararea.inspector;

import android.app.Application;

import net.cararea.inspector.db.Expense;

import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MonthViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<Expense>> expenses;

    MonthViewModel(Application application, Calendar start, Calendar end, int carId) {
        super(application);
        repository = new DataRepository(application);
        expenses = repository.getByRange(start, end, carId);
    }

    LiveData<List<Expense>> getAll() { return expenses; }

    public void insert(Expense expense) { repository.insert(expense); }
}
