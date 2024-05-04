package org.foxesworld.frozenlands.engine.shaders;

import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import org.foxesworld.frozenlands.engine.Kernel;

public class LSF extends  Shaders {

    LightScatteringFilter lsf;
    FilterPostProcessor fpp;

    public LSF(Kernel kernel, Vector3f lightDir) {
        super(kernel);
        this.fpp = kernel.getFpp();
        lsf = new LightScatteringFilter(lightDir);
    }

    public void setLightDensity(float destiny){
        lsf.setLightDensity(destiny);
    }

    public void compile(){
        this.fpp.addFilter(lsf);
    }
}
