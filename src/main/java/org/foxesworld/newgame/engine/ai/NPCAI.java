package org.foxesworld.newgame.engine.ai;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class NPCAI extends AbstractControl {
    private NPC npc;
    private Vector3f targetPosition;
    private float moveSpeed = 1.0f;

    public NPCAI(NPC npc) {
        this.npc = npc;
    }

    public void setTargetPosition(Vector3f targetPosition) {
        this.targetPosition = targetPosition;
    }

    public void update(float tpf) {
        if (targetPosition != null) {
            Vector3f npcPosition = npc.getModel().getLocalTranslation();
            Vector3f direction = targetPosition.subtract(npcPosition).normalizeLocal();

            // Движение NPC к целевой позиции
            npcPosition.addLocal(direction.mult(moveSpeed * tpf));
            npc.getModel().setLocalTranslation(npcPosition);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        System.out.println("GG");
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }
}
