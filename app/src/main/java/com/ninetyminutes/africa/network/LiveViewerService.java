package com.ninetyminutes.africa.network;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class LiveViewerService {

    private static String sessionId;

    public interface Callback {
        void onSuccess(String message);
        void onError(String error);
    }

    public static String getSessionId() {

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        return sessionId;
    }

    private static String getCurrentTimestamp() {

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        Locale.US
                );

        format.setTimeZone(
                TimeZone.getTimeZone("UTC")
        );

        return format.format(new Date());
    }

    public static void recordLiveView(
            String matchId,
            Callback callback
    ) {

        new Thread(() -> {

            try {

                JSONObject body =
                        new JSONObject();

                body.put(
                        "p_match_id",
                        Long.parseLong(matchId)
                );

                SupabaseClient.rpc(
                        "record_live_view",
                        body.toString()
                );

                if (callback != null) {
                    callback.onSuccess(
                            "Live view recorded"
                    );
                }

            } catch (Exception e) {

                if (callback != null) {
                    callback.onError(
                            e.getMessage()
                    );
                }
            }

        }).start();
    }

    public static void updatePeakViewers(
            String matchId,
            Callback callback
    ) {

        new Thread(() -> {

            try {

                JSONObject body =
                        new JSONObject();

                body.put(
                        "p_match_id",
                        Long.parseLong(matchId)
                );

                SupabaseClient.rpc(
                        "update_peak_viewers",
                        body.toString()
                );

                if (callback != null) {
                    callback.onSuccess(
                            "Peak viewers updated"
                    );
                }

            } catch (Exception e) {

                if (callback != null) {
                    callback.onError(
                            e.getMessage()
                    );
                }
            }

        }).start();
    }

    public static void startWatching(
            String matchId,
            Callback callback
    ) {

        new Thread(() -> {

            try {

                JSONObject body =
                        new JSONObject();

                body.put(
                        "match_id",
                        matchId
                );

                body.put(
                        "session_id",
                        getSessionId()
                );

                body.put(
                        "watch_seconds",
                        0
                );

                body.put(
                        "last_seen",
                        getCurrentTimestamp()
                );

                SupabaseClient.post(
                        "/rest/v1/live_viewers",
                        body.toString()
                );

                if (callback != null) {
                    callback.onSuccess(
                            "Viewer started"
                    );
                }

            } catch (Exception e) {

                if (callback != null) {
                    callback.onError(
                            e.getMessage()
                    );
                }
            }

        }).start();
    }

    public static void updateWatching(
            String matchId,
            int watchSeconds,
            Callback callback
    ) {

        new Thread(() -> {

            try {

                String filter =
                        "/rest/v1/live_viewers" +
                        "?match_id=eq." + matchId +
                        "&session_id=eq." +
                        getSessionId();

                JSONObject body =
                        new JSONObject();

                body.put(
                        "watch_seconds",
                        watchSeconds
                );

                body.put(
                        "last_seen",
                        getCurrentTimestamp()
                );

                SupabaseClient.patch(
                        filter,
                        body.toString()
                );

                if (callback != null) {
                    callback.onSuccess(
                            "Viewer updated"
                    );
                }

            } catch (Exception e) {

                if (callback != null) {
                    callback.onError(
                            e.getMessage()
                    );
                }
            }

        }).start();
    }

    public static void endWatching(
            String matchId,
            int watchSeconds,
            Callback callback
    ) {

        new Thread(() -> {

            try {

                String filter =
                        "/rest/v1/live_viewers" +
                        "?match_id=eq." + matchId +
                        "&session_id=eq." +
                        getSessionId();

                JSONObject body =
                        new JSONObject();

                body.put(
                        "watch_seconds",
                        watchSeconds
                );

                body.put(
                        "last_seen",
                        getCurrentTimestamp()
                );

                body.put(
                        "ended_at",
                        getCurrentTimestamp()
                );

                SupabaseClient.patch(
                        filter,
                        body.toString()
                );

                if (callback != null) {
                    callback.onSuccess(
                            "Viewer ended"
                    );
                }

            } catch (Exception e) {

                if (callback != null) {
                    callback.onError(
                            e.getMessage()
                    );
                }
            }

        }).start();
    }
}
