package com.contplayer.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.contplayer.R;
import com.contplayer.engine.ContPlayerCommandsManager;
import com.contplayer.engine.IContPlayerQueue;
import com.contplayer.ui.ContPlayerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContPlayerView contPlayerView = findViewById(R.id.contPlayerView);
        contPlayerView.setLifecycleOwner(this);
        IContPlayerQueue contPlayerQueue = getStreamArrayList();
        ContPlayerCommandsManager contPlayerCommandsManager = new ContPlayerCommandsManager(this, contPlayerQueue);
        //contPlayerCommandsManager.setPlayerQueue(contPlayerQueue);
        contPlayerView.setContPlayerCommandsManager(contPlayerCommandsManager);
        contPlayerView.setPlayerQueue(contPlayerQueue);
    }

    public DemoPlayerQueue getStreamArrayList() {
        DemoPlayerQueue demoPlayerQueue = new DemoPlayerQueue();
        ArrayList<String> streamUrlList = new ArrayList<>();
        streamUrlList.add("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8");
        streamUrlList.add("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8");
        streamUrlList.add("https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8");
        streamUrlList.add("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
        streamUrlList.add("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");
        streamUrlList.add("https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8");
        streamUrlList.add("https://moctobpltc-i.akamaihd.net/hls/live/571329/eight/playlist.m3u8");
        demoPlayerQueue.setArrayList(streamUrlList);
        return demoPlayerQueue;
    }
}
