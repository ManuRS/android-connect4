/*
 * DemosActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import es.uam.eps.dadm.connect4.R;

/**
 * Actividad para accedes a las pruebas de android
 */
public class DemosActivity extends AppCompatActivity {

    /**
     * Creador de la actividad
     *
     * @param savedInstanceState Bundle
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demos);

        /*Cogemos los botones*/
        Button wa = (Button)findViewById(R.id.buttonWA);
        Button gm = (Button)findViewById(R.id.buttonGM);
        Button s = (Button)findViewById(R.id.buttonS);
        Button at = (Button)findViewById(R.id.buttonAT);

        /*Escuchadores de los botnes*/

        wa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareDialogFragment().show(getFragmentManager(), "SHARE DIALOG");
            }
        });

        gm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment.lon_ext=null;
                MapFragment.lat_ext=null;
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SensorsActivity.class);
                startActivity(intent);
            }
        });

        at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                startService(intent);
            }
        });
    }
}
