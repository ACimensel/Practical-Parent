package ca.cmpt276.flame.model;

import android.content.Context;
import android.media.MediaPlayer;

import ca.cmpt276.flame.R;

/**
 * SoundPlayer is a static class for playing the different sounds in the application.
 * It is responsible for playing sounds which include a coin spin, peaceful background music,
 * and chirping upon timeout timer finishing
 */
public class SoundPlayer {
    private static MediaPlayer coinSpinSound;

    private SoundPlayer() {
        // static class: prevent other classes from creating new ones
    }

    public static void playCoinSpinSound(Context context) {
        if (coinSpinSound == null) {
            coinSpinSound = MediaPlayer.create(context, R.raw.coin_spin_sound);
        }
        coinSpinSound.seekTo(0);
        coinSpinSound.start();
    }
}
