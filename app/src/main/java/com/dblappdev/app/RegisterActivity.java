package com.dblappdev.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.dblappdev.app.api.APIResponse;
import com.dblappdev.app.api.LoginService;
import com.dblappdev.app.dataClasses.LoggedInUser;
import com.dblappdev.app.gregservice.GregService;

public class RegisterActivity extends AppCompatActivity {

    // This boolean checks whether there is a current request going on
    // This is to prevent the user from sending a new request while waiting for a response
    private boolean isRequestHappening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Event handler for the Register button on the register screen
     * @param view The View instance of the button that was pressed
     * Firstly, this method should check for the following:
     *     username_input_text must be between 1 and 30 inclusive ASCII characters
     *     email_in_text must adhere to the regex presented at https://emailregex.com
     *     password_in_text must be between 7 and 30 inclusive ASCII characters
     *     password_confirm_in_text must be identical to password
     * If any of these fail to hold, this method should create and show a Toast message notifying
     *             the user that the input is invalid.
     * Otherwise, if all of the constraints have been met, this method should make a register
     *             APIRequest ({@link LoginService#register(String, String, String, Context, APIResponse)}).
     * If this request returns an error, this method should create and show a Toast message
     *             containing the received error message.
     * If this request is successful, this method should store the received apiKey in the
     *             LoggedInUser class ({@link com.dblappdev.app.dataClasses.LoggedInUser}) and create
     *             a new HomeScreen activity, navigate towards that activity and finally finish
     *             this activity.
     */
    public void onRegisterClick(View view) {

        // Obtain the values of the text fields
        EditText usernameET = findViewById(R.id.username_input_text);
        EditText emailET = findViewById(R.id.email_in_text);
        EditText passwordET = findViewById(R.id.password_in_text);
        EditText passwordConfirmET = findViewById(R.id.password_confim_in_text);

        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();

        // Check if the input conforms to the requirements
        if (isValidInput(username, email, password, passwordConfirm)) {
            // Create a register API request if there is none going on
            if (!isRequestHappening) {
                isRequestHappening = true;
                registerAPICall(username, password, email, this);
            }
        } else {
            // Show a toast message notifying the user that the input was invalid
            showErrorToast("Invalid input!");
        }

    }

    // Send the user back to the login screen when pressing the back button on their device
    @Override
    public void onBackPressed() {
        // Start a new intent to go back to the LoginScreen and finish this activity
        Intent loginScreenIntent = new Intent(this, LoginActivity.class);
        startActivity(loginScreenIntent);
        finish();
    }

    /**
     * Checks whether the supplied strings make up valid input for registering
     * @return {@code true} iff the strings conform to the specification explained at {@link #onRegisterClick(View)}
     */
    private boolean isValidInput(String username, String email, String password, String passwordConfirm) {
        boolean validUsername = username.length() >= 1 && username.length() < 30 && GregService.isASCII(username);
        boolean validEmail = email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}" +
                "~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\" +
                "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]" +
                "[0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:" +
                "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b" +
                "\\x0c\\x0e-\\x7f])+)\\])");
        boolean validPassword = password.length() >= 6 && password.length() < 30 && GregService.isASCII(password);
        boolean validPassWordConfirm = passwordConfirm.equals(password);

        return validUsername && validEmail && validPassword && validPassWordConfirm;
    }

    /**
     * Makes a register API request that saves the apiKey and moves to the HomeScreen activity
     * upon success, shows a toast containing the error message upon failure.
     * @param username String containing the username to be used in the request
     * @param password String containing the password to be used in the request
     * @param email String containing the email to be used in the request
     * @param context Context to be used in the request
     */
    private void registerAPICall(String username, String password, String email, Context context) {
        LoginService.register(username, password, email, context,
                new APIResponse<String>() {
                    @Override
                    public void onResponse(String data) {
                        // Save the API key in the LoggedInUser singleton
                        LoggedInUser.logIn(data, username, email);
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
     * Shows a toast containing the provided error message
     * TODO: REFACTORED
     * @param errorMessage String to be displayed in the toast message
     */
    private void showErrorToast(String errorMessage) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, errorMessage, duration);
        toast.show();
    }

}
