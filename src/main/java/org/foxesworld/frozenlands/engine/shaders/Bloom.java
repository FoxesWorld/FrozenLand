package org.foxesworld.frozenlands.engine.shaders;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import org.foxesworld.frozenlands.engine.KernelInterface;

public class Bloom extends Shaders {

    private BloomFilter bloom;
    private  FilterPostProcessor fpp;

    public Bloom(KernelInterface kernelInterface) {
        super(kernelInterface);
        this.fpp = kernelInterface.getFpp();
        bloom = new BloomFilter();
    }

    public void setBloom(float intense) {
        bloom.setBloomIntensity(intense);
    }

    public void setExposurePover(int exposurePover) {
        bloom.setExposurePower(exposurePover);
    }

    public void compile(){
        this.fpp.addFilter(bloom);
    }
}
