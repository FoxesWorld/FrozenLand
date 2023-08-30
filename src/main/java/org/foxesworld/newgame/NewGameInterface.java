package org.foxesworld.newgame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import org.slf4j.Logger;

import java.util.Map;

public interface NewGameInterface {
    BulletAppState getBulletAppState();
    AssetManager getAssetManager();
    ViewPort getViewPort();
    FilterPostProcessor getFpp();
    Map getCONFIG();
    Logger getLogger();
}
