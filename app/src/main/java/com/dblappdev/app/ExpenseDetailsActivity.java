package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseDetailsActivity extends AppCompatActivity {

    String currentImagePath = null;
    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);
    }

    /**
     * Event handler for the back button
     *
     * @param view The View instance of the button that was pressed
     */
    public void onBack(View view) {

        // Redirect to the group screen
        finish();
    }

    /**
     * Event handler for the back button
     *
     * @param view The View instance of the button that was pressed
     */
    public void onCheckmark(View view) {

        // Redirect to the select members screen
        Intent selectMembersIntent = new Intent(this, SelectMembersActivity.class);
        Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
        selectMembersIntent.putExtra("BitmapImage", bitmap);
        startActivity(selectMembersIntent);
        finish();
    }

    /**
     * Event handler for the capture image button
     * Captures an Image through an Intent
     *
     * @param view The View instance of the button that was pressed
     */
    public void onCaptureImage(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            imageFile = getImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageFile != null) {
            Uri imageUri = FileProvider.getUriForFile(
                    this,
                    "com.example.android.fileprovider",
                    imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, IMAGE_REQUEST);
        }
    }

    /**
     * Event handler for the display image button
     * Displays an Image
     *
     * @param view The View instance of the button that was pressed
     */
    public void onDisplayImage(View view) {
        Intent intent = new Intent(this, DisplayImageActivity.class);
        intent.putExtra("image_path", currentImagePath);
        startActivity(intent);
    }

    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }
}
