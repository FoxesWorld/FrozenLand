package org.foxesworld.newgame;

import codex.j3map.J3mapFactory;
import codex.j3map.processors.BooleanProcessor;
import codex.j3map.processors.FloatProcessor;
import codex.j3map.processors.IntegerProcessor;
import codex.j3map.processors.StringProcessor;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import org.foxesworld.newgame.engine.Kernel;
import org.foxesworld.newgame.engine.config.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public class NewGame extends SimpleApplication implements NewGameInterface {

    /* TODO
     *  Replace nifty with Lemur
     * */

    private BulletAppState bulletAppState;
    private FilterPostProcessor fpp;
    private Map CONFIG;
    private int numSamples;

    private final Logger logger = LoggerFactory.getLogger(NewGame.class);

    public static void main(String[] args) {
        NewGame app = new NewGame();
        var cfg = new AppSettings(true);
        cfg.setVSync(false);
        cfg.setResolution(1360, 768);
        cfg.setFullscreen(false);
        cfg.setSamples(16);
        cfg.setTitle("FrozenLand");
        app.setShowSettings(true);
        app.setDisplayFps(true);
        app.setDisplayStatView(false);
        app.setSettings(cfg);
        setIcon(cfg);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        CONFIG = new ConfigReader(new String[]{"userInput", "internal/sounds"}).getCfgMaps();
        GuiGlobals.initialize(this);
        numSamples = getContext().getSettings().getSamples();
        assetManager.registerLoader(J3mapFactory.class, "j3map");
        J3mapFactory.registerAllProcessors(
                BooleanProcessor.class,
                StringProcessor.class,
                IntegerProcessor.class,
                FloatProcessor.class);

        bulletAppState = new BulletAppState();

        //bulletapp.setDebugViewPorts(viewPort);
        //bulletapp.setDebugEnabled(true);

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
                    ImageIO.read(NewGame.class.getResource( "/test64.png" )),
                    ImageIO.read(NewGame.class.getResource( "/test32.png" )),
                    ImageIO.read(NewGame.class.getResource( "/test16.png" ))
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
    @Override
    public Logger getLogger() {
        return this.logger;
    }


}