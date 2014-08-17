package com.lysdev.transperthcached;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.StringBuilder;
// import java.nio.charset.StandardCharsets;


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
}
