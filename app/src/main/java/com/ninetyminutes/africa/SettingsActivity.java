package com.ninetyminutes.africa;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private TextView settingNotifications;
    private TextView settingLanguage;
    private TextView settingTheme;
    private TextView settingData;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);

        applySavedTheme();

        setContentView(R.layout.activity_settings);

        settingNotifications = findViewById(R.id.settingNotifications);
        settingLanguage = findViewById(R.id.settingLanguage);
        settingTheme = findViewById(R.id.settingTheme);
        settingData = findViewById(R.id.settingData);

        updateNotificationText();
        updateLanguageText();
        updateThemeText();

        settingNotifications.setOnClickListener(v -> {

            boolean currentStatus =
                    preferences.getBoolean(
                            "notifications_enabled",
                            true
                    );

            preferences.edit()
                    .putBoolean(
                            "notifications_enabled",
                            !currentStatus
                    )
                    .apply();

            updateNotificationText();

            String message = !currentStatus
                    ? "🔔 Notifications zimewashwa"
                    : "🔕 Notifications zimezimwa";

            Toast.makeText(
                    SettingsActivity.this,
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        });

        settingLanguage.setOnClickListener(v -> showLanguageDialog());

        settingTheme.setOnClickListener(v -> showThemeDialog());

        settingData.setOnClickListener(v -> showDataDialog());
    }

    private void applySavedTheme() {

        boolean darkMode =
                preferences.getBoolean(
                        "dark_mode",
                        true
                );

        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }

    private void showDataDialog() {

        long cacheSize = getDirSize(getCacheDir());

        String storageText =
                Formatter.formatFileSize(
                        this,
                        cacheSize
                );

        boolean connected = isInternetConnected();

        String connectionText = connected
                ? "🟢 Internet: Connected"
                : "🔴 Internet: Not connected";

        String message =
                "📦 Cache: " + storageText + "\n\n" +
                connectionText + "\n\n" +
                "Unaweza kufuta cache bila kufuta " +
                "settings zako.";

        new AlertDialog.Builder(this)
                .setTitle("📶 Data & Storage")
                .setMessage(message)
                .setNegativeButton(
                        "CLOSE",
                        null
                )
                .setPositiveButton(
                        "CLEAR CACHE",
                        (dialog, which) -> {

                            clearAppCache();

                            Toast.makeText(
                                    SettingsActivity.this,
                                    "🗑️ Cache imefutwa",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    private boolean isInternetConnected() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager)
                        getSystemService(CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(
                        connectivityManager.getActiveNetwork()
                );

        return capabilities != null &&
                (
                        capabilities.hasTransport(
                                NetworkCapabilities.TRANSPORT_WIFI
                        )
                        ||
                        capabilities.hasTransport(
                                NetworkCapabilities.TRANSPORT_CELLULAR
                        )
                        ||
                        capabilities.hasTransport(
                                NetworkCapabilities.TRANSPORT_ETHERNET
                        )
                );
    }

    private void clearAppCache() {

        File cacheDir = getCacheDir();

        deleteDirectoryContents(cacheDir);
    }

    private void deleteDirectoryContents(File directory) {

        if (directory == null || !directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {
                deleteDirectoryContents(file);
            }

            file.delete();
        }
    }

    private long getDirSize(File directory) {

        if (directory == null || !directory.exists()) {
            return 0;
        }

        long size = 0;

        File[] files = directory.listFiles();

        if (files == null) {
            return 0;
        }

        for (File file : files) {

            if (file.isDirectory()) {
                size += getDirSize(file);
            } else {
                size += file.length();
            }
        }

        return size;
    }

    private void showThemeDialog() {

        String[] themes = {
                "🌙 Dark Mode",
                "☀️ Light Mode"
        };

        boolean darkMode =
                preferences.getBoolean(
                        "dark_mode",
                        true
                );

        int checkedItem = darkMode ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle("🎨 Chagua Theme")
                .setSingleChoiceItems(
                        themes,
                        checkedItem,
                        (dialog, which) -> {

                            boolean selectedDark =
                                    which == 0;

                            preferences.edit()
                                    .putBoolean(
                                            "dark_mode",
                                            selectedDark
                                    )
                                    .apply();

                            if (selectedDark) {
                                AppCompatDelegate.setDefaultNightMode(
                                        AppCompatDelegate.MODE_NIGHT_YES
                                );
                            } else {
                                AppCompatDelegate.setDefaultNightMode(
                                        AppCompatDelegate.MODE_NIGHT_NO
                                );
                            }

                            dialog.dismiss();
                        }
                )
                .show();
    }

    private void showLanguageDialog() {

        String[] languages = {
                "🇹🇿 Kiswahili",
                "🇬🇧 English"
        };

        String currentLanguage =
                preferences.getString(
                        "language",
                        "sw"
                );

        int checkedItem =
                currentLanguage.equals("en") ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle(
                        "🌐 Chagua Lugha / Choose Language"
                )
                .setSingleChoiceItems(
                        languages,
                        checkedItem,
                        (dialog, which) -> {

                            String selectedLanguage =
                                    which == 0 ? "sw" : "en";

                            preferences.edit()
                                    .putString(
                                            "language",
                                            selectedLanguage
                                    )
                                    .apply();

                            updateLanguageText();

                            dialog.dismiss();

                            String message =
                                    selectedLanguage.equals("sw")
                                            ? "🇹🇿 Kiswahili imechaguliwa"
                                            : "🇬🇧 English selected";

                            Toast.makeText(
                                    SettingsActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    private void updateNotificationText() {

        boolean enabled =
                preferences.getBoolean(
                        "notifications_enabled",
                        true
                );

        if (enabled) {
            settingNotifications.setText(
                    "🔔   Notifications — ON"
            );
        } else {
            settingNotifications.setText(
                    "🔕   Notifications — OFF"
            );
        }
    }

    private void updateLanguageText() {

        String language =
                preferences.getString(
                        "language",
                        "sw"
                );

        if (language.equals("en")) {
            settingLanguage.setText(
                    "🌐   Language     English"
            );
        } else {
            settingLanguage.setText(
                    "🌐   Language     Kiswahili"
            );
        }
    }

    private void updateThemeText() {

        boolean darkMode =
                preferences.getBoolean(
                        "dark_mode",
                        true
                );

        if (darkMode) {
            settingTheme.setText(
                    "🌙   Dark Mode     ON"
            );
        } else {
            settingTheme.setText(
                    "☀️   Light Mode     ON"
            );
        }
    }
}
