package org.foxesworld.newgame;

import com.jme3.bullet.BulletAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import org.slf4j.Logger;

import java.util.Map;

public interface NewGameInterface {
    BulletAppState getBulletAppState();
    FilterPostProcessor getFpp();
    NiftyJmeDisplay getNifty();
    Map getCONFIG();
    Logger getLogger();
}
