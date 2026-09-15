package com.ninetyminutes.africa;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private final android.os.Handler liveAnalyticsHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());

    private Runnable liveAnalyticsRefresh;

    private Uri selectedImageUri;
    
    private static final int IMAGE_PICK_REQUEST = 9001;
    
    private void pickNewsImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    
        if (requestCode == IMAGE_PICK_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
    
            selectedImageUri = data.getData();
    
            try {
                getContentResolver().takePersistableUriPermission(
                        selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {
            }
    
            Toast.makeText(
                    this,
                    "Picha imechaguliwa",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private static final String PREFS_NAME = "admin_session";
    private static final String TOKEN_KEY = "access_token";

    private static final int RED = Color.rgb(227, 6, 19);
    private static final int WHITE = Color.WHITE;
    private static final int DARK = Color.rgb(10, 10, 10);
    private static final int CARD = Color.rgb(20, 20, 20);
    private static final int INPUT = Color.rgb(32, 32, 32);
    private static final int GRAY = Color.rgb(175, 175, 175);

    private String accessToken;
    private OkHttpClient client;

    private LinearLayout content;
    private TextView messageText;
    private TextView errorText;

    private final ArrayList<JSONObject> newsItems = new ArrayList<>();
    private final ArrayList<JSONObject> fixtureItems = new ArrayList<>();
    private final ArrayList<JSONObject> resultItems = new ArrayList<>();
    private final ArrayList<JSONObject> standingItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        accessToken = prefs.getString(TOKEN_KEY, null);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            openLogin();
            return;
        }

        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        buildDashboard();

        checkAdmin(() -> {
            loadAll();
        });
    }

    private static final String ADMIN_EMAIL =
            "simbayanga4888@gmail.com";

    private void checkAdmin(Runnable onAllowed) {

        new Thread(() -> {

            try {

                Request request =
                        new Request.Builder()
                                .url(
                                        SupabaseConfig.URL
                                                + "/auth/v1/user"
                                )
                                .addHeader(
                                        "apikey",
                                        SupabaseConfig.KEY
                                )
                                .addHeader(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                                .get()
                                .build();

                Response response =
                        client.newCall(request).execute();

                String body =
                        response.body() != null
                                ? response.body().string()
                                : "";

                if (!response.isSuccessful()) {
                    runOnUiThread(this::openLogin);
                    return;
                }

                JSONObject user =
                        new JSONObject(body);

                String email =
                        user.optString(
                                "email",
                                ""
                        );

                if (!ADMIN_EMAIL.equalsIgnoreCase(email)) {

                    runOnUiThread(() -> {
                        Toast.makeText(
                                this,
                                "Huna ruhusa ya Admin.",
                                Toast.LENGTH_LONG
                        ).show();

                        logout();
                    });

                    return;
                }

                runOnUiThread(onAllowed);

            } catch (Exception e) {

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "Session ya Admin imekwisha. Ingia tena.",
                            Toast.LENGTH_LONG
                    ).show();

                    openLogin();
                });
            }

        }).start();
    }

    private void buildDashboard() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(DARK);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(16), dp(8), dp(12), dp(8));
        header.setBackgroundColor(Color.rgb(15, 15, 15));

        TextView logo = text(
                "90' MINUTES AFRICA",
                17,
                WHITE
        );
        logo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        header.addView(
                logo,
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1
                )
        );

        Button logout = button("TOKA");
        logout.setOnClickListener(v -> logout());

        header.addView(
                logout,
                new LinearLayout.LayoutParams(
                        dp(78),
                        dp(42)
                )
        );

        root.addView(header);

        View divider = new View(this);
        divider.setBackgroundColor(RED);

        root.addView(
                divider,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(2)
                )
        );

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(
                dp(14),
                dp(16),
                dp(14),
                dp(30)
        );

        TextView title = text(
                "ADMIN DASHBOARD",
                24,
                WHITE
        );
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        content.addView(title);

        TextView subtitle = text(
                "Mfumo wa usimamizi wa 90' Minutes Africa",
                14,
                GRAY
        );
        content.addView(subtitle);

        messageText = text(
                "",
                14,
                Color.rgb(100, 220, 130)
        );
        messageText.setVisibility(View.GONE);
        content.addView(messageText);

        errorText = text(
                "",
                14,
                Color.rgb(255, 100, 100)
        );
        errorText.setVisibility(View.GONE);
        content.addView(errorText);

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setPadding(0, dp(12), 0, dp(10));

        String[] tabsNames = {
                "HABARI",
                "RATIBA",
                "MATOKEO",
                "MSIMAMO"
        };

        for (int i = 0; i < tabsNames.length; i++) {
            final int index = i;

            Button tab = button(tabsNames[i]);
            tab.setTextSize(11);

            tab.setOnClickListener(
                    v -> showSection(index)
            );

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(
                            0,
                            dp(46),
                            1
                    );

            p.setMargins(
                    dp(2),
                    0,
                    dp(2),
                    0
            );

            tabs.addView(tab, p);
        }

        content.addView(tabs);

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        setContentView(root);

        showSection(0);
    }

    private void showSection(int section) {
        while (content.getChildCount() > 6) {
            content.removeViewAt(6);
        }

        switch (section) {
            case 0:
                buildNewsSection();
                break;

            case 1:
                buildFixturesSection();
                break;

            case 2:
                buildResultsSection();
                break;

            case 3:
                buildStandingsSection();
                break;
        }
    }

    private void buildNewsSection() {
        addHeading("ONGEZA HABARI");

        EditText title = input("Kichwa cha habari");
        EditText excerpt = input("Maelezo mafupi");

        EditText contentInput =
                input("Maudhui ya habari");

        contentInput.setMinLines(7);
        contentInput.setGravity(Gravity.TOP);
        Button imageButton = button("CHAGUA PICHA");
        TextView imageStatus = text("Hakuna picha iliyochaguliwa.", 14f, GRAY);

        imageButton.setOnClickListener(v -> {
            pickNewsImage();
            imageStatus.setText("Chagua picha kutoka kwenye simu...");
        });


        Spinner category = spinner(new String[]{
                "Tanzania",
                "Kimataifa",
                "Vilabu",
                "Mashindano",
                "Usajili",
                "Wachezaji"
        });

        EditText author = input("Mwandishi");
        author.setText("90 Minutes Africa");

        CheckBox published =
                check("Published", true);

        CheckBox breaking =
                check("🔥 Breaking", false);

        CheckBox featured =
                check("⭐ Featured", false);

        Button publish =
                button("PUBISHA HABARI");

        content.addView(title);
        content.addView(excerpt);
        content.addView(contentInput);
        content.addView(imageButton);
        content.addView(imageStatus);
        content.addView(category);
        content.addView(author);
        content.addView(published);
        content.addView(breaking);
        content.addView(featured);
        content.addView(publish);

        publish.setOnClickListener(v -> {
        String titleValue = title.getText().toString().trim();
        String excerptValue = excerpt.getText().toString().trim();
        String contentValue = contentInput.getText().toString().trim();
        String authorValue = author.getText().toString().trim();

        if (titleValue.isEmpty()) {
            showError("Andika kichwa cha habari.");
            return;
        }

        if (excerptValue.isEmpty()) {
            showError("Andika muhtasari wa habari.");
            return;
        }

        if (contentValue.isEmpty()) {
            showError("Andika habari kamili.");
            return;
        }

        publish.setEnabled(false);
        showMessage("Inahifadhi habari...");

        new Thread(() -> {
            try {
                String imageUrl = null;

                if (selectedImageUri != null) {
                    imageUrl = uploadNewsImage(selectedImageUri);
                }

                JSONObject data = new JSONObject();
                data.put("title", titleValue);
                data.put("excerpt", excerptValue);
                data.put("content", contentValue);
                data.put("category", category.getSelectedItem().toString());
                data.put(
                        "author",
                        authorValue.isEmpty()
                                ? "90 Minutes Africa"
                                : authorValue
                );
                data.put(
                        "image_url",
                        imageUrl == null
                                ? JSONObject.NULL
                                : imageUrl
                );
                data.put("featured", featured.isChecked());
                data.put("breaking", breaking.isChecked());
                data.put("published", published.isChecked());

                JSONObject finalData = data;

                runOnUiThread(() -> {
                    supabaseInsert(
                            "news",
                            finalData,
                            () -> {
                                showMessage(
                                        "Habari imeongezwa kikamilifu."
                                );

                                title.setText("");
                                excerpt.setText("");
                                contentInput.setText("");
                                author.setText("90 Minutes Africa");

                                published.setChecked(true);
                                breaking.setChecked(false);
                                featured.setChecked(false);

                                selectedImageUri = null;
                                imageStatus.setText(
                                        "Hakuna picha iliyochaguliwa."
                                );

                                publish.setEnabled(true);
                                loadNews();
                            }
                    );
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    publish.setEnabled(true);
                    showError(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Imeshindikana kuhifadhi habari."
                    );
                });
            }
        }).start();
    });

        addHeading("HABARI ZILIZOPO");

        if (newsItems.isEmpty()) {
            addEmpty(
                    "Hakuna habari zilizopatikana."
            );
        } else {
            for (JSONObject item : newsItems) {
                if (item != null) {
                    addNewsCard(item);
                }
            }
        }
    }

    private void addNewsCard(JSONObject item) {

        LinearLayout card = card();

        TextView title = text(
                item.optString(
                        "title",
                        "Bila title"
                ),
                17,
                WHITE
        );

        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(title);

        card.addView(
                text(
                        item.optString(
                                "category",
                                ""
                        )
                                + " • "
                                + item.optString(
                                "author",
                                ""
                        ),
                        13,
                        GRAY
                )
        );

        boolean published =
                item.optBoolean(
                        "published",
                        false
                );

        boolean breaking =
                item.optBoolean(
                        "breaking",
                        false
                );

        boolean featured =
                item.optBoolean(
                        "featured",
                        false
                );

        card.addView(
                text(
                        "Published: "
                                + (published
                                ? "NDIYO"
                                : "HAPANA")
                                + "   Breaking: "
                                + (breaking
                                ? "NDIYO"
                                : "HAPANA")
                                + "   Featured: "
                                + (featured
                                ? "NDIYO"
                                : "HAPANA"),
                        12,
                        GRAY
                )
        );

        LinearLayout actions =
                new LinearLayout(this);

        Button pub = smallButton(
                published
                        ? "UNPUBLISH"
                        : "PUBLISH"
        );

        Button br = smallButton(
                breaking
                        ? "ONDOA BREAKING"
                        : "BREAKING"
        );

        Button ft = smallButton(
                featured
                        ? "ONDOA FEATURED"
                        : "FEATURED"
        );

        Button del = smallButton("FUTA");

        actions.addView(pub);
        actions.addView(br);
        actions.addView(ft);
        actions.addView(del);

        card.addView(actions);

        String id =
                item.optString("id", "");

        pub.setOnClickListener(
                v -> updateNewsFlag(
                        id,
                        "published",
                        !published
                )
        );

        br.setOnClickListener(
                v -> updateNewsFlag(
                        id,
                        "breaking",
                        !breaking
                )
        );

        ft.setOnClickListener(
                v -> updateNewsFlag(
                        id,
                        "featured",
                        !featured
                )
        );

        del.setOnClickListener(
                v -> confirmDelete(
                        "news",
                        id,
                        "Habari hii?"
                )
        );

        content.addView(card);
    }

    private void buildFixturesSection() {
        addHeading("ONGEZA RATIBA");

        EditText date =
                input("Tarehe — YYYY-MM-DD");

        EditText time =
                input("Muda — HH:MM");

        EditText competition =
                input("Mashindano");

        EditText home =
                input("Timu ya nyumbani");

        EditText away =
                input("Timu ya ugenini");

        EditText venue =
                input("Uwanja");

        Spinner status = spinner(
                new String[]{
                        "UPCOMING",
                        "POSTPONED",
                        "CANCELLED"
                }
        );

        Button add =
                button("ONGEZA RATIBA");

        content.addView(date);
        content.addView(time);
        content.addView(competition);
        content.addView(home);
        content.addView(away);
        content.addView(venue);
        content.addView(status);
        content.addView(add);

        add.setOnClickListener(v -> {

            if (date.getText().toString().trim().isEmpty()
                    || time.getText().toString().trim().isEmpty()
                    || home.getText().toString().trim().isEmpty()
                    || away.getText().toString().trim().isEmpty()) {

                showError(
                        "Jaza tarehe, muda, home team na away team."
                );
                return;
            }

            JSONObject data = new JSONObject();

            try {
                data.put(
                        "match_date",
                        date.getText().toString().trim()
                );

                data.put(
                        "match_time",
                        time.getText().toString().trim()
                );

                data.put(
                        "competition",
                        competition.getText().toString().trim()
                );

                data.put(
                        "home_team",
                        home.getText().toString().trim()
                );

                data.put(
                        "away_team",
                        away.getText().toString().trim()
                );

                data.put(
                        "venue",
                        venue.getText().toString().trim()
                );

                data.put(
                        "status",
                        status.getSelectedItem().toString()
                );

            } catch (JSONException e) {
                showError(e.getMessage());
                return;
            }

            supabaseInsert(
                    "fixtures",
                    data,
                    () -> {
                        showMessage(
                                "Ratiba imeongezwa."
                        );
                        loadFixtures();
                    }
            );
        });

        addHeading("LIVE ANALYTICS");

        LinearLayout analyticsContainer = new LinearLayout(this);
        analyticsContainer.setOrientation(LinearLayout.VERTICAL);
        analyticsContainer.setPadding(dp(10), dp(10), dp(10), dp(10));
        analyticsContainer.setBackgroundColor(CARD);

        TextView analyticsStatus = text(
                "Inapakia LIVE analytics...",
                13,
                GRAY
        );

        analyticsContainer.addView(analyticsStatus);

        content.addView(
                analyticsContainer,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        loadLiveAnalyticsNative(
                analyticsContainer,
                analyticsStatus
        );

        liveAnalyticsRefresh = () -> {
            if (!isFinishing() && !isDestroyed()) {
                loadLiveAnalyticsNative(
                        analyticsContainer,
                        analyticsStatus
                );

                liveAnalyticsHandler.postDelayed(
                        liveAnalyticsRefresh,
                        10000
                );
            }
        };

        liveAnalyticsHandler.postDelayed(
                liveAnalyticsRefresh,
                10000
        );

    }

    private void loadLiveAnalyticsNative(
            LinearLayout container,
            TextView status
    ) {
        new Thread(() -> {
            try {
                String url =
                        SupabaseConfig.URL
                                + "/rest/v1/fixtures"
                                + "?select=id,home_team,away_team,competition,"
                                + "is_live,total_views,peak_viewers,live_title"
                                + "&is_live=eq.true"
                                + "&order=created_at.desc";

                Request request =
                        new Request.Builder()
                                .url(url)
                                .addHeader(
                                        "apikey",
                                        SupabaseConfig.KEY
                                )
                                .addHeader(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                                .get()
                                .build();

                Response response =
                        client.newCall(request).execute();

                String body =
                        response.body() != null
                                ? response.body().string()
                                : "[]";

                if (!response.isSuccessful()) {
                    throw new Exception(body);
                }

                JSONArray fixtures =
                        new JSONArray(body);

                if (fixtures.length() == 0) {
                    runOnUiThread(() -> {
                        status.setText("Hakuna LIVE kwa sasa.");
                        container.removeViews(
                                1,
                                Math.max(0, container.getChildCount() - 1)
                        );
                    });
                    return;
                }

                int totalCurrent = 0;
                int totalPeak = 0;
                int totalViews = 0;
                long totalWatchSeconds = 0;

                JSONArray analytics =
                        new JSONArray();

                for (int i = 0; i < fixtures.length(); i++) {

                    JSONObject fixture =
                            fixtures.getJSONObject(i);

                    String fixtureId =
                            fixture.optString("id", "");

                    int currentViewers =
                            getCurrentLiveViewers(
                                    fixtureId
                            );

                    int peak =
                            fixture.optInt(
                                    "peak_viewers",
                                    0
                            );

                    int views =
                            fixture.optInt(
                                    "total_views",
                                    0
                            );

                    long watch =
                            getWatchSeconds(
                                    fixtureId
                            );

                    totalCurrent += currentViewers;
                    totalPeak += peak;
                    totalViews += views;
                    totalWatchSeconds += watch;

                    JSONObject item =
                            new JSONObject();

                    item.put(
                            "fixture",
                            fixture
                    );
                    item.put(
                            "current_viewers",
                            currentViewers
                    );
                    item.put(
                            "peak_viewers",
                            peak
                    );
                    item.put(
                            "total_views",
                            views
                    );
                    item.put(
                            "watch_seconds",
                            watch
                    );

                    analytics.put(item);
                }

                int finalTotalCurrent = totalCurrent;
                int finalTotalPeak = totalPeak;
                int finalTotalViews = totalViews;
                long finalWatch = totalWatchSeconds;

                runOnUiThread(() -> {

                    status.setText(
                            "UPDATED: " +
                                    new java.text.SimpleDateFormat(
                                            "HH:mm:ss",
                                            Locale.getDefault()
                                    ).format(
                                            new java.util.Date()
                                    )
                    );

                    container.removeViews(
                            1,
                            Math.max(
                                    0,
                                    container.getChildCount() - 1
                            )
                    );

                    addAnalyticsSummary(
                            container,
                            finalTotalCurrent,
                            finalTotalPeak,
                            finalTotalViews,
                            finalWatch
                    );

                    try {
                        for (
                                int i = 0;
                                i < analytics.length();
                                i++
                        ) {
                            JSONObject item =
                                    analytics.getJSONObject(i);

                            addAnalyticsCard(
                                    container,
                                    item
                            );
                        }
                    } catch (Exception ignored) {
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() ->
                        status.setText(
                                "LIVE analytics error: "
                                        + e.getMessage()
                        )
                );
            }
        }).start();
    }

    private int getCurrentLiveViewers(
            String matchId
    ) throws Exception {

        long cutoff =
                System.currentTimeMillis() - 45000;

        String url =
                SupabaseConfig.URL
                        + "/rest/v1/live_viewers"
                        + "?select=id,last_seen,ended_at"
                        + "&match_id=eq."
                        + Uri.encode(matchId);

        Request request =
                new Request.Builder()
                        .url(url)
                        .addHeader(
                                "apikey",
                                SupabaseConfig.KEY
                        )
                        .addHeader(
                                "Authorization",
                                "Bearer " + accessToken
                        )
                        .get()
                        .build();

        Response response =
                client.newCall(request).execute();

        String body =
                response.body() != null
                        ? response.body().string()
                        : "[]";

        JSONArray viewers =
                new JSONArray(body);

        int active = 0;

        for (int i = 0; i < viewers.length(); i++) {

            JSONObject viewer =
                    viewers.getJSONObject(i);

            if (!viewer.isNull("ended_at")) {
                continue;
            }

            String lastSeen =
                    viewer.optString(
                            "last_seen",
                            ""
                    );

            try {
                long time =
                        java.time.Instant
                                .parse(lastSeen)
                                .toEpochMilli();

                if (time >= cutoff) {
                    active++;
                }
            } catch (Exception ignored) {
            }
        }

        return active;
    }

    private long getWatchSeconds(
            String matchId
    ) throws Exception {

        String url =
                SupabaseConfig.URL
                        + "/rest/v1/live_viewers"
                        + "?select=watch_seconds"
                        + "&match_id=eq."
                        + Uri.encode(matchId);

        Request request =
                new Request.Builder()
                        .url(url)
                        .addHeader(
                                "apikey",
                                SupabaseConfig.KEY
                        )
                        .addHeader(
                                "Authorization",
                                "Bearer " + accessToken
                        )
                        .get()
                        .build();

        Response response =
                client.newCall(request).execute();

        String body =
                response.body() != null
                        ? response.body().string()
                        : "[]";

        JSONArray viewers =
                new JSONArray(body);

        long total = 0;

        for (int i = 0; i < viewers.length(); i++) {
            total += viewers
                    .getJSONObject(i)
                    .optLong(
                            "watch_seconds",
                            0
                    );
        }

        return total;
    }

    private void addAnalyticsSummary(
            LinearLayout container,
            int current,
            int peak,
            int views,
            long watchSeconds
    ) {

        LinearLayout summary =
                new LinearLayout(this);

        summary.setOrientation(
                LinearLayout.VERTICAL
        );

        summary.setPadding(
                dp(8),
                dp(8),
                dp(8),
                dp(12)
        );

        summary.addView(
                text(
                        "LIVE SASA: " + current,
                        15,
                        WHITE
                )
        );

        summary.addView(
                text(
                        "WATAZAMAJI: " + current,
                        15,
                        WHITE
                )
        );

        summary.addView(
                text(
                        "PEAK: " + peak,
                        15,
                        WHITE
                )
        );

        summary.addView(
                text(
                        "TOTAL VIEWS: " + views,
                        15,
                        WHITE
                )
        );

        summary.addView(
                text(
                        "WATCH TIME: "
                                + formatWatchTime(
                                        watchSeconds
                                ),
                        15,
                        WHITE
                )
        );

        container.addView(summary);
    }

    private String formatWatchTime(
            long seconds
    ) {

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format(
                    Locale.getDefault(),
                    "%dh %02dm %02ds",
                    hours,
                    minutes,
                    secs
            );
        }

        return String.format(
                Locale.getDefault(),
                "%dm %02ds",
                minutes,
                secs
        );
    }

    private void addAnalyticsCard(
            LinearLayout container,
            JSONObject item
    ) {

        try {

            JSONObject fixture =
                    item.getJSONObject("fixture");

            String home =
                    fixture.optString(
                            "home_team",
                            "Home"
                    );

            String away =
                    fixture.optString(
                            "away_team",
                            "Away"
                    );

            String competition =
                    fixture.optString(
                            "competition",
                            ""
                    );

            String title =
                    fixture.optString(
                            "live_title",
                            ""
                    );

            LinearLayout card =
                    new LinearLayout(this);

            card.setOrientation(
                    LinearLayout.VERTICAL
            );

            card.setPadding(
                    dp(12),
                    dp(12),
                    dp(12),
                    dp(12)
            );

            card.setBackgroundColor(
                    Color.rgb(28, 28, 28)
            );

            card.addView(
                    text(
                            title.isEmpty()
                                    ? home + " vs " + away
                                    : title,
                            17,
                            WHITE
                    )
            );

            if (!competition.isEmpty()) {
                card.addView(
                        text(
                                competition,
                                13,
                                GRAY
                        )
                );
            }

            card.addView(
                    text(
                            "Watazamaji sasa: "
                                    + item.optInt(
                                            "current_viewers"
                                    ),
                            14,
                            WHITE
                    )
            );

            card.addView(
                    text(
                            "Peak: "
                                    + item.optInt(
                                            "peak_viewers"
                                    ),
                            14,
                            WHITE
                    )
            );

            card.addView(
                    text(
                            "Total views: "
                                    + item.optInt(
                                            "total_views"
                                    ),
                            14,
                            WHITE
                    )
            );

            card.addView(
                    text(
                            "Watch time: "
                                    + formatWatchTime(
                                            item.optLong(
                                                    "watch_seconds"
                                            )
                                    ),
                            14,
                            WHITE
                    )
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            -1,
                            -2
                    );

            params.setMargins(
                    0,
                    dp(8),
                    0,
                    0
            );

            container.addView(
                    card,
                    params
            );

        } catch (Exception ignored) {
        }
    }

    private void addFixtureCard(JSONObject item) {

        LinearLayout card = card();

        TextView match = text(
                item.optString(
                        "home_team",
                        "Home"
                )
                        + "  VS  "
                        + item.optString(
                        "away_team",
                        "Away"
                ),
                17,
                WHITE
        );

        match.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(match);

        card.addView(
                text(
                        item.optString(
                                "match_date",
                                ""
                        )
                                + " • "
                                + item.optString(
                                "match_time",
                                ""
                        ),
                        13,
                        GRAY
                )
        );

        card.addView(
                text(
                        item.optString(
                                "competition",
                                ""
                        )
                                + " • "
                                + item.optString(
                                "venue",
                                ""
                        ),
                        13,
                        GRAY
                )
        );

        card.addView(
                text(
                        "Status: "
                                + item.optString(
                                "status",
                                ""
                        ),
                        13,
                        RED
                )
        );

        boolean live =
                item.optBoolean(
                        "is_live",
                        false
                );

        Button liveButton =
                smallButton(
                        live
                                ? "EDIT LIVE"
                                : "WEKA LIVE"
                );

        Button delete =
                smallButton("FUTA");

        LinearLayout actions =
                new LinearLayout(this);

        actions.addView(liveButton);
        actions.addView(delete);

        card.addView(actions);

        liveButton.setOnClickListener(
                v -> showLiveEditor(item)
        );

        delete.setOnClickListener(
                v -> confirmDelete(
                        "fixtures",
                        item.optString("id", ""),
                        "Ratiba hii?"
                )
        );

        content.addView(card);
    }

    private void addLiveAnalytics() {

        LinearLayout box = card();

        int live = 0;
        int viewers = 0;
        int peak = 0;
        int total = 0;

        long watchSeconds = 0;

        for (JSONObject item : fixtureItems) {

            if (item == null) continue;

            if (item.optBoolean(
                    "is_live",
                    false
            )) {
                live++;

                viewers += item.optInt(
                        "current_viewers",
                        0
                );

                peak += item.optInt(
                        "peak_viewers",
                        0
                );

                total += item.optInt(
                        "total_views",
                        0
                );

                watchSeconds += item.optLong(
                        "watch_seconds",
                        0
                );
            }
        }

        box.addView(
                text(
                        "LIVE SASA: " + live,
                        15,
                        WHITE
                )
        );

        box.addView(
                text(
                        "WATAZAMAJI: " + viewers,
                        15,
                        WHITE
                )
        );

        box.addView(
                text(
                        "PEAK: " + peak,
                        15,
                        WHITE
                )
        );

        box.addView(
                text(
                        "TOTAL VIEWS: " + total,
                        15,
                        WHITE
                )
        );

        box.addView(
                text(
                        "WATCH TIME: "
                                + (watchSeconds / 60)
                                + " min",
                        15,
                        WHITE
                )
        );

        Button refresh =
                smallButton("REFRESH");

        box.addView(refresh);

        refresh.setOnClickListener(
                v -> loadFixtures()
        );

        content.addView(box);
    }

    private void showLiveEditor(JSONObject fixture) {

        LinearLayout form =
                new LinearLayout(this);

        form.setOrientation(
                LinearLayout.VERTICAL
        );

        CheckBox enabled =
                check(
                        "Washa LIVE",
                        fixture.optBoolean(
                                "is_live",
                                false
                        )
                );

        EditText title =
                input("Kichwa cha LIVE");

        title.setText(
                fixture.optString(
                        "live_title",
                        ""
                )
        );

        EditText url =
                input("Stream URL");

        url.setText(
                fixture.optString(
                        "stream_url",
                        ""
                )
        );

        Spinner type =
                spinner(
                        new String[]{
                                "hls",
                                "dash",
                                "iframe"
                        }
                );

        form.addView(enabled);
        form.addView(title);
        form.addView(url);
        form.addView(type);

        new AlertDialog.Builder(this)
                .setTitle(
                        fixture.optString(
                                "home_team",
                                ""
                        )
                                + " VS "
                                + fixture.optString(
                                "away_team",
                                ""
                        )
                )
                .setView(form)
                .setNegativeButton(
                        "FUNGUA",
                        null
                )
                .setPositiveButton(
                        "HIFADHI LIVE",
                        (dialog, which) -> {

                            JSONObject data =
                                    new JSONObject();

                            try {
                                data.put(
                                        "is_live",
                                        enabled.isChecked()
                                );

                                data.put(
                                        "live_title",
                                        title.getText()
                                                .toString()
                                                .trim()
                                );

                                data.put(
                                        "stream_url",
                                        url.getText()
                                                .toString()
                                                .trim()
                                );

                                data.put(
                                        "stream_type",
                                        type.getSelectedItem()
                                                .toString()
                                );

                            } catch (JSONException e) {
                                showError(
                                        e.getMessage()
                                );
                                return;
                            }

                            supabaseUpdate(
                                    "fixtures",
                                    fixture.optString("id"),
                                    data,
                                    () -> {
                                        showMessage(
                                                "LIVE imehifadhiwa."
                                        );
                                        loadFixtures();
                                    }
                            );
                        }
                )
                .show();
    }

    private void buildResultsSection() {

        addHeading("ONGEZA MATOKEO");

        EditText date =
                input("Tarehe — YYYY-MM-DD");

        EditText competition =
                input("Mashindano");

        EditText home =
                input("Timu ya nyumbani");

        EditText away =
                input("Timu ya ugenini");

        EditText homeScore =
                numberInput("Magoli ya nyumbani");

        EditText awayScore =
                numberInput("Magoli ya ugenini");

        Spinner status =
                spinner(
                        new String[]{
                                "FT",
                                "HT",
                                "AET",
                                "PEN"
                        }
                );

        Button add =
                button("ONGEZA MATOKEO");

        content.addView(date);
        content.addView(competition);
        content.addView(home);
        content.addView(away);
        content.addView(homeScore);
        content.addView(awayScore);
        content.addView(status);
        content.addView(add);

        add.setOnClickListener(v -> {

            if (date.getText().toString().trim().isEmpty()
                    || home.getText().toString().trim().isEmpty()
                    || away.getText().toString().trim().isEmpty()) {

                showError(
                        "Jaza tarehe, home team na away team."
                );
                return;
            }

            JSONObject data =
                    new JSONObject();

            try {
                data.put(
                        "match_date",
                        date.getText().toString().trim()
                );

                data.put(
                        "competition",
                        competition.getText()
                                .toString()
                                .trim()
                );

                data.put(
                        "home_team",
                        home.getText()
                                .toString()
                                .trim()
                );

                data.put(
                        "away_team",
                        away.getText()
                                .toString()
                                .trim()
                );

                data.put(
                        "home_score",
                        parseInt(
                                homeScore.getText()
                                        .toString(),
                                0
                        )
                );

                data.put(
                        "away_score",
                        parseInt(
                                awayScore.getText()
                                        .toString(),
                                0
                        )
                );

                data.put(
                        "status",
                        status.getSelectedItem()
                                .toString()
                );

            } catch (JSONException e) {
                showError(e.getMessage());
                return;
            }

            supabaseInsert(
                    "results",
                    data,
                    () -> {
                        showMessage(
                                "Matokeo yameongezwa."
                        );
                        loadResults();
                    }
            );
        });

        addHeading("MATOKEO YALIYOPO");

        if (resultItems.isEmpty()) {
            addEmpty(
                    "Hakuna matokeo yaliyopatikana."
            );
        } else {
            for (JSONObject item : resultItems) {
                if (item != null) {
                    addResultCard(item);
                }
            }
        }
    }

    private void addResultCard(JSONObject item) {

        LinearLayout card = card();

        TextView match = text(
                item.optString(
                        "home_team",
                        ""
                )
                        + "  "
                        + item.optInt(
                        "home_score",
                        0
                )
                        + " - "
                        + item.optInt(
                        "away_score",
                        0
                )
                        + "  "
                        + item.optString(
                        "away_team",
                        ""
                ),
                17,
                WHITE
        );

        match.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(match);

        card.addView(
                text(
                        item.optString(
                                "match_date",
                                ""
                        )
                                + " • "
                                + item.optString(
                                "competition",
                                ""
                        )
                                + " • "
                                + item.optString(
                                "status",
                                ""
                        ),
                        13,
                        GRAY
                )
        );

        Button delete =
                smallButton("FUTA");

        card.addView(delete);

        delete.setOnClickListener(
                v -> confirmDelete(
                        "results",
                        item.optString("id", ""),
                        "Matokeo haya?"
                )
        );

        content.addView(card);
    }

    private void buildStandingsSection() {

        addHeading("ONGEZA MSIMAMO");

        EditText team =
                input("Timu");

        EditText position =
                numberInput("Position");

        EditText played =
                numberInput("Played");

        EditText won =
                numberInput("Won");

        EditText drawn =
                numberInput("Drawn");

        EditText lost =
                numberInput("Lost");

        EditText gf =
                numberInput("Goals For");

        EditText ga =
                numberInput("Goals Against");

        EditText points =
                numberInput("Points");

        EditText competition =
                input("Mashindano");

        Button add =
                button("ONGEZA MSIMAMO");

        content.addView(team);
        content.addView(position);
        content.addView(played);
        content.addView(won);
        content.addView(drawn);
        content.addView(lost);
        content.addView(gf);
        content.addView(ga);
        content.addView(points);
        content.addView(competition);
        content.addView(add);

        add.setOnClickListener(v -> {

            if (team.getText().toString().trim().isEmpty()) {
                showError(
                        "Weka jina la timu."
                );
                return;
            }

            int goalsFor =
                    parseInt(
                            gf.getText().toString(),
                            0
                    );

            int goalsAgainst =
                    parseInt(
                            ga.getText().toString(),
                            0
                    );

            JSONObject data =
                    new JSONObject();

            try {
                data.put(
                        "team",
                        team.getText()
                                .toString()
                                .trim()
                );

                data.put(
                        "position",
                        parseNullableInt(
                                position.getText()
                                        .toString()
                        )
                );

                data.put(
                        "played",
                        parseInt(
                                played.getText()
                                        .toString(),
                                0
                        )
                );

                data.put(
                        "won",
                        parseInt(
                                won.getText()
                                        .toString(),
                                0
                        )
                );

                data.put(
                        "drawn",
                        parseInt(
                                drawn.getText()
                                        .toString(),
                                0
                        )
                );

                data.put(
                        "lost",
                        parseInt(
                                lost.getText()
                                        .toString(),
                                0
                        )
                );

                data.put(
                        "goals_for",
                        goalsFor
                );

                data.put(
                        "goals_against",
                        goalsAgainst
                );

                data.put(
                        "goal_difference",
                        goalsFor - goalsAgainst
                );

                data.put(
                        "points",
                        parseInt(
                                points.getText()
                                        .toString(),
                                0
                        )
                );

                data.put(
                        "competition",
                        competition.getText()
                                .toString()
                                .trim()
                );

            } catch (JSONException e) {
                showError(e.getMessage());
                return;
            }

            supabaseInsert(
                    "standings",
                    data,
                    () -> {
                        showMessage(
                                "Msimamo umeongezwa."
                        );
                        loadStandings();
                    }
            );
        });

        addHeading("MSIMAMO ULIOPO");

        if (standingItems.isEmpty()) {
            addEmpty(
                    "Hakuna msimamo uliopatikana."
            );
        } else {
            for (JSONObject item : standingItems) {
                if (item != null) {
                    addStandingCard(item);
                }
            }
        }
    }

    private void addStandingCard(JSONObject item) {

        LinearLayout card = card();

        TextView team = text(
                "#"
                        + item.optString(
                        "position",
                        "-"
                )
                        + "  "
                        + item.optString(
                        "team",
                        ""
                ),
                17,
                WHITE
        );

        team.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(team);

        card.addView(
                text(
                        "P " + item.optInt("played", 0)
                                + "   W "
                                + item.optInt("won", 0)
                                + "   D "
                                + item.optInt("drawn", 0)
                                + "   L "
                                + item.optInt("lost", 0),
                        13,
                        GRAY
                )
        );

        int gd =
                item.optInt("goals_for", 0)
                        - item.optInt(
                        "goals_against",
                        0
                );

        card.addView(
                text(
                        "GF "
                                + item.optInt(
                                "goals_for",
                                0
                        )
                                + "   GA "
                                + item.optInt(
                                "goals_against",
                                0
                        )
                                + "   GD "
                                + gd
                                + "   PTS "
                                + item.optInt(
                                "points",
                                0
                        ),
                        13,
                        WHITE
                )
        );

        Button delete =
                smallButton("FUTA");

        card.addView(delete);

        delete.setOnClickListener(
                v -> confirmDelete(
                        "standings",
                        item.optString("id", ""),
                        "Msimamo huu?"
                )
        );

        content.addView(card);
    }

    private void loadAll() {
        loadNews();
        loadFixtures();
        loadResults();
        loadStandings();
    }

    private void loadNews() {
        supabaseGet(
                "news",
                "select=*&order=created_at.desc",
                data -> {

                    newsItems.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item =
                                data.optJSONObject(i);

                        if (item != null) {
                            newsItems.add(item);
                        }
                    }

                    showSection(0);
                }
        );
    }

    private void loadFixtures() {
        supabaseGet(
                "fixtures",
                "select=*&order=created_at.desc",
                data -> {

                    fixtureItems.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item =
                                data.optJSONObject(i);

                        if (item != null) {
                            fixtureItems.add(item);
                        }
                    }

                    showSection(1);
                }
        );
    }

    private void loadResults() {
        supabaseGet(
                "results",
                "select=*&order=match_date.desc",
                data -> {

                    resultItems.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item =
                                data.optJSONObject(i);

                        if (item != null) {
                            resultItems.add(item);
                        }
                    }

                    showSection(2);
                }
        );
    }

    private void loadStandings() {
        supabaseGet(
                "standings",
                "select=*&order=position.asc",
                data -> {

                    standingItems.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item =
                                data.optJSONObject(i);

                        if (item != null) {
                            standingItems.add(item);
                        }
                    }

                    showSection(3);
                }
        );
    }

    private void updateNewsFlag(
            String id,
            String field,
            boolean value
    ) {
        JSONObject data =
                new JSONObject();

        try {
            data.put(field, value);
        } catch (JSONException e) {
            showError(e.getMessage());
            return;
        }

        supabaseUpdate(
                "news",
                id,
                data,
                () -> {
                    showMessage(
                            "Habari imesasishwa."
                    );
                    loadNews();
                }
        );
    }

    private void confirmDelete(
            String table,
            String id,
            String item
    ) {
        new AlertDialog.Builder(this)
                .setTitle("Futa")
                .setMessage(
                        "Una uhakika unataka kufuta "
                                + item
                )
                .setNegativeButton(
                        "GHAIRISHA",
                        null
                )
                .setPositiveButton(
                        "FUTA",
                        (dialog, which) ->
                                supabaseDelete(
                                        table,
                                        id,
                                        () -> {
                                            showMessage(
                                                    "Imefutwa."
                                            );
                                            loadAll();
                                        }
                                )
                )
                .show();
    }

    private void supabaseGet(
            String table,
            String query,
            JsonCallback callback
    ) {
        new Thread(() -> {

            try {
                Request request =
                        new Request.Builder()
                                .url(
                                        SupabaseConfig.URL
                                                + "/rest/v1/"
                                                + table
                                                + "?"
                                                + query
                                )
                                .addHeader(
                                        "apikey",
                                        SupabaseConfig.KEY
                                )
                                .addHeader(
                                        "Authorization",
                                        "Bearer "
                                                + accessToken
                                )
                                .get()
                                .build();

                try (
                        Response response =
                                client.newCall(request)
                                        .execute()
                ) {
                    String body =
                            response.body() == null
                                    ? ""
                                    : response.body()
                                    .string();

                    if (!response.isSuccessful()) {
                        showError(
                                "Supabase: " + body
                        );
                        return;
                    }

                    JSONArray array =
                            new JSONArray(body);

                    runOnUiThread(
                            () -> callback.onResult(array)
                    );
                }

            } catch (IOException |
                     JSONException e) {
                showError(e.getMessage());
            }
        }).start();
    }

    private void supabaseInsert(
            String table,
            JSONObject data,
            Runnable success
    ) {
        new Thread(() -> {

            try {
                RequestBody body =
                        RequestBody.create(
                                data.toString(),
                                MediaType.parse(
                                        "application/json"
                                )
                        );

                Request request =
                        new Request.Builder()
                                .url(
                                        SupabaseConfig.URL
                                                + "/rest/v1/"
                                                + table
                                )
                                .addHeader(
                                        "apikey",
                                        SupabaseConfig.KEY
                                )
                                .addHeader(
                                        "Authorization",
                                        "Bearer "
                                                + accessToken
                                )
                                .addHeader(
                                        "Content-Type",
                                        "application/json"
                                )
                                .addHeader(
                                        "Prefer",
                                        "return=minimal"
                                )
                                .post(body)
                                .build();

                try (
                        Response response =
                                client.newCall(request)
                                        .execute()
                ) {
                    String result =
                            response.body() == null
                                    ? ""
                                    : response.body()
                                    .string();

                    if (!response.isSuccessful()) {
                        showError(
                                "Supabase: " + result
                        );
                        return;
                    }

                    runOnUiThread(success);
                }

            } catch (IOException e) {
                showError(e.getMessage());
            }
        }).start();
    }

    private void supabaseUpdate(
            String table,
            String id,
            JSONObject data,
            Runnable success
    ) {
        new Thread(() -> {

            try {
                RequestBody body =
                        RequestBody.create(
                                data.toString(),
                                MediaType.parse(
                                        "application/json"
                                )
                        );

                Request request =
                        new Request.Builder()
                                .url(
                                        SupabaseConfig.URL
                                                + "/rest/v1/"
                                                + table
                                                + "?id=eq."
                                                + id
                                )
                                .addHeader(
                                        "apikey",
                                        SupabaseConfig.KEY
                                )
                                .addHeader(
                                        "Authorization",
                                        "Bearer "
                                                + accessToken
                                )
                                .addHeader(
                                        "Content-Type",
                                        "application/json"
                                )
                                .addHeader(
                                        "Prefer",
                                        "return=minimal"
                                )
                                .patch(body)
                                .build();

                try (
                        Response response =
                                client.newCall(request)
                                        .execute()
                ) {
                    String result =
                            response.body() == null
                                    ? ""
                                    : response.body()
                                    .string();

                    if (!response.isSuccessful()) {
                        showError(
                                "Supabase: " + result
                        );
                        return;
                    }

                    runOnUiThread(success);
                }

            } catch (IOException e) {
                showError(e.getMessage());
            }
        }).start();
    }

    private void supabaseDelete(
            String table,
            String id,
            Runnable success
    ) {
        new Thread(() -> {

            try {
                Request request =
                        new Request.Builder()
                                .url(
                                        SupabaseConfig.URL
                                                + "/rest/v1/"
                                                + table
                                                + "?id=eq."
                                                + id
                                )
                                .addHeader(
                                        "apikey",
                                        SupabaseConfig.KEY
                                )
                                .addHeader(
                                        "Authorization",
                                        "Bearer "
                                                + accessToken
                                )
                                .delete()
                                .build();

                try (
                        Response response =
                                client.newCall(request)
                                        .execute()
                ) {
                    if (!response.isSuccessful()) {
                        String result =
                                response.body() == null
                                        ? ""
                                        : response.body()
                                        .string();

                        showError(
                                "Supabase: " + result
                        );
                        return;
                    }

                    runOnUiThread(success);
                }

            } catch (IOException e) {
                showError(e.getMessage());
            }
        }).start();
    }

    private TextView text(
            String value,
            float size,
            int color
    ) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        v.setPadding(
                dp(2),
                dp(5),
                dp(2),
                dp(5)
        );
        return v;
    }

    private EditText input(String hint) {
        EditText v = new EditText(this);

        v.setHint(hint);
        v.setHintTextColor(
                Color.rgb(125, 125, 125)
        );
        v.setTextColor(WHITE);
        v.setTextSize(14);
        v.setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
        );
        v.setBackgroundColor(INPUT);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(52)
                );

        p.setMargins(
                0,
                0,
                0,
                dp(8)
        );

        v.setLayoutParams(p);

        return v;
    }

    private EditText numberInput(String hint) {
        EditText v = input(hint);

        v.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        return v;
    }

    private Spinner spinner(String[] values) {

        Spinner spinner =
                new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_dropdown_item,
                        values
                );

        spinner.setAdapter(adapter);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(52)
                );

        p.setMargins(
                0,
                0,
                0,
                dp(8)
        );

        spinner.setLayoutParams(p);

        return spinner;
    }

    private CheckBox check(
            String label,
            boolean checked
    ) {
        CheckBox box =
                new CheckBox(this);

        box.setText(label);
        box.setTextColor(WHITE);
        box.setTextSize(14);
        box.setChecked(checked);

        return box;
    }

    private Button button(String label) {

        Button b = new Button(this);

        b.setText(label);
        b.setTextColor(WHITE);
        b.setTextSize(13);
        b.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        b.setAllCaps(false);
        b.setBackgroundColor(RED);

        return b;
    }

    private Button smallButton(String label) {

        Button b = button(label);

        b.setTextSize(10);
        b.setPadding(
                dp(3),
                0,
                dp(3),
                0
        );

        return b;
    }

    private LinearLayout card() {

        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );

        box.setBackgroundColor(CARD);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        p.setMargins(
                0,
                0,
                0,
                dp(10)
        );

        box.setLayoutParams(p);

        return box;
    }

    private void addHeading(String title) {

        TextView h =
                text(title, 18, WHITE);

        h.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        h.setPadding(
                0,
                dp(12),
                0,
                dp(10)
        );

        content.addView(h);
    }

    private void addEmpty(String value) {

        TextView empty =
                text(value, 14, GRAY);

        empty.setPadding(
                dp(5),
                dp(10),
                dp(5),
                dp(15)
        );

        content.addView(empty);
    }

    private int parseInt(
            String value,
            int fallback
    ) {
        try {
            return Integer.parseInt(
                    value.trim()
            );
        } catch (Exception e) {
            return fallback;
        }
    }

    private Object parseNullableInt(
            String value
    ) {
        if (value == null
                || value.trim().isEmpty()) {
            return JSONObject.NULL;
        }

        return parseInt(value, 0);
    }

    private void showMessage(String value) {
        runOnUiThread(() -> {
            messageText.setText(value);
            messageText.setVisibility(
                    View.VISIBLE
            );
            errorText.setVisibility(
                    View.GONE
            );

            Toast.makeText(
                    this,
                    value,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void showError(String value) {
        runOnUiThread(() -> {
            errorText.setText(
                    value == null
                            ? "Hitilafu imetokea."
                            : value
            );

            errorText.setVisibility(
                    View.VISIBLE
            );

            messageText.setVisibility(
                    View.GONE
            );
        });
    }

    @Override
    protected void onDestroy() {
        if (liveAnalyticsRefresh != null) {
            liveAnalyticsHandler.removeCallbacks(
                    liveAnalyticsRefresh
            );
        }

        super.onDestroy();
    }

    private void logout() {
        getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        )
                .edit()
                .remove(TOKEN_KEY)
                .apply();

        openLogin();
    }

    private void openLogin() {
        startActivity(
                new Intent(
                        this,
                        AdminLoginActivity.class
                )
        );

        finish();
    }

    private int dp(float value) {
        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
                        + 0.5f
        );
    }


    private String uploadNewsImage(Uri uri) throws Exception {
        if (uri == null) return null;

        String extension = ".jpg";
        String mime = getContentResolver().getType(uri);

        if ("image/png".equals(mime)) {
            extension = ".png";
        } else if ("image/webp".equals(mime)) {
            extension = ".webp";
        }

        String fileName = System.currentTimeMillis()
                + "-" + java.util.UUID.randomUUID()
                + extension;

        String storagePath = "news/" + fileName;

        java.io.InputStream input =
                getContentResolver().openInputStream(uri);

        if (input == null) {
            throw new Exception("Imeshindikana kusoma picha.");
        }

        byte[] bytes;
        try {
            java.io.ByteArrayOutputStream output =
                    new java.io.ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int length;

            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }

            bytes = output.toByteArray();
        } finally {
            input.close();
        }

        okhttp3.RequestBody body =
                okhttp3.RequestBody.create(
                        bytes,
                        okhttp3.MediaType.parse(
                                mime != null ? mime : "image/jpeg"
                        )
                );

        okhttp3.Request request =
                new okhttp3.Request.Builder()
                        .url(
                                SupabaseConfig.URL
                                        + "/storage/v1/object/news-images/"
                                        + storagePath
                        )
                        .addHeader("apikey", SupabaseConfig.KEY)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .addHeader("Content-Type",
                                mime != null ? mime : "image/jpeg")
                        .addHeader("x-upsert", "false")
                        .post(body)
                        .build();

        okhttp3.Response response =
                client.newCall(request).execute();

        String responseBody =
                response.body() != null
                        ? response.body().string()
                        : "";

        if (!response.isSuccessful()) {
            throw new Exception(
                    "Upload ya picha imeshindikana: "
                            + responseBody
            );
        }

        return SupabaseConfig.URL
                + "/storage/v1/object/public/news-images/"
                + storagePath;
    }

    private interface JsonCallback {
        void onResult(JSONArray data);
    }
}
