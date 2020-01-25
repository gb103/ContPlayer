package com.contplayer.engine;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public interface IPlayer {

    public void playMusic(Context context, String[] streamingUrl);
    public void preparePlayer(Context context);
    public void releasePlayer();
    public void stop();
    public void pause();
    public void play();
    public void seekTo(long position);
    public void setVolume(float volume);
    public void attachVideoView(PlayerView playerView);

}
