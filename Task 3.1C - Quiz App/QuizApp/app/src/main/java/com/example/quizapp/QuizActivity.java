package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private TextView tvProgress, tvQuestion;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnSubmitNext;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int selectedOptionIndex = 0;
    private int score = 0;
    private String userName;
    private boolean isSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        userName = getIntent().getStringExtra("USER_NAME");

        initViews();
        loadQuestions();
        setQuestionData();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnSubmitNext = findViewById(R.id.btnSubmitNext);

        btnOption1.setOnClickListener(this);
        btnOption2.setOnClickListener(this);
        btnOption3.setOnClickListener(this);
        btnOption4.setOnClickListener(this);
        btnSubmitNext.setOnClickListener(this);
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question("What is the capital of Australia?", "Sydney", "Melbourne", "Canberra", "Perth", 3));
        questionList.add(new Question("Which programming language is used for Android?", "Java", "Swift", "Python", "C#", 1));
        questionList.add(new Question("What does XML stand for?", "Extra Modern Link", "Extensible Markup Language", "Example Markup Language", "X-Markup Language", 2));
        questionList.add(new Question("Which of these is not an Android component?", "Activity", "Service", "Intent", "Fragment", 3));
        questionList.add(new Question("Who owns Android?", "Apple", "Microsoft", "Google", "IBM", 3));
    }

    private void setQuestionData() {
        resetButtonColors();
        isSubmitted = false;
        selectedOptionIndex = 0;
        btnSubmitNext.setText("Submit");

        Question currentQuestion = questionList.get(currentQuestionIndex);

        tvQuestion.setText(currentQuestion.getQuestionText());
        btnOption1.setText(currentQuestion.getOption1());
        btnOption2.setText(currentQuestion.getOption2());
        btnOption3.setText(currentQuestion.getOption3());
        btnOption4.setText(currentQuestion.getOption4());

        progressBar.setProgress(currentQuestionIndex + 1);
        tvProgress.setText((currentQuestionIndex + 1) + "/" + questionList.size());
    }

    @Override
    public void onClick(View v) {
        if (isSubmitted && v.getId() != R.id.btnSubmitNext) {
            return;
        }

        if (v.getId() == R.id.btnOption1) {
            selectedOptionView(btnOption1, 1);
        } else if (v.getId() == R.id.btnOption2) {
            selectedOptionView(btnOption2, 2);
        } else if (v.getId() == R.id.btnOption3) {
            selectedOptionView(btnOption3, 3);
        } else if (v.getId() == R.id.btnOption4) {
            selectedOptionView(btnOption4, 4);
        } else if (v.getId() == R.id.btnSubmitNext) {

            if (!isSubmitted) {
                if (selectedOptionIndex == 0) {
                    Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show();
                } else {
                    checkAnswer();
                }
            } else {
                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    setQuestionData();
                } else {
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("USER_NAME", userName);
                    intent.putExtra("SCORE", score);
                    intent.putExtra("TOTAL_QUESTIONS", questionList.size());
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    private void selectedOptionView(Button btn, int optionIndex) {
        resetButtonColors();
        selectedOptionIndex = optionIndex;
        btn.setBackgroundColor(Color.parseColor("#000000"));
    }

    private void resetButtonColors() {
        btnOption1.setBackgroundColor(Color.parseColor("#FF6200EE")); // Default Purple
        btnOption2.setBackgroundColor(Color.parseColor("#FF6200EE"));
        btnOption3.setBackgroundColor(Color.parseColor("#FF6200EE"));
        btnOption4.setBackgroundColor(Color.parseColor("#FF6200EE"));
    }

    private void checkAnswer() {
        isSubmitted = true;
        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedOptionIndex == currentQuestion.getCorrectAnswerIndex()) {
            score++;
        } else {
            colorButton(selectedOptionIndex, "#F44336"); // Red
        }

        colorButton(currentQuestion.getCorrectAnswerIndex(), "#4CAF50"); // Green

        if (currentQuestionIndex == questionList.size() - 1) {
            btnSubmitNext.setText("Finish Quiz");
        } else {
            btnSubmitNext.setText("Go to Next Question");
        }
    }

    private void colorButton(int index, String colorHex) {
        int color = Color.parseColor(colorHex);
        switch (index) {
            case 1: btnOption1.setBackgroundColor(color); break;
            case 2: btnOption2.setBackgroundColor(color); break;
            case 3: btnOption3.setBackgroundColor(color); break;
            case 4: btnOption4.setBackgroundColor(color); break;
        }
    }
}