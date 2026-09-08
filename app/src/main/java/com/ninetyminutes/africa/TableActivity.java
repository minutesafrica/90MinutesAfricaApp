package com.ninetyminutes.africa;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ninetyminutes.africa.network.FootballService;
import com.ninetyminutes.africa.network.StandingsCalculator;

import org.json.JSONArray;
import org.json.JSONObject;

public class TableActivity extends AppCompatActivity {

    private LinearLayout tableContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        tableContainer = findViewById(R.id.tableContainer);

        loadStandings();
    }

    private void loadStandings() {

        tableContainer.removeAllViews();
        addMessage("Inapakia msimamo...");

        FootballService.getResults(new FootballService.Callback() {

            @Override
            public void onSuccess(JSONArray data) {

                runOnUiThread(() -> {

                    tableContainer.removeAllViews();

                    try {

                        JSONArray standings =
                                StandingsCalculator.calculate(data);

                        if (standings.length() == 0) {
                            addMessage(
                                    "Hakuna data ya msimamo kwa sasa."
                            );
                            return;
                        }

                        for (int i = 0;
                             i < standings.length();
                             i++) {

                            JSONObject team =
                                    standings.getJSONObject(i);

                            addTeamRow(team, i);
                        }

                    } catch (Exception e) {

                        addMessage(
                                "Imeshindikana kuhesabu msimamo."
                        );

                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(String error) {

                runOnUiThread(() -> {

                    tableContainer.removeAllViews();

                    addMessage(
                            "Imeshindikana kupakia msimamo.\n"
                                    + error
                    );
                });
            }
        });
    }

    private void addTeamRow(
            JSONObject team,
            int index
    ) {

        int position =
                team.optInt(
                        "position",
                        index + 1
                );

        String name =
                team.optString(
                        "team",
                        "Unknown"
                );

        int played =
                team.optInt(
                        "played",
                        0
                );

        int won =
                team.optInt(
                        "won",
                        0
                );

        int drawn =
                team.optInt(
                        "drawn",
                        0
                );

        int lost =
                team.optInt(
                        "lost",
                        0
                );

        int goalDifference =
                team.optInt(
                        "goal_difference",
                        0
                );

        int points =
                team.optInt(
                        "points",
                        0
                );

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                0, 0, 0, 0
        );

        row.setMinimumHeight(54);

        row.setBackgroundColor(
                index % 2 == 0
                        ? Color.rgb(16, 16, 16)
                        : Color.rgb(21, 21, 21)
        );

        addCell(
                row,
                position + ". " + name,
                3,
                "#FFFFFF",
                Gravity.CENTER_VERTICAL,
                true
        );

        addCell(
                row,
                String.valueOf(played),
                1,
                "#FFFFFF",
                Gravity.CENTER,
                false
        );

        addCell(
                row,
                String.valueOf(won),
                1,
                "#FFFFFF",
                Gravity.CENTER,
                false
        );

        addCell(
                row,
                String.valueOf(drawn),
                1,
                "#FFFFFF",
                Gravity.CENTER,
                false
        );

        addCell(
                row,
                String.valueOf(lost),
                1,
                "#FFFFFF",
                Gravity.CENTER,
                false
        );

        String gd =
                goalDifference > 0
                        ? "+" + goalDifference
                        : String.valueOf(goalDifference);

        addCell(
                row,
                gd,
                1,
                "#FFFFFF",
                Gravity.CENTER,
                false
        );

        addCell(
                row,
                String.valueOf(points),
                1,
                "#E30613",
                Gravity.CENTER,
                true
        );

        tableContainer.addView(row);
    }

    private void addCell(
            LinearLayout parent,
            String text,
            int weight,
            String color,
            int gravity,
            boolean bold
    ) {

        TextView cell =
                new TextView(this);

        cell.setText(text);
        cell.setTextColor(
                Color.parseColor(color)
        );

        cell.setTextSize(12);

        cell.setGravity(gravity);

        if (bold) {
            cell.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }

        cell.setPadding(
                4, 0, 4, 0
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        weight
                );

        cell.setLayoutParams(params);

        parent.addView(cell);
    }

    private void addMessage(
            String message
    ) {

        TextView view =
                new TextView(this);

        view.setText(message);
        view.setTextSize(15);
        view.setTextColor(
                Color.parseColor("#AAAAAA")
        );

        view.setPadding(
                10, 30, 10, 30
        );

        tableContainer.addView(view);
    }
}
