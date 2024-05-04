package org.foxesworld.frozenlands;

import codex.j3map.J3mapFactory;
import codex.j3map.processors.BooleanProcessor;
import codex.j3map.processors.FloatProcessor;
import codex.j3map.processors.IntegerProcessor;
import codex.j3map.processors.StringProcessor;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.frozenlands.engine.Kernel;
import org.foxesworld.frozenlands.engine.config.ConfigReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public class FrozenLands extends SimpleApplication implements FrozenLandsInterface {

    private BulletAppState bulletAppState;
    private FilterPostProcessor fpp;
    private Map CONFIG;
    private int numSamples;

    public static final Logger logger =  LogManager.getLogger(FrozenLands.class);

    public static void main(String[] args) {
        FrozenLands app = new FrozenLands();
        var cfg = new AppSettings(true);
        cfg.setVSync(false);
        cfg.setResolution(2560, 1440);
        cfg.setFullscreen(false);
        cfg.setSamples(16);
        cfg.setTitle("FrozenLands");
        app.setShowSettings(true);
        app.setDisplayFps(true);
        app.setDisplayStatView(false);
        app.setSettings(cfg);
        setIcon(cfg);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        CONFIG = new ConfigReader(new String[]{"userInput", "engine"}).getCfgMaps();
        GuiGlobals.initialize(this);
        numSamples = getContext().getSettings().getSamples();
        assetManager.registerLoader(J3mapFactory.class, "j3map");
        J3mapFactory.registerAllProcessors(
                BooleanProcessor.class,
                StringProcessor.class,
                IntegerProcessor.class,
                FloatProcessor.class);

        bulletAppState = new BulletAppState();

        //bulletAppState.setDebugViewPorts(viewPort);
        //bulletAppState.setDebugEnabled(true);

        GuiGlobals globals = GuiGlobals.getInstance();
        BaseStyles.loadStyleResources("themes/medieval/medieval.groovy");
        globals.getStyles().setDefaultStyle("medieval");

        fpp = new FilterPostProcessor(assetManager);
        if (numSamples > 0) fpp.setNumSamples(numSamples);

        stateManager.attach(bulletAppState);
        stateManager.attach(new Kernel(this));
    }

    private static void setIcon(AppSettings settings) {
        try {
            BufferedImage[] icons = new BufferedImage[] {
                    ImageIO.read(FrozenLands.class.getResource( "/test64.png" )),
                    ImageIO.read(FrozenLands.class.getResource( "/test32.png" )),
                    ImageIO.read(FrozenLands.class.getResource( "/test16.png" ))
            };
            settings.setIcons(icons);
        } catch(IOException e) {}
    }
    @Override
    public BulletAppState getBulletAppState() {
        return this.bulletAppState;
    }
    @Override
    public FilterPostProcessor getFpp() {
        return this.fpp;
    }
    @Override
    public Map getCONFIG() {
        return this.CONFIG;
    }


}