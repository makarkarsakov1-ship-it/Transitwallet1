package com.transitwallet.app.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import com.transitwallet.app.R;

public class SoundManager {

    private static SoundManager instance;
    private SoundPool soundPool;
    private int soundNfcStart;
    private int soundSuccess;
    private int soundError;
    private boolean loaded = false;

    private SoundManager(Context ctx) {
        AudioAttributes attrs = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();

        soundPool = new SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(attrs)
            .build();

        soundPool.setOnLoadCompleteListener((sp, id, status) -> {
            if (status == 0) loaded = true;
        });

        soundNfcStart = soundPool.load(ctx, R.raw.nfc_start, 1);
        soundSuccess  = soundPool.load(ctx, R.raw.payment_success, 1);
        soundError    = soundPool.load(ctx, R.raw.payment_error, 1);
    }

    public static SoundManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new SoundManager(
                ctx.getApplicationContext());
        }
        return instance;
    }

    public void playNfcStart() {
        soundPool.play(soundNfcStart, 0.7f, 0.7f, 1, 0, 1.0f);
    }

    public void playSuccess() {
        soundPool.play(soundSuccess, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playError() {
        soundPool.play(soundError, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            instance = null;
        }
    }
}
