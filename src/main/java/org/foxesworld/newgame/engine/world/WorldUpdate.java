package org.foxesworld.newgame.engine.world;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import org.foxesworld.newgame.engine.KernelInterface;

public class WorldUpdate extends BaseAppState {
    private long time = 0;

    public WorldUpdate(KernelInterface kernelInterface){

    }

    @Override
    protected void initialize(Application application) {

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
