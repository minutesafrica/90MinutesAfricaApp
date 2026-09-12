package com.ninetyminutes.africa;

import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Intent;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView breakingTitle;
    private TextView featuredTitle;
    private TextView featuredMeta;
    private LinearLayout latestNewsContainer;
    private LinearLayout fixturesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        breakingTitle = findViewById(R.id.breakingTitle);
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

            JSONObject breaking = news.getJSONObject(0);

            for (int i = 0; i < news.length(); i++) {
                JSONObject item = news.getJSONObject(i);
                if (item.optBoolean("breaking", false)) {
                    breaking = item;
                    break;
                }
            }

            breakingTitle.setText(
                    breaking.optString("title", "Hakuna breaking news kwa sasa.")
            );

            TranslateAnimation animation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, -1.0f,
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, 0
            );

            animation.setDuration(15000);
            animation.setRepeatCount(TranslateAnimation.INFINITE);
            animation.setRepeatMode(TranslateAnimation.RESTART);
            breakingTitle.startAnimation(animation);

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
        LinearLayout liveContainer = findViewById(R.id.liveContainer);

        liveContainer.removeAllViews();

        try {
            int liveCount = 0;

            for (int i = 0; i < fixtures.length(); i++) {
                JSONObject match = fixtures.getJSONObject(i);

                if (!match.optBoolean("is_live", false)) {
                    continue;
                }

                String home = match.optString("home_team", "Home");
                String away = match.optString("away_team", "Away");
                String time = match.optString("match_time", "");
                String title = match.optString("live_title", "");
                String competition = match.optString("competition", "");
                String streamUrl = match.optString("stream_url", "");
                String streamType = match.optString("stream_type", "hls");

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(17, 17, 17, 17);
                card.setBackgroundColor(Color.rgb(16, 16, 16));

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                cardParams.setMargins(0, 0, 0, 12);
                card.setLayoutParams(cardParams);

                TextView liveTime = new TextView(this);
                liveTime.setText(
                        "🔴 LIVE" +
                        (time.isEmpty() ? "" : "     " + time.substring(0, Math.min(5, time.length())))
                );
                liveTime.setTextColor(Color.rgb(255, 59, 48));
                liveTime.setTextSize(12);
                liveTime.setTypeface(null, Typeface.BOLD);

                TextView teams = new TextView(this);
                teams.setText(home + "    VS    " + away);
                teams.setTextColor(Color.WHITE);
                teams.setTextSize(15);
                teams.setTypeface(null, Typeface.BOLD);
                teams.setGravity(Gravity.CENTER);
                teams.setPadding(0, 14, 0, 10);

                TextView status = new TextView(this);
                String statusText = !title.isEmpty()
                        ? title
                        : (!competition.isEmpty() ? competition : "LIVE");

                status.setText(statusText);
                status.setTextColor(Color.rgb(227, 6, 19));
                status.setTextSize(11);
                status.setTypeface(null, Typeface.BOLD);
                status.setGravity(Gravity.CENTER);

                Button watchButton = new Button(this);
                watchButton.setText(
                        streamUrl.isEmpty()
                                ? "LIVE INAKUJA"
                                : "▶ TAZAMA LIVE"
                );
                watchButton.setTextColor(Color.WHITE);
                watchButton.setTextSize(13);
                watchButton.setAllCaps(false);
                watchButton.setEnabled(!streamUrl.isEmpty());

                if (!streamUrl.isEmpty()) {
                    watchButton.setOnClickListener(v -> {
                        Intent intent =
                                new Intent(MainActivity.this, LiveActivity.class);

                        intent.putExtra("stream_url", streamUrl);
                        intent.putExtra("stream_type", streamType);
                        intent.putExtra("live_title", title);
                        intent.putExtra("competition", competition);
                        intent.putExtra("home_team", home);
                        intent.putExtra("away_team", away);

                        startActivity(intent);
                    });
                }

                card.addView(liveTime);
                card.addView(teams);
                card.addView(status);
                card.addView(watchButton);

                liveContainer.addView(card);
                liveCount++;
            }

            if (liveCount == 0) {
                liveStatus.setVisibility(View.VISIBLE);
                liveStatus.setText(
                        "Hakuna mechi inayorushwa LIVE kwa sasa."
                );
            } else {
                liveStatus.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            liveStatus.setVisibility(View.VISIBLE);
            liveStatus.setText(
                    "Hakuna mechi inayorushwa LIVE kwa sasa."
            );
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
