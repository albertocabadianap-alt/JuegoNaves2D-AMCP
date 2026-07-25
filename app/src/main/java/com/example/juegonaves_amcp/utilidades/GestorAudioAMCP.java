package com.example.juegonaves_amcp.utilidades;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.juegonaves_amcp.R;

public class GestorAudioAMCP {

    private MediaPlayer musica_AMCP;
    private SoundPool soundPool_AMCP;
    private int idColision_AMCP = 0;

    public GestorAudioAMCP(Context ctx_AMCP) {
        // Música
        musica_AMCP = MediaPlayer.create(ctx_AMCP, R.raw.musica_juego_amcp);
        if (musica_AMCP != null) {
            musica_AMCP.setLooping(true);
        }

        // Efectos
        AudioAttributes attrs_AMCP = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool_AMCP = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(attrs_AMCP)
                .build();

        idColision_AMCP = soundPool_AMCP.load(ctx_AMCP, R.raw.colision_amcp, 1);
    }

    public void reproducirMusica_AMCP() {
        if (musica_AMCP != null && !musica_AMCP.isPlaying()) {
            musica_AMCP.start();
        }
    }

    public void pausarMusica_AMCP() {
        if (musica_AMCP != null && musica_AMCP.isPlaying()) {
            musica_AMCP.pause();
        }
    }

    public void sonidoColision_AMCP() {
        if (soundPool_AMCP != null && idColision_AMCP != 0) {
            soundPool_AMCP.play(idColision_AMCP, 1f, 1f, 1, 0, 1f);
        }
    }

    public void liberar_AMCP() {
        if (musica_AMCP != null) {
            try { musica_AMCP.release(); } catch (Exception ignored) {}
            musica_AMCP = null;
        }
        if (soundPool_AMCP != null) {
            try { soundPool_AMCP.release(); } catch (Exception ignored) {}
            soundPool_AMCP = null;
        }
    }
}
