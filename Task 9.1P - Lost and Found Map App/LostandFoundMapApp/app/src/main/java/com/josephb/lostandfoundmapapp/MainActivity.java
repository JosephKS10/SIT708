package com.josephb.lostandfoundmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreate = findViewById(R.id.btnCreate);
        Button btnShowAll = findViewById(R.id.btnShowAll);
        Button btnShowOnMap = findViewById(R.id.btnShowOnMap);

        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, CreateAdvertActivity.class)));

        btnShowAll.setOnClickListener(v ->
                startActivity(new Intent(this, AllItemsActivity.class)));

        btnShowOnMap.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));
    }
}
