package net.cararea.inspector.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.core.content.ContextCompat;

public class LocationManager<T extends Activity & LocationManager.UserLocationListener> {
    private T activity;
    private static Location lastLocation;

    public LocationManager(T activity) {
        this.activity = activity;
    }

    public void requestUserLocation() {
        FusedLocationProviderClient locationProvider = LocationServices.
                getFusedLocationProviderClient(activity);
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider.getLastLocation().addOnSuccessListener(activity,
                    this::onReceiveUserLocation);
        }
    }

    private void onReceiveUserLocation(Location location) {
        lastLocation = location;
        activity.onReceiveUserLocation(location);
    }

    public interface UserLocationListener {
        void onReceiveUserLocation(Location location);
    }

    public static Location getLastLocation() {
        return lastLocation;
    }
}
