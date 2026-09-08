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
        card.setPadding(28, 24, 28, 24);

        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();

        background.setColor(android.graphics.Color.rgb(21, 21, 21));
        background.setCornerRadius(20);

        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 18);
        card.setLayoutParams(cardParams);

        TextView competitionView = createText(
                competition,
                12,
                "#D4AF37"
        );

        TextView dateView = createText(
                date + (time.isEmpty() ? "" : "  •  " + time),
                13,
                "#AAAAAA"
        );

        TextView teamsView = createText(
                home + "  vs  " + away,
                18,
                "#FFFFFF"
        );
        teamsView.setTypeface(null, android.graphics.Typeface.BOLD);

        card.addView(competitionView);
        card.addView(dateView);

        LinearLayout.LayoutParams teamParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        teamParams.setMargins(0, 14, 0, 10);
        teamsView.setLayoutParams(teamParams);

        card.addView(teamsView);

        if (!venue.isEmpty()) {
            TextView venueView = createText(
                    "Uwanja: " + venue,
                    12,
                    "#AAAAAA"
            );
            card.addView(venueView);
        }

        String finalStatus;

        if (isLive) {
            finalStatus = "LIVE";
        } else if (!status.isEmpty()) {
            finalStatus = status;
        } else {
            finalStatus = "Inakuja";
        }

        TextView statusView = createText(
                finalStatus,
                12,
                isLive ? "#FF4444" : "#D4AF37"
        );

        statusView.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        statusParams.setMargins(0, 8, 0, 0);
        statusView.setLayoutParams(statusParams);

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
