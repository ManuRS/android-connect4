/*
 * SelectActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.extra.MyArrayAdapter;
import es.uam.eps.dadm.connect4.servidor.InterfazConServidor;
import es.uam.eps.dadm.connect4.servidor.ServidorAdapter;

/**
 * Actividad para seleccionar partidas
 */
public class SelectActivity extends AppCompatActivity {

    private String gameid = "181";
    private ArrayList<String> roundsNuevasID;
    private ArrayList<String> roundsPendientesID;
    private ArrayList<String> roundsPendientesTablero;
    private ArrayList<String> roundsCurso;
    private ArrayList<String> roundsCursoNames;
    private ArrayList<String> roundsCursoYo;
    private ArrayList<String> roundsCursoYoTablero;
    private ArrayList<String> roundsPendientesRival;
    public String winner = "";

    private Handler periodica;

    public static boolean primerplano=false;
    public static boolean refresh=true;

    /**
     * Metodo del ciclo de vida que crea la actividad
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        primerplano=true;
        setContentView(R.layout.activity_select);
        roundsNuevasID = new ArrayList<>();
        roundsPendientesID = new ArrayList<>();
        roundsPendientesTablero = new ArrayList<>();
        roundsCurso = new ArrayList<>();
        roundsCursoNames = new ArrayList<>();
        roundsCursoYo = new ArrayList<>();
        roundsCursoYoTablero = new ArrayList<>();
        roundsPendientesRival =  new ArrayList<>();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getBoolean("fin", false) ){
            winner = bundle.getString("winner");
            new GameEndDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
        }else{
            pierdo();
        }
        try {
            Thread.sleep(50);
        } catch (Exception e) {}
        //refreshtablasA();
        //refreshtablasB();
        refresh=true;
        periodica = new Handler();
        actualizador.run();
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
     * Metodo del ciclo de vida
     *
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onPause(){
        super.onPause();
        refresh=false;
        primerplano=false;
        periodica.removeCallbacks(actualizador);
    }

    /**
     * Metodo del ciclo de vida
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        primerplano=true;
        refresh=true;
        try {
            Thread.sleep(50);
        } catch (Exception e) {}
        pierdo();
        //refreshtablasA();
        //refreshtablasB();
        periodica = new Handler();
        actualizador.run();
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_selector_movil, menu);

        //Tabla A
        ListView lv = (ListView)findViewById(R.id.list_pendientes);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Han seleccionado una partida pendiente, preparamos el roundID y el tablero

                /*MAPA*/
                mapa(roundsPendientesID.get(position));

                Intent i = new Intent(getApplicationContext(), GameActivity.class);
                i.putExtra("roundID", roundsPendientesID.get(position));
                i.putExtra("tablero", roundsPendientesTablero.get(position));
                i.putExtra("rival", roundsPendientesRival.get(position));
                startActivity(i);
                //Log.d("Connect4", "list=" + position);
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
                                mapa(roundsNuevasID.get(position));
                                //refreshtablasA();
                                refreshtablasB();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.d("connect4", "Server error 2");
                                //refreshtablasA();
                                //refreshtablasB();
                            }
                        });
                //Log.d("Connec4t", "list=" + position);
            }
        });

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
                                                Log.d("connect4", volleyError.getMessage());
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
     * Maneja los botones
     *
     * @param keyCode boton
     * @param event evento
     * @return atendido
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(this, MenuActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *
     * Llama al mapa adecuadamente (info del rival)
     *
     * @param roundID
     */
    private void mapa (String roundID){

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
