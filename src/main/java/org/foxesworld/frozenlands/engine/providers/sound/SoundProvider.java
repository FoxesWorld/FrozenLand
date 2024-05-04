package org.foxesworld.frozenlands.engine.providers.sound;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import org.foxesworld.frozenlands.FrozenLands;
import org.foxesworld.frozenlands.engine.KernelInterface;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.foxesworld.frozenlands.engine.utils.Utils.inputJsonReader;

public class SoundProvider {
    private KernelInterface kernelInterface;
    private int totalSounds = 0;
    private Map<String, Map<String, List<AudioNode>>> Sounds = new HashMap<>();

    public SoundProvider(KernelInterface kernelInterface) {
        this.kernelInterface = kernelInterface;
    }

    public void loadSounds(String path) {
        //JsonNode jsonRoot = inputJsonReader(kernelInterface, path);
        Iterator<String> iterator = jsonRoot.fieldNames();
        while (iterator.hasNext()) {
            String currentBlock = iterator.next();
            FrozenLands.logger.info("====== Scanning block " + currentBlock + " ======");
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
                    AudioNode audioNode = new AudioNode(kernelInterface.getAssetManager(), filePath, dataType);

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
                    totalSounds+=soundsNum.get();
                });
                kernelInterface.getLogger().info("Added " + soundsNum + " sounds to '" + event + "' event");
                soundBlock.put(event, audioNodes);
            });
            Sounds.put(currentBlock, soundBlock);
        }
        kernelInterface.getLogger().info("Finished adding sounds, total sndAmount: " + Sounds.size() + "x" + totalSounds);
    }

    public Map<String, List<AudioNode>> getSoundBlock(String blockName) {
        return Sounds.get(blockName);
    }

    public void update(float tpf) {
        for (Map.Entry<String, Map<String, List<AudioNode>>> entry : Sounds.entrySet()) {
            for (Map.Entry<String, List<AudioNode>> update : entry.getValue().entrySet()) {
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

    public AudioNode getRandomAudioNode(List<AudioNode> sndList) {
        Random random = new Random();
        if (sndList != null && !sndList.isEmpty()) {
            int randomIndex = random.nextInt(sndList.size());
            return sndList.get(randomIndex);
        }
        return null;
    }

    @Deprecated
    public Map<String, Map<String, List<AudioNode>>> getSounds() {
        return Sounds;
    }
}
