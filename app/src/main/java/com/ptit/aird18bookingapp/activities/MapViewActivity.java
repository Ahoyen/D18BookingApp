package com.ptit.aird18bookingapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import com.google.gson.Gson;
import com.ptit.aird18bookingapp.models.RoomDetail;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapViewActivity extends AppCompatActivity {

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private String destinationAddress;
    private GeoPoint userPoint;
    private Marker userMarker;
    private TextView txtName;
    private TextView distanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_map_view);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        }
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        txtName = findViewById(R.id.txtName);
        distanceView = findViewById(R.id.distanceView);


        destinationAddress = getIntent().getStringExtra("destination_address");

        String json = getIntent().getStringExtra("room_detail");
        if (json != null) {
            RoomDetail roomDetail = new Gson().fromJson(json, RoomDetail.class);
            if (roomDetail != null && roomDetail.location != null && !roomDetail.location.isEmpty()) {
                destinationAddress = roomDetail.location;
                Log.d("testlocation", roomDetail.location);
            } else {
                Toast.makeText(this, "Không có địa chỉ trong roomDetail", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không nhận được dữ liệu roomDetail", Toast.LENGTH_SHORT).show();
        }

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
            getUserLocationWithFusedApi();
        }
    }

    private void showUserLocation() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);
    }

    private void getUserLocationWithFusedApi() {
        FusedLocationProviderClient fusedClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d("testlocation", "Vị trí thực tế (Fused): " + userPoint);

                        if (userMarker == null) {
                            userMarker = new Marker(map);
                            userMarker.setTitle("Vị trí của bạn");
                        }

                        userMarker.setPosition(userPoint);
                        if (!map.getOverlays().contains(userMarker)) {
                            map.getOverlays().add(userMarker);
                        }

                        map.getController().setCenter(userPoint);
                        map.invalidate();
                    } else {
                        Toast.makeText(this, "Không lấy được vị trí người dùng (null)", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi khi lấy vị trí: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void searchLocationAndRoute(GeoPoint startPoint, String query) {
        new Thread(() -> {
            try {
                String url = "https://nominatim.openstreetmap.org/search?q=" + query.replace(" ", "+") + "&format=json&limit=1";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "OSMDroidDemo")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) return;

                JSONArray results = new JSONArray(response.body().string());
                Log.d("MapDebug", "Nominatim result: " + results.toString());
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

                    double distanceInMeters = obj.getJSONArray("routes")
                            .getJSONObject(0)
                            .getDouble("distance");

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

                        String distanceText = formatDistance(distanceInMeters);
                        distanceView.setText(distanceText);
                        txtName.setText("Đường đi đến " + destinationAddress);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private String formatDistance(double distanceMeters) {
        if (distanceMeters < 1000) {
            return (int) distanceMeters + " m";
        } else {
            double km = distanceMeters / 1000.0;
            return String.format("%.1f km", km);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }
    public void backClick(View view) {
        finish();
    }
    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Không làm gì cả khi nhấn nút back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showUserLocation();
            getUserLocationWithFusedApi();
        } else {
            Toast.makeText(this, "Vui lòng cấp quyền vị trí để hiển thị bản đồ", Toast.LENGTH_LONG).show();
        }
    }

    public void confirmed(View view) {
        Log.d("testlocation", "UserPoint khi bấm nút: " + userPoint);
        if (userPoint == null) {
            Toast.makeText(this, "Chưa xác định được vị trí của bạn", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destinationAddress == null || destinationAddress.isEmpty()) {
            Toast.makeText(this, "Không có địa chỉ đích để chỉ đường", Toast.LENGTH_SHORT).show();
            return;
        }

//        map.getOverlays().clear();
//        map.getOverlays().add(myLocationOverlay);
//        if (userMarker != null) {
//            map.getOverlays().add(userMarker);
//        }
        Log.d("MapDebug", "Clearing overlays...");
        map.getOverlays().clear();
        Log.d("MapDebug", "Adding myLocationOverlay...");
        map.getOverlays().add(myLocationOverlay);
        if (userMarker != null) {
            Log.d("MapDebug", "Adding userMarker...");
            map.getOverlays().add(userMarker);
        }
        Log.d("MapDebug", "Starting searchLocationAndRoute...");
        searchLocationAndRoute(userPoint, destinationAddress);
    }
}


