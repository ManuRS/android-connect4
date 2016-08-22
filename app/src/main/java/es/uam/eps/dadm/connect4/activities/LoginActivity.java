/*
 * LoginActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.database.Repository;
import es.uam.eps.dadm.connect4.database.RepositoryFactory;


/**
 * Actividad para loguearse en la app
 */
public class LoginActivity extends AppCompatActivity {

    private Repository repository;
    private Repository.LoginRegisterCallback loginCallback;

    /**
     * Metodo del ciclo de vida que crea la actividad
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**Definir si hay internet**/
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfo1 = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ( (networkInfo!=null && networkInfo.isConnected()) || (networkInfo1!=null && networkInfo1.isConnected()) ) {
            C4PreferencesActivity.setInternet(this, true);
            TextView login = (TextView) findViewById(R.id.textOnline);
            login.setText(R.string.omode);
        } else {
            C4PreferencesActivity.setInternet(this, false);
            TextView login = (TextView) findViewById(R.id.textOnline);
            login.setText(R.string.offmode);
        }

        /***** DB ******/

        RepositoryFactory.closeRepository();
        repository = RepositoryFactory.createRepository(LoginActivity.this);
        loginCallback = new Repository.LoginRegisterCallback(){

            /**
             * Implementacion de la interfaz Repository
             *
             * @param playerUuid identificador del usuario
             * @see es.uam.eps.dadm.connect4.database.Repository
             */
            @Override
            public void onLogin(String playerUuid) {
                Toast.makeText(LoginActivity.this, getText(R.string.welcome) +" "+ playerUuid, Toast.LENGTH_SHORT).show();
                C4PreferencesActivity.setUserCode(LoginActivity.this, playerUuid);
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            }

            /**
             * Implementacion de la interfaz Repository
             *
             * @param error explicacion del error sucedido
             * @see es.uam.eps.dadm.connect4.database.Repository
             */
            @Override
            public void onError(String error) {
                if(error.compareTo("nouser")==0)
                    Toast.makeText(LoginActivity.this, getText(R.string.nouser), Toast.LENGTH_SHORT).show();
                else if(error.compareTo("volley")==0)
                    Toast.makeText(LoginActivity.this, getText(R.string.conexerr), Toast.LENGTH_SHORT).show();
                else if(error.compareTo("inuse")==0)
                    Toast.makeText(LoginActivity.this, getText(R.string.inuse), Toast.LENGTH_SHORT).show();
                else if(error.compareTo("created")==0)
                    Toast.makeText(LoginActivity.this, getText(R.string.created), Toast.LENGTH_SHORT).show();
            }
        };

        /***** BOTONES *****/

        Button login = (Button) findViewById(R.id.buttonLogIn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText t = (EditText)findViewById(R.id.user);
                EditText p = (EditText) findViewById(R.id.password);
                if(t.getText().toString().compareTo("")!=0 && p.getText().toString().compareTo("")!=0 )
                    repository.login(t.getText().toString(), p.getText().toString(), loginCallback);
                if(C4PreferencesActivity.getInternet(v.getContext())==true)
                    Snackbar.make(findViewById(android.R.id.content), getText(R.string.connect), Snackbar.LENGTH_LONG).show();

            }
        });

        Button newuser = (Button) findViewById(R.id.buttonNewUser);
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText t = (EditText)findViewById(R.id.user);
                EditText p = (EditText) findViewById(R.id.password);
                if(t.getText().toString().compareTo("")!=0 && p.getText().toString().compareTo("")!=0 )
                    repository.register(t.getText().toString(), p.getText().toString(), loginCallback);
                if(C4PreferencesActivity.getInternet(v.getContext())==true)
                    Snackbar.make(findViewById(android.R.id.content), getText(R.string.connect), Snackbar.LENGTH_LONG).show();

            }
        });

        Button invitado = (Button) findViewById(R.id.buttonNoLogin);
        if(C4PreferencesActivity.getInternet(this)){
            invitado.setVisibility(View.GONE);
        }else{
            invitado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repository.login("guest", "guest", loginCallback);
                }
            });
        }

        if(C4PreferencesActivity.getTabletCode(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }
}
