package org.foxesworld.engine.world.water;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class WaterPhysicsControl extends RigidBodyControl {

    private float waterLevel;

    public WaterPhysicsControl(float waterLevel, BulletAppState bulletAppState) {
        super(0);
        this.waterLevel = waterLevel;
        bulletAppState.getPhysicsSpace().add(this);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        adjustForWater();
    }

    private void adjustForWater() {
        if (spatial != null) {
            Vector3f location = spatial.getLocalTranslation();
            if (location.y < waterLevel) {
                location.y = waterLevel;
                setPhysicsLocation(location);
            }
        }
    }
}