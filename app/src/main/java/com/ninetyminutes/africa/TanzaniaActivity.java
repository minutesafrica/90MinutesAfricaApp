package com.ninetyminutes.africa;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class TanzaniaActivity extends AppCompatActivity {

    private LinearLayout newsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanzania);

        MobileAds.initialize(this, initializationStatus -> {});
        AdView tanzaniaBannerAd = findViewById(R.id.tanzaniaBannerAd);
        tanzaniaBannerAd.loadAd(new AdRequest.Builder().build());


        findViewById(R.id.tanzaniaBack).setOnClickListener(v -> finish());

        newsContainer = findViewById(R.id.tanzaniaNewsContainer);

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
                runOnUiThread(() -> showMessage("Imeshindwa kupakia habari."));
            }
        });
    }

    private void displayNews(JSONArray news) {
        newsContainer.removeAllViews();

        int count = 0;

        try {
            for (int i = 0; i < news.length(); i++) {
                JSONObject item = news.getJSONObject(i);

                String category = item.optString("category", "").trim();

                if (!category.equalsIgnoreCase("TANZANIA")) {
                    continue;
                }

                addNewsCard(item);
                count++;

                if (count >= 20) {
                    break;
                }
            }

            if (count == 0) {
                showMessage("Hakuna habari za TANZANIA kwa sasa.");
            }

        } catch (Exception e) {
            showMessage("Imeshindwa kupakia habari.");
        }
    }

    private void addNewsCard(JSONObject item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(0), dp(0), dp(0), dp(16));
        card.setBackgroundColor(Color.rgb(20, 20, 20));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(params);

        ImageView image = new ImageView(this);
        image.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(210)
        ));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView category = new TextView(this);
        category.setText(item.optString("category", "").toUpperCase());
        category.setTextColor(Color.rgb(227, 6, 19));
        category.setTextSize(11);
        category.setTypeface(null, Typeface.BOLD);
        category.setPadding(dp(16), dp(14), dp(16), 0);

        TextView title = new TextView(this);
        title.setText(item.optString("title", "Habari"));
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(dp(16), dp(7), dp(16), dp(7));

        TextView excerpt = new TextView(this);
        excerpt.setText(item.optString("excerpt", ""));
        excerpt.setTextColor(Color.LTGRAY);
        excerpt.setTextSize(13);
        excerpt.setPadding(dp(16), 0, dp(16), 0);

        card.addView(image);
        card.addView(category);
        card.addView(title);

        if (!item.optString("excerpt", "").isEmpty()) {
            card.addView(excerpt);
        }

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

        card.setOnClickListener(v -> {
            Intent intent = new Intent(TanzaniaActivity.this, ArticleDetailActivity.class);
            intent.putExtra("news_id", item.optString("id", ""));
            intent.putExtra("title", item.optString("title", ""));
            intent.putExtra("excerpt", item.optString("excerpt", ""));
            intent.putExtra("content", item.optString("content", ""));
            intent.putExtra("category", item.optString("category", ""));
            intent.putExtra("image_url", item.optString("image_url", ""));
            intent.putExtra("author", item.optString("author", "90 Minutes Africa"));
            startActivity(intent);
        });

        newsContainer.addView(card);
    }

    private void showMessage(String message) {
        newsContainer.removeAllViews();

        TextView text = new TextView(this);
        text.setText(message);
        text.setTextColor(Color.WHITE);
        text.setTextSize(15);
        text.setPadding(dp(16), dp(24), dp(16), dp(24));

        newsContainer.addView(text);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
