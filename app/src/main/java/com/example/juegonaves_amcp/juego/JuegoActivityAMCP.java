package com.example.juegonaves_amcp.juego;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.juegonaves_amcp.utilidades.GestorAudioAMCP;

public class JuegoActivityAMCP extends AppCompatActivity {

    private GestorAudioAMCP gestorAudio_AMCP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int dificultad_AMCP = getIntent().getIntExtra("dificultad_AMCP", 0);
        String nombreJugador_AMCP = getIntent().getStringExtra("nombreJugador_AMCP");

        // ✅ Crear audio y arrancar música
        gestorAudio_AMCP = new GestorAudioAMCP(this);
        gestorAudio_AMCP.reproducirMusica_AMCP();

        // ✅ Usar el constructor NUEVO (con audio)
        VistaJuegoSurfaceAMCP vistaJuego_AMCP =
                new VistaJuegoSurfaceAMCP(this, dificultad_AMCP, nombreJugador_AMCP, gestorAudio_AMCP);

        setContentView(vistaJuego_AMCP);

        vistaJuego_AMCP.setFocusable(true);
        vistaJuego_AMCP.setFocusableInTouchMode(true);
        vistaJuego_AMCP.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gestorAudio_AMCP != null) gestorAudio_AMCP.pausarMusica_AMCP();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gestorAudio_AMCP != null) gestorAudio_AMCP.reproducirMusica_AMCP();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gestorAudio_AMCP != null) gestorAudio_AMCP.liberar_AMCP();
    }
}
