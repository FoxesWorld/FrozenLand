package org.foxesworld.engine.shaders;

import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;

public class LSF {

    LightScatteringFilter lsf;
    FilterPostProcessor fpp;

    public LSF(FilterPostProcessor fpp, Vector3f lightDir) {
        this.fpp = fpp;
        lsf = new LightScatteringFilter(lightDir);
    }

    public void setLightDensity(float destiny){
        lsf.setLightDensity(destiny);
    }

    public void compile(){
        this.fpp.addFilter(lsf);
    }
}
