package com.ninetyminutes.africa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArticleDetailActivity extends AppCompatActivity {

    private ImageView articleImage;
    private TextView articleCategory;
    private TextView articleTitle;
    private TextView articleMeta;
    private TextView articleExcerpt;
    private TextView articleContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_article_detail
        );

        articleImage =
                findViewById(R.id.articleImage);

        articleCategory =
                findViewById(R.id.articleCategory);

        articleTitle =
                findViewById(R.id.articleTitle);

        articleMeta =
                findViewById(R.id.articleMeta);

        articleExcerpt =
                findViewById(R.id.articleExcerpt);

        articleContent =
                findViewById(R.id.articleContent);

        ImageButton back =
                findViewById(R.id.articleBack);

        back.setOnClickListener(
                v -> finish()
        );

        loadArticle();
    }

    private void loadArticle() {

        String title =
                getIntent().getStringExtra(
                        "article_title"
                );

        String category =
                getIntent().getStringExtra(
                        "article_category"
                );

        String excerpt =
                getIntent().getStringExtra(
                        "article_excerpt"
                );

        String content =
                getIntent().getStringExtra(
                        "article_content"
                );

        String author =
                getIntent().getStringExtra(
                        "article_author"
                );

        String imageUrl =
                getIntent().getStringExtra(
                        "article_image"
                );

        String createdAt =
                getIntent().getStringExtra(
                        "article_created_at"
                );

        int views =
                getIntent().getIntExtra(
                        "article_views",
                        0
                );

        articleCategory.setText(
                safe(category)
        );

        articleTitle.setText(
                safe(title)
        );

        StringBuilder meta =
                new StringBuilder();

        if (!safe(author).isEmpty()) {
            meta.append(author);
        }

        if (!safe(createdAt).isEmpty()) {

            if (meta.length() > 0) {
                meta.append(" • ");
            }

            meta.append(createdAt);
        }

        if (views > 0) {

            if (meta.length() > 0) {
                meta.append(" • ");
            }

            meta.append(views)
                    .append(" views");
        }

        articleMeta.setText(
                meta.toString()
        );

        articleExcerpt.setText(
                safe(excerpt)
        );

        articleContent.setText(
                htmlToText(content)
        );

        if (imageUrl == null ||
                imageUrl.trim().isEmpty()) {

            articleImage.setVisibility(
                    View.GONE
            );

        } else {

            loadImage(
                    articleImage,
                    imageUrl
            );
        }
    }

    private Spanned htmlToText(String value) {

        String text = safe(value);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.N) {

            return Html.fromHtml(
                    text,
                    Html.FROM_HTML_MODE_LEGACY
            );

        } else {

            return Html.fromHtml(text);
        }
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private void loadImage(
            ImageView imageView,
            String imageUrl
    ) {

        new Thread(() -> {

            HttpURLConnection connection = null;
            InputStream input = null;

            try {

                URL url =
                        new URL(imageUrl);

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setConnectTimeout(
                        10000
                );

                connection.setReadTimeout(
                        10000
                );

                connection.setInstanceFollowRedirects(
                        true
                );

                input =
                        connection.getInputStream();

                Bitmap bitmap =
                        BitmapFactory.decodeStream(
                                input
                        );

                if (bitmap != null) {

                    runOnUiThread(() ->
                            imageView.setImageBitmap(
                                    bitmap
                            )
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
}
