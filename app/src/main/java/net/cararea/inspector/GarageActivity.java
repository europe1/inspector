package net.cararea.inspector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.cararea.inspector.db.Car;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class GarageActivity extends AppCompatActivity {
    private static int selectedCarId = 1;
    private GarageAdapter adapter;
    private List<Car> carList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        RecyclerView recyclerView = findViewById(R.id.garage_recycler_view);
        adapter = new GarageAdapter(this, carList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemAnimator animator = new FadeInAnimator();
        animator.setAddDuration(200);
        recyclerView.setItemAnimator(animator);

        GarageViewModel viewModel = ViewModelProviders.of(this).get(GarageViewModel.class);
        viewModel.getAllCars().observe(this, (cars) -> {
            carList.clear();
            for (Car car : cars) {
                carList.add(car);
                adapter.notifyItemInserted(carList.size() - 1);
            }
        });
    }

    public void addCar(View view) {
        Intent intent = new Intent(this, AddCarActivity.class);
        startActivity(intent);
    }

    static void setSelectedCarId(int carId) {
        selectedCarId = carId;
    }

    public static int getSelectedCarId() {
        return selectedCarId;
    }
}
