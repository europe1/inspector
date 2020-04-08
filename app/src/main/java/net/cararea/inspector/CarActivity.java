package net.cararea.inspector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.cararea.inspector.db.Setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import static net.cararea.inspector.GarageActivity.setSelectedCarId;

public class CarActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        intent = getIntent();
        TextView carName = findViewById(R.id.car_name_text);
        carName.setText(intent.getExtras().getString("carName"));
    }

    public void setCurrentCar(View v) {
        int carId = intent.getExtras().getInt("carId");
        setSelectedCarId(carId);
        SettingRepository repository = new SettingRepository(this.getApplicationContext());
        LiveData<Setting> setting = repository.get(MainActivity.SELECTED_CAR_SETTING);
        setting.observe(this, (selectedCar) -> {
            setting.removeObservers(this);
            selectedCar.value = carId;
            repository.update(selectedCar);
        });
        finish();
    }
}
