package com.ninetyminutes.africa;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationsActivity extends AppCompatActivity {

    private LinearLayout notificationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationsContainer =
                findViewById(R.id.notificationsContainer);

        findViewById(R.id.notificationBack)
                .setOnClickListener(v -> finish());

        loadNotifications();
    }

    private void loadNotifications() {

        new Thread(() -> {

            try {

                String endpoint =
                        "/rest/v1/notifications" +
                        "?select=*" +
                        "&order=created_at.desc" +
                        "&limit=50";

                String response =
                        SupabaseClient.get(endpoint);

                JSONArray notifications =
                        new JSONArray(response);

                runOnUiThread(() ->
                        displayNotifications(notifications)
                );

            } catch (Exception e) {

                runOnUiThread(() ->
                        showMessage(
                                "Imeshindikana kupakia notifications."
                        )
                );
            }
        }).start();
    }

    private void displayNotifications(
            JSONArray notifications
    ) {

        notificationsContainer.removeAllViews();

        if (notifications.length() == 0) {

            showMessage(
                    "Hakuna notifications kwa sasa."
            );

            return;
        }

        for (int i = 0;
             i < notifications.length();
             i++) {

            try {

                JSONObject item =
                        notifications.getJSONObject(i);

                String title =
                        item.optString(
                                "title",
                                "Notification"
                        );

                String message =
                        item.optString(
                                "message",
                                ""
                        );

                String type =
                        item.optString(
                                "type",
                                "general"
                        );

                String createdAt =
                        item.optString(
                                "created_at",
                                ""
                        );

                LinearLayout card =
                        new LinearLayout(this);

                card.setOrientation(
                        LinearLayout.VERTICAL
                );

                card.setPadding(
                        20,
                        18,
                        20,
                        18
                );

                card.setBackgroundColor(
                        Color.parseColor("#101010")
                );

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                params.setMargins(
                        0,
                        0,
                        0,
                        14
                );

                card.setLayoutParams(params);

                TextView typeView =
                        new TextView(this);

                typeView.setText(
                        type.toUpperCase()
                );

                typeView.setTextColor(
                        Color.parseColor("#E30613")
                );

                typeView.setTextSize(11);
                typeView.setTypeface(null, 1);

                TextView titleView =
                        new TextView(this);

                titleView.setText(title);
                titleView.setTextColor(Color.WHITE);
                titleView.setTextSize(17);
                titleView.setTypeface(null, 1);
                titleView.setPadding(0, 7, 0, 7);

                TextView messageView =
                        new TextView(this);

                messageView.setText(message);
                messageView.setTextColor(
                        Color.parseColor("#CCCCCC")
                );

                messageView.setTextSize(14);

                TextView dateView =
                        new TextView(this);

                dateView.setText(
                        formatDate(createdAt)
                );

                dateView.setTextColor(
                        Color.parseColor("#777777")
                );

                dateView.setTextSize(11);
                dateView.setPadding(0, 10, 0, 0);

                card.addView(typeView);
                card.addView(titleView);
                card.addView(messageView);
                card.addView(dateView);

                notificationsContainer.addView(card);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void showMessage(String message) {

        notificationsContainer.removeAllViews();

        TextView view =
                new TextView(this);

        view.setText(message);
        view.setTextColor(
                Color.parseColor("#AAAAAA")
        );

        view.setTextSize(15);
        view.setGravity(Gravity.CENTER);
        view.setPadding(20, 50, 20, 50);

        notificationsContainer.addView(view);
    }

    private String formatDate(String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "";
        }

        if (value.length() >= 16) {

            return value.substring(
                    0,
                    16
            ).replace(
                    "T",
                    " "
            );
        }

        return value;
    }
}
