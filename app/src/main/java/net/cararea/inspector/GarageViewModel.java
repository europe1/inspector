package net.cararea.inspector;

import android.app.Application;

import net.cararea.inspector.db.Car;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class GarageViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<Car>> cars;

    public GarageViewModel (Application application) {
        super(application);
        repository = new DataRepository(application);
        cars = repository.getAllCars();
    }

    LiveData<List<Car>> getAllCars() { return cars; }

    public void insert(Car car) { repository.insert(car); }
}
