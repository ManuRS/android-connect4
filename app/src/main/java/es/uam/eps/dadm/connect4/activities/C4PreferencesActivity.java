/*
 * C4PreferencesActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import es.uam.eps.dadm.connect4.R;

/**
 * Actividad para encapsular el acceso a las preferencias de la app
 */
public class C4PreferencesActivity extends AppCompatActivity {

    public final static String TABLET_KEY = "tablet";

    public final static String USER_CODE_KEY = "usercode";
    public final static String USER_CODE_DEFAULT = "none";

    public final static String USER_NUMBER_KEY = "usernumber";
    public final static String USER_NUMBER_DEFAULT = "-1";

    public final static String FIGURE_CODE_KEY = "figurecode";
    public final static String FIGURE_CODE_DEFAULT = "0";

    public final static String FONDO_CODE_KEY = "colorcode";
    public final static String FONDO_CODE_DEFAULT = "azul";

    public final static String MUSIC_KEY = "music";

    public final static String MODO_BUSQUEDA_KEY = "modo_busqueda";
    public final static String MODO_BUSQUEDA_DEFAULT = "fecha";

    public final static String MODO_MAPA_KEY = "modo_mapa";
    public final static String MODO_MAPA_DEFAULT = "1";

    public final static String LAT_KEY = "lat";
    public final static String LAT_DEFAULT = "-1";

    public final static String LON_KEY = "lon";
    public final static String LON_DEFAULT = "-1";

    public final static String MODO_INTERNET_KEY = "modo_internet";
    public final static Boolean MODO_INTERNET_DEFAULT = false;

    public final static String MODO_BOT_KEY = "modo_bot";
    public final static String MODO_BOT_DEFAULT = "1";

    public final static String GCM_KEY = "gcm";
    public final static String GCM_DEFAULT = "";

    public final static String APP_VERSION_KEY = "app_version";
    public final static String APP_VERSION_DEFAULT = "";


    /**
     * Creador de la actividad
     *
     * @param savedInstanceState Bundle
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        C4PreferencesFragment fragment = new C4PreferencesFragment();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }

    /*MODO TABLET*/

    /**
     * Consulta la preferencia de si es tablet
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static Boolean getTabletCode(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TABLET_KEY, false);
    }

    /**
     * Set para la preferencia TabletCode
     *
     * @param context contexto
     * @param tabletcode preferencia
     */
    public static void setTabletCode(Context context, Boolean tabletcode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(C4PreferencesActivity.TABLET_KEY, tabletcode);
        editor.commit();
    }

    /*USUARIO*/
    /**
     * Consulta el user acutal
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getUserCode(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(USER_CODE_KEY, USER_CODE_DEFAULT);
    }

    /**
     * Set para la preferencia UserCode
     *
     * @param context contexto
     * @param usercode preferencia
     */
    public static void setUserCode(Context context, String usercode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.USER_CODE_KEY, usercode);
        editor.commit();
    }

    /**
     * Consulta el user actual
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getUserNumber(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(USER_NUMBER_KEY, USER_NUMBER_DEFAULT);
    }

    /**
     * Set para la preferencia UserCode
     *
     * @param context contexto
     * @param usernumber preferencia
     */
    public static void setUserNumber(Context context, String usernumber){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.USER_NUMBER_KEY, usernumber);
        editor.commit();
    }

    /*FIGURAS*/
    /**
     * Consulta figura a mostrar
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getFigureCode(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(FIGURE_CODE_KEY, FIGURE_CODE_DEFAULT);
    }

    /**
     * Set para la preferencia FigureCode
     *
     * @param context contexto
     * @param figurecode preferencia
     */
    public static void setFigureCode(Context context, String figurecode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.FIGURE_CODE_KEY, figurecode);
        editor.commit();
    }

    /*FONDO*/
    /**
     * Consulta color de fondo del tablero
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getFondoCode(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(FONDO_CODE_KEY, FONDO_CODE_DEFAULT);
    }

    /**
     * Set para la preferencia FondoCode
     *
     * @param context contexto
     * @param fondocode preferencia
     */
    public static void setFondoCode(Context context, String fondocode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.FONDO_CODE_KEY, fondocode);
        editor.commit();
    }

    /*MUSICA*/
    /**
     * Consulta valor de la musica
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static boolean getMusic(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MUSIC_KEY, false);
    }

    /**
     * Set para la preferencia Music
     *
     * @param context contexto
     * @param musiccode preferencia
     */
    public static void setMusic(Context context, boolean musiccode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(C4PreferencesActivity.MUSIC_KEY, musiccode);
        editor.commit();
    }

    /*BUSQUEDA*/
    /**
     * Consulta como buscar
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getBusqueda(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(MODO_BUSQUEDA_KEY, MODO_BUSQUEDA_DEFAULT);
    }

    /**
     * Set para la preferencia Busqueda
     *
     * @param context contexto
     * @param busquedacode preferencia
     */
    public static void setBusqueda(Context context, String busquedacode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.MODO_BUSQUEDA_KEY, busquedacode);
        editor.commit();
    }

    /*MAPA*/
    /**
     * Consulta modo mapa
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getMapa(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(MODO_MAPA_KEY, MODO_MAPA_DEFAULT);
    }

    /**
     * Set para la preferencia Mapa
     *
     * @param context contexto
     * @param mapacode preferencia
     */
    public static void setMapa(Context context, String mapacode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.MODO_MAPA_KEY, mapacode);
        editor.commit();
    }

    /**
     * Consulta modo lat
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getLat(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LAT_KEY, LAT_DEFAULT);
    }

    /**
     * Set para la preferencia lat
     *
     * @param context contexto
     * @param lat preferencia
     */
    public static void setLat(Context context, String lat){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.LAT_KEY, lat);
        editor.commit();
    }

    /**
     * Consulta modo lon
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getLon(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LON_KEY, LON_DEFAULT);
    }

    /**
     * Set para la preferencia lon
     *
     * @param context contexto
     * @param lon preferencia
     */
    public static void setLon(Context context, String lon){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.LON_KEY, lon);
        editor.commit();
    }

    /*CONEXION*/
    /**
     * Consulta modo internet
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static Boolean getInternet(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MODO_INTERNET_KEY, MODO_INTERNET_DEFAULT);
    }

    /**
     * Set para la preferencia internet
     *
     * @param context contexto
     * @param internet preferencia
     */
    public static void setInternet(Context context, Boolean internet){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(C4PreferencesActivity.MODO_INTERNET_KEY, internet);
        editor.commit();
    }

    /*BOT*/
    /**
     * Consulta bot
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getModoBot(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(MODO_BOT_KEY, MODO_BOT_DEFAULT);
    }

    /**
     * Set para la preferencia bot
     *
     * @param context contexto
     * @param botcode preferencia
     */
    public static void setModoBotDefault(Context context, String botcode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.MODO_BOT_KEY, botcode);
        editor.commit();
    }

    /*GCM*/
    /**
     * Consulta GCM number
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getGCM(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(GCM_KEY, GCM_DEFAULT);
    }

    /**
     * Set para la preferencia bot
     *
     * @param context contexto
     * @param gcm preferencia
     */
    public static void setGCM(Context context, String gcm){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.GCM_KEY, gcm);
        editor.commit();
    }

    /**
     * Consulta GCM number
     *
     * @param context contexto
     * @return valor de la preferencia
     */
    public static String getAppVersion(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(APP_VERSION_KEY, APP_VERSION_DEFAULT);
    }

    /**
     * Set para la preferencia bot
     *
     * @param context contexto
     * @param version preferencia
     */
    public static void setAppVersion(Context context, String version){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(C4PreferencesActivity.APP_VERSION_KEY, version);
        editor.commit();
    }
}
