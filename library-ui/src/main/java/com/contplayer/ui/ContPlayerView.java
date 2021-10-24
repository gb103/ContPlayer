package com.contplayer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPagerUtils;

import com.contplayer.engine.ContPlayer;
import com.contplayer.engine.ContPlayerCommandsManager;
import com.contplayer.engine.IContPlayerQueue;
import com.contplayer.engine.IViewBindListener;
import com.contplayer.engine.SoloPlayer;
import com.contplayer.engine.ContPlayerUtils;
import com.google.android.exoplayer2.ui.PlayerView;
import com.library_ui.R;

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
    private boolean isPlaying;

    public ContPlayerView(Context context) {
        this(context, null, -1);
    }

    public ContPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    class ContPlayerViewVideoItemClickListener implements OnVideoItemClilckListener {

        WeakReference<ContPlayerView> contPlayerViewWeakReference;

        ContPlayerViewVideoItemClickListener(ContPlayerView contPlayerView) {
            this.contPlayerViewWeakReference = new WeakReference<>(contPlayerView);
        }

        @Override
        public void onVideoItemClicked(View view) {
            ContPlayerView contPlayerView = contPlayerViewWeakReference.get();
            if(contPlayerView != null) {
                contPlayerView.togglePlay(view);
            }
        }
    }


    /**
     * when play-pause is clicked from the UI
     * @param view
     */
    private void togglePlay(View view) {
        ImageView playIcon = (ImageView) view.findViewById(R.id.play_icon);
        if(isPlaying) {
            contPlayerCommandsManager.pause();
            isPlaying = false;
        } else {
            contPlayerCommandsManager.play();
            isPlaying = true;
        }
        if(playIcon != null) {//show play  button when made the video pauses, else make visibility gone when
            playIcon.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        }
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

    /**
     * set lifecycle owner to dispose video view
     * @param lifecycleOwner
     */
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
        ContPlayerViewVideoItemClickListener contPlayerViewVideoItemClickListener = new ContPlayerViewVideoItemClickListener(this);
        adapter = new ContPlayerViewPagerAdapter(contPlayerViewVideoItemClickListener);
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

    /**
     * set player queue of the media urls
     * @param iContPlayerQueue
     */
    public void setPlayerQueue(IContPlayerQueue iContPlayerQueue) {
        this.contPlayerQueue = iContPlayerQueue;
        adapter.setObjectArrayList(iContPlayerQueue.getArrayList());
        adapter.notifyDataSetChanged();
    }

    void move(@ContPlayerUtils.MOTION_DIRECTION int dir) {
        contPlayerCommandsManager.moveInProgress(dir);
    }

    /**
     * when setup the ContPlayer and feed it with queue then on first launch to start play this should be called
     */
    public void startPlay() {
        settle();
    }

    /**
     * when scroll settles then take the decision
     */
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
        View pagerView;
        switch (playerType) {
            case PLAYER_CURRENT:
                pagerView = ViewPagerUtils.getCurrentView(viewPager);
                if(pagerView != null) {
                    playerView = pagerView.findViewById(R.id.player_view);
                    soloPlayer.attachVideoView(playerView);
                }
                break;
            case PLAYER_NEXT:
                pagerView = ViewPagerUtils.getNextView(viewPager);
                if(pagerView != null) {
                    playerView = pagerView.findViewById(R.id.player_view);
                    soloPlayer.attachVideoView(playerView);
                }
                break;
            case PLAYER_PREVIOUS:
                pagerView = ViewPagerUtils.getPreviousView(viewPager);
                if(pagerView != null) {
                    playerView = pagerView.findViewById(R.id.player_view);
                    soloPlayer.attachVideoView(playerView);
                }
                break;
        }
    }

    /**
     * release commands manager
     */
    public void release() {
        if(contPlayerCommandsManager != null) {
            contPlayerCommandsManager.release();
        }
    }
}
