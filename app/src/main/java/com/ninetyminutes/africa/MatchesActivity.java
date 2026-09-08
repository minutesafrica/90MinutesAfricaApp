package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MatchesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        TextView navResults = findViewById(R.id.navResults);

        navResults.setOnClickListener(v -> {
            Intent intent = new Intent(MatchesActivity.this, ResultsActivity.class);
            startActivity(intent);
        });
    }
}
