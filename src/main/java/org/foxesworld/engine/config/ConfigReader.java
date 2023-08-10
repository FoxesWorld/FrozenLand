package org.foxesworld.engine.config;

import java.util.Map;

public class ConfigReader extends ConfigAbstract {
    public ConfigReader(String[] configFiles) {
        setCfgExportDir("assets/cfg");
        setDirPathIndex(0);
        setCfgFileExtension(".json");
        addCfgFiles(configFiles);
    }

    public Map<String, Map> getCfgMaps() {
        return getAllCfgMaps();
    }
}
