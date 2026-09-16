package com.ninetyminutes.africa;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultsActivity extends AppCompatActivity {

    private LinearLayout allResultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        MobileAds.initialize(this, initializationStatus -> {});
        AdView resultsBannerAd = findViewById(R.id.resultsBannerAd);
        resultsBannerAd.loadAd(new AdRequest.Builder().build());

        allResultsContainer =
                findViewById(R.id.allResultsContainer);

        findViewById(R.id.resultsBack).setOnClickListener(
                v -> finish()
        );

        loadResults();
    }

    private void loadResults() {
        ResultsService.loadResults(new ResultsService.Callback() {

            @Override
            public void onSuccess(JSONArray results) {
                runOnUiThread(() -> displayResults(results));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        displayResults(new JSONArray())
                );
            }
        });
    }

    private void displayResults(JSONArray results) {
        allResultsContainer.removeAllViews();

        try {
            if (results.length() == 0) {
                TextView empty = new TextView(this);
                empty.setText("Hakuna matokeo kwa sasa.");
                empty.setTextColor(Color.WHITE);
                empty.setTextSize(15);
                empty.setPadding(16, 20, 16, 20);
                allResultsContainer.addView(empty);
                return;
            }

            for (int i = 0; i < results.length(); i++) {
                JSONObject match = results.getJSONObject(i);

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.HORIZONTAL);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(16, 16, 16, 16);
                card.setBackgroundColor(Color.rgb(34, 34, 34));

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                params.setMargins(0, 0, 0, 12);
                card.setLayoutParams(params);

                TextView home = createTeam(
                        match.optString("home_team", "")
                );

                TextView away = createTeam(
                        match.optString("away_team", "")
                );

                LinearLayout scoreBox =
                        new LinearLayout(this);

                scoreBox.setOrientation(
                        LinearLayout.VERTICAL
                );
                scoreBox.setGravity(Gravity.CENTER);

                TextView score = new TextView(this);
                score.setText(
                        match.optString("home_score", "0")
                                + " - "
                                + match.optString("away_score", "0")
                );
                score.setTextColor(Color.WHITE);
                score.setTextSize(20);
                score.setTypeface(
                        null,
                        Typeface.BOLD
                );
                score.setGravity(Gravity.CENTER);

                TextView status = new TextView(this);
                status.setText(
                        match.optString(
                                "status",
                                "Full Time"
                        )
                );
                status.setTextColor(Color.LTGRAY);
                status.setTextSize(10);
                status.setGravity(Gravity.CENTER);

                scoreBox.addView(score);
                scoreBox.addView(status);

                card.addView(home);
                card.addView(scoreBox);
                card.addView(away);

                allResultsContainer.addView(card);
            }

        } catch (Exception e) {
            TextView error = new TextView(this);
            error.setText("Imeshindwa kupakia matokeo.");
            error.setTextColor(Color.WHITE);
            error.setTextSize(15);
            error.setPadding(16, 20, 16, 20);
            allResultsContainer.addView(error);
        }
    }

    private TextView createTeam(String name) {
        TextView team = new TextView(this);

        team.setText(name);
        team.setTextColor(Color.WHITE);
        team.setTextSize(14);
        team.setTypeface(null, Typeface.BOLD);
        team.setGravity(Gravity.CENTER);

        team.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        return team;
    }
}
