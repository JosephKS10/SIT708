package com.josephb.lostandfoundapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    private long itemId = -1;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        itemId = getIntent().getLongExtra("item_id", -1);
        if (itemId == -1) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        item = db.getItemById(itemId);
        if (item == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvRelativeTime = findViewById(R.id.tv_relative_time);
        ImageView ivImage = findViewById(R.id.iv_image);
        TextView tvCategory = findViewById(R.id.tv_category);
        TextView tvLocation = findViewById(R.id.tv_location);
        TextView tvIncidentDate = findViewById(R.id.tv_incident_date);
        TextView tvPhone = findViewById(R.id.tv_phone);
        TextView tvDescription = findViewById(R.id.tv_description);
        Button btnRemove = findViewById(R.id.btn_remove);

        String prefix = Item.TYPE_LOST.equals(item.getPostType()) ? "Lost " : "Found ";
        tvTitle.setText(prefix + item.getName());
        tvRelativeTime.setText(TimeFormatter.relative(item.getCreatedAt()));
        tvCategory.setText("Category: " + item.getCategory());
        tvLocation.setText("Location: " + item.getLocation());
        tvIncidentDate.setText("Date: " + item.getIncidentDate());
        tvPhone.setText("Phone: " + (item.getPhone() == null || item.getPhone().isEmpty() ? "—" : item.getPhone()));
        tvDescription.setText(item.getDescription());

        if (item.getImagePath() != null) {
            ivImage.setImageBitmap(BitmapFactory.decodeFile(item.getImagePath()));
        }

        btnRemove.setOnClickListener(v -> confirmRemove());
    }

    private void confirmRemove() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_remove_title)
                .setMessage(R.string.confirm_remove_msg)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_remove, (dialog, which) -> doRemove())
                .show();
    }

    private void doRemove() {
        DatabaseHelper db = new DatabaseHelper(this);
        int rows = db.deleteItem(itemId);

        if (rows > 0) {
            // Best-effort: delete the image file from internal storage too
            if (item.getImagePath() != null) {
                File f = new File(item.getImagePath());
                if (f.exists()) f.delete();
            }
            Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to remove", Toast.LENGTH_SHORT).show();
        }
    }
}