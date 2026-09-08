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
        card.setPadding(20, 18, 20, 18);
        card.setBackgroundColor(0xFF151515);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 14);

        card.setLayoutParams(params);

        TextView category = new TextView(this);
        category.setText(
                news.getCategory().toUpperCase(Locale.ROOT)
        );
        category.setTextColor(0xFFD4AF37);
        category.setTextSize(12);
        category.setTypeface(null, 1);

        TextView title = new TextView(this);
        title.setText(news.getTitle());
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(18);
        title.setTypeface(null, 1);
        title.setPadding(0, 8, 0, 8);

        TextView excerpt = new TextView(this);
        excerpt.setText(news.getExcerpt());
        excerpt.setTextColor(0xFFBBBBBB);
        excerpt.setTextSize(14);

        TextView meta = new TextView(this);
        meta.setText(
                "90' MINUTES AFRICA  •  "
                        + news.getAuthor()
                        + "  •  "
                        + news.getViews()
                        + " views"
        );
        meta.setTextColor(0xFF777777);
        meta.setTextSize(12);
        meta.setPadding(0, 12, 0, 0);

        card.addView(category);
        card.addView(title);
        card.addView(excerpt);
        card.addView(meta);

        card.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            NewsActivity.this,
                            ArticleDetailActivity.class
                    );

            intent.putExtra(
                    "article_id",
                    news.getId()
            );

            intent.putExtra(
                    "article_category",
                    news.getCategory()
            );

            intent.putExtra(
                    "article_title",
                    news.getTitle()
            );

            intent.putExtra(
                    "article_excerpt",
                    news.getExcerpt()
            );

            intent.putExtra(
                    "article_content",
                    news.getContent()
            );

            intent.putExtra(
                    "article_author",
                    news.getAuthor()
            );

            intent.putExtra(
                    "article_image",
                    news.getImageUrl()
            );

            intent.putExtra(
                    "article_created_at",
                    news.getCreatedAt()
            );

            intent.putExtra(
                    "article_views",
                    news.getViews()
            );

            startActivity(intent);
        });

        newsContainer.addView(card);
    }
}
