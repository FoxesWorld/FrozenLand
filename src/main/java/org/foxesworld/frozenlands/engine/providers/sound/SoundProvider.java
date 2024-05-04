package org.foxesworld.frozenlands.engine.providers.sound;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import org.foxesworld.frozenlands.FrozenLands;
import org.foxesworld.frozenlands.engine.KernelInterface;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
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
        Gson gson = new Gson();
        String soundsJson = inputJsonReader(kernelInterface, path);

        // Определяем тип данных для разбора JSON
        Type soundsType = new TypeToken<Map<String, List<SoundEvent>>>(){}.getType();

        // Преобразуем JSON в соответствующую структуру данных
        Map<String, List<SoundEvent>> soundEvents = gson.fromJson(soundsJson, soundsType);

        // Определяем ключ верхнего уровня в JSON
        String topLevelKey = soundEvents.keySet().iterator().next();

        // Проходим по каждому типу звуков
        soundEvents.forEach((eventType, events) -> {
            FrozenLands.logger.info("====== Scanning event type " + eventType + " ======");

            // Создаем карту звуков для данного типа события
            Map<String, List<AudioNode>> eventMap = new HashMap<>();

            // Проходим по каждому событию в данном типе звуков
            events.forEach(soundEvent -> {
                AtomicInteger soundsNum = new AtomicInteger();
                List<AudioNode> audioNodes = new ArrayList<>();

                // Создаем AudioNode для каждого звукового файла
                soundEvent.getSounds().forEach(soundFile -> {
                    String filePath = "sounds/" +  topLevelKey + '/' + soundEvent.getSoundDir() + soundEvent.getEvent() + soundFile;
                    AudioData.DataType dataType = AudioData.DataType.valueOf(soundEvent.getSettings().getDataType());
                    AudioNode audioNode = new AudioNode(kernelInterface.getAssetManager(), filePath, dataType);

                    // Устанавливаем настройки звука
                    if (soundEvent.getSettings().getVolume() != null) {
                        audioNode.setVolume(soundEvent.getSettings().getVolume());
                    }
                    if (soundEvent.getSettings().isPositional() != null) {
                        audioNode.setPositional(soundEvent.getSettings().isPositional());
                    }
                    if (soundEvent.getSettings().getPitch() != null) {
                        audioNode.setPitch(soundEvent.getSettings().getPitch());
                    }

                    // Добавляем AudioNode в список звуков данного события
                    audioNodes.add(audioNode);
                    soundsNum.getAndIncrement();
                    totalSounds += soundsNum.get();
                });

                // Выводим информацию о добавленных звуках
                FrozenLands.logger.info("Added " + soundsNum + " sounds to event '" + soundEvent.getEvent() + "'");

                // Добавляем список звуков в карту звуков для данного события
                eventMap.put(soundEvent.getEvent(), audioNodes);
            });

            // Добавляем карту звуков для данного типа события в общую карту звуков
            Sounds.put(eventType, eventMap);
        });

        // Выводим информацию о завершении загрузки звуков
        FrozenLands.logger.info("Finished adding sounds, total sndAmount: " + Sounds.size() + "x" + totalSounds);
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

    static class SoundEvent {
        private String event;
        private String soundDir;
        private SoundSettings settings;
        private List<String> sounds;

        public String getEvent() {
            return event;
        }

        public String getSoundDir() {
            return soundDir;
        }

        public SoundSettings getSettings() {
            return settings;
        }

        public List<String> getSounds() {
            return sounds;
        }
    }

    static class SoundSettings {
        private Float volume;
        private Boolean positional;
        private Float pitch;
        private String dataType;

        public Float getVolume() {
            return volume;
        }

        public Boolean isPositional() {
            return positional;
        }

        public Float getPitch() {
            return pitch;
        }

        public String getDataType() {
            return dataType;
        }
    }
}
