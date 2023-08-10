package org.foxesworld.engine.world.terrain;

import com.jme3.math.Vector3f;

public class WorldCoords{
    private final Vector3f scale;
    private final Vector3f translation;

    public WorldCoords(Vector3f scale, Vector3f translation) {
        this.scale = scale;
        this.translation = translation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f getTranslation() {
        return translation;
    }
}