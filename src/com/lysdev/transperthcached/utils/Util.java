package com.lysdev.transperthcached.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.StringBuilder;

import android.app.Activity;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.View;


public class Util {
    private static final int BUFFER_SIZE = 4 * 1024;

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(
            inputStream,
            "UTF_8"
            // StandardCharsets.UTF_8
        );

        char[] buffer = new char[BUFFER_SIZE];
        int length;

        while ((length = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, length);
        }

        return builder.toString();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager;

        inputMethodManager = (InputMethodManager) activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        );
        View view = activity.getCurrentFocus();

        if (view == null) {
            Log.d("TransperthCached", "Couldn't find window to remove keyboard from");
        } else {
            IBinder bind = view.getWindowToken();

            // inputMethodManager.hideSoftInputFromWindow(bind, 0);
            inputMethodManager.hideSoftInputFromWindow(
                bind,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            );
        }
    }
}
