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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        featuredTitle = findViewById(R.id.featuredTitle);
        featuredMeta = findViewById(R.id.featuredMeta);
        latestNewsContainer = findViewById(R.id.latestNewsContainer);

        loadNews();
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
