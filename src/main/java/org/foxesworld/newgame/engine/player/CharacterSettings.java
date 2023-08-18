package org.foxesworld.newgame.engine.player;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class CharacterSettings {
    private float currentSpeed = 0.0f;
    private float walkSpeed = 4.0f;
    private float runSpeed = 8.0f;
    private float maxSmoothSpeedChange = 2.0f;
    private float rotationMultiplierWalking = 0.04f;
    private float rotationMultiplierRunning = 0.1f;
    private boolean isJumping = false;
    private boolean isAttacking = false;
    private boolean isRunning = false;

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public float getRunSpeed() {
        return runSpeed;
    }

    public float getMaxSmoothSpeedChange() {
        return maxSmoothSpeedChange;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public boolean isJumping() {return isJumping;}

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public float getPlayerDistanceAboveGround(Spatial spatial) {
        Vector3f characterPosition = spatial.getWorldTranslation();
        float characterHeight = 2.0f; // Подставьте фактическую высоту Box персонажа
        float distanceAboveGround = characterPosition.y - characterHeight * 0.5f;
        return distanceAboveGround;
    }

    public float getRotationMultiplierWalking() {
        return rotationMultiplierWalking;
    }

    public float getRotationMultiplierRunning() {
        return rotationMultiplierRunning;
    }
}
