package net.cararea.inspector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import net.cararea.inspector.utils.L;
import net.cararea.inspector.utils.LocationManager;
import net.cararea.inspector.utils.PermissionManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.cararea.inspector.utils.Util.isOnline;

public class InspectionActivity extends AppCompatActivity implements  OnMapReadyCallback,
        Callback<DirectionsResponse>, LocationManager.UserLocationListener {
    private final static String ACCESS_TOKEN = "pk.eyJ1IjoiZGVucHJvMTgiLCJhIjoiY2poZXhwMDh0MDgwODMwb3V3cjJvOXozcCJ9.5Iwse3YxPcGXsm6CeZQShw";
    private final static String STYLE_URL = "mapbox://styles/denpro18/cjr7e09th011o2rpbplpqa11n";

    private Bundle instanceState;
    private PermissionManager permissionManager;

    private NavigationMapRoute navigationMapRoute;
    private MapView mapView;

    private Point origin;
    private Point dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, ACCESS_TOKEN);
        setContentView(R.layout.activity_inspection);

        mapView = findViewById(R.id.mapView);
        permissionManager = new PermissionManager(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getDoubleArray("latLng") != null) {
            instanceState = savedInstanceState;
            double[] latLng = extras.getDoubleArray("latLng");
            loadMap();
            dest = Point.fromLngLat(latLng[1], latLng[0]);
        }

        // Resize view
        //mapView.getLayoutParams().height = 100;
        //mapView.requestLayout();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PermissionManager.REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap();
            } else {
                displayError(R.string.error_loading_map);
            }
        }
    }

    @Override
    public void onReceiveUserLocation(Location location) {
        if (location == null)
            return;

        origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
        if (dest != null) {
            requestDirectionsRoute(dest);
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(new Style.Builder().fromUrl(STYLE_URL), (Style style) -> {
            findViewById(R.id.map_error_container).setVisibility(View.GONE);

            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap,
                    R.style.CustomMapRoute, "admin-state-province");
            UiSettings settings = mapboxMap.getUiSettings();
            settings.setRotateGesturesEnabled(false);

            settings.setLogoGravity(Gravity.END ^ Gravity.BOTTOM);
            settings.setLogoMargins(0, 0, 1, 1);

            settings.setAttributionGravity(Gravity.END ^ Gravity.TOP);
            settings.setAttributionMargins(0, 5, 5, 0);

            //settings.setLogoEnabled(false);
            //settings.setAttributionEnabled(false);

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationComponent.activateLocationComponent(this, style);
            }

            //TODO: make gps icon smaller
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.GPS);

            LocationManager<InspectionActivity> locationManager =
                    new LocationManager<>(this);
            locationManager.requestUserLocation();
        });
    }

    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        if (response.body() != null && !response.body().routes().isEmpty()) {
            List<DirectionsRoute> routes = response.body().routes();
            navigationMapRoute.addRoute(routes.get(0));
        }
    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
        L.e(throwable.getMessage());
    }

    public void retryClick(View v) {
        loadMap();
    }

    public void startDiagnoseActivity(View v) {
        Intent intent = new Intent(this, DiagnoseActivity.class);
        startActivity(intent);
    }

    public void requestDirectionsRoute(Point destination) {
        MapboxDirections directions = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .accessToken(ACCESS_TOKEN)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .annotations(DirectionsCriteria.ANNOTATION_MAXSPEED)
                .steps(true)
                .build();

        directions.enqueueCall(this);
    }

    private void loadMap() {
        if (!isOnline(this)) {
            displayError(R.string.no_internet_connection);
        } else {
            permissionManager.requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void initMap() {
        mapView.onCreate(instanceState);
        mapView.getMapAsync(this);
    }

    private void displayError(int resId) {
        findViewById(R.id.map_error_container).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.map_error)).setText(resId);
    }
}
