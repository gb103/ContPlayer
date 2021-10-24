package com.contplayer.engine;

import android.content.Context;

public class ContPlayerCommandsManager {

    ContPlayer contPlayer;
    IContPlayerQueue contPlayerQueue;

    public ContPlayerCommandsManager(Context context, IContPlayerQueue contPlayerQueue) {
        contPlayer = new ContPlayer(context);
        this.contPlayerQueue = contPlayerQueue;
        contPlayer.setContPlayerQueue(contPlayerQueue);
    }

    public void initiateContPlayer(IViewBindListener iViewBindListener) {
        contPlayer.setup(iViewBindListener);
    }

    public void setPlayerQueue(IContPlayerQueue contPlayerQueue) {
        this.contPlayerQueue = contPlayerQueue;
        contPlayer.setContPlayerQueue(contPlayerQueue);
    }

    public void moveInProgress(@ContPlayerUtils.MOTION_DIRECTION int dir) {
        if(dir == ContPlayerUtils.IN_MOTION_PREV_LEFT) {
            contPlayer.movingPrev();
        } else if(dir == ContPlayerUtils.IN_MOTION_NEXT_RIGHT){
            contPlayer.movingNext();
        }
    }

    public void settle() {
        contPlayer.settle();
    }

    public void moveNext() {
        contPlayerQueue.next();
        contPlayer.next();
    }

    public void movePrev() {
        contPlayerQueue.prev();
        contPlayer.prev();
    }

    /**
     * call to pause the contPlayer
     */
    public void pause() {
        contPlayer.pause();
    }

    /**
     * call to play the contPlayer
     */
    public void play() {
        contPlayer.play();
    }

    public void stop() {
        contPlayer.stop();
    }

    /**
     * release all the player instance when user exit the video play module
     */
    public void release() {
        contPlayer.releaseAllPlayers();
    }
}
