package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        TextView articleBack = findViewById(R.id.articleBack);
        TextView articleCategory = findViewById(R.id.articleCategory);
        TextView articleTitle = findViewById(R.id.articleTitle);
        TextView articleMeta = findViewById(R.id.articleMeta);
        TextView articleContent = findViewById(R.id.articleContent);
        TextView articleShare = findViewById(R.id.articleShare);

        String title = getIntent().getStringExtra("article_title");
        String category = getIntent().getStringExtra("article_category");
        String content = getIntent().getStringExtra("article_content");
        int views = getIntent().getIntExtra("article_views", 0);

        if (title == null || title.isEmpty()) {
            title = "Habari kuu ya soka";
        }

        if (category == null || category.isEmpty()) {
            category = "FOOTBALL";
        }

        if (content == null || content.isEmpty()) {
            content = "Hakuna maudhui ya habari yaliyopatikana.";
        }

        articleCategory.setText(category);
        articleTitle.setText(title);

        articleMeta.setText(
                "90' MINUTES AFRICA • Leo • 👁 " + views
        );

        articleContent.setText(content);

        articleBack.setOnClickListener(v -> finish());

        final String shareTitle = title;

        articleShare.setOnClickListener(v -> {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    shareTitle + "\n\n90' MINUTES AFRICA"
            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Shiriki habari"
                    )
            );
        });
    }
}
