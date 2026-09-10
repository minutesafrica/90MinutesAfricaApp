package com.ninetyminutes.africa;

public class LiveMatch {

    private final String id;
    private final String homeTeam;
    private final String awayTeam;
    private final String matchTime;
    private final String competition;
    private final String liveTitle;
    private final String streamUrl;
    private final String streamType;
    private final boolean isLive;

    public LiveMatch(
            String id,
            String homeTeam,
            String awayTeam,
            String matchTime,
            String competition,
            String liveTitle,
            String streamUrl,
            String streamType,
            boolean isLive
    ) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchTime = matchTime;
        this.competition = competition;
        this.liveTitle = liveTitle;
        this.streamUrl = streamUrl;
        this.streamType = streamType;
        this.isLive = isLive;
    }

    public String getId() {
        return id;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public String getCompetition() {
        return competition;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getStreamType() {
        return streamType;
    }

    public boolean isLive() {
        return isLive;
    }
}
