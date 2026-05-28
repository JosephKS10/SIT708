package com.josephb.lostandfoundmapapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.josephb.lostandfoundmapapp.db.AppDatabase;
import com.josephb.lostandfoundmapapp.db.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fusedClient;
    private LatLng userLocation;
    private List<Item> allItems = new ArrayList<>();
    private int radiusKm = 5;

    private TextView tvRadius, tvCount;

    private final ActivityResultLauncher<String> permLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) fetchUserLocationAndRender();
                else {
                    Toast.makeText(this, "Location permission denied, showing all items", Toast.LENGTH_SHORT).show();
                    renderAll();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        tvRadius = findViewById(R.id.tvRadius);
        tvCount = findViewById(R.id.tvCount);
        SeekBar seekRadius = findViewById(R.id.seekRadius);

        seekRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                radiusKm = progress + 1;
                tvRadius.setText(radiusKm + " km");
                redraw();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Item> items = AppDatabase.get(this).itemDao().getAll();
            runOnUiThread(() -> {
                allItems = items;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    fetchUserLocationAndRender();
                } else {
                    permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            });
        });
    }

    private void fetchUserLocationAndRender() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            renderAll();
            return;
        }
        fusedClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc != null) {
                userLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                redraw();
            } else {
                Toast.makeText(this, "No location available, showing all items", Toast.LENGTH_SHORT).show();
                renderAll();
            }
        });
    }

    private void renderAll() {
        if (map == null) return;
        map.clear();
        for (Item it : allItems) {
            LatLng pos = new LatLng(it.latitude, it.longitude);
            float hue = "LOST".equals(it.type) ? BitmapDescriptorFactory.HUE_VIOLET : BitmapDescriptorFactory.HUE_AZURE;
            map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(it.type + ": " + it.name)
                    .snippet(it.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
        }
        tvCount.setText(allItems.size() + " items (no location filter)");
        if (!allItems.isEmpty()) {
            Item first = allItems.get(0);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(first.latitude, first.longitude), 10));
        }
    }

    private void redraw() {
        if (map == null) return;
        map.clear();

        if (userLocation == null) {
            renderAll();
            return;
        }

        map.addCircle(new CircleOptions()
                .center(userLocation)
                .radius(radiusKm * 1000.0)
                .strokeColor(Color.BLACK)
                .strokeWidth(2f)
                .fillColor(Color.argb(20, 0, 0, 0)));

        map.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        int within = 0;
        for (Item it : allItems) {
            double dKm = haversineKm(userLocation.latitude, userLocation.longitude, it.latitude, it.longitude);
            if (dKm <= radiusKm) {
                within++;
                LatLng pos = new LatLng(it.latitude, it.longitude);
                float hue = "LOST".equals(it.type) ? BitmapDescriptorFactory.HUE_VIOLET : BitmapDescriptorFactory.HUE_AZURE;
                map.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(it.type + ": " + it.name)
                        .snippet(String.format(Locale.US, "%.2f km — %s", dKm, it.description))
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            }
        }

        tvCount.setText(within + " of " + allItems.size() + " items within " + radiusKm + " km");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomForRadius(radiusKm)));
    }

    private float zoomForRadius(int km) {
        if (km <= 2) return 14f;
        if (km <= 5) return 12f;
        if (km <= 15) return 11f;
        if (km <= 30) return 10f;
        return 9f;
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
