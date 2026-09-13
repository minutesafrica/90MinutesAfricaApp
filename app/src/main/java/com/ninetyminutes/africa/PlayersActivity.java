package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlayersActivity extends AppCompatActivity {

    private LinearLayout newsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        newsContainer = findViewById(R.id.playersNewsContainer);

        findViewById(R.id.playersBack).setOnClickListener(v -> finish());

        loadPlayersNews();
    }

    private void loadPlayersNews() {
        NewsService.loadNews(new NewsService.Callback() {
            @Override
            public void onSuccess(JSONArray news) {
                runOnUiThread(() -> {
                    newsContainer.removeAllViews();

                    for (int i = 0; i < news.length() && i < 20; i++) {
                        try {
                            JSONObject item = news.getJSONObject(i);

                            String category = item.optString("category", "").trim();

                            if (!category.equalsIgnoreCase("Wachezaji")) {
                                continue;
                            }

                            TextView card = new TextView(PlayersActivity.this);
                            card.setText(
                                    item.optString("title", "") +
                                    "\n\n" +
                                    item.optString("excerpt", "")
                            );
                            card.setTextColor(0xFFFFFFFF);
                            card.setTextSize(16);
                            card.setPadding(20, 20, 20, 20);
                            card.setBackgroundColor(0xFF151515);

                            LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                            params.setMargins(0, 0, 0, 12);
                            card.setLayoutParams(params);

                            card.setOnClickListener(v -> {
                                Intent intent = new Intent(
                                        PlayersActivity.this,
                                        ArticleDetailActivity.class
                                );

                                intent.putExtra("news_id", item.optString("id"));
                                intent.putExtra("title", item.optString("title"));
                                intent.putExtra("excerpt", item.optString("excerpt"));
                                intent.putExtra("content", item.optString("content"));
                                intent.putExtra("category", item.optString("category"));
                                intent.putExtra("image_url", item.optString("image_url"));
                                intent.putExtra("author", item.optString("author"));

                                startActivity(intent);
                            });

                            newsContainer.addView(card);

                        } catch (Exception ignored) {
                        }
                    }

                    if (newsContainer.getChildCount() == 0) {
                        TextView empty = new TextView(PlayersActivity.this);
                        empty.setText("Hakuna habari za wachezaji kwa sasa.");
                        empty.setTextColor(0xFFAAAAAA);
                        empty.setTextSize(15);
                        empty.setPadding(20, 30, 20, 30);
                        newsContainer.addView(empty);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    TextView error = new TextView(PlayersActivity.this);
                    error.setText("Imeshindwa kupakia habari za wachezaji.");
                    error.setTextColor(0xFFE30613);
                    error.setPadding(20, 30, 20, 30);
                    newsContainer.removeAllViews();
                    newsContainer.addView(error);
                });
            }
        });
    }
}
