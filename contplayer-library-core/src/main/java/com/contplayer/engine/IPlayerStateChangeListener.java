package com.contplayer.engine;

public interface IPlayerStateChangeListener {

    public void onPlayerStateChanged(int playerState);
    public void onMediaCompleted();
    public void onMediaPrepared();

}
