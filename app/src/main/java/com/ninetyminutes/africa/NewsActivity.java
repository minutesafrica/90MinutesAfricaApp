package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.NewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewsActivity extends AppCompatActivity {

    private LinearLayout newsContainer;
    private EditText newsSearch;

    private final List<NewsItem> allNews = new ArrayList<>();
    private String selectedCategory = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsContainer = findViewById(R.id.newsContainer);
        newsSearch = findViewById(R.id.newsSearch);

        String incomingCategory =
                getIntent().getStringExtra("selected_category");

        if (incomingCategory != null &&
                !incomingCategory.trim().isEmpty()) {
            selectedCategory =
                    incomingCategory.toUpperCase(Locale.ROOT);
        }

        setupCategories();
        setupSearch();
        loadNews();
    }

    private void setupCategories() {

        findViewById(R.id.categoryAll).setOnClickListener(v -> {
            selectedCategory = "ALL";
            displayNews();
        });

        findViewById(R.id.categoryTanzania).setOnClickListener(v -> {
            selectedCategory = "TANZANIA";
            displayNews();
        });

        findViewById(R.id.categoryAfrica).setOnClickListener(v -> {
            selectedCategory = "AFRICA";
            displayNews();
        });

        findViewById(R.id.categoryWorld).setOnClickListener(v -> {
            selectedCategory = "WORLD";
            displayNews();
        });
    }

    private void setupSearch() {

        newsSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                displayNews();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadNews() {

        newsContainer.removeAllViews();

        TextView loading = new TextView(this);
        loading.setText("Inapakia habari...");
        loading.setTextColor(0xFFD4AF37);
        loading.setTextSize(16);
        loading.setPadding(20, 30, 20, 30);

        newsContainer.addView(loading);

        NewsService.getNews(new NewsService.Callback() {

            @Override
            public void onSuccess(List<NewsItem> news) {

                runOnUiThread(() -> {

                    allNews.clear();
                    allNews.addAll(news);

                    displayNews();
                });
            }

            @Override
            public void onError(String error) {

                runOnUiThread(() -> {

                    newsContainer.removeAllViews();

                    TextView errorText = new TextView(NewsActivity.this);
                    errorText.setText(
                            "Imeshindikana kupakia habari.\n\n"
                                    + error
                    );
                    errorText.setTextColor(0xFFFF6666);
                    errorText.setTextSize(15);
                    errorText.setPadding(20, 30, 20, 30);

                    newsContainer.addView(errorText);

                    Toast.makeText(
                            NewsActivity.this,
                            "Tatizo la kuunganisha na server",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    private void displayNews() {

        newsContainer.removeAllViews();

        String searchText =
                newsSearch.getText()
                        .toString()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        int count = 0;

        for (NewsItem news : allNews) {

            boolean categoryMatch =
                    selectedCategory.equals("ALL")
                            || news.getCategory()
                            .toUpperCase(Locale.ROOT)
                            .contains(selectedCategory);

            boolean searchMatch =
                    searchText.isEmpty()
                            || news.getTitle()
                            .toLowerCase(Locale.ROOT)
                            .contains(searchText)
                            || news.getExcerpt()
                            .toLowerCase(Locale.ROOT)
                            .contains(searchText);

            if (categoryMatch && searchMatch) {

                addNewsCard(news);
                count++;
            }
        }

        if (count == 0) {

            TextView empty = new TextView(this);
            empty.setText("Hakuna habari zilizopatikana.");
            empty.setTextColor(0xFFAAAAAA);
            empty.setTextSize(15);
            empty.setPadding(20, 40, 20, 40);

            newsContainer.addView(empty);
        }
    }

    private void addNewsCard(NewsItem news) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);

        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(16);
        bg.setStroke(1, 0xFF262626);
        card.setBackground(bg);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);

        String imageUrl = news.getImageUrl();

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(
                    new LinearLayout.LayoutParams(-1, 200)
            );
            image.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );
            card.addView(image);

            new Thread(() -> {
                try {
                    HttpURLConnection connection =
                            (HttpURLConnection)
                                    new URL(imageUrl).openConnection();

                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);

                    InputStream input =
                            connection.getInputStream();

                    Bitmap bitmap =
                            BitmapFactory.decodeStream(input);

                    input.close();
                    connection.disconnect();

                    runOnUiThread(() -> {
                        if (bitmap != null) {
                            image.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception ignored) {
                }
            }).start();
        }

        TextView category = createNewsText(
                news.getCategory().toUpperCase(Locale.ROOT),
                10,
                0xFFE30613
        );
        category.setTypeface(null, 1);
        category.setPadding(0, 14, 0, 0);

        TextView title = createNewsText(
                news.getTitle(),
                19,
                0xFFFFFFFF
        );
        title.setTypeface(null, 1);
        title.setPadding(0, 6, 0, 0);

        TextView excerpt = createNewsText(
                news.getExcerpt(),
                14,
                0xFF999999
        );
        excerpt.setPadding(0, 8, 0, 0);

        TextView readMore = createNewsText(
                "SOMA HABARI  →",
                12,
                0xFFE30613
        );
        readMore.setTypeface(null, 1);
        readMore.setPadding(0, 12, 0, 0);

        TextView meta = createNewsText(
                "90' MINUTES AFRICA  •  "
                        + news.getAuthor()
                        + "  •  "
                        + news.getViews()
                        + " views",
                11,
                0xFF777777
        );
        meta.setPadding(0, 8, 0, 0);

        card.addView(category);
        card.addView(title);

        if (news.getExcerpt() != null &&
                !news.getExcerpt().trim().isEmpty()) {
            card.addView(excerpt);
        }

        card.addView(readMore);
        card.addView(meta);

        card.setOnClickListener(v -> openArticle(news));

        newsContainer.addView(card);
    }

    private TextView createNewsText(
            String text,
            int size,
            int color
    ) {
        TextView view = new TextView(this);
        view.setText(text == null ? "" : text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        view.setLineSpacing(2f, 1.05f);
        return view;
    }

    private void openArticle(NewsItem news) {
        Intent intent =
                new Intent(
                        NewsActivity.this,
                        ArticleDetailActivity.class
                );

        intent.putExtra("article_id", news.getId());
        intent.putExtra("article_category", news.getCategory());
        intent.putExtra("article_title", news.getTitle());
        intent.putExtra("article_excerpt", news.getExcerpt());
        intent.putExtra("article_content", news.getContent());
        intent.putExtra("article_author", news.getAuthor());
        intent.putExtra("article_image", news.getImageUrl());
        intent.putExtra("article_created_at", news.getCreatedAt());
        intent.putExtra("article_views", news.getViews());

        startActivity(intent);
    }

}
