package net.cararea.inspector.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    public final static int REQUEST_PERMISSIONS = 1;

    private Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    public void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(activity,
                permissions, REQUEST_PERMISSIONS);
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
