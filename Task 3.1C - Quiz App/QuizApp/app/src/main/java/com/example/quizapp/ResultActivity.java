package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView tvCongrats, tvScore;
    private Button btnNewQuiz, btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvCongrats = findViewById(R.id.tvCongrats);
        tvScore = findViewById(R.id.tvScore);
        btnNewQuiz = findViewById(R.id.btnNewQuiz);
        btnFinish = findViewById(R.id.btnFinish);

        String userName = getIntent().getStringExtra("USER_NAME");
        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);

        tvCongrats.setText("Congratulations, " + userName + "!");
        tvScore.setText("Your Score: " + score + " / " + totalQuestions);

        btnNewQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);

                intent.putExtra("USER_NAME", userName);

                startActivity(intent);
                finish();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }
}