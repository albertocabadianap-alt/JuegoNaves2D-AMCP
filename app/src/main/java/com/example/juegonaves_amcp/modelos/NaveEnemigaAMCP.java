package com.example.juegonaves_amcp.modelos;

import android.graphics.RectF;

public class NaveEnemigaAMCP {

    public float x_AMCP;
    public float y_AMCP;
    public float ancho_AMCP;
    public float alto_AMCP;
    public float velocidadX_AMCP;

    public NaveEnemigaAMCP(float x, float y, float ancho, float alto, float velocidadX) {
        this.x_AMCP = x;
        this.y_AMCP = y;
        this.ancho_AMCP = ancho;
        this.alto_AMCP = alto;
        this.velocidadX_AMCP = velocidadX;
    }

    public void actualizar_AMCP(float deltaSegundos_AMCP) {
        x_AMCP += velocidadX_AMCP * deltaSegundos_AMCP;
    }

    public RectF getRect_AMCP() {
        return new RectF(x_AMCP, y_AMCP, x_AMCP + ancho_AMCP, y_AMCP + alto_AMCP);
    }
}
