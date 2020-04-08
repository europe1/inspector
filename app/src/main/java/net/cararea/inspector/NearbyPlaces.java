package net.cararea.inspector;

import android.os.AsyncTask;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.cararea.inspector.utils.GsonRequest;
import net.cararea.inspector.utils.L;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class NearbyPlaces {
    final static int MAP_ELEMENTS_READY = 0;
    private final static String TAG_NEARBY = "TagNearby";
    private final static String DEFAULT_ENCODING = "ASCII";
    private final static String URL = "https://overpass.kumi.systems/api/interpreter?";

    private static NearbyPlaces instance;

    private Handler handler;
    private RequestQueue requestQueue;
    private AsyncTask asyncTask;

    private boolean isFetchInProgress = false;

    private String placeFilter = "[shop=car_repair]";
    private Map<String, String> headers = new HashMap<>();

    private double[] latLng = new double[2];
    private List<Place> places = new ArrayList<>();

    private Response.Listener<JsonObject> responseListener = (JsonObject response) -> {
        if (response.isJsonNull() || !isFetchInProgress)
            return;

        for (JsonElement node : response.getAsJsonArray("elements")) {
            JsonObject element = node.getAsJsonObject();
            JsonObject tags = element.get("tags").getAsJsonObject();

            Place place = new Place(element.get("lat").getAsDouble(),
                    element.get("lon").getAsDouble());

            if (tags.has("name")) {
                place.setName(tags.get("name").getAsString());
            }

            places.add(place);
        }

        if (handler != null) {
            handler.sendEmptyMessage(MAP_ELEMENTS_READY);
        } else {
            L.i("You haven't set any message handlers for this response");
        }

        isFetchInProgress = false;
    };

    private static class AsyncFetchAddress extends AsyncTask<Place, Void, Void> {
        private RequestQueue queue;
        private Handler handler;

        AsyncFetchAddress(RequestQueue queue, Handler handler) {
            this.queue = queue;
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(Place... places) {
            for (Place place : places) {
                if (place.fetchAddress(queue, handler)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            return null;
        }
    }

    private Response.ErrorListener errorListener = (VolleyError error) -> {
        L.e(String.format("error %s", error.toString()));
    };

    static NearbyPlaces getInstance() {
        if (instance == null)
            instance = new NearbyPlaces();
        return instance;
    }

    private NearbyPlaces() {
        headers.put("Content-Type", "charset=ASCII");
    }

    void fetchData() {
        isFetchInProgress = true;

        StringBuilder dataBuilder = new StringBuilder("[out:json];node(around:1500,");
        dataBuilder.append(String.format(Locale.ENGLISH, "%f,%f", latLng[0], latLng[1]));
        dataBuilder.append(String.format(")%s;out;", placeFilter));

        String data;
        try{
            data = "data=".concat(URLEncoder.encode(dataBuilder.toString(), DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException uee) {
            L.e("UnsupportedEncodingException", "Can't encode string");
            return;
        }

        String url = URL.concat(data);

        GsonRequest<JsonObject> jsonRequest = new GsonRequest<>(
                Request.Method.GET, url, null, JsonObject.class, headers,
                responseListener, errorListener);
        jsonRequest.setTag(TAG_NEARBY);
        requestQueue.add(jsonRequest);
    }

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    void setRequestQueue(RequestQueue queue) {
        requestQueue = queue;
    }

    void setFilter(String filter) {
        placeFilter = filter;
    }

    void setUserCoordinates(double lat, double lng) {
        latLng[0] = lat;
        latLng[1] = lng;
    }

    void fetchAddresses() {
        Place[] placeArray = places.toArray(new Place[0]);
        asyncTask = new AsyncFetchAddress(requestQueue, handler).execute(placeArray);
    }

    List<Place> getPlaces() {
        return places;
    }

    //FIXME: it will always return true if there are no nearby places
    boolean shouldFetch() {
        return !isFetchInProgress && places.isEmpty();
    }

    void cancelErrors() {
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG_NEARBY);
            requestQueue = null;
        }

        if (handler != null) {
            handler.removeMessages(MAP_ELEMENTS_READY);
            handler = null;
        }
    }

    void cancelPlaces() {
        if (requestQueue != null) {
            requestQueue.cancelAll(Place.TAG_ADDRESS);
            requestQueue = null;
        }

        if (handler != null) {
            handler.removeMessages(Place.PLACE_ELEMENT_UPDATED);
            handler = null;
        }

        if (asyncTask != null)
            asyncTask.cancel(true);
    }
}
