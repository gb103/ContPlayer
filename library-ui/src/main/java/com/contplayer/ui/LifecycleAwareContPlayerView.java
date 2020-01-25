package com.contplayer.ui;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.contplayer.engine.ILifeCycleAwareCustomView;

public class LifecycleAwareContPlayerView implements ILifeCycleAwareCustomView {

    ContPlayerView contPlayerView;

    @Override
    public void wrap(Object object) {
        contPlayerView = (ContPlayerView) object;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        contPlayerView.release();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        contPlayerView.release();
    }

    @Override
    public void destroy() {
        onDestroy();
    }
}
