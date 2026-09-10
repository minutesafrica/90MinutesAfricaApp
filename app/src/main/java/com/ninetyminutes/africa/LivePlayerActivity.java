package com.ninetyminutes.africa;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.ninetyminutes.africa.network.LiveViewerService;

public class LivePlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private WebView webView;
    private ProgressBar loading;
    private TextView errorText;

    private TextView viewerCountText;
    private TextView watchTimeText;

    private String streamUrl;
    private String streamType;
    private String matchId;
    private String viewerId;

    private int watchSeconds = 0;
    private boolean trackingStarted = false;
    private boolean trackingEnded = false;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private Runnable heartbeatRunnable;
    private Runnable viewerCountRunnable;
    private Runnable watchTimerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_player);

        playerView = findViewById(R.id.playerView);
        webView = findViewById(R.id.liveWebView);
        loading = findViewById(R.id.playerLoading);
        errorText = findViewById(R.id.playerError);

        TextView title =
                findViewById(R.id.livePlayerTitle);

        TextView match =
                findViewById(R.id.livePlayerMatch);

        TextView competition =
                findViewById(R.id.livePlayerCompetition);

        viewerCountText =
                findViewById(
                        getResources().getIdentifier(
                                "liveViewerCount",
                                "id",
                                getPackageName()
                        )
                );

        watchTimeText =
                findViewById(
                        getResources().getIdentifier(
                                "liveWatchTime",
                                "id",
                                getPackageName()
                        )
                );

        matchId =
                getIntent().getStringExtra("match_id");

        streamUrl =
                getIntent().getStringExtra("stream_url");

        streamType =
                getIntent().getStringExtra("stream_type");

        String home =
                getIntent().getStringExtra("home_team");

        String away =
                getIntent().getStringExtra("away_team");

        String liveTitle =
                getIntent().getStringExtra("live_title");

        String comp =
                getIntent().getStringExtra("competition");

        title.setText("SPORT LIVE");

        if (liveTitle != null &&
                !liveTitle.trim().isEmpty()) {

            match.setText(liveTitle);

        } else {

            match.setText(
                    safe(home)
                            + "   VS   "
                            + safe(away)
            );
        }

        competition.setText(
                comp != null &&
                        !comp.trim().isEmpty()
                        ? comp
                        : "LIVE"
        );

        findViewById(R.id.livePlayerBack)
                .setOnClickListener(
                        v -> finish()
                );

        if (matchId == null ||
                matchId.trim().isEmpty()) {

            showError(
                    "Mechi ya LIVE haijapatikana."
            );

            return;
        }

        if (streamUrl == null ||
                streamUrl.trim().isEmpty()) {

            showError(
                    "Hakuna stream iliyowekwa kwa mechi hii."
            );

            return;
        }

        startLiveTracking();

        if ("iframe".equalsIgnoreCase(streamType)) {

            openIframe(streamUrl);

        } else if ("dash".equalsIgnoreCase(streamType)) {

            showError(
                    "DASH player bado haijawezeshwa. " +
                    "Tumia HLS au Iframe kwa sasa."
            );

        } else {

            openHls(streamUrl);
        }
    }

    private void startLiveTracking() {

        LiveViewerService.startWatching(
                this,
                matchId,
                new LiveViewerService.StartCallback() {

                    @Override
                    public void onSuccess(
                            String id,
                            int existingWatchSeconds
                    ) {

                        runOnUiThread(() -> {

                            viewerId = id;

                            watchSeconds =
                                    existingWatchSeconds;

                            trackingStarted =
                                    viewerId != null &&
                                    !viewerId.isEmpty();

                            updateWatchText();

                            if (trackingStarted) {
                                startTrackingTimers();
                            }
                        });
                    }

                    @Override
                    public void onError(
                            String error
                    ) {
                        // Player bado inaweza kuendelea
                        // hata analytics ikishindwa.
                    }
                }
        );
    }

    private void startTrackingTimers() {

        if (!trackingStarted) {
            return;
        }

        stopTrackingTimers();

        heartbeatRunnable =
                new Runnable() {

                    @Override
                    public void run() {

                        if (!trackingStarted ||
                                trackingEnded ||
                                viewerId == null) {
                            return;
                        }

                        LiveViewerService.heartbeat(
                                matchId,
                                viewerId,
                                20
                        );

                        handler.postDelayed(
                                this,
                                20000
                        );
                    }
                };

        handler.postDelayed(
                heartbeatRunnable,
                20000
        );

        viewerCountRunnable =
                new Runnable() {

                    @Override
                    public void run() {

                        if (!trackingStarted ||
                                trackingEnded) {
                            return;
                        }

                        LiveViewerService.getViewerCount(
                                matchId,
                                count -> runOnUiThread(() -> {

                                    if (viewerCountText != null) {

                                        viewerCountText.setText(
                                                "● "
                                                        + count
                                                        + " Watazamaji"
                                        );
                                    }

                                    LiveViewerService.updatePeak(
                                            matchId,
                                            count
                                    );
                                })
                        );

                        handler.postDelayed(
                                this,
                                10000
                        );
                    }
                };

        handler.post(
                viewerCountRunnable
        );

        watchTimerRunnable =
                new Runnable() {

                    @Override
                    public void run() {

                        if (!trackingStarted ||
                                trackingEnded) {
                            return;
                        }

                        watchSeconds++;

                        updateWatchText();

                        handler.postDelayed(
                                this,
                                1000
                        );
                    }
                };

        handler.postDelayed(
                watchTimerRunnable,
                1000
        );
    }

    private void updateWatchText() {

        if (watchTimeText == null) {
            return;
        }

        int minutes =
                watchSeconds / 60;

        int seconds =
                watchSeconds % 60;

        watchTimeText.setText(
                String.format(
                        java.util.Locale.US,
                        "%02d:%02d",
                        minutes,
                        seconds
                )
        );
    }

    private void stopTrackingTimers() {

        if (heartbeatRunnable != null) {

            handler.removeCallbacks(
                    heartbeatRunnable
            );

            heartbeatRunnable = null;
        }

        if (viewerCountRunnable != null) {

            handler.removeCallbacks(
                    viewerCountRunnable
            );

            viewerCountRunnable = null;
        }

        if (watchTimerRunnable != null) {

            handler.removeCallbacks(
                    watchTimerRunnable
            );

            watchTimerRunnable = null;
        }
    }

    private void endLiveTracking() {

        if (trackingEnded) {
            return;
        }

        trackingEnded = true;

        stopTrackingTimers();

        if (viewerId != null &&
                !viewerId.isEmpty()) {

            LiveViewerService.endWatching(
                    viewerId
            );
        }

        viewerId = null;
    }

    private void openHls(String url) {

        playerView.setVisibility(
                View.VISIBLE
        );

        webView.setVisibility(
                View.GONE
        );

        loading.setVisibility(
                View.VISIBLE
        );

        errorText.setVisibility(
                View.GONE
        );

        player =
                new ExoPlayer.Builder(this)
                        .build();

        playerView.setPlayer(
                player
        );

        MediaItem mediaItem =
                MediaItem.fromUri(
                        Uri.parse(url)
                );

        player.setMediaItem(
                mediaItem
        );

        player.addListener(
                new Player.Listener() {

                    @Override
                    public void onPlaybackStateChanged(
                            int state
                    ) {

                        if (state ==
                                Player.STATE_READY) {

                            loading.setVisibility(
                                    View.GONE
                            );
                        }

                        if (state ==
                                Player.STATE_BUFFERING) {

                            loading.setVisibility(
                                    View.VISIBLE
                            );
                        }
                    }

                    @Override
                    public void onPlayerError(
                            PlaybackException error
                    ) {

                        loading.setVisibility(
                                View.GONE
                        );

                        showError(
                                "Imeshindikana kuunganisha LIVE stream."
                        );
                    }
                }
        );

        player.prepare();
        player.play();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openIframe(String url) {

        playerView.setVisibility(
                View.GONE
        );

        webView.setVisibility(
                View.VISIBLE
        );

        loading.setVisibility(
                View.VISIBLE
        );

        errorText.setVisibility(
                View.GONE
        );

        WebSettings settings =
                webView.getSettings();

        settings.setJavaScriptEnabled(
                true
        );

        settings.setDomStorageEnabled(
                true
        );

        settings.setMediaPlaybackRequiresUserGesture(
                false
        );

        settings.setAllowFileAccess(
                true
        );

        settings.setAllowContentAccess(
                true
        );

        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public void onPageFinished(
                            WebView view,
                            String url
                    ) {

                        loading.setVisibility(
                                View.GONE
                        );
                    }
                }
        );

        webView.loadUrl(url);
    }

    private void showError(
            String message
    ) {

        loading.setVisibility(
                View.GONE
        );

        playerView.setVisibility(
                View.GONE
        );

        webView.setVisibility(
                View.GONE
        );

        errorText.setText(
                message
        );

        errorText.setVisibility(
                View.VISIBLE
        );
    }

    private String safe(
            String value
    ) {
        return value == null
                ? ""
                : value;
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {

        endLiveTracking();

        if (player != null) {

            player.release();

            player = null;
        }

        if (webView != null) {

            webView.loadUrl(
                    "about:blank"
            );

            webView.destroy();
        }

        super.onDestroy();
    }
}
