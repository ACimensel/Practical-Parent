package ca.cmpt276.flame.model;

import android.content.Context;
import android.media.MediaPlayer;

import ca.cmpt276.flame.R;

/**
 * SoundPlayer is a static class for playing the different sounds in the application.
 * It is responsible for playing sounds which include a coin spin, peaceful background music,
 * and chirping upon timeout timer finishing
 */
public class BGMusicPlayer {
    private static MediaPlayer bgMusic;

    private BGMusicPlayer() {
        // static class: prevent other classes from creating new ones
    }

    public static void playBgMusic(Context context) {
        if (bgMusic == null) {
            bgMusic = MediaPlayer.create(context, R.raw.stasis_faster_version);
            bgMusic.start();
            bgMusic.setLooping(true);
        }
    }

    public static void pauseBgMusic() {
        if (bgMusic != null) {
            bgMusic.pause();
        }
    }

    public static void resumeBgMusic() {
        if (bgMusic != null) {
            bgMusic.start();
        }
    }
}
