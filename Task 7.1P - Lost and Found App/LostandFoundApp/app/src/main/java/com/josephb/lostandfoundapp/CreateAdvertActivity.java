package com.josephb.lostandfoundapp;

import android.app.DatePickerDialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioButton rbLost;
    private EditText etName, etPhone, etDescription, etDate, etLocation;
    private Spinner spCategory;
    private ImageView ivPreview;

    private String pickedImagePath; // absolute path after copy into internal storage

    private ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        rbLost = findViewById(R.id.rb_lost);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etDescription = findViewById(R.id.et_description);
        etDate = findViewById(R.id.et_date);
        etLocation = findViewById(R.id.et_location);
        spCategory = findViewById(R.id.sp_category);
        ivPreview = findViewById(R.id.iv_preview);
        Button btnPickDate = findViewById(R.id.btn_pick_date);
        Button btnPickImage = findViewById(R.id.btn_pick_image);
        Button btnSave = findViewById(R.id.btn_save);

        // Category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Item.CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Image picker — Photo Picker; no permissions needed on API 24+
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                this::onImagePicked);

        btnPickImage.setOnClickListener(v ->
                pickImageLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

        // Date picker
        btnPickDate.setOnClickListener(v -> showDatePicker());
        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> onSaveClicked());
    }

    private void onImagePicked(Uri uri) {
        if (uri == null) return; // user cancelled

        String path = ImageStorageUtil.copyUriToInternal(this, uri);
        if (path == null) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return;
        }
        pickedImagePath = path;
        ivPreview.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dlg = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String formatted = String.format(Locale.US,
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDate.setText(formatted);
                }, y, m, d);
        dlg.show();
    }

    private void onSaveClicked() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = (String) spCategory.getSelectedItem();
        String postType = rbLost.isChecked() ? Item.TYPE_LOST : Item.TYPE_FOUND;

        // Validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (location.isEmpty()) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }
        if (pickedImagePath == null) {
            Toast.makeText(this, "Please attach an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Item item = new Item();
        item.setPostType(postType);
        item.setName(name);
        item.setPhone(phone);
        item.setDescription(description);
        item.setIncidentDate(date);
        item.setLocation(location);
        item.setCategory(category);
        item.setImagePath(pickedImagePath);
        item.setCreatedAt(System.currentTimeMillis());

        DatabaseHelper db = new DatabaseHelper(this);
        long id = db.insertItem(item);

        if (id == -1) {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}