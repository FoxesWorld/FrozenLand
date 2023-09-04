package org.foxesworld.frozenlands.engine.player;

import codex.j3map.J3map;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;

public class PlayerOptions {

    private BetterCharacterControl characterControl;
    private String modelPath;
    private float scale;
    private Camera fpsCam;
    private Spatial.CullHint cullHint;
    private RenderQueue.ShadowMode shadowMode;
    private Vector3f jumpForce;
    private int initialHealth;
    private float mass;

    public PlayerOptions(J3map options) {
        loadOptions(options);
    }

    private void loadOptions(J3map optionsMap) {
        try {
            this.modelPath = optionsMap.getString("model");
            this.scale = optionsMap.getFloat("scale");
            this.cullHint = Spatial.CullHint.valueOf(optionsMap.getString("cullHint"));
            this.shadowMode = RenderQueue.ShadowMode.valueOf(optionsMap.getString("shadowMode"));
            this.jumpForce = new Vector3f(0, optionsMap.getFloat("jumpForce"), 0);
            this.initialHealth = optionsMap.getInteger("initialHealth");
            this.mass = optionsMap.getFloat("mass");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getModelPath() {
        return modelPath;
    }

    public float getScale() {
        return scale;
    }

    public Spatial.CullHint getCullHint() {
        return cullHint;
    }

    public RenderQueue.ShadowMode getShadowMode() {
        return shadowMode;
    }

    public Vector3f getJumpForce() {
        return jumpForce;
    }

    public int getInitialHealth() {
        return initialHealth;
    }

    public float getMass() {
        return mass;
    }

    public Camera getFpsCam() {
        return fpsCam;
    }

    public void setFpsCam(Camera fpsCam) {
        this.fpsCam = fpsCam;
    }

    public BetterCharacterControl getCharacterControl() {
        return characterControl;
    }

    public void setCharacterControl(BetterCharacterControl characterControl) {
        this.characterControl = characterControl;
    }
}
