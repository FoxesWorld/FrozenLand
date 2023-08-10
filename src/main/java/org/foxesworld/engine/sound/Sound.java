package org.foxesworld.engine.sound;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.phonon.scene.emitters.PositionalSoundEmitterControl;
import org.foxesworld.NewGame;

import java.util.*;

public class Sound {

    private static final String KEY_DATATYPE = "DataType";
    private static final String KEY_NAME = "name";
    private static final String KEY_POSITIONAL = "positional";
    private static final String KEY_LOOPING = "looping";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_SOUNDS = "sounds";

    private SimpleApplication app;
    private Map<String, List<AudioNode>> nodeMap = new HashMap<>();

    public Sound(SimpleApplication app) {
        System.out.println("AidensCode v0.4 SoundLib");
        this.app = app;
        this.listCategories((Map<String, Map>) NewGame.getCONFIG().get("internal/sounds"));
    }

    private void listCategories(Map<String, Map> sounds) {
        for (Map.Entry<String, Map> map : sounds.entrySet()) {
            setUpAudio(map.getValue(), map.getKey());
        }
    }

    private void setUpAudio(Map<String, Map<String, Object>> inputSounds, String soundSection) {
        int soundsNum = 0;
        for (Map.Entry<String, Map<String, Object>> sndUnit : inputSounds.entrySet()) {
            String sndGroupTitle = sndUnit.getKey();
            List<AudioNode> soundsArray = new ArrayList<>();
            int arrayCount = 0;
            Map<String, Object> soundGroup = inputSounds.get(sndGroupTitle);
            for (String sndPath : (List<String>) soundGroup.get("sounds")) {
                soundsArray.add(createAudioNode(arrayCount, sndPath, soundGroup));
                arrayCount++;
            }
            System.out.println("    " + soundSection + '/' + sndGroupTitle + " has " + arrayCount + " sub sounds");
            nodeMap.put(soundSection + '/' + sndGroupTitle, soundsArray);
            soundsNum++;
        }
        System.out.println("Total " + soundSection + " events " + soundsNum);
    }

    private AudioNode createAudioNode(int soundNum, String soundPath, Map<String, Object> soundGroup) {
        AudioNode node = new AudioNode(
                app.getAssetManager(),
                "assets/" + soundPath,
                AudioData.DataType.valueOf((String) soundGroup.get(KEY_DATATYPE))
        );
        node.setName((String) soundGroup.get(KEY_NAME) + soundNum);
        node.setPositional((Boolean) soundGroup.get(KEY_POSITIONAL));
        node.setLooping((Boolean) soundGroup.get(KEY_LOOPING));
        node.setVolume((Integer)soundGroup.get(KEY_VOLUME));
        app.getRootNode().attachChild(node);

        return node;
    }

    public void playSound(String snd) {
        Random rand = new Random();
        int index = rand.nextInt(nodeMap.get(snd).size());
        nodeMap.get(snd).get(index).playInstance();
    }

    //WARN if we get a sound node it will have only one sound it was initialised with
    public AudioNode getSoundNode(String snd, boolean positional, float refDist, float maxDist, float volume) {
        AudioNode sound;
        List<AudioNode> soundList;
        int soundNum = nodeMap.get(snd).size();
        Random rand = new Random();
        int index = rand.nextInt(soundNum);
        if(soundNum >= 1) {
            soundList = nodeMap.get(snd);
            //System.out.println("SndNode - "+ nodeMap.get(snd).get(index));
            sound = soundList.get(index);
        } else {
            sound = nodeMap.get(snd).get(index);
        }

        sound.setPositional(positional);
        sound.setVolume(volume);

        return sound;
    }

    @Deprecated
    public PositionalSoundEmitterControl getPosSound(String path, boolean reverb, boolean looping, float volume, Vector3f offset){
        PositionalSoundEmitterControl soundEmitter = new PositionalSoundEmitterControl(app.getAssetManager(), path);
        soundEmitter.setReverbEnabled(reverb);
        soundEmitter.setLooping(looping);
        soundEmitter.setOffset(offset);
        soundEmitter.setVolume(volume);
        return  soundEmitter;
    }

}
