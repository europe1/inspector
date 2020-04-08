package net.cararea.inspector;

import android.app.Application;

import net.cararea.inspector.expenses.ExpenseType;
import net.cararea.inspector.utils.Util;

import java.util.Calendar;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import static net.cararea.inspector.GarageActivity.getSelectedCarId;

class PriceViewModel extends AndroidViewModel {
    private LiveData<Float> month;
    private LiveData<Float> year;
    private LiveData<Float> all;

    PriceViewModel(Application application, ExpenseType type) {
        super(application);
        DataRepository repository = new DataRepository(application);
        int carId = getSelectedCarId();

        Calendar calendar = Calendar.getInstance();
        Calendar[] monthRange = Util.monthRange(calendar);
        Calendar[] yearRange = Util.yearRange(calendar);

        month = repository.getTotalPrice(monthRange[0], monthRange[1], type, carId);
        year = repository.getTotalPrice(yearRange[0], yearRange[1], type, carId);
        all = repository.getTotalPrice(type, carId);
    }

    LiveData<Float> getMonthPrice() {
        return month;
    }

    LiveData<Float> getYearPrice() {
        return year;
    }

    LiveData<Float> getTotalPrice() {
        return all;
    }
}
