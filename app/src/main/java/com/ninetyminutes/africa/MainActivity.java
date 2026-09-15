package com.ninetyminutes.africa;

import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Typeface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private int adminLogoTaps = 0;
    private long lastAdminLogoTap = 0L;

    private void handleAdminLogoTap() {
        long now = System.currentTimeMillis();

        if (now - lastAdminLogoTap > 2000) {
            adminLogoTaps = 0;
        }

        lastAdminLogoTap = now;
        adminLogoTaps++;

        if (adminLogoTaps >= 5) {
            adminLogoTaps = 0;
            startActivity(new Intent(this, AdminLoginActivity.class));
        }
    }



    private TextView breakingTitle;
    private TextView featuredTitle;
    private TextView featuredMeta;
    private ImageView featuredImage;
    private LinearLayout latestNewsContainer;
    private LinearLayout fixturesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView adminLogo = findViewById(R.id.adminLogo);
        adminLogo.setOnClickListener(v -> handleAdminLogoTap());
        TextView visitWebsite = findViewById(R.id.visitWebsite);
        visitWebsite.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.website_glow));
        visitWebsite.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://90minutesafrica.online"));
            startActivity(browserIntent);
        });

        View sportLiveIndicator = findViewById(R.id.sportLiveIndicator);
        android.view.animation.Animation liveBlink =
                android.view.animation.AnimationUtils.loadAnimation(
                        this,
                        R.anim.live_blink
                );
        sportLiveIndicator.startAnimation(liveBlink);

        breakingTitle = findViewById(R.id.breakingTitle);
        featuredTitle = findViewById(R.id.featuredTitle);
        featuredMeta = findViewById(R.id.featuredMeta);
        featuredImage = findViewById(R.id.featuredImage);
        latestNewsContainer = findViewById(R.id.latestNewsContainer);
        fixturesContainer = findViewById(R.id.fixturesContainer);

        findViewById(R.id.menuButton).setOnClickListener(this::showNavigationMenu);

        findViewById(R.id.navTanzania).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TanzaniaActivity.class))
        );

        findViewById(R.id.navInternational).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, InternationalActivity.class))
        );

        findViewById(R.id.navTransfers).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TransfersActivity.class))
        );

        findViewById(R.id.navClubs).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ClubsActivity.class))
        );

        findViewById(R.id.navPlayers).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PlayersActivity.class))
        );

        findViewById(R.id.navCompetitions).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CompetitionsActivity.class))
        );

        findViewById(R.id.footerAbout).setOnClickListener(v -> openLegal("about"));
        findViewById(R.id.footerTerms).setOnClickListener(v -> openLegal("terms"));
        findViewById(R.id.footerPrivacy).setOnClickListener(v -> openLegal("privacy"));
        findViewById(R.id.footerCookies).setOnClickListener(v -> openLegal("cookies"));
        findViewById(R.id.footerDisclaimer).setOnClickListener(v -> openLegal("disclaimer"));
        findViewById(R.id.footerContact).setOnClickListener(v -> openLegal("contact"));

        findViewById(R.id.footerWhatsApp).setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    android.net.Uri.parse(
                            "https://whatsapp.com/channel/0029Vb6mtUXDjiOZZOGkCw00"
                    )
            );
            startActivity(intent);
        });

        loadNews();
        loadFixtures();
    }


    private void openLegal(String page) {
        Intent intent = new Intent(MainActivity.this, LegalActivity.class);
        intent.putExtra("page", page);
        startActivity(intent);
    }

    private void showNavigationMenu(View anchor) {
        View menuView = LayoutInflater.from(this)
                .inflate(R.layout.popup_nav_menu, null);

        PopupWindow popupWindow = new PopupWindow(
                menuView,
                dp(260),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT)
        );
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(dp(12));

        TextView menuHome = menuView.findViewById(R.id.menuHome);
        TextView menuResults = menuView.findViewById(R.id.menuResults);
        TextView menuStandings = menuView.findViewById(R.id.menuStandings);

        menuHome.setOnClickListener(v -> {
            popupWindow.dismiss();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        menuResults.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(MainActivity.this, ResultsActivity.class));
        });

        menuStandings.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(MainActivity.this, StandingsActivity.class));
        });

        popupWindow.showAsDropDown(anchor, -dp(214), dp(4));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (breakingTitle != null) {
            loadNews();
        }
    }

    private void loadNews() {
        NewsService.loadNews(new NewsService.Callback() {

            @Override
            public void onSuccess(JSONArray news) {
                runOnUiThread(() -> displayNews(news));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    featuredTitle.setText("Imeshindwa kupakia habari.");
                    featuredMeta.setText("");
                });
            }
        });
    }

    private void displayNews(JSONArray news) {
        try {
            latestNewsContainer.removeAllViews();

            if (news.length() == 0) {
                featuredTitle.setText("Hakuna habari kwa sasa.");
                featuredMeta.setText("");
                return;
            }

            JSONObject breaking = null;

            for (int i = 0; i < news.length(); i++) {
                JSONObject item = news.getJSONObject(i);
                if (item.optBoolean("breaking", false)) {
                    breaking = item;
                    break;
                }
            }

            if (breaking == null) breaking = news.getJSONObject(0);
            breakingTitle.setText(
                    breaking.optString("title", "Hakuna breaking news kwa sasa.")
            );

            TranslateAnimation animation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, -1.0f,
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, 0
            );

            animation.setDuration(15000);
            animation.setRepeatCount(TranslateAnimation.INFINITE);
            animation.setRepeatMode(TranslateAnimation.RESTART);
            breakingTitle.startAnimation(animation);

            JSONObject featured = news.getJSONObject(0);

            String featuredImageUrl = featured.optString("image_url", "");
            if (!featuredImageUrl.isEmpty()) {
                new Thread(() -> {
                    try {
                        URL url = new URL(featuredImageUrl);
                        HttpURLConnection connection =
                                (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        input.close();
                        connection.disconnect();

                        if (bitmap != null) {
                            runOnUiThread(() -> {
                                featuredImage.setImageBitmap(bitmap);
                                featuredImage.setVisibility(View.VISIBLE);
                            });
                        }
                    } catch (Exception ignored) {
                    }
                }).start();
            }


            featuredTitle.setText(
                    featured.optString("title", "Hakuna kichwa")
            );

            featuredMeta.setText(
                    featured.optString("category", "Tanzania")
            );

            for (int i = 1; i < news.length() && i < 7; i++) {
                JSONObject item = news.getJSONObject(i);
                addNewsCard(item);
            }

        } catch (Exception e) {
            featuredTitle.setText("Imeshindwa kupakia habari.");
        }
    }

    private void addNewsCard(JSONObject item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(0, 0, 0, 0);
        card.setBackgroundColor(Color.rgb(17, 17, 17));

        ImageView image = new ImageView(this);
        image.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                210
        ));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView text = new TextView(this);
        String title = item.optString("title", "Hakuna kichwa");
        String category = item.optString("category", "Tanzania");
        String excerpt = item.optString("excerpt", "");

        text.setText(
                category.toUpperCase() + "\n\n" + title +
                (excerpt.isEmpty() ? "" : "\n\n" + excerpt)
        );
        text.setTextColor(Color.WHITE);
        text.setTextSize(16);
        text.setPadding(18, 18, 18, 18);

        card.addView(image);
        card.addView(text);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        params.setMargins(0, 0, 0, 12);
        card.setLayoutParams(params);

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

        card.setOnClickListener(v -> openArticle(item));
        latestNewsContainer.addView(card);
    }

    private void loadFixtures() {
        FootballService.loadFixtures(new FootballService.Callback() {

            @Override
            public void onSuccess(JSONArray fixtures) {
                runOnUiThread(() -> displayLiveFixtures(fixtures));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        findViewById(R.id.liveStatus).setVisibility(View.VISIBLE)
                );
            }
        });
    }

    private void displayLiveFixtures(JSONArray fixtures) {
        TextView liveStatus = findViewById(R.id.liveStatus);
        LinearLayout liveContainer = findViewById(R.id.liveContainer);

        liveContainer.removeAllViews();

        try {
            int liveCount = 0;

            for (int i = 0; i < fixtures.length(); i++) {
                JSONObject match = fixtures.getJSONObject(i);

                if (!match.optBoolean("is_live", false)) {
                    continue;
                }

                String home = match.optString("home_team", "Home");
                String away = match.optString("away_team", "Away");
                String time = match.optString("match_time", "");
                String title = match.optString("live_title", "");
                String competition = match.optString("competition", "");
                String streamUrl = match.optString("stream_url", "");
                String streamType = match.optString("stream_type", "hls");

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(17, 17, 17, 17);
                card.setBackgroundColor(Color.rgb(16, 16, 16));

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                cardParams.setMargins(0, 0, 0, 12);
                card.setLayoutParams(cardParams);

                TextView liveTime = new TextView(this);
                liveTime.setText(
                        "🔴 LIVE" +
                        (time.isEmpty() ? "" : "     " + time.substring(0, Math.min(5, time.length())))
                );
                liveTime.setTextColor(Color.rgb(255, 59, 48));
                liveTime.setTextSize(12);
                liveTime.setTypeface(null, Typeface.BOLD);

                TextView teams = new TextView(this);
                teams.setText(home + "    VS    " + away);
                teams.setTextColor(Color.WHITE);
                teams.setTextSize(15);
                teams.setTypeface(null, Typeface.BOLD);
                teams.setGravity(Gravity.CENTER);
                teams.setPadding(0, 14, 0, 10);

                TextView status = new TextView(this);
                String statusText = !title.isEmpty()
                        ? title
                        : (!competition.isEmpty() ? competition : "LIVE");

                status.setText(statusText);
                status.setTextColor(Color.rgb(227, 6, 19));
                status.setTextSize(11);
                status.setTypeface(null, Typeface.BOLD);
                status.setGravity(Gravity.CENTER);

                Button watchButton = new Button(this);
                watchButton.setText(
                        streamUrl.isEmpty()
                                ? "LIVE INAKUJA"
                                : "▶ TAZAMA LIVE"
                );
                watchButton.setTextColor(Color.WHITE);
                watchButton.setTextSize(13);
                watchButton.setAllCaps(false);
                watchButton.setEnabled(!streamUrl.isEmpty());

                if (!streamUrl.isEmpty()) {
                    watchButton.setOnClickListener(v -> {
                        Intent intent =
                                new Intent(MainActivity.this, LiveActivity.class);

                        intent.putExtra("stream_url", streamUrl);
                        intent.putExtra("stream_type", streamType);
                        intent.putExtra("live_title", title);
                        intent.putExtra("competition", competition);
                        intent.putExtra("home_team", home);
                        intent.putExtra("away_team", away);

                        startActivity(intent);
                    });
                }

                card.addView(liveTime);
                card.addView(teams);
                card.addView(status);
                card.addView(watchButton);

                liveContainer.addView(card);
                liveCount++;
            }

            if (liveCount == 0) {
                liveStatus.setVisibility(View.VISIBLE);
                liveStatus.setText(
                        "Hakuna mechi inayorushwa LIVE kwa sasa."
                );
            } else {
                liveStatus.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            liveStatus.setVisibility(View.VISIBLE);
            liveStatus.setText(
                    "Hakuna mechi inayorushwa LIVE kwa sasa."
            );
        }
    }

    private void displayFixtures(JSONArray fixtures) {
        fixturesContainer.removeAllViews();

        try {
            java.util.ArrayList<JSONObject> upcoming = new java.util.ArrayList<>();

            java.text.SimpleDateFormat formatter =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm",
                            java.util.Locale.getDefault()
                    );

            java.util.Date now = new java.util.Date();

            for (int i = 0; i < fixtures.length(); i++) {
                JSONObject match = fixtures.getJSONObject(i);

                if (match.optBoolean("is_live", false)) {
                    continue;
                }

                String date = match.optString("match_date", "");
                String time = match.optString("match_time", "");

                if (date.isEmpty()) {
                    continue;
                }

                if (time.length() >= 5) {
                    time = time.substring(0, 5);
                } else if (time.isEmpty()) {
                    time = "00:00";
                }

                try {
                    java.util.Date matchDate =
                            formatter.parse(date + "T" + time);

                    if (matchDate != null && matchDate.after(now)) {
                        upcoming.add(match);
                    }
                } catch (Exception ignored) {
                }
            }

            java.util.Collections.sort(
                    upcoming,
                    (a, b) -> {
                        try {
                            String dateA = a.optString("match_date", "");
                            String timeA = a.optString("match_time", "00:00");
                            String dateB = b.optString("match_date", "");
                            String timeB = b.optString("match_time", "00:00");

                            if (timeA.length() >= 5) {
                                timeA = timeA.substring(0, 5);
                            }

                            if (timeB.length() >= 5) {
                                timeB = timeB.substring(0, 5);
                            }

                            java.util.Date aDate =
                                    formatter.parse(dateA + "T" + timeA);
                            java.util.Date bDate =
                                    formatter.parse(dateB + "T" + timeB);

                            return aDate.compareTo(bDate);
                        } catch (Exception e) {
                            return 0;
                        }
                    }
            );

            int count = 0;

            for (JSONObject match : upcoming) {
                if (count >= 7) {
                    break;
                }

                String home = match.optString("home_team", "Home");
                String away = match.optString("away_team", "Away");
                String date = match.optString("match_date", "");
                String time = match.optString("match_time", "");
                if (time.length() >= 5) {
                    time = time.substring(0, 5);
                }
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(16, 14, 16, 14);
                card.setBackgroundResource(R.drawable.bg_fixture_card);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 12);
                card.setLayoutParams(params);

                TextView dateView = new TextView(this);
                dateView.setText(date);
                dateView.setTextColor(Color.LTGRAY);
                dateView.setTextSize(12);
                dateView.setGravity(Gravity.CENTER);
                card.addView(dateView);

                TextView timeView = new TextView(this);
                timeView.setText(time);
                timeView.setTextColor(Color.rgb(227, 6, 19));
                timeView.setTextSize(22);
                timeView.setTypeface(null, android.graphics.Typeface.BOLD);
                timeView.setGravity(Gravity.CENTER);
                card.addView(timeView);

                LinearLayout teams = new LinearLayout(this);
                teams.setOrientation(LinearLayout.HORIZONTAL);
                teams.setGravity(Gravity.CENTER_VERTICAL);

                TextView homeView = new TextView(this);
                homeView.setText(home);
                homeView.setTextColor(Color.WHITE);
                homeView.setTextSize(16);
                homeView.setTypeface(null, android.graphics.Typeface.BOLD);
                homeView.setGravity(Gravity.CENTER);
                homeView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                ));

                TextView vsView = new TextView(this);
                vsView.setText("VS");
                vsView.setTextColor(Color.LTGRAY);
                vsView.setTextSize(12);
                vsView.setTypeface(null, android.graphics.Typeface.BOLD);
                vsView.setGravity(Gravity.CENTER);
                vsView.setPadding(10, 0, 10, 0);

                TextView awayView = new TextView(this);
                awayView.setText(away);
                awayView.setTextColor(Color.WHITE);
                awayView.setTextSize(16);
                awayView.setTypeface(null, android.graphics.Typeface.BOLD);
                awayView.setGravity(Gravity.CENTER);
                awayView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                ));

                teams.addView(homeView);
                teams.addView(vsView);
                teams.addView(awayView);
                card.addView(teams);

                fixturesContainer.addView(card);
                count++;
            }

            if (count == 0) {
                TextView empty = new TextView(this);
                empty.setText("Hakuna ratiba kwa sasa.");
                empty.setTextColor(Color.LTGRAY);
                empty.setTextSize(15);
                empty.setPadding(18, 18, 18, 18);
                fixturesContainer.addView(empty);
            }

        } catch (Exception e) {
            TextView error = new TextView(this);
            error.setText("Imeshindwa kupakia ratiba.");
            error.setTextColor(Color.LTGRAY);
            error.setTextSize(15);
            error.setPadding(18, 18, 18, 18);
            fixturesContainer.addView(error);
        }
    }

    private void openArticle(JSONObject item) {
        try {
            String newsId = item.optString("id", "");

            if (newsId.isEmpty()) {
                return;
            }

            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            ArticleDetailActivity.class
                    );

            intent.putExtra("news_id", newsId);
            startActivity(intent);

        } catch (Exception e) {
            // Ignore invalid article
        }
    }
}
