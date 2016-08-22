/*
 * ServidorAdapter.java
 */
package es.uam.eps.dadm.connect4.servidor;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.activities.C4PreferencesActivity;
import es.uam.eps.dadm.connect4.database.Repository;

/**
 * Actividad para comunicarse con un servidor que implementa la interfaz Repository
 *
 * @see es.uam.eps.dadm.connect4.database.Repository
 * @author Manuel Reyes
 */
public class ServidorAdapter implements Repository {

    private String name;
    public static Resources resources;
    public static Context context;
    private InterfazConServidor interfazConServidor;
    private String gameid = "181";

    /**
     * Constructor de la clase
     *
     * @param context contexto de quien llama
     */
    public ServidorAdapter(Context context) {
        name= C4PreferencesActivity.getUserCode(context);
        resources = context.getResources();
        this.context = context;
    }

    /**
     * Abre un repository
     */
    @Override
    public void open() throws Exception {
        interfazConServidor = InterfazConServidor.getServer(context);
    }

    /**
     * Cierra un repository
     */
    @Override
    public void close() {
        interfazConServidor.closeInterfazConServidor();
    }

    /**
     * Funcion para loguearse
     *
     * @param playername nick
     * @param password contrasenya
     * @param callback objeto al que se llamara con los resultados
     */
    @Override
    public void login(final String playername, final String password, final LoginRegisterCallback callback) {
        interfazConServidor.login(
                playername,
                password,
                C4PreferencesActivity.getGCM(context),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Log.d("TAG", s);
                        if(s.compareTo("-1")==0){
                            callback.onError("nouser");
                        }else{
                            name=playername;
                            callback.onLogin(playername);
                            C4PreferencesActivity.setUserNumber(context, s);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callback.onError("volley");
                    }
                });
    }

    /**
     * Funcion que registra un jugador
     *
     * @param playername nick
     * @param password contrasenya
     * @param callback objeto al que se llamara con los resultados
     */
    @Override
    public void register(final String playername, final String password, final LoginRegisterCallback callback) {
        interfazConServidor.account(
                playername,
                password,
                C4PreferencesActivity.getGCM(context),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Log.d("TAG", s);
                        if(s.compareTo("-1")==0){
                            callback.onError("inuse");
                        }else{
                            callback.onError("created");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callback.onError("volley");
                    }
                });
    }

    /**
     * Funcion que anyade un resultado
     *
     * @param playeruuid identificador de jugador
     * @param time tiempo transcurrido
     * @param points puntos conseguidos
     * @param otherInfo campo extra
     * @param callback objeto al que se llamara con los resultados
     */
    @Override
    public void addResult(String playeruuid, int time, int points, String otherInfo, BooleanCallback callback) {
        interfazConServidor.addResult(
                playeruuid,
                time+"",
                points+"",
                otherInfo,
                gameid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
    }

    /**
     * Obtiene un resultado
     *
     * @param playeruuid identificador unico
     * @param orderByField campo por el cual ordenar
     * @param group agrupar
     * @param callback objeto al que se llamara con los resultados
     */
    @Override
    public void getResults(String playeruuid, String orderByField, final String group, final ResultsCallback callback) {

        final ArrayList<Result> list = new ArrayList<Result>();

        if(playeruuid.compareTo("//getPartida//")==0){
            interfazConServidor.finishedrounds(
                    C4PreferencesActivity.getUserNumber(context),
                    gameid,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i=0; i<response.length(); i++) {
                                if(Integer.parseInt(group)==i){
                                    try {
                                        JSONObject fila = (JSONObject) response.get(i);
                                        //Nick
                                        Result r = new Result();
                                        r.otherInfo = fila.getString("playernames").replaceAll(",", " VS ");
                                        list.add(r);
                                        //Fechas
                                        String tmp = (String) fila.get("dateevent");
                                        String tmp1 = tmp.substring(0, 10);
                                        String tmp2 = tmp.substring(10, 16);
                                        //a
                                        r = new Result();
                                        r.otherInfo = tmp1;
                                        list.add(r);
                                        //b
                                        r = new Result();
                                        r.otherInfo = tmp2;
                                        list.add(r);
                                        // -1
                                        r = new Result();
                                        r.otherInfo = "-1";
                                        list.add(r);
                                        //Tablero
                                        r = new Result();
                                        r.otherInfo = fila.getString("codedboard");
                                        list.add(r);
                                        //Ganador
                                        String[] jugadores = fila.getString("playernames").split(",");
                                        r = new Result();
                                        if(fila.getString("turn").equals("1")){
                                            r.otherInfo = jugadores[1];
                                        }else{
                                            r.otherInfo = jugadores[0];
                                        }
                                        list.add(r);
                                        //Fin
                                        callback.onResponse(list);
                                        return;

                                    }catch (Exception e){

                                    }
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                        }
                    });
        }
        else {
            interfazConServidor.finishedrounds(
                    playeruuid,
                    gameid,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject fila = (JSONObject) response.get(i);
                                    String tmp1 = fila.getString("playernames").replaceAll(",", " VS ");
                                    String tmp2 = (String) fila.get("dateevent");
                                    tmp2 = tmp2.substring(0, 16);
                                    String tmp = "" + tmp2 + " -> " + tmp1;
                                    Result r = new Result();
                                    r.otherInfo = tmp;
                                    list.add(r);

                                } catch (Exception e) {
                                }
                            }
                            callback.onResponse(list);
                            //Log.d("connect4", response.toString());
                            return;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                        }
                    });
        }
    }


    /**
     * Metodo para obtener una partida por indica
     *
     * @param numero indice
     * @param orderByField order by
     * @return ArrayList de cadenas con los resultados
     */
    public ArrayList<String> getPartida(final int numero, String orderByField){
        final ArrayList<String> res = new ArrayList<>();


        interfazConServidor.finishedrounds(
                C4PreferencesActivity.getUserNumber(context),
                gameid,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i<response.length(); i++) {
                            if(i==numero){
                                try {
                                    JSONObject fila = (JSONObject) response.get(i);
                                    String tmp = (String) fila.get("dateevent");
                                    String tmp1 = tmp.substring(0, 9);
                                    String tmp2 = tmp.substring(10, 14);
                                    res.add( fila.getString("playernames").replaceAll(",", " VS ") );
                                    res.add(tmp1);
                                    res.add(tmp2);
                                    res.add("-1");
                                    res.add( fila.getString("codeboard") );
                                    String[] jugadores = fila.getString("playernames").split(",");
                                    if(fila.getString("turn").equals("1")){
                                        res.add(jugadores[1]);
                                    }else{
                                        res.add(jugadores[0]);
                                    }
                                }catch (Exception e){
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });

        while(res.size()!=6){
        }
        return res;
    }
}