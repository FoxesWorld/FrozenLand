package org.foxesworld.newgame.engine.player.camera;

import com.jme3.input.controls.ActionListener;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class CameraSwingControl extends AbstractControl implements ActionListener {
    private Camera cam;
    private ShakeCam camSwing;
    private float maxSwingAngle = 2f; // Максимальный угол покачивания камеры (в радианах)
    private float currentSwingAngle = 0f;
    private float swingSpeed = 10f; // Скорость покачивания камеры

    public CameraSwingControl(Camera cam) {
        this.cam = cam;
        this.camSwing =new ShakeCam(cam);
    }

    @Override
    protected void controlUpdate(float tpf) {
        // Применяем покачивание камеры
        //if (isEnabled()) {
            currentSwingAngle = (float) Math.sin(2 * Math.PI * swingSpeed * tpf);
            cam.getDirection().addLocal(currentSwingAngle * maxSwingAngle, 0, 0);
        float shakeDuration =0.5f;
        //period in milis, period/shake change witin duration. It should be much less than shakeDuration
        float period = 0.1f;
        //frequency
        int frequency = 20;
        // how much you want to shake
        float amplitude = 2f;
        //if the shake should decay as it expires
        boolean falloff=true;
        //if the shake is allways of the same value and thus doesnt change cam position. True for less real, but stable shake
        boolean  equalShake =false;
        //shake direction - predefined
        int directon=ShakeCam.SHAKE_DIR_ALL;
        camSwing.shake(shakeDuration,period, frequency, amplitude, falloff, equalShake, directon);
        //}
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
