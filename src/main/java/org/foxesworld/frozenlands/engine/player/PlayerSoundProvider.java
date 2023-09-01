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
    private float currentVolume = 0.0f; // Начальная громкость звука
    private float targetVolume = 1.0f; // Целевая громкость звука
    private float fadeDuration = 1.0f; // Длительность появления/затухания в секундах
    private float timeElapsed = 1.0f;
    private boolean fadingIn = false;
    private boolean fadingOut = false;
    private  AudioNode thisAudio;

    public PlayerSoundProvider(PlayerInterface playerInterface) {
        this.frozenLands = playerInterface.getKernelInterface().getFrozenLands();
        this.playerInterface = playerInterface;
        this.rootNode = playerInterface.getRootNode();
        this.soundProvider = playerInterface.getSoundManager();
    }

    public void playWalkAudio(String userState) {

        walkAudio = soundProvider.getRandomAudioNode(playerInterface.getPlayerSounds().get(userState));
        if (walkAudio != null) {
            thisAudio = walkAudio;
            walkAudio.setLocalTranslation(playerInterface.getPlayerPosition());
            walkAudio.setPitch(playerInterface.getPlayerData().getCurrentSpeed() / 4);
            rootNode.attachChild(walkAudio);
            walkAudio.play();
            fadeInAudio(walkAudio); // Начнем с плавного появления звука
        }
        stopWalkAudio();
    }

    public void stopWalkAudio() {
        if (walkAudio != null) {
            if (fadingIn) {
                // Если звук плавно появляется, остановим плавное появление
                frozenLands.getTimer().reset();
                fadingIn = false;
            }
            if (fadingOut) {
                // Если звук плавно затухает, остановим плавное затухание
                frozenLands.getTimer().reset();
                fadingOut = false;
            }

            fadeOutAudio(thisAudio); // Начнем плавное затухание звука
        }
    }

    private void fadeInAudio(AudioNode audioNode) {
        audioNode.setVolume(0.0f); // Начинаем с нулевой громкости
        audioNode.play();

        frozenLands.enqueue(() -> {
            float elapsed = audioNode.getPlaybackTime();
            if (elapsed < fadeDuration) {
                float alpha = FastMath.clamp(elapsed / fadeDuration, 0.0f, 1.0f);
                float current = alpha * targetVolume;
                audioNode.setVolume(current);
                return false; // Продолжаем выполнение таймера
            } else {
                return true; // Завершаем выполнение таймера
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
                return false; // Продолжаем выполнение таймера
            } else {
                audioNode.stop();
                rootNode.detachChild(audioNode);
                return true; // Завершаем выполнение таймера
            }
        });
    }
}
