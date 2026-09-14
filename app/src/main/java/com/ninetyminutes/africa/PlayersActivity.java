package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

                            LinearLayout card = new LinearLayout(PlayersActivity.this);
                            card.setOrientation(LinearLayout.VERTICAL);
                            card.setPadding(0, 0, 0, 16);
                            card.setBackgroundColor(0xFF151515);

                            ImageView image = new ImageView(PlayersActivity.this);
                            image.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 210
                            ));
                            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            card.addView(image);

                            TextView text = new TextView(PlayersActivity.this);
                            text.setText(
                                    item.optString("title", "") +
                                    "\n\n" +
                                    item.optString("excerpt", "")
                            );
                            text.setTextColor(0xFFFFFFFF);
                            text.setTextSize(16);
                            text.setPadding(20, 20, 20, 20);
                            card.addView(text);

                            String imageUrl = item.optString("image_url", "");
                            if (!imageUrl.isEmpty()) {
                                new Thread(() -> {
                                    try {
                                        URL url = new URL(imageUrl);
                                        HttpURLConnection connection =
                                                (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();

                                        InputStream input = connection.getInputStream();
                                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                                        input.close();
                                        connection.disconnect();

                                        if (bitmap != null) {
                                            runOnUiThread(() -> image.setImageBitmap(bitmap));
                                        }
                                    } catch (Exception ignored) {
                                    }
                                }).start();
                            }

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
            public void onError(String errorMessage) {
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
