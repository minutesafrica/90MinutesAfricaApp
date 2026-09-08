package com.ninetyminutes.africa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticleDetailActivity extends AppCompatActivity {

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private ImageView articleImage;

    private TextView articleViews;
    private TextView articleShares;
    private Button articleLike;
    private Button articleComment;
    private Button articleShareButton;

    private EditText commentName;
    private EditText commentText;
    private Button sendComment;

    private TextView commentsEmpty;
    private LinearLayout commentsContainer;

    private String newsId;
    private String sessionId;

    private boolean liked = false;
    private int likesCount = 0;
    private int commentsCount = 0;
    private int viewsCount = 0;
    private int sharesCount = 0;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        preferences = getSharedPreferences(
                "90minutes_preferences",
                Context.MODE_PRIVATE
        );

        initializeViews();
        loadArticleData();
        initializeSession();

        loadStats();
        loadLikes();
        loadComments();

        incrementView();

        setupActions();
    }

    private void initializeViews() {

        articleImage = findViewById(R.id.articleImage);

        articleViews = findViewById(R.id.articleViews);
        articleShares = findViewById(R.id.articleShares);

        articleLike = findViewById(R.id.articleLike);
        articleComment = findViewById(R.id.articleComment);
        articleShareButton =
                findViewById(R.id.articleShareButton);

        commentName = findViewById(R.id.commentName);
        commentText = findViewById(R.id.commentText);
        sendComment = findViewById(R.id.sendComment);

        commentsEmpty =
                findViewById(R.id.commentsEmpty);

        commentsContainer =
                findViewById(R.id.commentsContainer);
    }

    private void loadArticleData() {

        TextView articleBack =
                findViewById(R.id.articleBack);

        TextView articleCategory =
                findViewById(R.id.articleCategory);

        TextView articleTitle =
                findViewById(R.id.articleTitle);

        TextView articleMeta =
                findViewById(R.id.articleMeta);

        TextView articleExcerpt =
                findViewById(R.id.articleExcerpt);

        TextView articleContent =
                findViewById(R.id.articleContent);

        newsId =
                getIntent().getStringExtra("article_id");

        String title =
                getIntent().getStringExtra("article_title");

        String category =
                getIntent().getStringExtra("article_category");

        String excerpt =
                getIntent().getStringExtra("article_excerpt");

        String content =
                getIntent().getStringExtra("article_content");

        String author =
                getIntent().getStringExtra("article_author");

        String imageUrl =
                getIntent().getStringExtra("article_image");

        String createdAt =
                getIntent().getStringExtra(
                        "article_created_at"
                );

        viewsCount =
                getIntent().getIntExtra(
                        "article_views",
                        0
                );

        if (newsId == null) {
            newsId = "";
        }

        if (title == null || title.trim().isEmpty()) {
            title = "Habari kuu ya soka";
        }

        if (category == null ||
                category.trim().isEmpty()) {
            category = "FOOTBALL";
        }

        if (excerpt == null) {
            excerpt = "";
        }

        if (content == null ||
                content.trim().isEmpty()) {
            content =
                    "Hakuna maudhui ya habari yaliyopatikana.";
        }

        if (author == null ||
                author.trim().isEmpty()) {
            author = "90' MINUTES AFRICA";
        }

        if (createdAt == null ||
                createdAt.trim().isEmpty()) {
            createdAt = "Leo";
        }

        articleCategory.setText(category);
        articleTitle.setText(title);

        if (excerpt.trim().isEmpty()) {
            articleExcerpt.setVisibility(View.GONE);
        } else {
            articleExcerpt.setVisibility(View.VISIBLE);
            articleExcerpt.setText(excerpt);
        }

        articleContent.setText(content);

        articleMeta.setText(
                "Na " + author +
                " • " + formatDate(createdAt)
        );

        articleViews.setText(
                "Views " + viewsCount
        );

        articleShares.setText(
                "Shares " + sharesCount
        );

        loadArticleImage(imageUrl);

        articleBack.setOnClickListener(
                v -> finish()
        );
    }

    private void initializeSession() {

        sessionId =
                preferences.getString(
                        "news_session_id",
                        null
                );

        if (sessionId == null ||
                sessionId.trim().isEmpty()) {

            sessionId = UUID.randomUUID().toString();

            preferences.edit()
                    .putString(
                            "news_session_id",
                            sessionId
                    )
                    .apply();
        }
    }

    private void loadStats() {

        if (newsId.isEmpty()) {
            return;
        }

        executor.execute(() -> {

            try {

                String endpoint =
                        "/rest/v1/news" +
                        "?select=views,shares" +
                        "&id=eq." +
                        URLEncoder.encode(
                                newsId,
                                "UTF-8"
                        );

                String response =
                        SupabaseClient.get(endpoint);

                JSONArray array =
                        new JSONArray(response);

                if (array.length() > 0) {

                    JSONObject object =
                            array.getJSONObject(0);

                    viewsCount =
                            object.optInt(
                                    "views",
                                    viewsCount
                            );

                    sharesCount =
                            object.optInt(
                                    "shares",
                                    0
                            );
                }

                runOnUiThread(() -> {

                    articleViews.setText(
                            "Views " + viewsCount
                    );

                    articleShares.setText(
                            "Shares " + sharesCount
                    );
                });

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }

    private void incrementView() {

        if (newsId.isEmpty()) {
            return;
        }

        executor.execute(() -> {

            try {

                JSONObject body =
                        new JSONObject();

                body.put(
                        "news_id_input",
                        newsId
                );

                String response =
                        postRpc(
                                "increment_news_views",
                                body
                        );

                viewsCount++;

                runOnUiThread(() ->
                        articleViews.setText(
                                "Views " + viewsCount
                        )
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }

    private void loadLikes() {

        if (newsId.isEmpty()) {
            return;
        }

        executor.execute(() -> {

            try {

                String endpoint =
                        "/rest/v1/news_likes" +
                        "?select=id,session_id" +
                        "&news_id=eq." +
                        URLEncoder.encode(
                                newsId,
                                "UTF-8"
                        );

                String response =
                        SupabaseClient.get(endpoint);

                JSONArray array =
                        new JSONArray(response);

                likesCount =
                        array.length();

                liked = false;

                for (int i = 0;
                     i < array.length();
                     i++) {

                    JSONObject item =
                            array.getJSONObject(i);

                    if (sessionId.equals(
                            item.optString(
                                    "session_id"
                            )
                    )) {

                        liked = true;
                        break;
                    }
                }

                runOnUiThread(
                        this::updateLikeButton
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }

    private void handleLike() {

        if (newsId.isEmpty() ||
                sessionId.isEmpty()) {
            return;
        }

        articleLike.setEnabled(false);

        executor.execute(() -> {

            try {

                if (liked) {

                    String endpoint =
                            "/rest/v1/news_likes" +
                            "?news_id=eq." +
                            URLEncoder.encode(
                                    newsId,
                                    "UTF-8"
                            ) +
                            "&session_id=eq." +
                            URLEncoder.encode(
                                    sessionId,
                                    "UTF-8"
                            );

                    delete(endpoint);

                    liked = false;
                    likesCount =
                            Math.max(
                                    0,
                                    likesCount - 1
                            );

                } else {

                    JSONObject body =
                            new JSONObject();

                    body.put(
                            "news_id",
                            newsId
                    );

                    body.put(
                            "session_id",
                            sessionId
                    );

                    post(
                            "/rest/v1/news_likes",
                            body
                    );

                    liked = true;
                    likesCount++;
                }

                runOnUiThread(() -> {

                    updateLikeButton();

                    articleLike.setEnabled(true);
                });

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(() ->
                        articleLike.setEnabled(true)
                );
            }
        });
    }

    private void updateLikeButton() {

        if (liked) {

            articleLike.setText(
                    "Liked " + likesCount
            );

        } else {

            articleLike.setText(
                    "Like " + likesCount
            );
        }

        articleComment.setText(
                "Comment " + commentsCount
        );
    }

    private void loadComments() {

        if (newsId.isEmpty()) {
            return;
        }

        executor.execute(() -> {

            try {

                String endpoint =
                        "/rest/v1/news_comments" +
                        "?select=*" +
                        "&news_id=eq." +
                        URLEncoder.encode(
                                newsId,
                                "UTF-8"
                        ) +
                        "&order=created_at.desc";

                String response =
                        SupabaseClient.get(endpoint);

                JSONArray array =
                        new JSONArray(response);

                commentsCount =
                        array.length();

                runOnUiThread(() ->
                        displayComments(array)
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }

    private void displayComments(
            JSONArray comments
    ) {

        commentsContainer.removeAllViews();

        if (comments.length() == 0) {

            commentsEmpty.setVisibility(
                    View.VISIBLE
            );

        } else {

            commentsEmpty.setVisibility(
                    View.GONE
            );

            for (int i = 0;
                 i < comments.length();
                 i++) {

                try {

                    JSONObject item =
                            comments.getJSONObject(i);

                    String name =
                            item.optString(
                                    "name",
                                    "Msomaji"
                            );

                    String comment =
                            item.optString(
                                    "comment",
                                    ""
                            );

                    String createdAt =
                            item.optString(
                                    "created_at",
                                    ""
                            );

                    LinearLayout card =
                            new LinearLayout(this);

                    card.setOrientation(
                            LinearLayout.VERTICAL
                    );

                    card.setPadding(
                            14,
                            14,
                            14,
                            14
                    );

                    card.setBackgroundColor(
                            android.graphics.Color
                                    .parseColor(
                                            "#151515"
                                    )
                    );

                    LinearLayout.LayoutParams
                            cardParams =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                    cardParams.setMargins(
                            0,
                            0,
                            0,
                            12
                    );

                    card.setLayoutParams(
                            cardParams
                    );

                    TextView nameView =
                            new TextView(this);

                    nameView.setText(name);
                    nameView.setTextColor(
                            android.graphics.Color
                                    .parseColor(
                                            "#E30613"
                                    )
                    );
                    nameView.setTextSize(15);

                    TextView commentView =
                            new TextView(this);

                    commentView.setText(comment);
                    commentView.setTextColor(
                            android.graphics.Color
                                    .parseColor(
                                            "#DDDDDD"
                                    )
                    );
                    commentView.setTextSize(15);
                    commentView.setPadding(
                            0,
                            6,
                            0,
                            6
                    );

                    TextView dateView =
                            new TextView(this);

                    dateView.setText(
                            formatCommentDate(
                                    createdAt
                            )
                    );

                    dateView.setTextColor(
                            android.graphics.Color
                                    .parseColor(
                                            "#777777"
                                    )
                    );

                    dateView.setTextSize(11);

                    card.addView(nameView);
                    card.addView(commentView);
                    card.addView(dateView);

                    commentsContainer.addView(
                            card
                    );

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }

        articleComment.setText(
                "Comment " + commentsCount
        );
    }

    private void sendComment() {

        String text =
                commentText.getText()
                        .toString()
                        .trim();

        String name =
                commentName.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {
            name = "Msomaji";
        }

        if (text.isEmpty()) {

            Toast.makeText(
                    this,
                    "Andika comment kwanza.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (newsId.isEmpty()) {
            return;
        }

        sendComment.setEnabled(false);
        sendComment.setText("Inatuma...");

        final String finalName = name;

        executor.execute(() -> {

            try {

                JSONObject body =
                        new JSONObject();

                body.put(
                        "news_id",
                        newsId
                );

                body.put(
                        "name",
                        finalName
                );

                body.put(
                        "comment",
                        text
                );

                body.put(
                        "session_id",
                        sessionId
                );

                post(
                        "/rest/v1/news_comments",
                        body
                );

                runOnUiThread(() -> {

                    commentText.setText("");

                    sendComment.setEnabled(
                            true
                    );

                    sendComment.setText(
                            "Tuma Comment"
                    );

                    Toast.makeText(
                            this,
                            "Comment imetumwa.",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadComments();
                });

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(() -> {

                    sendComment.setEnabled(
                            true
                    );

                    sendComment.setText(
                            "Tuma Comment"
                    );

                    Toast.makeText(
                            this,
                            "Imeshindikana kutuma comment.",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    private void incrementShare() {

        if (newsId.isEmpty()) {
            return;
        }

        executor.execute(() -> {

            try {

                JSONObject body =
                        new JSONObject();

                body.put(
                        "news_id_input",
                        newsId
                );

                postRpc(
                        "increment_news_shares",
                        body
                );

                sharesCount++;

                runOnUiThread(() ->
                        articleShares.setText(
                                "Shares " + sharesCount
                        )
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }

    private void shareArticle() {

        String title =
                getIntent().getStringExtra(
                        "article_title"
                );

        if (title == null) {
            title = "Habari kutoka 90' MINUTES AFRICA";
        }

        Intent shareIntent =
                new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                title
        );

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                title +
                "\n\n" +
                "Angalia habari hii kutoka " +
                "90' MINUTES AFRICA."
        );

        try {

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Shiriki habari"
                    )
            );

            incrementShare();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Hakuna app ya kushirikisha habari.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void setupActions() {

        TextView articleBack =
                findViewById(R.id.articleBack);

        TextView articleShare =
                findViewById(R.id.articleShare);

        articleBack.setOnClickListener(
                v -> finish()
        );

        articleShare.setOnClickListener(
                v -> shareArticle()
        );

        articleLike.setOnClickListener(
                v -> handleLike()
        );

        articleComment.setOnClickListener(
                v -> {

                    commentsContainer.requestFocus();

                    ((android.view.ViewGroup)
                            commentsContainer.getParent())
                            .requestFocus();
                }
        );

        articleShareButton.setOnClickListener(
                v -> shareArticle()
        );

        sendComment.setOnClickListener(
                v -> sendComment()
        );
    }

    private String postRpc(
            String function,
            JSONObject body
    ) throws Exception {

        return post(
                "/rest/v1/rpc/" + function,
                body
        );
    }

    private String post(
            String endpoint,
            JSONObject body
    ) throws Exception {

        URL url =
                new URL(
                        SupabaseClient.SUPABASE_URL +
                        endpoint
                );

        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty(
                "apikey",
                SupabaseClient.SUPABASE_KEY
        );
        connection.setRequestProperty(
                "Authorization",
                "Bearer " +
                        SupabaseClient.SUPABASE_KEY
        );
        connection.setRequestProperty(
                "Content-Type",
                "application/json"
        );
        connection.setDoOutput(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        byte[] data =
                body.toString()
                        .getBytes("UTF-8");

        OutputStream output =
                connection.getOutputStream();

        output.write(data);
        output.flush();
        output.close();

        return readResponse(connection);
    }

    private void delete(
            String endpoint
    ) throws Exception {

        URL url =
                new URL(
                        SupabaseClient.SUPABASE_URL +
                        endpoint
                );

        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();

        connection.setRequestMethod(
                "DELETE"
        );

        connection.setRequestProperty(
                "apikey",
                SupabaseClient.SUPABASE_KEY
        );

        connection.setRequestProperty(
                "Authorization",
                "Bearer " +
                        SupabaseClient.SUPABASE_KEY
        );

        connection.setRequestProperty(
                "Content-Type",
                "application/json"
        );

        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        readResponse(connection);
    }

    private String readResponse(
            HttpURLConnection connection
    ) throws Exception {

        int responseCode =
                connection.getResponseCode();

        InputStream stream;

        if (responseCode >= 200 &&
                responseCode < 300) {

            stream =
                    connection.getInputStream();

        } else {

            stream =
                    connection.getErrorStream();
        }

        StringBuilder result =
                new StringBuilder();

        if (stream != null) {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    stream
                            )
                    );

            String line;

            while ((line =
                    reader.readLine()) != null) {

                result.append(line);
            }

            reader.close();
        }

        connection.disconnect();

        if (responseCode < 200 ||
                responseCode >= 300) {

            throw new Exception(
                    "Supabase error " +
                    responseCode +
                    ": " +
                    result
            );
        }

        return result.toString();
    }

    private void loadArticleImage(
            String imageUrl
    ) {

        if (imageUrl == null ||
                imageUrl.trim().isEmpty()) {

            articleImage.setVisibility(
                    View.GONE
            );

            return;
        }

        executor.execute(() -> {

            Bitmap bitmap = null;

            try {

                URL url =
                        new URL(imageUrl);

                HttpURLConnection connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod(
                        "GET"
                );

                connection.setConnectTimeout(
                        15000
                );

                connection.setReadTimeout(
                        15000
                );

                connection.connect();

                if (connection.getResponseCode()
                        >= 200 &&
                        connection.getResponseCode()
                        < 300) {

                    InputStream input =
                            connection.getInputStream();

                    bitmap =
                            BitmapFactory
                                    .decodeStream(
                                            input
                                    );

                    input.close();
                }

                connection.disconnect();

            } catch (Exception e) {

                e.printStackTrace();
            }

            final Bitmap finalBitmap =
                    bitmap;

            runOnUiThread(() -> {

                if (finalBitmap != null) {

                    articleImage.setImageBitmap(
                            finalBitmap
                    );

                } else {

                    articleImage.setVisibility(
                            View.GONE
                    );
                }
            });
        });
    }

    private String formatDate(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "Leo";
        }

        try {

            if (value.length() >= 10) {

                String date =
                        value.substring(0, 10);

                String[] parts =
                        date.split("-");

                if (parts.length == 3) {

                    return parts[2] +
                            "/" +
                            parts[1] +
                            "/" +
                            parts[0];
                }
            }

        } catch (Exception ignored) {
        }

        return value;
    }

    private String formatCommentDate(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "";
        }

        if (value.length() >= 16) {

            return value.substring(
                    0,
                    16
            ).replace(
                    "T",
                    " "
            );
        }

        return value;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        executor.shutdownNow();
    }
}
