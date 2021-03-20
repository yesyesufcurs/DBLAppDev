package com.dblappdev.app.gregservice;

import android.content.Context;
import android.widget.Toast;

/**
 * Abstract class representing a service containing a number of methods that are commonly used
 * throughout the application in one central place
 */
public abstract class GregService {

    /**
     * Shows a toast containing the provided error message in the provided context
     * @param errorMessage String to be displayed in the toast message
     * @param context Context in which the toast message should be displayed
     */
    public static void showErrorToast(String errorMessage, Context context) {
        Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Returns whether the supplied string contains only ASCII characters
     * @param string String to be checked
     * @return {@code true} if string only contains ASCII characters, false otherwise
     */
    public static boolean isASCII(String string) {
        for (char c : string.toCharArray()) {
            // The characters between 0 - 127 are the ASCII characters
            if (c >= 128) {
                 return false;
            }
        }
        return true;
    }

}
