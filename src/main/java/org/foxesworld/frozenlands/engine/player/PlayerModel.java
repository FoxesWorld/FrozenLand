package org.foxesworld.frozenlands.engine.player;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class PlayerModel extends Node {

    private Spatial playerSpatial;

    public PlayerModel(AssetManager assetManager, PlayerOptions playerOptions) {
        playerSpatial = assetManager.loadModel(playerOptions.getModelPath());
        playerSpatial.setLocalScale(playerOptions.getScale());
        attachChild(playerSpatial);
    }

    public void setCullHint(Spatial.CullHint cullHint) {
        playerSpatial.setCullHint(cullHint);
    }

    public void setShadowMode(RenderQueue.ShadowMode shadowMode) {
        playerSpatial.setShadowMode(shadowMode);
    }

    public Spatial getPlayerSpatial() {
        return playerSpatial;
    }
}
