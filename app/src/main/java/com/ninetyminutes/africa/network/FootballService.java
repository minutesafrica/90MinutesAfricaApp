package com.ninetyminutes.africa.network;

import org.json.JSONArray;
import org.json.JSONObject;

public class FootballService {

    public interface Callback {
        void onSuccess(JSONArray data);
        void onError(String error);
    }

    public static void getFixtures(Callback callback) {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/fixtures" +
                        "?select=*" +
                        "&order=match_date.desc" +
                        "&order=match_time.asc" +
                        "&limit=50";

                String response = SupabaseClient.get(endpoint);

                callback.onSuccess(new JSONArray(response));

            } catch (Exception e) {

                callback.onError(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Imeshindikana kupakia ratiba."
                );
            }

        }).start();
    }


    public static void getResults(Callback callback) {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/results" +
                        "?select=*" +
                        "&order=match_date.desc" +
                        "&limit=100";

                String response = SupabaseClient.get(endpoint);

                callback.onSuccess(new JSONArray(response));

            } catch (Exception e) {

                callback.onError(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Imeshindikana kupakia matokeo."
                );
            }

        }).start();
    }


    public static void getStandings(Callback callback) {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/standings" +
                        "?select=*" +
                        "&order=position.asc";

                String response = SupabaseClient.get(endpoint);

                callback.onSuccess(new JSONArray(response));

            } catch (Exception e) {

                callback.onError(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Imeshindikana kupakia msimamo."
                );
            }

        }).start();
    }


    public static void getLiveFixtures(Callback callback) {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/fixtures" +
                        "?select=*" +
                        "&is_live=eq.true" +
                        "&order=created_at.desc";

                String response = SupabaseClient.get(endpoint);

                callback.onSuccess(new JSONArray(response));

            } catch (Exception e) {

                callback.onError(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Imeshindikana kupakia LIVE."
                );
            }

        }).start();
    }
}
