package net.cararea.inspector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.cararea.inspector.db.Setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import static net.cararea.inspector.GarageActivity.setSelectedCarId;

public class MainActivity extends AppCompatActivity {
    static final String SELECTED_CAR_SETTING = "selected_car";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSettings();
    }

    public void startCalendarActivity(View view) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    public void startInspectionActivity(View view) {
        Intent intent = new Intent(this, InspectionActivity.class);
        startActivity(intent);
    }

    public void startBudgetActivity(View view) {
        Intent intent = new Intent(this, BudgetActivity.class);
        startActivity(intent);
    }

    public void startGarageActivity(View view) {
        Intent intent = new Intent(this, GarageActivity.class);
        startActivity(intent);
    }

    private void setSettings() {
        SettingRepository repository = new SettingRepository(this);
        LiveData<Setting> selectedCar = repository.get(SELECTED_CAR_SETTING);
        selectedCar.observe(this, (selCar) -> {
            if (selCar == null) {
                Setting selectedCarSetting = new Setting();
                selectedCarSetting.name = SELECTED_CAR_SETTING;
                selectedCarSetting.value = 1;
                repository.insert(selectedCarSetting);
            } else {
                setSelectedCarId(selCar.value);
            }
        });
    }
}
