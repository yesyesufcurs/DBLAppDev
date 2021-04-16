package com.dblappdev.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayImageActivity extends AppCompatActivity {

    ImageView imageView;

    /**
     * This activity should simply show the image that has been supplied through the extras of the
     * intent that launched this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard Android activity code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        // Find the UI element in which the image should be shown
        imageView = findViewById(R.id.mimageView);
        // Obtain the image path from the intent extras and decode it
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        // Show the decoded image in the image view UI element
        imageView.setImageBitmap(bitmap);
    }
}