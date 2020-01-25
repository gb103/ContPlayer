package com.contplayer.demo;

import com.contplayer.engine.IContPlayerQueue;
import com.contplayer.engine.IStreamUrlFetchListener;

import java.util.ArrayList;

public class DemoPlayerQueue extends IContPlayerQueue<String> {


    public DemoPlayerQueue() {
        super();
    }

    public DemoPlayerQueue(ArrayList<String> strings) {
        super(strings);
    }

    @Override
    public void setArrayList(ArrayList<String> strings) {
        super.setArrayList(strings);
    }

    @Override
    public void getCurrentStreamingUrl(IStreamUrlFetchListener iStreamUrlFetchListener) {
        iStreamUrlFetchListener.streamUrlFetched(playableEntityList.get(currentIndex));
    }

    @Override
    public void getPrevStreamingUrl(IStreamUrlFetchListener iStreamUrlFetchListener) {
        if(isPrevIndexExists()) {
            iStreamUrlFetchListener.streamUrlFetched(playableEntityList.get(currentIndex - 1));
        } else {
            iStreamUrlFetchListener.streamUrlFetched(null);
        }
    }

    @Override
    public void getNextStreamUrl(IStreamUrlFetchListener iStreamUrlFetchListener) {
        if(isNextIndexExists()) {
            iStreamUrlFetchListener.streamUrlFetched(playableEntityList.get(currentIndex + 1));
        } else {
            iStreamUrlFetchListener.streamUrlFetched(null);
        }
    }
}
