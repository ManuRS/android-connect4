/*
 * InterfazConServidor.java
 */
package es.uam.eps.dadm.connect4.servidor;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Clse que se comunica con el servidor
 */
public class InterfazConServidor {

    private static final String BASE_URL = "http://ptha.ii.uam.es/juegosreunidos/";

    private static final String ACCOUNT_PHP = "account.php"; //registros y accesos
    private static final String OPENROUNDS_PHP = "openrounds.php"; //
    private static final String ACTIVEROUNDS_PHP = "activerounds.php"; //
    private static final String FINISHEDROUNDS_PHP = "finishedrounds.php"; //partidas terminadas
    private static final String NEWROUND_PHP = "newround.php"; //crea una partida
    private static final String ADDPLAYERROUND_PHP = "addplayertoround.php"; //unirse a una partida
    private static final String ISMYTURN_PHP = "ismyturn.php"; //devuelve estado del tablero y turno
    private static final String NEWMOVEMENT_PHP = "newmovement.php"; //realiza el movimiento
    private static final String SENDMSG_PHP = "sendmsg.php"; // enviar mensaje
    private static final String GETMSGS_PHP = "getmsgs.php"; // recibir mensaje
    private static final String ROUND_PHP = "roundhistory.php"; // historial de una partida
    private static final String ADDRESULT_PHP = "addresult.php"; //añade datos al final de la partida
    private static final String GETRESULT_PHP = "getresults.php"; // recupera datos de una partida

    private RequestQueue queue;
    private static InterfazConServidor serverInterface;

    /**
     * Constructor privado, para cumplir que la clase sea singleton
     * @param context contexto
     */
    private InterfazConServidor(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    /**
     * Proporciona una instancia de la clase
     * @param context contexto
     * @return InterfazConServidor
     */
    public static InterfazConServidor getServer(Context context) {
        if (serverInterface == null) {
            serverInterface = new InterfazConServidor(context);
        }
        return serverInterface;
    }

    /**
     * Cierra la conexion
     */
    public void closeInterfazConServidor(){
        if (serverInterface!=null){
            queue.stop();
            queue=null;
            serverInterface=null;
        }
    }

    /**
     * Registro en el servidor
     *
     * @param playername nombre del jugador
     * @param playerpass contraseña del jugador
     * @param gcmregid GCM
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void account (final String playername,
                         final String playerpass,
                         final String gcmregid,
                         Response.Listener<String> callback,
                         Response.ErrorListener errorCallback) {

        String url = BASE_URL + ACCOUNT_PHP;
        StringRequest r = new StringRequest(Request.Method.POST, url, callback, errorCallback) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("playername", playername);
                params.put("playerpassword", playerpass);
                params.put("gcmregid", gcmregid);
                return params;
            }
        };

        queue.add(r);
    }

    /**
     * Login en el servidor
     *
     * @param playername nombre del jugador
     * @param playerpass contraseña del jugador
     * @param gcmregid GCM
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void login (final String playername,
                       final String playerpass,
                       final String gcmregid,
                       Response.Listener<String> callback,
                       Response.ErrorListener errorCallback) {

        String url = BASE_URL + ACCOUNT_PHP;
        StringRequest r = new StringRequest(Request.Method.POST, url, callback, errorCallback) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("playername", playername);
                params.put("playerpassword", playerpass);
                params.put("gcmregid", gcmregid);
                params.put("login", "");
                return params;
            }
        };

        queue.add(r);
    }

    /**
     * Crea una partida
     *
     * @param playerid id del jugador
     * @param gameid id del juego
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void newRound (final String playerid,
                          final String gameid,
                          Response.Listener<String> callback,
                          Response.ErrorListener errorCallback) {

        String url = BASE_URL + NEWROUND_PHP;
        StringRequest r = new StringRequest(Request.Method.POST, url, callback, errorCallback){
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("playerid", playerid);
                params.put("gameid", gameid);
                params.put("codedboard", "0000000000000000000000000000000000000000001");
                return params;
            }
        };
        queue.add(r);
    }

    /**
     * Consulta rondas abiertas
     *
     * @param playerid id del jugador
     * @param gameid id del juego
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void openRounds(
                        final String playerid,
                        final String gameid,
                        Response.Listener<JSONArray> callback,
                        Response.ErrorListener errorCallback) {

        String url = BASE_URL + OPENROUNDS_PHP + "?"
                     + "playerid=" + playerid + "&"
                     + "gameid="   + gameid;

        JsonArrayRequest r = new JsonArrayRequest(url, callback, errorCallback);
        queue.add(r);
    }

    /**
     * Consulta rondas activas
     *
     * @param playerid id del jugador
     * @param gameid id del juego
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void activeRounds(
                        final String playerid,
                        final String gameid,
                        Response.Listener<JSONArray> callback,
                        Response.ErrorListener errorCallback) {

        String url = BASE_URL + ACTIVEROUNDS_PHP + "?"
                + "playerid=" + playerid + "&"
                + "gameid="   + gameid;

        JsonArrayRequest r = new JsonArrayRequest(url, callback, errorCallback);
        queue.add(r);
    }

    /**
     * Une un jugador a una ronda
     *
     * @param playerid id del jugador
     * @param roundid id de la ronda
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void addPlayerRound(
                        final String playerid,
                        final String roundid,
                        Response.Listener<String> callback,
                        Response.ErrorListener errorCallback) {

        String url = BASE_URL + ADDPLAYERROUND_PHP;
        StringRequest r = new StringRequest(Request.Method.POST, url, callback, errorCallback){
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("playerid", playerid);
                params.put("roundid", roundid);
                return params;
            }
        };
        queue.add(r);
    }

    /**
     * Consulta si le toca a un jugador mover en una ronda
     *
     * @param playerid id del jugador
     * @param roundid id de la ronda
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void isMyTurn(
                    final String playerid,
                    final String roundid,
                    Response.Listener<JSONObject> callback,
                    Response.ErrorListener errorCallback) {

        String url = BASE_URL + ISMYTURN_PHP + "?"
                + "playerid=" + playerid + "&"
                + "roundid="   + roundid;

        //Log.d("connect4", url);
        JsonObjectRequest r = new JsonObjectRequest(url, null, callback, errorCallback);
        //JsonArrayRequest r = new JsonArrayRequest(url, callback, errorCallback);
        queue.add(r);
    }

    /**
     * Realiza un movimiento en una ronda
     *
     * @param playerid id del jugador
     * @param roundid id de la ronda
     * @param codedboard tablero
     * @param fin partida finalizada
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void newMovement(
                final String playerid,
                final String roundid,
                final String codedboard,
                boolean fin,
                Response.Listener<JSONObject> callback,
                Response.ErrorListener errorCallback) {

        String url = BASE_URL + NEWMOVEMENT_PHP + "?"
                + "playerid=" + playerid + "&"
                + "roundid="   + roundid + "&"
                + "codedboard=" + codedboard;

        if(fin)
            url = url + "&finished";

        //Log.d("connect4", url);
        JsonObjectRequest r = new JsonObjectRequest(url, null, callback, errorCallback);
        queue.add(r);
    }

    /**
     * Añade un resultado
     *
     * @param playerid id del jugador
     * @param gameid id del juego
     * @param roundtime duracion
     * @param points puntos
     * @param otherinfo adicional
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void addResult(
                final String playerid,
                final String gameid,
                final String roundtime,
                final String points,
                final String otherinfo,
                Response.Listener<String> callback,
                Response.ErrorListener errorCallback){

        String url = BASE_URL + ADDRESULT_PHP;
        StringRequest r = new StringRequest(Request.Method.POST, url, callback, errorCallback){
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("playerid", playerid);
                params.put("roundid", roundtime);
                params.put("points", points);
                params.put("otherinfo", otherinfo);
                params.put("gameid", gameid);
                return params;
            }
        };
        queue.add(r);

    }

    /**
     * Consulta resultado
     *
     * @param playerid id del jugador
     * @param gameid id del juego
     * @param groupbyuser groupbyuser
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void getResults(
                final String playerid,
                final String gameid,
                final boolean groupbyuser,
                Response.Listener<JSONArray> callback,
                Response.ErrorListener errorCallback){

        String url = BASE_URL + GETRESULT_PHP + "?"
                + "playerid=" + playerid + "&"
                + "gameid="   + gameid;

        if(groupbyuser)
            url = url + "&groupbyuser";

        JsonArrayRequest r = new JsonArrayRequest(url, callback, errorCallback);
        queue.add(r);
    }

    /**
     * Consulta resultado
     *
     * @param playerid id del jugador
     * @param gameid id del juego
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void finishedrounds(
                final String playerid,
                final String gameid,
                Response.Listener<JSONArray> callback,
                Response.ErrorListener errorCallback) {

        String url = BASE_URL + FINISHEDROUNDS_PHP + "?"
                + "playerid=" + playerid + "&"
                + "gameid="   + gameid;

        JsonArrayRequest r = new JsonArrayRequest(url, callback, errorCallback);
        queue.add(r);
    }

    /**
     * Envia mensaje a un usuario
     *
     * @param playerid id del jugador
     * @param to nick del destinatario
     * @param msg mensaje
     */
    public void sendMsgUser(
                final String playerid,
                final String to,
                final String msg){

        String url = BASE_URL + SENDMSG_PHP + "?"
                +"playerid=" + playerid + "&"
                +"to=" + to + "&"
                +"msg=" + msg;
        //Log.d("connect4", url);

        Response.Listener<String> callback = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        };

        StringRequest r = new StringRequest(url, callback, null);
        queue.add(r);
    }

    /**
     * Consulta mensaje de un usuario
     *
     * @param playerid id del jugador
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void getMsgUser(
                final String playerid,
                Response.Listener<JSONArray> callback,
                Response.ErrorListener errorCallback){

        String url = BASE_URL + GETMSGS_PHP + "?"
                + "playerid=" + playerid + "&"
                + "markasread";

        JsonArrayRequest r = new JsonArrayRequest(url, callback, errorCallback);
        queue.add(r);
    }

    /**
     * Envia mensaje a una ronda
     *
     * @param playerid id del jugador
     * @param toround id de la ronda
     * @param msg mensaje
     */
    public void sendMsgPartida(
            final String playerid,
            final String toround,
            final String msg){

        String url = BASE_URL + SENDMSG_PHP + "?"
                +"playerid=" + playerid + "&"
                +"toround=" + toround + "&"
                +"msg=" + msg;

        //Log.d("connect4", url);

        Response.Listener<String> callback = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        };

        StringRequest r = new StringRequest(url, callback, null);
        queue.add(r);
    }

    /**
     * Consulta mensaje de una ronda
     *
     * @param roundid id de la ronda
     * @param callback metodo de callback
     * @param errorCallback callback on error
     */
    public void getMsgPartida(
            final String roundid,
            Response.Listener<JSONArray> callback,
            Response.ErrorListener errorCallback){

        String url = BASE_URL + GETMSGS_PHP + "?"
                + "roundid=" + roundid;
        //Log.d("connect4", url);
        JsonArrayRequest r = new JsonArrayRequest(url, callback, errorCallback);
        queue.add(r);
    }
}
