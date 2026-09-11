package com.ninetyminutes.africa;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView featuredTitle;
    private TextView featuredMeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        featuredTitle = findViewById(R.id.featuredTitle);
        featuredMeta = findViewById(R.id.featuredMeta);

        loadNews();
    }

    private void loadNews() {
        NewsService.loadNews(new NewsService.Callback() {

            @Override
            public void onSuccess(JSONArray news) {
                runOnUiThread(() -> {
                    try {
                        if (news.length() == 0) {
                            featuredTitle.setText("Hakuna habari kwa sasa.");
                            featuredMeta.setText("");
                            return;
                        }

                        JSONObject firstNews = news.getJSONObject(0);

                        String title = firstNews.optString(
                                "title",
                                "Hakuna kichwa"
                        );

                        String category = firstNews.optString(
                                "category",
                                "Tanzania"
                        );

                        featuredTitle.setText(title);
                        featuredMeta.setText(category);

                    } catch (Exception e) {
                        featuredTitle.setText("Imeshindwa kupakia habari.");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        featuredTitle.setText(
                                "Imeshindwa kupakia habari."
                        )
                );
            }
        });
    }
}
