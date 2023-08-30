package org.foxesworld.newgame.engine.shaders;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.DirectionalLightShadowFilter;
import org.foxesworld.newgame.engine.KernelInterface;

public class Shaders extends BaseAppState {
    private FilterPostProcessor fpp;
    private  KernelInterface kernelInterface;
    private AssetManager assetManager;
    private ViewPort viewPort;
    private  LSF lsf;
    private Bloom bloom;
    private DOF dof;

    public Shaders(KernelInterface kernelInterface){
        this.kernelInterface = kernelInterface;
        this.fpp = kernelInterface.getFpp();
        this.assetManager = kernelInterface.getAssetManager();
        this.viewPort = kernelInterface.getViewPort();

    }
    @Override
    protected void initialize(Application application) {
        bloom = new Bloom(kernelInterface);
        bloom.setBloom(1.0f);
        bloom.setExposurePover(60);
        bloom.compile();

        //Light Scattering Filter
        lsf = new LSF(kernelInterface, new Vector3f(-4.9236743f, -1.27054665f, 5.896916f).mult(-300));
        lsf.setLightDensity(0.5f);
        lsf.compile();

        //Depth of field Filter
        dof = new DOF(kernelInterface);
        dof.setFocusDistance(0);
        dof.setFocusRange(100);
        dof.compile();

        fpp.addFilter(new FXAAFilter());
        viewPort.addProcessor(fpp);

        //Shadows EXP
        FilterPostProcessor processor = new FilterPostProcessor(assetManager);
        DirectionalLightShadowFilter filter = new DirectionalLightShadowFilter(assetManager, 2048, 1);
        filter.setLight(kernelInterface.getSky().getSun());
        processor.addFilter(filter);
        viewPort.addProcessor(processor);
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

    public LSF getLsf() {
        return lsf;
    }

    public Bloom getBloom() {
        return bloom;
    }

    public DOF getDof() {
        return dof;
    }
}
