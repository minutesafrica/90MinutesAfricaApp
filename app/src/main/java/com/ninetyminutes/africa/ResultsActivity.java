package com.ninetyminutes.africa;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.FootballService;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultsActivity extends AppCompatActivity {

    private LinearLayout resultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultsContainer = findViewById(R.id.resultsContainer);

        loadResults();
    }

    private void loadResults() {

        resultsContainer.removeAllViews();
        addMessage("Inapakia matokeo...");

        FootballService.getResults(new FootballService.Callback() {

            @Override
            public void onSuccess(JSONArray data) {

                runOnUiThread(() -> {

                    resultsContainer.removeAllViews();

                    if (data.length() == 0) {
                        addMessage("Hakuna matokeo kwa sasa.");
                        return;
                    }

                    for (int i = 0; i < data.length(); i++) {

                        try {
                            JSONObject result = data.getJSONObject(i);
                            addResultCard(result);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {

                runOnUiThread(() -> {

                    resultsContainer.removeAllViews();

                    addMessage(
                            "Imeshindikana kupakia matokeo.\n" + error
                    );
                });
            }
        });
    }

    private void addResultCard(JSONObject result) {

        String competition =
                result.optString("competition", "MECHI");

        String home =
                result.optString("home_team", "Home");

        String away =
                result.optString("away_team", "Away");

        String date =
                result.optString("match_date", "");

        String status =
                result.optString("status", "");

        int homeScore =
                result.optInt("home_score", 0);

        int awayScore =
                result.optInt("away_score", 0);

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                16, 16, 16, 16
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(21, 21, 21)
        );

        background.setCornerRadius(20);

        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0, 0, 0, 12
        );

        card.setLayoutParams(cardParams);

        TextView competitionView =
                createText(
                        competition,
                        12,
                        "#D4AF37"
                );

        competitionView.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView teamsView =
                createText(
                        home + "    " +
                        homeScore +
                        "  -  " +
                        awayScore +
                        "    " +
                        away,
                        17,
                        "#FFFFFF"
                );

        teamsView.setGravity(
                Gravity.CENTER
        );

        teamsView.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams teamsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        teamsParams.setMargins(
                0, 14, 0, 0
        );

        teamsView.setLayoutParams(
                teamsParams
        );

        TextView infoView =
                createText(
                        status +
                        (date.isEmpty()
                                ? ""
                                : "  •  " + date),
                        13,
                        "#AAAAAA"
                );

        infoView.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        infoParams.setMargins(
                0, 8, 0, 0
        );

        infoView.setLayoutParams(
                infoParams
        );

        card.addView(
                competitionView
        );

        card.addView(
                teamsView
        );

        card.addView(
                infoView
        );

        resultsContainer.addView(
                card
        );
    }

    private TextView createText(
            String text,
            int size,
            String color
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(
                Color.parseColor(color)
        );

        return view;
    }

    private void addMessage(
            String message
    ) {

        TextView view =
                createText(
                        message,
                        15,
                        "#AAAAAA"
                );

        view.setPadding(
                10, 30, 10, 30
        );

        resultsContainer.addView(
                view
        );
    }
}
