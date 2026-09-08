package com.ninetyminutes.africa;

import android.content.Intent;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.FootballService;
import com.ninetyminutes.africa.network.LiveService;
import com.ninetyminutes.africa.network.NewsService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout breakingContainer;
    private LinearLayout latestNewsContainer;
    private LinearLayout fixturesContainer;
    private LinearLayout liveContainer;
    private LinearLayout trendingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        breakingContainer =
                findViewById(R.id.breakingContainer);

        latestNewsContainer =
                findViewById(R.id.latestNewsContainer);

        fixturesContainer =
                findViewById(R.id.fixturesContainer);

        liveContainer =
                findViewById(R.id.liveContainer);

        trendingContainer =
                findViewById(R.id.trendingContainer);

        setupNavigation();

        loadNews();

        loadFixtures();

        loadLive();
    }

    private void setupNavigation() {
        findViewById(R.id.visitWebsite).setOnClickListener(v ->
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://90minutesafrica.online")
                ))
        );

        findViewById(R.id.navMenu).setOnClickListener(v -> {
            View menuView = getLayoutInflater().inflate(
                    R.layout.popup_nav_menu,
                    null
            );

            PopupWindow popup = new PopupWindow(
                    menuView,
                    dp(240),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popup.setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(
                            Color.TRANSPARENT
                    )
            );
            popup.setOutsideTouchable(true);
            popup.setElevation(dp(12));

            menuView.findViewById(R.id.menuHome).setOnClickListener(item -> {
                popup.dismiss();
                startActivity(new Intent(
                        MainActivity.this,
                        MainActivity.class
                ));
            });

            menuView.findViewById(R.id.menuNews).setOnClickListener(item -> {
                popup.dismiss();
                startActivity(new Intent(
                        MainActivity.this,
                        NewsActivity.class
                ));
            });

            menuView.findViewById(R.id.menuMatches).setOnClickListener(item -> {
                popup.dismiss();
                startActivity(new Intent(
                        MainActivity.this,
                        MatchesActivity.class
                ));
            });

            menuView.findViewById(R.id.menuResults).setOnClickListener(item -> {
                popup.dismiss();
                startActivity(new Intent(
                        MainActivity.this,
                        ResultsActivity.class
                ));
            });

            menuView.findViewById(R.id.menuTable).setOnClickListener(item -> {
                popup.dismiss();
                startActivity(new Intent(
                        MainActivity.this,
                        TableActivity.class
                ));
            });

            String[] categories = {
                    "Tanzania",
                    "Kimataifa",
                    "Vilabu",
                    "Mashindano",
                    "Usajili",
                    "Wachezaji"
            };

            int[] ids = {
                    R.id.menuTanzania,
                    R.id.menuKimataifa,
                    R.id.menuVilabu,
                    R.id.menuMashindano,
                    R.id.menuUsajili,
                    R.id.menuWachezaji
            };

            for (int i = 0; i < ids.length; i++) {
                final String category = categories[i];

                menuView.findViewById(ids[i]).setOnClickListener(item -> {
                    Intent intent = new Intent(
                            MainActivity.this,
                            NewsActivity.class
                    );
                    intent.putExtra(
                            "selected_category",
                            category
                    );
                    popup.dismiss();
                    startActivity(intent);
                });
            }

            popup.showAsDropDown(
                    v,
                    -dp(192),
                    dp(6)
            );
        });

        findViewById(R.id.navNotifications).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        NotificationsActivity.class
                ))
        );

        findViewById(R.id.viewAllNews).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        NewsActivity.class
                ))
        );

        findViewById(R.id.footerTerms).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        TermsActivity.class
                ))
        );

        findViewById(R.id.footerPrivacy).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        PrivacyPolicyActivity.class
                ))
        );

        findViewById(R.id.footerCookie).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        CookiePolicyActivity.class
                ))
        );

        findViewById(R.id.footerDisclaimer).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        DisclaimerActivity.class
                ))
        );

        findViewById(R.id.footerAbout).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        AboutActivity.class
                ))
        );

        findViewById(R.id.footerContact).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        ContactActivity.class
                ))
        );
    }

    private int dp(int value) {
        return (int) (
                value * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    private void loadNews() {

        NewsService.getNews(
                new NewsService.Callback() {

                    @Override
                    public void onSuccess(List<NewsItem> news) {

                        runOnUiThread(() -> {

                            breakingContainer.removeAllViews();

                            latestNewsContainer.removeAllViews();

                            trendingContainer.removeAllViews();

                            if (news == null ||
                                    news.isEmpty()) {

                                addMessage(
                                        latestNewsContainer,
                                        "Hakuna habari kwa sasa."
                                );

                                return;
                            }

                            NewsItem breaking = null;

                            for (NewsItem item : news) {

                                String title =
                                        item.getTitle();

                                if (title != null &&
                                        !title.trim().isEmpty()) {

                                    breaking = item;
                                    break;
                                }
                            }

                            if (breaking != null) {
                                addBreakingCard(breaking);
                            }

                            int limit =
                                    Math.min(6, news.size());

                            for (int i = 0;
                                 i < limit;
                                 i++) {

                                addNewsCard(
                                        latestNewsContainer,
                                        news.get(i)
                                );
                            }

                            int trendingLimit =
                                    Math.min(3, news.size());

                            for (int i = 0;
                                 i < trendingLimit;
                                 i++) {

                                addTrendingItem(
                                        trendingContainer,
                                        i + 1,
                                        news.get(i)
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() -> {

                            addMessage(
                                    latestNewsContainer,
                                    "Imeshindikana kupakia habari."
                            );
                        });
                    }
                }
        );
    }

    private void loadFixtures() {

        FootballService.getFixtures(
                new FootballService.Callback() {

                    @Override
                    public void onSuccess(JSONArray data) {

                        runOnUiThread(() -> {

                            fixturesContainer.removeAllViews();

                            if (data.length() == 0) {

                                addMessage(
                                        fixturesContainer,
                                        "Hakuna ratiba kwa sasa."
                                );

                                return;
                            }

                            int limit =
                                    Math.min(5, data.length());

                            for (int i = 0;
                                 i < limit;
                                 i++) {

                                try {

                                    JSONObject match =
                                            data.getJSONObject(i);

                                    addFixtureCard(match);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() ->
                                addMessage(
                                        fixturesContainer,
                                        "Imeshindikana kupakia ratiba."
                                )
                        );
                    }
                }
        );
    }

    private void loadLive() {

        LiveService.getLiveFixtures(
                new LiveService.Callback() {

                    @Override
                    public void onSuccess(JSONArray data) {

                        runOnUiThread(() -> {

                            liveContainer.removeAllViews();

                            if (data.length() == 0) {

                                addMessage(
                                        liveContainer,
                                        "Hakuna mechi LIVE kwa sasa."
                                );

                                return;
                            }

                            for (int i = 0;
                                 i < data.length();
                                 i++) {

                                try {

                                    JSONObject match =
                                            data.getJSONObject(i);

                                    addLiveCard(match);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() ->
                                addMessage(
                                        liveContainer,
                                        "Live haipatikani kwa sasa."
                                )
                        );
                    }
                }
        );
    }

    private void addBreakingCard(
            NewsItem item
    ) {

        LinearLayout card =
                createCard();

        card.setPadding(
                18, 18, 18, 18
        );

        card.setBackgroundResource(
                R.drawable.bg_news_card
        );
        card.setPadding(
                18, 18, 18, 18
        );

        TextView badge =
                createText(
                        "BREAKING",
                        10,
                        "#E30613"
                );

        badge.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(badge);

        TextView title =
                createText(
                        item.getTitle(),
                        19,
                        "#FFFFFF"
                );

        title.setTypeface(
                null,
                Typeface.BOLD
        );
        title.setSingleLine(true);
        title.setEllipsize(android.text.TextUtils.TruncateAt.MARQUEE);
        title.setMarqueeRepeatLimit(-1);
        title.setSelected(true);

        title.setPadding(
                0, 6, 0, 0
        );

        card.addView(title);

        TextView excerpt =
                createText(
                        item.getExcerpt(),
                        14,
                        "#999999"
                );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        excerpt.setLayoutParams(params);

        card.addView(excerpt);

        TextView readMore =
                createText(
                        "SOMA HABARI  →",
                        12,
                        "#E30613"
                );

        readMore.setTypeface(
                null,
                Typeface.BOLD
        );

        readMore.setPadding(
                0, 12, 0, 0
        );

        card.addView(readMore);

        card.setOnClickListener(
                v -> openArticle(item)
        );

        breakingContainer.addView(card);
    }

    private void addNewsCard(
            LinearLayout container,
            NewsItem item
    ) {

        LinearLayout card =
                createCard();

        String imageUrl = item.getImageUrl();

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {

            ImageView image =
                    new ImageView(this);

            image.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            -1,
                            180
                    )
            );

            image.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );

            card.addView(image);

            new Thread(() -> {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection =
                            (HttpURLConnection) url.openConnection();

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

        TextView category =
                createText(
                        item.getCategory(),
                        10,
                        "#E30613"
                );

        category.setTypeface(
                null,
                Typeface.BOLD
        );

        category.setPadding(
                0, 14, 0, 0
        );

        card.addView(category);

        TextView title =
                createText(
                        item.getTitle(),
                        19,
                        "#FFFFFF"
                );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setPadding(
                0, 6, 0, 0
        );

        card.addView(title);

        String excerpt =
                item.getExcerpt();

        if (excerpt != null &&
                !excerpt.trim().isEmpty()) {

            TextView excerptView =
                    createText(
                            excerpt,
                            14,
                            "#999999"
                    );

            excerptView.setPadding(
                    0, 8, 0, 0
            );

            card.addView(excerptView);
        }

        TextView readMore =
                createText(
                        "SOMA HABARI  →",
                        12,
                        "#E30613"
                );

        readMore.setTypeface(
                null,
                Typeface.BOLD
        );

        readMore.setPadding(
                0, 12, 0, 0
        );

        card.addView(readMore);

        TextView meta =
                createText(
                        formatDate(
                                item.getCreatedAt()
                        ),
                        12,
                        "#777777"
                );

        meta.setPadding(
                0, 8, 0, 0
        );

        card.addView(meta);

        card.setOnClickListener(
                v -> openArticle(item)
        );

        container.addView(card);
    }

    private void addTrendingItem(
            LinearLayout container,
            int number,
            NewsItem item
    ) {

        TextView view =
                createText(
                        number + ". " + item.getTitle(),
                        14,
                        "#FFFFFF"
                );

        view.setPadding(
                16, 14, 16, 14
        );

        view.setBackgroundColor(
                Color.rgb(21, 21, 21)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(
                0, 0, 0, 6
        );

        view.setLayoutParams(params);

        view.setOnClickListener(
                v -> openArticle(item)
        );

        container.addView(view);
    }

    private void addFixtureCard(
            JSONObject match
    ) {

        String home =
                match.optString(
                        "home_team",
                        "Home"
                );

        String away =
                match.optString(
                        "away_team",
                        "Away"
                );

        String competition =
                match.optString(
                        "competition",
                        "Mechi"
                );

        String date =
                match.optString(
                        "match_date",
                        ""
                );

        String time =
                match.optString(
                        "match_time",
                        ""
                );

        LinearLayout card =
                createCard();

        card.setBackgroundResource(
                R.drawable.bg_fixture_card
        );

        card.setPadding(
                16, 16, 16, 16
        );

        TextView competitionView =
                createText(
                        competition,
                        12,
                        "#E30613"
                );

        competitionView.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView teams =
                createText(
                        home + "    vs    " + away,
                        16,
                        "#FFFFFF"
                );

        teams.setGravity(
                Gravity.CENTER
        );

        teams.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView dateView =
                createText(
                        date +
                                (time.isEmpty()
                                        ? ""
                                        : "  •  " + time),
                        12,
                        "#999999"
                );

        dateView.setGravity(
                Gravity.CENTER
        );

        card.addView(
                competitionView
        );

        card.addView(
                teams
        );

        card.addView(
                dateView
        );

        fixturesContainer.addView(card);
    }

    private void addLiveCard(
            JSONObject match
    ) {
        String home =
                match.optString(
                        "home_team",
                        "Home"
                );

        String away =
                match.optString(
                        "away_team",
                        "Away"
                );

        String competition =
                match.optString(
                        "competition",
                        "LIVE"
                );

        String liveTitle =
                match.optString(
                        "live_title",
                        ""
                );

        String matchId = match.optString("id", "");
        String streamUrl =
                match.optString(
                        "stream_url",
                        ""
                );

        String streamType =
                match.optString(
                        "stream_type",
                        "hls"
                );

        LinearLayout card = createCard();

        card.setBackgroundResource(
                R.drawable.bg_live_card
        );

        card.setPadding(
                16, 16, 16, 16
        );

        TextView live =
                createText(
                        "LIVE NOW",
                        11,
                        "#E53935"
                );

        live.setTypeface(
                null,
                Typeface.BOLD
        );

        live.setPadding(
                0, 0, 0, 8
        );

        live.setGravity(
                Gravity.CENTER
        );

        TextView teams =
                createText(
                        home + "  vs  " + away,
                        17,
                        "#FFFFFF"
                );

        teams.setGravity(
                Gravity.CENTER
        );

        teams.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView comp =
                createText(
                        competition,
                        11,
                        "#999999"
                );

        comp.setGravity(
                Gravity.CENTER
        );

        card.addView(live);
        card.addView(teams);
        card.addView(comp);

        if (!liveTitle.isEmpty()) {

            TextView title =
                    createText(
                            liveTitle,
                            13,
                            "#AAAAAA"
                    );

            title.setGravity(
                    Gravity.CENTER
            );

            card.addView(title);
        }

        TextView watch =
                createText(
                        "WATCH LIVE  →",
                        12,
                        "#FFFFFF"
                );

        watch.setTypeface(
                null,
                Typeface.BOLD
        );

        watch.setGravity(
                Gravity.CENTER
        );

        watch.setPadding(
                14, 12, 14, 12
        );

        watch.setBackgroundResource(
                R.drawable.bg_red_button
        );

        card.addView(watch);

        TextView viewers =
                createText(
                        "LIVE STREAM",
                        11,
                        "#999999"
                );

        viewers.setGravity(
                Gravity.CENTER
        );

        viewers.setPadding(
                0, 10, 0, 0
        );

        card.addView(viewers);

        if (!streamUrl.isEmpty()) {

            card.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                LiveActivity.class
                        );

                intent.putExtra(
                        "stream_url",
                        streamUrl
                );

                intent.putExtra(
                        "stream_type",
                        streamType
                );

                intent.putExtra(
                        "live_title",
                        liveTitle
                );

                intent.putExtra(
                        "home_team",
                        home
                );

                intent.putExtra(
                        "away_team",
                        away
                );

                intent.putExtra(
                        "competition",
                        competition
                );

                intent.putExtra("match_id", matchId);
                startActivity(intent);
            });

        } else {

            card.setAlpha(0.65f);

            card.setOnClickListener(v ->
                    Toast.makeText(
                            MainActivity.this,
                            "Hakuna stream iliyowekwa kwa mechi hii.",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }

        liveContainer.addView(card);
    }

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                16, 16, 16, 16
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(16, 16, 16)
        );

        background.setCornerRadius(16);

        card.setBackground(background);
        card.setElevation(2f);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(
                0, 0, 0, 12
        );

        card.setLayoutParams(params);

        return card;
    }

    private TextView createText(
            String text,
            int size,
            String color
    ) {

        TextView view =
                new TextView(this);

        view.setText(
                text == null
                        ? ""
                        : text
        );

        view.setTextSize(size);
        view.setIncludeFontPadding(false);
        view.setLineSpacing(2f, 1.05f);

        view.setTextColor(
                Color.parseColor(color)
        );

        return view;
    }

    private void addMessage(
            LinearLayout container,
            String message
    ) {

        TextView view =
                createText(
                        message,
                        14,
                        "#AAAAAA"
                );

        view.setPadding(
                10, 20, 10, 20
        );

        container.addView(view);
    }

    private void openArticle(
            NewsItem item
    ) {

        Intent intent =
                new Intent(
                        MainActivity.this,
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
                "article_excerpt",
                item.getExcerpt()
        );

        intent.putExtra(
                "article_content",
                item.getContent()
        );

        intent.putExtra(
                "article_category",
                item.getCategory()
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

    private String formatDate(
            String date
    ) {

        if (date == null ||
                date.trim().isEmpty()) {

            return "";
        }

        try {

            SimpleDateFormat input =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.US
                    );

            Date parsed =
                    input.parse(date);

            if (parsed == null) {
                return date;
            }

            SimpleDateFormat output =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                    );

            return output.format(parsed);

        } catch (Exception e) {

            return date;
        }
    }
}
