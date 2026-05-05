package com.example.serene;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static MediaPlayer previewPlayer;
    private static int currentTrack = -1;

    public static void play(Context context, int resId) {
        stop();
        mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        currentTrack = resId;
    }

    public static void stop() {
        stopPreview();
        stopMain();
    }

    private static void stopMain() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentTrack = -1;
    }

    private static void stopPreview() {
        if (previewPlayer != null) {
            previewPlayer.stop();
            previewPlayer.release();
            previewPlayer = null;
        }
    }

    public static int getCurrentTrack() {
        return currentTrack;
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}