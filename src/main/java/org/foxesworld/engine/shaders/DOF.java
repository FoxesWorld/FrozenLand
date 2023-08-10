package org.foxesworld.engine.shaders;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;

public class DOF {

    private FilterPostProcessor fpp;
    private  DepthOfFieldFilter dof;

    public DOF(FilterPostProcessor fpp) {
        this.fpp = fpp;
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
