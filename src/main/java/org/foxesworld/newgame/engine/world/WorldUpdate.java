package org.foxesworld.newgame.engine.world;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import javax.sound.sampled.Control;

public class WorldUpdate extends AbstractControl {

    @Override
    protected void controlUpdate(float v) {
        System.out.println("Updated " + v);
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }
}
