package com.example.asad.quizapplication;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Asad on 5/27/2016.
 */
public class QuizGame extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}