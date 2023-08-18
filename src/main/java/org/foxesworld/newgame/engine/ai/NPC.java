package org.foxesworld.newgame.engine.ai;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.foxesworld.newgame.engine.player.Player;

public class NPC {
    private Spatial model;
    private float speed = 3f;
    private NPCAI npcAI;

    public NPC(Spatial model) {
        this.model = model;
        this.npcAI = new NPCAI(this);
    }

    public Spatial getModel() {
        return model;
    }

    public float getSpeed() {
        return speed;
    }

    public void move(Vector3f direction) {
        model.move(direction);
    }

    public void faceDirection(Vector3f direction) {
        Quaternion rotation = new Quaternion();
        rotation.lookAt(direction, Vector3f.UNIT_Y);
        model.getLocalRotation().slerp(rotation, 0.1f);
    }

    public void attack(Player player) {
        // Логика атаки на игрока
    }

    public NPCAI getNpcAI() {
        return npcAI;
    }
}
