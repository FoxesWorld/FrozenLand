package org.foxesworld.newgame.engine.config;

import java.util.Map;

public class ConfigReader extends ConfigAbstract {
    public ConfigReader(String[] configFiles) {
        setCfgExportDir("config");
        setDebug(true);
        setDirPathIndex(0);
        setCfgFileExtension(".json");
        addCfgFiles(configFiles);
    }

    public Map<String, Map> getCfgMaps() {
        return getAllCfgMaps();
    }
}
