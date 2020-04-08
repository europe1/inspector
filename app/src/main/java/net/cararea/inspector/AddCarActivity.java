package net.cararea.inspector;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.cararea.inspector.db.Car;

import androidx.appcompat.app.AppCompatActivity;

public class AddCarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
    }

    public void saveCar(View view) {
        EditText fieldName = findViewById(R.id.car_name);
        String text = fieldName.getText().toString();

        String name;
        String image = "suv.png";
        String make = "Toyota";

        if (text.isEmpty()) {
            name = make;
        } else {
            name = text;
        }

        Car car = new Car();
        car.image = image;
        car.make = make;
        car.name = name;

        DataRepository repository = new DataRepository(this);
        repository.insert(car);

        finish();
    }
}
