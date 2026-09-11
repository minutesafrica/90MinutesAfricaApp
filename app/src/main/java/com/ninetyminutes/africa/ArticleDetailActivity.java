package com.ninetyminutes.africa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class ArticleDetailActivity extends AppCompatActivity {

    private ImageView articleImage;
    private TextView titleView, metaView, contentView;
    private TextView viewsText, sharesText;
    private Button likeButton, commentButton, shareButton, whatsappButton;
    private EditText commentName, commentText;
    private Button sendCommentButton;
    private LinearLayout commentsContainer;

    private String newsId;
    private String sessionId;
    private int views = 0;
    private int shares = 0;
    private int likes = 0;
    private boolean liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        articleImage = findViewById(R.id.articleImage);
        titleView = findViewById(R.id.articleTitle);
        metaView = findViewById(R.id.articleMeta);
        contentView = findViewById(R.id.articleContent);

        viewsText = findViewById(R.id.viewsText);
        sharesText = findViewById(R.id.sharesText);

        likeButton = findViewById(R.id.likeButton);
        commentButton = findViewById(R.id.commentButton);
        shareButton = findViewById(R.id.shareButton);
        whatsappButton = findViewById(R.id.whatsappButton);

        commentName = findViewById(R.id.commentName);
        commentText = findViewById(R.id.commentText);
        sendCommentButton = findViewById(R.id.sendCommentButton);
        commentsContainer = findViewById(R.id.commentsContainer);

        newsId = getIntent().getStringExtra("news_id");

        if (newsId == null || newsId.trim().isEmpty()) {
            titleView.setText("Habari haipatikani.");
            return;
        }

        sessionId = getSharedPreferences(
                "news_session",
                MODE_PRIVATE
        ).getString("session_id", null);

        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();

            getSharedPreferences(
                    "news_session",
                    MODE_PRIVATE
            ).edit()
                    .putString("session_id", sessionId)
                    .apply();
        }

        likeButton.setOnClickListener(v -> handleLike());
        commentButton.setOnClickListener(v -> scrollToComments());
        shareButton.setOnClickListener(v -> handleShare());
        whatsappButton.setOnClickListener(v -> shareWhatsApp());
        sendCommentButton.setOnClickListener(v -> handleComment());

        loadArticle();
        loadStats();
        incrementView();
        loadLikes();
        loadComments();
    }

    private void loadArticle() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                String encodedId = URLEncoder.encode(newsId, "UTF-8");

                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news?select=*&id=eq." +
                        encodedId +
                        "&published=eq.true"
                );

                connection = openConnection(url);

                int code = connection.getResponseCode();

                String response = readResponse(connection, code);

                if (code >= 200 && code < 300) {
                    JSONArray array = new JSONArray(response);

                    runOnUiThread(() -> displayArticle(array));
                } else {
                    runOnUiThread(() ->
                            titleView.setText(
                                    "Imeshindwa kupakia habari."
                            )
                    );
                }

            } catch (Exception e) {
                runOnUiThread(() ->
                        titleView.setText(
                                "Imeshindwa kupakia habari."
                        )
                );
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void displayArticle(JSONArray array) {
        try {
            if (array.length() == 0) {
                titleView.setText("Habari haipatikani.");
                return;
            }

            JSONObject article = array.getJSONObject(0);

            titleView.setText(
                    article.optString("title", "Habari")
            );

            metaView.setText(
                    article.optString("category", "Tanzania")
            );

            contentView.setText(
                    article.optString(
                            "content",
                            article.optString("excerpt", "")
                    )
            );

            String imageUrl =
                    article.optString("image_url", "");

            if (!imageUrl.isEmpty()) {
                loadImage(imageUrl);
            }

        } catch (Exception e) {
            titleView.setText(
                    "Imeshindwa kuonyesha habari."
            );
        }
    }

    private void loadImage(String imageUrl) {
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

                if (bitmap != null) {
                    runOnUiThread(() -> {
                        articleImage.setImageBitmap(bitmap);
                        articleImage.setVisibility(View.VISIBLE);
                    });
                }

            } catch (Exception ignored) {
            }
        }).start();
    }

    private void loadStats() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news?select=views,shares&id=eq." +
                        URLEncoder.encode(newsId, "UTF-8")
                );

                connection = openConnection(url);

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    JSONArray array =
                            new JSONArray(
                                    readResponse(connection, code)
                            );

                    if (array.length() > 0) {
                        JSONObject data =
                                array.getJSONObject(0);

                        views = data.optInt("views", 0);
                        shares = data.optInt("shares", 0);

                        runOnUiThread(this::updateStats);
                    }
                }

            } catch (Exception ignored) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void incrementView() {
        new Thread(() -> {
            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/rpc/increment_news_views"
                );

                HttpURLConnection connection =
                        openConnection(url);

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                String body =
                        "{\"news_id_input\":\"" +
                        newsId +
                        "\"}";

                connection.getOutputStream()
                        .write(body.getBytes("UTF-8"));

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    runOnUiThread(() -> {
                        views++;
                        updateStats();
                    });
                }

                connection.disconnect();

            } catch (Exception ignored) {
            }
        }).start();
    }

    private void loadLikes() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news_likes?select=id,session_id&news_id=eq." +
                        URLEncoder.encode(newsId, "UTF-8")
                );

                connection = openConnection(url);

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    JSONArray array =
                            new JSONArray(
                                    readResponse(connection, code)
                            );

                    likes = array.length();
                    liked = false;

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item =
                                array.getJSONObject(i);

                        if (sessionId.equals(
                                item.optString("session_id", "")
                        )) {
                            liked = true;
                            break;
                        }
                    }

                    runOnUiThread(this::updateLikeButton);
                }

            } catch (Exception ignored) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void handleLike() {
        if (liked) {
            deleteLike();
        } else {
            insertLike();
        }
    }

    private void insertLike() {
        new Thread(() -> {
            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news_likes"
                );

                HttpURLConnection connection =
                        openConnection(url);

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );
                connection.setRequestProperty(
                        "Prefer",
                        "return=minimal"
                );

                String body =
                        "{\"news_id\":\"" +
                        newsId +
                        "\",\"session_id\":\"" +
                        sessionId +
                        "\"}";

                connection.getOutputStream()
                        .write(body.getBytes("UTF-8"));

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    liked = true;
                    likes++;

                    runOnUiThread(this::updateLikeButton);
                }

                connection.disconnect();

            } catch (Exception ignored) {
            }
        }).start();
    }

    private void deleteLike() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news_likes?news_id=eq." +
                        URLEncoder.encode(newsId, "UTF-8") +
                        "&session_id=eq." +
                        URLEncoder.encode(sessionId, "UTF-8")
                );

                connection = openConnection(url);
                connection.setRequestMethod("DELETE");

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    liked = false;
                    likes = Math.max(0, likes - 1);

                    runOnUiThread(this::updateLikeButton);
                }

            } catch (Exception ignored) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void loadComments() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news_comments?select=*&news_id=eq." +
                        URLEncoder.encode(newsId, "UTF-8") +
                        "&order=created_at.desc"
                );

                connection = openConnection(url);

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    JSONArray array =
                            new JSONArray(
                                    readResponse(connection, code)
                            );

                    runOnUiThread(() ->
                            displayComments(array)
                    );
                }

            } catch (Exception ignored) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void displayComments(JSONArray comments) {
        commentsContainer.removeAllViews();

        commentButton.setText(
                "💬 Comment " + comments.length()
        );

        if (comments.length() == 0) {
            TextView empty = new TextView(this);

            empty.setText(
                    "Hakuna comment bado. Kuwa wa kwanza kutoa maoni!"
            );

            empty.setTextColor(
                    android.graphics.Color.LTGRAY
            );

            empty.setPadding(0, 16, 0, 16);

            commentsContainer.addView(empty);
            return;
        }

        try {
            for (int i = 0; i < comments.length(); i++) {
                JSONObject item =
                        comments.getJSONObject(i);

                TextView comment = new TextView(this);

                String name =
                        item.optString(
                                "name",
                                "Msomaji"
                        );

                String text =
                        item.optString(
                                "comment",
                                ""
                        );

                comment.setText(
                        name + "\n" + text
                );

                comment.setTextColor(
                        android.graphics.Color.WHITE
                );

                comment.setTextSize(15);
                comment.setPadding(14, 14, 14, 14);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                params.setMargins(0, 0, 0, 10);
                comment.setLayoutParams(params);

                commentsContainer.addView(comment);
            }

        } catch (Exception ignored) {
        }
    }

    private void handleComment() {
        String name =
                commentName.getText()
                        .toString()
                        .trim();

        String text =
                commentText.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {
            name = "Msomaji";
        }

        if (text.isEmpty()) {
            return;
        }

        final String finalName = name;

        sendCommentButton.setEnabled(false);

        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/news_comments"
                );

                connection = openConnection(url);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );
                connection.setRequestProperty(
                        "Prefer",
                        "return=representation"
                );

                JSONObject body = new JSONObject();

                body.put("news_id", newsId);
                body.put("name", finalName);
                body.put("comment", text);
                body.put("session_id", sessionId);

                connection.getOutputStream()
                        .write(body.toString()
                                .getBytes("UTF-8"));

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    runOnUiThread(() -> {
                        commentText.setText("");
                        sendCommentButton.setEnabled(true);
                        loadComments();
                    });
                } else {
                    runOnUiThread(() -> {
                        sendCommentButton.setEnabled(true);
                        Toast.makeText(
                                this,
                                "Imeshindikana kutuma comment.",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    sendCommentButton.setEnabled(true);
                    Toast.makeText(
                            this,
                            "Imeshindikana kutuma comment.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void handleShare() {
        incrementShare();

        String url =
                "https://90minutesafrica.online/news/" +
                newsId;

        Intent intent = new Intent(
                Intent.ACTION_SEND
        );

        intent.setType("text/plain");

        intent.putExtra(
                Intent.EXTRA_TEXT,
                "Angalia habari hii kutoka 90' Minutes Africa\n" +
                url
        );

        startActivity(
                Intent.createChooser(
                        intent,
                        "Shiriki habari"
                )
        );
    }

    private void shareWhatsApp() {
        incrementShare();

        String url =
                "https://90minutesafrica.online/news/" +
                newsId;

        String text =
                "Angalia habari hii kutoka 90' Minutes Africa\n" +
                url;

        try {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW
            );

            intent.setData(
                    Uri.parse(
                            "https://wa.me/?text=" +
                            Uri.encode(text)
                    )
            );

            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "WhatsApp haipatikani.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void incrementShare() {
        new Thread(() -> {
            try {
                URL url = new URL(
                        SupabaseConfig.URL +
                        "/rest/v1/rpc/increment_news_shares"
                );

                HttpURLConnection connection =
                        openConnection(url);

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                String body =
                        "{\"news_id_input\":\"" +
                        newsId +
                        "\"}";

                connection.getOutputStream()
                        .write(body.getBytes("UTF-8"));

                int code = connection.getResponseCode();

                if (code >= 200 && code < 300) {
                    runOnUiThread(() -> {
                        shares++;
                        updateStats();
                    });
                }

                connection.disconnect();

            } catch (Exception ignored) {
            }
        }).start();
    }

    private void scrollToComments() {
        commentsContainer.requestFocus();

        findViewById(R.id.commentsTitle)
                .getParent()
                .requestFocus();
    }

    private void updateStats() {
        viewsText.setText(
                "👁️ " + views + " Views"
        );

        sharesText.setText(
                "🔗 " + shares + " Shares"
        );
    }

    private void updateLikeButton() {
        likeButton.setText(
                (liked ? "❤️" : "🤍") +
                " Like " +
                likes
        );

        commentButton.setText(
                "💬 Comment"
        );
    }

    private HttpURLConnection openConnection(URL url)
            throws Exception {

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestProperty(
                "apikey",
                SupabaseConfig.KEY
        );

        connection.setRequestProperty(
                "Authorization",
                "Bearer " + SupabaseConfig.KEY
        );

        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        return connection;
    }

    private String readResponse(
            HttpURLConnection connection,
            int code
    ) throws Exception {

        InputStream stream =
                code >= 200 && code < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

        if (stream == null) {
            return "";
        }

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(stream)
                );

        StringBuilder result =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();

        return result.toString();
    }
}
