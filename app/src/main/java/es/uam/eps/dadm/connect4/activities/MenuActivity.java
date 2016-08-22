/*
 * MenuActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.database.Repository;
import es.uam.eps.dadm.connect4.database.RepositoryFactory;
import es.uam.eps.dadm.connect4.extra.MyArrayAdapter;

/**
 * Clase del menu de la app connec4
 */
public class MenuActivity extends AppCompatActivity implements MenuFragment.OnButtonSelectedListener {

    /**
     * Creacion de la actividad
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        FragmentManager fm = getFragmentManager();
        if(fm.findFragmentById(R.id.fragment_menu)==null){
            MenuFragment fragmentMenu = new MenuFragment();
            fm.beginTransaction().add(R.id.fragment_menu, fragmentMenu).commit();
        }

        if(C4PreferencesActivity.getTabletCode(this)==true){
            if(fm.findFragmentById(R.id.fragment_about)==null){
                Fragment frag = new AboutFragment();
                fm.beginTransaction().add(R.id.fragment_about, frag).commit();
            }
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    /**
     * Maneja el comportamiento de los botones
     * @param str informacion sobre el boton pulsado
     */
    @Override
    public void onButtonSelected(String str){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_about);
        if(fragment!=null){
            /*Si existe el fragmento es que somos una tablet, sustituimos el fragment*/
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if(str=="local"){
                //Nos vamos al juego
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }else if(str=="stats"){
                fm.beginTransaction().replace(R.id.fragment_about, new StatsFragment()).commit();
            }else if(str=="statsP"){
                fm.beginTransaction().replace(R.id.fragment_about, new StatsPFragment()).commit();
            }else{
                fm.beginTransaction().replace(R.id.fragment_about, new AboutFragment()).commit();
            }

        }else {
            /*Si no hay fragment somos un movil y vamos a irnos a otra actividad*/
            Intent intent;
            if(str=="local"){
                if(C4PreferencesActivity.getInternet(this)) {
                    intent = new Intent(getApplicationContext(), SelectActivity.class); //En online nos vamos al selector
                    intent.putExtra("fin", false);
                }else{
                    intent = new Intent(getApplicationContext(), GameActivity.class); //En local nos vamos al juego
                }
            }else if(str=="stats") {
                intent = new Intent(getApplicationContext(), StatsActivity.class);
            }else if(str=="statsP") {
                intent = new Intent(getApplicationContext(), StatsPActivity.class);
            }else{
                intent = new Intent(getApplicationContext(), AboutActivity.class);
            }
            startActivity(intent);
        }
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
        inflater.inflate(R.menu.menu_activity, menu);
        return true;
    }

    /**
     * Determina comportamiento de las opciones del menu
     *
     * @param item
     * @return atendido
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.aboutMenu:
                /*Dialogo about*/
                new ContactDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
                return true;

            /*
            case R.id.preferences:
                /*Lanzamos las preferencias
                intent = new Intent(getApplicationContext(), C4PreferencesActivity.class);
                startActivity(intent);
                return true;
            */

            case R.id.logoutMenu:
                /*Deslogueamos y volvemos al splash*/
                C4PreferencesActivity.setUserCode(this, "none");
                C4PreferencesActivity.setUserNumber(this, "-1");
                C4PreferencesActivity.setGCM(this, "");
                RepositoryFactory.closeRepository();
                intent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(intent);
                return true;

            case R.id.extraMenu:
                intent = new Intent(getApplicationContext(), DemosActivity.class);
                startActivity(intent);
                return true;

            case R.id.compartirPuntGeneric:
                if(C4PreferencesActivity.getInternet(this)){
                    Intent send = new Intent(android.content.Intent.ACTION_SEND);
                    send.setType("text/plain");
                    send.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareplay));
                    startActivity(Intent.createChooser(send, getResources().getString(R.string.share_using)));
                }else{
                    /*Consulta DB*/
                    Repository repository = RepositoryFactory.createRepository(this);
                    Repository.ResultsCallback resultCallback = new Repository.ResultsCallback(){

                        /**
                         * Implementacion de la interfaz Repository
                         *
                         * @param results array de Result
                         * @see es.uam.eps.dadm.connect4.database.Repository
                         */
                        @Override
                        public void onResponse(ArrayList<Repository.Result> results) {
                        /*Enviar mis puntos*/
                            Intent send = new Intent(android.content.Intent.ACTION_SEND);
                            send.setType("text/plain");
                            send.putExtra(android.content.Intent.EXTRA_TEXT, results.get(0).otherInfo);
                            startActivity(Intent.createChooser(send, getResources().getString(R.string.share_using)));
                        }

                        /**
                         * Implementacion de la interfaz Repository
                         *
                         * @param error explicacion del error sucedido
                         * @see es.uam.eps.dadm.connect4.database.Repository
                         */
                        @Override
                        public void onError(String error) {
                            Toast.makeText(MenuActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    };

                    repository.getResults(C4PreferencesActivity.getUserCode(this), null, null, resultCallback);
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
            startActivity(new Intent().addCategory(Intent.CATEGORY_HOME).setAction(Intent.ACTION_MAIN));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
