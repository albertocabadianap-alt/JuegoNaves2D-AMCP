package com.example.juegonaves_amcp.juego;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class BucleJuegoThreadAMCP extends Thread {

    private final SurfaceHolder holder_AMCP;
    private final VistaJuegoSurfaceAMCP vista_AMCP;

    private boolean corriendo_AMCP = false;

    public BucleJuegoThreadAMCP(SurfaceHolder holder, VistaJuegoSurfaceAMCP vista) {
        this.holder_AMCP = holder;
        this.vista_AMCP = vista;
    }

    public void setCorriendo_AMCP(boolean valor_AMCP) {
        corriendo_AMCP = valor_AMCP;
    }

    @Override
    public void run() {
        long tiempoAnteriorMs_AMCP = System.currentTimeMillis();

        while (corriendo_AMCP) {

            long ahoraMs_AMCP = System.currentTimeMillis();
            long deltaMs_AMCP = ahoraMs_AMCP - tiempoAnteriorMs_AMCP;
            tiempoAnteriorMs_AMCP = ahoraMs_AMCP;

            Canvas canvas_AMCP = null;

            try {
                canvas_AMCP = holder_AMCP.lockCanvas();
                if (canvas_AMCP == null) continue;

                // 1) Actualizar lógica (por ahora solo límites)
                vista_AMCP.actualizar_AMCP(deltaMs_AMCP);

                // 2) Dibujar
                vista_AMCP.dibujar_AMCP(canvas_AMCP);

            } finally {
                if (canvas_AMCP != null) {
                    holder_AMCP.unlockCanvasAndPost(canvas_AMCP);
                }
            }

            // Pequeño descanso para no ir a lo loco (aprox 60 FPS)
            try {
                sleep(16);
            } catch (InterruptedException e) {
                // no pasa nada
            }
        }
    }
}
