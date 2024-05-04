package org.foxesworld.frozenlands.engine.config;

import org.foxesworld.frozenlands.FrozenLands;

import java.util.Map;

public class ConfigReader extends ConfigAbstract {
    public ConfigReader(String[] configFiles) {
        setLogger(FrozenLands.logger);
        setCfgExportDir("config");
        setDirPathIndex(0);
        setCfgFileExtension(".json");
        addCfgFiles(configFiles);
    }
    public Map<String, Map<String, Object>> getCfgMaps() {
        return getAllCfgMaps();
    }
}
