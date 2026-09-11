package com.ninetyminutes.africa;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView featuredTitle;
    private TextView featuredMeta;
    private LinearLayout latestNewsContainer;
    private LinearLayout fixturesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        featuredTitle = findViewById(R.id.featuredTitle);
        featuredMeta = findViewById(R.id.featuredMeta);
        latestNewsContainer = findViewById(R.id.latestNewsContainer);
        fixturesContainer = findViewById(R.id.fixturesContainer);

        loadNews();
        loadFixtures();
    }

    private void loadNews() {
        NewsService.loadNews(new NewsService.Callback() {

            @Override
            public void onSuccess(JSONArray news) {
                runOnUiThread(() -> displayNews(news));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    featuredTitle.setText("Imeshindwa kupakia habari.");
                    featuredMeta.setText("");
                });
            }
        });
    }

    private void displayNews(JSONArray news) {
        try {
            latestNewsContainer.removeAllViews();

            if (news.length() == 0) {
                featuredTitle.setText("Hakuna habari kwa sasa.");
                featuredMeta.setText("");
                return;
            }

            JSONObject featured = news.getJSONObject(0);

            featuredTitle.setText(
                    featured.optString("title", "Hakuna kichwa")
            );

            featuredMeta.setText(
                    featured.optString("category", "Tanzania")
            );

            for (int i = 1; i < news.length() && i < 7; i++) {
                JSONObject item = news.getJSONObject(i);
                addNewsCard(item);
            }

        } catch (Exception e) {
            featuredTitle.setText("Imeshindwa kupakia habari.");
        }
    }

    private void addNewsCard(JSONObject item) {
        TextView card = new TextView(this);

        String title = item.optString(
                "title",
                "Hakuna kichwa"
        );

        String category = item.optString(
                "category",
                "Tanzania"
        );

        card.setText(
                category.toUpperCase() +
                "\n\n" +
                title
        );

        card.setTextColor(Color.WHITE);
        card.setTextSize(16);
        card.setPadding(18, 18, 18, 18);
        card.setBackgroundColor(Color.rgb(17, 17, 17));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 12);
        card.setLayoutParams(params);

        card.setOnClickListener(v -> openArticle(item));

        latestNewsContainer.addView(card);
    }


    private void loadFixtures() {
        FootballService.loadFixtures(new FootballService.Callback() {

            @Override
            public void onSuccess(JSONArray fixtures) {
                runOnUiThread(() -> displayLiveFixtures(fixtures));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        findViewById(R.id.liveStatus).setVisibility(View.VISIBLE)
                );
            }
        });
    }

    private void displayLiveFixtures(JSONArray fixtures) {
        TextView liveStatus = findViewById(R.id.liveStatus);

        displayFixtures(fixtures);

        try {
            StringBuilder liveText = new StringBuilder();
            int liveCount = 0;

            for (int i = 0; i < fixtures.length(); i++) {
                JSONObject match = fixtures.getJSONObject(i);

                if (!match.optBoolean("is_live", false)) {
                    continue;
                }

                String home = match.optString("home_team", "Home");
                String away = match.optString("away_team", "Away");
                String title = match.optString("live_title", "");

                if (liveCount > 0) {
                    liveText.append("\n\n");
                }

                liveText.append("🔴 LIVE\n")
                        .append(home)
                        .append("  vs  ")
                        .append(away);

                if (!title.isEmpty()) {
                    liveText.append("\n").append(title);
                }

                liveCount++;
            }

            if (liveCount == 0) {
                liveStatus.setText("Hakuna mechi live kwa sasa.");
            } else {
                liveStatus.setText(liveText.toString());
            }

        } catch (Exception e) {
            liveStatus.setText("Hakuna mechi live kwa sasa.");
        }
    }


    private void displayFixtures(JSONArray fixtures) {
        fixturesContainer.removeAllViews();

        try {
            int count = 0;

            for (int i = 0; i < fixtures.length() && count < 7; i++) {
                JSONObject match = fixtures.getJSONObject(i);

                if (match.optBoolean("is_live", false)) {
                    continue;
                }

                String home = match.optString("home_team", "Home");
                String away = match.optString("away_team", "Away");
                String date = match.optString("match_date", "");
                String time = match.optString("match_time", "");

                TextView card = new TextView(this);

                card.setText(
                        date + "  •  " + time + "\n\n" +
                        home + "   VS   " + away
                );

                card.setTextColor(Color.WHITE);
                card.setTextSize(16);
                card.setPadding(18, 18, 18, 18);
                card.setBackgroundColor(Color.rgb(17, 17, 17));

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                params.setMargins(0, 0, 0, 12);
                card.setLayoutParams(params);

                fixturesContainer.addView(card);
                count++;
            }

            if (count == 0) {
                TextView empty = new TextView(this);
                empty.setText("Hakuna ratiba kwa sasa.");
                empty.setTextColor(Color.LTGRAY);
                empty.setTextSize(15);
                empty.setPadding(18, 18, 18, 18);
                fixturesContainer.addView(empty);
            }

        } catch (Exception e) {
            TextView error = new TextView(this);
            error.setText("Imeshindwa kupakia ratiba.");
            error.setTextColor(Color.LTGRAY);
            fixturesContainer.addView(error);
        }
    }

    private void openArticle(JSONObject item) {
        try {
            String newsId = item.optString("id", "");

            if (newsId.isEmpty()) {
                return;
            }

            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            ArticleDetailActivity.class
                    );

            intent.putExtra("news_id", newsId);
            startActivity(intent);

        } catch (Exception e) {
            // Ignore invalid article
        }
    }
}
