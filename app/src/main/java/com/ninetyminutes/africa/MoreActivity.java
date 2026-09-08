package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        TextView moreNotifications = findViewById(R.id.moreNotifications);
        TextView moreSettings = findViewById(R.id.moreSettings);
        TextView moreAbout = findViewById(R.id.moreAbout);
        TextView morePrivacy = findViewById(R.id.morePrivacy);
        TextView moreTerms = findViewById(R.id.moreTerms);
        TextView moreContact = findViewById(R.id.moreContact);

        moreNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        moreSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        moreAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        morePrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        moreTerms.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
            startActivity(intent);
        });

        moreContact.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, ContactActivity.class);
            startActivity(intent);
        });
    }
}
