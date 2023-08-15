package org.foxesworld.newgame.engine.sound;

public class SoundSettings {
    private String audioFile;
    private boolean looping;
    private float volume;
    private float pitch;
    private boolean positional;

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isPositional() {
        return positional;
    }

    public void setPositional(boolean positional) {
        this.positional = positional;
    }
}
