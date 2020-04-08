package net.cararea.inspector.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;

public class Util {
    public static Calendar[] monthRange(Calendar date) {
        Calendar[] range = new Calendar[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1,
                0, 0, 0);
        range[0] = (Calendar) calendar.clone();
        calendar.add(Calendar.MONTH, 1);
        range[1] = (Calendar) calendar.clone();
        return range;
    }

    public static Calendar[] yearRange(Calendar date) {
        Calendar[] range = new Calendar[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.get(Calendar.YEAR), 0, 1,
                0, 0, 0);
        range[0] = (Calendar) calendar.clone();
        calendar.add(Calendar.YEAR, 1);
        range[1] = (Calendar) calendar.clone();
        return range;
    }

    public static double getDistanceBetweenTwoPoints(double lat1, double lng1,
                                                      double lat2, double lng2) {
        final double R = 6371e3; // Earth radius in meters
        double lat1rad = Math.toRadians(lat1);
        double lat2rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2-lat1);
        double deltaLng = Math.toRadians(lng2-lng1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1rad) *
                Math.cos(lat2rad) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }
}
