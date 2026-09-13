package com.ninetyminutes.africa;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StandingsActivity extends AppCompatActivity {

    private LinearLayout standingsContainer;

    private static class TeamStats {
        String team;
        int mp, w, d, l, gf, ga, gd, pts;

        TeamStats(String team) {
            this.team = team;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        standingsContainer = findViewById(R.id.standingsContainer);

        findViewById(R.id.standingsBack).setOnClickListener(v -> finish());

        loadStandings();
    }

    private void loadStandings() {
        ResultsService.loadResults(new ResultsService.Callback() {
            @Override
            public void onSuccess(JSONArray results) {
                runOnUiThread(() -> displayStandings(results));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> displayStandings(new JSONArray()));
            }
        });
    }

    private void displayStandings(JSONArray results) {
        standingsContainer.removeAllViews();

        try {
            Map<String, TeamStats> table = new HashMap<>();

            for (int i = 0; i < results.length(); i++) {
                JSONObject match = results.getJSONObject(i);

                String home = match.optString("home_team", "").trim();
                String away = match.optString("away_team", "").trim();

                if (home.isEmpty() || away.isEmpty()) {
                    continue;
                }

                int homeScore = parseScore(match, "home_score");
                int awayScore = parseScore(match, "away_score");

                TeamStats h = table.get(home);
                if (h == null) {
                    h = new TeamStats(home);
                    table.put(home, h);
                }

                TeamStats a = table.get(away);
                if (a == null) {
                    a = new TeamStats(away);
                    table.put(away, a);
                }

                h.mp++;
                a.mp++;

                h.gf += homeScore;
                h.ga += awayScore;

                a.gf += awayScore;
                a.ga += homeScore;

                if (homeScore > awayScore) {
                    h.w++;
                    h.pts += 3;
                    a.l++;
                } else if (homeScore < awayScore) {
                    a.w++;
                    a.pts += 3;
                    h.l++;
                } else {
                    h.d++;
                    a.d++;
                    h.pts++;
                    a.pts++;
                }
            }

            if (table.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("Hakuna msimamo kwa sasa.");
                empty.setTextColor(Color.WHITE);
                empty.setTextSize(15);
                empty.setPadding(16, 24, 16, 24);
                standingsContainer.addView(empty);
                return;
            }

            ArrayList<TeamStats> teams = new ArrayList<>(table.values());

            for (TeamStats team : teams) {
                team.gd = team.gf - team.ga;
            }

            Collections.sort(teams, new Comparator<TeamStats>() {
                @Override
                public int compare(TeamStats a, TeamStats b) {
                    if (b.pts != a.pts) {
                        return Integer.compare(b.pts, a.pts);
                    }

                    if (b.gd != a.gd) {
                        return Integer.compare(b.gd, a.gd);
                    }

                    if (b.gf != a.gf) {
                        return Integer.compare(b.gf, a.gf);
                    }

                    return a.team.compareToIgnoreCase(b.team);
                }
            });

            addHeader();

            for (int i = 0; i < teams.size(); i++) {
                addTeamRow(i + 1, teams.get(i));
            }

        } catch (Exception e) {
            TextView error = new TextView(this);
            error.setText("Imeshindwa kupakia msimamo.");
            error.setTextColor(Color.WHITE);
            error.setTextSize(15);
            error.setPadding(16, 24, 16, 24);
            standingsContainer.addView(error);
        }
    }

    private int parseScore(JSONObject match, String key) {
        try {
            return Integer.parseInt(match.optString(key, "0"));
        } catch (Exception e) {
            return match.optInt(key, 0);
        }
    }

    private void addHeader() {
        LinearLayout row = createRow();

        row.addView(createCell("#", 0.45f, true));
        row.addView(createCell("TIMU", 2.1f, true));
        row.addView(createCell("MP", 0.55f, true));
        row.addView(createCell("W", 0.55f, true));
        row.addView(createCell("D", 0.55f, true));
        row.addView(createCell("L", 0.55f, true));
        row.addView(createCell("GD", 0.65f, true));
        row.addView(createCell("PTS", 0.75f, true));

        standingsContainer.addView(row);
    }

    private void addTeamRow(int position, TeamStats team) {
        LinearLayout row = createRow();

        row.addView(createCell(String.valueOf(position), 0.45f, false));
        row.addView(createCell(team.team, 2.1f, false));
        row.addView(createCell(String.valueOf(team.mp), 0.55f, false));
        row.addView(createCell(String.valueOf(team.w), 0.55f, false));
        row.addView(createCell(String.valueOf(team.d), 0.55f, false));
        row.addView(createCell(String.valueOf(team.l), 0.55f, false));
        row.addView(createCell(String.valueOf(team.gd), 0.65f, false));
        row.addView(createCell(String.valueOf(team.pts), 0.75f, true));

        standingsContainer.addView(row);
    }

    private LinearLayout createRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(8, 13, 8, 13);
        row.setBackgroundColor(Color.rgb(24, 24, 24));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 1);
        row.setLayoutParams(params);

        return row;
    }

    private TextView createCell(
            String text,
            float weight,
            boolean bold
    ) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setTextColor(Color.WHITE);
        cell.setTextSize(12);
        cell.setGravity(Gravity.CENTER);

        if (bold) {
            cell.setTypeface(null, Typeface.BOLD);
        }

        cell.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        weight
                )
        );

        return cell;
    }
}
