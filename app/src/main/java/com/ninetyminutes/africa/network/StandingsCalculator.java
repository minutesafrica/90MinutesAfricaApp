package com.ninetyminutes.africa.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandingsCalculator {

    public static JSONArray calculate(JSONArray results) {

        Map<String, TeamStanding> table = new HashMap<>();

        for (int i = 0; i < results.length(); i++) {

            try {

                JSONObject match = results.getJSONObject(i);

                String status =
                        match.optString("status", "");

                if (!"FT".equalsIgnoreCase(status)) {
                    continue;
                }

                String home =
                        match.optString("home_team", "").trim();

                String away =
                        match.optString("away_team", "").trim();

                if (home.isEmpty() || away.isEmpty()) {
                    continue;
                }

                int homeScore =
                        match.optInt("home_score", -1);

                int awayScore =
                        match.optInt("away_score", -1);

                if (homeScore < 0 || awayScore < 0) {
                    continue;
                }

                if (!table.containsKey(home)) {
                    table.put(home, new TeamStanding(home));
                }

                if (!table.containsKey(away)) {
                    table.put(away, new TeamStanding(away));
                }

                TeamStanding homeTeam = table.get(home);
                TeamStanding awayTeam = table.get(away);

                homeTeam.played++;
                awayTeam.played++;

                homeTeam.goalsFor += homeScore;
                homeTeam.goalsAgainst += awayScore;

                awayTeam.goalsFor += awayScore;
                awayTeam.goalsAgainst += homeScore;

                if (homeScore > awayScore) {

                    homeTeam.won++;
                    awayTeam.lost++;
                    homeTeam.points += 3;

                } else if (homeScore < awayScore) {

                    awayTeam.won++;
                    homeTeam.lost++;
                    awayTeam.points += 3;

                } else {

                    homeTeam.drawn++;
                    awayTeam.drawn++;

                    homeTeam.points++;
                    awayTeam.points++;
                }

            } catch (Exception ignored) {
            }
        }

        List<TeamStanding> teams =
                new ArrayList<>(table.values());

        for (TeamStanding team : teams) {
            team.goalDifference =
                    team.goalsFor - team.goalsAgainst;
        }

        Collections.sort(
                teams,
                new Comparator<TeamStanding>() {
                    @Override
                    public int compare(
                            TeamStanding a,
                            TeamStanding b) {

                        if (b.points != a.points) {
                            return b.points - a.points;
                        }

                        if (b.goalDifference !=
                                a.goalDifference) {
                            return b.goalDifference -
                                    a.goalDifference;
                        }

                        if (b.goalsFor != a.goalsFor) {
                            return b.goalsFor -
                                    a.goalsFor;
                        }

                        return a.team.compareToIgnoreCase(
                                b.team
                        );
                    }
                }
        );

        JSONArray output = new JSONArray();

        for (int i = 0; i < teams.size(); i++) {

            TeamStanding team = teams.get(i);

            JSONObject item = new JSONObject();

            try {

                item.put("position", i + 1);
                item.put("team", team.team);
                item.put("played", team.played);
                item.put("won", team.won);
                item.put("drawn", team.drawn);
                item.put("lost", team.lost);
                item.put("goals_for", team.goalsFor);
                item.put(
                        "goals_against",
                        team.goalsAgainst
                );
                item.put(
                        "goal_difference",
                        team.goalDifference
                );
                item.put("points", team.points);

                output.put(item);

            } catch (Exception ignored) {
            }
        }

        return output;
    }


    private static class TeamStanding {

        String team;

        int played;
        int won;
        int drawn;
        int lost;
        int goalsFor;
        int goalsAgainst;
        int goalDifference;
        int points;

        TeamStanding(String team) {
            this.team = team;
        }
    }
}
