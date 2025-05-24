package com.ptit.aird18bookingapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ptit.aird18bookingapp.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapViewActivity extends AppCompatActivity {

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_map_view);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);

        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            showUserLocation();
        }
    }

    private void showUserLocation() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();

        map.getOverlays().add(myLocationOverlay);

        myLocationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            GeoPoint userPoint = myLocationOverlay.getMyLocation();
            if (userPoint != null) {
                map.getController().setCenter(userPoint);

                // Thêm marker tại vị trí người dùng
                Marker userMarker = new Marker(map);
                userMarker.setPosition(userPoint);
                userMarker.setTitle("Vị trí của bạn");
                map.getOverlays().add(userMarker);

                // Ví dụ: tìm đích và vẽ tuyến đường
                searchLocationAndRoute(userPoint, "Ben Thanh Market");
            } else {
                Toast.makeText(this, "Không thể lấy vị trí người dùng", Toast.LENGTH_SHORT).show();
            }

        }));
    }

    private void searchLocationAndRoute(GeoPoint startPoint, String query) {
        new Thread(() -> {
            try {
                String url = "https://nominatim.openstreetmap.org/search?q=" +
                        query.replace(" ", "+") + "&format=json&limit=1";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "OSMDroidDemo")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) return;

                JSONArray results = new JSONArray(response.body().string());
                if (results.length() == 0) return;

                JSONObject location = results.getJSONObject(0);
                double lat = location.getDouble("lat");
                double lon = location.getDouble("lon");
                GeoPoint destination = new GeoPoint(lat, lon);

                runOnUiThread(() -> {
                    Marker marker = new Marker(map);
                    marker.setPosition(destination);
                    marker.setTitle("Đích: " + query);
                    map.getOverlays().add(marker);

                    getRouteAndDraw(startPoint, destination);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void getRouteAndDraw(GeoPoint start, GeoPoint end) {
        new Thread(() -> {
            try {
                String url = "http://router.project-osrm.org/route/v1/driving/"
                        + start.getLongitude() + "," + start.getLatitude() + ";"
                        + end.getLongitude() + "," + end.getLatitude()
                        + "?overview=full&geometries=geojson";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    JSONObject obj = new JSONObject(json);

                    JSONArray coords = obj.getJSONArray("routes")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONArray("coordinates");

                    List<GeoPoint> geoPoints = new ArrayList<>();
                    for (int i = 0; i < coords.length(); i++) {
                        JSONArray point = coords.getJSONArray(i);
                        double lon = point.getDouble(0);
                        double lat = point.getDouble(1);
                        geoPoints.add(new GeoPoint(lat, lon));
                    }

                    runOnUiThread(() -> {
                        Polyline line = new Polyline();
                        line.setPoints(geoPoints);
                        line.setColor(Color.BLUE);
                        line.setWidth(6.0f);
                        map.getOverlays().add(line);
                        map.invalidate();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showUserLocation();
        } else {
            Toast.makeText(this, "Vui lòng cấp quyền vị trí để hiển thị bản đồ", Toast.LENGTH_LONG).show();
        }
    }
}
