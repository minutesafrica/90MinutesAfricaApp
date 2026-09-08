package com.ninetyminutes.africa.network;

import org.json.JSONArray;

public class LiveService {

    public interface Callback {
        void onSuccess(JSONArray data);
        void onError(String error);
    }

    public static void getLiveFixtures(Callback callback) {
        getLiveMatches(callback);
    }

    public static void getLiveMatches(Callback callback) {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/fixtures" +
                        "?select=id,home_team,away_team,competition," +
                        "is_live,live_title,stream_url,stream_type," +
                        "total_views,peak_viewers,match_date,match_time" +
                        "&is_live=eq.true" +
                        "&order=created_at.desc";

                String response =
                        SupabaseClient.get(endpoint);

                callback.onSuccess(
                        new JSONArray(response)
                );

            } catch (Exception e) {

                callback.onError(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "LIVE haijapatikana."
                );
            }

        }).start();
    }
}
