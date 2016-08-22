/*
 * TableroConecta4View.java
 */
package es.uam.eps.dadm.connect4.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.activities.C4PreferencesActivity;
import es.uam.eps.dadm.connect4.activities.GameActivity;
import es.uam.eps.dadm.connect4.activities.SelectActivity;
import es.uam.eps.dadm.connect4.model.TableroConecta4;
import es.uam.eps.multij.Tablero;

/**
 * View android personalizada para el tablero del conecta 4
 *
 * @author Manuel Reyes
 * @see android.view.View
 */
public class TableroConecta4View extends View {

    private Paint colorFondo = new Paint();
    private Paint color0 = new Paint();
    private Paint color1 = new Paint();
    private Paint color2 = new Paint();

    private float h;
    private float w;

    public ValueAnimator va, va2;
    private float animY, animY2;
    private float radio, radio2;

    private OnPlayListener listener  = null;
    private TableroConecta4 tablero = null;

    int figura;
    boolean finUnico = false;
    public static boolean cambio = false;

    Context c;

    /**
     * Constructor
     *
     * @param context
     */
    public TableroConecta4View(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public TableroConecta4View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TableroConecta4View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        c=context;
    }

    /**
     * Inicializacion de la vista
     *
     */
    private void init(){
        /*Brochas*/
        colorFondo.setStyle(Paint.Style.FILL_AND_STROKE);
        colorFondo.setStrokeWidth(1);
        color0.setStyle(Paint.Style.FILL_AND_STROKE);
        color0.setColor(Color.WHITE);
        color0.setStrokeWidth(1);
        color1.setStyle(Paint.Style.FILL_AND_STROKE);
        color1.setColor(Color.RED);
        color1.setStrokeWidth(1);
        color2.setStyle(Paint.Style.FILL_AND_STROKE);
        color2.setColor(Color.GREEN);
        color2.setStrokeWidth(1);
        personalizar();

        /*Tamaños*/
        if(w>h)
            radio=h/TableroConecta4.COLUMNAS/2;
        else
            radio=w/TableroConecta4.COLUMNAS/2;
        radio2 = (float) (radio*0.85);
        animY = 5 * h / TableroConecta4.COLUMNAS + 2 * radio;
    }

    /**
     * Adaptado de
     *
     *      http://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation
     *
     * para intentar que la vista sea cuadrada (**)
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /*int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthSize>heightSize)
            setMeasuredDimension(heightSize, heightSize);
        else
            setMeasuredDimension(widthSize, widthSize);*/



        int desiredWidth = 500;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        // Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            // Be whatever you want
            width = desiredWidth;
        }

        // Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            // Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...usamos width  (**)
            height = Math.min(width, heightSize); //(**)
        } else {
            // Aqui ponemos width hacer que sea cuadrada (**)
            height = width;                            //(**)
        }

        width = height = Math.min(width, height);

        // MUST CALL THIS
        setMeasuredDimension(width, height);

    }

    /**
     * Cada vez que hay un cambio de tamaño del componente nos avisan con este método.
     * Utilizado para redefinir algunos variables.
     *
     * @param w nueva anchura
     * @param h nueva altura
     * @param oldw antigual anchura
     * @param oldh antigua altura
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        if(w>h)
            radio=h/TableroConecta4.COLUMNAS/2;
        else
            radio=w/TableroConecta4.COLUMNAS/2;
        radio2 = (float) (radio*0.85);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Transforma el punto de toque en el eje x en una columna del juego
     *
     * @param x coordenada
     * @return columna en el tablero
     */
    public int xToColumn(float x) {
        if(x<0) return -1;
        if(x<1*w/TableroConecta4.COLUMNAS) return 0;
        if(x<2*w/TableroConecta4.COLUMNAS) return 1;
        if(x<3*w/TableroConecta4.COLUMNAS) return 2;
        if(x<4*w/TableroConecta4.COLUMNAS) return 3;
        if(x<5*w/TableroConecta4.COLUMNAS) return 4;
        if(x<6*w/TableroConecta4.COLUMNAS) return 5;
        if(x<w) return 6;
        return -1;
    }

    public void setCaidasB(){
        /*Animacion 1*/
        if (va!=null) va.cancel();
        animY = -1-radio;
        va = ValueAnimator.ofFloat(animY, (tablero.ultimaJugada/10) * h / TableroConecta4.COLUMNAS + 2 * radio);
        va.setDuration(2500);
        va.setInterpolator(new AccelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animY = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(tablero.getEstado()!=Tablero.EN_CURSO) {
                    GameActivity c2 = (GameActivity) c;
                    c2.finPartida();
                }else if (!C4PreferencesActivity.getTabletCode(getContext())){
                    Intent i = new Intent(getContext(), SelectActivity.class);
                    i.putExtra("fin", false);
                    Toast.makeText(getContext(), getContext().getText(R.string.done), Toast.LENGTH_SHORT).show();
                    getContext().startActivity(i);

                }else{
                    GameActivity c2 = (GameActivity) c;
                    Toast.makeText(getContext(), getContext().getText(R.string.done), Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {}
                    c2.refreshtablasB();
                    c2.refreshtablasA();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
    }


    /**
     * Define las animaciones para la caida de las fichas
     *
     */
    public void setCaidas(){
        if(tablero.getEstado()!=Tablero.EN_CURSO && !finUnico) {
            finUnico=true;
        }else if(finUnico){
            return;
        }
        GameActivity.mutex=true;
        /*Animacion 1*/
        if (va!=null) va.cancel();
        animY = -1-radio;
        va = ValueAnimator.ofFloat(animY, (tablero.ultimaJugada/10) * h / TableroConecta4.COLUMNAS + 2 * radio);
        va.setDuration(1000);
        va.setInterpolator(new AccelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animY = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(tablero.getEstado()!=Tablero.EN_CURSO){
                    GameActivity c2 = (GameActivity)c;
                    c2.finPartida();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.setStartDelay(990); //Comienza tarde para que se ejecute la otra primero
        va.start();

        /*Animacion 2*/
        if (va2!=null) va2.cancel();
        animY2 = -1-radio;
        va2 = ValueAnimator.ofFloat(animY2, (tablero.penultimaJugada/10) * h / TableroConecta4.COLUMNAS + 2 * radio);
        va2.setDuration(1000);
        va2.setInterpolator(new AccelerateInterpolator());
        va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animY2 = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //if(tablero.getEstado()== Tablero.EN_CURSO)
            va2.start();
    }

    /**
     * Metodo llamada cuando hay un evento de tipo Touch
     *
     * @param event
     * @return true cuando ha sido atendido
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (listener !=null) {
                int c = xToColumn(event.getX());
                listener.onPlay(c);
            }
        }
        return true;
    }

    /**
     * Metodo que dibuja la view (el tablero en este caso)
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(va!=null && va2!=null){
            if(!va.isRunning() && !va2.isRunning()){
                GameActivity.mutex=false;
            }
        }

        personalizar();

        /*Dibujamos el rectangulo*/
        canvas.drawRect(0, 0, w, h, colorFondo);
        if(tablero!=null){

            /*Recorremos todas las casillas*/
            for(int i=0; i<TableroConecta4.FILAS; i++){
                for(int j=0; j<TableroConecta4.COLUMNAS; j++){

                    /*Para cada casilla averiguamos el centro*/

                    float centroX = j*w/TableroConecta4.COLUMNAS + radio;
                    float centroY = i*h/TableroConecta4.COLUMNAS + 2*radio;

                    /*Internet*/
                    Paint colorA = color1;
                    Paint colorB = color2;
                    if (cambio){ //&& (tablero.numMovs%2)!=0 ) {
                        colorA = color2;
                        colorB = color1;
                    }
                    if(tablero.ultimaJugada==i*10+j && C4PreferencesActivity.getInternet(getContext()) ) {
                        canvas.drawCircle(centroX, centroY, radio2, color0); //para que se vea blanco hasta que caiga
                        if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR1) {
                            canvas.drawCircle(centroX, animY, radio2, colorA);
                        } else if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR2) {
                            canvas.drawCircle(centroX, animY, radio2, colorB);
                        }
                        if (animY == i * h / TableroConecta4.COLUMNAS + 2 * radio)
                            GameActivity.mutex = false;

                    }else if(C4PreferencesActivity.getInternet(getContext())){
                        if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR1) {
                            canvas.drawCircle(centroX, centroY, radio2, colorA);
                        } else if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR2) {
                            canvas.drawCircle(centroX, centroY, radio2, colorB);
                        } else{
                            canvas.drawCircle(centroX, centroY, radio2, color0); //para que se vea blanco hasta que caiga
                        }

                    /*Ultima jugada, usamos la primera animacion*/
                    }else if(tablero.ultimaJugada==i*10+j && tablero.getNumJugadas()>2) {
                        canvas.drawCircle(centroX, centroY, radio2, color0); //para que se vea blanco hasta que caiga
                        if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR1) {
                            canvas.drawCircle(centroX, animY, radio2, color1);
                        } else if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR2){
                            canvas.drawCircle(centroX, animY, radio2, color2);
                        }
                        if(animY==i*h/TableroConecta4.COLUMNAS + 2*radio)
                            GameActivity.mutex=false;

                    /*Penultima jugada, usamos la seguna anumacion*/
                    }else if(tablero.penultimaJugada==i*10+j && tablero.getNumJugadas()>2 && tablero.getEstado()==TableroConecta4.EN_CURSO){
                        canvas.drawCircle(centroX, centroY, radio2, color0); //para que se vea blanco hasta que caiga
                        if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR1) {
                            canvas.drawCircle(centroX, animY2, radio2, color1);
                        } else if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR2) {
                            canvas.drawCircle(centroX, animY2, radio2, color2);
                        }

                    /*Jugada mas anterior sin animacion*/
                    }else {
                        if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR1) {
                            canvas.drawCircle(centroX, centroY, radio2, color1);
                        } else if (tablero.getPosicion(i, j) == TableroConecta4.JUGADOR2) {
                            canvas.drawCircle(centroX, centroY, radio2, color2);
                        } else{
                            canvas.drawCircle(centroX, centroY, radio2, color0); //para que se vea blanco hasta que caiga
                        }
                    }

                    if(figura==1){
                        float tmp = (float) 0.5;
                        canvas.drawCircle(centroX, centroY, radio2 - radio2*tmp, colorFondo);
                    }
                }
            }
        }
    }

    /**
     * Define el listener
     *
     * @param listener
     */
    public void setOnPlayListener(OnPlayListener listener) {
        this.listener = listener;
    }

    /**
     * Define el tablero
     *
     * @param tablero
     */
    public void setTablero(TableroConecta4 tablero) {
        personalizar();
        this.tablero = tablero;
    }

    private void personalizar(){
        figura = GameActivity.figurecode;
        if(GameActivity.fondocode.compareTo("azul")==0)
            colorFondo.setColor(Color.BLUE);
        else if(GameActivity.fondocode.compareTo("negro")==0)
            colorFondo.setColor(Color.BLACK);
        else
            colorFondo.setColor(Color.MAGENTA);
    }
}