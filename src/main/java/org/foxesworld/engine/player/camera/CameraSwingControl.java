package org.foxesworld.engine.player.camera;

import com.jme3.input.controls.ActionListener;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class CameraSwingControl extends AbstractControl implements ActionListener {
    private Camera cam;
    private float maxSwingAngle = 0.02f; // Максимальный угол покачивания камеры (в радианах)
    private float currentSwingAngle = 0f;
    private float swingSpeed = 0.01f; // Скорость покачивания камеры

    public CameraSwingControl(Camera cam) {
        this.cam = cam;
    }

    @Override
    protected void controlUpdate(float tpf) {
        // Применяем покачивание камеры
        if (isEnabled()) {
            currentSwingAngle = (float) Math.sin(2 * Math.PI * swingSpeed * tpf);
            cam.getDirection().addLocal(currentSwingAngle * maxSwingAngle, 0, 0);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Ничего не делаем
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        // Ничего не делаем
    }
}
