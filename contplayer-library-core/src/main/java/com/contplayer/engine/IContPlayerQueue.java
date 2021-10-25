package com.contplayer.engine;

import java.util.ArrayList;

public abstract class IContPlayerQueue<T> {

    protected ArrayList<T> playableEntityList;
    protected int currentIndex = 0;

    public IContPlayerQueue() {

    }

    public IContPlayerQueue(ArrayList<T> tArrayList) {
        this.playableEntityList = tArrayList;
    }

    protected void setArrayList(ArrayList<T> tArrayList) {
        this.playableEntityList = tArrayList;
    }

    protected boolean isPrevIndexExists() {
        if(currentIndex > 0) {
            return true;
        }
        return false;
    }

    protected boolean isNextIndexExists() {
        if(currentIndex < (playableEntityList.size() - 1)) {
            return true;
        }
        return false;
    }

    protected void next() {
        if(isNextIndexExists()) {
            currentIndex++;
        }
    }

    protected void prev() {
        if(isPrevIndexExists()) {
            currentIndex--;
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getSize() {
        return playableEntityList == null ? 0 : playableEntityList.size();
    }

    public ArrayList<T> getArrayList() {
        return playableEntityList;
    }

    public abstract void getCurrentStreamingUrl(IStreamUrlFetchListener iStreamUrlFetchListener);
    public abstract void getPrevStreamingUrl(IStreamUrlFetchListener iStreamUrlFetchListener);
    public abstract void getNextStreamUrl(IStreamUrlFetchListener iStreamUrlFetchListener);

}
