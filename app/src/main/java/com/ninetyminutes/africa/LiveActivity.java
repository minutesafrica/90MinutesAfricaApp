package com.ninetyminutes.africa;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class LiveActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView titleView;
    private TextView competitionView;
    private TextView statusView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        MobileAds.initialize(this, initializationStatus -> {});
        AdView liveBannerAd = findViewById(R.id.liveBannerAd);
        liveBannerAd.loadAd(new AdRequest.Builder().build());


        playerView = findViewById(R.id.livePlayerView);
        titleView = findViewById(R.id.liveTitle);
        competitionView = findViewById(R.id.liveCompetition);
        statusView = findViewById(R.id.liveStatus);

        String streamUrl = getIntent().getStringExtra("stream_url");
        String streamType = getIntent().getStringExtra("stream_type");
        String liveTitle = getIntent().getStringExtra("live_title");
        String competition = getIntent().getStringExtra("competition");
        String homeTeam = getIntent().getStringExtra("home_team");
        String awayTeam = getIntent().getStringExtra("away_team");

        if (liveTitle == null || liveTitle.isEmpty()) {
            liveTitle = homeTeam + " vs " + awayTeam;
        }

        titleView.setText(liveTitle);

        if (competition == null || competition.isEmpty()) {
            competitionView.setVisibility(View.GONE);
        } else {
            competitionView.setText(competition);
        }

        if (streamUrl == null || streamUrl.isEmpty()) {
            statusView.setText("Hakuna stream iliyowekwa kwa mechi hii.");
            return;
        }

        if ("iframe".equalsIgnoreCase(streamType)) {
            statusView.setText(
                    "Stream hii inahitaji iframe. Player ya Android itahitaji WebView."
            );
            return;
        }

        if ("dash".equalsIgnoreCase(streamType)) {
            statusView.setText(
                    "DASH player bado haijawezeshwa. Tumia HLS au Iframe kwa sasa."
            );
            return;
        }

        startPlayer(streamUrl);
    }

    private void startPlayer(String streamUrl) {
        statusView.setText("Inaunganisha LIVE...");

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(streamUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        player.addListener(new androidx.media3.common.Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState ==
                        androidx.media3.common.Player.STATE_READY) {
                    statusView.setVisibility(View.GONE);
                } else if (playbackState ==
                        androidx.media3.common.Player.STATE_BUFFERING) {
                    statusView.setVisibility(View.VISIBLE);
                    statusView.setText("Inaunganisha LIVE...");
                }
            }

            @Override
            public void onPlayerError(
                    androidx.media3.common.PlaybackException error) {
                statusView.setVisibility(View.VISIBLE);
                statusView.setText(
                        "Imeshindikana kuunganisha LIVE stream."
                );
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (player != null) {
            player.release();
            player = null;
        }
    }
}
