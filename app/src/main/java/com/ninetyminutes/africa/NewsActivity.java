package com.ninetyminutes.africa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.NewsService;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private LinearLayout newsContainer;
    private ProgressBar progressBar;

    private final int RED = Color.rgb(227, 6, 19);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(153, 153, 153);
    private final int CARD = Color.rgb(16, 16, 16);
    private final int BORDER = Color.rgb(38, 38, 38);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news);

        newsContainer = findViewById(R.id.newsContainer);

        showLoading();
        loadNews();

        findViewById(R.id.newsMenu).setOnClickListener(v -> finish());
    }

    private void showLoading() {

        newsContainer.removeAllViews();

        progressBar = new ProgressBar(this);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                );

        params.gravity = Gravity.CENTER;
        params.topMargin = dp(40);
        params.bottomMargin = dp(20);

        newsContainer.addView(progressBar, params);

        TextView loading = new TextView(this);

        loading.setText("Inapakia habari...");
        loading.setTextColor(MUTED);
        loading.setTextSize(14);
        loading.setGravity(Gravity.CENTER);

        newsContainer.addView(
                loading,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );
    }

    private void loadNews() {

        NewsService.getNews(new NewsService.Callback() {

            @Override
            public void onSuccess(List<NewsItem> news) {

                runOnUiThread(() -> {

                    newsContainer.removeAllViews();

                    if (news == null || news.isEmpty()) {
                        showMessage("Hakuna habari mpya.");
                        return;
                    }

                    int limit = Math.min(news.size(), 6);

                    for (int i = 0; i < limit; i++) {
                        addNewsCard(news.get(i));
                    }
                });
            }

            @Override
            public void onError(String error) {

                runOnUiThread(() -> {

                    newsContainer.removeAllViews();

                    showMessage(
                            error == null || error.trim().isEmpty()
                                    ? "Imeshindikana kupakia habari."
                                    : error
                    );

                    Toast.makeText(
                            NewsActivity.this,
                            "Imeshindikana kupakia habari.",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    private void addNewsCard(NewsItem item) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(CARD);
        card.setPadding(0, 0, 0, 0);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(18)
        );

        newsContainer.addView(card, cardParams);

        ImageView image = new ImageView(this);

        image.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(180)
                )
        );

        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        card.addView(image);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        card.addView(content);

        TextView category = new TextView(this);

        category.setText(
                item.getCategory() == null
                        ? ""
                        : item.getCategory()
        );

        category.setTextColor(RED);
        category.setTextSize(10);
        category.setTypeface(
                null,
                Typeface.BOLD
        );
        category.setLetterSpacing(0.10f);

        content.addView(category);

        TextView title = new TextView(this);

        title.setText(
                item.getTitle() == null
                        ? ""
                        : item.getTitle()
        );

        title.setTextColor(WHITE);
        title.setTextSize(19);
        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setLineSpacing(
                0f,
                1.25f
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = dp(10);

        content.addView(title, titleParams);

        TextView excerpt = new TextView(this);

        excerpt.setText(
                item.getExcerpt() == null
                        ? ""
                        : item.getExcerpt()
        );

        excerpt.setTextColor(MUTED);
        excerpt.setTextSize(14);
        excerpt.setLineSpacing(
                0f,
                1.5f
        );

        LinearLayout.LayoutParams excerptParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        excerptParams.topMargin = dp(10);
        excerptParams.bottomMargin = dp(16);

        content.addView(excerpt, excerptParams);

        TextView readMore = new TextView(this);

        readMore.setText("Soma zaidi →");
        readMore.setTextColor(WHITE);
        readMore.setTextSize(13);
        readMore.setTypeface(
                null,
                Typeface.BOLD
        );
        readMore.setGravity(Gravity.CENTER);

        readMore.setBackgroundResource(
                R.drawable.bg_read_more
        );

        readMore.setPadding(
                dp(14),
                dp(9),
                dp(14),
                dp(9)
        );

        LinearLayout.LayoutParams readParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        content.addView(readMore, readParams);

        if (item.getImageUrl() != null &&
                !item.getImageUrl().trim().isEmpty()) {

            loadImage(
                    image,
                    item.getImageUrl()
            );

        } else {

            image.setVisibility(View.GONE);
        }

        View.OnClickListener openArticle =
                v -> openArticle(item);

        card.setOnClickListener(openArticle);
        readMore.setOnClickListener(openArticle);
    }

    private void openArticle(NewsItem item) {

        Intent intent =
                new Intent(
                        NewsActivity.this,
                        ArticleDetailActivity.class
                );

        intent.putExtra(
                "article_id",
                item.getId()
        );

        intent.putExtra(
                "article_title",
                item.getTitle()
        );

        intent.putExtra(
                "article_category",
                item.getCategory()
        );

        intent.putExtra(
                "article_excerpt",
                item.getExcerpt()
        );

        intent.putExtra(
                "article_content",
                item.getContent()
        );

        intent.putExtra(
                "article_author",
                item.getAuthor()
        );

        intent.putExtra(
                "article_image",
                item.getImageUrl()
        );

        intent.putExtra(
                "article_created_at",
                item.getCreatedAt()
        );

        intent.putExtra(
                "article_views",
                item.getViews()
        );

        startActivity(intent);
    }

    private void showMessage(String message) {

        TextView text = new TextView(this);

        text.setText(message);
        text.setTextColor(MUTED);
        text.setTextSize(15);
        text.setGravity(Gravity.CENTER);
        text.setPadding(
                dp(20),
                dp(40),
                dp(20),
                dp(40)
        );

        newsContainer.addView(
                text,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );
    }

    private void loadImage(
            ImageView imageView,
            String imageUrl
    ) {

        new Thread(() -> {

            HttpURLConnection connection = null;
            InputStream input = null;

            try {

                URL url = new URL(imageUrl);

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setInstanceFollowRedirects(true);

                input = connection.getInputStream();

                Bitmap bitmap =
                        BitmapFactory.decodeStream(input);

                if (bitmap != null) {

                    runOnUiThread(() ->
                            imageView.setImageBitmap(bitmap)
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                try {

                    if (input != null) {
                        input.close();
                    }

                } catch (Exception ignored) {
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    private int dp(int value) {

        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}
