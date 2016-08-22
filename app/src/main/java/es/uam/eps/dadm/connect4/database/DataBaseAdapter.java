/*
 * DataBaseAdapter.java
 */
package es.uam.eps.dadm.connect4.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.activities.C4PreferencesActivity;

/**
 * Actividad para manejar la base de datos de C4 que implementa la interfaz Repository
 *
 * @see es.uam.eps.dadm.connect4.database.Repository
 * @author Manuel Reyes
 */
public class DataBaseAdapter implements Repository {


    private static final String DEBUG_TAG = "DatabaseAdapter";
    private static final String DATABASE_NAME = "esuamepsdadmconnect4.db";
    //Version que debemos incrementar cuando hagamos cambios en la estrucutra de la db, para los dispositivos que actualicen la app
    private static final int DATABASE_VERSION = 4;
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private String name;
    public static Resources resources;


    /**
     * Constructor de la clase
     *
     * @param context contexto de quien llama
     */
    public DataBaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
        name=C4PreferencesActivity.getUserCode(context);
        resources = context.getResources();
    }

    /**
     * Clase privada que se encarga de crear la base de datos o actualizarla
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Constructor de la clase
         *
         * @param context contexto de quien llama
         */
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Funcion que crea la base de datos
         *
         * @param db objeto SQLiteDatabase
         */
        public void onCreate(SQLiteDatabase db) {
            createTable(db);
        }

        /**
         * Funcion que actua cuando se actualiza la version de DataBaseAdapter.java
         *
         * @param db objeto SQLiteDatabase
         * @param oldVersion num version vieja
         * @param newVersion num version nueva
         */
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RoundTable.TABLE_NAME);
            Log.w(DEBUG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ". All data will be deleted");
            createTable(db);
        }

        /**
         * Consultas de creacion de la db
         *
         * @param db objeto SQLiteDatabase
         */
        private void createTable(SQLiteDatabase db) {

            String str1 = "CREATE TABLE " + UserTable.TABLE_NAME + " ("
                    + UserTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + UserTable.NICK + " TEXT UNIQUE, "
                    + UserTable.PASSWORD + " TEXT, "
                    + UserTable.NUM_JUGADAS + " INTEGER, "
                    + UserTable.NUM_GANADAS + " INTEGER);";

            String str2 = "CREATE TABLE " + RoundTable.TABLE_NAME + " ("
                    + RoundTable.NICK + " TEXT, "
                    + RoundTable.NICKRIVAL + " TEXT, "
                    + RoundTable.DURACION + " INTEGER, "
                    + RoundTable.NPIEZAS + " INTEGER, "
                    + RoundTable.GANO + " TEXT, "
                    + RoundTable.TABLERO + " TEXT, "
                    + RoundTable.FECHA + " DATE);";

            String str3 = "INSERT INTO " + UserTable.TABLE_NAME + " ("
                    + UserTable.NICK + ","
                    + UserTable.PASSWORD + ","
                    + UserTable.NUM_JUGADAS + ","
                    + UserTable.NUM_GANADAS + ")"
                    + " VALUES ("
                    + "'" + "guest" + "',"
                    + "'" + "guest" + "',"
                    + 0 + ","
                    + 0 + ");";

            db.execSQL(str1);
            db.execSQL(str2);
            db.execSQL(str3);
        }
    }

    /*REPOSITORY*/

    /**
     * Abre un repository
     */
    @Override
    public void open() throws Exception {
        db = helper.getWritableDatabase();
    }

    /**
     * Cierra un repository
     */
    @Override
    public void close() {
        db.close();
    }

    /**
     * Funcion para loguearse
     *
     * @param playername nick
     * @param password contrasenya
     * @param callback objeto al que se llamara con los resultados
     */
    @Override
    public void login(String playername, String password, LoginRegisterCallback callback) {

        String str1 = "SELECT * FROM "
                + UserTable.TABLE_NAME
                + " where '" + playername + "'=" + UserTable.NICK
                + " and '" + password + "'=" + UserTable.PASSWORD + ";";

        Cursor cursor = db.rawQuery(str1, null);
        int count = cursor.getCount();

        if (count == 1) {
            name=playername;
            callback.onLogin(playername);
        } else {
            callback.onError("nouser");
        }
    }

    /**
     * Funcion que registra un jugador
     *
     * @param playername nick
     * @param password contrasenya
     * @param callback objeto al que se llamara con los resultados
     */
    @Override
    public void register(String playername, String password, LoginRegisterCallback callback) {

        String str1 = "SELECT * FROM "
                + UserTable.TABLE_NAME
                + " where '" + playername + "'=" + UserTable.NICK + ";";

        Cursor cursor = db.rawQuery(str1, null);

        int count = cursor.getCount();
        if(count == 1){
            callback.onError("inuse");
            return;
        }

        String str2 = "INSERT INTO " + UserTable.TABLE_NAME + " ("
                + UserTable.NICK + ","
                + UserTable.PASSWORD + ","
                + UserTable.NUM_JUGADAS + ","
                + UserTable.NUM_GANADAS + ")"
                + " VALUES ("
                + "'" + playername + "',"
                + "'" + password + "',"
                + 0 + ","
                + 0 + ");";

        db.execSQL(str2);
        callback.onError("created");
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
        String[] otherInfoSplit = otherInfo.split("uuu");
        /*Añadimos ronda*/
        String str = "INSERT INTO " + RoundTable.TABLE_NAME + " ("
                + RoundTable.NICK + ","
                + RoundTable.NICKRIVAL + ","
                + RoundTable.DURACION + ","
                + RoundTable.NPIEZAS + ","
                + RoundTable.FECHA + ","
                + RoundTable.TABLERO + ","
                + RoundTable.GANO + ")"
                + " VALUES ("
                + "'" + name + "',"
                + "'" + playeruuid + "',"
                + time + ","
                + points + ","
                + "date('now'),"
                + "'" + otherInfoSplit[0] + "',"
                + "'" + otherInfoSplit[1] + "');";

        /*Actualizamos info del jugador*/
        String str1 = "UPDATE " + UserTable.TABLE_NAME + " SET "
                + UserTable.NUM_JUGADAS + "=" + UserTable.NUM_JUGADAS + "+1 "
                + "WHERE " + UserTable.NICK + "='" + name + "';";

        db.execSQL(str);
        db.execSQL(str1);

        if(otherInfoSplit[1].compareTo(playeruuid)!=0){
            /*Actualizamos info del jugador*/
            String str2 = "UPDATE " + UserTable.TABLE_NAME + " SET "
                    + UserTable.NUM_GANADAS + "=" + UserTable.NUM_GANADAS + "+1 "
                    + "WHERE " + UserTable.NICK + "='" + name + "';";
            db.execSQL(str2);
        }
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
    public void getResults(String playeruuid, String orderByField, String group, ResultsCallback callback) {

        ArrayList<Result> list = new ArrayList<Result>();

        if(playeruuid!=null){

            if(playeruuid.compareTo("//getPartida//")==0){
                if(orderByField!=null) {
                    if (orderByField.compareTo("fecha") == 0 || orderByField.compareTo("nick") == 0) {
                        String str1 = "SELECT * "
                                + " FROM " + RoundTable.TABLE_NAME
                                + " ORDER BY " + orderByField + ";";

                        Cursor c = db.rawQuery(str1, null);
                        int i=0;
                        while (c.moveToNext()) {
                            if(Integer.parseInt(group)==i){
                                //Nick
                                String tmp = "" + c.getString(c.getColumnIndex(RoundTable.NICK))
                                        + " VS "
                                        + c.getString(c.getColumnIndex(RoundTable.NICKRIVAL));
                                Result r = new Result();
                                r.otherInfo=tmp;
                                list.add(r);
                                //Fecha
                                tmp =  " " + c.getString(c.getColumnIndex(RoundTable.FECHA));
                                r = new Result();
                                r.otherInfo=tmp;
                                list.add(r);
                                //Duracion
                                tmp =  " " + c.getString(c.getColumnIndex(RoundTable.DURACION));
                                r = new Result();
                                r.otherInfo=tmp;
                                list.add(r);
                                //Npiezas
                                tmp =  " " + c.getString(c.getColumnIndex(RoundTable.NPIEZAS));
                                r = new Result();
                                r.otherInfo=tmp;
                                list.add(r);
                                //Tablero
                                tmp =  "" + c.getString(c.getColumnIndex(RoundTable.TABLERO));
                                r = new Result();
                                r.otherInfo=tmp;
                                list.add(r);
                                //Gano
                                tmp =  " " + c.getString(c.getColumnIndex(RoundTable.GANO));
                                r = new Result();
                                r.otherInfo=tmp;
                                list.add(r);
                                //Fin
                                callback.onResponse(list);
                                return;
                            }
                            i++;

                        }
                    }
                }
            }


            if(playeruuid.compareTo("//internal_val_all//")==0){
                //vamos a devolver las stats de los usuarios
                String str1 = "SELECT "
                        + UserTable.NICK + ", "
                        + UserTable.NUM_GANADAS + ", "
                        + UserTable.NUM_JUGADAS
                        + " FROM " + UserTable.TABLE_NAME + ";";

                Cursor c = db.rawQuery(str1, null);
                while (c.moveToNext()) {

                    String tmp = "" + c.getString(c.getColumnIndex(UserTable.NICK))
                            + " " + resources.getString(R.string.getResults1) + " "
                            + c.getString(c.getColumnIndex(UserTable.NUM_GANADAS))
                            + " " + resources.getString(R.string.getResults2)+ " "
                            + c.getString(c.getColumnIndex(UserTable.NUM_JUGADAS))
                            + " " + resources.getString(R.string.getResults3);

                    Result r = new Result();
                    r.otherInfo=tmp;
                    list.add(r);
                }

                callback.onResponse(list);
                return;
            }else{
                String str1 = "SELECT "
                        + UserTable.NICK + ", "
                        + UserTable.NUM_GANADAS + ", "
                        + UserTable.NUM_JUGADAS
                        + " FROM " + UserTable.TABLE_NAME
                        + " WHERE " + UserTable.NICK + "='" + playeruuid + "';";
                Cursor c = db.rawQuery(str1, null);
                c.moveToNext();
                String tmp = resources.getString(R.string.share1) + " "
                        + c.getString(c.getColumnIndex(UserTable.NUM_GANADAS))
                        + " " + resources.getString(R.string.share2) + " "
                        + c.getString(c.getColumnIndex(UserTable.NUM_JUGADAS))
                        + " " + resources.getString(R.string.share3);
                Result r = new Result();
                r.otherInfo=tmp;
                list.add(r);
                callback.onResponse(list);
                return;
            }
        }

        if(orderByField!=null) {
            if (orderByField.compareTo("fecha") == 0 || orderByField.compareTo("nick") == 0) {
                String str1 = "SELECT "
                        + RoundTable.NICK + ", "
                        + RoundTable.NICKRIVAL + ", "
                        + RoundTable.FECHA
                        + " FROM " + RoundTable.TABLE_NAME
                        + " ORDER BY " + orderByField + ";";

                Cursor c = db.rawQuery(str1, null);
                while (c.moveToNext()) {
                    String tmp = "" + c.getString(c.getColumnIndex(RoundTable.FECHA)) + ": "
                            + c.getString(c.getColumnIndex(RoundTable.NICK))
                            + " VS "
                            + c.getString(c.getColumnIndex(RoundTable.NICKRIVAL));

                    Result r = new Result();
                    r.otherInfo = tmp;
                    list.add(r);
                }
                callback.onResponse(list);
                return;
            }
        }
    }
}
