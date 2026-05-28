package com.josephb.lostandfoundmapapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Locale;

public class PlacePickerActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedClient;

    private final ActivityResultLauncher<String> permLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) returnCurrentLocation();
                else Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        String apiKey = getString(R.string.app_name);
        try {
            android.content.pm.ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            apiKey = String.valueOf(ai.metaData.get("com.google.android.geo.API_KEY"));
        } catch (Exception ignored) {}

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
            autocompleteFragment.setHint("Search a place");

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng ll = place.getLatLng();
                    if (ll == null) {
                        Toast.makeText(PlacePickerActivity.this,
                                "No coordinates for that place", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String displayName = place.getName() != null ? place.getName() : place.getAddress();
                    returnResult(displayName, ll.latitude, ll.longitude);
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Toast.makeText(PlacePickerActivity.this,
                            "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        Button btnUseCurrent = findViewById(R.id.btnUseCurrent);
        btnUseCurrent.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                returnCurrentLocation();
            }
        });
    }

    private void returnCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc != null) {
                String name = String.format(Locale.US, "Current (%.5f, %.5f)",
                        loc.getLatitude(), loc.getLongitude());
                returnResult(name, loc.getLatitude(), loc.getLongitude());
            } else {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnResult(String name, double lat, double lng) {
        Intent data = new Intent();
        data.putExtra("name", name);
        data.putExtra("lat", lat);
        data.putExtra("lng", lng);
        setResult(RESULT_OK, data);
        finish();
    }
}
