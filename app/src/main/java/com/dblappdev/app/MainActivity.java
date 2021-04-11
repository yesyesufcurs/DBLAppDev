package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

public class MainActivity extends AppCompatActivity {

    /**
     * When first opening the app, this activity should ask for camera and storage permissions
     * Once these have been granted, this activity should show a splash screen for at most 3 seconds
     * before moving to the login screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // set splash screen

        // Ask for camera permissions if they have not yet been granted, otherwise go to the login
        // screen.
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        } else {
            goToLogin();
        }
    }

    /**
     * Callback method for when the user grants or denies the permissions that were asked for
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == 100) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        101);
            } else {
                goToLogin();
            }
        }
        if (requestCode == 101) {
            goToLogin();
        }
    }

    /**
     * This method should redirect the user to the login screen within 3 seconds of calling
     */
    private void goToLogin() {
        // Wait 2000ms before creating a new login activity event and finishing this one
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }

}
