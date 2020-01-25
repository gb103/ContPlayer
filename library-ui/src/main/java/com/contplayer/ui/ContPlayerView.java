package com.contplayer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPagerUtils;

import com.contplayer.R;
import com.contplayer.engine.ContPlayerCommandsManager;
import com.contplayer.engine.IContPlayerQueue;
import com.contplayer.engine.IViewBindListener;
import com.contplayer.engine.SoloPlayer;
import com.contplayer.engine.ContPlayerUtils;
import com.google.android.exoplayer2.ui.PlayerView;

import java.lang.ref.WeakReference;

import static com.contplayer.engine.ContPlayerUtils.PLAYER_CURRENT;
import static com.contplayer.engine.ContPlayerUtils.PLAYER_NEXT;
import static com.contplayer.engine.ContPlayerUtils.PLAYER_PREVIOUS;

public class ContPlayerView<T> extends FrameLayout {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private ContPlayerViewPagerAdapter<T> adapter;
    private ViewPager viewPager;
    private ContPlayerCommandsManager contPlayerCommandsManager;
    private IContPlayerQueue contPlayerQueue;
    private ViewBindListener viewBindListener;
    private LifecycleOwner lifecycleOwner;

    public ContPlayerView(Context context) {
        this(context, null, -1);
    }

    public ContPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ContPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
        init();
    }

    public ContPlayerView(Context context, LifecycleOwner lifecycleOwner) {
        super(context, null, -1);
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
        this.lifecycleOwner = lifecycleOwner;
        init();
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        if(lifecycleOwner != null) {
            LifecycleAwareContPlayerView lifecycleAwareContPlayerView = new LifecycleAwareContPlayerView();
            lifecycleAwareContPlayerView.wrap(this);
            lifecycleOwner.getLifecycle().addObserver(lifecycleAwareContPlayerView);
        }
    }

    private int getLayoutId() {
        return R.layout.cont_player_view;
    }

    void init() {
        View view = layoutInflater.inflate(getLayoutId(), this, false);
        viewBindListener = new ViewBindListener(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        adapter = new ContPlayerViewPagerAdapter();
        viewPager.setAdapter(adapter);
        VideoPageChangeListener videoPageChangeListener = new VideoPageChangeListener(this);
        viewPager.addOnPageChangeListener(videoPageChangeListener);
        addView(view);
        if(lifecycleOwner != null) {
            LifecycleAwareContPlayerView lifecycleAwareContPlayerView = new LifecycleAwareContPlayerView();
            lifecycleAwareContPlayerView.wrap(this);
            lifecycleOwner.getLifecycle().addObserver(lifecycleAwareContPlayerView);
        }
    }

    public void setContPlayerCommandsManager(ContPlayerCommandsManager contPlayerCommandsManager) {
        this.contPlayerCommandsManager = contPlayerCommandsManager;
        this.contPlayerCommandsManager.initiateContPlayer(viewBindListener);
    }

    public void setPlayerQueue(IContPlayerQueue iContPlayerQueue) {
        this.contPlayerQueue = iContPlayerQueue;
        adapter.setObjectArrayList(iContPlayerQueue.getArrayList());
        adapter.notifyDataSetChanged();
    }

    void move(@ContPlayerUtils.MOTION_DIRECTION int dir) {
        contPlayerCommandsManager.moveInProgress(dir);
    }

    void settle() {
        if(viewPager.getCurrentItem() == contPlayerQueue.getCurrentIndex()) {
            contPlayerCommandsManager.settle();
        } else if(viewPager.getCurrentItem() > contPlayerQueue.getCurrentIndex()) {
            contPlayerCommandsManager.moveNext();
        } else {
            contPlayerCommandsManager.movePrev();
        }
    }


    private static class VideoPageChangeListener implements ViewPager.OnPageChangeListener {

        private WeakReference<ContPlayerView> contPlayerViewWeakReference;
        private int scrollState = ViewPager.SCROLL_STATE_IDLE;

        VideoPageChangeListener(ContPlayerView contPlayerView) {
            contPlayerViewWeakReference = new WeakReference<>(contPlayerView);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(scrollState != ViewPager.SCROLL_STATE_IDLE) {
                if (position + positionOffset > 0.0f) {
                    contPlayerViewWeakReference.get().move(ContPlayerUtils.IN_MOTION_NEXT_RIGHT);
                } else {
                    contPlayerViewWeakReference.get().move(ContPlayerUtils.IN_MOTION_PREV_LEFT);
                }
            } else {
                contPlayerViewWeakReference.get().settle();
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            scrollState = state;
            if(state == ViewPager.SCROLL_STATE_IDLE || state == ViewPager.SCROLL_STATE_SETTLING) {
                contPlayerViewWeakReference.get().settle();
            }
        }
    }

    private static class ViewBindListener implements IViewBindListener {

        private WeakReference<ContPlayerView> contPlayerViewWeakReference;

        ViewBindListener(ContPlayerView contPlayerView) {
            contPlayerViewWeakReference = new WeakReference<>(contPlayerView);
        }

        @Override
        public void onViewBind(int PLAYER_TYPE, SoloPlayer soloPlayer) {
            contPlayerViewWeakReference.get().onViewBind(PLAYER_TYPE, soloPlayer);
        }
    }

    void onViewBind(int playerType, SoloPlayer soloPlayer) {
        PlayerView playerView;
        switch (playerType) {
            case PLAYER_CURRENT:
                playerView = ViewPagerUtils.getCurrentView(viewPager).findViewById(R.id.player_view);
                soloPlayer.attachVideoView(playerView);
                break;
            case PLAYER_NEXT:
                playerView = ViewPagerUtils.getNextView(viewPager).findViewById(R.id.player_view);
                soloPlayer.attachVideoView(playerView);
                break;
            case PLAYER_PREVIOUS:
                playerView = ViewPagerUtils.getPreviousView(viewPager).findViewById(R.id.player_view);
                soloPlayer.attachVideoView(playerView);
                break;
        }
    }

    public void release() {
        if(contPlayerCommandsManager != null) {
            contPlayerCommandsManager.release();
        }
    }
}
