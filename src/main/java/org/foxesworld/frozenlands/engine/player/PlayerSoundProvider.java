package org.foxesworld.frozenlands.engine.player;

import com.jme3.audio.AudioNode;
import org.foxesworld.frozenlands.engine.KernelInterface;
import org.foxesworld.frozenlands.engine.providers.sound.SoundProvider;

import java.util.List;
import java.util.Map;

public class PlayerSoundProvider extends SoundProvider {

    private Map<String, List<AudioNode>> playerSounds;

    public PlayerSoundProvider(KernelInterface kernelInterface) {
        super(kernelInterface);
        playerSounds = kernelInterface.getSoundManager().getSoundBlock("player");
    }

    public void playSound(String sound){
        List<AudioNode> eventSounds = playerSounds.get(sound);
        if(eventSounds != null) {
            this.getRandomAudioNode(eventSounds).play();
        }
    }
}
