package com.osurestream;

import com.osurestream.engine.AppBase;
import com.osurestream.game.AppDirector;

public class OsuRestream {
    public static void main(String[] args) {
        AppBase.run(new AppDirector());
    }
}