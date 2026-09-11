package com.ninetyminutes.africa;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.LiveMatch;
import com.ninetyminutes.africa.network.FootballService;
import com.ninetyminutes.africa.network.StandingsCalculator;
import com.ninetyminutes.africa.network.LiveMatchService;
import com.ninetyminutes.africa.network.NewsService;
import com.ninetyminutes.africa.network.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView breakingTitle;
    private LinearLayout latestNewsContainer;
    private LinearLayout fixturesContainer;
    private LinearLayout liveContainer;
    private LinearLayout trendingContainer;
    private LinearLayout resultsContainer;
    private LinearLayout standingsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        breakingTitle = findViewById(R.id.breakingTitle);

        latestNewsContainer =
                findViewById(R.id.latestNewsContainer);

        fixturesContainer =
                findViewById(R.id.fixturesContainer);

        liveContainer =
                findViewById(R.id.liveContainer);

        trendingContainer =
                findViewById(R.id.trendingContainer);

        resultsContainer = findViewById(R.id.resultsContainer);
        standingsContainer = findViewById(R.id.standingsContainer);

        setupNavigation();

        loadNews();

        loadFixtures();

        loadLive();
        loadResults();
    }

    private void openWhatsappChannel() {
    Intent intent = new Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://whatsapp.com/channel/0029Vb6mtUXDjiOZZOGkCw00")
    );
    startActivity(intent);
}

private void setupNavigation() {

        TextView websiteButton = findViewById(R.id.visitWebsite);

        websiteButton.setOnClickListener(v ->
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://90minutesafrica.online")
                ))
        );

        ObjectAnimator websitePulse = ObjectAnimator.ofFloat(
                websiteButton,
                "alpha",
                1.0f,
                0.55f,
                1.0f
        );

        websitePulse.setDuration(1800);
        websitePulse.setRepeatCount(ObjectAnimator.INFINITE);
        websitePulse.setRepeatMode(ObjectAnimator.RESTART);
        websitePulse.start();

        findViewById(R.id.navMenu).setOnClickListener(v -> {

            View menuView = getLayoutInflater().inflate(
                    R.layout.popup_nav_menu,
                    null
            );

            PopupWindow popup = new PopupWindow(
                    menuView,
                    dp(285),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popup.setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(
                            Color.TRANSPARENT
                    )
            );

            popup.setOutsideTouchable(true);
            popup.setElevation(dp(20));

            menuView.findViewById(R.id.menuHome).setOnClickListener(item -> {
                popup.dismiss();
            });

            menuView.findViewById(R.id.menuNews).setOnClickListener(item -> {
                popup.dismiss();
                startActivity(new Intent(
                        MainActivity.this,
                        NewsActivity.class
                ));
            });

            menuView.findViewById(R.id.menuFixtures).setOnClickListener(item -> {
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

            popup.showAtLocation(
                    findViewById(android.R.id.content),
                    Gravity.RIGHT | Gravity.TOP,
                    0,
                    dp(70)
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

        findViewById(R.id.socialFacebook).setOnClickListener(v ->
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                                "https://www.facebook.com/profile.php?id=61574232046522"
                        )
                ))
        );

        findViewById(R.id.socialWhatsapp).setOnClickListener(v ->
                openWhatsappChannel()
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

    private void openCategory(String category) {

        Intent intent = new Intent(
                MainActivity.this,
                NewsActivity.class
        );

        intent.putExtra(
                "selected_category",
                category
        );

        startActivity(intent);
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

                            latestNewsContainer.removeAllViews();
                            trendingContainer.removeAllViews();

                            if (news == null || news.isEmpty()) {
                                addMessage(
                                        latestNewsContainer,
                                        "Hakuna habari kwa sasa."
                                );
                                return;
                            }

                            // WEBSITE LOGIC:
                            // featured = habari yenye featured=true,
                            // ikiwa hakuna, tumia habari ya kwanza.
                            NewsItem featured = null;

                            for (NewsItem item : news) {
                                if (item.isFeatured()) {
                                    featured = item;
                                    break;
                                }
                            }

                            if (featured == null) {
                                featured = news.get(0);
                            }

                            final NewsItem featuredArticle = featured;

                            // HABARI KUU
                            TextView featuredTitle =
                                    findViewById(R.id.featuredNewsTitle);

                            TextView featuredBadge =
                                    findViewById(R.id.featuredNewsBadge);

                            TextView featuredMeta =
                                    findViewById(R.id.featuredNewsMeta);

                            ImageView featuredImage =
                                    findViewById(R.id.featuredNewsImage);

                            featuredImage.setBackgroundColor(
                                    Color.rgb(35, 35, 35)
                            );

                            featuredTitle.setText(
                                    safeText(featuredArticle.getTitle())
                            );

                            featuredBadge.setText("TOP STORY");

                            featuredMeta.setText(
                                    safeText(featuredArticle.getCategory())
                                            + " • "
                                            + formatNewsTime(
                                                    featuredArticle.getCreatedAt()
                                            )
                            );

                            android.util.Log.d(
                                    "TOP_STORY_IMAGE",
                                    "title=" + featuredArticle.getTitle()
                                            + " | imageUrl=" + featuredArticle.getImageUrl()
                            );

                            loadImageIntoView(
                                    featuredArticle.getImageUrl(),
                                    featuredImage
                            );

                            featuredTitle.setOnClickListener(
                                    v -> openArticle(featuredArticle)
                            );

                            findViewById(R.id.featuredNewsCard).setOnClickListener(v -> openArticle(featuredArticle));
                            featuredImage.setOnClickListener(
                                    v -> openArticle(featuredArticle)
                            );

                            // BREAKING NEWS
                            NewsItem breaking = null;

                            for (NewsItem item : news) {
                                String title = item.getTitle();

                                if (title != null
                                        && !title.trim().isEmpty()) {
                                    breaking = item;
                                    break;
                                }
                            }

                            if (breaking != null) {
                                addBreakingCard(breaking);
                            }

                            // HABARI MPYA
                            int addedLatest = 0;

                            for (NewsItem item : news) {

                                if (featuredArticle.getId() != null
                                        && featuredArticle.getId()
                                        .equals(item.getId())) {
                                    continue;
                                }

                                addNewsCard(
                                        latestNewsContainer,
                                        item
                                );

                                addedLatest++;

                                if (addedLatest >= 6) {
                                    break;
                                }
                            }

                            // Ikiwa kuna habari moja tu,
                            // ionyeshe pia kwenye HABARI MPYA.
                            if (addedLatest == 0 && news.size() == 1) {
                                addNewsCard(
                                        latestNewsContainer,
                                        news.get(0)
                                );
                            }

                            // TRENDING
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


                            latestNewsContainer.removeAllViews();

                            addMessage(
                                    latestNewsContainer,
                                    "Imeshindikana kupakia habari."
                            );
                        });
                    }
                }
        );
    }


    private void addTrendingItem(
            LinearLayout container,
            int position,
            NewsItem news
    ) {
        if (container == null || news == null) return;

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(16, 16, 16));
        background.setCornerRadius(dp(12));
        row.setBackground(background);

        TextView number = new TextView(this);
        number.setText(String.valueOf(position));
        number.setTextColor(Color.rgb(229, 57, 53));
        number.setTextSize(20);
        number.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        number.setGravity(Gravity.CENTER);

        row.addView(
                number,
                new LinearLayout.LayoutParams(dp(42), dp(42))
        );

        TextView title = new TextView(this);
        title.setText(safeText(news.getTitle()));
        title.setTextColor(Color.WHITE);
        title.setTextSize(14);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setMaxLines(2);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        title.setPadding(dp(12), 0, 0, 0);

        row.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        row.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ArticleDetailActivity.class
            );
            intent.putExtra("article_id", news.getId());
            startActivity(intent);
        });

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, dp(8));
        container.addView(row, params);
    }


    private void addFixtureCard(JSONObject match) {
        if (fixturesContainer == null || match == null) return;

        try {
            String home = match.optString("home_team", "Home");
            String away = match.optString("away_team", "Away");
            String time = match.optString("match_time", "");
            String date = match.optString("match_date", "");
            String competition = match.optString("competition", "");

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(14), dp(14), dp(14), dp(14));

            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.rgb(16, 16, 16));
            background.setCornerRadius(dp(14));
            card.setBackground(background);

            TextView competitionText = new TextView(this);
            competitionText.setText(safeText(competition));
            competitionText.setTextColor(Color.rgb(229, 57, 53));
            competitionText.setTextSize(11);
            competitionText.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );

            TextView teams = new TextView(this);
            teams.setText(home + "  VS  " + away);
            teams.setTextColor(Color.WHITE);
            teams.setTextSize(15);
            teams.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
            teams.setPadding(0, dp(8), 0, dp(8));

            TextView info = new TextView(this);

            String displayTime =
                    time.length() >= 5
                            ? time.substring(0, 5)
                            : time;

            info.setText(date + " • " + displayTime);
            info.setTextColor(Color.GRAY);
            info.setTextSize(12);

            card.addView(competitionText);
            card.addView(teams);
            card.addView(info);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0, 0, 0, dp(10));

            fixturesContainer.addView(card, params);

        } catch (Exception ignored) {
        }
    }

    private void loadFixtures() {
        FootballService.getFixtures(
                new FootballService.Callback() {

                    @Override
                    public void onSuccess(JSONArray data) {

                        runOnUiThread(() -> {

                            fixturesContainer.removeAllViews();

                            if (data == null || data.length() == 0) {
                                addMessage(
                                        fixturesContainer,
                                        "Hakuna ratiba kwa sasa."
                                );
                                return;
                            }

                            ArrayList<JSONObject> upcoming =
                                    new ArrayList<>();

                            long now = System.currentTimeMillis();

                            for (int i = 0; i < data.length(); i++) {

                                try {
                                    JSONObject match =
                                            data.getJSONObject(i);

                                    String matchDate =
                                            match.optString(
                                                    "match_date",
                                                    ""
                                            );

                                    String matchTime =
                                            match.optString(
                                                    "match_time",
                                                    "00:00"
                                            );

                                    if (matchDate.isEmpty()) {
                                        continue;
                                    }

                                    java.text.SimpleDateFormat parser =
                                            new java.text.SimpleDateFormat(
                                                    "yyyy-MM-dd HH:mm",
                                                    java.util.Locale.US
                                            );

                                    parser.setLenient(false);

                                    java.util.Date date =
                                            parser.parse(
                                                    matchDate
                                                            + " "
                                                            + matchTime
                                                                    .substring(
                                                                            0,
                                                                            Math.min(
                                                                                    5,
                                                                                    matchTime.length()
                                                                            )
                                                                    )
                                            );

                                    if (date != null
                                            && date.getTime() > now) {

                                        upcoming.add(match);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            upcoming.sort(
                                    (a, b) -> {

                                        try {

                                            String aDate =
                                                    a.optString(
                                                            "match_date",
                                                            ""
                                                    )
                                                    + " "
                                                    + a.optString(
                                                            "match_time",
                                                            "00:00"
                                                    )
                                                    .substring(
                                                            0,
                                                            Math.min(
                                                                    5,
                                                                    a.optString(
                                                                            "match_time",
                                                                            "00:00"
                                                                    ).length()
                                                            )
                                                    );

                                            String bDate =
                                                    b.optString(
                                                            "match_date",
                                                            ""
                                                    )
                                                    + " "
                                                    + b.optString(
                                                            "match_time",
                                                            "00:00"
                                                    )
                                                    .substring(
                                                            0,
                                                            Math.min(
                                                                    5,
                                                                    b.optString(
                                                                            "match_time",
                                                                            "00:00"
                                                                    ).length()
                                                            )
                                                    );

                                            java.text.SimpleDateFormat parser =
                                                    new java.text.SimpleDateFormat(
                                                            "yyyy-MM-dd HH:mm",
                                                            java.util.Locale.US
                                                    );

                                            return parser.parse(aDate)
                                                    .compareTo(
                                                            parser.parse(bDate)
                                                    );

                                        } catch (Exception e) {
                                            return 0;
                                        }
                                    }
                            );

                            int limit =
                                    Math.min(7, upcoming.size());

                            if (limit == 0) {
                                addMessage(
                                        fixturesContainer,
                                        "Hakuna ratiba kwa sasa."
                                );
                                return;
                            }

                            for (int i = 0; i < limit; i++) {

                                addFixtureCard(
                                        upcoming.get(i)
                                );
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

        LiveMatchService.getLiveMatches(
                new LiveMatchService.Callback() {

                    @Override
                    public void onSuccess(List<LiveMatch> matches) {

                        runOnUiThread(() -> {

                            liveContainer.removeAllViews();

                            if (matches == null || matches.isEmpty()) {

                                addMessage(
                                        liveContainer,
                                        "Hakuna mechi inayorushwa LIVE kwa sasa."
                                );

                                return;
                            }

                            for (LiveMatch match : matches) {
                                addLiveCard(match);
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

    private void addLiveCard(LiveMatch match) {

        LinearLayout card = createCard();

        card.setBackgroundResource(
                R.drawable.bg_live_card
        );

        card.setPadding(
                16,
                16,
                16,
                16
        );

        TextView live = createText(
                "● LIVE",
                11,
                "#FF3B30"
        );

        live.setTypeface(
                null,
                Typeface.BOLD
        );

        live.setGravity(
                Gravity.CENTER
        );

        live.setPadding(
                0,
                0,
                0,
                8
        );

        android.view.animation.AlphaAnimation livePulse =
                new android.view.animation.AlphaAnimation(1.0f, 0.35f);
        livePulse.setDuration(800);
        livePulse.setRepeatMode(android.view.animation.Animation.REVERSE);
        livePulse.setRepeatCount(android.view.animation.Animation.INFINITE);
        live.startAnimation(livePulse);

        TextView teams = createText(
                match.getHomeTeam()
                        + "   VS   "
                        + match.getAwayTeam(),
                17,
                "#FFFFFF"
        );

        teams.setTypeface(
                null,
                Typeface.BOLD
        );

        teams.setGravity(
                Gravity.CENTER
        );

        teams.setPadding(
                0,
                8,
                0,
                8
        );

        String competition = match.getCompetition();

        if (competition == null) {
            competition = "";
        }

        TextView comp = createText(
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

        String liveTitle = match.getLiveTitle();

        if (liveTitle != null &&
                !liveTitle.trim().isEmpty()) {

            TextView title = createText(
                    liveTitle,
                    13,
                    "#AAAAAA"
            );

            title.setGravity(
                    Gravity.CENTER
            );

            title.setPadding(
                    0,
                    6,
                    0,
                    0
            );

            card.addView(title);
        }

        TextView watch = createText(
                "▶  TAZAMA LIVE",
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
                14,
                12,
                14,
                12
        );

        watch.setBackgroundResource(
                R.drawable.bg_live_button
        );

        LinearLayout.LayoutParams watchParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        watchParams.setMargins(
                0,
                14,
                0,
                0
        );

        watch.setLayoutParams(watchParams);

        card.addView(watch);

        String streamUrl = match.getStreamUrl();

        if (streamUrl != null &&
                !streamUrl.trim().isEmpty()) {

            View.OnClickListener openPlayer = v -> {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                LivePlayerActivity.class
                        );

                intent.putExtra(
                        "live_id",
                        match.getId()
                );

                intent.putExtra(
                        "stream_url",
                        match.getStreamUrl()
                );

                intent.putExtra(
                        "stream_type",
                        match.getStreamType()
                );

                intent.putExtra(
                        "live_title",
                        match.getLiveTitle()
                );

                intent.putExtra(
                        "home_team",
                        match.getHomeTeam()
                );

                intent.putExtra(
                        "away_team",
                        match.getAwayTeam()
                );

                intent.putExtra(
                        "competition",
                        match.getCompetition()
                );

                intent.putExtra(
                        "match_time",
                        match.getMatchTime()
                );

                startActivity(intent);
            };

            card.setOnClickListener(openPlayer);
            watch.setOnClickListener(openPlayer);

        } else {

            watch.setText("LIVE INAKUJA");
            watch.setAlpha(0.55f);
            watch.setEnabled(false);

            card.setAlpha(0.75f);
        }

        liveContainer.addView(card);
    }

    private void loadHomeStandings() {
        FootballService.getResults(new FootballService.Callback() {
            @Override
            public void onSuccess(JSONArray data) {
                runOnUiThread(() -> {
                    standingsContainer.removeAllViews();

                    try {
                        JSONArray standings =
                                StandingsCalculator.calculate(data);

                        if (standings.length() == 0) {
                            addMessage(
                                    standingsContainer,
                                    "Hakuna msimamo kwa sasa."
                            );
                            return;
                        }

                        int limit = Math.min(standings.length(), 6);

                        for (int i = 0; i < limit; i++) {
                            JSONObject team =
                                    standings.getJSONObject(i);

                            LinearLayout row = createCard();
                            row.setPadding(16, 12, 16, 12);

                            TextView name = createText(
                                    team.optInt("position", i + 1)
                                            + ". "
                                            + team.optString("team", "Timu"),
                                    14,
                                    "#FFFFFF"
                            );
                            name.setTypeface(null, Typeface.BOLD);

                            TextView stats = createText(
                                    "MP " + team.optInt("played", 0)
                                            + "   W " + team.optInt("won", 0)
                                            + "   D " + team.optInt("drawn", 0)
                                            + "   L " + team.optInt("lost", 0)
                                            + "   PTS " + team.optInt("points", 0),
                                    11,
                                    "#AAAAAA"
                            );

                            row.addView(name);
                            row.addView(stats);
                            standingsContainer.addView(row);
                        }

                    } catch (Exception e) {
                        addMessage(
                                standingsContainer,
                                "Imeshindikana kupakia msimamo."
                        );
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        addMessage(
                                standingsContainer,
                                "Imeshindikana kupakia msimamo."
                        )
                );
            }
        });
    }

    private void loadResults() {
        FootballService.getResults(new FootballService.Callback() {
            @Override
            public void onSuccess(JSONArray data) {
                runOnUiThread(() -> {
                    resultsContainer.removeAllViews();

                    if (data == null || data.length() == 0) {
                        addMessage(resultsContainer, "Hakuna matokeo kwa sasa.");
                        return;
                    }

                    int limit = Math.min(data.length(), 6);

                    for (int i = 0; i < limit; i++) {
                        try {
                            JSONObject result = data.getJSONObject(i);

                            String competition =
                                    result.optString("competition", "MECHI");
                            String home =
                                    result.optString("home_team", "Home");
                            String away =
                                    result.optString("away_team", "Away");
                            String status =
                                    result.optString("status", "FT");
                            String date =
                                    result.optString("match_date", "");

                            int homeScore =
                                    result.optInt("home_score", 0);
                            int awayScore =
                                    result.optInt("away_score", 0);

                            LinearLayout card = createCard();

                            TextView comp = createText(
                                    competition.toUpperCase(),
                                    11,
                                    "#E30613"
                            );
                            comp.setTypeface(null, Typeface.BOLD);

                            TextView teams = createText(
                                    home + "    " + homeScore
                                            + "  -  "
                                            + awayScore + "    " + away,
                                    15,
                                    "#FFFFFF"
                            );
                            teams.setTypeface(null, Typeface.BOLD);
                            teams.setGravity(Gravity.CENTER);
                            teams.setPadding(0, 14, 0, 14);

                            TextView info = createText(
                                    status.toUpperCase()
                                            + (date.isEmpty()
                                            ? ""
                                            : "  •  " + date),
                                    11,
                                    "#999999"
                            );
                            info.setGravity(Gravity.CENTER);

                            card.addView(comp);
                            card.addView(teams);
                            card.addView(info);

                            resultsContainer.addView(card);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    resultsContainer.removeAllViews();
                    addMessage(
                            resultsContainer,
                            "Imeshindikana kupakia matokeo."
                    );
                });
            }
        });
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


    private void loadImageIntoView(String imageUrl, ImageView imageView) {
        if (imageView == null || imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                java.net.HttpURLConnection connection =
                        (java.net.HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.connect();

                Bitmap bitmap =
                        BitmapFactory.decodeStream(connection.getInputStream());

                connection.disconnect();

                if (bitmap != null) {
                    imageView.post(() -> imageView.setImageBitmap(bitmap));
                }

            } catch (Exception ignored) {
            }
        }).start();
    }

    private void addBreakingCard(NewsItem news) {
        if (breakingTitle == null || news == null) {
            return;
        }

        breakingTitle.setText(safeText(news.getTitle()));
        breakingTitle.setSingleLine(true);
        breakingTitle.setEllipsize(android.text.TextUtils.TruncateAt.MARQUEE);
        breakingTitle.setMarqueeRepeatLimit(-1);
        breakingTitle.setSelected(true);
        breakingTitle.post(() -> breakingTitle.setSelected(true));
        breakingTitle.setOnClickListener(v -> openArticle(news));
    }

    private void addNewsCard(LinearLayout container, NewsItem news) {
        if (container == null || news == null) {
            return;
        }

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(12, 12, 12, 12);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(16, 16, 16));
        bg.setCornerRadius(14);
        card.setBackground(bg);

        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        dp(105),
                        dp(80)
                );

        card.addView(image, imageParams);

        LinearLayout textBox = new LinearLayout(this);
        textBox.setOrientation(LinearLayout.VERTICAL);
        textBox.setPadding(12, 0, 0, 0);

        TextView category = new TextView(this);
        category.setText(safeText(news.getCategory()).toUpperCase(Locale.US));
        category.setTextColor(Color.rgb(229, 57, 53));
        category.setTextSize(11);
        category.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView title = new TextView(this);
        title.setText(safeText(news.getTitle()));
        title.setTextColor(Color.WHITE);
        title.setTextSize(15);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setMaxLines(3);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);

        TextView time = new TextView(this);
        time.setText(formatNewsTime(news.getCreatedAt()));
        time.setTextColor(Color.GRAY);
        time.setTextSize(11);
        time.setPadding(0, 5, 0, 0);

        textBox.addView(category);
        textBox.addView(title);
        textBox.addView(time);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        card.addView(textBox, textParams);

        String imageUrl = news.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            loadImageIntoView(imageUrl, image);
        }

        card.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ArticleDetailActivity.class
            );
            intent.putExtra("article_id", news.getId());
            startActivity(intent);
        });

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, dp(10));
        container.addView(card, cardParams);
    }
private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String formatNewsTime(String date) {
        if (date == null || date.trim().isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat input =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.US
                    );

            Date parsed = input.parse(date);

            if (parsed == null) {
                return "";
            }

            long diff = System.currentTimeMillis() - parsed.getTime();

            if (diff < 60000) {
                return "Dakika 1 iliyopita";
            }

            long minutes = diff / 60000;

            if (minutes < 60) {
                return "Dakika " + minutes + " zilizopita";
            }

            long hours = minutes / 60;

            if (hours < 24) {
                return "Saa " + hours + " zilizopita";
            }

            long days = hours / 24;

            if (days == 1) {
                return "Jana";
            }

            return days + " siku zilizopita";

        } catch (Exception e) {
            return "";
        }
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
