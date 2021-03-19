package com.dblappdev.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.APIService;
import com.dblappdev.app.dataClasses.LoggedInUser;

public class LoginActivity extends AppCompatActivity {
    boolean isRequestHappening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Event handler for the Login button on the login screen
     *
     * @param view The View instance of the button that was pressed
     *
     * First the following requirements should be checked:
     *             username_input_text must be between 1 and 30 inclusive ASCII characters
     *             password_in_text must be between 7 and 30 inclusive ASCII characters
     * If any of these fail to hold, this method should create and show a Toast message notifying
     *             the user that the input is invalid.
     * Otherwise, if all of the constraints have been met, this method should make a register
     *             APIRequest ({@link com.dblappdev.app.api.APIService#login(String, String,
     *             Context, APIResponse)}).
     * If this request returns an error, this method should create and show a Toast message
     *             containing the received error message.
     * If this request is successful, this method should store the received apiKey in the
     *             LoggedInUser class ({@link com.dblappdev.app.dataClasses.LoggedInUser}) and
     *             create a new HomeScreen activity, navigate towards that activity and finally
     *             finish this activity.
     */
    public void onLoginClick(View view) {

        // Obtain the values of the text fields
        EditText usernameET = findViewById(R.id.username_input_text);
        EditText passwordET = findViewById(R.id.password_in_text);

        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        if (isValidInput(username, password)) {
            // Create a register API request if there is none going on
            if (!isRequestHappening) {
                isRequestHappening = true;
                loginAPICall(username, password, this);
            }
        } else {
            showErrorToast("invalid input");
        }
    }

    void loginAPICall(String username, String password, Context context) {
        APIService.login(username, password, context,
                new APIResponse<String>() {
                    @Override
                    //TODO: remove email requirement for login request
                    public void onResponse(String data) {
                        //Save the API key in the LoggedInUser singleton
                        LoggedInUser.logIn(data, username, "");
                        // Create a new HomeScreen activity, navigate there and finish this activity
                        Intent homeScreenIntent = new Intent(context, HomeScreenActivity.class);
                        startActivity(homeScreenIntent);
                        finish();
                        // Allow a new request to be made
                        isRequestHappening = false;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error, String errorMessage) {
                        // Show a toast message notifying the user that the input was invalid
                        showErrorToast(errorMessage);
                        // Allow a new request to be made
                        isRequestHappening = false;
                    }
                });

    }

    /**
     * Event handler for the Register button on the login screen
     *
     * @param view The View instance of the button that was pressed
     *
     * This method should create a new register screen activity, navigate towards that activity and
     *             finally finish this activity.
     */
    public void onRegisterClick(View view) {

        // Redirect to the Register screen
        // Start this as a new activity to make sure pressing the back button takes you back to this
        // screen again
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    /**
     * Checks whether the supplied strings make up valid input for logging in
     * @return {@code true} iff the strings conform to the specification explained at {@link #onLoginClick(View)}
     */
    private boolean isValidInput(String username, String password) {
        boolean validUsername = username.length() >= 1 && username.length() <= 30 && isASCII(username);
        boolean validPassword = password.length() >= 7 && password.length() <= 30 && isASCII(password);

        return validUsername && validPassword;
    }

    /**
     * TODO: Make general method
     * Shows a toast containing the provided error message
     * @param errorMessage String to be displayed in the toast message
     */
    private void showErrorToast(String errorMessage) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, errorMessage, duration);
        toast.show();
    }

    /**
     * TODO: Copied this one from APIService, should maybe look into refactoring that to prevent copying
     * Returns if string only contains ASCII characters
     *
     * @param string string to be checked
     * @return true if string only contains ASCII, else false
     */
    private static boolean isASCII(String string) {
        for (char c : string.toCharArray()) {
            // The characters between 0 - 127 are the ASCII characters
            if (c >= 128) {
                return false;
            }
        }
        return true;
    }
}