package org.foxesworld.engine.shaders;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;

public class Bloom {

    private BloomFilter bloom;
    private  FilterPostProcessor fpp;

    public Bloom(FilterPostProcessor fpp) {
        this.fpp = fpp;
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
