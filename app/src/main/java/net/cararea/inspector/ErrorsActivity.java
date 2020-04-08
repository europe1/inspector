package net.cararea.inspector;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import net.cararea.inspector.obd.ObdError;
import net.cararea.inspector.obd.ObdErrorReader;
import net.cararea.inspector.utils.L;
import net.cararea.inspector.utils.LocationManager;

import static net.cararea.inspector.utils.Util.isOnline;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ErrorsActivity extends AppCompatActivity implements
        LocationManager.UserLocationListener {
    private NearbyPlaces nearbyPlaces;
    private List<ObdError> errors = new ArrayList<>();
    private ErrorAdapter adapter;

    private LocationManager<ErrorsActivity> locationManager;

    private Animation slideUp;
    private Animation slideDown;

    private static class ResponseHandler extends Handler {
        private WeakReference<ErrorsActivity> reference;

        ResponseHandler(ErrorsActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NearbyPlaces.MAP_ELEMENTS_READY) {
                reference.get().showServicesNearby();
            }
            super.handleMessage(msg);
        }
    }

    private static class GetErrorsTask extends AsyncTask<String, Void, Void> {
        private WeakReference<ErrorsActivity> reference;
        private InputStreamReader reader;

        GetErrorsTask(ErrorsActivity activity, InputStreamReader reader) {
            reference = new WeakReference<>(activity);
            this.reader = reader;
        }

        @Override
        protected Void doInBackground(String... errorCodesVal) {
            List<String> errorCodes = Arrays.asList(errorCodesVal);
            try {
                List<ObdError> errorList = ObdErrorReader.getErrorsDefinitions(reader, errorCodes);
                reference.get().errors.addAll(errorList);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            reference.get().adapter.notifyDataSetChanged();
        }
    }

    /* If the activity will receive updates so new errors can be added
    private static class GetErrorTask extends AsyncTask<String, Void, Void> {
        private WeakReference<ErrorsActivity> reference;
        private InputStreamReader reader;

        GetErrorTask(ErrorsActivity activity, InputStreamReader reader) {
            reference = new WeakReference<>(activity);
            this.reader = reader;
        }

        @Override
        protected Void doInBackground(String... errorCodes) {
            try {
                ObdError error = ObdErrorReader.getErrorDefinition(reader, errorCodes[0]);
                reference.get().errors.add(error);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            reference.get().adapter.notifyItemInserted(reference.get().errors.size() - 1);
        }
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errors);

        slideUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

        RecyclerView recyclerView = findViewById(R.id.errors_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ErrorAdapter(errors);
        recyclerView.setAdapter(adapter);

        //TODO: Here goes actual OBD logic
        fillInTestData();

        locationManager = new LocationManager<>(this);
        locationManager.requestUserLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (nearbyPlaces != null)
            nearbyPlaces.cancelErrors();
    }

    /*
    private void onCreate() {
        Trip trip;
        if (getIntent().getExtras() == null) {
            trip = TripRecord.getInstance().createDbObject();
        } else {
            //TODO: get trip info from the db
            //getIntent().getExtras().getInt("tripId");
            trip = TripRecord.getInstance().createDbObject();
        }

        String[] troubleCodes = trip.troubleCodes.split(",");
        List<String> permanentTroubleCodes = Arrays.asList(trip.permanentTroubleCodes.split(
                ","));

        RecyclerView recyclerView = findViewById(R.id.errors_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {
            InputStream codesDb = getAssets().open("tc.json",
                    AssetManager.ACCESS_BUFFER);
            InputStreamReader reader = new InputStreamReader(codesDb, "UTF-8");
            new GetErrorsTask(recyclerView, reader).execute(troubleCodes);
        } catch (IOException ioe) {
            L.e("IOException", "Can't get error codes definitions");
        }
    }
    */

    @Override
    public void onReceiveUserLocation(Location location) {
        if (location == null) {
            showErrorPopup(getString(R.string.no_location_data));
            return;
        }

        nearbyPlaces = NearbyPlaces.getInstance();
        nearbyPlaces.setUserCoordinates(location.getLatitude(), location.getLongitude());

        RequestQueue queue = QueueSingleton.getInstance(getApplicationContext()).getQueue();
        Handler responseHandler = new ResponseHandler(this);

        if (isOnline(this)) {
            if (nearbyPlaces.shouldFetch()) {
                nearbyPlaces.setHandler(responseHandler);
                nearbyPlaces.setRequestQueue(queue);
                nearbyPlaces.fetchData();
            } else {
                showServicesNearby();
            }
        } else {
            responseHandler.postDelayed(() ->
                            showErrorPopup(getString(R.string.no_internet_connection)), 2000);
        }
    }

    private void fillInTestData() {
        String[] errorCodes = {"P0021", "P2021"};

        try {
            InputStream codesDb = getAssets().open("tc.json",
                    AssetManager.ACCESS_BUFFER);
            InputStreamReader reader = new InputStreamReader(codesDb, "UTF-8");
            new GetErrorsTask(this, reader).execute(errorCodes);
        } catch (IOException ioe) {
            L.e("IOException", "Can't open trouble codes file");
        }
    }

    public void startPlacesActivity(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    private void showServicesNearby() {
        int serviceCount = nearbyPlaces.getPlaces().size();
        StringBuilder stringBuilder = new StringBuilder();

        if (serviceCount > 0) {
            stringBuilder.append(getResources().getString(R.string.services_nearby));
            stringBuilder.append(" ");
            stringBuilder.append(serviceCount);
            stringBuilder.append(" ");
            stringBuilder.append(getResources().getString(R.string.sc));
        } else {
            stringBuilder.append(getResources().getString(R.string.view_all_services));
        }

        showPopup(stringBuilder.toString());
    }

    private void showPopup(String text) {
        View holder = findViewById(R.id.services_nearby_holder);
        TextView textView = findViewById(R.id.services_nearby_text);
        Drawable background = ContextCompat.getDrawable(this,
                R.drawable.gradient_background);

        holder.setBackground(background);
        holder.setOnClickListener(this::startPlacesActivity);
        textView.setText(text);
        if (holder.getVisibility() != View.VISIBLE) {
            holder.startAnimation(slideUp);
            holder.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorPopup(String text) {
        View holder = findViewById(R.id.services_nearby_holder);
        TextView textView = findViewById(R.id.services_nearby_text);
        Drawable background = ContextCompat.getDrawable(this,
                R.drawable.error_background);

        holder.setBackground(background);
        holder.setOnClickListener((v) -> {
            hidePopup();
            locationManager.requestUserLocation();
        });
        textView.setText(text);

        if (holder.getVisibility() != View.VISIBLE) {
            holder.startAnimation(slideUp);
            holder.setVisibility(View.VISIBLE);
        }
    }

    private void hidePopup() {
        View holder = findViewById(R.id.services_nearby_holder);
        holder.startAnimation(slideDown);
        holder.setVisibility(View.GONE);
    }
}
