package com.example.juegonaves_amcp.juego;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.juegonaves_amcp.R;
import com.example.juegonaves_amcp.inicio.InicioActivityAMCP;

public class GameOverActivityAMCP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover_amcp);

        String nombreJugador_AMCP = getIntent().getStringExtra("nombreJugador_AMCP");
        int dificultad_AMCP = getIntent().getIntExtra("dificultad_AMCP", 0);
        int segundos_AMCP = getIntent().getIntExtra("segundos_AMCP", 0);

        TextView tvInfo_AMCP = findViewById(R.id.tvInfo_AMCP);
        Button btnVolverMenu_AMCP = findViewById(R.id.btnVolverMenu_AMCP);

        String difTexto_AMCP = (dificultad_AMCP == 0) ? "Fácil" : "Difícil";
        tvInfo_AMCP.setText("Jugador: " + nombreJugador_AMCP + "\nDificultad: " + difTexto_AMCP + "\nTiempo: " + segundos_AMCP + "s");

        btnVolverMenu_AMCP.setOnClickListener(v -> {
            Intent i_AMCP = new Intent(this, InicioActivityAMCP.class);
            i_AMCP.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i_AMCP);
            finish();
        });
    }
}
