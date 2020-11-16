package com.egr423.sanitizor;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class SoundManager {

    private static final String SOUNDS_FOLDER = "sound_bites";
    private static final int MAX_SOUNDS = 50;

    private static SoundManager mManager;

    private AssetManager mAssets;
    private SoundPool mSoundPool;

    private Map<String, Integer> sounds;
    private Map<Integer, Boolean> soundIsReady;
    private Map<Integer, String> runningSounds;

    // Variables for a readied song to play after loading is complete
    private int readiedId;
    private String readiedName;
    private int readiedLoop;

    private boolean playGameAudio;

    public static SoundManager getInstance() {
        if (mManager == null) {
            mManager = new SoundManager();
        }
        return mManager;
    }

    public SoundManager() {
        //Initialize the sound pool
        AudioAttributes audioAttrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_SOUNDS)
                .setAudioAttributes(audioAttrs)
                .build();
        runningSounds = new ArrayMap<>();
        playGameAudio = false;
    }

    public void loadSounds(Context context) {
        if (sounds != null)
            return;

        //Load in all sounds in the assets/sound_bites folder
        mAssets = context.getAssets();

        String[] soundNames;
        try {
            soundNames = mAssets.list(SOUNDS_FOLDER);
        } catch (Exception e) {
            Log.e("SoundManager", e.toString());
            return;
        }

        sounds = new ArrayMap<>();
        soundIsReady = new ArrayMap<>();
        for (String filename : soundNames) {
            try {
                AssetFileDescriptor assetFd = mAssets.openFd(SOUNDS_FOLDER + "/" + filename);
                int id = mSoundPool.load(assetFd, 1);
                soundIsReady.put(id, false);
                mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        soundIsReady.put(sampleId, true);
                        //If the sound we just loaded is the sound that is readied, then play it
                        if (readiedId == sampleId) {
                            playReadied();
                        }
                    }
                });
                sounds.put(filename, id);
                Log.d("SoundManager", filename);
            } catch (Exception e) {
                Log.e("SoundManager", e.toString());
            }
        }
    }

    public void playReadied() {
        // If there is a readied sound, then play it
        if (readiedName != null) {
            Log.d("SoundManager", "Playing readied sound: " + readiedName);
            playSound(readiedName, readiedLoop);
            readiedName = null;
            readiedId = -1;
        }
    }

    // Play the sound once
    public void playSound(String soundName) {
        playSound(soundName, 0);
    }

    // Play the sound with the given loop setting
    public void playSound(String soundName, int loop) {
        if (soundName == null) return;

        Integer soundId = sounds.get(soundName);
        if (soundId != null) {
            // If the sound is readied, then play it
            if (soundIsReady.get(soundId)) {
                Log.d("SoundManager", "Playing sound: " + soundId + ", " + soundName);
                int playId;
                if (playGameAudio) {
                    playId = mSoundPool.play(soundId, 0.75f, 0.75f, 1, loop, 1.0f);
                } else {
                    playId = mSoundPool.play(soundId, 0, 0, 1, loop, 1.0f);
                }
                runningSounds.put(playId, soundName);
            } else {
                //Otherwise, ready it
                Log.d("SoundManager", "Readied sound: " + soundName);
                readiedId = soundId;
                readiedName = soundName;
                readiedLoop = loop;
            }
        } else {
            Log.d("SoundManager", "Could not find id for sound: " + soundName);
        }
    }

    public void pause() {
        Log.d("SoundManager", "Audio Paused");
        mSoundPool.autoPause();
    }

    public void resume() {
        Log.d("SoundManager", "Audio Resumed");
        mSoundPool.autoResume();
    }

    public void stopAll() {
        Log.d("SoundManager", "Audio Stopped");
        for (int id : runningSounds.keySet()) {
            mSoundPool.stop(id);
        }
    }

    public void stopSound(String soundName) {
        for (int id : runningSounds.keySet()) {
            if (runningSounds.get(id).equals(soundName)) {
                mSoundPool.stop(id);
            }
        }
    }

    public void setAudioStatus(boolean play) {
        playGameAudio = play;

        for (int id : runningSounds.keySet()) {
            if (!play) {
                mSoundPool.setVolume(id, 0, 0);
            } else {
                mSoundPool.setVolume(id, 0.75f, 0.75f);
            }
        }
    }
}
