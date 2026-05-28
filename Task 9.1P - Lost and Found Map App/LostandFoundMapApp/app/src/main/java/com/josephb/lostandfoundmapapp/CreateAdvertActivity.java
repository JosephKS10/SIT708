package com.josephb.lostandfoundmapapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.josephb.lostandfoundmapapp.db.AppDatabase;
import com.josephb.lostandfoundmapapp.db.Item;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup rgType;
    private EditText etName, etPhone, etDescription, etDate, etLocation;
    private FusedLocationProviderClient fusedClient;

    private double pickedLat = 0, pickedLng = 0;
    private boolean locationPicked = false;

    private final ActivityResultLauncher<Intent> placePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String name = result.getData().getStringExtra("name");
                    pickedLat = result.getData().getDoubleExtra("lat", 0);
                    pickedLng = result.getData().getDoubleExtra("lng", 0);
                    locationPicked = true;
                    etLocation.setText(name);
                }
            });

    private final ActivityResultLauncher<String> permLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) fetchCurrentLocation();
                else Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        rgType = findViewById(R.id.rgType);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnSave = findViewById(R.id.btnSave);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        etDate.setOnClickListener(v -> showDatePicker());

        etLocation.setOnClickListener(v ->
                placePickerLauncher.launch(new Intent(this, PlacePickerActivity.class)));

        btnGetLocation.setOnClickListener(v -> requestLocation());

        btnSave.setOnClickListener(v -> save());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            String s = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            etDate.setText(s);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            fetchCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc != null) {
                pickedLat = loc.getLatitude();
                pickedLng = loc.getLongitude();
                locationPicked = true;
                etLocation.setText(String.format(Locale.US, "Current (%.5f, %.5f)", pickedLat, pickedLng));
            } else {
                Toast.makeText(this, "Could not get location, try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String loc = etLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || desc.isEmpty() || date.isEmpty() || !locationPicked) {
            Toast.makeText(this, "Fill all fields and pick a location", Toast.LENGTH_SHORT).show();
            return;
        }

        Item item = new Item();
        item.type = rgType.getCheckedRadioButtonId() == R.id.rbLost ? "LOST" : "FOUND";
        item.name = name;
        item.phone = phone;
        item.description = desc;
        item.date = date;
        item.locationName = loc;
        item.latitude = pickedLat;
        item.longitude = pickedLng;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.get(this).itemDao().insert(item);
            runOnUiThread(() -> {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
