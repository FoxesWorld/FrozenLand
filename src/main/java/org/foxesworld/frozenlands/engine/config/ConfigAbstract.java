package org.foxesworld.frozenlands.engine.config;

import org.apache.logging.log4j.Logger;
import org.foxesworld.cfgProvider.CfgProvider;

import java.util.Map;

public abstract class ConfigAbstract {

    private String cfgFileExtension = "";

    protected void addCfgFiles(String[] configFiles){
        for(String cfgUnit: configFiles){
            String cfgFileName = cfgUnit + cfgFileExtension;
            new CfgProvider(cfgFileName);
            System.out.println("Adding " + cfgFileName + " to CONFIG");
        }
    }

    protected void setDirPathIndex(int index){
        CfgProvider.setBaseDirPathIndex(index);
    }

    protected  void setCfgExportDir(String dir){
        CfgProvider.setCfgExportDirName(dir);
    }

    protected void  setLogger(Logger LOGGER) { CfgProvider.setLOGGER(LOGGER);}

    protected void setCfgFileExtension(String ext){
        this.cfgFileExtension = ext;
        CfgProvider.setCfgFileExtension(ext);
    }
    protected Map<String, Map<String, Object>> getAllCfgMaps(){
        return CfgProvider.getAllCfgMaps();
    }
}
