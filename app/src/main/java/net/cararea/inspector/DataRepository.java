package net.cararea.inspector;

import android.content.Context;
import android.os.AsyncTask;

import net.cararea.inspector.db.AppDatabase;
import net.cararea.inspector.db.Car;
import net.cararea.inspector.db.CarDao;
import net.cararea.inspector.db.Expense;
import net.cararea.inspector.db.ExpenseDao;
import net.cararea.inspector.expenses.ExpenseType;

import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.LiveData;

public class DataRepository {
    private ExpenseDao expenseDao;
    private CarDao carDao;
    private LiveData<List<Expense>> allExpenses;
    private LiveData<List<Car>> allCars;

    DataRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        expenseDao = db.expenseDao();
        carDao = db.carDao();
        allExpenses = expenseDao.getAll();
        allCars = carDao.getAll();
    }

    LiveData<List<Expense>> getAll() {
        return allExpenses;
    }

    LiveData<List<Car>> getAllCars() { return allCars; }

    LiveData<List<Expense>> getByType(ExpenseType type) {
        return expenseDao.getByType(type);
    }

    LiveData<List<Expense>> getByRange(Calendar start, Calendar end, int carId) {
        return expenseDao.getByRange(start, end, carId);
    }

    LiveData<Float> getTotalPrice(ExpenseType type, int carId) {
        return expenseDao.getTotalPriceByType(type, carId);
    }

    LiveData<Float> getTotalPrice(Calendar start, Calendar end, ExpenseType type, int carId) {
        return expenseDao.getTotalPriceByRangeAndType(start, end, type, carId);
    }

    LiveData<Integer> getRowCount(ExpenseType type) { return expenseDao.getRowCountByType(type); }

    public void insert(Expense expense) {
        new insertAsyncTask(expenseDao).execute(expense);
    }

    public void insert(Car car) {
        new insertCarAsyncTask(carDao).execute(car);
    }

    private static class insertAsyncTask extends AsyncTask<Expense, Void, Void> {

        private ExpenseDao mAsyncTaskDao;

        insertAsyncTask(ExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Expense... params) {
            mAsyncTaskDao.insertAll(params);
            return null;
        }
    }

    private static class insertCarAsyncTask extends AsyncTask<Car, Void, Void> {

        private CarDao mAsyncTaskDao;

        insertCarAsyncTask(CarDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Car... params) {
            mAsyncTaskDao.insertAll(params);
            return null;
        }
    }
}