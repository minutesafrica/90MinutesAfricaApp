package com.ninetyminutes.africa;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.FootballService;

import org.json.JSONArray;
import org.json.JSONObject;

public class MatchesActivity extends AppCompatActivity {

    private LinearLayout matchesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        matchesContainer = findViewById(R.id.matchesContainer);

        loadFixtures();
    }

    private void loadFixtures() {
        matchesContainer.removeAllViews();

        addMessage("Inapakia ratiba...");

        FootballService.getFixtures(new FootballService.Callback() {
            @Override
            public void onSuccess(JSONArray data) {
                runOnUiThread(() -> {
                    matchesContainer.removeAllViews();

                    if (data.length() == 0) {
                        addMessage("Hakuna ratiba kwa sasa.");
                        return;
                    }

                    for (int i = 0; i < data.length(); i++) {
                        try {
                            JSONObject match = data.getJSONObject(i);
                            addMatchCard(match);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    matchesContainer.removeAllViews();
                    addMessage("Imeshindikana kupakia ratiba.\n" + error);
                });
            }
        });
    }

    private void addMatchCard(JSONObject match) throws Exception {
        String home = match.optString("home_team", "Home");
        String away = match.optString("away_team", "Away");
        String competition = match.optString("competition", "");
        String date = match.optString("match_date", "");
        String time = match.optString("match_time", "");
        String venue = match.optString("venue", "");
        String status = match.optString("status", "");
        boolean isLive = match.optBoolean("is_live", false);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);

        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(16);
        bg.setStroke(1, 0xFF262626);
        card.setBackground(bg);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);
        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);

        TextView competitionView = createText(
                competition.toUpperCase(),
                12,
                "#E30613"
        );
        competitionView.setTypeface(null, 1);

        TextView dateView = createText(
                date + (time.isEmpty() ? "" : "  •  " + time),
                13,
                "#999999"
        );
        dateView.setPadding(0, 5, 0, 0);

        TextView teamsView = createText(
                home + "    vs    " + away,
                16,
                "#FFFFFF"
        );
        teamsView.setTypeface(null, 1);
        teamsView.setGravity(android.view.Gravity.CENTER);
        teamsView.setPadding(0, 16, 0, 16);

        card.addView(competitionView);
        card.addView(dateView);
        card.addView(teamsView);

        if (!venue.trim().isEmpty()) {
            View divider = new View(this);
            divider.setBackgroundColor(0xFF222222);
            card.addView(
                    divider,
                    new LinearLayout.LayoutParams(-1, 1)
            );

            TextView venueView = createText(
                    venue,
                    12,
                    "#888888"
            );
            venueView.setPadding(0, 10, 0, 0);
            card.addView(venueView);
        }

        String finalStatus =
                isLive
                        ? "LIVE"
                        : (status.isEmpty() ? "INAKUJA" : status);

        TextView statusView = createText(
                finalStatus.toUpperCase(),
                11,
                isLive ? "#E53935" : "#999999"
        );
        statusView.setTypeface(null, 1);
        statusView.setPadding(0, 10, 0, 0);

        card.addView(statusView);
        matchesContainer.addView(card);
    }

    private TextView createText(
            String text,
            int size,
            String color
    ) {
        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(android.graphics.Color.parseColor(color));

        return view;
    }

    private void addMessage(String message) {
        TextView view = createText(
                message,
                15,
                "#AAAAAA"
        );

        view.setPadding(10, 30, 10, 30);

        matchesContainer.addView(view);
    }
}
