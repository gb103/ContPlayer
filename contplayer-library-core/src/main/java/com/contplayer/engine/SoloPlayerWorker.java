package com.contplayer.engine;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.SampleQueueMappingException;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

public class SoloPlayerWorker implements ExoPlayer.EventListener {

    public static DefaultBandwidthMeter BANDWIDTH_METER = null;
    private SimpleExoPlayer player;
    private int lastReportedPlaybackState;
    private boolean lastReportedPlayWhenReady;
    private Uri[] uris;
    private Context mContext;
    private Listener listener;

    SoloPlayerWorker(Context context, Listener listener) {
        this.listener = listener;
        this.mContext = context;
        initialiseBandwidthMeter();
        initializePlayer();
    }

    private void initialiseBandwidthMeter() {
        if (BANDWIDTH_METER == null) {
            BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(mContext).setInitialBitrateEstimate(40_000).build();
        }
    }

    private void initializePlayer() {
        //Intent intent = getIntent();
        boolean needNewPlayer = player == null;
        if (needNewPlayer) {
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext);//default renderer mode is off

            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory();//BANDWIDTH_METER now passed to player initialisation

            DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            DefaultTrackSelector.ParametersBuilder parametersBuilder = trackSelector.buildUponParameters();
            parametersBuilder.setAllowAudioMixedSampleRateAdaptiveness(true);
            trackSelector.setParameters(parametersBuilder);
            DefaultLoadControl gaanaLoadControl = new DefaultLoadControl.Builder()
                    .setTargetBufferBytes(200 * 64 * 1024)
                    .setPrioritizeTimeOverSizeThresholds(false)
                    .createDefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory, trackSelector, gaanaLoadControl, null, BANDWIDTH_METER);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            player.addListener(this);
            maybeReportPlayerState();
        }
    }

    public static String getUserAgent() {
        String userAgent = System.getProperty("http.agent");
        return userAgent;
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(getUserAgent(), bandwidthMeter);
    }

    /*public DataSource.Factory buildDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }*/

    private DefaultDataSource.Factory buildHttpDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }

    private DataSource.Factory buildDataSourceFactory(Context context, final boolean useBandwidthMeter) {
        return buildHttpDataSourceFactory(context, useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private MediaSource buildMediaSource(Context context, Uri uri, DataSource.Factory mediaDataSourceFactory, String overrideExtension, boolean allowChunkless) {
        int type = TextUtils.isEmpty(overrideExtension) ? com.google.android.exoplayer2.util.Util.inferContentType(uri)
                : com.google.android.exoplayer2.util.Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .setAllowChunklessPreparation(allowChunkless)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                if (Util.isLocalFileUri(uri)) {
                    return new ProgressiveMediaSource.Factory(buildHttpDataSourceFactory(context, null)).createMediaSource(uri);
                } else {
                    return new ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
                }
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    public void playWithUri(Uri[] uris) {
        this.uris = uris;
        MediaSource[] mediaSources = new MediaSource[uris.length];
        DataSource.Factory mediaDataSourceFactory = buildDataSourceFactory(mContext, true);
        mediaSources[0] = buildMediaSource(mContext, uris[0], mediaDataSourceFactory, null, true);
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        player.prepare(mediaSource, false, false);
        maybeReportPlayerState();
    }

    public boolean getPlayWhenReady() {
        return player.getPlayWhenReady();
    }

    public void setVolume(float volume) {
        player.setVolume(volume);
    }

    public void resume() {
        player.setPlayWhenReady(true);
    }

    public void pause() {
        player.setPlayWhenReady(false);
    }

    public void stop() {
        player.stop();
    }

    public void seekTo(long position) {
        player.seekTo(position);
    }

    public void release() {
        player.release();
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public int getPlaybackState() {
        int playerState = player.getPlaybackState();
        return playerState;
    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = player.getPlayWhenReady();
        int playbackState = getPlaybackState();
        if (lastReportedPlayWhenReady != playWhenReady || lastReportedPlaybackState != playbackState) {
            lastReportedPlayWhenReady = playWhenReady;
            lastReportedPlaybackState = playbackState;
            if(listener != null) {
                listener.onStateChanged(playWhenReady, playbackState);
            }
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (error != null && error.getCause() instanceof SampleQueueMappingException) {
            playWithUri(uris);
        } else {
            if(listener != null) {
                listener.onError(error);
            }
        }
    }

    public interface Listener {
        void onStateChanged(boolean playWhenReady, int playbackState);

        void onError(Exception e);
    }


}
