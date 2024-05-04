package org.foxesworld.frozenlands.engine.world;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import org.foxesworld.frozenlands.engine.Kernel;
import org.foxesworld.frozenlands.engine.world.effect.SnowfallEffect;

public class WorldUpdate extends BaseAppState {
    private long time = 0;
    private Kernel kernel;

    public WorldUpdate(Kernel kernel){
        this.kernel = kernel;
    }

    @Override
    protected void initialize(Application application) {
        SnowfallEffect snowfallEffect = new SnowfallEffect(kernel);
        snowfallEffect.setLocalTranslation(0, 10, 0);
        kernel.getRootNode().attachChild(snowfallEffect);
    }

    @Override
    protected void cleanup(Application application) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void update(float tpf){
        time+=1;
        if(time % 800 == 0) {
            //System.out.println("Second");
            time = 0;
        }
    }
}
