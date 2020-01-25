package com.contplayer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.library_core.R;

import java.util.ArrayList;

public class ContPlayerViewPagerAdapter<T> extends PagerAdapter {

    ArrayList<T> objectArrayList;

    ContPlayerViewPagerAdapter() {

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
        View view = layoutInflater.inflate(getItemLayoutId(), container, false);
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
