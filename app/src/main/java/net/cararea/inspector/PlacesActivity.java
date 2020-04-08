package net.cararea.inspector;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.RequestQueue;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlacesActivity extends AppCompatActivity {
    private List<Place> places;
    private NearbyPlaces nearbyPlaces;
    private PlaceAdapter adapter;

    private static class ResponseHandler extends Handler {
        private WeakReference<PlacesActivity> reference;

        ResponseHandler(PlacesActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Place.PLACE_ELEMENT_UPDATED) {
                reference.get().places = NearbyPlaces.getInstance().getPlaces();
                reference.get().adapter.notifyItemChanged(msg.arg1);
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        nearbyPlaces = NearbyPlaces.getInstance();
        places = nearbyPlaces.getPlaces();

        RecyclerView recyclerView = findViewById(R.id.places_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlaceAdapter(places, this);
        recyclerView.setAdapter(adapter);

        RequestQueue requestQueue = QueueSingleton.getInstance(getApplicationContext()).getQueue();
        nearbyPlaces.setRequestQueue(requestQueue);

        ResponseHandler responseHandler = new ResponseHandler(this);
        nearbyPlaces.setHandler(responseHandler);

        //TODO: improve this operation
        for (Place place : places) {
            if (!place.isFetched()) {
                nearbyPlaces.fetchAddresses();
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (nearbyPlaces != null)
            nearbyPlaces.cancelPlaces();
    }
}
