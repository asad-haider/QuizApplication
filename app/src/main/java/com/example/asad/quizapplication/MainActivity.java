package com.example.asad.quizapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createQuiz(View view) {
        startActivity(new Intent(this, CreateQuiz.class));
    }

    public void playQuiz(View view) {
        startActivity(new Intent(this, QuizScreen.class));
    }

    public void exitApplication(View view) {
        finish();
        System.exit(0);
    }
}
