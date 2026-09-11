package com.ninetyminutes.africa;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsService {

    public interface Callback {
        void onSuccess(JSONArray news);
        void onError(String error);
    }

    public static void loadNews(Callback callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news?select=*&published=eq.true&order=created_at.desc"
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

                int responseCode = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                responseCode >= 200 && responseCode < 300
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

                if (responseCode >= 200 && responseCode < 300) {
                    JSONArray data = new JSONArray(result.toString());
                    callback.onSuccess(data);
                } else {
                    callback.onError(
                            "Supabase error " + responseCode
                    );
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
