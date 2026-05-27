package com.josephb.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreate = findViewById(R.id.btn_create_advert);
        Button btnShow = findViewById(R.id.btn_show_items);

        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, CreateAdvertActivity.class)));

        btnShow.setOnClickListener(v ->
                startActivity(new Intent(this, ListActivity.class)));
    }
}