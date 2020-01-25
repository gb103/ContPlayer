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
        streamUrlList.add("https://mnmedias.api.telequebec.tv/m3u8/29880.m3u8");
        streamUrlList.add("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
        streamUrlList.add("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");
        demoPlayerQueue.setArrayList(streamUrlList);
        return demoPlayerQueue;
    }
}
