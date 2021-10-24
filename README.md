# ContPlayer
ContPlayer enables developers to implement the smooth experience of video/audio playing in a easy swipe style which is famous in TikTok and Reels kind of applications.

This implementation is designed into 2 modules.
a) library-core : Handles core play-pause functionality of playing videos and maintain the player states on smooth scroll.

b) library-ui : it offers the default customisable UI, implemented using ViewPager and displays the video items which are added into the player queue.

Step by Step guide to implement this code into your app :

1. Clone this repository.
2. To use core part of ContPlayer,
'''
implementation project(':library-core')
'''

To use ui part as well :
implementation project(':library-ui')

3. Add compile options into the build.gradle
compileOptions {
    sourceCompatibility 1.8
    targetCompatibility 1.8
}



4. Create a class which will implement the interface IContPlayerQueue<T> and put the definitions of required functions.

5. Initiate the ContPlayer, IContPlayerQueue, ContPlayerCommandsanager in the below manner,

a) Init Object of ContPlayerView and set lifecycle owner as current activity or fragment
    ContPlayerView contPlayerView = findViewById(R.id.contPlayerView);
    contPlayerView.setLifecycleOwner(this);
    
b) Get the PlayerQueue feeded with player items     
     IContPlayerQueue contPlayerQueue = getStreamArrayList();
     
c) Initiate ContPlayerCommandsManager, it will take play, pause, release etc commands 
     ContPlayerCommandsManager contPlayerCommandsManager = new ContPlayerCommandsManager(this, contPlayerQueue);
     
d) set everything to ContPlayerView object
     contPlayerView.setContPlayerCommandsManager(contPlayerCommandsManager);
     contPlayerView.setResizeMode(Const.RESIZE_MODE_FILL);
     contPlayerView.setPlayerQueue(contPlayerQueue);
        
e) start play of ContPlayer
      new Handler().postDelayed(() -> contPlayerView.startPlay(), 300);
      
      
p.s. For any implementation doubts refer demo app, available in the same repository.

