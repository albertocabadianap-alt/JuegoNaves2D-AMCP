package com.example.juegonaves_amcp.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.juegonaves_amcp.R;
import com.example.juegonaves_amcp.juego.JuegoActivityAMCP;

public class InicioActivityAMCP extends AppCompatActivity {

    private EditText etNombre_AMCP;
    private RadioGroup rgDificultad_AMCP;
    private Button btnJugar_AMCP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_amcp);

        etNombre_AMCP = findViewById(R.id.etNombre_AMCP);
        rgDificultad_AMCP = findViewById(R.id.rgDificultad_AMCP);
        btnJugar_AMCP = findViewById(R.id.btnJugar_AMCP);

        btnJugar_AMCP.setOnClickListener(v -> {
            String nombreJugador_AMCP = etNombre_AMCP.getText().toString().trim();

            if (nombreJugador_AMCP.isEmpty()) {
                Toast.makeText(this, "Introduce tu nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            int dificultad_AMCP;
            if (rgDificultad_AMCP.getCheckedRadioButtonId() == R.id.rbFacil_AMCP) {
                dificultad_AMCP = 0; // FACIL
            } else {
                dificultad_AMCP = 1; // DIFICIL
            }

            Intent intentJuego_AMCP = new Intent(this, JuegoActivityAMCP.class);
            intentJuego_AMCP.putExtra("nombreJugador_AMCP", nombreJugador_AMCP);
            intentJuego_AMCP.putExtra("dificultad_AMCP", dificultad_AMCP);
            startActivity(intentJuego_AMCP);
        });
    }
}
