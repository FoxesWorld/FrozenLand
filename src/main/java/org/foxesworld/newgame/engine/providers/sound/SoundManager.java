package org.foxesworld.newgame.engine.providers.sound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import org.foxesworld.newgame.engine.Kernel;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SoundManager {
    private AssetManager assetManager;
    private  Map<String, Map<String, List<AudioNode>>> Sounds = new HashMap<>();

    public SoundManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        loadSounds(assetManager.locateAsset(new AssetKey<>("sounds.json")).openStream());
    }

    private void loadSounds(InputStream inputStream) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonRoot = objectMapper.readTree(inputStream);
            Iterator<String> iterator = jsonRoot.fieldNames();
            while(iterator.hasNext()) {
                String currentBlock = iterator.next();
                Kernel.logger.info("====== Scanning block " +currentBlock + " ======");
                JsonNode eventsArray = jsonRoot.get(currentBlock);
                Map<String, List<AudioNode>> soundBlock = new HashMap<>();
                eventsArray.forEach(eventNode -> {
                    AtomicInteger soundsNum = new AtomicInteger();
                    String event = eventNode.get("event").asText();
                    String sndPackage = (eventNode.has("package")) ? eventNode.get("package").asText() : "";
                    JsonNode settingsNode = eventNode.get("settings");
                    List<AudioNode> audioNodes = new ArrayList<>();
                    JsonNode soundsArray = eventNode.get("sounds");
                    soundsArray.forEach(soundNode -> {

                        String fileName = soundNode.asText();
                        AudioData.DataType dataType = AudioData.DataType.valueOf(settingsNode.get("dataType").asText());
                        String filePath = "sounds/" + currentBlock + '/' + sndPackage + event + fileName;
                        AudioNode audioNode = new AudioNode(assetManager, filePath, dataType);

                        if (settingsNode != null) {
                            if (settingsNode.has("volume")) {
                                audioNode.setVolume((float) settingsNode.get("volume").asDouble());
                            }
                            if (settingsNode.has("positional")) {
                                audioNode.setPositional(settingsNode.get("volume").asBoolean(false));
                            }
                            if (settingsNode.has("pitch")) {
                                audioNode.setPitch((float) settingsNode.get("pitch").asDouble());
                            }
                        }
                        audioNodes.add(audioNode);
                        soundsNum.getAndIncrement();
                    });
                    Kernel.logger.info("Added " + soundsNum + " sounds to '" + event + "' event");
                    soundBlock.put(event, audioNodes);
                });
                Sounds.put(currentBlock, soundBlock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Kernel.logger.info("Sound scanning done!");
    }

    public Map<String, List<AudioNode>> getSoundBlock(String blockName){
        return Sounds.get(blockName);
    }

    public void update(float tpf) {
        for (Map.Entry<String, Map<String, List<AudioNode>>> entry : Sounds.entrySet()) {
            for(Map.Entry<String, List<AudioNode>> update: entry.getValue().entrySet()) {
                List<AudioNode> audioNodes = update.getValue();
                Iterator<AudioNode> iterator = audioNodes.iterator();

                while (iterator.hasNext()) {
                    AudioNode audioNode = iterator.next();
                    boolean removeAfterPlayback = audioNode.getUserData("removeAfterPlayback") != null ? audioNode.getUserData("removeAfterPlayback") : false;

                    if (removeAfterPlayback && audioNode.getStatus() == AudioSource.Status.Stopped) {
                        audioNode.removeFromParent();
                        iterator.remove();
                    }
                    float cooldown = audioNode.getUserData("cooldown") != null ? audioNode.getUserData("cooldown") : -1f;
                    if (cooldown > 0) {
                        cooldown -= tpf;
                        if (cooldown <= 0) {
                            audioNode.setUserData("cooldown", null);
                        } else {
                            audioNode.setUserData("cooldown", cooldown);
                        }
                    }
                }
            }
        }
    }

    public Map<String, Map<String, List<AudioNode>>> getSounds() {
        return Sounds;
    }
}
