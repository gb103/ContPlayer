package com.contplayer.engine;

import com.contplayer.utility.ContPlayerUtils;

public interface IViewBindListener {

    public void onViewBind(@ContPlayerUtils.PLAYER_TYPE int PLAYER_TYPE, SoloPlayer soloPlayer);

}
