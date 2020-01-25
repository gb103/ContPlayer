package com.contplayer.engine;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

public class ContPlayer {

    private SoloPlayer currentPlayer, nextPlayer, prevPlayer;
    private IContPlayerQueue contPlayerQueue;
    private Context mContext;
    private AudioFocusRequest audioFocusRequest;
    private boolean needToResume = false;
    private IViewBindListener iViewBindListener;
    private OnPlayerStateChangeListener onPlayerStateChangeListener;

    public ContPlayer(Context context) {
        this.mContext = context;
    }

    void setContPlayerQueue(IContPlayerQueue playerQueue) {
        this.contPlayerQueue = playerQueue;
    }

    void setup(IViewBindListener iViewBindListener) {
        this.iViewBindListener = iViewBindListener;
        onPlayerStateChangeListener = new OnPlayerStateChangeListener(this);
        /*setupCurrentPlayer();
        setupNextPlayer();
        setupPrevPlayer();*/
    }


    void setupCurrentPlayer() {
        currentPlayer = getNewPlayer();
        currentPlayer.setIsPrimaryPlayer(true);
        currentPlayer.setPlayerStateChangeListener(onPlayerStateChangeListener);
        contPlayerQueue.getCurrentStreamingUrl(new IStreamUrlFetchListener() {
            @Override
            public void streamUrlFetched(String url) {
                if(grabAudioFocus() && !TextUtils.isEmpty(url)) {
                    currentPlayer.playMusic(mContext, new String[]{url});
                    if(iViewBindListener != null) {
                        iViewBindListener.onViewBind(ContPlayerUtils.PLAYER_CURRENT, currentPlayer);
                    }
                }
            }
        });
    }

    void setupNextPlayer() {
        nextPlayer = getNewPlayer();
        nextPlayer.setIsPrimaryPlayer(false);
        contPlayerQueue.getNextStreamUrl(new IStreamUrlFetchListener() {
            @Override
            public void streamUrlFetched(String url) {
                if(!TextUtils.isEmpty(url)) {
                    nextPlayer.playMusic(mContext, new String[]{url});
                    if(iViewBindListener != null) {
                        iViewBindListener.onViewBind(ContPlayerUtils.PLAYER_NEXT, nextPlayer);
                    }
                }
            }
        });
    }

    void setupPrevPlayer() {
        prevPlayer = getNewPlayer();
        prevPlayer.setIsPrimaryPlayer(false);
        contPlayerQueue.getPrevStreamingUrl(new IStreamUrlFetchListener() {
            @Override
            public void streamUrlFetched(String url) {
                if(!TextUtils.isEmpty(url)) {
                    prevPlayer.playMusic(mContext, new String[]{url});
                    if(iViewBindListener != null) {
                        iViewBindListener.onViewBind(ContPlayerUtils.PLAYER_PREVIOUS, prevPlayer);
                    }
                }
            }
        });
    }

    SoloPlayer getNewPlayer() {
        return new SoloPlayer();
    }

    void movingNext() {
        if(prevPlayer != null) prevPlayer.pause();
        if(nextPlayer != null) nextPlayer.play();
    }

    void movingPrev() {
        if(nextPlayer != null) nextPlayer.pause();
        if(prevPlayer != null) prevPlayer.play();
    }

    void settle() {
        if(currentPlayer != null) {
            currentPlayer.play();
        } else {
            setupCurrentPlayer();
        }
        if(nextPlayer != null) {
            nextPlayer.pause();
        } else {
            setupNextPlayer();
        }
        if(prevPlayer != null) {
            prevPlayer.pause();
        } else {
            setupPrevPlayer();
        }

    }

    void next() {
        if(prevPlayer != null) prevPlayer.releasePlayer();
        prevPlayer = currentPlayer;
        prevPlayer.pause();
        prevPlayer.setIsPrimaryPlayer(false);
        currentPlayer = nextPlayer;
        currentPlayer.setIsPrimaryPlayer(true);
        currentPlayer.setPlayerStateChangeListener(onPlayerStateChangeListener);
        play();
        nextPlayer = null;
        setupNextPlayer();
    }

    void prev() {
        if(nextPlayer != null) nextPlayer.releasePlayer();
        nextPlayer = currentPlayer;
        nextPlayer.pause();
        nextPlayer.setIsPrimaryPlayer(false);
        currentPlayer = prevPlayer;
        currentPlayer.setIsPrimaryPlayer(true);
        currentPlayer.setPlayerStateChangeListener(onPlayerStateChangeListener);
        play();
        prevPlayer = null;
        setupPrevPlayer();
    }

    void play() {
        if(grabAudioFocus()) currentPlayer.play();
    }

    void pause() {
        currentPlayer.pause();
        releaseAudioFocus();
    }

    void stop() {
        currentPlayer.stop();
    }

    void seekTo(long position) {
        currentPlayer.seekTo(position);
    }

    void releasePlayer(SoloPlayer player) {
        player.releasePlayer();
    }

    void releaseAllPlayers() {
        currentPlayer.releasePlayer();
        prevPlayer.releasePlayer();
        nextPlayer.releasePlayer();
        releaseAudioFocus();
    }

    private void releaseAudioFocus() {
        AudioManager audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if(ContPlayerUtils.hasOreo() && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else if(ContPlayerUtils.hasOreo() && _audioFocusChangeListener != null){
            audioManager.abandonAudioFocus(_audioFocusChangeListener);
        }
    }

    public boolean grabAudioFocus() {
        AudioManager audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int result;
        if (ContPlayerUtils.hasOreo()) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                    .build())
                    .setOnAudioFocusChangeListener(_audioFocusChangeListener)
                    .build();
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(_audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            // Show Error Toast on Player Screen (if it exists)
            return false;
        }
        return true;
    }

    private final AudioManager.OnAudioFocusChangeListener _audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            /*if (Constants.IS_DEBUGGABLE) {
                Log.d("Testing", "onAudioFocusChange " + focusChange);
            }*/

            if(currentPlayer == null) return;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // resume playback in case of transient loss
                    if(!needToResume) return;
                    if (!currentPlayer.isPlaying()) {
                        currentPlayer.play();
                    }
                    currentPlayer.setVolume(1.0f);
                    needToResume = false;
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    // Lost focus for an unbounded amount of time: stop playback and
                    // release media player
                    if (currentPlayer.isPlaying()) {
                        //currentPlayer.pause();
                        currentPlayer.pause();
                        needToResume = false;
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Lost focus for a short time, but we have to stop playback.
                    // We don't release the media player because playback
                    // is likely to resume
                    if (currentPlayer.isPlaying()) {
                        currentPlayer.pause();
                        needToResume = true;
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lost focus for a short time, but it's ok to keep playing at
                    // an attenuated level
                    if (currentPlayer.isPlaying()) {
                        currentPlayer.setVolume(0.1f);
                    }
                    break;

                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    // resume playback after short playback stopp
                    if (currentPlayer.isPlaying() && needToResume) {
                        currentPlayer.setVolume(1.0f);
                        needToResume = false;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    break;
            }
            //videoActionListener.onAudioFocusChanged(focusChange);
        }
    };

    private static class OnPlayerStateChangeListener implements IPlayerStateChangeListener {

        private WeakReference<ContPlayer> contPlayerWeakReference;

        OnPlayerStateChangeListener(ContPlayer contPlayer) {
            contPlayerWeakReference = new WeakReference<>(contPlayer);
        }

        @Override
        public void onPlayerStateChanged(int playerState) {

        }

        @Override
        public void onMediaCompleted() {

        }

        @Override
        public void onMediaPrepared() {

        }
    }

}
