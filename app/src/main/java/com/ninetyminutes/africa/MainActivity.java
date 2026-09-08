package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView navNews = findViewById(R.id.navNews);
        TextView navMatches = findViewById(R.id.navMatches);
        TextView navTable = findViewById(R.id.navTable);
        TextView navMore = findViewById(R.id.navMore);

        navNews.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(intent);
        });

        navMatches.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
            startActivity(intent);
        });

        navTable.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TableActivity.class);
            startActivity(intent);
        });

        navMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoreActivity.class);
            startActivity(intent);
        });
    }
}
