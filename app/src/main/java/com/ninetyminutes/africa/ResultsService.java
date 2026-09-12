package com.ninetyminutes.africa;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultsService {

    public interface Callback {
        void onSuccess(JSONArray results);
        void onError(String error);
    }

    public static void loadResults(Callback callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/results?select=*&order=match_date.desc"
                );

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty(
                        "apikey",
                        SupabaseConfig.KEY
                );
                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + SupabaseConfig.KEY
                );
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int code = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                code >= 200 && code < 300
                                        ? connection.getInputStream()
                                        : connection.getErrorStream()
                        )
                );

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();

                if (code >= 200 && code < 300) {
                    callback.onSuccess(
                            new JSONArray(result.toString())
                    );
                } else {
                    callback.onError("Supabase error " + code);
                }

            } catch (Exception e) {
                callback.onError(e.getMessage());

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
