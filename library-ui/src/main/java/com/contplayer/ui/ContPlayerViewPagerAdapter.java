package com.contplayer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.library_ui.R;

import java.util.ArrayList;

public class ContPlayerViewPagerAdapter<T> extends PagerAdapter {

    ArrayList<T> objectArrayList;
    OnVideoItemClilckListener onVideoItemClilckListener;

    ContPlayerViewPagerAdapter(OnVideoItemClilckListener onVideoItemClilckListener) {
        this.onVideoItemClilckListener = onVideoItemClilckListener;
    }

    ContPlayerViewPagerAdapter(ArrayList<T> objectList) {
        this.objectArrayList = objectList;
    }

    void setObjectArrayList(ArrayList<T> objectArrayList) {
        this.objectArrayList = objectArrayList;
    }

    private int getItemLayoutId() {
        return R.layout.pager_item;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());
        final View view = layoutInflater.inflate(getItemLayoutId(), container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onVideoItemClilckListener != null) {
                    onVideoItemClilckListener.onVideoItemClicked(view);
                }
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return objectArrayList != null ? objectArrayList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
