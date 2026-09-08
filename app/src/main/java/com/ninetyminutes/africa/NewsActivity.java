package com.ninetyminutes.africa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private LinearLayout newsContainer;
    private EditText newsSearch;

    private final List<NewsItem> newsList = new ArrayList<>();

    private String selectedCategory = "ZOTE";
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsContainer = findViewById(R.id.newsContainer);
        newsSearch = findViewById(R.id.newsSearch);

        TextView categoryAll = findViewById(R.id.categoryAll);
        TextView categoryTanzania = findViewById(R.id.categoryTanzania);
        TextView categoryAfrica = findViewById(R.id.categoryAfrica);
        TextView categoryWorld = findViewById(R.id.categoryWorld);

        loadNews();

        showNews();

        newsSearch.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        searchText = s.toString().trim();
                        showNews();
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {
                    }
                }
        );

        categoryAll.setOnClickListener(v -> {
            selectedCategory = "ZOTE";
            updateCategoryButtons(
                    categoryAll,
                    categoryTanzania,
                    categoryAfrica,
                    categoryWorld
            );
            showNews();
        });

        categoryTanzania.setOnClickListener(v -> {
            selectedCategory = "TANZANIA";
            updateCategoryButtons(
                    categoryTanzania,
                    categoryAll,
                    categoryAfrica,
                    categoryWorld
            );
            showNews();
        });

        categoryAfrica.setOnClickListener(v -> {
            selectedCategory = "AFRICA";
            updateCategoryButtons(
                    categoryAfrica,
                    categoryAll,
                    categoryTanzania,
                    categoryWorld
            );
            showNews();
        });

        categoryWorld.setOnClickListener(v -> {
            selectedCategory = "DUNIA";
            updateCategoryButtons(
                    categoryWorld,
                    categoryAll,
                    categoryTanzania,
                    categoryAfrica
            );
            showNews();
        });
    }

    private void loadNews() {

        newsList.clear();

        newsList.add(
                new NewsItem(
                        "TANZANIA",
                        "SIMBA SC YATWAA SUPERBRAND TENA",
                        "Simba SC imeendelea kuonyesha nguvu yake katika soka la Tanzania.",
                        "Simba SC imefanikiwa kuendelea kujijengea nafasi kubwa katika soka la Tanzania. " +
                        "Taarifa zaidi kuhusu timu, maandalizi na mipango yake zitaendelea kutolewa na 90' MINUTES AFRICA.",
                        0
                )
        );

        newsList.add(
                new NewsItem(
                        "TANZANIA",
                        "YANGA SC YAJIWEKA KATIKA HATUA MPYA",
                        "Yanga SC inaendelea na maandalizi kuelekea msimu mpya wa mashindano.",
                        "Yanga SC inaendelea kujiandaa kwa changamoto mbalimbali za msimu. " +
                        "Mashabiki wanatarajia kuona kikosi kikifanya vizuri katika mashindano ya ndani na kimataifa.",
                        0
                )
        );

        newsList.add(
                new NewsItem(
                        "AFRICA",
                        "TAARIFA MPYA ZA SOKA LA AFRIKA",
                        "Fuata habari muhimu kutoka Tanzania na bara zima la Afrika.",
                        "90' MINUTES AFRICA inakuletea taarifa za soka kutoka Tanzania, Afrika na dunia. " +
                        "Endelea kutufuatilia kwa habari mpya, uchambuzi na taarifa muhimu za mchezo wa soka.",
                        0
                )
        );

        newsList.add(
                new NewsItem(
                        "DUNIA",
                        "HABARI KUBWA ZA SOKA DUNIANI",
                        "Taarifa muhimu kutoka kwenye soka la kimataifa.",
                        "Fuata taarifa za timu kubwa, mashindano ya kimataifa, wachezaji na matukio muhimu ya soka duniani kupitia 90' MINUTES AFRICA.",
                        0
                )
        );
    }

    private void showNews() {

        newsContainer.removeAllViews();

        int count = 0;

        for (NewsItem item : newsList) {

            boolean categoryMatch =
                    selectedCategory.equals("ZOTE") ||
                    item.category.equals(selectedCategory);

            boolean searchMatch =
                    searchText.isEmpty() ||
                    item.title.toLowerCase().contains(
                            searchText.toLowerCase()
                    ) ||
                    item.excerpt.toLowerCase().contains(
                            searchText.toLowerCase()
                    );

            if (categoryMatch && searchMatch) {
                addNewsCard(item);
                count++;
            }
        }

        if (count == 0) {

            TextView empty = new TextView(this);

            empty.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            empty.setText("Hakuna habari iliyopatikana.");
            empty.setTextColor(Color.rgb(170, 170, 170));
            empty.setTextSize(15);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(16, 40, 16, 40);

            newsContainer.addView(empty);
        }
    }

    private void addNewsCard(NewsItem item) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);
        card.setBackgroundColor(Color.rgb(21, 21, 21));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 20, 0, 0);
        card.setLayoutParams(cardParams);

        TextView image = new TextView(this);

        image.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        140
                )
        );

        image.setGravity(Gravity.CENTER);
        image.setText("PICHA YA HABARI");
        image.setTextColor(Color.rgb(119, 119, 119));
        image.setBackgroundColor(Color.rgb(34, 34, 34));

        TextView category = new TextView(this);

        category.setText(item.category);
        category.setTextColor(Color.rgb(212, 175, 55));
        category.setTextSize(12);
        category.setGravity(Gravity.START);
        category.setPadding(0, 12, 0, 0);

        TextView title = new TextView(this);

        title.setText(item.title);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setGravity(Gravity.START);
        title.setPadding(0, 6, 0, 0);

        TextView excerpt = new TextView(this);

        excerpt.setText(item.excerpt);
        excerpt.setTextColor(Color.rgb(170, 170, 170));
        excerpt.setTextSize(13);
        excerpt.setPadding(0, 6, 0, 0);

        card.addView(image);
        card.addView(category);
        card.addView(title);
        card.addView(excerpt);

        card.setOnClickListener(v -> {

            Intent intent = new Intent(
                    NewsActivity.this,
                    ArticleDetailActivity.class
            );

            intent.putExtra("article_category", item.category);
            intent.putExtra("article_title", item.title);
            intent.putExtra("article_excerpt", item.excerpt);
            intent.putExtra("article_content", item.content);
            intent.putExtra("article_views", item.views);

            startActivity(intent);
        });

        newsContainer.addView(card);
    }

    private void updateCategoryButtons(
            TextView selected,
            TextView button1,
            TextView button2,
            TextView button3
    ) {

        selected.setTextColor(Color.rgb(8, 8, 8));
        selected.setBackgroundColor(Color.rgb(212, 175, 55));

        button1.setTextColor(Color.WHITE);
        button1.setBackgroundColor(Color.rgb(34, 34, 34));

        button2.setTextColor(Color.WHITE);
        button2.setBackgroundColor(Color.rgb(34, 34, 34));

        button3.setTextColor(Color.WHITE);
        button3.setBackgroundColor(Color.rgb(34, 34, 34));
    }

    private static class NewsItem {

        String category;
        String title;
        String excerpt;
        String content;
        int views;

        NewsItem(
                String category,
                String title,
                String excerpt,
                String content,
                int views
        ) {

            this.category = category;
            this.title = title;
            this.excerpt = excerpt;
            this.content = content;
            this.views = views;
        }
    }
}
