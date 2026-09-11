package com.ninetyminutes.africa;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView titleView;
    private TextView metaView;
    private TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        titleView = findViewById(R.id.articleTitle);
        metaView = findViewById(R.id.articleMeta);
        contentView = findViewById(R.id.articleContent);

        String newsId = getIntent().getStringExtra("news_id");

        if (newsId == null || newsId.trim().isEmpty()) {
            titleView.setText("Habari haipatikani.");
            return;
        }

        loadArticle(newsId);
    }

    private void loadArticle(String newsId) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                String encodedId = java.net.URLEncoder.encode(
                        newsId,
                        "UTF-8"
                );

                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news?select=*&id=eq." +
                        encodedId +
                        "&published=eq.true"
                );

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty(
                        "apikey",
                        SupabaseConfig.KEY
                );
                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + SupabaseConfig.KEY
                );

                int code = connection.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                code >= 200 && code < 300
                                        ? connection.getInputStream()
                                        : connection.getErrorStream()
                        )
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                if (code >= 200 && code < 300) {
                    org.json.JSONArray array =
                            new org.json.JSONArray(response.toString());

                    runOnUiThread(() -> displayArticle(array));
                } else {
                    runOnUiThread(() ->
                            titleView.setText(
                                    "Imeshindwa kupakia habari."
                            )
                    );
                }

            } catch (Exception e) {
                runOnUiThread(() ->
                        titleView.setText(
                                "Imeshindwa kupakia habari."
                        )
                );
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void displayArticle(org.json.JSONArray array) {
        try {
            if (array.length() == 0) {
                titleView.setText("Habari haipatikani.");
                return;
            }

            JSONObject article = array.getJSONObject(0);

            titleView.setText(
                    article.optString("title", "Habari")
            );

            metaView.setText(
                    article.optString("category", "Tanzania")
            );

            contentView.setText(
                    article.optString(
                            "content",
                            article.optString("excerpt", "")
                    )
            );

        } catch (Exception e) {
            titleView.setText("Imeshindwa kuonyesha habari.");
        }
    }
}
