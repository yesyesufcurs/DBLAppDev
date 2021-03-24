package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;
import com.dblappdev.app.dataClasses.LoggedInUser;
import com.dblappdev.app.gregservice.GregService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExpenseDetailsActivity extends AppCompatActivity {

    String currentImagePath = null;
    int expenseGroupId;
    String MODE;
    int EXPENSE_ID;
    private static final int IMAGE_REQUEST = 1;
    public static Activity currentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        currentContext = this;

        if (LoggedInUser.getInstance() == null) {
            throw new RuntimeException("Something went wrong with logging in: no loggged in user" +
                    " found upon creation of the home screen!");
        }

        Bundle bundle = getIntent().getExtras();

        if (!getIntent().hasExtra("MODE")) {
            throw new RuntimeException("Something went wrong with opening the expense details: no " +
                    "mode selected.");
        }
        MODE = bundle.getString("MODE");
        if (!getIntent().hasExtra("EXPENSE_GROUP_ID")) {
            throw new RuntimeException("Something went wrong with opening the expense details: no " +
                    "expense group selected.");
        }
        expenseGroupId = bundle.getInt("EXPENSE_GROUP_ID");
        if (bundle.get("MODE").equals("EDIT")) {
            if (!getIntent().hasExtra("EXPENSE_ID")) {
                throw new RuntimeException("Something went wrong with opening the expense details: no " +
                        "expense selected.");
            }
            EXPENSE_ID = bundle.getInt("EXPENSE_ID");
            ((TextView) findViewById(R.id.topBarText)).setText("Edit expense");
            APIService.getExpenseDetails(LoggedInUser.getInstance().getApiKey(), "" + bundle.getInt("EXPENSE_ID"), this, new APIResponse<List<Map<String, String>>>() {
                @Override
                public void onResponse(List<Map<String, String>> data) {
                    Map<String, String> ourData = data.get(0);
                    ((EditText) findViewById(R.id.expense_name_input_text)).setText(ourData.get("title"));
                    ((EditText) findViewById(R.id.expense_price_input_text)).setText(ourData.get("amount"));
                    Bitmap picture = weblinkToBitmap(
                            "http://94.130.144.25:5000/getExpensePicture/" + ourData.get("id") + "/" + LoggedInUser.getInstance().getApiKey()
                    );
                    //create a file to write bitmap data
                    if (picture != null) {
                        try {
                            File f = new File(currentContext.getCacheDir(), "tempicture");
                            f.createNewFile();

                            //Convert bitmap to byte array

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            picture.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            //write the bytes in file
                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            currentImagePath = f.getAbsolutePath();
                        } catch (Exception e) {
                            throw new IllegalStateException("Cannot save picture");
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error, String errorMessage) {
                    GregService.showErrorToast(errorMessage, currentContext);
                }
            });
        }
    }

    /**
     * Returns a Bitmap object of the given weblink
     *
     * @param weblink link to picture to be converted
     * @return Bitmap object of the picture
     */
    private static Bitmap weblinkToBitmap(String weblink) throws IllegalStateException {
        // Convert link with website to bitmap object
        URL url = null;
        Bitmap bmp = null;
        try {
            url = new URL(weblink);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
        }
        return bmp;
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
        selectMembersIntent.putExtra("title", ((TextView) findViewById(R.id.expense_name_input_text)).getText().toString());
        selectMembersIntent.putExtra("price", ((TextView) findViewById(R.id.expense_price_input_text)).getText().toString());
        selectMembersIntent.putExtra("imagePath", currentImagePath);
        selectMembersIntent.putExtra("expenseGroupId", expenseGroupId);
        selectMembersIntent.putExtra("MODE", MODE);
        if (MODE.equals("EDIT")) {
            selectMembersIntent.putExtra("EXPENSE_ID", EXPENSE_ID);
        }
        startActivity(selectMembersIntent);
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
