package com.ninetyminutes.africa.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SupabaseClient {

    public static final String SUPABASE_URL =
            "https://udottjnuooljvchlxlzc.supabase.co";

    public static final String SUPABASE_KEY =
            "sb_publishable_SvIDjEWnF0Pw-tjeLa-Xpg_Ra0_QKss";

    public static String get(String endpoint) throws Exception {

        URL url = new URL(SUPABASE_URL + endpoint);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        setHeaders(connection);

        return readResponse(connection);
    }

    public static String post(
            String endpoint,
            String body
    ) throws Exception {

        return sendWithBody(
                "POST",
                endpoint,
                body
        );
    }

    public static String patch(
            String endpoint,
            String body
    ) throws Exception {

        return sendWithBody(
                "PATCH",
                endpoint,
                body
        );
    }

    public static String rpc(
            String functionName,
            String body
    ) throws Exception {

        return post(
                "/rest/v1/rpc/" + functionName,
                body
        );
    }

    private static String sendWithBody(
            String method,
            String endpoint,
            String body
    ) throws Exception {

        URL url = new URL(SUPABASE_URL + endpoint);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);

        setHeaders(connection);

        connection.setDoOutput(true);

        byte[] data =
                body.getBytes(StandardCharsets.UTF_8);

        connection.setRequestProperty(
                "Content-Length",
                String.valueOf(data.length)
        );

        try (OutputStream outputStream =
                     connection.getOutputStream()) {

            outputStream.write(data);
            outputStream.flush();
        }

        return readResponse(connection);
    }

    private static void setHeaders(
            HttpURLConnection connection
    ) {

        connection.setRequestProperty(
                "apikey",
                SUPABASE_KEY
        );

        connection.setRequestProperty(
                "Authorization",
                "Bearer " + SUPABASE_KEY
        );

        connection.setRequestProperty(
                "Content-Type",
                "application/json"
        );

        connection.setRequestProperty(
                "Accept",
                "application/json"
        );
    }

    private static String readResponse(
            HttpURLConnection connection
    ) throws Exception {

        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        int responseCode =
                connection.getResponseCode();

        BufferedReader reader;

        if (responseCode >= 200 &&
                responseCode < 300) {

            reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );

        } else {

            reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getErrorStream(),
                            StandardCharsets.UTF_8
                    )
            );
        }

        StringBuilder response =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        if (responseCode < 200 ||
                responseCode >= 300) {

            throw new Exception(
                    "Supabase error " +
                    responseCode +
                    ": " +
                    response
            );
        }

        return response.toString();
    }
}
