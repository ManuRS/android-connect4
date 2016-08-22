/*
 * SplashActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.*;

import es.uam.eps.dadm.connect4.R;

/**
 * Actividad inicial de la aplicacion
 */
public class SplashActivity extends Activity {

    String SENDERIDGCM = "125442492416";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static int VERSION_CODE = 5;

    /**
     * Creador de la actividad
     *
     * @param savedInstanceState Bundle
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*GCM*/
        if (checkPlayServices()){
            // If id de registro (guardado en prefs) está vacío o si ha cambiado
            // de versión la app se llama a registrarse
            if (C4PreferencesActivity.getGCM(getBaseContext()).equals("") || !C4PreferencesActivity.getAppVersion(getBaseContext()).equals(VERSION_CODE+"") ) {
                registerInBackground();
            }
        }


        /*Definimos que somos*/
        Display display =getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        //int orientation = getResources().getConfiguration().orientation;
        float dpWidth;
        //if(orientation==1)
            dpWidth = outMetrics.widthPixels / density;
        //else
            //dpWidth = outMetrics.heightPixels / density;
        if(dpWidth>=600){/*Somos una tablet*/
            C4PreferencesActivity.setTabletCode(this, true);
        }
        else {/*Somos un movil*/
            C4PreferencesActivity.setTabletCode(this, false);
        }

        /*Animacion*/
        Point size = new Point();
        display.getSize(size);
        float width = size.x;

        ImageView img_animation = (ImageView) findViewById(R.id.imageView);
        TranslateAnimation animation = new TranslateAnimation((float)0.2*width, -(float)0.2*width, 0.0f, 0.0f); //Esto indica los pixeles de la coordenada de inicio y fin
        animation.setDuration(5000);
        animation.setRepeatCount(10);
        animation.setRepeatMode(2);
        img_animation.startAnimation(animation);

        if(C4PreferencesActivity.getTabletCode(this)==false){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Metodo llamada cuando hay un evento de tipo Touch
     *
     * @param event evento
     * @return true cuando ha sido atendido
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if ( C4PreferencesActivity.getUserCode(this).compareTo("none") == 0 ){
                /*No hay usuario logueado, asi que vamos a la pantalla de login*/
                startActivity(new Intent(this, LoginActivity.class));
            }else{
                /*Ya hay usuario, entramos directamente al menu*/
                startActivity(new Intent(this, MenuActivity.class));
            }
        }
        return true;
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
            startActivity(new Intent().addCategory(Intent.CATEGORY_HOME).setAction(Intent.ACTION_MAIN));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Registramos la aplicación en los servidores GCM asincronamente.
     *
     * Guardamos la información del id en preferencias. También de la versión
     * de la app
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String id = "";
                try {
                    Thread.sleep(600);
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
                    // Nos registramos en los servidores de GCM
                    id = gcm.register(SENDERIDGCM);
                    // Guardamos el id
                    C4PreferencesActivity.setGCM(getBaseContext(), id);
                    C4PreferencesActivity.setAppVersion(getBaseContext(), VERSION_CODE+"");
                } catch (Exception ex) {
                    Log.d("connect4", "Error registro en GCM:" + ex.getMessage());
                }
                return id;
            }
        }.execute(null, null, null);
    }

    /**
     * Comprueba si estan instalados los google play services
     * @return boolean
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("connect4", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}