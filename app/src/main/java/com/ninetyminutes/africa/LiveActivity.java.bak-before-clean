package com.ninetyminutes.africa;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.ui.PlayerView;

import com.ninetyminutes.africa.network.LiveViewerService;

public class LiveActivity extends AppCompatActivity {

    private PlayerView playerView;
    private WebView webView;

    private TextView liveTitle;
    private TextView liveStatus;
    private TextView liveMatch;

    private ExoPlayer player;

    private String matchId = "";
    private int watchSeconds = 0;

    private final Handler viewerHandler =
            new Handler(Looper.getMainLooper());

    private Runnable viewerRunnable;

    private boolean viewerTrackingStarted = false;
    private boolean viewerTrackingStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live);

        playerView = findViewById(R.id.playerView);
        webView = findViewById(R.id.webView);

        liveTitle = findViewById(R.id.liveTitle);
        liveStatus = findViewById(R.id.liveStatus);
        liveMatch = findViewById(R.id.liveMatch);

        ImageButton back =
                findViewById(R.id.liveBack);

        back.setOnClickListener(v -> finish());

        matchId =
                getIntent().getStringExtra("match_id");

        String streamUrl =
                getIntent().getStringExtra(
                        "stream_url"
                );

        String streamType =
                getIntent().getStringExtra(
                        "stream_type"
                );

        String title =
                getIntent().getStringExtra(
                        "live_title"
                );

        String homeTeam =
                getIntent().getStringExtra(
                        "home_team"
                );

        String awayTeam =
                getIntent().getStringExtra(
                        "away_team"
                );

        String competition =
                getIntent().getStringExtra(
                        "competition"
                );

        liveTitle.setText(
                title != null && !title.isEmpty()
                        ? title
                        : "LIVE FOOTBALL"
        );

        String teams = "";

        if (homeTeam != null && !homeTeam.isEmpty()) {
            teams = homeTeam;
        }

        if (awayTeam != null && !awayTeam.isEmpty()) {

            if (!teams.isEmpty()) {
                teams += "  vs  ";
            }

            teams += awayTeam;
        }

        liveMatch.setText(teams);

        if (competition != null &&
                !competition.isEmpty()) {

            liveStatus.setText(competition);
        }

        if (streamUrl == null ||
                streamUrl.trim().isEmpty()) {

            showError(
                    "Hakuna stream iliyowekwa kwa mechi hii."
            );

            return;
        }

        startViewerTracking();

        if ("iframe".equalsIgnoreCase(streamType)) {

            playIframe(streamUrl);

            return;
        }

        if ("dash".equalsIgnoreCase(streamType)) {

            showError(
                    "DASH player bado haijawezeshwa. Tumia HLS au Iframe kwa sasa."
            );

            stopViewerTracking();

            return;
        }

        playHls(streamUrl);
    }

    private void playHls(String streamUrl) {

        playerView.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        liveStatus.setText(
                "Inaunganisha LIVE..."
        );

        player =
                new ExoPlayer.Builder(this)
                        .build();

        playerView.setPlayer(player);

        DefaultHttpDataSource.Factory
                dataSourceFactory =
                new DefaultHttpDataSource.Factory()
                        .setAllowCrossProtocolRedirects(true);

        HlsMediaSource mediaSource =
                new HlsMediaSource.Factory(
                        dataSourceFactory
                ).createMediaSource(
                        MediaItem.fromUri(
                                Uri.parse(streamUrl)
                        )
                );

        player.setMediaSource(mediaSource);

        player.prepare();

        player.setPlayWhenReady(true);

        player.addListener(
                new Player.Listener() {

                    @Override
                    public void onPlaybackStateChanged(
                            int state
                    ) {

                        if (state ==
                                Player.STATE_BUFFERING) {

                            liveStatus.setText(
                                    "Ina-load matangazo LIVE..."
                            );

                        } else if (state ==
                                Player.STATE_READY) {

                            liveStatus.setText(
                                    "LIVE"
                            );

                        } else if (state ==
                                Player.STATE_ENDED) {

                            liveStatus.setText(
                                    "Matangazo yamekwisha."
                            );
                        }
                    }

                    @Override
                    public void onPlayerError(
                            androidx.media3.common.PlaybackException error
                    ) {

                        showError(
                                "Imeshindikana kucheza stream hii."
                        );
                    }
                }
        );
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void playIframe(String streamUrl) {

        playerView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        liveStatus.setText(
                "Inaunganisha LIVE..."
        );

        WebSettings settings =
                webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView.setWebViewClient(
                new WebViewClient()
        );

        webView.setWebChromeClient(
                new WebChromeClient()
        );

        webView.loadUrl(streamUrl);

        liveStatus.setText("LIVE");
    }

    private void startViewerTracking() {

        if (matchId == null || matchId.isEmpty() ||
                viewerTrackingStarted) {
            return;
        }

        viewerTrackingStarted = true;
        viewerTrackingStopped = false;

        LiveViewerService.recordLiveView(
                matchId,
                null
        );

        LiveViewerService.startWatching(
                matchId,
                null
        );

        viewerRunnable = new Runnable() {

            @Override
            public void run() {

                if (viewerTrackingStopped) {
                    return;
                }

                watchSeconds += 15;

                LiveViewerService.updateWatching(
                        matchId,
                        watchSeconds,
                        null
                );

                LiveViewerService.updatePeakViewers(
                        matchId,
                        null
                );

                viewerHandler.postDelayed(
                        this,
                        15000
                );
            }
        };

        viewerHandler.postDelayed(
                viewerRunnable,
                15000
        );
    }

    private void stopViewerTracking() {

        if (viewerTrackingStopped) {
            return;
        }

        viewerTrackingStopped = true;

        if (viewerRunnable != null) {

            viewerHandler.removeCallbacks(
                    viewerRunnable
            );

            viewerRunnable = null;
        }

        if (matchId != null && !matchId.isEmpty() &&
                viewerTrackingStarted) {

            LiveViewerService.endWatching(
                    matchId,
                    watchSeconds,
                    null
            );
        }
    }

    @Override
    protected void onPause() {

        if (webView != null) {
            webView.onPause();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onStop() {

        stopViewerTracking();

        super.onStop();

        if (player != null) {

            player.release();
            player = null;
        }

        if (playerView != null) {
            playerView.setPlayer(null);
        }
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.loadUrl(
                    "about:blank"
            );

            webView.stopLoading();
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }

    private void showError(String message) {

        playerView.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);

        liveStatus.setText(message);

        if (player != null) {

            player.release();
            player = null;
        }

        if (playerView != null) {
            playerView.setPlayer(null);
        }
    }
}
