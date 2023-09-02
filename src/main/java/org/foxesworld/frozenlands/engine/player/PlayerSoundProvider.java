package org.foxesworld.frozenlands.engine.player;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.scene.Node;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.Timer;
import org.foxesworld.frozenlands.FrozenLands;
import org.foxesworld.frozenlands.engine.providers.sound.SoundProvider;

import java.util.List;
import java.util.Random;

public class PlayerSoundProvider {
    private FrozenLands frozenLands;
    private SoundProvider soundProvider;
    private PlayerInterface playerInterface;
    private Node rootNode;
    private AudioNode walkAudio;
    private float currentVolume = 0.0f;
    private float targetVolume = 1.0f;
    private float fadeDuration = 1.0f;
    private float timeElapsed = 1.0f;
    private boolean fadingIn = false;
    private boolean fadingOut = false;
    private  AudioNode thisAudio;

    public PlayerSoundProvider(PlayerInterface playerInterface) {
        this.frozenLands = playerInterface.getKernelInterface().getFrozenLands();
        this.playerInterface = playerInterface;
        this.rootNode = playerInterface.getRootNode();
        this.soundProvider = playerInterface.getKernelInterface().getSoundManager();
    }

    public void playWalkAudio(String userState) {

        walkAudio = soundProvider.getRandomAudioNode(soundProvider.getSoundBlock("player").get(userState));
        if (walkAudio != null) {
            thisAudio = walkAudio;
            walkAudio.setLocalTranslation(playerInterface.getPlayerPosition());
            walkAudio.setPitch(1);//playerInterface.getPlayerData().getCurrentSpeed() / 4
            rootNode.attachChild(walkAudio);
            walkAudio.play();
            fadeInAudio(walkAudio);
        }
        stopWalkAudio();
    }

    public void stopWalkAudio() {
        if (walkAudio != null) {
            if (fadingIn) {
                frozenLands.getTimer().reset();
                fadingIn = false;
            }
            if (fadingOut) {
                frozenLands.getTimer().reset();
                fadingOut = false;
            }

            fadeOutAudio(thisAudio);
        }
    }

    private void fadeInAudio(AudioNode audioNode) {
        audioNode.setVolume(0.0f);
        audioNode.play();

        frozenLands.enqueue(() -> {
            float elapsed = audioNode.getPlaybackTime();
            if (elapsed < fadeDuration) {
                float alpha = FastMath.clamp(elapsed / fadeDuration, 0.0f, 1.0f);
                float current = alpha * targetVolume;
                audioNode.setVolume(current);
                return false;
            } else {
                return true;
            }
        });
    }

    private void fadeOutAudio(AudioNode audioNode) {
        frozenLands.enqueue(() -> {
            float elapsed = audioNode.getPlaybackTime();
            if (elapsed < fadeDuration) {
                float alpha = FastMath.clamp(elapsed / fadeDuration, 0.0f, 1.0f);
                float current = (1.0f - alpha) * targetVolume;
                audioNode.setVolume(current);
                return false;
            } else {
                audioNode.stop();
                rootNode.detachChild(audioNode);
                return true;
            }
        });
    }
}
