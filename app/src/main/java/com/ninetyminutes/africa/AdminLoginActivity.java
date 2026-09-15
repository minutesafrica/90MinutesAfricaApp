package com.ninetyminutes.africa;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminLoginActivity extends AppCompatActivity {

    private static final String ADMIN_EMAIL = "simbayanga4888@gmail.com";

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private ProgressBar progressBar;

    private final OkHttpClient client = new OkHttpClient();

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLoginUI();
    }

    private void createLoginUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(40, 50, 40, 40);
        root.setBackgroundColor(android.graphics.Color.rgb(10, 10, 10));

        TextView logo = new TextView(this);
        logo.setText("90' MINUTES AFRICA");
        logo.setTextColor(android.graphics.Color.WHITE);
        logo.setTextSize(25);
        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        logoParams.setMargins(0, 0, 0, 35);
        root.addView(logo, logoParams);

        TextView title = new TextView(this);
        title.setText("Admin Login");
        title.setTextColor(android.graphics.Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Ingia kwenye mfumo wa usimamizi");
        subtitle.setTextColor(android.graphics.Color.LTGRAY);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        subtitleParams.setMargins(0, 8, 0, 35);
        root.addView(subtitle, subtitleParams);

        emailInput = createInput("Email ya admin");
        emailInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        root.addView(emailInput);

        passwordInput = createInput("Password");
        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        LinearLayout.LayoutParams passwordParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        passwordParams.setMargins(0, 18, 0, 0);
        root.addView(passwordInput, passwordParams);

        loginButton = new Button(this);
        loginButton.setText("INGIA");
        loginButton.setTextColor(android.graphics.Color.WHITE);
        loginButton.setTextSize(16);
        loginButton.setBackgroundColor(
                android.graphics.Color.rgb(213, 0, 0)
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        buttonParams.setMargins(0, 28, 0, 0);
        root.addView(loginButton, buttonParams);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        progressParams.gravity = Gravity.CENTER_HORIZONTAL;
        progressParams.setMargins(0, 20, 0, 0);
        root.addView(progressBar, progressParams);

        setContentView(root);

        loginButton.setOnClickListener(v -> login());
    }

    private EditText createInput(String hint) {

        EditText input = new EditText(this);

        input.setHint(hint);
        input.setHintTextColor(android.graphics.Color.GRAY);
        input.setTextColor(android.graphics.Color.WHITE);
        input.setTextSize(16);
        input.setSingleLine(true);
        input.setPadding(20, 15, 20, 15);

        input.setBackgroundColor(
                android.graphics.Color.rgb(34, 34, 34)
        );

        return input;
    }

    private void login() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Jaza Email na Password.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        setLoading(true);

        new Thread(() -> {

            try {

                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                Request request = new Request.Builder()
                        .url(
                                SupabaseConfig.URL +
                                "/auth/v1/token?grant_type=password"
                        )
                        .addHeader("apikey", SupabaseConfig.KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(
                                RequestBody.create(
                                        body.toString(),
                                        JSON
                                )
                        )
                        .build();

                try (Response response =
                             client.newCall(request).execute()) {

                    String responseBody =
                            response.body() != null
                                    ? response.body().string()
                                    : "";

                    if (!response.isSuccessful()) {
                        showError("Email au password si sahihi.");
                        return;
                    }

                    JSONObject result =
                            new JSONObject(responseBody);

                    String accessToken =
                            result.optString("access_token", "");

                    JSONObject user =
                            result.optJSONObject("user");

                    String userEmail =
                            user != null
                                    ? user.optString("email", "")
                                    : "";

                    if (accessToken.isEmpty()) {
                        showError("Imeshindikana kuingia Admin.");
                        return;
                    }

                    if (!ADMIN_EMAIL.equalsIgnoreCase(userEmail)) {

                        signOut(accessToken);

                        showError(
                                "Huna ruhusa ya kuingia Admin."
                        );

                        return;
                    }

                    saveSession(accessToken);

                    runOnUiThread(() -> {

                        setLoading(false);

                        Intent intent = new Intent(
                                AdminLoginActivity.this,
                                AdminDashboardActivity.class
                        );

                        startActivity(intent);
                        finish();
                    });
                }

            } catch (Exception e) {

                showError(
                        "Tatizo la mtandao. Jaribu tena."
                );
            }

        }).start();
    }

    private void signOut(String accessToken) {

        try {

            Request request = new Request.Builder()
                    .url(SupabaseConfig.URL + "/auth/v1/logout")
                    .addHeader("apikey", SupabaseConfig.KEY)
                    .addHeader(
                            "Authorization",
                            "Bearer " + accessToken
                    )
                    .post(RequestBody.create("", JSON))
                    .build();

            client.newCall(request).execute().close();

        } catch (Exception ignored) {
        }
    }

    private void saveSession(String accessToken) {

        getSharedPreferences(
                "admin_session",
                MODE_PRIVATE
        )
                .edit()
                .putString("access_token", accessToken)
                .apply();
    }

    private void showError(String message) {

        runOnUiThread(() -> {

            setLoading(false);

            Toast.makeText(
                    AdminLoginActivity.this,
                    message,
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    private void setLoading(boolean loading) {

        runOnUiThread(() -> {

            progressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );

            loginButton.setEnabled(!loading);

            loginButton.setText(
                    loading ? "Inaingia..." : "INGIA"
            );
        });
    }
}
