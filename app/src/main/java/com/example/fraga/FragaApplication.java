package com.example.fraga;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class FragaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
} 