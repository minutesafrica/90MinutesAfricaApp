package com.ninetyminutes.africa.network;

import com.ninetyminutes.africa.LiveMatch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LiveMatchService {

    public interface Callback {
        void onSuccess(List<LiveMatch> matches);
        void onError(String error);
    }

    public static void getLiveMatches(final Callback callback) {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/fixtures" +
                        "?select=*" +
                        "&is_live=eq.true" +
                        "&order=match_date.asc" +
                        "&order=match_time.asc";

                String response = SupabaseClient.get(endpoint);

                JSONArray array = new JSONArray(response);

                List<LiveMatch> matches = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject item = array.getJSONObject(i);

                    LiveMatch match = new LiveMatch(
                            item.optString("id", ""),
                            item.optString("home_team", ""),
                            item.optString("away_team", ""),
                            item.optString("match_time", ""),
                            item.optString("competition", ""),
                            item.optString("live_title", ""),
                            item.optString("stream_url", ""),
                            item.optString("stream_type", "hls"),
                            item.optBoolean("is_live", false)
                    );

                    matches.add(match);
                }

                callback.onSuccess(matches);

            } catch (Exception e) {

                callback.onError(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Imeshindikana kupakia Sport Live."
                );
            }

        }).start();
    }
}
