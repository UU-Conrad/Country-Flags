package com.example.countryflags;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen); // Layout for splash screen

        // Set a delay of 2 seconds before navigating to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity after the delay
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);

                // Close the SplashScreen activity so the user can't go back to it
                finish();
            }
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}