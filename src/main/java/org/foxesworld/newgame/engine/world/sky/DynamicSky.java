package org.foxesworld.newgame.engine.world.sky;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import org.foxesworld.newgame.engine.world.sky.items.DynamicSkyBackground;
import org.foxesworld.newgame.engine.world.sky.items.DynamicStars;
import org.foxesworld.newgame.engine.world.sky.items.DynamicSun;
import org.foxesworld.newgame.engine.world.sky.utils.SkyBillboardItem;

public class DynamicSky extends DirectionalLight {
    private ViewPort viewPort = null;
    private AssetManager assetManager = null;

    private DynamicSun dynamicSun = null;
    private DynamicStars dynamicStars = null;
    private DynamicSkyBackground dynamicBackground = null;

    private float scaling = 900;

    public DynamicSky(AssetManager assetManager, ViewPort viewPort, Node rootNode) {
        //super("Sky");
        this.assetManager = assetManager;
        this.viewPort = viewPort;

        dynamicSun = new DynamicSun(assetManager, viewPort, rootNode, scaling);
        rootNode.attachChild(dynamicSun);

        dynamicStars = new DynamicStars(assetManager, viewPort, scaling);
        rootNode.attachChild(dynamicStars);

        dynamicBackground = new DynamicSkyBackground(assetManager, viewPort, rootNode);
    }

    public Vector3f getSunDirection(){
        return dynamicSun.getSunDirection();
    }

    public SkyBillboardItem getSun(){
        return dynamicSun.getSun();
    }

    public void updateTime(){
        dynamicSun.updateTime();
        dynamicBackground.updateLightPosition(dynamicSun.getSunSystem().getPosition());
        dynamicStars.update(dynamicSun.getSunSystem().getDirection());
        dynamicStars.lookAt(dynamicSun.getSunSystem().getPosition(), Vector3f.ZERO);
    }

}
