package net.cararea.inspector;

import android.os.Handler;
import android.os.Message;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.cararea.inspector.utils.GsonRequest;
import net.cararea.inspector.utils.L;

import java.util.Locale;

class Place {
    final static int PLACE_ELEMENT_UPDATED = 1;
    final static String TAG_ADDRESS = "TagAddress";
    private final static String API_KEY = "44a3eafe14b9f6";
    private final static String URL =
            "https://eu1.locationiq.com/v1/reverse.php?format=json&accept_language=ru";

    private String name;
    private String address;
    private double lat;
    private double lng;

    private boolean fetched = false;

    private Response.ErrorListener errorListener = (VolleyError error) -> {
        L.e(String.format("error %s", error.toString()));
    };

    Place(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    String getAddress() {
        return address;
    }

    double getLatitude() {
        return lat;
    }

    double getLongitude() {
        return lng;
    }

    boolean fetchAddress(RequestQueue queue, Handler handler) {
        final int index = NearbyPlaces.getInstance().getPlaces().indexOf(this);
        String url = String.format(Locale.ENGLISH, "%s&lat=%f&lon=%f&key=%s",
                URL, lat, lng, API_KEY);

        GsonRequest<JsonObject> jsonRequest = new GsonRequest<>(Request.Method.GET, url,
                null, JsonObject.class, null, (JsonObject response) -> {
            if (response == null)
                return;

            JsonObject responseAddress = response.getAsJsonObject("address");

            JsonElement houseNumber = responseAddress.get("house_number");
            JsonElement street = responseAddress.get("road");
            JsonElement district = responseAddress.get("city_district");

            String address;
            if (street != null) {
                if (houseNumber != null)
                    address = String.format("%s, %s", street.getAsString(),
                            houseNumber.getAsString());
                else
                    address = street.getAsString();
            } else if (district != null) {
                address = district.getAsString();
            } else {
                address = null;
            }

            this.address = address;
            this.fetched = true;

            Message message = Message.obtain();
            message.what = PLACE_ELEMENT_UPDATED;
            message.arg1 = index;
            handler.sendMessage(message);
        }, errorListener);


        Cache.Entry requestCache = queue.getCache().get(jsonRequest.getCacheKey());

        jsonRequest.setTag(TAG_ADDRESS);
        queue.add(jsonRequest);

        return requestCache == null || requestCache.isExpired();
    }

    boolean isFetched() {
        return fetched;
    }
}
