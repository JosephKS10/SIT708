package com.example.quizapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        btnStart = findViewById(R.id.btnStart);

        if (getIntent().hasExtra("USER_NAME")) {
            String existingName = getIntent().getStringExtra("USER_NAME");
            etName.setText(existingName);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etName.getText().toString().trim();

                if (userName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your name to continue", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    intent.putExtra("USER_NAME", userName);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}