package com.contplayer.engine;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class SoloPlayer implements IPlayer, SoloPlayerWorker.Listener {

    private SoloPlayerWorker soloPlayerWorker;
    private Uri[] contentUri;
    private boolean isPrimaryPlayer;
    private IPlayerStateChangeListener playerStateChangedListener;
    private boolean isPrepared = false;

    public void setIsPrimaryPlayer(boolean isPrimaryPlayer) {
        this.isPrimaryPlayer = isPrimaryPlayer;
    }

    public void setPlayerStateChangeListener(IPlayerStateChangeListener iPlayerStateChangeListener) {
        this.playerStateChangedListener = iPlayerStateChangeListener;
    }


    @Override
    public void playMusic(Context context, String[] streamingUrl) {
        contentUri = new Uri[streamingUrl.length];
        for (int i = 0; i < streamingUrl.length; i++) {
            contentUri[i] = Uri.parse(streamingUrl[i]);
        }
        isPrepared = false;
        preparePlayer(context);
    }

    public boolean isPlaying() {
        if(soloPlayerWorker != null && soloPlayerWorker.getPlayWhenReady()){
            int playBackStatus = soloPlayerWorker.getPlaybackState();
            if(playBackStatus == ExoPlayer.STATE_BUFFERING || playBackStatus == ExoPlayer.STATE_READY)
                return true;
        }
        return false;
    }

    @Override
    public void preparePlayer(Context context) {
        soloPlayerWorker = new SoloPlayerWorker(context, this);
        soloPlayerWorker.playWithUri(contentUri);
        if(isPrimaryPlayer) soloPlayerWorker.pause();
        else soloPlayerWorker.resume();
    }

    @Override
    public void setVolume(float volume) {
        if(soloPlayerWorker != null) soloPlayerWorker.setVolume(volume);
    }

    @Override
    public void attachVideoView(PlayerView playerView) {
        playerView.setPlayer(soloPlayerWorker.getPlayer());
    }

    @Override
    public void releasePlayer() {
        if(soloPlayerWorker != null) soloPlayerWorker.release();
    }

    @Override
    public void stop() {
        if(soloPlayerWorker != null) soloPlayerWorker.stop();
    }

    @Override
    public void pause() {
        if(soloPlayerWorker != null) soloPlayerWorker.pause();
    }

    @Override
    public void play() {
        if(soloPlayerWorker != null) soloPlayerWorker.resume();
    }

    @Override
    public void seekTo(long position) {
        if(soloPlayerWorker != null) soloPlayerWorker.seekTo(position);
    }

    //Solo Player worker callbacks - start

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                if( playerStateChangedListener != null && isPrimaryPlayer)
                    playerStateChangedListener.onPlayerStateChanged(PlaybackStateCompat.STATE_BUFFERING);
                break;
            case ExoPlayer.STATE_ENDED:
                if( playerStateChangedListener != null && isPrimaryPlayer) {
                    playerStateChangedListener.onMediaCompleted();
                }
                break;
            case ExoPlayer.STATE_IDLE:
                if( playerStateChangedListener != null && isPrimaryPlayer)
                    playerStateChangedListener.onPlayerStateChanged(PlaybackStateCompat.STATE_STOPPED);
                break;
            case ExoPlayer.STATE_READY:
                if( playerStateChangedListener != null && isPrimaryPlayer) {
                    if(playWhenReady) {
                        playerStateChangedListener.onPlayerStateChanged(PlaybackStateCompat.STATE_PLAYING);
                    } else {
                        playerStateChangedListener.onPlayerStateChanged(PlaybackStateCompat.STATE_PAUSED);
                    }
                }
                if(!isPrepared && playWhenReady) {
                    if( playerStateChangedListener != null ) {
                        playerStateChangedListener.onMediaPrepared();
                        isPrepared = true;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Exception e) {

    }

    //Solo Player worker callbacks - end
}
