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
        
        findViewById(R.id.moreCookie).setOnClickListener(v -> {
            startActivity(new Intent(
                    MoreActivity.this,
                    CookiePolicyActivity.class
            ));
        });

        findViewById(R.id.moreDisclaimer).setOnClickListener(v -> {
            startActivity(new Intent(
                    MoreActivity.this,
                    DisclaimerActivity.class
            ));
        });
        
        findViewById(R.id.moreFacebook).setOnClickListener(
                v -> openLink("https://facebook.com/"));

        findViewById(R.id.moreInstagram).setOnClickListener(
                v -> openLink("https://instagram.com/"));

        findViewById(R.id.moreYouTube).setOnClickListener(
                v -> openLink("https://youtube.com/"));

        findViewById(R.id.moreWhatsApp).setOnClickListener(
                v -> openLink(
                        "https://whatsapp.com/channel/0029Vb6mtUXDjiOZZOGkCw00"
                ));

        findViewById(R.id.moreTanzania).setOnClickListener(v -> openCategory("Tanzania"));
        findViewById(R.id.moreKimataifa).setOnClickListener(v -> openCategory("Kimataifa"));
        findViewById(R.id.moreVilabu).setOnClickListener(v -> openCategory("Vilabu"));
        findViewById(R.id.moreMashindano).setOnClickListener(v -> openCategory("Mashindano"));
        findViewById(R.id.moreUsajili).setOnClickListener(v -> openCategory("Usajili"));
        findViewById(R.id.moreWachezaji).setOnClickListener(v -> openCategory("Wachezaji"));
    }

    private void openLink(String url) {
        try {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    android.net.Uri.parse(url)
            ));
        } catch (Exception ignored) {
        }

    }

    private void openCategory(String category) {
        Intent intent =
                new Intent(MoreActivity.this, NewsActivity.class);

        intent.putExtra(
                "selected_category",
                category
        );

        startActivity(intent);
    }
}
