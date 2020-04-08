package net.cararea.inspector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;

import net.cararea.inspector.db.Trip;
import net.cararea.inspector.obd.AvailableCommands;
import net.cararea.inspector.obd.ConstantKeys;
import net.cararea.inspector.obd.ObdParameter;
import net.cararea.inspector.obd.ObdService;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DiagnoseActivity extends AppCompatActivity {
    private final static String SPEED_EXTRA = "speed";
    private final static String RPM_EXTRA = "rpm";

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose);

        List<ObdCommand> obdCommands = new ArrayList<>();
        obdCommands.add(new SpeedCommand());
        obdCommands.add(new RPMCommand());
        AvailableCommands.setCommands(obdCommands);

        if (getIntent().getExtras() != null) {
            // TODO: retrieve trip from the database by id passed in the intent extras
            getIntent().getExtras().getInt("tripId");
        }

        RecyclerView recyclerView = findViewById(R.id.parameter_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<ObdParameter> parameters = new ArrayList<>();
        parameters.add(new ObdParameter(R.string.speed, "30", R.string.kmh, SPEED_EXTRA));
        parameters.add(new ObdParameter(R.string.rpm, "1200", RPM_EXTRA));
        ParameterAdapter parameterAdapter = new ParameterAdapter(parameters);
        recyclerView.setAdapter(parameterAdapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantKeys.ACTION_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ConstantKeys.ACTION_OBD_CONNECTION_STATUS);
        //registerReceiver(obdReaderReceiver, intentFilter);
        //startService(new Intent(this, ObdService.class));
    }

    public void openErrors(View v) {
        Intent errorsActivity = new Intent(this, ErrorsActivity.class);
        startActivity(errorsActivity);
    }

    private final BroadcastReceiver obdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            if (action.equals(ConstantKeys.ACTION_OBD_REAL_TIME_DATA)) {
                TripRecord tripRecord = TripRecord.getInstance();
                //tripRecord.getSpeed();
                //tripRecord.getEngineRpm();
            }
        }
    };

    // Need to save trip data to db before calling this function
    private void stop() {
        unregisterReceiver(obdReaderReceiver);
        stopService(new Intent(this, ObdService.class));
        TripRecord tripRecord = TripRecord.getInstance();
        tripRecord.endTrip();
        ObdService.setServiceRunning(false);
    }
}
