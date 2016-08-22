/*
 * GameActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.database.Repository;
import es.uam.eps.dadm.connect4.database.RepositoryFactory;
import es.uam.eps.dadm.connect4.extra.Music;
import es.uam.eps.dadm.connect4.extra.MyArrayAdapter;
import es.uam.eps.dadm.connect4.jugadoresConecta4.JugadorBot;
import es.uam.eps.dadm.connect4.jugadoresConecta4.JugadorRemoto;
import es.uam.eps.dadm.connect4.model.*;
import es.uam.eps.dadm.connect4.servidor.InterfazConServidor;
import es.uam.eps.dadm.connect4.servidor.ServidorAdapter;
import es.uam.eps.dadm.connect4.views.*;
import es.uam.eps.multij.*;

/**
 * Actividad para jugar de la aplicacion
 *
 * @author Manuel Reyes
 */
public class GameActivity extends AppCompatActivity implements Jugador, OnPlayListener {

    private TableroConecta4View view;
    private Partida partida;
    private ArrayList<Jugador> jugadores;
    private boolean musicOn = true;
    private GameActivity activity;
    public String winner = "";
    public static int figurecode=0;
    public static String fondocode="azul";
    public static boolean mutex = false;
    private String rival;
    private String ganador;
    private Chronometer chronometer;

    private String roundid;
    private String gameid = "181";
    private boolean off = false;
    private boolean mapoff = true;
    public static boolean refresh = true;
    private Handler periodica;
    public static boolean primerplano=false;

    private ArrayList<String> roundsNuevasID;
    private ArrayList<String> roundsPendientesID;
    private ArrayList<String> roundsPendientesTablero;
    private ArrayList<String> roundsCurso;
    private ArrayList<String> roundsCursoNames;
    private ArrayList<String> roundsCursoYo;
    private ArrayList<String> roundsCursoYoTablero;
    private ArrayList<String> roundsPendientesRival;



    /**
     * Se crea lo necesario para jugar y se escuchan los botones
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        primerplano=true;
        Jugador jugadorBot;
        String tLoad = "";

        if( C4PreferencesActivity.getInternet(getBaseContext()) && C4PreferencesActivity.getTabletCode(getBaseContext()) ){
            //Tablet e internet
            setContentView(R.layout.activity_game_tablet_online);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            jugadorBot = new JugadorRemoto("Remoto");
            roundsNuevasID = new ArrayList<>();
            roundsPendientesID = new ArrayList<>();
            roundsPendientesTablero = new ArrayList<>();
            roundsCurso = new ArrayList<>();
            roundsCursoNames = new ArrayList<>();
            roundsCursoYo = new ArrayList<>();
            roundsCursoYoTablero = new ArrayList<>();
            roundsPendientesRival =  new ArrayList<>();
            off=true;
            try {
                Thread.sleep(50);
            } catch (Exception e) {}
            pierdo();
            //refreshtablasA();
            //refreshtablasB();
            refresh=true;
            periodica = new Handler();
            actualizador.run();
        }

        else if( C4PreferencesActivity.getInternet(getBaseContext()) && !C4PreferencesActivity.getTabletCode(getBaseContext()) ){
            //Movil e internet
            setContentView(R.layout.activity_game);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            jugadorBot = new JugadorRemoto("Remoto");
            Bundle bundle = getIntent().getExtras();
            tLoad=bundle.getString("tablero");
            roundid=bundle.getString("roundID");
            rival=bundle.getString("rival");
            chronometer = (Chronometer) findViewById(R.id.chronometer);
            chronometer.setVisibility(View.GONE);
        }

        else{
            //Local y lo que sea
            setContentView(R.layout.activity_game);
            if (C4PreferencesActivity.getTabletCode(getBaseContext())) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if(C4PreferencesActivity.getModoBot(this).equals("1")){
                jugadorBot = new JugadorBot("Movil");
                rival = "MinMaxBot";
            }else{
                jugadorBot = new JugadorAleatorio("Movil");
                rival = "AleaBot";
            }
            chronometer = (Chronometer) findViewById(R.id.chronometer);
            chronometer.start();

        }

        /*Animacion*/
        TableroConecta4View v = (TableroConecta4View) findViewById(R.id.tableroConecta4View);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.tablero_anim);
        v.startAnimation(a);

        /*Personalizar*/
        figurecode = Integer.parseInt(C4PreferencesActivity.getFigureCode(this));
        fondocode = C4PreferencesActivity.getFondoCode(this);
        musicOn = C4PreferencesActivity.getMusic(this);

        jugadores = new ArrayList<Jugador>();
        jugadores.add(jugadorBot);
        jugadores.add(this);
        partida = new Partida(new TableroConecta4(), jugadores);
        if(!tLoad.equals("")){
            try {
                partida.getTablero().stringToTablero(tLoad);
            } catch (ExcepcionJuego excepcionJuego) {

            }
            if(partida.getTablero().getTurno()==0) {
                TableroConecta4View.cambio = true;
            }
        }

        /*View set*/
        view = (TableroConecta4View) findViewById(R.id.tableroConecta4View);
        view.setTablero((TableroConecta4)partida.getTablero());
        view.setOnPlayListener(this);

        /*Botones de la interfaz*/

        //Boton de reset
        /*Button botonReset = (Button) findViewById(R.id.buttonReset);
        botonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partida = new Partida(new TableroConecta4(), jugadores);
                view.setTablero((TableroConecta4) partida.getTablero());
                view.invalidate();
                partida.comenzar();
            }
        });*/

        activity = this;
        if(!C4PreferencesActivity.getTabletCode(getBaseContext()) || !C4PreferencesActivity.getInternet(getBaseContext())){
            /*No tablet o no internet*/

            //Musica on/off
            Button bottonMusic = (Button) findViewById(R.id.buttonSound);
            bottonMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(musicOn){
                        Music.pause(activity);
                    }else{
                        Music.play(activity, R.raw.thebuilder);
                    }
                    musicOn=!musicOn;
                }
            });

            //Boton about
            Button botonAbout = (Button) findViewById(R.id.buttonAbout);
            botonAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(findViewById(android.R.id.content), getText(R.string.aboutText), Snackbar.LENGTH_LONG).show();
                }
            });

        }

        /*Fin*/
        partida.comenzar();
    }

    /**
     * Para el cronometro
     *
     * @return tiempo transcurrido
     */

    private int stopChronometer (){
        chronometer.stop();
        int elapsedTime=0;
        String chronometerText = chronometer.getText().toString();
        String array[] = chronometerText.split(":");
        if (array.length == 2){
            elapsedTime = Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1]);
        } else if (array.length == 3){
            elapsedTime = Integer.parseInt(array[0]) * 60 * 60 + Integer.parseInt(array[1]) * 60 + Integer.parseInt(array[2]);
        }
        return elapsedTime;
    }


    /**Jugador**/

    /**
     * Indentificador del jugador
     */
    @Override
    public String getNombre() {
        return "Humano";
    }

    /**
     * Indica si un tablero es jugable por el jugador
     *
     * @param tablero
     * @return true if es TableroConecta4 else false
     */
    @Override
    public boolean puedeJugar(Tablero tablero) {
        return (tablero instanceof TableroConecta4);
    }

    /**
     * Recive eventos de la partida y actua en consecuencia
     *
     * @param evento
     */
    @Override
    public void onCambioEnPartida(Evento evento) {
        switch (evento.getTipo()) {
            case Evento.EVENTO_CAMBIO:
                view.invalidate();
                break;

            case Evento.EVENTO_CONFIRMA:
                break;

            case Evento.EVENTO_TURNO:
                break;

            case Evento.EVENTO_FIN:

                //finPartida();
                if(partida.getTablero().getEstado()==TableroConecta4.TABLAS)
                    ganador = " - ";
                else if(partida.getJugador(partida.getTablero().getTurno()).getNombre().equals("Humano"))
                    ganador=rival;
                else
                    ganador = C4PreferencesActivity.getUserCode(GameActivity.this);
                //
                if(!C4PreferencesActivity.getInternet(this)) {
                    TableroConecta4 t = (TableroConecta4) partida.getTablero();
                    //DataBaseAdapter repo = (DataBaseAdapter) RepositoryFactory.createRepository(GameActivity.this);
                    Repository repo = RepositoryFactory.createRepository(GameActivity.this);
                    Repository.BooleanCallback callback = null;
                    if(C4PreferencesActivity.getInternet(getBaseContext()))
                        repo.addResult(C4PreferencesActivity.getUserNumber(getBaseContext()), 0, t.numMovs, t.tableroToString()+"uuu"+ganador, callback);
                    else
                        repo.addResult(rival, stopChronometer(), t.numMovs, t.tableroToString()+"uuu"+ganador, callback);
                    //repo.addResultAll(rival, stopChronometer(), t.numMovs, t.tableroToString(), ganador, callback);
                }
                mutex=false;
                break;
        }
    }

    /**OnPlayListener**/

    /**
     * Recibe movimientos que realizar en la partida
     *
     * @param columna a la cual se mueve
     */
    @Override
    public void onPlay(int columna) {
        if(C4PreferencesActivity.getInternet(this) && !C4PreferencesActivity.getTabletCode(this)){
            //Internet y movil
            onPlayInternet(columna);
            return;
        }

        else if (C4PreferencesActivity.getInternet(this) && C4PreferencesActivity.getTabletCode(this)){
            if(off){
                Toast.makeText(GameActivity.this, getText(R.string.select), Toast.LENGTH_SHORT).show();
                return;
            }else{
                onPlayInternet(columna);
                off=true;
                return;
            }
        }

        else{
            //No internet
            if(mutex==false) {
                try {
                    partida.realizaAccion(new AccionMover(this, new MovimientoConecta4(columna)));
                } catch (Exception e) {
                    if (partida.getTablero().getEstado() != Tablero.EN_CURSO)
                        finPartida();
                    else
                        Toast.makeText(GameActivity.this, getText(R.string.badPlay), Toast.LENGTH_SHORT).show();
                    return;
                }
                view.setCaidas();
                return;
            }
        }
    }

    /**
     * Recibe movimientos que realizar en la partida
     *
     * @param columna a la cual se mueve
     */
    public void onPlayInternet(int columna) {
        try {
            partida.realizaAccion(new AccionMover(this, new MovimientoConecta4(columna)));
        } catch (Exception e) {
            if (partida.getTablero().getEstado()!=Tablero.EN_CURSO){
                finPartida();
            }else{
                Toast.makeText(GameActivity.this, getText(R.string.badPlay), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        boolean fin;
        if(partida.getTablero().getEstado()!=TableroConecta4.EN_CURSO)
            fin=true;
        else
            fin=false;
        //Movimiento permitido en local, podemos transmitirlo
        InterfazConServidor.getServer(this).newMovement(
                C4PreferencesActivity.getUserNumber(this),
                roundid,
                partida.getTablero().tableroToString(),
                fin,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("connect4", volleyError.getMessage());}
                }
        );

        view.setCaidasB();
    }

    /**
     * Informa de cual es la situación final de la partida
     */
    public void finPartida(){
        //Log.d("connect4", partida.getJugador(partida.getTablero().getTurno()).getNombre()+"");
        if(partida.getTablero().getEstado()==TableroConecta4.TABLAS) {
            winner = getText(R.string.tie).toString();
            //Toast.makeText(GameActivity.this, getText(R.string.tie), Toast.LENGTH_LONG).show();
        }

        else if(partida.getJugador(partida.getTablero().getTurno()).getNombre().equals("Humano")) {
            if(C4PreferencesActivity.getInternet(getBaseContext()))
                winner = getText(R.string.win).toString();
            else
                winner = getText(R.string.loose).toString();
            //Toast.makeText(GameActivity.this, getText(R.string.loose), Toast.LENGTH_LONG).show();
        }
        else if(partida.getJugador(partida.getTablero().getTurno()).getNombre().equals("Movil")) {
            winner = getText(R.string.win).toString();
            //Toast.makeText(GameActivity.this, getText(R.string.win), Toast.LENGTH_LONG).show();
        }
        else if(partida.getJugador(partida.getTablero().getTurno()).getNombre().equals("Remoto")) {
            winner = getText(R.string.win).toString();
            //Toast.makeText(GameActivity.this, getText(R.string.win), Toast.LENGTH_LONG).show();
        }

        if(C4PreferencesActivity.getInternet(this) && !C4PreferencesActivity.getTabletCode(this)){
            finPartidaOnline();
        }else if (C4PreferencesActivity.getInternet(this)){
            finPartidaOnlineT();
        }else{
            finPartidaOffline();
        }

    }

    /**
     * Informa de cual es la situación final de la partida
     */
    private void finPartidaOnline(){
        msgVictoria();
        Intent i = new Intent (this, SelectActivity.class);
        i.putExtra("fin", true);
        i.putExtra("winner", winner);
        startActivity(i);
    }

    /**
     * Informa de cual es la situación final de la partida
     */
    private void finPartidaOnlineT(){
        msgVictoria();
        new GameEndDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
    }

    /**
     * Informa de cual es la situación final de la partida
     */
    private void finPartidaOffline(){
        new GameEndDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
        view.setCaidas();
        mutex=false;
    }

    /**
     * Informa de cual es la situación final de la partida
     */
    private void msgVictoria(){
        InterfazConServidor.getServer(this).sendMsgUser(C4PreferencesActivity.getUserNumber(this), rival, "jeje");
    }

    /**
     * Informa de cual es la situación final de la partida
     */
    private void pierdo(){
        InterfazConServidor.getServer(this).getMsgUser(
                C4PreferencesActivity.getUserNumber(this),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length()==0)
                            return;
                        winner = getString(R.string.loose)+ " " + response.length() + " " + getString(R.string.loose2) +" ";
                        for (int i=0; i<response.length(); i++){
                            try{
                                JSONObject fila = (JSONObject) response.get(i);
                                winner = winner + fila.getString("playername");
                                if(i==response.length()-2)
                                    winner = winner + " & ";
                                else if(i<response.length()-2)
                                    winner = winner + ", ";
                            }catch (Exception e){}

                        }
                        new GameEndDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        );
    }

    /**
     * Actualizador de las listas de partidas
     */
    Runnable actualizador = new Runnable() {
        @Override
        public void run() {
            try{
                /*
                Aqui hay un comentario importante que hacer.
                Mi idea era que cuando gcm avise refresquemos las tablas
                sin embargo gcm a veces (bastante en mi caso) no es instantaneo
                si no que tarda (pueden llegar a ser esperas de minutos)
                esto para las notificaciones no afecta demasiado, pero cuando
                yuno esta en la lista si que se nota si la otra persona esta
                contestando al momento.
                Dejo la linea buena comentada
                */
                // if(refresh && primerplano) {
                if(primerplano) {
                    refreshtablasA();
                    refreshtablasB();
                    refresh=false;
                }
            }
            finally {
                periodica.postDelayed(actualizador, 6000); //A los 6s volvera
            }
        }
    };

    /**Ciclo de vida**/

    /**
     * Metodo del ciclo de vida
     *
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onResume(){
        super.onResume();
        primerplano=true;
        if(musicOn)Music.play(this, R.raw.thebuilder);
        if(C4PreferencesActivity.getTabletCode(getBaseContext()) && C4PreferencesActivity.getInternet(getBaseContext())){
            try {
                Thread.sleep(50);
            } catch (Exception e) {}
            pierdo();
            //refreshtablasA();
            //refreshtablasB();
            refresh=true;
            periodica = new Handler();
            actualizador.run();
        }
    }

    /**
     * Metodo del ciclo de vida
     *
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onPause(){
        super.onPause();
        refresh=false;
        primerplano=false;
        Music.pause(this);
        if(C4PreferencesActivity.getInternet(getBaseContext()) && C4PreferencesActivity.getTabletCode(getBaseContext()))
            periodica.removeCallbacks(actualizador);
    }

    /**
     * Metodo del ciclo de vida
     *
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        outState.putString("tablero", partida.getTablero().tableroToString());
        outState.putBoolean("music", musicOn);
        super.onSaveInstanceState(outState);
    }

    /**
     * Metodo del ciclo de vida
     *
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    public void onRestoreInstanceState (Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        musicOn=savedInstanceState.getBoolean("music");
        try {
            partida.getTablero().stringToTablero(savedInstanceState.getString("tablero"));
            if(partida.getTablero().getEstado()==Tablero.EN_CURSO)
                partida.continuar();
            view.invalidate();
        } catch (ExcepcionJuego ignored) {
        }
    }

    /**
     * Maneja los botones
     *
     * @param keyCode boton
     * @param event evento
     * @return atendido
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && C4PreferencesActivity.getInternet(this) && !C4PreferencesActivity.getTabletCode(this)) {
            Toast.makeText(GameActivity.this, getString(R.string.makemove), Toast.LENGTH_SHORT).show();
            return true;
        }*/
        return super.onKeyDown(keyCode, event);
    }

    /*MENU*/

    /**
     * Infla las opciones del boton menu
     *
     * @param menu menu
     * @return atendido
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if(C4PreferencesActivity.getInternet(this) && !C4PreferencesActivity.getTabletCode(this)){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.game_selector, menu);
        }

        if(C4PreferencesActivity.getInternet(this) && C4PreferencesActivity.getTabletCode(this)){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_selector, menu);

            //Tabla A
            ListView lv = (ListView)findViewById(R.id.list_pendientes);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                    //Han seleccionado una partida pendiente, preparamos el roundID y el tablero
                    mapa(roundsPendientesID.get(position));
                    try{
                        partida.getTablero().stringToTablero( roundsPendientesTablero.get(position) );
                    }catch (Exception e){}
                    //Log.d("connect4", partida.getTablero().getTurno()+"");
                    if(partida.getTablero().getTurno()==0) {
                        TableroConecta4View.cambio = true;
                    }
                    rival=roundsPendientesRival.get(position);
                    roundid=roundsPendientesID.get(position);
                    off=false;
                    view.invalidate();
                    Toast.makeText(getBaseContext(), getText(R.string.load), Toast.LENGTH_SHORT).show();
                    //Log.d("Connect4", "list=" + partida.getTablero().tableroToString());
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {}
                    pierdo();
                    //refreshtablasA();
                    //refreshtablasB();
                }
            });

            //Tabla B
            ListView lv2 = (ListView)findViewById(R.id.list_join);
            lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    InterfazConServidor.getServer(getBaseContext()).addPlayerRound(
                            C4PreferencesActivity.getUserNumber(getBaseContext()),
                            roundsNuevasID.get(position),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    if (s.equals("0")) {
                                        Log.d("connect4", "Server error 1");
                                    }
                                    Toast.makeText(getBaseContext(), getText(R.string.rivalstart), Toast.LENGTH_SHORT).show();
                                    try {
                                        Thread.sleep(100);
                                    } catch (Exception e) {}
                                    pierdo();
                                    mapa(roundsNuevasID.get(position));
                                    //refreshtablasA();
                                    refreshtablasB();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Log.d("connect4", "Server error 2");
                                }
                            });
                    //Log.d("Connec4t", "list=" + position);
                }
            });
        }
        return true;
    }

    /**
     * Refresca la tabla superior
     */
    public void refreshtablasA(){
        //coger partidas activas
        InterfazConServidor.getServer(getBaseContext()).activeRounds(
                C4PreferencesActivity.getUserNumber(getBaseContext()),
                gameid,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            roundsCurso.clear();
                            roundsCursoNames.clear();
                            roundsCursoYo.clear();
                            roundsCursoYoTablero.clear();
                            roundsPendientesID.clear();
                            roundsPendientesTablero.clear();
                            roundsPendientesRival.clear();
                            //Parseamos JSON
                            String[] cadenas = new String[response.length()];
                            for (int i=0; i<response.length(); i++){
                                JSONObject fila = (JSONObject) response.get(i);
                                roundsCurso.add(fila.getString("roundid"));
                                roundsCursoNames.add(fila.getString("playernames"));
                            }
                            //Ahora tenemos las partidas en las que participamos
                            //Las vamos a mostrar solo si nos toca jugar
                            for(String rid: roundsCurso){
                                Thread.sleep(50);
                                InterfazConServidor.getServer(getBaseContext()).isMyTurn(
                                        C4PreferencesActivity.getUserNumber(getBaseContext()),
                                        rid,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    roundsCursoYo.add(response.getString("turn"));
                                                    roundsCursoYoTablero.add(response.getString("codedboard"));
                                                    if(roundsCurso.size()==roundsCursoYo.size()){
                                                        //Es la ultima, imprimamos resultado
                                                        ArrayList <String> cadenasTmp = new ArrayList<String>();
                                                        for (int i=0; i<roundsCurso.size(); i++){
                                                            if(roundsCursoYo.get(i).equals("1")){
                                                                //Nos toca mover
                                                                String name = roundsCursoNames.get(i);
                                                                String[] jugadores = name.split(",");
                                                                if(jugadores[0].equals(C4PreferencesActivity.getUserCode(getBaseContext())))
                                                                    roundsPendientesRival.add(jugadores[1]);
                                                                else
                                                                    roundsPendientesRival.add(jugadores[0]);
                                                                name = name.replaceAll(",", " VS ");
                                                                cadenasTmp.add(getString(R.string.mover) + " " + name);
                                                                roundsPendientesID.add(roundsCurso.get(i));
                                                                roundsPendientesTablero.add(roundsCursoYoTablero.get(i));
                                                                //Log.d("connect4", roundsPendientesTablero.get(i));
                                                            }
                                                        }
                                                        String[] cadenasRes = new String[cadenasTmp.size()];
                                                        int i = 0;
                                                        for (String str: cadenasTmp){
                                                            cadenasRes[i++]=str;
                                                        }
                                                        MyArrayAdapter adapter3 = new MyArrayAdapter(getBaseContext(), cadenasRes);
                                                        ListView lv3 = (ListView)findViewById(R.id.list_pendientes);
                                                        lv3.setAdapter(adapter3);


                                                    }
                                                }catch (Exception e) {
                                                    Log.d("connect4", "Server error 3a");
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                Log.d("connect4", volleyError.getMessage()+" ");
                                            }
                                        }
                                );
                            }
                        } catch (Exception e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
    }

    /**
     * Refresca la tabla inferior
     */
    public void refreshtablasB(){
        InterfazConServidor.getServer(getBaseContext()).openRounds(
                C4PreferencesActivity.getUserNumber(getBaseContext()),
                gameid,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            roundsNuevasID.clear();
                            //Parseamos JSON
                            ArrayList <String> cadenas = new ArrayList<String>();
                            for (int i=0; i<response.length(); i++){
                                JSONObject fila = (JSONObject) response.get(i);
                                //solo añadimos en las que no estemos nosotros mismos
                                if(!fila.getString("playernames").equals(C4PreferencesActivity.getUserCode(getBaseContext()))){
                                    cadenas.add(getString(R.string.contra) + " " + fila.getString("playernames"));
                                    roundsNuevasID.add(fila.getString("roundid"));
                                }
                            }
                            //preparacion visual de la cadena
                            String[] cadenas2 = new String[cadenas.size()];
                            int i=0;
                            for (String s: cadenas) {
                                cadenas2[i++] = s;
                            }
                            MyArrayAdapter adapter3 = new MyArrayAdapter(getBaseContext(), cadenas2);
                            ListView lv3 = (ListView)findViewById(R.id.list_join);
                            lv3.setAdapter(adapter3);

                        } catch (JSONException e) {
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
    }

    /**
     * Determina comportamiento de las opciones del menu
     *
     * @param item
     * @return atendido
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.mapa:
                if(mapoff && C4PreferencesActivity.getTabletCode(this)){
                    Toast.makeText(GameActivity.this, getText(R.string.select), Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(MapFragment.lat_ext==null || MapFragment.lat_ext.equals("-1")){
                    Toast.makeText(GameActivity.this, getText(R.string.mapnoshare), Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent;
                intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                return true;

            /*case R.id.refreshtablas:
                pierdo();
                refreshtablasA();
                refreshtablasB();
                Snackbar.make(findViewById(android.R.id.content), R.string.refresh, Snackbar.LENGTH_LONG).show();
                return true;*/

            case R.id.newtabla:
                ServidorAdapter sa = new ServidorAdapter(this);
                try {
                    //Creamos nueva partida
                    InterfazConServidor.getServer(getBaseContext()).newRound(
                            C4PreferencesActivity.getUserNumber(getBaseContext()),
                            gameid,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    //refresh
                                    Toast.makeText(getBaseContext(), R.string.gcreated, Toast.LENGTH_SHORT).show();
                                    try {
                                        Thread.sleep(100);
                                    } catch (Exception e) {}
                                    pierdo();
                                    //refreshtablasA();
                                    //refreshtablasB();
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                }
                            });
                } catch (Exception e) {
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Llama al mapa adecuadamente (info del rival)
     *
     * @param roundID
     */
    private void mapa (String roundID){
        /*MAPA*/
        if(!C4PreferencesActivity.getLat(this).equals("-1")) {
            InterfazConServidor.getServer(getBaseContext()).sendMsgPartida(
                    C4PreferencesActivity.getUserNumber(getBaseContext()),
                    roundID,
                    C4PreferencesActivity.getLat(this)+"u"+C4PreferencesActivity.getLon(this));
        }

        InterfazConServidor.getServer(getBaseContext()).getMsgPartida(
                roundID,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{

                            for(int i=response.length()-1; i>=0; i--){
                                JSONObject fila = (JSONObject) response.get(i);
                                if(!fila.getString("playername").equals(C4PreferencesActivity.getUserCode(getBaseContext()))){
                                    String res = fila.getString("message");
                                    String[] resSplit = res.split("u");
                                    MapFragment.lat_ext = resSplit[0];
                                    MapFragment.lon_ext = resSplit[1];
                                    mapoff=false;
                                    return;
                                }
                            }

                            MapFragment.lat_ext=null;
                            MapFragment.lon_ext=null;
                            Log.d("connect4", "No coordenadas");

                        }
                        catch (Exception e){}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        );
    }
}
