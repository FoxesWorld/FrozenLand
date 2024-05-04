package org.foxesworld.frozenlands.engine.shaders;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;
import org.foxesworld.frozenlands.engine.Kernel;

public class DOF extends Shaders {

    private FilterPostProcessor fpp;
    private  DepthOfFieldFilter dof;

    public DOF(Kernel kernel) {
        super(kernel);
        this.fpp = kernel.getFpp();
        this.dof = new DepthOfFieldFilter();
    }

    public void setFocusDistance(int focus){
        this.dof.setFocusDistance(focus);
    }

    public void setFocusRange(int range){
        this.dof.setFocusRange(range);
    }

    public void compile(){
        fpp.addFilter(dof);
    }
}
