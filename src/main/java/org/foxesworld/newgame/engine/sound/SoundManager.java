package org.foxesworld.newgame.engine.sound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SoundManager {

    private Map<String, List<AudioNode>> soundMap = new HashMap<>();
    private AssetManager assetManager;
    private Random random = new Random();

    public SoundManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        loadSounds(assetManager.locateAsset(new AssetKey<>("sounds.json")).openStream());
    }

    private void loadSounds(InputStream inputStream) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            JsonNode eventsArray = rootNode.get("events");
            eventsArray.forEach(eventNode -> {
                String event = eventNode.get("event").asText();
                JsonNode settingsNode = eventNode.get("settings");

                List<AudioNode> audioNodes = new ArrayList<>();
                JsonNode soundsArray = eventNode.get("sounds");
                soundsArray.forEach(soundNode -> {
                    String fileName = soundNode.asText();
                    AudioData.DataType dataType = AudioData.DataType.valueOf(settingsNode.get("dataType").asText());
                    AudioNode audioNode = new AudioNode(assetManager, "sounds/" + fileName, dataType);
                    audioNode.setPositional(false);

                    if (settingsNode != null) {
                        if (settingsNode.has("volume")) {
                            audioNode.setVolume((float) settingsNode.get("volume").asDouble());
                        }
                        if (settingsNode.has("pitch")) {
                            audioNode.setPitch((float) settingsNode.get("pitch").asDouble());
                        }
                    }

                    audioNodes.add(audioNode);
                });

                soundMap.put(event, audioNodes);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AudioNode getRandomAudioNode(String event) {
        List<AudioNode> audioNodes = soundMap.get(event);
        if (audioNodes != null && !audioNodes.isEmpty()) {
            int randomIndex = random.nextInt(audioNodes.size());
            return audioNodes.get(randomIndex);
        }
        return null;
    }


    public void playSound(String event, float cooldown) {
        AudioNode audioNode = getRandomAudioNode(event);
        if (audioNode != null) {
            audioNode.playInstance();
            audioNode.setUserData("removeAfterPlayback", true);

            if (cooldown > 0) {
                audioNode.setUserData("cooldown", cooldown);
            }
        }
    }

    public void update(float tpf) {
        for (Map.Entry<String, List<AudioNode>> entry : soundMap.entrySet()) {
            List<AudioNode> audioNodes = entry.getValue();
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


    public Map<String, List<AudioNode>> getSoundMap() {
        return soundMap;
    }
}
