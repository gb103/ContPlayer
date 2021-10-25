package com.contplayer.engine;

import androidx.lifecycle.LifecycleObserver;

public interface ILifeCycleAwareCustomView extends LifecycleObserver {
    public void wrap(Object object);

    public void destroy();
}
