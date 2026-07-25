package com.example.juegonaves_amcp.juego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.juegonaves_amcp.R;
import com.example.juegonaves_amcp.inicio.InicioActivityAMCP;
import com.example.juegonaves_amcp.modelos.NaveEnemigaAMCP;
import com.example.juegonaves_amcp.utilidades.GestorAudioAMCP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class VistaJuegoSurfaceAMCP extends SurfaceView implements SurfaceHolder.Callback {

    // HUD
    private float hudY_AMCP = 170;
    private float hudAlto_AMCP = 170;

    // Botones HUD (zonas táctiles)
    private RectF btnPausaRect_AMCP;
    private RectF btnMenuRect_AMCP;

    private SurfaceHolder holder_AMCP;
    private Paint pincel_AMCP;
    private BucleJuegoThreadAMCP bucle_AMCP;

    // Jugador
    private float xJugador_AMCP;
    private float yJugador_AMCP;
    private float anchoNave_AMCP = 120;
    private float altoNave_AMCP = 120;

    // Sprites
    private Bitmap bmpJugador_AMCP;
    private Bitmap bmpEnemigo_AMCP;

    // Control
    private float velocidadJugador_AMCP = 35;
    private int altoPantalla_AMCP = 0;

    // Marco
    private float margenSuperior_AMCP = 260; // más para no invadir HUD/botones
    private float margenInferior_AMCP = 120;

    // Enemigos
    private ArrayList<NaveEnemigaAMCP> listaEnemigos_AMCP = new ArrayList<>();
    private Random random_AMCP = new Random();
    private long tiempoSpawnAcumuladoMs_AMCP = 0;

    // Dificultad
    private int dificultad_AMCP; // 0 fácil, 1 difícil
    private int maxEnemigos_AMCP;
    private long spawnCadaMs_AMCP;
    private float velocidadBaseEnemigos_AMCP;

    // Tamaño enemigos
    private float anchoEnemigo_AMCP = 90;
    private float altoEnemigo_AMCP = 90;

    // Tiempo y aceleración
    private long tiempoTotalMs_AMCP = 0;

    // Datos jugador
    private String nombreJugador_AMCP = "Jugador";

    // Estado
    private boolean juegoTerminado_AMCP = false;
    private boolean pausado_AMCP = false;

    // Audio
    private GestorAudioAMCP gestorAudio_AMCP;

    public VistaJuegoSurfaceAMCP(Context context, int dificultad, String nombreJugador, GestorAudioAMCP gestorAudio) {
        super(context);

        this.dificultad_AMCP = dificultad;
        this.gestorAudio_AMCP = gestorAudio;

        if (nombreJugador != null && !nombreJugador.trim().isEmpty()) {
            this.nombreJugador_AMCP = nombreJugador.trim();
        }

        holder_AMCP = getHolder();
        holder_AMCP.addCallback(this);

        pincel_AMCP = new Paint();
        pincel_AMCP.setAntiAlias(true);

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        // Parámetros según dificultad
        if (dificultad_AMCP == 0) { // FÁCIL
            maxEnemigos_AMCP = 6;
            spawnCadaMs_AMCP = 900;
            velocidadBaseEnemigos_AMCP = 450f;
        } else { // DIFÍCIL
            maxEnemigos_AMCP = 12;
            spawnCadaMs_AMCP = 500;
            velocidadBaseEnemigos_AMCP = 650f;
        }

        // Cargar sprites
        cargarSprites_AMCP();
    }

    private void cargarSprites_AMCP() {
        // Jugador
        bmpJugador_AMCP = BitmapFactory.decodeResource(getResources(), R.drawable.nave_jugador_amcp);
        if (bmpJugador_AMCP != null) {
            bmpJugador_AMCP = Bitmap.createScaledBitmap(
                    bmpJugador_AMCP,
                    (int) anchoNave_AMCP,
                    (int) altoNave_AMCP,
                    false
            );
        }

        // Enemigo
        bmpEnemigo_AMCP = BitmapFactory.decodeResource(getResources(), R.drawable.nave_enemiga_amcp);
        if (bmpEnemigo_AMCP != null) {
            bmpEnemigo_AMCP = Bitmap.createScaledBitmap(
                    bmpEnemigo_AMCP,
                    (int) anchoEnemigo_AMCP,
                    (int) altoEnemigo_AMCP,
                    false
            );
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int anchoPantalla_AMCP = getWidth();
        altoPantalla_AMCP = getHeight();

        // Jugador fijo a la derecha
        xJugador_AMCP = anchoPantalla_AMCP - anchoNave_AMCP - 40;
        yJugador_AMCP = (altoPantalla_AMCP / 2f) - (altoNave_AMCP / 2f);
        limitarYJugador_AMCP();

        // Zonas botones HUD (se calculan con el ancho real)
        btnPausaRect_AMCP = new RectF(getWidth() - 360, 30, getWidth() - 200, 30 + 80);
        btnMenuRect_AMCP  = new RectF(getWidth() - 190, 30, getWidth() - 30,  30 + 80);

        bucle_AMCP = new BucleJuegoThreadAMCP(holder_AMCP, this);
        bucle_AMCP.setCorriendo_AMCP(true);
        bucle_AMCP.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (bucle_AMCP != null) {
            bucle_AMCP.setCorriendo_AMCP(false);
            boolean reintentar_AMCP = true;
            while (reintentar_AMCP) {
                try {
                    bucle_AMCP.join();
                    reintentar_AMCP = false;
                } catch (InterruptedException e) { }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    // =======================
    //  LÓGICA
    // =======================

    public void actualizar_AMCP(long deltaMs_AMCP) {
        if (juegoTerminado_AMCP) return;
        if (pausado_AMCP) return;

        limitarYJugador_AMCP();

        // Tiempo total
        tiempoTotalMs_AMCP += deltaMs_AMCP;
        int segundos_AMCP = (int) (tiempoTotalMs_AMCP / 1000);

        // Velocidad creciente: cada 5s suma 60px/s
        float velocidadExtra_AMCP = (segundos_AMCP / 5) * 60f;
        float velocidadActualEnemigos_AMCP = velocidadBaseEnemigos_AMCP + velocidadExtra_AMCP;

        // Spawn por tiempo
        tiempoSpawnAcumuladoMs_AMCP += deltaMs_AMCP;
        if (tiempoSpawnAcumuladoMs_AMCP >= spawnCadaMs_AMCP) {
            tiempoSpawnAcumuladoMs_AMCP = 0;
            if (listaEnemigos_AMCP.size() < maxEnemigos_AMCP) {
                crearEnemigoAleatorio_AMCP(velocidadActualEnemigos_AMCP);
            }
        }

        float deltaSegundos_AMCP = deltaMs_AMCP / 1000f;
        RectF rectJugador_AMCP = getRectJugador_AMCP();

        Iterator<NaveEnemigaAMCP> it_AMCP = listaEnemigos_AMCP.iterator();
        while (it_AMCP.hasNext()) {
            NaveEnemigaAMCP enemigo_AMCP = it_AMCP.next();
            enemigo_AMCP.actualizar_AMCP(deltaSegundos_AMCP);

            // Colisión -> Game Over inmediato
            if (RectF.intersects(rectJugador_AMCP, enemigo_AMCP.getRect_AMCP())) {
                if (gestorAudio_AMCP != null) gestorAudio_AMCP.sonidoColision_AMCP();
                lanzarGameOver_AMCP(segundos_AMCP);
                return;
            }

            // Eliminar si sale por la derecha
            if (enemigo_AMCP.x_AMCP > getWidth() + 200) {
                it_AMCP.remove();
            }
        }
    }

    private RectF getRectJugador_AMCP() {
        return new RectF(
                xJugador_AMCP,
                yJugador_AMCP,
                xJugador_AMCP + anchoNave_AMCP,
                yJugador_AMCP + altoNave_AMCP
        );
    }

    private void crearEnemigoAleatorio_AMCP(float velocidadX_AMCP) {
        float minY_AMCP = margenSuperior_AMCP;
        float maxY_AMCP = altoPantalla_AMCP - altoEnemigo_AMCP - margenInferior_AMCP;

        float yAleatoria_AMCP = minY_AMCP + random_AMCP.nextFloat() * (maxY_AMCP - minY_AMCP);
        float xInicial_AMCP = -anchoEnemigo_AMCP - 50;

        listaEnemigos_AMCP.add(new NaveEnemigaAMCP(
                xInicial_AMCP,
                yAleatoria_AMCP,
                anchoEnemigo_AMCP,
                altoEnemigo_AMCP,
                velocidadX_AMCP
        ));
    }

    private void lanzarGameOver_AMCP(int segundos_AMCP) {
        juegoTerminado_AMCP = true;

        if (bucle_AMCP != null) {
            bucle_AMCP.setCorriendo_AMCP(false);
        }

        Context ctx_AMCP = getContext();
        if (ctx_AMCP instanceof Activity) {
            ((Activity) ctx_AMCP).runOnUiThread(() -> {
                Intent i_AMCP = new Intent(ctx_AMCP, GameOverActivityAMCP.class);
                i_AMCP.putExtra("nombreJugador_AMCP", nombreJugador_AMCP);
                i_AMCP.putExtra("dificultad_AMCP", dificultad_AMCP);
                i_AMCP.putExtra("segundos_AMCP", segundos_AMCP);
                ctx_AMCP.startActivity(i_AMCP);
                ((Activity) ctx_AMCP).finish();
            });
        }
    }

    // =======================
    //  DIBUJO
    // =======================

    public void dibujar_AMCP(Canvas canvas_AMCP) {
        canvas_AMCP.drawColor(Color.BLACK);

        // HUD fondo
        pincel_AMCP.setColor(Color.argb(170, 0, 0, 0));
        canvas_AMCP.drawRect(20, 20, getWidth() - 20, 20 + hudAlto_AMCP, pincel_AMCP);

        // Botón pausa
        if (btnPausaRect_AMCP != null) {
            pincel_AMCP.setColor(Color.DKGRAY);
            canvas_AMCP.drawRoundRect(btnPausaRect_AMCP, 18, 18, pincel_AMCP);
            pincel_AMCP.setColor(Color.WHITE);
            pincel_AMCP.setTextSize(30);
            canvas_AMCP.drawText(pausado_AMCP ? "Reanudar" : "Pausar",
                    btnPausaRect_AMCP.left + 20, btnPausaRect_AMCP.top + 52, pincel_AMCP);
        }

        // Botón menú
        if (btnMenuRect_AMCP != null) {
            pincel_AMCP.setColor(Color.DKGRAY);
            canvas_AMCP.drawRoundRect(btnMenuRect_AMCP, 18, 18, pincel_AMCP);
            pincel_AMCP.setColor(Color.WHITE);
            pincel_AMCP.setTextSize(30);
            canvas_AMCP.drawText("Menú",
                    btnMenuRect_AMCP.left + 40, btnMenuRect_AMCP.top + 52, pincel_AMCP);
        }

        // Textos info
        pincel_AMCP.setColor(Color.WHITE);
        pincel_AMCP.setTextSize(28);
        int segundos_AMCP = (int) (tiempoTotalMs_AMCP / 1000);
        canvas_AMCP.drawText("Jugador: " + nombreJugador_AMCP + " | Tiempo: " + segundos_AMCP + "s",
                40, hudY_AMCP + 60, pincel_AMCP);

        // Enemigos (sprites)
        for (NaveEnemigaAMCP enemigo_AMCP : listaEnemigos_AMCP) {
            if (bmpEnemigo_AMCP != null) {
                canvas_AMCP.drawBitmap(bmpEnemigo_AMCP, enemigo_AMCP.x_AMCP, enemigo_AMCP.y_AMCP, null);
            } else {
                pincel_AMCP.setColor(Color.RED);
                canvas_AMCP.drawRect(
                        enemigo_AMCP.x_AMCP,
                        enemigo_AMCP.y_AMCP,
                        enemigo_AMCP.x_AMCP + enemigo_AMCP.ancho_AMCP,
                        enemigo_AMCP.y_AMCP + enemigo_AMCP.alto_AMCP,
                        pincel_AMCP
                );
            }
        }

        // Jugador (sprite)
        if (bmpJugador_AMCP != null) {
            canvas_AMCP.drawBitmap(bmpJugador_AMCP, xJugador_AMCP, yJugador_AMCP, null);
        } else {
            pincel_AMCP.setColor(Color.CYAN);
            canvas_AMCP.drawRect(
                    xJugador_AMCP,
                    yJugador_AMCP,
                    xJugador_AMCP + anchoNave_AMCP,
                    yJugador_AMCP + altoNave_AMCP,
                    pincel_AMCP
            );
        }

        // Aviso pausa
        if (pausado_AMCP) {
            pincel_AMCP.setColor(Color.WHITE);
            pincel_AMCP.setTextSize(55);
            canvas_AMCP.drawText("PAUSA", getWidth() / 2f - 90, getHeight() / 2f, pincel_AMCP);
        }
    }

    private void limitarYJugador_AMCP() {
        if (altoPantalla_AMCP == 0) return;

        float minY_AMCP = margenSuperior_AMCP;
        float maxY_AMCP = altoPantalla_AMCP - altoNave_AMCP - margenInferior_AMCP;

        if (yJugador_AMCP < minY_AMCP) yJugador_AMCP = minY_AMCP;
        if (yJugador_AMCP > maxY_AMCP) yJugador_AMCP = maxY_AMCP;
    }

    // =======================
    //  CONTROLES
    // =======================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (altoPantalla_AMCP == 0) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float xTouch_AMCP = event.getX();
            float yTouch_AMCP = event.getY();

            // Botón pausa
            if (btnPausaRect_AMCP != null && btnPausaRect_AMCP.contains(xTouch_AMCP, yTouch_AMCP)) {
                pausado_AMCP = !pausado_AMCP;

                if (gestorAudio_AMCP != null) {
                    if (pausado_AMCP) gestorAudio_AMCP.pausarMusica_AMCP();
                    else gestorAudio_AMCP.reproducirMusica_AMCP();
                }
                return true;
            }

            // Botón menú
            if (btnMenuRect_AMCP != null && btnMenuRect_AMCP.contains(xTouch_AMCP, yTouch_AMCP)) {
                volverMenu_AMCP();
                return true;
            }
        }

        // Movimiento por touch mientras no esté pausado
        if ((event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) && !pausado_AMCP) {
            yJugador_AMCP = event.getY() - (altoNave_AMCP / 2f);
            limitarYJugador_AMCP();
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void volverMenu_AMCP() {
        juegoTerminado_AMCP = true;

        if (bucle_AMCP != null) {
            bucle_AMCP.setCorriendo_AMCP(false);
        }
        if (gestorAudio_AMCP != null) {
            gestorAudio_AMCP.pausarMusica_AMCP();
        }

        Context ctx_AMCP = getContext();
        if (ctx_AMCP instanceof Activity) {
            ((Activity) ctx_AMCP).runOnUiThread(() -> {
                Intent i_AMCP = new Intent(ctx_AMCP, InicioActivityAMCP.class);
                i_AMCP.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx_AMCP.startActivity(i_AMCP);
                ((Activity) ctx_AMCP).finish();
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (altoPantalla_AMCP == 0) return super.onKeyDown(keyCode, event);

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (!pausado_AMCP) {
                yJugador_AMCP -= velocidadJugador_AMCP;
                limitarYJugador_AMCP();
            }
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!pausado_AMCP) {
                yJugador_AMCP += velocidadJugador_AMCP;
                limitarYJugador_AMCP();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
