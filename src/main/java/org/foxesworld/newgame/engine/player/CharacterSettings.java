package org.foxesworld.newgame.engine.player;

import org.foxesworld.newgame.engine.player.input.PlayerState;

public class CharacterSettings {
    private PlayerState playerState = PlayerState.STANDING;
    private float currentSpeed = 0.0f;
    private float walkSpeed = 4.0f;
    private float runSpeed = 8.0f;
    private float maxSmoothSpeedChange = 2.0f;
    private float rotationSpeed = 0.5f;
    private float rotationMultiplierWalking = 0.04f;
    private float rotationMultiplierRunning = 0.1f;
    private float isInAir = 0;
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

    public boolean isJumping() {
        return isJumping;
    }

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

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }
}
